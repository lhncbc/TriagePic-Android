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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.triagepic.dummy.DummyHospitalContent;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Persons. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link HospitalDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class HospitalListFragment extends ListFragment {
    TriagePic app;
    Activity parent;
    CheckBox ck;
    private ProgressDialog progressDialog;

    //	private Spinner hospitals;
    private DataSource s;
    private ArrayAdapter<String> adapter;
    private SharedPreferences settings;
    private ArrayList<String> hospitalNameList;

    //	private Activity thisActivity;
    private String hospitalCurSel;

    private ArrayList<Hospital> hospitalList;
    Hospital hCurSel;
    int curSelHospital = -1;

    // 1. add class
    public class MyListAdapter extends ArrayAdapter<String> {
        Context myContext;

        public MyListAdapter(Context context, int textViewResourceId,
                             String[] objects) {
            super(context, textViewResourceId, objects);
            myContext = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);

            LayoutInflater inflater =
                    (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.item_single_line_with_check_box, parent, false);
            TextView label=(TextView)row.findViewById(R.id.textViewBoxName);
//            label.setText(month[position]);
            label.setText(hospitalList.get(position).name);
            label.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    curSelHospital = position;
                    mCallbacks.onItemSelected(DummyHospitalContent.ITEMS.get(position).id);
                    notifyDataSetChanged();
                }});

            // check box
            ck = (CheckBox)row.findViewById(R.id.checkBoxSel);
            if (position == curSelHospital){
                ck.setChecked(true);
            }
            else {
                ck.setChecked(false);
            }
            ck.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    curSelHospital = position;
                    mCallbacks.onItemSelected(DummyHospitalContent.ITEMS.get(position).id);
                    notifyDataSetChanged();
                }
            });
            return row;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            hCurSel = hospitalList.get(curSelHospital);
            app.setCurSelHospitalId(hCurSel.rowIndex);
            app.setCurSelHospital(hCurSel.name);
            app.setCurSelHospitalShortName(hCurSel.shortname);
            Toast.makeText(getContext(), hCurSel.name + " is selected.", Toast.LENGTH_SHORT).show();
        }
    }

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
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HospitalListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        parent = getActivity();
        app = (TriagePic) parent.getApplication();
        app.detectMobileDevice(parent);
        app.setScreenOrientation(parent);

        Initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void Initialize() {
        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        hospitalList = d.getAllHospitals();
        if (hospitalList.isEmpty() == true){
            createInitialHospitalTable(d);
            hospitalList = d.getAllHospitals();
        }
        d.close();

        hospitalNameList = new ArrayList<String>();
        if (hospitalList.isEmpty() == false){
            for (int i = 0; i < hospitalList.size(); i++){
                String s = hospitalList.get(i).name;
                hospitalNameList.add(s);
            }
        }
        // if there is no hospitals in database, set a default one.
        else {
            Hospital h = new Hospital();
            h.toDefault();
            hospitalList.add(h);
            hospitalNameList.add(h.name);
        }

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

        DummyHospitalContent.ITEMS.clear();
        for (int i = 0; i < hospitalNameList.size(); i++){
            Hospital h = hospitalList.get(i);
            String pidSuffixVariable;
            if (h.pidSuffixVariable == true){
                pidSuffixVariable = "true";
            }
            else {
                pidSuffixVariable = "false";
            }
            DummyHospitalContent.addItem(new DummyHospitalContent.DummyHospitalItem(
                    String.valueOf(i),
                    String.valueOf(h.rowIndex),
                    h.name,
                    h.shortname,
                    h.street1,
                    h.street2,
                    h.city,
                    h.county,
                    h.state,
                    h.zip,
                    h.country,
                    h.phone,
                    h.fax,
                    h.email,
                    h.www,
                    h.npi,
                    h.latitude,
                    h.longitude,
                    h.pidPrefix,
                    pidSuffixVariable,
                    String.valueOf(h.pidSuffixFixedLength)
            ));
        }

        // 3. replace here
        /*
        setListAdapter(new ArrayAdapter<DummyHospitalContent.DummyHospitalItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                DummyHospitalContent.ITEMS));
                */

        // replace above
        String[] myArray = new String[hospitalList.size()];
        for (int i = 0; i < hospitalList.size(); i++){
            myArray[i] = hospitalList.get(i).name;
        }

        MyListAdapter myListAdapter =
                new MyListAdapter(getActivity(), R.layout.item_single_line_with_check_box, myArray);
        setListAdapter(myListAdapter);
    }

    private void createInitialHospitalTable(DataSource d) {
        Hospital h = new Hospital();
        h.toDefault();
        d.createHospital(h);
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
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(DummyHospitalContent.ITEMS.get(position).id);
        app.setCurSelHospitalId(Integer.getInteger(DummyHospitalContent.ITEMS.get(position).rowIndex));
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

    /*
    private void deleteSelHospital() {
        if (hCurSel == null){
            return;
        }

        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        d.deleteHospitalById(hCurSel.getId());
        d.close();

        Initialize();
        return;
    }
    */

    /*
    private void addNewHospital() {
        Intent i = new Intent(parent, AddHospitalActivity.class);
        startActivityForResult(i, ADD_NEW);
    }
    */

    /*
    private void testLatency() {
        Intent i = new Intent(parent, LatencyActivity.class);
        i.putExtra("hospital", hCurSel.getWebService());
        parent.startActivity(i);
    }
    */

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW){
            Initialize();
        }
    }
    */

    // Menu sections
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (WebServer.AmIConnected(parent) == true) {
                    new getHospitalListAsyncTask().execute();
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

            DataSource s = new DataSource(parent, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

        Intent i = new Intent(parent, LatencyActivity.class);
        i.putExtra("webServer", ws.getWebService());
        startActivity(i);
    }

    //To use the AsyncTask, it must be subclassed
    private class getHospitalListAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(parent);
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

    // Refreshing the hospital from web server and update the local database file.
    private void getHospitalList() {
        long webServerId = app.getWebServerId();
//		String userName = app.getUsername();
//		String passWord = app.getPassword();

        DataSource s = new DataSource(parent, app.getSeed());
        WebServer wb = new WebServer();
        s.open();
        wb = s.getWebServerFromId(webServerId);
        s.close();

        if (wb == null){
            wb = new WebServer();
        }
        wb.setToken(app.getToken());
        String returnMsg = wb.callUpdateHospitalInformation(parent, app.getSeed());
    }
}
