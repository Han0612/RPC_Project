package hank.wang.netty.client;

import java.util.concurrent.atomic.AtomicInteger;

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
