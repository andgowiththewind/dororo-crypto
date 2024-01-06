@echo off
%~d0
cd %~dp0
cd ..
cd ..
call mvn clean
pause