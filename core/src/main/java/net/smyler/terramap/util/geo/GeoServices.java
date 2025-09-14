package net.smyler.terramap.util.geo;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.smyler.terramap.files.kml.KmlDocument;
import net.smyler.terramap.files.kml.KmlFile;
import net.smyler.terramap.files.kml.KmlPlacemark;
import net.smyler.terramap.files.kml.KmlPoint;
import net.smyler.terramap.Terramap;

/**
 * Utility class to open various geo services such as osm, Google Maps, Earth, etc...
 * 
 * @author SmylerMC
 *
 */
//TODO Open places on services other than gmaps and refactor this mess
public final class GeoServices {
    
    private GeoServices() {}

    public static final String GMAPS_BASE_URL = "https://www.google.com/maps/@{latitude},{longitude},{zoom}z";
    public static final String GMAPS_PLACE_URL = "https://www.google.com/maps/place/{place}/@{latitude},{longitude},{zoom}z";
    public static final String OSM_SITE_BASE_URL = "https://www.openstreetmap.org/#map={zoom}/{latitude}/{longitude}";
    public static final String GEARTH_WEB_BASE_URL = "https://earth.google.com/web/@{latitude},{longitude},0a,10000d,1y,-0h,0t,0r";
    public static final String BTE_SITE_BASE_URL = "https://buildtheearth.net/map?z={zoom}&lat={latitude}&lng={longitude}";

    public static final String BING_SITE_BASE_URL = "https://www.bing.com/maps?cp={latitude}~{longitude}&lvl={zoom}";
    public static final String WIKIMAPIA_SITE_BASE_URL = "https://wikimapia.org/#lat={latitude}&lon={longitude}&z={zoom}";
    public static final String YANDEX_SITE_BASE_URL = "https://yandex.com/maps/?ll={longitude}%2C{latitude}&z={zoom}";

    public static final String OSM_CR_LINK = "https://www.openstreetmap.org/copyright";

    private static final DecimalFormat decFormat6 = new DecimalFormat();
    private static final DecimalFormat decFormat1 = new DecimalFormat();

    static {
        decFormat6.setMaximumFractionDigits(6);
        decFormat1.setMaximumFractionDigits(1);
        DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
        decFormat1.setDecimalFormatSymbols(usSymbols);
    }

    public static String formatStringWithCoords(String str, int zoomLevel, double longitude, double latitude, double placeLongitude, double placeLatitude) {
        try {
            String dispLong = GeoServices.formatGeoCoordForDisplay(longitude);
            String dispLat = GeoServices.formatGeoCoordForDisplay(latitude);
            String dispPlace;
            dispPlace = URLEncoder.encode(GeoServices.numeric2NSEW(placeLongitude, placeLatitude), StandardCharsets.UTF_8.toString());
            return str.replace("{zoom}", String.valueOf(zoomLevel))
                    .replace("{latitude}", dispLat)
                    .replace("{longitude}", dispLong)
                    .replace("{place}", dispPlace);
        } catch (UnsupportedEncodingException e) {
            Terramap.instance().logger().error("Failed to format a string with coordinates: ");
            Terramap.instance().logger().catching(e);
        }
        return str;
    }

    public static void openInOSMWeb(int zoom, double lon, double lat, double markerLon, double markerLat) {
        GeoServices.openURI(GeoServices.formatStringWithCoords(OSM_SITE_BASE_URL, zoom, lon, lat, markerLon, markerLat));
    }

    public static void openInGoogleMaps(int zoom, double lon, double lat) {
        GeoServices.openURI(GeoServices.formatStringWithCoords(GMAPS_BASE_URL, zoom, lon, lat, lon, lat));
    }

    public static void openPlaceInGoogleMaps(int zoom, double centerLon, double centerLat, double markerLon, double markerLat) {
        GeoServices.openURI(GeoServices.formatStringWithCoords(GMAPS_PLACE_URL, zoom, centerLon, centerLat, markerLon, markerLat));
    }

    public static void openInBTEMap(int zoom, double lon, double lat, double markerLon, double markerLat) {
        int z = Math.min(zoom, 18);
        GeoServices.openURI(GeoServices.formatStringWithCoords(BTE_SITE_BASE_URL, z, lon, lat, markerLon, markerLat));
    }

    public static void opentInGoogleEarthWeb(double longitude, double latitude, double markerLon, double markerLat) {
        GeoServices.openURI(GeoServices.formatStringWithCoords(GEARTH_WEB_BASE_URL, 0, longitude, latitude, markerLon, markerLat));
    }

    public static void openInBingMaps(int zoom, double lon, double lat, double markerLon, double markerLat) {
        int z = Math.min(zoom, 18);
        GeoServices.openURI(GeoServices.formatStringWithCoords(BING_SITE_BASE_URL, z, lon, lat, markerLon, markerLat));
    }

    public static void openInWikimapia(int zoom, double lon, double lat, double markerLon, double markerLat) {
        int z = Math.min(zoom, 18);
        GeoServices.openURI(GeoServices.formatStringWithCoords(WIKIMAPIA_SITE_BASE_URL, z, lon, lat, markerLon, markerLat));
    }

    public static void openInYandex(int zoom, double lon, double lat, double markerLon, double markerLat) {
        int z = Math.min(zoom, 18);
        GeoServices.openURI(GeoServices.formatStringWithCoords(YANDEX_SITE_BASE_URL, z, lon, lat, markerLon, markerLat));
    }

    public static void openInGoogleEarthPro(double lon, double lat) {
        KmlFile kml = new KmlFile();
        KmlDocument doc = kml.getDocument();
        doc.setName("Terramap kml document");
        KmlPlacemark placemark = new KmlPlacemark();
        KmlPoint point = new KmlPoint(lon, lat);
        placemark.setPoint(point);
        placemark.setName("Terramap");
        placemark.setDescription("Location exported from Terramap");
        doc.addPlacemark(placemark);
        try {
            Path path = Files.createTempFile("terramap_export", ".kmz");
            File file = path.toFile();
            kml.save(file, true);
            Desktop.getDesktop().open(file);
            file.deleteOnExit();
        } catch(Exception e) {
            Terramap.instance().logger().error("There was an error when trying to open a place in Google Earth");
            Terramap.instance().logger().catching(e);
        }
    }

    public static void openURI(String uriStr) {
        try {
            URI uri = new URI(uriStr);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | UnsupportedOperationException e) {
            Terramap.instance().logger().error("Failed to open uri: {}", uriStr);
            Terramap.instance().logger().catching(e);
        } catch (URISyntaxException e) {
            Terramap.instance().logger().error("Tried to open a malformed URI: {}", uriStr);
        }

    }

    public static String formatGeoCoordForDisplay(double coordinate) {
        if(Double.isNaN(coordinate)) return "-";
        return decFormat6.format(coordinate);
    }

    public static String formatAzimuthForDisplay(float az) {
        if(Float.isNaN(az)) return "-";
        return decFormat1.format(az);
    }

    public static String formatGeoPointForDisplay(GeoPoint<?> location) {
        return formatGeoCoordForDisplay(location.latitude()) + "° " + formatGeoCoordForDisplay(location.longitude()) + "°";
    }
    public static String formatZoomLevelForDisplay(double zoomLevel) {
        if(Double.isNaN(zoomLevel)) return "-";
        return decFormat1.format(zoomLevel);
    }

    public static String numeric2NSEW(double longitude, double latitude) {
        double fixedLon = GeoUtil.getLongitudeInRange(longitude);
        double fixedLat = GeoUtil.getLatitudeInRange(latitude);
        String eo = fixedLon < 0 ? "W": "E";
        String ns = fixedLat < 0 ? "S" : "N";
        double absLon = Math.abs(fixedLon);
        double absLat = Math.abs(fixedLat);
        int longitudeDegrees = (int) absLon;
        int latitudeDegrees = (int) absLat;
        double minLon = absLon * 60 - longitudeDegrees * 60;
        double minLat = absLat * 60 - latitudeDegrees * 60;
        int longitudeMinutes = (int) minLon;
        int latitudeMinutes = (int) minLat;
        double secLon = minLon * 60 - longitudeMinutes * 60;
        double secLat = minLat * 60 - latitudeMinutes * 60;
        String formatedLongitude = longitudeDegrees + "°" + longitudeMinutes + "'" + decFormat1.format(secLon) + "\"" + eo;
        String formatedLatitude = latitudeDegrees + "°" + latitudeMinutes + "'" + decFormat1.format(secLat) + "\"" + ns;
        return formatedLatitude + " " + formatedLongitude;
    }

}
