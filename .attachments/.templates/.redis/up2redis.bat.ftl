@echo off

for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set datetimeStr=%%a
set datetimeStr=%datetimeStr:~0,8%_%datetimeStr:~8,6%

set outputFile=%userprofile%\Desktop\RedisServerStatus.txt
set errorOccurred=0

type nul > %outputFile%


<#list list as item>
netstat -ano | findstr "${item.port}" >> nul
IF %ERRORLEVEL% EQU 1 (
    start "" "${item.batPath}"
) ELSE (
    echo "[%datetimeStr%]: ${item.name} error message: Port ${item.port} is already in use." >> %outputFile%
    set errorOccurred=1
)

</#list>

IF %errorOccurred% EQU 1 (
start notepad %outputFile%
) ELSE (
exit
)
