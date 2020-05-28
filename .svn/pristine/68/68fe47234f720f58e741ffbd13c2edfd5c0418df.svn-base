 
 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="MkWeb" prefix="mkw" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="./css/mystyle.css" />
<script>

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

</script>
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
                        </tr>
                    </thead>
                    <tbody id="table-wrapper">
                    	<mkw:get obj="list" rst="alpha">
                    		<tr class="one-item">
                    			<td> ${alpha.name}</td>
                    			<td> ${alpha.address}</td>
                    		</tr>
                    	</mkw:get>
                        
                    </tbody>
                </table>

                <div id="search_name">
                    <form action="/main" method="post">
                        <!-- <label>이름 : </label> -->
                        <input type="text" name="param">
                        <input type="submit" value="찾기">
                    </form>
            </div>
        </div>

        <!-- footer -->
        <div class="footer">

        </div>
    </div>
</div>
</body>
</html>
