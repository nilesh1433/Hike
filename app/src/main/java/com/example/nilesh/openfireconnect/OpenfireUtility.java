package com.example.nilesh.openfireconnect;

import android.util.Log;

import com.example.nilesh.model.UserDetails;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nilesh on 3/28/2015.
 */
public class OpenfireUtility {

    public ArrayList<UserDetails> getMembers()
    {
        Log.v("TAG", "getMembers()");
        ArrayList<UserDetails> members;
        Roster roster = ConnectToXmpp.liveConnection.getRoster();

        members = new ArrayList<UserDetails>();

        Collection<RosterEntry> rosterEntries = roster.getEntries();
        Collection<RosterGroup> rosterGroups = roster.getGroups();
            System.out.println(roster.getGroupCount());

        for (RosterGroup temp : rosterGroups) {
            System.out.println(temp.getName());//temp.getName()+" "+temp.getUser());
        }

        for (RosterEntry temp : rosterEntries) {
            UserDetails user = new UserDetails();
            user.setUserName(temp.getUser());
            Log.v("TAG", "*************"+user.getUserName());
            members.add(user);
        }

        return members;
    }
}
