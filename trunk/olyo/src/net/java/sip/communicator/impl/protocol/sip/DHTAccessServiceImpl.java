package net.java.sip.communicator.impl.protocol.sip;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;

import net.java.sip.communicator.util.Logger;

/**
 * The implementation of DHT Access Service
 * @author Wang Yao
 *
 */
public class DHTAccessServiceImpl implements DHTAccessService{

	private static final Logger logger = Logger.getLogger(DHTAccessServiceImpl.class);
	
	private static XmlRpcClient xmlRpcClient = null;
	
	private XmlRpcClientConfigImpl cfg;
	
	//default address of RPC server
	private static final String DEF_RPC_SERVER_URL_STR = 
		new String("http://127.0.0.1:3998/");
	
	private static MessageDigest md = null;

	private static final String ENCODE = new String("US-ASCII");
	
	private static final String HASH_ALG_NAME = new String("SHA-1");
	
	private static final String DHT_APP_NAME = new String("SIP-Communicator");
	
	private static final String PUT_METHOD_NAME = new String("put_removable");
	
	private static final String GET_METHOD_NAME = new String("get");
	
	private static final String REMOVE_METHOD_NAME = new String("rm");
	
	/**
	 * The following codes, including SUCCESS_CODE, OVER_CAPACITY_CODE and RETRY_CODE,
	 * are the result code of XML RPC
	 * defined in http://opendht.org/users-guide.html
	 */ 
	private static final int SUCCESS_CODE = 0;
	private static final int OVER_CAPACITY_CODE = 1;
	private static final int RETRY_CODE = 2;
	
	private static final int DEF_TTL = 3600; //used by remove()

	/**
	 * Create a new instance for DHTAccessServiceImpl with a String which 
	 * represents the XML-RPC server's URL.
	 * @param rpcServerURIStr a String which looks like "http://127.0.0.1:3998/"
	 * @throws MalformedURLException if the parameter is in illegal format
	 */
	public DHTAccessServiceImpl(String rpcServerURLStr) 
		throws MalformedURLException{
		if (null == DHTAccessServiceImpl.xmlRpcClient){
			DHTAccessServiceImpl.xmlRpcClient = new XmlRpcClient();
		}
		
		URL rpcServerURL = null;
		
		try{
			if (null == rpcServerURLStr){
				rpcServerURL = new URL(DEF_RPC_SERVER_URL_STR);
			}else{
				rpcServerURL = new URL(rpcServerURLStr);
			}
		}catch(MalformedURLException e){
			throw new MalformedURLException("illegal URL String: " + rpcServerURLStr);
		}
		
		this.cfg = new XmlRpcClientConfigImpl();
		this.cfg.setServerURL(rpcServerURL);
		xmlRpcClient.setConfig(this.cfg);
		
		try{
			if (null == md){
				md = MessageDigest.getInstance(HASH_ALG_NAME);
			}
		}catch(NoSuchAlgorithmException e){
			//this should never happen, just log it
			logger.error("NoSuchAlgorithmException occur while initialize " +
					"digest in the constructor of DHTAccessServiceImpl.\n" +
					"The eror msg is:\n" + e.getMessage());
		}
	}
	
	/**
	 * Create a new instance for DHTAccessServiceImpl with default server address,
	 * which is "http://127.0.0.1:3998"
	 * @throws MalformedURLException Actually, this exception should never happen.
	 */
	public DHTAccessServiceImpl() throws MalformedURLException{
		this(null);
	}

	/**
	 * !!NOTE: the return value is a set of byte[].
	 */
	public Set<Object> get(Object key) throws Exception {
		if (null == key){
			throw new IllegalArgumentException("The parameter 'key' is null.");
		}
		
		List paramList = new ArrayList();
		Set resultSet = new HashSet();
		
		//return at most 1 value in each operation
		int maxNumOfReturnValue = 1;
		
		//1st parameter, "key" which is base64
		byte [] keyHashResult = hashUsingSHA1(key);
		paramList.add(keyHashResult);
		
		//2nd parameter, the maximum number of values to return (<int>)
		paramList.add(maxNumOfReturnValue);
		
		//3rd parameter, placemark from a previous get or an empty byte
		//string if this is the first get on this key (<base64>)
		byte[] placemark = null;
		placemark = new String("").getBytes(ENCODE);
		paramList.add(placemark);
		
		//4th parameter, and the application name (<string>).
		paramList.add(DHT_APP_NAME);
		
		while(true){
			XmlRpcRequest request = new XmlRpcClientRequestImpl(this.cfg, GET_METHOD_NAME,paramList);
			Object [] result = null;
			
			try{
				result = (Object[])xmlRpcClient.execute(request);
				logger.info("Making GET request to: " + this.cfg.getServerURL()+"\n"
						+ "the key is :" + key + "\n");
				
				Object retValue[] = (Object[])result[0];
				if (1 == retValue.length){
					resultSet.add((byte[])retValue[0]);
				}
				
				placemark = (byte[])result[1];
				if (0 >= placemark.length){
					break;
				}else{
					paramList.remove(2);//placemark is the 3rd param
					paramList.add(2, placemark);
				}
			}catch(XmlRpcException e){
				logger.error("XML-RPC failed when execute xmlRpcClient.execute(request) in get().\n" +
					"Throw XmlRpcException to the invoker of this method.");
				throw new XmlRpcException("XML-RPC failed when execute xmlRpcClient.execute(request) in get()", e);
			}
		}
		
		return resultSet;
	}

	/**
	 * Construct a XML-RPC request for PUT message and send it.
	 * The detailed specification of the PUT request is described at
	 * http://opendht.org/users-guide.html
	 */
	public void put(Object key, Object value, int ttl, String secret) throws Exception {
		if (null == key){
			throw new IllegalArgumentException("The parameter 'key' is null.");
		}else if (null == value){
			throw new IllegalArgumentException("The parameter 'value' is null.");
		}		
		
		/**
		 * First of all, construct the parameter List.
		 */
		List paramList = new LinkedList();
				
		//1st parameter, "key" which is base64
		byte [] keyHashResult = hashUsingSHA1(key);
		paramList.add(keyHashResult);
		
		//2nd parameter, "value" which is base64
		paramList.add(value.toString().getBytes());
		
		/**
		 * 3rd and 4th parameter: hash_type and secret_hash. 
		 * hash_type should either be "SHA", in which case secret_hash (<base64>) 
		 * should be the SHA-1 hash of the secret used for removes, 
		 * or hash_type should be the empty string (""), 
		 * in which case secret_hash should be an empty byte array.
		 */
		if (null == secret){
			paramList.add(new String(""));
			paramList.add(new byte[0]);
		}else{
			paramList.add(new String("SHA"));
			
			byte [] secretHashResult = hashUsingSHA1(secret);
			paramList.add(secretHashResult);
		}
		
		//5th parameter, "ttl" which is int
		paramList.add(ttl);
		
		//6th parameter, "Application Name" which is string
		paramList.add(DHT_APP_NAME);
		
		/**
		 * Then send the request and receive response.
		 */
		XmlRpcRequest request = new XmlRpcClientRequestImpl(this.cfg, PUT_METHOD_NAME,paramList);
		Object result = null;
		Integer resultCode = null;
		try{			
			do{
				//execute the request in the blocking mode
				result = xmlRpcClient.execute(request);
				logger.info("Making PUT request to: " + this.cfg.getServerURL()+"\n"
						+"The key is: " + key.toString()
						+"\nthe value is: " + value.toString()+"\n");
				
				if (result instanceof Integer){
					resultCode = Integer.class.cast(result);
				}else{
					//This should never happen
					logger.error("The return value is in the type of: "
							+result.getClass()
							+"\nillegal type of return value.");
					throw new Exception("The return value is in the type of: "
							+result.getClass()
							+"\nillegal type of return value.");
				}
				
				logger.info("The return code is: "+ resultCode +"\n");
			}while(RETRY_CODE == resultCode);
		}catch(XmlRpcException e){
			logger.error("XML-RPC failed when execute xmlRpcClient.execute(request) in put().\n" +
					"Throw XmlRpcException to the invoker of this method.");
			throw new XmlRpcException("XML-RPC failed when execute xmlRpcClient.execute(request) in put()", e);
		}
		
		if (OVER_CAPACITY_CODE == resultCode){
			throw new DHTOverCapacityException();
		}		
	}

	public void remove(Object key, Object value, String secret) throws Exception {
		if (null == key){
			throw new IllegalArgumentException("The parameter 'key' is null.");
		}else if (null == value){
			throw new IllegalArgumentException("The parameter 'value' is null.");
		}else if (null == secret){
			throw new IllegalArgumentException("The parameter 'secret' is null.");
		}
		
		List paramList = new LinkedList();
		
		//1st parameter, "Hashed key" which is base64
		paramList.add(hashUsingSHA1(key));
		
		//2nd parameter, "Hashed value" which is base64
		paramList.add(hashUsingSHA1(value));
		
		//3rd parameter, "hash_type" 
		paramList.add(new String("SHA"));
		
		//4th parameter, "secret" WITHOUT HASH
		paramList.add(secret.getBytes());
		
		//5th parameter, "ttl" which is int
		paramList.add(DEF_TTL);
		
		//6th parameter, "Application Name" which is string
		paramList.add(DHT_APP_NAME);
		
		XmlRpcRequest request = new XmlRpcClientRequestImpl(this.cfg, REMOVE_METHOD_NAME,paramList);
		Object result = null;
		Integer resultCode = null;
		try{			
			do{
				//execute the request in the blocking mode
				result = xmlRpcClient.execute(request);
				logger.info("Making REMOVE request to: " + this.cfg.getServerURL()+"\n"
						+"The key is: " + key.toString()
						+"\nthe value is: " + value.toString()+"\n");
				
				resultCode = Integer.class.cast(result);
				
				logger.info("The return code is: "+ resultCode +"\n");
			}while(RETRY_CODE == resultCode);
		}catch(XmlRpcException e){
			logger.error("XML-RPC failed when execute xmlRpcClient.execute(request) in remove().\n" +
					"Throw XmlRpcException to the invoker of this method.");
			throw new XmlRpcException("XML-RPC failed when execute xmlRpcClient.execute(request) in remove()", e);
		}
		
		if (OVER_CAPACITY_CODE == resultCode){
			throw new DHTOverCapacityException();
		}		
	}
	
	private byte[] hashUsingSHA1(Object o) throws HashOperationFailedException {
		try{
			md.update(o.toString().getBytes());
			MessageDigest tmpMD = (MessageDigest)md.clone();
			md.reset();
			return tmpMD.digest();
		}catch(CloneNotSupportedException e){
			throw new HashOperationFailedException(
					"CloneNotSupportedException when calculating Hash value", e);
		}		
	}
	
	/**
	 * Used to test this class
	 * @param args
	 */
	public static void main(String args[]){
		DHTAccessService dht;
		try {
			dht = new DHTAccessServiceImpl("http://127.0.0.1:3998/");
			dht.put("Key", "TestValue", 60*5, "123");
			dht.put("Key", "TestValue1", 60*5, null);
			dht.put("Key", "TestValue2", 60*5, null);
			dht.put("Key", "TestValue3", 60*5, null);
			Set resultSet = dht.get(new String("Key"));
			for(Iterator it = resultSet.iterator(); it.hasNext();){
				System.out.println(new String((byte[])it.next()));
			}
			dht.remove("Key", "TestValue", "123");
			resultSet = dht.get(new String("Key"));
			for(Iterator it = resultSet.iterator(); it.hasNext();){
				System.out.println(new String((byte[])it.next()));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
	}

}
