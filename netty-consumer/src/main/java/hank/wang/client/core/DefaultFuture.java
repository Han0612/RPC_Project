package hank.wang.client.core;


import hank.wang.client.param.ClientRequest;
import hank.wang.client.param.Response;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class DefaultFuture {

    public static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
    final Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    private Response response;
    private Long timeout = 2*60*1000l;  // 两分钟请求没有返回则默认超时
    private Long start = System.currentTimeMillis();

    public DefaultFuture(ClientRequest request) {
        allDefaultFuture.put(request.getId(), this);
    }

    // 主线程获取数据，首先要等待结果
    public Response get() {
        lock.lock();

        try {
            while(!done()) {
                condition.await();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return this.response;
    }

    public Response get(long timeout) {
        lock.lock();

        try {
            while(!done()){
                condition.await(timeout, TimeUnit.MILLISECONDS);
                if((System.currentTimeMillis()-start) > timeout){
					System.out.println("Future中的请求超时");
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return this.response;
    }

    public static void receive(Response response) {
        DefaultFuture defaultFuture = allDefaultFuture.get(response.getId());

        if (defaultFuture != null) {
            Lock lock = defaultFuture.lock;
            lock.lock();

            try {
                defaultFuture.setResponse(response);
                defaultFuture.condition.signal();
                allDefaultFuture.remove(defaultFuture);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean done() {
        if(this.response != null){
            return true;
        }
        return false;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Long getStart() {
        return start;
    }

    // 清理线程
    static class CleanFutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> ids = allDefaultFuture.keySet();
            for (Long id : ids) {
                DefaultFuture defaultFuture = allDefaultFuture.get(id);

                if (defaultFuture == null) {
                    allDefaultFuture.remove(defaultFuture);
                } else if (defaultFuture.getTimeout() < (System.currentTimeMillis() - defaultFuture.getStart())) {
                    Response response1 = new Response();
                    response1.setCode("33333");
                    response1.setMsg("链路超时");
                    response1.setId(id);

                    receive(response1);
                }
            }
        }
    }

    // 静态代码块在类被初始化后立即运行
    static {
        CleanFutureThread cleanFutureThread = new CleanFutureThread();
        cleanFutureThread.setDaemon(true);  // 当JVM中没有任何活跃的用户线程时，守护线程会被JVM自动终止
        cleanFutureThread.start();
    }
}






