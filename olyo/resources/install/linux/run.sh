mkdir -p $HOME/.sip-communicator/log

export PATH=$PATH:native
export JAVA_HOME=jre
${JAVA_HOME}/bin/java -classpath "lib/jdic-all.jar:lib/jdic_stub.jar:lib/felix.jar:lib/sc-launcher.jar:sc-bundles/util.jar" -Djava.library.path=native -Dfelix.config.properties=file:./lib/felix.client.run.properties -Djava.util.logging.config.file=lib/logging.properties net.java.sip.communicator.launcher.SIPCommunicator
