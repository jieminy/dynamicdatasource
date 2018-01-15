package com.discipline.thyme.dynamicdatasource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Autowired
    private DataBaseManager datebaseManager;
    private Logger log = Logger.getLogger(this.getClass());
    // 默认数据源，也就是主库
    protected DataSource masterDataSource;
    // 保存动态创建的数据源
    private static final Map targetDataSource = new HashMap<>();

    @Override
    protected DataSource determineTargetDataSource() {
        // 根据数据库选择方案，拿到要访问的数据库
        String dataSourceName = determineCurrentLookupKey();
        if("dataSource".equals(dataSourceName)) {
            // 访问默认主库
            return masterDataSource;
        }

        // 根据数据库名字，从已创建的数据库中获取要访问的数据库
        DataSource dataSource = (DataSource) targetDataSource.get(dataSourceName);
        if(null == dataSource) {
            // 从已创建的数据库中获取要访问的数据库，如果没有则创建一个
            dataSource = this.selectDataSource(dataSourceName);
        }
        return dataSource;
    }
    /**
     * 该方法为同步方法，防止并发创建两个相同的数据库
     * 使用双检锁的方式，防止并发
     * @param dbType
     * @return
     */
    private synchronized DataSource selectDataSource(String dbType) {
        // 再次从数据库中获取，双检锁
        DataSource obj = (DataSource)this.targetDataSource.get(dbType);
        if (null != obj) {
            return obj;
        }
        // 为空则创建数据库
        BasicDataSource dataSource = this.getDataSource(dbType);
        if (null != dataSource) {
            // 将新创建的数据库保存到map中
            targetDataSource.put(dbType, dataSource);
            return dataSource;
        }else {
           // throw new Exception("创建数据源失败！");
            return dataSource;
        }
    }
    @Override
    protected String determineCurrentLookupKey() {
        // TODO Auto-generated method stub
        String dataSourceName = Dbs.getDbType();
        if (dataSourceName == null || dataSourceName == "dataSource") {
            // 默认的数据源名字
            dataSourceName = "dataSource";
        }
        log.debug("use datasource : " + dataSourceName);
        return dataSourceName;
    }

    /**
     * 查询对应数据库的信息
     * @param dbtype
     * @return
     */
    private BasicDataSource getDataSource(String dbtype) {
        String oriType = Dbs.getDbType();
        // 先切换回主库
        Dbs.setDbType("dataSource");
        // 查询所需信息
        DataBaseConfig datebase = datebaseManager.getConfigByName(dbtype);
        // 切换回目标库
        Dbs.setDbType(oriType);

        BasicDataSource dataSource = createDataSource("oracle.jdbc.OracleDriver",datebase.getUrl(),datebase.getUsername(),datebase.getPassword());
        return dataSource;
    }

    //创建数据源
    private BasicDataSource createDataSource(String driverClassName, String url,
                                             String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setTestWhileIdle(true);

        return dataSource;
    }
}
