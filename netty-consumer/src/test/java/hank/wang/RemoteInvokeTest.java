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
    public void testSaveUserMultipleTimes() throws InterruptedException {
        User user = new User();
        user.setName("hank");

        // 模拟10000次客户端请求
        for (int i = 0; i < 10000; i++) {
            user.setId(i);
            // 模拟发送请求
            userRemote.saveUser(user);
        }
    }
}
