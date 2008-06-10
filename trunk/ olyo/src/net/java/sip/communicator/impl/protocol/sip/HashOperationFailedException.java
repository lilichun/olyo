package net.java.sip.communicator.impl.protocol.sip;

public class HashOperationFailedException extends Exception {
	public HashOperationFailedException(String msg, Throwable t){
		super(msg, t);
	}
	
	public HashOperationFailedException(String msg){
		super(msg);
	}
}
