package hank.wang.client;

import hank.wang.netty.client.ClientRequest;
import hank.wang.netty.util.Response;
import hank.wang.netty.client.TcpClient;
import hank.wang.user.bean.User;
import org.junit.Test;

public class TcpClientTest {

    @Test
    public void testGetResponse() {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setContent("测试tcp长连接请求");
        Response response = TcpClient.send(clientRequest);
        System.out.println(response.getResult());
    }

    @Test
    public void testSaveUser() {
        ClientRequest clientRequest = new ClientRequest();

        User user = new User();
        user.setId(1);
        user.setName("hank");

        clientRequest.setContent(user);
        clientRequest.setCommand("hank.wang.user.controller.UserController.saveUser");

        Response response = TcpClient.send(clientRequest);
        System.out.println(response.getResult());
    }

}