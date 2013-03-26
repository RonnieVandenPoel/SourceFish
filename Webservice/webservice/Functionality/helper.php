<?php
//helper methods

//helper methods
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
	$ssql="SELECT 'OK' FROM tbl_gebruiker where uname='$uname'";
	
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

function getUsername()
{
	$digest = $_SERVER['PHP_AUTH_DIGEST'];
	$digestparts=explode(",",$digest);
	$digestparts=explode("\"",$digestparts[0]);
	return $digestparts[1];
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
?>