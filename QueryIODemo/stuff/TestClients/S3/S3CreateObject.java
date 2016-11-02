

package S3;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus; 
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
//PUT OBJECT

public class S3CreateObject{
	
	/*
	 * This program adds a object to the bucket
	 * @param serverURL: URL of S3 Compatible REST server(http://<S3 server IP> : <S3 server port> /queryio/). For example: http://192.168.0.1:6678/queryio/
	 * @param token: authorization token.
	 * @param bucketName: Name of the bucket in which object will be added
	 * @param objectName: Name of the object to be added
	 * @param filePath: Path of the local file that will be uploaded
	 */
	public static void putObject(String os3Url, String token, String bucket, String object, String filePath) throws Exception
    {
        HttpPut request = new HttpPut(os3Url + bucket + "/" + object);	//HttpPut instance with path of the object to be created

        request.addHeader("authorization", token);	//Provides token for authorization
        
        RandomAccessFile f = new RandomAccessFile(filePath, "r");	//Instance to read object provided on filepath
        byte[] b = new byte[(int)f.length()];	//get total length of object in bytes.
        f.read(b);	//read number of bytes from object
        request.setEntity(new ByteArrayEntity(b));	//assign PUT request with byte array of object to be uploaded
        DefaultHttpClient client = new DefaultHttpClient();	//Create object of DefaultHttpClient
        HttpResponse response = client.execute(request);	//Executes the client request
        
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)	//Check for OK response
        {
            System.err.println(response.getStatusLine().getReasonPhrase());
            throw new Exception("Put Object failed");	//Create object operation failed
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
			putObject("http://192.168.0.1:6678/queryio/",token, "Data", "temp.txt", "/AppPerfect/WebTest/readme/license.txt");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


