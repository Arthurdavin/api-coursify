@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off

set APP_NAME=Gradle
set WRAPPER_JAR=%~dp0gradle\wrapper\gradle-wrapper.jar
set WRAPPER_PROPERTIES=%~dp0gradle\wrapper\gradle-wrapper.properties

java -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
