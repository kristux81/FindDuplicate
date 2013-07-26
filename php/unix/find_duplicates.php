<?php

// turn off all errors ( should be removed to get list of non processed files due to errors )
error_reporting(0);

// File Names Association with Signature
// A dummy key-pair added to avoid Null value failure at later stage
$out= array("0"=>"0");

// File name that were not processed by the script
$err= array();

// calculate total disk space taken by duplicate content
$tot_size=0;

define(MAGIC_ID, " ; ");
/*
  Calculates Signature of each file. An association is build between all duplicate files 
  ( ; sepeareted list )  
*/
function buildMap($filename, &$out, &$argv, &$tot_size)
{
 $file=fopen($filename,"r") or exit("Unable to Open file : ".$filename);
 $i=0;
 
 $file2=fopen($filename.'.siz',"w") or exit("Unable to Write file : ".$filename.'.siz');
 
 // processed count
$pCount=0;
 
 // duplicate count
$dupCount=0;
 
 // total dup file size
$tot_size=0.0;
 
 while(!feof($file))
 {
  $line=trim(fgets($file)," \t");
  if($line!=NULL){
	$sign = sha1_file($line=str_replace("\\","/",trim($line)));
	if ( $sign !== FALSE  )
	{
	  if(array_key_exists($sign,$out)){
		$out[$sign]=$out[$sign].MAGIC_ID.$line;
		$str=sprintf("%-150s : %16.2f KB\n", $line, filesize($line) / 1024);
		fwrite($file2, $str);
		$tot_size += filesize($line);
        $dupCount ++;
	  }
	  else $out[$sign]=$line;
	} 
	else $err[$i++]=$line;
	$pCount++;
  }
 }
 
 print "Total files Processed : ".$pCount."\n" ;
 print "Total Duplicate files Found : ".$dupCount."\n" ;
 printf ("Total Disk Space taken up by Duplicate files : %.2f MB\n" , $tot_size/(1024*1024));
 print "Duplicate File Size Details added to : ".$filename.".siz\n";
 
 fclose($file2); 
 fclose($file);
}

/*
  Writes an Array to File by indexing keys and values 
*/
function writeFile($filename, $map)
{
 $file=fopen($filename,"w") or exit("Unable to Write file : ".$filename);
 $keys=array_keys($map);
 $values=array_values($map);
 $size=count($keys);
 
 // Start indexing from 1 to ignore dummy key-pair
 for($i=1; $i<$size; $i++)
 {
 // search if value contains magical key MAGIC_ID
 $pos = strpos($values[$i], MAGIC_ID);
 
 // Write entry to file only if key found
 if($pos !== false)
  fwrite($file, $keys[$i]."  ".$values[$i]."\n");
 }
  print "Duplicate Files Association added to : ".$filename."\n";
 fclose($file);
}

/* PROGRAM BEGINS
*/
// Get commandline options ( -f with filename )
$options = getopt("f:");

//Signature File Name
$inputfile=$options['f'];

if(!isset($inputfile)) die("No Signature Lookup File as Input. Exiting ..");
else
{
 /* On Screen Debug 
 var_dump($inputfile);
 */
 
 // DS Creation with Signature Calculation
 buildMap($inputfile, $out, $argv, $tot_size);

//  Entries File
	writeFile($inputfile.".out", $out);

// Not Processed Entries File	( Feature not working currently )
if(sizeof($err)>0) {
    writeFile($inputfile.".nop", $err);
	print "Unprocessed Files entries have been added to : ".$inputfile.".nop\n" ;
}
 
 /* On Screen Debug 
 var_dump($out);
 var_dump($dup);
 */
}
