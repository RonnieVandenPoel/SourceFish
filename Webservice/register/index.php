<?php

require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();



$app= new \Slim\Slim();




$app->contentType("application/json");
$app->get('/tryConnect','tryConnect');
$app->post('/registerUser',function() use ($app)
{
	$request = $app->request();
	$body=$request->getBody();
	
	echo $body;
	
	$data=json_decode($body);
	
	$key=md5(microtime());
	$eid=md5($data->username . "@S0urc3F1sh!*!");
	$sql0="SELECT AES_ENCRYPT('$data->password','$key') AS crypt";
	
	//echo $sql0;
	$db=getConnection();
	//echo "- 1 -";
	try{
		
		$stmt0=$db->query($sql0);
		$pass=$stmt0->fetchColumn();
		//echo $pass;
		//$pass=str_replace("'", "''", $pass);
		
		echo "email".$data->username;
		echo "passwoord".$data->password;
		
		$sql0="SELECT 'Username/email al in gebruik' FROM tbl_gebruiker WHERE LOWER(uname)=LOWER('$data->username')";
		//$sql1="INSERT INTO tbl_gebruiker(`uname`,`email`,`wachtwoord`,`voornaam`,`achternaam`) VALUES ('$data->username','$data->email','$pass','$data->firstname','$data->lastname')";
		$sql1="INSERT INTO tbl_gebruiker(`uname`,`wachtwoord`) VALUES ('$data->username','$pass')";
		$sql2="INSERT INTO encrypt(`eid`,`key`) VALUES ('$eid','$key')";
		/*echo "eid:$eid, key:$key";
		echo $sql1;
		echo "2";*/
		
		$result=$db->query($sql0)->fetchColumn();
		
		if($result)
		{
			echo json_encode(array("error"=>$result));
		}
		else
		{
		$db->beginTransaction();
		/*echo "3";
		
		echo "4";*/
		$db->exec($sql1);
		//echo "5";
		$db->exec($sql2);
		//echo $sql2;
		//echo "6";
		$msg="A user account for you has been created, use your username $data->username to login!";
		mail($data->username,"An account has been made for you, with username = $data->username!",$msg,"Registration service<registration@projecten3.eu5.org\r\n");
		//echo json_encode($data);
		//echo "7";		
		$db->commit();
		//echo "8";	
		}
		
	}catch(PDOException $ex)
	{
		echo "error";
		$db->rollBack();
		echo json_encode(array("error"=>$ex->getMessage()));
	}
});

//$app->get("/getAuth","getAuth");




$app->run();
function getAuth()
{
try{
	$db=getConnection();
	echo $query="SELECT uname, AES_DECRYPT( wachtwoord,  `key` ) 
	FROM tbl_gebruiker, encrypt
	WHERE eid = MD5( CONCAT( uname,  '@S0urc3F1sh!*!' ) ) ";
	$statement=$db->query($query);
	$result=$statement->fetchAll();
	
	foreach($result as $value)
	{
		$arrAuthentication[$value[0]]=$value[1];
		print_r($value);
		
	}
	print_r($arrAuthentication);
	}catch(exception $ex)
	{
		echo $ex->getMessage();
	}
}


function tryConnect()
{
	echo json_encode(array("msg"=>"OK"));
}


function getConnection()
{
	
	$dbhost='maartendr.be.mysql';
	$user='maartendr_be';
	$pw='S0urcef1sh';
	$dbname='maartendr_be';
	$dbh=new PDO("mysql:host=$dbhost;dbname=$dbname",$user,$pw);
	$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	return $dbh;
	
}

?>