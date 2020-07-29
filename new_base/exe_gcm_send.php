<?php
$accountnumber = $_POST['param1'];
$to_facebookid = $_POST['param2'];
$payload = $_POST['param3'];
/*
	payload format
	{
		host : from_d
		to_id : to_facebookid
		payload_type : invite_check, invite_accept, invite_no
				host_cancel, invite_drop
		payload : message
	}
*/
$data = json_decode($payload);
$to_id = $data -> to_id;
$host = $data -> host;
$payload_type = $data -> payload_type;
$payload_message = $data -> payload_message;
$output = shell_exec('php send_gcm_message.php '.$accountnumber.' '.$to_facebookid.' '.$payload_message);
echo "$output";
?>
