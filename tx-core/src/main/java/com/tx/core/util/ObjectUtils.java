/*
 * 描          述:  <描述>
 * 修  改   人:  brady
 * 修改时间:  2013-8-22
 * <修改描述:>
 */
package com.tx.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.reflection.MetaObject;

import com.tx.core.exceptions.io.ResourceAccessException;
import com.tx.core.exceptions.util.AssertUtils;
import com.tx.core.reflection.exception.ReflectionException;

/**
 * 对象工具类<br/>
 *     主要补充commonslang包中方法不足
 *     利用该类补足对应方法<br/>
 * <功能详细描述>
 * 
 * @author  brady
 * @version  [版本号, 2013-8-22]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ObjectUtils {
    
    public static <T> void populate(T obj, Map<String, Object> properties) {
        try {
            BeanUtils.populate(obj, properties);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("invoke populate error.", e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("invoke populate error.", e);
        }
    }
    
    /**
      * 根据传入的参数生成对象实例
      *<功能详细描述>
      * @param type
      * @param objects
      * @return [参数说明]
      * 
      * @return T [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> type, Object... objects) {
        T res = null;
        try {
            res = (T) ConstructorUtils.invokeConstructor(type, objects);
            return res;
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(
                    "invokeConstructor create newInstance error.", e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(
                    "invokeConstructor create newInstance error.", e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(
                    "invokeConstructor create newInstance error.", e);
        } catch (InstantiationException e) {
            throw new ReflectionException(
                    "invokeConstructor create newInstance error.", e);
        }
    }
    
    /**
     * 判断入参数是否为空<br/>
     * " "不会被判定为false<br/>
     * <功能详细描述>
     * @param obj
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public static boolean isEmpty(Object obj) {
        //为空时认为是empty的
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return StringUtils.isEmpty((String) obj);
        } else if (obj instanceof Collection<?>) {
            return CollectionUtils.isEmpty((Collection<?>) obj);
        } else if (obj instanceof Map<?, ?>) {
            return MapUtils.isEmpty((Map<?, ?>) obj);
        } else if (TypeUtils.isArrayType(obj.getClass())) {
            return ArrayUtils.isEmpty((Object[]) obj);
        } else {
            return false;
        }
    }
    
    /**
      * 根据依赖的属性名生成对象的HashCode
      *<功能详细描述>
      * @param thisObj
      * @param dependPropertyName
      * @return [参数说明]
      * 
      * @return int [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static int generateHashCode(int superHashCode, Object thisObj,
            String... dependPropertyName) {
        AssertUtils.notNull(thisObj, "thisObj is null.");
        
        MetaObject metaObject = MetaObject.forObject(thisObj);
        int resHashCode = thisObj.getClass().hashCode();
        for (String propertyNameTemp : dependPropertyName) {
            Object value = metaObject.getValue(propertyNameTemp);
            resHashCode += value == null ? superHashCode : value.hashCode();
        }
        return resHashCode;
    }
    
    /**
      * 根据依赖的属性名，判断对象是否相等
      *     当指定字段都为空时判断两对象引用是否相同
      *<功能详细描述>
      * @param thisObj
      * @param otherObj
      * @param dependPropertyName
      * @return [参数说明]
      * 
      * @return boolean [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static boolean equals(Object thisObj, Object otherObj,
            String... dependPropertyName) {
        AssertUtils.notNull(thisObj, "thisObj is null.");
        
        if (thisObj == otherObj) {
            return true;
        } else if (!thisObj.getClass().isAssignableFrom(otherObj.getClass())) {
            return false;
        } else {
            MetaObject thisMetaObject = MetaObject.forObject(thisObj);
            MetaObject otherMetaObject = MetaObject.forObject(otherObj);
            
            for (String propertyNameTemp : dependPropertyName) {
                if (!org.apache.commons.lang.ObjectUtils.equals(thisMetaObject.getValue(propertyNameTemp),
                        otherMetaObject.getValue(propertyNameTemp))) {
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
      * 对一个Serializable的对象进行深度Clone
      *     基于序列化与反序列化实现，
      *     该方法通用性强，但性能反而不如clone或BeanUtils的应用<br/>
      *     如果深度克隆的对象未实现Serializable将会抛出异常
      * <功能详细描述>
      * @param srcObj
      * @return [参数说明]
      * 
      * @return T [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T srcObj) {
        T resObject = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(srcObj);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    baos.toByteArray());
            ois = new ObjectInputStream(bais);
            resObject = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ResourceAccessException("deepClone error.", e);
        } finally {
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(ois);
            IOUtils.closeQuietly(baos);
        }
        return resObject;
    }
}
