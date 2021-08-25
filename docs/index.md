---
layout: custom
---

# Welcome to MkWeb!

-----

# Install

You can download WAR file here: [Download War](https://github.com/dev-whoan/MKWeb/tree/master/deploy)

Or you can clone from github, use json files under WebContent with modifying sources in src/com/mkweb.
- To operate MkWeb with cloning...
  - You must change WebContent folder name into ROOT
  - The sources you downloaded must be compiled into .class, and put them inside of ROOT/WEB-INF/classes
	- com/mkweb/...

## With War file

---
JDK Version: Java13
---

### For Windows

1. Place war file in servlet container such as Tomcat.

2. Unzip war file with starting servlet container as Administrator.

3. Move to /path/to/mkweb/ROOT/WEB-INF/classes/configs

4. Modify MkWeb.conf and require json configs and add Controllers you want to use.

5. Restart the tomcat

### For Linux

1. Place war file specified location.
```bash
$ mkdir /mkweb/webapps
$ mv MkWeb.war /mkweb/webapps
```

2. Give Read Write Permission on MkWeb's location to servlet container.
```bash
$ chown -R SERVLET_CONTAINER /mkweb
$ chmod -R 755 /mkweb
```

3. Unzip the war file with starting servlet container.
```bash
# With Tomcat9
$ systemctl start tomcat9
```

4. Rename just unzipped folder into ROOT, and move to configs folder
```bash
$ cd /mkweb/webapps
$ mv MkWeb ROOT
$ cd ROOT/WEB-INF/classes/configs
```

5. Modify MkWeb.conf and require json configs and add Controller you want to use.

6. Restart the tomcat
```bash
# @ will be version of tomcat. for me, tomcat@ => tomcat9
$ systemctl restart tomcat@
```

- If tomcat doesn't respond to log or any ftp requests, you need to check Servlet Container's permission.

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
#Add your mkweb paths
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
![artifacts](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/intellij.png)

4. Follow [With War file] above.

- You should compile the project up to Java8
- If you use higher than Java8, you need to set your Servlet container's java version in a same version.

## When you operate MkWeb...

- with standalone logging
![standalone](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/operated_log.png)

- with catalina
![catalina](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/operated_cat.png)

-----
# About

MkWeb is Web Server Framework based on Servlet.

You can use MkWeb with JSP, HTML and even another front library like Reactjs, Vuejs.

## Motive

Front developers should consider lots of things when they want to launch kind of services; Application, Web Site, Databases, etc.

Simply Front-End and Back-End.

And their ideas come from Front-End: 'How about creatinga food delivery service?', 'Why there is no service like ...'

However, they need to crew a team for developing their ideas; Backend developer, Frontend developer, Designer, ... 

So MkWeb's idea is started with 'Front developer just need to focus on the Planning, Front-End easily View-side and user experience.

-----

# Architecture

## Design

We are designing our MkWeb with MVC pattern.

![MVC Pattern](https://user-images.githubusercontent.com/65178775/81583650-9b94b300-93ec-11ea-8683-c4ffc67215f9.png)

## Model

We defined Model as server-side resources: DB, File server, Logger

## Controller

We designed Controller as relationships between Model and View-side

## Service

We defined Service as the actual functions which operates for Controller, in Controller.

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
