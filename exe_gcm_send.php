<?php
$accountnumber = $_POST['appkey'];
$to_facebookid = $_POST['param2'];
$message = $_POST['param3'];
$output = shell_exec('php send_gcm_message.php '.$accountnumber.' '.$to_facebookid.' '.$message);
echo "$output";
?>
