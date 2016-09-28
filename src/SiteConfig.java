/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author shikai
 * default site config
 */
public class SiteConfig {
	
	ResourceManager resourceManager;

	
	SiteConfig(){
		resourceManager = new ResourceManager(this);
	}
	
	Properties prop = new Properties();
	SiteConfig(String fileName) throws IOException{
		resourceManager = new ResourceManager(this);

//		FileInputStream input = new FileInputStream(fileName);
//		prop.load(input);
//		input.close();
	}
	
	final public ResourceManager getResourceManager(){
		return resourceManager;
	}
	
	
	public long getAliveTimeout(){
		return 1000;
	}
	
	public int getMaxLongConnections(){
		return 3;
	}
	
	public int getPort(){
		return 10001;
	}
	
	public boolean isKeepAlive(){
		return false;
	}
	
	public boolean isUseCache(){
		return false;
	} 
	
	public String getBaseDirectory(){
		return "./test_site";
	}
	
	public int getCacheSize(){
		return 1024000;
	}
	
	public String getDefaultHome(){
		return "index.html";
	}
	public String getServerName(){
		return "Jin";
	}
	public String getResponseMaker(){
		return "StaticResponseMaker";
	}
	
	public String getNotFoundPage(){
		return "notfound.html";
	}
}
