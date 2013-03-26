<?php


//GET route definitions


$app->get('/getProjects','getProjects');
$app->get('/getProjectById/:id','getProjectById');
$app->get('/getCompleteProjectById/:id','getCompleteProjectById');
$app->get('/getProjectId/:name','getProjectId');
$app->get('/getLastProject','getLastProject');
$app->get('/getOpenEntry/:id','getOpenEntry');
$app->get('/getUser/:id','getUser');
$app->get('/cryptPassword/:pw','cryptPassword');
$app->get('/decryptPassword/:pw','decryptPassword');
$app->get('/getUserDetailsOnProject/:uid/:pid','getUserDetailsOnProject');
$app->get('/getNote/:trid','getNote');
$app->get('/getRid/:pid/:uname','getRid');
$app->get('/getLastPersonalProject','getLastPersonalProject');



//POST route definitions
$app->post("/restartLastEntryProject",function() use ($app)
{
	//pid
	$data=json_decode($app->request()->getBody());
	$uid=getUserID();

	$db=getConnection();
	
	try{
		$sqlgettrid = "SELECT trid FROM tbl_tijdregistratie 
					WHERE eind = (
					SELECT MAX( eind ) 
					FROM tbl_tijdregistratie AS tr INNER JOIN tbl_projectgebruiker AS p ON tr.puid = p.puid
					WHERE p.pid = '$data->pid' AND uid='$uid') AND trid =  '$data->trid'";

		$statement=$db->query($sqlgettrid);
		
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
			$trid=$row['trid'];
				
		$sql="UPDATE tbl_tijdregistratie SET eind='0000-00-00 00:00:00' WHERE trid='$trid'";
		$statement=$db->prepare($sql);
		$statement->execute();
		echo json_encode(array("Entries_restarted"=>$statement->rowCount()));
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
	
	
});






$app->post("/deleteEntry",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$uid=getUserID();
	
	$db=getConnection();
	
	try{
		$sql0="SELECT puid FROM tbl_tijdregistratie WHERE trid='$data->trid'";
		$statement=$db->query($sql0);
		$puid=$statement->fetchColumn();
		
		
		$sql="DELETE FROM tbl_tijdregistratie WHERE trid='$data->trid' AND puid IN(SELECT DISTINCT puid FROM tbl_projectgebruiker 
		WHERE pid=(SELECT pid FROM tbl_projectgebruiker WHERE puid='$puid')
		AND uid IN(SELECT uid FROM tbl_projectgebruiker WHERE rid>(SELECT rid FROM tbl_projectgebruiker WHERE uid='$uid'
		AND pid=(SELECT pid FROM tbl_projectgebruiker WHERE puid='$puid')
		) OR uid='$uid')
		)";
		$stmt=$db->prepare($sql);
		$db->exec($sql);
		echo json_encode(array("msg"=>"query succesful"));
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
});

$app->post("/endProject",function() use ($app)
{
	//pid,end
	$data=json_decode($app->request()->getBody());
	$puid_rid=getPUID_AND_RID($data->pid);
	$rid=$puid_rid[1];
	$db=getConnection();
	
	if($rid=="1")
	{
		try{
			$sql="UPDATE tbl_project SET einddatum='$data->end' WHERE pid='$data->pid' AND einddatum IS NULL";
			$statement=$db->prepare($sql);
			$statement->execute();
			echo json_encode(array("projects_ended"=>$statement->rowCount()));
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}
	}else{
		echo json_encode(array("error"=>"not project admin"));
	}
	
});

$app->post("/restartProject",function() use ($app)
{
	//pid
	$data=json_decode($app->request()->getBody());
	$puid_rid=getPUID_AND_RID($data->pid);
	$rid=$puid_rid[1];
	$db=getConnection();
	
	if($rid=="1")
	{
		try{
			$sql="UPDATE tbl_project SET einddatum=NULL WHERE pid='$data->pid'";
			$statement=$db->prepare($sql);
			$statement->execute();
			echo json_encode(array("projects_restarted"=>$statement->rowCount()));
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>"$ex->getMessage()"));
		}	
	}else
	{
		echo json_encode(array("error"=>"not project admin"));		
	}
	
});

$app->post("/newEntry",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$uid=getUserID();
	
	if(pidExists($data->pid)&&!getOpenEntry($data->pid)&&!getIsClosed($data->pid))
	{
	try
	{
		$db=getConnection();
		$sql1="SELECT puid FROM tbl_projectgebruiker WHERE pid='$data->pid' AND uid='$uid'";
		$sql2="INSERT INTO tbl_tijdregistratie(puid,begin,notities) VALUES (:puid,:begin,:notities)";
		
		$statement=$db->query($sql1);
		$result=$statement->fetchAll();
		
		
		$puid=$result[0][0];
		
		$statement=$db->prepare($sql2);
		$statement=$db->prepare($sql2);
		$statement->bindParam("puid",$puid);
		$statement->bindParam("begin",$data->begin);
		$statement->bindParam("notities",$data->notities);
		
		$statement->execute();
		echo json_encode(array("trid"=>$db->lastInsertId()));
	}
	catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
	}else{
		echo json_encode(array("error"=>"Project closed or still open entries"));
	}
}
);


$app->post("/closeEntry",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	//pid,eind
	$puid_rid=getPUID_AND_RID($data->pid);
	$puid=$puid_rid[0];
	$rid=$puid_rid[1];
	$con=getConnection();
	$extra="";
	if(isset($data->notities))
	{
		$extra=", notities='$data->notities'";
	}
	$sql="UPDATE tbl_tijdregistratie SET eind='$data->eind'$extra WHERE puid='$puid' AND eind='0000-00-00 00:00:00'";
	
	try{
		$rows_affected=$con->exec($sql);
		if($rows_affected>0)
		{
		echo json_encode(array("msg"=>$rows_affected." rows affected."));
		}
		else
		{
			echo json_encode(array("error"=>"Could not close entry"));
		}
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}	
);


$app->post("/manualEntry",function() use($app){
	$data=json_decode($app->request()->getBody());
	$uid=getUserID();
	
	try
	{
		$db=getConnection();
		$sql1="SELECT puid FROM tbl_projectgebruiker WHERE pid='$data->pid' AND uid='$uid'";
		$sql2="INSERT INTO tbl_tijdregistratie(puid,begin,eind,notities) VALUES (:puid,:begin,:eind,:notities)";
		
		$statement=$db->query($sql1);
		$result=$statement->fetchAll();
		$puid=$result[0][0];
		
		$statement=$db->prepare($sql2);
		$statement=$db->prepare($sql2);
		$statement->bindParam("puid",$puid);
		$statement->bindParam("begin",$data->begin);
		$statement->bindParam("eind",$data->eind);
		$statement->bindParam("notities",$data->notities);
		
		$statement->execute();
		echo json_encode(array("trid"=>$db->lastInsertId()));
	}
	catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
});
//run the services
$app->run();

//GET method functions



function getProjectId($name)
{
	$uid=getUserID();
	$sql="SELECT tbl_project.pid FROM tbl_project 
	LEFT JOIN tbl_projectgebruiker ON tbl_project.pid=tbl_projectgebruiker.pid
	LEFT JOIN tbl_tijdregistratie ON tbl_projectgebruiker.puid=tbl_tijdregistratie.puid
	WHERE tbl_projectgebruiker.uid='$uid' AND projectnaam='$name'
	ORDER BY begin ASC";
	
	try{
		$db=getConnection();
		$result=$db->query($sql);
		$pid=$result->fetchColumn();
		if($pid==null)
		{
			echo json_encode(array("error"=>"pid not found."));
		}
		else{
			echo json_encode(array("pid"=>"$pid"));
		}
		
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function getProjects()
{
	$userid=getUserID();
	$con=getConnection();
	$sql="SELECT tbl_project.*,uname FROM tbl_project LEFT JOIN tbl_gebruiker ON tbl_project.oprichterid=tbl_gebruiker.uid
	WHERE pid IN (SELECT pid FROM tbl_projectgebruiker WHERE uid =  '$userid') ORDER BY begindatum DESC";
	try
	{
		$statement=$con->query($sql);
		$arrResult=array();
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$oprichter=false;
			if($row['oprichterid']==$userid)
			{
				$oprichter=true;
			}
			$arrResult[$i]=array("pid"=>$row['pid'] ,"projectnaam"=>$row['projectnaam'],"opdrachtgever"=>$row['opdrachtgever'],
			"begindatum"=>$row['begindatum'],"einddatum"=>$row['einddatum'],"omschrijving"=>$row['omschrijving'],"isOprichter"=>$oprichter,"oprichter"=>$row['uname']);
			$i++;
		}
		
		if(empty($arrResult))
		{
			echo json_encode(array("error"=>"no projects yet."));
		}
		else
		{
			echo json_encode($arrResult);	
		}
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function getProjectById($pid)
{
	$uid=getUserID();
	$con=getConnection();
	
	if(pidExists($pid))
	{
		try
		{		
		$arrResult=array();
		$puid_rid=getPUID_AND_RID($pid);
		$puid=$puid_rid[0];
		$rid=$puid_rid[1];
		
		$sql0="SELECT projectnaam,opdrachtgever,begindatum,omschrijving,oprichterid,tbl_gebruiker.uname,einddatum FROM tbl_project 
		LEFT JOIN tbl_gebruiker ON tbl_project.oprichterid=tbl_gebruiker.uid WHERE pid='$pid'";
		$sql="SELECT trid,voornaam,achternaam,tbl_projectgebruiker.uid as uid,rid,begin,eind,notities,uname
		 FROM tbl_tijdregistratie
		  LEFT JOIN tbl_projectgebruiker ON tbl_tijdregistratie.puid=tbl_projectgebruiker.puid
		  LEFT JOIN tbl_gebruiker ON tbl_projectgebruiker.uid=tbl_gebruiker.uid 
		  WHERE tbl_tijdregistratie.puid='$puid' ORDER BY begin ASC";
		  
		$statement0=$con->query($sql0);
		$res=$statement0->fetchAll(PDO::FETCH_NUM);
		$arrResult['projectnaam']=$res[0][0];
		$arrResult['opdrachtgever']=$res[0][1];
		$arrResult['begindatum']=$res[0][2];
		$arrResult['omschrijving']=$res[0][3];
		$arrResult['oprichterid']=$res[0][4];
		$arrResult['uname']=$res[0][5];
		$arrResult['einddatum']=$res[0][6];
		
		$statement=$con->query($sql);
		$arrResult["rid"]=$rid;
		$entries=array();
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$entries[$i]=array("trid"=>$row['trid'],"voornaam"=>$row['voornaam'],"achternaam"=>$row['achternaam'],"uid"=>$row['uid'],"rid"=>$row['rid'],
			"begin"=>$row['begin'],"eind"=>$row['eind'],"notities"=>$row['notities'],"uname"=>$row['uname']);
			$i++;
		}
		$arrResult["entries"]=$entries;
		if($i != 0)
			$arrResult["lastentry"]=$entries[$i-1]['trid'];
		
		echo json_encode($arrResult);
		
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}	
	}
	else {
		echo json_encode(array("error"=>"project with pid $pid not found"));
	}
}

function getCompleteProjectById($pid)
{
		
	$uid=getUserID();
	$con=getConnection();
	
	if(pidExists($pid))
	{
	try
	{		
		$arrResult=array();
		$puid_rid=getPUID_AND_RID($pid);
		$puid=$puid_rid[0];
		$rid=$puid_rid[1];
		//todo check if user is in project
		$sql0="SELECT projectnaam,opdrachtgever,begindatum,omschrijving,oprichterid,tbl_gebruiker.uname,einddatum FROM tbl_project 
		LEFT JOIN tbl_gebruiker ON tbl_project.oprichterid=tbl_gebruiker.uid WHERE pid='$pid'";
		$sql="SELECT trid,voornaam,achternaam,tbl_projectgebruiker.uid as uid,rid,begin,eind,notities,uname
		 FROM tbl_tijdregistratie
		  LEFT JOIN tbl_projectgebruiker ON tbl_tijdregistratie.puid=tbl_projectgebruiker.puid
		  LEFT JOIN tbl_gebruiker ON tbl_projectgebruiker.uid=tbl_gebruiker.uid 
		  WHERE tbl_tijdregistratie.puid IN(SELECT puid FROM tbl_projectgebruiker WHERE pid='$pid') ORDER BY begin ASC";
		  
		$statement0=$con->query($sql0);
		$res=$statement0->fetchAll(PDO::FETCH_NUM);
		$arrResult['projectnaam']=$res[0][0];
		$arrResult['opdrachtgever']=$res[0][1];
		$arrResult['begindatum']=$res[0][2];
		$arrResult['omschrijving']=$res[0][3];
		$arrResult['oprichterid']=$res[0][4];
		$arrResult['uname']=$res[0][5];
		$arrResult['einddatum']=$res[0][6];
		
		$statement=$con->query($sql);
		$arrResult["rid"]=$rid;
		$entries=array();
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$entries[$i]=array("trid"=>$row['trid'],"voornaam"=>$row['voornaam'],"achternaam"=>$row['achternaam'],"uid"=>$row['uid'],"rid"=>$row['rid'],
			"begin"=>$row['begin'],"eind"=>$row['eind'],"notities"=>$row['notities'],"uname"=>$row['uname']);
			$i++;
		}
		$arrResult["entries"]=$entries;
		
		echo json_encode($arrResult);
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
	}
	else
	{
		echo json_encode(array("error"=>"project with pid $pid not found."));
	}
	
}




function cryptPassword($input)
{
	$con=getConnection();
	$sql1="SELECT `key` FROM encrypt WHERE eid='".md5(getUsername() . "@S0urc€F1sh!*!")."'";

	try
	{
		echo $sql1;
		$statement1=$con->query($sql1);
		$key=$statement1->fetchColumn();
		$sql2="SELECT AES_ENCRYPT('$input','$key') AS crypt";
		echo $sql2;
		$statement2=$con->query($sql2);
		$cryptpass=$statement2->fetchColumn();
		echo $cryptpass;
		return $cryptpass;
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function decryptPassword($input,$username)
{
	$con=getConnection();
	$sql1="SELECT `key` FROM encrypt WHERE eid='".md5($username . "@S0urc€F1sh!*!")."'";

	try
	{
		echo $sql1;
		$statement1=$con->query($sql1);
		$key=$statement1->fetchColumn();
		if(!$key)
		{
			return false;
		}
		echo "- key : $key -";
		$sql2="SELECT AES_DECRYPT('$input','$key') AS crypt";
		echo $sql2;
		$statement2=$con->query($sql2);
		$decryptpass=$statement2->fetchColumn();
		echo $decryptpass;
		return $decryptpass;
	}catch(PDOException $ex)
	{
		echo $ex->getMessage();
	}
}


function isUser_ProjectAdmin($pid,$uid=null)
{
	$db=getConnection();
	$sql="SELECT rid FROM tbl_projectgebruiker WHERE uid='" . (!$uid ? getUserID() : $uid) . "' AND pid='$pid'";
	
	try{
		$statement=$db->query($sql);
		$rid_result=$statement->fetch(PDO::FETCH_NUM);
		
		if($rid_result[0]=='1'||$rid_result[0]=='2')
		{
			//echo "user is rid 1 of 2";
			return true;
		}
		else {
			//echo "user is rid 3";
			return false;
		}
	}
	catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
		return false;
	}
}



function getOpenEntry($pid)
{
	$r=getPUID_AND_RID($pid);
	$puid=$r[0];
	$rid=$r[1];
	
	
	$sql="SELECT trid FROM tbl_tijdregistratie WHERE puid='$puid' AND eind='0000-00-00 00:00:00'";
	$con=getConnection();
	
	try
	{
		$statement=$con->query($sql);
		$arrTrid=array();
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			 $arrTrid["opentrid"]=$row["trid"];
		}
		if(!$arrTrid==null)
		{
		echo json_encode($arrTrid);
		}
		return $arrTrid;
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function getLastPersonalProject()
{
	$uid=getUserID();
	$sql="SELECT tbl_projectgebruiker.pid FROM tbl_projectgebruiker 
	LEFT JOIN tbl_tijdregistratie ON tbl_projectgebruiker.puid=tbl_tijdregistratie.puid 
	LEFT JOIN tbl_project ON tbl_projectgebruiker.pid=tbl_project.pid
	WHERE begin=(SELECT MAX(begin) FROM tbl_tijdregistratie WHERE puid IN(SELECT puid FROM tbl_projectgebruiker WHERE uid='$uid'))
	AND tbl_projectgebruiker.uid='$uid'";
	$sql1="SELECT tbl_project.pid FROM tbl_project 
	LEFT JOIN tbl_projectgebruiker ON tbl_project.pid=tbl_projectgebruiker.pid
	WHERE oprichterid='$uid' AND begindatum=(SELECT MAX(begindatum) FROM tbl_project WHERE oprichterid='$uid'";
	
	$con=getConnection();
	try{
		$statement=$con->query($sql);
		$arrResult=array();
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$arrResult['pid']=$row['pid'];
		}
		
		if(empty($arrResult))
		{
			$statement1=$con->query($sql1);
			$arrResult['pid']=$statement1->fetchColumn();
		}
		
		
		echo json_encode($arrResult);
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function getLastProject()
{
	$uid=getUserID();
	$sql="SELECT pid FROM tbl_projectgebruiker WHERE puid=(SELECT puid
		FROM tbl_tijdregistratie
		WHERE eind = ( 
		SELECT MAX( eind ) 
		FROM tbl_tijdregistratie
		WHERE puid IN(SELECT puid FROM tbl_projectgebruiker WHERE uid='$uid')
		 )) AND uid='$uid'";
		 
	$sql1="SELECT tbl_project.pid FROM tbl_project LEFT JOIN tbl_projectgebruiker ON tbl_project.puid=tbl_projectgebruiker.puid
	WHERE tbl_projectgebruiker.uid='$uid' AND begindatum=MAX(begindatum)";
	$con=getConnection();
	
	try{
		$statement=$con->query($sql);
		$arrResult=array();
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$arrResult['pid']=$row['pid'];
		}
		
		if(empty($arrResult))
		{
			$statement1=$con->query($sql1);
			$arrResult['pid']=$statement1->fetchColumn();
		}
		
		
		echo json_encode($arrResult);
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}

function getUserProfile($uid)
{
	if($uid=="0")
	{
		$uid=getUserID();
	}
	
	if(uidExists($uid))
	{
		$sql="SELECT uname,voornaam,achternaam FROM tbl_gebruiker where uid='$uid'";
	$con=getConnection();
		
	try{
		$statement=$con->query($sql);
		$user=array();
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$user['voornaam']=$row['voornaam'];
			$user['achternaam']=$row['achternaam'];
			$user['email']=$row['uname'];
		}
		echo json_encode($user);
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
	}
	else {
		echo json_encode(array("error"=>"user does not exist."));
	}
	
	
}

function getUser($uid)
{
	if($uid=="0")
		$uid=getUserID();	
	
	
	if(uidExists($uid))
	{
		$sql="SELECT uname, voornaam,achternaam, count(tr.trid) as totallogs, count(distinct pid) as totalprojects, 
		(SELECT SUM(UNIX_TIMESTAMP(eind)-UNIX_TIMESTAMP(`begin`)) FROM tbl_tijdregistratie WHERE eind<>'0000-00-00 00:00:00' AND
		puid IN (SELECT puid FROM tbl_projectgebruiker WHERE  uid='$uid')) as totaltime
		FROM tbl_gebruiker AS g
		LEFT JOIN tbl_projectgebruiker AS pg on pg.uid=g.uid
		LEFT JOIN tbl_tijdregistratie AS tr on pg.puid=tr.puid
		WHERE g.uid = '$uid'";
	
	$con=getConnection();
		
	try{
		$statement=$con->query($sql);
		$user=array();
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$user['uname']=$row['uname'];
			$user['voornaam']=$row['voornaam'];
			$user['achternaam']=$row['achternaam'];
			$user['totallogs']=$row['totallogs'];
			$user['totalprojects']=$row['totalprojects'];
			$user['totaltime']=$row['totaltime'];
		}
		echo json_encode($user);
		
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
	}
	else
	{
		echo json_encode(array("error"=>"user does not exist!"));
	}
	
}



function getUserDetailsOnProject($uid,$pid)
{
	$sql="SELECT SUM(UNIX_TIMESTAMP(eind)-UNIX_TIMESTAMP(`begin`)) AS tijd, COUNT(*) AS total, rid FROM tbl_tijdregistratie AS tr INNER JOIN tbl_projectgebruiker AS pg ON tr.puid = pg.puid  WHERE eind<>'0000-00-00 00:00:00' AND
	 tr.puid=(SELECT puid FROM tbl_projectgebruiker WHERE pid='$pid' AND uid='$uid')";
	 
	 try{
	 	$db=getConnection();
		$statement=$db->query($sql);		
		$result=$statement->fetchAll(PDO::FETCH_ASSOC);
		echo json_encode(array("tijd"=>$result[0]["tijd"],"total"=>$result[0]["total"], "rid"=>$result[0]["rid"]));
	 }catch(PDOException $ex)
	 {
	 	echo json_encode(array("error"=>$ex->getMessage()));
	 }
}

function getNote($trid)
{
	$sql="SELECT notities,begin,eind,uname FROM tbl_tijdregistratie LEFT JOIN tbl_projectgebruiker
	ON tbl_tijdregistratie.puid=tbl_projectgebruiker.puid 
	LEFT JOIN tbl_gebruiker ON tbl_projectgebruiker.uid=tbl_gebruiker.uid WHERE trid='$trid'";
	
	try{
		$db=getConnection();
		$statement=$db->query($sql);
		$result=$statement->fetchAll(PDO::FETCH_ASSOC);
		echo json_encode(array("Notities"=>$result[0]["notities"],"Begin"=>$result[0]["begin"],"Eind"=>$result[0]["eind"],
		"Username"=>$result[0]["uname"]));	
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}










?>