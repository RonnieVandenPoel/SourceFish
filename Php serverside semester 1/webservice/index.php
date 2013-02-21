<?php
use Slim\HttpDigestAuth;
require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();
require 'Slim/Middleware/HttpDigestAuth.php';


$app= new \Slim\Slim();
$arrAuthentication=array();
//get authentication from database
try{
	$db=getConnection();
	$query="SELECT uname,wachtwoord FROM tbl_gebruiker";
	$statement=$db->query($query);
	$result=$statement->fetchAll();
	
	
	foreach($result as $value)
	{
		$arrAuthentication[$value[0]]=$value[1];
	}
}

catch(PDOException  $ex)
{
	echo $ex->getMessage();
}


$app->add(new HttpDigestAuth($arrAuthentication));


$app->contentType("application/json");

//webservice route definitions

//welcomes the user (mainly testing purposes)
$app->get('/hello/:name','helloWorld');

//update the user using a json post, sends back the username
$app->post('/updateUser',function() use ($app)
{
	
	$username=getUsername();
	$data=json_decode($app->request()->getBody());
	try{
		$db=getConnection();
		$sql="UPDATE tbl_gebruiker SET uname=:username,email=:email,wachtwoord=:wachtwoord,voornaam=:voornaam,achternaam=:achternaam WHERE uname='$username'";
		$statement=$db->prepare($sql);
		$statement->bindParam("username",$data->username);
		$statement->bindParam("email",$data->email);
		$statement->bindParam("wachtwoord",$data->wachtwoord);
		$statement->bindParam("voornaam",$data->voornaam);
		$statement->bindParam("achternaam",$data->achternaam);
		$statement->execute();
	}catch(PDOException $ex)
	{
		echo $ex->getMessage();
	}
	
	echo json_encode(array("msg"=>"gebruiker : $username is aangepast"));	
});

$app->post('/addProject',function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	
	
	$sql1="INSERT INTO tbl_projectgebruiker(uid,rid) VALUES (:uid,'1')";
	$sql2="INSERT INTO tbl_project(projectnaam,opdrachtgever,omschrijving) VALUES(:projectnaam,:opdrachtgever,:omschrijving)";
	$sql3="UPDATE tbl_projectgebruiker SET pid=:pid WHERE puid=:puid";
	
	$db=getConnection();
	
	$uid=getUserID();
	
	try
	{	
		$statement1=$db->prepare($sql1);
		$statement1->bindParam("uid",$uid);
				
		var_dump($statement1);
		exit;
		$statement2=$db->prepare($sql2);
		$statement2->bindParam("projectnaam",$data->projectnaam);
		$statement2->bindParam("opdrachtgever",$data->opdrachtgever);
		$statement2->bindParam("omschrijving",$data->omschrijving);
		
		$statement3=$db->prepare($sql3);
		
		$db->beginTransaction();
		
		$statement1->execute();
		$puid=$db->lastInsertId();
		
		$statement2->execute();
		$pid=$db->lastInsertId();
		
		var_dump($pid);
		exit;
		
		$statement3->bindParam("pid",$pid);
		$statement3->bindParam("puid",$puid);
		$statement3->execute();
		
		$db->commit();
		echo json_encode(array("msg"=>"Gelukt!"));
	}
	catch(PDOException $ex)
	{
		$db->rollBack();
		echo json_encode(array("msg"=>$ex->getMessage()));
	}
});

$app->post("/addProjectUser",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$userIsAdmin=isUser_ProjectAdmin($data->pid);
	$db=getConnection();
	$sql="INSERT INTO tbl_projectgebruiker(uid,pid,rid) VALUES('".getUserID($data->username)."','$data->pid',2)";
	
	try
	{
		$db->exec($sql);
		echo json_encode(array("msg"=>"gebruiker toegevoegd"));	
	}catch(PDOException $ex)
	{
		echo json_encode(array("msg"=>$ex->getMessage()));
	}
}
);

//run the services
$app->run();

//functions
function helloWorld($name)
{
	$arrMsg=array("msg"=>"hello $name");
	echo json_encode($arrMsg);
}

function getConnection()
{
	
	$dbhost='localhost';
	$user='root';
	$pw='root';
	$dbname='sourcefish';
	$dbh=new PDO("mysql:host=$dbhost;dbname=$dbname",$user,$pw);
	$dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	return $dbh;
}

function getUserID($username=null)
{
	if($username==null)
	{
	$username=getUsername();
	}
	$sql="SELECT uid FROM tbl_gebruiker WHERE uname='$username'";
	$db=getConnection();
	try
	{
		$statement=$db->query($sql);
		$uid_result=$statement->fetch(PDO::FETCH_NUM);
		return $uid_result[0];
	}catch(PDOException $ex)
	{
		echo $ex->getMessage();
	}	
}

function getUsername()
{
	$digest = $_SERVER['PHP_AUTH_DIGEST'];
	$digestparts=explode(",",$digest);
	$digestparts=explode("\"",$digestparts[0]);
	return $digestparts[1];
}

function isUser_ProjectAdmin($pid)
{
	$db=getConnection();
	$sql="SELECT rid FROM tbl_projectgebruiker WHERE uid='".getUserID()."' AND pid='$pid'";
	try{
	$statement=$db->query($sql);
	$rid_result=$statement->fetch(PDO::FETCH_NUM);
		if($rid_result[0]=='1')
		{
			return true;
		}
		else {
			return false;
		}
	}
	catch(PDOException $ex)
	{
		echo json_encode(array("msg"=>$ex->getMessage()));
		return false;
	}
}

?>