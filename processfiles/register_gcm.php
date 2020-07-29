<?php

include 'databaseauth.php';
$facebookid = $_POST['appkey'];
$gcmreg = $_POST['param2'];

mysql_query("UPDATE details SET gcm='$gcmreg' WHERE facebookid='$facebookid'") or die(mysql_error());
echo "ok";

?>
