<?php
include 'databaseauth.php';
include 'keys.php';
$appkey = $_POST['appkey'];//thos is account number
$accountnumber = $_POST['param2'];

//if($appkey == $mykey){

$result = mysql_query("SELECT * FROM profiledata
 WHERE usrid ='$accountnumber'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );


echo $row['usrprofession']."#%-->".$row['usrworksat']."#%-->".$row['usrstayingat']."#%-->".$row['usrhometown']."#%-->".$row['usrhobbies']."#%-->".$row['usrmusic']."#%-->".$row['usrmovies']."#%-->".$row['usrbooks']."#%-->".$row['usrstatus']."#%-->".$row['age'];

//}
//else{
//echo "no";
//}
?>
