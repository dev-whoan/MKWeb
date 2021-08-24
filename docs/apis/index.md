---
layout: custom
---

# RESTful API

## Location: /WEB-INF/classes/configs/apis

-----

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