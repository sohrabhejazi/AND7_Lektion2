package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
//import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import db.Note;
import db.NoteLocation;
import db.Project;

public class LocationClient
{ 
	private String uriStringGet= "http://localhost:8080/GeoNotes/GetLocationsServlet";
	private final String uriStringSave =
		"http://localhost:8080/GeoNotes/SaveLocationServlet";
//Initialisierung > Aufgabe 2.6
	private String uriString;
	private final String servletGet ="GeoNotes/GetLocationsServlet";
	private final String servletSave ="GeoNotes/SaveLocationServlet";
	public LocationClient(String uri) throws URISyntaxException
	{
		this.uriString = new URI(uri).toString();
	}
	
	public ArrayList<Note> getNextLocations(Note data, int id)
	{
		DefaultHttpClient locationClient =
		new DefaultHttpClient();
		//HttpPost httpPost = new HttpPost(uriStringGet);
		HttpPost httpPost =
				new HttpPost(uriString + servletGet);
		// Parameter in den Request-Anhang speichern (Code 2.7)
		setParams(data, id, httpPost);
		HttpResponse response = null;
		try {
			response = locationClient.execute(httpPost);
		} catch (ClientProtocolException cpe) {
			log(cpe.toString());
		} catch (IOException ioe) {
			log(ioe.toString());
		}
		HttpEntity responseEntity = response.getEntity();
		// neu gegenüber "getLocations": Rückgabe-Objekt
		// erstellen:
		ArrayList<Note> retList = null;
		if(responseEntity != null) {
		try {
			BufferedReader reader = new BufferedReader(
			new InputStreamReader(
			responseEntity.getContent()));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null)
				sb.append(line);
			reader.close();
			String result = sb.toString();
			retList = this.extractNoteList(result);
			for(Note note : retList) {
			// provisorische Kontrollausgabe
			// TODO: Präsentation im Projekt
				log(note.toString());
		}
		} catch (Exception e) {
		// möglich: IllegalStateException, IOException
			log(e.toString());
		}
		} else
			log("Die Server-Antwort ist null!");
		return retList;
}
	
	public String saveLocation(Note data, int id) {
		DefaultHttpClient locationClient = new DefaultHttpClient();
		//HttpPost httpPost = new HttpPost(uriStringSave);
		HttpPost httpPost =
				new HttpPost(uriString + servletSave);
		setParams(data, id, httpPost);
		HttpResponse response = null;
		try {
		response = locationClient.execute(httpPost);
		} catch (ClientProtocolException cpe) {
		log(cpe.toString());
		} catch (IOException ioe) {
		log(ioe.toString());
		}
		HttpEntity responseEntity = response.getEntity();
		if(responseEntity != null) {
		try {
		BufferedReader reader =
		new BufferedReader(
		new InputStreamReader(
		responseEntity.getContent()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
		sb.append(line);
		reader.close();
		return sb.toString();
		} catch (IOException ioe) {
		log(ioe.toString());
		}
		}
		return null;
		}

private ArrayList<Note> extractNoteList(String response)
	{
		ArrayList<Note> retList = new ArrayList<Note>();
		Project project = new Project();
		StringTokenizer tokNotes =
		new StringTokenizer(response, "#");
		while(tokNotes.hasMoreTokens())
		{
			StringTokenizer tokNote =
			new StringTokenizer(tokNotes.nextToken(), ";");
			// Rückumwandlung vom Server-seitigen "int"
			// (Microdegrees) zum App-seitigen "double" (Degrees)
			double latitude = ((double)Integer
			.parseInt(tokNote.nextToken())) / 1E6;
			double longitude = ((double)Integer
			.parseInt(tokNote.nextToken())) / 1E6;
			int altitude = Integer.parseInt(tokNote.nextToken());
			String time = tokNote.nextToken();
			String subject = tokNote.nextToken();
			String noteStr = tokNote.nextToken();
			int id = Integer.parseInt(tokNote.nextToken());
			int distance = Integer.parseInt(tokNote.nextToken());
			NoteLocation location = new NoteLocation(latitude,longitude, altitude, "gps", project);
			Note note = new Note(time, subject, noteStr,location);
			retList.add(note);
			// Kontrollausgabe für distance und id:
			log("id=" + id + ", distance=" + distance);
		}
		// die Liste wird schon im LocationManager sortiert
		return retList;
	}
	
	private void setParams(Note data, int id,HttpPost httpPost)
	{
			ArrayList<NameValuePair> paramList =new ArrayList<NameValuePair>();
			String lat = "" + (int)(data.location.geoPoint.latitude * 1E6);
			paramList.add(new BasicNameValuePair("latitude", lat));
			String lon = "" + (int)(data.location.geoPoint.longitude * 1E6);
			paramList.add(new BasicNameValuePair("longitude", lon));
			paramList.add(new BasicNameValuePair("altitude", ""+ data.location.altitude));
			paramList.add(new BasicNameValuePair("time", ""+ data.getTime()));
			paramList.add(new BasicNameValuePair("subject",data.subject));
			paramList.add(new BasicNameValuePair("note",data.note));
			paramList.add(new BasicNameValuePair("id", "" + id));
			try {
				httpPost.setEntity(
				new UrlEncodedFormEntity(paramList));
			} catch (UnsupportedEncodingException uee) {
				log(uee.toString());
			}
	}
public void getLocations()
{
		DefaultHttpClient locationClient =
		new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(uriStringGet);
		// weitere Informationen für den Server?
		HttpResponse response = null;
		try {
			response = locationClient.execute(httpPost);
		} catch (ClientProtocolException cpe) {
			log(cpe.toString());
		} catch (IOException ioe) {
			log(ioe.toString());
		}
		HttpEntity responseEntity = response.getEntity();
		if(responseEntity != null) {
		try {
		responseEntity.writeTo(System.out);
		} catch (IOException ioe) {
		log("getNextLocations – " + ioe.toString());
		}
		} else
		// ggf. Benutzerhinweis wie
		log("Die Server-Antwort ist null!");
	}
	

		public String readLine() throws IOException
		{
			DefaultHttpClient locationClient =
				new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(uriStringGet);
				// weitere Informationen für den Server?
				HttpResponse response = null;
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null) {
			try {
			// 1. Ausgabe mit "writeTo":
			//responseEntity.writeTo(System.out);
			// 2. Auswertung mit "getContent":
			BufferedReader reader = new BufferedReader(
			new InputStreamReader(
			responseEntity.getContent()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			sb.append(line); // Zeilenschaltungen unnötig
			reader.close();
			String result = sb.toString();
			
			// TODO provisorisch:
			System.out.println(result);
			return result;
			} catch (IOException ioe) {
			log(ioe.toString());
			}
			}
			return null;
			
		
	    }
	
	// provisorisches Logging:
	private static final String TAG =
	LocationClient.class.getSimpleName();
	private String newLine =
	System.getProperty("line.separator");
	private static SimpleDateFormat sdf =
	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void log(String message)
	{
		System.out.println(newLine
		+ sdf.format(new Date())
		+ " " + TAG + ": " + message);
	}
	
	
	
}