package fr.thesmyler.terramap.files.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class KmlDocument {
	
	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="Placemark")
	private final List<KmlPlacemark> placemarks = new ArrayList<KmlPlacemark>();
	
	public KmlDocument(String name) {
		this.name = name;
	}
	
	public KmlDocument() {}

	@XmlTransient
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addPlacemark(KmlPlacemark place) {
		this.placemarks.add(place);
	}
	
	public void removePlacemark(KmlPlacemark place) {
		this.placemarks.add(place);
	}
	
	public KmlPlacemark[] getPlacemarks(KmlPlacemark place) {
		return this.placemarks.toArray(new KmlPlacemark[0]);
	}
}
