<?php
$eventid = $_POST['param1'];
$choice = $_POST['param2'];
$output = shell_exec('python yelp.py '.$eventid.' '.$choice);
echo "$output";
?>
