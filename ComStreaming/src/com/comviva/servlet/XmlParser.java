package com.comviva.servlet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlParser {
    public static void main(String[] args) {
	HttpURLConnection httpConnection = null;
	DataOutputStream wr = null;
	InputStream is = null;
	BufferedReader rd = null;
	URL url = null;
	//String strURL = "http://localhost:8080/VideoStream/xml/success.xml";
	//String strURL = "http://localhost:8080/VideoStream/xml/failure1.xml";
	String strURL = "http://localhost:8080/VideoStream/xml/failure2.xml";
	StringBuffer response = new StringBuffer();
	InputSource ipsour = null;
	String sdpResp = "",sdpResp1 = "";
	try {

	    url = new URL(strURL);

	    httpConnection = (HttpURLConnection) url.openConnection();
	    int connTimeout = 1000;
	    httpConnection.setConnectTimeout(connTimeout);
	    httpConnection.setRequestMethod("GET");
	    httpConnection.connect();

	    // Get Response
	    is = httpConnection.getInputStream();
	    rd = new BufferedReader(new InputStreamReader(is));
	    String line;

	    while ((line = rd.readLine()) != null) {
		response.append(line);
	    }
	    rd.close();
	    is.close();
	    httpConnection.disconnect();

	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    ipsour = new InputSource();
	    ipsour.setCharacterStream(new StringReader(response.toString().trim()));
	    Document mydoc = builder.parse(ipsour);
	    mydoc.getDocumentElement().normalize();
	    NodeList nlist = mydoc.getElementsByTagName("Application");
	    boolean flag = false, aflag= false;
	    if (nlist.getLength() > 0) {

		for (int i = 0; i < nlist.getLength(); i++) {
		    Node node = nlist.item(i);
		    
		    if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			sdpResp = getValue("Name", element);
			//System.out.println("errorCode: " + sdpResp);
			if(sdpResp.equals("live")){
			    aflag = true;
			    NodeList nlist1 = mydoc.getElementsByTagName("Stream");
			    if (nlist1.getLength() > 0) {

				for (int j = 0; j < nlist1.getLength(); j++) {
				    Node node1 = nlist1.item(j);
				    
				    if (node1.getNodeType() == Node.ELEMENT_NODE) {
					Element element1 = (Element) node1;
					sdpResp1 = getValue("Name", element1);
					//System.out.println("errorCode: " + sdpResp1);
					if(sdpResp1.equals("myStream")){
					    flag = true;
					}
					
				    }
				}
			    } 
			}
			
		    }
		}
	    } 
	    if(aflag && flag){
		System.out.println("Stream hai");
	    }else if(aflag && !flag){
		System.out.println("live hai par stream nahi");
	    }else if(!aflag && !flag){
		System.out.println("kuch nahi");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    try {
		if (wr != null)
		    wr.close();
		if (is != null)
		    is.close();
		if (rd != null)
		    rd.close();
		if (httpConnection != null)
		    httpConnection.disconnect();

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	    // for testing
	}
    }

    private static String getValue(String tag, Element element) {
	NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
	Node node = (Node) nodes.item(0);
	return node.getNodeValue();
    }
}
