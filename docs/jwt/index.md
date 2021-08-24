---
layout: custom
---

# Json Web Tokens

## Location: /WEB-INF/classes/configs/MkAuthToken.json

-----

# JSON Configs

You can only create 1 MkAuthToken.json.

## Basic Properties

| name           | description                     | value                                                           |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| debug          | level to log                    | debug, info, warn, error                                        |
| algorithm      | algorithm to sign               | must not duplicated with other views that have same parent uri. HMACSHA256 only supported now  |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| auth                                                                                                               | 
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| sql                                                                                                                | 
| controller     | controller that have sql to generate JWT   | Usually user login sql |
| service        | service id that include user login sql     | Usually user login sql |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| parameter                                                                                                          | 
| "1" to "n"     | number of parameter to execute services   | n is amount of number of required parameters          |
| values         | parameter to execute services. it is wrapped with "@" in target sql service   | example: "userid", "userpw" |

## Payload

Payload is one of body that generating JWT.

So you must define items that forming payload, however you must not put private information about user or must not be exposed.

### What You can Put
- user nickname
- user id
- user followers
- user likes
- ...

### What You CANNOT Put
- user password
- user home address
- ...

### Payload Examples
```json
"payload":{
  "sql result keys": "payload keys"
}
```

Payload have above structure. MkWeb re-factor the sql results because of safety.

For example, if your sql just returned exact db table's column without modifying, hackers can attack your db.

And even front web can get payload if you don't hash your JWT, so MkWeb re-factor those keys.

-----

# Basic Javascript for handling Token in Front Side

```javascript
// name must be same with defined in MkWeb.conf --> mkweb.auth.controller.name
let __MK_TOKEN_NAME__ = 'mkauthtoken';
// lifetime need to be same with defined in MkWeb.conf --> mkweb.auth.lifetime
// if the time is not same, may be alive in your cookie, but will be invalid
let __MK_TOKEN_LIFETIME__ = 600;


function setTokenCookie(token){
    var date = new Date();
    date.setTime(date.getTime() + __MK_TOKEN_LIFETIME__ * 1000);
    let cookieInfo = __MK_TOKEN_NAME__ + '=' + token + ';expires=' + date.toUTCString() + ';path=/';
    return cookieInfo;
}

function getToken(){
    var value = document.cookie.match('(^|;) ?' + __MK_TOKEN_NAME__ + '=([^;]*)(;|$)');
    return value ? value[2] : null;
}

function removeTokenCookie(cookieInfo) {
    var date = new Date();
    document.cookie = __MK_TOKEN_NAME__ + "= ; expires=" + date.toUTCString() + "; path=/";
}
```

-----

# Usage Examples

## MkAuthToken.json
```json
{
  "Controller":{
    "level":"debug",
    "algorithm":"HMACSHA256",
    "auths":{
      "sql":{
        "controller":"user",
        "service":"doLogin"
      },
      "parameter":{
        "1":"id",
        "2":"password"
      }
    },
    "payload":{
      "id":"user",
      "age":"old"
    }
  }
}
```

## Sql Controller
```json
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
          "where":"userid = @id@ AND userpw = @password@"
        }
      }
    ]
  }
}
```

## How to Generate Token

### Request of Generating Token
```javascript
//javascript
var myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

var raw = JSON.stringify({
  "id": USER_ID,
  "passwod": USER_PW
});

var requestOptions = {
  method: 'POST',
  headers: myHeaders,
  body: raw,
  redirect: 'follow'
};

fetch("/auth/login", requestOptions)
  .then(response => response.text())
  .then(result => {
    if(result.token){
	  setTokenCookie(token);
	}
  })
  .catch(error => console.log('error', error));
```

### Result of Generating Token

```json
{
  "code":200,
  "token":"eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJib29rbWFyayI6IltdIiwibGlrZSI6IltdIiwibGFzdGxvZ2luIjoiMjAyMS0wOC0xNCAwMToxNjozNy4wIiwic2V4IjoiMCIsInByb2ZpbGVpbWciOiJudWxsIiwidXNlcmVtYWlsIjoibWFzdGVyX2FkbWluQHVuaWVhcnRoLmNvbSIsInVzZXJuaWNrbmFtZSI6Iuq0gOumrOyekCIsInRpbWVzdGFtcCI6MTYyOTc4Mjk5NTE3OX0.4oDk0qTn32i-qkR5kPfIafmVd8CpflzmT2CMQg-dJ3g"
}
```

### Using

```javascript
/*
Javascript
executing some services that requires authority OR
*/
var myHeaders = new Headers();
let myToken = getToken();

myHeaders.append("Content-Type", "application/json");
myHeaders.append("Authorization", ("Bearer " + myToken) );

var raw = JSON.stringify({
  "id": USER_ID,
  "passwod": USER_PW
});

var requestOptions = {
  method: 'POST',
  headers: myHeaders,
  body: raw,
  redirect: 'follow'
};

//
fetch("/requestpath", requestOptions)
  .then(response => response.text())
  .then(result => console.log(result))
  .catch(error => console.log('error', error));
```

It's okay when you explorer the pages, but need to send token when you REQUEST something.
