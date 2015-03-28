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

import com.example.nilesh.openfireconnect.HikeService;
import com.example.nilesh.util.Constants;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class GrantPermission extends ActionBarActivity {

    TextView user;
    CheckBox vibrate, alarm;
    Button grantPermission;
    String toUsername;
    Connection connection;
    StringBuilder text;
    String requestedPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_permission);
        toUsername = getIntent().getStringExtra("username");
        text = new StringBuilder();
        connection = HikeService.connection;
        requestedPermission = getIntent().getStringExtra("requestedPermission");
        setUpViews();
    }

    private void setUpViews()
    {
        user = (TextView) findViewById(R.id.user);
        vibrate = (CheckBox) findViewById(R.id.vibrate);
        alarm = (CheckBox) findViewById(R.id.alarm);
        grantPermission = (Button) findViewById(R.id.confirm);

        Log.v("TAG", requestedPermission);
        if(requestedPermission.contains(""+Constants.PRIORITY.VIBRATE))
        {
            Log.v("TAG", "vibrate is required");
            vibrate.setChecked(true);
        }
        if(requestedPermission.contains(""+Constants.PRIORITY.VIBRATE_RING))
        {
            Log.v("TAG", "vibrate and ring is required");
            alarm.setChecked(true);
        }

        grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message
                String to = toUsername;
                Log.i("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
                Message msg = new Message();

                if(vibrate.isChecked())
                    text.append(Constants.PRIORITY.VIBRATE + "@");
                if(alarm.isChecked())
                    text.append(Constants.PRIORITY.VIBRATE_RING + "@");

                if(text.length()==0)
                {
                    Crouton.makeText(GrantPermission.this, "Select atleast one.", Style.ALERT).show();
                    return;
                }

                msg.setBody("Approval@"+text.toString().substring(0, text.length()-1));
                msg.setType(Message.Type.chat);
                msg.setTo(to);

                try {
                    Log.i("XMPPClient", "Sending text [" + msg.toXML() + "] to [" + to + "] from "+ "["+ connection.getUser()+"]");
                    connection.sendPacket(msg);
                    Crouton.makeText(GrantPermission.this, "Approval Sent.", Style.CONFIRM).show();
                    new CountDownTimer(1000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            GrantPermission.this.finish();
                        }
                    }.start();
                } catch (Exception e) {
                    Crouton.makeText(GrantPermission.this, "Request sending failed.Please try after some time......", Style.ALERT).show();
                    e.printStackTrace();
                }
            }
        });

    }

}
