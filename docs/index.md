---
layout: custom
---

# Welcome to MkWeb!

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

![Branching](https://user-images.githubusercontent.com/65178775/81583650-9b94b300-93ec-11ea-8683-c4ffc67215f9.png)

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

# Please dont hesitate to contact us

## dev-whoan, PM & Developer (2020.04 ~ )
- dev.whoan@gmail.com

## hyeonic, Developer (2020.04~2020.09)
- evan3566@gmail.com

## koh, Developer     (2021.01~2021.03)
- khj1538104@gmail.com