/*
 * 描          述:  <描述>
 * 修  改   人:  Administrator
 * 修改时间:  2014年3月23日
 * <修改描述:>
 */
package com.tx.component.rule.session;


 /**
  * 可获取值的环绕对象<br/>
  * <功能详细描述>
  * 
  * @author  Administrator
  * @version  [版本号, 2014年3月23日]
  * @see  [相关类/方法]
  * @since  [产品/模块版本]
  */
public interface ValueWrapper<T> {
    
    /**
      * 获取实际值 <br/> 
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return T [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public T getValue();
    
    /**
      * 设计实际值<br/> 
      *<功能详细描述>
      * @param value [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public void setValue(T value);
}
