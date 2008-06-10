@echo off

if "%OW_HOME%" == "" set OW_HOME=%~dp0..
set BIN_DIR=%OW_HOME%\bin
set LIB_DIR=%OW_HOME%\lib
set TARGET_DIR=%OW_HOME%\target
set BUILD_DIR=%OW_HOME%\build

set CLASSPATH=%BUILD_DIR%;%TARGET_DIR%\overlayweaver.jar;%LIB_DIR%\je-3.2.76.jar;%LIB_DIR%\xmlrpc-common-3.0.jar;%LIB_DIR%\xmlrpc-server-3.0.jar;%LIB_DIR%\ws-commons-util-1.0.1.jar;%LIB_DIR%\commons-cli-1.1.jar;%LIB_DIR%\jetty-6.1.10.jar;%LIB_DIR%\jetty-util-6.1.10.jar;%LIB_DIR%\servlet-api-2.5-6.1.10.jar;%LIB_DIR%\clink170.jar

set CLASSPATH=..\build;..\target\overlayweaver.jar;..\lib\je-3.2.76.jar;..\lib\xmlrpc-common-3.0.jar;..\lib\xmlrpc-server-3.0.jar;..\lib\ws-commons-util-1.0.1.jar;..\lib\commons-cli-1.1.jar;..\lib\jetty-6.1.10.jar;%LIB_DIR%\jetty-util-6.1.10.jar;..\lib\servlet-api-2.5-6.1.10.jar;..\lib\clink170.jar


set LOGGING_CONFIG=.\logging.properties

java -Djava.util.logging.config.file=%LOGGING_CONFIG% ow.tool.dhtshell.Main %*
