package and7.lektion2.components;

import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import client.LocationClient;
import utils.HtmlGenerator;
import utils.MailHelper;
import db.DbManager;
import db.Note;
import db.NoteLocation;
import db.Project;
import dialogs.WebserverDialogFragment;
import dialogs.WebserverDialogFragment.WebserverDialogListener;
import and7.lektion2.components.GatherActivity;
import and7.lektion2.components.LocationService;
import and7.lektion2.components.NewNoteMapActivity;
import and7.lektion2.menus.EditProjectDialogFragment;
import and7.lektion2.menus.SelectProjectDialogFragment;
import and7.lektion2.menus.EditProjectDialogFragment.EditDialogListener;
import and7.lektion2.menus.SelectProjectDialogFragment.SelectDialogListener;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GatherActivity extends ActionBarActivity implements EditDialogListener,  
   SelectDialogListener, WebserverDialogListener{

	private Intent serviceIntent;
	private static final String TAG = GatherActivity.class.getSimpleName();
	private LocationService service;
	private TextView tvOutput;
	private TextView tvHello;
	private EditText etSubject;
	private EditText etNote;
	private String newLine =System.getProperty("line.separator");
	private LocationService.LocationServiceBinder binder;
	private Project project;
	private DbManager dbManager;
	private SimpleDateFormat sdfDateTime =
	new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private int[] time = new int[] {9000, 4000, 4000, 4000};
	private int[] distance = new int[] {10, 25, 50, 100};
	private String editProjectTag = "edit_project";
	private String selectProjectTag = "select_project";
	private ArrayList<Note> projectNotes;
	private int notesIndex;
	private String tvNoteNumerDefaultText = "0/0";
	private  String tvNoteNumberText = tvNoteNumerDefaultText;
	private TextView tvNoteNumber;
	private boolean newNote;
	private HtmlGenerator htmlGenerator;
	private String webserverTag = "contact_webserver";
	
	//private String newLine =System.getProperty("line.separator");
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("project", project);
		outState.putSerializable("notes", projectNotes);
		outState.putBoolean("newNote", newNote);
		outState.putInt("notesIndex", notesIndex);
		NoteLocation lastLocation = null;
		if(binder != null)
		lastLocation = binder.getLastLocation();
		outState.putParcelable("lastLocation", lastLocation);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvNoteNumber = (TextView) findViewById(R.id.tv_note_number);
		tvNoteNumber.setText(tvNoteNumberText);
	    dbManager = new DbManager(this);
		serviceIntent = new Intent(this, LocationService.class); 
		project= new Project();
		/*
		if(savedInstanceState != null) // beim ersten Mal
	    {
		    project = savedInstanceState.getParcelable ("pro- ject");
		    projectNotes = (ArrayList<Note>)savedInstanceState.getSerializable("notes");
		    newNote = savedInstanceState.getBoolean("newNote");
		    notesIndex = savedInstanceState.getInt ("notes Index");
		    lastLocation = (NoteLocation) savedInstanceState
            getParcelable("lastLocation");
	    }*/
		this.useProjectDialog();
		long newId = project.getProjectID();
		long oldId = -1;
		Project temp = dbManager.loadProject(project.getName());
		if(temp != null) {
			useProjectDialog();
		//oldId = temp.getProjectID();
		//project = temp;
		}
		Log.d(TAG, "Project-IDs (new/DB): " + newId + "/" + oldId
		+ ((oldId == -1)?" - Projekt noch nicht in DB"
		: " - Projekt aus DB verwendet"));
		serviceIntent.putExtra("project", project);
		//this.startService(serviceIntent);
		this.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
		final Button btQuit = (Button)this.findViewById(R.id.bt_quit);
		tvHello = (TextView) this.findViewById(R.id.tv_hello);
 		tvHello.append(" " + project.getName());
		//tvHello.setText("bla bla bla bla ");
		tvOutput = (TextView) this.findViewById(R.id.tv_output);
		etSubject= (EditText) this.findViewById(R.id.et_subject);
		etNote= (EditText) this.findViewById(R.id.et_note);
		final Button btSave = (Button) findViewById(R.id.bt_save);
		final Button btNewNote = (Button) findViewById(R.id.bt_new_note);
		btNewNote.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				String subject = etSubject.getText().toString();
				String note = etNote.getText().toString();
				String category = "pin";
				
				NoteLocation lastLocation =
				binder.getLastLocation();
				Note retNote = new Note(subject, note, category,
				project, lastLocation);
				if(projectNotes == null)
				projectNotes = new ArrayList<Note>();
				projectNotes.add(retNote);
				notesIndex = projectNotes.size()-1;
				newNote = true;
				int nextIndex = projectNotes.size() + 1;
				tvNoteNumberText = nextIndex + "/"
				+ nextIndex; // wird in "showNote" gezeigt
				if(!saveNote(retNote))
	        		updateNote(retNote);
				showNote(nextIndex-1);
			// bewirkt Rücksetzung der Felder auf leere Strings
			}
		});
		final Button  btFirst= (Button) findViewById(R.id.bt_nav_first);
		btFirst.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View view)
		{
		// TODO: implement
			notesIndex = 0;
			showNote(0);
			
		}
		});
		final Button btLeft = (Button) findViewById(R.id.bt_nav_left);
		btLeft.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View view)
		{
		// TODO: implement
			if(notesIndex > 0){
				--notesIndex;
				showNote(0);
			}
		}
		});
		final Button  btRight= (Button) findViewById(R.id.bt_nav_right);
		btRight.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View view)
		{
		// TODO: implement
			if(notesIndex <= projectNotes.size()-2)
				showNote(++notesIndex);
		}
		});
		final Button  btLast= (Button) findViewById(R.id.bt_nav_last);
		btLast.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View view)
		{
		// TODO: implement
			
			notesIndex = projectNotes.size()-1;
			showNote(0);
		}
		});
		
		Button btLocation = (Button)
				this.findViewById(R.id.bt_location);
		        btLocation.setOnClickListener(new OnClickListener()
				{
				@Override
				public void onClick(View v) {
					    
					    //projectNotes.get(0).location.geoPoint.longitude=16.22;
					    		/*projectNotes.get(1).location.geoPoint.latitude=
							    projectNotes.get(1).location.geoPoint.longitude=
							    		projectNotes.get(2).location.geoPoint.latitude=
									    projectNotes.get(2).location.geoPoint.longitude=*/
						Note note = createNote();
						if(note != null) {
						saveNote(note);
						Intent intent = new Intent(GatherActivity.this,
						NewNoteMapActivity.class);
						/*intent.putExtra("latitude",note.location.geoPoint.latitude);
						intent.putExtra("longitude",note.location.geoPoint.longitude);
						intent.putExtra("subject",etSubject.getText().toString());
						intent.putExtra("note", note.note);
						//GatherActivity.this.startActivity(intent);
						//intent.putExtra("prjNotes", projectNotes.get(notesIndex)); 
						Note Pnote= projectNotes.get(1);
						//Pnote.location.geoPoint = new LatLng(38.20, 15.20);
						Pnote.setGeo(new LatLng(38.20, 15.20));
					    projectNotes.set(1, Pnote);
					    Pnote= projectNotes.get(2);
					    Pnote.location.geoPoint = new LatLng(58.20, 17.20);
					    projectNotes.set(2, Pnote);*/
						intent.putExtra("prjNotes", projectNotes);
						intent.putExtra("index", notesIndex);
						
						try {
							startActivity(intent);
							} catch(Exception e) {
							Log.e(TAG, e.toString());
							}
				}
				}
				});
		/*btSave.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View arg0)
		{
		//tvOutput.setText("Sie wollen speichern? Danke für den Hinweis, er wird alsbald mit AND07C erledigt!");
			Note note = createNote();
			if(note != null) {
			dbManager.writeInDb(project.getInsertString());
			dbManager.writeInDb(note.getInsertString());
			dbManager.writeInDb(note.location.getInsertString());
			tvOutput.setText("Notiz vom "
			+ sdfDateTime.format(new Date(note.getTime()))
			+ " gespeichert.");
			}
		}
		});*/
		        
		btSave.setOnClickListener(new OnClickListener() {
		        	@Override
		        	public void onClick(View arg0)
		        	{
			        	newNote=true;
		        		Note note = createNote();
			        	if(note != null) {
			        	if(!saveNote(note))
			        		updateNote(note);
		        	}
		        	}
		        	});
		
		btQuit.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Fall "Beenden"
						if(btQuit.getText().equals(
						getResources().getString(R.string.bt_quit)))
						{
						binder.stopTimer();
						endService("ButtonQuit.onClick");
						long diff = LocationService.Duration;
						int Seconds = (int)diff % 60;  
						int Hours = (int)diff / 3600;
						int Minutes = (int)(diff-Hours*3600) /60;         
						tvOutput.setText("GPS-Dienst beendet");
						tvOutput.append(newLine +LocationService.Counter + " GPS-Abrufe, Laufzeit " 
						+Hours+":"+Minutes+":"+Seconds);
						binder = null;
						btQuit.setText(getResources()
						.getString(R.string.bt_restart));
						btQuit.setTextColor(getResources()
						.getColor(R.color.warning));
						// Fall "Neustart"
						} else {
						bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
						tvOutput.setText("GPS-Dienst läuft wieder");
						btQuit.setText(getResources()
						.getString(R.string.bt_quit));
						btQuit.setTextColor(getResources()
						.getColor(R.color.text));
						}
						}
				});
	}
	private ServiceConnection serviceConnection =
			new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name,
		IBinder service) {
		binder =
		(LocationService.LocationServiceBinder)service;
		}
			@Override
		public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
				Log.d(TAG,
						"ServiceConnection.onServiceDisconnected von "
						+ name.getClassName() + " aufgerufen.");
				
			}
			};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.endService("onDestroy");
	}
/*	private void endService(String caller) {
		Log.d(TAG, "service=" + service);
		if(service != null) {
		this.unbindService(serviceConnection);
		this.stopService(serviceIntent);
		service = null;
		Log.d(TAG, "Service durch " + caller + " beendet");
		} else
		Log.d(TAG, "endService: Service (IBinder) ist null");
	} */
	
	private void endService(String caller) {
		Log.d(TAG, "service=" + binder);
		if(binder != null) {
		this.unbindService(serviceConnection);
		dbManager.closeDb();
		this.stopService(serviceIntent);
		binder = null;
		Log.d(TAG, "Service durch " + caller + " beendet");
		} else
		Log.d(TAG, "endService: Binder ist null");
		}

	private Note createNote()
	{
		String subject = etSubject.getText().toString();
		String note = etNote.getText().toString();
		String category = "pin";
		if(newNote)
		{
			if(binder == null)
			{
				tvOutput.setText("Der GPS-Dienst wurde beendet"
				+ " - bitte zunächst neu starten.");
				return null;
		    }
			
			NoteLocation lastLocation =binder.getLastLocation();
			
			if (lastLocation == null)
			{
				tvOutput.setText("Es liegen noch keine GPS-"
				+ "Daten vor - bitte erneut versuchen");
				return null;
			}
			tvOutput.setText(lastLocation.toString());
			Note retNote = new Note(subject, note, category,
			project, lastLocation);
			if(projectNotes == null)
			projectNotes = new ArrayList<Note>();
			projectNotes.add(retNote);
			notesIndex = projectNotes.size()-1;
			showNote(notesIndex);
			newNote = false;
			return retNote;
		} 
		else {
		//if(projectNotes == null) projectNotes = new ArrayList<Note>();
		Note retNote = projectNotes.get(notesIndex);
		retNote.subject = subject;
		retNote.note = note;
		retNote.category = category;
		return retNote;
		}
		    /*if(binder == null)
			{
				tvOutput.setText("Der GPS-Dienst wurde beendet -bitte zunächst neu starten.");
				return null;
			}
			NoteLocation lastLocation = binder.getLastLocation();
			if (lastLocation == null)
			{
				tvOutput.setText("Es liegen noch keine GPS-Daten vor - bitte erneut versuchen");
				return null;
			}
			tvOutput.setText(lastLocation.toString());
			String subject = etSubject.getText().toString();
			String note = etNote.getText().toString();
			String category = "pin";
			return new Note(subject, note, category,
			project, lastLocation);*/
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.gather, menu);
		MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.optionsmenu, menu);
		inflater.inflate(R.menu.gather, menu);
		menu.findItem(R.id.m_edit_project).setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_edit));
		menu.findItem(R.id.m_select_project).setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_set_as));
		menu.findItem(R.id.m_gps).setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_mylocation));
		menu.findItem(R.id.action_settings).setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_more)); 
		menu.findItem(R.id.del).setIcon(getResources().getDrawable(
                android.R.drawable.ic_menu_delete ));
		menu.findItem(R.id.m_backup_project)
		.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_upload));
		//menu.findItem(R.id.m_del).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_more)); 
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		DialogFragment dialog;
		android.app.FragmentManager fragmentManager;
		switch(itemId)
		{
		    
		case R.id.m_webserver:
			if(projectNotes == null) {
				tvOutput.setText("Bitte vor Kontakt mit dem Webserver zunächst ein Projekt auswählen oder eine Notiz erstellen und speichern.");
				return false;
				} else {
				dialog = new WebserverDialogFragment();
				fragmentManager = getFragmentManager();
				dialog.show(fragmentManager, webserverTag);
				return true;
				}
		
		case R.id.del:
		    
		    	Note note = projectNotes.get(notesIndex);
		    	int size = projectNotes.size();
		    	if(size==0)
		    		return false;
		        dbManager.writeInDb(note.getdeleteString());
		        projectNotes.remove(note);
		        if(size==1)
		        {
		        	etSubject.setText("");
					etNote.setText("");
					tvNoteNumberText = 0 + "/"+ projectNotes.size();
				    tvNoteNumber.setText(tvNoteNumberText);
				    dbManager.writeInDb(project.getdeleteString());
		        	return true;
		        }
		        else if(size==notesIndex+1)
		        notesIndex--;
		        showNote(notesIndex);
		        return true;
		   case R.id.m_edit_project:
				dialog = new EditProjectDialogFragment();
				fragmentManager = getFragmentManager();
				dialog.show(fragmentManager, editProjectTag);
				return true;
			case R.id.m_select_project:
				dialog = new SelectProjectDialogFragment();
				fragmentManager = getFragmentManager();
				dialog.show(fragmentManager, selectProjectTag);
				return true;
			case R.id.action_settings:
				Log.d(TAG, "Eventhandling zu \"action-settings\" noch nicht programmiert");
				return false;
			case R.id.gps_1:
				return setGpsInterval(0, item);
			case R.id.gps_2:
				return setGpsInterval(1, item);
			case R.id.gps_3:
				return setGpsInterval(2, item);
			case R.id.gps_4:
				return setGpsInterval(3, item);
			case R.id.m_backup_project:
				MailHelper.sendProject(project, projectNotes, this);
				return true;
			case R.id.m_show_osm:
				if(htmlGenerator == null)
					htmlGenerator = new HtmlGenerator(this);
				if(projectNotes != null)
				{
					//htmlGenerator.getOsmHtmlPath(projectNotes,
					//notesIndex, R.drawable.pin);
					String filePath = htmlGenerator
							.getOsmHtmlPath(projectNotes, notesIndex,
							R.drawable.pin);
							if(filePath != null)
							this.displayOnOSM(filePath);
							else
							tvOutput.setText("Fehler beim Zugriff auf \"osm.html\"");
				} else
					tvOutput.setText("Bitte zunächst ein Projekt erstellen oder auswählen");
				return true;
			default:
				return super.onOptionsItemSelected(item);
	    }
	}
	// Hilfsmethode zu den GPS-Vorgaben:
	private boolean setGpsInterval(int modus, MenuItem item)
	{
		binder.setGpsInterval(time[modus], distance[modus]);
		item.setChecked(true);
		return true;
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		String output = "";
		if (dialog instanceof EditProjectDialogFragment)
		{
			EditProjectDialogFragment fragment =(EditProjectDialogFragment) dialog;
			if(project != null) {
				project.appendToName(fragment.termToAdd);
				project.description = fragment.description;
				dbManager.writeInDb(project.getUpdateString());
				tvHello.setText("aktuelles Projekt: "
				+ project.getName());
				output="Projektname geändert, Projektbeschreibung: "
				+ ((project.description.equals(""))?
				"Keine" : project.description);
			} else
				output="Das Projekt muss vor Namenserweiterung mindestens eine Notiz enthalten";
		}
		
		else if (dialog instanceof SelectProjectDialogFragment)
		{
			SelectProjectDialogFragment fragment =(SelectProjectDialogFragment) dialog;
			//tvHello.setText("aktuelles Projekt: "+ fragment.projectName);
			project = dbManager.loadProject(fragment.projectName);
						if(project != null) {
							tvHello.setText("aktuelles Projekt: "
							+ project.getName());
							output = "neues Projekt geladen";
							//projectNotes = dbManager.loadNotes(project);
							this.loadNotes();
							GatherActivity.this.showNote(0);
					    }
		}
		// TODO else-Fall: SelectProjectDialogFragment
		dialog.dismiss();
		tvOutput.setText(output);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		tvOutput.setText(dialog.toString() + " abgebrochen");
		
	}
	
	private void showNote(int nextindex)
	{
		if(projectNotes != null) // sicherheitshalber
		{
			int size = projectNotes.size();
			if(size > 0 && nextindex >= 0 && notesIndex < size)
			{
				etSubject.setText(projectNotes.get(notesIndex).subject);
				etNote.setText(projectNotes.get(notesIndex).note);
				tvNoteNumberText = notesIndex+1 + "/"+ projectNotes.size();
			} else // Eingabefelder zurücksetzen {
			{
				etSubject.setText("");
				etNote.setText("");
			}
			tvNoteNumber.setText(tvNoteNumberText);
		} else
			Log.e(TAG, ".showNotes(int): \"projectNotes\" ist null (sollte aber hier nicht eintreten)");
	}
	public ArrayList<String> getProjectNames() {
		return dbManager.findProjectNames();
		}
		
	private boolean saveNote(Note note)
	{
		if(!project.isPersistent)
		{
			dbManager.writeInDb(project.getInsertString());
			project.isPersistent = true;
		}
		if(!note.isPersistent)
		{
			dbManager.writeInDb(note.getInsertString());
			dbManager.writeInDb(note.location.getInsertString());
			note.isPersistent = true;
			tvOutput.setText("Notiz vom "+ sdfDateTime.format(new Date(note.getTime()))
			+ " gespeichert.");
			return true;
		} else
			return false;
	}
	
	private void updateNote(Note note)
	{
		if(note.isPersistent)
		{
			dbManager.writeInDb(note.getUpdateString());
			tvOutput.setText("Notiz vom "
			+ sdfDateTime.format(new Date(
			note.getTime())) + " aktualisiert.");
		}
	}
	
	private void loadNotes() {
		projectNotes = dbManager.loadNotes(project);
		newNote = false;
	}
	
	private void useProjectDialog(){
		
		  Builder builder = new AlertDialog.Builder(this);
	      builder.setTitle("Vorhandenes Projekt \""+ project.getName()+"\" verwenden?");
	      //builder.setMessage("This ends the activity");
	      //builder.setCancelable(true);
	      builder.setPositiveButton("Ja", new  DialogInterface.OnClickListener()
			{
	  		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					return;
				}
		  	});
	      builder.setNegativeButton("Nein", new  DialogInterface.OnClickListener()
			{
		  		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					project.appendToName(Long.toString(project.getProjectID()));
					
				}
		  	});
	      AlertDialog dialog = builder.create();
	      dialog.show();
		
	}
	private void displayOnOSM(String filePath)
	{
		Intent intent = new Intent(this, OsmWebView.class);
		intent.putExtra("osm_access", filePath);
		startActivity(intent);
	}

	@Override
	public void onDialogSendLocation(WebserverDialogFragment dialog) {
		// TODO Auto-generated method stub
		// bei Fehlern gibt es nur eine Nachricht
		/*if(!dialog.message.equals(""))
		tvOutput.setText(dialog.message);
		else if(projectNotes != null) {
		LocationClient locationClient;
		try {
		locationClient = new LocationClient(dialog.host);
		String serverResponse = locationClient
		.saveLocation(projectNotes.get(notesIndex),
		dialog.userID);
		if(serverResponse != null)
		tvOutput.setText(serverResponse);
		else
		tvOutput.setText("Keine Antwort vom Webserver");
		} catch (URISyntaxException e) {
		tvOutput.setText("Fehlerhafte Host-Angabe: "
		+ e.getMessage());
		}
		}*/
		//WebserverDialogFragment WWWsave = new WebserverDialogFragment();
		new LocationSaveTask().execute(dialog);
	}

	@Override
	public void onDialogCallLocations(WebserverDialogFragment dialog) {
		// TODO Auto-generated method stub
		/*if(!dialog.message.equals(""))
			tvOutput.setText(dialog.message);
			else if(projectNotes != null) {
			LocationClient locationClient;
			try {
			locationClient = new LocationClient(dialog.host);
			// die Liste der Note-Objekte vom Server:
			ArrayList<Note> locList = locationClient
			.getNextLocations(projectNotes.get(notesIndex),
			dialog.userID);
			if(locList != null) {
				Intent intent = new Intent(GatherActivity.this,
				NewNoteMapActivity.class);
				intent.putExtra("notes", locList);
				// die erste Location vom Server ist die
				// nächstliegende, daher Indexwert 0:
				intent.putExtra("index", 0);
				GatherActivity.this.startActivity(intent);
				tvOutput.setText(locList.size() + " Locations vom Server übermittelt");
			} else
				tvOutput.setText("Keine Daten vom Webserver");
			} catch (URISyntaxException e) {
				tvOutput.setText("Fehlerhafte Host-Angabe: " + e.getMessage());
			}
			}
		*/
		//WebserverDialogFragment WWWsave = new WebserverDialogFragment();
		new LocationsGetTask().execute(dialog);
	}
	
	class LocationSaveTask extends AsyncTask<WebserverDialogFragment, Void, String>
	{
		@Override
		protected void onPreExecute() {
			tvOutput.setText("Speichern der aktuellen Notiz auf dem Webserver gestartet...");
		}
		@Override
		protected String doInBackground(WebserverDialogFragment... params)
			{
				String returnString;
				if(!params[0].message.equals(""))
					returnString = params[0].message;
				else if(projectNotes != null)
				{
					LocationClient locationClient;
				try {
					locationClient = new LocationClient(
							params[0].host);
					String serverResponse = locationClient
							.saveLocation(projectNotes.get(notesIndex),
									params[0].userID);
				if(serverResponse != null)
				{
					
					if(serverResponse.startsWith("<html>"))
					 {
					     Intent webIntent = new Intent(GatherActivity.this, DefaultWebView.class); 
					     webIntent.putExtra("htmlResult", serverResponse);
					     GatherActivity.this.startActivity(webIntent);
					     returnString = "Location auf Server gespeichert";
					 }
					else 
						returnString = serverResponse;
				}
				else
					returnString = "Keine Antwort vom Webserver";
				} catch (URISyntaxException e) {
					returnString = "Fehlerhafte Host-Angabe: "
							+ e.getMessage();
				}
				} else
					returnString = "Bitte erst ein Projekt erstellen oder  laden";
				return returnString;
		}
		@Override
		protected void onPostExecute(String result)
		{
			if(!result.startsWith("<html>"))
			
			tvOutput.setText(result);
		}
	}
	
	class LocationsGetTask extends AsyncTask<WebserverDialogFragment, Void, String>
	{
		@Override
		protected void onPreExecute() {
		tvOutput.setText("Abruf von Notizen auf dem Webserver gestartet...");
		}
		@Override
		protected String doInBackground(
		WebserverDialogFragment... params)
		{
			String returnString;
		if(!params[0].message.equals(""))
			returnString = params[0].message;
		else if(projectNotes != null)
		{
		LocationClient locationClient;
		try {
			locationClient = new LocationClient(
			params[0].host);
			ArrayList<Note> locList = locationClient
			.getNextLocations(projectNotes
			.get(notesIndex), params[0].userID);
			if(locList != null) {
			Intent intent = new Intent(GatherActivity.this, NewNoteMapActivity.class);
			intent.putExtra("notes", locList);
			// die erste Location vom Server ist die
			// nächstliegende, daher Indexwert 0:
			intent.putExtra("index", 0);
			GatherActivity.this.startActivity(intent);
			returnString = locList.size() + " Locations vom  Server übermittelt";
		} else
			returnString = "Keine Daten vom Webserver";
		} catch (URISyntaxException e) {
			returnString = "Fehlerhafte Host-Angabe: "
					+ e.getMessage();
		}
		} else
			returnString = "Bitte erst ein Projekt erstellen oder laden";
			return returnString;
		}
		@Override
		protected void onPostExecute(String result)
		{
			tvOutput.setText(result);
		}
	}
		
}
