package com.example.nilesh.hike;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nilesh.adapter.UserAdapter;
import com.example.nilesh.eventmodel.EventData;
import com.example.nilesh.hike.R;
import com.example.nilesh.model.MessageDetails;
import com.example.nilesh.model.UserDetails;
import com.example.nilesh.openfireconnect.ConnectToXmpp;
import com.example.nilesh.openfireconnect.ConnectionListener;
import com.example.nilesh.openfireconnect.HikeService;
import com.example.nilesh.openfireconnect.OpenfireUtility;
import com.example.nilesh.util.Constants;
import com.example.nilesh.util.SharedPrefInstance;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class UserListActivity extends HikeActivity {

    ListView userList;
    UserAdapter userAdapter;
    private ProgressBar progressBar;
    private ConnectToXmpp connectToXmpp;
    private Connection connection;
    private ArrayList<UserDetails> members;
    private OpenfireUtility openfireUtility;
    private static boolean isForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        connectToXmpp = new ConnectToXmpp();
        openfireUtility = new OpenfireUtility();
        setUpView();
    }

    private void setUpView()
    {
        userList = (ListView) findViewById(R.id.userList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        connection = HikeService.connection;
        EventBus.getDefault().register(this);
        progressBar.setVisibility(View.VISIBLE);

        //basically this code will run only when the user has timeout, need to change
        if((connection==null || !connection.isConnected()) && !HikeService.connectionInProgress)
        {
            Log.d("TAG", "connecting through userlistactivity "+ connection+" "+connection.isConnected());
            connectToXmpp.connect(SharedPrefInstance.getString(
                            UserListActivity.this,
                            Constants.SharedPref.SHARED_PREF,
                            Constants.SharedPref.USERNAME),
                    SharedPrefInstance.getString(
                            UserListActivity.this,
                            Constants.SharedPref.SHARED_PREF,
                            Constants.SharedPref.PASSWORD),new ConnectionListener() {
                    @Override
                    public void onConnected(XMPPConnection connection) {
                        UserListActivity.this.connection = connection;
                        HikeService.connection = connection;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Crouton.makeText(UserListActivity.this, "Logged in", Style.CONFIRM).show();
                                openfireUtility.getMembers();
                            }
                        });

                        return;
                    }

                    @Override
                    public void onConnectionError(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Crouton.makeText(UserListActivity.this, "Problem connecting to server.", Style.ALERT).show();
                            }
                        });

                    }
                    });
            return;
        }

        if(!HikeService.connectionInProgress)
            openfireUtility.getMembers();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        isForeground = false;
        super.onPause();
    }

    public static boolean isUserListActivityForeground()
    {
        return isForeground;
    }

    public void onEventMainThread(EventData data)
    {
        if(data.isSuccessfulLogin())
        {
            connection = HikeService.connection;
            Crouton.makeText(UserListActivity.this, "Logged in", Style.CONFIRM).show();
            openfireUtility.getMembers();
            return;
        }
        progressBar.setVisibility(View.GONE);
        Crouton.makeText(UserListActivity.this, "Problem connecting to server.", Style.ALERT).show();
    }

    public void onEventMainThread(ArrayList<UserDetails> users)
    {
        members = users;
        userAdapter = new UserAdapter(UserListActivity.this, members);
        userList.setAdapter(userAdapter);
        progressBar.setVisibility(View.GONE);
        Crouton.makeText(UserListActivity.this, "Members fetched successfully.", Style.CONFIRM).show();
    }

    public void onEventMainThread(MessageDetails messageDetails)
    {
        Crouton.makeText(UserListActivity.this, messageDetails.getMessage(), Style.CONFIRM).show();
        if(messageDetails.getMessage().toLowerCase().contains("approval"))
        {
            userAdapter = new UserAdapter(UserListActivity.this, members);
            userList.setAdapter(userAdapter);
        }
    }
}
