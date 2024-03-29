/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.sipaccregwizz;

/**
 * The <tt>SIPAccountRegistration</tt> is used to store all user input data
 * through the <tt>SIPAccountRegistrationWizard</tt>.
 *
 * @author Yana Stamcheva
 */
public class SIPAccountRegistration {

    private String uin;

    private String password;

    private boolean rememberPassword;

    private String serverAddress;

    private String serverPort;

    private String proxyPort;

    private String proxy;

    private String preferredTransport;

    private boolean enablePresence;

    private boolean forceP2PMode;

/**
 *@author Dong Fengyu
 *
 */

   private boolean enableP2PSIP;

   private boolean enablePassCheck;





    private String pollingPeriod;

    private String subscriptionExpiration;

    private String keepAliveMethod;

    private String keepAliveInterval;

    public String getPreferredTransport()
    {
        return preferredTransport;
    }

    public void setPreferredTransport(String preferredTransport)
    {
        this.preferredTransport = preferredTransport;
    }

    public String getProxy()
    {
        return proxy;
    }

    public void setProxy(String proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Returns the password of the sip registration account.
     * @return the password of the sip registration account.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the sip registration account.
     * @param password the password of the sip registration account.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns TRUE if password has to remembered, FALSE otherwise.
     * @return TRUE if password has to remembered, FALSE otherwise
     */
    public boolean isRememberPassword() {
        return rememberPassword;
    }

    /**
     * Sets the rememberPassword value of this sip account registration.
     * @param rememberPassword TRUE if password has to remembered, FALSE
     * otherwise
     */
    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    /**
     * Returns the UIN of the sip registration account.
     * @return the UIN of the sip registration account.
     */
    public String getUin() {
        return uin;
    }

    /**
     * The address of the server we will use for this account
     * @return String
     */
    public String getServerAddress()
    {
        return serverAddress;
    }

    /**
     * The port on the specified server
     * @return int
     */
    public String getServerPort()
    {
        return serverPort;
    }
    
    /**
     * The port on the specified proxy
     * @return int
     */
    public String getProxyPort()
    {
        return proxyPort;
    }

    /**
     * Sets the UIN of the sip registration account.
     * @param uin the UIN of the sip registration account.
     */
    public void setUin(String uin) {
        this.uin = uin;
    }

    /**
     * Sets the server
     * @param serverAddress String
     */
    public void setServerAddress(String serverAddress)
    {
        this.serverAddress = serverAddress;
    }

    /**
     * Sets the server port.
     * @param port int
     */
    public void setServerPort(String port)
    {
        this.serverPort = port;
    }
    
    /**
     * Sets the proxy port.
     * @param port int
     */
    public void setProxyPort(String port)
    {
        this.proxyPort = port;
    }

    /**
     * If the presence is enabled
     * @return If the presence is enabled
     */
    public boolean isEnablePresence() {
        return enablePresence;
    }

    /**
     * If the p2p mode is forced
     * @return If the p2p mode is forced
     */
    public boolean isForceP2PMode() {
        return forceP2PMode;
    }



/**
  *If the user choose the P2PSIP protocol
  *@return If the P2PSIP protocol is used
  *@author Dong Fengyu
  *
  */
   public boolean isP2PSIP(){
       return enableP2PSIP;
}



/**
  *If the password will be checked
  *@return If the password will be checked
  *@author Dong Fengyu
  *
  */
   public boolean isEnablePassCheck(){
       return enablePassCheck;
}




  

    /**
     * The offline contact polling period
     * @return the polling period
     */
    public String getPollingPeriod() {
        return pollingPeriod;
    }

    /**
     * The default expiration of subscriptions
     * @return the subscription expiration
     */
    public String getSubscriptionExpiration() {
        return subscriptionExpiration;
    }

    /**
     * Sets if the presence is enabled
     * @param enablePresence if the presence is enabled
     */
    public void setEnablePresence(boolean enablePresence) {
        this.enablePresence = enablePresence;
    }

    /**
     * Sets if we have to force the p2p mode
     * @param forceP2PMode if we have to force the p2p mode
     */
    public void setForceP2PMode(boolean forceP2PMode) {
        this.forceP2PMode = forceP2PMode;
    }


/**
  *Sets if the P2PSIP protocol is used
  *@param enableP2PSIP if the protocol is used
  *@author Dong Fengyu
  */
  public void setP2PSIP(boolean enableP2PSIP){
      this.enableP2PSIP =enableP2PSIP;
}



/**
  *Sets if the password will be checked
  *@param enablePassCheck if the password will be checked
  *@author Dong Fengyu
  */
  public void setPassCheck(boolean enablePassCheck){
      this.enablePassCheck=enablePassCheck;
}



    /**
     * Sets the offline contacts polling period
     * @param pollingPeriod the offline contacts polling period
     */
    public void setPollingPeriod(String pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }

    /**
     * Sets the subscription expiration value
     * @param subscriptionExpiration the subscription expiration value
     */
    public void setSubscriptionExpiration(String subscriptionExpiration) {
        this.subscriptionExpiration = subscriptionExpiration;
    }

    /**
     * Returns the keep alive method.
     * 
     * @return the keep alive method.
     */
    public String getKeepAliveMethod()
    {
        return keepAliveMethod;
    }

    /**
     * Sets the keep alive method.
     * 
     * @param keepAliveMethod the keep alive method to set
     */
    public void setKeepAliveMethod(String keepAliveMethod)
    {
        this.keepAliveMethod = keepAliveMethod;
    }

    /**
     * Returns the keep alive interval.
     * 
     * @return the keep alive interval
     */
    public String getKeepAliveInterval()
    {
        return keepAliveInterval;
    }

    /**
     * Sets the keep alive interval.
     * 
     * @param keepAliveInterval the keep alive interval to set
     */
    public void setKeepAliveInterval(String keepAliveInterval)
    {
        this.keepAliveInterval = keepAliveInterval;
    }
}
