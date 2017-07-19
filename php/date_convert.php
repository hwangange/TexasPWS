

<?php
$i = 8;
if($i !=8) {
	$date=date_create("31-Dec-96");
$date = date_format($date,"Y-m-d");
echo $date;
//31-Dec-06
}

else {echo "they r equal";}
?>