package com.example.nilesh.openfireconnect;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nilesh.eventmodel.EventData;
import com.example.nilesh.util.Constants;
import com.example.nilesh.util.Constants.SharedPref;
import com.example.nilesh.util.SharedPrefInstance;

import de.greenrobot.event.EventBus;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class ConnectToXmpp {

	public static final String TAG = "ConnectToXmpp";

	public static void connect(final String username, final String password, final ConnectionListener listener) {

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Create a connection
                EventData eventData = new EventData();
				try {
					XMPPConnection connection = connectToChatServer();
					connection.login(username, password);
					Log.i("XMPPClient",
							"Logged in as " + connection.getUser());

					// Set the status to available
					Presence presence = new Presence(
							Presence.Type.available);
					connection.sendPacket(presence);
                    listener.onConnected(connection);

				} catch (XMPPException ex) {

                    Log.e(TAG, "[SettingsDialog] Failed to log in as "
							+ username);
					Log.e(TAG, ex.toString());
                    listener.onConnectionError(ex.toString());

				} catch (Exception e) {

                    Log.e(TAG, e.toString());
                    listener.onConnectionError(e.toString());
				}
				return null;
			}
		}.execute();

	}

	public void register(final Context context,final String username, final String password)
	{
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				// Create a connection

                EventData event = new EventData();

				try {
					XMPPConnection connection = connectToChatServer();
					AccountManager am = new AccountManager(connection);
					try {
						am.createAccount(username, password);

						SharedPrefInstance.setBoolean(
                                context,
                                SharedPref.SHARED_PREF,
                                SharedPref.IS_REGISTERED, true);
						SharedPrefInstance.setString(
								context,
								SharedPref.SHARED_PREF,
								Constants.SharedPref.USERNAME, username+"@"+Constants.ServerDetails.SERVICE);
						SharedPrefInstance.setString(
                                context,
                                SharedPref.SHARED_PREF,
                                SharedPref.PASSWORD, password);

                        event.setUserCreated(true);

					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
                        event.setUserCreated(false);
                        event.setError("User name already exsists!");
					}

				} catch (Exception ex) {
					event.setUserCreated(false);
                    Log.v("TAG", ex.toString());
                    event.setError("OOPs!!!! could not connect to server. Try again....");
				}
                EventBus.getDefault().post(event);
				return null;
			}
		}.execute();
	}

	private static XMPPConnection connectToChatServer() throws Exception
	{
		AndroidConnectionConfiguration connConfig = new AndroidConnectionConfiguration(
				Constants.ServerDetails.SERVER_LINK,
				Constants.ServerDetails.PORT,
				Constants.ServerDetails.SERVICE);

		connConfig.setReconnectionAllowed(true);
		XMPPConnection connection = new XMPPConnection(connConfig);

		try {
			connection.connect();
			//setting the connection
			Log.i("XMPPClient", "Connected to " + connection.getHost());
		}
		catch (Exception ex) {
			Log.e(TAG, "[SettingsDialog] Failed to connect to "
					+ connection.getHost());
			ex.printStackTrace();
			throw new Exception("Could not connect to Server");
		}

		return connection;
	}
}
