@echo off
%~d0
cd %~dp0
cd ..
cd ..
mvn -U idea:idea
pause