<?php

/*
NAME  : setup_account.php
Author : Naveen Mysore
email : navimn1991@gmail.com
name:  setup_account.php
desp : This script will take operation keyword and does insert or update accordingly 
*/

include 'databaseauth.php';
include 'keys.php';
$operation = $_POST['param1'];
$payload = $_POST['param2'];

$json_data = json_decode($payload, true);
$user_id = $json_data['id'];

if($operation == "setup"){
	$result = mysql_query("SELECT * FROM accounts WHERE USERID = '$user_id'") or die(mysql_error());
	$row = mysql_fetch_array($result);
	if($row){
		$payload = $row['PAYLOAD'];
		echo $payload;
	}else{
		mysql_query("INSERT INTO `accounts`( `USERID`,`PAYLOAD`) VALUES ( '$user_id','$payload')") or die(mysql_error());
		echo "inserted";
	}
}

if($operation == "update"){

	mysql_query("UPDATE accounts SET `PAYLOAD`='$payload' WHERE USERID='$user_id'") or die(mysql_error());
	echo "updated";
}

?>
