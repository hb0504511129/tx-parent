/*
 * 描          述:  <描述>
 * 修  改   人:  brady
 * 修改时间:  2013-8-14
 * <修改描述:>
 */
package com.tx.component.basicdata.executor;

import java.util.List;
import java.util.Map;

import com.tx.core.paged.model.PagedList;

/**
 * 基础数据类型<br/>
 *     该类中由于默认仅提供了根据主键对对象的操作<br/>
 *     所以delete,update直接返回true or false<br/>
 * 
 * @author  brady
 * @version  [版本号, 2013-8-14]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface BasicDataExecutor<T> {
    
    /**
     * 判断基础数据是否包含指定数据<br/>
     *<功能详细描述>
     * @param obj
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public boolean contains(String pk);
    
    /**
     * 根据主键获取对象
     *     与find不同的是，get的使用方法为
     *     首先用list获取所有对象，然后建立一个map映射后
     *     再通过该map.get出对象
     *<功能详细描述>
     * @param findCondition
     * @return [参数说明]
     * 
     * @return T [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public T get(String pk);
    
    /**
      * 根据条件查询指定对象
      *<功能详细描述>
      * @param findCondition
      * @return [参数说明]
      * 
      * @return T [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public T find(String pk);
    
    /**
      * 获取对应的所有基础数据<br/>
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return List<T> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public List<T> list();
    
    /**
      * 查询对应基础数据的列表<br/>
      *<功能详细描述>
      * @param params
      * @return [参数说明]
      * 
      * @return List<T> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public List<T> query(Map<String, Object> params);
    
    /**
      * 分页查询基础数据列表<br/>
      *<功能详细描述>
      * @param params
      * @param pageIndex
      * @param pageSize
      * @return [参数说明]
      * 
      * @return PagedList<T> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public PagedList<T> queryPagedList(Map<String, Object> params,
            int pageIndex, int pageSize);
    
    /**
      * 查询基础数据数目<br/>
      *<功能详细描述>
      * @param params
      * @return [参数说明]
      * 
      * @return int [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public int count(Map<String, Object> params);
    
    /**
      * 插入基础数据实例<br/>
      *<功能详细描述>
      * @param instance [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public void insert(T instance);
    
    /**
      * 删除对象<br/>
      *<功能详细描述>
      * @param params
      * @return [参数说明]
      * 
      * @return int [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public boolean delete(String pk);
    
    /**
      * 更新基础数据实例<br/>
      *<功能详细描述>
      * @param instance
      * @return [参数说明]
      * 
      * @return int [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public boolean update(Map<String, Object> params);
}
