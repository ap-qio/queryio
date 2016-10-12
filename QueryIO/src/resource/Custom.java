import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;


public class Custom {
		
	public static String HDFS_URL = "hdfs://192.168.0.9:9000";
	
	public static String REPLICATION_COUNT = "2";
	
	public static int OBJECT_SIZE = 10 * 1024; // 10 kb 
	
	private static byte[] staticBuffer;
	
	protected static final String LETTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
	
	protected static final Random SEEDED_RANDOM = new Random(19873482373L);
	
	static 
	{
		staticBuffer = randomString(LETTERS, OBJECT_SIZE);
	}
	 
	private static byte[] randomString(String sampler, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++){
        	bytes[i] = (byte) sampler.charAt(SEEDED_RANDOM.nextInt(sampler.length()));
        }            
        return bytes;
	}
		
	public static void main(String arg[]) {
		
		FileSystem dfs = null;
		
		try{				
			Configuration config = new Configuration();
			config.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, HDFS_URL);
			config.set(DFSConfigKeys.DFS_REPLICATION_KEY, REPLICATION_COUNT);
//			
////			System.setProperty( "sun.security.krb5.debug", "true");
////	        System.setProperty( "java.security.krb5.realm", "queryiorealm"); 
////	        System.setProperty( "java.security.krb5.kdc", "192.168.0.19");
////	        
////	        config.set(DFSConfigKeys.DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY, "true");
////	        config.set(DFSConfigKeys.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
////	        config.set(DFSConfigKeys.HADOOP_SECURITY_AUTHORIZATION, "true");
////	        config.set(DFSConfigKeys.DFS_NAMENODE_KEYTAB_FILE_KEY, "/AppPerfect/Keytabs/eshan12.keytab");
////	        config.set(DFSConfigKeys.HADOOP_SECURITY_SERVICE_USER_NAME_KEY, "hdfs/192.168.0.12");	        	    
////	        config.set("dfs.namenode.kerberos.principal", "hdfs/192.168.0.12@queryiorealm");	        
////	        config.set("dfs.namenode.user.name", "hdfs");
////	        config.set("dfs.http.address", "192.168.0.12:50070");
////	        
////	        UserGroupInformation.setConfiguration(config);
//			

			Thread.currentThread().setName("admin");
			
			dfs = FileSystem.get(config);
			Path path = new Path("/usr/dummy/file0.txt");
			
//			dfs.setPermission(path, new FsPermission((short) 0777));
//			
			dfs.create(path);
//			
//			System.out.println("Owner: " + dfs.getFileStatus(path).getOwner());
//			System.out.println("Group: " + dfs.getFileStatus(path).getGroup());
//			System.out.println("Owner: " + dfs.getFileStatus(path).getPermission().toString());
////			dfs.setPermission(iNode, new FsPermission((short) 0777));
			int i=1; 
			while(true){
				FSDataOutputStream dos = dfs.create(new Path("/usr/dummy/file" + i + ".txt"), true, 10240);
				i++;
				dos.write(staticBuffer);	
				dos.close();
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(dfs!=null)
					dfs.close();
			}
			catch(Exception exObject){
				exObject.printStackTrace();
			}
		}
 	}
}
