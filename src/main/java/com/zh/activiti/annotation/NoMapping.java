package com.zh.activiti.annotation;

import java.lang.annotation.*;

/** 不映射 注解
 * Created by Mrkin on 2016/11/3.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoMapping {
}
