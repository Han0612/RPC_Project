package hank.wang.user.remote;

import hank.wang.netty.annotation.Remote;
import hank.wang.netty.util.Response;
import hank.wang.netty.util.ResponseUtil;
import hank.wang.user.bean.User;
import hank.wang.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Remote
public class UserRemoteImpl implements UserRemote{

    @Resource   //UserService的一个实例将被自动注入到service字段
    private UserService service;

    public Response saveUser(User user){
        service.saveUser(user);

        return ResponseUtil.createSuccessResponse(user);
    }

    public Response saveUsers(List<User> userlist){
        service.saveUSerList(userlist);

        return ResponseUtil.createSuccessResponse(userlist);
    }
}
