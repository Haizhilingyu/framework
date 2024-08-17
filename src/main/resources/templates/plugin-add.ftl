<!DOCTYPE html>
<html lang="${lang}">
<head>
    <title>${i18n.get("initdb.title")}</title>
</head>
<body>
<h1>${i18n.get("plugin.add_title")}</h1>
<p>${i18n.get(errorMsg)}</p>
<form method="post" action="/plugin/add" enctype="multipart/form-data">
    <div>
        <label for="plugin-name">${i18n.get("plugin.name")}</label>
        <input type="text" id="plugin-name" name="pluginName"
               value="${pluginName}"/>
    </div>
    <div>
        <label for="plugin-version">${i18n.get("plugin.version")}</label>
        <input type="text" id="plugin-version" name="pluginVersion"
               value="${pluginVersion}"/>
    </div>
    <div>
        <label for="plugin-desc">${i18n.get("plugin.desc")}</label>
        <input type="text" id="plugin-desc" name="pluginDesc"
               value="${pluginDesc}"/>
    </div>
    <div>
        <label for="plugin-file">${i18n.get("plugin.file")}</label>
        <input type="file" id="plugin-file" name="file" accept=".jar" multiple />
    </div>

    <button type="submit">${i18n.get("initdb.button")}</button>

</form>
</body>
</html>