/*
 *     Shikai Jin, 9/27/2016
 *     Adobe Homework
 */

import java.io.InputStream;
import java.io.OutputStream;

public interface ResponseMaker {
	void make(HTTPHeader request, InputStream payload, int payloadLen, 
			OutputStream clientWriter, 
			SiteConfig config);
}
