package and7.lektion2.components;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import db.Note;
import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
//xxx
public class NewNoteMapActivity extends ActionBarActivity implements
OnMarkerClickListener{
	private MapView mapView;
	private final String TAG = getClass().getSimpleName();
	private GoogleMap googleMap;
	private LatLng geoPoint;
	private String subject, note;
	private ArrayList<Note> projectNotes;
	//xxx
	private Note	noteArr;
	private int notesIndex;
	private String newLine =System.getProperty("line.separator");
	
	@Override
	protected void onStart()
	{
		super.onStart();
			
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newnotemap);
		mapView = (MapView) this.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null) {
			//project = intent.getExtras().getParcelable("project");
		//noteArr=bundle.getParcelable("prjNotes");
			//xxx
		projectNotes=bundle.getParcelableArrayList("prjNotes");
		notesIndex = bundle.getInt("index");
		//geoPoint =projectNotes.get(notesIndex).location.geoPoint ;
		/*geoPoint =new LatLng(bundle.getDouble("latitude"),bundle.getDouble("longitude"));
		subject = bundle.getString("subject");
		//note = bundle.getString("note");
		subject = subject.equals("")? "Info" : subject;
		note = bundle.getString("note");
		note = note.equals("")? "Keine Notiz" : note;*/
		//subject=projectNotes.get(notesIndex).subject;
		//subject = subject.equals("")? "Info" : subject;
		//note=projectNotes.get(notesIndex).note ;
		//note = note.equals("")? "Keine Notiz" : note;
		} else
		Log.d(TAG, "noch keine Geoposition verfügbar");
		// 3. Map initialisieren
		try{
		MapsInitializer.initialize(this);
		} catch (Exception e) {
		Log.e(TAG, e.toString());
		}
		// 4. Map verfügbar machen
		googleMap = mapView.getMap();
		if(googleMap != null)
		{
			/*googleMap.setInfoWindowAdapter(
					new MarkerInfoWindow());*/
			//xxx
			googleMap.setOnMarkerClickListener(this);
			Log.d(TAG, "Map in onCreate verfügbar");
		}
		else
		Log.d(TAG,
		"Map in onCreate n i c h t verfügbar");
		// 5. Marker adden
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.pin);
		for(int i=0; i<projectNotes.size();i++)
 		{	
			String noteData="Note: "+ projectNotes.get(i).note + newLine + 
					"Category: "+ projectNotes.get(i).category + newLine +
					 projectNotes.get(i).location.getGeo() ;
			googleMap.addMarker(new MarkerOptions()
			.position(projectNotes.get(i).location.geoPoint)
			.title(projectNotes.get(i).subject)
			.snippet(noteData)
			.icon(bitmap)
			.anchor(0.5F,0.5F));
			
 		}
			// 5. Auf Marker zentrieren und Zoomen
			try {
				CameraUpdate update = CameraUpdateFactory
				.newCameraPosition(CameraPosition
				.fromLatLngZoom(projectNotes.get(notesIndex).location.geoPoint, 10));
				Log.d(TAG, "camera moved to the location");
				googleMap.moveCamera(update);
				} 
			
			catch(Exception e) {
				Log.e(TAG, e.toString());
				}
	 		}
	   
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gather, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
	super.onDestroy();
	mapView.onDestroy();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	mapView.onSaveInstanceState(outState);
	}
	@Override
	public void onLowMemory() {
	mapView.onLowMemory();
	}
	@Override
	protected void onResume() {
	super.onResume();
	mapView.onResume();
	}
	@Override
	protected void onPause() {
	super.onPause();
	mapView.onPause();
	}
	
	/*public class MarkerInfoWindow implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker marker)
		{
		LayoutInflater inflater =NewNoteMapActivity.this.getLayoutInflater();
		View infoWindow = inflater.inflate(and07c.lektion2.components.R.layout.info_window_layout, null);
		TextView tvTitle = (TextView) infoWindow
		.findViewById(R.id.iw_title);
		tvTitle.setText(marker.getTitle());
		TextView tvSnippet = (TextView) infoWindow
		.findViewById(R.id.iw_snippet);
		tvSnippet.setText(marker.getSnippet());return infoWindow;
			
		}
		@Override
		public View getInfoWindow(Marker marker)
		{
		// Auto-generated method stub - wird beibehalten
		return null;
		}

	}*/
//xxx
	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
	      builder.setTitle(marker.getTitle() );
	      builder.setMessage(marker.getSnippet());
	      builder.setNeutralButton  ("OK", new  DialogInterface.OnClickListener()
			{
	  		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					return;
				}
		  	});
	     
	      AlertDialog dialog = builder.create();
	      dialog.show();

		return true;
	}
	
}
