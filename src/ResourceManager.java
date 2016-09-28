/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

/**
 * @author shikai
 * Optional LRU cache of file resources
 */
public class ResourceManager {
	public ResourceManager(SiteConfig config){
		siteConfig = config;
	}
	//Object mutex = new Object();
	SiteConfig siteConfig;
	
	int cacheUsed = 0;
	
	/**
	 * @param URI
	 * @return full path of the site
	 */
	public String getFullPath(String URI)
	{
		try {
			URI = URLDecoder.decode(URI, java.nio.charset.StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("URI decode error!");
		}
		String path = siteConfig.getBaseDirectory() + URI;
		if(URI.equals("/"))
			path += siteConfig.getDefaultHome();
		return path;
	}
	
	public FileInputStream getFileStream(String URI) throws FileNotFoundException{
		String path = getFullPath(URI);

		File file = new File(path);
		FileInputStream stream = new FileInputStream(file);
		
		return stream;
	}
	
	LinkedHashMap<String, byte[]> cacheLRU = new LinkedHashMap<String, byte[]>();
	long bytesInCache = 0;
	
	byte[] getCacheValue(String URI){
		synchronized(cacheLRU){
			byte[] value = cacheLRU.get(URI);
			return value;
		}
	} 
	
	void removeCacheFront(){
		synchronized(cacheLRU){
			if(cacheLRU.keySet().iterator().hasNext()){
				String toRemove = cacheLRU.keySet().iterator().next();
				bytesInCache -= cacheLRU.get(toRemove).length;
				cacheLRU.remove(toRemove);
			}
		}
	}
	
	void putCache(String URI, byte[] arr){
		synchronized(cacheLRU){
			cacheLRU.put(URI, arr);
			bytesInCache += arr.length;
		}
	}
	
	void touchCache(String URI, byte[] value){
		synchronized(cacheLRU){
			cacheLRU.remove(URI);
			cacheLRU.put(URI, value);
		}
	}
	

	/**
	 * @param URI
	 * @return run LRU cache, return file stream
	 * @throws IOException
	 */
	public  InputStream accessFile(String URI) throws IOException{
		//synchronized(cacheLRU){
		byte[] value = getCacheValue(URI);
		if (value == null) {
			BufferedInputStream fileStream = new BufferedInputStream(getFileStream(URI));
			ByteBuffer buffer = ByteBuffer.allocate(fileStream.available());
			byte[] readBuffer = new byte[1024]; // Adjust if you want
			int bytesRead;
			int totalRead = 0;
			while ((bytesRead = fileStream.read(readBuffer)) != -1){
				buffer.put(readBuffer, 0, bytesRead);
				totalRead += bytesRead;
			}
	
			fileStream.close();
			byte[] arr = new byte[buffer.position()];
			synchronized(cacheLRU){
				while (bytesInCache + totalRead >= siteConfig.getCacheSize()) {
					removeCacheFront();
				}
				buffer.rewind();
				buffer.get(arr);
				putCache(URI, arr);
			}
			value = arr;
			ServerLog.writeln("Items in Cache: " + Integer.toString(cacheLRU.size()));
			ServerLog.writeln("Bytes in Cache: " + Long.toString(bytesInCache));
		} else {
			ServerLog.writeln("Cache hit!!");
			touchCache(URI, value);
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(value);
		return stream;
		//}
	}
	
	public InputStream getStream(String URI) {
		try{
			if(siteConfig.isUseCache())
				return accessFile(URI);
			else
				return getFileStream(URI);
		}
		catch(IOException e){
			System.out.println(e.getMessage());

			return null;
		}
		
	}
}
