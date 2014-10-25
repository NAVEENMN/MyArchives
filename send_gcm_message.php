<?php
include 'databaseauth.php';
$from_accountnumber = $_POST['appkey'];//who sent it
$to_email_as_ref = $_POST['param2'];//check to whom to send with email
$message = $_POST['param3'];
//-------> getting to gcm refrence
$result = mysql_query("SELECT * FROM Accounts WHERE EmailId = '$to_email_as_ref'") or die(mysql_error());

$row = mysql_fetch_array( $result );
$to_gcm = $row['gcmid'];
//-------> getting who sent it
$sen = mysql_query("SELECT * FROM Accounts WHERE UserId = '$from_accountnumber'") or die(mysql_error());
$from_name = $sen['UserFirstName'];
//--------

    define("GOOGLE_API_KEY", "AIzaSyDD4oVTLZlXbD7QkUrl7OAyqWRTs4C8fu4");
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
    $msg = $from_name;

    send_gcm_notify($reg_id, $msg);
