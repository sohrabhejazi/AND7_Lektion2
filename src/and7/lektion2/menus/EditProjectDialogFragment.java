package and7.lektion2.menus;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EditProjectDialogFragment extends DialogFragment {
	
	        private final String TAG = EditProjectDialogFragment.class.getSimpleName();
			public String termToAdd;
			public String description;
			private EditDialogListener dialogListener;
			
			
	public interface EditDialogListener {
			public void onDialogPositiveClick(DialogFragment dialog);
			public void onDialogNegativeClick(DialogFragment dialog);
	}
	@Override
	public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				dialogListener = (EditDialogListener) activity;
			} catch(Exception e) {
				throw new ClassCastException(activity.getClass()
				.getName() + " muss das Interface " +
				"\"EditProjectDialogFragment.EditDialogListener\""
				+ " implementieren!");
	}
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				AlertDialog.Builder builder =
				new AlertDialog.Builder(getActivity());
				LayoutInflater inflater =getActivity().getLayoutInflater();
				 final View sub_layout = inflater.inflate(and7.lektion2.components.R.layout.edit_project_dialog, null);
				builder.setView(sub_layout)
				.setTitle("Projektnamen ergänzen")
				.setPositiveButton(and7.lektion2.components.R.string.edit_project_positive,new DialogInterface.OnClickListener()
					{
					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						EditText etTermToAdd = (EditText)
						sub_layout.findViewById(
						and7.lektion2.components.R.id.et_name_project_dialog);
						EditText etDescription = (EditText)
						sub_layout.findViewById(
						and7.lektion2.components.R.id.et_desc_project_dialog);
						EditProjectDialogFragment
						.this.termToAdd =
						etTermToAdd.getText().toString().trim();
						EditProjectDialogFragment
						.this.description =
						etDescription.getText().toString().trim();
						Log.d(TAG, "Dialog \"edit_project\" durchlaufen");
						dialogListener.onDialogPositiveClick(
								EditProjectDialogFragment.this);
					}
					})
			.setNegativeButton(and7.lektion2.components.R.string.edit_project_negative,
					new DialogInterface.OnClickListener()
					{
					@Override
					public void onClick(DialogInterface dialog,
					int which)
					{
					dialogListener.onDialogNegativeClick(
								EditProjectDialogFragment.this);
					Log.d(TAG, "Dialog \"edit_project\" abgebrochen");
					}
					});
					return builder.create();
			}

}
