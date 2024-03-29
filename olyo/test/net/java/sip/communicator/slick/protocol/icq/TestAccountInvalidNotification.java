/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.slick.protocol.icq;

import junit.framework.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * Phoney tests to signal specific problems with the
 * accounts.properties file
 * @author Brian Burch
 */
public class TestAccountInvalidNotification extends TestCase
{
    private static final Logger logger =
        Logger.getLogger(TestAccountInstallation.class);
    /**
     * The lock that we wait on until registration is finalized.
     */
    private Object registrationLock = new Object();

    ProtocolProviderFactory icqProviderFactory  = null;

    public TestAccountInvalidNotification(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();

    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * It is not meaningful to define a test suite. Each of the
     * pseudo-tests reports a different setup failure, so the
     * appropriate test should be added individually.
     * <p>
     * As a safety measure, we add an empty test suite which
     * will generate a "no tests found" failure.
     *
     * @return an empty test suite.
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        // will generate a jUnit "no tests found" error condition
        return suite;
    }

    /**
     * The icq test suites MUST have an accounts.properties file
     * that defines two icq test accounts. This test is ONLY
     * executed when icqProtocolProviderSlick.start() has failed
     * to load the Properties and it deliberately fails with a
     * meaningful message.
     */
    public void failIcqTesterAgentMissing()
    {
        fail("The IcqTesterAgent on icq was not defined. "
            +"Possible reasons: account.properties file not found "
            +"in lib directory. Please see wiki for advice on unit "
            +"test setup.");
    }

    /**
     * This test is ONLY executed when icqProtocolProviderSlick.start()
     * has failed to register with the icq service when providing
     * the username and password defined in the account.properties file.
     * It deliberately fails with a meaningful message.
     */
    public void failIcqTesterAgentRegisterRejected()
    {
        fail("Registering the IcqTesterAgent on icq has failed. "
            +"Possible reasons: authentification failure (wrong ICQ "
            +"account number, no password, wrong password), "
            +"or Connection rate limit exceeded.");
    }
}
