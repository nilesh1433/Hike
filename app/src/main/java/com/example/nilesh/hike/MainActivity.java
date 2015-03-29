package com.example.nilesh.hike;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.nilesh.eventmodel.EventData;
import com.example.nilesh.openfireconnect.ConnectToXmpp;
import com.example.nilesh.openfireconnect.ConnectionListener;
import com.example.nilesh.openfireconnect.HikeService;

import org.jivesoftware.smack.XMPPConnection;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends HikeActivity {

    EditText username, password;
    Button register;
    ConnectToXmpp connectToXmpp;
    ProgressBar progressBar;
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        connectToXmpp = new ConnectToXmpp();
        setUpView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void setUpView()
    {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        register.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //make a connection and create user
            connectToXmpp.register(context, username.getText().toString(),
                    password.getText().toString());
            progressBar.setVisibility(View.VISIBLE);
            register.setEnabled(false);
        }
    };

    public void onEventMainThread(EventData data)
    {
        if(data.isUserCreated())
        {
            progressBar.setVisibility(View.GONE);
            Crouton.makeText(context, "Registered Succesfully", Style.CONFIRM).show();
            CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    startService(new Intent(MainActivity.this, HikeService.class));
                    startActivity(new Intent(context, UserListActivity.class));
                    finish();
                }
            }.start();

            return;
        }

        progressBar.setVisibility(View.GONE);
        register.setEnabled(true);
        Crouton.makeText(context, data.getError(), Style.ALERT).show();
    }
}