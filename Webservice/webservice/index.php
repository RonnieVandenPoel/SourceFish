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
$app->get('/getData','getAllUserData');


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

function getAllUserData()
{
	$userid=getUserID();
	$con=getConnection();
	//$sqlprojecten="SELECT pid,puid FROM tbl_projectgebruiker WHERE uid='$userid'";
	$sqlprojectdata="SELECT DISTINCT rid,tbl_project.pid,projectnaam,opdrachtgever,begindatum,einddatum,omschrijving,uname FROM tbl_project 
	LEFT JOIN tbl_gebruiker ON oprichterid=uid LEFT JOIN tbl_projectgebruiker ON tbl_gebruiker.uid=tbl_projectgebruiker.uid
	WHERE tbl_project.pid IN(SELECT pid FROM tbl_projectgebruiker WHERE 
	uid='$userid')";
	
	$arrData=array();
	
	
	try{
		$statement=$con->query($sqlprojectdata);
		
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$rid=$arrData[$i]['rid']=$row['rid'];
			$pid=$arrData[$i]['pid']=$row['pid'];
			$arrData[$i]['projectname']=$row['projectnaam'];
			$arrData[$i]['client']=$row['opdrachtgever'];
			$arrData[$i]['startdate']=$row['begindatum'];
			$arrData[$i]['enddate']=$row['eindatum'];
			$arrData[$i]['description']=$row['omschrijving'];
			$arrData[$i]['projectowner']=$row['uname'];
			
			$users=array();
			$sqlusers="SELECT tbl_projectgebruiker.uid,rid,uname FROM tbl_projectgebruiker LEFT JOIN tbl_gebruiker 
			ON tbl_projectgebruiker.uid=tbl_gebruiker.uid WHERE pid='$pid'";
			
			$k=0;
			$statement3=$con->query($sqlusers);
			$users=array();
			while($row3=$statement3->fetch(PDO::FETCH_ASSOC))
			{
				$users[$k]['uid']=$row3['uid'];
				$users[$k]['rid']=$row3['rid'];
				$users[$k]['username']=$row['uname'];
				$k++;
			}
			$arrData[$i]['users']=$users;
			
			
			$sqlentries="SELECT trid,begin,eind,notities,uname FROM tbl_tijdregistratie INNER JOIN tbl_projectgebruiker
			 ON tbl_tijdregistratie.puid=tbl_projectgebruiker.puid INNER JOIN tbl_gebruiker 
			 ON tbl_projectgebruiker.uid=tbl_gebruiker.uid WHERE(tbl_projectgebruiker.pid='$pid')";
			$entries=array();
			$j=0;
			$statement2=$con->query($sqlentries);
			while($row2=$statement2->fetch(PDO::FETCH_ASSOC))
			{
				$entries[$j]['trid']=$row2['trid'];
				$entries[$j]['start']=$row2['begin'];
				if($row2['eind']=="0000-00-00 00:00:00")
				{
					$entries[$j]['end']=null;
				}
				else{
					$entries[$j]['end']=$row2['eind'];
				}
				
				$entries[$j]['notes']=$row2['notities'];
				$entries[$j]['entryowner']=$row2['uname'];
				$j++;
			}
			$arrData[$i]["entries"]=$entries;
			$i++;
		}
		
		echo json_encode($arrData);
	}
	catch(Exception $e)
	{
		echo json_encode(array("error"=>$e->getMessage()));
	}
	
	
}

include 'Functionality/rapportage.php';
include 'Functionality/projectmanagement.php';
include 'Functionality/teammanagement.php';

include 'Functionality/entrymanagement.php';



?>