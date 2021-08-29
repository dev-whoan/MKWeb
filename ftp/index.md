---
layout: custom
---

# File Transfer

## Location: /WEB-INF/classes/configs/ftps

-----

# JSON Configs

You can only have 1 ftp config, or much more.

I recommend you to seprate ftp configs into a pages or file extensions.

## Basic Properties

| name           | description                     | value                                                           |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| name           | name of Controller              | Unique ID in FTP Controller                                     |
| path           | path to save received files     | relative path and the root dir is webapps folder                |
| debug          | level to log                    | debug, info, warn, error                                        |
| auth           | is ftp require authority or not | yes, no |

## Service Properties

| name           | description                     | value                                                           |
|:---------------|:--------------------------------|:----------------------------------------------------------------| 
| id             | name of Controller              | Unique ID in FTP Controller                                     |
| type           | ftp type                        | ftp-receiver, ftp-remover                                       |
| servicepath    | sub directory under "path"      | don't need to be unique                                         |
| hash_dir       | hashing servicepath or not      | true, false. can be skipped |
| max_count      | limitation of file counts for one request | Integer > 0       |
| format         | allowed file format | example: jpeg, png, avi, txt, zip, ...  |

-----

# Usage Examples

## enctype must be multipart/form-data

FTP service will be called through MkWeb.conf's `mkweb.ftp.uri`.

It means, you must define ftp service in some of View Controller.

You can call the service via `mkweb.ftp.uri` with sending parameters that have defined in `View Controller's ftp service`.

Error will be occured if no service is defined in View Controller, or request with wrong parameters.

MkWeb do not define the result when you request more than max_count.

```json
{
  "Controller": {
    "name":"freeboard",
    "path":"/files",
    "debug":"error",
    "auth":"no",
    "services":[
      {
        "id":"posts",
        "type":"ftp-receiver",
        "servicepath":"/post",
        "hash_dir":"true",
        "max_count":"10",
        "format":{
          "1":"jpg",
          "2":"png",
          "3":"gif",
          "4":"PNG",
          "5":"jpeg",
          "6":"bmp",
          "7":"mp4",
          "8":"avi",
          "9":"zip"
        }
      },
      {
        "id":"comments",
        "type":"ftp-receiver",
        "servicepath":"/comment",
        "hash_dir":"true",
        "max_count":"1",
        "format":{
          "1":"jpg",
          "2":"png",
          "3":"gif",
          "4":"PNG",
          "5":"jpeg",
          "6":"bmp"
        }
      }
    ]
  }
}
```

## Result of executing ftp


```json
{
  "code":"201",
  "response":"[Success to upload : /files/post/1629781473272/MKW.png],",
  "excuted":"/files/post/1629781473272/MKW.png,"
}
```


Or Error will be returned in response.

## Test HTML for FTP

### View Controller Example
```json
{
  "Controller": {
    "name":"ftp-uploader",
    "last_uri":"Feed",
    "auth":"no",
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
          "kind":"ftp",
          "id":"posts"
        },
        "method":"post",
        "obj":"list",
        "parameter_name":"post_file",
        "value":{
          "1":"upload"
        }
      },
      {
        "page_static":"false",
        "type":{
          "kind":"ftp",
          "id":"comments"
        },
        "method":"post",
        "obj":"list",
        "parameter_name":"comment_file",
        "value":{
          "1":"upload"
        }
      }
    ]
  }
}
```


### HTML Example
```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MKWEB_FTP_TEST</title>
</head>
<body>
  <div>
  <div class="wrap">
    <!-- header -->
    <div class="header">Default Upload Page</div>
    <!-- section -->
    <div class="section">
      <div class="container">
	  
      <h1 id="title">MKWeb Test FTP - Upload</h1>
      <form action="/ftp/execute" method="POST" enctype="multipart/form-data">
        <label for="form-file">[Form]Select a Image file:</label>
        <input type="file" id="form-file" name="post_file.upload">
        <input type="file" id="form-file" name="post_file.upload">
        <input type="file" id="form-file" name="post_file.upload">
        <input type="file" id="form-file" name="post_file.upload">
        <input type="submit" />
      </form>
      <h1 id="title">MKWeb Test FTP - Image</h1>
      <mkw:ftp name="freeboard" id="posts" obj="list" img="yes">
      <p>name : ${mkw.name} </p>
      <img src="${mkw.result}" />
      </mkw:ftp>
	  
      </div>
    </div>
   </div>
  </div>
</body>
</html>
```