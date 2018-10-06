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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.pl.triagepic.Result.SearchResult;
import com.pl.triagepic.dummy.DummyBoxContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Box detail screen.
 * This fragment is either contained in a {@link BoxListFragmentActivity}
 * in two-pane mode (on tablets) or a {@link BoxDetailFragmentActivity}
 * on handsets.
 */
public class BoxDetailFragment extends Fragment {
    private static final String TAG = "BoxDetailFragment";

    private static final int PATIENT_INFO = 7;
    private static final int SEARCH_TRIAGETRAK = 8;
    private static final int SEARCH_BY_PHOTO = 9;
    private static final int BACK_FROM_FILTER = 10;

    private static final int DRAFTS = 0;
    private static final int SENT = 1;
    private static final int OUTBOX = 2;
    private static final int DELETED = 3;
    private static final int TRIAGETRAK = 4;

    private List<Patient> patientsDeleted = new ArrayList<Patient>();
    private List<Patient> patientsDrafts = new ArrayList<Patient>();
    private List<Patient> patientsSent = new ArrayList<Patient>();
    private List<Patient> patientsOutbox = new ArrayList<Patient>();
    private List<Patient> patientsTriageTrak = new ArrayList<Patient>();
    private List<Patient> curPatientList = new ArrayList<Patient>();

    private static String [] strBoxes = new String[] {
                "Drafts",
                "Sent",
                "Outbox",
                "Deleted",
                "TriageTrak"
    };

    Activity parent;
    TriagePic app;

    ViewSettings viewSettings;

    // LoadMore button
    int totalPages = 0;
    int totalRecords = 0;
    int pageSize = 0;
    int currentPage = 0;
    int currentPageStart = 0;

    // top part viewswitch. 1. check box; 2. input controls for triageTrack
    ViewSwitcher viewSwitcherTopPart;
    boolean isNextViewTopPart;

    // bottom part viewswitch. 1. progress bar; 2. patient list.
    ViewSwitcher viewSwitcherBottomPart;
    boolean isNextViewBottomPart;

    Button buttonFilter;
    Button buttonStartSearch;
    Button nextPageButton;
//    Button buttonImageSearch;
    RadioButton radioButtonByName;
    RadioButton radioButtonByPhoto;

    CheckBox ckCheckAll;
    ListView lvPatient;
    View footerView;
    boolean patientChecked[];
    TextView tvPatient;
    EditText editTextQuery;
    TextView info1;
    TextView info2;
    TextView info3;
    TextView info4;
    TextView info5;

    //	String strPatients[];
    ItemPatientListBaseAdapter adapterPatient;

    private int curSelBox = 0;

    boolean firstCheckBox;

    ProgressBar progressBarPatientList;

    String curSelDes = "";

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyBoxContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BoxDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initials.
        setHasOptionsMenu(true);

        parent = getActivity();
        app = (TriagePic)parent.getApplication();
        app.detectMobileDevice(parent);
        app.setScreenOrientation(parent);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyBoxContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            curSelDes = getArguments().getString(ARG_ITEM_ID);

            Initialize();
        }
    }

    private void Initialize() {
        // get view settings from database.
        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        viewSettings = d.getViewSettings();
        if (viewSettings == null || viewSettings.getId() == 0){
            viewSettings = new ViewSettings();
            viewSettings.SetToDefault();
            viewSettings.setId((int)d.createViewSettings(viewSettings));
        }
        d.close();

        try {
            curSelBox = Integer.parseInt(curSelDes);
        }
        catch(NumberFormatException nfe) {
            Log.e("Error:", "Parse integer from string \"curSelDes\": " + curSelDes);
        }
        switch (curSelBox) {
            case DRAFTS:
                loadPatients(DRAFTS);
                createPhotoPatients(DRAFTS);
                break;
            case SENT:
                loadPatients(SENT);
                createPhotoPatients(SENT);
                break;
            case OUTBOX:
                loadPatients(OUTBOX);
                createPhotoPatients(OUTBOX);
                break;
            case DELETED:
                loadPatients(DELETED);
                createPhotoPatients(DELETED);
                break;
            case TRIAGETRAK:
                loadPatients(TRIAGETRAK);
                createPhotoPatients(TRIAGETRAK);
                break;
            default:
                break;
        }
    }

     private String showNextPage() {
        String message = "";
        totalRecords = Integer.valueOf(app.getCurSearchCount());
        if (viewSettings.getPageStart() > totalRecords){
            message = "Search is completed.";
            return message;
        }

        /**
         * Clean patient TriageTrak list, if this is the first page
         * This is done in two steps: one the patientsTriageTrak list and two is the list view
         */
        if (!curPatientList.isEmpty()){
            curPatientList.clear();
        }

        /**
         * Search the current page
         */
        // set status of image before downloading
        curPatientList = searchPatients();

        if (!curPatientList.isEmpty()) {
            setStatusImageDownload(false);

            /**
             * searchPatientImages
             */
            searchPatientImages();

            savePatientImagesToDatabase();
            setStatusImageDownload(true);

            /**
             * Save to the database.
             */
            savePatientTriageTrakListToDatabase();

            /**
             * add to patienttriageTrak list
             */
            patientsTriageTrak.addAll(curPatientList);
        }

        Log.i(TAG, "curPatientList size: " + Integer.valueOf(curPatientList.size()));
        Log.i(TAG, "patientsTriageTrak size: " + Integer.valueOf(patientsTriageTrak.size()));

        return message;
    }

    private void displayPatientTrageTrakList() {
        Initialize();
        DisplayPatientList(TRIAGETRAK);
    }

    private void clearTriageTrakListView() {
        adapterPatient.notifyDataSetChanged();
    }

    private void setStatusImageDownload(boolean b) {
        for (int i = 0; i < patientsTriageTrak.size(); i++){
            Patient p = patientsTriageTrak.get(i);
            p.setStatusPhotoDownload(b);
            patientsTriageTrak.set(i, p);
        }
    }

    private void savePatientImagesToDatabase() {
        if (curPatientList.isEmpty()){
            return;
        }

        DataSource s = new DataSource(parent, app.getSeed());
        s.open();

        for (int i = 0; i < curPatientList.size(); i++){
            Patient p = curPatientList.get(i);
            if (p != null){
                ArrayList<Image> images = p.getImages();
                if (images != null){
                    for (int j = 0; j < images.size(); j++){
                        Image img = images.get(j);
                        img.setPid(p.getPid());
                        img.setSquence(i);
                        s.createImage(img);
                    }
                }
            }
        }

        s.close();
    }

    private void searchPatientImages() {
        if (curPatientList.isEmpty()){
            return;
        }

        for (int i = 0; i < curPatientList.size(); i++){
            Patient p = curPatientList.get(i);
            Log.i(TAG, p.getLastName() + ", " + p.getFirstName() + " has " + p.images.size() + ".");
            if (p != null){
                ArrayList<Image> images = p.getImages();
                if (images != null){
                    for (int j = 0; j < images.size(); j++){
                        Image img = images.get(j);
                        img.downloadPatientPhoto();

                        /**
                         * Simple is better.
                         */
                        int outWidth;
                        int outHeight;
                        int inWidth = img.getBitmap().getWidth();
                        int inHeight = img.getBitmap().getHeight();
                        if(inWidth > inHeight){
                            outWidth = Image.MAX_SIZE;
                            outHeight = (inHeight * Image.MAX_SIZE) / inWidth;
                        } else {
                            outHeight = Image.MAX_SIZE;
                            outWidth = (inWidth * Image.MAX_SIZE) / inHeight;
                        }
                        Bitmap resized = Bitmap.createScaledBitmap(img.getBitmap(), outWidth, outHeight, false);
//                        Bitmap resize = Bitmap.createScaledBitmap(img.getBitmap(), Image.MAX_SIZE, Image.MAX_SIZE, false);
                        img.setBitmap(resized);
                        img.setDigest(img.getDigestFromBitmap(resized));
                        img.setEncoded(p.encodeBitmapToString(resized));

                        images.set(j, img);
                    }
                }
                if (images != null) {
                    p.setImages(images);
                }
                int size = p.images.size();
            }

            // update
            if (p.images != null){
                curPatientList.set(i, p); // updated
            }
        }
    }

    public void saveReportAsTriageTrak(int index) {
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();

        Patient p = curPatientList.get(index);
        p.boxId = Patient.TRIAGETRAK;
        p = s.createPatientPlus(p);

        s.close();
    }

    public void updateReportAsTriageTrak(int index) {
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();

        Patient p = patientsTriageTrak.get(index);
        s.updatePatient(p);

        // Image
        if (p.images != null){
            for (int i = 0; i < p.images.size(); i++){
                Image img = p.images.get(i);
                img.setPid(p.getPid());
                img.setSquence(i);
                img = s.createImage(img);
                if (img == null) {
                    Log.e("Error", "Failed to create record in image table.");
                }
            }
        }
        s.close();
    }

    /**
     * Save TriageTrak list to database.
     */
    public void savePatientTriageTrakListToDatabase() {
        if (curPatientList.isEmpty()){
            return;
        }
        for (int i = 0; i < curPatientList.size(); i++){
            saveReportAsTriageTrak(i);
        }
    }

    public void updatePatientTriageTrakListToDatabase() {
        if (patientsTriageTrak.isEmpty()){
            return;
        }
        for (int i = 0; i < patientsTriageTrak.size(); i++){
            updateReportAsTriageTrak(i);
        }
    }

    public List<Patient> searchPatients() {
//        Filters filters = new Filters();
//        filters.setDefaults();
        String searchTerm = editTextQuery.getText().toString();
        int pageStart = viewSettings.getPageStart();
        List<Patient> list = searchTriageTrak(pageStart, searchTerm, viewSettings, app.getFilters());
        return list;
    }

    // Modified for api version v34
    private  List<Patient> searchTriageTrak(int currentPageStart, String searchTerm, ViewSettings viewSettings, Filters filters) {
        List<Patient> list = new ArrayList<Patient>();

        String curSearchTerm = searchTerm;
        WebServer ws = new WebServer();
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        ws.setSearchCountOnly(false);
        ws.setCurPageStart(currentPageStart);
        ws.setQuery(searchTerm);
        ws.setSearchCountOnly(false);

        Event e = new Event();
        e.name = app.getCurSelEvent();
        e.shortname = app.getCurSelEventShortName();
        e.incident_id = app.getCurSelEventId();
        ws.setEvent(e);

        Hospital h = new Hospital();
        h.name = app.getCurSelHospital();
        h.shortname = app.getCurSelHospitalShortName();
        h.uuid = app.getCurSelHospitalId();
        ws.setHospital(h);
        SearchResult sr = ws.callSearchCountV34(filters, viewSettings, app.getCurSelEventShortName(), parent);
        if (sr.getErrorCode().equals("0")){
            /**
             * parse the result.
             */
            ws.JSONParserForPatient(app.getCurSelEventShortName(), sr.getResultSet());
            list = ws.getPatientList();
        }
        return list;
    }

    private void createPhotoPatients(int curSelBox) {
        if (curSelBox == DRAFTS) {
            for (int i = 0; i < patientsDrafts.size(); i++) {
                Patient p = patientsDrafts.get(i);
                if (p.images.size() > 0) {
                    // changed in version 9.0.0
                    Bitmap b = Patient.decodeStringToBitmap(p.images.get(0).getEncoded());
                    p.photo = new BitmapDrawable(getResources(), b);
//                    p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, parent, false));
                    patientsDrafts.set(i, p);
                }
            }
        }
        else if (curSelBox == SENT){
            for (int i = 0; i < patientsSent.size(); i++){
                Patient p = patientsSent.get(i);
                if (p.images.size() > 0){
                    // changed in version 9.0.0
                    Bitmap b = Patient.decodeStringToBitmap(p.images.get(0).getEncoded());
                    p.photo = new BitmapDrawable(getResources(), b);
//                    p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, parent, false));
                    patientsSent.set(i, p);
                }
            }
        }
        else if (curSelBox == OUTBOX){
            for (int i = 0; i < patientsOutbox.size(); i++){
                Patient p = patientsOutbox.get(i);
                if (p.images.size() > 0){
                    // changed in version 9.0.0
                    Bitmap b = Patient.decodeStringToBitmap(p.images.get(0).getEncoded());
                    p.photo = new BitmapDrawable(getResources(), b);
//                    p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, parent, false));
                    patientsOutbox.set(i, p);
                }
            }
        }
        else if (curSelBox == DELETED){
            for (int i = 0; i < patientsDeleted.size(); i++){
                Patient p = patientsDeleted.get(i);
                if (p.images.size() > 0){
                    // changed in version 9.0.0
                    Bitmap b = Patient.decodeStringToBitmap(p.images.get(0).getEncoded());
                    p.photo = new BitmapDrawable(getResources(), b);
//                    p.photo = new BitmapDrawable(ReportPatientImageHandler.resizedBitmap(p.images.get(0).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, parent, false));
                    patientsDeleted.set(i, p);
                }
            }
        }
        else if (curSelBox == TRIAGETRAK){
            for (int i = 0; i < patientsTriageTrak.size(); i++){
                Patient p = patientsTriageTrak.get(i);

                // to get the images from image table.
                p.images = getImagesByPid(p.patientId);
                if (p.images.size() > 0) {
                    // changed in version 9.0.0
                    Bitmap b = Patient.decodeStringToBitmap(p.images.get(0).getEncoded());
                    p.photo = new BitmapDrawable(getResources(), b);
                }
                patientsTriageTrak.set(i, p);
            }
        }
    }

    public ArrayList<Image> getImagesByPid(long patientId) {
        ArrayList<Image> imgList = new ArrayList<Image>();
        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        imgList = d.getAllImages(patientId);
        d.close();
        return imgList;
    }

    /**
     * loads the patients from the database, sorts them by date, and then sets
     * the adapter and launches threads to load patient thumbnails
     * @param curSelBox
     */
    private void loadPatients(int curSelBox) {
        DataSource d = new DataSource(parent, app.getSeed());
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
        else if (curSelBox == TRIAGETRAK){
            patientsTriageTrak = d.getAllPatientsTriageTrakDesc();
        }
//		else {
//			patientsAll = d.getAllPatientsDesc();
//		}
        d.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.box_detail_more, container, false);

        // Show the dummy content as text in a TextView.
        if (!curSelDes.isEmpty()) {
            InitializeView(rootView);
//            ((TextView) rootView.findViewById(R.id.box_detail_id)).setText(curSelDes);
//            ((TextView) rootView.findViewById(R.id.box_detail_text)).setText(mItem.content);
//            ((TextView) rootView.findViewById(R.id.box_detail_number)).setText(mItem.number);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        // it does not work.
        super.onResume();
    }

    private void InitializeView(View v) {
        progressBarPatientList = (ProgressBar) v.findViewById(R.id.progressBarPatientList);
        progressBarPatientList.setVisibility(View.INVISIBLE);

        ckCheckAll = (CheckBox) v.findViewById(R.id.checkBoxSelAll);
        ckCheckAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ckCheckAll.isChecked() == true){
//                    ckCheckAll.setChecked(true);
                    checkAllItems(true);
                }
                else {
//                    ckCheckAll.setChecked(false);
                    checkAllItems(false);
                }
            }
        });



        // filters
        buttonFilter = (Button) v.findViewById(R.id.buttonFilters);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(parent, FiltersActivity.class);
                startActivityForResult(i, BACK_FROM_FILTER);
            }
        });

        // test edit
        editTextQuery = (EditText) v.findViewById(R.id.editTextQuery);
        editTextQuery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayInfoTextView();
            }
        });
        info1 = (TextView) v.findViewById(R.id.textViewInfo1);
        info2 = (TextView) v.findViewById(R.id.textViewInfo2);
        info3 = (TextView) v.findViewById(R.id.textViewInfo3);
        info4 = (TextView) v.findViewById(R.id.textViewInfo4);
        info5 = (TextView) v.findViewById(R.id.textViewInfo5);

        // add key listener to catch return key
        editTextQuery.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            TurnOffSoftKeyBoard();
                            moveToPage(0);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        editTextQuery.setText("");

        radioButtonByName = (RadioButton) v.findViewById(R.id.radioButtonByName);
        radioButtonByName.setChecked(true);
        radioButtonByPhoto = (RadioButton) v.findViewById(R.id.radioButtonByPhoto);
        radioButtonByPhoto.setChecked(false);

        buttonStartSearch = (Button) v.findViewById(R.id.buttonStartSearch);
        buttonStartSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (radioButtonByName.isChecked() == true){
                    TurnOffSoftKeyBoard();

                    // define the viewSettings
                    DataSource s = new DataSource(parent, app.getSeed());
                    s.open();
                    viewSettings = s.getViewSettings();
                    viewSettings.setIsImageSearch(0);
                    viewSettings.setEncodedImage("");
                    s.updateViewSettings(viewSettings.getId(), viewSettings);
                    s.close();

                    if (WebServer.AmIConnected(parent) == true) {
                        new callSearchCountAsyncTask().execute();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
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
                else {
                    editTextQuery.setText("");
                    Intent i = new Intent(parent, SearchByPhotoActivity.class);
                    startActivityForResult(i, SEARCH_BY_PHOTO);
                }
            }
        });

        /*
        buttonImageSearch = (Button) v.findViewById(R.id.buttonImageSearch);
        buttonImageSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(parent, SearchByPhotoActivity.class);
                startActivityForResult(i, SEARCH_BY_PHOTO);
            }
        });
        */

        tvPatient = (TextView) v.findViewById(R.id.textViewPatient);
        tvPatient.setVisibility(View.INVISIBLE);

        lvPatient = (ListView) v.findViewById(R.id.listViewPatient);
        // Instantiate footerView using a LayoutInflater and add to listView
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_view_load_more, null, false);
        // additionally, find the "load more button" inside the footer view
        Button buttonLoadMore = (Button) footerView.findViewById(R.id.load_more);
        buttonLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMore();
            }
        });

        if (ckCheckAll.isChecked() == true){
            ckCheckAll.setChecked(false);
        }

//		new DisplayPatientListAsyncTask().execute();
        DisplayPatientList(curSelBox);

        TurnOffSoftKeyBoard();

        hideInfoTextView();

        viewSwitcherTopPart = (ViewSwitcher) v.findViewById(R.id.viewSwitcherTopPart);
        if (curSelBox == TRIAGETRAK){
            viewSwitcherTopPart.setDisplayedChild(viewSwitcherTopPart.indexOfChild(v.findViewById(R.id.viewSearch)));
        }
        else {
            viewSwitcherTopPart.setDisplayedChild(viewSwitcherTopPart.indexOfChild(v.findViewById(R.id.viewReport)));
        }
    }

    private void loadMore() {
        // get viewSettings data
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        viewSettings = s.getViewSettings();
        if (viewSettings == null){
            viewSettings = new ViewSettings();
            viewSettings.SetToDefault();
        }
        else {
            totalRecords = Integer.valueOf(app.getCurSearchCount());
            if (viewSettings.getPageStart() + viewSettings.getPageSize() > totalRecords){
                Toast.makeText(parent, "You are already on the last page.", Toast.LENGTH_SHORT).show();
                return;
            }
            int newPage = viewSettings.getCurPage() + 1;
            viewSettings.setCurPage(newPage);
            viewSettings.setPageStart();
        }
        s.updateViewSettings(viewSettings.getId(), viewSettings);
        s.close();

        // clean the database
        curSelBox = TRIAGETRAK;
        curPatientList.clear();

        new showNextPageAsync().execute();
    }

    protected void displayInfoTextView() {
        info1.setVisibility(View.VISIBLE);
        info2.setVisibility(View.VISIBLE);
        info3.setVisibility(View.VISIBLE);
        info4.setVisibility(View.VISIBLE);
        info5.setVisibility(View.VISIBLE);

        if (app.isTablet() == false){
            lvPatient.setVisibility(View.GONE);
        }
        else {
            lvPatient.setVisibility(View.VISIBLE);
        }
    }

    protected void hideInfoTextView() {
        lvPatient.setVisibility(View.VISIBLE);

        if (app.isTablet() == false) {
            info1.setVisibility(View.GONE);
            info2.setVisibility(View.GONE);
            info3.setVisibility(View.GONE);
            info4.setVisibility(View.GONE);
            info5.setVisibility(View.GONE);
        }
        else {
            info1.setVisibility(View.VISIBLE);
            info2.setVisibility(View.VISIBLE);
            info3.setVisibility(View.VISIBLE);
            info4.setVisibility(View.VISIBLE);
            info5.setVisibility(View.VISIBLE);
        }
    }

    private void TurnOffSoftKeyBoard() {
        InputMethodManager mgr = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editTextQuery.getWindowToken(), 0);
        hideInfoTextView();
    }

    public void moveToPage(final int pageNumber){
        curSelBox = TRIAGETRAK;

        // clean the database
        deleteAllReportsInTriageTrakBox();

        // clean the list
        if (!patientsTriageTrak.isEmpty()){
            int size = patientsTriageTrak.size();
            while (!patientsTriageTrak.isEmpty()){

                // need to clean the images as well
                // fixed the error in version 9.0.4
                // start
                ArrayList<Image> list = patientsTriageTrak.get(0).getImages();
                if (!list.isEmpty()){
                    while(!list.isEmpty()){
                        list.remove(0);
                    }
                }
                // end

                patientsTriageTrak.remove(0);
            }
        }

        // define the viewSettings
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        viewSettings = s.getViewSettings();
        if (viewSettings == null){
            viewSettings = new ViewSettings();
            viewSettings.SetToDefault();
        }
        viewSettings.setCurPage(pageNumber);
        viewSettings.setPageStart();
        s.updateViewSettings(viewSettings.getId(), viewSettings);
        s.close();

        new showNextPageAsync().execute();
    }

    public void moveToPreviousPage(){
        // verify
        if (viewSettings.getCurPage() <= 0){
            Toast.makeText(parent, "You are already on the first page.", Toast.LENGTH_SHORT).show();
            return;
        }
        int newPage = viewSettings.getCurPage() - 1;

        // clean the database
        curSelBox = TRIAGETRAK;
        deleteAllReportsInTriageTrakBox();

        // clean the patientlist
        if (!patientsTriageTrak.isEmpty()){
            int size = patientsTriageTrak.size();
            while (size > 0){
                patientsTriageTrak.remove(size - 1);
                size = patientsTriageTrak.size();
            }
        }

        // start currentPage from 0
        // reset viewSetting
        // initial the curpage to 0
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        viewSettings = s.getViewSettings();
        if (viewSettings == null){
            viewSettings = new ViewSettings();
        }
        viewSettings.setCurPage(newPage);
        viewSettings.setPageStart();
        viewSettings.setPageStart();
        s.updateViewSettings(viewSettings.getId(), viewSettings);
        s.close();

        showNextPage();
    }
    public void moveToNextPage(){
        // verify
        totalRecords = Integer.valueOf(app.getCurSearchCount());
        if (viewSettings.getCurPage() >= totalRecords){
            Toast.makeText(parent, "You are already on the last page.", Toast.LENGTH_SHORT).show();
            return;
        }
        int newPage = viewSettings.getCurPage() + 1;

        // clean the database
        curSelBox = TRIAGETRAK;
        /*
        deleteAllReportsInTriageTrakBox();

        // clean the patientlist
        if (!patientsTriageTrak.isEmpty()){
            int size = patientsTriageTrak.size();
            while (size > 0){
                patientsTriageTrak.remove(size - 1);
                size = patientsTriageTrak.size();
            }
        }
        */

        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        viewSettings = s.getViewSettings();
        if (viewSettings == null){
            viewSettings = new ViewSettings();
        }
        viewSettings.setCurPage(newPage);
        viewSettings.setPageStart();
        s.updateViewSettings(viewSettings.getId(), viewSettings);
        s.close();

        showNextPage();
    }


    public void deleteAllReportsInTriageTrakBox() {
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        for (int i = 0; i < patientsTriageTrak.size(); i++){
            Patient p = patientsTriageTrak.get(i);
            s.deleteImageByPid(p.patientId);
        }
        s.deleteAllPatientsBoxType(Patient.TRIAGETRAK);
        s.close();
    }

    public void searchTriageTrak(){
        Intent i = new Intent();
        i = new Intent(parent, BoxListFragmentActivity.class);
        startActivity(i);
    }

    private void checkAllItems(boolean isChecked) {
        // Change the data
        for (int i = 0; i < lvPatient.getCount(); i++){
            Object o = lvPatient.getItemAtPosition(i);
            if (o != null){
                ItemPatientView obj_itemDetails = (ItemPatientView)o;
                obj_itemDetails.setCheckStatus(isChecked);
            }
        }
        // Change the item on view
        for (int i = 0; i < lvPatient.getChildCount(); i++){
            CheckBox cb = (CheckBox) lvPatient.getChildAt(i).findViewById(R.id.checkBoxSel);
            cb.setChecked(isChecked);
        }
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
        else if (curSelBox == TRIAGETRAK){
            patientsCur = patientsTriageTrak;
        }

        if (patientsCur.isEmpty() == true){
            progressBarPatientList.setVisibility(View.INVISIBLE);
            lvPatient.setVisibility(View.INVISIBLE);
            tvPatient.setText("No file is found.");
            tvPatient.setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<ItemPatientView> image_Patient_details = GetSearchPatientResults(curSelBox);
        adapterPatient = new ItemPatientListBaseAdapter(parent, image_Patient_details);
        lvPatient.setAdapter(adapterPatient);

        progressBarPatientList.setVisibility(View.INVISIBLE);
        tvPatient.setVisibility(View.INVISIBLE);
        lvPatient.setVisibility(View.VISIBLE);

        // may not need the foot view
        if (curSelBox == TRIAGETRAK){
            /**
             * fix the error in footerView control.
             * version 9.0.6-beta code 9000601
             */
            int total;
            if (app.getTotalCount().isEmpty()){
                total = 0;
            }
            else {
                total = Integer.valueOf(app.getTotalCount());
            }
            if (total > patientsCur.size()) {
//            if (Integer.valueOf(app.getCurSearchCount()) > viewSettings.getPageSize()){
                if (lvPatient.getFooterViewsCount() == 0) {
                    lvPatient.addFooterView(footerView);
                }
            }
            else {
                if (lvPatient.getFooterViewsCount() > 0) {
                    lvPatient.removeFooterView(footerView);
                }
            }
        }

        // Setting new scroll position
        lvPatient.setSelectionFromTop(0, 0); // Start from 0
        lvPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lvPatient.getItemAtPosition(position);
                ItemPatientView obj_itemDetails = (ItemPatientView)o;

                int curSelId = (int)obj_itemDetails.getId();
//        		String str = obj_itemDetails.getPatienId();;
//        		int rowIndex = Integer.parseInt(str);
                Toast.makeText(parent, "Selected patient is in " + obj_itemDetails.getMyZone().name + " zone.", Toast.LENGTH_SHORT).show();

//				if (curSelBox == SENT){
//					Intent i = new Intent(HomeActivity.this, PatientDisplayActivity.class);
//					i.putExtra("rowId", rowIndex);
//					startActivityForResult(i, PATIENT_DISPLAY);
//				}
//				else {

                if (curSelBox == TRIAGETRAK){
                    Intent i = new Intent(parent, PatientSearchedInfoActivity.class);
                    i.putExtra("rowId", curSelId); // must be integer
                    startActivityForResult(i, PATIENT_INFO);
                }
                else {
                    // changed in version 8.0.0
                    if (app.isTablet() == true) {
                        Intent i = new Intent(parent, PatientInfoActivity.class);
                        i.putExtra("rowId", curSelId); // must be integer
                        startActivityForResult(i, PATIENT_INFO);
                    } else {
                        Intent i = new Intent(parent, PatientInfoCellActivity.class);
                        i.putExtra("rowId", curSelId); // must be integer
                        startActivityForResult(i, PATIENT_INFO);
                    }
                }
            }

            private void StartPatientInforActivity(ItemPatientView item) {
                Toast.makeText(parent, "To be added", Toast.LENGTH_SHORT).show();
            }

            private Patient searchPatientFromList(String strPersonUUID) {
                Toast.makeText(parent, "To be added", Toast.LENGTH_SHORT).show();
                return null;
            }
            ;
        });
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
        else if (curSelBox == TRIAGETRAK){
            patientsCur = patientsTriageTrak;
        }
        else {
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

            if (curSelBox == TRIAGETRAK) {
                item_details.setPageNumber(String.valueOf(i + 1) + "/" + String.valueOf(app.getCurSearchCount()));
            }
            else {
                item_details.setPageNumber(String.valueOf(i + 1) + "/" + String.valueOf(patientsCur.size()));
            }

            results.add(item_details);
        }
        return results;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.box_detail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        switch (item.getItemId()) {
            case R.id.itemReport:
////                Report();
                break;
            case R.id.itemSetWebServer:
////                SetWebServer();
                break;
            case R.id.itemSetHospitalInfo:
////                SetHospitalInfo();
                break;
            case R.id.itemSetEventInfo:
////                SetEventInfo();
                break;
//            case R.id.itemWebService:
//            	SetWebService();
//            	break;
            case R.id.itemHome:
////                GoHome();
                break;
            case R.id.itemDelete:
                deleteReports(curSelBox);

                // 9.0.3 start
                Initialize();
                DisplayPatientList(curSelBox);
                // 9.0.3 end

                break;
//            case R.id.itemLogout:
//                logout();
//                break;
            case R.id.itemSelectWebServer:
////                selectWebServer();
                break;
            case R.id.itemSelectHospital:
////                selectHospital();
                break;
            case R.id.itemSelectEvent:
////                selectEvent();
                break;
            case R.id.itemLogin:
////                login();
                break;
            case R.id.itemContactUs:
////                ContactUs();
                break;
            case R.id.itemLogout:
////                removeDataAndLogout();
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
////                lookUserInfo();
                break;
            case R.id.itemAbout:
////                about();
                break;
            case R.id.itemQuestionAndAnswer:
////                questionAndAnswer();
                break;
            case R.id.itemChangeLog:
////                changeLog();
                break;
            case R.id.itemViewSettings:
                viewSettings();
                break;
            case R.id.itemLatency:
                testLatency();
                break;
            /*
            case R.id.itemNextPage:
                moveToNextPage();
                break;
            case R.id.itemPreviousPage:
                moveToPreviousPage();
                break;
                */
            default:
                // might add some thing here.
                break;
        }
        return true;
    }



    private void switchBottomView() {
        if (isNextViewBottomPart == false){
            lvPatient.setVisibility(View.INVISIBLE);
//                    tvPatient.setText("No file is saved on device.");
            tvPatient.setVisibility(View.INVISIBLE);
            progressBarPatientList.setVisibility(View.VISIBLE);
            isNextViewBottomPart = true;
        }
        else {
            progressBarPatientList.setVisibility(View.INVISIBLE);
            tvPatient.setVisibility(View.INVISIBLE);
            lvPatient.setVisibility(View.VISIBLE);
//                    tvPatient.setText("No file is saved on device.");
            isNextViewBottomPart = false;
        }
    }

    private void testLatency() {
        WebServer ws = new WebServer();
        if (app.getWebServerId() != -1){
            ws.setId(app.getWebServerId());

            DataSource s = new DataSource(parent, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

        Intent i = new Intent(parent, LatencyActivity.class);
        i.putExtra("webServer", ws.getWebService());
        startActivity(i);
    }

    private void viewSettings() {
        Intent i = new Intent(parent, ViewActivity.class);
        parent.startActivity(i);
    }

    private void refreshPatientList() {

        DisplayPatientList(curSelBox);
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
            Toast.makeText(parent, "No item is selected.", Toast.LENGTH_SHORT).show();
        }
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
                DataSource s = new DataSource(parent, app.getSeed());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PATIENT_INFO){
            Initialize();
            DisplayPatientList(curSelBox);
//            getActivity().getFragmentManager().popBackStack();
//            System.exit(0);
        }
        else if (requestCode == ReportPatientImageHandler.IMAGE_FIND_EX){
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(parent, "Search is canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            ReportPatientImageHandler camera = new ReportPatientImageHandler(parent);
            Image image = new Image();
            image = camera.onActivityResult(requestCode, resultCode, data);

            viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
            viewSettings.setEncodedImage(image.getEncoded());
            viewSettings.setSortBy(ViewSettings.SORT_SIMILARITY_DESC);
            // define the viewSettings
            DataSource s = new DataSource(parent, app.getSeed());
            s.open();
            s.updateViewSettings(viewSettings.getId(), viewSettings);
            s.close();

            moveToPage(0);
        }
        else if (requestCode == ReportPatientImageHandler.IMAGE_CAPTURE){ // get image back from triagepic gallery
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(parent, "Search is canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            ReportPatientImageHandler camera = new ReportPatientImageHandler(parent);
            Image image = new Image();
            image = camera.onActivityResult(requestCode, resultCode, data);

            viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
            viewSettings.setEncodedImage(image.getEncoded());
            viewSettings.setSortBy(ViewSettings.SORT_SIMILARITY_DESC);
            // define the viewSettings
            DataSource s = new DataSource(parent, app.getSeed());
            s.open();
            s.updateViewSettings(viewSettings.getId(), viewSettings);
            s.close();

            moveToPage(0);
        }
        else if (requestCode == SEARCH_BY_PHOTO){
            String returnStr = data.getExtras().getString("ENCODED_PHOTO").trim();
            if (returnStr.isEmpty()){
                Toast.makeText(parent, "Search is canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            String encodedPhoto = app.getCurEncodedImage();
            app.setCurEncodedImage("");
            viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
            viewSettings.setEncodedImage(encodedPhoto);
            viewSettings.setSortBy(ViewSettings.SORT_SIMILARITY_DESC);
            // define the viewSettings
            DataSource s = new DataSource(parent, app.getSeed());
            s.open();
            s.updateViewSettings(viewSettings.getId(), viewSettings);
            s.close();

            moveToPage(0);
        }
        else if (requestCode == BACK_FROM_FILTER){
            moveToPage(0);
        }
    }

    // Async for showNextPage()
    private class showNextPageAsync extends AsyncTask<Void, Integer, Void> {
        private String returnMsg = "";

        public showNextPageAsync() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            returnMsg = "";
            switchBottomView();
        }

        @Override
        protected Void doInBackground(Void... params) {
            returnMsg = showNextPage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            switchBottomView();
            /**
             * To display.
             */
            if (returnMsg.isEmpty() == false){
                Toast.makeText(parent, returnMsg, Toast.LENGTH_SHORT).show();
            }
            else if (patientsTriageTrak.isEmpty()) {
                Toast.makeText(parent, "No record is found.", Toast.LENGTH_SHORT).show();
            }
            else {
                String msg = "Page " + String.valueOf(viewSettings.getCurPage() + 1) + ": files from " + String.valueOf(viewSettings.getPageStart() + 1) + " to " + String.valueOf(viewSettings.getPageStart() + viewSettings.getPageSize());
                Toast.makeText(parent, msg, Toast.LENGTH_SHORT).show();
            }
//            displayPatientTrageTrakList();
            new displayPatientTrageTrakListAsync().execute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class displayPatientTrageTrakListAsync extends AsyncTask<Void, Integer, Void> {
        public displayPatientTrageTrakListAsync() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Initialize();
            DisplayPatientList(TRIAGETRAK);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (viewSettings.getCurPage() > 0) {
                scrollListViewToBottom();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void scrollListViewToBottom(){
        lvPatient.smoothScrollToPosition(lvPatient.getCount() - 1);
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
            else {
                app.setCurSearchCount("0");
            }
            moveToPage(0);
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
}
