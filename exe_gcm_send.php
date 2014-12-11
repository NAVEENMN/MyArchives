<?php
$accountnumber = $_POST['appkey'];
$to_contactnumber = $_POST['param2'];
$message = $_POST['param3'];
$output = shell_exec('php send_gcm_message.php '.$accountnumber.' '.$to_contactnumber.' '.$message);
echo "<pre>$output</pre>";
?>
