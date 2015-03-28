package com.example.nilesh.openfireconnect;

import org.jivesoftware.smack.XMPPConnection;

public interface ConnectionListener {

	void onConnected(XMPPConnection connection);
	void onConnectionError(String error);
	
}
