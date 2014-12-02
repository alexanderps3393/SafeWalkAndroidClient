package edu.purdue.del;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

/**
 * This fragment is the "page" where the user inputs information about the
 * request, he/she wishes to send.
 * 
 * @author YL
 */
public class ClientFragment extends Fragment implements OnClickListener {

	/**
	 * Activity which have to receive callbacks.
	 */
	private SubmitCallbackListener activity;

	private EditText name;

	private Spinner toSpinner;

	private Spinner fromSpinner;

	private RadioButton type0;

	private RadioButton type1;

	private RadioButton type2;

	static int toIndex = 0;

	static int fromIndex = 0;

	/**
	 * Creates a ProfileFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the submit Button.
	 * 
	 *            ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	 * 
	 * @return the fragment initialized.
	 */
	// ** DO NOT CREATE A CONSTRUCTOR FOR ProfileFragment **
	public static ClientFragment newInstance(SubmitCallbackListener activity) {
		ClientFragment f = new ClientFragment();

		f.activity = activity;
		return f;
	}

	/**
	 * Called when the fragment will be displayed.
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		final View view = inflater.inflate(R.layout.client_fragment_layout,
				container, false);

		/**
		 * Register this fragment to be the OnClickListener for the submit
		 * Button.
		 */
		view.findViewById(R.id.bu_submit).setOnClickListener(this);

		// TODO: import your Views from the layout here. See example in
		// ServerFragment.

		this.name = (EditText) view.findViewById(R.id.nameEditText);
		this.type0 = (RadioButton) view.findViewById(R.id.type0);
		this.type1 = (RadioButton) view.findViewById(R.id.type1);
		this.type2 = (RadioButton) view.findViewById(R.id.type2);
		this.toSpinner = (Spinner) view.findViewById(R.id.toSpinner);
		this.fromSpinner = (Spinner) view.findViewById(R.id.fromSpinner);

		toSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Get select item
				toIndex = toSpinner.getSelectedItemPosition();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		fromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Get select item
				fromIndex = fromSpinner.getSelectedItemPosition();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		type0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type1.setChecked(false);
				type2.setChecked(false);
				type0.setChecked(true);
			}
		});

		type1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type1.setChecked(true);
				type2.setChecked(false);
				type0.setChecked(false);
			}
		});

		type2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type1.setChecked(false);
				type2.setChecked(true);
				type0.setChecked(false);
			}
		});

		return view;
	}

	public String getName() {
		return name.getText().toString();
	}

	String[] toArray = { "LWSN", "CL50", "EE", "PMU", "PUSH", "*" };
	String[] fromArray = { "LWSN", "CL50", "EE", "PMU", "PUSH" };

	public String getTo() {
		return toArray[toIndex];
	}

	public String getFrom() {
		return fromArray[fromIndex];
	}

	public int getType() {
		if (type0.isChecked()) {
			return 0;
		} else if (type1.isChecked()) {
			return 1;
		} else if (type2.isChecked()) {
			return 2;
		} else {
			return -1;
		}
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		this.activity.onSubmit();
	}
}
