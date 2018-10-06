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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.triagepic.Result.SearchResult;

import java.util.ArrayList;

public class SplashActivity extends Activity {
    private static final String GUEST = "Guest";
    private static final int LOGIN = 1;
    
    TriagePic app;

    //    TriagePic app;
	TextView tv;
	ProgressBar progressBar;
	//A ProgressDialog object  
    private ProgressDialog progressDialog;  
    boolean quickStart = false;
    
	String webServer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		app.setSeed("nih/nlm/ceb/TriagePic" + Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID));

        long authorId = -1;
    	String username = "";
    	String password = "";
        String token = "";
        long webServerId = -1;
        long hospitalId = -1;
        long eventId = -1;

        Filters filters = new Filters();
        ViewSettings viewSettings = new ViewSettings();
        viewSettings.SetToDefault();

    	DataSource s = new DataSource(this, app.getSeed());
    	try {
    		s.open();
        	try {
                authorId = s.getAuthId();
        		username = s.getUsername();
        		password = s.getPassword();
                token = s.getToken();
                webServerId = s.getWebServerId();
                hospitalId = s.getHospitalId();
                eventId = s.getEventId();
                filters = s.getFilters();
                viewSettings = s.getViewSettings();
        	}
        	catch(android.content.ActivityNotFoundException ex) {
        		username = "";
        		password = "";
                token = "";
                webServerId = -1;
                hospitalId = -1;
                eventId = -1;
                filters.setDefaults();;
                viewSettings.SetToDefault();
        	}
    	
        	// First time sign in. Need to login.
        	if (authorId == -1){
        		username = TriagePic.GUEST;
        		password = "";
                token = "";
                webServerId = -1;
                hospitalId = -1;
                eventId = -1;
                filters.setDefaults();;
                viewSettings.SetToDefault();
        		authorId = s.createAuthentication(username, password, token, -1, -1, -1);
        		if (authorId != -1){
        			app.setAuthId(authorId);
        			app.setAuthStatus(false);
        			app.setUsername(username);
        			app.setPassword(password);
                    app.setToken(token);
        			app.setWebServerId(-1);
                    app.setCurSelHospitalId(-1);
                    app.setCurSelEventId(-1);
                    app.setFilters(filters);
                    app.setViewSettings(viewSettings);
        		}
        	}
        	else {
        		app.setAuthId(s.getAuthId());
                app.setAuthStatus(true);
                if (!s.getUsername().isEmpty()){
                    app.setUsername(s.getUsername());
                }
                if (!s.getPassword().isEmpty()){
                    app.setPassword(s.getPassword());
                }
                if (!s.getToken().isEmpty()){
                    app.setToken(s.getToken());
                }
                if (!app.getUsername().isEmpty() && !app.getPassword().isEmpty() && !app.getToken().isEmpty()){
                    app.setAuthStatus(true);
                }
                else {
                    app.setAuthStatus(false);
                }
                if (s.getWebServerId() != -1) {
                    app.setWebServerId(s.getWebServerId());
                }
                if (s.getHospitalId() != -1){
                    app.setCurSelHospitalId(s.getHospitalId());
                }
                if (s.getEventId() != -1){
                    app.setCurSelEventId(s.getEventId());
                }
                app.setFilters(s.getFilters());
                app.setViewSettings(s.getViewSettings());

                // get hospital by hospital ID - start
                ArrayList<Hospital> hospitalList = s.getAllHospitals();
                if (hospitalList.isEmpty() == true){
                    createInitialHospitalTable(s);
                    hospitalList = s.getAllHospitals();
                }

                int curSelHospital = -1;
                Hospital hCurSel;
                long hospitalIdCurSel = app.getCurSelHospitalId();
                if (hospitalIdCurSel == -1){
                    hCurSel = hospitalList.get(0);
                    curSelHospital = 0;
                }
                else {
                    for (int i = 0; i < hospitalList.size(); i++){
                        Hospital h = new Hospital();
                        h = hospitalList.get(i);
                        if (hospitalIdCurSel == h.rowIndex){
                            hCurSel = h;
                            curSelHospital = i;
                            break;
                        }
                    }
                }

                if (curSelHospital == -1){
                    curSelHospital = 0;
                }
                hCurSel = hospitalList.get(curSelHospital);
                app.setCurSelHospitalId(hCurSel.rowIndex);
                app.setCurSelHospital(hCurSel.name);
                app.setCurSelHospitalShortName(hCurSel.shortname);
                // get hospital by hospital ID - end

                if (s.getEventId() != -1){
                    app.setCurSelEventId(s.getEventId());
                }

                // get event by event ID - start
                ArrayList<Event> eventList = s.getAllEvents();
                if (eventList.isEmpty() == true) {
                    createInitialEventTable(s);
                    eventList = s.getAllEvents();
                }


                Event eventCurSel = null;
                int curSelEventPos = -1;
                long eventIdCurSel = app.getCurSelEventId();
                if (eventIdCurSel == -1) {
                    eventCurSel = eventList.get(0);
                    curSelEventPos = 0;
                } else {
                    for (int i = 0; i < eventList.size(); i++) {
                        Event e = new Event();
                        e = eventList.get(i);
                        if (eventIdCurSel == e.incident_id) {
                            eventCurSel = e;
                            curSelEventPos = i;
                            break;
                        }
                    }
                }

                if (eventCurSel == null) {
                    eventCurSel = eventList.get(0);
                } else {
                    eventCurSel = eventList.get(curSelEventPos);
                }
                app.setCurSelEventId(eventCurSel.incident_id);
                app.setCurSelEvent(eventCurSel.name);
                app.setCurSelEventShortName(eventCurSel.shortname);
                // get event by event ID - end
        	}
        	s.close();
    	}
    	catch (Exception e){ // if wrong in first attempt to read database
    		Toast.makeText(this, "Error to read database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    		Log.e("Error", e.getMessage());
            System.exit(0);
    	}

        Toast.makeText(SplashActivity.this, "Welcome " + username.toString() + "!", Toast.LENGTH_SHORT).show();
        if (username.equalsIgnoreCase(TriagePic.GUEST)){
    		new SplashAsyncTask().execute();
    	}
    	else {
    		if (WebServer.AmIConnected(this) == false){
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setMessage("Press \"Yes\" to work on offline or \"No\" to exit?")
    				   .setTitle("No connectivity is detected")
    				   .setIcon(android.R.drawable.ic_dialog_alert)
    			       .setCancelable(true)
    			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			        	    Toast.makeText(SplashActivity.this, "Good Bye!", Toast.LENGTH_SHORT).show();
    			                dialog.cancel();
    			                System.exit(0);
    			                return;
    			           }
    			       })
    			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    						dialog.dismiss();
    		        	    Toast.makeText(SplashActivity.this, "You are working on offline.", Toast.LENGTH_SHORT).show();
    						new SplashAsyncTask().execute();
    			           }
    			       });
    			AlertDialog alert = builder.create();	
    			alert.show();
    		}
    		else {
    			new SplashAsyncTask().execute();
    		}
    	}
	}

    private void createInitialEventTable(DataSource s) {
        Event e = new Event();
        e.toDefault();
        s.createEvent(e);
    }

    private void createInitialHospitalTable(DataSource d) {
        Hospital h = new Hospital();
        h.toDefault();
        d.createHospital(h);
    }

    private void login() {

	}

	//To use the AsyncTask, it must be subclassed  
    private class SplashAsyncTask extends AsyncTask<Void, Integer, Void>  
    {
        static final int MAX = 10;//100;//50;

        //Before running code in separate thread
        @SuppressWarnings("deprecation")
		@Override  
        protected void onPreExecute()  
        {  
			progressBar = (ProgressBar) findViewById(R.id.progressBarSplash);

            // get the search count. added in version 9.0.0
            if (WebServer.AmIConnected(SplashActivity.this) == true){
                Toast.makeText(SplashActivity.this, "Counting the number of patients...", Toast.LENGTH_SHORT).show();
                new callSearchCountAsyncTask().execute();
            }
        }
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {
            //Get the current thread's token
			synchronized (this)
			{
				int eachSleep = 10;

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
            super.onPostExecute(result);

            Toast.makeText(SplashActivity.this, String.valueOf(app.getCurSearchCount()) + " patients are found in latest event." , Toast.LENGTH_SHORT).show();

            Intent i;
            i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
        	SplashActivity.this.finish();
        }
    }

    public boolean isLogin(){
        boolean result = false;
        String u = app.getUsername();
        String p = app.getPassword();
        boolean a = app.getAuthStatus();

        if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST)){
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LOGIN){
			new SplashAsyncTask().execute();
		}	
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
            }
            // added in version 9.0.3
            else if (searchResult.getErrorCode().equalsIgnoreCase(SearchResult.MY_ERROR_CODE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setMessage(searchResult.getErrorMessage())
                        .setCancelable(false)
                        .setTitle("No Connection")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                app.setCurSearchCount("0");
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                app.setCurSearchCount("0");
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
        app.setCurSearchCount(returnString);
        app.setCurSelWebServer(ws);
        return sr;
    }
}
