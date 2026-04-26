@echo off
setlocal

set "MAVEN_VERSION=3.9.9"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Baixando Maven %MAVEN_VERSION%... aguarde.
    if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"
    powershell -Command "& { $ProgressPreference='SilentlyContinue'; $tmp=[System.IO.Path]::GetTempFileName()+'.zip'; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile $tmp; Expand-Archive -Path $tmp -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force; Remove-Item $tmp }"
    echo Maven %MAVEN_VERSION% pronto!
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
