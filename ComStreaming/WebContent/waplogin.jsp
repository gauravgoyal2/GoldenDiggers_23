<!doctype html>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", -1);
%>
<html lang="en-US">
	<head>

		<meta charset="utf-8">

		<title>Live Stream - Login</title>

		<link rel="stylesheet"
			href="http://fonts.googleapis.com/css?family=Varela+Round">

		<link rel="stylesheet" href="css/sample.css">
	</head>

	<body style="background-image: url('img/backgroundDefault1.jpg');background-position: center;" >
		<table width=100%>
			<tr>
				<td>
					<img src="img/logoa.gif" align="left"
						style="margin-left: 12%; margin-top: 5%;">
				</td>
				<td>
					<img src="img/comviva-logo.png" align="right"
						style="margin-right: 12%; margin-bottom: 10%;">
				</td>
			</tr>
		</table>
		<div id="loginw">

			<h2>
				<span class="fontawesome-lock"></span>Sign In
			</h2>

			<form method="post" name="frm"
				action="SubmitRequest?method=generateotp">
				<input type="hidden" name="isAliveFlag" id="isAliveFlag" value="<%=request.getAttribute("isAliveFlag")%>">
				<input type="hidden" name="liveCount" id="liveCount" value="<%=request.getAttribute("liveCount")%>">

				<fieldset>
					<p>
						<%
						    if (null != request.getAttribute("error") && !request.getAttribute("error").equals("")) {
						%>
						<font color="red"><%=(String) request.getAttribute("error")%></font>
						<%
						    }
						%>
					</p>

					<p>
						<label for="email" style="font-size: 48px;">
							E-mail address
						</label>
					</p>
					<p>
						<input type="email" id="emailid" name="emailid"
							value="mail@mahindracomviva.com" onFocus="if(this.value=='mail@mahindracomviva.com')this.value=''" style="font-size: 48px;" size="50">
						<!-- pattern="^[A-Za-z0-9.-_\\+]+(\\[._A-Za-z0-9-]+)*@mahindracomviva.com" -->
					</p>
					<!-- <p>
						<font style="font-size: small;color: red;">Email Format: userid@mahindracomviva.com</font>
					</p> -->
					<!-- JS because of IE support; better: placeholder="mail@address.com" -->

					<!-- <p><label for="password">Password</label></p>
				<p><input type="password" id="password" value="" required></p>-->
					<!-- JS because of IE support; better: placeholder="password" -->

					<p>
						<input type="submit" value="Sign In">
					</p>

				</fieldset>

			</form>

		</div>
		<!-- end login -->
		<table width="100%" style="margin-top: 30%" >
		<tr>
		
		<td style="text-align: center;margin-bottom:2%">
		<font color="white" style="font-size: 28px">&copy; Copyright Comviva Technologies Limited. 2016</font>
		</td>
		</tr>
		</table>
	</body>
</html>
