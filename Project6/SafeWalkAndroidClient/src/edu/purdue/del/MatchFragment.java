package edu.purdue.del;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Fragment;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This fragment is the "page" where the application display the log from the
 * server and wait for a match.
 * 
 * @author YL
 */
public class MatchFragment extends Fragment implements OnClickListener {

	private static final String DEBUG_TAG = "DEBUG";

	/**
	 * Activity which have to receive callbacks.
	 */
	private StartOverCallbackListener activity;

	/**
	 * AsyncTask sending the request to the server.
	 */
	private Client client;

	/**
	 * Coordinate of the server.
	 */
	private String host;
	private int port;
	private Socket socket;

	/**
	 * Command the user should send.
	 */
	private String command;

	private TextView info1;

	private TextView info2;

	private TextView info3;

	private TextView toText;

	private TextView fromText;

	private TextView partnerText;
	
	static int count = 0;
	
	private String name = "";
	private String to = "";
	private String from = "";


	// TODO: your own class fields here

	// Class methods
	/**
	 * Creates a MatchFragment
	 * 
	 * @param activity
	 *            activity to notify once the user click on the start over
	 *            Button.
	 * @param host
	 *            address or IP address of the server.
	 * @param port
	 *            port number.
	 * 
	 * @param command
	 *            command you have to send to the server.
	 * 
	 * @return the fragment initialized.
	 */
	// TODO: you can add more parameters, follow the way we did it.
	// ** DO NOT CREATE A CONSTRUCTOR FOR MatchFragment **
	public static MatchFragment newInstance(StartOverCallbackListener activity,
			String host, int port, String command) {
		MatchFragment f = new MatchFragment();

		f.activity = activity;
		f.host = host;
		f.port = port;
		f.command = command;

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

		View view = inflater.inflate(R.layout.match_fragment_layout, container,
				false);

		/**
		 * Register this fragment to be the OnClickListener for the startover
		 * button.
		 */
		view.findViewById(R.id.bu_start_over).setOnClickListener(this);

		// TODO: import your Views from the layout here. See example in
		// ServerFragment.

		this.info1 = (TextView) view.findViewById(R.id.info1);
		this.info2 = (TextView) view.findViewById(R.id.info2);
		this.info3 = (TextView) view.findViewById(R.id.info3);

		this.toText = (TextView) view.findViewById(R.id.toText);
		this.fromText = (TextView) view.findViewById(R.id.fromText);
		this.partnerText = (TextView) view.findViewById(R.id.partnerText);

		/**
		 * Launch the AsyncTask
		 */
		this.client = new Client();
		this.client.execute("");

		return view;
	}

	/**
	 * Callback function for the OnClickListener interface.
	 */
	@Override
	public void onClick(View v) {
		/**
		 * Close the AsyncTask if still running.
		 */
		this.client.close();

		/**
		 * Notify the Activity.
		 */
		this.activity.onStartOver();
	}

	class Client extends AsyncTask<String, String, String> implements Closeable {

		/**
		 * NOTE: you can access MatchFragment field from this class:
		 * 
		 * Example: The statement in doInBackground will print the message in
		 * the Eclipse LogCat view.
		 */

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		protected String doInBackground(String... params) {

			/**
			 * TODO: Your Client code here.
			 */
			Log.d(DEBUG_TAG, String
					.format("The Server at the address %s uses the port %d",
							host, port));

			Log.d(DEBUG_TAG, String.format(
					"The Client will send the command: %s", command));

			PrintWriter out = null;
			BufferedReader in = null;

			try {
				socket = new Socket(host, port);
				
				publishProgress("Successfully connected to server.");
				
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				
				out.println(command);
				
				publishProgress("Successfully sent request to server.");
				
				String response = "";
				String response2 = "";
				while ((response2 = in.readLine()) != null) {
					response = response2;
				}
				
				
				//check for error message like CONNECTION RESET
				
				
				if (response.contains("RESPONSE:")) {
					String[] arrayMagic1 = response.split(":");
					String[] arrayMagic2 = arrayMagic1[1].split(",");
					name = arrayMagic2[0];
					from = arrayMagic2[1];
					to = arrayMagic2[2];
					out.println(":ACK");
					socket.close();
				}

				
				

			} catch (UnknownHostException e) {
				// tell user shit is fucked
				e.printStackTrace();
			} catch (IOException e) {
				// tell user shit is fucked
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return "";
		}

		public void close() {
			// TODO: Clean up the client
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.cancel(true);
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */

		// TODO: use the following method to update the UI.
		// ** DO NOT TRY TO CALL UI METHODS FROM doInBackground!!!!!!!!!! **

		/**
		 * Method executed just before the task.
		 */
		@Override
		protected void onPreExecute() {
			info1.setText("");
			info2.setText("");
			info3.setText("");
			toText.setText("");
			fromText.setText("");
			partnerText.setText("");
		}

		/**
		 * Method executed once the task is completed.
		 */
		@Override
		protected void onPostExecute(String result) {
			toText.setText(to);
			fromText.setText(from);
			partnerText.setText(name);
			info1.setText("Succesfully connected to server");
			info2.setText("Sent request");
			info3.setText("Found pair");
		}

		/**
		 * Method executed when progressUpdate is called in the doInBackground
		 * function.
		 */
		@Override
		protected void onProgressUpdate(String... result) {
			if (count == 0) {
				info1.setText(result[0]);
				count++;
			} else if (count == 1) {
				info2.setText(result[0]);
				info1.setText(result[0]);
				count++;
			} else if (count == 2) {
				info2.setText(result[0]);
				info1.setText(result[0]);
				info3.setText(result[0]);
				count++;
			}
		}
	}

}
