package site.xiweihai.framework.controller;

import lombok.extern.slf4j.Slf4j;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.i18n.annotation.I18n;
import site.xiweihai.framework.InitDb;

import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * 首页控制器
 *
 * @author hai
 * @since 2024/08/12
 */
@I18n
@Controller
@Slf4j
public class IndexController {

    /**
     * 处理根路径请求，提供视图展示功能
     *
     * @param ctx 上下文对象，用于处理请求和响应
     */
    @Mapping("/")
    public void view(Context ctx) {
        // 通过ServiceProxy获取数据库服务实例
        AnylineService service = ServiceProxy.service("db");

        // 如果服务实例为空，则重定向到初始化页面
        if (service == null) {
            ctx.redirect("/init");
        }else{
            // 返回视图页面
            ctx.forward("/index.html");
        }
    }

    /**
     * 初始化数据库操作的控制器方法
     *
     * @param driver 数据库驱动类名
     * @param url 数据库连接URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @param ctx 上下文对象，用于重定向等操作
     * @param locale 用户区域设置，用于确定语言环境
     * @return 返回视图和模型数据
     */
    @Mapping("/init")
    public ModelAndView init(@Param(name = "driver") String driver,
                             @Param(name = "url") String url,
                             @Param(name = "username") String username,
                             @Param(name = "password") String password,
                             Context ctx,
                             Locale locale) {
        AnylineService service = ServiceProxy.service("db");
        if(service!=null){
            ctx.forward("/");
        }
        // 创建ModelAndView对象，指定视图模板
        ModelAndView model = new ModelAndView("initdb.ftl");
        // 向模型中添加语言环境信息
        model.put("lang", locale.getLanguage());
        // 向模型中添加标题信息
        model.put("title", "dock");
        model.put("driver", driver!=null?driver:"org.sqlite.JDBC");
        model.put("url", url!=null?url:"jdbc:sqlite:db.db");
        model.put("username", username!=null?username:"");
        model.put("password", password!=null?password:"");
        model.put("errorMsg", "initdb.initmsg");
        // 检查数据库驱动类名是否为空
        if (driver == null) {
            // 如果为空，向模型中添加错误信息并返回模型
            return model;
        }
        try {
            // 尝试初始化数据库
            InitDb.run(driver, url, username, password);
            // 将数据库连接信息写入配置文件
            try (FileOutputStream outputStream = new FileOutputStream("db_ext/_db.properties")) {
                Properties props = new Properties();
                props.put("db.driverClassName", driver);
                props.put("db.jdbcUrl", url);
                props.put("db.username", username);
                props.put("db.password", password);
                // 保存属性到输出流
                props.store(outputStream, "auto create");
            }
            // 重定向到主页
            ctx.redirect("/");
        } catch (Exception e) {
            // 捕获异常，记录错误日志
            log.error(e.getMessage(), e);
            // 向模型中添加错误信息
            model.put("errorMsg", "initdb.fail");
        }

        // 返回模型和视图
        return model;
    }

}

