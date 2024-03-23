package hank.wang.netty.medium;

import hank.wang.netty.annotation.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

// 中介者模式
// 服务端接收到客户端数据，通过medium交给userController处理
@Component
public class InitialMedium implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    // 在Spring容器中bean被初始化后做额外的处理
    // 将所有Remote类添加到beanMethodMap中
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean.getClass().isAnnotationPresent(Remote.class)) {
//            System.out.println(bean.getClass().getName());

            Method[] methods = bean.getClass().getMethods();
            for (Method m : methods) {
                String beanMethodName = bean.getClass().getInterfaces()[0].getName() + "." + m.getName();
                System.out.println(beanMethodName);

                Map<String, BeanMethod> beanMethodMap = Media.beanMethodMap;

                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(m);

                beanMethodMap.put(beanMethodName, beanMethod);
            }
        }

        return bean;
    }
}
