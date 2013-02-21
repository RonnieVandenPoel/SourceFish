<?php
//adding projects, removing projects, changing project metadata, ending, restart project

//create a new project, again using a json post, sends verification or error message
$app->post('/addProject',function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	
	if(!$data->projectname==""&&$data->projectname){
		
	$sql0="SELECT projectnaam FROM tbl_project WHERE projectnaam='$data->projectname'";
	$sql1="INSERT INTO tbl_projectgebruiker(uid,rid) VALUES (:uid,'1')";
	$sql2="INSERT INTO tbl_project(projectnaam,opdrachtgever,begindatum,omschrijving,oprichterid) VALUES(:projectnaam,:opdrachtgever,:begindatum,:omschrijving,:id)";
	$sql3="UPDATE tbl_projectgebruiker SET pid=:pid WHERE puid=:puid";
	
	$db=getConnection();
	
	$uid=getUserID();
	try
	{
		
		$statement=$db->query($sql0);
		$result=$statement->fetchAll(PDO::FETCH_ASSOC);
		
		
		$bestaat=false;
		
		for ($i=0; $i <count($result) ; $i++) { 
			$bestaat=true;		
		}
		
		if(!$bestaat)
		{	
		$statement1=$db->prepare($sql1);
		$statement1->bindParam("uid",$uid);
		
		$statement2=$db->prepare($sql2);
		$statement2->bindParam("projectnaam",$data->projectname);
		$statement2->bindParam("opdrachtgever",$data->client);
		$statement2->bindParam("omschrijving",$data->summary);
		$statement2->bindParam("begindatum",$data->start);
		$statement2->bindParam("id",getUserID());
		$statement3=$db->prepare($sql3);
		
		$db->beginTransaction();
		
		$statement1->execute();
		$puid=$db->lastInsertId();
		
		$statement2->execute();
		$pid=$db->lastInsertId();
		
		$statement3->bindParam("pid",$pid);
		$statement3->bindParam("puid",$puid);
		$statement3->execute();
		
		$db->commit();
		echo json_encode(array("pid"=>"$pid"));
		}
		else{
			echo json_encode(array("error"=>"You already got a project with this name."));
		}
	}

	catch(PDOException $ex)
	{
		$db->rollBack();
		echo json_encode(array("error"=>$ex->getMessage()));
	}
	}else{
		echo json_encode(array("error"=>"No projectname posted"));	
	}
});

$app->post("/changeProject",function() use($app)
{
	$data=json_decode($app->request()->getBody());
	$puid_rid=getPUID_AND_RID($data->pid);
	$rid=$puid_rid[1];
	//pid,name,customer,description
	if($rid=="1")
	{
		$sql="UPDATE tbl_project SET projectnaam='$data->projectname', opdrachtgever='$data->client', omschrijving='$data->summary' 
		WHERE pid='$data->pid'";
		
		try{
			$db=getConnection();
			$statement=$db->prepare($sql);
			$statement->execute();
			echo json_encode(array("msg"=>$statement->rowCount()." rows affected."));
		}catch(PDOException $ex)
		{
			echo json_encode(array("error"=>$ex->getMessage()));
		}
	}
	else {
		echo json_encode(array("error"=>"Unauthorized."));
	}
});


$app->post("/deleteProject",function() use ($app)
{
	$data=json_decode($app->request()->getBody());
	$uid=getUserID();
	
	$db=getConnection();
	$puid_rid=getPUID_AND_RID($data->pid);
	if($puid_rid[1]=="1")
	{
		try{
			$sql1="DELETE FROM tbl_tijdregistratie WHERE puid IN(SELECT puid FROM tbl_projectgebruiker WHERE pid='$data->pid')";
			$sql2="DELETE FROM tbl_projectgebruiker WHERE pid='$data->pid'";
			$sql3="DELETE FROM tbl_project WHERE pid='$data->pid'";
			
			
			$arrReturn=array();
			$db->beginTransaction();
			$statement1=$db->prepare($sql1);
			$statement2=$db->prepare($sql2);
			$statement3=$db->prepare($sql3);
			$statement1->execute();
			$arrReturn['deletedEntries']=$statement1->rowCount();
			$statement2->execute();
			$arrReturn['deletedProjectUsers']=$statement2->rowCount();
			$statement3->execute();
			$arrReturn['deletedProjects']=$statement3->rowCount();
			$db->commit();
			echo json_encode($arrReturn);
			
		}catch(PDOException $ex)
		{
			$db->rollBack();
			echo json_encode(array("error"=>$ex->getMessage()));
		}
	}
	else {
		echo json_encode(array("error"=>"You are not the owner of the project."));
	}	
});


?>