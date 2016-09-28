/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;



public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		// start server
		TheServer s = new TheServer();
		s.start();
//		SiteConfigByFile con = new SiteConfigByFile("./test_site/config.properties");
//		
//
//        ExecutorService Texecutor 
//        	= Executors.newFixedThreadPool(
//        			ServerConfig.getInstance().getThreadPoolCapacity());
//
//		SiteHost host = new SiteHost(new SiteConfig(), Texecutor);
//		host.startHost();

	}

}
