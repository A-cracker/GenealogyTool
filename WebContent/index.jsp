<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<!DOCTYPE HTML>
<html>
<head>
<title>工具开发环境</title>
<style type="text/css">
.head {
	background-color: rgba(192, 192, 192, 0.6);
	text-align: center;
	color: rgba(16, 64, 0, 1);
	font-size: 30px;
	font-family: Microsoft YaHei;
}

.block {
	background-color: rgba(192, 192, 192, 0.4);
	padding-top: 1px;
	padding-bottom: 1px;
	height: 32px;
	display: flex;
}

.name {
	display: inline-block;
	text-align: right;
	height: 100%;
	width: 124px;
}

#errorMsg {
	color: red;
	text-align: right;
}

body {
	width: 1024px;
	margin: 0 auto;
	padding-top: 10%;
}

input {
    box-sizing: border-box;
	flex: 1 1 auto;
	height: 30px;
	border: none;
	font-size: 20px;
	background-color: rgb(253, 240, 240);
}

input:-webkit-autofill {
	-webkit-box-shadow: 0 0 0px 16px rgb(253, 240, 240) inset;
}

select {
	flex: 1 1 auto;
	height: 32px;
	border: none;
	font-size: 20px;
	background-color: rgb(253, 240, 240);
}

.select {
	flex: 1 1 auto;
	height: 32px;
	border: none;
	font-size: 20px;
	background-color: rgb(253, 240, 240);
}

a {
	display: block;
	text-decoration: none;
	color: black;
	text-align: center;
	font-size: 26px;
	background-color: rgba(192, 192, 192, 0.6);
}

a:HOVER {
	background-color: rgba(192, 192, 192, 0.8);
	transition: background-color 0.5s;
}

#daemonButtonDiv{
	text-align: center;
}

#daemonButtonDiv button{
	width: 20%;
}
</style>
<body>
	<div class="head">工具开发环境</div>
	<form id="form">
		<div class="block">
			<div class="name">工具ID：</div>
			<input name="toolID" id="toolID" placeholder="工具ID，用不到可以不填" value="4389044">
		</div>
		<div class="block">
			<div class="name">帮区ID：</div>
			<input name="runningBandID" id="runningBandID" placeholder="工具运行帮区视图ID，用不到可以不填" value="404750018">
		</div>
		<div class="block">
			<div class="name">bandObjID：</div>
			<input name="bandObjID" id="bandObjID" placeholder="帮区对象ID，用不到可以不填" value="404750016">
		</div>
		<div class="block">
			<div class="name">bandName：</div>
			<input name="bandName" id="bandName" placeholder="帮区名称，用不到可以不填" value="飞元网校主区">
		</div>
		<div class="block">
			<div class="name">设施帮区ID：</div>
			<input name="facilityBandID" id="facilityBandID" placeholder="工具运行设施帮区ID，用不到可以不填">
		</div>
		<div class="block">
			<div class="name">organizationID：</div>
			<input name="organizationID" id="organizationID" placeholder="机构ID" value="404750018">
		</div>
		<div class="block">
			<div class="name">工具操作平台：</div>
			<select name="operationPlatform">
				<option value="0">PC端工具</option>
				<option value="1">移动端工具</option>
				<option value="2">通用</option>
			</select>
		</div>
		<div class="block">
			<div class="name">用户帐号：</div>
			<input name="userAccount" placeholder="运行工具的用户的帐号，用不到可以不填" value="linjintian">
		</div>
		<div class="block">
			<div class="name">用户名：</div>
			<input name="userName" placeholder="运行工具的用户的用户名，用不到可以不填" value="林进添">
		</div>
		<div class="block">
			<div class="name">用户ID：</div>
			<input name="userID" id="userID" placeholder="运行工具的用户ID，用不到可以不填" value="4749755">
		</div>
		<div class="block">
			<div class="name">accessToken：</div>
			<input name="accessToken" id="accessToken" placeholder="运行工具的用户的登录令牌，用不到可以不填" value="">
		</div>
		<div class="block">
			<div class="name">是否有数据库：</div>
			<select name="hasDB">
				<option value="0">否</option>
				<option value="1">是</option>
			</select>
		</div>
		<div class="block">
			<div class="name">工具运行参数：</div>
			<input name="param" id="param" placeholder="工具运行参数，用不到可以不填" >
		</div>
		<div class="block">
			<div class="name">返回类型：</div>
			<select name="returnType">
				<option value="PAGE">页面</option>
				<option value="VALUE">结果值</option>
			</select>
		</div>
		<div class="block">
			<div class="name">运发平台：</div>
			<select class="select" name="clientType1">
				<option value="1">web</option>
				<option value="2">app</option>
				<option value="3">微信</option>
			</select> <select class="select" name="clientType2">
				<option value="1">windows</option>
				<option value="2">linux</option>
				<option value="3">android</option>
				<option value="4">IOS</option>
			</select>
		</div>
		<a href="#" onclick="gotoRunTool();">运行</a>
	</form>
	<br>
	<br>
	<br>
	<br>
	<form id="form" style="background-color: #e5e5e5;">
		<fieldset>
			<legend>后台守护运行</legend>

			运行参数: <input type="text" id="deamonParam" value="Hello world" > <br><br>
			延迟启动时间: <input type="number" id="startTime" value="0" style="width: 800px;">&nbsp;毫秒
			<br><br> 时间表达式（cron）: <input type="text" id="timeExpression" value="0/5 * * * * ?" style="width: 800px;">
			<br><br> 运行时间: <input type="number" placeholder="-1为永不停止" id="stopTime" value="-1" style="width: 800px;">&nbsp;毫秒
			<br> <br>
			<div id="daemonButtonDiv">
				<button type="button" id="startDeamon" onclick="start()">启动</button>
				&nbsp;&nbsp;
				<button id="stopDeamon" type="button" onclick="stop()">停止</button>
			</div>
		</fieldset>
	</form>
	<br>
	<br>
	<div id="errorMsg"></div>
	<script>
		function gotoRunTool() {
			var form = document.getElementById("form");
			form.action = '${pageContext.request.contextPath}' + "/gotoRunTool";
			form.method = "post";
			form.target = "_blank";
			form.submit();
		}
		
		function start(){
			var time = new Date().getTime();
			var delay = document.getElementById("startTime").value;
			var stopTime = document.getElementById("stopTime").value;
			
			stopTime = stopTime == -1 ? -1 : Number(time) + Number(stopTime);
			
			var fd = new FormData();
		    fd.append("deamonMode", "BACKGROUND");
		    fd.append("param", document.getElementById("deamonParam").value);
		    fd.append("startTime", Number(time) + Number(delay));
		    fd.append("stopTime", stopTime);
		    fd.append("timeExpression", document.getElementById("timeExpression").value);
		    fd.append("toolID", document.getElementById("toolID").value);
		    fd.append("bandID", document.getElementById("runningBandID").value);
		    fd.append("userID", document.getElementById("userID").value);
		    fd.append("facilityBandID", document.getElementById("facilityBandID").value);
		    
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.onreadystatechange=function(){
				if(xmlHttp.readyState==4){
					alert(xmlHttp.responseText);
				}
			}
			xmlHttp.open("POST","deamon",true);
			xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			xmlHttp.send(conver(fd));
			
			function conver(fd){
				var rs = '';
				var keys = fd.keys();
				for(var v = keys.next();!v.done;v = keys.next()){
					rs = rs + "&" + v.value + "=" + fd.get(v.value);
				}
				
				return rs.slice(1,rs.length)
			}
		}
		
		function stop(){
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.onreadystatechange=function(){
				if(xmlHttp.readyState==4){
					alert(xmlHttp.responseText);
				}
			}
			xmlHttp.open("DELETE","deamon",true);
			xmlHttp.send();
		}
	</script>
</body>
</html>
