# This Project Has Been Shutdown.

# New project, EgWeb is started to replace this project.

# https://github.com/dev-whoan/EgWeb

<img src="https://user-images.githubusercontent.com/65178775/83269009-6a481f80-a201-11ea-8475-0a779375a005.png" width="100%" />

---

# Welcome to MkWeb!

-----

# Install

You can download WAR file here: [https://github.com/dev-whoan/MKWeb/tree/master/deploy]

Or you can clone from github, use json files under WebContent with modifying sources in src/com/mkweb.
- To operate MkWeb with cloning...
  - You must change WebContent folder name into ROOT
  - The sources you downloaded must be compiled and put them into ROOT/WEB-INF/classes folder
	- com/mkweb/...

## With War file

### WAR file compiled by Java version 13, so you have to use Java13 to run your servlet container. 

### For Windows

1. Place war file in servlet container such as Tomcat.

2. Unzip war file with starting servlet container as Administrator.

3. Go to /path/to/mkweb/ROOT/WEB-INF/classes/configs

4. Modify MkWeb.conf and require json configs and add Controllers you want to use.
- Controller means such as View Controllers, SQL Controllers, FTP Controllers, ...

5. Restart the tomcat

### For Linux

1. Place war file to specified location.
```bash
# Specified location; for example /mkweb/webapps/...
$ mkdir /mkweb/webapps
$ mv MkWeb.war /mkweb/webapps
```

2. Allow Read/Write to servlet container for the location.
```bash
$ chown -R SERVLET_CONTAINER /mkweb
$ chmod -R 755 /mkweb
```

3. Unzip the war file by starting servlet container.
```bash
# Tomcat@ → @ means number. for me, tomcat9
$ systemctl start tomcat@
```

4. Rename the just unzipped folder into ROOT and go to configs folder under the folder.
```bash
$ cd /mkweb/webapps
$ mv MkWeb ROOT
# abs path: /mkweb/webapps/ROOT/WEB-INF/classes/configs
$ cd ROOT/WEB-INF/classes/configs
```

5. Modify MkWeb.conf and require json configs and add Controller you want to use.
- Controller means such as View Controllers, SQL Controllers, FTP Controllers, ...

6. Restart the tomcat
```bash
$ systemctl restart tomcat@
```

- If tomcat doesn't respond to log or any ftp requests, you need to check Servlet Container's permission level.

```bash
# For tomcat
$ cd /etc/systemd/system/multi-user.target.wants
# Modify Tomcat service
$ sudo vi tomcat@.service

#######tomcat9.service######
...
#Security
...
ReadWritePaths=/etc/tomcat@/Catalina/
ReadWritePaths=/var/lib/tomcat@/webapps/
ReadWritePaths=/var/log/tomcat@/
# Add your mkweb paths
ReadWritePaths=/mkweb/webapps/
```

After modify it, you need to change user/group of tomcat's umask.

```bash
$ vi /usr/share/tomcat@/bin/catalina.sh

### find UMASK, change it into 0022
...
if [ -z "$UMASK" ]; then
    UMASK="0022"
fi
...
```

Save the catalina.sh, and restart the tomcat.
```bash
$ systemctl daemon-reload
$ systemctl restart tomcat9
```

## With Cloning Codes

1. Open the project with Java IDE.

2. Modify require json configs and the source you want to change.
- json configs are located in WebContent/WEB-INF/classes/configs

3. Export Project to WAR file
- For eclipse
    - You can export it with Runnable Jar
	  - To export into Runnable jar, only sources should be exported.
	  - After exports jar file, located jar into your webapps jar lib, and copy the /WEB-INF/classes/configs into your /WEB-INF folder
	- Or WAR file
	  - Follow [With War file] above.

- For IntelliJ, Build artifacts.
![artifacts](https://raw.githubusercontent.com/dev-whoan/MKWeb/docs/assets/img/png/intellij.png)

4. Follow [With War file] above.

- You should compile the project up to Java8
- If you use higher than Java8, you need to set your Servlet container's java version in a same version.

## When you operate MkWeb...

- with standalone logging
![standalone](https://raw.githubusercontent.com/dev-whoan/MKWeb/docs/assets/img/png/operated_log.png)

- with catalina
![catalina](https://raw.githubusercontent.com/dev-whoan/MKWeb/docs/assets/img/png/operated_cat.png)

-----
# About

MkWeb is Web Server Framework based on Servlet.

You can use MkWeb with Web: JSP, HTML and even another front library like Reactjs, Vuejs. And Mobile application(iOS, Android)

## Motive

Front developers should consider lots of things when they want to launch kind of services; Application, Web Site, Databases, etc.

Simply Front-End and Back-End.

And their ideas come from Front-End: 'How about creatinga food delivery service?', 'Why there is no service like ...'

However, they need to crew a team for developing their ideas; Backend developer, Frontend developer, Designer, ... 

So MkWeb's idea is started with 'Front developer just need to focus on the Planning, Front-End easily View-side and user experience.

-----

# Architecture

## Design

We are designing MkWeb with Independent MVSC pattern, inspired from MVC pattern.

![Baseline Architecture](https://user-images.githubusercontent.com/65178775/81583650-9b94b300-93ec-11ea-8683-c4ffc67215f9.png)

-----

![MVSC Pattern](https://github.com/dev-whoan/MKWeb/blob/docs/assets/img/png/architecture/design_MVsC.png)

-----

## Model

We defined Model as server-side resources: DB, File server, Logger

## Controller

We designed Controller as relationships between Model and Service

## Service

We defined Service as receiver which handles requests from front end software, such that Web, Mobile Application, ...

Service will handle the request by getting data by Controller and responsing it to requester

## View

We allow Cross Platform. View is the front end software, such that Web(HTML, React, ...), Mobile Application(iOS, Android)

-----

# Operating

## JSON Configs

In MkWeb, you can use several offered Controllers.

To use those Controllers, you need to set, config them with modifying and adding JSON files.

And after you modify the json configs, you don't have to restart the webserver. The modifying information will be automatically covered. (however you have to restart when you create new one.)

## Controllers and Services

JSON Configs are the biggest profit when you use MkWeb.

You can easily access to Model via Services and Controllers.

For example, you just need to set relevant Configs to access DB, one is about Page, and another is about Sql.

However, before using MkWeb you need to design DBA, DBO, and whatever you need to access to the DB.

Using MkWeb, the hassle is gone!

## Logger

MkWeb includes MkLogger which logs everything happend in MkWeb.

-----

# Features

## Logging

MkLogger logs every tasks on MkWeb, so you can easily trace the currently situation in MkWeb.

- Some JSON settings are wrong, and how can I fix it.

- Someone request something, and how MkWeb responses about it.

- Some error have occured, and what is wrong.

## Connecting to RDBMS(mariadb, mysql)

MkWeb can connect to RDBMS without programming about it.

You just need to set DB information on MkWeb.conf, and setting JSON for pages and sqls.

## RESTful API

MkWeb supports RESTful API.

Request header content type must be `application/json`.

`GET` for getting data, `POST` for generating data, `PUT` for modifying data, `DELETE` for deleting data, and `HEAD`, `OPTIONS`.

GET: You can get specify the target data with Document URI.
```bash
curl --request GET "http://localhost/users/name/Jhon"
```

POST: You can generate data with sending Body parameter. 
```bash
curl --request POST "http://localhost/users" --data '{"name":"Jhon", "age":"23"}'
```

PUT: You can modify exists data or generating new data. Condition to search original data will be sent with Document URI, and new data will be sent in Body parameter.
```bash
curl --request PUT "http://localhost/users/name/Jhon" --data '{"age":"24"}'
```

DELETE: You can remove exists data with specifying in Body parmater
```bash
curl --request DELETE "http://localhost/users" --data '{"name":"Jhon"}'
```

### Options for RESTful API

`pretty`: You can receive the response in pretty json.
```bash
curl --request GET "http://localhost/users?pretty"
```

`paging`: You can paging the result datas.
```bash
curl --request GET "http://localhost/users?paging=5"
```

`orderby`: You can ordering the result datas.
`orderway`: You can specify the ordering method, DESC or ASC.

```bash
curl --request GET "http://localhost/users?orderby=name&orderway=desc"
```

-----
# Please don't hesitate to contact us

## dev-whoan, PM & Developer (2020.04 ~ )
- dev.whoan@gmail.com

## hyeonic, Developer (2020.04~2020.09)
- evan3566@gmail.com

## koh, Developer     (2021.01~2021.03)
- khj1538104@gmail.com

# What we want you to do when you use MKWeb or copy and distribute MKWeb(convey).
* MKWeb을 사용하거나 복제, 배포할 때 해줬으면 하는 일

1. Don't hesitate to feedback.
- We are opened for your feedback!

2. Please scout us.
