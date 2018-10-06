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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;

public class WebServerActivity extends Activity implements View.OnClickListener {
    private static final int ADD_NEW = 1;
    
    TriagePic app;

    ListView lv;
	TextView tvWevServer;
	TextView tvId;
	TextView tvShortName;
	TextView tvWebService;
	TextView tvNameSpace;
	TextView tvUrl;
	
	//	private Spinner hospitals;
	private DataSource s;
	private ArrayAdapter<String> adapter;
	private SharedPreferences settings;
	private ArrayList<String> webServerNameList;

//	private Activity thisActivity;
	private String hospitalCurSel;
	
	private ArrayList<WebServer> webServerList;
	WebServer wbCurSel;
	int curSelWebServer = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_server);
		
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		Initialize();
	}

	private void Initialize() {
		
		DataSource d = new DataSource(this, app.getSeed());
		d.open();
		webServerList = d.getAllWebServers();
		if (webServerList.isEmpty() == true){
			createInitialWebServerTable(d);
			webServerList = d.getAllWebServers();
		}
		d.close();
		
		webServerNameList = new ArrayList<String>();
		if (webServerList.isEmpty() == false){
			for (int i = 0; i < webServerList.size(); i++){
				String s = webServerList.get(i).getName();
				webServerNameList.add(s);
			}
		}
		
		long webServerIdCurSel = app.getWebServerId();
		if (webServerIdCurSel == -1){
			wbCurSel = webServerList.get(0);
			curSelWebServer = 0;
		}
		else {
			for (int i = 0; i < webServerList.size(); i++){
				WebServer w = new WebServer();
				w = webServerList.get(i);
				if (webServerIdCurSel == w.getId()){
					wbCurSel = w;
					curSelWebServer = i;
					break;
				}
			}
		}
		
		wbCurSel = webServerList.get(curSelWebServer);
		
		tvWevServer = (TextView) findViewById(R.id.textViewWebServer);
		tvId = (TextView) findViewById(R.id.textViewId);
		tvShortName = (TextView) findViewById(R.id.textViewShortName);
		tvWebService = (TextView) findViewById(R.id.textViewWebService);
		tvNameSpace = (TextView) findViewById(R.id.textViewNameSpace);
		tvUrl = (TextView) findViewById(R.id.textViewUrl);
		
		tvWevServer.setText(wbCurSel.getName());
		tvId.setText(String.valueOf(wbCurSel.getId()));
		tvShortName.setText(wbCurSel.getShortName());
		tvWebService.setText(wbCurSel.getWebService());
		tvNameSpace.setText(wbCurSel.getNameSpace());
		tvUrl.setText(wbCurSel.getUrl());
		
		lv = (ListView) findViewById(R.id.listViewWebServer);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
        		String name = o.toString();
        		tvWevServer.setText(name);
        		
        		wbCurSel = new WebServer();
        		DataSource d = new DataSource(WebServerActivity.this, app.getSeed());
        		d.open();
        		wbCurSel = d.getWebServerFromName(name);
        		d.close();

                /*
        		if (name.compareToIgnoreCase(WebServer.PL_STAGE_NAME) == 0) {
        			wbCurSel.setName(WebServer.PL_STAGE_NAME);
        			wbCurSel.setShortName(WebServer.PL_STAGE_SHORT_NAME);
        			wbCurSel.setWebService(WebServer.PL_STAGE_WEB_SERVICE);
        			wbCurSel.setNameSpace(WebServer.PL_STAGE_NAMESPACE);
        			wbCurSel.setUrl(WebServer.PL_STAGE_URL);
        		}
        		else if (name.compareToIgnoreCase(WebServer.PL_MOBILE_NAME) == 0) {
        			wbCurSel.setName(WebServer.PL_MOBILE_NAME);
        			wbCurSel.setShortName(WebServer.PL_MOBILE_SHORT_NAME);
        			wbCurSel.setWebService(WebServer.PL_MOBILE_WEB_SERVICE);
        			wbCurSel.setNameSpace(WebServer.PL_MOBILE_NAMESPACE);
        			wbCurSel.setUrl(WebServer.PL_MOBILE_URL);
        		}
        		else if (name.compareToIgnoreCase(WebServer.PL_NAME) == 0) {
        			wbCurSel.setName(WebServer.PL_NAME);
        			wbCurSel.setShortName(WebServer.PL_SHORT_NAME);
        			wbCurSel.setWebService(WebServer.PL_WEB_SERVICE);
        			wbCurSel.setNameSpace(WebServer.PL_NAMESPACE);
        			wbCurSel.setUrl(WebServer.PL_URL);
        		}
                else if (name.compareToIgnoreCase("PL") == 0) {
                    wbCurSel.setName(WebServer.PL_NAME);
                    wbCurSel.setShortName(WebServer.PL_SHORT_NAME);
                    wbCurSel.setWebService(WebServer.PL_WEB_SERVICE);
                    wbCurSel.setNameSpace(WebServer.PL_NAMESPACE);
                    wbCurSel.setUrl(WebServer.PL_URL);
                }
                else if (name.compareToIgnoreCase("TT") == 0) {
                    wbCurSel.setName(WebServer.TT_NAME);
                    wbCurSel.setShortName(WebServer.TT_SHORT_NAME);
                    wbCurSel.setWebService(WebServer.TT_WEB_SERVICE);
                    wbCurSel.setNameSpace(WebServer.TT_NAMESPACE);
                    wbCurSel.setUrl(WebServer.TT_URL);
                }
                else if (name.compareToIgnoreCase("TS") == 0) {
                    wbCurSel.setName(WebServer.TS_NAME);
                    wbCurSel.setShortName(WebServer.TS_SHORT_NAME);
                    wbCurSel.setWebService(WebServer.TS_WEB_SERVICE);
                    wbCurSel.setNameSpace(WebServer.TS_NAMESPACE);
                    wbCurSel.setUrl(WebServer.TS_URL);
                }
                */

                if (name.compareToIgnoreCase("TT") == 0) {
                    wbCurSel.setName(WebServer.TT_NAME);
                    wbCurSel.setShortName(WebServer.TT_SHORT_NAME);
                    wbCurSel.setWebService(WebServer.TT_WEB_SERVICE);
                    wbCurSel.setNameSpace(WebServer.TT_NAMESPACE);
                    wbCurSel.setUrl(WebServer.TT_URL);
                }

        		if (wbCurSel == null){
        			Toast.makeText(WebServerActivity.this, "WebServer " + name + " is not found", Toast.LENGTH_SHORT).show();
        			return;
        		}
        		
        		tvWevServer.setText(wbCurSel.getName());
        		tvId.setText(String.valueOf(wbCurSel.getId()));
        		tvShortName.setText(wbCurSel.getShortName());
        		tvWebService.setText(wbCurSel.getWebService());
        		tvNameSpace.setText(wbCurSel.getNameSpace());
        		tvUrl.setText(wbCurSel.getUrl());        		
        	}
        });
        
		adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_activated_1,
//				android.R.layout.simple_list_item_1, 
				webServerNameList
				);
		lv.setAdapter(adapter);	
//        lv.setTextFilterEnabled(true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setItemChecked(curSelWebServer, true);

		int positionCurSel = -1;
		for (int i = 0; i < webServerNameList.size(); i++){
			if (wbCurSel.getName().equalsIgnoreCase(webServerNameList.get(i)) == true){
				positionCurSel = i;
			}
		}
		if (positionCurSel == -1){
			positionCurSel = 0;
		}
		
		tvWevServer.setText(webServerNameList.get(positionCurSel));
		
	/*	
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
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
        		String name = o.toString();
        		s.open();
        		Event e = s.getEvent(name);
        		s.close();
        		
        		EventActivity.this.getSharedPreferences("Info", 0).edit().putString("event", e.name).commit();
        		eventCurSel = name;
        		
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
        });
        
        // get the event list
		s = new DataSource(this);
		s.open();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, s.getEventsList());
		s.close();
		
		lv.setAdapter(adapter);		

		// display the initial data
		eventCurSel = EventActivity.this.getSharedPreferences("Info", 0).getString("event", "Test");
		if (eventCurSel.isEmpty() == false){
    		s.open();
    		Event e = s.getEvent(eventCurSel);
    		s.close();
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
    		tvEvent.setText("");
    		tvShortName.setText("");
    		tvIncidentId.setText("");
    		tvDate.setText("");
    		tvType.setText("");
    		tvVisibility.setText("");
    		tvAddress.setText("");
    		tvLatitude.setText("");
    		tvLongitude.setText("");
		}
		*/
	}

	private void createInitialWebServerTable(DataSource d) {
        /*
		WebServer w0 = new WebServer();
		w0.setName(WebServer.PL_NAME);
		w0.setShortName(WebServer.PL_SHORT_NAME);
		w0.setWebService(WebServer.PL_WEB_SERVICE);
		w0.setNameSpace(WebServer.PL_NAMESPACE);
		w0.setUrl(WebServer.PL_URL);
		w0.setId(d.createWebServer(w0));

		WebServer w1 = new WebServer();
		w1.setName(WebServer.PL_MOBILE_NAME);
		w1.setShortName(WebServer.PL_MOBILE_SHORT_NAME);
		w1.setWebService(WebServer.PL_MOBILE_WEB_SERVICE);
		w1.setNameSpace(WebServer.PL_MOBILE_NAMESPACE);
		w1.setUrl(WebServer.PL_MOBILE_URL);
		w1.setId(d.createWebServer(w1));
		
		WebServer w2 = new WebServer();
		w2.setName(WebServer.PL_STAGE_NAME);
		w2.setShortName(WebServer.PL_STAGE_SHORT_NAME);
		w2.setWebService(WebServer.PL_STAGE_WEB_SERVICE);
		w2.setNameSpace(WebServer.PL_STAGE_NAMESPACE);
		w2.setUrl(WebServer.PL_STAGE_URL);		
		w2.setId(d.createWebServer(w2));

        WebServer w3 = new WebServer();
        w3.setName(WebServer.TT_NAME);
        w3.setShortName(WebServer.TT_SHORT_NAME);
        w3.setWebService(WebServer.TT_WEB_SERVICE);
        w3.setNameSpace(WebServer.TT_NAMESPACE);
        w3.setUrl(WebServer.TT_URL);
        w3.setId(d.createWebServer(w3));

        WebServer w4 = new WebServer();
        w4.setName(WebServer.TS_NAME);
        w4.setShortName(WebServer.TS_SHORT_NAME);
        w4.setWebService(WebServer.TS_WEB_SERVICE);
        w4.setNameSpace(WebServer.TS_NAMESPACE);
        w4.setUrl(WebServer.TS_URL);
        w4.setId(d.createWebServer(w4));
        */

        WebServer w0 = new WebServer();
        w0.setName(WebServer.TT_NAME);
        w0.setShortName(WebServer.TT_SHORT_NAME);
        w0.setWebService(WebServer.TT_WEB_SERVICE);
        w0.setNameSpace(WebServer.TT_NAMESPACE);
        w0.setUrl(WebServer.TT_URL);
        w0.setId(d.createWebServer(w0));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.textViewWebSite:
			break;
		}	
	}

	@Override
	public void onBackPressed() {
		// check if it is changed.
		long oldWebServerId = app.getWebServerId();
		if (oldWebServerId == wbCurSel.getId()){
			Toast.makeText(this, "No change is made.", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, wbCurSel.getName() + " is selected.", Toast.LENGTH_SHORT).show();
			app.setWebServerId(wbCurSel.getId());	
			app.setCurSelHospital("");
			app.setCurSelEvent("");
            app.setCurSelEventShortName("");
		}
		this.finish();
		super.onBackPressed();
	}

    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_server_menu, menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.itemLatency:
        	testLatency();
        	break;
        case R.id.itemAddNew:
        	addNewWebServer();
        	break;
        case R.id.itemDelete:
        	deleteSelWebServer();
        	break;
        }
        return true;
	}

	private void deleteSelWebServer() {
		if (wbCurSel == null){
			return;
		}
		
		DataSource d = new DataSource(WebServerActivity.this, app.getSeed());
		d.open();
		d.deleteWebServerById(wbCurSel.getId());
		d.close();

		Initialize();
		return;
	}

	private void addNewWebServer() {
		Intent i = new Intent(this, AddWebServerActivity.class);
		startActivityForResult(i, ADD_NEW);
	}

	private void testLatency() {
		Intent i = new Intent(WebServerActivity.this, LatencyActivity.class);
		i.putExtra("webServer", wbCurSel.getWebService());
		startActivity(i);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ADD_NEW){
			Initialize();
		}
	}		
}
