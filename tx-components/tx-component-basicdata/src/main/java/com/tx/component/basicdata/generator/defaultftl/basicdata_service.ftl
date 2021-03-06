/*
 * 描          述:  <描述>
 * 修  改   人:  
 * 修改时间:  
 * <修改描述:>
 */
package ${service.basePackage}.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
<#list service.extentionTypeNames as typeName>
import ${typeName};
</#list> 

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ${service.basePackage}.dao.${service.entitySimpleName}Dao;
import ${service.basePackage}.model.${service.entitySimpleName};
import com.tx.core.exceptions.util.AssertUtils;
import com.tx.core.paged.model.PagedList;

/**
 * ${service.entitySimpleName}的业务层
 * <功能详细描述>
 * 
 * @author  
 * @version  [版本号, ]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component("${service.lowerCaseEntitySimpleName}Service")
public class ${service.entitySimpleName}Service {
    
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(${service.entitySimpleName}Service.class);
    
    @Resource(name = "${service.lowerCaseEntitySimpleName}Dao")
    private ${service.entitySimpleName}Dao ${service.lowerCaseEntitySimpleName}Dao;
    
    /**
      * 将${service.lowerCaseEntitySimpleName}实例插入数据库中保存
      * 1、如果${service.lowerCaseEntitySimpleName}为空时抛出参数为空异常
      * 2、如果${service.lowerCaseEntitySimpleName}中部分必要参数为非法值时抛出参数不合法异常
     * <功能详细描述>
     * @param ${service.lowerCaseEntitySimpleName} [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws
     * @see [类、类#方法、类#成员]
    */
    @Transactional
    public void insert${service.entitySimpleName}(${service.entitySimpleName} ${service.lowerCaseEntitySimpleName}) {
        //FIXME:验证参数是否合法
        AssertUtils.notNull(${service.lowerCaseEntitySimpleName}, "${service.lowerCaseEntitySimpleName} is null.");
        
        
        //FIXME:为添加的数据需要填入默认值的字段填入默认值
        
        
        //调用数据持久层对实体进行持久化操作
        this.${service.lowerCaseEntitySimpleName}Dao.insert${service.entitySimpleName}(${service.lowerCaseEntitySimpleName});
    }
      
     /**
      * 根据${service.idPropertyName}删除${service.lowerCaseEntitySimpleName}实例
      * 1、如果入参数为空，则抛出异常
      * 2、执行删除后，将返回数据库中被影响的条数
      * @param ${service.idPropertyName}
      * @return 返回删除的数据条数，<br/>
      * 有些业务场景，如果已经被别人删除同样也可以认为是成功的
      * 这里讲通用生成的业务层代码定义为返回影响的条数
      * @return int [返回类型说明]
      * @exception throws 
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public boolean deleteBy${service.upCaseIdPropertyName}(String ${service.idPropertyName}) {
        AssertUtils.notEmpty(${service.idPropertyName}, "${service.idPropertyName} is empty.");
        
        ${service.entitySimpleName} condition = new ${service.entitySimpleName}();
        condition.set${service.upCaseIdPropertyName}(${service.idPropertyName});
        int resInt = this.${service.lowerCaseEntitySimpleName}Dao.delete${service.entitySimpleName}(condition);
        return resInt > 0;
    }
    
    /**
      * 根据${service.upCaseIdPropertyName}查询${service.entitySimpleName}实体
      * 1、当${service.idPropertyName}为empty时抛出异常
      * <功能详细描述>
      * @param ${service.idPropertyName}
      * @return [参数说明]
      * 
      * @return ${service.entitySimpleName} [返回类型说明]
      * @exception throws 可能存在数据库访问异常DataAccessException
      * @see [类、类#方法、类#成员]
     */
    public ${service.entitySimpleName} find${service.entitySimpleName}By${service.upCaseIdPropertyName}(String ${service.idPropertyName}) {
        AssertUtils.notEmpty(${service.idPropertyName}, "${service.idPropertyName} is empty.");
        
        ${service.entitySimpleName} condition = new ${service.entitySimpleName}();
        condition.set${service.upCaseIdPropertyName}(${service.idPropertyName});
        
        ${service.entitySimpleName} res = this.${service.lowerCaseEntitySimpleName}Dao.find${service.entitySimpleName}(condition);
        return res;
    }
    
    /**
      * 查询${service.entitySimpleName}实体列表
<#if !StringUtils.isEmpty(validPropertyName)>
      *     不包含无效的实体
</#if>
      * <功能详细描述>
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
      * @param ${key}
</#if>
</#list> 
      *       
      * @return [参数说明]
      * 
      * @return List<${service.entitySimpleName}> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
      */
    public List<${service.entitySimpleName}> query${service.entitySimpleName}List(
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		${service.queryConditionName2TypeNameMapping[key]} ${key}<#if key_has_next>,</#if>
</#if>
</#list>    
    	) {
        //判断条件合法性
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		params.put("${key}",${key});
</#if>
</#list> 
<#if !StringUtils.isEmpty(validPropertyName)>
		params.put("${validPropertyName}",true);
</#if>  
        
        //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
        List<${service.entitySimpleName}> resList = this.${service.lowerCaseEntitySimpleName}Dao.query${service.entitySimpleName}List(params);
        
        return resList;
    }
    
<#if !StringUtils.isEmpty(validPropertyName)>
	 /**
      * 查询${service.entitySimpleName}实体列表,
      *		如果实体集合中不包含appoint${service.upCaseIdPropertyName}对应的实体将根据该id查询到对应的实体然后加入到集合中（appoint${service.upCaseIdPropertyName}被禁用的情况）
      *     appoint${service.upCaseIdPropertyName}对应的实体如果不存在，系统将抛出异常（appoint${service.upCaseIdPropertyName}被删除的情况）
      * <功能详细描述>
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
      * @param ${key}
</#if>
</#list> 
      *       
      * @return [参数说明]
      * 
      * @return List<${service.entitySimpleName}> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
      */
	public List<${service.entitySimpleName}> query${service.entitySimpleName}ListIncludeAppoint${service.upCaseIdPropertyName}(
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
			${service.queryConditionName2TypeNameMapping[key]} ${key},
</#if>
</#list>
		String appoint${service.upCaseIdPropertyName}) {
       //判断条件合法性
       AssertUtils.notEmpty(appoint${service.upCaseIdPropertyName}, "appoint${service.upCaseIdPropertyName} is empty.");
       
       //生成查询条件
       Map<String, Object> params = new HashMap<String, Object>();
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		params.put("${key}",${key});
</#if>
</#list> 
<#if !StringUtils.isEmpty(validPropertyName)>
		params.put("${validPropertyName}",true);
</#if>  
       
       //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
       List<${service.entitySimpleName}> resList = this.${service.lowerCaseEntitySimpleName}Dao.query${service.entitySimpleName}List(params);
       
       Set<String> ${service.idPropertyName}Set = new HashSet<String>();
       for(${service.entitySimpleName} temp : resList){
           ${service.idPropertyName}Set.add(temp.get${service.upCaseIdPropertyName}());
       }
       if(!${service.idPropertyName}Set.contains(appoint${service.upCaseIdPropertyName})){
       	   ${service.entitySimpleName} findRes = find${service.entitySimpleName}By${service.upCaseIdPropertyName}(appoint${service.upCaseIdPropertyName});
           resList.add(0, findRes);
       }
       
       return resList;
   }

    /**
      * 查询包含已经停用的${service.entitySimpleName}实体列表
      * <功能详细描述>
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
      * @param ${key}
</#if>
</#list> 
      *       
      * @return [参数说明]
      * 
      * @return List<${service.entitySimpleName}> [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public List<${service.entitySimpleName}> query${service.entitySimpleName}ListIncludeInvalid(
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		${service.queryConditionName2TypeNameMapping[key]} ${key}<#if key_has_next>,</#if>
</#if>
</#list>       
    	) {
        //判断条件合法性
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		params.put("${key}",${key});
</#if>
</#list> 
        
        //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
        List<${service.entitySimpleName}> resList = this.${service.lowerCaseEntitySimpleName}Dao.query${service.entitySimpleName}List(params);
        
        return resList;
    }
</#if> 
    
    /**
     * 分页查询${service.entitySimpleName}实体列表
<#if !StringUtils.isEmpty(validPropertyName)>
      *     不包含无效的实体
</#if>
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
      * @param ${key}
</#if>
</#list>
     * @param pageIndex 当前页index从1开始计算
     * @param pageSize 每页显示行数
     * 
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return List<${service.entitySimpleName}> [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public PagedList<${service.entitySimpleName}> query${service.entitySimpleName}PagedList(
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
			${service.queryConditionName2TypeNameMapping[key]} ${key},
</#if>
</#list>
    		int pageIndex,
            int pageSize) {
        //T判断条件合法性
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		params.put("${key}",${key});
</#if>
</#list>       
<#if !StringUtils.isEmpty(validPropertyName)>
		params.put("${validPropertyName}",true);
</#if>  
 
        //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
        PagedList<${service.entitySimpleName}> resPagedList = this.${service.lowerCaseEntitySimpleName}Dao.query${service.entitySimpleName}PagedList(params, pageIndex, pageSize);
        
        return resPagedList;
    }
    
<#if !StringUtils.isEmpty(validPropertyName)>
    /**
     * 分页查询${service.entitySimpleName}实体列表
     *     包含无效的的实体
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
      * @param ${key}
</#if>
</#list>
     * @param pageIndex 当前页index从1开始计算
     * @param pageSize 每页显示行数
     * 
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return List<${service.entitySimpleName}> [返回类型说明]
     * @exception throws [异常类型] [异常说明]
     * @see [类、类#方法、类#成员]
    */
    public PagedList<${service.entitySimpleName}> query${service.entitySimpleName}PagedListIncludeInvalid(
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
			${service.queryConditionName2TypeNameMapping[key]} ${key},
</#if>
</#list>
    		int pageIndex,
            int pageSize) {
        //T判断条件合法性
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
<#list service.queryConditionName2TypeNameMapping?keys as key>
<#if validPropertyName != key>
		params.put("${key}",${key});
</#if>
</#list> 
 
        //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
        PagedList<${service.entitySimpleName}> resPagedList = this.${service.lowerCaseEntitySimpleName}Dao.query${service.entitySimpleName}PagedList(params, pageIndex, pageSize);
        
        return resPagedList;
    }
</#if> 
    
     /**
      * 判断是否已经存在<br/>
      * <功能详细描述>
      * @return [参数说明]
      * 
      * @return int [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    public boolean isExist(Map<String,String> key2valueMap, String exclude${service.upCaseIdPropertyName}) {
        AssertUtils.notEmpty(key2valueMap, "key2valueMap is empty");
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.putAll(key2valueMap);
        params.put("exclude${service.upCaseIdPropertyName}", exclude${service.upCaseIdPropertyName});
        
        //根据实际情况，填入排序字段等条件，根据是否需要排序，选择调用dao内方法
        int res = this.${service.lowerCaseEntitySimpleName}Dao.count${service.entitySimpleName}(params);
        
        return res > 0;
    }
    
    /**
      * 根据${service.idPropertyName}更新对象
      * <功能详细描述>
      * @param ${service.lowerCaseEntitySimpleName}
      * @return [参数说明]
      * 
      * @return boolean [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public boolean updateBy${service.upCaseIdPropertyName}(${service.entitySimpleName} ${service.lowerCaseEntitySimpleName}) {
        //FIXME:验证参数是否合法，必填字段是否填写，
        AssertUtils.notNull(${service.lowerCaseEntitySimpleName}, "${service.lowerCaseEntitySimpleName} is null.");
        AssertUtils.notEmpty(${service.lowerCaseEntitySimpleName}.get${service.upCaseIdPropertyName}(), "${service.lowerCaseEntitySimpleName}.${service.idPropertyName} is empty.");
        
        
        //生成需要更新字段的hashMap
        Map<String, Object> updateRowMap = new HashMap<String, Object>();
        updateRowMap.put("${service.idPropertyName}", ${service.lowerCaseEntitySimpleName}.get${service.upCaseIdPropertyName}());
        
        //FIXME:需要更新的字段
<#list service.updateAbleName2SqlMapColumnMapping?values as column>
<#if !column.isId()>
<#if column.isSimpleType()>
		updateRowMap.put("${column.propertyName}", ${service.lowerCaseEntitySimpleName}.${column.getterMethodSimpleName}());	
<#else>
		//type:${column.javaType.name}
		updateRowMap.put("${column.propertyName}", ${service.lowerCaseEntitySimpleName}.${column.getterMethodSimpleName}());
</#if>
</#if>
</#list>
        int updateRowCount = this.${service.lowerCaseEntitySimpleName}Dao.update${service.entitySimpleName}(updateRowMap);
        
        //如果需要大于1时，抛出异常并回滚，需要在这里修改
        return updateRowCount >= 1;
    }
    
<#if !StringUtils.isEmpty(validPropertyName)>
     /**
      * 根据${service.idPropertyName}禁用${service.entitySimpleName}<br/>
      * <功能详细描述>
      * @param ${service.idPropertyName}
      * @return [参数说明]
      * 
      * @return boolean [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public boolean disableBy${service.upCaseIdPropertyName}(String ${service.idPropertyName}) {
        AssertUtils.notEmpty(${service.idPropertyName}, "${service.idPropertyName} is empty.");
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${service.idPropertyName}", ${service.idPropertyName});
        params.put("${validPropertyName}", false);
        
        this.${service.lowerCaseEntitySimpleName}Dao.update${service.entitySimpleName}(params);
        
        return true;
    }
    
    /**
      * 根据${service.idPropertyName}启用${service.entitySimpleName}<br/>
      * <功能详细描述>
      * @param postId
      * @return [参数说明]
      * 
      * @return boolean [返回类型说明]
      * @exception throws [异常类型] [异常说明]
      * @see [类、类#方法、类#成员]
     */
    @Transactional
    public boolean enableBy${service.upCaseIdPropertyName}(String ${service.idPropertyName}) {
        AssertUtils.notEmpty(${service.idPropertyName}, "${service.idPropertyName} is empty.");
        
        //生成查询条件
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("${service.idPropertyName}", ${service.idPropertyName});
        params.put("${validPropertyName}", true);
        
        this.${service.lowerCaseEntitySimpleName}Dao.update${service.entitySimpleName}(params);
        
        return true;
    }
</#if>
}
