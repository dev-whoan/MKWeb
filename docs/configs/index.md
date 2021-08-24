---
layout: custom
---

# How to set MkWeb.conf

## Location: /WEB-INF/classes/configs

```ApacheConf
##############################################################
#					 Version 0.1.1
#
#                    Default Setting
#
##############################################################

# web address
mkweb.web.hostname=localhost
mkweb.web.default.language=ko
# DB DML(without Select) URI
# default: yes
mkweb.web.receive.use=yes

# The uri to receive data
# Don't touch it if you don't modify MkWeb.
# If you change, you need to modify web.xml to.
# Default: /data/receive
mkweb.web.receive.uri=/data/receive

##############################################################
#                     Setting MkWebAuthToken            
# mkweb.auth.use                    use mkweb jwt or not
# mkweb.auth.controller.name        is name of controller.
# mkweb.auth.uri                    is uri of create jwt
# mkweb.auth.redirect.use           is setting to use redirect when not been authorized
# mkweb.auth.redirect.uri           is uri to redirect when authorize required
#                                   page must be defined in configs
# mkweb.auth.secretkey              is secret key to generate signature
# mkweb.auth.lifetime               is lifetime of jwt (unit: second)
##############################################################
mkweb.auth.use=yes
mkweb.auth.controller.name=mkauthtoken
mkweb.auth.uri=/auth/login
mkweb.auth.redirect.use=yes
mkweb.auth.redirect.uri=/user/login
mkweb.auth.secretkey=mksecretkey
mkweb.auth.lifetime=600

##############################################################
#                     Setting DB            
# mkweb.db.hostname is ip address of data base.
# mkweb.db.port     is port number of data base.
# mkweb.db.id       is id of the data base.
# mkweb.db.pw       is pw of the data base.
# mkweb.db.db       is Database name
##############################################################
mkweb.ftp.use=yes
mkweb.ftp.uri=/ftp/execute

##############################################################
#                     Setting DB            
# mkweb.db.hostname is ip address of data base.
# mkweb.db.port     is port number of data base.
# mkweb.db.id       is id of the data base.
# mkweb.db.pw       is pw of the data base.
# mkweb.db.db       is Database name
##############################################################
mkweb.db.hostname=localhost
mkweb.db.port=3306
mkweb.db.database=db
mkweb.db.id=id
mkweb.db.pw=pw

##############################################################
#                      		REST Api
##############################################################
# default: no
mkweb.restapi.use=yes

# default : mkapi
mkweb.restapi.uri=mkapi

# default : mkapi
mkweb.restapi.docs=docs

# Only restapi can accessed by host web site.
# It means, the data cannot be accessed cross-origin.
# default : no [yes, no]
mkweb.restapi.hostonly=no

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


