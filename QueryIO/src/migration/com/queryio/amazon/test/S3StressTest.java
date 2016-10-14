package com.queryio.amazon.test;

import java.net.HttpURLConnection;
import java.util.Random;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.CallingFormat;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;
import com.amazon.s3.Utils;

public class S3StressTest {

	public static final String AWSACCESSKEYID = "AKIAIJN3NGFEZZ2SMBHQ";
	public static final String AWSSECRETACCESSKEY = "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";
	
	public static final String BUCKET_NAME = AWSACCESSKEYID.toLowerCase() + "-test-bucket3";
	
	public static final int UNSPECIFIEDMAXKEYS = -1;
	
	static int assertionCount = 0;
	
    static AWSAuthConnection conn;
    
    public static final int THREAD_COUNT = 1;
	public static final int OBJECT_SIZE = 1024 * 10; // 10 KB
	public static final int WAIT_INTERVAL_BETWEEN_EACH_OPERATION_MS = 1000;
	
	public static final boolean RUN_DELETE = false;
	public static final boolean RUN_GET = true;
	
	protected static final Random SEEDED_RANDOM = new Random(19873482373L);
	
	private static byte[] staticBuffer;
	
	protected static final String LETTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	
	static {
		staticBuffer = randomString(LETTERS, OBJECT_SIZE);	
	}
	 
	private static byte[] randomString(String sampler, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = (byte) sampler.charAt(SEEDED_RANDOM.nextInt(sampler.length()));
        return bytes;
    }
	
	
	private static void putBucket(String bucketName) throws Exception{
System.out.println("Nishant Jain");
 
		 
        Response response = conn.createBucket(bucketName, AWSAuthConnection.LOCATION_DEFAULT, null);
        assertEquals(
                "couldn't create bucket",
                HttpURLConnection.HTTP_OK,
                response.connection.getResponseCode());
			
	}
	
	private static void putObject(String bucketName, String key) throws Exception{
		 Response response = conn.put(bucketName, "prasoon/" + key, new S3Object(staticBuffer, null), null);
	        assertEquals(
	                "couldn't put simple object",
	                HttpURLConnection.HTTP_OK,
	                response.connection.getResponseCode());
	}
	
	private static void getObject(String bucketName, String key)throws Exception{
        conn.get(bucketName, key, null);  
	}
	
//	private static List getKeys(String bucketName) throws Exception{
//		 ListBucketResponse listBucketResponse = conn.listBucket(bucketName, null, null, null, null);
//        assertEquals(
//                "couldn't list bucket",
//                HttpURLConnection.HTTP_OK,
//                listBucketResponse.connection.getResponseCode());
//        List entries = listBucketResponse.entries;
//        
//        verifyBucketResponseParameters(listBucketResponse, bucketName, "", "", UnspecifiedMaxKeys, null, false, null);
//        return entries;	   
//	}
	
	private static void deleteObject(String bucketName, String key) throws Exception{
		 Response response = conn.delete(bucketName, key, null);
		 assertEquals(
               "couldn't delete entry",
               HttpURLConnection.HTTP_NO_CONTENT,
               response.connection.getResponseCode());
	}
	
	
	private static void init(CallingFormat format, boolean secure, String server) throws Exception{		 
    	conn = new AWSAuthConnection(AWSACCESSKEYID, AWSSECRETACCESSKEY, secure, server, format);        
	}
	
//	private static void verifyBucketResponseParameters( ListBucketResponse listBucketResponse,
//        String bucketName, String prefix, String marker,
//        int maxKeys, String delimiter, boolean isTruncated,
//        String nextMarker ) {
//		assertEquals("Bucket name should match.", bucketName, listBucketResponse.name);
//		assertEquals("Bucket prefix should match.", prefix, listBucketResponse.prefix);
//		assertEquals("Bucket marker should match.", marker, listBucketResponse.marker);
//		assertEquals("Bucket delimiter should match.", delimiter, listBucketResponse.delimiter);
//		if ( UnspecifiedMaxKeys != maxKeys ) {
//			assertEquals("Bucket max-keys should match.", maxKeys, listBucketResponse.maxKeys);
//		}
//		assertEquals("Bucket should not be truncated.", isTruncated, listBucketResponse.isTruncated);
//		assertEquals("Bucket nextMarker should match.", nextMarker, listBucketResponse.nextMarker);
//	}	


    private static void assertEquals(String message, int expected, int actual) {
        assertionCount++;
        if (expected != actual) {
            throw new RuntimeException(message + ": expected " + expected + " but got " + actual);
        }
    }

//    private static void assertEquals(String message, Object expected, Object actual) {
//        assertionCount++;
//        if (expected != actual && (actual == null || ! actual.equals(expected))) {
//            throw new RuntimeException(message + ": expected " + expected + " but got " + actual);
//        }
//    }
    public static String[] bucketNames={"appperfect"};
    public static void main(String[] args) {
		final Thread[] testThreads = new Thread[THREAD_COUNT];
		try {
			init(CallingFormat.getSubdomainCallingFormat(), true, Utils.DEFAULT_HOST);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		try {
			for (int i = 0; i < testThreads.length; i++) {
				final int currentBucket = i;
				testThreads[i] = new Thread("Thread" + i) {
					@Override
					public void run() {
						String bucketName = bucketNames[currentBucket]; 
						try {
							System.out.println("inserting bucket " + bucketName);
							S3StressTest.putBucket(bucketName);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						while(true) {
							String objName = "File" + this.getId() + "-" + System.nanoTime();
							
							try {								
								System.out.println("inserting object " + objName + " in bucket " + bucketName);
								putObject(bucketName, objName);
								System.out.println("inserted object " + objName + " in bucket " + bucketName);
								Thread.sleep(WAIT_INTERVAL_BETWEEN_EACH_OPERATION_MS);
							}
							catch(Exception e) {
								e.printStackTrace();
							}
							
							if(RUN_GET){								
								try {
									System.out.println("Get obj " + objName);
									getObject(bucketName, objName);
									System.out.println("Get obj done" + objName);
									Thread.sleep(WAIT_INTERVAL_BETWEEN_EACH_OPERATION_MS);
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}

							if(RUN_DELETE){
								
								try {
									deleteObject(bucketName, objName);
									Thread.sleep(WAIT_INTERVAL_BETWEEN_EACH_OPERATION_MS);
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				};
				testThreads[i].start();
			}
			for (int i = 0; i < testThreads.length; i++) {
				testThreads[i].join();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
