package com.zh.activiti.annotation;

import java.lang.annotation.*;

/**逻辑删除标志位
 * Created by Mrkin on 2016/12/14.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogicDelete {
}
