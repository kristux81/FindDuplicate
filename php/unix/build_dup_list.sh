#!/bin/sh

# Generates a file containing list of files with absolute path and their MD5 digest
FILENAME=list_sha1.lst

# set search root here
SRCHROOT=/home

echo Searching All Files......
find $SRCHROOT -type f -print > $FILENAME 2> $FILENAME.err

echo Extracting Duplicate Files from list of all Files......
php find_duplicates.php -f $FILENAME

# cat $FILENAME.dup

