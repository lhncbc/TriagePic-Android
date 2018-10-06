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

/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * •	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 * •	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * •	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 *
 */

package com.pl.triagepic;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pl.triagepic.Result.SearchResult;

public class FiltersActivity extends Activity implements View.OnClickListener {
	static final int DEFINE_FILTERS = 2;

    TriagePic app;
    String webServer = "";
    String nameSpace = "";
    String url = "";
    String soapAction = "";

    Event currentEvent;
	TextView tvEventName;
    Filters filters;
    ViewSettings viewSettings;

	ProgressBar pbTotalNumber;
	ProgressBar pbNewTotalNumber;

	TextView tvNewTotalNumber;
//	Button btNext;
	Button btNext2;
	
	CheckBox cbMale;
	CheckBox cbFemale;
	CheckBox cbComplex;
	CheckBox cbGenderUnknown;
	
	CheckBox cbAgeAdult;
	CheckBox cbAgeChild;
	CheckBox cbAgeUnknown;

	CheckBox cbGreen;
	CheckBox cbBhGreen;
	CheckBox cbYellow;
	CheckBox cbRed;
	CheckBox cbGray;
    CheckBox cbBlack;
	CheckBox cbUnassigned;

    // photo section - start
    RadioGroup radioGroupPhoto;
    RadioButton radioButtonSelPhotoOnly;
    RadioButton radioButtonSelBoth;

    RadioGroup radioGroupPageSettings;
    RadioButton radioButtonSel5;
    RadioButton radioButtonSel10;
    RadioButton radioButtonSel15;
    RadioButton radioButtonSel20;
    // photo section - end

    int patientCount = 0;
    String patientCountStr = "0";
	int threadCounter = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters);

        app = ((TriagePic)getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);


		Initialize();

        if (WebServer.AmIConnected(this) == true) {
            new callSearchCountAsyncTask().execute();
        }
    }
	
	private void Initialize() {
		pbNewTotalNumber = (ProgressBar) findViewById(R.id.progressBarNewTotalNumber);
		tvNewTotalNumber = (TextView) findViewById(R.id.tvNewTotalNumber);

		cbMale = (CheckBox) findViewById(R.id.checkBoxMale);
		cbFemale = (CheckBox) findViewById(R.id.checkBoxFemale);
		cbComplex = (CheckBox) findViewById(R.id.checkBoxComplex);
		cbGenderUnknown = (CheckBox) findViewById(R.id.checkBoxGenderUnknown);
				
		cbAgeAdult = (CheckBox)findViewById(R.id.checkBoxAgeAdult);
		cbAgeChild = (CheckBox)findViewById(R.id.checkBoxAgeChild);
		cbAgeUnknown = (CheckBox)findViewById(R.id.checkBoxAgeUnknown);

		cbGreen = (CheckBox)findViewById(R.id.checkBoxGreen);
		cbBhGreen = (CheckBox)findViewById(R.id.checkBoxBhGreen);
		cbYellow = (CheckBox)findViewById(R.id.checkBoxYellow);
		cbRed = (CheckBox)findViewById(R.id.checkBoxRed);
        cbGray = (CheckBox)findViewById(R.id.checkBoxGray);
        cbBlack = (CheckBox)findViewById(R.id.checkBoxBlack);
		cbUnassigned = (CheckBox)findViewById(R.id.checkBoxUnassigned);

		cbMale.setOnClickListener(this);
		cbFemale.setOnClickListener(this);
		cbComplex.setOnClickListener(this);
		cbGenderUnknown.setOnClickListener(this);

		cbAgeAdult.setOnClickListener(this);
	    cbAgeChild.setOnClickListener(this);
	    cbAgeUnknown.setOnClickListener(this);

        cbGreen.setOnClickListener(this);
        cbBhGreen.setOnClickListener(this);
        cbYellow.setOnClickListener(this);
        cbRed.setOnClickListener(this);
        cbGray.setOnClickListener(this);
        cbBlack.setOnClickListener(this);
        cbUnassigned.setOnClickListener(this);

        // photo section - start
        radioGroupPhoto = (RadioGroup)findViewById(R.id.radioGroupPhoto);
        radioButtonSelPhotoOnly = (RadioButton)findViewById(R.id.radioButtonSelPhotoOnly);
        radioButtonSelBoth = (RadioButton)findViewById(R.id.radioButtonSelBoth);

        radioGroupPageSettings = (RadioGroup)findViewById(R.id.radioGroupPageSettings);
        radioButtonSel5 = (RadioButton)findViewById(R.id.radioButtonSel5);
        radioButtonSel10 = (RadioButton)findViewById(R.id.radioButtonSel10);
        radioButtonSel15 = (RadioButton)findViewById(R.id.radioButtonSel15);
        radioButtonSel20 = (RadioButton)findViewById(R.id.radioButtonSel20);

        if (isLargeScreen() == true){
            radioButtonSel10.setText("10 records");
            radioButtonSel15.setText("15 records - recommended");
        }
        else {
            radioButtonSel10.setText("10 records - recommended");
            radioButtonSel15.setText("15 records");
        }

        radioButtonSelPhotoOnly.setOnClickListener(this);
        radioButtonSelBoth.setOnClickListener(this);

        radioButtonSel5.setOnClickListener(this);
        radioButtonSel10.setOnClickListener(this);
        radioButtonSel15.setOnClickListener(this);
        radioButtonSel20.setOnClickListener(this);

        radioButtonSel5.setEnabled(false);
        radioButtonSel10.setEnabled(false);
        radioButtonSel15.setEnabled(false);
        radioButtonSel20.setEnabled(false);

        // get filter from database.
        DataSource d = new DataSource(this, app.getSeed());
        d.open();
        filters = d.getFilters();
        if (filters.getId() == 0){
            filters = new Filters();
            filters.setDefaults();
            filters.setId((int) d.createFilters(filters));
        }
        app.setFilters(filters);

        viewSettings = d.getViewSettings();
        if (viewSettings.getId() == 0){
            viewSettings = new ViewSettings();
            viewSettings.SetToDefault();
            viewSettings.setId((int) d.createViewSettings(viewSettings));
        }
        app.setViewSettings(viewSettings);
        d.close();
        // end of getting filter from database

        // display initial data
		cbMale.setChecked(filters.getMale());
    	cbFemale.setChecked(filters.getFemale());
    	cbComplex.setChecked(filters.getComplex());
    	cbGenderUnknown.setChecked(filters.getGenderUnknown());

        cbAgeAdult.setChecked(filters.getAdult());
    	cbAgeChild.setChecked(filters.getChild());
    	cbAgeUnknown.setChecked(filters.getAgeUnknown());

        cbGreen.setChecked(filters.getGreenZone());
        cbBhGreen.setChecked(filters.getBhGreenZone());
        cbYellow.setChecked(filters.getYellowZone());
        cbRed.setChecked(filters.getRedZone());
        cbGray.setChecked(filters.getGrayZone());
        cbBlack.setChecked(filters.getBlackZone());
        cbUnassigned.setChecked(filters.getUnassignedZone());

        // Display
        switch (viewSettings.getPhotoSel()){
            case ViewSettings.PHOTO_ONLY:
                radioButtonSelPhotoOnly.setChecked(true);
                break;
            case ViewSettings.BOTH:
                radioButtonSelBoth.setChecked(true);
                break;
            default:
                radioButtonSelBoth.setChecked(true);
                break;
        }

        switch (viewSettings.getPageSize()){
            case ViewSettings.PAGE_SIZE_5:
                radioButtonSel5.setChecked(true);
                break;
            case ViewSettings.PAGE_SIZE_10:
                radioButtonSel10.setChecked(true);
                break;
            case ViewSettings.PAGE_SIZE_15:
                radioButtonSel15.setChecked(true);
                break;
            case ViewSettings.PAGE_SIZE_20:
                radioButtonSel20.setChecked(true);
                break;
            default:
                if (isLargeScreen() == true){
                    radioButtonSel15.setChecked(true);
                }
                else {
                    radioButtonSel10.setChecked(true);
                }
                break;
        }
	}

    public boolean isLargeScreen() {
        boolean b = false;
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            b = true;
        }
        else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            b = false;
        }
        else if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            b = false;
        }
        else if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            b = true;
        }
        else {
            b = false;
        }
        return b;
    }

    public void onClick(View v) {
		switch (v.getId()){
		case R.id.checkBoxMale:
			filters.setMale(cbMale.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
    		break;
		case R.id.checkBoxFemale:
			filters.setFemale(cbFemale.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxComplex:
			filters.setComplex(cbComplex.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxGenderUnknown:
			filters.setGenderUnknown(cbGenderUnknown.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxAgeAdult:
			filters.setAdult(cbAgeAdult.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxAgeChild:
			filters.setChild(cbAgeChild.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxAgeUnknown:
			filters.setAgeUnknown(cbAgeUnknown.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxGreen:
			filters.setGreenZone(cbGreen.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxBhGreen:
			filters.setBhGreenZone(cbBhGreen.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxYellow:
			filters.setYellowZone(cbYellow.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxRed:
			filters.setRedZone(cbRed.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
		case R.id.checkBoxGray:
			filters.setGrayZone(cbGray.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
			break;
        case R.id.checkBoxBlack:
            filters.setBlackZone(cbBlack.isChecked());
            break;
        case R.id.checkBoxUnassigned:
            filters.setUnassignedZone(cbUnassigned.isChecked());
            app.setFilters(filters);
            new callSearchCountAsyncTask().execute();
            break;
        // photo section - start
        case R.id.radioButtonSelPhotoOnly:
            if (radioButtonSelPhotoOnly.isChecked()) {
                viewSettings.setPhotoSel(ViewSettings.PHOTO_ONLY);
            }
            else {
                viewSettings.setPhotoSel(ViewSettings.BOTH);
            }
            app.setViewSettings(viewSettings);
            new callSearchCountAsyncTask().execute();
            break;
        case R.id.radioButtonSelBoth:
            if (radioButtonSelBoth.isChecked()){
                viewSettings.setPhotoSel(ViewSettings.BOTH);
            }
            else {
                viewSettings.setPhotoSel(ViewSettings.PHOTO_ONLY);
            }
            app.setViewSettings(viewSettings);
            new callSearchCountAsyncTask().execute();
            break;
        case R.id.radioButtonSel5:
            viewSettings.setPageSize(ViewSettings.PAGE_SIZE_5);
            app.setViewSettings(viewSettings);
            break;
        case R.id.radioButtonSel10:
            viewSettings.setPageSize(ViewSettings.PAGE_SIZE_10);
            app.setViewSettings(viewSettings);
            break;
        case R.id.radioButtonSel15:
            viewSettings.setPageSize(ViewSettings.PAGE_SIZE_15);
            app.setViewSettings(viewSettings);
            break;
        case R.id.radioButtonSel20:
            viewSettings.setPageSize(ViewSettings.PAGE_SIZE_20);
            app.setViewSettings(viewSettings);
            break;
            // photo section - end
        default:
            break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

        ReturnFilters();
        saveViewSettings();
    }

    private void saveViewSettings() {
        // get filter from database.
        DataSource d = new DataSource(this, app.getSeed());
        d.open();
        d.updateViewSettings(viewSettings.getId(), viewSettings);
        d.close();
    }

    protected void ReturnFilters() {
        filters.setMale(cbMale.isChecked());
        filters.setFemale(cbFemale.isChecked());
        filters.setComplex(cbComplex.isChecked());
        filters.setGenderUnknown(cbGenderUnknown.isChecked());

        filters.setAdult(cbAgeAdult.isChecked());
        filters.setChild(cbAgeChild.isChecked());
        filters.setAgeUnknown(cbAgeUnknown.isChecked());

        filters.setGreenZone(cbGreen.isChecked());
        filters.setBhGreenZone(cbBhGreen.isChecked());
        filters.setYellowZone(cbYellow.isChecked());
        filters.setRedZone(cbRed.isChecked());
        filters.setGrayZone(cbGray.isChecked());
        filters.setBlackZone(cbBlack.isChecked());
        filters.setUnassignedZone(cbUnassigned.isChecked());

        // get filter from database.
        DataSource d = new DataSource(this, app.getSeed());
        d.open();
        d.updateFilters(filters.getId(), filters);
        d.close();
        app.setFilters(filters);

		FiltersActivity.this.finish();
	}

	private void SearchCountForEvents() {
	}

    // Menu sections
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filters_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemLatency:
                testLatency();
                break;
            case R.id.itemDefaultSettings:
                defaultSettings();
                new callSearchCountAsyncTask().execute();
        }
        return true;
    }

    private void defaultSettings() {
        filters.setDefaults();
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        s.updateFilters(filters.getId(), filters);
        s.close();
        app.setFilters(filters);
        Initialize();
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

    public SearchResult callSearchCount(){
        String returnStr = "";
        long webServerId = app.getWebServerId();
        DataSource s = new DataSource(FiltersActivity.this, app.getSeed());
        WebServer wb = new WebServer();
        s.open();
        wb = s.getWebServerFromId(webServerId);
        s.close();
        if (wb == null) {
            wb = new WebServer();
        }
        wb.setToken(app.getToken());
//        wb.callSearchCount(app.getFilters(), app.getViewSettings(), FiltersActivity.this);
        wb.setSearchCountOnly(true);
        SearchResult sr = wb.callSearchCountV34(app.getFilters(), app.getViewSettings(), app.getCurSelEventShortName(), FiltersActivity.this);
        return sr;
    }

    private class callSearchCountAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        SearchResult sr = new SearchResult();
        String returnString = "";

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            sr.toDefault();
        	threadCounter++;
            tvNewTotalNumber.setVisibility(View.GONE);
            pbNewTotalNumber.setVisibility(View.VISIBLE);

            /*
        	cbMale.setEnabled(false);
        	cbFemale.setEnabled(false);
        	cbComplex.setEnabled(false);
        	cbGenderUnknown.setEnabled(false);

        	cbAgeAdult.setEnabled(false);
        	cbAgeChild.setEnabled(false);
        	cbAgeUnknown.setEnabled(false);

        	cbMissing.setEnabled(false);
        	cbAliveAndWell.setEnabled(false);
        	cbInjured.setEnabled(false);
        	cbDeceased.setEnabled(false);
        	cbStatusUnknown.setEnabled(false);
        	cbFound.setEnabled(false);
        	*/
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
			synchronized (this)
			{
                sr = callSearchCount();
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
        	threadCounter--;
        	if (threadCounter == 0){
        		/*
            	cbMale.setEnabled(true);
            	cbFemale.setEnabled(true);
            	cbComplex.setEnabled(true);
            	cbGenderUnknown.setEnabled(true);

            	cbAgeAdult.setEnabled(true);
            	cbAgeChild.setEnabled(true);
            	cbAgeUnknown.setEnabled(true);

            	cbMissing.setEnabled(true);
            	cbAliveAndWell.setEnabled(true);
            	cbInjured.setEnabled(true);
            	cbDeceased.setEnabled(true);
            	cbStatusUnknown.setEnabled(true);
            	cbFound.setEnabled(true);
            	*/

            	pbNewTotalNumber.setVisibility(View.GONE);
        		tvNewTotalNumber.setVisibility(View.VISIBLE);
                if (sr.getErrorCode().toString().equals("0") == true){
                    tvNewTotalNumber.setText(sr.getRecordsFound().toString());
                    app.setCurSearchCount(sr.getRecordsFound());
                }
                else {
                    tvNewTotalNumber.setText("-1");
                }
        	}
        	else {
                tvNewTotalNumber.setVisibility(View.GONE);
                pbNewTotalNumber.setVisibility(View.VISIBLE);
        	}
        }
    }
}
