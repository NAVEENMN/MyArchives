<?php session_start(); ?>
<html><body>
<?php
include 'databaseauth.php';
$email = mysql_real_escape_string($_POST['email']);
$password = $_POST['password'];
$urlerror = 'http://localhost/isb/error.html';

//get userId and customerId
$result1 = mysql_query("SELECT * FROM employee
 WHERE EmailId = '$email'") or die(mysql_error());
$row1 = mysql_fetch_array( $result1 );
$userid = $row1['UserId'];
$customID = $row1['EmployeeId'];
// check password based on userId
$result2 = mysql_query("SELECT * FROM UserId
 WHERE UserId= '$userid'") or die(mysql_error());  
// get the first (and hopefully only) entry from the result
$row2 = mysql_fetch_array( $result2 );

if($row2['LoginPassword'] == $password ){
//--**************************************** Change login status to 1
//--**************************************** Change login status to 1
$_SESSION['loggedin'] = "yes";
if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
    $ip = $_SERVER['HTTP_CLIENT_IP'];
} elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
    $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
} else {
    $ip = $_SERVER['REMOTE_ADDR'];
}
$LoginStatus = "yes";
$token = rand(1000, 10000);
$key = $userid.$token;
setcookie("user", "$userid", time()+3600);
setcookie("employeeid", "$customID", time()+3600);
setcookie("isbtokenkey", "$key", time()+3600);

mysql_query("INSERT INTO isb.`Login`( `User`,`TokenKey`, `LoginStatus`, `IP`) 
			VALUES ( '$userid','$key','$LoginStatus','$ip')")
or die(mysql_error());

//-------------------------------------------------------------------

$lastlogindate = date("Y/m/d");
$lastdatemodified = date("Y/m/d");



// update the LastLoginDate in UserRoleId table in database 

mysql_query("UPDATE UserId SET LastLoginDate = '$lastlogindate'
 WHERE UserId = '$userid'") 
or die(mysql_error());


// update the LastDateModified in employee table in database
mysql_query("UPDATE employee SET LastDateModified = '$lastdatemodified'
 WHERE EmailId = '$email'") 
or die(mysql_error());


//-------------------------------------------------------------------

$urlwelcomeback = 'http://localhost/isb/employeedashboard.php?ref='.$customID;
header( "Location: $urlwelcomeback" );
}

//http://localhost/we/landing-page/accounts/mick75/profile.php
else{

header( "Location: $urlerror" );
}

?>
</body></html>
