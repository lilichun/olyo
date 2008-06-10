package net.java.sip.communicator.service.dhtaccess;

/**
 * This is a service which enable SIP-Communicator access the 
 * PUT/GET/REMOVE services of DHT overlay networks
 * 
 * @author Wang Yao 
 */
public interface DHTAccessService {
	
	/**
	 * This method insert a key-value pair into DHT network.
	 * 
	 * @param key the 'key' to be inserted. 
	 * NOTICE: there is no restriction on the length of 'key'. 
	 * The impelementation itself must handle the mapping from 
	 * a given key which is of any type and in any length to a specified length.
	 * 
	 * @param value the 'value' correspond to the 'key'.
	 * NOTICE: the restriction on the length of 'value' is specified by implementation. 
	 * If the invoker give a value which overcome the restriction, the impelementation 
	 * must throw a suiable Exception to inform invoker of the overrun.
	 * 
	 * @param ttl Time-To-Live value of the key-value pair, measured in Second.
	 * NOTICE: if this parameter is not a positive integer, a Exception will be thrown.
	 * 
	 * @param secret optional parameter. If the secret is specified, one can 
	 * invoke the remove() method to remove the key-value pair; otherwise, the
	 * key-value pair cannot be removed until its ttl expires. If you do not want to 
	 * use secret, please specify it to be null.  
	 * 
	 * @throws Exception indicate at least one of the following conditions is met:
	 * <p>1) at least one of the input parameters is illegal;</p>
	 * <p>2) the put operation fail due to some reasons;</p>
	 * Implementation should extend the Exception class to give the invoker information
	 * on exceptions as detailed as possible.    
	 */
	void put(Object key, Object value, int ttl, String secret) throws Exception;
	
	/**
	 * This method returns an array of values conrespondent to the given key.
	 * 
	 * @param key the 'key' to be retrieved. The format of this parameter is in 
	 * accordance with that is defined in DHTAccessService.put()
	 * 
	 * @return an array of all the values correspond to the key.
	 * 
	 * @throws Exception indicate the GET operation fails due to some reasons.
	 * Implementation should extend the Exception class to give the invoker information
	 * on exceptions as detailed as possible.
	 */
	Object[] get(Object key) throws Exception;
	
	/**
	 * Remove the kay-value pair in the DHT. In case that there are multiple values
	 * corresponding to one key, each REMOVE operation will ONLY remove the value 
	 * specified in parameter, other values will still exist in DHT. 
	 * 
	 * @param key the 'key' to be removed. The format of this parameter is in 
	 * accordance with that is defined in DHTAccessService.put()
	 * 
	 * @param value the 'value' to be removed.
	 * 
	 * @param secret the secret which is used when PUT the key-value pair. 
	 * Null is not accepatable.
	 * 
	 * @throws Exception indicates that the REMOVE operation fails due to some reasons.
	 * Implementation should extend the Exception class to give the invoker information
	 * on exceptions as detailed as possible.
	 */
	void remove(Object key, Object value, String secret) throws Exception;
}
