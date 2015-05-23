<?php
include 'databaseauth.php';
$from_accountnumber = $argv[1];//$_POST['appkey'];//who sent it
$to_facebookid = $argv[2];//$_POST['param2'];//check to whom to send with email
$message = $argv[3];
//$message = $_POST['param3'];
//-------> getting to gcm refrence
$result = mysql_query("SELECT * FROM details
 WHERE facebookid ='$to_facebookid'") or die(mysql_error());
$row = mysql_fetch_array( $result );
if($row){
$to_gcm = $row['gcm'];
//-------> getting who sent it
$res = mysql_query("SELECT * FROM details
 WHERE facebookid ='$from_accountnumber'") or die(mysql_error());
$dat = mysql_fetch_array( $res );
$sender_name  = $dat['name'];

    define("GOOGLE_API_KEY", "AIzaSyBfCAM-iEN1R2gYLuAIbeqpRRy0rnFjmhs");
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
