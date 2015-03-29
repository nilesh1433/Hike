package com.example.nilesh.hike;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.nilesh.openfireconnect.ConnectToXmpp;
import com.example.nilesh.openfireconnect.HikeService;
import com.example.nilesh.util.Constants;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class AskPermission extends ActionBarActivity {

    TextView user;
    CheckBox vibrate, alarm, hikePlus;
    Button askPermission;
    String toUsername;
    Connection connection;
    StringBuilder text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permission);
        toUsername = getIntent().getStringExtra("username");
        connection = HikeService.connection;
        text = new StringBuilder();
        setUpViews();

        getSupportActionBar().setTitle("Ask Permission - "+toUsername);
    }

    private void setUpViews()
    {
        user = (TextView) findViewById(R.id.user);
        vibrate = (CheckBox) findViewById(R.id.vibrate);
        alarm = (CheckBox) findViewById(R.id.alarm);
        hikePlus = (CheckBox) findViewById(R.id.hikePlus);

        askPermission = (Button) findViewById(R.id.confirm);
        askPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message
                String to = toUsername+"@"+ Constants.ServerDetails.SERVICE;
                Log.i("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
                Message msg = new Message();

                if(vibrate.isChecked())
                    text.append(Constants.PRIORITY.VIBRATE+"@");
                if(alarm.isChecked())
                    text.append(Constants.PRIORITY.VIBRATE_RING+"@");
                if(hikePlus.isChecked())
                    text.append(Constants.PRIORITY.HIKE_PLUS+"@");

                if(text.length()==0)
                {
                    Crouton.makeText(AskPermission.this, "Select atleast one.", Style.ALERT).show();
                    return;
                }
                msg.setBody("Permission@"+text.toString().substring(0, text.length()-1));
                msg.setType(Message.Type.chat);
                msg.setTo(to);

                try {
                    Log.i("XMPPClient", "Sending text [" + msg.toXML() + "] to [" + to + "] from "+ "["+ connection.getUser()+"]");
                    connection.sendPacket(msg);
                    Crouton.makeText(AskPermission.this, "Request Sent.", Style.CONFIRM).show();
                    new CountDownTimer(2000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            AskPermission.this.finish();
                        }
                    }.start();
                } catch (Exception e) {
                    Crouton.makeText(AskPermission.this, "Request sending failed.Please try after some time......", Style.ALERT).show();
                    e.printStackTrace();
                }
            }
        });

    }
}
