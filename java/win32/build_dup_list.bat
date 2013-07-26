@echo off

REM  Generates a file containing list of files with absolute path and their MD5 digest
setlocal

REM  set search root here
set SRCHROOT=D:\
set FILENAME=signatures.lst

echo Searching All Files......
@%~dp0bin\find.exe %SRCHROOT% -type f -print > %FILENAME% 2> %FILENAME%.err

echo Building File - Signature Association for all Files......
echo Build Started at : %time%
java -jar %~dp0bin\utils.jar %FILENAME% -o 2> %0.err
echo Build Ended at : %time%

endlocal
pause
