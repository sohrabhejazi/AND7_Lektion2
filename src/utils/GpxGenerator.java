package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import db.Note;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import android.net.Uri;
import android.os.Environment;
import android.util.Log ;
public class GpxGenerator
{
	private static final String TAG =
	GpxGenerator.class.getSimpleName();
	private ArrayList<Note> projectNotes;
	private SimpleDateFormat sdfUTCDate =
			new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
	private SimpleDateFormat sdfUTCTime =
			new SimpleDateFormat("hh:mm:ss", Locale.GERMANY);
	private String projectName;
	private String projectDesc;
	private String filePath;
	public GpxGenerator(String projectName,
	String projectDesc, ArrayList<Note> projectNotes)
	{
		this.projectName = projectName;
		this.projectDesc = projectDesc;
		this.projectNotes = projectNotes;
		filePath = Environment.getExternalStorageDirectory()
		.getPath() + "/" + projectName + ".gpx";
	}
	private Uri serialize(Document doc) {
		File destFile = new File(filePath);
		TransformerFactory factory =
		TransformerFactory.newInstance();
		try {
		Transformer transformer =
		factory.newTransformer();
		transformer.transform(
		new DOMSource(doc.getDocumentElement()),
		new StreamResult(destFile)
		);
		return Uri.parse("file://" + filePath);
		} catch (TransformerConfigurationException tce) {
			
			Log.e(TAG, tce.toString());
		} catch (TransformerException te) {
		Log.e(TAG, te.toString());
		}
		return null;
	}
	
	private void appendTrackpoints(Document doc)
	{
		Element trk = doc.createElement("trk");
		Element trkseg = doc.createElement("trkseg");
		doc.getDocumentElement().appendChild(trk);
		trk.appendChild(trkseg);
		for(Note note : projectNotes)
		{
			Element trkpt = doc.createElement("trkpt");
			trkpt.setAttribute("lat", Double.toString(
			note.location.geoPoint.latitude));
			trkpt.setAttribute("lon", Double.toString(
			note.location.geoPoint.longitude));
			// alles Weitere in Aufgabe 3.7
			Element ele = doc.createElement("ele");
			ele.appendChild(doc.createTextNode(
			Integer.toString(note.location.altitude)));
			trkpt.appendChild(ele);
			Element time = doc.createElement("time");
			time.appendChild(doc.createTextNode(
			toUTCString(note.location.getTime())));
			trkpt.appendChild(time);
			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(note.subject));
			trkpt.appendChild(name);
			Element desc = doc.createElement("desc");
			desc.appendChild(doc.createTextNode(note.note));
			trkpt.appendChild(desc);
			trkseg.appendChild(trkpt);
		}
	}
	private String toUTCString(long date)
	{
		Date d = new Date(date);
		return sdfUTCDate.format(d) + "T"
		+ sdfUTCTime.format(d) + "Z";
	}
	public Uri createGpxFile()
	{
		Document doc = this.createGpxDocument();
		this.createRootElement(doc);
		appendTrackpoints(doc);
		return serialize(doc);
		// provisorisch :
		//return null;
	}
	private Document createGpxDocument()
	{
		Document gpxDocument;
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
		try {
		DocumentBuilder docBuilder =
		factory.newDocumentBuilder();
		gpxDocument = docBuilder.newDocument();
		} catch (ParserConfigurationException pce) {
		Log.e(TAG, pce.toString());
		return null;
		}
		return gpxDocument;
	}
	private void createRootElement(Document doc) {
		Element root = doc.createElement("gpx");
		root.setAttribute("xmlns",
		"http://www.topografix.com/GPX/1/1");
		root.setAttribute("xmlns:xsi",
		"http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation",
		"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
		root.setAttribute("version", "1.1");
		root.setAttribute("creator", "GeoNotes");
		doc.appendChild(root);
		}
}