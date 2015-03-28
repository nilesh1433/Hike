package com.example.nilesh.eventmodel;

public class EventData {

	//we are making it false explicitly when connection was unsuccessful
	//this is made true so that after login connected is not required to be set
	boolean isConnected = true;
	boolean isSuccessfulLogin;
	boolean isUserAMember;
    boolean isUserCreated;
    String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isUserCreated() {
        return isUserCreated;
    }

    public void setUserCreated(boolean isUserCreated) {
        this.isUserCreated = isUserCreated;
    }

    public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isSuccessfulLogin() {
		return isSuccessfulLogin;
	}

	public void setSuccessfulLogin(boolean isSuccessfulLogin) {
		this.isSuccessfulLogin = isSuccessfulLogin;
	}

	public boolean isUserAMember() {
		return isUserAMember;
	}

	public void setUserAMember(boolean isUserAMember) {
		this.isUserAMember = isUserAMember;
	}
	
	
}
