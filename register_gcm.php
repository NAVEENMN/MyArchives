<?php

include 'databaseauth.php';
$accountnumber = $_POST['appkey'];
$gcmreg = $_POST['param2'];

mysql_query("UPDATE `Accounts` SET gcmid=$gcmreg WHERE UserId=$accountnumber");


?>
