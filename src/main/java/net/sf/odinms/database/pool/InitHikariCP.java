package net.sf.odinms.database.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.odinms.database.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author EasyZhang
 * hikariCP 初始化
 * @date 2018/12/27 -  17:04
 */

public class InitHikariCP {

    private static final Logger logger = LoggerFactory.getLogger(InitHikariCP.class);
    private static HikariDataSource ds = null;

    public static boolean init(){
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(100);
        config.setDataSourceClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", "3306");
        config.addDataSourceProperty("databaseName", "mydb");
        config.addDataSourceProperty("user", "bart");
        config.addDataSourceProperty("password", "51mp50n");
        ds = new HikariDataSource(config);
        return !ds.isClosed();
    }

    /**
     * by reading hikariCP code
     * we know when ds is closed it will throw a exception
     * so restart the hikariCP
     */
    public static Connection getCollection(){
        if(Objects.isNull(ds) || ds.isClosed()){
            init();
        }
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            return getCollection();
        }
    }

    /**
     * execute the sql
     * @param sql
     * @return
     */
    public static PreparedStatement execute(String sql){
        logger.info("execute sql {}",sql);
        try {
            return getCollection().prepareStatement(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
            // i don't know what to return
            throw new DatabaseException("执行SQL异常");
        }
    }

    /**
     * close database cp pool
     */
    public static void close(){
        ds.close();
    }
}
