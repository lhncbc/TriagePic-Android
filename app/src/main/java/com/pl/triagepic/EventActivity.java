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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends Activity implements View.OnClickListener {
	ListView lv;
	TextView tvEvent;
	TextView tvShortName;
	/*
	TextView tvStreet1;
	TextView tvStreet2;
	TextView tvCity;
	TextView tvCounty;
	TextView tvState;
	TextView tvZip;
	TextView tvCountry;
	*/
	TextView tvIncidentId;
	TextView tvDate;
	TextView tvType;
	TextView tvVisibility;
	TextView tvAddress;
	TextView tvLatitude;
	TextView tvLongitude;

	//	private Spinner hospitals;
	private DataSource s;
	private ArrayAdapter<String> adapter;
    private String eventCurSel;
    private String eventCurSelShortName;
	private int positionCurSel;
	
    private ProgressDialog progressDialog;  
    
	TriagePic app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);

		app = ((TriagePic) getApplication());
		app.detectMobileDevice(this);
		app.setScreenOrientation(this);

		if (WebServer.AmIConnected(this) == true) {
			new getEventListAsyncTask().execute();
		} else {
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

	private void Initialize() {
		// Get reference
        eventCurSel = EventActivity.this.getSharedPreferences("Info", 0).getString("event", Event.DEFAULT_EVENT_NAME);
        eventCurSelShortName = EventActivity.this.getSharedPreferences("Info", 0).getString("eventShortName", Event.DEFAULT_EVENT_SHORT_NAME);
		positionCurSel = EventActivity.this.getSharedPreferences("Info", 0).getInt("position", 0);

		tvEvent = (TextView) findViewById(R.id.textViewEvent);
		tvShortName = (TextView) findViewById(R.id.textViewShortName);
		tvIncidentId = (TextView) findViewById(R.id.textViewIncidentId);
		tvDate = (TextView) findViewById(R.id.textViewDate);
		tvType = (TextView) findViewById(R.id.textViewType);
		tvVisibility = (TextView) findViewById(R.id.textViewVisibility);
		tvAddress = (TextView) findViewById(R.id.textViewAddress);
		tvLatitude = (TextView) findViewById(R.id.textViewLatitude);
		tvLongitude = (TextView) findViewById(R.id.textViewLongitude);
		
		lv = (ListView) findViewById(R.id.listViewHospital);
        // get the event list
		s = new DataSource(this, app.getSeed());
		s.open();
		adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_activated_1,
//				android.R.layout.simple_list_item_1, 
				s.getEventsList()
				);
		s.close();
		
		lv.setAdapter(adapter);		
//        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// display the initial data
        if (eventCurSel.isEmpty()){
        	eventCurSel = "Test Exercise";
            eventCurSelShortName = "test";
        	positionCurSel = 0;

        	// Write to shared preference
        	EventActivity.this.getSharedPreferences("Info", 0).edit().putString("event", eventCurSel).commit();
            EventActivity.this.getSharedPreferences("Info", 0).edit().putString("eventShortName", eventCurSelShortName).commit();
        	EventActivity.this.getSharedPreferences("Info", 0).edit().putInt("position", positionCurSel).commit();
        }

        lv.setItemChecked(positionCurSel, true);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		lv.setItemChecked(position, true);
        		
        		Object o = lv.getItemAtPosition(position);
        		String name = o.toString();
        		s.open();
        		Event e = s.getEvent(name);
        		s.close();
        		
        		// Cur value
        		eventCurSel = name;
                eventCurSelShortName = e.shortname.toString();
        		positionCurSel = position;
        		
        		// Write to shared preference
                EventActivity.this.getSharedPreferences("Info", 0).edit().putString("event", eventCurSel).commit();
                EventActivity.this.getSharedPreferences("Info", 0).edit().putString("eventShortName", eventCurSelShortName).commit();
        		EventActivity.this.getSharedPreferences("Info", 0).edit().putInt("position", positionCurSel).commit();

        		tvEvent.setText(e.name);
        		tvShortName.setText(e.shortname);
        		tvIncidentId.setText(String.valueOf(e.incident_id));
        		tvDate.setText(e.date);
        		tvType.setText(e.type);
//        		tvVisibility.setText(e.group.equals("null") ? "Public" : "Private");
        		tvVisibility.setText(e.group.equals("0") ? Event.REPORTING_OPEN : Event.REPORTING_CLOSED);
        		tvAddress.setText(e.street);
        		tvLatitude.setText(String.valueOf(e.latitude));
        		tvLongitude.setText(String.valueOf(e.longitude));
        		
        		Toast.makeText(EventActivity.this, "You are selecting \"" + e.name + "\"!", Toast.LENGTH_SHORT).show();
        	}
        });
        
        if (adapter.getCount() == 0){ // not update the event list yet
        	return;
        }
        
        lv.setItemChecked(positionCurSel, true);
        Object o = lv.getItemAtPosition(positionCurSel);
		String eventCurSel = o.toString();
		s.open();
		Event e = s.getEvent(eventCurSel);
		s.close();

		if (e != null){
    		tvEvent.setText(e.name);
    		tvShortName.setText(e.shortname);
    		tvIncidentId.setText(String.valueOf(e.incident_id));
    		tvDate.setText(e.date);
    		tvType.setText(e.type);
    		tvVisibility.setText(e.group.equals("null") ? "Public" : "Private");
    		tvAddress.setText(e.street);
    		tvLatitude.setText(String.valueOf(e.latitude));
    		tvLongitude.setText(String.valueOf(e.longitude));
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
			String msg = "Failed to read event: \"" + eventCurSel + "\" in database.";
			builder.setMessage(msg)
				   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setCancelable(true)
			       .setTitle("Warning")
			       .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                return;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
		}
	}

	@Override
	public void onClick(View v) {

	}
	
	@Override
	public void onBackPressed() {
        super.onBackPressed();

		app.setCurSelEvent(eventCurSel);
        app.setCurSelEventShortName(eventCurSelShortName);
		
		Intent backData = new Intent();
        backData.putExtra("eventname", eventCurSel);
        backData.putExtra("eventnameShortName", eventCurSelShortName);
		setResult(EventActivity.RESULT_OK, backData);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}	
	
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.event_menu, menu);
        return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
        	if (WebServer.AmIConnected(this) == true) {
				new getEventListAsyncTask().execute();
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
		   	break;
        case R.id.itemLatency:
        	testLatency();
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

	private void getEventList() {
		long webServerId = app.getWebServerId();
		String userName = app.getUsername();
		String passWord = app.getPassword();
		
		DataSource s = new DataSource(this, app.getSeed());
		WebServer wb = new WebServer();
		s.open();
		wb = s.getWebServerFromId(webServerId);
		s.close();

        if (wb == null){
            wb = new WebServer();
        }
        wb.setToken(app.getToken());
		String returnMsg = wb.callUpdateEventInformation(this, app.getSeed());
	}	
	
	//To use the AsyncTask, it must be subclassed  
    private class getEventListAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
            progressDialog = new ProgressDialog(EventActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Retrieving events, please wait...");
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
                getEventList();
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
            
            //initialize the View  
            Initialize();
      }
    }
}
