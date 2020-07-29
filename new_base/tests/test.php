<?php

// This is the data you want to pass to Python
$rr = array('k' => 1, 'b' => 2, 'c' => 3, 'd' => 4, 'e' => 5,'unique' => true);
//$data = $_POST['param1'];
$json_data = json_encode($rr);//json_decode($data);
// Execute the python script with the JSON data
$result = shell_exec('python mndb.py ' . escapeshellarg($json_data));
// Decode the result
$resultData = json_decode($result, true);

// This will contain: array('status' => 'Yes!')
$response = var_dump($resultData);

?>
