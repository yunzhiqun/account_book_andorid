@rem Gradle startup script for Windows

@if "%DEBUG%"=="" @echo off

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

if not exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle Wrapper JAR not found.
    echo Please run: gradle wrapper
    echo Or open this project in Android Studio.
    exit /b 1
)

@rem Find java.exe
set JAVA_EXE=java.exe
if defined JAVA_HOME goto findJavaFromJavaHome
goto execute

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
if exist "%JAVA_EXE%" goto execute
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
exit /b 1

:execute
@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
