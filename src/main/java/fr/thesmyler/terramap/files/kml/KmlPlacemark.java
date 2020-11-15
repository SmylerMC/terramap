package fr.thesmyler.terramap.files.kml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class KmlPlacemark {
	
	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="description")
	private String description;
	
	@XmlElement(name="Point")
	private KmlPoint point;

	@XmlTransient
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public KmlPoint getPoint() {
		return point;
	}

	public void setPoint(KmlPoint point) {
		this.point = point;
	}
	
	

	
}
