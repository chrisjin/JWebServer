/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author shikai
 * when config file exists, it will be used, otherwise use default config.
 * 
 * config file is
 * key1 = value1
 * key2 = value2
 * key3 = value3
 * ....
 * 
 */
public class SiteConfigByFile extends SiteConfig {
	
	Properties prop = new Properties();
	SiteConfigByFile(String fileName) throws IOException{
		FileInputStream input = new FileInputStream(fileName);
		prop.load(input);
		input.close();
	}
	
	@Override
	synchronized public long getAliveTimeout(){
		String ret = prop.getProperty("AliveTimeout");
		if(ret == null)
			return super.getAliveTimeout();
		else
			return Long.parseLong(ret);
	}
	@Override	
	synchronized public int getMaxLongConnections(){
		String ret = prop.getProperty("MaxLongConnections");
		if(ret == null)
			return super.getMaxLongConnections();
		else
			return Integer.parseInt(ret);
	}
	@Override	
	synchronized public int getPort(){
		String ret = prop.getProperty("Port");
		if(ret == null)
			return super.getPort();
		else
			return Integer.parseInt(ret);
	}
	@Override
	synchronized public boolean isKeepAlive(){
		String ret = prop.getProperty("KeepAlive");
		if(ret == null)
			return super.isKeepAlive();
		else
			return Boolean.parseBoolean(ret);
	}
	@Override
	synchronized public boolean isUseCache(){
		String ret = prop.getProperty("UseCache");
		if(ret == null)
			return super.isUseCache();
		else
			return Boolean.parseBoolean(ret);
	} 
	@Override
	synchronized public String getBaseDirectory(){
		String ret = prop.getProperty("BaseDirectory");
		if(ret == null)
			return super.getBaseDirectory();
		else
			return ret;
	}
	@Override
	synchronized public int getCacheSize(){
		String ret = prop.getProperty("CacheSize");
		if(ret == null)
			return 1024000;
		else
			return Integer.parseInt(ret);
	}
	@Override
	synchronized public String getDefaultHome(){
		String ret = prop.getProperty("DefaultHome");
		if(ret == null)
			return "index.html";
		else
			return ret;
		
	}
	@Override
	public String getResponseMaker(){
		String ret = prop.getProperty("ResponseMaker");
		if(ret == null)
			return super.getResponseMaker();
		else
			return ret;
		
	}
	@Override
	synchronized public String getNotFoundPage(){
		String ret = prop.getProperty("NotFoundPage");
		if(ret == null)
			return super.getNotFoundPage();
		else
			return ret;
		
	}
}
