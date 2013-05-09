<?php
$host="localhost";
use Slim\HttpDigestAuth;
require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();
require 'Slim/Middleware/HttpDigestAuth.php';

$app=new \Slim\Slim();

//authentication
$arrAuthentication=array();
//get authentication from database

function check_password($hash, $password) {

    $full_salt = substr($hash, 0, 29);

    $new_hash = crypt($password, $full_salt);

    return ($hash == $new_hash);

}

function getUserIdFromCredentials($username,$password)
{
try{
	$db=getConnection();
	$query="SELECT wachtwoord
	FROM tbl_gebruiker WHERE lower(uname)=lower('$username')";
	$statement=$db->query($query);
	$hash=$statement->fetchColumn();

    if(check_password($hash,$password))
    {
        $userid=$username;
    }

}

catch(PDOException  $ex)
{
	echo json_encode(array("error"=>$ex->getMessage()));
}
    return $userid;
}


$authenticateFromCredentials = function () use ($app) {
    return function ($route) use ($app) {

        $username = $app->request()->headers('PHP_AUTH_USER');
        $password = $app->request()->headers('PHP_AUTH_PW');

        if (!isset($username) || !isset($password)) {
            $app->response()->header('WWW-Authenticate', 'Basic realm="User specific Area"');
            $app->halt(401, "No crendentials supplied!");
        }

        $userId = getUserIdFromCredentials($username, $password);

        if ($userId === FALSE) {
            $app->response()->header('WWW-Authenticate', 'Basic realm="User specific Area"');
            $app->halt(401, "Invalid login");
        }

        $routeParams = $route->getParams();

        if (!is_array($routeParams)) {
            $routeParams = array();
        }

        array_push($routeParams, $userId);  // <== could load the user object from the DB (or cache)

        $route->setParams($routeParams);
    };
};

$app->get('/tryLogin',$authenticateFromCredentials(),'tryLogin');
$app->get('/getData',$authenticateFromCredentials(),'getAllUserData');


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

function getUsername()
{

        return $_SERVER['PHP_AUTH_USER'];
};

function getAllUserData()
{
	$userid=getUserID();
	$con=getConnection();
	//$sqlprojecten="SELECT pid,puid FROM tbl_projectgebruiker WHERE uid='$userid'";
	$sqlprojectdata="SELECT DISTINCT tbl_project.pid,projectnaam,opdrachtgever,begindatum,einddatum,omschrijving,uname FROM tbl_project 
	LEFT JOIN tbl_gebruiker ON oprichterid=uid LEFT JOIN tbl_projectgebruiker ON tbl_gebruiker.uid=tbl_projectgebruiker.uid
	WHERE tbl_project.pid IN(SELECT pid FROM tbl_projectgebruiker WHERE 
	uid='$userid')";
	
	$arrData=array();
	
	
	try{
		$statement=$con->query($sqlprojectdata);
		
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			
			$pid=$arrData[$i]['pid']=$row['pid'];
			$arrData[$i]['rid']=getRid($pid,getUsername());
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
				$users[$k]['username']=$row3['uname'];
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

//helper methods

//helper methods
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

function pidExists($pid)
{
    $sql="SELECT 'OK' FROM tbl_project WHERE pid='$pid'";
    $db=getConnection();

    try{
        if($db->query($sql)->fetchColumn()=='OK'){
            return true;
        }
        else{
            return false;
        }
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>"problem checking if project excists"));
    }
}

function uidExists($uid)
{
    $sql="SELECT 'OK' FROM tbl_gebruiker where uid='$uid'";

    $db=getConnection();

    try{
        $statement=$db->query($sql);
        if($statement->fetchColumn()=='OK'){
            return true;
        }
        else{
            return false;
        }
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>"problem checking if uid excists"));
        //echo json_encode(array("error"=>$ex->getMessage()));
    }
}


function unameExists($uname)
{
    $sql="SELECT 'OK' FROM tbl_gebruiker where uname='$uname'";

    $db=getConnection();

    try{
        if($db->query($sql)->fetchColumn()=='OK'){
            return true;
        }
        else{
            return false;
        }
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>"problem checking if uid excists"));
    }
}

function getUserID($username=null)
{
    if($username==null)
        $username=getUsername();

    $sql="SELECT uid FROM tbl_gebruiker WHERE LOWER(uname)=LOWER('$username')";
    $db=getConnection();

    try
    {
        $statement=$db->query($sql);
        $uid_result=$statement->fetch(PDO::FETCH_NUM);
        return $uid_result[0];
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>$ex->getMessage()));
    }
}


function isUser_inProject($pid,$uid=null,$direct=null)
{
    $db=getConnection();
    $sql="SELECT rid FROM tbl_projectgebruiker WHERE uid='" . (!$uid ? getUserID() : $uid) . "' AND pid='$pid'";

    try{
        $statement=$db->query($sql);
        $rid_result=$statement->fetch(PDO::FETCH_NUM);
        if($direct==1)
            echo json_encode(array("result"=>$rid_result[0]));
        else
            return isset($rid_result[0]);
    }
    catch(PDOException $ex)
    {
        echo json_encode(array("error"=>$ex->getMessage()));
        return false;
    }
}

function getPUID_AND_RID($pid)
{
    $uid=getUserID();
    $sql="SELECT rid,puid FROM tbl_projectgebruiker WHERE uid='".$uid."' AND pid='$pid'";
    $con=getConnection();
    try{
        $statement=$con->query($sql);
        $result=$statement->fetchAll();
        $rid=$result[0][0];
        $puid=$result[0][1];
        return array($puid,$rid);
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>$ex->getMessage()));
    }
}

function getRid($pid,$uname)
{
    $sql="SELECT rid FROM tbl_projectgebruiker WHERE uid='".getUserId($uname)."' AND pid='$pid'";
    $con=getConnection();
    try{
        $statement=$con->query($sql);
        $result=$statement->fetchAll();
        $rid=$result[0][0];
        //echo json_encode(array("rid"=>$rid));
        return $rid;
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>$ex->getMessage()));
    }

}

function getPUID_AND_RID_byUser($pid,$uid)
{
    $sql="SELECT rid,puid FROM tbl_projectgebruiker WHERE uid='".$uid."' AND pid='$pid'";
    $con=getConnection();
    try{
        $statement=$con->query($sql);
        $result=$statement->fetchAll();
        $rid=$result[0][0];
        $puid=$result[0][1];
        return array($puid,$rid);
    }catch(PDOException $ex)
    {
        echo json_encode(array("error"=>$ex->getMessage()));
    }
}

function getIsClosed($pid)
{
    $sql="SELECT pid FROM tbl_project WHERE pid='$pid' AND einddatum IS NOT NULL";
    $db=getConnection();

    try{
        $statement=$db->prepare($sql);
        $statement->execute();
        return $statement->rowCount()>0;
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