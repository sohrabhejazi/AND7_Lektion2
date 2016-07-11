package dialogs;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

import and7.lektion2.components.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class WebserverDialogFragment extends DialogFragment {

	private final String TAG =
			WebserverDialogFragment.class.getSimpleName();
	private EditText etUserId;
	private EditText etHost;
	public int userID;
	public String host;
	private Preferences prefs;
	private final String keyUserId = "keyUserId";
	private final String keyHost = "keyHost";
	private final int defaultUserId = 1234567890;
	//private final String defaultHost = "http://localhost:8080/";
	private final String defaultHost = "http://10.0.2.2:8080";
	public String message = "";
	// Aufruf am Ende von onCreateDialog:
	private void initializeFromPreferences()
	{
		prefs = Preferences.userNodeForPackage(
		WebserverDialogFragment.class);
		userID = prefs.getInt(keyUserId, defaultUserId);
		etUserId.setText("" + userID);
		host = prefs.get(keyHost, defaultHost);
		etHost.setText(host);
	}
			// Interface für den Datenaustausch mit dem Dialog:
	public interface WebserverDialogListener
			{
				public void onDialogSendLocation(
				WebserverDialogFragment dialog);
				public void onDialogCallLocations(
				WebserverDialogFragment dialog);
			}
			// interne Listener-Instanz:
	private WebserverDialogListener dialogListener;
			@Override
			public void onAttach(Activity activity)
			{
			super.onAttach(activity);
			// Verifizieren, dass die aufrufende Activity das
			// obige Interface implementiert:
			try {
			dialogListener = (WebserverDialogListener)activity;
			} catch(Exception e) {
			throw new ClassCastException(
			activity.getClass().getName()
			+ " muss das Interface \"WebserverDialogFragment .WebserverDialogListener\" implementieren!");
			}
			}
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
			AlertDialog.Builder builder =
			new AlertDialog.Builder(getActivity());
			LayoutInflater inflater =
			getActivity().getLayoutInflater();
			final View subLayout = inflater.inflate(
			R.layout.webserver_dialog, null);
			builder.setView(subLayout)
			.setTitle("Kontakt mit Webserver")
			.setPositiveButton(R.string.bt_send, new DialogInterface.OnClickListener()
			{
			@Override
			public void onClick(DialogInterface dialog,
			int which)
			{
				checkValues();
				dialogListener.onDialogSendLocation(
						WebserverDialogFragment.this);
			}
			})
			.setNegativeButton(R.string.bt_call,
			new DialogInterface.OnClickListener()
			{
			@Override
			public void onClick(DialogInterface dialog,
			int which)
			{
				checkValues();
				dialogListener.onDialogCallLocations(
						WebserverDialogFragment.this);
			}
			});
			
			etUserId = (EditText) subLayout.findViewById(
					R.id.et_userid);
			etHost = (EditText) subLayout.findViewById(
					R.id.et_uri);
			this.initializeFromPreferences();
			return builder.create();
			}
			@Override
			public void onDestroy() {
				prefs.putInt(keyUserId, userID);
				prefs.put(keyHost, host);
				super.onDestroy();
			}
			private void checkValues()
			{
			String temp = etUserId.getText().toString().trim();
			try {
			userID = Integer.parseInt(temp);
			} catch(NumberFormatException e) {
			message = "Die User-ID wurde im Dialog nicht korrekt geändert.Eingabe=\"" + e.getMessage() + "\"";
			}
			temp = etHost.getText().toString().trim();
			try {
				new URL(temp);
				// Zuweisung, wenn Konstruktoraufruf geklappt hat:
				host = temp;
				if(!host.endsWith("/"))
				host +="/";
				} catch(MalformedURLException e) {
				if(!message.equals(""))
				message += "\n";
				message += "Die Host-Adresse wurde im Dialog nicht korrekt eingegeben (" + e.getMessage() + ")";
				}
				}
	}
	