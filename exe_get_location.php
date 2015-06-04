<?php
$eventid = "86987987";//$_POST['param1'];
$output = shell_exec('python get_location.py ' . $eventid);
echo "$output";
?>
