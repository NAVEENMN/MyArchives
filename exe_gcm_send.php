<?php
$accountnumber = $_POST['appkey'];
$to_email = $_POST['param2'];
$output = shell_exec('php send_gcm_message.php '.$accountnumber.' '.$to_email);
echo "<pre>$output</pre>";
?>
