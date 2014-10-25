<?php

include 'databaseauth.php';
include 'keys.php';
$appkey = $_POST['appkey'];
$accountnumber = $_POST['param2'];
//----------------------------------------
//if($appkey == $mykey){

	mysql_query("DELETE FROM Location WHERE UsrID = '$accountnumber'  ");

//}
//else{

//	echo "noaccess";

//----------------------------------------
?>
