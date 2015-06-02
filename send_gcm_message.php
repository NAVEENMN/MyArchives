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
$from_accountnumber = $argv[1];//$_POST['appkey'];//who sent it
$to_facebookid = $argv[2];//$_POST['param2'];//check to whom to send with email
$message = $argv[3];
/*
	$data = json_decode($payload)
	type = $data -> payload_type
	switch(type){	

	case "invite_check" :
				invite_check();
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
				something_wrong();
				break;
	
	}	
*/

//$message = $_POST['param3'];
//-------> getting to gcm refrence
$result = mysql_query("SELECT * FROM accounts
 WHERE USERID ='$to_facebookid'") or die(mysql_error());
$row = mysql_fetch_array( $result );
$json_data = $row['PAYLOAD'];
$data = json_decode($json_data);
if($row){
$to_gcm = $data -> gcm;
//-------> getting who sent it
$res = mysql_query("SELECT * FROM accounts
 WHERE USERID ='$from_accountnumber'") or die(mysql_error());
$raw = mysql_fetch_array( $res );
$json_data = $raw['PAYLOAD'];
$dat = json_decode($json_data);
$sender_name  = $dat -> username;

    define("GOOGLE_API_KEY", "AIzaSyAej8YahV9nPJXz-8VtzM-iI80bo2394f0");
    define("GOOGLE_GCM_URL", "https://android.googleapis.com/gcm/send");
    function send_gcm_notify($reg_id, $message) {
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
    $reg_id = $to_gcm;
    $msg = $sender_name."-#>".$message."-#>".$from_accountnumber;
    send_gcm_notify($reg_id, $msg);
}else{
	echo "doesnot-exist";
}
?>
