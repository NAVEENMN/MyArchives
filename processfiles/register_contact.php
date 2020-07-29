<?php

include 'databaseauth.php';
$facebookid = $_POST['appkey'];
$contact = $_POST['param2'];

mysql_query("UPDATE details SET contact='$contact' WHERE facebookid='$facebookid'") or die(mysql_error());
echo "ok";

?>
