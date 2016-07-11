package utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import db.Note;
import db.Project;

public class MailHelper {

	
	public static void sendProject(Project project,
			ArrayList<Note> projectNotes,
			Context context)
			{
			Intent eMailIntent = new Intent(Intent.ACTION_SEND);
			eMailIntent.setType("text/xml");
			eMailIntent.putExtra(Intent.EXTRA_EMAIL,
			new String[] {"###"});
			eMailIntent.putExtra(Intent.EXTRA_SUBJECT,
			"###");
			eMailIntent.putExtra(Intent.EXTRA_TEXT,
			"###");
			GpxGenerator generator = new GpxGenerator(
			project.getName(),
			project.description,
			projectNotes);
			Uri uri = generator.createGpxFile();
			eMailIntent.putExtra(Intent.EXTRA_STREAM, uri);
			context.startActivity(Intent.createChooser(
			eMailIntent,
			"Projekt exportieren"));
			}
}
