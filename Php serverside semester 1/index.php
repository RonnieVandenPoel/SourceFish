<?php
$host="localhost";
use Slim\HttpDigestAuth;
require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();
require 'Slim/Middleware/HttpDigestAuth.php';

$app=new \Slim\Slim();

include 'Functionality/helper.php';
//authentication
$arrAuthentication=array();
//get authentication from database
try{
	$db=getConnection();
	$query="SELECT LOWER(uname), AES_DECRYPT( wachtwoord,  `key` ) 
	FROM tbl_gebruiker, encrypt
	WHERE eid = MD5( CONCAT( uname,  '@S0urc3F1sh!*!' ) ) ";
	$statement=$db->query($query);
	$result=$statement->fetchAll();
	
	foreach($result as $value)
	{
		$arrAuthentication[$value[0]]=$value[1];	
	}
}

catch(PDOException  $ex)
{
	echo json_encode(array("error"=>$ex->getMessage()));
}


$app->add(new HttpDigestAuth($arrAuthentication));

$app->get('/tryLogin','tryLogin');


$app->contentType("application/json");
function tryLogin()
{
	$sql="SELECT uname FROM tbl_gebruiker WHERE LOWER(uname)='".getUsername()."'";
	$con=getConnection();
	
	try{
		$result=$con->query($sql);
		echo json_encode(array("username"=>$result->fetchColumn()));	
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}


include 'Functionality/rapportage.php';
include 'Functionality/projectmanagement.php';
include 'Functionality/teammanagement.php';

include 'Functionality/entrymanagement.php';



?>