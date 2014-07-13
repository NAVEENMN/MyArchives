<?php

include 'databaseauth.php';
include 'keys.php';
date_default_timezone_set('America/Los_Angeles');

//----------------------------------- data fetch area
$appkey = $_POST['appkey'];
$accountnumber = $_POST['param2']; // user account number use this to update two tables
$userhometown = $_POST['param3'];
$userstayingin = $_POST['param4'];
$userprofession = $_POST['param5'];
$userworksat = $_POST['param6']; 
$userhobbies = $_POST['param7'];
$usermusic = $_POST['param8'];
$usermovies = $_POST['param9'];
$userbooks = $_POST['param10'];
$userstatus = $_POST['param11'];
$userimage = $_POST['param12']; // its in different table
$userpassion = $_POST['param13'];
if($appkey == $mykey){
//-------------------------------------------------------------------------------
//----------
if($userimage != NULL ){
$result = mysql_query("UPDATE Accounts SET image='$userimage' WHERE UserId='$accountnumber'") 
or die(mysql_error());
}
// some user fields could be null so update only those data
if($userhometown != NULL ){
$result = mysql_query("UPDATE profiledata SET usrhometown='$userhometown' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userstayingin != NULL ){
$result = mysql_query("UPDATE profiledata SET usrstayingat='$userstayingin' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userprofession != NULL ){
$result = mysql_query("UPDATE profiledata SET usrprofession='$userprofession' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userworksat != NULL ){
$result = mysql_query("UPDATE profiledata SET usrworksat='$userworksat' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userhobbies != NULL ){
$result = mysql_query("UPDATE profiledata SET usrhobbies='$userhobbies' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($usermusic != NULL ){
$result = mysql_query("UPDATE profiledata SET usrmusic='$usermusic' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($usermovies != NULL ){
$result = mysql_query("UPDATE profiledata SET usrmovies='$usermovies' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userbooks != NULL ){
$result = mysql_query("UPDATE profiledata SET usrbooks='$userbooks' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userstatus != NULL ){
$result = mysql_query("UPDATE profiledata SET usrstatus='$userstatus' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userpassion != NULL ){
$result = mysql_query("UPDATE profiledata SET passion='$userpassion' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}

//-------------------------------------------------------------------------------
}


?>
