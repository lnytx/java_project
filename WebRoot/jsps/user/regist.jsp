<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
 -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <!-- <base href="<%=basePath%>"> -->
    
    <title>注册页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/regist.css'/>">
	<!--无效 <link rel="stylesheet" type="text/css" href="/jsps/css/user/regist.css"> -->
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jsps/js/user/regist.js'/>"></script>
	<!-- <script type="text/javascript" src="<c:url value='/jsps/jquery-1.5.1.js'></c:url>"></script> -->
  </head>
  
  <body>
  <div id="divMain">
  <div id="divTitle">
  	<span id="spanTitle">新用户注册</span>
  </div>
  <div id="divBody">
  	<form action="<c:url value='/UserServlet'/>" method="post" id="registForm">
  	  <!--  表示该为隐藏域,通常用于前后台传值。或者存取值 -->
  	<input type="hidden" name="method" value="regist"/>
  	<table id="tableForm">
  	<tr>
  	<td class="tdText">用户名</td>
  	<td class="tdInput">
  		<input class="inputClass" type="text" name="loginname" id="loginname" value="${form.loginname}"/><%--${form.loginname}这个是从UserServletreq.setAttribute("form",formUser)方法中返回的数据，将原来的数据返回回去，否则前台一提交如果有问题的话，所有写入的数据都丢失了，又需要重写，很不方便 --%>
  	</td>
  	<td class="tdError">
  		<label class="errorClass" id="loginnameError">${errors.loginname}</label>
  	</td>
  	</tr>
  	  	<tr>
  	<td class="tdText">登录密码</td>
  	<td>
  		<input class="inputClass" type="password" name="loginpass" id="loginpass" value="${form.loginpass}"/>
  	</td>
  	<td>
  		<label class="errorClass" id="loginpassError">${errors.loginpass}</label>
  	</td>
  	</tr>
  	 <tr>
  	<td class="tdText">确认密码</td>
  	<td>
  		<input class="inputClass" type="password" name="reloginpass" id="reloginpass" value="${form.reloginpass}"/>
  	</td>
  	<td>
  		<label class="errorClass" id="reloginpassError">${errors.reloginpass}</label>
  	</td>
  	</tr>
  	  	<tr>
  	<td class="tdText">Email</td>
  	<td>
  		<input class="inputClass" type="text" name="email" id="email" value="${form.email}"/>
  	</td>
  	<td>
  		<label class="errorClass" id="emailError">${errors.email}</label>
  	</td>
  	</tr>
  	<tr>
  	<td class="tdText">验证码</td>
  	<td>
  		<input class="inputClass" type="text" name="verifyCode" id="verifyCode" value="${form.verifyCode}"/>
  	</td>
  	<td>
  		<label class="errorClass" id="verifyCodeError">${errors.verifyCode}</label>
  	</td>
  	</tr>
  	<tr>
  	<td></td>
  	<td>
  		<div id="divVerifyCode"><img id="imgVerifyCode" src="<c:url value='/VerifyCodeServlet1'/>"/></div>
  	</td>
  	<td>
  		<label><a href="javascript:_hyz()">换一张</a></label>
  	</td>
  	</tr>
  	<tr>
  	<td></td>
  	<td>
  		<!-- HTML规定的，<input type=image>的图片上点击了可以提交 -->
  		<input type="image" src='<c:url value='/images/regist1.jpg'></c:url>' id="submitBtn"/>
  	</td>
  	<td>
  		<label></label>
  	</td>
  	</tr>
  	</table>
  	</form>
  </div>
  </div>
  </body>
</html>
