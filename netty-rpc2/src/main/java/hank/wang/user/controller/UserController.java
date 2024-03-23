package hank.wang.user.controller;

import hank.wang.netty.util.Response;
import hank.wang.netty.util.ResponseUtil;
import hank.wang.user.bean.User;
import hank.wang.user.service.UserService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

// 在引入Remote注解后，可将此类删除
// 编写业务代码
@Controller
public class UserController {

    @Resource
    private UserService userService;

    public Response saveUser(User user) {

        userService.saveUser(user);

        return ResponseUtil.createSuccessResponse(user);
    }

    public Response saveUsers(List<User> userlist){

        userService.saveUSerList(userlist);

        return ResponseUtil.createSuccessResponse(userlist);
    }
}
