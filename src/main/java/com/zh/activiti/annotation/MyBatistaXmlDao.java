package com.zh.activiti.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标识MyBatis的DAO,方便扫描
 *
 * Created by Marlon on 2016/11/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface MyBatistaXmlDao {
    String value() default "";
}
