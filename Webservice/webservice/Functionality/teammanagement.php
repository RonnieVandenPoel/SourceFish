<?php
//team specific functions adding, removing, promoting, demoting

$app->get('/getUsersInProject/:id','getUsersInProject');
$app->get('/getUsersOutProject/:id','getUsersOutProject');
$app->get('/isUser_inProject/:uid/:pid/:direct','isUser_inProject');

//add a user to a project, gives confirmation or error message
$app->post("/addProjectUser",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$db=getConnection();
	$userid=getUserID($data->username);
	if(isUser_ProjectAdmin($data->pid)&&$userid)
	{
		$sql="INSERT INTO tbl_projectgebruiker(uid,pid,rid) VALUES('".$userid."','$data->pid',3)";
	
		try
		{
			$db->exec($sql);
			echo json_encode(array("msg"=>"user added"));	
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}
	}
	else
	{
		echo json_encode(array("error"=>"Problem adding user to project. Verify if the user excists and if you have admin rights"));
	}
	
}
);


//promote user, shows verification or error message
$app->post("/promoteProjectUser",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$uid=getUserID($data->username);
	if(isUser_ProjectAdmin($data->pid)&&isUser_inProject($data->pid,$uid))
	{
		$sql="UPDATE tbl_projectgebruiker SET rid='2' WHERE pid='$data->pid' AND uid='$uid'";
		
		try
		{
			$db=getConnection();
			$db->exec($sql);
			echo json_encode(array("msg"=>"user promoted."));	
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}
	}
	else {
		echo json_encode(array("error"=>"no admin rights, or user not in project yet!"));
	}	
}
);


$app->post("/leaveProject",function() use($app)
{
	$data=json_decode($app->request()->getBody());
	$puid_rid=getPUID_AND_RID($data->pid);
	$puid=$puid_rid[0];
	$rid=$puid_rid[1];
	$db=getConnection();
	
	if($rid!="1")
	{
		try{
			$sql="DELETE FROM tbl_projectgebruiker WHERE puid='$puid'";
			$statement=$db->prepare($sql);
			$statement->execute();
			echo json_encode(array("projects_left"=>$statement->rowCount()));	
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}	
	}else{
		echo json_encode(array("error"=>"Creator can\'t leave project"));
	}
}
);

$app->post("/passProject",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$puid_rid=getPUID_AND_RID($data->pid);
	$puid=$puid_rid[0];
	$rid=$puid_rid[1];
	$puid_rid_target=getPUID_AND_RID_byUser($data->pid, $data->uid);
	$target_puid=$puid_rid_target[0];
	$db=getConnection();
	
	if($rid=="1")
	{
		try{
			$sql="UPDATE tbl_projectgebruiker SET rid='2' WHERE puid='$puid'";
			$sql2="UPDATE tbl_projectgebruiker SET rid='1' WHERE puid='$target_puid'";
			$sql3="UPDATE tbl_project SET oprichterid='$data->uid' WHERE pid='$data->pid'";
			
			$arrResult=array();
			
			$db->beginTransaction();
			$statement1=$db->prepare($sql);
			$statement2=$db->prepare($sql2);
			$statement3=$db->prepare($sql3);
			
			$statement1->execute();
			$statement2->execute();
			$statement3->execute();
			
			
			if($statement1->rowCount()==1 && $statement2->rowCount()==1 && $statement3->rowCount()==1){
				$db->commit();
				echo json_encode(array("msg"=>"success!"));
			}else{
				$db->rollBack();
				echo json_encode(array("error"=>"failed!"));
			}
		}	
			catch(PDOException $ex)
			{
				$db->rollBack();
				echo json_encode(array("error"=>$ex->getMessage()));
			}
	}
	else {
		echo json_encode(array("error"=>"Only creator can pass project"));
	}
});

$app->post("/removeProjectUser",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	//pid,username
	$userid=getUserID();
	$sql1="SELECT rid FROM tbl_projectgebruiker WHERE uid='$userid' AND pid='$data->pid'";
	$db=getConnection();
	
	try
	{
		$result=$db->query($sql1);
		$rid=$result->fetchColumn();
		if($rid=="2"||$rid=="1")
		{
			$sql2="DELETE FROM tbl_projectgebruiker WHERE uid=(SELECT uid FROM tbl_gebruiker WHERE uname='$data->username') AND pid='$data->pid'";
			$statement=$db->prepare($sql2);
			$statement->execute();
			if($statement->rowCount()>1)
			{
				echo json_encode(array("error"=>"Couldn't remove any user"));
			}
			echo json_encode(array("msg"=>"$statement->rowCount() rows affected."));
		}
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
}
);

$app->post("/demoteProjectUser",function() use($app)
{
$data=json_decode($app->request()->getBody());
	//pid,username
	$userid=getUserID();
	$sql1="SELECT rid FROM tbl_projectgebruiker WHERE uid='$userid' AND pid='$data->pid'";
	$db=getConnection();
	
	try
	{
		$result=$db->query($sql1);
		$rid=$result->fetchColumn();
		echo "rid:$rid";
		if($rid=="1")
		{
			$sql2="UPDATE tbl_projectgebruiker SET rid='3' WHERE uid=(SELECT uid FROM tbl_gebruiker WHERE uname='$data->username') AND pid='$data->pid'";
			echo $sql2;
			$statement=$db->prepare($sql2);
			$statement->execute();
			// error op $statement->rowCount(), niet defined wat?
			echo json_encode(array("msg"=>"rows affected"));
		}
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
});

// enter "NOT" as parameter for in, to get the users that are not in the project
function getProjectUsers($pid,$in="")
{
	if(pidExists($pid))
	{
	$extravraag="";
	if(!$in=="")
	{
		$extravraag=" GROUP BY selecteduid";
		$sql="SELECT uid as selecteduid,uname,voornaam,achternaam,email FROM tbl_gebruiker
		WHERE uid NOT IN(SELECT uid FROM tbl_projectgebruiker where pid='$pid')
		";
	}
	else
	{
		$sql="SELECT tbl_gebruiker.uid as selecteduid,uname,voornaam,achternaam,naam,tbl_projectgebruiker.rid as rid FROM tbl_gebruiker 
	LEFT JOIN tbl_projectgebruiker ON tbl_gebruiker.uid=tbl_projectgebruiker.uid
	LEFT JOIN rechten ON tbl_projectgebruiker.rid=rechten.rid
	WHERE tbl_gebruiker.uid IN (SELECT uid FROM tbl_projectgebruiker WHERE tbl_projectgebruiker.pid='$pid') AND"
	// $in
	 ." tbl_projectgebruiker.pid='$pid'";
		
	}
	$con=getConnection();
	try{
		$statement=$con->query($sql);
		$arrResultaat=array();
		$i=0;
		while($row=$statement->fetch(PDO::FETCH_ASSOC))
		{
			$arrResultaat[$i]=array("uid"=>$row['selecteduid'],"uname"=>$row['uname'],"voornaam"=>$row['voornaam'],"achternaam"=>$row['achternaam']);	
			if(isset($row['naam']))
			{
				$arrResultaat[$i]["rechten"]=$row['naam'];
				$arrResultaat[$i]["rid"]=$row['rid'];
			}
			$i++;
		}
		echo json_encode(array("users"=>$arrResultaat));	
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}	
	}
	else {
		echo json_encode(array("error"=>"project with pid $pid does not excist"));
	}
}

function getUsersInProject($pid)
{
	if(pidExists($pid))
	{
	getProjectUsers($pid);
	}
	else{
		echo json_encode(array("error"=>"project doesn't excist"));
	}	
}

function getUsersOutProject($pid)
{
	if(pidExists($pid))
	{
	getProjectUsers($pid,"NOT");
	}
	else{
		echo json_encode(array("error"=>"project doesn't excist"));
	}			
}


//update the user using a json post, sends back the username
$app->post('/updateUser',function() use ($app)
{
	
	$username=getUsername();
	$data=json_decode($app->request()->getBody());
	try{
		$db=getConnection();
		//$sql0="SELECT AES_ENCRYPT('$data->password',`key`) FROM encrypt WHERE `eid`='".md5(getUsername().'@S0urc3F1sh!*!')."'";
		//echo $sql0;
		$sql="UPDATE tbl_gebruiker SET voornaam=:voornaam,achternaam=:achternaam WHERE uname=:username";
		//print_r($data);
		//$result=$db->query($sql0);
		//$cryptpass=$result->fetchColumn();
		$statement=$db->prepare($sql);
		//$statement->bindParam("wachtwoord",$cryptpass);
		$statement->bindParam("voornaam",$data->firstname);
		$statement->bindParam("username",$username);
		$statement->bindParam("achternaam",$data->lastname);
		$statement->execute();
		echo json_encode(array("msg"=>"user : $username is changed"));
	}catch(PDOException $ex)
	{
		echo json_encode(array("error"=>$ex->getMessage()));
	}
	
		
});

$app->post('/setProfilePicture',function() use($app){
	$userid=getUserID();
	$data=$app->request()->getBody();
	//var_dump($app->request());
	//echo $data;
	/*$splitdata=explode("\n", $data);
	$correctsplitdata=array_slice($splitdata,4,count($splitdata)-2);
	
	$image=implode("\n",$correctsplitdata);
	echo $image;*/
	
	
	try{
		$db=getConnection();
		$sql="UPDATE tbl_gebruiker SET profielfoto=? WHERE uid=?";
		$query=$db->prepare($sql);
		$query->execute(array($data,$userid));
		
		//echo $data;
	}
	catch(Exception $e)
	{
		echo $e->getMessage();
	}
	
});

$app->get("/getProfilePicture/:uid",function($username) use ($app)
{
	$res=$app->response();
	$res['Content-Type'] = 'image/png';
	$res['Content-Disposition'] ='attachment; filename=' . $username.".png";
	$res['Content-Transfer-Encoding'] = 'binary';
    $res['Expires'] = '0';
    $res['Cache-Control'] = 'must-revalidate';
	try
	{
		$db=getConnection();
		$sql="SELECT profielfoto FROM tbl_gebruiker WHERE LOWER(uname)=LOWER('$username')";
		$data;
		$statement=$db->query($sql);
		echo $statement->fetchColumn();
	}
	catch(Exception $e)
	{
		echo $e->getMessage();
	}
	
	
});





?>