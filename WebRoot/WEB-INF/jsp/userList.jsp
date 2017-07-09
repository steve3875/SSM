<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/tag.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>用户列表</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
</head>

<body>
	用户列表：
	<table width="100%" border=1>
		<tr>
			<td>id</td>
			<td>usercode</td>
			<td>username</td>
			<td>password</td>
			<td>salt</td>
			<td>locked</td>
			<td>operator</td>
		</tr>
		<c:forEach items="${userList }" var="user">
			<tr>
				<td>${user.id }</td>
				<td>${user.usercode }</td>
				<td>${user.username }</td>
				<td>${user.password }</td>
				<td>${user.salt }</td>
				<td>${user.locked }</td>

				<td>
					<shiro:hasPermission name="user:edit">
						<a href="${pageContext.request.contextPath }/userEdit.action?id=${user.id}">修改</a>
					</shiro:hasPermission>

				</td>
			</tr>
		</c:forEach>

	</table>
</body>
</html>
