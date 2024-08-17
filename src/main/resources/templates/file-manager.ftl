<!DOCTYPE html>
<html lang="${lang}">
<head>
    <title>${i18n.get("plugin.file_manager_title")}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>${i18n.get("plugin.file_manager_title")}</h1>
<#if errorMsg??>
    <div style="color: red;">${i18n.get(errorMsg)}</div>
</#if>
<form action="/plugin/file/upload/${pluginId}" method="post" enctype="multipart/form-data">
    <input type="file" name="files" multiple webkitdirectory/>
    <button type="submit">${i18n.get("plugin.upload_file")}</button>
</form>
<table>
    <thead>
    <tr>
        <th>${i18n.get("file.name")}</th>
        <th>${i18n.get("file.size")}</th>
        <th>${i18n.get("file.lastModified")}</th>
        <th>${i18n.get("file.md5")}</th>
    </tr>
    </thead>
    <tbody>
    <#list files as file>
        <tr>
            <td>
                <#if file.isDirectory>
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#000"><path d="M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h240l80 80h320q33 0 56.5 23.5T880-640v400q0 33-23.5 56.5T800-160H160Zm0-80h640v-400H447l-80-80H160v480Zm0 0v-480 480Z"/></svg>
                <#else>
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#000"><path d="M200-200h560v-367L567-760H200v560Zm0 80q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h400l240 240v400q0 33-23.5 56.5T760-120H200Zm80-160h400v-80H280v80Zm0-160h400v-80H280v80Zm0-160h280v-80H280v80Zm-80 400v-560 560Z"/></svg>
                </#if>
                <a href="/plugin/file/${pluginId}?path=${currentPath+"/"+file.name}">
                    ${file.name}
                </a></td>
            <td>${file.size}</td>
            <td>${file.lastModified?string("yyyy-MM-dd HH:mm:ss")}</td>
            <td>${file.md5}</td>
        </tr>
    </#list>
    </tbody>
</table>
<#if parentPath??>
    <p><a href="/plugin/file/${pluginId}?path=${parentPath}">${i18n.get("back_to_parent")}</a></p>
</#if>

<p><a href="/plugin">${i18n.get("back_to_plugin_list")}</a></p>



</body>
</html>