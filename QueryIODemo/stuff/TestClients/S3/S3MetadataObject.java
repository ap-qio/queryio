

package S3;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus; //from authentication
	


public class S3MetadataObject {
//HEAD OBJECT
	
	/*
 	 * This program retrieves a object from the bucket
	 * @param serverURL: URL of S3 Compatible REST server(http://<S3 server IP> : <S3 server port> /queryio/). For example: http://192.168.0.1:6678/queryio/
	 * @param token: authorization token.
	 * @param bucketName: Name of the bucket from which object will be retrieved
	 * @param objectName: Name of the object whose metadata will be retrieved
	 */
		public static void getObject(String serverURL, String token, String bucketName,
			String objectName) throws Exception {
		URL url = null;
		HttpURLConnection httpCon = null;
		InputStream is = null;
		try {
			/* append "/" at end of serverURL */
			if (!serverURL.endsWith("/")) {
				serverURL = serverURL + "/";
			}
			url = new URL(serverURL + bucketName + "/" + objectName);	//creates a URL with appending bucket name and objectName

			//Returns a URLConnection object that represents a connection to the remote object referred to by the URL.
			httpCon = (HttpURLConnection) url.openConnection();			
			
			httpCon.setDoOutput(true);	// to use the URL connection for output
			httpCon.setRequestMethod("HEAD");	//HEAD request is used

			httpCon.setRequestProperty("authorization", token);	//Provides token for authorization

			httpCon.connect();	//Opens a communications link to the resource reference by the URL

			if (httpCon.getResponseCode() == HttpStatus.SC_OK) {	//OK response for successful GET Object request
				 for (int i = 0;; i++) {
				      
					 String headerName = httpCon.getHeaderFieldKey(i);
				      String headerValue = httpCon.getHeaderField(i);
				      if (headerName == null && headerValue == null) {
					        break;
					      }
				      System.out.println(headerName + " : " + headerValue);
				    }
			}
		} finally {
			try {	
				if (is != null)
					is.close();	//close stream
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (httpCon != null) {
				httpCon.disconnect();	//close connection
			}
		}
	}
	
	
	public static String login(String serverURL, String username,
			String password) throws Exception {
		URL url = null;
		HttpURLConnection httpCon = null;
		String token = null;
		try {
			/* appending "/" at the end of serverURL */
			if (!serverURL.endsWith("/")) {
				serverURL = serverURL + "/";
			}
			url = new URL(serverURL); //create a new URL object

			//Returns a URLConnection object that represents a connection to the remote object referred to by the URL.
			httpCon = (HttpURLConnection) url.openConnection();			
			
			httpCon.setDoOutput(true);	// to use the URL connection for output
			httpCon.setRequestMethod("GET");	//GET request to be used

			httpCon.addRequestProperty("username", username);	//set username property of HttpURLConnection
			httpCon.addRequestProperty("password", password);	//set password property of HttpURLConnection

			httpCon.connect();	//Opens a communications link to the resource reference by the URL

			if (httpCon.getResponseCode() == HttpStatus.SC_OK) {
				token = httpCon.getHeaderField("authorization");	//get authorization token
				// Use this token in subsequent requests for the current
				// session.
			}
		} finally {	//close connection
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}

		return token;
	}
	
	public static void main(String argv[])
	{
		try {
			String token;
			token= login("http://192.168.0.1:6678/queryio/","admin","admin");
			System.out.print("Reply Token: "+token);
			getObject("http://192.168.0.1:6678/queryio/",token, "queryio/demo","file.txt");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


