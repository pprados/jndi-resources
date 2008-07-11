@echo off
REM
REM Copyright 2008 Philippe Prados.
REM 
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at
REM 
REM       http://www.apache.org/licenses/LICENSE-2.0
REM 
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.
REM 

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