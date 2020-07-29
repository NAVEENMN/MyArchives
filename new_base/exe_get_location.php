<?php
$eventid = $_POST['param1'];
$eventid = str_replace('-->', '--', $eventid);
$output = shell_exec('python location_driver.py ' . $eventid);
echo "$output";
?>
