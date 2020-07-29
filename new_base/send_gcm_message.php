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
$event_reference =  $incoming_data -> event_reference;
$sender_name = $incoming_data -> sender_name;//$incoming_data -> sender_name; 
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
/*
$from_json_data = $dat['PAYLOAD'];
$from_data = json_decode($from_json_data);
$from_name = $from_data -> username;
*/
switch($type){	

	case "invite_check" :
 				echo "invite_check";
				$message = $sender_name . "--" . $event_reference;
				echo $event_reference;
				invite_check($incoming_data->host,$incoming_data->to_id, $to_gcm, "invite_check", $message
				, $sender_name, $event_reference);
				$TO = $incoming_data -> to_id;
				$Event_Name = $incoming_data -> event_name;
				$result = mysql_query("SELECT * FROM accounts WHERE USERID = '$TO'") or die(mysql_error());
				$row = mysql_fetch_array($result);
				# building a new payload
				$arr = array('from_id' => $incoming_data->host, 'from_name' => $sender_name, 'event_reference' => $event_reference
					     , 'status' => 'pending', 'event_name' => $Event_Name);
				$new_data =  json_encode($arr);
				# key and payload ready
				$ra = str_replace("-->","--",$event_reference);
				#shell_exec('python manage_invites_fb.py '.$TO.' '.$ra. ' '.$Event_Name.' '.$sender_name );
	
				break;
	case "invite_accept" :
				$message = "invite" . "--" . "accepted";
				invite_accept($incoming_data->host,$incoming_data->to_id, $to_gcm, "invite_accept",$message,
				$sender_name, $event_reference);
				break;
	case "invite_reject" :
				$message = "invite" . "--" . "rejected";
				invite_reject($incoming_data->host,$incoming_data->to_id, $to_gcm, "invite_reject", $message,
				$sender_name, $event_reference);
				break;
	case "host_cancel":
				$message = "host" . "--" . "drop";
				host_cancle($incoming_data->host,$incoming_data->to_id, $to_gcm, "host_cancel", $message,
				$sender_name, $event_reference);
				$TO = $incoming_data -> to_id;
				$ra = str_replace("-->","--",$event_reference);
				shell_exec('python handle_host_drop.py '.$TO.' '.$ra);
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
	name : host_cancel
	param: 
	return :
	desp : This function checks which type of message and build a payload for that 
*/
function host_cancle($from, $to, $togcm, $type, $message, $sender_name, $event_refrence){
$arr = array("host" => $from,
	     "to_id" => $to,
	     "payload_type" => $type,
	     "sender_name" => $sender_name,
	     "event_reference" => $event_refrence,
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);
}

/*
	name : invite_accept
	param: 
	return :
	desp : This function checks which type of message and build a payload for that 
*/
function invite_accept($from, $to, $togcm, $type, $message, $sender_name, $event_refrence){
$arr = array("host" => $from,
	     "to_id" => $to,
	     "sender_name" => $sender_name,
	     "event_reference" => $event_refrence, 
	     "payload_type" =>  "invite_accept",
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);
}

/*
	name : invite_reject
	param: 
	return :
	desp : This function checks which type of message and build a payload for that 
*/

function invite_reject($from, $to, $togcm, $type, $message, $sender_name, $event_refrence){
$arr = array("host" => $from,
	     "to_id" => $to,
	     "sender_name" => $sender_name,
	     "event_reference" => $event_refrence,
	     "payload_type" =>  "invite_reject",
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);
}

/*
	name : invite_check
	param: 
	return :
	desp : This function checks which type of message and build a payload for that 
*/

function invite_check($from, $to, $togcm, $type, $message, $sender_name, $event_refrence){
$arr = array("host" => $from,
	     "to_id" => $to,
	     "sender_name" => $sender_name,
	     "event_reference" => $event_refrence,
	     "payload_type" =>  "invite_check",
             "payload_message" => $message);
$new_payload = json_encode($arr);
send_gcm_notify($togcm, $new_payload);
}


//-------> getting who sent it
function send_gcm_notify($reg_id, $message) {
define("GOOGLE_API_KEY", "AIzaSyBsCq-AI-J0xdDK1YFQs_xOMtGayAomRt8");
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
