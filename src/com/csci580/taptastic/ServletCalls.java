package com.csci580.taptastic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class ServletCalls extends AsyncTask<String, Integer, String> {
	String data = null;
	String IPAddress = "192.168.0.3";

	public AsyncResponse delegate=null;

	@Override
	protected String doInBackground(String... param) {

		InputStream iStream = null;
		try {
			int operationID = Integer.parseInt(param[0]);
			String strUrl = "";
			switch (operationID) {
			case 1: {
				String userName = param[1];
				String USCID = param[2];
				String password = param[3];

				strUrl = "http://" + IPAddress + ":8080/examples/servlets/servlet/taptasticServlet?id=1&uscid=" + USCID + "&uname=" + userName + "&pwd=" + password + "&tagid=15c88912";
				break;
			}
			case 2: {
				strUrl = "http://" + IPAddress + ":8080/examples/servlets/servlet/taptasticServlet?id=2&uscid=9790886604&uname=ameya&pwd=abc&tagid=15c88912";
				break;
			}
			case 3: {
				String USCID = param[1];
				String classID = param[2];

				strUrl = "http://" + IPAddress + ":8080/examples/servlets/servlet/taptasticServlet?id=3&uscid=" + USCID + "&classid=" + classID + "&tagid=15c88912";
				break;
			}
			case 4: {
				strUrl = "http://" + IPAddress + ":8080/examples/servlets/servlet/taptasticServlet?id=4&uscid=9790886604&uname=ameya&pwd=abc&tagid=15c88912";
				break;
			}
			case 5: {
				strUrl = "http://" + IPAddress + ":8080/examples/servlets/servlet/taptasticServlet?id=5&uscid=9790886604&uname=ameya&pwd=abc&tagid=15c88912";
				break;
			}
			}

			URL urlServlet = new URL(strUrl);

			HttpURLConnection urlConnection = (HttpURLConnection) urlServlet.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
		} catch (Exception e) {
			Log.d("Background Task", e.toString());
		}
		return data;
	}

	@Override
	protected void onPostExecute(String result) {
		 delegate.processFinish(result);
	}
}
