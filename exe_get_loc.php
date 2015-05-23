<?php
$eventid = $_POST['appkey'];
$output = shell_exec('python get_location.py ' . $eventid);
echo "$output";
?>
