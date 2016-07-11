package db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbManager extends SQLiteOpenHelper {
	public static final String DB_NAME = "GeoNotes";
	public static final int DB_VERSION = 1;
	private SQLiteDatabase db;
	private static final String TAG =
			DbManager.class.getSimpleName();
	private final String CREATE_PROJECTS =
			"CREATE TABLE Projects(project_id INTEGER PRIMARY KEY NOT NULL,name TEXT NOT NULL,description TEXT)";
	private final String CREATE_NOTES =
			"CREATE TABLE Notes(time INTEGER PRIMARY KEY NOT NULL,time_loc INTEGER NOT NULL,project_id INTEGER NOT NULL,subject TEXT,note TEXT NOT NULL,category TEXT,CONSTRAINT LocationsFK FOREIGN KEY(time_loc)REFERENCES Locations(time)ON DELETE RESTRICT ON UPDATE CASCADE,CONSTRAINT ProjectFK FOREIGN KEY(project_id)REFERENCES Projects(project_id)ON DELETE RESTRICT ON UPDATE CASCADE)";
	private final String CREATE_LOCATIONS =
			"CREATE TABLE Locations(time INTEGER PRIMARY KEY NOT NULL,project_id INTEGER NOT NULL,latitude REAL NOT NULL,longitude REAL NOT NULL,altitude INTEGER,provider TEXT NOT NULL,CONSTRAINT ProjectFK FOREIGN KEY(project_id)REFERENCES Projects(project_id)ON DELETE RESTRICT ON UPDATE CASCADE)";
	public DbManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public String getDbPath() { return db.getPath(); }
	
	public ArrayList<Note> loadNotes(Project project)
	{
		ArrayList<Note> notes = new ArrayList<Note>();
		String queryNotes =
				"SELECT * FROM Notes WHERE project_id="+ project.getProjectID();
		Cursor cursor = queryDb(queryNotes);
		while(cursor.moveToNext())
		{
			long time = cursor.getLong(0);
			long time_loc = cursor.getLong(1);
			// project_id in Spalte 2 nicht erforderlich
			String subject = cursor.getString(3);
			String note = cursor.getString(4);
			String category = cursor.getString(5);
			// TODO: Note adden
			notes.add(new Note(time, subject, note, category,
					project, time_loc));
		}
		cursor.close();
		for(Note note : notes)
		{
		String queryLocations = "SELECT * FROM Locations WHERE time=" + note.time_loc ; // Variable muss public sein
		cursor = queryDb(queryLocations);
		if(cursor.moveToNext())
		{
			long time = cursor.getLong(0);
			// project_id in Spalte 1 nicht erforderlich
			double latitude = cursor.getDouble(2);
			double langitude = cursor.getDouble(3);
			int altitude = cursor.getInt(4);
			String provider = cursor.getString(5);
			// TODO: Location initialisieren
			note.location = new NoteLocation(time, latitude, langitude, altitude, provider, project);
		}
		cursor.close();
		}
		return notes;
	}
	
	
	
	
	
	public Project loadProject(String name)
	{
		Project project = null; // return-Objekt
		String query = "SELECT * FROM Projects WHERE name='"
		+ name+"';";
		Cursor cursor = myQuery(query);
		if (cursor== null)
			return null;
		if(cursor.moveToNext()) {
		long projectID = cursor.getLong(0);
		String subject = cursor.getString(1);
		String description = cursor.getString(2);
		Log.d(TAG, "loadProject: Projekt aus DB initialisiert");
		project = new Project(projectID, subject, description);
		} else {
		Log.d(TAG, "loadProject: Projekt \""
		+ name + "\" noch nicht in DB");
		}
		
		cursor.close();
		return project;
	}
	
	public Cursor myQuery(String sqlString) {
		if(db == null || !db.isOpen())
		db = this.getReadableDatabase();
		Cursor cursor = null;
		try {
		cursor = db.rawQuery(sqlString, null);
		Log.d(TAG, "Abfrage: " + sqlString);
		} catch(SQLException sqle) {
		Log.d(TAG, sqle.toString());
		}
		return cursor;
		}
	
	
	public void writeInDb(String sqlString)
	{
		if(db == null || !db.isOpen())
		db = this.getWritableDatabase();
		try {
		db.execSQL(sqlString);
		Log.d(TAG, "ausgefuehrt: " + sqlString);
		} catch(SQLException sqle) {
		Log.e(TAG, sqle.toString());
	}
	}
	public Cursor queryDb(String sqlString) {
	// TODO: implement!(Wiederholungsaufgabe 2.4)
		if(db == null || !db.isOpen())
			db = this.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.rawQuery(sqlString, null);
		if (cursor== null)
			return null;
		else return cursor;
	}
	public void closeDb() {
		db.close();
		Log.d(TAG, "Datenbank geschlossen (closeDB)");
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			db.execSQL(CREATE_PROJECTS);
			db.execSQL(CREATE_NOTES);
			db.execSQL(CREATE_LOCATIONS);
			Log.d(TAG, "DB erzeugt in: \"" + db.getPath() +
			"\"");
			} catch(SQLException sqle) {
			Log.d(TAG, sqle.toString());
			}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<String> findProjectNames()
	{
		ArrayList<String> projectNames =
		new ArrayList<String>();
		String query = "SELECT name FROM Projects ORDER BY name";
		Cursor cursor = queryDb(query);
		if (cursor== null)
		{
			projectNames.add("prj1");
			projectNames.add("prj2");
			projectNames.add("prj3");
			return projectNames;
		}
		while(cursor.moveToNext()) {
		projectNames.add(cursor.getString(0));
		}
		cursor.close();
		return projectNames;
	}


}
