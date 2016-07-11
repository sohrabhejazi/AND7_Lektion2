package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import db.Note;
import and7.lektion2.components.R;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
public class HtmlGenerator
{
	private static final String TAG =
	HtmlGenerator.class.getSimpleName();
	private Context context;
	private String sdCardPath;
	private String newLine =
			System.getProperty("line.separator");
	private String token_fix = "var map; function init() { map = new OpenLayers.Map (\"map\", { controls:[ new OpenLayers.Control.Navigation(), new OpenLayers.Control.PanZoomBar(), new OpenLayers.Control.LayerSwitcher(), new OpenLayers.Control.Attribution()], maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34), maxResolution: 156543.0399, numZoomLevels: 19, units: \"m\", projection: new OpenLayers.Projection(\"EPSG:900913\"), displayProjection: new OpenLayers.Projection(\"EPSG:4326\") } ); layerMapnik = new OpenLayers.Layer.OSM.Mapnik(\"Mapnik\"); map.addLayer(layerMapnik); layerCycleMap = new OpenLayers.Layer.OSM.CycleMap(\"CycleMap\"); map.addLayer(layerCycleMap); layerMarkers = new OpenLayers.Layer.Markers(\"Marker\"); map.addLayer(layerMarkers);  var size = new OpenLayers.Size(35, 35); var offset = new OpenLayers.Pixel(-(size.w/2), -size.h); var icon = new OpenLayers.Icon(markerFileName, size, offset);  for(var i=0; i<geopoints.length; i++) { var popuptext=\"<b>\" + notes[i][0] + \"</b><p>\" + notes[i][1] + \"</p>\"; var lonLat = new OpenLayers.LonLat(geopoints[i][0], geopoints[i][1]) .transform(new OpenLayers.Projection(\"EPSG:4326\"), map.getProjectionObject()); if(i == centerIndex) map.setCenter(lonLat, zoom); var marker = new OpenLayers.Marker(lonLat, icon.clone()); var feature = new OpenLayers.Feature(layerMarkers, lonLat); feature.closeBox = true; feature.popupClass = OpenLayers.Class(OpenLayers.Popup.FramedCloud, { minSize: new OpenLayers.Size(200, 200), maxSize: new OpenLayers.Size(300, 200) } ); feature.data.popupContentHTML = popuptext; feature.data.overflow = \"hidden\"; marker.feature = feature;  var markerClick = function(evt) { if (this.popup == null) { this.popup = this.createPopup(this.closeBox); map.addPopup(this.popup); this.popup.show(); } else { this.popup.toggle(); } OpenLayers.Event.stop(evt); }; marker.events.register(\"mousedown\", feature, markerClick); layerMarkers.addMarker(marker); } }";
	private String markerFileName;
	private int zoom=5;
	private String htmlName;
	public HtmlGenerator(Context context)
	{
		htmlName="osm.html";
		this.context = context;
		sdCardPath = Environment.getExternalStorageDirectory()
		.getPath() + "/";
	}
	private String createScript(ArrayList<Note> projectNotes, int centerIndex)
	{
		StringBuffer sb = new StringBuffer(5000);
		// Geopoints-Array erstellen:
		sb.append("var geopoints = [");
		for(int i=0; i<projectNotes.size(); i++) {
			Note note = projectNotes.get(i);
			sb.append("['");
			sb.append(note.location.geoPoint.longitude);
			sb.append("','");
			sb.append(note.location.geoPoint.latitude);
			sb.append("']");
			if(i<projectNotes.size()-1)
			sb.append(",");
		}
		sb.append("];");
		// Notes-Array erstellen:
		sb.append("var notes = [");
		for(int i=0; i<projectNotes.size(); i++) {
			Note note = projectNotes.get(i);
			sb.append("['");
			sb.append(note.subject);
			sb.append("','");
			sb.append(note.note);
			sb.append("']");
			if(i<projectNotes.size()-1)
			sb.append(",");
		}
		sb.append("];");
		// Weitere Variablen "zoom", "centerIndex"
		// und "markerFileName":
		sb.append("var zoom=");
		sb.append(zoom);
		sb.append("; var centerIndex=");
		sb.append(centerIndex);
		sb.append("; var markerFileName=\"");
		sb.append(markerFileName);
		sb.append("\";");
		sb.append(token_fix);
		return sb.toString();
	}
	
	private String createOsmHtml(String script)
	{
		Document htmlDocument;
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
		try {
		DocumentBuilder docBuilder =
		factory.newDocumentBuilder();
		// Doucument-Objekt erzeugen
		// mit speziellem Zugriff auf raw-Ressource
		htmlDocument = docBuilder
		.parse(context.getResources()
		.openRawResource(R.raw.geonotes_on_osm));
		// Script in der htmlSeite ergänzen
		Element scriptNode = htmlDocument
		.createElement("script");
		scriptNode.setAttribute("type", "text/javascript");
		scriptNode.setTextContent(script);
		NodeList nodes = htmlDocument
		.getElementsByTagName("head");
		// es gibt nur ein head-Element
		nodes.item(0).appendChild(scriptNode);
		// TODO: Dokument als Datei speichern
		// und deren Zugriffspfad zurückgeben
		return serialize(htmlDocument);
		} catch (Exception e) {
			Log.e(TAG, "createOsmHtml: " + e.toString());
		}
		return null;
	}
	
	private String serialize(Document htmlDoc)
	{
		String path = sdCardPath + htmlName;
		File destFile = new File(path);
		TransformerFactory factory =
		TransformerFactory.newInstance();
		try {
		Transformer transformer = factory.newTransformer();
		transformer.transform(new DOMSource(
		htmlDoc.getDocumentElement()),
		new StreamResult(destFile));
		return path;
		} catch (TransformerConfigurationException tce) {
			Log.e(TAG, tce.toString());
		} catch (TransformerException te) {
			Log.e(TAG, te.toString());
		}
		return null;
	}
	
	public String getOsmHtmlPath(ArrayList<Note> projectNotes, int notesIndex, int markerResourceID)
	{
		String resString = context.getResources()
		.getString(markerResourceID);
		markerFileName = resString.substring(
		resString.lastIndexOf("/") + 1);
		String destinationPath = sdCardPath + markerFileName;
		File markerFile = new File(destinationPath);
		if(!markerFile.exists())
		{
		this.copyFile(destinationPath, markerResourceID);
		}
		return createOsmHtml(createScript(projectNotes,
		notesIndex));
	}
	
	private void copyFile(String destinationPath,
			int rawResourceID)
			{
				BufferedReader reader = new BufferedReader(
				new InputStreamReader(context.getResources()
				.openRawResource(rawResourceID)));
				StringBuffer sb = new StringBuffer();
				String line;
				try {
				while((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append(newLine);
				}
				reader.close();
				OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(destinationPath));
				out.write(sb.toString());
				out.close();
				} catch (FileNotFoundException fnfe) {
				Log.e(TAG, fnfe.toString());
				} catch (IOException ioe) { // von readLine
				Log.e(TAG, ioe.toString());
				}
			}
	
}