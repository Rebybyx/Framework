package com.zh.activiti.annotation;

import java.lang.annotation.*;

/**文本显示字段
 * Created by Mrkin on 2016/12/12.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Name {
}
