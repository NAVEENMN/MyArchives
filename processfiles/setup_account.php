<?php

include 'databaseauth.php';
include 'keys.php';

$facebookid = $_POST['appkey'];
$name = $_POST['param2'];
$email = $_POST['param3'];
$first = null;
$dum = null;
//-------------> check if that account exist
// Get a specific result from the "example" table
$result = mysql_query("SELECT * FROM details
 WHERE facebookid ='$facebookid'") or die(mysql_error());
$row = mysql_fetch_array( $result );
if($row){
	$contact = $row['contact'];
	$eventid = $row['eventid'];
	echo "ref-->".$contact."-->".$eventid."-->ref";//account exists
}else{
	mysql_query("INSERT INTO `details`( `facebookid`,`name`, `email`,`contact`) VALUES ( '$facebookid','$name','$email','$dum')") or die(mysql_error());
	echo "ref-->".$name."-->".$first."-->ref";
}

?>
