<?php

$con=mysqli_connect("mysql.serversfree.com", "u564549822_off", "***", "****");
// Check connection
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$user = getVariable("user");
$status = getVariable("status");
$action = getVariable("action");
$dev_tok = getVariable("dev_tok");
$msg = getVariable("msg");

if($action == "get"){
	
	$result = mysqli_query($con, "SELECT * FROM `states`");

	$arrVal = array();
	while($row = mysqli_fetch_array($result)) {
		$arrVal[] = $row;
	}

	echo json_encode($arrVal);
} else if($action == "setToken"){

	mysqli_query($con, "UPDATE `states` SET `dev_tok`='$dev_tok' WHERE `user`='$user'");

	echo "success";

} else if($action=="announce") {

	$regRes = mysqli_query($con,"SELECT `dev_tok` FROM `states`");
	$myRegArr = array();
	while($row = mysqli_fetch_array($regRes)) {
		$myRegArr[] = $row['dev_tok'];
	}
	sendNotification($myRegArr, $msg, "");
	echo "Success";

} else if($user){

	$result = mysqli_query($con, "SELECT * FROM `states` WHERE `user`='$user'");
	$resArr = mysqli_fetch_array($result);
	if($resArr['status'] != $status){
	$currentTime = localtime();
	mysqli_query($con, "UPDATE `states` SET `status`='$status' WHERE `user`='$user'");

	$regRes = mysqli_query($con,"SELECT `dev_tok` FROM `states`");
	$myRegArr = array();
	while($row = mysqli_fetch_array($regRes)) {
		$myRegArr[] = $row['dev_tok'];
	}
	$message = ucfirst($user) . '\'s ' . ($status=='0'?'left':'at') . ' the office.';
	sendNotification($myRegArr, $message, $user);
	echo "Success, User = $user, Status = $status";
	} else echo "No changes made to user $user";

} else
echo "Wrong params";

mysqli_close($con);

function getVariable($var){
	if(isset($_GET[$var]))
		$myvar=$_GET[$var];
	else
		$myvar=$_POST[$var];
	return $myvar;
}

function sendNotification($regId, $message, $user){
	// Replace with the real server API key from Google APIs
	$apiKey = "AIzaSyBPpJZL_NazwCJEJrS5Fnnvl7T1dCEq3LA";

	// Replace with the real client registration IDs
	// $registrationIDs = array( "reg id1","reg id2");

	// Message to be sent

	// Set POST variables
	$url = 'https://android.googleapis.com/gcm/send';

	$fields = array(
			'registration_ids' => $regId,
			'data' => array( "message" => $message , "user" => $user),
	);
	$headers = array(
			'Authorization: key=' . $apiKey,
			'Content-Type: application/json'
	);

	// Open connection
	$ch = curl_init();

	// Set the URL, number of POST vars, POST data
	curl_setopt( $ch, CURLOPT_URL, $url);
	curl_setopt( $ch, CURLOPT_POST, true);
	curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true);

	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode( $fields));

	// Execute post
	$result = curl_exec($ch);

	// Close connection
	curl_close($ch);
	echo $result;

}

?>
