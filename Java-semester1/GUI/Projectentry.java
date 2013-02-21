package gui;

public class Projectentry {
	private String id;	
	private String projectnaam;
	private String opdrachtgever;
	private String datum;
	private String omschrijving;
	
	public Projectentry(String id, String projectnaam, String opdrachtgever, String datum, String omschrijving) {
		this.setId(id);
		this.setProjectnaam(projectnaam);
		this.setOpdrachtgever(opdrachtgever);
		this.setDatum(datum);
		this.setOmschrijving(omschrijving);
	
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectnaam() {
		return projectnaam;
	}

	public void setProjectnaam(String projectnaam) {
		this.projectnaam = projectnaam;
	}

	public String getOpdrachtgever() {
		return opdrachtgever;
	}

	public void setOpdrachtgever(String opdrachtgever) {
		this.opdrachtgever = opdrachtgever;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}
	
}
