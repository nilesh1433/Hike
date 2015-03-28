package com.example.nilesh.hike;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nilesh.adapter.UserAdapter;
import com.example.nilesh.eventmodel.EventData;
import com.example.nilesh.hike.R;
import com.example.nilesh.model.UserDetails;
import com.example.nilesh.openfireconnect.ConnectToXmpp;
import com.example.nilesh.openfireconnect.ConnectionListener;
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
        EventBus.getDefault().register(this);
        progressBar.setVisibility(View.VISIBLE);
        if(connection==null || !connection.isConnected())
        {
            connectToXmpp.connect(SharedPrefInstance.getString(
                            UserListActivity.this,
                            Constants.SharedPref.SHARED_PREF,
                            Constants.SharedPref.USERNAME),
                    SharedPrefInstance.getString(
                            UserListActivity.this,
                            Constants.SharedPref.SHARED_PREF,
                            Constants.SharedPref.PASSWORD));
            return;
        }
        fetchMembers();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEventMainThread(EventData data)
    {
        if(data.isSuccessfulLogin())
        {
            connection = ConnectToXmpp.liveConnection;
            Crouton.makeText(UserListActivity.this, "Logged in", Style.CONFIRM).show();
            fetchMembers();
            return;
        }
        progressBar.setVisibility(View.GONE);
        Crouton.makeText(UserListActivity.this, "Problem connecting to server.", Style.ALERT).show();
    }

    private void fetchMembers()
    {
        members = openfireUtility.getMembers();
        userAdapter = new UserAdapter(UserListActivity.this, members);
        userList.setAdapter(userAdapter);
        progressBar.setVisibility(View.GONE);
        Crouton.makeText(UserListActivity.this, "Members fetched successfully.", Style.CONFIRM).show();
    }
}
