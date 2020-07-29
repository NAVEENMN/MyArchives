<?php
/*
NAME : flush_invites.php
Author : Naveen Mysore
email : navimn1991@gmail.com
desp : this sript will flush all invites
*/
include 'databaseauth.php';
include 'keys.php';
$flush = null;
$user_id = '859842507380812';
mysql_query("UPDATE accounts SET `INVITES`= '$flush' WHERE USERID='$user_id'") or die(mysql_error());


?>
