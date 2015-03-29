package com.example.nilesh.openfireconnect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nilesh.database.DbHelper;
import com.example.nilesh.eventmodel.EventData;
import com.example.nilesh.hike.GrantPermission;
import com.example.nilesh.hike.MainActivity;
import com.example.nilesh.hike.R;
import com.example.nilesh.hike.SendMessage;
import com.example.nilesh.hike.UserListActivity;
import com.example.nilesh.model.MessageDetails;
import com.example.nilesh.util.Constants;
import com.example.nilesh.util.SharedPrefInstance;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import de.greenrobot.event.EventBus;

public class HikeService extends Service {

	public static final String TAG = "HikeService";
	public static Connection connection;
    public static boolean connectionInProgress;
    private DbHelper dbHelper;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		synchronized (intent) {

            Log.v("TAG", "service started");
            dbHelper = new DbHelper(getApplicationContext());

			if (connection != null)
				System.out.println(connection.isConnected());
			// make connection to xmppp server if it is not connected and the
			// user is already registered
			if ((connection == null || (connection != null && !connection
					.isConnected()))
					&& SharedPrefInstance.getBoolean(getApplicationContext(),
                    Constants.SharedPref.SHARED_PREF, Constants.SharedPref.IS_REGISTERED) == true) {

                Log.d("TAG", "connection through service");
                connectionInProgress = true;

				ConnectToXmpp.connect(SharedPrefInstance.getString(
                        getApplicationContext(), Constants.SharedPref.SHARED_PREF,
                        Constants.SharedPref.USERNAME), SharedPrefInstance.getString(
                        getApplicationContext(), Constants.SharedPref.SHARED_PREF,
                        Constants.SharedPref.PASSWORD), new ConnectionListener() {
                    @Override
                    public void onConnected(XMPPConnection connection) {
                        connectionInProgress = false;
                        HikeService.connection = connection;
                        registerXmppPacketListener();
                        EventData data = new EventData();
                        data.setSuccessfulLogin(true);
                        EventBus.getDefault().post(data);
                    }

                    @Override
                    public void onConnectionError(String error) {
                        connectionInProgress = false;
                        EventData data = new EventData();
                        data.setSuccessfulLogin(false);
                        EventBus.getDefault().post(data);
                    }
                });
			}
            else
            {

            }
		}

		return super.onStartCommand(intent, flags, startId);
	}

	public void registerXmppPacketListener() {
        Log.v("TAG", "registered");
		PacketFilter chatFilter = new MessageTypeFilter(Message.Type.chat);
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
				if (message.getBody() != null) {
					String fromName = StringUtils.parseBareAddress(message
							.getFrom());
					//send message to activity
					Log.i(TAG, "Got text [" + message.getBody() + "] from ["
							+ fromName + "]");
                    sendNotification(message.getBody(), fromName);
				}
			}
		}, chatFilter);
	}

	public void sendNotification(String message, String name) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext());


        System.out.println(message + " " + name);
        String split[] = message.split("@");
        Intent intent = null;
        if(split[0].equalsIgnoreCase("permission"))
        {
            //normal app icon
            builder.setSmallIcon(R.drawable.ic_launcher);

            //create a permission request
            String requestedPermission="";
            intent = new Intent(getApplicationContext(), GrantPermission.class);
            intent.putExtra("username", name);
            message = name.split("@")[0] + " has requested for ";
            for(int i=1; i<split.length; i++)
            {
                Log.v("TAG", split[i]);
                if(split[i].equals(""+Constants.PRIORITY.VIBRATE))
                {
                    message += "Vibrate ";
                    requestedPermission += Constants.PRIORITY.VIBRATE+"@";
                }
                else if(split[i].equals(""+Constants.PRIORITY.VIBRATE_RING))
                {
                    message += "Vibrate And Ring ";
                    requestedPermission += Constants.PRIORITY.VIBRATE_RING+"@";
                }
                else if(split[i].equals(""+Constants.PRIORITY.HIKE_PLUS))
                {
                    message += "Hike Plus ";
                    requestedPermission += Constants.PRIORITY.HIKE_PLUS+"@";
                }

            }
            Log.v("TAG from HikeService", requestedPermission);
            intent.putExtra("requestedPermission", requestedPermission);

            sendConfirmationToUserListActivity(name.split("@")[0], message);


        }
        else if(split[0].equalsIgnoreCase("approval"))
        {
            //normal app icon
            builder.setSmallIcon(R.drawable.ic_launcher);
            //save it in database
            Log.v("TAG", "permission saved "+ message.substring(message.indexOf("@")+1, message.length()));
            dbHelper.insertPriority(name.split("@")[0], message.substring(message.indexOf("@")+1, message.length()));

            //handle approval
            intent = new Intent();
            message = "Approval received from "+name.split("@")[0];

            sendConfirmationToUserListActivity(name.split("@")[0], message);
        }
        else
        {
            intent = new Intent(getApplicationContext(), SendMessage.class);
            intent.putExtra("username", name.split("@")[0]);

            if(message.contains("@"))
            {
                //negation for priority message
                builder.setSmallIcon(R.drawable.priority);
                intent.putExtra("message", message.split("@")[0]);
                switch (Integer.parseInt(message.split("@")[1]))
                {
                    case Constants.PRIORITY.VIBRATE:
                        builder.setVibrate(Constants.pattern);
                        builder.setColor(Color.RED);
                        break;
                    case Constants.PRIORITY.VIBRATE_RING:
                        builder.setVibrate(Constants.pattern);
                        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ringtone));
                        builder.setColor(Color.YELLOW);
                        break;
                    case Constants.PRIORITY.HIKE_PLUS:
                        builder.setColor(Color.BLUE);
                        break;
                }
                message = message.split("@")[0];
                sendConfirmationToSendMessage(name.split("@")[0], message);
            }
            else
            {
                //normal app icon
                builder.setSmallIcon(R.drawable.ic_launcher);
                intent.putExtra("message", message);
                sendConfirmationToSendMessage(name.split("@")[0], message);
            }

        }

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setAutoCancel(true);
		builder.setContentTitle(name.split("@")[0]);
		builder.setContentText(message);
		builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());
	}

    private void sendConfirmationToUserListActivity(String name, String message)
    {
        //if foreground show popup
        if(UserListActivity.isUserListActivityForeground())
        {
            MessageDetails messageDetails = new MessageDetails();
            messageDetails.setUser(name);
            messageDetails.setMessage(message);
            EventBus.getDefault().post(messageDetails);
        }
    }

    private void sendConfirmationToSendMessage(String name, String message)
    {
        //if foreground show popup
        Log.d("TAG", "username "+SendMessage.getCurrentUser()+" "+name);
        if(SendMessage.isSendMessageForeground() && SendMessage.getCurrentUser().equalsIgnoreCase(name))
        {
            MessageDetails messageDetails = new MessageDetails();
            messageDetails.setUser(name);
            messageDetails.setMessage(message);
            EventBus.getDefault().post(messageDetails);
        }
    }
}
