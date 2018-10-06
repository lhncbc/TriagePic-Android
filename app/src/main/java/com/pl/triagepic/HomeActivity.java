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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements View.OnClickListener {
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static boolean started = false;

    private boolean enableEncryptImages = false;

    private static final String LOGIN = "Login";
	private static final String LOGOUT = "Logout";
    private static final String GUEST = "Guest";
    private static final int LOGIN_REQUEST = 1;
    private static final int SELECT_WEBSERVER = 2;
    private static final int ACCEPTANCE_REQUEST = 3;
    private static final int BURDEN_STATEMENT_SPLASH = 4;
    private static final int SPLASH_SCREEN = 5;
    private static final int ADD_NEW = 6;
    private static final int PATIENT_INFO = 7;
    private static final int PATIENT_DISPLAY = 8;
	static boolean wasSplashCalled = false;
	private static final int LOGIN_ACTIVITY = 9;
    private static final int SELECT_HOSPITAL = 10;
    private static final int SELECT_EVENT = 11;

    private Waiter waiter;
    private long userInteractionTime = 0;
    private long curSelItem;

    TriagePic app;
    boolean bAuthStatus = false;
    String webServer = "";
    long webServerId = 0;
    String username = "";
 	String password = "";
    String token = "";
    String curSelEvent = "";
    String curSelEventShortName = "";
    String curSelHospital = "";
    String oldUsername = "";
 	String oldPassword = "";
 	Credential credential;	
 	
	private List<Patient> patientsDeleted = new ArrayList<Patient>();
	private List<Patient> patientsDrafts = new ArrayList<Patient>();
	private List<Patient> patientsSent = new ArrayList<Patient>();
	private List<Patient> patientsOutbox = new ArrayList<Patient>();

	private static final int DRAFTS = 0;
	private static final int SENT = 1;
	private static final int OUTBOX = 2;
	private static final int DELETED = 3;
	
	private int curSelBox = 0;
	
	protected static final ArrayList<ItemBoxView> image_Box_details = null;
	ListView lvBox;
	String strBoxes[];
	ItemBoxListBaseAdapter adapterEx;
	
	CheckBox ckCheckAll;
	ListView lvPatient;
	boolean patientChecked[];
	TextView tvPatient;
//	String strPatients[];

	boolean firstCheckBox;
	
	ProgressBar progressBarPatientList;
	
	ProgressDialog progressDialog = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
		
		// Set up the action bar to show tabs.
//		final ActionBar actionBar = HomeActivity.this.getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Initial with globe variables.
        app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		credential = new Credential();
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
        if (!app.getPassword().isEmpty() && !app.getToken().isEmpty()){
            app.setAuthStatus(true);
        }
        else {
            app.setAuthStatus(false);
        }

//		settings = getSharedPreferences("Info", 0);
//		Intent start = new Intent(this, Webservice.class);
//		start.putExtra("check cred", true);
//		this.startService(start);

        // change isLogin
		if (isLogin() == false)
		{
            AlertDialog dlg;
			login();
		}
        else {
            if (this.enableEncryptImages == true){
                new decryptImagesAsyncTask().execute();
            }
        }

        app.setWebServerId(webServerId);
        app.setCurSelEvent(curSelEvent);
        app.setCurSelEventShortName(curSelEventShortName);
        app.setCurSelHospital(curSelHospital);

//        new InitializeAsyncTask(username).execute();
		Initialize();

//        waiter=new Waiter(1*60*1000); // will be 15 mins 15 * 60 * 1000
//        waiter.start();
	}

    /*
    public boolean isLogin(Context c){
    	boolean result = false;
    	String u = "";
    	String p = "";
    	DataSource s = new DataSource(c, app.getSeed());
    	s.open();
    	try {
    		u = s.getUsername();
    		p = s.getPassword();
    	}
    	catch(android.content.ActivityNotFoundException ex) {
    		u = "";
    		p = "";
    	}
    	s.close();
    	
    	if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST)){
    		app.setUsername(u);
    		app.setPassword(p);
    		app.setAuthStatus(false);
    		result = false;
    	}
    	else {
       		app.setUsername(u);
    		app.setPassword(p);
    		app.setAuthStatus(true);       	
    		result = true;
    	}
    	return result;
    }
*/
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

	private void Initialize() {
		loadPatients(DRAFTS);
		createPhotoPatients(DRAFTS);
		loadPatients(OUTBOX);
		createPhotoPatients(OUTBOX);
		loadPatients(SENT);
		createPhotoPatients(SENT);
		loadPatients(DELETED);
		createPhotoPatients(DELETED);

		if (started == true){
//			splash();
			started = false;
			
			curSelBox = DRAFTS;
		}

		strBoxes = new String[] {
				"Drafts",
				"Sent",
				"Outbox",
				"Deleted"
		};

//		strPatients = new String[] {
//				"Davies, Smith",
//				"Daniel, Hard",
//				"Westwood, Nigel",
//				"Ford, Glenn"
//		};
	
		progressBarPatientList = (ProgressBar) findViewById(R.id.progressBarPatientList);
		progressBarPatientList.setVisibility(View.INVISIBLE);
		
		ckCheckAll = (CheckBox) findViewById(R.id.checkBoxSelAll);
		ckCheckAll.setOnClickListener(this);

		tvPatient = (TextView) findViewById(R.id.textViewPatient);
		tvPatient.setVisibility(View.INVISIBLE);

		lvPatient = (ListView) findViewById(R.id.listViewPatient);
		
		lvBox = (ListView) findViewById(R.id.listViewBox);
		ArrayList<ItemBoxView> image_Box_details = GetSearchResults();
		lvBox.setAdapter(new ItemBoxListBaseAdapter(HomeActivity.this, image_Box_details));
//		lvBox.setTextFilterEnabled(true);
		lvBox.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvBox.setItemChecked(curSelBox, true);
        
		lvBox.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (ckCheckAll.isChecked() == true){
                    ckCheckAll.setChecked(false);
                }

        		Object o = lvBox.getItemAtPosition(position);
        		ItemBoxView obj_itemDetails = (ItemBoxView)o;
        		String strName = obj_itemDetails.getName();
        		
    			if (strName.equalsIgnoreCase("Deleted") == true){
    			    curSelBox = DELETED;
    			    Toast.makeText(HomeActivity.this, "You are selecting \"Deleted\".", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("Drafts") == true){
    			    curSelBox = DRAFTS;
    			    Toast.makeText(HomeActivity.this, "You are selecting \"Drafts\".", Toast.LENGTH_SHORT).show();	
    			}	
    			else if (strName.equalsIgnoreCase("Sent") == true){
    			    curSelBox = SENT;
    			    Toast.makeText(HomeActivity.this, "You are selecting \"Sent\".", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("Outbox") == true){
    			    curSelBox = OUTBOX;
    			    Toast.makeText(HomeActivity.this, "You are selecting \"Outbox\".", Toast.LENGTH_SHORT).show();							
    			}	
    			else {
    			    Toast.makeText(HomeActivity.this, "No selection.", Toast.LENGTH_SHORT).show();
                    return; // no change
    			}
                // no async no crash
//    			new DisplayPatientListAsyncTask().execute();
			    DisplayPatientList(curSelBox);
        	}
		});
		// no async no crash
//		new DisplayPatientListAsyncTask().execute();
		DisplayPatientList(curSelBox);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		outState.putInt("curSelBox", curSelBox);
		
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

		curSelBox = savedInstanceState.getInt("curSelBox");
		DisplayPatientList(curSelBox);
	}

	private void DisplayPatientList(final int curSelBox) {
    	List<Patient> patientsCur = new ArrayList<Patient>();
    	if (curSelBox == DRAFTS){
    		patientsCur = patientsDrafts;
    	}
    	else if (curSelBox == SENT){
    		patientsCur = patientsSent;
    	}
    	else if (curSelBox == OUTBOX){
    		patientsCur = patientsOutbox;
    	}
    	else if (curSelBox == DELETED){
    		patientsCur = patientsDeleted;
    	}
    	
		if (patientsCur.isEmpty() == true){
			progressBarPatientList.setVisibility(View.INVISIBLE);
			lvPatient.setVisibility(View.INVISIBLE);
			tvPatient.setText(strBoxes[curSelBox] + " is empty.");
			tvPatient.setVisibility(View.VISIBLE);
			return;
		}
		
		
		// get listview current position - used to maintain scroll
		// position
//		int currentPosition = lvPatient.getFirstVisiblePosition(); // do not need the currentPosition
		
		ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults(curSelBox);
        lvPatient.setAdapter(new ItemPatientListBaseAdapter(HomeActivity.this, image_Patient_details));

    	progressBarPatientList.setVisibility(View.INVISIBLE);
		tvPatient.setVisibility(View.INVISIBLE);
		lvPatient.setVisibility(View.VISIBLE);

		// Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatient.getItemAtPosition(position);
        		ItemPatientView obj_itemDetails = (ItemPatientView)o;
        		
        		int curSelId = (int)obj_itemDetails.getId();
//        		String str = obj_itemDetails.getPatienId();;
//        		int rowIndex = Integer.parseInt(str);
				Toast.makeText(HomeActivity.this, obj_itemDetails.getMyZone().name, Toast.LENGTH_SHORT).show();
				
//				if (curSelBox == SENT){
//					Intent i = new Intent(HomeActivity.this, PatientDisplayActivity.class);
//					i.putExtra("rowId", rowIndex);
//					startActivityForResult(i, PATIENT_DISPLAY);					
//				}
//				else {
					Intent i = new Intent(HomeActivity.this, PatientInfoActivity.class);
					i.putExtra("rowId", curSelId); // must be integer
					startActivityForResult(i, PATIENT_INFO);
//				}
        	}
        	        	
			private void StartPatientInforActivity(ItemPatientView item) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
				return null;
			}
        	;
		});        
	}

	protected void DisplayOutboxPatientList() {
		if (patientsOutbox.isEmpty() == true){
			lvPatient.setVisibility(View.INVISIBLE);
			tvPatient.setText("Outbox is empty.");
			tvPatient.setVisibility(View.VISIBLE);
			return;
		}
		tvPatient.setVisibility(View.INVISIBLE);
		lvPatient.setVisibility(View.VISIBLE);
		
		// get listview current position - used to maintain scroll
		// position
		ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults();
        lvPatient.setAdapter(new ItemPatientListBaseAdapter(HomeActivity.this, image_Patient_details));

		// Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatient.getItemAtPosition(position);
        		ItemPatientView obj_itemDetails = (ItemPatientView)o;

        		// Start activity and pass the data 
        		StartPatientInforActivity(obj_itemDetails);
        	}
        	
			private void StartPatientInforActivity(ItemPatientView item) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
				return null;
			}
        	;
		});        		
	}

	protected void DisplaySentPatientList() {
		if (patientsSent.isEmpty() == true){
			lvPatient.setVisibility(View.INVISIBLE);
			tvPatient.setText("Sent box is empty.");
			tvPatient.setVisibility(View.VISIBLE);
			return;
		}
		tvPatient.setVisibility(View.INVISIBLE);
		lvPatient.setVisibility(View.VISIBLE);

		// get listview current position - used to maintain scroll
		// position

		ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults();
        lvPatient.setAdapter(new ItemPatientListBaseAdapter(HomeActivity.this, image_Patient_details));

		// Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatient.getItemAtPosition(position);
        		ItemPatientView obj_itemDetails = (ItemPatientView)o;

        		// Start activity and pass the data 
        		StartPatientInforActivity(obj_itemDetails);
        	}
        	
			private void StartPatientInforActivity(ItemPatientView item) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
				return null;
			}
        	;
		});        
	}

	protected void DisplayDraftsPatientList() {
		if (patientsDrafts.isEmpty() == true){
			lvPatient.setVisibility(View.INVISIBLE);
			tvPatient.setText("Drafts box is empty.");
			tvPatient.setVisibility(View.VISIBLE);
			return;
		}
		tvPatient.setVisibility(View.INVISIBLE);
		lvPatient.setVisibility(View.VISIBLE);
		
		// get listview current position - used to maintain scroll
		// position

		ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults();
        lvPatient.setAdapter(new ItemPatientListBaseAdapter(HomeActivity.this, image_Patient_details));

		// Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatient.getItemAtPosition(position);
        		ItemPatientView obj_itemDetails = (ItemPatientView)o;

        		// Start activity and pass the data 
        		StartPatientInforActivity(obj_itemDetails);
        	}
        	
			private void StartPatientInforActivity(ItemPatientView item) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
				return null;
			}
        	;
		});        
	}

	protected void DisplayDeletedPatientList() {
		if (patientsDeleted.isEmpty() == true){
			lvPatient.setVisibility(View.INVISIBLE);
			tvPatient.setText("Deleted box is empty.");
			tvPatient.setVisibility(View.VISIBLE);
			return;
		}
		tvPatient.setVisibility(View.INVISIBLE);
		lvPatient.setVisibility(View.VISIBLE);
		
		// get listview current position - used to maintain scroll
		// position

		ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults();
        lvPatient.setAdapter(new ItemPatientListBaseAdapter(HomeActivity.this, image_Patient_details));

		// Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatient.getItemAtPosition(position);
        		ItemPatientView obj_itemDetails = (ItemPatientView)o;

        		// Start activity and pass the data 
        		StartPatientInforActivity(obj_itemDetails);
        	}
        	
			private void StartPatientInforActivity(ItemPatientView item) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Toast.makeText(HomeActivity.this, "To be added", Toast.LENGTH_SHORT).show();
				return null;
			}
        	;
		});        
	}
/*
	private void DisplayAllPatientList() {
*/
	/**
	 * loads the patients from the database, sorts them by date, and then sets
	 * the adapter and launches threads to load patient thumbnails
	 * @param curSelBox 
	 */
	private void loadPatients(int curSelBox) {
		DataSource d = new DataSource(this, app.getSeed());
		d.open();
		if (curSelBox == DRAFTS){
			patientsDrafts = d.getAllPatientsDraftsDesc();
		}
		else if (curSelBox == SENT){
			patientsSent = d.getAllPatientsSentDesc();
		}
		else if (curSelBox == OUTBOX){
			patientsOutbox = d.getAllPatientsOutboxDesc();
		}
		else if (curSelBox == DELETED){
			patientsDeleted = d.getAllPatientsDeletedDesc();
		}
		d.close();
	}
	
	@SuppressWarnings("deprecation")
	private void createPhotoPatients(int curSelBox) {
		if (curSelBox == DRAFTS) {
            for (int i = 0; i < patientsDrafts.size(); i++) {
                Patient p = patientsDrafts.get(i);
                if (p.images.size() > 0) {
                    p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false));
                    patientsDrafts.set(i, p);
                }
            }
        }
		else if (curSelBox == SENT){
			for (int i = 0; i < patientsSent.size(); i++){
				Patient p = patientsSent.get(i);
				if (p.images.size() > 0){
					p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false));
					patientsSent.set(i, p);
				}
			}
		}
		else if (curSelBox == OUTBOX){
			for (int i = 0; i < patientsOutbox.size(); i++){
				Patient p = patientsOutbox.get(i);
				if (p.images.size() > 0){
					p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false));
					patientsOutbox.set(i, p);
				}
			}
		}
		else if (curSelBox == DELETED){
			for (int i = 0; i < patientsDeleted.size(); i++){
				Patient p = patientsDeleted.get(i);
				if (p.images.size() > 0){
					p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false));
					patientsDeleted.set(i, p);
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SPLASH_SCREEN){
		}
		else if (requestCode == ADD_NEW){
			Initialize();
		}
		else if (requestCode == PATIENT_INFO){
			Initialize();
		}
		else if (requestCode == LOGIN_ACTIVITY){
			if (app.getAuthStatus()){
                String msg = "You will need to make two selections:\n\t1. Hospital \n\t2. Event";
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setIcon(R.drawable.triagepic_small_icon);
                builder.setMessage(msg)
                        .setTitle("Welcome " + app.getUsername() + "!")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(HomeActivity.this, "You are selecting hospital...", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(HomeActivity.this, HospitalActivity.class);
                                startActivityForResult(i, SELECT_HOSPITAL);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                alert.setCancelable(false); // V 7.0.2. No close. It is necessary to have this line.
			}
			else {
				System.exit(0);
			}
		}
        else if (requestCode == SELECT_HOSPITAL){
            String msg = "You have selected hospital: " + app.getCurSelHospital() + "\nNow will need to make the second selection: \n\tEvent";
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setIcon(R.drawable.triagepic_small_icon);
            builder.setMessage(msg)
                    .setTitle("Welcome " + app.getUsername() + "!")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(HomeActivity.this, "You are selecting event...", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(HomeActivity.this, EventActivity.class);
                            startActivityForResult(i,SELECT_EVENT);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            alert.setCancelable(false); // V 7.0.2. No close. It is necessary to have this line.
        }
        else if (requestCode == SELECT_EVENT){
            Toast.makeText(HomeActivity.this, "All settings are done. You may start to report...", Toast.LENGTH_LONG).show();
            saveCredentials();
			Initialize();
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

    private ArrayList<ItemBoxView> GetSearchResults(){
    	ArrayList<ItemBoxView> results = new ArrayList<ItemBoxView>();
	    	
    	ItemBoxView item_details;    	
    	for (int i = 0; i < strBoxes.length; i++)
    	{
    		item_details = new ItemBoxView();
    		String name = strBoxes[i];
    		item_details.setName(name);
    		if (name.equalsIgnoreCase("Drafts") == true){
    			item_details.setNumber(patientsDrafts.size());
    		}
    		else if (name.equalsIgnoreCase("Sent") == true){
    			item_details.setNumber(patientsSent.size());
    		}
    		else if (name.equalsIgnoreCase("Outbox") == true){
    			item_details.setNumber(patientsOutbox.size());
    		}
    		else if (name.equalsIgnoreCase("Deleted") == true){
    			item_details.setNumber(patientsDeleted.size());
    		}
//    		else {
 //   			item_details.setNumber(patientsAll.size());
//    		}
    		item_details.setImageId(i);
    		results.add(item_details);
    	}
    	return results;
	}

    private ArrayList<ItemPatientView> GetSearchPatientResults(int curSelBox){
    	ArrayList<ItemPatientView> results = new ArrayList<ItemPatientView>();
    	
    	List<Patient> patientsCur = new ArrayList<Patient>();
    	if (curSelBox == DRAFTS){
    		patientsCur = patientsDrafts;
    	}
    	else if (curSelBox == SENT){
    		patientsCur = patientsSent;
    	}
    	else if (curSelBox == OUTBOX){
    		patientsCur = patientsOutbox;
    	}
    	else if (curSelBox == DELETED){
    		patientsCur = patientsDeleted;
    	}
	    	
    	ItemPatientView item_details;    	
    	for (int i = 0; i < patientsCur.size(); i++)
    	{
    		Patient p = patientsCur.get(i);
    		item_details = new ItemPatientView();
   
    		item_details.setCurSel(true);
    		item_details.setName(p.firstName + " " + p.lastName);
   			item_details.setGender(p.gender.name);
   			item_details.setAge(p.age.name);
   			item_details.setPrefixPid(String.valueOf(p.prefixPid));
   			item_details.setPatientId(String.valueOf(p.patientId));
   			item_details.setFpid(p.getFpid());
   			item_details.setId(p.rowIndex);
   			item_details.setUuid(p.uuid);
   			item_details.setHospital(p.hospital);
   			item_details.setEvent(p.event);
   			
   			item_details.setMyZone(p.myZone);
   			
   			item_details.setImages(p.images);
   			item_details.setPhoto(p.photo);
//    		item_details.setImageId(p.images.get(p.images.i));
    		
    		/*
    		item_details.setImageId(i + 1);
    		*/
    		results.add(item_details);
    	}
    	return results;
	}

    private ArrayList<ItemPatientView> GetSearchPatientResults(){
    	ArrayList<ItemPatientView> results = new ArrayList<ItemPatientView>();
    	
    	List<Patient> patientsCur = new ArrayList<Patient>();
    	if (curSelBox == DRAFTS){
    		patientsCur = patientsDrafts;
    	}
    	else if (curSelBox == SENT){
    		patientsCur = patientsSent;
    	}
    	else if (curSelBox == OUTBOX){
    		patientsCur = patientsOutbox;
    	}
    	else if (curSelBox == DELETED) {
    		patientsCur = patientsDeleted;
    	}
	    	
    	ItemPatientView item_details;    	
    	for (int i = 0; i < patientsCur.size(); i++)
    	{
    		Patient p = patientsCur.get(i);
    		item_details = new ItemPatientView();
   
    		item_details.setCurSel(true);
    		item_details.setName(p.firstName + " " + p.lastName);
   			item_details.setGender(p.gender.name);
   			item_details.setAge(p.age.name);
   			item_details.setPrefixPid(String.valueOf(p.prefixPid));
   			item_details.setPatientId(String.valueOf(p.patientId));
   			item_details.setFpid(p.getFpid());
   			item_details.setHospital(p.hospital);
   			item_details.setEvent(p.event);
   			
   			item_details.setMyZone(p.myZone);
   			
//    		item_details.setImageId(p.images.get(p.images.i));
   			item_details.setImages(p.images);
   			item_details.setPhoto(p.photo);
    		
    		/*
    		item_details.setImageId(i + 1);
    		*/
    		results.add(item_details);
    	}
    	return results;
	}

    @Override
	public void onClick(View v) {
    	switch (v.getId()){
    	case R.id.checkBoxSelAll:
    		checkAllItems();
    		break;
    	}		
	}
    
	private void checkAllItems() {
		if (ckCheckAll.isChecked() == true){
    		checkAllItems(true);
		}
		else {
    		checkAllItems(false);
		}
	}

	private void checkAllItems(boolean isChecked) {
		// Change the data
		for (int i = 0; i < lvPatient.getCount(); i++){
			Object o = lvPatient.getItemAtPosition(i);
			ItemPatientView obj_itemDetails = (ItemPatientView)o;
			obj_itemDetails.setCheckStatus(isChecked);
		}
		// Change the item on view 
		for (int i = 0; i < lvPatient.getChildCount(); i++){
			CheckBox cb = (CheckBox) lvPatient.getChildAt(i).findViewById(R.id.checkBoxSel);
			cb.setChecked(isChecked);
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        if (TriagePic.RUN_ON_TABLET_ONLY){
            getMenuInflater().inflate(R.menu.activity_main_tablet_only, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.activity_main, menu);
        }
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.itemReport:
        		Report();
        		break;
        	case R.id.itemSetWebServer:
        		SetWebServer();
        		break;
            case R.id.itemSetHospitalInfo:
            	SetHospitalInfo();
            	break;
            case R.id.itemSetEventInfo:
            	SetEventInfo();
            	break;
//            case R.id.itemWebService:
//            	SetWebService();
//            	break;
            case R.id.itemHome:
            	GoHome();
            	break;
            case R.id.itemDelete:
                deleteReports(curSelBox);
            	break;
            case R.id.itemLatency:
            	testLatency();
            	break;
//            case R.id.itemLogout:
//                logout();
//                break;
            case R.id.itemLogin:
                login();
                break;
            case R.id.itemContactUs:
             	ContactUs();
             	break;
            case R.id.itemLogout:
                removeDataAndLogout();
                break;
//            case R.id.itemEncryptImages:
//                encryptImages();
//                break;
//            case R.id.itemDecryptImages:
//                decryptImages();
//                break;
            /*
            case R.id.itemDeleteImages:
                deleteImages();
                break;
                */
//            case R.id.itemClose:
//                closeApp();
//                break;
            /*
            case R.id.itemSetIdleTime:
                setIdleTime();
                break;
                */
//            case R.id.itemLookFilesSent:
//                lookFilesSent();
//                break;
            case R.id.itemUserInfo:
                lookUserInfo();
                break;
            case R.id.itemAbout:
                about();
                break;
            case R.id.itemQuestionAndAnswer:
                questionAndAnswer();
                break;
            case R.id.itemChangeLog:
                changeLog();
                break;
        }
        return true;
    }

    private void changeLog() {
        Intent i = new Intent(this, ChangeLogActivity.class);
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

    public void deleteReports(int curSelBox){
        String msg = "";
        if (isItemSelected() == true){
            try{
                DeleteCheckedReports();
            }
            catch (Exception e){
                msg = e.getMessage();
            }

            Initialize(); // this fixes the crash after deleting
                    /*
            try {
                RefreshPatientList(curSelBox);
            }
            catch (Exception e){
                msg = e.getMessage();
            }
            */
        }

        else {
            Toast.makeText(this, "No item is selected.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isItemSelected(){
        boolean found = false;
        for (int i = 0; i < lvPatient.getCount(); i++){
            Object o = lvPatient.getItemAtPosition(i);
            ItemPatientView obj_itemDetails = (ItemPatientView)o;
            boolean isChecked = obj_itemDetails.getCheckStatus();
            if (isChecked == true){
                found = true;
                break;
            }
        }
        return found;
    }

    private void lookUserInfo(){
        String msg = "Current user is \"" + app.getUsername() + "\".";
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage(msg)
                .setTitle("User Info")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void lookFilesSent(){
        Intent i = new Intent(this, LpWebPuuidActivity.class);
        String url = "https://removed.com/";
        i.putExtra("url", url);
        startActivity(i);
    }
    private void setIdleTime() {
        final CharSequence[] items = { "5 minutes", "10 minutes", "15 minutes (default)", "20 minutes", "30 minutes" };
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Select Idle Time");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        curSelItem = item;
                        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int minuts = 0;
                if (curSelItem == 0){
                    app.waiter.setPeriod(Waiter.FIVE_MUNITES);
                    minuts = 5;
                }
                else if (curSelItem == 1){
                    app.waiter.setPeriod(Waiter.TEN_MUNITES);
                    minuts = 10;
                }
                else if (curSelItem == 2){
                    app.waiter.setPeriod(Waiter.FIFTEEN_MUNITES);
                    minuts = 15;
                }
                else if (curSelItem == 3){
                    app.waiter.setPeriod(Waiter.TWENTY_MUNITES);
                    minuts = 20;
                }
                else if (curSelItem == 4){
                    app.waiter.setPeriod(Waiter.THIRTY_MUNITES);
                    minuts = 30;
                }
                Toast.makeText(HomeActivity.this, "Select idle time " + Long.toString(minuts) + " minutes.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                curSelItem = -1;
                Toast.makeText(HomeActivity.this, "No change", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
//        app.waiter.setPeriod(6000);
    }

    public void encryptImages(){
        if (app.getIsImageEncrypted() == false){
            long start, end, elapse;
            start = System.currentTimeMillis();
            Image img = new Image();
            img.EncryptAllImages(HomeActivity.this, app.getSeed());
            end = System.currentTimeMillis();
            elapse = end - start;
            String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
            Log.i("encryptImages", msg);
            app.setIsImageEncrypted(true);
        }
   }

    public void testEncryptImages(){
        long start, end, elapse;
        start = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        elapse = end - start;
        String msg = "OnStop() is called: elapse time " + Long.toString(elapse) + " seconds.";
        Log.i("encryptImages", msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void testDecryptImages(){
        long start, end, elapse;
        start = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        elapse = end - start;
        String msg = "OnStart() is called: elapse time " + Long.toString(elapse) + " seconds.";
        Log.i("encryptImages", msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void decryptImages(){
        if (app.getIsImageEncrypted() == true){
            long start, end, elapse;
            start = System.currentTimeMillis();
            Image img = new Image();
            img.DecryptAllImages(HomeActivity.this, app.getSeed());
            end = System.currentTimeMillis();
            elapse = end - start;
            String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
            Log.i("decryptImages", msg);
            app.setIsImageEncrypted(false);
        }
    }

    public void deleteImages(){
        long start, end, elapse;
        start = System.currentTimeMillis();
        Image img = new Image();
        img.DeleteAllImages(HomeActivity.this);
        end = System.currentTimeMillis();
        elapse = end - start;
        String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
        Log.i("deleteImages", msg);
    }

    private void removeDataAndLogout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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

    private void cleanCredentials() {
        credential.cleanAll();
    }

    protected void cleanAllTables() {
		// clean all the patient's files.
		DataSource s = new DataSource(HomeActivity.this, app.getSeed());
		s.open();
		s.deleteAllEvents();
		s.deleteAllHospitals();
		s.deleteAllWebServers();
		s.deleteAllUsers();
		s.deleteAllImages();
		s.deleteAllPatients();
		s.close();
	}

    protected void cleanUsersTable() {
        DataSource s = new DataSource(HomeActivity.this, app.getSeed());
        s.open();
        s.deleteAllEvents();
        s.deleteAllHospitals();
        s.deleteAllWebServers();
        s.deleteAllUsers();
        s.deleteAllImages();
        s.deleteAllPatients();
        s.close();
    }

	private void exit() {
		Toast.makeText(this, "Good bye!", Toast.LENGTH_SHORT).show();
		System.exit(0);
	}

	private void login() {
        if (isLogin() == false){
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY);
            return;
        }
        String name = app.getUsername();
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
            Toast.makeText(HomeActivity.this, "You are already logged out.", Toast.LENGTH_SHORT).show();
            return;
        }
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
	
	private void testLatency() {
        WebServer ws = new WebServer();
        if (app.getWebServerId() != -1){
            ws.setId(app.getWebServerId());

            DataSource s = new DataSource(this, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

		Intent i = new Intent(HomeActivity.this, LatencyActivity.class);
		i.putExtra("webServer", ws.getWebService());
		startActivity(i);		
	}

	private void Latency() {
		Intent i = new Intent(HomeActivity.this, Webservice.class);
		i.putExtra("ping", true);
		this.startService(i);
    }

	private void RefreshPatientList(int curSelBox) {
        /*
    	if (curSelBox != DELETED){
    		loadPatients(curSelBox);
    		createPhotoPatients(curSelBox);
    	}
    	*/
		loadPatients(DELETED);
		createPhotoPatients(DELETED);

    	DisplayBoxList();
	}

	private void DisplayBoxList() {
		ArrayList<ItemBoxView> image_Box_details = GetSearchResults();
		lvBox.setAdapter(new ItemBoxListBaseAdapter(HomeActivity.this, image_Box_details));
//		lvBox.setTextFilterEnabled(true);
		lvBox.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvBox.setItemChecked(curSelBox, true);
		lvBox.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvBox.getItemAtPosition(position);
        		ItemBoxView obj_itemDetails = (ItemBoxView)o;
        		String strName = obj_itemDetails.getName();
        		
    			if (strName.equalsIgnoreCase("Deleted") == true){
    			    curSelBox = DELETED;
    			    Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("Drafts") == true){
    			    curSelBox = DRAFTS;
    			    Toast.makeText(HomeActivity.this, "Drafts", Toast.LENGTH_SHORT).show();	
    			}	
    			else if (strName.equalsIgnoreCase("Sent") == true){
    			    curSelBox = SENT;
    			    Toast.makeText(HomeActivity.this, "Sent", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("Outbox") == true){
    			    curSelBox = OUTBOX;
    			    Toast.makeText(HomeActivity.this, "Outbox", Toast.LENGTH_SHORT).show();							
    			}	
    			else {
    			    Toast.makeText(HomeActivity.this, "Not implemented.", Toast.LENGTH_SHORT).show();							
    			}
				if (WebServer.AmIConnected(HomeActivity.this) == true) {
					new DisplayPatientListAsyncTask().execute();
//			    DisplayPatientList(curSelBox);
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
		});

        // must not be asyncTack - cause crash
//		new DisplayPatientListAsyncTask().execute();
		DisplayPatientList(curSelBox);
	}

	private void DeleteCheckedReports() {
        if (ckCheckAll.isChecked() == true){
            ckCheckAll.setChecked(false);
        }
		for (int i = 0; i < lvPatient.getCount(); i++){
			Object o = lvPatient.getItemAtPosition(i);
			ItemPatientView obj_itemDetails = (ItemPatientView)o;
			boolean isChecked = obj_itemDetails.getCheckStatus();
			if (isChecked == true){
				// get the pid
				long id = obj_itemDetails.getId();
				// delete in database
				DataSource s = new DataSource(this, app.getSeed());
				s.open();
				if (curSelBox == DELETED || curSelBox == SENT){
					s.deletePatient(id);
				}
				else {
					s.updatePatientBox(id, Patient.DELETED);
				}
				s.close();
			}
		}
	}

	private void Notification(String notificationTitle, String notificationMessage)
    {
    	/*
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "A New Message!", System.currentTimeMillis());
 
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
 
        notification.setLatestEventInfo(MainActivity.this, notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(10001, notification);
        */
    }	
    
	private void SetWebService() {
		Intent updates = new Intent(HomeActivity.this, Webservice.class);
		updates.putExtra("update all", true);
		HomeActivity.this.startService(updates);
	}

	private void SetEventInfo() {
		Intent i = new Intent(HomeActivity.this, EventActivity.class);
		startActivity(i);
	}

	private void SetHospitalInfo() {
		Intent i = new Intent(HomeActivity.this, HospitalActivity.class);
		startActivity(i);
	}

	private void SetWebServer() {
		Intent i = new Intent(HomeActivity.this, WebServerActivity.class);
		startActivity(i);
	}    
    
 	private void GoHome() {
		Intent i = new Intent(HomeActivity.this, HomeActivity.class);
		startActivity(i);
	}

	private void Report() {
		Intent i = new Intent(HomeActivity.this, ReportActivity.class);
		startActivityForResult(i, ADD_NEW);
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
      

    // 
    // Chase's codes start
    //
	private SharedPreferences settings;
	/**
	 * Set actions to be taken from varrying webservice results.
	 * 
	 * First recieves a credential check. If the internet was not available, but
	 * a username/password is available it continues to report, first noting
	 * that the internet is not available letting the user choose to continue
	 * without webservices or exit the app.
	 * 
	 * If internet is available first checks that username/password are valid.
	 * If so it then updates the hospital and event lists. If not it requests
	 * new credentials looping until valid credentials are submitted.
	 * 
	 * If the new list doesn't include the defualt event/hospital or this is the
	 * user's first time event/hospital defualts are requested
	 */

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("check cred")) {
				int i = intent.getIntExtra("check cred ret", 0);
				if (i == 0) {
					DataSource d = new DataSource(HomeActivity.this, app.getSeed());
					d.open();
					Event e = d.getEvent(settings.getString("event", null));
					Hospital h = d.getHospital(settings.getString("hospital", null));
					d.close();

					if (e != null && h != null) {
						// tempory disable this
//						updateAlert();
					} else {
						Intent updates = new Intent(HomeActivity.this,
								Webservice.class);
						updates.putExtra("update all", true);
						HomeActivity.this.startService(updates);
					}

				} else if (i == 9996) {
					if (settings.getString("pw", null) != null
							&& settings.getString("un", null) != null) {
						continueAlert();
					} else {
						closingAlert(false);
					}
				} else {
					loginAlert();
				}
			}
			if (intent.getAction().equals("update all")) {
				pickDefualtHospital();
			}
		}
	};

	/**
	 * This starts the activity sending the request to check the user's
	 * credentials
	 */
	public void getSaharedPreferences(){
		settings = getSharedPreferences("Info", 0);
		Intent start = new Intent(this, Webservice.class);
		start.putExtra("check cred", true);
		this.startService(start);
	}

	/**
	 * In the event that the user is not online but has a set of credentials,
	 * asks if the user wants to continue without webservices and acts
	 * accordingly.
	 */
	public void continueAlert() {
		Toast.makeText(HomeActivity.this, "No connectivity is detected. Working on offline.", Toast.LENGTH_SHORT).show();
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("No connectivity is detected. Press \"Yes\" to work on offline or \"No\" to exit?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(HomeActivity.this, "startActivity(new Intent(MainActivity.this, ReportPatient.class));", Toast.LENGTH_SHORT).show();
				
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				closingAlert(true);
			}
		});
		builder.create().show();
		 */
	}

	/**
	 * asks if user wants to update hospital/event lists
	 */
	public void updateAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Would you like to update the hospital and event lists?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent updates = new Intent(HomeActivity.this, Webservice.class);
				updates.putExtra("update all", true);
				HomeActivity.this.startService(updates);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(HomeActivity.this, "MainActivity.this.startActivity(new Intent(MainActivity.this, ReportPatient.class));", Toast.LENGTH_SHORT).show();
			}
		});
		builder.create().show();
	}

	/**
	 * closes app if not online and no stored un/pw
	 */
	public void closingAlert(boolean retry) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (retry)
			builder.setMessage("Closing app");
		else {
			builder.setMessage("Cannot continue without Webservices or a set of Credentials");
		}
		builder.setCancelable(false);

		builder.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.this.finish();
                    }
                });
		if (retry)
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							continueAlert();

						}
					});
		builder.create().show();

	}

	/**
	 * Loads hospitals from database and asks user to select a defualt hospital
	 * 
	 */
	public void pickDefualtHospital() {
		DataSource d = new DataSource(this, app.getSeed());
		d.open();
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.my_simple_spinner_item, d.getHospitalList());
		adapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
		d.close();
		new AlertDialog.Builder(this).setTitle("Select Hospital")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						settings.edit()
								.putString("hospital", adapter.getItem(which))
								.commit();
						pickDefualtEvent();
					}
				}).create().show();
	}

	/**
	 * loads events from database and asks user to select a defualt event
	 */
	public void pickDefualtEvent() {
		DataSource d = new DataSource(this, app.getSeed());
		d.open();
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.my_simple_spinner_item, d.getEventsList());
		adapter.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
		d.close();

		new AlertDialog.Builder(this).setTitle("Select Event")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						settings.edit()
								.putString("event", adapter.getItem(which))
								.commit();
						Toast.makeText(HomeActivity.this, "MainActivity.this.startActivity(new Intent(MainActivity.this, ReportPatient.class));", Toast.LENGTH_SHORT).show();
					
						dialog.cancel();
					}
				}).create().show();
	}

	/**
	 * Asks user to enter Credentials
	 */
	public void loginAlert() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.text_alert, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setIcon(R.drawable.edit);
		builder.setIcon(R.drawable.triagepic_small_icon);
		builder.setTitle("Welcome to TriagePic� on Android Tablets");
		builder.setView(textEntryView);
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	                return;
	           }
	       });
		builder.setPositiveButton("Login",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						// Check user credentials
						String un = ((TextView) textEntryView
								.findViewById(R.id.username_field)).getText()
								.toString();
						String pw = ((TextView) textEntryView
								.findViewById(R.id.password_field)).getText()
								.toString();
						if (un.equals("") && pw.equals("")) {
							Toast.makeText(HomeActivity.this, R.string.tryagain_up,
									Toast.LENGTH_SHORT).show();
							loginAlert();
							dialog.cancel();
						} else if (!un.equals("") && pw.equals("")) {
							Toast.makeText(HomeActivity.this, R.string.tryagain_p,
									Toast.LENGTH_SHORT).show();
							loginAlert();
							dialog.cancel();
						} else if (un.equals("") && !pw.equals("")) {
							Toast.makeText(HomeActivity.this, R.string.tryagain_u,
									Toast.LENGTH_SHORT).show();
							loginAlert();
							dialog.cancel();
						} else {
							settings.edit().putString("un", un)
									.putString("pw", pw).commit();
							Intent start = new Intent(HomeActivity.this,
									Webservice.class);
							start.putExtra("check cred", true);

							HomeActivity.this.startService(start);
						}
					}
				}).create().show();
	}    
	//
	// Chase's codes end
	//
	
	
	// Using AsyncTask to start  
    private class DisplayPatientListAsyncTask extends AsyncTask<Void, Integer, Void>  
    { 
        //Before running code in separate thread  
        @SuppressWarnings("deprecation")
		@Override  
        protected void onPreExecute()  
        {
        	// progress Bar
    		tvPatient.setVisibility(View.INVISIBLE);
    		lvPatient.setVisibility(View.INVISIBLE);
        	progressBarPatientList.setVisibility(View.VISIBLE);
        }
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{  
				DisplayPatientList(curSelBox);
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
        	progressBarPatientList.setVisibility(View.INVISIBLE);
    		tvPatient.setVisibility(View.INVISIBLE);
    		lvPatient.setVisibility(View.VISIBLE);
        }
    }


	@Override
	public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Do you want to exit?")
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.super.onBackPressed();
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
            progressDialog = new ProgressDialog(HomeActivity.this);  
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
				int eachSleep = 50;

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
        	System.exit(0);
        }

    }	
    
	//To use the AsyncTask, it must be subclassed  
    private class InitializeAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
		static final int MAX = 100;
		static final int STEP = 1;
		String username = "";
		
		public InitializeAsyncTask(String username){
			super();
			this.username = username; 
		}

		//Before running code in separate thread  
        @SuppressWarnings("deprecation")
		@Override  
        protected void onPreExecute()  
        {  
            progressDialog = new ProgressDialog(HomeActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if (username.isEmpty() == false){
            	progressDialog.setMessage("Welcome " + username + ", initializing, please wait...");            	
            }
            else {
            	progressDialog.setMessage("Initializing, please wait...");            	
            }
            progressDialog.setCancelable(false);  
            progressDialog.setIndeterminate(false);  
            progressDialog.show();
        }  
  
        @Override
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{  
				int eachSleep = 50;

				Initialize();
				
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
        }

    }	
    
    // Email to us
	public void ContactUs() {
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
		    Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
        s += "\nUsername: " + app.getUsername();
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

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //To use the AsyncTask, it must be subclassed
    private class encryptImagesAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Encrypting images, please wait...");
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
                encryptImages();
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
            HomeActivity.this.finish();

        }
    }
    //To use the AsyncTask, it must be subclassed
    private class decryptImagesAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Decrypting images, please wait...");
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
                decryptImages();
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
