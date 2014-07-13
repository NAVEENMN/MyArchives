<?php
include 'databaseauth.php';
include 'keys.php';
$appkey = $_POST['appkey'];//thos is account number
$accountnumber = $_POST['param2'];

if($appkey == $mykey){

// FirstName-LastName-Image-Gender-Age-Status-Profession-worksat-CurrentCity-hometown-hobbies-music-movies-books-aboutstatus

//---------------- Get data from Accounts

$result = mysql_query("SELECT * FROM profiledata
 WHERE usrid ='$accountnumber'") or die(mysql_error()); 
$row = mysql_fetch_array( $result );

$result2 = mysql_query("SELECT * FROM Accounts
 WHERE UserId ='$accountnumber'") or die(mysql_error()); 
$row2 = mysql_fetch_array( $result2 );

$loc = mysql_query("SELECT * FROM Location
 WHERE UsrID ='$accountnumber'") or die(mysql_error());
$locat = mysql_fetch_array( $loc );

//---------------------------------------------

$FirstName = $row2['UserFirstName'];
$LastName = $row2['UserLastName'];
$Image = $row2['image'];
$Gender = $row2['UserGender'];
$Age = $row['age'];
$Status = $locat['Status'];
$Profession = $row['usrprofession'];
$WorksAt = $row['usrworksat'] ;
$CurrentyCity = $row['usrstayingat'] ;
$Hometown = $row['usrhometown'];
$Hobbies = $row['usrhobbies'];
$Music = $row['usrmusic'];
$Movies = $row['usrmovies'];
$Books = $row['usrbooks'];
$AboutStatus = $row['usrstatus'];
$Passion = $row['passion'];
$latitude = $locat['Latitude'];
$longitude = $locat['Longitude'];
//---------------------------------------------


echo $FirstName."#%-->".$LastName."#%-->".$Image."#%-->".$Gender."#%-->".$Age."#%-->".$Status."#%-->".$Profession."#%-->".$WorksAt."#%-->".$CurrentyCity."#%-->".$Hometown."#%-->".$Hobbies."#%-->".$Music."#%-->".$Movies."#%-->".$Books."#%-->".$AboutStatus."#%-->".$Passion."#%-->".$latitude."#%-->".$longitude;


}
else{
echo "no";
}
?>
