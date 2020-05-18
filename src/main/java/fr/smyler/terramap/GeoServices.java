package fr.smyler.terramap;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class GeoServices {

	public static final String GMAPS_BASE_URL = "https://www.google.com/maps/@{latitude},{longitude},{zoom}z";
	public static final String OSM_SITE_BASE_URL = "https://www.openstreetmap.org/#map={zoom}/{latitude}/{longitude}";
	
	public static String formatStringWithCoords(String str, int zoomLevel, double longitude, double latitude) {
		return str.replace("{zoom}", "" + zoomLevel).replace("{latitude}", "" + latitude).replace("{longitude}", "" + longitude);
	}
	
	public static void openInOSMWeb(int zoom, double lon, double lat) {
		GeoServices.openURI(GeoServices.formatStringWithCoords(OSM_SITE_BASE_URL, zoom, lon, lat));
	}
	
	public static void openInGoogleMaps(int zoom, double lon, double lat) {
		GeoServices.openURI(GeoServices.formatStringWithCoords(GMAPS_BASE_URL, zoom, lon, lat));
	}
	
	public static void openURI(String uriStr) {
		try {
			URI uri = new URI(uriStr);
			Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
			TerramapMod.logger.error("Failed to open uri: " + uriStr);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			TerramapMod.logger.error("Tried to open a malformed URI: " + uriStr);
		}

	}
}
