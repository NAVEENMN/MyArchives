<?php
$eventid = $_POST['param1'];
$choice = $_POST['param2'];
$output = shell_exec('python get_yelp_ranking.py '.$eventid.' '.$choice);
echo "$output";
?>
