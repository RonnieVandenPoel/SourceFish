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
	$data=json_decode($body);
	$sql="INSERT INTO tbl_gebruiker(uname,email,wachtwoord,voornaam,achternaam) VALUES (:uname,:email,:wachtwoord,:voornaam,:achternaam)";

	try{
		$db=getConnection();
		$stmt=$db->prepare($sql);
		$stmt->bindParam("uname",$data->username);
		$stmt->bindParam("email",$data->email);
		$stmt->bindParam("wachtwoord",$data->wachtwoord);
		$stmt->bindParam("voornaam",$data->voornaam);
		$stmt->bindParam("achternaam",$data->achternaam);
		
		$stmt->execute();
		$db=null;
		$msg="A user account for you has been created, use your emailaddress $data->email to login with the temporary password $data->wachtwoord";
		mail($data->email,"An account has been made for you, with username = $data->username!",$msg,"Registration service<registration@projecten3.eu5.org\r\n");
		echo json_encode($data);
		
	}catch(PDOException $ex)
	{
		echo $ex->getMessage();
	}
	
});



$app->run();

function tryConnect()
{
	echo json_encode(array("msg"=>"OK"));
}


function getConnection()
{
	
	$dbhost='localhost';
	$user='189356';
	$pw='S0urcef1sh';
	$dbname='189356';
	$dbh=new PDO("mysql:host=$dbhost;dbname=$dbname",$user,$pw);
	$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	return $dbh;
	
}




?>