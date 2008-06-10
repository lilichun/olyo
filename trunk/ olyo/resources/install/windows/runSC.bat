mkdir "%UserProfile%/.sip-communicator/log"
set PATH=%PATH%;native
start javaw -classpath "lib/jdic-all.jar;lib/jdic_stub.jar;lib/felix.jar;lib/sc-launcher.jar;lib/ws-commons-util-1.0.2.jar;lib/xmlrpc-client-3.1.jar;lib/xmlrpc-server-3.1.jar;sc-bundles/util.jar" -Dfelix.config.properties=file:./lib/felix.client.run.properties -Djava.util.logging.config.file=lib/logging.properties -Duser.language=us net.java.sip.communicator.launcher.SIPCommunicator
