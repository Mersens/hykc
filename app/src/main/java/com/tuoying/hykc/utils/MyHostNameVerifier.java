package com.tuoying.hykc.utils;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MyHostNameVerifier implements HostnameVerifier{
	
    @Override
    public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
    	Log.i("", "verify");
            return true;
    }
}