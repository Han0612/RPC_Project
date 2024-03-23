package hank.wang.client.param;

import java.util.concurrent.atomic.AtomicInteger;

// 封装客户端请求具体内容
public class ClientRequest {
    private final long id;
    private Object content;
    private final AtomicInteger aid = new AtomicInteger(0);

    private String command; // 存储获取服务端具体类和具体方法的信息

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public ClientRequest() {
        this.id = aid.incrementAndGet();
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }
}
