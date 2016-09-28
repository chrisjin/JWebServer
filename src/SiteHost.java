/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author shikai
 * for one web site in one base folder specified in the config file
 * It runs SiteWorkers in a shared thread pool
 */
public class SiteHost {
	public SiteHost(SiteConfig siteConf, ExecutorService executor) throws IOException{
		siteConfig = siteConf;
		threadExecutor = executor;
		responseMaker = getResponseMaker(siteConfig.getResponseMaker());
		listener = new ServerSocket(siteConfig.getPort());
	}
	
	public void startHost() throws IOException{
        try {
	        while (true) {
	
	    		Socket socket = listener.accept();
	    		System.out.println(" -- Worker Starting -- ");
	    		Runnable worker = new SiteWorker(siteConfig, socket, responseMaker);
	    		synchronized(threadExecutor){
	    			threadExecutor.execute(worker);
	    		}
	        }
        }
        finally {
            listener.close();
        }
	}
	
	/**
	 * @param className
	 * @return get response maker from config file, 
	 * if file not found, get the default one, i.e. the static response.
	 */
	ResponseMaker getResponseMaker(String className){
		ResponseMaker maker = null;
		try{
			Class myClass = Class.forName(className);
	
			Class[] types = {};
			Constructor constructor = myClass.getConstructor(types);
	
			Object[] parameters = {};
			Object instanceOfMyClass = constructor.newInstance(parameters);
			maker = (ResponseMaker)instanceOfMyClass;
		}
		catch(ClassNotFoundException e){
			maker = new StaticResponseMaker(); 
		}
		catch(Exception e){
			maker = new StaticResponseMaker(); 
		}
		return maker;
		
	}
	
	SiteConfig siteConfig;
	ExecutorService threadExecutor;
	ResponseMaker responseMaker;
    ServerSocket listener;

}
