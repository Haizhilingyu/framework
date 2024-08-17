<!DOCTYPE html>
<html lang="${lang}">
<head>
    <title>${i18n.get("plugin.title")}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            margin: 0;
            padding: 20px;
        }
        h1 {
            color: #333;
        }
        a {
            color: #007BFF;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #e9ecef;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .status-enabled {
            color: green;
        }
        .status-disabled {
            color: red;
        }
    </style>
</head>
<body>
<h1>${i18n.get("plugin.title")}</h1>
<p>${i18n.get(errorMsg)}</p>
<a href="/plugin/add">${i18n.get("plugin.add_title")}</a>
<table>
    <caption>${i18n.get("plugin.list")}</caption>
    <thead>
    <tr>
        <th>${i18n.get("plugin.id")}</th>
        <th>${i18n.get("plugin.name")}</th>
        <th>${i18n.get("plugin.version")}</th>
        <th>${i18n.get("plugin.desc")}</th>
        <th>${i18n.get("plugin.path")}</th>
        <th>${i18n.get("plugin.status")}</th>
        <th>${i18n.get("plugin.update_time")}</th>
        <th>${i18n.get("plugin.operate")}</th>
    </tr>
    </thead>
    <tbody>
    <#list data as plugin>
        <tr>
            <td>${plugin.ID}</td>
            <td>${plugin.PLUGIN_NAME}</td>
            <td>${plugin.PLUGIN_VERSION}</td>
            <td>${plugin.PLUGIN_DESC}</td>
            <td>${plugin.PLUGIN_PATH}</td>
            <td>
                <#if plugin.PLUGIN_STATUS == 1>
                    <span class="status-enabled">${i18n.get("plugin."+plugin.PLUGIN_STATUS)}</span>
                <#else>
                    <span class="status-disabled">${i18n.get("plugin."+plugin.PLUGIN_STATUS)}</span>
                </#if>
            </td>
            <td>${(plugin.UPDATE_TIME?date('yyyy-MM-dd'))?string("yyyy-MM-dd")}</td>
            <td>
                <#if plugin.PLUGIN_STATUS == 0>
                    <a href="/plugin/enable/${plugin.ID}">${i18n.get("plugin.enable")}</a>
                <#else>
                    <a href="/plugin/disable/${plugin.ID}">${i18n.get("plugin.disable")}</a>
                </#if>
                <a href="/plugin/update/${plugin.ID}">${i18n.get("update")}</a>
                <a href="/plugin/delete/${plugin.ID}">${i18n.get("delete")}</a>
                <a href="/plugin/file/${plugin.ID}">${i18n.get("plugin.file_manage_button")}</a>
            </td>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>
