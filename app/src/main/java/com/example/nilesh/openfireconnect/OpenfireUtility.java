package com.example.nilesh.openfireconnect;

import android.util.Log;

import com.example.nilesh.apicall.ApiListener;
import com.example.nilesh.apicall.MakeAPICall;
import com.example.nilesh.model.UserDetails;
import com.example.nilesh.util.Constants;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;

import de.greenrobot.event.EventBus;

/**
 * Created by Nilesh on 3/28/2015.
 */
public class OpenfireUtility {

    MakeAPICall apiCall;
    ArrayList<UserDetails> users;

    public ArrayList<UserDetails> getMembers()
    {
        Log.v("TAG", "getMembers()");
        apiCall = new MakeAPICall();
        apiCall.makeGETRequest(Constants.APILinks.GET_ALL_USERS, "", new ApiListener() {
            @Override
            public void onLoadFinished(Object data) {
                users = (ArrayList<UserDetails>) data;
                EventBus.getDefault().post(users);
            }

            @Override
            public void onLoadError() {

            }
        });
        return users;
    }
}
