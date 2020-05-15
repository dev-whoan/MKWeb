<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="MkWeb" prefix="mkw" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

This is home... (^_^)7
<mkw:get obj="list" rst="item">
	<h1>${item.test_SEQ} 번째 정보 </h1>
	<p> ${item.TITLE} </p>
	${item.INFO}
</mkw:get>


</body>
</html>