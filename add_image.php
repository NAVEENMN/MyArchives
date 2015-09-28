/*
Name : add_image.php
Author : Naveen Mysore
email : navimn1991@gmail.com
desp : this scripts add images to the db
*/

include 'databaseauth.php'
include 'keys.php'
$user_id = $_POST['param1'];
$image = $_POST['param2'];

$json_data = json_decode($payload, true);
$user_id = $json_data['id'];
$image = $json_data['image'];

mysql_query("UPDATE accounts SET `IMAGES`='$image' WHERE USERID='$user_id'") or die(mysql_error());
echo "updated";
