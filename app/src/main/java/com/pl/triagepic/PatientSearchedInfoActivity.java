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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.pl.triagepic.Result.AddCommentResult;
import com.pl.triagepic.Result.ReportAbuseResult;
import com.pl.triagepic.Result.ReportResult;
import com.pl.triagepic.Result.ReservePIDResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PatientSearchedInfoActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "PatientSearchedInfoActivity";
    Credential credential;

    private static final int MSG_INC = 0;
    private static final int MSG_DEC = 1;
    private static final int[] zones = { R.id.green, R.id.green_bh, R.id.gray,
            R.id.yellow, R.id.red, R.id.black };
    private static final int[] genders = { R.id.male, R.id.female };
    private static final int[] ages = { R.id.adult, R.id.peds };
//	private static final int[] incDec = { R.id.increment, R.id.decrement };

    private int curImageIndex = 0;
    private String prefixPid;
    private long pid;
    private long rowId;
    private Drawable pidBack;
    private Patient editPatientOriginal;
    private Patient patientCurSel;
    private Patient.Age age;
    private Patient.Gender gender;
    //	private Patient.Zone zone;
    private Patient.MyZone myZone;
    public ArrayList<Image> images = new ArrayList<Image>(); ;
    private Event e;
    private Hospital h;
    private String hospital, event;
    private ReportPatientImageHandler camera;
    private ScheduledExecutorService mUpdater;
    private static long rate = 1;
    private static int rateCheck = 0;
    private boolean bReadPatientTable = false;

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

    // add photo
    int curSelSource;

    static final int PICK_EVENT_REQUEST = 1;
    static final int TAKE_PHOTO_REQUEST = 2;
    static final int ADDRESS_REQUEST = 3;
    static final int QUERY_MEDIA = 4;
    static final int PICK_HOSPITAL_REQUEST = 5;
    static final int CROP_IMAGE = 6;
    static final int PATIENT_INFO = 7;
    static final int BARCODE_SCANNER = 8;
    static final int ADD_COMMENT = 9;

    TriagePic app;

    Intent i;

    private Uri uriRealCurSel;

    Button buttonAddComment;
    Button buttonReportAbuse;
//    Button buttonSuggestedStatus;
    Button buttonSuggestedLocation;
    Button buttonAddPhoto;

    TextView tvEvent;
    Button buEvent;
    String eventCurSel;

    Spinner spZoneSel;
    int nZoneCurSel;

    TextView tvHospital;
    Button buHospital;
    String hospitalCurSel;

    TextView textViewPrefixPid;
    TextView textViewPid;
    TextView textViewPid2;
    TextView textViewPid2Auto;
    Button buPlus;
    Button buMinus;
    Button buttonEnterNumber;
    Button buBarcodeScanner;
    Button buttonEnterNumberAuto;

    TextView tvLastName;
    TextView tvFirstName;

    ViewFlipper viewFlipperPidInput;
    RadioButton radioManual;
    RadioButton radioBarcode;
    RadioButton radioAuto;

    CheckBox ckMale;
    CheckBox ckFemale;

    CheckBox ckAdult;
    CheckBox ckChild;

    TextView textViewUuid;

    EditText etComments;

    Bitmap bmpPhoto;
    ImageView ivPhoto;
    TextView tvPrimaryPhoto;
    Button btMoveLeft;
    Button btMoveRight;

    TextView tvCapition;
    Button btCapition;

    private ProgressDialog progressDialog;


    /**
     * This handler is used with the increment and decrement buttons so they can
     * increment decrement faster and at higher powers of ten if held longer
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INC:
                    updatePID(true);
                    return;
                case MSG_DEC:
                    updatePID(false);
                    return;
            }
            super.handleMessage(msg);
        }

    };
    /**
     * The Reciever clears the page after a report is sent, it closes the page
     * if an edit is deleted, and it alters the Patient Id EditText Background
     * between normal and orange if the id is unused or used
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Sent"))
                freshPage();
            if (intent.getAction().equals("Deleted"))
                PatientSearchedInfoActivity.this.finish();
/*
			if (intent.getAction().equals("Check")) {
				if (!intent.getBooleanExtra("result", true))
					((EditText) findViewById(R.id.patient_id_field))
							.setBackgroundDrawable(pidBack);
				else
					((EditText) findViewById(R.id.patient_id_field))
							.setBackgroundColor(getResources().getColor(
									R.color.orange1));
			}
			*/
        }
    };

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
///////////////////
    /**
     * The orientation is first checked to determine which screen to load, the
     * actionbars background is then set and the camera opened. Then fields are
     * set according to whether this is an edit or a new record. the page then
     * has its listeners and screen set.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		int ort = ((WindowManager) this.getSystemService(WINDOW_SERVICE))
//				.getDefaultDisplay().getRotation();
/*
		if (ort == Surface.ROTATION_0 || ort == Surface.ROTATION_180)
			setContentView(R.layout.report_patient_landscape);
		else
			setContentView(R.layout.report_patient_portrait);
*/

//		getSupportActionBar().setBackgroundDrawable(
//				getResources().getDrawable(R.drawable.ab_background));

        setContentView(R.layout.patient_searched_info);

        app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        credential = new Credential(this);

        Initialize();
    }

    private void Initialize() {		
		/*
		 * Get the intent data
		 */
        rowId = this.getIntent().getIntExtra("rowId", 0);
        if (rowId == 0){
            Toast.makeText(this, "Failed to get ID.", Toast.LENGTH_SHORT).show();
            Log.e("Error", "Failed to get ID from previous activity.");
        }

        buttonAddComment = (Button) findViewById(R.id.buttonAddComment);
        buttonAddComment.setOnClickListener(this);

        buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        buttonAddPhoto.setOnClickListener(this);

        buttonSuggestedLocation = (Button) findViewById(R.id.buttonSuggestedLocation);
        buttonSuggestedLocation.setOnClickListener(this);

        buttonReportAbuse = (Button) findViewById(R.id.buttonReportAbuse);
        buttonReportAbuse.setOnClickListener(this);

        // UI
        tvEvent = (TextView) findViewById(R.id.textViewEvent);
        buEvent = (Button) findViewById(R.id.buttonSelectEvent);
        buEvent.setOnClickListener(this);

        tvHospital = (TextView) findViewById(R.id.textViewHospital);
        buHospital = (Button) findViewById(R.id.buttonSelectHospital);
        buHospital.setOnClickListener(this);

        buPlus = (Button) findViewById(R.id.buttonPlus);
        buPlus.setOnClickListener(this);
        buMinus = (Button) findViewById(R.id.buttonMinus);
        buMinus.setOnClickListener(this);
        buttonEnterNumber = (Button) findViewById(R.id.buttonEnterNumber);
        buttonEnterNumber.setOnClickListener(this);
        textViewPrefixPid = (TextView) findViewById(R.id.textViewPrefixPid);
        textViewPid = (TextView) findViewById(R.id.textViewPid);
        textViewPid2 = (TextView) findViewById(R.id.textViewPid2);

        // auto
        buttonEnterNumberAuto = (Button) findViewById(R.id.buttonEnterNumberAuto);
        buttonEnterNumberAuto.setOnClickListener(this);
        textViewPid2Auto = (TextView) findViewById(R.id.textViewPid2Auto);

        buBarcodeScanner = (Button) findViewById(R.id.buttonBarcodeScanner);
        buBarcodeScanner.setOnClickListener(this);

        tvLastName = (TextView) findViewById(R.id.last_name_field);
        tvFirstName = (TextView) findViewById(R.id.first_name_field);

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

        textViewUuid = (TextView) findViewById(R.id.textViewUuid);
        textViewUuid.setOnClickListener(this);

//		etComments = (EditText) findViewById(R.id.comments);

        camera = new ReportPatientImageHandler(this);

//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ArrayList<ItemZoneView> image_details = GetSearchResults();
        spZoneSel = (Spinner) findViewById(R.id.spinnerZoneSel);
        spZoneSel.setAdapter(new ItemZoneListBaseAdapter(PatientSearchedInfoActivity.this, image_details));
        spZoneSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                nZoneCurSel = arg2;
                myZone = Patient.MyZone.getZone(arg2);
                PatientSearchedInfoActivity.this.getSharedPreferences("Info", 0).edit().putInt("zone", myZone.i).commit();
//        		ReportActivity.this.getSharedPreferences("Info", 0).edit().putInt("zone", z.i).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spZoneSel.setEnabled(false); // for info only

        etComments = (EditText) findViewById(R.id.comments);
//        etComments.setFocusable(false);

        ivPhoto = (ImageView) findViewById(R.id.gallery);
        tvPrimaryPhoto = (TextView) findViewById(R.id.textViewPrimaryPhoto);

        btMoveLeft = (Button) findViewById(R.id.left_arrow_imageview);
        btMoveLeft.setOnClickListener(this);

        btMoveRight = (Button) findViewById(R.id.right_arrow_imageview);
        btMoveRight.setOnClickListener(this);

        btCapition = (Button) findViewById(R.id.buttonAddCapition);
        btCapition.setOnClickListener(this);

        tvCapition = (TextView) findViewById(R.id.textViewCapition);
		/*
		 * Set the value and screen
		 */
        patientCurSel = getPatientFromId(rowId);

        tvEvent.setText(patientCurSel.getEvent());
        tvHospital.setText(patientCurSel.getHospital());
//		tvId.setText(String.valueOf(patientCurSel.rowIndex));
        //???
//        etLastName.setText(patientCurSel.getLastName());
        tvLastName.setText(patientCurSel.getLastName());
        tvFirstName.setText(patientCurSel.getFirstName());

        nZoneCurSel = patientCurSel.myZone.i;
        myZone = Patient.MyZone.getZone(nZoneCurSel);
        spZoneSel.setSelection(myZone.i);

        if (rowId != 0){
            setFromPatient();
        }
        else{
//			freshPage();
            // display error message and return
        }

        setScreen();

        setMyImage();
    }

    public Patient getPatientFromId(long id){
        Patient p;
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        p = s.getPatientById(rowId);
        p.images = s.getAllImages(p.getPid());

        for (int i = 0; i < p.images.size(); i++){
            Image iP = p.images.get(i);
            Image iT = new Image();
            try{
                iT = s.getImage(p.getPid(), i);
            }
            catch (Exception e) {
                Log.e("Err: getPatientFromId()", "Failed to read image data for PID: " + String.valueOf(p.getPid()));
                s.close();
                return p;
            }

            if (iT == null){
                Log.e("Err: getPatientFromId()", "Failed to read image data for PID: " + String.valueOf(p.getPid()));
                s.close();
                return p;
            }

            iP.setId(iT.getId());
            iP.setSquence(iT.getSquence());
            iP.setNumberOfFacesDetected(iT.getNumberOfFacesDetected());
            iP.setRect(iT.getRect());

            if (iP.getBitmap() == null) {
                // changed in 9.0.0
                String encoded = iP.getEncoded();
                if (!encoded.isEmpty()) {
                    Bitmap bm = Patient.decodeStringToBitmap(iP.getEncoded());
                    iP.setBitmap(bm);
                }
//				Bitmap bm = ReportPatientImageHandler.resizedBitmap(iP.getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, true);
//				iP.setBitmap(bm);
            }
            if (iP.getBitmap() == null){
                Log.e("Error in SetMyImage()", "Failed to create bitmap file.");
            }

            p.images.set(i, iP);
        }
        s.close();
        return p;
    }

    private void SelectHospital() {
        Intent i = new Intent(PatientSearchedInfoActivity.this, HospitalListFragmentActivity.class);
        i.putExtra("hospitalname", tvHospital.getText().toString());
        startActivityForResult(i, PICK_HOSPITAL_REQUEST);
    }

    private void SelectEvent() {
        Intent i = new Intent(PatientSearchedInfoActivity.this, EventListFragmentActivity.class);
        i.putExtra("eventname", tvEvent.getText().toString());
        startActivityForResult(i, PICK_EVENT_REQUEST);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonAddComment:
                addComment();
                break;
            case R.id.buttonReportAbuse:
                reportAbuse();
                break;
            case R.id.buttonSuggestedLocation:
                addSuggestedLocation();
                break;
            case R.id.buttonAddPhoto:
                addSuggestedPhoto();
                break;
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
            case R.id.male:
                if (ckMale.isChecked()){
                    ckMale.setChecked(false);
                }
                else {
                    ckMale.setChecked(true);
                }
//                SelectMale();
                break;
            case R.id.female:
                if (ckFemale.isChecked()){
                    ckFemale.setChecked(false);
                }
                else {
                    ckFemale.setChecked(true);
                }
//                SelectFemale();
                break;
            case R.id.adult:
                if (ckAdult.isChecked()){
                    ckAdult.setChecked(false);
                }
                else {
                    ckAdult.setChecked(true);
                }
//                SelectAdult();
                break;
            case R.id.peds:
                if (ckChild.isChecked()){
                    ckChild.setChecked(false);
                }
                else {
                    ckChild.setChecked(true);
                }
//                SelectChild();
                break;
//		case R.id.spinnerEventSel:
//			spEventSel.requestFocus();
//			break;
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
            case R.id.textViewUuid:
                GoLpWebForPuuid(textViewUuid.getText().toString());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            progressDialog = new ProgressDialog(PatientSearchedInfoActivity.this);
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
                Toast.makeText(PatientSearchedInfoActivity.this, "Patient IDs are reserved.", Toast.LENGTH_SHORT).show();

                String newPid = app.getReservePIDs().get(0);
                String tokens[] =  parsePID(newPid);
                enterPIDFull(tokens[0], tokens[1]);
                app.getReservePIDs().remove(0);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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



    private void addComment() {
        // follows are old codes
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle("Add Comment");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String comment = input.getText().toString().trim();
                Toast.makeText(getApplicationContext(), comment, Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                new callAddCommentAsyncTask().execute(comment, "", "", "");
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void addSuggestedLocation() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle("Suggested Location");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String suggestedLocation = input.getText().toString().trim();
//                String suggestedLocation = "{\"street1\":\"123 old georgetown road\",\"street2\":\"apartment #6\",\"neighborhood\":\"pooks hill\",\"city\":\"bethesda\",\"region\":\"maryland\",\"postal_code\":\"20814\",\"country\":\"USA\",\"gps\":{\"latitude\":\"38.9035\",\"longitude\":\"-77.0231\"}}";
                Toast.makeText(getApplicationContext(), suggestedLocation, Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                new callAddCommentAsyncTask().execute("", "", suggestedLocation, "");
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void addSuggestedPhoto() {
        curSelSource = 0;
        final String source[] = {"Camera", "Photo Gallery"};
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Select Suggested Zone");
        alert.setSingleChoiceItems(source, curSelSource, new DialogInterface.OnClickListener() {
            public int curSel;
            public void onClick(DialogInterface dialog, int item) {
                curSelSource = item;
            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(PatientSearchedInfoActivity.this, source[curSelSource].toString() + " is selected", Toast.LENGTH_SHORT).show();
                if (curSelSource == 0) {
                    i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i, TAKE_PHOTO_REQUEST);
                }
                else {
                    i = new Intent(PatientSearchedInfoActivity.this, QueryMediaActivity.class);
                    startActivityForResult(i, QUERY_MEDIA);
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private class callAddCommentAsyncTask extends AsyncTask<String, Integer, Void>
    {
        AddCommentResult addCommentResult;
        WebServer ws;
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            addCommentResult = new AddCommentResult();
            ws = new WebServer();
            ws.setToken(app.getToken());

            progressDialog = new ProgressDialog(PatientSearchedInfoActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Adding comment, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(String... comment)
        {
            //Get the current thread's token
            synchronized (this)
            {
                addCommentResult = ws.callAddComment(app.getToken(), patientCurSel.uuid, comment[0], comment[1], comment[2], comment[3]);
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
            String message = "";
            if (addCommentResult.getErrorCode().equalsIgnoreCase("0") == false) {
                message = "Error: failed to send the report. \nError Code: " + addCommentResult.getErrorCode().toString() + "\n, Error Message: " + addCommentResult.getErrorMessage().toString();
            }
            else {
                message = "Your information was sent successfully.";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
            builder.setMessage(message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
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

    private void reportAbuse() {
         final AlertDialog.Builder alert = new AlertDialog.Builder(this);
         final EditText input = new EditText(this);
         alert.setView(input);
         alert.setIcon(android.R.drawable.ic_dialog_alert);
         alert.setTitle("Report Abuse");
         alert.setMessage("Explanation:");
         alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
                 String explanation = input.getText().toString().trim();
                 Toast.makeText(getApplicationContext(), explanation, Toast.LENGTH_SHORT).show();
                 InputMethodManager imm = (InputMethodManager)getSystemService(
                         Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                 new callReportAbuseAsyncTask().execute(explanation);
             }
         });
         alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
                 InputMethodManager imm = (InputMethodManager)getSystemService(
                         Context.INPUT_METHOD_SERVICE);
                 imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                 dialog.cancel();
             }
         });
         alert.show();
     }



    private class callReportAbuseAsyncTask extends AsyncTask<String, Integer, Void>
    {
        ReportAbuseResult reportAbuseResult;
        WebServer ws;
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            reportAbuseResult = new ReportAbuseResult();
            ws = new WebServer();
            ws.setToken(app.getToken());

            progressDialog = new ProgressDialog(PatientSearchedInfoActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Reporting abuse, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(String... explanation)
        {
            //Get the current thread's token
            synchronized (this)
            {
                reportAbuseResult = ws.callReportAbuse(app.getToken(), patientCurSel.uuid, explanation[0]);
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
            String message = "";
            if (reportAbuseResult.getErrorCode().equalsIgnoreCase("0") == false) {
                message = "Error: failed to send the report. \nError Code: " + reportAbuseResult.getErrorCode().toString() + "\n, Error Message: " + reportAbuseResult.getErrorMessage().toString();
            }
            else {
                message = "Your information was sent successfully.";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
            builder.setMessage(message)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
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

    private void admin() {
        if (isAdmin() == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
            builder.setMessage("You do not have administrator privilege. ")
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

        }
    }

    public boolean isAdmin(){
        boolean isAdmin = false;

        return isAdmin;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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

    public void deleteCurrentImage(){
        // Need to add confirm dialogue box
        if (images.size() <= 0){
            Toast.makeText(PatientSearchedInfoActivity.this, "No image is to be deleted.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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

    private void enterNumber() {
        int n = Integer.parseInt(textViewPid.getText().toString());
        String msg = "";
        if (h.pidSuffixFixedLength <= 0){
            msg = "Maximum length is " + Hospital.MAXIMUM_SUFFIX_LENGTH;
        }
        else {
            msg = "Maximum length is " + h.pidSuffixFixedLength;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
        final EditText editText = new EditText(PatientSearchedInfoActivity.this);
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
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(editText.getWindowToken(), textViewPid.getText().length());

                        dialog.cancel();
                        return;
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // hide the keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        String str = editText.getText().toString().trim();
                        if (str.isEmpty()==true){
                            str = "0";
                        }
                        if (h.pidSuffixFixedLength > 0){
                            str = formatNumber(Integer.parseInt(str), h.pidSuffixFixedLength);
                        }
                        if (str == ""){
                            Toast.makeText(PatientSearchedInfoActivity.this, "Error in input.", Toast.LENGTH_SHORT).show();
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

    private void PingAndSend() {
        TriagePic app = ((TriagePic)this.getApplication());
        long webServerId = app.getWebServerId();

        DataSource s = new DataSource(this, app.getSeed());
        WebServer w = new WebServer();
        s.open();
        w = s.getWebServerFromId(webServerId);
        s.close();

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

    private void GoLpWebForPuuid(String url) {
        Intent i = new Intent(PatientSearchedInfoActivity.this, LpWebPuuidActivity.class);

        i.putExtra("url", url);
        startActivity(i);
    }

    private void AddCapition() {
        if (images.size() <= 0){
            Toast.makeText(PatientSearchedInfoActivity.this, "There is no photo. No caption is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
        final EditText editText = new EditText(PatientSearchedInfoActivity.this);
        editText.setText(tvCapition.getText());
        builder.setView(editText)
                .setCancelable(true)
                .setTitle("Add Caption")
                .setIcon(R.drawable.edit)
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // hide the keyboard
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        Toast.makeText(PatientSearchedInfoActivity.this, "Quit", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PatientSearchedInfoActivity.this, "Saving... \"" + str + "\"", Toast.LENGTH_SHORT).show();
                        Image i = images.get(curImageIndex);
                        i.setCaption(str);
                        tvCapition.setText(str);

                        return;
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
            textViewPid2.setText(textViewPrefixPid.getText().toString() + "00000");
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
            if (str == ""){
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

    protected void selectCurrentPhotoAsPrimary() {
        Image imgCurSel = images.get(curImageIndex);
        Image imgCurPri = images.get(0);
        images.set(0, imgCurSel);
        images.set(curImageIndex, imgCurPri);

        curImageIndex = 0;
        setMyImage();
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

        if (img.getBitmap().getWidth() < Image.WIDTH_MINIMUM_IMAGE)
        {
            Toast.makeText(this, "The selected image is too small to crop. The required minimum width is " + String.valueOf(Image.WIDTH_MINIMUM_IMAGE), Toast.LENGTH_SHORT).show();
            return;
        }
        if (img.getBitmap().getHeight() < Image.HEIGHT_MINIMUM_IMAGE){
            Toast.makeText(this, "The selected image is too small to crop. The required minimum height is " + String.valueOf(Image.HEIGHT_MINIMUM_IMAGE), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Gallery app is not installed. Please install Gallery.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            intent.setData(uriRealCurSel);

            intent.putExtra("outputX", Image.CROP_WIDTH);
            intent.putExtra("outputY", Image.CROP_HEIGHT);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            // Just make simple. Grllery.app
            Intent i = new Intent(intent);
            ResolveInfo res	= list.get(0);
            i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_IMAGE);

            /*
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_IMAGE);
        	} 
        	else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
//		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_IMAGE);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (uriRealCurSel != null ) {
		                    getContentResolver().delete(uriRealCurSel, null, null );
		                    uriRealCurSel = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        	*/
        }
    }

    public void Outbox(){
        // Stage 1 verify input data
        String message = verifyData();
        if (message.isEmpty() == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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

    public void SaveReportAsOutbox() {
        Patient p = new Patient();
        p = getPatient();
        p.boxId = Patient.OUTBOX;
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        if (this.editPatientOriginal != null) {
            p.rowIndex = this.editPatientOriginal.rowIndex;
            s.updatePatient(p);

        }
        else{
            p = s.createPatientPlus(p);
        }
        s.close();
        Toast.makeText(this, "Report is saved to Outbox", Toast.LENGTH_SHORT).show();
        this.finish();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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
            Patient p = SaveReportAsSent();
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
            age = Patient.Age.UNKNOWN;
            msg = "Age is not selected.";
            return msg;
        }
        if (ckAdult.isChecked() == true){
            age = Patient.Age.ADULT;
        }
        else {
            age = Patient.Age.PEDIATRIC;
        }

        // gender
        if (ckMale.isChecked() == false && ckFemale.isChecked() == false){
            msg = "Gender is not selected.";
            age = Patient.Age.UNKNOWN;
            return msg;
        }
        if (ckMale.isChecked() == true){
            gender = Patient.Gender.MALE;
        }
        else {
            gender = Patient.Gender.FEMALE;
        }


        return msg;
    }

    public ReportResult sendReport(Patient p){
        ReportResult reportResult = new ReportResult();
		/*
		Intent report = new Intent(PatientSearchedInfoActivity.this, Webservice.class);
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

        if (isLogin() == false){
            reportResult.setErrorCode(ReportResult.MY_ERROR_CODE);
            reportResult.setErrorMessage("You are in off line mode.");
            Toast.makeText(this, reportResult.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
            return reportResult;
        }

        // v33
        reportResult = ws.callReport(p.rowIndex, this, app.getToken(), app.getSeed());
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
            progressDialog = new ProgressDialog(PatientSearchedInfoActivity.this);
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
                Toast.makeText(PatientSearchedInfoActivity.this, "Report is sent.", Toast.LENGTH_SHORT).show();
                PatientSearchedInfoActivity.this.finish();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
                builder.setMessage(reportResult.getErrorMessage().toString())
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

    public Patient SaveReportAsSent() {
        Toast.makeText(this, "Sending Report", Toast.LENGTH_SHORT).show();
        Patient p = new Patient();
        p = getPatient();
        p.boxId = Patient.SENT;
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        if (this.editPatientOriginal != null) {
            p.rowIndex = this.editPatientOriginal.rowIndex;
            s.updatePatient(p);

        }
        else{
            p = s.createPatientPlus(p);
        }

        s.close();
        return p;
    }

    private void SelectAdult() {
        if (ckAdult.isChecked() == true){
            ckAdult.setChecked(true);
            ckChild.setChecked(false);
            age = Patient.Age.ADULT;
        }
        else{
            ckAdult.setChecked(false);
            ckChild.setChecked(true);
            age = Patient.Age.PEDIATRIC;
        }
    }

    private void SelectChild() {
        if (ckChild.isChecked() == true){
            ckAdult.setChecked(false);
            ckChild.setChecked(true);
            age = Patient.Age.PEDIATRIC;
        }
        else {
            ckAdult.setChecked(true);
            ckChild.setChecked(false);
            age = Patient.Age.ADULT;
        }
    }

    private void SelectMale() {
        if (ckMale.isChecked() == true){
            ckMale.setChecked(true);
            ckFemale.setChecked(false);
            gender = Patient.Gender.MALE;
        }
        else{
            ckMale.setChecked(false);
            ckFemale.setChecked(true);
            gender = Patient.Gender.FEMALE;
        }
    }

    private void SelectFemale() {
        if (ckFemale.isChecked() == true){
            ckMale.setChecked(false);
            ckFemale.setChecked(true);
            gender = Patient.Gender.FEMALE;
        }
        else {
            ckMale.setChecked(true);
            ckFemale.setChecked(false);
            gender = Patient.Gender.MALE;
        }
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
    }

    /**
     * button listeners are set
     */
    private void setScreen() {
        /**
         * open the camera dialog
         */
		/*
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
//				zone = Zone.getZone(v.getId());
				myZone = Patient.MyZone.getZone(v.getId());
				return true;
			}
		};
		for (int i : zones)
			((CheckBox) findViewById(i)).setOnTouchListener(zoneListener);
*/
        /**
         * store defualt EditText background
         */
//		pidBack = ((EditText) findViewById(R.id.patient_id_field))
//				.getBackground();

        /**
         * if the increment/decrement is released stopp increment/decrementing
         * if pressed start
         */
        View.OnTouchListener intDecTou = new View.OnTouchListener() {
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
                                updatePID(true);
                            } else {
                                updatePID(false);
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

        /**
         * if the increment/decrement is released stopp increment/decrementing
         * if pressed start
         */
        View.OnKeyListener intDecKey = new View.OnKeyListener() {

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
		/*
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
        ((ImageView) findViewById(R.id.gallery))
                .setOnTouchListener(new View.OnTouchListener() {

                    GestureDetector t = new GestureDetector(
                            new MyGestureDetector());

                    public boolean onTouch(View v, MotionEvent event) {
                        return t.onTouchEvent(event);
                    }
                });

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
     * clear the page of old data and create a clear page, also set defualt
     * values
     */
    public void freshPage() {
		/*
		 * If the event is null, the activity was just instantiated, if not a
		 * record is being clearedand these fields have no need to be reset If
		 * event isn't null a record is being cleared so zones need to be unset
		 */
        if (event == null) {
            SharedPreferences settings = this.getSharedPreferences("Info", 0);
            DataSource s = new DataSource(this, app.getSeed());
            event = settings.getString("event", "Test Exercise");
            hospital = settings.getString("hospital", "NLM (testing)");
            pid = settings.getLong(event, 1);
            s.open();
            h = s.getHospital(hospital);
            e = s.getEvent(event);
            s.close();
            ((TextView) findViewById(R.id.patient_id_prefix))
                    .setText(h.pidPrefix);
        } else {
            setUnpressed();
            for (int i : ages)
                ((CheckBox) findViewById(i)).setChecked(false);
            for (int i : genders)
                ((CheckBox) findViewById(i)).setChecked(false);
            ((TextView) findViewById(R.id.first_name_field)).setText("");
            ((TextView) findViewById(R.id.last_name_field)).setText("");
        }
        age = Patient.Age.ADULT;
        gender = Patient.Gender.UNKNOWN;
        myZone = Patient.MyZone.UNASSIGNED;
        images = new ArrayList<Image>();

        if (!gender.equals(Patient.Gender.UNKNOWN))
            ((CheckBox) findViewById(gender.i)).setChecked(true);
        ((CheckBox) findViewById(age.i)).setChecked(true);
//		((EditText) findViewById(R.id.patient_id_field)).setText(String.valueOf(pid));
//		tvPid.setText(String.valueOf(patientCurSel.getPid()));
//		tvId.setText(String.valueOf(patientCurSel.rowIndex));
    }

    /**
     * set defualt values based on patient being edited.
     */
    private void setFromPatient() {
        // Read from database
        editPatientOriginal = patientCurSel;
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
//		editPatientOriginal = s.getPatientById(rowId);
        h = s.getHospital(editPatientOriginal.hospital);
        e = s.getEvent(editPatientOriginal.event);
//		setTitle("Editing Patient " + editPatientOriginal.patientId);
//		System.out.println(h + " " + e);
        s.close();

        // Display

        hospitalCurSel = editPatientOriginal.hospital;
        eventCurSel = editPatientOriginal.event;

        gender = editPatientOriginal.gender;
        age = editPatientOriginal.age;

        ((TextView) findViewById(R.id.first_name_field))
                .setText(editPatientOriginal.firstName);
        ((TextView) findViewById(R.id.last_name_field))
                .setText(editPatientOriginal.lastName);

        prefixPid = editPatientOriginal.prefixPid;
        textViewPrefixPid.setText(prefixPid);
//		if (prefixPid == 0){
//			textViewPrefixPid.setText("");
//		}
//		else {
//			textViewPrefixPid.setText(String.valueOf(prefixPid));
//		}
        pid = editPatientOriginal.patientId;
        String str = "";
        if (h.pidSuffixFixedLength <= 1){
            if (pid == 0){
                str = "";
            }
            else {
                str = String.valueOf(pid);
            }
        }
        else {
            str = formatNumber(pid, h.pidSuffixFixedLength);
            if (str == ""){
                if (pid == 0){
                    str = "";
                }
                else {
                    str = String.valueOf(pid);
                }
            }
        }
        textViewPid.setText(str);
        textViewPid2.setText(textViewPrefixPid.getText().toString() + str.toString());
        textViewPid2Auto.setText(textViewPid2.getText());

//		zone = editPatientOriginal.zone;
        myZone = editPatientOriginal.myZone;
        images = editPatientOriginal.images;
//		((TextView) findViewById(R.id.patient_id_prefix)).setText(h.pIDPrefix);
//		if (!gender.equals(Gender.UNKNOWN)){
//			((CheckBox) findViewById(gender.i)).setChecked(true);
//		}

        if (gender.equals(Patient.Gender.MALE)){
            ckMale.setChecked(true);
        }
        else{
            ckMale.setChecked(false);
        }
        if (gender.equals(Patient.Gender.FEMALE)){
            ckFemale.setChecked(true);
        }
        else{
            ckFemale.setChecked(false);
        }

        if (age.equals(Patient.Age.ADULT)){
            ckAdult.setChecked(true);
        }
        else {
            ckAdult.setChecked(false);
        }
        if (age.equals(Patient.Age.PEDIATRIC)){
            ckChild.setChecked(true);
        }
        else {
            ckChild.setChecked(false);
        }

        if (patientCurSel.getUuid().isEmpty() == false){
            textViewUuid.setText("https://" + patientCurSel.getUuid());
        }

        etComments.setText(editPatientOriginal.comments);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
		IntentFilter filter = new IntentFilter();
		filter.addAction("Sent");
		filter.addAction("Deleted");
		filter.addAction("Check");
		this.registerReceiver(this.receiver, filter);
		*/
//        setMyImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
//		this.unregisterReceiver(this.receiver);
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


    /**
     * load all options as action items
     */
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getSupportMenuInflater();
		if (rowId == 0) {
			i.inflate(R.menu.clear, menu);
			i.inflate(R.menu.send, menu);
			i.inflate(R.menu.queue, menu);
			i.inflate(R.menu.setting, menu);

		} else {
			i.inflate(R.menu.delete, menu);
			i.inflate(R.menu.resend, menu);
			i.inflate(R.menu.cancel, menu);
		}
		return true;
	}
*/
    /**
     * respond accordingly to menu options. in the case of send/resend if
     * practice mode is selected do nothing, do nothing if no zone is selected.
     * start a send report service request otherwise, saving the sending report
     * to the database. If delete is selected show a pop up to be certain they
     * want to delete and aren't trying to clear the screen since clear/delete
     * share icons
     */
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Cancel"))
			finish();

		if (item.getTitle().equals("Queue"))
			startActivity(new Intent(ReportPatient.this,
					QueueFragmentActivity.class));

		if (item.getTitle().equals("Settings"))
			startActivity(new Intent(ReportPatient.this,
					SettingsFragmentActivity.class));

		if (item.getTitle().equals("Clear"))
			freshPage();

		if (item.getTitle().equals("Delete")) {
			this.delete(this.editPatientOriginal.rowIndex).show();
		}

		if (item.getTitle().equals("Send") || item.getTitle().equals("re-Send")) {
			if (((CheckBox) findViewById(R.id.practice_mode)).isChecked())
				Toast.makeText(this, "Practice Mode", Toast.LENGTH_SHORT)
						.show();
			else if (zone == Zone.UNASSIGNED)
				Toast.makeText(this, "Must select a zone", Toast.LENGTH_SHORT)
						.show();
			else {
				Toast.makeText(this, "Sending Report", Toast.LENGTH_SHORT)
						.show();
				Patient p = getPatient();
				DataSource s = new DataSource(this);
				s.open();
				if (this.editPatientOriginal != null) {
					p.rowIndex = this.editPatientOriginal.rowIndex;
					s.updatePatient(p);

				} else
					p = s.createPatient(p);
				s.close();

				Intent report = new Intent(ReportPatient.this, Webservice.class);
				report.putExtra("patient", true);
				report.putExtra("patientId", p.rowIndex);
				startService(report);
				if (editPatientOriginal != null)
					this.finish();
				else {
					pid++;
					this.getSharedPreferences("Info", 0).edit()
							.putLong(event, pid).commit();
					freshPage();
				}
			}
		}

		if (item.getTitle().equals("Cancel"))
			finish();
		return true;
	}
	*/

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

        Patient.Age a = age;
        Patient.Gender g = gender;
        Patient p = new Patient(
//				Long.parseLong(((EditText) findViewById(R.id.patient_id_field)).getText().toString()),
                prefixPid.toString(), // not in use
                sp, //
                textViewPid.getText().toString(),
//                etLastName.getText().toString(),
                tvLastName.getText().toString(),
                tvFirstName.getText().toString(),
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
     * update the rate at which the p.id is increasing/decreasing, the
     * increment/decrement accordingly, if the p.id over steps the
     * [1,9999999999] bound set the id to the corresponding limit and send a
     * toast that the limit cannot be exceeded
     *
     * @param inc
     */
    public void updatePID(boolean inc) {
        if (inc)
            changeRate(1);
        else
            changeRate(-1);

//		pid = Long.valueOf(((EditText) findViewById(R.id.patient_id_field)).getText().toString());
        pid = Long.parseLong(textViewPid.getText().toString());

        pid = pid + rate;
        if (!(pid < 9999999999L)) {
            pid = 9999999999L;
            Toast.makeText(this, "Patient Id cannot exceed 9999999999",
                    Toast.LENGTH_SHORT).show();
        }
        if (pid < 1) {
            pid = 1;
            Toast.makeText(this, "Must have a positive non-zero Patient Id",
                    Toast.LENGTH_SHORT).show();
        }

//		((EditText) findViewById(R.id.patient_id_field)).setText(String.valueOf(pid));
    }

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
     * unpress all zone buttons for page clearing
     */
    public void setUnpressed() {
        for (int i : zones)
            ((CheckBox) findViewById(i)).setChecked(false);
    }

    /**
     * check if image was selected and if so set is the currently viewed image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EVENT_REQUEST){
            String name = data.getExtras().getString("eventname").trim();
            if (name.isEmpty() == false){
                eventCurSel = name;
                tvEvent.setText(eventCurSel);

                // Write to shared preference
                PatientSearchedInfoActivity.this.getSharedPreferences("Info", 0).edit().putString("event", eventCurSel).commit();
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
                PatientSearchedInfoActivity.this.getSharedPreferences("Info", 0).edit().putString("hospital", hospitalCurSel).commit();
            }
        }
        else if (requestCode == TAKE_PHOTO_REQUEST){
            if (resultCode == 0) {// Cancel
                return;
            }

            Bundle extras = data.getExtras();
            bmpPhoto = (Bitmap)extras.get("data");

            // Convert to data byte
            ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
            bmpPhoto.compress(Bitmap.CompressFormat.PNG,50,byteArryPhoto);
            byte[] photoData = byteArryPhoto.toByteArray();

            String encodedPhoto = Base64.encodeToString(photoData, Base64.DEFAULT);
            new callAddCommentAsyncTask().execute("", "", "", encodedPhoto.toString());
        }
        else if (requestCode == ADDRESS_REQUEST){
			/*
			String serialId = data.getExtras().getString("serialId");
			if (serialId.equalsIgnoreCase("0") == true){
				return;
			}
 		   	etAddress1.setText(data.getExtras().getString("street1"));
 		   	etAddress2.setText(data.getExtras().getString("street2"));
 		   	etCity.setText(data.getExtras().getString("city"));
 		   	etState.setText(data.getExtras().getString("state"));    		   
 		   	etZip.setText(data.getExtras().getString("zip"));
 		   	etCountry.setText(data.getExtras().getString("country")); 
 		   	tvSelectLastSeenLocation.setText(selectLastSeenLocationDefaultString);
 		   	Toast.makeText(this, "Address is updated", Toast.LENGTH_SHORT).show();
 		   	*/
        }
        else if (requestCode == QUERY_MEDIA){
            if (resultCode == 0) {// Cancel
                return;
            }
            Bundle extras = data.getExtras();
            String fileFullName = (String)extras.get("fileFullName");

            Bitmap bmpPhoto = Shrinkmethod(fileFullName, 500,500);

            // Convert to data byte
            ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
            bmpPhoto.compress(Bitmap.CompressFormat.PNG,50,byteArryPhoto);
            byte[] photoData = byteArryPhoto.toByteArray();

            String encodedPhoto = Base64.encodeToString(photoData, Base64.DEFAULT);
            new callAddCommentAsyncTask().execute("", "", "", encodedPhoto.toString());
        }
        else if (requestCode == ReportPatientImageHandler.IMAGE_PRIV_GAL){ // get image back from triagepic gallery
            /*
			if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(PatientSearchedInfoActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();			
				return;
			}

			Image image = new Image();
			image = camera.onActivityResult(requestCode, resultCode, data);
			if (images == null) {
				images = new ArrayList<Image>();
				images.add(image);
			} 
			else{
				images.add(image);
			}
			if (curImageIndex != 0){
				curImageIndex = images.size() - 1;
			}
			
			curImageIndex = images.size() - 1;
			setMyImage();
			Toast.makeText(PatientSearchedInfoActivity.this, "Photo \"" + image.getFileName() + "\" is added.", Toast.LENGTH_SHORT).show();
			*/
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(PatientSearchedInfoActivity.this, "Canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            Image image = new Image();
            image = camera.onActivityResult(requestCode, resultCode, data);

            // changes made in version 9.0.0
//            Bitmap bm = ReportPatientImageHandler.resizedBitmap(image.getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, PatientSearchedInfoActivity.this, true);
//            image.setBitmap(image.getBitmap());
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

            Toast.makeText(PatientSearchedInfoActivity.this, "Photo \"" + image.getFileName() + "\" is added.", Toast.LENGTH_SHORT).show();

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

    Bitmap Shrinkmethod(String file,int width,int height){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(file, bitopt);

        int h=(int) Math.ceil(bitopt.outHeight/(float)height);
        int w=(int) Math.ceil(bitopt.outWidth/(float)width);

        if(h>1 || w>1){
            if(h>w){
                bitopt.inSampleSize=h;

            }else{
                bitopt.inSampleSize=w;
            }
        }
        bitopt.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(file, bitopt);
        return bit;
    }

    public void processCroppedImage(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED){
            String errorMsg = "This photo cannot be cropped.";
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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
                fileNew = Image.createTriagePicImagefile(PatientSearchedInfoActivity.this);

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

            fileNew = Image.createTriagePicImagefile(PatientSearchedInfoActivity.this);

            // Save bmp file to image
            try {
                Image.saveBitmapToFile(bmp, fileNew);
            }
            catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (!fileNew.exists()){
                String errorMsg = "Failed to create the image file.";
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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
                                Intent i = new Intent(PatientSearchedInfoActivity.this, ImageGallery.class);
                                startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /**
     * save values and store them to be reset after an activity result or screen
     * rotation that may have destroyed them otherwise
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * restore values from screen rotation/activtiy result
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tvEvent.setText(savedInstanceState.getString("eventCurSel", ""));
        tvHospital.setText(savedInstanceState.getString("hospitalCurSel", ""));
//		tvId.setText(savedInstanceState.getString("idCurSel", ""));
        textViewPrefixPid.setText(savedInstanceState.getString("prefixPidCurSel", ""));
        textViewPid.setText(savedInstanceState.getString("pidCurSel", ""));
        textViewPid2.setText(savedInstanceState.getString("pid2CurSel", ""));
        textViewPid2Auto.setText(textViewPid2.getText());

        // index for current images
        curImageIndex = savedInstanceState.getInt("curImageIndex");

        // Images
        int size = savedInstanceState.getInt("size", 0);
        images.clear();
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

        tvFirstName.setText(savedInstanceState.getString("firstName"));
        tvLastName.setText(savedInstanceState.getString("lastName"));

        setMyImage(curImageIndex);


    }

    public void setMyImage() {
        if (images.size() > 0) {
            Image i = images.get(curImageIndex);

            // changed made in version 9.0.0
            if (i.getBitmap() == null){
                String e = i.getEncoded();
                if (e.isEmpty() == true){
                    Toast.makeText(this, "Missing encoded data!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bitmap b = Patient.decodeStringToBitmap(e);
                    i.setBitmap(b);
                }
                /*
                File f = new File(i.getUri());
                if (f.exists()){
                    Bitmap bm = ReportPatientImageHandler.resizedBitmap(i.getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, true);
                    i.setBitmap(bm);
                }
                else {
                    Toast.makeText(this, "Missing file: " + i.getUri().toString(), Toast.LENGTH_SHORT).show();
                }
                */
            }
            if (i.getBitmap() == null){
                Log.e("Error in SetMyImage()", "Failed to create bitmap file.");
                return;
            }

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

                Image.Rect r = i.getRect();
                canvas.drawRect(
                        r.getX(),
                        r.getY(),
                        r.getX() + r.getW(),
                        r.getY() + r.getH(),
                        facePaint
                );

//				ivPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
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

            tvCapition.setText(i.getCaption());
            ((TextView) findViewById(R.id.image_index)).setText((this.curImageIndex + 1) + "/" + images.size());
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

                Image.Rect r = i.getRect();
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
     * used for cycling through images and and deleteing images
     *
     * @author bonifantmc
     *
     */
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
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
                Toast.makeText(PatientSearchedInfoActivity.this, "No photo is to be deleted.", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
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
                                if (curImageIndex > 0){
                                    curImageIndex = curImageIndex - 1;
                                }
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
     * a dialog that will send a service to delete the record if so desired
     *
     * @param patientId
     *            : id of patient to delete
     * @return
     */
    public Dialog delete(final long patientId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this record?")
                .setCancelable(false)
                .setPositiveButton("Yes, delete.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent delete = new Intent(PatientSearchedInfoActivity.this,
                                        Webservice.class);
                                delete.putExtra("delete", true);
                                delete.putExtra("patientId", patientId);
                                startService(delete);
                                finish();
                                dialog.cancel();
                            }
                        });
        builder.setNegativeButton("No.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();

    }

    /**
     * Give user the option to select an image from the device's main gallery,
     * TriagePics private gallery, or to capture a new image from the camera and
     * start a new activity for result accordingly
     *
     * @return
     */
    /*
	protected Dialog addPhoto() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		return builder
				.setMessage("Select photo from")
				.setCancelable(true)
				.setPositiveButton("External Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                                        new Intent(PatientSearchedInfoActivity.this,
                                                ImageGallery.class),
                                        ReportPatientImageHandler.IMAGE_PRIV_GAL);
                            }
                    }).show();
	}
	*/
    public void addPhoto() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setTitle("Add photo from");
        alert.setPositiveButton("Ext Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Uri u = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(i, ReportPatientImageHandler.IMAGE_FIND_EX);
                dialog.dismiss();
            }
        });
        alert.setNeutralButton("Int Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(PatientSearchedInfoActivity.this, ImageGallery.class);
                startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = camera.startCamera();
                        startActivityForResult(i, ReportPatientImageHandler.IMAGE_CAPTURE);
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    private void EXTERNAL_CONTENT_URI() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ReportPatientImageHandler.IMAGE_FIND_EX);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*
        String msg = "Pressing back key to go back home page will lose your work. Please confirm again.";
        msg += "\nSuggestion: ";
        msg += "\nYou may select \"Drafts\" button to save your work and return.";
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
        builder.setMessage(msg)
                .setCancelable(true)
                .setTitle("Warning")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PatientSearchedInfoActivity.this.onBackPressed();
                        PatientSearchedInfoActivity.this.finish();
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
        Intent i = new Intent(PatientSearchedInfoActivity.this, WebServerActivity.class);
        startActivity(i);
    }

    private void GoHome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchedInfoActivity.this);
        builder.setMessage("You are about to leave this page. You may lost your work.")
                .setCancelable(true)
                .setTitle("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(PatientSearchedInfoActivity.this, HomeActivity.class);
                        startActivity(i);
                        PatientSearchedInfoActivity.this.finish();
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
}