package site.xiweihai.framework.controller;

import ch.qos.logback.core.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.i18n.annotation.I18n;
import org.noear.solon.validation.annotation.NotEmpty;
import site.xiweihai.framework.utils.ToolUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 插件管理控制器
 *
 * @author hai
 * @since 2024/08/12
 */
@I18n
@Mapping("/plugin")
@Controller
@Slf4j
public class PluginController {

    private final String pluginPath = "plugins";

    /**
     * 映射到默认路径的处理器方法
     *
     * @param errorMsg 错误消息
     * @param locale   本地化信息
     * @return modelAndView
     */
    @Mapping("")
    public ModelAndView index(String errorMsg, Locale locale) {
        // 创建ModelAndView对象，指定视图名称为"plugin.ftl"
        ModelAndView modelAndView = new ModelAndView("plugin.ftl");

        // 将当前语言信息添加到modelAndView，以便在视图中使用
        modelAndView.put("lang", locale.getLanguage());

        // 通过ServiceProxy获取到AnylineService服务实例
        AnylineService service = ServiceProxy.service("db");

        // 通过服务实例查询并获取框架插件数据
        DataSet frameworkPlugin = service.querys("FRAMEWORK_PLUGIN");

        // 将查询到的框架插件数据添加到modelAndView，以便在视图中展示
        modelAndView.put("data", frameworkPlugin);

        // 将错误消息添加到modelAndView，如果errorMsg为空，则使用"defaultErrorMsg"作为默认值
        modelAndView.put("errorMsg", errorMsg != null ? errorMsg : "empty");

        // 返回modelAndView对象
        return modelAndView;
    }

    /**
     * 映射到"/add"路径，支持多部分上传
     *
     * @param locale        本地化设置，用于国际化
     * @param pluginName    插件名称
     * @param pluginDesc    插件描述
     * @param pluginVersion 插件版本
     * @param ctx           上下文对象，用于获取上传文件等
     * @return modelAndView
     * @throws IOException
     */
    @Mapping(path = "/add")
    public ModelAndView add(
            Locale locale,          // 本地化设置，用于国际化
            String pluginName,      // 插件名称
            String pluginDesc,      // 插件描述
            String pluginVersion,   // 插件版本
            Context ctx             // 上下文对象，用于获取上传文件等
    ) throws IOException {
        String errorMsg = "plugin.add_init_msg"; // 默认错误信息
        UploadedFile file = ctx.file("file");    // 从上下文中获取上传的文件
        // 检查文件和必要信息是否已提供
        if (file != null && !StringUtil.isEmpty(pluginName) && !StringUtil.isEmpty(pluginDesc) && !StringUtil.isEmpty(pluginVersion)) {
            try {
                // 构造文件保存路径
                String savePath = pluginPath + File.separator + pluginName + File.separator + pluginVersion + File.separator + file.getName();
                // 确保所需目录存在
                FileUtil.createMissingParentDirectories(new File(savePath));
                // 只有是.jar文件才进行保存
                if (file.getName().endsWith(".jar")) {
                    // 把上传的文件保存到指定路径
                    file.transferTo(new File(savePath));
                    // 初始化数据库服务
                    AnylineService service = ServiceProxy.service("db");
                    // 创建数据行，用于存储插件信息
                    DataRow dataRow = new DataRow();
                    dataRow.put("PLUGIN_NAME", pluginName);
                    dataRow.put("PLUGIN_DESC", pluginDesc);
                    dataRow.put("PLUGIN_PATH", savePath);
                    dataRow.put("PLUGIN_VERSION", pluginVersion);
                    dataRow.put("PLUGIN_STATUS", 0);
                    dataRow.put("UPDATE_TIME", LocalDateTime.now());
                    // 将插件信息插入数据库
                    service.insert("FRAMEWORK_PLUGIN", dataRow);
                    // 重定向到插件列表页面
                    ctx.redirect("/plugin");
                } else {
                    // 如果上传的不是.jar文件，设置错误信息
                    errorMsg = "plugin.not_jar";
                }
            } finally {
                // 清理可能的临时文件
                if (file != null) {
                    file.delete();
                }
            }
        }

        // 准备模型视图数据，无论成功还是失败
        ModelAndView modelAndView = new ModelAndView("plugin-add.ftl");
        modelAndView.put("lang", locale.getLanguage());
        modelAndView.put("errorMsg", errorMsg);
        modelAndView.put("pluginName", pluginName != null ? pluginName : "");
        modelAndView.put("pluginDesc", pluginDesc != null ? pluginDesc : "");
        modelAndView.put("pluginVersion", pluginVersion != null ? pluginVersion : "");
        return modelAndView;
    }


    /**
     * 删除指定的插件
     *
     * @param locale   用户界面的语言环境
     * @param pluginId 要删除的插件的ID
     * @param ctx      上下文对象，用于重定向和获取请求参数
     */
    @Mapping(path = "/delete/{pluginId}")
    public void delete(
            Locale locale,
            long pluginId,
            Context ctx
    ) {
        // 获取数据库服务代理
        AnylineService service = ServiceProxy.service("db");

        // 查询要删除的插件信息
        DataRow row = service.query("FRAMEWORK_PLUGIN", "ID:" + pluginId);

        // 如果插件不存在，调用notPlugin方法处理
        notPlugin(ctx, row);

        // 定义默认的错误消息
        String errorMsg = "plugin.delete_init_msg";

        // 如果插件信息存在
        if (row != null) {
            // 获取插件的文件路径
            String pluginPath = row.getString("PLUGIN_PATH");

            try {
                // 删除插件文件
                Files.delete(Path.of(pluginPath));
                // 删除数据库中的插件记录
                service.delete("FRAMEWORK_PLUGIN", row);
                // 设置成功消息
                errorMsg = "plugin.delete_success";
            } catch (IOException e) {
                // 记录异常日志
                log.error(e.getMessage(), e);
                // 设置文件删除失败的消息
                errorMsg = "plugin.delete_file_fail";
            }
        }

        // 重定向到插件列表页面，并附带错误消息参数
        ctx.redirect("/plugin?errorMsg=" + errorMsg);
    }


    /**
     * 映射到更新插件的路径
     *
     * @param locale        本地化设置，用于国际化
     * @param pluginId      插件ID，用于标识特定插件
     * @param pluginName    插件名称，将要更新的字段之一
     * @param pluginDesc    插件描述，将要更新的字段之一
     * @param pluginVersion 插件版本，将要更新的字段之一
     * @param ctx           上下文对象，用于重定向和错误处理
     * @return ModelAndView 模型视图对象，用于渲染更新插件页面
     */
    @Mapping(path = "/update/{pluginId}")
    public ModelAndView update(
            Locale locale,          // 本地化设置，用于国际化
            long pluginId,          // 插件ID，用于标识特定插件
            String pluginName,      // 插件名称，将要更新的字段之一
            String pluginDesc,      // 插件描述，将要更新的字段之一
            String pluginVersion,   // 插件版本，将要更新的字段之一
            Context ctx             // 上下文对象，用于重定向和错误处理
    ) {
        AnylineService service = ServiceProxy.service("db"); // 获取数据库服务
        DataRow row = service.query("FRAMEWORK_PLUGIN", "ID:" + pluginId); // 查询指定ID的插件信息
        notPlugin(ctx, row); // 验证row是否为插件，如果不是，执行相应操作（此处省略具体实现）
        String errorMsg = "plugin.update_init_msg"; // 初始化错误信息
        // 如果任一字段（插件名称、描述、版本）不为空，则尝试更新插件信息
        if (!(StringUtil.isEmpty(pluginName) && StringUtil.isEmpty(pluginDesc) && StringUtil.isEmpty(pluginVersion))) {
            try {
                row.putWithoutEmpty("PLUGIN_NAME", pluginName); // 更新插件名称，如果名称不为空
                row.putWithoutEmpty("PLUGIN_DESC", pluginDesc); // 更新插件描述，如果描述不为空
                row.putWithoutEmpty("PLUGIN_VERSION", pluginVersion); // 更新插件版本，如果版本不为空
                service.update("FRAMEWORK_PLUGIN", row); // 执行更新操作
                errorMsg = "plugin.update_success"; // 更新成功信息
                ctx.redirect("/plugin?errorMsg=" + errorMsg); // 重定向到插件列表页，携带成功信息
            } catch (Exception e) {
                errorMsg = "plugin.update_fail"; // 更新失败信息
                log.error(e.getMessage(), e); // 记录异常信息
            }
        }

        ModelAndView modelAndView = new ModelAndView("plugin-update.ftl"); // 创建ModelAndView对象，指定视图
        modelAndView.put("lang", locale.getLanguage()); // 设置语言环境到Model
        modelAndView.put("errorMsg", errorMsg); // 设置错误信息到Model
        modelAndView.put("pluginId", pluginId); // 将插件ID设置到Model
        modelAndView.put("pluginName", row.getString("PLUGIN_NAME")); // 将插件名称设置到Model
        modelAndView.put("pluginDesc", row.getString("PLUGIN_DESC")); // 将插件描述设置到Model
        modelAndView.put("pluginVersion", row.getString("PLUGIN_VERSION")); // 将插件版本设置到Model
        return modelAndView; // 返回ModelAndView对象
    }

    /**
     * 启用指定的插件
     *
     * @param locale   本地化设置，用于国际化
     * @param pluginId 需要启用的插件的ID
     * @param ctx      上下文对象，用于重定向和错误处理
     */
    @Mapping(path = "/enable/{pluginId}")
    public void enable(
            Locale locale,
            long pluginId,
            Context ctx
    ) {
        // 获取数据库服务代理
        AnylineService service = ServiceProxy.service("db");
        // 查询指定ID的插件信息
        DataRow row = service.query("FRAMEWORK_PLUGIN", "ID:" + pluginId);
        // 验证插件有效性，如果无效则进行相应处理
        notPlugin(ctx, row);

        try {
            // 标记插件为启用状态
            row.put("PLUGIN_STATUS", 1); // 假设1表示启用
            // 更新插件状态
            service.update("FRAMEWORK_PLUGIN", row);
            // 重定向到插件管理页面，显示成功消息
            ctx.redirect("/plugin?errorMsg=plugin.enable_success");
        } catch (Exception e) {
            // 日志记录异常信息
            log.error(e.getMessage(), e);
            // 重定向到插件管理页面，显示失败消息
            ctx.redirect("/plugin?errorMsg=plugin.enable_fail");
        }
    }

    /**
     * 禁用指定的插件。
     *
     * @param locale   用户界面的语言环境。
     * @param pluginId 要禁用的插件的ID。
     * @param ctx      上下文对象，用于重定向和错误消息传递。
     */
    @Mapping(path = "/disable/{pluginId}")
    public void disable(
            Locale locale,
            long pluginId,
            Context ctx
    ) {
        // 获取数据库服务代理
        AnylineService service = ServiceProxy.service("db");

        // 查询要禁用的插件信息
        DataRow row = service.query("FRAMEWORK_PLUGIN", "ID:" + pluginId);

        // 检查查询结果是否为插件，如果不是，进行相应处理
        notPlugin(ctx, row);

        try {
            // 修改插件状态为禁用
            row.put("PLUGIN_STATUS", 0);
            // 更新插件信息
            service.update("FRAMEWORK_PLUGIN", row);
            // 重定向到插件列表页面，传递成功消息
            ctx.redirect("/plugin?errorMsg=plugin.disable_success");
        } catch (Exception e) {
            // 日志记录异常信息
            log.error(e.getMessage(), e);
            // 重定向到插件列表页面，传递失败消息
            ctx.redirect("/plugin?errorMsg=plugin.disable_fail");
        }
    }


    /**
     * 文件管理页面控制器
     * 该方法用于处理插件文件管理的请求，根据提供的插件ID和路径，
     * 展示相应的文件或目录，以及可能的错误信息
     *
     * @param locale   语言环境，用于国际化
     * @param pluginId 插件ID，用于标识特定的插件
     * @param path     文件或目录的路径，相对于插件路径
     * @param errorMsg 错误信息，当操作失败或路径无效时显示
     * @param ctx      上下文对象，用于重定向或共享数据
     * @return 返回一个 ModelAndView 对象，包含视图路径和模型数据
     */
    @Mapping(path = "/file/{pluginId}")
    public ModelAndView fileManager(
            Locale locale,
            long pluginId,
            String path,
            @Param(defaultValue = "empty") String errorMsg,
            Context ctx
    ) {
        // 获取插件信息
        DataRow row = getPlugin(pluginId);
        // 验证插件有效性
        notPlugin(ctx, row);

        // 获取插件的根路径
        String pluginPath = row.getString("PLUGIN_PATH");
        // 创建插件目录对象
        File baseDir = new File(pluginPath).getParentFile();
        // 如果传递路径不是以插件路径开头，则将路径置空
        if (path != null && !path.startsWith(baseDir.getPath())) {
            path = null;
        }

        // 根据路径是否存在设置当前路径
        String currentPath = StringUtil.isEmpty(path) ? baseDir.getPath() : path;
        // 创建目标文件或目录对象
        File targetFileOrDir = new File(currentPath);

        // 初始化视图模型
        ModelAndView modelAndView = new ModelAndView("file-manager.ftl");
        // 设置语言环境和插件ID到模型
        modelAndView.put("lang", locale.getLanguage());
        modelAndView.put("pluginId", pluginId);

        // 如果目标路径不存在，设置错误信息
        if (!targetFileOrDir.exists()) {
            errorMsg = "file.not_exist";
        }
        // 如果目标是一个目录，获取其内容并添加到模型
        if (targetFileOrDir.isDirectory()) {
            modelAndView.put("parentPath", ToolUtils.getParentPath(baseDir.getPath(), targetFileOrDir));
            modelAndView.put("currentPath", currentPath);
            modelAndView.put("files", ToolUtils.getFileList(targetFileOrDir));
        } else if (targetFileOrDir.isFile() && ToolUtils.isConfigFile(targetFileOrDir.getName())) {
            // 如果目标是一个配置文件，重定向到编辑页面
            ctx.redirect("/plugin/file/save/" + pluginId + "?path=" + currentPath);
        } else {
            // 如果目标无效，设置错误信息并展示其父目录
            errorMsg = "file.invalid_type";
            File parentFile = targetFileOrDir.getParentFile();
            modelAndView.put("parentPath", ToolUtils.getParentPath(baseDir.getPath(), parentFile));
            modelAndView.put("currentPath", parentFile.getPath());
            modelAndView.put("files", ToolUtils.getFileList(parentFile));
        }
        // 将错误信息添加到模型
        modelAndView.put("errorMsg", errorMsg);
        // 返回视图模型
        return modelAndView;
    }


    /**
     * 当请求的插件不存在时，将请求重定向到插件不存在的错误页面
     * 此方法用于处理试图访问不存在的插件的情况，通过重定向用户到一个错误页面，告知用户相关的错误信息
     *
     * @param ctx 用于上下文对象，此处主要用于执行重定向操作
     * @param row 数据行对象，用于判断是否为空，从而决定是否需要进行重定向操作
     */
    private void notPlugin(Context ctx, DataRow row) {
        // 如果数据行为空，说明没有找到对应的插件，需要重定向到错误页面
        if (row == null) {
            ctx.redirect("/plugin?errorMsg=plugin.not_exist");
        }
    }


    /**
     * 根据插件ID获取插件数据
     *
     * @param pluginId 插件的唯一标识符
     * @return 返回查询到的插件数据，如果未找到则返回null
     */
    private DataRow getPlugin(long pluginId) {
        // 创建并初始化AnylineService对象，用于与数据库进行交互
        AnylineService service = ServiceProxy.service("db");

        // 使用插件ID查询数据库中的插件信息
        return service.query("FRAMEWORK_PLUGIN", "ID:" + pluginId);
    }


    @Mapping(path = "/file/upload/{pluginId}", multipart = true)
    public void uploadFile(
            Locale locale,
            long pluginId,
            Context ctx
    ) throws IOException {
        DataRow plugin = getPlugin(pluginId);
        notPlugin(ctx, plugin);
        List<UploadedFile> files = ctx.files("files");
        String pluginPath = new File(plugin.getString("PLUGIN_PATH")).getParentFile().getPath() + File.separator;
        for (UploadedFile file : files) {
            String savePath = pluginPath + file.getName();
            FileUtil.createMissingParentDirectories(new File(savePath));
            file.transferTo(new File(savePath));
        }
        ctx.redirect("/plugin/file/" + pluginId + "?errorMsg=file.upload_done");
    }

    /**
     * 映射到文件保存功能的路径
     *
     * @param locale   用户的区域设置，用于确定语言环境
     * @param pluginId 插件ID，用于标识特定插件
     * @param path     文件路径，用于指定要操作的文件
     * @param content  文件内容，用于写入文件
     * @param ctx      上下文对象，用于重定向和错误处理
     * @return ModelAndView 用于渲染文件保存页面
     * @throws IOException
     */
    @Mapping(path = "/file/save/{pluginId}")
    public ModelAndView saveFile(
            Locale locale,  // 用户的区域设置，用于确定语言环境
            long pluginId,  // 插件ID，用于标识特定插件
            String path,  // 文件路径，用于指定要操作的文件
            String content,  // 文件内容，用于写入文件
            Context ctx  // 上下文对象，用于重定向和错误处理
    ) throws IOException {
        DataRow row = getPlugin(pluginId);  // 获取指定插件的信息
        notPlugin(ctx, row);  // 验证插件有效性，如果不是插件则进行错误处理

        String errorMsg = "empty";  // 初始化错误消息为空
        if (path == null) {  // 如果路径为空，则重定向到插件文件页面并显示错误消息
            ctx.redirect("/plugin/file/" + pluginId + "?errorMsg=file.not_exist");
            return null;
        }

        File targetFileOrDir = new File(path);  // 目标文件或目录对象
        String pluginPath = row.getString("PLUGIN_PATH");  // 插件路径
        File baseDir = new File(pluginPath).getParentFile();  // 插件基目录

        ModelAndView modelAndView = new ModelAndView("file-edit.ftl");  // 初始化ModelAndView对象用于返回视图和数据
        modelAndView.put("lang", locale.getLanguage());  // 设置语言环境
        modelAndView.put("pluginId", pluginId);  // 设置插件ID

        if (content != null) {  // 如果有内容需要写入文件
            File file = new File(path);  // 文件对象
            Files.write(file.toPath(), content.getBytes());  // 将内容写入文件
            ctx.redirect("/plugin/file/" + pluginId + "?path=" + ToolUtils.getParentPath(baseDir.getPath(), targetFileOrDir) + "&errorMsg=file.save_success");  // 重定向并显示保存成功消息
            return null;
        }

        modelAndView.put("parentPath", ToolUtils.getParentPath(baseDir.getPath(), targetFileOrDir));  // 设置父路径
        modelAndView.put("currentPath", path);  // 设置当前路径

        try {
            modelAndView.put("fileContent", new String(Files.readAllBytes(targetFileOrDir.toPath())));  // 读取文件内容
        } catch (IOException e) {
            log.error(e.getMessage(), e);  // 记录读取文件异常
            errorMsg = "file.read_fail";  // 设置错误消息为读取失败
            modelAndView.put("fileContent", "");  // 将文件内容设置为空字符串
        }

        modelAndView.put("errorMsg", errorMsg);  // 设置错误消息
        return modelAndView;  // 返回ModelAndView对象
    }
}
