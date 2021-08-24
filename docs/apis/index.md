---
layout: custom
---

# RESTful API

## Location: /WEB-INF/classes/configs/apis

-----

# How to Set RESTful API

# View Configs

## Location: /WEB-INF/classes/configs/apis/views

You need each JSON files for every api data set you want to creat. This is because every API request requires target URI.

It means, there are 5 json view configs if you have 5 data sets to use RESTful API.

MkWeb offered 6 methods, and View Config will have only 6 services; one service for each method.

No method can be overwritten.

### Basic Properties

| name           | description                     | value                                                           |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| name           | name of Controller              | Unique ID in Views and Rest APi can be same with last_uri       |
| last_uri       | last segment of the page        | must not duplicated with other api views                        |
| debug          | level to log                    | debug, info, warn, error                                        |
| api            | is page for api or not          | yes |

### Device Properties

Every field of device must be empty, however must have default device.

| name           | description                                             | value                                 |
|:---------------|:--------------------------------------------------------|:--------------------------------------| 
| desktop        | Empty Field              | must be empty         |
| android        | Empty Field              | must be empty         |
| ios            | Empty Field              | must be empty         |
|:---------------|:--------------------------------------------------------|:--------------------------------------| 
| device services                                                                                                  |
| default        | Empty Field | must be empty |
| path	         | Empty Field | must be empty |
| file           | Empty Field | must be empty |
| uri            | Empty Field | must be empty |

### Service Properties

Api View Controller have at least one service. 

Services types are must be sql.

| name           | description                                                                                                               | value                           |
|:---------------|:----------------------------------------------------------------|:--------------------------------| 
| page_static    | FALSE                                                           | false                           |
|:---------------|:----------------------------------------------------------------|:--------------------------------| 
| type                                                                                                               |
| kind           | SQL         | sql                                               |
| id             | id of service that also must be defined in the API Configs.     | must be defined in target contr |
|:---------------|:--------------------------------------------------------------------------------------------------------------------------|:--------------------------------| 
| method         | method to execute the service. get for fetching, post for generating, put for modifying, delete for deleting, head for heading, options for options            | GET, POST, PUT, DELETE, HEAD, OPTIONS                       |
| obj            | service will return the result with the obj data type                                             | list                            |
| parameter_name | Empty Field   | must be empty |
| value          | columns for data sets. only can access to the column that defined here | must be defined in db table |

### And Other Default Options Are Defined on MkWeb.conf

```yaml
#...

##############################################################
#                     
#                     REST Api
#                     
##############################################################
# default: no
mkweb.restapi.use=yes

# default : mkapi
mkweb.restapi.uri=mkapi

# default : docs
mkweb.restapi.docs=docs

# Only restapi can accessed by host web site.
# It means, the data cannot be accessed cross-origin.
# default : no [yes, no]
mkweb.restapi.hostonly=no

# When you post data, returned sequence or auto_increment
# default: _update_seq
mkweb.restapi.dml.sequence=_update_seq

# auth require for using restapi
# if you change this value, server need to be restarted.
# default: no
mkweb.restapi.search.usekey=yes

# key for search
# ex) www.mkweb.com/mk_api_key/key/?search_key=KEY
# /mk_api_key is what you set url-pattern for MkWebRestApi
# default: search_key
mkweb.restapi.search.keyexp=search_key
#
# Parameter name to print pretty json
mkweb.restapi.search.opt.pretty.param=pretty

# Query parameter for paging
# If you don't send this parameter
# RESTful API will returns all columns based on DB's limitation
# default: paging
# usage: &paging=5
mkweb.restapi.search.opt.paging.param=paging

# Max columns per paging.
# default: 100
mkweb.restapi.search.opt.paging.limit=2

# Query string for ordering results
# This parameter will be standard for ordering
# default: orderby
# usage: &orderby=user_id
mkweb.restapi.search.opt.sorting.param=orderby

# Query string for ordering method
# This parameter will determine the ordering method
# default: orderway
# usage: &orderway=DESC
mkweb.restapi.search.opt.sorting.method.param=orderway

# This must not some value that may used to data value
# or any column name. It means must be unique value
# Don't touch it if you don't modify MkWeb
# default: SEARCH_ALL
mkweb.restapi.search.all=SEARCH_ALL

# The DB Table, which save api_key to use rest-api
# The table must be inside of mkweb.db.database
# The table should include following columns,
# `api_SEQ` int(11) NOT NULL AUTO_INCREMENT,
# `api_key` varchar(45) NOT NULL,
# `user_id` varchar(20) NOT NULL,
# CHARSET=utf8mb4
# default: MkApi
mkweb.restapi.key.table=MkApi

# The column name which stores search keys.
# The above property, mkweb.restapi.key.table, there are information about columns,
# and this property means the `api_key` column.
# default: api_key
mkweb.restapi.key.column.name=api_key

# The column name which is remark of search key.
# The above property, mkweb.restapi.key.table, there are `user_id` column,
# This property is set the column name of it.
# default: user_id
mkweb.restapi.key.column.remark=user_id
```

-----
# API View Controller Usage Examples

Head method is working equally same with get method, so "head" must be same service with "get" however only have different method.

Only defined method can be executed.

## Examples with Offering 6 Methods
```json
{
  "Controller": {
    "name":"users",
    "last_uri":"users",
    "device":{
      "desktop":{
        "default":{
          "path":"",
          "file":"",
          "uri":""
        }
      }
    },
    "debug":"error",
    "api":"yes",
    "services":[
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"get",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"SEQ",
          "2":"userid"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"head",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"SEQ",
          "2":"userid"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"createUser"
        },
        "method":"post",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"userid",
          "2":"userpw"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":""
        },
        "method":"options",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":""
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"put",
        "obj":"list",
        "parameter_name":"",
        "value":{
            "1":"SEQ",
            "2":"userid",
            "3":"userpw"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"removeUser"
        },
        "method":"delete",
        "obj":"list",
        "parameter_name":"",
        "value":{
            "1":"SEQ",
            "2":"userid"
        }
      }
    ]
  }
}
```

## Examples with Offering Only GET Methods
```json
{
  "Controller": {
    "name":"users",
    "last_uri":"users",
    "device":{
      "desktop":{
        "default":{
          "path":"",
          "file":"",
          "uri":""
        }
      }
    },
    "debug":"error",
    "api":"yes",
    "services":[
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"get",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"SEQ",
          "2":"userid"
        }
      }
    ]
  }
}
```

-----

# Sql Configs

## Location: /WEB-INF/classes/configs/apis/sqls

You need each JSON files for every api data set you want to creat.

I recommend you create each json for each dataset, for example, user.json, article.json.

It means, there are 2 json view configs and 2 json sql configs if you have 2 data sets to use RESTful API.

MkWeb offered 6 methods, however api sql configs only have totally 3 services for using all the 6 methods.

### Basic Properties

| name           | description                     | value                      |
|:---------------|:--------------------------------|:---------------------------| 
| name           | name of Data set                | Unique ID in API SQLs      |
| debug          | level to log                    | debug, info, warn, error   |
| db             | database to execute sqls        |   |
| table          | table to get the datas          |   |
| api            | YES   | yes |
| condition      | columns to use as conditions. only defined condition can be used to specify the datas. | columns that defined in table |
| parameter      | parameter that must be passed to use this data set | parameter is one of column that defined in table |


### Service Properties

| name           | description                     | value                      |
|:---------------|:--------------------------------|:---------------------------| 
| id             | id of service. Api View Controller use one of service id that defined here | Unique ID in SQLs          |
|:---------------|:--------------------------------|:---------------------------| 
| query                                                                         |
| crud           | define crud type                | select, update, insert, delete          |
| column         | columns before table            | select "COLUMNS" FROM ... / update table set "COLUMN1"="data1", "COLUMN2"="data2" WHERE where / INSERT INTO table (COLUMNS) VALUE (datas)|
| data           | must be same with column        | must be same with the column | 
| where          | Empty Field                     | Empty Field             |  

-----
# API Sql Controller Usage Examples

## Examples with Offering 6 Methods
```json
{
  "Controller": {
    "name":"api_user",
    "debug":"error",
    "db":"database",
    "table":"user",
    "api":"yes",
    "condition":{
      "1":"SEQ",
      "2":"userid",
      "3":"userpw"
    },
    "parameter":{
      "1":"*"
    },
    "services":[
      {
        "id":"getAllUser",
        "query":{
          "crud":"select",
          "column":{
            "1":"SEQ",
            "2":"userid"
          },
          "data":{
            "1":""
          },
          "where":""
        }
      },
      {
        "id":"createUser",
        "query":{
          "crud":"insert",
          "column":{
            "1":"userid",
            "2":"userpw"
          },
          "data":{
            "1":"userid",
            "2":"userpw"
          },
          "where":""
          
        }
      },
      {
        "id":"removeUser",
        "query":{
          "crud":"delete",
          "column":{
            "1":""
          },
          "data":{
            "1":""
          },
          "where":""
        }
      }
    ]
  }
}
```

## Examples with Offering 6 Methods Must Include userid

Below requests are will be returned 400 error because no userid is sent.

```bash
curl --request GET "http://localhost/users"
curl --request POST "http://localhost/users" --data '{"userid":"John", "userpw":12401244"}'
```

```json
{
  "Controller": {
    "name":"api_user",
    "debug":"error",
    "db":"database",
    "table":"user",
    "api":"yes",
    "condition":{
      "1":"SEQ",
      "2":"userid",
      "3":"userpw"
    },
    "parameter":{
      "1":"userid"
    }, 
    "services":[
      {
        "id":"getAllUser",
        "query":{
          "crud":"select",
          "column":{
            "1":"SEQ",
            "2":"userid"
          },
          "data":{
            "1":""
          },
          "where":""
        }
      },
      {
        "id":"createUser",
        "query":{
          "crud":"insert",
          "column":{
            "1":"userid",
            "2":"userpw"
          },
          "data":{
            "1":"userid",
            "2":"userpw"
          },
          "where":""
          
        }
      },
      {
        "id":"removeUser",
        "query":{
          "crud":"delete",
          "column":{
            "1":""
          },
          "data":{
            "1":""
          },
          "where":""
        }
      }
    ]
  }
}
```

However, above example is wrong, because we don't have to specify the data in document uri when we try to generate new one.

The purpose is satisified with POST, but when we specify userid in Document URI, it will be changed into PUT method.

But actually you will request those with POST method, and MkWeb do not define any results when you do like this.

I recommend you to use "parameter" field when you create a RESTful API that only allows "GET", (includes head, options) Methods.

-----

# How to Request

There are several constraints to send requests:
- "Content-Type": "application/json"
- Authorization
  - The key storage is defined on MkWeb.conf, `mkweb.restapi.key.table`
  - Get method via URL, must be exporessed with query parameter that defined with `search_key`
  - Key could be JWT which MkWeb offered, or just Hashed String
  - The other requests include get method through HTTP communication, must be sent with `Authorization` Header, value with:
    - "Authorization": "Bearer _____"
- GET, HEAD, OPTIONS only allow URI options
- PUT allow URI options for condition, body parameter for update
- DELETE allow body parameter for deleting
- POST allow body parameter

Here I explain with default options for default values
- `mkapi` is default value of mkweb.restapi.uri
- `search_key` is default value of mkweb.restapi.search.keyexp
  - which have mykey
- pretty is returning data as pretty, the default value of mkweb.restapi.search.opt.pretty.param
- and so on...

Let's suppose that there is a `user` data set. Which have `users` uri, and 4 columns that named `SEQ`, `userid`, `userpw` and `usernickname`.
```sql
CREATE TABLE `user` (
  `SEQ` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(45) NOT NULL,
  `userpw` varchar(45) NOT NULL,
  `usernickname` varchar(45) NOT NULL,
  PRIMARY KEY (`SEQ`,`userid`),
  UNIQUE KEY `userid` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4
```

-----

## GET Method

Get method is used to request data.

Examples:
- There is a user data set and want to get all the users
  - which means can get every datas from data set
- There is a user data set and want to get users nickname of 'John'
  - which means can get specified datas from data set
  
### Send Request

```bash
# No key required
$ curl --request GET "http://localhost/mkapi/users/usernickname/John"

# Key required
$ curl --request GET "http://localhost/mkapi/users/usernickname/John?search_key=mykey"
```

### Success to request

- There is at least one data
```javascript
{
    "took": "12",
    "code": "200",
    "response": "HTTP 1.1 200 OK",
    "Content-Type": "application/json;charset=UTF-8",
    "Content-Length": "72",
    "users": [
        {
            "userid": "john@gmail.com",
            "SEQ": "28",
            "usernickname": "John"
        },
		...
    ]
}
```

- If there is no data, the result will be empty and response code will be 204.

![delete](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_get_success.PNG)

-----

## POST Method

Post method is used to generate new data.

Examples:
- There is a user who want to register
  - userid must not be duplicated

### Send Request

```bash
$ curl --request GET "http://localhost/mkapi/users" --data '{"userid":"john@gmail.com","userpw":"123456","usernickname":"John"}'
```

The result will returned with JSON type.

The result JSON Object is wrapped with name of dataset, for this example, `users`. Remember that name of data set is defined on api's view configs.

### Success to Request

```json
{
  "took": "35",
  "code": "201",
  "response": "HTTP 1.1 201 Created",
  "Content-Type": "application/json;charset=UTF-8",
  "Content-Length": "94",
  "users": {
    "_update_seq": 28,
    "userpw": "123456",
    "userid": "john@gmail.com",
    "usernickname": "John"
  }
}
```

If you want to change _updated_seq into another string, please change MkWeb.conf -> `mkweb.restapi.dml.sequence`

![delete](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_post_success.PNG)

-----
## PUT Method

Put method is used to modify the data. However, in a special case you can generate a new data.

Examples:
- There is a user data and want to change nickname
  - which means you have to specify the user who wants to change the nickname
- I thought there is a data, and try to change, but actually there is none.
  - which means you have to specify the user who wants to change the nickanme
  - also I know the whole datas
  
### Send Request

```bash
# There is a user
$ curl --request PUT "http://localhost/mkapi/users/userid/smith@gmail.com" --data '{"usernickname":"changed!"}'

# There is no user
$ curl --request PUT "http://localhost/mkapi/users/userid/smith@gmail.com" --data '{"usernickname":"changed!", "userid":"smith@gmail.com", "userpw":"123456"}'
-----

### Success to request

- Success to Fetch the data
```json
{
  "took": "12",
  "code": "200",
  "response": "HTTP 1.1 200 OK",
  "Content-Type": "application/json;charset=UTF-8",
  "Content-Length": "26"
}
```

- Success to generate a new data
  - Remember, in this case, no data sequence or auto increment is returned.

```json
{
  "took": "139",
  "code": "201",
  "response": "HTTP 1.1 201 Created",
  "Content-Type": "application/json;charset=UTF-8",
  "Content-Length": "54"
}
```

![delete](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_put_success.PNG)

-----

## DELETE Method

Delete method is used to remove a data. Whether the data is exists or not, always returns empty results with code 204.

Constraints:
- You must specify the data. Nor All of the duplicated datas will be removed.

Examples:
- Whether user is exists or not want to remove the specified data.

### Send Request
```bash
$ curl --request DELETE "http://localhost/mkapi/users" --data '{"userid":"john@gmail.com"}'
```

### Success to request
- Empty result, and code 204 will be returned

![delete](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_delete_success.PNG)

-----

## HEAD Method

Head method is almost same with Get method, however no result will be returned.

### Send Request

```bash
# Whole users
$ curl --request HEAD "http://localhost/mkapi/users"
# Specify the target
$ curl --request HEAD "http://localhost/mkapi/users/userid/john@gmail.com"
```

![head](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_head_success.PNG)

-----

## OPTIONS Method

Options method is used to check which method is allowed for api data set.

### Send request

```bash
curl --request OPTIONS "http://localhost/mkapi/users"
```

### Success to request

```json
{
  "took": "2",
  "code": "200",
  "response": "HTTP/1.1 200 OK",
  "Content-Type": "application/json;charset=UTF-8",
  "Allow": "GET,HEAD,POST,OPTIONS,PUT,DELETE"
}
```

![head](https://raw.githubusercontent.com/dev-whoan/MKWeb/master/docs/assets/img/png/api_options_success.PNG)

-----

## Fail to Request

- If you send bad requests, results will include the error messages.

```json
{
    "took": "7",
    "code": "400",
    "response": "HTTP/1.1 400 Bad Request",
    "Content-Type": "application/json;charset=UTF-8",
    "Content-Length": "136",
    "error": {
        "message": "You must enter every column data.",
        "code": "400",
        "status": "Bad Request",
        "info": "deploy.dev-whoan.xyz:58443/docs/400"
    }
}
```

```json
{
    "took": "21",
    "code": "400",
    "response": "HTTP/1.1 400 Bad Request",
    "Content-Type": "application/json;charset=UTF-8",
    "Content-Length": "152",
    "error": {
        "message": "Duplicate entry 'john@gmail.com' for key 'userid'",
        "code": "400",
        "status": "Bad Request",
        "info": "deploy.dev-whoan.xyz:58443/docs/400"
    }
}
```
-----

## If You Require Authorization

- For `curl`

```bash
$ curl --request GET "http://localhost/mkapi/users" -H "Authorization: Bearer 123456"
$ curl --request POST "http://localhost/mkapi/users" --data '{"userid":"test@test.com", "userpw":"159159", "usernickname":"testnick"}' -H "Authorization: Bearer 123456"
# and so on....
```

- For `GET` via URL

```
http://localhost/mkapi/users?search_key=123456
```

## Options for RESTful API

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

## Used Controllers

- Api View Controller

```json
{
  "Controller": {
    "name":"users",
    "last_uri":"users",
    "device":{
      "desktop":{
        "default":{
          "path":"",
          "file":"",
          "uri":""
        }
      }
    },
    "debug":"error",
    "api":"yes",
    "services":[
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"get",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"SEQ",
          "2":"userid",
          "3":"usernickname"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"head",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"SEQ",
          "2":"userid",
          "3":"usernickname"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"createUser"
        },
        "method":"post",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":"userid",
          "2":"userpw",
          "3":"usernickname"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":""
        },
        "method":"options",
        "obj":"list",
        "parameter_name":"",
        "value":{
          "1":""
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"getAllUser"
        },
        "method":"put",
        "obj":"list",
        "parameter_name":"",
        "value":{
            "1":"SEQ",
            "2":"userid",
            "3":"userpw",
            "4":"usernickname"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"sql",
          "id":"removeUser"
        },
        "method":"delete",
        "obj":"list",
        "parameter_name":"",
        "value":{
            "1":"SEQ",
            "2":"userid",
            "3":"usernickname"
        }
      }
    ]
  }
}
```

- Api SQL Controller

```json
{
  "Controller": {
    "name":"api_user",
    "debug":"error",
    "db":"dogood",
    "table":"user",
    "api":"yes",
    "condition":{
      "1":"SEQ",
      "2":"userid",
      "3":"userpw",
      "4":"usernickname"
    },
    "parameter":{
      "1":"*"
    },
    "services":[
      {
        "id":"getAllUser",
        "query":{
          "crud":"select",
          "column":{
            "1":"SEQ",
            "2":"userid",
            "3":"usernickname"
          },
          "data":{
            "1":""
          },
          "where":""
        }
      },
      {
        "id":"createUser",
        "query":{
          "crud":"insert",
          "column":{
            "1":"userid",
            "2":"userpw",
            "3":"usernickname"
          },
          "data":{
            "1":"userid",
            "2":"userpw",
            "3":"usernickname"
          },
          "where":""

        }
      },
      {
        "id":"removeUser",
        "query":{
          "crud":"delete",
          "column":{
            "1":""
          },
          "data":{
            "1":""
          },
          "where":""
		}
      }
    ]
  }
}
```

-----