<?php
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
include 'databaseauth.php';
$from_accountnumber = $_POST['param1'];//who sent it
$to_facebookid = $_POST['param2'];//check to whom to send with email
$payload = $_POST['param3'];
$incoming_data = json_decode($payload);
$type = $incoming_data -> payload_type;
$result = mysql_query("SELECT * FROM accounts
 WHERE USERID ='$to_facebookid'") or die(mysql_error());
$row = mysql_fetch_array( $result );

$res = mysql_query("SELECT * FROM accounts
 WHERE USERID ='$from_accountnumber'") or die(mysql_error());
$dat = mysql_fetch_array( $res );

if($row){
$json_data = $row['PAYLOAD'];
$data = json_decode($json_data);
$to_gcm = $data -> gcm;

$from_jason_data = $dat['PAYLOAD'];
$from_data = json_decode($from_json_data);
$from_name = $from_data -> username;

switch($type){	

	case "invite_check" :
				$message = "invite from ";
				echo $from_name;
				$message = $message . $from_name;
				invite_check($incoming_data->host,$incoming_data->to_id, $to_gcm, "invite_check", $message);
				break;
	case "invite_accept" :
				invite_accept();
				break;
	case "invite_no" :
				invite_no();
				break;
	case "host_cancel":
				host_cancel();
				break;
	case "invite_drop":	
				invite_drop();
				break;
	default :
				echo "invalid request";
				break;
	
	}
}
else{
	// no user
	$v = 0;
}	

/*
	name : invite_check
	param: 
	return :
	desp : This function checks which type of message and build a payload for that 
*/
function invite_check($from, $to, $togcm, $type, $message){
$arr = array("host" => $from,
	     "to_id" => $to,
	     "payload_type" =>  $type,
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);
	
}

//-------> getting who sent it
function send_gcm_notify($reg_id, $message) {
define("GOOGLE_API_KEY", "AIzaSyAej8YahV9nPJXz-8VtzM-iI80bo2394f0");
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
