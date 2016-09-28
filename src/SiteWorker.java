/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author shikai
 * Each SiteHost may run multiple SiteWorkers to handle requests
 */
public class SiteWorker implements Runnable {
	
	public SiteWorker(SiteConfig config, Socket s, ResponseMaker maker) throws IOException{
		socket = s;
		responseMaker = maker;
        input = new BufferedInputStream(socket.getInputStream());
        siteConfig = config;
        
        socket.setKeepAlive(siteConfig.isKeepAlive());

	}
	
	SiteConfig siteConfig;
	Socket socket;
	BufferedInputStream input;
	ResponseMaker responseMaker;


	@Override
	public void run() {
		boolean isStarted = false;
		boolean isClientKeepAlive = false;
		long time = 0;
		//boolean isIncomplete = false;
		try{
			while(true){
	    		if(socket.isClosed() || socket.isInputShutdown())
	    			break;
	    		// handle long connection waiting
	    		if(input.available() == 0 && siteConfig.isKeepAlive() && isClientKeepAlive){
	    			if(isStarted == false){
	    				isStarted = true;
	    				time = System.currentTimeMillis();
	    			}

	    			if(isStarted == true && System.currentTimeMillis() - time 
	    					> siteConfig.getAliveTimeout()){
	    				//System.out.println(System.currentTimeMillis() - time);
	    				break;
	    			}
    				continue;
	    		}
	    		else
	    		{
	    			isStarted = false;
	    		}
	    		
	    		// parse http header
        		HTTPHeader httpHeader = HTTPHeader.readHeader(input);
        		// incomplete header
        		if(httpHeader == null || httpHeader.getFirstLine() == null
        				|| httpHeader.getFirstLine().split(" ").length < 2){
        			break;
        		}
        		String keepAliveHeader = httpHeader.getEntryByName(HTTPTerms.HEADER_CONNECTION);
        		if(keepAliveHeader != null 
        				&& keepAliveHeader.toUpperCase().equals(
        						HTTPTerms.CONN_KEEPALIVE.toUpperCase())){
        			ServerLog.writeln("Client:keep-alive");
        			isClientKeepAlive = true;
        		}
        		else
        			isClientKeepAlive = false;
        		ServerLog.writeln(httpHeader.firstLine);
       
            	OutputStream out =
            			socket.getOutputStream();
            	String contentLen = httpHeader.getEntryByName(HTTPTerms.HEADER_CONLEN);
            	InputStream payloadStream = null;
            	int payloadLen = 0;
            	
            	// handle payload
            	if(contentLen != null)
            	{
	            	payloadLen = Integer.parseInt(contentLen);
	            	if(input.available() > 0 || payloadLen > 0)
	            	{
	            		ServerLog.writeln("Remaining: " + input.available());
	            		payloadStream = input;
	            	}
            	}
            	if(payloadStream == null){
            		payloadLen = 0;
            	}
            	
            	// generate response
            	responseMaker.make(httpHeader, payloadStream, payloadLen, out, siteConfig);
            	//out.write(generateResponse().getBytes());
            	
            	// if not keep alive, close socket
    			if(!siteConfig.isKeepAlive())
    				break;

			}
		} catch (IOException e) {

		}
		finally{
			try {
				ServerLog.writeln("Closing");

				socket.close();
				
			} catch (IOException e) {
			}
		}

	}

}
