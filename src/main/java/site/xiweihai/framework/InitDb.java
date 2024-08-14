package site.xiweihai.framework;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;

import javax.sql.DataSource;

/**
 * 初始化数据库执行类，通过界面传递数据库参数进行数据库的初始化
 * @author hai
 * @since 2024-08-12
 */
public class InitDb {

    /**
     * 初始化数据库连接并创建表
     * 本函数用于配置数据库连接池，并通过连接池创建数据源，进而注册到数据源持有器中，
     * 以便后续可以通过服务代理获取服务并执行DDL操作创建表
     *
     * @param driver 数据库驱动类名
     * @param url 数据库连接URL
     * @param user 数据库用户名
     * @param password 数据库密码
     */
    public static void run(String driver, String url, String user, String password) {
        // 配置连接池
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);

        // 创建数据源
        DataSource ds_sso = new HikariDataSource(hikariConfig);
        // 定义临时数据库标识
        String tempDb = "db";

        try {
            // 注册数据源到持有器
            DataSourceHolder.reg(tempDb, ds_sso);
            // 通过服务代理获取服务
            AnylineService service = ServiceProxy.service(tempDb);

            // 创建表结构
            Table menuTable = createMenuTable();
            service.ddl().create(menuTable);

            Table pluginTable = createPluginTable();
            service.ddl().create(pluginTable);
        } catch (Exception e) {
            // 异常处理：抛出运行时异常
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建插件信息表
     * 该方法用于构建存储插件信息的表结构，包括插件的编号、名称、版本等信息
     *
     * @return Table 返回一个包含插件信息表结构的Table对象
     */
    private static Table createPluginTable() {
        Table table = new Table("FRAMEWORK_PLUGIN"); // 创建表，表名为"FRAMEWORK_PLUGIN"

        // 添加列，设置列名为"ID"，类型为BIGINT，允许自增，设为主键，注释为"插件编号"
        table.addColumn("ID", "BIGINT").autoIncrement(true).setPrimary(true).setComment("插件编号");
        // 添加列，设置列名为"NAME"，类型为VARCHAR(50)，注释为"插件名称"
        table.addColumn("PLUGIN_NAME", "VARCHAR(50)").setComment("插件名称");
        // 添加列，设置列名为"VERSION"，类型为VARCHAR(50)，注释为"插件版本"
        table.addColumn("PLUGIN_VERSION", "VARCHAR(50)").setComment("插件版本");
        // 添加列，设置列名为"DESC"，类型为VARCHAR(255)，注释为"插件说明"
        table.addColumn("PLUGIN_DESC", "VARCHAR(255)").setComment("插件说明");
        // 添加列，设置列名为"PATH"，类型为VARCHAR(500)，不允许为空，注释为"插件存储路径"
        table.addColumn("PLUGIN_PATH", "VARCHAR(500)").nullable(false).setComment("插件存储路径");
        // 添加列，设置列名为"STATUS"，类型为INT(11)，不允许为空，注释为"插件状态"
        table.addColumn("PLUGIN_STATUS", "INT(11)").nullable(false).setComment("插件状态");
        // 添加列，设置列名为"UPDATE_TIME"，类型为DATETIME，注释为"更新时间"
        table.addColumn("UPDATE_TIME", "DATETIME").setComment("更新时间");

        // 设置表的注释为"插件信息存储表"
        table.setComment("插件信息存储表");
        // 设置表的字符集为"utf8"
        table.setCharset("utf8");

        return table; // 返回构建好的表结构对象
    }

    /**
     * 创建菜单信息表
     * 该方法定义了数据库中用于存储菜单信息的表的结构
     * 包括字段名称、字段类型、主键、自增长等属性
     * 以及表的注释和字符集设置
     *
     * @return 返回创建好的表对象
     */
    private static Table createMenuTable() {
        // 创建一个名为"FRAMEWORK_MENU"的表对象
        Table table = new Table("FRAMEWORK_MENU");

        // 添加一个名为"ID"的列，类型为BIGINT，设置为自增长和主键，注释为"菜单编号"
        table.addColumn("ID", "BIGINT").autoIncrement(true).setPrimary(true).setComment("菜单编号");
        // 添加一个名为"NAME"的列，类型为VARCHAR(50)，注释为"菜单名称"
        table.addColumn("MENU_NAME", "VARCHAR(50)").setComment("菜单名称");
        // 添加一个名为"PARENT_ID"的列，类型为BIGINT，注释为"父菜单编号"
        table.addColumn("PARENT_ID", "BIGINT").setComment("父菜单编号");
        // 添加一个名为"URL"的列，类型为VARCHAR(500)，注释为"菜单URL"
        table.addColumn("MENU_URL", "VARCHAR(500)").setComment("菜单URL");
        // 添加一个名为"ICON"的列，类型为VARCHAR(50)，注释为"菜单图标"
        table.addColumn("MENU_ICON", "VARCHAR(50)").setComment("菜单图标");
        // 添加一个名为"TYPE"的列，类型为INT(11)，注释为"菜单类型"
        table.addColumn("MENU_TYPE", "INT(11)").setComment("菜单类型");
        // 添加一个名为"ORDER"的列，类型为INT(11)，注释为"菜单排序"
        table.addColumn("MENU_ORDER", "INT(11)").setComment("菜单排序");
        // 添加一个名为"STATUS"的列，类型为INT(11)，注释为"菜单状态"
        table.addColumn("MENU_STATUS", "INT(11)").setComment("菜单状态");
        // 添加一个名为"UPDATE_TIME"的列，类型为DATETIME，注释为"更新时间"
        table.addColumn("UPDATE_TIME", "DATETIME").setComment("更新时间");

        // 设置表的注释为"菜单信息存储表"
        table.setComment("菜单信息存储表");
        // 设置表的字符集为utf8
        table.setCharset("utf8");

        // 返回创建好的表对象
        return table;
    }


}
