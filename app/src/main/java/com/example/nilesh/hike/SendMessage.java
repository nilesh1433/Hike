package com.example.nilesh.hike;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nilesh.openfireconnect.HikeService;
import com.example.nilesh.util.Constants;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SendMessage extends ActionBarActivity {

    ListView listView;
    EditText chatText;
    Button send;
    String username;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        username = getIntent().getStringExtra("username");
        connection = HikeService.connection;
        setUpViews();
    }

    private void setUpViews()
    {
        listView = (ListView) findViewById(R.id.chatList);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.buttonSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message
                String to = username+"@nilesh";
                String message = chatText.getText().toString();
                Log.i("XMPPClient", "Sending text [" + message + "] to [" + to + "]");
                Message msg = new Message();

                msg.setBody(message);
                msg.setType(Message.Type.chat);
                msg.setTo(to);

                try {
                    Log.i("XMPPClient", "Sending text [" + msg.toXML() + "] to [" + to + "] from "+ "["+ connection.getUser()+"]");
                    connection.sendPacket(msg);
                    Crouton.makeText(SendMessage.this, "Message Sent.", Style.CONFIRM).show();
                    new CountDownTimer(1000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            SendMessage.this.finish();
                        }
                    }.start();
                } catch (Exception e) {
                    Crouton.makeText(SendMessage.this, "Sending message failed.Please try after some time......", Style.ALERT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
