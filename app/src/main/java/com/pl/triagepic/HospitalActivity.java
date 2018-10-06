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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

public class HospitalActivity extends Activity implements View.OnClickListener {
	ListView lv;
	TextView tvHospital;
	TextView tvShortName;
	TextView tvStreet1;
	TextView tvStreet2;
	TextView tvCity;
	TextView tvCounty;
	TextView tvState;
	TextView tvZip;
	TextView tvCountry;
	TextView tvPhone;
	TextView tvFax;
	TextView tvEmail;
	TextView tvWebSite;
	TextView tvProviderId;
	TextView tvLatitude;
	TextView tvLongitude;
	
	TextView textViewPidPrefix;
	TextView textViewPidSuffixLengthSpecified;
	TextView textViewPidSuffixLength;

	//	private Spinner hospitals;
	private DataSource s;
	private ArrayAdapter<String> adapter;
//	private View thisFragment;
//	private Activity thisActivity;
	private String hospitalCurSel;
	private int positionCurSel;
	
    private ProgressDialog progressDialog;  
    
    TriagePic app;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital);

		app = (TriagePic)this.getApplication();
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);
		
//		Initialize();
		if (WebServer.AmIConnected(this) == true) {
			new getHospitalListAsyncTask().execute();
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

	private void Initialize() {
		// Get reference
		hospitalCurSel = HospitalActivity.this.getSharedPreferences("Info", 0).getString("hospital", Hospital.DEFAULT_NAME);
		positionCurSel = HospitalActivity.this.getSharedPreferences("Info", 0).getInt("position", 0);
		
		// UI
		tvHospital = (TextView) findViewById(R.id.textViewHospital);
		tvShortName = (TextView) findViewById(R.id.textViewShortName);
		tvStreet1 = (TextView) findViewById(R.id.textViewStreet1);
		tvStreet2 = (TextView) findViewById(R.id.textViewStreet2);
		tvCity = (TextView) findViewById(R.id.textViewCity);
		tvCounty = (TextView) findViewById(R.id.textViewCounty);
		tvState = (TextView) findViewById(R.id.textViewState);
		tvZip = (TextView) findViewById(R.id.textViewZip);
		tvCountry = (TextView) findViewById(R.id.textViewCountry);
		tvPhone = (TextView) findViewById(R.id.textViewPhone);
		tvFax = (TextView) findViewById(R.id.textViewFax);
		tvEmail = (TextView) findViewById(R.id.textViewEmail);
		tvWebSite = (TextView) findViewById(R.id.textViewWebSite);
		tvWebSite.setOnClickListener(this);
		tvProviderId = (TextView) findViewById(R.id.textViewProviderId);
		tvLatitude = (TextView) findViewById(R.id.textViewLatitude);
		tvLongitude = (TextView) findViewById(R.id.textViewLongitude);
		
		textViewPidPrefix = (TextView) findViewById(R.id.textViewPidPrefix);
		textViewPidSuffixLengthSpecified = (TextView) findViewById(R.id.textViewPidSuffixLengthSpecified);
		textViewPidSuffixLength = (TextView) findViewById(R.id.textViewPidSuffixLength);
		
		lv = (ListView) findViewById(R.id.listViewHospital);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
        		String name = o.toString();
        		s.open();
        		Hospital h = s.getHospital(name);
        		s.close();
        		
        		// Cur value
        		hospitalCurSel = name;
        		positionCurSel = position;
        		
        		// Write to shared preference
        		HospitalActivity.this.getSharedPreferences("Info", 0).edit().putString("hospital", hospitalCurSel).commit();
        		HospitalActivity.this.getSharedPreferences("Info", 0).edit().putInt("position", positionCurSel).commit();
        		
        		tvHospital.setText(h.name);
        		tvShortName.setText(h.shortname);
        		tvStreet1.setText(h.street1);
        		tvStreet2.setText(h.street2);
        		tvCity.setText(h.city);
        		tvCounty.setText(h.county);
        		tvState.setText(h.state);
        		tvZip.setText(h.zip);
        		tvCountry.setText(h.country);
        		tvPhone.setText(h.phone);
        		tvFax.setText(h.fax);
        		tvEmail.setText(h.email);

//        		tvWebSite.setText(h.www);
        		SpannableString spString=new SpannableString(h.www);
                spString.setSpan(new UnderlineSpan(), 0, spString.length(), 0);
        		tvWebSite.setText(spString);
        		
        		tvProviderId.setText(h.npi);
        		tvLatitude.setText(h.latitude);
        		tvLongitude.setText(h.longitude);
        		
        		textViewPidPrefix.setText(h.pidPrefix);
        		if (h.pidSuffixVariable == true){
            		textViewPidSuffixLengthSpecified.setText("true");        			
        		}
        		else {
            		textViewPidSuffixLengthSpecified.setText("false");        			
        		}
        		textViewPidSuffixLength.setText(String.valueOf(h.pidSuffixFixedLength).toString());

        		Toast.makeText(HospitalActivity.this, "You are selecting \"" + name + "\"!", Toast.LENGTH_SHORT).show();
        	}
        });
        
		s = new DataSource(this, app.getSeed());
		s.open();
		adapter = new ArrayAdapter<String>(
				this,
//				android.R.layout.simple_list_item_1, 
				android.R.layout.simple_list_item_activated_1,
				s.getHospitalList()
				);
		s.close();
		
		lv.setAdapter(adapter);		
//        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// display the initial data
        if (hospitalCurSel.isEmpty()){
        	hospitalCurSel = Hospital.DEFAULT_NAME;
        	positionCurSel = 0;

        	// Write to shared preference
    		HospitalActivity.this.getSharedPreferences("Info", 0).edit().putString("hospital", hospitalCurSel).commit();
    		HospitalActivity.this.getSharedPreferences("Info", 0).edit().putInt("position", positionCurSel).commit();
        }

        if (adapter.getCount() == 0){ // not update the event list yet
        	return;
        }
		
        lv.setItemChecked(positionCurSel, true);
        Object o = lv.getItemAtPosition(positionCurSel);
		String name = o.toString();
		s.open();
		Hospital h = s.getHospital(name);
		s.close();
		
		if (h != null){
    		tvHospital.setText(h.name);
    		tvShortName.setText(h.shortname);
    		tvStreet1.setText(h.street1);
    		tvStreet2.setText(h.street2);
    		tvCity.setText(h.city);
    		tvCounty.setText(h.county);
    		tvState.setText(h.state);
    		tvZip.setText(h.zip);
    		tvCountry.setText(h.country);
    		tvPhone.setText(h.phone);
    		tvFax.setText(h.fax);
    		tvEmail.setText(h.email);
    		
    		SpannableString spString=new SpannableString(h.www);
            spString.setSpan(new UnderlineSpan(), 0, spString.length(), 0);
    		tvWebSite.setText(spString);
    		
    		tvProviderId.setText(h.npi);
    		tvLatitude.setText(h.latitude);
    		tvLongitude.setText(h.longitude);

    		textViewPidPrefix.setText(h.pidPrefix);
    		if (h.pidSuffixVariable == true){
        		textViewPidSuffixLengthSpecified.setText("true");        			
    		}
    		else {
        		textViewPidSuffixLengthSpecified.setText("false");        			
    		}
    		textViewPidSuffixLength.setText(String.valueOf(h.pidSuffixFixedLength).toString());
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(HospitalActivity.this);
			String msg = "Failed to read hospital: \"" + positionCurSel + "\" in database.";
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
		switch (v.getId()){
		case R.id.textViewWebSite:
			StartAboutHospital();
			break;
		}	
	}

	private void StartAboutHospital() {
		String url = tvWebSite.getText().toString();
		if (url.isEmpty() == true){
			return;
		}
		Intent i = new Intent(HospitalActivity.this, AboutHospitalActivity.class);
		i.putExtra("URL", url);    			
		final int result=1;
		startActivityForResult(i, result);
	}

	@Override
	public void onBackPressed() {
        super.onBackPressed();

		app.setCurSelHospital(hospitalCurSel);
		
		Intent backData = new Intent();
		backData.putExtra("hospitalname", hospitalCurSel);
		setResult(HospitalActivity.RESULT_OK, backData);
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
				new getHospitalListAsyncTask().execute();
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
	private void getHospitalList() {
		TriagePic app = ((TriagePic)this.getApplication());
		long webServerId = app.getWebServerId();
//		String userName = app.getUsername();
//		String passWord = app.getPassword();
		
		DataSource s = new DataSource(this, app.getSeed());
		WebServer wb = new WebServer();
		s.open();
		wb = s.getWebServerFromId(webServerId);
		s.close();

        if (wb == null){
            wb = new WebServer();
        }
        wb.setToken(app.getToken());
		String returnMsg = wb.callUpdateHospitalInformation(this, app.getSeed());
	}	
	
	//To use the AsyncTask, it must be subclassed  
    private class getHospitalListAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
            progressDialog = new ProgressDialog(HospitalActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Retrieving hospitals, please wait...");
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
				getHospitalList();
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
