package hank.wang.netty.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;



@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemoteInvoke {

}
