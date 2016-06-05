<!DOCTYPE html>
<%@page import="java.util.List"%>
<%@page import="com.comviva.vo.VideoVO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<html lang="en-US">
	<head>

		<meta charset="utf-8">
		<title>Live Streaming Count</title>
		<style>
table, tr, td {
    border: 1px solid black;
    font-weight: bold;
    font-size: larger;
    background-color: #D6D3D3;
    font-family: monospace;
    
}
</style>
	</head>

	<body style="background-image: url('img/backgroundDefault1.jpg');background-position: center;" >
		<a href="/VideoStream/SubmitRequest?method=getDBStreamCount">Click here</a> for DB Stream Count
			<%
		    if (null != request.getAttribute("appName") && !request.getAttribute("appName").equals("")) {
			%>
			<br/><br/><br/>
			
			<table align="center">
			<tr>
				<td>
					Stream Name 
				</td>
				<td>
					Count
				</td>
			</tr>
			<tr>
				<td>
					<%=request.getAttribute("appName")%>
				</td>
				<td>
					<%=request.getAttribute("totalCount")%>
				</td>
			</tr>
			
			</table>
			<br/><br/><br/>
			<table align="center">
			<tr>
				<td>
					Stream Name
				</td>
				<td>
					HLS Stream
				</td>
				<td>
					RTSP Stream
				</td>
			</tr>
			<%
		    if (null != request.getAttribute("streamlist")) {
		    	List<VideoVO> list = (ArrayList<VideoVO>)request.getAttribute("streamlist");
		    	Iterator itr = list.iterator();
		    	while(itr.hasNext()){
		    		VideoVO vo = (VideoVO)itr.next();
			%>
			
				<tr>
				<td>
					<%=vo.getStreamName()%>
				</td>
				<td>
					<%=vo.getHttpCount()%>
				</td>
				<td>
					<%=vo.getRtspCount()%>
				</td>
			</tr>
				
				<%} %>
			<%} %>
			
		<%
		    }
		%>	
		</table>
		
	</body>
</html>