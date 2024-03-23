package hank.wang.netty.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 定义了一个名为@Remote的自定义注解，旨在标注Java类型（类、接口等）或方法
 * 简化远程服务的使用和声明，特别是在构建分布式系统或微服务架构时
 * 这个注解可以用来标识需要通过网络调用的服务接口或方法 或者标识某个服务实现类需要被远程访问
 */
@Target({ElementType.TYPE, ElementType.METHOD}) // 该注解不仅可以放到类上，也可以放到方法上
@Retention(RetentionPolicy.RUNTIME) // 表明@Remote注解的信息在运行时依然保留，使得可以通过反射机制读取注解的信息
@Documented // 将此注解包含在Javadoc中
@Component // 表面该类是Spring管理的一个组件
public @interface Remote {
    String value() default "";
}
