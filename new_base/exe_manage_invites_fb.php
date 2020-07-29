<?php
	$event_ref = "119979305002439--event--4";//$_POST['param1'];
	$to_fb_id = "859842507380812";//$_POST['param2'];
	$output = shell_exec('python manage_invites_fb.py '.$to_fb_id.' '.$event_ref);
	echo "$output";
?>
