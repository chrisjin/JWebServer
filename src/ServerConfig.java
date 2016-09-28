/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.util.ArrayList;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

/**
 * @author shikai
 * server config file is
 * <server>
 * 	<prop1></prop1>
 *  <prop2></prop2>
 * </server>
 */
public class ServerConfig {
	static ServerConfig instance;
	synchronized public static ServerConfig getInstance(){
		if(instance == null)
			return new ServerConfig();
		else
			return instance;
	}
	final String configXMLName = "server_config.xml";
	int poolSize = 8;
	String serverName = "Jin";
	ArrayList<String> hostConfFiles = new ArrayList<String>();
	
	private ServerConfig()
	{
		parseXML(configXMLName);
	}
	
	/**
	 * @param fileName, the server config file
	 * 
	 */
	private void parseXML(String fileName)
	{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document document = null;
		try {
			document = builder.parse(new File(configXMLName));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		document.getDocumentElement().normalize();

		Element root = document.getDocumentElement();

		NodeList hostArrayNodes = root.getElementsByTagName("host");
		NodeList serverNameNode = root.getElementsByTagName("servername");
		NodeList poolSizeNode = root.getElementsByTagName("poolsize");
		if(hostArrayNodes != null){
			for (int i = 0; i < hostArrayNodes.getLength(); i++) {
				Node node = hostArrayNodes.item(i);
				hostConfFiles.add(node.getTextContent().trim());
			}
		}

		if(poolSizeNode != null && poolSizeNode.getLength() > 0){
			poolSize = Integer.parseInt(poolSizeNode.item(0).getTextContent().trim());
		}
		if(serverNameNode != null && serverNameNode.getLength() > 0){
			serverName = serverNameNode.item(0).getTextContent().trim();
		}
	}
	
	synchronized public ArrayList<String> getHostConfigs()
	{
		return hostConfFiles;
	}
	
	synchronized public int getThreadPoolCapacity(){
		return poolSize;
	}
	
	synchronized public String getServerName(){
		return serverName;
	}
}
