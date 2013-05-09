<?php

require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();



$app= new \Slim\Slim();




$app->contentType("application/json");
$app->get('/tryConnect','tryConnect');

function unique_salt()
{
    return substr(sha1(mt_rand()),0,22);
}

function goodhash($password)
{
    return crypt($password,'$2a$10$'.unique_salt());
}

$app->post('/registerUser',function() use ($app)
{
	$request = $app->request();
	$body=$request->getBody();
	
	echo $body;
	
	$data=json_decode($body);
	
	/*$key=md5(microtime());
	$eid=md5($data->username . "@S0urc3F1sh!*!");
	$sql0="SELECT AES_ENCRYPT('$data->password','$key') AS crypt";*/

	//echo $sql0;
	$db=getConnection();
	//echo "- 1 -";
	try{

		$sql0="SELECT 'Username/email al in gebruik' FROM tbl_gebruiker WHERE LOWER(uname)=LOWER('$data->username')";
		$sql1="INSERT INTO tbl_gebruiker(`uname`,`wachtwoord`) VALUES ('$data->username','".goodhash($data->password)."')";

		
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
		//$db->exec($sql2);
		//echo $sql2;
		//echo "6";
		//$msg="A user account for you has been created, use your username $data->username to login!";
		//mail($data->username,"An account has been made for you, with username = $data->username!",$msg,"Registration service<registration@projecten3.eu5.org\r\n");
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
	
	$dbhost='localhost';
	$user='root';
	$pw='S0urcef1sh';
	$dbname='projecten';
	$dbh=new PDO("mysql:host=$dbhost;dbname=$dbname",$user,$pw);
	$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	return $dbh;
	
}

?>