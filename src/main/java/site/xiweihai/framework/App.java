package site.xiweihai.framework;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.anyline.adapter.init.DefaultEnvironmentWorker;
import org.anyline.data.datasource.DataSourceHolder;
import org.noear.solon.Solon;
import org.noear.solon.SolonProps;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class App {
    /**
     * 应用程序的主入口点
     * 初始化并启动Solon应用程序，并在应用程序上下文中注册数据源
     * 如果配置中包含数据库连接信息，则初始化数据库连接池
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动Solon应用程序
        Solon.start(App.class, args, app -> {
            // 启动数据库框架默认的环境工作器
            DefaultEnvironmentWorker.start();
            // 检查配置中是否包含数据库连接URL，以决定是否初始化数据库连接池
            SolonProps cfg = Solon.cfg();
            if (cfg.containsKey("db.jdbcUrl")) {
                // 创建并配置HikariCP连接池
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setDriverClassName(cfg.get("db.driverClassName"));
                hikariConfig.setJdbcUrl(cfg.get("db.jdbcUrl"));
                hikariConfig.setUsername(cfg.get("db.username"));
                hikariConfig.setPassword(cfg.get("db.password"));
                // 使用配置好的连接池创建数据源
                HikariDataSource dataSource = new HikariDataSource(hikariConfig);
                // 注册数据源到上下文中，标识为"db"
                DataSourceHolder.reg("db", dataSource);
            }
        });
    }
}