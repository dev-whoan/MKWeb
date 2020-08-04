 
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="MkWeb" prefix="mkw" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
<link rel="stylesheet" href="./css/mystyle.css" />
<script>

var user_seq;

window.onload = function(){
	let get = document.getElementsByClassName("one-item");
	console.log(get);
	if(!get){
		console.log("데이터 없음");
		let tbody = document.getElementById('table-wrapper');
		
		if(tbody){
			tbody.innerHTML = "<tr> <td>없음</td> <td> 없음</td> </tr>";
		}
	}
};

$(document).ready(function(){
	$("#textAJAX").click(function(){
		$.ajax({
	        type : "POST", //전송방식을 지정한다 (POST,GET)
	        url : "/data/receive",//호출 URL을 설정한다. GET방식일경우 뒤에 파라티터를 붙여서 사용해도된다.
	        dataType : "text",//호출한 페이지의 형식이다. xml,json,html,text등의 여러 방식을 사용할 수 있다.
	        data : {
	        	"upd.user_name" : $("#Name").val(),
	        	"upd.user_info" : $("#Info").val(),
	        	"upd.user_SEQ" : user_seq
	        },
	        error : function(){
	            alert("통신실패!!!!");
	        },
	        success : function(rd){
	            console.log(rd);
	        }
	         
	    });
	});
	
	$("#testtest").click(function(){
		$.ajax({
			type : "OPTIONS", //전송방식을 지정한다 (POST,GET)
	        url : "/mk_api_key/userapi",//호출 URL을 설정한다. GET방식일경우 뒤에 파라티터를 붙여서 사용해도된다.
	        dataType : "text",//호출한 페이지의 형식이다. xml,json,html,text등의 여러 방식을 사용할 수 있다.
	        error : function(){
	            alert("통신실패!!!!");
	        },
	        success : function(rd){
	            console.log(rd);
	        }
		})
	});
});

function modifyUser(seq){
	$("#modify-user").addClass("show");
	user_seq = seq;
}

function removeUser(seq){
	console.log(seq);
	$.ajax({
        type : "POST", //전송방식을 지정한다 (POST,GET)
        url : "/data/receive",//호출 URL을 설정한다. GET방식일경우 뒤에 파라티터를 붙여서 사용해도된다.
        dataType : "text",//호출한 페이지의 형식이다. xml,json,html,text등의 여러 방식을 사용할 수 있다.
        data : {
        	"del.user_SEQ" : seq
        },
        error : function(){
            alert("통신실패!!!!");
        },
        success : function(rd){
            console.log(rd);
        }
         
    });
}

</script>
<style>
#modify-user{
	display: none;
}

#remove-user{
	display: none;
}

.show{
	display: block !important;
}
</style>


</head>
<body>
This is page1.jsp

<div>
	
	<div class="wrap">
        <!-- header -->
        <div class="header">
        </div>
 
        <!-- section -->
        <div class="section">
            <div class="container">
                <h1 id="title">test</h1>
                <table id="user_info_list">
                    <thead>
                        <tr>
                            <th>name</th>
                            <th>address</th>
                            <th>비고</th>
                        </tr>
                    </thead>
                    <tbody id="table-wrapper">
                    	<mkw:get obj="list" result="alpha" like="no">
                    		<tr class="one-item">
                    			<td> ${alpha.name}</td>
                    			<td> ${alpha.address}</td>
                    			<td>
                    			<button onclick="modifyUser('${alpha.user_SEQ}')">수정</button>
                    			<button onclick="removeUser('${alpha.user_SEQ}')">삭제</button>
                    			</td>
                    		</tr>
                    	</mkw:get>
                        
                    </tbody>
                </table>

                <div id="search_name">
                    <form action="" method="post">
                        <!-- <label>이름 : </label> -->
                        <input type="text" name="param.user_name">
                        <input type="text" name="param.user_info">
                        <input type="submit" value="찾기">
                    </form>
                </div>
                
                <div id="modify-user">
                	<input type="text" id="Name" />
	                <input type="text" id="Info" />
	                <button id="textAJAX">수정</button>
                </div>
            </div>
        </div>

        <!-- footer -->
        <div class="footer">

        </div>
    </div>
</div>
</body>
</html>
