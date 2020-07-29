<?php
ob_start();
$year = $_POST['year'];
$university = $_POST['university'];

echo $year;
echo $university;

$URL = "https://docs.zoho.com/sheet/view.do?url=http://54.183.113.236/excel/dd/files/".$year."/CA_".$year."_".$university.".xlsx&name=M";
header( "Location: $URL" );

?>
