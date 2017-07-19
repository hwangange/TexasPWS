<?php
	require_once 'connection.php';
 
	class importKML {
 
		private $db;
		private $connection;
 
		function __construct(){
			$this->db = new DB_Connection();
			$this->connection = $this->db->get_connection();
		}
 
		public function import(){
 
				$query = "LOAD DATA INFILE 'texas_violation_1.txt' INTO TABLE water";
				$result = mysqli_query($this->connection, $query);
				echo $result;
                                mysqli_close($this->connection);
 
 
 
		} 
 
 
	}
 
	$importKML = new importKML();
	$data = array();	
	$importKML -> import();
 
?>