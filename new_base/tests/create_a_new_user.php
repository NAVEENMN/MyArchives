<?php

include 'databaseauth.php';
include 'keys.php';

$user_id = "812365407380812";
$user_name = "Steve Woz";
$email = "woz@gmail.com";
$gcm = "APA91bHwra4VsamlqTPO-d-1H92jOX8hFyBPYUIgB-lU2kreyujLYTdMCU64tt2lhRUX6wpb_XiOuFJvX_GeefQFdEiZrGbV-CrX47aRc9NKJd9HfPn6stATID5-nl4CHV4UW9bQ-ytk8FCEBPe46twXOqywyq_NPA";

$arr = array('id' => $user_id, 
	     'username' => $user_name, 
	     'email' => $email, 
	     'gcm' => $gcm);
$payload = json_encode($arr);

$result = mysql_query("SELECT * FROM accounts WHERE USERID = '$user_id'") or die(mysql_error());
$row = mysql_fetch_array($result);
if($row){
	$payload = $row['PAYLOAD'];
	echo $payload;
}else{
	mysql_query("INSERT INTO `accounts`( `USERID`,`PAYLOAD`) VALUES ( '$user_id','$payload')") or die(mysql_error());
	echo "inserted a new member";
}


?>
