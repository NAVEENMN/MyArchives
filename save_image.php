<?php
$user_id = $_POST['param1'];
$image = $_POST['param2'];
$type = $_POST['param3'];
$file_name = $user_id . ".jpg";
file_put_contents('/var/www/html/metster/images/users/'.$file_name, $data);
?>
