<!DOCTYPE html>
<html lang="${lang}">
<head>
    <title>${i18n.get("initdb.title")}</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f4;
            color: #333;
            margin: 0;
            padding: 20px;
        }
        h1 {
            color: #0056b3;
            text-align: center;
        }
        p {
            color: #666;
            text-align: center;
        }
        form {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            margin: 20px auto;
        }
        label {
            display: block;
            margin-bottom: 5px;
        }
        input[type="text"],
        input[type="password"] {
            width: calc(100% - 22px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #0056b3;
            color: #fff;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: #003d82;
        }
    </style>
</head>
<body>
<h1>${i18n.get("initdb.title")}</h1>
<p>${i18n.get(errorMsg)}</p>
<form method="post" action="/init">
    <div>
        <label for="db-driver">${i18n.get("initdb.driver")}</label>
        <select id="db-driver" name="driver">
            <option value="org.sqlite.JDBC">Sqlite</option>
            <option value="org.postgresql.Driver">Postgresql</option>
            <option value="com.mysql.cj.jdbc.Driver">MySQL</option>
        </select>
    </div>
    <div>
        <label for="db-url">${i18n.get("initdb.url")}</label>
        <input type="text" id="db-url" name="url"
               value="${url}"/>

    </div>
    <div>
        <label for="db-username">${i18n.get("initdb.username")}</label>
        <input type="text" id="db-username" name="username" value="${username}"/>
    </div>
    <div>
        <label for="db-password">${i18n.get("initdb.password")}</label>
        <input type="password" id="db-password" name="password" value="${password}"/>
    </div>
    <button type="submit">${i18n.get("initdb.button")}</button>

</form>
</body>
</html>