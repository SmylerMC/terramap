package fr.thesmyler.terramap.files.kml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class KmlPoint {
	
	private double longitude, latitude;
	
	public KmlPoint(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@XmlTransient
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@XmlTransient
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@XmlElement(name="coordinates")
	public String getCoordinates() {
		return this.longitude + "," + this.latitude;
	}

}
