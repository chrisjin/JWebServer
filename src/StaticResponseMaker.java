/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author shikai
 * run static resource server policy
 */
public class StaticResponseMaker implements ResponseMaker {

	String getBuiltInFailPage(){
		String fail = "404 Not Found!";
		return fail;
	}
	
	void processGet(String uri, Map<String, String> properties, 
			OutputStream clientWriter, SiteConfig config) {
		
		ResourceManager rscMgr = config.getResourceManager();
		InputStream rawInput = rscMgr.getStream(uri);
		int code = 200;
		String filePath = rscMgr.getFullPath(uri);
		if(rawInput == null){
			rawInput = rscMgr.getStream("/" + config.getNotFoundPage());
			filePath = "/" + config.getNotFoundPage();
			//return;
			if(rawInput == null){
				rawInput = new ByteArrayInputStream(getBuiltInFailPage().getBytes());
			}
			code = 404;
		}
		sendFile(rawInput, clientWriter, filePath, code, config);
	}
	
	void processPost(String URI, Map<String, String> properties,
			InputStream payload, int payloadLen,
			OutputStream clientWriter, SiteConfig config) {
		ServerLog.writeln("PayloadLen: " + payloadLen + ", "
				+ "PayloadType: " + properties.get(HTTPTerms.HEADER_CONTYPE));
		byte[] postCon = new byte[payloadLen];
		try {
			payload.read(postCon);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		ServerLog.writeln("Payload: " + new String(postCon));
	}
	
	void processPut(String URI, Map<String, String> properties,
			InputStream payload, int payloadLen,
			OutputStream clientWriter, SiteConfig config) {
		byte[] postCon = new byte[payloadLen];
		try {
			payload.read(postCon);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		ServerLog.writeln("Payload: " + new String(postCon));	
	}
	
	@Override
	public void make(HTTPHeader request, InputStream payload, int payloadLen, 
			OutputStream clientWriter, SiteConfig config){

		String[] tokens = request.firstLine.split(" ");
		if(tokens.length < 2)
			return;
		String uri = tokens[1];
		if(payloadLen > 0){
			if(tokens[0].toUpperCase().equals(HTTPTerms.METHOD_POST))
			{
				processPost(uri, request.getProperties(), payload, 
						payloadLen, clientWriter, config);
			}
			if(tokens[0].toUpperCase().equals(HTTPTerms.METHOD_PUT))
			{
				processPut(uri, request.getProperties(), payload, 
						payloadLen, clientWriter, config);
			}
		}
		processGet(uri, request.getProperties(), clientWriter, config);

	}
	
	public void sendFile(InputStream rawInput, OutputStream clientWriter, String filePath, 
			int code, SiteConfig config){
		
		BufferedInputStream input = new BufferedInputStream(rawInput);
		BufferedOutputStream output = new BufferedOutputStream(clientWriter);
		HTTPResponseHeader responseHeader = new HTTPResponseHeader();
		responseHeader.setKeepAlive(config.isKeepAlive());
		responseHeader.setCode(code);
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead;
		try {
			responseHeader.setMimeType(filePath);
			responseHeader.setContentLength(input.available());
			ServerLog.writeln(responseHeader.getFirstLine());
			
			output.write(responseHeader.getFullHeader().getBytes());
			while ((bytesRead = input.read(buffer)) != -1){
				//System.out.println(bytesRead);
			        output.write(buffer, 0, bytesRead);
			        output.flush();
			}
			ServerLog.writeln("Done");
			input.close();
		} catch (IOException e) {
			ServerLog.writeln("WriteError");
		}
	} 

}
