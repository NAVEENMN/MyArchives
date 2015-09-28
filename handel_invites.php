<?php

/*
NAME  : handle_invites.php
Author : Naveen Mysore
email : navimn1991@gmail.com
name:  setup_account.php
desp : This script will take operation keyword and does insert or update  to invites tables accordingly 
*/

include 'databaseauth.php';
include 'keys.php';
$operation = $_POST['param1'];
$payload = $_POST['param2'];

$json_data = json_decode($payload, true);
$user_id = $json_data['id'];
$invite_data = $json_data['data'];

if($operation == "get_list"){
	$result = mysql_query("SELECT * FROM accounts WHERE USERID = '$user_id'") or die(mysql_error());
	$row = mysql_fetch_array($result);
	if($row){
		$payload = $row['INVITES'];
		echo $payload;
	}
}

if($operation == "update"){

	mysql_query("UPDATE accounts SET `INVITES`='$invite_data' WHERE USERID='$user_id'") or die(mysql_error());
	$output = shell_exec('python manage_invites_fb.py '.$to_fb_id.' '.$invite_data['event_reference']);
	echo "updated";
}

?>
