package hank.wang.user.remote;

import hank.wang.client.param.Response;
import hank.wang.user.bean.User;

import java.util.List;

public interface UserRemote {
    public Response saveUser(User user);
    public Response saveUsers(List<User> userlist);
}
