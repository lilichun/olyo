/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.sip;

import java.text.*;
import javax.sip.*;
import javax.sip.message.*;

import gov.nist.javax.sip.header.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * Class responsible for sending a DTMF Tone using SIP INFO.
 *
 * @todo - remove the following comment once this class has been sufficiently
 * tested.
 *
 * It should actually only be considered as a draft, since it was not heavily
 * tested, and just developped enough to get the DTMF function work I copied
 * and adapted code of the OpSetBasicTelephony implementation for SIP to make
 * this code.
 *
 * @author JM HEITZ
 */
public class OperationSetDTMFSipImpl
    implements SipListener,
    OperationSetDTMF
{
    /**
     * logger for the class
     */
    private static final Logger logger
        = Logger.getLogger(OperationSetDTMFSipImpl.class);

    /**
     * involved protocol provider service
     */
    ProtocolProviderServiceSipImpl pps = null;

    /**
     *Constructor
     *
     * @param pps the SIP Protocol provider service
     */
    public OperationSetDTMFSipImpl(ProtocolProviderServiceSipImpl pps)
    {
        this.pps = pps;
        pps.registerMethodProcessor(Request.INFO, this);
    }


    /**
     * This is just a copy of the bye method from the OpSetBasicTelephony,
     * which was enhanced with a body in order to send the DTMF tone
     *
     * @param callParticipant destination of the DTMF tone
     * @param dtmftone DTMF tone to send
     * @throws OperationFailedException
     */
    private void sayInfo(CallParticipantSipImpl callParticipant,
                         String dtmftone)
        throws OperationFailedException
    {
        Request info = null;
        try
        {
            info = callParticipant.getDialog().createRequest(Request.INFO);
        }
        catch (SipException ex)
        {
            logger.error("Failed to create info request!", ex);
            throw new OperationFailedException(
                "Failed to create info request!"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }

        //here we add the body
        ContentType ct = new ContentType("application", "dtmf-relay");
        String content = "Signal=" + dtmftone + "\r\nDuration=250\r\n";
        ContentLength cl = new ContentLength(content.length());
        info.setContentLength(cl);

        try
        {
            info.setContent(content.getBytes(), ct);
        }
        catch (ParseException ex)
        {
            logger.error("Failed to construct the INFO request", ex);
            throw new OperationFailedException(
                "Failed to construct a client the INFO request"
                , OperationFailedException.INTERNAL_ERROR
                , ex);

        }
        //body ended
        ClientTransaction clientTransaction = null;
        try
        {
            clientTransaction = callParticipant.getJainSipProvider()
                .getNewClientTransaction(info);
        }
        catch (TransactionUnavailableException ex)
        {
            logger.error(
                "Failed to construct a client transaction from the INFO request"
                , ex);
            throw new OperationFailedException(
                "Failed to construct a client transaction from the INFO request"
                , OperationFailedException.INTERNAL_ERROR
                , ex);
        }

        try
        {
            if (callParticipant.getDialog().getState()
                == DialogState.TERMINATED)
            {
                //this is probably because the call has just ended, so don't
                //throw an exception. simply log and get lost.
                logger.warn("Trying to send a dtmf tone inside a "
                            +"TERMINATED dialog.");
                return;
            }

            callParticipant.getDialog().sendRequest(clientTransaction);
            logger.debug("sent request:\n" + info);
        }
        catch (SipException ex)
        {
            throw new OperationFailedException(
                "Failed to send the INFO request"
                , OperationFailedException.NETWORK_FAILURE
                , ex);
        }
    }

    /**
     * Does nothing
     *
     * @param requestEvent the event request
     */
    public void processRequest(RequestEvent requestEvent)
    {
        if (requestEvent == null)
        {
            logger.debug("requestEvent null");
        }
        logger.error("We don't cope with requests" + requestEvent);
    }

    /**
     * Just look if the DTMF signal was well received, and log it
     *
     * @param responseEvent the response event
     */
    public void processResponse(ResponseEvent responseEvent)
    {
        if (responseEvent == null)
        {
            logger.debug("null responseEvent");
            return;
        }
        Response response = responseEvent.getResponse();
        if (response == null)
        {
            logger.debug("null response");
            return;
        }
        int code = response.getStatusCode();
        logger.debug("DTMF status code=" + code);
        if (code != 200)
        {
            logger.error("DTMF Send failed :" + code);
        }
        else
        {
            logger.debug("DTMF succeeded");
        }
    }

    /**
     * In case of timeout, just terminate the transaction
     *
     * @param timeoutEvent the timeout event
     */
    public void processTimeout(TimeoutEvent timeoutEvent)
    {
        //we do nothing
        logger.error("ioexception :" + timeoutEvent);

    }

    /**
     * Just log the exception
     *
     * @param exceptionEvent the event we have to handle
     */
    public void processIOException(IOExceptionEvent exceptionEvent)
    {
        //we do nothing
        if (exceptionEvent == null)
        {
            logger.debug("ioexception null");
            return;
        }
        logger.error("ioexception :" + exceptionEvent);
    }

    /**
     * Just log the end of the transaction
     *
     * @param transactionTerminatedEvent the event we have to handle
     */
    public void processTransactionTerminated(
        TransactionTerminatedEvent transactionTerminatedEvent)
    {
        //we do nothing
        logger.info("Transaction Terminated :" + transactionTerminatedEvent);
    }

    /**
     * Just log the end of the dialog
     *
     * @param dialogTerminatedEvent the event we have to handle
     */

    public void processDialogTerminated(
        DialogTerminatedEvent dialogTerminatedEvent)
    {
        //we do nothing
        logger.info("Dialog Terminated :" + dialogTerminatedEvent);
    }

    /**
     * Sends the <tt>DTMFTone</tt> <tt>tone</tt> to <tt>callParticipant</tt>.
     *
     * @param callParticipant the  call participant to send <tt>tone</tt> to.
     * @param tone the DTMF tone to send to <tt>callParticipant</tt>.
     *
     * @throws OperationFailedException with code OPERATION_NOT_SUPPORTED if
     * DTMF tones are not supported for <tt>callParticipant</tt>.
     *
     * @throws NullPointerException if one of the arguments is null.
     *
     * @throws IllegalArgumentException in case the call participant does not
     * belong to the underlying implementation.
     */
    public void sendDTMF(CallParticipant callParticipant, DTMFTone tone)
        throws OperationFailedException,
                NullPointerException,
                IllegalArgumentException
    {
        if (callParticipant == null || tone == null)
        {
            throw new NullPointerException();
        }
        if (! (callParticipant instanceof CallParticipantSipImpl))
        {
            throw new IllegalArgumentException();
        }

        CallParticipantSipImpl cp = (CallParticipantSipImpl) (callParticipant);
        this.sayInfo(cp, tone.getValue());
    }
}

