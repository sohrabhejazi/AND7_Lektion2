package and7.lektion2.menus;

import java.util.ArrayList;

import and7.lektion2.components.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class SelectProjectDialogFragment extends DialogFragment
{
private final String TAG = SelectProjectDialogFragment
.class.getSimpleName();
private SelectDialogListener dialogListener;
public String projectName;
//Inneres Listener-Interface
//(implementiert in "GatherActivity"):
public interface SelectDialogListener {
	public void onDialogPositiveClick(
	DialogFragment dialog);
	public void onDialogNegativeClick(
	DialogFragment dialog);
	//weitere Methode zur Beschaffung der
	//Projektnamen:
	public ArrayList<String> getProjectNames();
}
@Override
public void onAttach(Activity activity) {
	super.onAttach(activity);
	try {
		dialogListener = (SelectDialogListener) activity;
	} 
	catch(Exception e) {
		throw new ClassCastException(
		activity.getClass().getName()
		+ " muss das Interface \"SelectProjectDialogFragment .SelectDialogListener\" implementieren!");
	}
}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState)
{
// Das Sub-Layout beschaffen:
AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
LayoutInflater inflater = getActivity().getLayoutInflater();
final View sub_layout = inflater.inflate(and7.lektion2.components.R.layout.select_project_dialog, null);
// den Adapter konfigurieren
final AutoCompleteTextView actvProject = (AutoCompleteTextView) sub_layout.findViewById(and7.lektion2.components.R.id.tv_complete_select);
ArrayList<String> projectNames =dialogListener.getProjectNames();
ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
		and7.lektion2.components.R.layout.list_item_projects, projectNames);
actvProject.setAdapter(adapter);
builder.setView(sub_layout)
	.setTitle("Gespeichertes Projekt auswählen")
	.setPositiveButton(and7.lektion2.components.R.string.edit_project_positive, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog,
			int which)
			{
				projectName = actvProject.getText().toString();
				dialogListener.onDialogPositiveClick(SelectProjectDialogFragment.this);
				Log.d(TAG, "Dialog \"select_project\" durchlaufen");
			}
		})
	.setNegativeButton(
			and7.lektion2.components.R.string.edit_project_negative,
new DialogInterface.OnClickListener()
{
	@Override
	public void onClick(DialogInterface dialog,
	int which)
	{
	dialogListener.onDialogNegativeClick(
	SelectProjectDialogFragment.this);
	Log.d(TAG, "Dialog \"select_project\" abgebrochen");
	}
	});
	return builder.create();
}

}
