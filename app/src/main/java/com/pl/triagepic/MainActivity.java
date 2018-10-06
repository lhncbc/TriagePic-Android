/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD-like license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part 52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *  The names, trademarks, and service marks of the National Library of Medicine, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITEDTO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pl.triagepic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.triagepic.Result.SearchResult;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is main screen - home screen.
 * Every thing starts from here.
 */

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener, NetworkStateReceiver.NetworkStateReceiverListener {
    public static final String TAG = "MainActivity";

	private static final int SEL_WEB_SERVER_ID = Menu.FIRST + 1;

    private static final String LOGIN = "Login";
	private static final String LOGOUT = "Logout";
    private static final String GUEST = "Guest";
    private static final int LOGIN_REQUEST = 1;
    private static final int SELECT_WEBSERVER = 2;
    private static final int ACCEPTANCE_REQUEST = 3;
    private static final int BURDEN_STATEMENT_SPLASH = 4;

    private static final int LOGIN_ACTIVITY = 9;
    private static final int SELECT_HOSPITAL = 10;
    private static final int SELECT_EVENT = 11;

    /**
     * latency indicator
     */

    private static final int UNKNOWN = 0;
    private static final int DISCONNECTED = 1;
    private static final int POOR = 2;
    private static final int GOOD = 3;
    private static final int EXCELLENT = 4;

    private static final int CONNECTED = 2;

    private static final int[] ServerRatingImage = {
            R.drawable.status_unknown,
            R.drawable.status_disconnected,
            R.drawable.status_low,
            R.drawable.status_mid,
            R.drawable.status_high
    };
    private static final String[] ServerRatingString = {
            "Unknown",
            "Disconnected",
            "Poor",
            "Good",
            "Excellent"
    };
    private static final int[] ServerRatingColor = {
            Color.BLACK,
            Color.RED,
            Color.BLACK
    };

    TriagePic app;
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    String soapActionChechAuth = "";

    boolean bAuthStatus = false;
    String webServer = "";
    long webServerId = 0;
    String curSelEvent = "";
    String curSelEventShortName = "";
    String curSelHospital = "";
    long curSelHospitalId = -1;
    long curSelEventId = -1;

    String username = "";
 	String password = "";
    String token = "";
    String tokenAnonymous = "";
    int tokenStatus = TriagePic.TOKEN_UNKNOWN;

    String oldUsername = "";
 	String oldPassword = "";
 	Credential credential;	
    
 	boolean quickStart = false;
	private int nCurSelQuickStart = 1;
	final CharSequence[] items = {"1 second", "5 seconds"};
        
	static boolean wasBurdenCalled = false;
	static boolean wasSplashCalled = false;

// 	Button loginButton;
//	Button findPeopleButton;
	Button buttonStart;
	TextView displayText;
//	TextView whoTextView;
	Button buLoginLogout;
	TextView whoTextView;
	ImageView imageViewLogo;
    TextView youAreDeveloperNow;
//	Button aboutButton;
	
	TextView tvLatestEventName;
	TextView tvTotalCount;
	TextView tvCurDate;
	TextView tvWebServer;
//	RatingBar ratingBar;
    TextView textViewServerRating;

    Button buttonCheck;
    Button buttonChange;

    int ratings = 0;
	TextView tvServerLatency;
	
	String strDisplay;
	private String returnString = "";
    MyPingEcho myPingEcho = new MyPingEcho();
	
	//A ProgressDialog object  
    private ProgressDialog progressDialog;  
    
    private static boolean asked = false;

    // shake
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float lastX;
    private float lastY;
    private float lastZ;
    private final float NOISE = (float) 5.0;
    private final float SHAKE_THRESHOLD = (float) 800.00;
    private int shakeCount = 0;
    private int shakeLeftCount = 0;
    private int shakeRightCount = 0;
    private long lastUpdateTime = 0;
    private boolean developer;
//    TextView textViewX;
//    TextView textViewY;
//    TextView textViewZ;

    private NetworkStateReceiver networkStateReceiver;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * network connection
         * version 9.0.6 code 9000601
         */
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    
        // Initial with globe variables.
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

//		app.setWebServer(TriagePic.SPRINT_MOBIL_WEBSERVER);
//		app.setWebServer(TriagePic.PL_WEBSERVER);

		// Get the user auth
        credential = new Credential();

        if (app.getUsername().equalsIgnoreCase(TriagePic.GUEST)){
            bAuthStatus = credential.getAuthStatusPreferences();
            username = credential.getUsernamePreferences().toString();
            password = credential.getPasswordPreferences().toString();
            token = credential.getTokenPreferences().toString();
            webServer = credential.getWebServerPreferences().toString();
            webServerId = credential.getWebServerIdPreferences();
            curSelEvent = credential.getCurSelEventPreferences().toString();
            curSelEventShortName = credential.getCurSelEventShortNamePreferences().toString();
            curSelHospital = credential.getCurSelHospitalPreferences().toString();

            app.setUsername(username);
            app.setPassword(password);
            app.setToken(token);
        }

        // change isLogin
        if (isLogin() == false)
        {
            login();
        }

        if (!app.getPassword().isEmpty() && !app.getToken().isEmpty()){
            app.setAuthStatus(true);
        }
        else {
            app.setAuthStatus(false);
        }

		Initialize();
    }

	private void Acceptance() {
		Intent i = new Intent("com.pl.TriagePic.ACCEPTACTIVITY");
		startActivityForResult(i, ACCEPTANCE_REQUEST);
	}

	private void Initialize() {
        // shake
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        shakeCount = 0;
        shakeLeftCount = 0;
        shakeRightCount = 0;

		whoTextView = (TextView) findViewById(R.id.tvWho);
		buLoginLogout = (Button) findViewById(R.id.buLoginLogout);
		
		imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);

        youAreDeveloperNow = (TextView) findViewById(R.id.textViewYouAreDeveloperNow);
        if (app.getDeveloper() == true){
            youAreDeveloperNow.setVisibility(View.VISIBLE);
        }
        else {
            youAreDeveloperNow.setVisibility(View.INVISIBLE);
        }

//		findPeopleButton = (Button)findViewById(R.id.buFindPeople);
		buttonStart = (Button)findViewById(R.id.buttonStart);
		displayText = (TextView)findViewById(R.id.tv);

		tvWebServer = (TextView)findViewById(R.id.textViewWebServer);
        if (app.getCurSelWebServer() != null) {
            tvWebServer.setText(app.getCurSelWebServer().getName().toString());
        }
//		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        textViewServerRating = (TextView) findViewById(R.id.textViewServerRating);
		tvServerLatency = (TextView)findViewById(R.id.textViewServerLatency);

        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        buttonCheck.setOnClickListener(this);

        textViewServerRating.setText(ServerRatingString[UNKNOWN]);
        textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);

        tvLatestEventName = (TextView)findViewById(R.id.textViewLatestEventName);
        tvTotalCount = (TextView)findViewById(R.id.textViewTotalCount);
		tvCurDate = (TextView)findViewById(R.id.textViewCurDate);

        buttonChange = (Button) findViewById(R.id.buttonChange);
        buttonChange.setOnClickListener(this);

//		findPeopleButton.setOnClickListener(this);
		buttonStart.setOnClickListener(this);

        whoTextView.setText(app.getUsername());
        if (app.getUsername().equalsIgnoreCase("Guest") == true){
//            buLoginLogout.setBackgroundResource(R.drawable.button_login);
            buLoginLogout.setText("Login");
        }
        else {
//            buLoginLogout.setBackgroundResource(R.drawable.button_logout);
            buLoginLogout.setText("Logout");
        }
        buLoginLogout.setOnClickListener(this);

        if (app.getAuthStatus() == true){
            if (WebServer.AmIConnected(this) == true) {
                new callSearchCountAsyncTask().execute();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No internet connectivity now. Please reconnect and try again.")
                        .setCancelable(true)
                        .setTitle("Warning")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvLatestEventName.setText(app.getCurSelEvent());
//        tvTotalCount.setText(app.getCurSearchCount().toString());
        tvTotalCount.setText(app.getTotalCount().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); // modified in version 9.0.4
        String str = sdf.format(new Date());
        tvCurDate.setText(str);
    }

    @Override
	protected void onPause() {
		super.onPause();

        // shake
        mSensorManager.unregisterListener(this);
	}

	
	@Override
	protected void onResume() {
		super.onResume();

        // shake
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (WebServer.AmIConnected(this) == true) {
            new CheckInternetConnectionAsyncTask().execute();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No internet connectivity now. Please reconnect and try again.")
                    .setCancelable(true)
                    .setTitle("Warning")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
	}

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdateTime) < 100) {
            return;
        }

        long diffTime = (curTime - lastUpdateTime);
        lastUpdateTime = curTime;

        // read the data
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

        if (speed > SHAKE_THRESHOLD) {
            shakeCount++;
            if (shakeCount >= 7){
                shakeCount = 0;
                Log.d("sensor", "shake detected w/ speed: " + speed);
                if (developer == true){
                    developer = false;
                }
                else {
                    developer = true;
                }
                app.setDeveloper(developer);
                credential.saveDeveloperPreferences(developer);
                if (app.getDeveloper() == true){
                    youAreDeveloperNow.setVisibility(View.VISIBLE);
                }
                else {
                    youAreDeveloperNow.setVisibility(View.INVISIBLE);
                }
            }
        }
        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();		
	}
	

	public void onClick(View v) {
		switch(v.getId()) {
            case R.id.buLoginLogout:
                if (app.getAuthStatus() == false) {
                    Login();
                } else {
                    removeDataAndLogout();
                }
                break;
            case R.id.buttonStart:
                Intent i = new Intent();
                i = new Intent(MainActivity.this, BoxListFragmentActivity.class);
                startActivity(i);
                break;
            case R.id.buttonChange:
                selectEvent();
                break;
            case R.id.buttonCheck:
                testLatency();
                break;
        }
	}

    private void selectEvent() {
        Intent i = new Intent(MainActivity.this, EventListFragmentActivity.class);
        startActivityForResult(i,SELECT_EVENT);
    }

    private void EventList() {
		Intent i = new Intent("com.pl.TriagePic.EVENTLIST");
		startActivity(i);																							
	}

	private void Login() {
		Intent i = new Intent("com.pl.TriagePic.LOGINACTIVITY");
		startActivityForResult(i, LOGIN_REQUEST);												
		
	}

	private void FindPeople() {
		Intent i = new Intent("com.pl.TriagePic.PATIENTLISTEXACTIVITY");
		startActivity(i);																		
		
	}

	private void About() {
		Intent openAbout = new Intent("com.pl.TriagePic.ABOUT");
		startActivity(openAbout);												
	}

	private void Logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("You are about to logout")
			   .setTitle("Are you sure?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		       		credential.reset();
//					loginButton.setText(LOGIN);
					username = credential.getUsername();
					password = credential.getPassword();
// to do                    app.setGroupName(credential.getGroupName());
                       if (app.getTokenStatus() == TriagePic.TOKEN_AUTH){
                           credential.saveUserPreferences(username, password, app.getToken());
                       }
                       else {
                           credential.saveUserPreferences(username, password, app.getTokenAnonymous());
                       }
// to do                    credential.saveGroupIdPreferences(app.getGroupName());
		       		app.setUsername(username);
		       		app.setPassword(password);
					whoTextView.setText(username);
					if (username.equalsIgnoreCase("Guest") == true){
                        buLoginLogout.setText("Login");
                    }
					else {
                        buLoginLogout.setText("Logout");
					}
                       app.setToken("");
                       app.setTokenAnonymous("");
// to do                       app.setTimeWhenGotAnonymousToken(0);
                       app.setTokenStatus(TriagePic.TOKEN_UNKNOWN);
                       credential.saveTokenPreferences("");
                       credential.saveTokenAnonymousPreferences("");
                       credential.saveTimeWhenGotAnonymousTokenPreferences(0);
                       credential.saveTokenStatusPreferences(TriagePic.TOKEN_UNKNOWN);

                   }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LOGIN_REQUEST){
			if (data == null){ // In case of doing nothing
				return;
			}
			Bundle extras = data.getExtras();
			username = (String)extras.get("username");			
			password = (String)extras.get("password");
			credential.setUsernamePreferences(username);
			credential.setPasswordPreferences(password);
// to do            credential.setGroupNamePreferences(app.getGroupName());

            // version 4.0.0.
            credential.setTokenPreferences(app.getToken());
            credential.setTokenStatusPreferences(app.getTokenStatus());

			whoTextView.setText(username);
			if (username.equalsIgnoreCase("Guest") == true){
//				buLoginLogout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_login));
                buLoginLogout.setText("Login");
			}
			else {
//				buLoginLogout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_logout));
                buLoginLogout.setText("Logout");
			}
		}
		else if (requestCode == SELECT_WEBSERVER){
// to do			webServer = app.getWebServer();
			credential.saveWebServerPreferences(webServer);
		}
        else if (requestCode == LOGIN_ACTIVITY){
            if (app.getAuthStatus()) {
                String msg = "You will need to make two selections:\n\t1. Hospital \n\t2. Event";
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.triagepic_small_icon);
                builder.setMessage(msg)
                        .setTitle("Welcome " + app.getUsername() + "!")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(MainActivity.this, "You are selecting hospital...", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, HospitalListFragmentActivity.class);
                                startActivityForResult(i, SELECT_HOSPITAL);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                alert.setCancelable(false); // V 7.0.2. No close. It is necessary to have this line.
                Toast.makeText(this, "Welcome " + app.getUsername() + "!", Toast.LENGTH_SHORT).show();
            }
            else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
        else if (requestCode == SELECT_HOSPITAL){
            String msg = "You have selected hospital: " + app.getCurSelHospital() + "\nNow will need to make the second selection: \n\tEvent";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.triagepic_small_icon);
            builder.setMessage(msg)
                    .setTitle("Welcome " + app.getUsername() + "!")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "You are selecting event...", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, EventListFragmentActivity.class);
                            startActivityForResult(i,SELECT_EVENT);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            alert.setCancelable(false); // V 7.0.2. No close. It is necessary to have this line.
        }
        else if (requestCode == SELECT_EVENT){
            String u = app.getUsername();
            String p = app.getPassword();
            String t = app.getToken();
            Boolean a = app.getAuthStatus();
            long wid = app.getWebServerId();
            long hid = app.getCurSelHospitalId();
            long eid = app.getCurSelEventId();

            if (isLogin()){
                DataSource s = new DataSource(this, app.getSeed());
                s.open();
                long authId = s.getAuthId();
//                s.updateUsernamePassword(u, p, t, (int)wid, (int)hid, (int)eid, authId);
                s.setEventId((int) eid, (int) authId);
                s.close();
            }
            saveCredentials();

            Initialize();
        }
    }

    boolean bLogin = false;

    public String getAnonymousToken(){
        /*
        to do
        String errorCode = "";
        // get the anonymous token
        WebServer ws = new WebServer();
        RequestAnonTokenResult requestAnonTokenResult = ws.callRequestAnonToken();
        String anonymousToken = requestAnonTokenResult.getTokenAnonymous();
        errorCode = requestAnonTokenResult.getErrorCode();
        String errorMsg = requestAnonTokenResult.getErrorMessage();
        int tokenStatus = ws.getTokenStatus();
        if (errorCode.equalsIgnoreCase("0") == true){
            app.setTokenAnonymous(anonymousToken);
            app.setTokenStatus(TriagePic.TOKEN_ANONYMOUS);
            app.recordTimeWhenGotAnonymousToken();
            return errorCode;
        }
        else {
            returnString = "";
        }
        */
        return returnString;
    }

    private void changeLog() {
        Intent i = new Intent(this, ChangeLogActivity.class);
        startActivity(i);
    }

	private void ContactUs() {
		String s = getDiviceInfo();
		
		// Send email.
		Intent email = new Intent(Intent.ACTION_SEND); 
		email.setType("plain/text");
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{"removed@email.com".toString()});
		email.putExtra(Intent.EXTRA_SUBJECT, "");
		email.putExtra(Intent.EXTRA_TEXT, s);
		try { 
			startActivity(Intent.createChooser(email, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	private String getDiviceInfo() {
		// Get the info first
		String s="\n\n\n\n\n\n\n\nMy Device Info:";
		s += "\nModel: " + getManafacturer();
		s += "\nAndroid Ver: " + Build.VERSION.RELEASE;
		s += "\nKernel Ver: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")";
		s += "\nBuild Num: " + Build.ID;
		s += "\n";
		return s;
	}

	public String getManafacturer() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {
		    return capitalize(manufacturer) + " " + model;
		  }
		}

	private String capitalize(String s) {
		  if (s == null || s.length() == 0) {
		    return "";
		  }
		  char first = s.charAt(0);
		  if (Character.isUpperCase(first)) {
		    return s;
		  } else {
		    return Character.toUpperCase(first) + s.substring(1);
		  }
		}
		
	private void Latency2() {
		Intent i = new Intent(MainActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}

	private void SelectWebServer() {
		Intent i = new Intent("com.pl.TriagePic.WEBSERVERSELECTACTIVITY");
		startActivityForResult(i, SELECT_WEBSERVER);															
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

        if (app.getDeveloper() == true){
            menu.setHeaderTitle("Advanced Settings");
            menu.add(Menu.NONE, SEL_WEB_SERVER_ID, Menu.NONE, "Select Web Server");
        }
    }	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    
		switch (item.getItemId()) {
        case SEL_WEB_SERVER_ID:
        	SelectWebServer();
    	    break;
        }

		return super.onContextItemSelected(item);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        switch (item.getItemId()) {
            case R.id.itemLatency:
                testLatency();
                break;
            default:
                break;
        }
        return true;
    }

    private void testLatency() {
        WebServer ws = new WebServer();
        if (app.getWebServerId() != -1){
            ws.setId(app.getWebServerId());

            DataSource s = new DataSource(this, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

        Intent i = new Intent(this, LatencyActivity.class);
        i.putExtra("webServer", ws.getWebService());
        startActivity(i);
    }

    private void questionAndAnswer() {
        Intent i = new Intent(this, QuestionAndAnswerActivity.class);
        startActivity(i);
    }

    private void about() {
        Intent openAbout = new Intent(this, About.class);
        startActivity(openAbout);
    }

    @Override
	public boolean onPrepareOptionsMenu(Menu menu) { 
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean isOnline(){
	    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } 
        else {
        	return false;
        }	        
	}
    // modified for version 4.0.0.
    // 1. check if there is connection.
    // 2. check the token, if not available get the anonymous token.
    // 3. ping
    private void CheckInternetConnection() {
        // replaced by the following codes
        // version 4.0.0.
        if (app.getTokenStatus() == TriagePic.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
            returnString = myPingEcho.Call();
        }
        else if (app.getTokenStatus() == TriagePic.TOKEN_UNKNOWN){
            getAnonymousToken();
// to do            app.recordTimeWhenGotAnonymousToken();
            myPingEcho.setToken(app.getTokenAnonymous());
            returnString = myPingEcho.Call();
        }
        else if (app.getTokenStatus() == TriagePic.TOKEN_ANONYMOUS) {
            /*
            do do
            if (app.isAnonymousTokenExpired()){
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            */
            myPingEcho.setToken(app.getTokenAnonymous());
            returnString = myPingEcho.Call();
        }
        else {
            returnString = "No valid token.";
        }
    }

    private class CheckInternetConnectionAsyncTask extends AsyncTask<Void, Integer, Void>
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
            // added in version 4.0.0
            if (app.getTokenStatus() == TriagePic.TOKEN_UNKNOWN) {
                getAnonymousToken();
// to do                app.recordTimeWhenGotAnonymousToken();
            }
        	returnString = "";
			tvServerLatency.setText("Detecting...");

//			ratingBar.setRating(0);
            textViewServerRating.setText(ServerRatingString[UNKNOWN]);
            textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{
                CheckInternetConnection();
			}  
            return null;  
        }  
  
        //Update the progress  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
  			if (returnString.equalsIgnoreCase(MyPingEcho.TIME_OUT) == true || returnString.isEmpty() == true){
    			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    			builder.setMessage(myPingEcho.ERR_MSG)
    			       .setCancelable(true)
    			       .setTitle("Latency")
    			       .setNegativeButton("Close", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			                dialog.cancel();
    			           }
    			       });
    			AlertDialog alert = builder.create();		
    			alert.show();
                tvServerLatency.setText(myPingEcho.ERR_MSG);
    		}
    		else {
                tvServerLatency.setText(myPingEcho.getLatencyTime() + "ms");
                int r = Integer.valueOf(myPingEcho.getLatencyTime());
                if (r < 500) {
                    textViewServerRating.setText(ServerRatingString[EXCELLENT]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 750) {
                    textViewServerRating.setText(ServerRatingString[GOOD]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 1000) {
                    textViewServerRating.setText(ServerRatingString[POOR]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
    			else {
//    				ratingBar.setRating(0);
                    textViewServerRating.setText(ServerRatingString[UNKNOWN]);
                    textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);
                }
            }
		}
    }  
    
    private class GoodByeAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Good Bye...");  
            progressDialog.setCancelable(false);  
            progressDialog.setIndeterminate(false);  
            progressDialog.show();  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{  
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}  
            return null;  
        }  
  
        //Update the progress  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
            progressDialog.dismiss();  
			finish();
			System.exit(0);
        }  
    }

    private void login() {
        if (isLogin() == false){
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY);
            return;
        }
        String name = app.getUsername();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are already logged in. Current user is \"" + name + "\".")
                .setTitle("Warning!")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void logout() {
        if (isLogin() == false){
            Toast.makeText(this, "You are already logged out.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to logout of TriagePic?")
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cleanUsersTable();
                        app.setUsername("Guest");
                        app.setPassword("");
                        app.setToken("");
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isLogin(){
        boolean result = false;
        String u = app.getUsername();
        String p = app.getPassword();
        String t = app.getToken();
        boolean a = app.getAuthStatus();

        if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST) || t.isEmpty()){
            // return false
            if (a == true){
                app.setAuthStatus(false);
            }
            result = false;
        }
        else {
            // return true
            if (a == false){
                app.setAuthStatus(true);
            }
            result = true;
        }
        return result;
    }

    protected void cleanUsersTable() {
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        s.deleteAllEvents();
        s.deleteAllHospitals();
        s.deleteAllWebServers();
        s.deleteAllUsers();
        s.deleteAllImages();
        s.deleteAllPatients();
        s.close();
    }

    public class Credential {
        private boolean bAuthStatus = false;
        private String username = GUEST;
        private String password = "";
        private String token = "";
        private String webServer = WebServer.TT_NAME;
        private long webServerId = 0;
        private String curSelEvent = "";
        private String curSelEventShortName = "";
        private String curSelHospital = "";
        private boolean quickStart;
        private boolean developer;

       public Credential(){
            reset();// initial
            getAuthStatus();
            getUsernamePreferences();
            getPasswordPreferences();
            getTokenPreferences();
            getAuthStatus();
            getWebServer();
            getWebServerIdPreferences();
            getCurSelEventPreferences();
            getCurSelEventShortNamePreferences();
            getCurSelHospitalPreferences();
       }

        public void cleanAll(){
            reset();
            saveAllUserPreferences();
        }

        public void saveAllUserPreferences() {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("bAuthStatus", bAuthStatus);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("token", token);
            editor.putString("webServer", webServer);
            editor.putLong("webServerId", webServerId);
            editor.putString("curSelEvent", curSelEvent);
            editor.putString("curSelEventShortName", curSelEventShortName);
            editor.putString("curSelHospital", curSelHospital);
            editor.commit();
        }

        public String getUsername(){
            return username;
        }

        public String getPassword(){
            return password;
        }

        public String getToken(){
            return token;
        }

        public boolean getAuthStatus(){
// 			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true && token.isEmpty()){
// 				bAuthStatus = false;
// 			}
// 			else {
// 				bAuthStatus = true;
// 			}
            return bAuthStatus;
        }

        public String getWebServer(){
            return webServer;
        }

        public long getWebServerId() {return webServerId;}

        public void reset() {
            bAuthStatus = false;
            username = GUEST;
            password = "";
            token = "";
// 			saveUserPreferences(username, password);
            webServer = WebServer.TT_NAME;
            webServerId = 0;
// 			saveWebServerPreferences(webServer);
            curSelEvent = "";
            curSelEventShortName = "";
            curSelHospital = "";
        }

        public boolean verifyAuthStatus(){
            boolean bResult = false;
            if (bAuthStatus == false){
                bResult = false;
            }
            else if (username.equalsIgnoreCase(GUEST) == true){
                bResult = false;
            }
            else if (password.isEmpty() == true){
                bResult = false;
            }
            else if (token.isEmpty() == true){
                bResult = false;
            }
            else {
// 				if (CheckUserAuth() == true){
// 					bResult = true;
// 				}
            }
            return bResult;
        }

       private void setUsernamePreferences(String username){
           this.username = username;

           String encrapedUsername;
           if (this.username.isEmpty() == true){
               encrapedUsername = null;
           }
           else {
               encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
           }

           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putString("username", encrapedUsername);
           editor.commit();
       }

       private void setPasswordPreferences(String password){
           this.password = password;

           String encrapedPassword;
           if (this.password.isEmpty() == true){
               encrapedPassword = null;
           }
           else {
               encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
           }

           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putString("password", encrapedPassword);
           editor.commit();
       }

       private void setTokenPreferences(String token){
           saveTokenPreferences(token);
       }
       private void setTokenAnonymousPreferences(String tokenAnonymous){saveTokenAnonymousPreferences(tokenAnonymous);}
       private void setTimeWhenGotAnonymousTokenPreferences(long timeWhenGotAnonymousToken){saveTimeWhenGotAnonymousTokenPreferences(timeWhenGotAnonymousToken);}
       private void setTokenStatusPreferences(int tokenStatus){saveTokenStatusPreferences(tokenStatus);}

       public void saveUserPreferences(String username, String password, String token){
            this.username = username;
            this.password = password;
            this.token = token;
/*
 			String encrapedUsername;
 			if (this.username.isEmpty() == true){
 				encrapedUsername = null;
 			}
 			else {
 				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
 		    }

 			String encrapedPassword;
 			if (this.password.isEmpty() == true){
 				encrapedPassword = null;
 			}
 			else {
 				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
 		    }
 		    */

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("token", token);
            editor.commit();
        }

        public void saveAuthStatusPreferences(boolean bAuthStatus){
            this.bAuthStatus = bAuthStatus;

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("bAuthStatus", bAuthStatus);
            editor.commit();
        }

        public void saveUsernamePreferences(String username){
            this.username = username;

            /*
 			String encrapedUsername;
 			if (this.username.isEmpty() == true){
 				encrapedUsername = null;
 			}
 			else {
 				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
 		    }
 		    */

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.commit();
        }

        public void savePasswordPreferences(String password){
            this.password = password;
/*
 			String encrapedPassword;
 			if (this.password.isEmpty() == true){
 				encrapedPassword = null;
 			}
 			else {
 				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
 		    }
 		    */

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("password", password);
            editor.commit();
        }

        public void saveTokenPreferences(String token){
            this.token = token;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.commit();
        }

        public void saveWebServerPreferences(String webServer){
            this.webServer = webServer;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("webServer", this.webServer);
            editor.commit();
        }

        public void saveWebServerIdPreferences(long webServerId){
            this.webServerId = webServerId;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("webServerId", this.webServerId);
            editor.commit();
        }

        public void saveCurSelEventPreferences(String curSelEvent){
            this.curSelEvent = curSelEvent;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("curSelEvent", this.curSelEvent);
            editor.commit();
        }

        public void saveCurSelEventShortNamePreferences(String curSelEventShortName){
            this.curSelEventShortName = curSelEventShortName;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("curSelEventShortName", this.curSelEventShortName);
            editor.commit();
        }

        public void saveCurSelHospitalPreferences(String curSelHospital){
            this.curSelHospital = curSelHospital;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("curSelHospital", this.curSelHospital);
            editor.commit();
        }

       private void saveDeveloperPreferences(boolean developer) {
           this.developer = developer;
           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putBoolean("developer", this.developer);
           editor.commit();
       }

       private void saveTokenAnonymousPreferences(String tokenAnonymous){
           tokenAnonymous = tokenAnonymous;
           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putString("tokenAnonymous", tokenAnonymous);
           editor.commit();
       }

       private void saveTimeWhenGotAnonymousTokenPreferences(long timeWhenGotAnonymousToken){
           timeWhenGotAnonymousToken = timeWhenGotAnonymousToken;
           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putLong("timeWhenGotAnonymousToken", timeWhenGotAnonymousToken);
           editor.commit();
       }

       private void saveTokenStatusPreferences(int tokenStatus){
           tokenStatus = tokenStatus;
           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putInt("tokenStatus", tokenStatus);
           editor.commit();
       }

       public boolean getAuthStatusPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.bAuthStatus = sharedPreferences.getBoolean("bAuthStatus", false);
            return this.bAuthStatus;
        }

        public String getUsernamePreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.username = sharedPreferences.getString("username", "guest");
            /*
 			String encryptedUsername = sharedPreferences.getString("username", "guest");
 			if (encryptedUsername.equalsIgnoreCase("Guest") == true){
 				this.username = new String("Guest");
 			}
 			else {
 				this.username = new String(Base64.decode(encryptedUsername, Base64.DEFAULT ) );
 			}
 			*/
            return this.username;
        }

        public String getPasswordPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.password = sharedPreferences.getString("password", "");

            /*
            String encryptedPassword = sharedPreferences.getString("password", "");
 			if (encryptedPassword.isEmpty() == true){
 				this.password = new String("");
 			}
 			else {
 				this.password = new String(Base64.decode(encryptedPassword, Base64.DEFAULT ) );
 			}
 			*/
            return this.password;
        }

        public String getTokenPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.token = sharedPreferences.getString("token", "");
            return this.token;
        }

        public String getWebServerPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.webServer = sharedPreferences.getString("webServer", WebServer.TT_NAME);
            return this.webServer;
        }

        public long getWebServerIdPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.webServerId = sharedPreferences.getLong("webServerId", 0);
            return this.webServerId;
        }

        public String getCurSelEventPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.curSelEvent = sharedPreferences.getString("curSelEvent", "");
            return this.curSelEvent;
        }

        public String getCurSelEventShortNamePreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.curSelEventShortName = sharedPreferences.getString("curSelEventShortName", "");
            return this.curSelEventShortName;
        }

        public String getCurSelHospitalPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.curSelHospital = sharedPreferences.getString("curSelHospital", "");
            return this.curSelHospital;
        }


    }

    private void removeDataAndLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are about to logout the program and clean all the local patient files.")
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cleanAllTables();
                        cleanCredentials();
                        new ExitAsyncTask().execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void cleanAllTables() {
        // clean all the patient's files.
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        s.deleteAllEvents();
        s.deleteAllHospitals();
        s.deleteAllWebServers();
        s.deleteAllUsers();
        s.deleteAllImages();
        s.deleteAllPatients();
        s.close();
    }

    private void cleanCredentials() {
        credential.cleanAll();
    }

    //To use the AsyncTask, it must be subclassed
    private class ExitAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        static final int MAX = 100;
        static final int STEP = 1;

        //Before running code in separate thread
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Logging out, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                int sleepSeconds = 5000;
                int eachSleep = 50;
/*				if (app.getQuickStart() == true){
					sleepSeconds = 1000;
					eachSleep = 10;
				}
				else {
					sleepSeconds = 5000;
					eachSleep = 50;
				}
				*/
//				getInitialData();

                for (int i = 0; i < MAX; i++){
                    try {
                        Thread.sleep(eachSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

    }

    private void saveCredentials() {
        TriagePic app = ((TriagePic)this.getApplication());
        if (credential == null){
            credential = new Credential();
        }
        credential.saveUsernamePreferences(app.getUsername());
        credential.savePasswordPreferences(app.getPassword());
        credential.saveTokenPreferences(app.getToken());
        credential.saveWebServerPreferences(webServer);
        credential.saveWebServerIdPreferences(app.getWebServerId());
        credential.saveCurSelEventPreferences(app.getCurSelEvent());
        credential.saveCurSelEventShortNamePreferences(app.getCurSelEventShortName());
        credential.saveCurSelHospitalPreferences(app.getCurSelHospital());
//        credential.saveWebServerPreferences(app.getWebServerId());
    }

    private class callSearchCountAsyncTask extends AsyncTask<Void, Integer, Void>{
        SearchResult searchResult = new SearchResult();

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            searchResult.toDefault();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                searchResult = getTriageTrakCount();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            if (searchResult.getErrorCode().equalsIgnoreCase("0")){
                app.setCurSearchCount(searchResult.getRecordsFound());
                tvTotalCount.setText(searchResult.getRecordsFound());
            }
            else {
                app.setCurSearchCount("0");
                tvTotalCount.setText("0");
            }
        }
    }

    private SearchResult getTriageTrakCount() {
        WebServer ws = new WebServer();
        ws.setToken(app.getToken());
        app.setCurSearchCount("0");
        String returnString = "";

        ws.setSearchCountOnly(true);

        Filters f = new Filters();
        f.setDefaults();
        app.setFilters(f);

        ViewSettings v = new ViewSettings();
        v.SetToDefault();
        app.setViewSettings(v);

        SearchResult sr = ws.searchCountV34(app.getFilters(), app.getViewSettings(), app.getCurSelEventShortName());

        if (sr.getErrorCode().toString().equals("0") == true){
            returnString = sr.getRecordsFound();
        }
        else {
            returnString = "-1";
        }
        app.setTotalCount(returnString);
        app.setCurSearchCount(returnString);
        app.setCurSelWebServer(ws);
        return sr;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkAvailable() {
        Log.d("tommydevall", "I'm in, baby!");
    }

    @Override
    public void networkUnavailable() {
        Log.d("tommydevall", "I'm dancing with myself");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No internet connection is detected. Please check it.")
                .setCancelable(true)
                .setTitle("Warning")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}