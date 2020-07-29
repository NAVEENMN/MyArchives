<?php

include 'databaseauth.php';
include 'keys.php';
$result = mysql_query("SELECT * FROM accounts") or die(mysql_error());
while ($row = mysql_fetch_array($result)) {
    //output a row here
    // $data = json_decode($row);
   $data =  $row['PAYLOAD'];
   $content = json_decode($data);
   $fid = $content->id;
   $output = shell_exec('python add_notification.py '.$fid);
}

?>
