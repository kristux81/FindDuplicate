@echo off

REM  Generates a file containing list of files with absolute path and their MD5 digest
setlocal

REM set PHP parser home here
set PHP_HOME=C:\UNIX\php

REM  set search root here
set SRCHROOT="D:\Backup\music\Kishore Kumar"
set FILENAME=signatures.lst

echo Searching All Files......
@%~dp0bin\find.exe %SRCHROOT% -type f -print > %FILENAME% 2> %FILENAME%.err

echo Building File - Signature Association for all Files......
echo Build Started at : %time%
%PHP_HOME%\php %~dp0bin\find_duplicates.php -f %~dp0%FILENAME%
echo Build Ended at : %time%

endlocal
pause
