/*
 * 描          述:  <描述>
 * 修  改   人:  brady
 * 修改时间:  2013-1-23
 * <修改描述:>
 */
package com.tx.component.rule.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.transaction.annotation.Transactional;

import com.tx.component.rule.exceptions.RuleContextInitException;
import com.tx.component.rule.loader.RuleItem;
import com.tx.component.rule.loader.RuleRegister;
import com.tx.component.rule.loader.RuleStateEnum;
import com.tx.component.rule.session.RuleSession;
import com.tx.component.rule.transation.RuleSessionTransactionFactory;
import com.tx.core.TxConstants;
import com.tx.core.exceptions.util.AssertUtils;

/**
 * 规则容器<br/>
 *     系统启动时通过该规则容器加载已有的规则<br/>
 *     可以通过该容器实现规则重加载，添加新的规则<br/>
 * 
 * @author  brady
 * @version  [版本号, 2013-1-23]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class RuleContext extends RuleContextBuilder {
    
    /** 单例的rule容器 */
    private static Map<String, RuleContext> ruleContextMapping = new HashMap<String, RuleContext>();
    
    /** 获取规则容器实例 */
    public static RuleContext getContext(String name) {
        AssertUtils.notEmpty(name, "name is empty.");
        AssertUtils.isTrue(ruleContextMapping.containsKey(name),
                "ruleContext:[{}] is not exist or not inited.",
                new Object[] { name });
        
        RuleContext context = ruleContextMapping.get(name);
        return context;
    }
    
    /**
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            super.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuleContextInitException("规则容器初始化错误.", e);
        }
        ruleContextMapping.put(beanName, this);
    }
    
    /**
      * 获取规则会话事务工厂
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return RuleSessionTransactionFactory [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public RuleSessionTransactionFactory getRuleSessionTransactionFactory() {
        RuleSessionTransactionFactory res = this.ruleSessionTransactionFactory;
        return res;
    }
    
    /**
     * 判断容器中是否含有对应的规则<br/>
     *     1、如果存在规则则返回true,不存在返回false
     * <功能详细描述>
     * @param rule
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public boolean contains(String ruleKey) {
        AssertUtils.notEmpty(ruleKey, "ruleKey is empty.");
        
        if (this.ruleMap.containsKey(ruleKey)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
      * 根据规则key获取规则实例
      *     当对应规则不存在时，则返回null.
      *<功能详细描述>
      * @param ruleKey
      * @return [参数说明]
      * 
      * @return Rule [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public Rule getRuleByRuleKey(String ruleKey) {
        AssertUtils.notEmpty(ruleKey, "ruleKey is empty.");
        Rule resRule = this.ruleMap.get(ruleKey);
        return resRule;
    }
    
    /**
      * 根据规则实例构建规则会话实例<br/> 
      *<功能详细描述>
      * @param rule
      * @return [参数说明]
      * 
      * @return RuleSession [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public RuleSession buildRuleSession(Rule rule) {
        AssertUtils.notNull(rule, "rule is null.");
        AssertUtils.notEmpty(rule.getKey(), "rule.key is empty.");
        AssertUtils.notNull(rule.getRuleType(),
                "rule.ruleType is null.ruleKey:{}",
                rule.getKey());
        AssertUtils.isTrue(RuleStateEnum.OPERATION.equals(rule.getState()),
                "rule.state is not operation.ruleKey:{}",
                rule.getKey());
        AssertUtils.isTrue(this.ruleRegisterMap.containsKey(rule.getRuleType()),
                "un support ruleType.ruleKey:{} ruleType:{}",
                new Object[] { rule.getKey(), rule.getRuleType() });
        
        @SuppressWarnings("rawtypes")
        RuleRegister ruleRegister = this.ruleRegisterMap.get(rule.getRuleType());
        @SuppressWarnings("unchecked")
        RuleSession ruleSession = ruleRegister.buildRuleSession(rule);
        return ruleSession;
        
    }
    
    /**
      * 将规则注册入规则容器中
      * <功能详细描述>
      * @param spRule [参数说明]
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public Rule registerRule(RuleItem ruleItem) {
        validateRuleItem(ruleItem);
        final Rule realRule = buildRuleByRuleItem(ruleItem);
        this.ruleMap.put(ruleItem.getKey(), realRule);
        return null;
    }
    
    /**
      * 反注册规则
      * <功能详细描述>
      * @param rule [参数说明] serviceType.rule
      * 
      * @return void [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public boolean unRegisterRule(String ruleKey) {
        AssertUtils.notEmpty(ruleKey, "ruleKey is empty.");
        Rule ruleImpl = getRuleByRuleKey(ruleKey);
        if (ruleImpl == null) {
            return false;
        }
        
        boolean resFlag = this.ruleItemPersister.deleteRuleByRuleKey(ruleKey);
        ruleMap.remove(ruleKey);
        return resFlag;
    }
    
    /**
      * 获取所有规则集合<br/> 
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return List<Rule> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public List<Rule> getAllRuleList() {
        @SuppressWarnings("unchecked")
        List<Rule> ruleList = (List<Rule>) ListUtils.unmodifiableList(new ArrayList<>(
                this.ruleMap.values()));
        
        return ruleList;
    }
    
    /**
      * 获取所有规则的Map集合<br/>
      *<功能详细描述>
      * @return [参数说明]
      * 
      * @return Map<String,Rule> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public Map<String, Rule> getAllRuleMap() {
        @SuppressWarnings("unchecked")
        Map<String, Rule> resMap = MapUtils.unmodifiableMap(this.ruleMap);
        return resMap;
    }
    
    /**
      * 根据业务类型获取对应的规则集合<br/>
      *<功能详细描述>
      * @param serviceType
      * @return [参数说明]
      * 
      * @return List<Rule> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public List<Rule> getRuleListByServiceType(String serviceType) {
        AssertUtils.notEmpty(serviceType, "serviceType is empty.");
        
        List<Rule> ruleList = new ArrayList<>(
                TxConstants.INITIAL_CONLLECTION_SIZE);
        for (Rule ruleTemp : this.ruleMap.values()) {
            if (serviceType.equals(ruleTemp.getServiceType())) {
                ruleList.add(ruleTemp);
            }
        }
        @SuppressWarnings("unchecked")
        List<Rule> resList = ListUtils.unmodifiableList(ruleList);
        return resList;
    }
    
    /**
      * 获取指定业务类型的规则集<br/>
      *<功能详细描述>
      * @param serviceType
      * @return [参数说明]
      * 
      * @return Map<String,Rule> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public Map<String, Rule> getRuleMapByServiceType(String serviceType) {
        AssertUtils.notEmpty(serviceType, "serviceType is empty.");
        
        Map<String, Rule> ruleMap = new HashMap<>(TxConstants.INITIAL_MAP_SIZE);
        for (Entry<String, Rule> entryTemp : this.ruleMap.entrySet()) {
            if (serviceType.equals(entryTemp.getValue().getServiceType())) {
                ruleMap.put(entryTemp.getKey(), entryTemp.getValue());
            }
        }
        @SuppressWarnings("unchecked")
        Map<String, Rule> resMap = MapUtils.unmodifiableMap(ruleMap);
        return resMap;
    }
    
}
