package hank.wang.client.proxy;

import hank.wang.client.annotation.RemoteInvoke;
import hank.wang.client.core.TcpClient;
import hank.wang.client.param.ClientRequest;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

// 为那些被@RemoteInvoke注解标记的字段生成代理对象
@Component
public class InvokeProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(RemoteInvoke.class)) {
                // 在运行时可以访问私有字段
                field.setAccessible(true);

                final HashMap<Method, Class> methodClassMap = new HashMap<>();
                putMethodClass(methodClassMap, field);

                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{field.getType()});   // 设置代理对象要实现的接口
                enhancer.setCallback(new MethodInterceptor() {  // 拦截代理对象上所有的方法调用
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        // 采用netty客户端去调用服务器
                        ClientRequest clientRequest = new ClientRequest();
                        clientRequest.setContent(args[0]);
                        clientRequest.setCommand(methodClassMap.get(method).getName() + "." + method.getName());

                        return TcpClient.send(clientRequest);
                    }
                });

                try {
                    field.set(bean, enhancer.create()); // 使用反射将创建的代理对象设置到bean的相应字段上
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }

    //对属性的所有方法和属性类型放入到HashMap中
    private void putMethodClass(HashMap<Method, Class> methodMap, Field field) {
        // getType获得属性对应的类，进而获得该类的所有方法
        Method[] methods = field.getType().getDeclaredMethods();

        for (Method m : methods) {
            methodMap.put(m, field.getType());
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
