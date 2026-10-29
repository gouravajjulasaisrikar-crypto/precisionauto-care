@echo off
setlocal
if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    )
)
"%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd" %*
