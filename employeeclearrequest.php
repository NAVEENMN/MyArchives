<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../../assets/ico/favicon.ico">
    <link rel="stylesheet" href="../isb/css/jquery-ui.css">
    <link rel="stylesheet" href="../isb/css/application.css">
    <script src="../isb/js/jquery-1.10.2.js"></script>
    <script src="../isb/js/jquery-ui-1.10.4.custom.js"></script>
    <script>
			$(function() {
				$( "#tabs" ).tabs();
			});
    </script>
    <script>
	function popupupdatescreen(){
	window.open("http://localhost/isb/updateemployeeprofile.php","_blank","toolbar=yes, scrollbars=yes, resizable=yes, top=500, left=300, width=500, height=800");
	}    
    </script>
    <script src="../isb/js/test.js"></script>	
    <title>ISB Employee dashboard</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="justified-nav.css" rel="stylesheet">
  </head>

  <body>
<?php
include 'databaseauth.php';
$email2 = $_POST['email3'];
$cleartype = $_POST['cleartype'];
$token = $_POST['token'];
$cid = $_COOKIE["employeeid"];
$result3 = mysql_query("SELECT * FROM generalrequests
 WHERE RequestId = '$token'") or die(mysql_error());
$row3 = mysql_fetch_array( $result3);
if($cleartype == "all"){
mysql_query("TRUNCATE TABLE generalrequests") or die(mysql_error());
$errorid = 21 ;
 echo "<script> popupupdatescreen($errorid, $cid); </script>"; 
}
if($row3 == NULL)
{
	$errorid = 22 ;
 echo "<script> popupupdatescreen($errorid, $cid); </script>";
}
if($cleartype == "only"){
mysql_query("DELETE FROM generalrequests WHERE RequestId='$token'") 
or die(mysql_error());
$errorid = 20 ;
 echo "<script> popupupdatescreen($errorid, $cid); </script>";}
?>
</body>
</html>




