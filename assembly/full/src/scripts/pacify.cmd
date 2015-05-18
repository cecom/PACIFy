@echo off
setlocal ENABLEDELAYEDEXPANSION

set PACIFY_LIB_DIR=%~dp0..\lib

set PACIFY_CLASSPATH=%PACIFY_LIB_DIR%\*

"%JAVA_HOME%\bin\java.exe" -classpath "%PACIFY_CLASSPATH%" com.geewhiz.pacify.commandline.PacifyViaCommandline %*
