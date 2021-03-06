/*
 * 描          述:  <描述>
 * 修  改   人:  brady
 * 修改时间:  2013-4-3
 * <修改描述:>
 */
package com.tx.component.auth.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.tx.component.auth.model.AuthItem;
import com.tx.component.auth.model.AuthItemRef;
import com.tx.component.auth.model.CurrentSessionContext;
import com.tx.core.exceptions.util.AssertUtils;

/**
 * 权限会话容器<br/>
 * <功能详细描述>
 * 
 * @author  brady
 * @version  [版本号, 2013-4-3]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class AuthSessionContext {
    
    public final static String SESSION_KEY_CURRENT_OPERATOR_AUTHREF_MULTIVALUEMAP = "AUTH_CONTEXT_CURRENT_OPERATOR_AUTHREF_MULTIVALUEMAP";
    
    public final static String SESSION_KEY_CURRENT_OPERATOR_ID = "AUTH_CONTEXT_CURRENT_OPERATOR_ID";
    
    /**
     * 线程变量:当前会话容器<br/>
     * 获取到该容器后可以<br/>
     * 获取当前回话的session从而获取到相应的权限列表
     */
    private static ThreadLocal<CurrentSessionContext> currentSessionContext = new ThreadLocal<CurrentSessionContext>() {
        /**
         * @return
         */
        @Override
        protected CurrentSessionContext initialValue() {
            CurrentSessionContext csContext = new CurrentSessionContext();
            return csContext;
        }
    };
    
    /**
      * 将操作员的id放入session中
      * <功能详细描述>
      * @param operatorId [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static void putOperatorIdToSession(String operatorId) {
        AssertUtils.notEmpty(operatorId, "operatorId is empty.");
        
        currentSessionContext.get()
                .getSession()
                .setAttribute(SESSION_KEY_CURRENT_OPERATOR_ID, operatorId);
    }
    
    /**
      * 从当前会话中获取当前操作员的id
      * <功能详细描述> [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static String getOperatorIdFromSession() {
        String operatorId = (String) currentSessionContext.get()
                .getSession()
                .getAttribute(SESSION_KEY_CURRENT_OPERATOR_ID);
        
        return operatorId;
    }
    
    /**
     * 将当前操作人员的权限引用放入session中 <br/>
     * 1、如果当前不存在会话，者直接跳过该逻辑<br/>
     *<功能详细描述>
     * @param authItemRefList [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public static void putAuthRefToSession(List<AuthItemRef> authItemRefList) {
        MultiValueMap<String, AuthItemRef> authItemRefMap = new LinkedMultiValueMap<String, AuthItemRef>();
        //如果当前不存在会话，者直接跳过该逻辑
        if (!CollectionUtils.isEmpty(authItemRefList)) {
            //如果当前人员不含有权限，也会压入一个空的权限引用map
            for (AuthItemRef refTemp : authItemRefList) {
                authItemRefMap.add(refTemp.getAuthItem().getId(), refTemp);
            }
        }
        
        //将权限压入当前会话中
        currentSessionContext.get()
                .getSession()
                .setAttribute(SESSION_KEY_CURRENT_OPERATOR_AUTHREF_MULTIVALUEMAP,
                        authItemRefMap);
    }
    
    /**
      * 从session中获取权限集合<br/>
      * 1、当前如果不存在会话，者返回null
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return List<AuthItemRef> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static MultiValueMap<String, AuthItemRef> getAuthRefMultiValueMapFromSession() {
        @SuppressWarnings("unchecked")
        MultiValueMap<String, AuthItemRef> authItemRefMap = (MultiValueMap<String, AuthItemRef>) currentSessionContext.get()
                .getSession()
                .getAttribute(SESSION_KEY_CURRENT_OPERATOR_AUTHREF_MULTIVALUEMAP);
        return authItemRefMap == null ? new LinkedMultiValueMap<String, AuthItemRef>()
                : authItemRefMap;
    }
    
    /**
      * 从session中根据权限id获取权限项引用集合<br/>
      * <功能详细描述>
      * @param authItemId
      * @return [参数说明]
      * 
      * @return List<AuthItemRef> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static List<AuthItemRef> getAuthRefListFromSession(String authItemId) {
        @SuppressWarnings("unchecked")
        MultiValueMap<String, AuthItemRef> authItemRefMap = (MultiValueMap<String, AuthItemRef>) currentSessionContext.get()
                .getSession()
                .getAttribute(SESSION_KEY_CURRENT_OPERATOR_AUTHREF_MULTIVALUEMAP);
        List<AuthItemRef> authItemRefList = null;
        if (authItemRefMap != null) {
            authItemRefList = authItemRefMap.get(authItemId);
        }
        return authItemRefList;
    }
    
    /**
      * 获取当前所有权限引用对应的权限项列表
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return List<AuthItem> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static List<AuthItem> getAuthItemListDependAuthRefOfSession() {
        Map<String, AuthItem> authItemMapping = AuthContext.getContext()
                .getAllAuthItemMapping();
        
        List<AuthItem> authItemList = new ArrayList<AuthItem>();
        MultiValueMap<String, AuthItemRef> authRefMulMap = getAuthRefMultiValueMapFromSession();
        for (String authIdTemp : authRefMulMap.keySet()) {
            if (!authItemMapping.containsKey(authIdTemp)) {
                continue;
            }
            authItemList.add(authItemMapping.get(authIdTemp));
        }
        return authItemList;
    }
    
    /**
      * 根据父级权限id获取当前人员拥有的权限权限项列表
      *<功能详细描述>
      * @param parentId
      * @return [参数说明]
      * 
      * @return List<AuthItem> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static List<AuthItem> getAuthItemListByParentIdFromSession(
            String parentId) {
        AssertUtils.notEmpty(parentId, "parentId is empty.");
        
        Map<String, AuthItem> authItemMapping = AuthContext.getContext()
                .getAllAuthItemMapping();
        List<AuthItem> resList = new ArrayList<AuthItem>();
        MultiValueMap<String, AuthItemRef> authRefMulMap = getAuthRefMultiValueMapFromSession();
        for (String authIdTemp : authRefMulMap.keySet()) {
            if (!authItemMapping.containsKey(authIdTemp)) {
                continue;
            }
            AuthItem authItemTemp = authItemMapping.get(authIdTemp);
            if (parentId.equals(authItemTemp.getParentId())) {
                resList.add(authItemTemp);
            }
        }
        return resList;
    }
    
    /**
      * 根据父级权限id以及权限类型获取当前人员拥有的权限权限项列表
      *<功能详细描述>
      * @param authType
      * @param parentId
      * @return [参数说明]
      * 
      * @return List<AuthItem> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static List<AuthItem> getAuthItemListByAuthTypeAndParentIdFromSession(
            String authType, String parentId) {
        AssertUtils.notEmpty(parentId, "parentId is empty.");
        AssertUtils.notEmpty(authType, "authType is empty.");
        
        Map<String, AuthItem> authItemMapping = AuthContext.getContext()
                .getAllAuthItemMapping();
        
        List<AuthItem> resList = new ArrayList<AuthItem>();
        MultiValueMap<String, AuthItemRef> authRefMulMap = getAuthRefMultiValueMapFromSession();
        for (String authIdTemp : authRefMulMap.keySet()) {
            if (!authItemMapping.containsKey(authIdTemp)) {
                continue;
            }
            
            AuthItem authItemTemp = authItemMapping.get(authIdTemp);
            if (parentId.equals(authItemTemp.getParentId())
                    && authType.equals(authItemTemp.getAuthType())) {
                resList.add(authItemTemp);
            }
        }
        return resList;
    }
    
    /**
      * 获取当前拥有的永久类型权限项列表
      * <功能详细描述>
      * @return [参数说明]
      * 
      * @return List<AuthItem> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static List<AuthItem> getPerpetualAuthItemListDependAuthRefOfSession() {
        Map<String, AuthItem> authItemMapping = AuthContext.getContext()
                .getAllAuthItemMapping();
        
        List<AuthItem> authItemList = new ArrayList<AuthItem>();
        MultiValueMap<String, AuthItemRef> authRefMulMap = getAuthRefMultiValueMapFromSession();
        for (Entry<String, List<AuthItemRef>> entryTemp : authRefMulMap.entrySet()) {
            if (!authItemMapping.containsKey(entryTemp.getKey())) {
                continue;
            }
            
            AuthItem authItemTemp = authItemMapping.get(entryTemp.getKey());
            List<AuthItemRef> authItemRefListTemp = entryTemp.getValue();
            if (!CollectionUtils.isEmpty(authItemRefListTemp)) {
                for (AuthItemRef authItemRefTemp : authItemRefListTemp) {
                    if (!authItemRefTemp.isTemp()) {
                        //只压入非临时权限
                        authItemList.add(authItemTemp);
                    }
                }
            }
        }
        return authItemList;
    }
    
    /**
      * 获取当前线程中的会话
      * <功能详细描述>
      * @return [参数说明]
      * 
      * @return CurrentSessionContext [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static CurrentSessionContext getCurrentSessionContext() {
        return currentSessionContext.get();
    }
    
    /**
     * 将当前会话绑定到线程中
     * <功能详细描述>
     * @param request
     * @param response [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public static void bindCurrentSessionToThread(HttpServletRequest request,
            HttpServletResponse response) {
        //绑定线程前先remove一次，以保证不会残留上一次的会话，虽然不是特别需要，也不会占用太多资源
        currentSessionContext.remove();
        
        //将当前会话绑定到现成中
        currentSessionContext.get().install(request, response);
    }
    
    /**
      * 从当前线程中移除当前会话
      * <功能详细描述> [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public static void removeCurrentSessionFromThread() {
        currentSessionContext.get().uninstall();
        currentSessionContext.remove();
    }
}
