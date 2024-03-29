/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.sip;

import javax.sip.*;
import javax.sip.message.*;
import javax.sip.address.*;
import javax.sip.header.*;
import java.text.*;
import java.util.*;
import java.net.*;
import net.java.sip.communicator.util.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;

/**
 * Handles OPTIONS requests by replying with an OK response containing
 * methods that we support.
 *
 * @author Emil Ivov
 */
public class ClientCapabilities
    implements SipListener
{
    private static Logger logger = Logger.getLogger(ClientCapabilities.class);

    /**
     * The protocol provider that created us.
     */
    private ProtocolProviderServiceSipImpl provider = null;
    
    /**
     * The timer that runs the keep-alive task
     */
    private Timer keepAliveTimer = null;
    
    /**
     * The next long to use as a cseq header value.
     */
    private long nextCSeqValue = 1;

    public ClientCapabilities(ProtocolProviderServiceSipImpl protocolProvider)
    {
        this.provider = protocolProvider;
        provider.registerMethodProcessor(Request.OPTIONS, this);
        
        provider.addRegistrationStateChangeListener(new RegistrationListener());
    }

    /**
     * Receives options requests and replies with an OK response containing
     * methods that we support.
     *
     * @param requestEvent the incoming options request.
     */
    public void processRequest(RequestEvent requestEvent)
    {
        Response optionsOK = null;
        try
        {
            optionsOK = provider.getMessageFactory().createResponse(
                Response.OK,
                requestEvent.getRequest());

            Iterator supportedMethods
                = provider.getSupportedMethods().iterator();

            //add to the allows header all methods that we support
            while(supportedMethods.hasNext())
            {
                String method = (String)supportedMethods.next();

                //don't support REGISTERs
                if(method.equals(Request.REGISTER))
                    continue;

                optionsOK.addHeader(
                    provider.getHeaderFactory().createAllowHeader(method));
            }
            
            Iterator events = provider.getKnownEventsList().iterator();
            
            synchronized (provider.getKnownEventsList()) {
                while (events.hasNext()) {
                    String event = (String) events.next();
                    
                    optionsOK.addHeader(provider.getHeaderFactory()
                            .createAllowEventsHeader(event));
                }
            }
            
            //add a user agent header.
            optionsOK.setHeader(provider.getSipCommUserAgentHeader());
        }
        catch (ParseException ex)
        {
            //What else could we do apart from logging?
            logger.warn("Failed to create an incoming OPTIONS request", ex);
            return;
        }

        try
        {
            ServerTransaction sTran =  requestEvent.getServerTransaction();
            if (sTran == null)
            {
                SipProvider sipProvider = (SipProvider)requestEvent.getSource();
                sTran = sipProvider
                    .getNewServerTransaction(requestEvent.getRequest());
            }

            sTran.sendResponse(optionsOK);
        }
        catch (InvalidArgumentException ex)
        {
            //What else could we do apart from logging?
            logger.warn("Failed to send an incoming OPTIONS request", ex);
            return;
        }
        catch (SipException ex)
        {
            //What else could we do apart from logging?
            logger.warn("Failed to send an incoming OPTIONS request", ex);
            return;
        }
    }

    /**
     * ignore. don't needed.
     * @param dialogTerminatedEvent unused
     */
    public void processDialogTerminated(
                            DialogTerminatedEvent dialogTerminatedEvent)
    {

    }

    /**
     * ignore. don't needed.
     * @param exceptionEvent unused
     */
    public void processIOException(IOExceptionEvent exceptionEvent)
    {
    }

    /**
     * ignore for the time being
     * @param responseEvent unused
     */
    public void processResponse(ResponseEvent responseEvent)
    {
    }

    /**
     * ignore for the time being.
     * @param timeoutEvent unused
     */
    public void processTimeout(TimeoutEvent timeoutEvent)
    {
    }

    /**
     * ignore for the time being.
     * @param transactionTerminatedEvent unused
     */
    public void processTransactionTerminated(
        TransactionTerminatedEvent transactionTerminatedEvent)
    {
    }
    
    /**
     * Returns the next long to use as a cseq header value.
     * @return the next long to use as a cseq header value.
     */
    private long getNextCSeqValue()
    {
        return nextCSeqValue++;
    }
    
    private class KeepAliveTask
        extends TimerTask
    {
        public void run()
        {
            try
            {
                //From
                FromHeader fromHeader = null;
                try
                {
                    fromHeader = provider.getHeaderFactory().createFromHeader(
                        provider.getOurSipAddress(), ProtocolProviderServiceSipImpl
                        .generateLocalTag());
                }
                catch (ParseException ex)
                {
                    //this should never happen so let's just log and bail.
                    logger.error(
                        "Failed to generate a from header for our register request."
                        , ex);
                    return;
                }

                //Call ID Header
                CallIdHeader callIdHeader
                    = provider.getDefaultJainSipProvider().getNewCallId();

                //CSeq Header
                CSeqHeader cSeqHeader = null;
                try
                {
                    cSeqHeader = provider.getHeaderFactory().createCSeqHeader(
                        getNextCSeqValue(), Request.OPTIONS);
                }
                catch (ParseException ex)
                {
                    //Should never happen
                    logger.error("Corrupt Sip Stack", ex);
                    return;
                }
                catch (InvalidArgumentException ex)
                {
                    //Should never happen
                    logger.error("The application is corrupt", ex);
                    return;
                }

                //To Header 
                ToHeader toHeader = null;
                try
                {
                    toHeader = provider.getHeaderFactory().createToHeader(
                        provider.getOurSipAddress(), null);
                }
                catch (ParseException ex)
                {
                    logger.error("Could not create a To header for address:"
                                  + fromHeader.getAddress(),
                                  ex);
                    return;
                }

                InetAddress destinationInetAddress = null;
                try
                {
                    destinationInetAddress = InetAddress.getByName(
                        ((SipURI) provider.getOurSipAddress().getURI()).getHost());


			logger.info("ClientCapabilities.java---destinationInetAddress ="+destinationInetAddress);

			
                }
                catch (UnknownHostException ex)
                {
                    logger.error(ex);
                    return;
                }

                //Via Headers
                ArrayList viaHeaders = provider.getLocalViaHeaders(
                    destinationInetAddress, provider.getDefaultListeningPoint());

                //MaxForwardsHeader
                MaxForwardsHeader maxForwardsHeader = provider.
                    getMaxForwardsHeader();
                //Request
                Request request = null;
                try
                {
                    //create a host-only uri for the request uri header.
                    String domain 
                        = ((SipURI) toHeader.getAddress().getURI()).getHost();
                    SipURI requestURI 
                        = provider.getAddressFactory().createSipURI(null,domain);
                    request = provider.getMessageFactory().createRequest(
                          requestURI
                        , Request.OPTIONS
                        , callIdHeader
                        , cSeqHeader
                        , fromHeader
                        , toHeader
                        , viaHeaders
                        , maxForwardsHeader);


			logger.info("P1 :ClientCapabilities.java---request = "+request);		
                }
                catch (ParseException ex)
                {
                    logger.error("Could not create the register request!", ex);
                    return;
                }
                
                Iterator supportedMethods
                    = provider.getSupportedMethods().iterator();
                
                //add to the allows header all methods that we support
                while(supportedMethods.hasNext())
                {
                    String method = (String)supportedMethods.next();

                    //don't support REGISTERs
                    if(method.equals(Request.REGISTER))
                        continue;

                    request.addHeader(
                        provider.getHeaderFactory().createAllowHeader(method));
					

			logger.info("P2 :ClientCapabilities.java---request = "+request);	
			
                }

                Iterator events = provider.getKnownEventsList().iterator();

                synchronized (provider.getKnownEventsList()) 
                {
                    while (events.hasNext()) 
                    {
                        String event = (String) events.next();

                        request.addHeader(provider.getHeaderFactory()
                                .createAllowEventsHeader(event));
                    }
                }

                //User Agent
                UserAgentHeader userAgentHeader
                    = provider.getSipCommUserAgentHeader();
                if(userAgentHeader != null)
                    request.addHeader(userAgentHeader);

                //Contact Header (should contain IP)
                ContactHeader contactHeader
                    = provider.getContactHeader(
                        destinationInetAddress, provider.getDefaultListeningPoint());

                request.addHeader(contactHeader);
				

		logger.info("P3 :ClientCapabilities.java---request = "+request);


		

                //Transaction
                ClientTransaction optionsTrans = null;
                try
                {
                    optionsTrans = provider.getDefaultJainSipProvider().
                        getNewClientTransaction(request);
					

			logger.info("P4 :ClientCapabilities.java---ClientTransaction optionsTrans = "
				+optionsTrans);
			
                }
                catch (TransactionUnavailableException ex)
                {
                    logger.error("Could not create a register transaction!\n"
                                  + "Check that the Registrar address is correct!",
                                  ex);
                    return;
                }
                try
                {
                    optionsTrans.sendRequest();
                    logger.debug("sent request= " + request);
                }
                //we sometimes get a null pointer exception here so catch them all
                catch (Exception ex)
                {
                    logger.error("Could not send out the register request!", ex);
                    return;
                }
            }catch(Exception ex)
            {
                logger.error("Cannot send OPTIONS keep alive", ex);
            }
        }
   }
    
    private class RegistrationListener
        implements RegistrationStateChangeListener
    {
        /**
        * The method is called by a ProtocolProvider implementation whenever
        * a change in the registration state of the corresponding provider had
        * occurred. The method is particularly interested in events stating
        * that the SIP provider has unregistered so that it would fire
        * status change events for all contacts in our buddy list.
        *
        * @param evt ProviderStatusChangeEvent the event describing the status
        * change.
        */
        public void registrationStateChanged(RegistrationStateChangeEvent evt)
        {
            if(evt.getNewState() == RegistrationState.UNREGISTERING || 
                evt.getNewState() == RegistrationState.CONNECTION_FAILED)
            {
                // stop any task associated with the timer
                if (keepAliveTimer != null) 
                {
                    keepAliveTimer.cancel();
                    keepAliveTimer = null;
                }
            } else if (evt.getNewState().equals(RegistrationState.REGISTERED))
            {
                String keepAliveMethod = (String)provider.getAccountID().
                    getAccountProperties().
                        get(ProtocolProviderServiceSipImpl.KEEP_ALIVE_METHOD);
                
                if(keepAliveMethod == null || 
                    !keepAliveMethod.equalsIgnoreCase("options"))
                    return;
                
                String keepAliveIntStr = (String)provider.getAccountID().
                    getAccountProperties().
                        get(ProtocolProviderServiceSipImpl.KEEP_ALIVE_INTERVAL);
                
                if(keepAliveIntStr != null)
                {
                    int keepAliveInterval = -1;
                    try
                    {
                        keepAliveInterval = Integer.valueOf(keepAliveIntStr).intValue();
                    }
                    catch (Exception ex)
                    {
                        logger.error("Wrong value for keep-alive interval");
                    }

                    if(keepAliveInterval > 0)
                    {
                        if(keepAliveTimer == null)
                            keepAliveTimer = new Timer();
                        
                        keepAliveTimer.schedule(
                            new KeepAliveTask(), 0, keepAliveInterval * 1000);
                    }
                }
            }
        }
    }
}
