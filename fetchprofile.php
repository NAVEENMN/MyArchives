<?php
date_default_timezone_set('America/Los_Angeles');
include 'databaseauth.php';
$accountnumber = $_POST['appkey'];//thos is account number
$token = $_POST['param2'];
$zip = $_POST['param3']; // this is zip
$latitude = $_POST['param4'];
$longitude = $_POST['param5'];
$country = $_POST['param6'];
$pinpoint = $_POST['param7'];
$Status = "Hello there!!";
$timein = date('m/d/Y h:i:s a', time());// maintain the sever time
$timeout = date('m/d/Y h:i:s a', time() + 15);// 15 mins
//$timeout = now();
//----------------------------------------------------------
$result = mysql_query("SELECT * FROM Accounts
 WHERE UserId ='$accountnumber'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );

$loc = mysql_query("SELECT * FROM Location
 WHERE UsrID ='$accountnumber'") or die(mysql_error());
$locat = mysql_fetch_array( $loc );

$prf = mysql_query("SELECT * FROM profiledata
 WHERE usrid ='$accountnumber'") or die(mysql_error()); 
$prfdata = mysql_fetch_array( $prf );

//--------

$Gender = $row['UserGender'];
$Profession = $prfdata['usrprofession'];
$WorksAt = $prfdata['usrworksat'] ;
$CurrentyCity = $prfdata['usrstayingat'] ;
$About = $prfdata['About'];

//-----------------------------------------------------------
if($locat){ //location already exists
//delete old now and add new one
mysql_query("DELETE FROM Location WHERE UsrID ='$accountnumber'");
mysql_query("INSERT INTO Location 
(AddressId, Latitude, Longitude, Zipcode, UsrID, Country, Status, Timein, Timeout) VALUES('$accountnumber','$latitude', '$longitude','$zip','$accountnumber','$country','$Status','$timein','$timeout') ") 
or die(mysql_error());
}
else{ //location doesn`t exist add new one

// Insert a row of information into the table "example"
mysql_query("INSERT INTO Location 
(AddressId, Latitude, Longitude, Zipcode, UsrID, Country, Status ) VALUES('$accountnumber','$latitude', '$longitude','$zip','$accountnumber','$country','$Status' ) ") 
or die(mysql_error());
}

if ($row){
echo $row['UserFirstName']."-".$row['UserLastName']."-".$Gender."-".$About."-".$Profession."-".$WorksAt."-".$CurrentyCity;
}
else{
echo "no";
}
?>
