package site.xiweihai.framework;

import org.anyline.adapter.init.DefaultEnvironmentWorker;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.test.SolonTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InitDbTest {

    @BeforeAll
    static void init() {
        // 启动数据库框架默认的环境工作器
        DefaultEnvironmentWorker.start();
    }
    @Test
    void mysqlInit() {
        InitDb.run("com.mysql.cj.jdbc.Driver", "jdbc:mysql://127.0.0.1:4000/framework?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai", "root", "123456");
        AnylineService service = ServiceProxy.service("db");
        assertEquals(1,service.tables("FRAMEWORK_MENU").size());
        assertEquals(1,service.tables("FRAMEWORK_PLUGIN").size());
    }

    @Test
    void postgresqlInit() {
        InitDb.run("org.postgresql.Driver", "jdbc:postgresql://127.0.0.1:4001/", "postgres", "123456");
        AnylineService service = ServiceProxy.service("db");
        assertEquals(1,service.tables("FRAMEWORK_MENU").size());
        assertEquals(1,service.tables("FRAMEWORK_PLUGIN").size());
    }
    @Test
    void sqliteInit() {
        InitDb.run("org.sqlite.JDBC", "jdbc:sqlite:db.db", "", "");
        AnylineService service = ServiceProxy.service("db");
        assertEquals(1,service.tables("FRAMEWORK_MENU").size());
        assertEquals(1,service.tables("FRAMEWORK_PLUGIN").size());
    }
}