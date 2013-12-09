package com.csci580.taptastic;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.csci580.taptastic.adapter.NavDrawerListAdapter;
import com.csci580.taptastic.model.NavDrawerItem;

public class MainActivity extends Activity implements AsyncResponse {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	private String nfcTagId;
	private String nfcText;
	private int menuCode = 0;
	TelephonyManager telephonyManager;
	int TAKE_PHOTO_CODE = 0;
	public static int count = 0;
	private String userName;
	private String USCID;
	private String classID;
	com.csci580.taptastic.adapter.LoginDataBaseAdapter loginDataBaseAdapter;
	JSONObject jObject;

	private static enum FragmentSection {
		PHOTO, AUDIO, COURSES, ANNOUCEMENTS, APPOINTMENTS, POLLS, NEWAPPOINTMENTS, NEWANNOUCEMENTS, NEWPOLLS, SETTINGS, HELP
	};

	public static enum NfcMode {
		READ, WRITE
	};

	public static FragmentSection fragmentSection = FragmentSection.COURSES;
	public static NfcMode nfcMode = NfcMode.READ;
	private NfcAdapter mNfcAdapter;
	// nav drawer title
	private CharSequence mDrawerTitle;
	// used to store app title
	private CharSequence mTitle;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		SharedPreferences prefsPreviousAttendance = getSharedPreferences("Attendance", 0);
		if (prefsPreviousAttendance.getString("lastTappedDate", null) != null) {
			// Call servlet to update attendance
		}
		SharedPreferences prefsPreviousLogin = getSharedPreferences("Login", 0);
		loginDataBaseAdapter = new com.csci580.taptastic.adapter.LoginDataBaseAdapter(this);
		loginDataBaseAdapter = loginDataBaseAdapter.open();
		// Set user variables
		userName = prefsPreviousLogin.getString("userName", null);
		USCID = loginDataBaseAdapter.getUSCID(userName);
		classID = "CSCI588";
		mTitle = mDrawerTitle = getTitle();
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home

		// Courses/Statistics
		if(LoginActivity.appMode=="Student")
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		else navDrawerItems.add(new NavDrawerItem("Statistics", navMenuIcons.getResourceId(7, -1)));
		// Annoucements
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, "2"));
		// Appointments
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true, "22"));
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Help
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		if (LoginActivity.appMode == "Student") {
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		}
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		if (mNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if (!mNfcAdapter.isEnabled()) {
			Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_SHORT).show();
		}

		handleIntent(getIntent());

	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new CoursesFragment();
			fragmentSection = FragmentSection.COURSES;
			menuCode = 0;
			invalidateOptionsMenu();
			break;
		case 1:
			fragment = new AnnouncmentsFragments();
			fragmentSection = FragmentSection.ANNOUCEMENTS;

			if (LoginActivity.appMode == "Student")
				menuCode = 0;
			else
				menuCode = 1;
			invalidateOptionsMenu();
			break;
		case 2:
			fragment = new AppointmentsFragment();
			fragmentSection = FragmentSection.APPOINTMENTS;

			if (LoginActivity.appMode == "Student")
				menuCode = 1;
			else
				menuCode = 0;
			invalidateOptionsMenu();
			break;
		case 3:
			menuCode = 0;
			fragmentSection = FragmentSection.SETTINGS;

			invalidateOptionsMenu();
			fragment = new SettingsFragment();
			break;
		case 4:
			menuCode = 0;
			fragmentSection = FragmentSection.HELP;

			invalidateOptionsMenu();
			fragment = new HelpFragment();
			break;
		case 5:
			menuCode = 0;
			fragmentSection = FragmentSection.PHOTO;
			takePicture("newLecture");

			break;
		case 6:
			menuCode = 0;
			fragmentSection = FragmentSection.AUDIO;
			recordAudio("newLecture");
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	public void applySettings() {
		// Notification bar notification for messages from professor
		// sendnotification("Taptastic Notification","Yo this is the best project ever! I love this app. tap tap tap!");

		// Audio Changes
		// manageAudio();

		// Forward incoming calls to voice-mail
		// manageCalls();

		// Send SMS
		// manageSMS();

		// Enable Wi-Fi and disable mobile network
		// manageRadio();

		// Reject calls
		// BlockCalls();

		ServletCalls markAttendance = new ServletCalls();
		markAttendance.delegate = MainActivity.this;

		// Mark attendance and get messages
		markAttendance.execute("3", USCID, classID);

		// Toast.makeText(MainActivity.this, "All Done!",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		Fragment fragment = null;
		FragmentManager fragmentManager = getFragmentManager();
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			fragment = new SettingsFragment();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(3, true);
			mDrawerList.setSelection(3);
			setTitle(navMenuTitles[3]);
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		case R.id.action_add:
			if (fragmentSection == FragmentSection.APPOINTMENTS)
				fragment = new NewAppointment();
			else if (fragmentSection == FragmentSection.ANNOUCEMENTS)
				fragment = new NewAnnoucement();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(3, true);
			mDrawerList.setSelection(3);
			setTitle(navMenuTitles[3]);
			mDrawerLayout.closeDrawer(mDrawerList);

			return true;
		case R.id.action_switch:
			if (LoginActivity.appMode == "Student") {
				LoginActivity.appMode = "Professor";
				Toast.makeText(this, "Professor Mode", 10).show();
			} else {
				LoginActivity.appMode = "Student";
				Toast.makeText(this, "Student Mode", 10).show();
			}
			return true;
		case R.id.action_apply:
			applySettings();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void callfragment(String stats,Fragment fragment){
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			
			setTitle(stats);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}
	
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			moveTaskToBack(true);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (menuCode == 0)
			getMenuInflater().inflate(R.menu.menunoadd, menu);
		else if (menuCode == 1)
			getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// Code Below this section is for NFC Handling!!!

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * It's important, that the activity is in the foreground (resumed).
		 * Otherwise an IllegalStateException is thrown.
		 */
		setupForegroundDispatch(this, mNfcAdapter);
	}

	@Override
	protected void onPause() {
		/*
		 * Call this before onPause, otherwise an IllegalArgumentException is
		 * thrown as well.
		 */
		stopForegroundDispatch(this, mNfcAdapter);

		super.onPause();
	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);

			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				// if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
				// && Arrays.equals(ndefRecord.getType(),
				// NdefRecord.RTD_TEXT)) {
				try {
					return readText(ndefRecord);
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "Unsupported Encoding", e);
				}
				// }
			}

			return null;
		}

		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at
			 * 3.2.1
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength,
			// "US-ASCII");
			// e.g. "en"

			// Get the Text
			// return new String(payload, languageCodeLength + 1, payload.length
			// - languageCodeLength - 1, textEncoding);
			return new String(payload);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				// Toast.makeText(cont,result, Toast.LENGTH_LONG).show();
				nfcText = result;
				// nfcText.setText(result);
			}
		}
	}

	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][] {};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	public void manageSMS() {

		try {
			SmsManager sendSMS = SmsManager.getDefault();
			sendSMS.sendTextMessage("+12135955593", null, "some crap", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processFinish(String servletResponse) {

		JSONObject jObject;
		String status = "";
		String posts = "";
		Boolean resultsFound;

		try {
			jObject = new JSONObject(servletResponse);
			JSONObject response;
			try {
				response = jObject.getJSONObject("results");
				status = response.get("status").toString();
				posts = response.get("posts").toString().trim();
				resultsFound = true;
			} catch (Exception e) {
				resultsFound = false;
			}

			if (!resultsFound) {
				try {
					response = jObject.getJSONObject("records");
					status = response.get("status").toString();
					posts = response.get("posts").toString();
				} catch (Exception e) {
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Toast.makeText(this, status, Toast.LENGTH_SHORT).show();

		if (!posts.equals("")) {
			sendnotification("Announcement", posts);
		}
	}

	public void markAttendance() {
		Boolean success = false;
		Time now = new Time();
		now.setToNow();

		SharedPreferences sp = getSharedPreferences("Attendance", 0);
		SharedPreferences.Editor Ed = sp.edit();
		Ed.putString("lastTappedDate", now.toString());
		Ed.commit();

		// Send attendance to servlet

		// Edit shared preference to clear the tapped date if successfully
		// updated in servel
		if (success) {
			Ed.putString("lastTappedDate", null);
			Ed.commit();
		}

	}

	protected void sendnotification(String title, String message) {
		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_launcher;

		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
		contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.text, message);

		CharSequence tickerText = message;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.contentView = contentView;

		Intent notificationIntent = new Intent(this, MainActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.contentIntent = contentIntent;

		Random randInt = new Random();
		int id = randInt.nextInt(100) - 1;
		mNotificationManager.notify(id, notification);
	}

	public void takePicture(String fileName) {
		try {
			File dir = new File(Environment.getExternalStorageDirectory() + "/Taptastic/images/");
			if (!dir.exists()) {
				File newdir = new File(Environment.getExternalStorageDirectory().getPath() + "/Taptastic/images/");
				newdir.mkdirs();
			}

			String file = fileName + ".jpg";
			File newfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Taptastic/images/" + file);
			try {
				newfile.createNewFile();
			} catch (IOException e) {
			}

			Uri outputFileUri = Uri.fromFile(newfile);

			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void recordAudio(String fileName) {
		try {
			File dir = new File(Environment.getExternalStorageDirectory() + "/Taptastic/sounds/");
			if (!dir.exists()) {
				File newdir = new File(Environment.getExternalStorageDirectory().getPath() + "/Taptastic/sounds/");
				newdir.mkdirs();
			}

			final MediaRecorder recorder = new MediaRecorder();
			ContentValues values = new ContentValues(3);
			values.put(MediaStore.MediaColumns.TITLE, fileName);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/Taptastic/sounds/" + fileName);
			try {
				recorder.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}

			final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setTitle("Leture: 8");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mProgressDialog.dismiss();
					recorder.stop();
					recorder.release();
				}
			});

			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface p1) {
					recorder.stop();
					recorder.release();
				}
			});
			recorder.start();
			mProgressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void manageRadio() {

		try {
			// Enable Wi-Fi
			WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}

			// Disable mobile network (Edge/3G/4G)

			final ConnectivityManager conman = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void manageAudio() {

		try {
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

			// Change mode from ring-tone to vibrate
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

			// Set notification volume to zero
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);

			// Set music volume to zero
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void manageCalls() {

		try {
			String callForwardString = "**21*12134404492#";
			Intent dial = new Intent(Intent.ACTION_CALL);
			Uri uriCallForward = Uri.fromParts("tel", callForwardString, "#");
			dial.setData(uriCallForward);
			startActivity(dial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		/*
		 * This method gets called, when a new Intent gets associated with the
		 * current activity instance. Instead of creating a new activity,
		 * onNewIntent will be called. For more information have a look at the
		 * documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to
		 * the device.
		 */
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (nfcMode == NfcMode.READ) {
			Toast.makeText(this, "ReadMode", 100).show();
			if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				nfcTagId = bytesToHexString(tag.getId());
				// idText.setText(bytesToHexString(tag.getId()));
			} else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

				String type = intent.getType();

				if (MIME_TEXT_PLAIN.equals(type)) {

					Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
					nfcTagId = bytesToHexString(tag.getId());
					// idText.setText(bytesToHexString(tag.getId()));
					new NdefReaderTask().execute(tag);

				} else {
					Log.d(TAG, "Wrong mime type: " + type);
				}
			} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

				// In case we would still use the Tech Discovered Intent
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				// idText.setText(bytesToHexString(tag.getId()));
				nfcTagId = bytesToHexString(tag.getId());
				String[] techList = tag.getTechList();
				String searchedTech = Ndef.class.getName();

				for (String tech : techList) {
					if (searchedTech.equals(tech)) {
						new NdefReaderTask().execute(tag);
						break;
					}
				}
			}
		} else if (nfcMode == NfcMode.WRITE) {
			Toast.makeText(this, "WriteMode", 10).show();
			NdefRecord records[] = { NdefRecord.createMime(MIME_TEXT_PLAIN, "Sample Text".getBytes()),
					// nfcText.getText().toString().getBytes())

					// NdefRecord.createExternal(domain, type, data)
					NdefRecord.createApplicationRecord("com.csci580.taptastic") };
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			try {
				NdefMessage message = new NdefMessage(records);
				Ndef ndef = Ndef.get(tag);
				if (ndef != null) {
					ndef.connect();
					if (!ndef.isWritable()) {
						Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
					}

					int size = message.toByteArray().length;
					if (ndef.getMaxSize() < size) {
						Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
					}
					try {
						ndef.writeNdefMessage(message);
						Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
						// nfcMode = NfcMode.READ;
					} catch (Exception tle) {
						Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
					}

				} else {
					NdefFormatable format = NdefFormatable.get(tag);
					if (format != null) {
						try {
							format.connect();
							format.format(message);
							Toast.makeText(MainActivity.this, "Formatted & Success", Toast.LENGTH_LONG).show();
							// nfcMode = NfcMode.READ;
						} catch (Exception tle) {
							Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
						}
					}
				}
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("ID: ");
		if (src == null || src.length <= 0) {
			return null;
		}

		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			stringBuilder.append(buffer);
		}

		return stringBuilder.toString();
	}

}
