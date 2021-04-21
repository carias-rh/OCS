@rem @echo off

set MEM_ARGS=-Xms128m -Xmx256m

if exist "%JAVA_HOME%\bin\java.exe" (
	start /min with_java_home.bat
    goto end
)

start /min java %MEM_ARGS% -jar ./../lib/oct-offline-5.0.0.jar

:end