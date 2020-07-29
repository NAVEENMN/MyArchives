<?php
$from = "859842507380812";
$to = "859842507380812";
$type = "invite_check";
$sender_name = "Naveen";
$event_refrence = "859842507380812-->event-->0";
$message = "hello";
$arr = array("host" => $from,
	     "to_id" => $to,
	     "payload_type" => $type,
	     "sender_name" => $sender_name,
	     "event_reference" => $event_refrence,
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);

//-------> getting who sent it
function send_gcm_notify($reg_id, $message) {
define("GOOGLE_API_KEY", "AIzaSyAcuXleZkLuYEz6vFCGKCLIBqDBYuYj2kY");
define("GOOGLE_GCM_URL", "https://android.googleapis.com/gcm/send");
	$fields = array(
            'registration_ids'  => array( $reg_id ),
            'data'              => array( "message" => $message ),
        );
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, GOOGLE_GCM_URL);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Problem occurred: ' . curl_error($ch));
        }
        curl_close($ch);
        echo $result;
}

?>
