package com.lenovo.springmvc.demo.utils;

import java.lang.reflect.Field;

/**
 * 反射处理Bean，得到里面的属性值
 * 
 * @author shijy2
 *
 */
@SuppressWarnings("rawtypes")
public class ReflexObjectUtil {

    /**
     * 单个对象的某个键的值
     * 
     * @param object
     *            对象
     * 
     * @param key
     *            键
     * 
     * @return Object 键在对象中所对应得值 没有查到时返回空字符串
     */
	public static Object getValueByKey(Object obj, String key) {
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            try {
            
                if (f.getName().endsWith(key)) {
                    System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return "";
    }
    public static void setValueByKey(Object obj, String key,Object value) {
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            try {
            
                if (f.getName().endsWith(key)) {
                    f.set(obj, value);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    

}