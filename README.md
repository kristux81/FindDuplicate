FindDuplicate
=============

Scans file system for duplicate files. Presents a report in the end

Why it was written ?
---------------------

We copy and forget files at various location in our computer.
Some times there is a disk space crunch due to this.
Searching for such files on old file systems takes a lot of time.

Also many a times we update these files.
Now searching for the correct version/copy to update is a tough task
especially when the last update date for the files/folders is lost
( due to lots of moving data here and there )






What does it do ?
------------------

This program groups the files that have same content i.e. a unique signature ( File Message Digest ). 
It also calculate the total disk size that you can reclaim if you delete the duplicate files






What is the Philosophy behind this tool ?
-----------------------------------------

Two files with different name and locations can have same content but will still have same digest ( signature ).






What Files are created by this tool and what is each one for ?
---------------------------------------------------------------

Output file ending with extension .out : Conatins similar files grouped into lines ( all the files per line would
                                          be same in content ) seperated by semicolons. Each entry starts with the
   									  File signature.  
										  
Output file ending with extension .siz : Lists the duplicate file entries with their size on the disk.

Output file ending with extension .del : List of duplicate files that can be deleted ( or moved )
                                         You can use this file for removing or moving these files to other location.
										 
										 Example : To delete all these files just add "del /y " ( without double quotes )
										 in front of each line in this file. Rename the file so that it ends with .bat
										 extension and double click it.	

Output file ending with extension .err : These files record any program errors during the execution.										 

Apart from this is also saves a list of all the files it processes in a seperate file ( generally ending with .lst extension )






OK HOW DO I USE THIS PROGRAM ?
-------------------------------

Place these files to any location on your machine ( after unziping ).
Open file build_dup_list.bat in a text editor ( wordpad / editplus / notepad++ ).

Update the line -->  set SRCHROOT=D:\         to the folder you want to search files. ( defaullt is D:\ )
                   Example: to search all files in C drive -->                    set SRCHROOT=C:\   
                            to search all files in E drive's abc folder -->       set SRCHROOT=E:\abc\    or 	set SRCHROOT=E:\abc        ( which ever works !! )			   
Save the file.
Double click it to start the program.
The excution time will depend upon number and size of files and the size of filesystem.



HOW DO I RUN MULTIPLE COPIES OF THIS PROGRAM AT THE SAME TIME ?
---------------------------------------------------------------

CAUTION : Do this only for searching files in different partitions.
          This option must be used on multicore CPU or large RAM machines ( this again depends on the File system/partition size )

Create multiple copies of this tool ( at different folder locations ). Update the SRCHROOT variable in all these copied  build_dup_list.bat files with a 
different root location ( example C:\ E:\ D:\ F:\ i.e. different windows partitions ) as explained earlier.

Save these files ane double click all of them to launch multiple instance at the same time.	  
		  
