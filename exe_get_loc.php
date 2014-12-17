<?php
$eventid = "event-859842507380812";#$_POST['appkey'];
$output = shell_exec('python get_loc.py ' . $eventid);
echo "<pre>$eventid</pre>";
?>
