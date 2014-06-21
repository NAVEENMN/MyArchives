<?php
include 'databaseauth.php';
$accountnumber = $_POST['appkey'];//thos is account number
$token = $_POST['firstname'];
$zip = $_POST['lastname']; // this is zip
$latitude = $_POST['email'];
$longitude = $_POST['password'];
$country = $_POST['image'];
$result = mysql_query("SELECT * FROM Accounts
 WHERE UserId ='$accountnumber'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );

$loc = mysql_query("SELECT * FROM Location
 WHERE UsrID ='$accountnumber'") or die(mysql_error());
$locat = mysql_fetch_array( $loc );

if($locat){ //location already exists
//delete old now and add new one
mysql_query("DELETE FROM Location WHERE UsrID ='$accountnumber'");
mysql_query("INSERT INTO Location 
(Latitude, Longitude, Zipcode, UsrID, Country) VALUES('$latitude', '$longitude','$zip','$accountnumber','country' ) ") 
or die(mysql_error());
}
else{ //location doesn`t exist add new one

// Insert a row of information into the table "example"
mysql_query("INSERT INTO Location 
(Latitude, Longitude, Zipcode, UsrID, Country) VALUES('$latitude', '$longitude','$zip','$accountnumber','country' ) ") 
or die(mysql_error());
}

if ($row){
echo $row['UserFirstName']."-".$row['UserLastName'];
}
else{
echo "no";
}
?>
