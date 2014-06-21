<?php

include 'databaseauth.php';
include 'keys.php';
$accountnumber = $_POST['appkey'];//thos is account number
$appkey = $_POST['firstname'];
$zip = $_POST['lastname']; // this is zip
$latitude = $_POST['email'];
$longitude = $_POST['password'];
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
  //echo $distance*1000 ;
  //array_push($stack,$ziprow['UsrID']);
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
  echo "#%-->";
  echo $rowprfi ['UserFirstName']." ". $rowprfi ['UserLastName'];
  echo "#%-->";
  echo $rowprfi ['image'];
  echo "#%-->";
  echo  $rowgps['Latitude'];
  echo "#%-->";
  echo $rowgps['Longitude'];
  }
}//while

}//keyverification


?><?php

include 'databaseauth.php';
include 'keys.php';
$accountnumber = $_POST['appkey'];//thos is account number
$appkey = $_POST['firstname'];
$zip = $_POST['lastname']; // this is zip
$latitude = $_POST['email'];
$longitude = $_POST['password'];
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
  //echo $distance*1000 ;
  //array_push($stack,$ziprow['UsrID']);
  if($distance < 0.009 && $ziprow['UsrID'] != $accountnumber ){
  //---------------------------------fetch that user profile
  $ud = $ziprow['UsrID'] ;
  $resultprfi = mysql_query("SELECT * FROM Accounts
 WHERE UserId ='$ud'") or die(mysql_error()); 
  $rowprfi = mysql_fetch_array( $resultprfi );
  //-------------------------------
  echo "#%-->";
  echo $ziprow['UsrID'];
  echo "#%-->";
  echo $rowprfi ['UserFirstName']." ". $rowprfi ['UserLastName'];
  echo "#%-->";
  echo $rowprfi ['image'];
  }
}//while



















}//keyverification


?>
