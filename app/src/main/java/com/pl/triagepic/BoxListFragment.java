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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.triagepic.Result.SearchResult;
import com.pl.triagepic.dummy.DummyBoxContent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A list fragment representing a list of Persons. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link BoxDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link com.pl.triagepic.BoxListFragment.Callbacks}
 * interface.
 */
public class BoxListFragment extends ListFragment {
    Context myContext;
    MyListAdapter myListAdapter;

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

    private static final String CHANGE_LOG_FILENAME = "log.txt";

    TriagePic app;
    Activity parent;

    boolean bAuthStatus = false;
    String webServer = "";
    long webServerId = 0;
    String username = "";
    String password = "";
    String token = "";
    long curSelHospitalId = -1;
    long curSelEventId = -1;

    private List<Patient> patientsDeleted = new ArrayList<Patient>();
    private List<Patient> patientsDrafts = new ArrayList<Patient>();
    private List<Patient> patientsSent = new ArrayList<Patient>();
    private List<Patient> patientsOutbox = new ArrayList<Patient>();
    private List<Patient> patientsTriageTrak = new ArrayList<Patient>();

    private static final int DRAFTS = 0;
    private static final int SENT = 1;
    private static final int OUTBOX = 2;
    private static final int DELETED = 3;
    private static final int TRIAGETRAK = 4;

    private static final String TRIAGETRAK_TOTAL_COUNT = "TriageTrak (total)";

    private int curSelBox = 0;
    ProgressDialog progressDialog = null;

    protected static final ArrayList<ItemBoxView> image_Box_details = null;
    ListView lvBox;
    private static String [] strBoxes = new String[] {
            "Drafts",
            "Sent",
            "Outbox",
            "Deleted",
            TRIAGETRAK_TOTAL_COUNT
    };
    ItemBoxListBaseAdapter adapterEx;

    // 1. add class
    public class MyListAdapter extends ArrayAdapter<String> {


        public MyListAdapter(Context context, int textViewResourceId,
                             String[] objects) {
            super(context, textViewResourceId, objects);
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater =
                    (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.item_box, parent, false);
            TextView label=(TextView)row.findViewById(R.id.textViewBoxName);
            label.setText(DummyBoxContent.ITEMS.get(position).content);
            TextView number = (TextView)row.findViewById(R.id.textViewBoxNumber);

            //Customize your icon here
            DataSource d = new DataSource(myContext, app.getSeed());
            switch (position){
                case DRAFTS: // drafts
                    d.open();
                    patientsDrafts = d.getAllPatientsDraftsDesc();
                    d.close();
                    number.setText(String.valueOf(patientsDrafts.size()));
                    break;
                case SENT: // sent
                    d.open();
                    patientsSent = d.getAllPatientsSentDesc();
                    d.close();
                    number.setText(String.valueOf(patientsSent.size()));
                    break;
                case OUTBOX: // in box
                    d.open();
                    patientsOutbox = d.getAllPatientsOutboxDesc();
                    d.close();
                    number.setText(String.valueOf(patientsOutbox.size()));
                    break;
                case DELETED: // deleted
                    d.open();
                    patientsDeleted = d.getAllPatientsDeletedDesc();
                    d.close();
                    number.setText(String.valueOf(patientsDeleted.size()));
                    break;
                case TRIAGETRAK:
                    // get the search count.
                    if (app.getCurSearchCount().isEmpty() == true) {
                        number.setText("0");
                    }
                    else {
                        number.setText(app.getTotalCount());
                    }
                    break;
                default:
                    break;

            }

            return row;
        }
    }

    // 2.

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link com.pl.triagepic.BoxListFragment.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
            String ID = id;
        }
    };



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BoxListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        GetSearchResults();
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

        // get data from local database.
        DataSource s = new DataSource(parent, app.getSeed());
        s.open();
        long authId = s.getAuthId();
        if (authId == -1){
            s.createAuthentication(username, password, token, (int)webServerId, (int)curSelHospitalId, (int)curSelEventId);
        }
        else {
            username = s.getUsername();
            password = s.getPassword();
            token = s.getToken();
            webServerId = s.getWebServerId();
            curSelHospitalId = s.getHospitalId();
            curSelEventId = s.getEventId();
        }
        s.close();

        app.setUsername(username);
        app.setPassword(password);
        app.setToken(token);
        if (!app.getPassword().isEmpty() && !app.getToken().isEmpty()){
            app.setAuthStatus(true);
        }
        else {
            app.setAuthStatus(false);
        }

        // change isLogin
        if (isLogin() == false){
            login();
        }

        app.setWebServerId(webServerId);
        app.setCurSelHospitalId(curSelHospitalId);
        app.setCurSelEventId(curSelEventId);

        // get hospital by hospital ID - start
        s = new DataSource(parent, app.getSeed());
        s.open();
        ArrayList<Hospital> hospitalList = s.getAllHospitals();
        if (hospitalList.isEmpty() == true){
            Hospital h = new Hospital();
            h.toDefault();
            s.createHospital(h);
            hospitalList = s.getAllHospitals();
        }
        s.close();

        int curSelHospital = -1;
        Hospital hCurSel;
        long hospitalIdCurSel = app.getCurSelHospitalId();
        if (hospitalIdCurSel == -1 && hospitalList.size() > 0){
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

        if (curSelHospital == -1 && hospitalList.size() == 0){
            curSelHospital = 0;
            app.setCurSelHospitalId(0);
            app.setCurSelHospital("");
            app.setCurSelHospitalShortName("");
        }
        else {
            hCurSel = hospitalList.get(curSelHospital);
            app.setCurSelHospitalId(hCurSel.rowIndex);
            app.setCurSelHospital(hCurSel.name);
            app.setCurSelHospitalShortName(hCurSel.shortname);
        }
        // get hospital by hospital ID - end

        // get event by event ID - start
        s = new DataSource(parent, app.getSeed());
        s.open();
        ArrayList<Event> eventList = s.getAllEvents();
        if (eventList.isEmpty() == true) {
            Event e = new Event();
            e.toDefault();
            s.createEvent(e);
            eventList = s.getAllEvents();
        }
        s.close();

        Event eventCurSel = null;
        int curSelEventPos = -1;
        long eventIdCurSel = app.getCurSelEventId();
        if (eventIdCurSel == -1 && eventList.size() > 0) {
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

        InitializeData();

        // replace above
        myListAdapter = new MyListAdapter(getActivity(), R.layout.item_box, strBoxes);
        setListAdapter(myListAdapter);

        if (app.getCurSearchCount().isEmpty()){
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
    }

    private void InitializeData() {
        verifyPatients();

        loadPatients(DRAFTS);
        loadPatients(OUTBOX);
        loadPatients(SENT);
        loadPatients(DELETED);

        if (started == true){
            started = false;

            // always come to start up
            curSelBox = DRAFTS;
        }
    }

    // this is added in in case of some records are bad.
    // added in version 9.0.0
    private void verifyPatients() {
        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        d.verifyAllPatientsDesc();
        d.close();
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

    private boolean isLogin(){
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

    }

    private void InitializeView(View v) {
        lvBox = (ListView) v.findViewById(R.id.listViewBox);
        ArrayList<ItemBoxView> image_Box_details = GetSearchResults();
        lvBox.setAdapter(new ItemBoxListBaseAdapter(parent, image_Box_details));
//		lvBox.setTextFilterEnabled(true);
        lvBox.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvBox.setItemChecked(curSelBox, true);

        lvBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lvBox.getItemAtPosition(position);
                ItemBoxView obj_itemDetails = (ItemBoxView)o;
                String strName = obj_itemDetails.getName();

                if (strName.equalsIgnoreCase("Deleted") == true){
                    curSelBox = DELETED;
                    Toast.makeText(parent, "You are selecting \"Deleted\".", Toast.LENGTH_SHORT).show();
                }
                else if (strName.equalsIgnoreCase("Drafts") == true){
                    curSelBox = DRAFTS;
                    Toast.makeText(parent, "You are selecting \"Drafts\".", Toast.LENGTH_SHORT).show();
                }
                else if (strName.equalsIgnoreCase("Sent") == true){
                    curSelBox = SENT;
                    Toast.makeText(parent, "You are selecting \"Sent\".", Toast.LENGTH_SHORT).show();
                }
                else if (strName.equalsIgnoreCase("Outbox") == true){
                    curSelBox = OUTBOX;
                    Toast.makeText(parent, "You are selecting \"Outbox\".", Toast.LENGTH_SHORT).show();
                }
                else if (strName.equalsIgnoreCase(TRIAGETRAK_TOTAL_COUNT) == true){
                    curSelBox = TRIAGETRAK;
                    Toast.makeText(parent, "You are selecting \"TriageTrak\".", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(parent, "No selection.", Toast.LENGTH_SHORT).show();
                    return; // no change
                }
                // no async no crash ?
            }
        });
    }

    private ArrayList<ItemBoxView> GetSearchResults(){
        ArrayList<ItemBoxView> results = new ArrayList<ItemBoxView>();

        ItemBoxView item_details;

        item_details = new ItemBoxView();
        item_details.setName("Add New");
        item_details.setNumber(100);
        item_details.setImageId(0);
        results.add(item_details);

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
            else if (name.equalsIgnoreCase(TRIAGETRAK_TOTAL_COUNT) == true){
                if (app.getCurSearchCount().isEmpty()){
                    item_details.setNumber(0);
                }
                else {
                    item_details.setNumber(Integer.valueOf(app.getCurSearchCount()));
                }
            }
            item_details.setImageId(i);
            results.add(item_details);
        }
        return results;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        int size = 0;

        if (position == 0){
            return;
        }
        position--;

        switch (position){
            case DRAFTS:
                size = patientsDrafts.size();
                break;
            case SENT:
                size = patientsSent.size();
                break;
            case OUTBOX:
                size = patientsOutbox.size();
                break;
            case DELETED:
                size = patientsDeleted.size();
                break;
            case TRIAGETRAK:
                size = patientsTriageTrak.size();
                break;
        }

        // Small change to correct the error when clicking on empty box. It still needs refresh.
        // Changes are made in version 8.0.3
        if (size == 0 && position != TRIAGETRAK) {
            Toast.makeText(parent, strBoxes[position] + " is empty.", Toast.LENGTH_SHORT).show();
        }
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(DummyBoxContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Instantiate footerView using a LayoutInflater and add to listView
        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.header_view_add_new, null, false);
        Button addNew = (Button) headerView.findViewById(R.id.buttonAddNew);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Report();
            }
        });

        getListView().addHeaderView(headerView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.box_list_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

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
            case R.id.itemHome:
                GoHome();
                break;
            case R.id.itemDelete: // not implement here
                refreshListBox();
                return false;
            case R.id.itemLatency:
                testLatency();
                break;
//            case R.id.itemLogout:
//                logout();
//                break;
            case R.id.itemSelectWebServer:
                selectWebServer();
                break;
            case R.id.itemSelectHospital:
                selectHospital();
                break;
            case R.id.itemSelectEvent:
                selectEvent();
                break;
            case R.id.itemLogin:
                login();
                break;
            case R.id.itemContactUs:
                ContactUs();
                break;
            case R.id.itemLogout:
                removeDataAndLogout();
                break;
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
            case R.id.itemFilterSettings:
                filterSettings();
                break;
            case R.id.itemGetTriageTrakCount:
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
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
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
        return sr;
    }

    private void viewSettings() {
        Intent i = new Intent(parent, ViewActivity.class);
        parent.startActivity(i);
    }

    private void filterSettings() {
        Intent i = new Intent(parent, FiltersActivity.class);
        parent.startActivity(i);
    }

    private void writeChangeLog() {
        String data = generateChangeLogData();
        System.out.println(data);
        generateChangeLogFileOnSD(data);
    }

    private String generateChangeLogData() {
        final ChangeLogDialog changeLog = new ChangeLogDialog(parent);
        return changeLog.getHTML();
    }

    public static void generateChangeLogFileOnSD(String htmlString){
        String body = Html.fromHtml(htmlString).toString();
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "TriagePic");
            if (!root.exists()){
                root.mkdirs();
            }
            File file = new File(root, CHANGE_LOG_FILENAME);
            file.setReadable(true);
            FileWriter writer = new FileWriter(file);
            writer.write(body);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectHospital() {
        Intent i = new Intent(parent, HospitalListFragmentActivity.class);
        i.putExtra("hospital", 0);
        parent.startActivity(i);
    }

    private void selectEvent() {
        Intent i = new Intent(parent, EventListFragmentActivity.class);
        i.putExtra("event", 0);
        startActivityForResult(i, SELECT_EVENT);
    }

    private void selectWebServer() {
        Intent i = new Intent(parent, WebServerListFragmentActivity.class);
        i.putExtra("webServer", 0);
        parent.startActivity(i);
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
            Toast.makeText(myContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

    private void changeLog() {
        Intent i = new Intent(parent, ChangeLogActivity.class);
        startActivity(i);
    }

    private void questionAndAnswer() {
//        Intent i = new Intent(myContext, QuestionAndAnswerActivity.class);
        Intent i = new Intent(myContext, QaListFragmentActivity.class);
        startActivity(i);
    }

    private void about() {
        Intent openAbout = new Intent(myContext, About.class);
        startActivity(openAbout);
    }

    private void lookUserInfo(){
        String msg = "Current user is \"" + app.getUsername() + "\".";
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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
        Intent i = new Intent(myContext, LpWebPuuidActivity.class);
        String url = "https://removed.com/";
        i.putExtra("url", url);
        startActivity(i);
    }

    private void setIdleTime() {
        final CharSequence[] items = { "5 minutes", "10 minutes", "15 minutes (default)", "20 minutes", "30 minutes" };
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Select Idle Time");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                curSelItem = item;
                Toast.makeText(myContext, items[item], Toast.LENGTH_SHORT).show();
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
                Toast.makeText(myContext, "Select idle time " + Long.toString(minuts) + " minutes.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(myContext, "Cancel", Toast.LENGTH_SHORT).show();
                curSelItem = -1;
                Toast.makeText(myContext, "No change", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void encryptImages(){
        if (app.getIsImageEncrypted() == false){
            long start, end, elapse;
            start = System.currentTimeMillis();
            Image img = new Image();
            img.EncryptAllImages(myContext, app.getSeed());
            end = System.currentTimeMillis();
            elapse = end - start;
            String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
            Log.i("encryptImages", msg);
            app.setIsImageEncrypted(true);
        }
    }

    public void deleteImages(){
        long start, end, elapse;
        start = System.currentTimeMillis();
        Image img = new Image();
        img.DeleteAllImages(myContext);
        end = System.currentTimeMillis();
        elapse = end - start;
        String msg = "Elapse time " + Long.toString(elapse) + " seconds.";
        Log.i("deleteImages", msg);
    }

    private void removeDataAndLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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
    }

    protected void cleanAllTables() {
        // clean all the patient's files.
        DataSource s = new DataSource(myContext, app.getSeed());
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
        DataSource s = new DataSource(myContext, app.getSeed());
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
        Toast.makeText(myContext, "Good bye!", Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    private void login() {
        if (isLogin() == false){
            Intent i = new Intent(parent, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY);
            return;
        }
        String name = app.getUsername();
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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
            Toast.makeText(myContext, "You are already logged out.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
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

            DataSource s = new DataSource(myContext, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

        Intent i = new Intent(myContext, LatencyActivity.class);
        i.putExtra("webServer", ws.getWebService());
        startActivity(i);
    }

    private void Latency() {
        Intent i = new Intent(myContext, Webservice.class);
        i.putExtra("ping", true);
        myContext.startService(i);
    }

    private void SetEventInfo() {
        Intent i = new Intent(myContext, EventActivity.class);
        startActivity(i);
    }

    private void SetHospitalInfo() {
        Intent i = new Intent(myContext, HospitalListFragmentActivity.class);
        startActivity(i);
    }

    private void SetWebServer() {
        Intent i = new Intent(myContext, WebServerActivity.class);
        startActivity(i);
    }

    private void GoHome() {
        Intent i = new Intent(myContext, HomeActivity.class);
        startActivity(i);
    }

    private void Report() {

        BoxListFragmentActivity f = (BoxListFragmentActivity)super.getActivity();

        if (f.getTwoPane() == true){
            Intent i = new Intent(myContext, ReportActivity.class);
            startActivityForResult(i, ADD_NEW);
        }
        else {
            Intent i = new Intent(myContext, ReportCellActivity.class);
            startActivityForResult(i, ADD_NEW);
        }
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
            progressDialog = new ProgressDialog(myContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Exiting, please wait...");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPLASH_SCREEN){
        }
        else if (requestCode == ADD_NEW){
            InitializeData();
            refreshListBox();
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            // this is to avoid stay on slave fragment
            // removed in 9.0.1
        }
        else if (requestCode == PATIENT_INFO){
            InitializeData();
            refreshListBox();
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            // removed in version 9.0.1
        }

        else if (requestCode == LOGIN_ACTIVITY){
            if (app.getAuthStatus()) {
                Toast.makeText(parent, "Welcome " + app.getUsername() + "!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(parent, "Login failed. Exiting now!", Toast.LENGTH_SHORT).show();
                new ExitAsyncTask().execute();
            }
        }
        else if (requestCode == SELECT_HOSPITAL){
            String msg = "You have selected hospital: " + app.getCurSelHospital() + "\nNow will need to make the second selection: \n\tEvent";
            AlertDialog.Builder builder = new AlertDialog.Builder(parent);
            builder.setIcon(R.drawable.triagepic_small_icon);
            builder.setMessage(msg)
                    .setTitle("Welcome " + app.getUsername() + "!")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(parent, "You are selecting event...", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(parent, EventListFragmentActivity.class);
                            startActivityForResult(i,SELECT_EVENT);
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            alert.setCancelable(false); // V 7.0.2. No close. It is necessary to have this line.
        }
        else if (requestCode == SELECT_EVENT){
            String u = app.getUsername();
            String p = app.getPassword();
            String t = app.getToken();
            Boolean a = app.getAuthStatus();
            long wid = app.getWebServerId();
            long hid = app.getCurSelHospitalId();
            long eid = app.getCurSelEventId();

            if (isLogin()){
                DataSource s = new DataSource(parent, app.getSeed());
                s.open();
                long authId = s.getAuthId();
//                s.updateUsernamePassword(u, p, t, (int)wid, (int)hid, (int)eid, authId);
                s.setEventId((int) eid, (int) authId);
                s.close();
            }

            saveCredentials();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshListBox(); // 9.0.3
    }

    private void refreshListBox() {
        myListAdapter.notifyDataSetChanged();
    }

    private void saveCredentials() {
        return; // not in use
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action != null && action.equals("updateBoxList")) {
                // perform your update
                InitializeData();
                refreshListBox();
            }
        }
    };

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
            if (searchResult.getErrorCode() == "0"){
                app.setCurSearchCount(searchResult.getRecordsFound());
            }
            GetSearchResults();
        }
    }
}
