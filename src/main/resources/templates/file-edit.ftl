<!DOCTYPE html>
<html lang="${lang}">
<head>
    <title>${i18n.get("plugin.file_edit_title")}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        textarea {
            width: 100%;
            height: 400px;
        }
    </style>
</head>
<body>
<h1>${i18n.get("plugin.file_edit_title")}</h1>

<form action="/plugin/file/save/${pluginId}" method="post">
    <textarea name="content" id="content" cols="50" rows="10">${fileContent}</textarea>
    <input type="hidden" name="path" value="${currentPath}">
    <button type="submit">${i18n.get("save")}</button>
</form>

<p><a href="/plugin/file/${pluginId}?path=${parentPath}">${i18n.get("back_to_file_list")}</a></p>

</body>
</html>
