---
layout: custom
---

# Sql

## Location: /WEB-INF/classes/configs/sqls

-----

# JSON Configs

You need each JSON files for every sqls you want to creat. It means, there are 5 json files if you have 5 sql data sets.

I recommend you create each json for each dataset, for example, user.json, article.json.

Each json files could be mapping into one table, for example, user.json for user table, article.json for article table.

Sql includes services for executing sqls that can be separated into each DMLs, also specified conditions.

## Basic Properties

| name           | description                     | value                      |
|:---------------|:--------------------------------|:---------------------------| 
| name           | name of Controller              | Unique ID in SQLs          |
| debug          | level to log                    | debug, info, warn, error   |
| db             | database to execute sqls        |   |
| api            | is sql for RESTful API or not   | yes, no |

## Service Properties

| name           | description                     | value                      |
|:---------------|:--------------------------------|:---------------------------| 
| id             | id of service. you must use one of defined id in View Controller.              | Unique ID in SQLs          |
| auth           | requiring authority for the service or not                                     | yes, no                    |
|:---------------|:--------------------------------|:---------------------------| 
| query                                                                         |
| crud           | define crud type                | select, update, insert, delete          |
| column         | columns before table   | select "COLUMNS" FROM ... / update table set "COLUMN1"="data1", "COLUMN2"="data2" WHERE where / INSERT INTO table (COLUMNS) VALUE (datas)|
|:---------------|:--------------------------------|:---------------------------| 
| table                                                                         |
| from           | table to execute                | table name as t1           | 
| join                                                                          |
| type           | join type                       | INNER JOIN / OUTER JOIN / NATURAL JOIN          | 
| joinfrom       | table to join                   | table name as t2           | 
| on             | join conditions                 | t1.id = t2.id AND t1.id=@id@ |
|:---------------|:--------------------------------|:---------------------------| 
| data           | to modify or insert data, the parameter to receive those datas. this data must be same with View Controller's service's subscript.               | for select: "1":"" / update table set "column1"="DATA1", "column2"="DATA@" WHERE where / INSERT INTO table (columns) VALUE (DATAS)| 
| where          | where clause for sql            | user_id = @id@             | 

-----
# Usage Examples

## Sql Controller With One Service

This Sql connected to database named "database", and use table named "user".

As you remember, the parameters from View(page), is equally same with the value you have set in View Controller.
```javascript
//Part of View Controller's doLogin Service
...
{
		"page_static":"false",
		"type":{
		"kind":"sql",
		"id":"doLogin"
	},
	"method":"post",
	"obj":"list",
	"parameter_name":"user",
	// below values are used in sql services with wrapped by "@"
	"value":{
		"1":"id",
		"2":"password"
	}
},
...
```

```javascript
{
	"Controller": {
		"name":"user",
		"debug":"error",
		"db":"database",
		"api":"no",
		"services":[
			{
				"id":"doLogin",
				"auth":"no",
				"query":{
					"crud":"select",
					"column":{
						"1":"userid as id",
                        "2":"userage as age"
					},
					"table":{
                        "from":"user"
                    },
					"data":{
						"1":""
					},
					// the parameter from View is wrapped with "@" and without parameter_name
					"where":"userid = @id@ AND userpw = @password@"
				}
			}
		]
	}
}
```

## Sql Controller With Join Example

```javascript
{
	"Controller": {
		"name":"user",
		"debug":"error",
		"db":"database",
		"api":"no",
		"services":[
			{
				"id":"JOIN_EXAMPLE",
				"query":{
					"crud":"select",
					"column":{
						"1":"*"
					},
					"table":{
						"from":"user as u",
						"join":{
							"type":"INNER JOIN",
							"joinfrom":"article as a",
							"on":"u.id = a.writer AND u.id=@id@"
						"
					},
					"data":{
						"1":""
					},
					"where":"user.id = @id@"
				}
			}
		]
	}
}
```
