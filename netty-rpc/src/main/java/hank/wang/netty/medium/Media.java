package hank.wang.netty.medium;

import com.alibaba.fastjson.JSONObject;
import hank.wang.netty.handler.param.ServerRequest;
import hank.wang.netty.util.Response;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Media {

    public static final HashMap<String, BeanMethod> beanMethodMap = new HashMap<String,BeanMethod>();
    // 单例模式
    private static Media m = null;

    private Media() {

    }

    public static Media newInstance(){
        if(m == null){
            synchronized (Media.class) {
                if(m == null){
                    m = new Media();
                }
            }
        }
        return m;
    }


    // 用反射处理业务
    public Response process(ServerRequest serverRequest) {
        Response result = null;

        try {
            String command = serverRequest.getCommand();
            BeanMethod beanMethod = beanMethodMap.get(command);
            if (beanMethod == null) {
                // 构建并返回一个表示没有找到方法的Response对象
                Response response = new Response();
                response.setCode("404");
                response.setMsg("No corresponding method found");
                return response;
            }


            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();

            Class type = method.getParameterTypes()[0];  // 先实现一个参数的方法

            Object content = serverRequest.getContent();
            Object args = JSONObject.parseObject(JSONObject.toJSONString(content), type);

            result = (Response) method.invoke(bean, args);
            result.setId(serverRequest.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setCode("500");
            response.setMsg("Error processing request: " + e.getMessage());
            return response;
        }

        return result;
    }
}







