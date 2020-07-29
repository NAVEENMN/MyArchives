<?php

include 'databaseauth.php';
include 'keys.php';
date_default_timezone_set('America/Los_Angeles');
//----------------------------------- data fetch area
$appkey = $_POST['appkey'];
$firstname = $_POST['param2'];
$lastname = $_POST['param3'];
$email = $_POST['param4'];
$password = $_POST['param5'];
$image = $_POST['param6'];
$gender = $_POST['param7'];
$contactnumber =  $_POST['param8'];
$firstlogindate = date('m/d/Y h:i:s a', time()); // this will be server data and time for our reference
$lastlogindate = date('m/d/Y h:i:s a', time()); // 
$token = $lastname.rand(1000, 1000000);
if($appkey == $mykey){
//-------------------------------------------------------------------------------
//-------------> check if that email exist
// Get a specific result from the "example" table
$result = mysql_query("SELECT * FROM Accounts
 WHERE contactnumber ='$contactnumber'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );

if ($row ) {
	echo "no";
}
else{
// Get a specific result from the "example" table
$result = mysql_query("SELECT * FROM Accounts
 WHERE EmailId ='$email'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );

if ($row ) {
	echo "no";
}

else {//----------------------------------------> user ok to create account
//--------------
mysql_query("INSERT INTO `Accounts`( `TokenKey`,`image`,`UserFirstName`, `UserLastName`,`UserGender`,`FirstLoginDate`, `LastLoginDate`,`EmailId`, `LoginPassword`,`contactnumber`) VALUES ( '$token','$image','$firstname','$lastname','$gender',NOW(),NOW(),'$email','$password','$contactnumber')") or die(mysql_error());
$res = mysql_query("SELECT MAX(UserId) FROM Accounts");
$uid = mysql_fetch_array( $res );
$userid = $uid[0];
//--------------
$dummy = "none";
$is = "no";
mysql_query("INSERT INTO `profiledata`( `usrid`,`usrstayingat`, `usrprofession`,`usrworksat`,`About`,`facebook`,`linkedin`, `isupdated`) VALUES ( '$userid','$dummy','$dummy','$dummy','$dummy','$dummy','$dummy','$is')") or die(mysql_error());
//----------------------
echo $uid[0]."-".$token;

//------ add profile of the user


}
}
}//appkeytestarea

else{ // request from other source not the authentic app
$ip = $_SERVER['REMOTE_ADDR'];
echo "inv";
}
?>







