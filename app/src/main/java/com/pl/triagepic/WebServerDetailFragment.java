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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pl.triagepic.dummy.DummyWebServerContent;

import java.util.ArrayList;

/**
 * A fragment representing a single Person detail screen.
 * This fragment is either contained in a {@link WebServerListFragmentActivity}
 * in two-pane mode (on tablets) or a {@link WebServerDetailFragmentActivity}
 * on handsets.
 */

public class WebServerDetailFragment extends Fragment{
    TriagePic app;
    Activity parent;

    private DataSource s;
    private ArrayAdapter<String> adapter;
    private SharedPreferences settings;
    private ArrayList<String> webServerNameList;

    private ArrayList<WebServer> webServerList;
    WebServer wbCurSel;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyWebServerContent.DummyWebServerItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebServerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = TriagePic.getInstance();
        parent = getActivity();
        app.detectMobileDevice(parent);
        app.setScreenOrientation(parent);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String curSel = getArguments().getString(ARG_ITEM_ID);
            mItem = DummyWebServerContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_server_detail_more, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.textViewWebServer)).setText(mItem.name);
            ((TextView) rootView.findViewById(R.id.textViewId)).setText(mItem.webServerId);
            ((TextView) rootView.findViewById(R.id.textViewShortName)).setText(mItem.shortName);
            ((TextView) rootView.findViewById(R.id.textViewWebService)).setText(mItem.webService);
            ((TextView) rootView.findViewById(R.id.textViewNameSpace)).setText(mItem.nameSpace);
            ((TextView) rootView.findViewById(R.id.textViewUrl)).setText(mItem.url);
        }
        // initial display
        else {
            DataSource d = new DataSource(parent, app.getSeed());
            d.open();
            if (app.getWebServerId() == -1){
                webServerList = d.getAllWebServers();
                if (webServerList.isEmpty() == true){
                    createInitialWebServerTable(d);
                    webServerList = d.getAllWebServers();
                }
                d.close();

                webServerNameList = new ArrayList<String>();
                if (webServerList.isEmpty() == false){
                    wbCurSel = webServerList.get(0);
                    app.setWebServerId(0);
                }
            }
            else {
                wbCurSel = d.getWebServerFromId(app.getWebServerId());
            }
            d.close();

            if (wbCurSel != null){
                ((TextView) rootView.findViewById(R.id.textViewWebServer)).setText(wbCurSel.getName());
                ((TextView) rootView.findViewById(R.id.textViewId)).setText(String.valueOf(wbCurSel.getId()));
                ((TextView) rootView.findViewById(R.id.textViewShortName)).setText(wbCurSel.getShortName());
                ((TextView) rootView.findViewById(R.id.textViewWebService)).setText(wbCurSel.getWebService());
                ((TextView) rootView.findViewById(R.id.textViewNameSpace)).setText(wbCurSel.getNameSpace());
                ((TextView) rootView.findViewById(R.id.textViewUrl)).setText(wbCurSel.getUrl());
            }
            d.close();
        }
        return rootView;
    }

    private void createInitialWebServerTable(DataSource d) {
        WebServer w0 = new WebServer();
        w0.setName(WebServer.TT_NAME);
        w0.setShortName(WebServer.TT_SHORT_NAME);
        w0.setWebService(WebServer.TT_WEB_SERVICE);
        w0.setNameSpace(WebServer.TT_NAMESPACE);
        w0.setUrl(WebServer.TT_URL);
        w0.setId(d.createWebServer(w0));
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

        WebServer w4 = new WebServer();
        w4.setName(WebServer.TS_NAME);
        w4.setShortName(WebServer.TS_SHORT_NAME);
        w4.setWebService(WebServer.TS_WEB_SERVICE);
        w4.setNameSpace(WebServer.TS_NAMESPACE);
        w4.setUrl(WebServer.TS_URL);
        w4.setId(d.createWebServer(w4));
        */
    }
}
