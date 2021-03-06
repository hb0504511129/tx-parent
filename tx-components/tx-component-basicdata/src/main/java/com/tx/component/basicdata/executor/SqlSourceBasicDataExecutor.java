/*
 * 描          述:  <描述>
 * 修  改   人:  brady
 * 修改时间:  2013-9-2
 * <修改描述:>
 */
package com.tx.component.basicdata.executor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.tx.component.basicdata.annotation.BasicData;
import com.tx.component.basicdata.context.BasicDataContextConfigurator;
import com.tx.core.jdbc.sqlsource.SqlSource;
import com.tx.core.jdbc.sqlsource.SqlSourceBuilder;
import com.tx.core.paged.model.PagedList;
import com.tx.core.util.ObjectUtils;

/**
 * 基础数据容器默认执行器<br/>
 * <功能详细描述>
 * 
 * @author  brady
 * @version  [版本号, 2013-9-2]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class SqlSourceBasicDataExecutor<T> extends BaseBasicDataExecutor<T> {
    
    private SqlSource<T> simpleSqlSource;
    
    private SqlSourceBuilder simpleSqlSourceBuilder = new SqlSourceBuilder();
    
    private boolean cacheEnable;
    
    public SqlSourceBasicDataExecutor(Class<T> type,
            BasicDataContextConfigurator configurator) {
        super(type, configurator);
        
        this.simpleSqlSource = simpleSqlSourceBuilder.build(type,
                configurator.getDataSourceType().getDialect());
        
        if (type.isAnnotationPresent(BasicData.class)) {
            BasicData basicDataAnno = type.getAnnotation(BasicData.class);
            this.cacheEnable = basicDataAnno.isCache();
        } else {
            this.cacheEnable = true;
        }
    }
    
    /**
     * @return
     */
    @Override
    protected boolean isCacheEnable() {
        return cacheEnable;
    }
    
    /**
     * @param method
     * @param params
     * @return
     */
    @Override
    protected String generateCacheKey(String method, Object... params) {
        if ("getMultiValueMap".equals(method)) {
            return "cachekey_for_getMultiValueMap_" + params[0];
        } else if ("find".equals(method)) {
            return "cachekey_for_find_" + params[0];
        } else if ("query".equals(method)) {
            int hashCode = "query".hashCode();
            if (params[0] != null) {
                LinkedHashMap<String, Object> paramMap = simpleSqlSource.getQueryCondtionParamMaps(params[0]);
                for (Entry<String, Object> entryTemp : paramMap.entrySet()) {
                    if (ObjectUtils.isEmpty(entryTemp.getValue())) {
                        continue;
                    }
                    hashCode += entryTemp.getKey().hashCode()
                            + entryTemp.getValue().hashCode();
                }
            }
            String resKey = "cachekey_for_query_" + hashCode;
            return resKey;
        } else if ("count".equals(method)) {
            int hashCode = "count".hashCode();
            if (params[0] != null) {
                LinkedHashMap<String, Object> paramMap = simpleSqlSource.getQueryCondtionParamMaps(params[0]);
                for (Entry<String, Object> entryTemp : paramMap.entrySet()) {
                    if (ObjectUtils.isEmpty(entryTemp.getValue())) {
                        continue;
                    }
                    hashCode += entryTemp.getKey().hashCode()
                            + entryTemp.getValue().hashCode();
                }
            }
            String resKey = "cachekey_for_count_" + hashCode;
            return resKey;
        } else if ("queryPagedList".equals(method)) {
            int hashCode = "query".hashCode();
            if (params[0] != null) {
                LinkedHashMap<String, Object> paramMap = simpleSqlSource.getQueryCondtionParamMaps(params[0]);
                for (Entry<String, Object> entryTemp : paramMap.entrySet()) {
                    if (ObjectUtils.isEmpty(entryTemp.getValue())) {
                        continue;
                    }
                    hashCode += entryTemp.getKey().hashCode();
                    hashCode += entryTemp.getValue().hashCode();
                }
            }
            hashCode += (Integer) params[1];
            hashCode += (Integer) params[2];
            String resKey = "cachekey_for_queryPaged_" + hashCode;
            return resKey;
        } else {
            int hashCode = method.hashCode();
            if (params != null) {
                for (Object obj : params) {
                    if (obj != null) {
                        hashCode += obj.hashCode();
                    }
                }
            }
            String resKey = method + hashCode;
            return resKey;
        }
    }
    
    /**
     * @param pk
     * @return
     */
    @Override
    protected T doFind(String pk) {
        List<T> resList = getJdbcTemplate().query(simpleSqlSource.findSql(),
                new Object[] { pk },
                simpleSqlSource.getSelectRowMapper());
        return DataAccessUtils.singleResult(resList);
    }
    
    /**
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    protected PagedList<T> doQueryPagedList(Map<String, Object> params,
            int pageIndex, int pageSize) {
        int count = count(params);
        
        PagedList<T> result = new PagedList<T>();
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setCount(count);
        
        /* 如果count得到的结果为0则不再继续查询具体的哪几条 */
        if (count == 0) {
            return result;
        }
        int offset = pageSize * (pageIndex - 1);
        int limit = pageSize * pageIndex;
        limit = limit > count ? count : limit;
        
        List<T> resList = getJdbcTemplate().query(simpleSqlSource.queryPagedSql(params,
                pageIndex,
                pageSize),
                simpleSqlSource.getPagedQueryCondtionSetter(params,
                        offset,
                        limit),
                simpleSqlSource.getSelectRowMapper());
        result.setList(resList);
        
        return result;
    }
    
    /**
     * @param params
     * @return
     */
    @Override
    protected List<T> doQuery(Map<String, Object> params) {
        List<T> resList = getJdbcTemplate().query(simpleSqlSource.querySql(params),
                simpleSqlSource.getQueryCondtionSetter(params),
                simpleSqlSource.getSelectRowMapper());
        return resList;
    }
    
    /**
     * @param params
     * @return
     */
    @Override
    protected int doCount(Map<String, Object> params) {
        RowMapper<Integer> integerRowMapper = new SingleColumnRowMapper<Integer>(
                Integer.class);
        List<Integer> resList = getJdbcTemplate().query(simpleSqlSource.countSql(params),
                simpleSqlSource.getQueryCondtionSetter(params),
                integerRowMapper);
        int res = DataAccessUtils.singleResult(resList);
        return res;
    }
    
    /**
     * @param obj
     */
    @Override
    protected void doInsert(T obj) {
        getJdbcTemplate().update(simpleSqlSource.insertSql(),
                simpleSqlSource.getInsertSetter(obj));
    }
    
    /**
     * @param params
     * @return
     */
    @Override
    protected int doUpdate(Map<String, Object> params) {
        int res = getJdbcTemplate().update(simpleSqlSource.updateSql(params),
                simpleSqlSource.getUpdateSetter(params));
        return res;
    }
    
    /**
     * @param pk
     * @return
     */
    @Override
    protected int doDelete(String pk) {
        int res = getJdbcTemplate().update(simpleSqlSource.deleteSql(), pk);
        return res;
    }
}
