/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HTTPHeader {
	
	/*
	 *   
	 * 
	 */
	
	String firstLine = null;
	Map<String, String> entries = new HashMap<String, String>();
	
	static HTTPHeader parseByteArray(byte[] content){
		HTTPHeader ret = new HTTPHeader();
		int pos = 0;
		while(pos < content.length && content[pos] != '\r')
			pos++;
		ret.firstLine = new String(Arrays.copyOfRange(content, 0, pos));
		pos += 2;
		while(pos < content.length){
			int lineBegin = pos;
			while(pos < content.length && content[pos] != '\r' && content[pos] != ':')
				pos++;
			if(pos < content.length){
				if(content[pos] == ':')
				{
					String name = null, value = null;
					name = new String(Arrays.copyOfRange(content, lineBegin, pos));
					while(pos < content.length && content[pos] == ' ')
						pos++;
					int valueBegin = pos;
					while(pos < content.length && content[pos] != '\r')
						pos++;
					value = new String(Arrays.copyOfRange(content, valueBegin, pos));
					pos += 2;
					ret.entries.put(name, value);
				}
				else if(content[pos] == '\r'){
					pos += 2;
					break;
				}
			}
		}
		return ret;
	}
	
	static HTTPHeader readHeader(InputStream input) throws IOException{
		String line = "";
		boolean isFirst = true;
		HTTPHeader ret = new HTTPHeader();
		while(true){
			line = readLine(input);

			if(line.length() == 0)
				break;
			if(isFirst)
				ret.firstLine = line;
			else
			{
				String[] pair = line.split(":");
				if(pair.length >= 2)
					ret.entries.put(pair[0].trim(), pair[1].trim());
			}
			isFirst = false;
		}
		return ret;
	}


	static String readLine(InputStream input) throws IOException{
		String line = "";
		byte curByte = 0;
		while((curByte = (byte) input.read()) != -1){
			if(curByte == '\r'){
				input.read();
				break;
			}
			line += (char)curByte;
		}
		return line;
	}
	
	public String getEntryByName(String name){
		return entries.get(name);
	}
	
	public void setEntry(String name, String content){
		entries.put(name, content);
	}
	
	public void setFirstLine(String content){
		firstLine = content;
	}
	
	public String getFirstLine(){
		return firstLine;
	}
	
	public String getFullHeader(){
		if(firstLine == null || firstLine.length() == 0)
			return null;
		String fullText = firstLine + "\r\n";
		for (Map.Entry<String, String> entry : entries.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    fullText += key;
		    fullText += ": ";
		    fullText += value;
		    fullText += "\r\n";
		}
		fullText += "\r\n";
		return fullText;
		
	}
	
	public Map<String, String> getProperties(){
		return this.entries;
	}
}
