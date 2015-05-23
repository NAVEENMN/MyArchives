<?php

include 'databaseauth.php';
$facebookid = $_POST['appkey'];
$eventid = null;

mysql_query("UPDATE details SET eventid='$eventid' WHERE facebookid='$facebookid'") or die(mysql_error());
echo "ok";

?>
