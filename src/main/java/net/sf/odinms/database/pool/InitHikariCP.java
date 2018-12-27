package net.sf.odinms.database.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author EasyZhang
 * hikariCP 初始化
 * @date 2018/12/27 -  17:04
 */

public class InitHikariCP {

    private static final Logger logger = LoggerFactory.getLogger(InitHikariCP.class);
    private static HikariDataSource ds = null;

    public static void init(){
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(100);
        config.setDataSourceClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", "3306");
        config.addDataSourceProperty("databaseName", "mydb");
        config.addDataSourceProperty("user", "bart");
        config.addDataSourceProperty("password", "51mp50n");
        ds = new HikariDataSource(config);
    }

    public static Connection getDataSource() throws Exception{
        if(Objects.isNull(ds)){
            init();
        }
        return ds.getConnection();
    }
}
