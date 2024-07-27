@echo off
setlocal enabledelayedexpansion
:input
set /p userInput="give me a number >= 10:"

set "isValid=true"
set "num=!userInput!"

for /f "delims=0123456789" %%i in ("!num!") do set "isValid=false"
if !isValid! equ true (
    if !num! lss 11 set "isValid=false"
) else (
    set "isValid=false"
)

if !isValid! equ true (
    echo success yeah..
) else (
    echo error,try again..
    goto input
)
echo.
echo git log ...
cd %~dp0
cd ../../
set "desktop_path=%USERPROFILE%\Desktop"
set datetimeStr=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set txtPath="%desktop_path%\project_%datetimeStr%_git_log_msg.txt"
git log -%userInput% --pretty=format:"%%ad %%s" --date=short > %txtPath%
start "" "%txtPath%"
endlocal