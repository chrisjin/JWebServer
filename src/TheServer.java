/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 *                thread             threadpool
 *     TheServer --------> SiteHost -------------> SiteWorker
 * 
 */

/**
 * @author shikai
 *
 */
public class TheServer {
	
	/**
	 *  thread pool
	 */
	ExecutorService threadPool = Executors
			.newFixedThreadPool(ServerConfig.getInstance().getThreadPoolCapacity());
	
	
	@SuppressWarnings("unused")
	public static void runHost(ExecutorService threadPool, String hostConfig, int id){
		SiteConfig con = null;

		try {
			con = new SiteConfigByFile(hostConfig);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ServerLog.writeln(" " + hostConfig + " Config File not open, use default config!");
			return;
		}

		//ExecutorService Texecutor = Executors
		//		.newFixedThreadPool(ServerConfig.getInstance().getThreadPoolCapacity());

		SiteHost host;
		try {
			// System.out.println(con.getResponseMaker());
			if (con == null)
				con = new SiteConfig();
			host = new SiteHost(con, threadPool);
			String hostTitle = "Host " + Integer.toString(id) + ": ";
			ServerLog.writeln(hostTitle + hostConfig);
			ServerLog.writeln(hostTitle + "port -- " + Integer.toString(con.getPort()));
			ServerLog.writeln(hostTitle + "dir -- " + con.getBaseDirectory());

			host.startHost();
		} catch (IOException e) {
			ServerLog.writeln(" " + hostConfig + " Fail!");
			ServerLog.writeln("Please kill other running server instance!");
		}
	}
	
	class HostRunner implements Runnable
	{
		public HostRunner(ExecutorService pool, String config, int i){
			hostConfig = config;
			id = i;
			threadPool = pool;
		}
		String hostConfig = null;
		int id = 0;
		ExecutorService threadPool = null;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			runHost(threadPool, hostConfig, id);
		}
		
	}
	
	@SuppressWarnings("unused")
	public void start() {
		ArrayList<String> hosts = ServerConfig.getInstance().getHostConfigs();
		
		// A server runs multiple hosts in different threads, 
		// the last host running in main thread
		// Each host contains one whole web site with its own configurations
		// Hosts share same thread pool.
		
		int id = 1;
		for(int i = 0; i < hosts.size(); i++)
		{
			String hostConfig = hosts.get(i);
			if(i == hosts.size() - 1)
				runHost(threadPool, hostConfig, i);
			else
				(new Thread(new HostRunner(threadPool, hostConfig, i))).start();
				
		}

	}
}
