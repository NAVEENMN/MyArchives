<?php
include 'databaseauth.php';
$accountnumber = $_POST['appkey'];//thos is account number
$token = $_POST['param2'];
$zip = $_POST['param3']; // this is zip
$latitude = $_POST['param4'];
$longitude = $_POST['param5'];
$country = $_POST['param6'];
$pinpoint = $_POST['param7'];
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



//-----------------------------------------------------------
if($locat){ //location already exists
//delete old now and add new one

$result = mysql_query("UPDATE Location SET Latitude='$latitude' WHERE UsrID='$accountnumber'") 
or die(mysql_error());

$result = mysql_query("UPDATE Location SET Longitude='$longitude' WHERE UsrID='$accountnumber'") 
or die(mysql_error());

}
else{ //location doesn`t exist add new one

// Insert a row of information into the table "example"
$result = mysql_query("UPDATE Location SET Latitude='$latitude' WHERE UsrID='$accountnumber'") 
or die(mysql_error());

$result = mysql_query("UPDATE Location SET Longitude='$longitude' WHERE UsrID='$accountnumber'") 
or die(mysql_error());

}

if ($row){
echo $row['UserFirstName'];
}
else{
echo "no";
}
?>
