package edu.purdue.del;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SubmitCallbackListener,
		StartOverCallbackListener {

	/**
	 * The ClientFragment used by the activity.
	 */
	private ClientFragment clientFragment;

	/**
	 * The ServerFragment used by the activity.
	 */
	private ServerFragment serverFragment;

	/**
	 * UI component of the ActionBar used for navigation.
	 */
	private Button left;
	private Button right;
	private TextView title;

	/**
	 * Called once the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		this.clientFragment = ClientFragment.newInstance(this);
		this.serverFragment = ServerFragment.newInstance();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Creates the ActionBar: - Inflates the layout - Extracts the components
	 */
	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.action_bar, null);

		// Set up the ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

		// Extract the UI component.
		this.title = (TextView) actionBarLayout.findViewById(R.id.tv_title);
		this.left = (Button) actionBarLayout.findViewById(R.id.bu_left);
		this.right = (Button) actionBarLayout.findViewById(R.id.bu_right);
		this.right.setVisibility(View.INVISIBLE);

		return true;
	}

	/**
	 * Callback function called when the user click on the right button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onRightClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.client));
		this.left.setVisibility(View.VISIBLE);
		this.right.setVisibility(View.INVISIBLE);
		ft.replace(R.id.fl_main, this.clientFragment);
		ft.commit();
	}

	/**
	 * Callback function called when the user click on the left button of the
	 * ActionBar.
	 * 
	 * @param v
	 */
	public void onLeftClick(View v) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(this.getResources().getString(R.string.server));
		this.left.setVisibility(View.INVISIBLE);
		this.right.setVisibility(View.VISIBLE);
		ft.replace(R.id.fl_main, this.serverFragment);
		ft.commit();

	}

	/**
	 * Callback function called when the user click on the submit button.
	 */
	@Override
	public void onSubmit() {
		// TODO: Get client info via client fragment

		String toString = clientFragment.getTo();
		String fromString = clientFragment.getFrom();
		int type = clientFragment.getType();
		String name = clientFragment.getName();
		
		boolean goBack = false;

		// Server info
		String host = this.serverFragment.getHost(getResources().getString(
				R.string.default_host));
		if (getResources().getString(R.string.default_port).equals("")) {
			goBack = true;
		}
		int port = 2;
		String error = "";
		try {
			port = this.serverFragment.getPort(Integer.parseInt(getResources()
					.getString(R.string.default_port)));
		} catch (Exception e) {
			error += "Port is Empty";
		}

		// TODO: sanity check the results of the previous two dialogs

		if (name.contains(",")) {
			error += "name can't have a comma\n";
		}
		if (name.equals("")) {
			error += "name can't be empty\n";
		}
		if (toString.equals(fromString)) {
			error += "to and from cannot be the same\n";
		}
		if (toString.equals("*") && type != 2) {
			error += "to go to '*' you must be a volunteer\n";
		}
		if (host.equals("")) {
			error += "host cannot be empty\n";
		}
		if (host.contains(" ")) {
			error += "host cannot have a space";
		}
		if (port < 1 || port > 65535) {
			error += "port must be between 1 and 65535\n";
		}
		if (type == -1) {
			error += "cannot leave preferences empty\n";
		}
		
		if (!error.equals("")) {
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(error);
            builder1.setCancelable(true);
            builder1.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
		
			return;
		}
		

		// TODO: Need to get command from client fragment
		String command = String.format("%s,%s,%s,%d", name, fromString,
				toString, type);

		FragmentTransaction ft = getFragmentManager().beginTransaction();

		this.title.setText(getResources().getString(R.string.match));
		this.left.setVisibility(View.INVISIBLE);
		this.right.setVisibility(View.INVISIBLE);

		// TODO: You may want additional parameters here if you tailor
		// the match fragment
		MatchFragment frag = MatchFragment.newInstance(this, host, port,
				command);

		ft.replace(R.id.fl_main, frag);
		ft.commit();

	}

	/**
	 * Callback function call from MatchFragment when the user want to create a
	 * new request.
	 */
	@Override
	public void onStartOver() {
		onRightClick(null);
	}

}
