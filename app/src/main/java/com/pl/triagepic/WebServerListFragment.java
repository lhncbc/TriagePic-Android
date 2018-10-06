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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.triagepic.dummy.DummyWebServerContent;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Persons. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link WebServerDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class WebServerListFragment extends ListFragment {
    private static final int ADD_NEW = 1;

    TriagePic app;
    Activity parent;
    CheckBox ck;

    //	private Spinner hospitals;
    private DataSource s;
    private ArrayAdapter<String> adapter;
    private SharedPreferences settings;
    private ArrayList<String> webServerNameList;

    //	private Activity thisActivity;
    private String hospitalCurSel;

    private ArrayList<WebServer> webServerList;
    WebServer wbCurSel;
    int curSelWebServer = -1;

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
            label.setText(webServerList.get(position).getName());
            label.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    curSelWebServer = position;
                    mCallbacks.onItemSelected(DummyWebServerContent.ITEMS.get(position).id);
                    notifyDataSetChanged();
                }});

            // check box
            ck = (CheckBox)row.findViewById(R.id.checkBoxSel);
            if (position == curSelWebServer){
                ck.setChecked(true);
            }
            else {
                ck.setChecked(false);
            }
            ck.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    curSelWebServer = position;
                    mCallbacks.onItemSelected(DummyWebServerContent.ITEMS.get(position).id);
                    notifyDataSetChanged();
                }
            });
            return row;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            wbCurSel = webServerList.get(curSelWebServer);
            app.setWebServerId(wbCurSel.getId());
            Toast.makeText(getContext(), wbCurSel.getName() + " is selected.", Toast.LENGTH_SHORT).show();
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
    public WebServerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = TriagePic.getInstance();
        parent = getActivity();
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
            curSelWebServer = 0;
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
        app.setWebServerId(wbCurSel.getId());

        DummyWebServerContent.ITEMS.clear();
        for (int i = 0; i < webServerNameList.size(); i++){
            WebServer ws = webServerList.get(i);
              DummyWebServerContent.addItem(new DummyWebServerContent.DummyWebServerItem(
                      String.valueOf(i),
                      String.valueOf(ws.getId()),
                      ws.getName(),
                      ws.getShortName(),
                      ws.getWebService(),
                      ws.getUrl(),
                      ws.getNameSpace()
              ));
        }

        // 3. replace here
        /*
        setListAdapter(new ArrayAdapter<DummyWebServerContent.DummyWebServerItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                DummyWebServerContent.ITEMS));
                */

        // replace above
        String[] myArray = new String[webServerList.size()];
        for (int i = 0; i < webServerList.size(); i++){
            myArray[i] = webServerList.get(i).getName();
        }

        MyListAdapter myListAdapter =
                new MyListAdapter(getActivity(), R.layout.item_single_line_with_check_box, myArray);
        setListAdapter(myListAdapter);
    }

    private void createInitialWebServerTable(DataSource d) {
        WebServer w0 = new WebServer();
        w0.setName(WebServer.TT_NAME);
        w0.setShortName(WebServer.TT_SHORT_NAME);
        w0.setWebService(WebServer.TT_WEB_SERVICE);
        w0.setNameSpace(WebServer.TT_NAMESPACE);
        w0.setUrl(WebServer.TT_URL);
        w0.setId(d.createWebServer(w0));

        WebServer w1 = new WebServer();
        w1.setName(WebServer.TS_NAME);
        w1.setShortName(WebServer.TS_SHORT_NAME);
        w1.setWebService(WebServer.TS_WEB_SERVICE);
        w1.setNameSpace(WebServer.TS_NAMESPACE);
        w1.setUrl(WebServer.TS_URL);
        w1.setId(d.createWebServer(w1));

        /*
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
        w3.setName(WebServer.PL_NAME);
        w3.setShortName(WebServer.PL_SHORT_NAME);
        w3.setWebService(WebServer.PL_WEB_SERVICE);
        w3.setNameSpace(WebServer.PL_NAMESPACE);
        w3.setUrl(WebServer.PL_URL);
        w3.setId(d.createWebServer(w3));
        */

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
        mCallbacks.onItemSelected(DummyWebServerContent.ITEMS.get(position).id);
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
    // Menu sections
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.web_server_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            Toast.makeText(parent, "No selected web server to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (wbCurSel.getName().compareToIgnoreCase("TriageTrak") == 0){
            Toast.makeText(parent, "Please don't delete the default web server. It may be useful for test.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataSource d = new DataSource(parent, app.getSeed());
        d.open();
        d.deleteWebServerById(wbCurSel.getId());
        d.close();

        Initialize();
        return;
    }

    private void addNewWebServer() {
        Intent i = new Intent(parent, AddWebServerActivity.class);
        startActivityForResult(i, ADD_NEW);
    }

    private void testLatency() {
        Intent i = new Intent(parent, LatencyActivity.class);
        i.putExtra("webServer", wbCurSel.getWebService());
        parent.startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NEW){
            Initialize();
        }
    }
 }
