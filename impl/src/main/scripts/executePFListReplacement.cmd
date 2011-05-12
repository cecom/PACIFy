@echo off
setlocal ENABLEDELAYEDEXPANSION

set PFLIST_LIB_DIR=%~dp0..\lib

set PFLIST_CLASSPATH=%PFLIST_LIB_DIR%\*

"%JAVA_HOME%\bin\java.exe" -classpath "%PFLIST_CLASSPATH%" de.oppermann.maven.pflist.PFListPropertyReplacer %*
