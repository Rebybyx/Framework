package com.zh.activiti.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 扩展spring的BeanUtils，增加拷贝属性排除null值的功能(注：String为null不考虑)
 * 
 * @author 陈晓亮
 * 
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

	public static void copyNotNullProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
		copyNotNullProperties(source, target, null, ignoreProperties);
	}

	public static void copyNotNullProperties(Object source, Object target, Class<?> editable) throws BeansException {
		copyNotNullProperties(source, target, editable, null);
	}

	public static void copyNotNullProperties(Object source, Object target) throws BeansException {
		copyNotNullProperties(source, target, null, null);
	}

	private static void copyNotNullProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class ["
						+ editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value != null || readMethod.getReturnType().getName().equals("java.lang.String")) {// 这里判断以下value是否为空，当然这里也能进行一些特殊要求的处理
																												// 例如绑定时格式转换等等，如果是String类型，则不需要验证是否为空
							boolean isEmpty = false;
							if (value instanceof Set) {
								Set s = (Set) value;
								if (s == null || s.isEmpty()) {
									isEmpty = true;
								}
							} else if (value instanceof Map) {
								Map m = (Map) value;
								if (m == null || m.isEmpty()) {
									isEmpty = true;
								}
							} else if (value instanceof List) {
								List l = (List) value;
								if (l == null || l.size() < 1) {
									isEmpty = true;
								}
							} else if (value instanceof Collection) {
								Collection c = (Collection) value;
								if (c == null || c.size() < 1) {
									isEmpty = true;
								}
							}
							if (!isEmpty) {
								Method writeMethod = targetPd.getWriteMethod();
								if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
									writeMethod.setAccessible(true);
								}
								writeMethod.invoke(target, value);
							}
						}
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param target
	 *            目标对象
	 * @param source
	 *            原始对象
	 * @throws Exception
	 */
	public static void copy(Object target, Object source) throws Exception {
		if (target == null) {
			throw new IllegalArgumentException("No targetination bean specified");
		}
		if (source == null) {
			throw new IllegalArgumentException("No sourcein bean specified");
		}

		Class sourceClass = source.getClass();
		Class targetClass = target.getClass();
		if (sourceClass == String.class || sourceClass.isPrimitive()) {
			target = source;
		}
		if (source.getClass().isArray()) {
			Object[] targetArr = (Object[]) target;
			Object[] sourceArr = (Object[]) source;
			Class elemenClass = targetArr.getClass().getComponentType();

			for (int i = 0; i < sourceArr.length; i++) {
				if (targetArr[i] == null) {
					targetArr[i] = elemenClass.newInstance();
				}

				copy(targetArr[i], sourceArr[i]);
			}
		}
		String classLogInfo = "sourceClass:" + sourceClass.getName() + ",targetClass:" + targetClass.getName() + ",";
		PropertyDescriptor[] sourceDescriptors = PropertyUtils.getPropertyDescriptors(source);
		for (int i = 0; i < sourceDescriptors.length; i++) {
			String name = sourceDescriptors[i].getName();
			if ("class".equals(name)) {
				continue;
			}
			Object value = null;
			if (PropertyUtils.isReadable(source, name) && PropertyUtils.isWriteable(target, name)) {
				try {
					value = PropertyUtils.getSimpleProperty(source, name);
					PropertyUtils.setSimpleProperty(target, name, value);
				} catch (IllegalArgumentException e) {
					// 绫诲瀷涓嶅悓
					try {
						PropertyDescriptor targetDescriptor = PropertyUtils.getPropertyDescriptor(target, name);
						Object new_value = targetDescriptor.getPropertyType().newInstance();
						copy(new_value, value);
						// LOG.info(new_value);
						PropertyUtils.setSimpleProperty(target, name, new_value);
					} catch (IllegalArgumentException e1) {
						//
					} catch (IllegalAccessException e1) {
						throw e1;
					} catch (InvocationTargetException e1) {
						throw e1;
					} catch (NoSuchMethodException e1) {
						throw e1;
					} catch (InstantiationException e1) {
						throw e1;
					}
				} catch (NoSuchMethodException e) {
					throw e;
				} catch (IllegalAccessException e) {
					throw e;
				} catch (InvocationTargetException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 从map拷贝对象
	 * 
	 * @param target
	 *            目标对象
	 * @param source
	 *            源对象
	 * @throws Exception
	 */
	public static void copyByMap(Object target, Map<String, Object> source) throws Exception {
		if (target == null) {
			throw new IllegalArgumentException("No targetination bean specified");
		}
		if (source == null) {
			throw new IllegalArgumentException("No sourcein bean specified");
		}

		Field[] field = target.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
			Field f = field[i];
			System.out.println(f.getName());
			String name = f.getName();
			Object value = source.get(name);
			if ("serialVersionUID".equals(name)) {
				continue;
			}
			name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法

			Method m = target.getClass().getMethod("set" + name, f.getType());
			m.invoke(target, value);
		}
	}

}