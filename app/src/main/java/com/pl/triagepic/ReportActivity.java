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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.pl.triagepic.Image.Rect;
import com.pl.triagepic.Patient.Age;
import com.pl.triagepic.Patient.Gender;
import com.pl.triagepic.Result.ReportResult;
import com.pl.triagepic.Result.ReservePIDResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReportActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ReportActivity";

	private static final int GPS_PERMISSION_REQUEST_CODE = 1;
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
	public static final int RUN_GPS = 100;
	public static final int RUN_MAP = 101;
	public static final int RUN_CAMERA = 102;

	Credential credential;

	// Chase's codes
	// Start
	public static final int EDIT = 1616;
	private List<Patient> patients = new ArrayList<Patient>();
	public int mTabState;
	ActionBar actionBar;
	private static long fakeID = 1;
			
	private static final int MSG_INC = 0;
	private static final int MSG_DEC = 1;
	private static final int[] zones = { 
		R.id.green, 
		R.id.green_bh, 
		R.id.gray,
		R.id.yellow, 
		R.id.red, 
		R.id.black 
	};
	private static final int[] genders = {
		R.id.male, 
		R.id.female 
	};
	private static final int[] ages = {
		R.id.adult, 
		R.id.peds 
	};
//	private static final int[] incDec = {
//		R.id.increment, 
//		R.id.decrement 
//	};
	private int curImageIndex = 0;
	private long pid, rowId;
//	private Drawable pidBack;
	private Patient editPatientOriginal;
	private Patient.Age age = Age.UNKNOWN;
	private Patient.Gender gender = Gender.UNKNOWN;
	private Patient.Zone zone;
	private Patient.MyZone myZone;
	private ArrayList<Image> images;
	private Event e;
	private Hospital h;
//	private String hospital; // ->hospitalCurSel
//	private String event; // -> eventCurSel
	private ReportPatientImageHandler camera;
	private ScheduledExecutorService mUpdater;
	private static long rate = 1;
	private static int rateCheck = 0;
	/**
	 * This handler is used with the increment and decrement buttons so they can
	 * increment decrement faster and at higher powers of ten if held longer
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INC:
//				updatePID(true);
				return;
			case MSG_DEC:
//				updatePID(false);
				return;
			}
			super.handleMessage(msg);
		}
	};
	/**
	 * update the rate at which the p.id is increasing/decreasing, the
	 * increment/decrement accordingly, if the p.id over steps the
	 * [1,9999999999] bound set the id to the corresponding limit and send a
	 * toast that the limit cannot be exceeded
	 * 
	 * @param inc
	 */
	/*
	public void updatePID(boolean inc) {
		if (inc)
			changeRate(1);
		else
			changeRate(-1);

//		pid = Long.valueOf(((EditText) findViewById(R.id.patient_id_field)).getText().toString());// not in use
//		patient_id_field to replace editTextPatientId
//		String strPid = etPatientIdMain.getText().toString();
//		if (strPid.isEmpty() == true){
//			strPid = "0";
//		}
//		pid = Long.valueOf(strPid);
		pid = pid + rate;
		if (!(pid < 9999999999L)) {
			pid = 9999999999L;
			Toast.makeText(this, "Patient Id exceeds 9999999999", Toast.LENGTH_SHORT).show();
		}
		if (pid < 1) {
			pid = 1;
		}

//		((EditText) findViewById(R.id.patient_id_field)).setText(String.valueOf(pid));// not in use
//		etPatientIdMain.setText(String.valueOf(pid));
	}
*/
	/**
	 * for every 20 increments/decrements start incrementing/decrementing by an
	 * additional power until the incrementing/decrementing by 10000
	 * 
	 * @param i
	 *            positive if incrementing, negative if decrementing
	 */
	public void changeRate(int i) {
		if (rateCheck >= 0 && rateCheck < 20) {
			rate = i * 1;
			rateCheck++;
		}
		if (rateCheck >= 20 && rateCheck < 40) {
			rate = i * 10;
			rateCheck++;
		}
		if (rateCheck >= 40 && rateCheck < 60) {
			rate = i * 100;
			rateCheck++;
		}
		if (rateCheck >= 60 && rateCheck < 80) {
			rate = i * 1000;
			rateCheck++;
		}
		if (rateCheck >= 80) {
			rate = i * 10000;
			rateCheck++;
		}
	}
	
	/**
	 * start a service to make sure the P.id does not clash with any already on
	 * the device or in pl
	 */
	private void checkPID() {
		if (editPatientOriginal != null) {
			if (pid == editPatientOriginal.patientId)
				return;
		}
		Intent check = new Intent(this, Webservice.class);
		check.putExtra("check pid", true);
		check.putExtra("short", e.shortname);
		check.putExtra("name", e.name);
		check.putExtra("prefix", h.pidPrefix);
		check.putExtra("id", pid);
		this.startService(check);
	}

	/**
	 * This Runnable is used with the increment and decrement buttons so they
	 * can increment decrement faster and at higher powers of ten if held longer
	 * 
	 * @author bonifantmc
	 */
	private class UpdateCounterTask implements Runnable {
		private boolean mInc;

		public UpdateCounterTask(boolean inc) {
			mInc = inc;
		}

		public void run() {
			if (mInc) {
				mHandler.sendEmptyMessage(MSG_INC);
			} else {
				mHandler.sendEmptyMessage(MSG_DEC);
			}
		}
	}
	
	// End	
	
	static final int PICK_EVENT_REQUEST = 1;
	static final int TAKE_PHOTO_REQUEST = 2;
	static final int ADDRESS_REQUEST = 3;
	static final int QUERY_MEDIA = 4;
	static final int PICK_HOSPITAL_REQUEST = 5;
	static final int CROP_IMAGE = 6;
	static final int ADD_CAPITION = 7;
    static final int BARCODE_SCANNER = 8;
	
	TriagePic app;
//	private long idleTime;
//	private long idleStart;
//	private long idleEnd;
	
	Intent cameraIntent;
	
	Uri uriCroppedImage = null;
	Uri uriTobeCropped = null;
	File fileTriagePicDir = null;

	private Uri uriRealCurSel;
		
	TextView tvEvent;
	Button buEvent;
	String eventCurSel;
    String eventCurSelShortName;
					
	TextView tvHospital;
	Button buHospital;
	String hospitalCurSel;
	Hospital hospital;
	
	TextView textViewPrefixPid;
	TextView textViewPid;
    TextView textViewPid2;
    TextView textViewPid2Auto;
	Button buPlus;
	Button buMinus;
	Button buttonEnterNumber;
    Button buBarcodeScanner;
    Button buttonEnterNumberAuto;

	EditText etLastName;
	EditText etFirstName;

    // added in version 8.0.0
    ViewFlipper viewFlipperPidInput;
    RadioButton radioManual;
    RadioButton radioBarcode;
    RadioButton radioAuto;

    CheckBox ckMale;
	CheckBox ckFemale;
	
	CheckBox ckAdult;
	CheckBox ckChild;
	
	Button btSelectPatientId;
	
	TextView tvCurSelGender;
	TextView tvCurSelAge;
	
//	Spinner spZone;
	Spinner spZoneSel;
	int zoneCurSel;
	
	Spinner spEventSel;
	
	EditText etComments;
	
	Button btSend;
	Button btSaveToDrafts;
	Button btSaveToOutbox;
	
	Button btCamera;
	Bitmap bmpPhoto;
	ImageView ivPhoto;
    TextView tvPrimaryPhoto;
	Button btGallery;
	Button btPrimary;
	Button btCrop;
    Button btDeleteImage;
	Button btMoveLeft;
	Button btMoveRight;
	
	TextView tvCapition;
	Button btCapition;
	
//	TextView tvPatientIdPrefix;
//	EditText etPatientIdMain;
//	Button btPlus;
//	Button btMinus;

	final CharSequence[] genderItems = {"Male", "Female", "Unknown"};
	private String strGender = "Unknown";
	private int nGenderItem = 2;
	private int nCurSelGenderItem = nGenderItem;
	private String strCurSelGender = strGender;
	
	final CharSequence[] ageItems = {"Adult", "Child", "Unknown"};
	private String strAge = "Unknown";
	private int nAgeItem = 2;
	private int nCurSelAgeItem = nGenderItem;
	private String strCurSelAge = strAge;
	
    String[] zoneNames = {"Green Zone", "BH Green Zone", "Yellow Zone", "Red Zone", "Gray Zone", "Black Zone", "White Zone"};
    int zoneImages[] = { 
    		R.drawable.cell_shape_green,             
    		R.drawable.cell_shape_light_green, 
    		R.drawable.cell_shape_yellow,
            R.drawable.cell_shape_red, 
            R.drawable.cell_shape_gray, 
            R.drawable.cell_shape_black,
            R.drawable.cell_shape_white
            };
    
    private ProgressDialog progressDialog;  
       
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);	
		
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        credential = new Credential(this);

        Initialize();
	}

	private void Initialize() {
		fileTriagePicDir = this.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
//        fileTriagePicDir = this.getDir(MediaStore.EXTRA_OUTPUT, Context.MODE_PRIVATE);

//		zoneCurSel = ReportActivity.this.getSharedPreferences("Info", 0).getInt("zone", 0);
		zoneCurSel = ReportActivity.this.getSharedPreferences("Info", 0).getInt("zone", 0);
		myZone = Patient.MyZone.getZone(zoneCurSel);

		// UI
		tvEvent = (TextView) findViewById(R.id.textViewEvent);
        eventCurSel = app.getCurSelEvent();
		tvEvent.setText(eventCurSel);
		buEvent = (Button) findViewById(R.id.buttonSelectEvent);
		buEvent.setOnClickListener(this);

        // Get reference
//        eventCurSel = ReportActivity.this.getSharedPreferences("Info", 0).getString("event", "");
//        eventCurSel = app.getCurSelEvent();
//        if (eventCurSel.isEmpty() == true){
//            tvEvent.setText(eventCurSel);
//            SelectEvent();
//        }

		tvHospital = (TextView) findViewById(R.id.textViewHospital);
        hospitalCurSel = app.getCurSelHospital();
		tvHospital.setText(hospitalCurSel);
		buHospital = (Button) findViewById(R.id.buttonSelectHospital);
		buHospital.setOnClickListener(this);

//        hospitalCurSel = ReportActivity.this.getSharedPreferences("Info", 0).getString("hospital", "");
//        hospitalCurSel = app.getCurSelHospital();
//        if (hospitalCurSel.isEmpty() == true){
//            tvHospital.setText(hospitalCurSel);
//            SelectHospital();
//        }

        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        h = s.getHospital(hospitalCurSel);
        if (h != null){
//    		h.removeNonNumericCharInPrefix(); // we do not need to remove no number any more
        }
        s.close();

        buPlus = (Button) findViewById(R.id.buttonPlus);
		buPlus.setOnClickListener(this);
		buMinus = (Button) findViewById(R.id.buttonMinus);
		buMinus.setOnClickListener(this);
		buttonEnterNumber = (Button) findViewById(R.id.buttonEnterNumber);
		buttonEnterNumber.setOnClickListener(this);
		textViewPrefixPid = (TextView) findViewById(R.id.textViewPrefixPid);
        if (h == null){
            textViewPrefixPid.setText("");
        }
        else {
            textViewPrefixPid.setText(h.pidPrefix);
        }
        textViewPid = (TextView) findViewById(R.id.textViewPid);
        textViewPid2 = (TextView) findViewById(R.id.textViewPid2);

        // auto
        buttonEnterNumberAuto = (Button) findViewById(R.id.buttonEnterNumberAuto);
        buttonEnterNumberAuto.setOnClickListener(this);
        textViewPid2Auto = (TextView) findViewById(R.id.textViewPid2Auto);

//        DataSource s = new DataSource(this, app.getSeed());

        // modified in version 9.0.0. If any exception error happens, restart from 0.
        // the operator may define any number from here,
        s.open();
        long nCurId = 0;
        try {
            nCurId = s.getLastId() + 1;
        }
        catch (Exception e){
            nCurId = 0;
        }
        s.close();

		String str = "";
        if (h == null){
            str = String.valueOf(nCurId);
        }
        else {
            if (h.pidSuffixFixedLength <= 1){
                str = String.valueOf(nCurId);
            }
            else {
                str = formatNumber(nCurId, h.pidSuffixFixedLength);
                if (str == ""){
                    str = String.valueOf(nCurId);
                }
            }
        }
		textViewPid.setText(str);
        textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
        textViewPid2Auto.setText(textViewPid2.getText());

        buBarcodeScanner = (Button) findViewById(R.id.buttonBarcodeScanner);
        buBarcodeScanner.setOnClickListener(this);

		etLastName = (EditText) findViewById(R.id.last_name_field);
//		etLastName.setFocusable(false);
		etFirstName = (EditText) findViewById(R.id.first_name_field);
//		etFirstName.setFocusable(false);

        // added in since version 8.0.0
        viewFlipperPidInput = (ViewFlipper) findViewById(R.id.viewFlipperPidInput);
        radioBarcode = (RadioButton)findViewById(R.id.radioButtonBarcode);
        radioBarcode.setOnClickListener(this);
        radioManual = (RadioButton)findViewById(R.id.radioButtonManual);
        radioManual.setOnClickListener(this);
        radioAuto = (RadioButton)findViewById(R.id.radioButtonAuto);
        radioAuto.setOnClickListener(this);
        if (credential.getPidInputToolReference() == Credential.USING_BARCODE_SCANNER){
            radioBarcode.setChecked(true);
            radioManual.setChecked(false);
            radioAuto.setChecked(false);
            viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewBarcode)));
        }
        else if (credential.getPidInputToolReference() == Credential.USING_MANUAL_INPUT){
            radioBarcode.setChecked(false);
            radioManual.setChecked(true);
            radioAuto.setChecked(false);
            viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewManual)));
        }
        else {
            radioBarcode.setChecked(false);
            radioManual.setChecked(false);
            radioAuto.setChecked(true);
            viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewAuto)));
        }

		ckMale = (CheckBox) findViewById(R.id.male);
		ckMale.setOnClickListener(this);
		
		ckFemale = (CheckBox) findViewById(R.id.female);
		ckFemale.setOnClickListener(this);

		ckAdult = (CheckBox) findViewById(R.id.adult);
		ckAdult.setOnClickListener(this);
		
		ckChild = (CheckBox) findViewById(R.id.peds);
		ckChild.setOnClickListener(this);

		// get the event list
		s.open();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, s.getEventsList());
		s.close();
		
		ArrayList<ItemZoneView> image_details = GetSearchResults();
		spZoneSel = (Spinner) findViewById(R.id.spinnerZoneSel);
		spZoneSel.setAdapter(new ItemZoneListBaseAdapter(ReportActivity.this, image_details));
		spZoneSel.setSelection(myZone.i);
		spZoneSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	zoneCurSel = arg2;
            	myZone = Patient.MyZone.getZone(arg2);
        		ReportActivity.this.getSharedPreferences("Info", 0).edit().putInt("zone", myZone.i).commit();
            }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
		
		etComments = (EditText) findViewById(R.id.comments);

		btSend = (Button) findViewById(R.id.buttonSend);
		btSend.setOnClickListener(this);
		
		btSaveToDrafts = (Button) findViewById(R.id.buttonDraft);
		btSaveToDrafts.setOnClickListener(this);

		btSaveToOutbox = (Button) findViewById(R.id.buttonOutbox);
		btSaveToOutbox.setOnClickListener(this);

		btCamera = (Button) findViewById(R.id.buttonCamera);
		btCamera.setOnClickListener(this);
		
		ivPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        tvPrimaryPhoto = (TextView) findViewById(R.id.textViewPrimary);
		
		btGallery = (Button) findViewById(R.id.buttonGallery);
		btGallery.setOnClickListener(this);
		
		btPrimary = (Button) findViewById(R.id.buttonPrimary);
		btPrimary.setOnClickListener(this);
		
		btCrop = (Button) findViewById(R.id.buttonCrop);
		btCrop.setOnClickListener(this);

        btDeleteImage = (Button) findViewById(R.id.buttonDeleteImage);
        btDeleteImage.setOnClickListener(this);

		btMoveLeft = (Button) findViewById(R.id.left_arrow_imageview);
		btMoveLeft.setOnClickListener(this);
		
		btMoveRight = (Button) findViewById(R.id.right_arrow_imageview);
		btMoveRight.setOnClickListener(this);
		
		btCapition = (Button) findViewById(R.id.buttonAddCapition);
		btCapition.setOnClickListener(this);
		
		tvCapition = (TextView) findViewById(R.id.textViewCapition);
		
//		tvPatientIdPrefix = (TextView) findViewById(R.id.textViewPatientIdPrefix);
//		etPatientIdMain = (EditText) findViewById(R.id.patient_id_field); // not in use
//		btPlus = (Button) findViewById(R.id.buttonPlus);
//		btMinus = (Button) findViewById(R.id.buttonMinus);
//		btPlus.setOnClickListener(this);
//		btMinus.setOnClickListener(this);
/*		
		btPlus.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				int id = v.getId();
				long check = pid;
				if (id == R.id.buttonPlus || id == R.id.buttonMinus) {
					boolean isReleased = event.getAction() == MotionEvent.ACTION_UP
							|| event.getAction() == MotionEvent.ACTION_CANCEL;
					boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;

					if (isReleased) {
						v.setPressed(false);
						stopUpdating();
						if (check == pid) {
							if (v.getId() == R.id.buttonPlus) {
								updatePID(true);
							} else {
								updatePID(false);
							}
							checkPID();
							return false;
						}
					} else if (isPressed) {
						v.setPressed(true);
						startUpdating(id == R.id.buttonMinus);
					}
					return true;
				}
				return false;
			}			
		});
		
		btMinus.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				int id = v.getId();
				long check = pid;
				if (id == R.id.buttonPlus || id == R.id.buttonMinus) {
					boolean isReleased = event.getAction() == MotionEvent.ACTION_UP
							|| event.getAction() == MotionEvent.ACTION_CANCEL;
					boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;

					if (isReleased) {
						v.setPressed(false);
						stopUpdating();
						if (check == pid) {
							if (v.getId() == R.id.buttonPlus) {
								updatePID(true);
							} else {
								updatePID(false);
							}
							checkPID();
							return false;
						}
					} else if (isPressed) {
						v.setPressed(true);
						startUpdating(id == R.id.buttonMinus);
					}
					return true;
				}
				return false;
			}			
		});
*/
		// Set the initial data
		camera = new ReportPatientImageHandler(this);
		rowId = this.getIntent().getLongExtra("rowId", 0);
		if (rowId != 0)
			setFromPatient();
		else
			freshPage();
		setScreen();
	}
	
    private ArrayList<ItemZoneView> GetSearchResults(){
    	ArrayList<ItemZoneView> results = new ArrayList<ItemZoneView>();
	    	
    	ItemZoneView item_details;    	
    	for (int i = 0; i < zoneNames.length; i++)
    	{
    		item_details = new ItemZoneView();
    		item_details.setName(zoneNames[i]);
    		item_details.setImageNumber(i + 1);
    		results.add(item_details);
    	}
    	return results;
	}
	
	
	/**
	 * button listeners are set
	 */
	private void setScreen() {
		/**
		 * open the camera dialog
		 */
		/* Lee
		((Button) findViewById(R.id.takeAPicture))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						addPhoto().show();
					}
				});
				*/
		/**
		 * user can select male, female or neighter
		 */
		/*
		OnClickListener genderClick = new OnClickListener() {
			public void onClick(View v) {
				CheckBox male = (CheckBox) findViewById(R.id.male);
				CheckBox female = (CheckBox) findViewById(R.id.female);
				// update check boxes
				if (v == female) {
					male.setChecked(false);
					if (female.isChecked())
						gender = Gender.FEMALE;
					else
						gender = Gender.UNKNOWN;
					return;
				}
				if (v == male) {
					female.setChecked(false);
					if (male.isChecked())
						gender = Gender.MALE;
					else
						gender = Gender.UNKNOWN;
					return;
				}
			}
		};
		for (int i : genders)
			((CheckBox) findViewById(i)).setOnClickListener(genderClick);
*/
		/**
		 * user can select adult or peds, adult by defualt
		 */
		/*
		OnClickListener ageClick = new OnClickListener() {
			public void onClick(View v) {
				for (int i : ages)
				((CheckBox) findViewById(i)).setChecked(false);
				((CheckBox) v).setChecked(true);
				age = Age.getAge(v.getId());
				return;
			}

		};
		for (int i : ages)
			((CheckBox) findViewById(i)).setOnClickListener(ageClick);
*/
		/**
		 * user can select only one zone, once a zone is selected they can only
		 * change zones, they cannot revert to no zone selected
		 */
		/*
		OnTouchListener zoneListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setUnpressed();
				((CheckBox) findViewById(v.getId())).setChecked(true);
				zone = Zone.getZone(v.getId());
				return true;
			}
		};
		for (int i : zones)
			((CheckBox) findViewById(i)).setOnTouchListener(zoneListener);
			*/

		/**
		 * store defualt EditText background
		 */
	
//		pidBack = ((EditText) findViewById(R.id.patient_id_field)).getBackground(); // not in use

		/**
		 * if the increment/decrement is released stopp increment/decrementing
		 * if pressed start
		 */
		/*
		OnTouchListener intDecTou = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int id = v.getId();
				long check = pid;
				if (id == R.id.increment || id == R.id.decrement) {
					boolean isReleased = event.getAction() == MotionEvent.ACTION_UP
							|| event.getAction() == MotionEvent.ACTION_CANCEL;
					boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;

					if (isReleased) {
						v.setPressed(false);
						stopUpdating();
						if (check == pid) {
							if (v.getId() == R.id.increment) {
//								updatePID(true); // not in use
							} else {
//								updatePID(false); // not in use
							}
							checkPID();
							return false;
						}
					} else if (isPressed) {
						v.setPressed(true);
						startUpdating(id == R.id.increment);
					}
					return true;
				}
				return false;
			}
		};
*/
		/**
		 * if the increment/decrement is released stopp increment/decrementing
		 * if pressed start
		 */
		OnKeyListener intDecKey = new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean isKeyOfInterest = keyCode == KeyEvent.KEYCODE_DPAD_CENTER
						|| keyCode == KeyEvent.KEYCODE_ENTER;
				boolean isReleased = event.getAction() == KeyEvent.ACTION_UP;
				boolean isPressed = event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getAction() != KeyEvent.ACTION_MULTIPLE;

				if (isKeyOfInterest && isReleased) {
					stopUpdating();
				} else if (isKeyOfInterest && isPressed) {
//					startUpdating(v.getId() == R.id.increment);
				}
				return false;
			}
		};

		/*
		for (int i : incDec) {
			Button b = (Button) findViewById(i);
			b.setOnTouchListener(intDecTou);
			b.setOnKeyListener(intDecKey);
		}
*/
		/**
		 * if a person edits the patient id field mannually check the p.id once
		 * the field is nolonger being focused on
		 */
		/* Lee
		((EditText) findViewById(R.id.patient_id_field))
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					public void onFocusChange(View arg0, boolean arg1) {
						pid = Long.parseLong(((EditText) arg0).getText()
								.toString());
						if (arg0.getId() == R.id.patient_id_field
								&& !arg0.isFocused())
							checkPID();
					}
				});
				*/
		
		/**
		 * set a customized gesture listener to detect swiping right and left
		 * and long clicks left/right cycles through photos accordingly and long
		 * click deletes
		 */
		((ImageView) findViewById(R.id.imageViewPhoto))
				.setOnTouchListener(new OnTouchListener() {

					GestureDetector t = new GestureDetector(
							new MyGestureDetector());

					public boolean onTouch(View v, MotionEvent event) {
						return t.onTouchEvent(event);
					}
				});
		}
	
	/**
	 * used for cycling through images and and deleteing images
	 * 
	 * @author bonifantmc
	 * 
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		private static final float SWIPE_THRESHOLD_VELOCITY = 500;

		/**
		 * if there are no images do nothing, if swipe left increase the image
		 * index, if swipe right decrease the index check that curImage is not
		 * out of bounds, go to 0 if larger than the size, and go to the size if
		 * reaching negative so user cna cycle through like a carousel
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// right to left swipe
			int size = images.size();
			if (size <= 1)
				return false;
			if (velocityX > SWIPE_THRESHOLD_VELOCITY)
				curImageIndex--;
			else if (velocityX < -SWIPE_THRESHOLD_VELOCITY)
				curImageIndex++;

			if (curImageIndex < 0)
				curImageIndex = size - 1;
			if (size == curImageIndex)
				curImageIndex = 0;

			setMyImage();
			
			return true;
		}

		/**
		 * if the image is held down long remove it from the image list
		 * make a notice appear warning that this will remove the image
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			// Need to add confirm dialogue box
			if (images.size() <= 0){
				Toast.makeText(ReportActivity.this, "No image is to be deleted.", Toast.LENGTH_SHORT).show();
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
			builder.setMessage("Do you want to delete this photo?")
				   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setCancelable(true)
			       .setTitle("Warning")
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       })
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			   			if (images.size() > 0) {
							images.remove(curImageIndex);
							if (curImageIndex > 0)
								curImageIndex = curImageIndex - 1;
							setMyImage();
						}
						dialog.dismiss();
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
		}

		/**
		 * defualt is return false, set to true so onFling and onLongPress are
		 * performed
		 */
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

	/**
	 * set defualt values based on patient being edited.
	 */
	private void setFromPatient() {
		DataSource s = new DataSource(this, app.getSeed());
		s.open();
		editPatientOriginal = s.getPatient(rowId);
//		setTitle("Editing Patient " + editPatientOriginal.patientId);
		h = s.getHospital(editPatientOriginal.hospital);
		e = s.getEvent(editPatientOriginal.event);
		System.out.println(h + " " + e);
		s.close();
		
		gender = editPatientOriginal.gender;
		age = editPatientOriginal.age;

		((EditText) findViewById(R.id.first_name_field)).setText(editPatientOriginal.firstName);
//		etFirstName.setText(editPatientOriginal.firstName);
		((EditText) findViewById(R.id.first_name_field)).setText(editPatientOriginal.lastName);
//		etLastName.setText(editPatientOriginal.lastName);
		pid = editPatientOriginal.patientId;
		zone = editPatientOriginal.zone;
		images = editPatientOriginal.images;

//		((TextView) findViewById(R.id.patient_id_prefix)).setText(h.pIDPrefix);
//		tvPatientIdPrefix.setText(h.pIDPrefix);
		
		if (!gender.equals(Gender.UNKNOWN)){
			((CheckBox) findViewById(gender.i)).setChecked(true);
		}
		/*
		if (gender.equals(Gender.MALE) == true){
			rbMale.setChecked(true);
		}
		else if (gender.equals(Gender.FEMALE) == true){
			rbFemale.setChecked(true);
		}
		
//		((CheckBox) findViewById(age.i)).setChecked(true);
		if (age.i == 0){
			rbAdult.setChecked(true);
		}
		else {
			rbChild.setChecked(true);			
		}
		*/
		
//		((CheckBox) findViewById(zone.i)).setChecked(true);
		if (zone.i == 0){
			
		}
		
//		((EditText) findViewById(R.id.patient_id_field)).setText(String.valueOf(pid));// Not in use

	}
	
	/**
	 * clear the page of old data and create a clear page, also set defualt
	 * values
	 */
	public void freshPage() {
		/*
		 * If the event is null, the activity was just instantiated, if not a
		 * record is being clearedand these fields have no need to be reset If
		 * event isn't null a record is being cleared so zones need to be unset
		 */
		if (eventCurSel.isEmpty()) {
			SharedPreferences settings = this.getSharedPreferences("Info", 0);
			DataSource s = new DataSource(this, app.getSeed());
            eventCurSel = settings.getString("event", "Test Exercise");
            eventCurSelShortName = settings.getString("eventShortName", "Test");
			hospitalCurSel = settings.getString("hospital", "NLM (testing)");
			pid = settings.getLong(eventCurSel, 1);
			s.open();
			h = s.getHospital(hospitalCurSel);
			e = s.getEvent(eventCurSel);
			s.close();
//			((TextView) findViewById(R.id.textViewPatientIdPrefix))
//					.setText(h.pIDPrefix);
		} else {
			// This is for zone using check boxes. Not in use.
//			setUnpressed();
			for (int i : ages)
				((CheckBox) findViewById(i)).setChecked(false);
			for (int i : genders)
				((CheckBox) findViewById(i)).setChecked(false);
			etFirstName.setText("");
			etLastName.setText("");
		}
		age = Age.UNKNOWN;
		gender = Gender.UNKNOWN;
//		zone = Zone.UNASSIGNED;
		images = new ArrayList<Image>();

//		if (gender.equals(Gender.UNKNOWN) == false){
//			((CheckBox) findViewById(gender.i)).setChecked(true);
//		}
		/*
		if (gender.equals(Gender.MALE)){
			rbMale.setChecked(true);
		}
		else if (gender.equals(Gender.FEMALE)){
			rbFemale.setChecked(true);
		}
		*/
		
//		((CheckBox) findViewById(age.i)).setChecked(true);
		/*
		if (age.equals(Age.ADULT)){
			rbAdult.setChecked(true);
		}
		else if (age.equals(Age.PEDIATRIC)){
			rbChild.setChecked(true);
		}
		*/

		//		((EditText) findViewById(R.id.patient_id_field)).setText(String
//				.valueOf(pid));
//		etPatientIdMain.setText(String.valueOf(pid));// not in use
	}	

	/**
	 * Patient ID increment/decrement handling
	 */
	private void startUpdating(final boolean inc) {
		if (mUpdater != null) {
			return;
		}
		mUpdater = Executors.newSingleThreadScheduledExecutor();
		mUpdater.scheduleAtFixedRate(new UpdateCounterTask(inc), 200, 100,
				TimeUnit.MILLISECONDS);
	}

	private void stopUpdating() {
		mUpdater.shutdownNow();
		mUpdater = null;
		rate = 0;
		rateCheck = 0;
	}


	/**
	 * unpress all zone buttons for page clearing
	 */
	public void setUnpressed() {
		for (int i : zones)
			((CheckBox) findViewById(i)).setChecked(false);
	}
	
	  public class MyAdapter extends ArrayAdapter<String>{
		  
	        public MyAdapter(Context context, int textViewResourceId,   String[] objects) {
	            super(context, textViewResourceId, objects);
	        }
	 
	        @Override
	        public View getDropDownView(int position, View convertView,ViewGroup parent) {
	            return getCustomView(position, convertView, parent);
	        }
	 
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            return getCustomView(position, convertView, parent);
	        }
	 
	        public View getCustomView(int position, View convertView, ViewGroup parent) {
	 
	            LayoutInflater inflater=getLayoutInflater();
	            View row=inflater.inflate(R.layout.item_zone, parent, false);
	            TextView label=(TextView)row.findViewById(R.id.textZone);
	            label.setText(zoneNames[position]);
	 
	            View icon=(View)row.findViewById(R.id.viewZone);
	            icon.setBackgroundResource(zoneImages[position]);
	 
	            return row;
	            }
	        }	

	@Override
	public void onClick(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        switch(v.getId()){
            case R.id.radioButtonBarcode:
                if (radioBarcode.isChecked() == true){
                    viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewBarcode)));
                    credential.savePidInputToolPreferences(Credential.USING_BARCODE_SCANNER);
                }
                break;
            case R.id.radioButtonManual:
                if (radioManual.isChecked() == true) {
                    viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewManual)));
                    credential.savePidInputToolPreferences(Credential.USING_MANUAL_INPUT);
                }
                break;
            case R.id.radioButtonAuto:
                if (radioAuto.isChecked() == true) {
                    viewFlipperPidInput.setDisplayedChild(viewFlipperPidInput.indexOfChild(findViewById(R.id.viewAuto)));
                    credential.savePidInputToolPreferences(Credential.USING_AUTO);
                }
                break;
		case R.id.buttonSelectEvent:
			SelectEvent();
			break;
		case R.id.buttonSelectHospital:
			SelectHospital();
			break;
		case R.id.buttonSend:
			PingAndSend();
			break;
		case R.id.buttonDraft:
			SaveReportAsDrafts();
			break;
		case R.id.buttonOutbox:
			Outbox();
			break;
		case R.id.male:
			SelectMale();
			break;
		case R.id.female:
			SelectFemale();
			break;
		case R.id.adult:
			SelectAdult();
			break;
		case R.id.peds:
			SelectChild();
			break;
//		case R.id.spinnerEventSel:
//			spEventSel.requestFocus();
//			break;
		case R.id.buttonCamera:
			new checkPermissionAsyncTask(RUN_CAMERA, this).execute();
//			TakePhoto();
			break;
		case R.id.buttonGallery:
//			addPhoto();
			addPhotoFromGallery();
			break;
		case R.id.buttonPrimary:
			setPrimary();
			break;
		case R.id.buttonCrop:
			startCropProcess();
			break;
        case R.id.buttonDeleteImage:
            deleteCurrentImage();
            break;
		case R.id.left_arrow_imageview:
			MovePhotoLeft();
			break;
		case R.id.right_arrow_imageview:
			MovePhotoRight();
			break;
		case R.id.buttonAddCapition:
			AddCapition();
			break;
		case R.id.buttonPlus:
			PatientIdPlus();
			break;
		case R.id.buttonMinus:
			PatientIdMinus();
			break;
		case R.id.buttonEnterNumber:
			enterNumber();
			break;
        case R.id.buttonBarcodeScanner:
            launchBarcodeScanner();
            break;
        case R.id.buttonEnterNumberAuto:
            enterAPatientID();
            break;
		default:
			break;
		}
	}

    public String[] parsePID(String newPid){
        String tokens[] = {"", ""};

        Log.i(TAG, "newPid = " + newPid);
        if (newPid.contains("-")){
            String delims = "[-]";
            tokens = newPid.split(delims);
            Log.i(TAG, "pre = " + tokens[0] + " num = " + tokens[1]);
        }
        else {
            int i = 0;
            while (!Character.isDigit(newPid.charAt(i))){
                i++;
            }
            tokens[0] = newPid.substring(0, i);
            tokens[1] = newPid.substring(i);
            Log.i(TAG, "pre = " + tokens[0] + " num = " + tokens[1]);
        }
        return tokens;
    }


    public void enterAPatientID() {
        if (app.getReservePIDs().isEmpty()){
            new reservePatientIDsAsyncTask().execute();
        }
        else {
            String newPid = app.getReservePIDs().get(0);
            String tokens[] =  parsePID(newPid);
            enterPIDFull(tokens[0], tokens[1]);
            app.getReservePIDs().remove(0);
        }
    }

    public void clearPatientIDs(){
        if (!app.getReservePIDs().isEmpty()){
            app.getReservePIDs().clear();
        }
    }

    public void enterPIDFull(String pre, String num){
        String msg = "";
        if (pre.isEmpty()){
            msg = "Reserved PID has a empty prefix.";
        }
        else if (!pre.contains("AUTO")){
            msg = "Reserved PID does not start with \"AUTO\".";
        }
        else if (num.isEmpty()){
            msg = "Reserved PID has a empty number.";
        }
        if (!msg.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
            builder.setMessage(msg)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							return;
						}
					});
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            textViewPid2Auto.setText(pre + num);
            textViewPrefixPid.setText(pre);
            textViewPid.setText(num);
        }
    }

    public void startCropProcess() {
		// added in version 9.0.4.
		// temporary
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_menu_crop)
					.setTitle("Warning")
					.setMessage("Crop function is temporary disabled for Android OS 5.1.1 and above.")
					.setCancelable(true)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else {
			if (images.size() == 0) // select only
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_menu_crop)
						.setTitle("Crop")
						.setMessage("Do you want to select the image cropped before?")
						.setCancelable(true)
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.setNeutralButton("Select", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(ReportActivity.this, ImageGallery.class);
								startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_menu_crop)
						.setTitle("Crop")
						.setMessage("Do you want to \n\t1. crop the current displayed image, or\n\t2. select the image cropped before?")
						.setCancelable(true)
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.setNeutralButton("Select", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(ReportActivity.this, ImageGallery.class);
								startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
								dialog.dismiss();
							}
						})
						.setPositiveButton("Crop", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								doCrop();
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
    }

    private ReservePIDResult reservePatientIDs() {
        WebServer ws = new WebServer();
        ReservePIDResult reservePIDResult = ws.callReservePatientIDs(app.getToken(), String.valueOf(h.uuid));
        return reservePIDResult;
    }

    //To use the AsyncTask, it must be subclassed
    private class reservePatientIDsAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        ReservePIDResult reservePIDResult;

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(ReportActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Reserving patient IDs, please wait...");
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
                reservePIDResult = reservePatientIDs();
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

            if (reservePIDResult.getErrorCode().equalsIgnoreCase("0")) {
                app.setReservePIDs(reservePIDResult.getReservePIDs());
                Toast.makeText(ReportActivity.this, "Patient IDs are reserved.", Toast.LENGTH_SHORT).show();

                String newPid = app.getReservePIDs().get(0);
                String tokens[] =  parsePID(newPid);
                enterPIDFull(tokens[0], tokens[1]);
                app.getReservePIDs().remove(0);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setMessage(reservePIDResult.getErrorMessage().toString())
                        .setCancelable(true)
                        .setTitle("Error")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void deleteCurrentImage(){
        // Need to add confirm dialogue box
        if (images.size() <= 0){
            Toast.makeText(ReportActivity.this, "No image is to be deleted.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setMessage("Do you want to delete this image?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setTitle("Warning")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (images.size() > 0) {
							images.remove(curImageIndex);
							if (curImageIndex > 0)
								curImageIndex = curImageIndex - 1;
							setMyImage();
						}
						dialog.dismiss();
					}
				});
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void PatientIdMinus() {
        int i;
        String str = textViewPid.getText().toString();
        if (str.isEmpty() == true){
            i = 0;
            textViewPid.setText("000000");
            textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
            textViewPid2Auto.setText(textViewPid2.getText());
            return;
        }
        i = Integer.parseInt(str);
        if (i <= 0){
            i = 0;
            return;
        }
        i--;

        if (h.pidSuffixFixedLength <= 1){
            str = String.valueOf(i);
        }
        else {
            str = formatNumber(i, h.pidSuffixFixedLength);
            if (str == ""){
                return;
            }
        }

        textViewPid.setText(str);
        textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
        textViewPid2Auto.setText(textViewPid2.getText());
    }

    // modified in version 8.0.4
    private void PatientIdPlus() {
        long i;
        String str = textViewPid.getText().toString();
        if (str.isEmpty() == true){
            str = "0";
        }
        i = Integer.parseInt(str);

        i++;

        if (h.pidSuffixFixedLength <= 1){
            str = String.valueOf(i);
        }
        else {
            str = formatNumber(i, h.pidSuffixFixedLength);
            if (str == "") {
                return;
            }
        }

        textViewPid.setText(str);
        textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
        textViewPid2Auto.setText(textViewPid2.getText());
    }

    private String formatNumber(long i, int length) {
        String str = "";
        if (length == 2){
            if (i > 99){
                Toast.makeText(this, "Maximum is 99.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%02d", i);
            return str;
        }
        else if (length == 3){
            if (i > 999){
                Toast.makeText(this, "Maximum is 999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%03d", i);
            return str;
        }
        else if (length == 4){
            if (i > 9999){
                Toast.makeText(this, "Maximum is 9999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%04d", i);
            return str;
        }
        else if (length == 5){
            if (i > 99999){
                Toast.makeText(this, "Maximum is 99999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%05d", i);
            return str;
        }
        else if (length == 6){
            if (i > 999999){
                Toast.makeText(this, "Maximum is 999999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%06d", i);
            return str;
        }
        else if (length == 7){
            if (i > 9999999){
                Toast.makeText(this, "Maximum is 9999999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%07d", i);
            return str;
        }
        else if (length == 8){
            if (i > 99999999){
                Toast.makeText(this, "Maximum is 99999999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%08d", i);
            return str;
        }
        else if (length == 9){
            if (i > 999999999){
                Toast.makeText(this, "Maximum is 999999999.", Toast.LENGTH_SHORT).show();
                return str;
            }
            str = String.format("%09d", i);
            return str;
        }
        else if (length >= 10){
            Toast.makeText(this, "Input number is too big - " + String.valueOf(length), Toast.LENGTH_SHORT).show();
            return str;
        }

        return str;
    }

    private void enterNumber() {
		int n = Integer.parseInt(textViewPid.getText().toString());
		String msg = "";
		if (h.pidSuffixFixedLength <= 0){
			msg = "Maximum length is " + Hospital.MAXIMUM_SUFFIX_LENGTH;			
		}
		else {
			msg = "Maximum length is " + h.pidSuffixFixedLength;			
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		final EditText editText = new EditText(ReportActivity.this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		InputFilter[] FilterArray = new InputFilter[1];
		if (h.pidSuffixFixedLength <= 0) {
			FilterArray[0] = new InputFilter.LengthFilter(Hospital.MAXIMUM_SUFFIX_LENGTH);			
		}
		else {
			FilterArray[0] = new InputFilter.LengthFilter(h.pidSuffixFixedLength);
		}
		editText.setFilters(FilterArray);
		
		if (h.pidSuffixFixedLength <= 0){
			editText.setText(textViewPid.getText().toString());						
		}
		else {
			editText.setText(String.valueOf(n));			
		}
		builder.setView(editText)
		       .setCancelable(true)
		       .setTitle("Enter Patient Suffix Number")
		       .setMessage(msg)
			   .setIcon(R.drawable.edit)
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   // hide the keyboard
//    		    		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//    		    		mgr.hideSoftInputFromWindow(editText.getWindowToken(), textViewPid.getText().length());

					   dialog.cancel();
					   return;
				   }
			   })
		       .setPositiveButton("Save", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   // hide the keyboard
//	   		    		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//	   		    		mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

					   String str = editText.getText().toString().trim();
					   if (str.isEmpty() == true) {
						   str = "0";
					   }
					   if (h.pidSuffixFixedLength > 0) {
						   str = formatNumber(Integer.parseInt(str), h.pidSuffixFixedLength);
					   }
					   if (str == "") {
						   Toast.makeText(ReportActivity.this, "Error in input.", Toast.LENGTH_SHORT).show();
						   return;
					   }
					   textViewPid.setText(str);
					   textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
					   textViewPid2Auto.setText(textViewPid2.getText());
					   dialog.dismiss();

					   return;
				   }
			   });
		AlertDialog alert = builder.create();		
		alert.show();
	}

    private void enterNumber2(){
        int n = Integer.parseInt(textViewPid.getText().toString());
        String msg = "";
        final int maxLength;
        if (h.pidSuffixFixedLength <= 0){
            msg = "Maximum length is " + Hospital.MAXIMUM_SUFFIX_LENGTH;
        }
        else {
            msg = "Maximum length is " + h.pidSuffixFixedLength;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        LayoutInflater inflater = ReportActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_enter_number, null);
        TextView message = (TextView) v.findViewById(R.id.textViewMessage);
        message.setText(msg);
        final EditText number = (EditText) v.findViewById(R.id.editTextNumber);

        InputFilter[] FilterArray = new InputFilter[1];
        if (h.pidSuffixFixedLength < 0) {
//            FilterArray[0] = new InputFilter.LengthFilter(Hospital.MAXIMUM_SUFFIX_LENGTH);
        }
        else {
            FilterArray[0] = new InputFilter.LengthFilter(h.pidSuffixFixedLength);
            number.setFilters(FilterArray);
        }
        number.setText(textViewPid.getText());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setIcon(R.drawable.edit)
                .setCancelable(true)
                .setTitle("Enter Patient Suffix Number")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(ReportActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				})
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String num = number.getText().toString();
						if (h.pidSuffixFixedLength > 0) {
							int n = Integer.parseInt(num);
							num = intToStringWithLeadingZeros(n, h.pidSuffixFixedLength);
						}
						textViewPid.setText(num);
						Toast.makeText(ReportActivity.this, "Number is: " + num, Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String intToStringWithLeadingZeros(int n, int length){
        String result = "";
        String format = "%0" + String.valueOf(length) + "d";
        result = String.format(format, n);
        return result;
    }

    private int stringWithLeadingZerosToInt(String str){
        str = str.trim();
        while(str.charAt(0) == '0'){
            str = str.substring(1);
        }
        return Integer.parseInt(str);
    }

	private void PingAndSend() {
		TriagePic app = ((TriagePic)this.getApplication());
		long webServerId = app.getWebServerId();
		
		DataSource s = new DataSource(this, app.getSeed());
		WebServer w = new WebServer();
		s.open();
		w = s.getWebServerFromId(webServerId);
		s.close();

        // make changes for v33
        if (w == null){
            w = new WebServer();
        }
        w.setToken(app.getToken());
        String returnString = "";
		MyPingEcho myPingEcho = new MyPingEcho(w);
        if (app.getAuthStatus() == true){
            myPingEcho.setToken(app.getToken());
            myPingEcho.setUsername(app.getUsername());
            returnString = myPingEcho.Call();
        }

		if (returnString.equalsIgnoreCase(MyPingEcho.TIME_OUT) == true || returnString.isEmpty() == true){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(myPingEcho.ERR_MSG)
			       .setCancelable(true)
			       .setTitle("No Network Connectivity")
			       .setIcon(android.R.drawable.ic_dialog_alert)
			       .setNegativeButton("Save to Outbox", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                Outbox();
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();		
		}
		else {
			Send();
		}
	}

	private void AddCapition() {
		if (images.size() <= 0){
    		Toast.makeText(ReportActivity.this, "There is no photo. No caption is required.", Toast.LENGTH_SHORT).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		final EditText editText = new EditText(ReportActivity.this);
		editText.setText(tvCapition.getText());
		builder.setView(editText)
		       .setCancelable(true)
		       .setTitle("Add Caption")
			   .setIcon(android.R.drawable.ic_menu_edit)
		       .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) { 		    		   
    		    		// hide the keyboard
    		    		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		    		mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		                
    		    		Toast.makeText(ReportActivity.this, "Quit", Toast.LENGTH_SHORT).show();
    		    		dialog.cancel();
		                return;
		           }
		       })
		       .setPositiveButton("Save", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   // hide the keyboard
					   InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					   mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					   dialog.dismiss();

					   String str = editText.getText().toString().trim();
					   Toast.makeText(ReportActivity.this, "Saving... \"" + str + "\"", Toast.LENGTH_SHORT).show();
					   Image i = images.get(curImageIndex);
					   i.setCaption(str);
					   tvCapition.setText(str);

					   return;
				   }
			   });
		AlertDialog alert = builder.create();		
		alert.show();			
	}

	public void doCrop() {
		if (images.size() <= 0){
			Toast.makeText(this, "No photo is selected.", Toast.LENGTH_SHORT).show();
			return;
		}

		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		Image img = new Image();
		img = images.get(curImageIndex); 
		uriRealCurSel = img.getUriReal(); 
		
		int width = img.getBitmap().getWidth();
		if (width < Image.WIDTH_MINIMUM_IMAGE)
		{
        	Toast.makeText(this, "The selected image is too small to crop. The required minimum width is " + String.valueOf(Image.WIDTH_MINIMUM_IMAGE), Toast.LENGTH_SHORT).show();
			return;
		}
		int height = img.getBitmap().getHeight(); 
		if (height < Image.HEIGHT_MINIMUM_IMAGE){
        	Toast.makeText(this, "The selected image is too small to crop. The required minimum height is " + String.valueOf(Image.HEIGHT_MINIMUM_IMAGE), Toast.LENGTH_SHORT).show();
        	return;
		}
   	
    	Toast.makeText(this, "Photo width: " + String.valueOf(width) + ", height: " + String.valueOf(height), Toast.LENGTH_SHORT).show();

    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();

        if (size == 0) {
        	Toast.makeText(this, "Gallery app is not installed. Please install Gallery app.", Toast.LENGTH_SHORT).show();
            return;
        } 
        else {
        	intent.setData(uriRealCurSel);

			intent.putExtra("crop", "false"); 
        	intent.putExtra("outputX", Image.CROP_WIDTH);
            intent.putExtra("outputY", Image.CROP_HEIGHT);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            Intent i = new Intent(intent);
            ResolveInfo res	= list.get(0);
            i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, CROP_IMAGE);
        }
	}
	
	public void setPrimary() {
		if (images.size() <= 0){
			Toast.makeText(this, "Patient has no photo.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (curImageIndex == 0){
			Toast.makeText(this, "Current photo is primary photo.", Toast.LENGTH_SHORT).show();
			return;
		}

   	   selectCurrentPhotoAsPrimary();
   	   Toast.makeText(this, "Current photo is now set as primary photo.", Toast.LENGTH_SHORT).show();
	}

	protected void selectCurrentPhotoAsPrimary() {
		Image imgCurSel = images.get(curImageIndex);
		Image imgCurPri = images.get(0);
		images.set(0, imgCurSel);
		images.set(curImageIndex, imgCurPri);
		
		curImageIndex = 0;
		setMyImage();
	}

	private void SelectAdult() {
		if (ckAdult.isChecked() == true){
			ckAdult.setChecked(true);			
			ckChild.setChecked(false);
			age = Age.ADULT;
		}
		else{
			ckAdult.setChecked(false);			
			ckChild.setChecked(true);
			age = Age.PEDIATRIC;
		}
	}

	private void SelectChild() {
		if (ckChild.isChecked() == true){
			ckAdult.setChecked(false);			
			ckChild.setChecked(true);
			age = Age.PEDIATRIC;
		}
		else {
			ckAdult.setChecked(true);			
			ckChild.setChecked(false);
			age = Age.ADULT;
		}
	}

	private void SelectMale() {
		if (ckMale.isChecked() == true){
			ckMale.setChecked(true);			
			ckFemale.setChecked(false);
			gender = Gender.MALE;
		}
		else{
			ckMale.setChecked(false);			
			ckFemale.setChecked(true);
			gender = Gender.FEMALE;
		}
	}

	private void SelectFemale() {
		if (ckFemale.isChecked() == true){
			ckMale.setChecked(false);			
			ckFemale.setChecked(true);
			gender = Gender.FEMALE;
		}
		else {
			ckMale.setChecked(true);			
			ckFemale.setChecked(false);
			gender = Gender.MALE;
		}
	}

	private void SelectHospital() {
		Intent i = new Intent(ReportActivity.this, HospitalListFragmentActivity.class);
        String curSel = tvHospital.getText().toString();
        if (curSel.isEmpty()){
            i.putExtra("hospitalname", "");
        }
        else {
            i.putExtra("hospitalname", curSel);
        }
		startActivityForResult(i, PICK_HOSPITAL_REQUEST);
	}

	private void SelectEvent() {
		Intent i = new Intent(ReportActivity.this, EventListFragmentActivity.class);
        String curSel = tvEvent.getText().toString();
        if (curSel.isEmpty()){
            i.putExtra("eventname", "");
        }
        else {
            i.putExtra("eventname", curSel);
        }
		startActivityForResult(i, PICK_EVENT_REQUEST);
	}

	private void MovePhotoRight() {
		// right to left swipe
		int size = images.size();
		if (size <= 1){
			return;
		}
//		if (velocityX > SWIPE_THRESHOLD_VELOCITY)
//			curImageIndex--;
//		else if (velocityX < -SWIPE_THRESHOLD_VELOCITY)
			curImageIndex++;

		if (curImageIndex < 0)
			curImageIndex = size - 1;
		if (size == curImageIndex)
			curImageIndex = 0;

        Toast.makeText(this, "Moving to image number " + String.valueOf(curImageIndex + 1), Toast.LENGTH_SHORT).show();
		setMyImage();

//		displayFaces();
		
	}

	private void MovePhotoLeft() {
		// right to left swipe
		int size = images.size();
		if (size <= 1){
			return;
		}
//		if (velocityX > SWIPE_THRESHOLD_VELOCITY)
			curImageIndex--;
//		else if (velocityX < -SWIPE_THRESHOLD_VELOCITY)
//			curImageIndex++;

		if (curImageIndex < 0)
			curImageIndex = size - 1;
		if (size == curImageIndex)
			curImageIndex = 0;

        Toast.makeText(this, "Moving to image number " + String.valueOf(curImageIndex + 1), Toast.LENGTH_SHORT).show();
		setMyImage();
		
//		displayFaces();
	}

	private void TakePhoto() {
		cameraIntent = camera.startCamera();
		startActivityForResult(cameraIntent, ReportPatientImageHandler.IMAGE_CAPTURE);
		app.setCameraIntent(cameraIntent);
	}

	public void SaveReportAsDrafts() {
		Patient p = getPatient();
		p.boxId = Patient.DRAFTS;
		DataSource s = new DataSource(this, app.getSeed());
		s.open();
		if (this.editPatientOriginal != null) {
			p.rowIndex = this.editPatientOriginal.rowIndex;
			s.updatePatient(p);

			// Image
			s.deleteImageByPid(p.getPid());
			for (int i = 0; i < p.images.size(); i++){
				Image img = images.get(i);
				img.setPid(p.getPid());
				img.setSquence(i);
				img = s.createImage(img);
				if (img == null) {
					Log.e("Error", "Failed to create record in image table.");
				}
			}
		} 
		else{
            p = s.createPatientPlus(p);

            // Image
			for (int i = 0; i < p.images.size(); i++){
				Image img = images.get(i);
				img.setPid(p.getPid());
				img.setSquence(i);
				img = s.createImage(img);
				if (img == null) {
					Log.e("Error", "Failed to create record in image table.");
				}
			}
		}		
		s.close();
		Toast.makeText(this, "Report is saved as draft", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	public void SaveReportAsOutbox() {
		Patient p = getPatient();
		p.boxId = Patient.OUTBOX;
		DataSource s = new DataSource(this, app.getSeed());
		s.open();
		if (this.editPatientOriginal != null) {
			p.rowIndex = this.editPatientOriginal.rowIndex;
			s.updatePatient(p);
			
			// Image
			s.deleteImageByPid(p.getPid());
			for (int i = 0; i < p.images.size(); i++){
				Image img = images.get(i);
				img.setPid(p.getPid());
				img.setSquence(i);
				s.createImage(img);
			}
		} 
		else{
            p = s.createPatientPlus(p);

            // Image
			for (int i = 0; i < p.images.size(); i++){
				Image img = images.get(i);
				img.setPid(p.getPid());
				img.setSquence(i);
				s.createImage(img);
			}
		}		
		s.close();
		Toast.makeText(this, "Report is saved to Outbox", Toast.LENGTH_SHORT).show();
		this.finish();
	}
	
	public void Outbox(){
		// Stage 1 verify input data
		String message = verifyData();
		if (message.isEmpty() == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
			builder.setMessage(message)
			       .setCancelable(true)
			       .setTitle("Error")
				   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setNegativeButton("Close", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                return;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();			
		}
		else {
			SaveReportAsOutbox();
		}		
	}

	
	// Stage 1 verify input data
	// Stage 2 save a copy to local
	// Stage 3 Upload
	public void Send() {
		if (isLogin() == false){
			String msg = "You are working on off line.";
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			Log.e("Error", msg);
			return;
		}
		if (app.getWebServerId() == -1){
			String msg = "Web server name is not selected.";
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			Log.e("Error", msg);
			return;
		}
		
		// Stage 1 verify input data
		String message = verifyData();
		if (message.isEmpty() == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
			builder.setMessage(message)
			       .setCancelable(true)
			       .setTitle("Error")
				   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setNegativeButton("Close", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                return;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();			
		}
		else {
			Patient p = saveReportAsOutbox(); // 9.0.3
			new sendReportAsyncTask(p).execute();
		}
	}

    /*
    public boolean isLogin(Context c){
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
    		return false;
    	}
    	return true;
    }
*/
    public boolean isLogin(){
        boolean result = false;
        String u = app.getUsername();
        String p = app.getPassword();
        String t = app.getToken();
        boolean a = app.getAuthStatus();

        if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST) || t.isEmpty() == true){
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

	public ReportResult sendReport(Patient p){
        ReportResult reportResult = new ReportResult();
		/*
		Intent report = new Intent(ReportActivity.this, Webservice.class);
		report.putExtra("patient", true);
		report.putExtra("patientId", p.rowIndex);
		startService(report);
		*/
		TriagePic app = ((TriagePic)this.getApplication());
		long webServerId = app.getWebServerId();
		
		DataSource s = new DataSource(this, app.getSeed());
		WebServer ws = new WebServer();
		s.open();
		ws = s.getWebServerFromId(webServerId);
		s.close();

        if (ws == null){
            String msg = "Web server is not found in database.";
            reportResult.setErrorCode(ReportResult.MY_ERROR_CODE);
            reportResult.setErrorMessage(msg);
            Toast.makeText(this, reportResult.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
            return reportResult;
        }

        if (isLogin() == false){
            reportResult.setErrorCode(ReportResult.MY_ERROR_CODE);
            reportResult.setErrorMessage("You are in off line mode.");
            Toast.makeText(this, reportResult.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
            return reportResult;
        }

		if (!(p.getUuid() == null || p.getUuid().isEmpty())) {
			reportResult = ws.callReportDeleteImage(p.rowIndex, this, app.getToken(), app.getSeed());
		}
		if (reportResult.getErrorCode().isEmpty() || reportResult.getErrorCode().contentEquals("0")) {
			reportResult = ws.callReport(p.rowIndex, this, app.getToken(), app.getSeed());
		}

        return  reportResult;
    }
	
	//To use the AsyncTask, it must be subclassed  
    private class sendReportAsyncTask extends AsyncTask<Void, Integer, Void>  
    {
        ReportResult reportResult;
    	private Patient patient;
    	public sendReportAsyncTask(Patient p) {
            super();
            patient = p;
        }
    	
    	//Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
            progressDialog = new ProgressDialog(ReportActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Sending report, please wait...");  
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
                reportResult = sendReport(patient);
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

            if (reportResult.getErrorCode().equalsIgnoreCase("0")) {
                Toast.makeText(ReportActivity.this, "Report is sent.", Toast.LENGTH_SHORT).show();
                ReportActivity.this.finish();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setMessage(reportResult.getErrorMessage().toString() + "\nReport is saved to Outbox.") // message is changed in 9.0.3
                        .setCancelable(true)
                        .setTitle("Error")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                ReportActivity.this.finish();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

	public Patient saveReportAsOutbox() {
		Toast.makeText(this, "Sending Report", Toast.LENGTH_SHORT).show();
		Patient p = getPatient();
		p.boxId = Patient.OUTBOX;
		DataSource s = new DataSource(this, app.getSeed());
		s.open();
		if (this.editPatientOriginal != null) {
			p.rowIndex = this.editPatientOriginal.rowIndex;
			s.updatePatient(p);
		} 
		else{
            p = s.createPatientPlus(p);
            this.editPatientOriginal = p; // 9.0.3

            // Image
            for (int i = 0; i < p.images.size(); i++){
                Image img = images.get(i);
                img.setPid(p.getPid());
                img.setSquence(i);
                img = s.createImage(img);
                if (img == null) {
                    Log.e("Error", "Failed to create record in image table.");
                }
            }
		}
		s.close();
		return p;
	}

	/**
	 * check all fields and values to instantiate a patient.
	 * 
	 * @return
	 */
	private Patient getPatient() {
		long pp;
		long sp;
		
		String prefixPid = textViewPrefixPid.getText().toString();
//		if (prefixPid.isEmpty()){
//			pp = 0;
//		}
//		else {
//			pp = Long.parseLong(prefixPid);
//		}
		
		String pid = textViewPid.getText().toString();
		if (pid.isEmpty()){
			sp = 0;
		}
		else {
			sp = Long.parseLong(pid);	
		}
		
		Age a = age;
		Gender g = gender;
		Patient p = new Patient(
//				Long.parseLong(((EditText) findViewById(R.id.patient_id_field)).getText().toString()),
                prefixPid.toString(), // not in use
				sp, //
				textViewPid.getText().toString(),
				etLastName.getText().toString(),
				etFirstName.getText().toString(), 
				age, 
				gender, 
//				zone, 
				myZone, 
				eventCurSel, 
				hospitalCurSel,
				new Date().getTime(),
				/* send status 'sending' */9991,
				images
				);
		if (editPatientOriginal != null)
			p.uuid = editPatientOriginal.uuid;
		
		// Add comments
		p.comments = etComments.getText().toString();
//        p.comments = "";

		return p;
	}

	private String verifyData() {
		String msg = "";	
		
		// Event
		if (eventCurSel.isEmpty() == true){
			msg = "Event is not selected.";
			return msg;			
		}
		// Zone
		int pos = spZoneSel.getSelectedItemPosition();
//		zone = Zone.getZone(spZoneSel.getSelectedItemId());
		myZone = Patient.MyZone.getZone(pos);
		if (myZone == Patient.MyZone.UNASSIGNED){
			msg = "Zone is not selected.";
			return msg;
		}

		// pid
		String prefixPid = textViewPrefixPid.getText().toString();
		if (prefixPid.isEmpty() && h.pidPrefix.isEmpty() == false){
			msg = "Prefix of patient ID is empty.";
			return msg;
		}
		
		String pid = textViewPid.getText().toString();
		if (pid.isEmpty() == true){
			msg = "Patient ID is empty.";
			return msg;			
		}

		/*
		// Name
		String lastName = etLastName.getText().toString();
		if (lastName.isEmpty() == true){
			msg = "Last name is empty.";
			return msg;
		}
		
		String firstName = etFirstName.getText().toString();
		if (firstName.isEmpty() == true){
			msg = "First name is empty.";
			return msg;			
		}
		*/
		
		// Age
        if (ckAdult.isChecked() == false && ckChild.isChecked() == false){
            age = Age.UNKNOWN;
            msg = "Age is not selected.";
            return msg;
        }
        if (ckAdult.isChecked() == true){
            age = Age.ADULT;
        }
        else {
            age = Age.PEDIATRIC;
        }

        // gender
		if (ckMale.isChecked() == false && ckFemale.isChecked() == false){
			msg = "Gender is not selected.";
            age = Age.UNKNOWN;
			return msg;
		}
        if (ckMale.isChecked() == true){
            gender = Gender.MALE;
        }
        else {
            gender = Gender.FEMALE;
        }

		return msg;
	}

	private void SelectZone() {
		Intent i = new Intent(ReportActivity.this, ZoneActivity.class);
		startActivity(i);
	}

	private void SelectGender() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		builder.setTitle("Gender selection");
		
		builder.setCancelable(false);
		
		builder.setSingleChoiceItems(genderItems, nGenderItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item){
				nCurSelGenderItem = item;
				strCurSelGender = (String) genderItems[nCurSelGenderItem];
			}				
		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nGenderItem = nCurSelGenderItem;
				strGender = strCurSelGender;
				tvCurSelGender.setText(strGender);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.cancel();
				
			}
		});
		AlertDialog alert = builder.create();		
		alert.show();		
	}

	private void SelectAge() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		builder.setTitle("Age selection");
		
		builder.setCancelable(false);
		
		builder.setSingleChoiceItems(ageItems, nAgeItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item){
				nCurSelAgeItem = item;
				strCurSelAge = (String) ageItems[nCurSelAgeItem];
			}				
		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nAgeItem = nCurSelAgeItem;
				strAge = strCurSelAge;
				tvCurSelAge.setText(strAge);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.cancel();
				
			}
		});
		AlertDialog alert = builder.create();		
		alert.show();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_EVENT_REQUEST){
            String name = data.getExtras().getString("eventname").trim();
			if (name.isEmpty() == false){
                eventCurSel = name;
				tvEvent.setText(eventCurSel);
                app.setCurSelEvent(name);
				
        		// Write to shared preference
                ReportActivity.this.getSharedPreferences("Info", 0).edit().putString("event", eventCurSel).commit();
			}
            String nameShortName = data.getExtras().getString("eventnameShortName").trim();
            if (nameShortName.isEmpty() == false){
                eventCurSelShortName = nameShortName;
                app.setCurSelEventShortName(nameShortName);
//                tvEvent.setText(eventCurSel);

                // Write to shared preference
                ReportActivity.this.getSharedPreferences("Info", 0).edit().putString("eventShortName", eventCurSelShortName).commit();
            }
		}
        else if (requestCode == PICK_HOSPITAL_REQUEST){
			String name = data.getExtras().getString("hospitalname").trim();
			if (name.isEmpty() == false){
				hospitalCurSel = name;
				tvHospital.setText(hospitalCurSel);

                // get hospital patient ID prefix
                // added in version 9.0.0
                DataSource s = new DataSource(this, app.getSeed());
                s.open();
                h = s.getHospital(hospitalCurSel);
                s.close();
                if (h != null){
                    if (h.pidPrefix.isEmpty()){
                        textViewPrefixPid.setText("");
                    }
                    else {
                        textViewPrefixPid.setText(h.pidPrefix);
                    }
                }

                clearPatientIDs(); // clean the reserved pids as hospital is changed.

                // Write to shared preference
				ReportActivity.this.getSharedPreferences("Info", 0).edit().putString("hospital", hospitalCurSel).commit();
			}
		}
		else if (requestCode == TAKE_PHOTO_REQUEST){
			if (resultCode == 0) {// Cancel
				return;
			}
			
			Bundle extras = data.getExtras();
			bmpPhoto = (Bitmap)extras.get("data");

			// Detect the face.
			if (bmpPhoto == null){
				return;
			}
			
			ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
		}
		else if (requestCode == ADDRESS_REQUEST){
		}
		else if (requestCode == QUERY_MEDIA){
			if (resultCode == 0) {// Cancel
				return;
			}
		}
		else if (requestCode == ReportPatientImageHandler.IMAGE_CAPTURE) { // Camera
			if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(ReportActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();			
				return;
			}
			
			Image image = new Image();
			image = camera.onActivityResult(requestCode, resultCode, app.getCameraIntent());

            // changes made in version 9.0.0
            image.resizeImageFile();
			image.DetectFaces();

			if (images == null) {
				images = new ArrayList<Image>();
				image.setSquence(curImageIndex + 1);
				images.add(image);
			} 
			else{
				image.setSquence(0);
				images.add(image);
			}
			if (curImageIndex != 0){
				curImageIndex = images.size() - 1;
			}
			curImageIndex = images.size() - 1;

			setMyImage();
			
			Toast.makeText(ReportActivity.this, "Photo is taken and added.", Toast.LENGTH_SHORT).show();	
		}
		else if (requestCode == ReportPatientImageHandler.IMAGE_FIND_EX){ // get image back from device galleryif 
			if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(ReportActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();			
				return;
			}

			Image image = new Image();
			image = camera.onActivityResult(requestCode, resultCode, data);

            // changes made in version 9.0.0
			image.DetectFaces();

			if (images == null) {
				images = new ArrayList<Image>();
				image.setSquence(curImageIndex + 1);
				images.add(image);
			} 
			else{
				image.setSquence(0);
				images.add(image);
			}
			if (curImageIndex != 0){
				curImageIndex = images.size() - 1;
			}
			curImageIndex = images.size() - 1;
			setMyImage();

//			findFaces();

			Toast.makeText(ReportActivity.this, "Image \"" + image.getFileName() + "\" is added.", Toast.LENGTH_SHORT).show();
		}
		else if (requestCode == ReportPatientImageHandler.IMAGE_PRIV_GAL){ // get image back from triagepic gallery
			if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(ReportActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();			
				return;
			}

            Image image = new Image();
			image = camera.onActivityResult(requestCode, resultCode, data);

            // changes made in version 9.0.0
//            Bitmap bm = ReportPatientImageHandler.resizedBitmap(image.getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, ReportActivity.this, true);
//			image.setBitmap(bm);
			image.DetectFaces();
//			image.createBitmapWithBoundingBox();
			
			if (images == null) {
				images = new ArrayList<Image>();
				image.setSquence(curImageIndex + 1);
				images.add(image);
			} 
			else{
				image.setSquence(0);
				images.add(image);
			}
			if (curImageIndex != 0){
				curImageIndex = images.size() - 1;
			}
			
			curImageIndex = images.size() - 1;
			setMyImage();

			Toast.makeText(ReportActivity.this, "Image \"" + image.getFileName() + "\" is added.", Toast.LENGTH_SHORT).show();
		}
		else if (requestCode == CROP_IMAGE){
            processCroppedImage(requestCode,  resultCode, data);
	    }
        else if (requestCode == BARCODE_SCANNER){
            if (resultCode == RESULT_OK){
                String contents = data.getStringExtra("SCAN_RESULT");
//                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                if (contents.isEmpty() == false){
                    if (contents.startsWith("MD") == true){
                        String str = contents.substring(2);
                        textViewPid.setText(str);
                    }
                    textViewPid2.setText(contents.toString());
                    textViewPid2Auto.setText(textViewPid2.getText());
                }
            }
            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
	}

    public void processCroppedImage(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED){
            String errorMsg = "This photo cannot be cropped.";
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
            builder.setMessage(errorMsg)
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
        else {
            // image is cropped successfully
            Bundle extras = data.getExtras();
			Bitmap bmp = extras.getParcelable("data");

			// Create new file name
			File fileNew = null;

			// if this is very first time, create the file twice
			File dir = this.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
			if (dir.listFiles().length == 0){
				fileNew = Image.createTriagePicImagefile(ReportActivity.this);

				// Save bmp file to image
				try {
					Image.saveBitmapToFile(bmp, fileNew);
				}
				catch (IOException e){
					Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			fileNew = Image.createTriagePicImagefile(ReportActivity.this);

			// Save bmp file to image
			try {
				Image.saveBitmapToFile(bmp, fileNew);
			}
			catch (IOException e){
				Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}

			if (!fileNew.exists()){
				String errorMsg = "Failed to create the image file.";
				AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
				builder.setMessage(errorMsg)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setCancelable(true)
						.setTitle("Warning")
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								return;
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
			else {
				Toast.makeText(this, "Cropped image is saved!", Toast.LENGTH_SHORT).show();

				AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
				builder.setMessage("The cropped image is saved successfully. \nDo you want to add the cropped image in now, or later?")
						.setIcon(android.R.drawable.ic_menu_crop)
						.setCancelable(true)
						.setTitle("Question")
						.setNegativeButton("Later", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.setPositiveButton("Now", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent i = new Intent(ReportActivity.this, ImageGallery.class);
								startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}

	public String CreateNewImage(String path) {
		File dir = this.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
		File internalFile = new File(dir, System.currentTimeMillis() + ".jpg");
//        File dir = new File(MediaStore.EXTRA_OUTPUT);
//        File internalFile = new File(dir, System.currentTimeMillis() + ".jpg");

		InputStream in;
		try {
			in = this.getContentResolver().openInputStream(Uri.parse(path));
			OutputStream out = new FileOutputStream(internalFile);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1)
				out.write(buffer, 0, read);
			in.close();
			out.flush();
			out.close();
			this.getContentResolver().delete(Uri.parse(path), null, null);
			return internalFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return path;
	}
	
	/**
	 * set image based on curren image index, or else display the defualt image.
	 * Display (0/0) if no images, or else the current image out of however many
	 * images their are
	 */
	public void setImage() {
		if (images.size() > 0) {
			Image i = images.get(curImageIndex);

            // replace the following line in version 9.0.0
            Bitmap b = i.getBitmap();
            if (b == null){
                String e = i.getEncoded();
                if (e.isEmpty() == true){
                    Toast.makeText(this, "Missing encoded data!", Toast.LENGTH_SHORT).show();
                }
                else {
                    b = Patient.decodeStringToBitmap(e);
                    i.setBitmap(b);
                }
            }
            if (i.getBitmap() == null){
                Log.e("Error in SetMyImage()", "Failed to create bitmap file.");
                return;
            }

            ivPhoto.setImageBitmap(b);
//            ivPhoto.setImageDrawable(new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(i.getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, ReportActivity.this, false)));
						
//			((TextView) findViewById(R.id.image_index)).setText("("
//					+ (this.curImageIndex + 1) + "/" + images.size() + ")");
			((TextView) findViewById(R.id.image_index)).setText((this.curImageIndex + 1) + "/" + images.size());			
		} else {
			ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
			// R.drawable.defaultpatient is replaced by R.drawable.questionhead
			
//			((TextView) findViewById(R.id.image_index)).setText("(0/0)");
			((TextView) findViewById(R.id.image_index)).setText("0/0");
		}
	}	
	
	public void setMyImage() {
		if (images.size() > 0) {
			Image i = images.get(curImageIndex);

            Bitmap tmp = Bitmap.createBitmap(i.getBitmap(), 1, 1, i.getBitmap().getWidth()-1, i.getBitmap().getHeight()-1);
            Canvas canvas = new Canvas(tmp);
            canvas.drawBitmap(tmp, 0,0, null);

			if (i.getNumberOfFacesDetected() <= 0){
				ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), i.getBitmap()));
				tvCapition.setText("");
			}
			else {
				Paint facePaint = new Paint();
		        facePaint.setColor(Color.GREEN);
		        facePaint.setStyle(Paint.Style.STROKE);
		        facePaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

		        Rect r = i.getRect();
	        	canvas.drawRect(
	        			r.getX(), 
	        			r.getY(), 
	        			r.getX() + r.getW(), 
	        			r.getY() + r.getH(),
	        			facePaint
	        			);
	        	
//				ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
				tvCapition.setText(i.getCaption());
			}

            // Draw frame to tell primary or secondary photo
            Paint primaryPaint = new Paint();
            primaryPaint.setColor(Color.WHITE);
            primaryPaint.setStyle(Paint.Style.STROKE);
            primaryPaint.setStrokeWidth(1);

//            primaryPaint.setStyle(Paint.Style.STROKE);
//            canvas.drawCircle(10, 10, 5, primaryPaint);

            int size = 24;
            primaryPaint.setTextSize(size);
            String tx;
            if (this.curImageIndex == 0){
                tx = "Primary image";
            }
            else {
                tx = "Secondary image";
            }
            canvas.drawText(tx, 0, size, primaryPaint);
            ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
            tvPrimaryPhoto.setText(tx);

			((TextView) findViewById(R.id.image_index)).setText((this.curImageIndex + 1) + "/" + images.size());			
			tvCapition.setText(i.getCaption());
		} else {
			ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
			((TextView) findViewById(R.id.image_index)).setText("0/0");
            tvPrimaryPhoto.setText("No image");
			tvCapition.setText("");
		}
	}	

	public void setMyImage(int curSel) {
		if (images.size() > 0) {
			Image i = images.get(curSel);
			if (i.getNumberOfFacesDetected() <= 0){
				ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), i.getBitmap()));
				tvCapition.setText("");
			}
			else {
//				i.DrawCanvas();
				
				Bitmap tmp = Bitmap.createBitmap(i.getBitmap(), 1, 1, i.getBitmap().getWidth()-1, i.getBitmap().getHeight()-1);
				Canvas canvas = new Canvas(tmp);
				canvas.drawBitmap(tmp, 0,0, null);
				Paint myPaint = new Paint();
		        myPaint.setColor(Color.GREEN);
		        myPaint.setStyle(Paint.Style.STROKE); 
		        myPaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

		        Rect r = i.getRect();
	        	canvas.drawRect(
	        			r.getX(), 
	        			r.getY(), 
	        			r.getX() + r.getW(), 
	        			r.getY() + r.getH(),
	        			myPaint
	        			);
	        	
				ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
			}
			((TextView) findViewById(R.id.image_index)).setText((this.curImageIndex + 1) + "/" + images.size());			
			tvCapition.setText(i.getCaption());
		} else {
			ivPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
			((TextView) findViewById(R.id.image_index)).setText("0/0");
			tvCapition.setText("");
		}
	}	

	/**
	 * Give user the option to select an image from the device's main gallery,
	 * TriagePics private gallery, or to capture a new image from the camera and
	 * start a new activity for result accordingly
	 * 
	 * @return
	 */
	protected Dialog addPhoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		return builder
				.setMessage("Select image from")
				.setCancelable(true)
				.setPositiveButton("External Gallery",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Uri u = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
								Intent i = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
										);
								startActivityForResult(i, ReportPatientImageHandler.IMAGE_FIND_EX);
							}
						})
				.setNeutralButton("TriagePic Gallery",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startActivityForResult(
										new Intent(
												ReportActivity.this,
												ImageGallery.class
												),
										ReportPatientImageHandler.IMAGE_PRIV_GAL);
							}
						}).show();
//				.setNegativeButton("Capture Image with Camera",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								Intent i = camera.startCamera();
//								startActivityForResult(i, ReportPatientImageHandler.IMAGE_CAPTURE);
//							}
//						}).create();
	}
	
	protected void addPhotoFromGallery() {
		Intent i = new Intent
				(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				);
		startActivityForResult(i, ReportPatientImageHandler.IMAGE_FIND_EX);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_menu, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.itemHome:
        		GoHome();
        		break;
//        	case R.id.itemAddExample:     
//        		AddExample();
//        		break;
//        	case R.id.itemAdd100Examples:
//        		Add100Examples();
//        		break;
        	case R.id.itemSetWebServer:     
        		SetWebServer();
        		break;
            case R.id.itemSetHospitalInfo:
            	SelectHospital();
            	break;
            case R.id.itemSetEventInfo:
            	SelectEvent();
            	break;
            case R.id.itemLatency:
            	testLatency();
            	break;
//            case R.id.itemBarcodeScanner:
//                launchBarcodeScanner();
//                break;
            case R.id.itemRandomMaleName:    // added in version 8.0.5
                getRandomMaleName();
                break;
            case R.id.itemRandomFemaleName:    // added in version 8.0.5
                getRandomFemaleName();
                break;
//            case R.id.itemEnterAPatientID:
//                enterAPatientID();
//                break;
            default:
                break;

        }
        return true;
    }

    // added in version 8.0.5
    private void getRandomMaleName() {
        PopularNames pn = new PopularNames();
        String firstName = pn.getRandomBoyName();
        String lastName = pn.getRandomLastName();

        etLastName.setText(lastName);
        etFirstName.setText(firstName);
        ckMale.setChecked(true);
        ckFemale.setChecked(false);
    }

    // added in version 8.0.5
    private void getRandomFemaleName() {
        PopularNames pn = new PopularNames();
        String firstName = pn.getRandomGirlName();
        String lastName = pn.getRandomLastName();

        etLastName.setText(lastName);
        etFirstName.setText(firstName);
        ckMale.setChecked(false);
        ckFemale.setChecked(true);
    }

    private void launchBarcodeScanner() {
        // add check if scan app is installed. V 7.0.2
        try{
            Intent i = new Intent("com.google.zxing.client.android.SCAN");
            if (isCallable(i) == true){
                i.putExtra("SCAN_MODE", "ONE_D_MODE");
                startActivityForResult(i, BARCODE_SCANNER);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setMessage("ZXing barcode scanner is not installed on your device. You won't be able to scan the barcode. Please call this function again after installation.")
                        .setCancelable(true)
                        .setTitle("Do you want to install barcode scanner now?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                return;
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
                                startActivity(i);
                                dialog.dismiss();
                                finish();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Barcode scanner is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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
    
	private void SetWebServer() {
		Intent i = new Intent(ReportActivity.this, WebServerActivity.class);
		startActivity(i);
	}    
    
	private void Add100Examples() {
		addFakeRecord(100);
		addToSentBox();
	}
	
	private void addToSentBox() {
		DataSource s = new DataSource(this, app.getSeed());
		s.open();

		for (int i = 0; i < patients.size(); i++){
			Patient p = patients.get(i);
            p = s.createPatientPlus(p);
        }

		s.close();
	}

	private void AddExample() {
		addFakeRecord(1);
		addToSentBox();
		updateUi();
	}

	private void GoHome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setMessage("You are about to leave this page. You may lost your work.")
                .setCancelable(true)
                .setTitle("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(ReportActivity.this, HomeActivity.class);
                        startActivity(i);
                        ReportActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	/**
	 * generates test data to insert in the queue
	 * 
	 * @param i
	 *            the number of records to insert
	 */
	private void addFakeRecord(int i) {
		for (int x = 0; x < i; x++) {
			Patient p = new Patient(this);
			p.patientId = fakeID;
			fakeID++;
			p.rowIndex = -fakeID;
			patients.add(p);
		}
	}
	/**
	 * set defualt values based on patient being edited.
	 */
	private void updateUi() {
		int size = patients.size();
		if (size == 0){
			return;
		}
		Patient p =	patients.get(size - 1);
		
		etLastName.setText(p.lastName);
		etFirstName.setText(p.firstName);
/*		
		if (p.age.equals(Age.ADULT)){
			rbAdult.setChecked(true);
		}
		else if (p.age.equals(Age.PEDIATRIC)){
			rbChild.setChecked(true);
		}

		if (p.gender.equals(Gender.MALE)){
			rbMale.setChecked(true);
		}
		else if (p.gender.equals(Gender.FEMALE)){
			rbFemale.setChecked(true);
		}
*/		
//		etPatientIdMain.setText(String.valueOf(p.patientId));// not in use
		
		ivPhoto.setImageDrawable(p.photo);
		
	
/*		
//		images = editPatientOriginal.images;
		((TextView) findViewById(R.id.patient_id_prefix)).setText(h.pIDPrefix);
		if (!gender.equals(Gender.UNKNOWN))
			((CheckBox) findViewById(gender.i)).setChecked(true);
		((CheckBox) findViewById(age.i)).setChecked(true);
		((CheckBox) findViewById(zone.i)).setChecked(true);
		((EditText) findViewById(R.id.patient_id_field)).setText(String
				.valueOf(pid));
*/
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// index for current images
		outState.putInt("curImageIndex", curImageIndex);
		
		// Images
		outState.putInt("size", images.size());
		for (int i = 0; i < images.size(); i++){
			Image img = images.get(i);
			outState.putString("caption:" + String.valueOf(i), img.getCaption());
			outState.putString("uri:" + String.valueOf(i), img.getUri());
			outState.putString("digest:" + String.valueOf(i), img.getDigest());
			outState.putString("size:" + String.valueOf(i), img.getSize());
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			img.getBitmap().compress(Bitmap.CompressFormat.PNG, 50, stream);
			byte[] bitmapbyteArray = stream.toByteArray();
			outState.putByteArray("bitmap:" + String.valueOf(i), bitmapbyteArray);
			
			outState.putInt("numberOfFacesDetected:" + String.valueOf(i), img.getNumberOfFacesDetected());
			for (int j = 0; j < img.getNumberOfFacesDetected(); j++){
				Image.Rect r = img.getRect();
				outState.putFloat("rect_x:" + String.valueOf(i) + String.valueOf(j), r.getX());
				outState.putFloat("rect_y:" + String.valueOf(i) + String.valueOf(j), r.getY());
				outState.putFloat("rect_h:" + String.valueOf(i) + String.valueOf(j), r.getH());
				outState.putFloat("rect_w:" + String.valueOf(i) + String.valueOf(j), r.getW());
			}
		}
		
		outState.putString("firstName",	etFirstName.getText().toString());
		outState.putString("lastName", etLastName.getText().toString());
		outState.putString("pid", textViewPid.getText().toString());
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// index for current images
		curImageIndex = savedInstanceState.getInt("curImageIndex");
		
		// Images
		int size = savedInstanceState.getInt("size", 0);
		for (int i = 0; i < size; i++){
			Image img = new Image();
			img.setCaption(savedInstanceState.getString("caption:" + String.valueOf(i), ""));
			img.setUri(savedInstanceState.getString("uri:" + String.valueOf(i), ""));
			img.setDigest(savedInstanceState.getString("digest:" + String.valueOf(i), ""));
			img.setSize(savedInstanceState.getString("size:" + String.valueOf(i), ""));
			
			byte[] byteArray1 = savedInstanceState.getByteArray("bitmap:" + String.valueOf(i));
			img.setBitmap(BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length));

			img.setNumberOfFacesDetected(savedInstanceState.getInt("numberOfFacesDetected:" + String.valueOf(i)));
			for (int j = 0; j < img.getNumberOfFacesDetected(); j++){
				float x = savedInstanceState.getFloat("rect_x:" + String.valueOf(i) + String.valueOf(j));
				float y = savedInstanceState.getFloat("rect_y:" + String.valueOf(i) + String.valueOf(j));
				float h = savedInstanceState.getFloat("rect_h:" + String.valueOf(i) + String.valueOf(j));
				float w = savedInstanceState.getFloat("rect_w:" + String.valueOf(i) + String.valueOf(j));
				img.getRect().setX(x);
				img.getRect().setY(y);
				img.getRect().setH(h);
				img.getRect().setW(w);
			}
			img.setSquence(i);
			images.add(img);
		}
		
		etFirstName.setText(savedInstanceState.getString("firstName"));
		etLastName.setText(savedInstanceState.getString("lastName"));
		textViewPid.setText(savedInstanceState.getString("pid"));

		setMyImage(curImageIndex);
		
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	public void onBackPressed() {
        super.onBackPressed();
/*
        String msg = "Pressing back key to go back home page will lose your work. Please confirm again.";
        msg += "\nSuggestion: ";
        msg += "\nYou may select \"Drafts\" button to save your work and return.";
  	   AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		builder.setMessage(msg)
		       .setCancelable(true)
		       .setTitle("Warning")
			   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   ReportActivity.this.onBackPressed();
			        	   ReportActivity.this.finish();
			           }
			       })
		       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();		                
		                return;
		           }
		       });
		AlertDialog alert = builder.create();		
		alert.show();

		*/
	}

	private void unlockScreen() {
	  	   AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
			builder.setMessage("Username password")
			       .setCancelable(true)
			       .setTitle("Unlock Screen")
				   .setIcon(android.R.drawable.ic_dialog_alert)
				       .setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   if (verifyPassword("password"))
				        	   {
				        		   return;
				        	   }
				        	   else {
				        		   Toast.makeText(ReportActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
				        		   unlockScreen();
				        	   }
				           }
				       })
			       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.cancel();	
			        	   return;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
	}

	private boolean verifyPassword(String password) {
		if (password.equalsIgnoreCase(app.getPassword())){
			return true;
		}
		return false;
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

    public void encryptImages(){
        if (app.getIsImageEncrypted() == false){
            long start, end, elapse;
            start = System.currentTimeMillis();
            Image img = new Image();
            img.EncryptAllImages(this, app.getSeed());
            end = System.currentTimeMillis();
            elapse = end - start;
            String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
            Log.i("encryptImages", msg);
            app.setIsImageEncrypted(true);
        }
    }

    public void decryptImages(){
        if (app.getIsImageEncrypted() == true){
            long start, end, elapse;
            start = System.currentTimeMillis();
            Image img = new Image();
            img.DecryptAllImages(this, app.getSeed());
            end = System.currentTimeMillis();
            elapse = end - start;
            String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
            Log.i("decryptImages", msg);
            app.setIsImageEncrypted(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        /*
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn){
            this.finish();
        }
*/
        super.onStop();
    }

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	/**
	 * Permissions
	 * starting from SDK 23, need to enable permission in running time.
	 * two permissions are needed:
	 * fine location
	 * external
	 *
	 * version 9.0.6-beta
	 * code 9000601
	 */

	public class checkPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
		private boolean permission = false;
		private int func;
		private Context c;

		ProgressDialog progressDialog;

		checkPermissionAsyncTask(int func, Context c) {
			this.func = func;
			this.c = c;
		}

		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage("Checking permission...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			//Get the current thread's token
			synchronized (this) {
				permission = checkPermission(func, c);
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			//close the progress dialog
			if (permission == false) {
				new requestPermissionAsyncTask(func, c).execute();
			} else { // enabled
				TakePhoto();
			}
		}

		private boolean checkPermission(int func, Context c) {
			int result;
			result = c.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (result == PackageManager.PERMISSION_GRANTED) {
				return true;
			} else {
				return false;
			}
		}
	}

	public class requestPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
		private int func;
		private Context c;

		ProgressDialog progressDialog;

		requestPermissionAsyncTask(int func, Context c) {
			this.func = func;
			this.c = c;
		}

		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage("Requesting permission...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			//Get the current thread's token
			synchronized (this) {
				requestPermission(func);
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}

	}

	public void requestPermission(int func) {
		requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Permission is granted")
					.setCancelable(false)
					.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Permission is denied")
					.setCancelable(false)
					.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}