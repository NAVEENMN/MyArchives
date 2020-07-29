<?php
//---------------------------
include 'databaseauth.php';
include 'keys.php';
$appkey = $_POST['appkey'];
$email = $_POST['param2'];
$password = $_POST['param3'];
//---------------------------
$result = mysql_query("SELECT * FROM Accounts
 WHERE EmailId = '$email'") or die(mysql_error());
$row = mysql_fetch_array( $result );

if($appkey == $mykey){
	if($row){ // then email exists
        	$Password = $row['LoginPassword'];// get the password
        	if($password == $Password){//good for login
        		$accountnumber = $row['UserId'];
        		$TokenKey = $row['TokenKey'];
        		$Image = $row['image'];
        		echo $accountnumber."#%-->".$TokenKey."#%-->".$Image;
  		}
	else{//invalid password
			echo "invalidpassword";	
	}
	}
	else{
		echo "noemail";
	}	 
}
else{
echo "noaccess";
}
?>
