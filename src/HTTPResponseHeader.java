/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponseHeader extends HTTPHeader {
	Map<Integer, String> codeToMsg = new HashMap<Integer, String>();
	Map<String, String> extToType = new HashMap<String, String>();
	void fillCodeEntries()
	{
		codeToMsg.put(200, "OK");
		codeToMsg.put(404, "Not Found");
	}
	
	
	void fillExtToType(){
		extToType.put("html", HTTPTerms.MIME_HTML);
		extToType.put("htm", HTTPTerms.MIME_HTML);
		extToType.put("css", HTTPTerms.MIME_CSS);
		extToType.put("jpeg", HTTPTerms.MIME_JPEG);
		extToType.put("jpg", HTTPTerms.MIME_JPEG);
		extToType.put("gif", HTTPTerms.MIME_JPEG);
		extToType.put("txt", HTTPTerms.MIME_PLAIN);
		extToType.put("js", HTTPTerms.MIME_JS);
	}
	
	
	public HTTPResponseHeader(){
		fillCodeEntries();
		fillExtToType();
		this.setEntry(HTTPTerms.HEADER_DATE, new Date().toString());
		this.setEntry(HTTPTerms.HEADER_SERVER, ServerConfig.getInstance().getServerName());
		this.setEntry(HTTPTerms.HEADER_CONNECTION, "keep-alive");
		setCode(200);
		setContentType(HTTPTerms.MIME_HTML);
	}
	
	public String getCodeText(int code){
		return codeToMsg.get(code);
	}
	
	public void setKeepAlive(boolean is){
		if(is)
			this.setEntry(HTTPTerms.HEADER_CONNECTION, HTTPTerms.CONN_KEEPALIVE);
		else
			this.setEntry(HTTPTerms.HEADER_CONNECTION, HTTPTerms.CONN_CLOSED);

	}
	
	public void setCode(int code){
		String firstLine = HTTPTerms.HTTP_VERSION + " " + Integer.toString(code) + 
				" " + getCodeText(code);
		this.setFirstLine(firstLine);
	}
	
	public String getFileExtension(String fileName){
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	// /xxx.html -> text/html
	public void setMimeType(String fileName){
		String ext = getFileExtension(fileName);
		if(extToType.containsKey(ext))
		{

			setContentType(extToType.get(ext));
		}
		else
			setContentType(HTTPTerms.MIME_PLAIN);

	}
	
	public void setContentType(String type){
		this.setEntry(HTTPTerms.HEADER_CONTYPE, type);
	}
	
	public void setContentLength(int len){
		this.setEntry(HTTPTerms.HEADER_CONLEN, Integer.toString(len));
	}
}
