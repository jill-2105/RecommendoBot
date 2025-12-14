@REM Maven Wrapper startup script
@echo off

set MAVEN_PROJECTBASEDIR=%~dp0

@REM Find java.exe
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found
echo.
goto error

:init
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set MAVEN_CMD_LINE_ARGS=%*

%JAVA_EXE% -jar %WRAPPER_JAR% %MAVEN_CMD_LINE_ARGS%

:error
exit /b 1
