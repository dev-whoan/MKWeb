<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>MKWeb Insert Test</title>
    
    <style>
        body {
            position: relative;
            width: 100%;
            height: 100%;
            margin: auto;
        }
        /* section */
        .section {
            width: 96%;
            height: 100%;
            padding: 2%;
        }

        .container {
            margin-top: 50px;
            background-color: whitesmoke;
            width: 94%;
            height: 100%;
            padding: 3%;
            text-align: center;
        }

        /* login */
        #action_login_title {
            font-family: Fantasy;
            font-weight: bold;
        }

        #input_id, #input_password {
            width: 100%;
            height: 50px;
            line-height: 50px;
            text-align: center;
            margin-top: 2%;
            margin-bottom: 3%;
            border: 1px solid #ccc;
            background-color: white;
        }

        #login_btn {
            width: 100%;
            height: 50px;
            line-height: 50px;
            text-align: center;
            margin-top: 2%;
            border: 1px solid #ccc;
            background-color: mediumaquamarine;
        }

        #input_id input, #input_password input {
            padding-left: 2%;
            font-size: 100%;
            width: 88%;
            height: 70%;
            line-height: 70%;
            border: 0px;
        }

        #login_btn input {
            font-family: sans-serif;
            font-weight: bold;
            font-size: larger;
            width: 100%;
            height: 100%;
            border: 0px;
            background-color: mediumaquamarine;
        }

    </style>
</head>

<body>
    <div class="wrap">
            <!-- section -->
        <div class="section">
            <div class="container">
                <h1 id="action_login_title">
                    대여 시스템
                </h1>
                <div id="login_wrap">
                
                    <form action="/mkwebdata/receive" method="post" id="login_form">
                        <div id="input_id">
                            <input type="text" name="param.user_name" placeholder="아이디">
                        </div>
                        <div id="input_password">
                            <input type="password" name="param.user_info" placeholder="비밀번호">
                        </div>
                        <div id="login_btn">
                            <input type="submit" value="로그인">
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
    </div>
</body>

</html>