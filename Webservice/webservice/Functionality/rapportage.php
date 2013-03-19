<?php
require '../tcpdf/tcpdf/tcpdf.php';

class rapportage extends TCPDF {
	
	protected $title;
	function __construct($title)
	{
		parent::__construct();
		$this->title=$title;
	}
	
	public function Header()
	{
		// Logo
        $image_file = 'sourcefish.png';
        $this->Image($image_file, 10, 10, '30', '', 'PNG', '', 'T', false, 300, '', false, false, 0, false, false, false);
        // Set font
        $this->SetFont('times', 'B', 20);
        // Title
        $this->Cell(0, 15, $this->title, 0, false, 'C', 0, '', 0, false, 'M', 'M');
	}
}

$app->post('/showPDF',function() use($app)
{
	$data=json_decode($app->request()->getBody());
	if(pidExists($data->pid))
	{
	$app->contentType("application/pdf");
	//pid,uids,modus
	$data=json_decode($app->request()->getBody());
	$db=getConnection();
	$sqlomschrijving="SELECT projectnaam,opdrachtgever,begindatum,einddatum,omschrijving,voornaam,achternaam FROM tbl_project 
	LEFT JOIN tbl_gebruiker ON oprichterid=uid WHERE pid='$data->pid'";
	$uids="";
	if($data->uids)
	{
		$extra=" AND tbl_projectgebruiker.uid IN(".implode(",",$data->uids).")";	
	}
	$sqlentries="SELECT rid,voornaam,achternaam,`begin`,eind,notities FROM tbl_tijdregistratie 
	LEFT JOIN tbl_projectgebruiker ON tbl_tijdregistratie.puid=tbl_projectgebruiker.puid
	LEFT JOIN tbl_gebruiker ON tbl_projectgebruiker.uid=tbl_gebruiker.uid
	WHERE pid='$data->pid'"
	.$extra;
	
	$modus=$data->modus;
	
	if($modus=="user")
	{
		$sqlentries.=" ORDER BY rid,achternaam ASC";
	}
	
	if($modus=="chron")
	{
		$sqlentries.=" ORDER BY `begin` ASC";
	}

	try{
		$statementomschrijving=$db->query($sqlomschrijving);
		$resultomschrijving=$statementomschrijving->fetchAll(PDO::FETCH_ASSOC);
		$resultaatoutput=array();
		$resultaatoutput['projectgegevens']=$resultomschrijving[0];
		$statemententries=$db->query($sqlentries);
		$resultentries=$statemententries->fetchAll(PDO::FETCH_ASSOC);
		$resultaatoutput['entries']=$resultentries;
		
		
		$pdf=new rapportage("Project : ".$resultaatoutput['projectgegevens']['projectnaam']);
		$pdf->setAuthor($resultaatoutput['projectgegevens']['voornaam']." ".$resultaatoutput['projectgegevens']['achternaam']);
		$pdf->setTitle("Result of ".$resultaatoutput['projectgegevens']['projectnaam']);
		$pdf->SetMargins(PDF_MARGIN_LEFT, PDF_MARGIN_TOP, PDF_MARGIN_RIGHT);
		$pdf->SetHeaderMargin(PDF_MARGIN_HEADER);
		$pdf->SetFooterMargin(PDF_MARGIN_FOOTER);

		
		$pdf->AddPage();
		$font="times";
		$fontsize_title = 16;
    	$fontsize_big = 14;
    	$fontsize_normal = 12;
		$pdf->setFont($font,"B",$fontsize_big);
		$pdf->Cell(0,5,utf8_decode("Started by ".$resultaatoutput['projectgegevens']['voornaam']." ".
		$resultaatoutput['projectgegevens']['achternaam']), 0, 1);
		$pdf->Cell(0,5,utf8_decode("Client: ".$resultaatoutput['projectgegevens']['opdrachtgever']), 0, 1);
		
		$pdf->SetFont($font,'',$fontsize_normal);
		
		$w=$pdf->GetLineWidth();
		

		$count=1;
		for($i=0;$i<count($resultentries);$i++)
		{
			$r=$resultentries[$i];
			
			$pdf->SetFont($font,'B',$fontsize_normal);
			$pdf->MultiCell(20,0,utf8_decode($count. ". Name:"), 0, 1);
			$pdf->SetFont($font,'',$fontsize_normal);
			$pdf->MultiCell(100,0,utf8_decode($r["voornaam"]." ".$r["achternaam"]), 0, 1);

			$pdf->SetFont($font,'B',$fontsize_normal);
			$pdf->MultiCell(20,0,utf8_decode("Start: "), 0, 1);
			$pdf->SetFont($font,'',$fontsize_normal);
			$pdf->MultiCell(100,5,utf8_decode($r["begin"]), 0, 1);
			
			$pdf->SetFont($font,'B',$fontsize_normal);
			$pdf->MultiCell(20,0,utf8_decode("End:"), 0, 1);
			$pdf->SetFont($font,'',$fontsize_normal);
			if($r["eind"]=="0000-00-00 00:00:00")
			{
				$pdf->MultiCell(100,0,utf8_decode(" in progress"), 0, 1);
						
			}else{
				$pdf->MultiCell(100,0,utf8_decode($r["eind"]), 0, 1);
			}
			
			$pdf->SetFont($font,'B',$fontsize_normal);
			$pdf->MultiCell(20,0,utf8_decode("Notes:"), 0, 1);
			$pdf->SetFont($font,'',$fontsize_normal);
			$pdf->MultiCell(100,0,utf8_decode($r["notities"]), 0, 1);
			
			$pdf->Ln();
			$pdf->Line($pdf->getX(),$pdf->GetY(),$pdf->getX()+75,$pdf->GetY(),
			array('width' => 0.2, 'cap' => 'butt', 'join' => 'miter', 'dash' => 0, 'color' => array(0, 0, 0)));
			$pdf->Ln();
			$count++;	
		}
		
		$pdf->Output($resultaatoutput['projectgegevens']['projectnaam'].".pdf",'I');
		exit;
		
	}
	catch(PDOException $ex)
	{
		echo json_encode(array("error"=>"er is iets misgelopen met het ophalen uit de databank"));	
	}	
	}
	else
	{
		echo json_encode(array("error"=>"project with pid $pid does not exist."));		
	}
	
});
?>