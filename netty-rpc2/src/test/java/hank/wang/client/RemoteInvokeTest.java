package hank.wang.client;

import hank.wang.netty.annotation.RemoteInvoke;
import hank.wang.netty.client.ClientRequest;
import hank.wang.netty.client.TcpClient;
import hank.wang.netty.util.Response;
import hank.wang.user.bean.User;
import hank.wang.user.remote.UserRemote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("hank.wang")
public class RemoteInvokeTest {

    @RemoteInvoke
    private UserRemote userRemote;

//    @Test
//    public void testSaveUser() {
//        ClientRequest clientRequest = new ClientRequest();
//
//        User user = new User();
//        user.setId(1);
//        user.setName("hank");
//
//        clientRequest.setContent(user);
//        clientRequest.setCommand("hank.wang.user.controller.UserController.saveUser");
//
//        Response response = TcpClient.send(clientRequest);
//        System.out.println(response.getResult());
//    }

    @Test
    public void testSaveUser2() {

        User user = new User();
        user.setId(1);
        user.setName("hank");

        userRemote.saveUser(user);
    }

}
