<!doctype html>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", -1);
%>
<html lang="en-US">
<head>

<meta charset="utf-8">

<title>Live Stream - Validation</title>

<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Varela+Round">

<link rel="stylesheet" href="css/sample.css">

</head>

<body
	style="background-image: url('img/bg-live.jpg'); background-position: center;">
	<%
		if (null != request.getAttribute("emailid")
				&& !request.getAttribute("emailid").equals("")) {
	%>
	<table width="100%">
		<tr>
			<td><img src="img/logo-live.gif" align="left"
				style="margin-left: 12%; width: 30%; margin-top: 5%;"></td>
			<td><img src="img/comviva-logo.png" align="right"
				style="margin-right: 12%; width: 40%; margin-bottom: 10%;"></td>
		</tr>
	</table>

	<div id="login" style="margin-top: 30%; width: 80%; text-align: center">

		<h2>
			<span class="fontawesome-lock"></span>Validate
		</h2>

		<form method="post" name="frm"
			action="SubmitRequest?method=validateotp">

			<fieldset>
				<p>
					<%
						if (null != request.getAttribute("error")
									&& !request.getAttribute("error").equals("")) {
					%>
					<font color="red"><%=(String) request.getAttribute("error")%></font>
					<%
						}
					%>
				</p>

				<p>
					<label for="password"> Enter otp <input type="hidden"
						name="emailid" id="emailid"
						value='<%=request.getAttribute("emailid")%>' />
					</label>
				</p>
				<p>
					<input type="password" id="otp" name="otp"
						oninvalid="setCustomValidity('Only 4 digit numeric allowed.')"
						maxlength="4">
				</p>

				<p>
					<input type="submit" value="Enter">
				</p>

			</fieldset>

		</form>

	</div>
	<table width="100%" style="margin-top: 15%">
		<tr>

			<td
				style="position: relative; bottom: 0; width: 100%; margin-top: 100%; text-align: center">
				<font color="white">&copy; Copyright Comviva Technologies
					Limited. 2016</font>
			</td>
		</tr>
	</table>
	<!-- end login -->
	<%
		} else {
	%>
	<jsp:forward page="/"></jsp:forward>
	<%
		}
	%>
</body>
</html>