<?php
ob_start();
$email = $_POST["email"];
$password = $_POST["password"];
$home = 'http://54.183.113.236/excel/dd/login_page.php';
$dashboard = 'http://54.183.113.236/excel/dd/dashboard.php';
if($email == "ar@uncc.edu" and $password == "ar123"){
	header('Location: http://54.183.113.236/excel/dd/dashboard.php');
		ob_end_flush();
}else{
	header('Location: http://54.183.113.236/excel/dd/login_page.php');
	ob_end_flush();
}

?>
