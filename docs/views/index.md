---
layout: custom
---

# View

## Location: /WEB-INF/classes/configs/views

-----

# JSON Configs

You need each JSON files for every pages you want to create. It means, there are 5 json files if your web site have 5 pages.

Each json files, indeed views, is one of Controllers.

Also page includes services, for examples, sqls, jwt, ftp, etc, therefore you need to set services inside of json file.

## Basic properties

| name           | description                     | value                                                           |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| name           | name of Controller              | Unique ID in Views could have same value with last_uri          |
| last_uri       | last segment of the page        | must not duplicated with other views that have same parent uri  |
| debug          | level to log                    | debug, info, warn, error                                        |
| auth           | requiring authority or not      | yes, part for some services, no                                 |
| api            | is page for api or not          | yes for other library(react, just html, ...), no for jsp        |

MkWeb also supports separate pages between different platforms.

## Device properties

| name           | description                                             | value                                 |
|:---------------|:--------------------------------------------------------|:--------------------------------------| 
| desktop        | When client connected via desktop platform              | must includes device service          |
| android        | When client connected via android platform              | must includes device service          |
| ios            | When client connected via ios     platform              | must includes device service          |
|:---------------|:--------------------------------------------------------|:--------------------------------------| 
| device services                                                                                                  |
| default        | index page for default language. you can add more supports pages for another language with changing "default" key into another language like "en".              | must have default key         |
| path	         | JSP: dri location for jsp file. ANOTHER: empty path |  |
| file           | JSP: jsp file name (index.jsp). ANOTHER: empty file |  |
| uri            | uri for the platform | |

## Service properties

View Controller have at least one service. 

Services are could be SQL, FTP.

| name           | description                                                                                                   | value                           |
|:---------------|:--------------------------------------------------------------------------------------------------------------|:--------------------------------| 
| page_static    | is service executes right after page have loaded. Usually services that executed without user request.        | true, false                     |
|:---------------|:--------------------------------------------------------------------------------------------------------------|:--------------------------------| 
| type                                                                                                                                                             |
| kind           | type of service. the type is a kind of controller, and the controller must have service that have same id     | sql, ftp                        
| id             | id of service that defined in the target Controller.                                                          | must be defined on target contr 
|:---------------|:--------------------------------------------------------------------------------------------------------------|:--------------------------------| 
| method         | method to execute the service                                                                                 | GET, POST                       |
| obj            | service will return the result with the obj data type                                                         | list                            |
| parameter_name | superscript of parameter for data communication. each service must have unique superscript. For page_static services, this property must be empty. this service is used with value property.                                                         | Unique parameter name |
| value          | subscript of parameter for data communication. this service is used with parameter_name property. for sql service, this value is used on SQL json file too, will be wrapped with "@". You can check details on SQL page |  |

## Usage Examples

### Device Examples

```javascript
...
"device":{
    "desktop":{
        "default":{
            "path":"/views/root/desktop",
            "file":"main.jsp",
            "uri":""
        },
        "en":{
            "path":"/views/root/desktop/eng",
            "file":"main.jsp",
            "uri":"/eng"
        }
    },
    "android":{
        "default":{
            "path":"/views/root/mobile/android",
            "file":"main.jsp",
            "uri":""
        },
        "en":{
            "path":"/views/root/mobile/android/eng",
            "file":"main.jsp",
            "uri":"/eng"
        }
    },
    "ios":{
        "default":{
            "path":"/views/root/mobile/ios",
            "file":"main.jsp",
            "uri":""
        },
        "en":{
            "path":"/views/root/mobile/ios/eng",
            "file":"main.jsp",
            "uri":"/eng"
        }
    }
}
...
```

### View Controller without any service

This controller is for index page because there is no name and no uri.

Also controller have no services even page_static, and the jsp file is located in /WEB-INF/views/root/main.jsp (As I repeat, if you use some speicifed library, jsp file can be skipped );

No user login required.

```javascript
{
    "Controller": {
        "name":"",
        "last_uri":"",
        "debug":"error",
		"auth":"no",
        "api":"no",
		"device":{
            "desktop":{
                "default":{
                    "path":"/views/root",
                    "file":"main.jsp",
                    "uri":""
                }
            }
        },
        "services":[
        {
            "page_static":"true",
            "type":{
                "kind":"",
                "id":""
            },
            "method":"",
            "obj":"",
            "parameter_name":"",
            "value":{
                "1":""
            }
        }]
    }
}
```


### Page Controller with one login service

This controller means, index page that include user login.

User login service is sql type, and id of sql is doLogin. It means, in some SQL json file must include service that named doLogin.

To request a user login, page file (.jsp, .html, .jsx whatever) must send request with parameters: user.id, user.password

```javascript
{
    "Controller": {
        "name":"",
        "last_uri":"",
		"debug":"debug",
		"auth":"no",
        "api":"no",
        "device":{
            "desktop":{
                "default":{
                    "path":"/views/root",
                    "file":"main.jsp",
                    "uri":""
                }
            }
        },
        "services":[
        {
            "page_static":"true",
            "type":{
                "kind":"",
                "id":""
            },
            "method":"",
            "obj":"",
            "parameter_name":"",
            "value":{
                "1":""
            }
        },
        {
            "page_static":"false",
            "type":{
                "kind":"sql",
                "id":"doLogin"
            },
            "method":"post",
            "obj":"list",
            "parameter_name":"user",
            "value":{
                "1":"id",
                "2":"password"
            }
        }]
    }
}
```