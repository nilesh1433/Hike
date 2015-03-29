package com.example.nilesh.util;

public class Constants {
	
	public static class SharedPref{
		public static final String IS_REGISTERED = "registered";
		public static final String SHARED_PREF = "preference";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
	}

	public static class ServerDetails{
		public static final String SERVER_LINK = "192.168.0.130";
		public static final int PORT = 5222;
		public static final String SERVICE = "nilesh";
        public static final String GROUP_NAME = "nil";
        public static final String JID = "@example.com";
	}

    public static class APILinks{
        public static final String GET_ALL_USERS =  "http://"+ ServerDetails.SERVER_LINK+":9090" + "/plugins/userService/users";
    }

    public static class PRIORITY{
        public static final int VIBRATE = 1;
        public static final int VIBRATE_RING = 2;
        public static final int HIKE_PLUS = 3;
    };

    public static final long pattern[] = {0, 2000, 2000, 2000, 2000};
}
