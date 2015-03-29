package com.example.nilesh.hike;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.nilesh.adapter.MessageAdapter;
import com.example.nilesh.database.DbHelper;
import com.example.nilesh.model.MessageDetails;
import com.example.nilesh.openfireconnect.HikeService;
import com.example.nilesh.util.Constants;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SendMessage extends ActionBarActivity {

    ListView listView;
    EditText chatText;
    Button send;
    static String username;
    Connection connection;
    DbHelper dbHelper;
    MessageAdapter messageAdapter;
    RadioButton ringAndVibrate, vibrate, hike;
    RadioGroup priorityGroup;
    ArrayList<MessageDetails> messageDetailsList;
    private static boolean isForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        username = getIntent().getStringExtra("username");
        connection = HikeService.connection;
        dbHelper = new DbHelper(this);
        messageDetailsList = new ArrayList<MessageDetails>();
        //messageAdapter = dbHelper.getAllMessage()
        messageAdapter = new MessageAdapter(this, messageDetailsList);
        setUpViews();
        updatePriorityView();

        getSupportActionBar().setTitle(username+" - Chat");
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        isForeground = true;
    }

    @Override
    protected void onStop() {
        isForeground = false;
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static boolean isSendMessageForeground()
    {
        return isForeground;
    }

    public static String getCurrentUser()
    {
        return username;
    }

    private void updatePriorityView()
    {
        String priority = dbHelper.getPriority(username);
        if(priority.equals(""))
            priorityGroup.setVisibility(View.GONE);
        else
        {
            String s[] = priority.split("@");
            for(int i=0;i<s.length;i++)
            {
                if(Integer.parseInt(s[i]) == Constants.PRIORITY.VIBRATE)
                    vibrate.setVisibility(View.VISIBLE);
                else if(Integer.parseInt(s[i]) == Constants.PRIORITY.VIBRATE_RING)
                    ringAndVibrate.setVisibility(View.VISIBLE);
                else if(Integer.parseInt(s[i]) == Constants.PRIORITY.HIKE_PLUS)
                    hike.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setUpViews()
    {
        listView = (ListView) findViewById(R.id.chatList);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.buttonSend);
        ringAndVibrate = (RadioButton) findViewById(R.id.ringAndVibrate);
        vibrate = (RadioButton) findViewById(R.id.vibrate);
        hike = (RadioButton) findViewById(R.id.hike);
        priorityGroup = (RadioGroup) findViewById(R.id.priorityGroup);

        listView.setAdapter(messageAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message
                String to = username+"@"+ Constants.ServerDetails.SERVICE;
                String message = chatText.getText().toString();
                hideKeyboard();

                if(message.trim().length()==0)
                {
                    Crouton.makeText(SendMessage.this, "Enter valid message", Style.ALERT).show();
                    return;
                }

                int selectedPriorityId = priorityGroup.getCheckedRadioButtonId();
                switch(selectedPriorityId)
                {
                    case R.id.vibrate:
                        message += "@"+Constants.PRIORITY.VIBRATE;
                        break;
                    case R.id.ringAndVibrate:
                        message += "@"+Constants.PRIORITY.VIBRATE_RING;
                        break;
                    case R.id.hike:
                        message += "@"+Constants.PRIORITY.HIKE_PLUS;
                        break;
                }


                Log.i("XMPPClient", "Sending text [" + message + "] to [" + to + "]");
                Message msg = new Message();

                msg.setBody(message);
                msg.setType(Message.Type.chat);
                msg.setTo(to);

                try {
                    Log.i("XMPPClient", "Sending text [" + msg.toXML() + "] to [" + to + "] from "+ "["+ connection.getUser()+"]");
                    connection.sendPacket(msg);
                    chatText.setText("");
                    dbHelper.insertMessages(username, connection.getUser().split("@")[0], msg.getBody());

                    MessageDetails messageDetails = new MessageDetails();
                    messageDetails.setMessage(msg.getBody().split("@")[0]);
                    messageDetails.setUser(connection.getUser().split("@")[0]);
                    messageDetails.setLoggedInUserSender(true);
                    messageDetailsList.add(messageDetails);
                    messageAdapter.notifyDataSetChanged();

                    Crouton.makeText(SendMessage.this, "Message Sent.", Style.CONFIRM).show();

                    priorityGroup.clearCheck();

                    new CountDownTimer(1000, 1000){

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            //SendMessage.this.finish();
                        }
                    }.start();
                } catch (Exception e) {
                    Crouton.makeText(SendMessage.this, "Sending message failed.Please try after some time......", Style.ALERT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void onEventMainThread(MessageDetails messageDetails)
    {
        messageDetailsList.add(messageDetails);
        dbHelper.insertMessages(connection.getUser().split("@")[0], messageDetails.getUser(), messageDetails.getMessage());
        messageAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard()
    {
        EditText myEditText = (EditText) findViewById(R.id.chatText);
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
    }
}
