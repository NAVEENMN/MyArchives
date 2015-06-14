<?php
//$url = 'http://server.com/path';
$url = 'http://52.8.173.36/metster/send_gcm_message.php';
$param1 = '859834507580812';
$param2 = '859834507580812';
$event_ref = '859842507380812-->event-->0';
$name = 'naveen';

$payload = array('host' => $param1, 'to_id' => $param2, 'payload_type' => 'invite_check', 'payload_message' => 'sounds_great', 'event_refrence' => $event_ref, 'sender_name' => $name );

$param3 = json_encode($payload);
$data = array('param1' => $param1, 'param2' => $param2, 'param3' =>  $param3);

// use key 'http' even if you send the request to https://...
$options = array(
    'http' => array(
        'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
        'method'  => 'POST',
        'content' => http_build_query($data),
    ),
);
$context  = stream_context_create($options);
$result = file_get_contents($url, false, $context);

var_dump($result);
?>
