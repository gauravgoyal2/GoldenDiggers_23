<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", -1);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Streaming Player</title>

<!-- only needed for manual HLS level selection -->
<link rel="stylesheet"
	href="//releases.flowplayer.org/quality-selector/flowplayer.quality-selector.css">

<!-- load the Flowplayer script -->
<script src="//releases.flowplayer.org/6.0.5/flowplayer.min.js"></script>

<!-- load the latest version of the hlsjs plugin -->
<script src="//releases.flowplayer.org/hlsjs/flowplayer.hlsjs.min.js"></script>

<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Varela+Round">

<link rel="stylesheet" href="css/sample.css">
</head>
<body
	style="background-image: url('img/bg-live.jpg'); background-position: center;">

	<table width="100%">
		<tr>
			<td><img src="img/logo-live.gif" align="left"
				style="margin-left: 12%; width: 30%; margin-top: 5%;"></td>
			<td><img src="img/comviva-logo.png" align="right"
				style="margin-right: 12%; width: 40%; margin-bottom: 10%;"></td>
		</tr>
	</table>



	<!-- <div align = "center" style="clear:both; margin-top:45px; margin-bottom:45px; font-weight:bold">
<font color ="#E00000" size="6px" ><h2>ComConnect</h2></font>
</div> -->

	<%
		if (null != request.getAttribute("isAliveFlag")
				&& request.getAttribute("isAliveFlag").equals(99)) {
	%>
	<div style="margin-top: 45px;">
		<div align="center" itemprop="video" itemscope=""
			itemtype="http://schema.org/VideoObject">
			<meta itemprop="duration" content="T1M33S" />
			<meta itemprop="thumbnailUrl" content="thumbnail.jpg" />

			<div id="player" style="width: 800px; height: 600px;">
				<h2>
					<span class="fontawesome-lock"></span>Live Stream
				</h2>
				<!-- <div   class="player_rtmp" style="width: 624px; height: 260px;" > -->
				<video id="player_rtmp" class="flowplayer" controls width="800"
					height="260"
					style="width: 800px; height: 600px; background-color:#000;"
					preload="metadata" autoplay="true"> <source
					type="application/x-mpegURL"
					src="http://ec2-52-76-56-4.ap-southeast-1.compute.amazonaws.com:1935/live1/smil:npdEventStream.smil/playlist.m3u8" />
				<source type="video/x-flv" src="mp4:stsp" /> </video>
			</div>
		</div>
	</div>

	<%
		} else {
	%>
	<div id="player" style="margin-top: 45px; margin-left: 15%"
		align="center">

		<h2>
			<span class="fontawesome-lock"></span>No Live Event
		</h2>


		<fieldset>
			<table width="640px" height="480px">
				<tr>
					<td style="text-align: center;"><img src="img/nolive1.png"
						height="480px" align="middle"></td>
				</tr>
			</table>



		</fieldset>


	</div>
	<%
		}
	%>



	<table width="100%" style="margin-top: 15%">
		<tr>

			<td
				style="position: relative; bottom: 0; width: 100%; margin-top: 100%; text-align: center">
				<font color="white">&copy; Copyright Comviva Technologies
					Limited. 2016</font>
			</td>
		</tr>
	</table>

</body>
</html>