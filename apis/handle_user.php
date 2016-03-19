<?php
/*
NAME  : handle_user.php
Author : Naveen Mysore
email : navimn1991@gmail.com
desp : This script will take operation keyword and does insert or update accordingly
*/

/*
payload:
id :
name :
email:
devicetoken :
 */
ob_start();
include 'keys.php';
$operation = $_POST['operation'];
$key = $_POST['key'];
$payload = json_encode($_POST['payload']);
passthru('/usr/bin/python2.7 /var/www/html/metster/apis/pf1123py/ntp_handle_user.py '. $operation.' '. $payload);
$output = ob_get_clean();
echo $output;
?>
