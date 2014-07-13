<?php

include 'databaseauth.php';
include 'keys.php';
$accountnumber = $_POST['appkey'];//thos is account number
$appkey = $_POST['param2'];
$zip = $_POST['param3']; // this is zip
$latitude = $_POST['param4'];
$longitude = $_POST['param5'];
$stack = array();
if($appkey == $mykey){
// Get a specific result from the "example" table
$zipresult = mysql_query("SELECT * FROM Location
 WHERE Zipcode='$zip'") or die(mysql_error()); 

while($ziprow = mysql_fetch_array($zipresult)) {
  $perlati = $ziprow['Latitude'];
  $perlongi = $ziprow['Longitude'];
  $diffperlati = $perlati - $latitude ;
  $diffperlongi = $perlongi - $longitude ;
  $distance = sqrt(pow($diffperlati, 2)+ pow($diffperlongi, 2));
  if($distance < 0.0005 && $ziprow['UsrID'] != $accountnumber ){
  //---------------------------------fetch that user profile
  $ud = $ziprow['UsrID'] ;
  $resultprfi = mysql_query("SELECT * FROM Accounts
 WHERE UserId ='$ud'") or die(mysql_error()); 
  $rowprfi = mysql_fetch_array( $resultprfi );
  //-------------------------------
  //------------------------------- fetch the gps location
  $gpsprfi = mysql_query("SELECT * FROM Location
 WHERE UsrID ='$ud'") or die(mysql_error()); 
  $rowgps = mysql_fetch_array( $gpsprfi );
  //-------------------------------------------------------
  echo "#%-->";
  echo $ziprow['UsrID'];
  }
}//while

}//keyverification


?>
