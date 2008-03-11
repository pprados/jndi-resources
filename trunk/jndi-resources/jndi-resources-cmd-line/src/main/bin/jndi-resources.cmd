@echo off
@if "%OS%" == "Windows_NT"  setlocal enableDelayedExpansion

Rem calculate JNDI_RESOURCES_HOME
set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
pushd %DIRNAME%..
set JNDI_RESOURCES_HOME=%CD%
popd

Rem Calculate CLASSPATH
set CLASSPATH=
for %%i in (%JNDI_RESOURCES_HOME%\lib\*.*) do set CLASSPATH=!CLASSPATH!;%%i

rem echo ---------
rem echo JAVA_HOME=%JAVA_HOME%
rem echo JAVA_OPTS=%JAVA_OPTS%
rem echo CLASSPATH=%CLASSPATH%
rem echo JNDI_RESOURCES_HOME=%JNDI_RESOURCES_HOME%
rem echo ---------

java %JAVA_OPTS% -classpath "%CLASSPATH%" -Djndi.resources.home="%JNDI_RESOURCES_HOME%" com.googlecode.jndiresources.JNDIResources  -t "%JNDI_RESOURCES_HOME%/templates" %*