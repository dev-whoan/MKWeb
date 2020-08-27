<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="MkWeb" prefix="mkw" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MKWeb Page</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
<link rel="stylesheet" href="./css/mystyle.css" />

<style>
* {margin: 0;padding: 0;}
body {position: relative;width: 100%;height: 100%;margin: auto;text-align: center;}
.container {position: relative;max-width: 550px;max-height: 600px;margin: auto;margin-top: 5%;border: 1px solid #ccc;box-shadow: 3px 3px 3px #aaa;padding: 10px; overflow-y: scroll;}
#title {margin-top: 3%;font-family: Fantasy;border-bottom: 1px solid #ccc;padding-bottom: 10px;font-size: 40px;font-weight: bold;}
#user_info_list {border-collapse: collapse;width: 100%;}
#user_info_list td, #user_info_list th {border: 1px solid #ccc;width: 50%;}
#search_name {text-align: left;margin-top: 3%;margin-bottom: 2%;}
#modify-user{display: none;}
#remove-user{display: none;}
.show{display: block !important;}
.submit{display:inline-block;background-color: lightcyan;width:75px;height:35px;line-height:35px;border:1px solid black;cursor:pointer;}
</style>
<script>

$(document).ready(function(){
	$('.submit').click(function(){
		let id = $(this).attr('id');
		
		if(id == 'ajax_insert'){
			$.ajax({
		        type : "POST", //전송방식을 지정한다 (POST,GET)
		        url : "/data/receive",//호출 URL을 설정한다. GET방식일경우 뒤에 파라티터를 붙여서 사용해도된다.
		        dataType : "text",//호출한 페이지의 형식이다. xml,json,html,text등의 여러 방식을 사용할 수 있다.
		        data : {
		        	"ins.uname" : $("#user_name").val(),
		        	"ins.uclass" : $("#user_class").val()
		        },
		        error : function(){
		            alert("통신실패!!!!");
		        },
		        success : function(rd){
		            console.log(rd);
			        location.href="/";
		        }
		    });
		}
		
	});
});

</script>
</head>
<body>

<div>
	<div class="wrap">
        <!-- header -->
        <div class="header">
        </div>
 
        <!-- section -->
        <div class="section">
            <div class="container">
                <h1 id="title">MKWeb Get User</h1>
                <table id="user_info_list">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Class</th>
                        </tr>
                    </thead>
                    <tbody id="table-wrapper">
                    	<mkw:get name="selectUser" obj="list" like="no">
                    		<tr class="one-item">
                    			<td> ${mkw.name}</td>
                    			<td> ${mkw.u_class}</td>
                    		</tr>
                    	</mkw:get>
                    </tbody>
                </table>

                <div id="search_name">
                    <form method="post" style="margin: 0 auto;">
                        <!-- <label>이름 : </label> -->
                        <input type="text" name="user_class">
                        <input type="submit" value="Search">
                    </form>
                </div>
                <h1 id="title">Get User By Class</h1>
                <table id="user_info_list">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Class</th>
                        </tr>
                    </thead>
                    <tbody id="table-wrapper">
                    	<mkw:get name="selectUserByClass" obj="list" like="no">
                    		<tr class="one-item">
                    			<td> ${mkw.name}</td>
                    			<td> ${mkw.u_class}</td>
                    		</tr>
                    	</mkw:get>
                    </tbody>
                </table>
                <h1 id="title">Insert User By Form</h1>
                <br>
                <form action="/data/receive" method="post">
                	<label for="ins_uname">Name</label>
                	<input type="text" name="ins.uname" />
                	<label for="ins_class">Class</label>
                	<input type="text" name="ins.uclass" />
                	<input type="submit" value="Submit" />
                </form>
                
                <h1 id="title">Insert User By Ajax</h1>
                <br>
                <label for="user_name">Name</label>
                <input type="text" id="user_name" />
                <label for="user_class">Class</label>
                <input type="text" id="user_class" />
                <span class="submit" id="ajax_insert">Submit</span>
                <br><br>
            </div>
        </div>

        <!-- footer -->
        <div class="footer">

        </div>
    </div>
</div>
</body>
</html>
