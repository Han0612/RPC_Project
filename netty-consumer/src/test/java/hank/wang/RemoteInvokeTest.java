package hank.wang;


import com.alibaba.fastjson.JSONObject;
import hank.wang.client.annotation.RemoteInvoke;
import hank.wang.user.bean.User;
import hank.wang.user.remote.UserRemote;
import hank.wang.client.param.Response;
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

    @Test
    public void testSaveUser() {

        User user = new User();
        user.setId(1);
        user.setName("hank");

        // 此处会被CGLIB生成的动态代理对象拦截（enhancer）拦截做增强处理
        // 客户端可以无需关心网络通信的细节，就能像使用本地服务一样使用远程服务
        Response response1 = userRemote.saveUser(user);
        System.out.println(JSONObject.toJSONString(response1));
    }

    @Test
    public void testSaveUserMultipleTimes() throws InterruptedException {
        User user = new User();
//        user.setId(1);
        user.setName("hank");

        // 尝试两次请求，希望能够观察到请求被分配到不同的节点
        for (int i = 0; i < 6; i++) {
            user.setId(i);
            // 模拟发送请求
            userRemote.saveUser(user);

            // 等待一段时间，以确保下一次请求可以被分配到另一个节点
            // 注意：这里的等待可能不是必要的，具体取决于TcpClient和ChannelManager的实现
//            Thread.sleep(1000); // 等待1秒
        }
    }

}
