<?php

function generate_points ( $lat, $lon ) {
	$result['lat'] = $lat; //change this
	$result['lon'] = $lon; //change this
	$radius = .0002;
	$height = 400;

	$points = array();

	$i = 0;
	$k = 0;

	for ( $i = 0; $i <= 360; $i += 10, ++$k ) {
		$points[$k]['lat'] = cos( ( 2 * 3.14 * $i ) / 360 ) * $radius + $result['lat'];
		$points[$k]['lon'] = sin( ( 2 * 3.14 * $i ) / 360 ) * $radius + $result['lon'];
		
		if ( $i == 360 ) {
			++$k;
			$points[$k]['lat'] = cos( ( 2 * 3.14 * 0 ) / 360 ) * $radius + $result['lat'];
			$points[$k]['lon'] = sin( ( 2 * 3.14 * 0 ) / 360 ) * $radius + $result['lon'];
			break;
		}
	}

	$xmlstring = '';

	foreach( $points as $point ) {
		$xmlstring .= (string) ( $point['lat'] . ',' . $point['lon'] . ',' . $height . "\n" );
	}

	return $xmlstring;
}

generate_points( -122.084073, 37.430983152841 );