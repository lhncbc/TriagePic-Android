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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LatencyActivity extends Activity  implements View.OnClickListener {
    private static final String METHOD_NAME_CHECH_AUTH = "checkUserAuth";
    
    private static final String LOGIN = "Login";
	private static final String LOGOUT = "Logout";
    private static final String GUEST = "Guest";
    private static final int LOGIN_REQUEST = 1;
    private static final int SELECT_WEBSERVER = 2;

	private ProgressDialog progressDialog = null; 
    
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    String soapActionChechAuth = "";
    
    String username = "";
 	String password = "";
 	Credential credential;	
 	
	TextView tvWebServer;
//	RatingBar ratingBar;
	int ratings = 0;
	TextView tvServerLatency;

    ImageView imageViewServerRating;
    TextView textViewServerRating;

	private String returnString = "";
//    MyPing myPing = new MyPing();
    MyPingEcho myPingEcho;

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

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.latency);

        TriagePic app = (TriagePic) this.getApplication();
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        WebServer ws = new WebServer();
        if (app.getWebServerId() != -1){
            ws.setId(app.getWebServerId());

            DataSource s = new DataSource(this, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }
        if (ws == null){
            Log.e("Error", "webServer is not defined in database." );
        }

        myPingEcho = new MyPingEcho(ws);
        if (app.getTokenStatus() == TriagePic.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
            myPingEcho.setUsername(app.getUsername());
            myPingEcho.Call();
        }
        else if (app.getTokenStatus() == TriagePic.TOKEN_ANONYMOUS){
            myPingEcho.setToken(app.getTokenAnonymous());
            myPingEcho.setUsername(app.getUsername());
            myPingEcho.Call();
        }
        else {
            Toast.makeText(this, "Warning: token is not defined.", Toast.LENGTH_SHORT).show();
        }

		Initialize();

		new CheckInternectConnectionAsyncTask().execute();		
	}

	private void Initialize() {
		tvWebServer = (TextView)findViewById(R.id.textViewWebServer);
		tvServerLatency = (TextView)findViewById(R.id.textViewServerLatency);

        imageViewServerRating = (ImageView) findViewById(R.id.imageViewServerRating);
        textViewServerRating = (TextView) findViewById(R.id.textViewServerRating);

        imageViewServerRating.setImageResource(ServerRatingImage[UNKNOWN]);
        textViewServerRating.setText(ServerRatingString[UNKNOWN]);
        textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);

	    Intent sender = getIntent();
	    webServer = sender.getExtras().getString("webServer");	
	    tvWebServer.setText(webServer);
	}

	public void onClick(View v) {

	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.latency_menu, menu);
        return true;
    }
    
    boolean bLogin = false;

    @Override
 	public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
         case R.id.menu_refresh:
     		new CheckInternectConnectionAsyncTask().execute();
         	break;
//         case R.id.itemMainPage:
//         	GoBackToMainPage();
//         	break;
//         case R.id.itemTutorials:
//         	Tutorials();
//         	break;
         case R.id.itemContactUs:
         	ContactUs();
         	break;
          }
         return true;
 	}

    // Email to us
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
    	    Toast.makeText(LatencyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	}
    }
    private String getDiviceInfo() {
    	// Get the info first
    	String s="\n\n\n\n\n\n\n\nMy Device Info:";
    	s += "\nModel: " + getManafacturer();
    	s += "\nAndroid Ver: " + android.os.Build.VERSION.RELEASE;
    	s += "\nKernel Ver: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
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
    // End of email to us
    
    private void Tutorials() {
    	Toast.makeText(this, "To be added", Toast.LENGTH_SHORT).show();
//		Intent i = new Intent(LatencyActivity.this, TutorialListActivity.class);
//		startActivity(i);	
	}

	public class Credential {
		private boolean bAuthStatus;
		private String username;
		private String password;
		private String webServer;
		
		Credential(){
			getUsernamePreferences();
			getPasswordPreferences();
			getAuthStatus();
			getWebServer();
		}
		
		public String getUsername(){
			return username;
		}
		
		public String getPassword(){
			return password;
		}
		
		public boolean getAuthStatus(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true){
				bAuthStatus = false;
			}
			else {
				bAuthStatus = true;				
			}
			return bAuthStatus;
		}
		
		public String getWebServer(){
			return webServer;
		}

		public void reset() {
			bAuthStatus = false;
			username = GUEST;
			password = "";
			saveUserPreferences(username, password);
//			webServer = TriagePic.PL_WEBSERVER;
			webServer = WebServer.TT_NAME;
			saveWebServerPreferences(webServer);
		}
		
		private void saveWebServerPreferences(String webServer) {
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
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
			else {
				if (CheckUserAuth() == true){
					bResult = true;
				}
			}
			return bResult;
		}
		
		private void checkUserAuth() {
	    	SoapObject request = new SoapObject(nameSpace, METHOD_NAME_CHECH_AUTH);

	    	// Register user
	    	// Start
	    	request.addProperty("username", username.toString());
	    	request.addProperty("password", password.toString());
	    	// End
	    	
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = false;
	    	envelope.setOutputSoapObject(request);
	    	
//	    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
	    	HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
	        aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//	        SoapObject result = null;
	        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
	    	try
	    	{
				aht.debug = true;
	    		aht.call(soapActionChechAuth, envelope);
				Log.w("Soap request ", aht.requestDump);
				Log.w("Soap responce ", aht.responseDump);
				result = (SoapPrimitive)envelope.getResponse();  
	    	} catch (Exception e) {
	    		result = null;
	    	}
	    	
	       	SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
	    	String valid = resultRequestSOAP.getPropertyAsString("valid");
	    	String errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
	    	String errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
	    	
	    	if (errorMessage.equalsIgnoreCase("0") == true){
	    		if (valid.equalsIgnoreCase("true")){
	    			returnString = "true";
	    		}
	    		else {
	    			returnString = "false";
	    		}
	    	}
		}
		
		public void saveUserPreferences(String username, String password){
			this.username = username;
			this.password = password;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", this.username);
			editor.putString("password", this.password);
			editor.commit();
		}
		
		private void setUsernamePreferences(String username){
			this.username = username;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", this.username);
			editor.commit();
		}
		  
		private void setPasswordPreferences(String password){
			this.password = password;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("password", this.password);
			editor.commit();
		}

		private void setWebServerPreferences(String webServer){
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
		}

		private String getUsernamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.username = sharedPreferences.getString("username", "guest");
			return this.username;
		}
	
		private String getPasswordPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.password = sharedPreferences.getString("password", "");
			return this.password;
		}

		private String getWebServerPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.webServer = sharedPreferences.getString("webServer", "");
			return this.webServer;
		}
	}

	public boolean CheckUserAuth() {
		boolean bResult = false;
		returnString = "";

		// Better to use the threads. 
	    //limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			checkUserAuth();
	    		}

				private void checkUserAuth() {
			    	SoapObject request = new SoapObject(nameSpace, METHOD_NAME_CHECH_AUTH);

			    	// Register user
			    	// Start
			    	request.addProperty("username", username.toString());
			    	request.addProperty("password", password.toString());
			    	// End
			    	
			    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			    	envelope.dotNet = false;
			    	envelope.setOutputSoapObject(request);
			    	
			    	HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
			        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
			    	try
			    	{
						aht.debug = true;
			    		aht.call(soapActionChechAuth, envelope);
						Log.w("Soap request ", aht.requestDump);
						Log.w("Soap responce ", aht.responseDump);
						result = (SoapPrimitive)envelope.getResponse();  
			    	} catch (Exception e) {
			    		result = null;
			    	}
			    	
			       	SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
			    	String valid = resultRequestSOAP.getPropertyAsString("valid");
			    	String errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
			    	String errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
			    	
			    	if (errorMessage.equalsIgnoreCase("0") == true){
			    		if (valid.equalsIgnoreCase("true")){
			    			returnString = "true";
			    		}
			    		else {
			    			returnString = "false";
			    		}
			    	}
				}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
	    if (returnString.equalsIgnoreCase("true")){
	    	bResult = true;
	    }
	    else{
	    	bResult = false;
	    }
	    return bResult;
	}

	private void CheckInternectConnection() {
        returnString = myPingEcho.Call();
	}
		
    private class CheckInternectConnectionAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
        	returnString = "";
			tvServerLatency.setText("Detecting...");
            imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
            textViewServerRating.setText(ServerRatingString[DISCONNECTED]);
            textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);

            progressDialog = new ProgressDialog(LatencyActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Detecting the speed, please wait...");  
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
				CheckInternectConnection();
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
        	
    		if (returnString.equalsIgnoreCase(MyPingEcho.TIME_OUT) == true || returnString.isEmpty() == true){
    			AlertDialog.Builder builder = new AlertDialog.Builder(LatencyActivity.this);
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
                    imageViewServerRating.setImageResource(ServerRatingImage[EXCELLENT]);
                    textViewServerRating.setText(ServerRatingString[EXCELLENT]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 750) {
                    imageViewServerRating.setImageResource(ServerRatingImage[GOOD]);
                    textViewServerRating.setText(ServerRatingString[GOOD]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 1000) {
                    imageViewServerRating.setImageResource(ServerRatingImage[POOR]);
                    textViewServerRating.setText(ServerRatingString[POOR]);
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else {
                    imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
                    textViewServerRating.setText(ServerRatingString[DISCONNECTED]);
                    textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);
                }
            }
    	}
    }
	private void GoBackToMainPage() {
		Intent i = new Intent(LatencyActivity.this, HomeActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}
}