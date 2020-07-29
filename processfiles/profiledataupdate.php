<?php

include 'databaseauth.php';
include 'keys.php';
date_default_timezone_set('America/Los_Angeles');

//----------------------------------- data fetch area
$appkey = $_POST['appkey'];
$accountnumber = $_POST['param2']; // user account number use this to update two tables
$userstayingin = $_POST['param4'];
$userprofession = $_POST['param5'];
$userworksat = $_POST['param6'];
$userfacebook = $_POST['param7'];
$userlinkedin = $_POST['param8'];
$userabout = $_POST['param11'];
$userimage = $_POST['param12']; // its in different table
if($appkey == $mykey){
//-------------------------------------------------------------------------------
//----------
if($userimage != NULL ){
$result = mysql_query("UPDATE Accounts SET image='$userimage' WHERE UserId='$accountnumber'") 
or die(mysql_error());
}
// some user fields could be null so update only those data
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
if($userabout != NULL ){
$result = mysql_query("UPDATE profiledata SET usrstatus='$userstatus' WHERE usrid='$accountnumber'") 
or die(mysql_error());
}
if($userfacebook != NULL){
$userfacebook = "https://www.facebook.com/".$userfacebook;
$result = mysql_query("UPDATE profiledata SET facebook='$userfacebook' WHERE usrid='$accountnumber'")
or die(mysql_error());
}
if($userlinkedin != NULL){
$userlinkedin = "http://www.linkedin.com/".$userlinkedin;
$result = mysql_query("UPDATE profiledata SET linkedin='$userlinkedin' WHERE usrid='$accountnumber'")
or die(mysql_error());
}

//-------------------------------------------------------------------------------
}


?>
