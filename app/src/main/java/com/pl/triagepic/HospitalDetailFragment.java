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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pl.triagepic.dummy.DummyHospitalContent;

import java.util.ArrayList;

public class HospitalDetailFragment extends android.support.v4.app.Fragment {
    TriagePic app;
    Activity parent;

    private DataSource s;
    private ArrayAdapter<String> adapter;
    private SharedPreferences settings;
    private ArrayList<String> webServerNameList;

    private ArrayList<Hospital> webServerList;
    Hospital wbCurSel;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyHospitalContent.DummyHospitalItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HospitalDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = getActivity();
        app = (TriagePic) parent.getApplication();
        app.detectMobileDevice(parent);
        app.setScreenOrientation(parent);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String curSel = getArguments().getString(ARG_ITEM_ID);
            mItem = DummyHospitalContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hospital_detail_more, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.textViewHospital)).setText(mItem.name);
//            ((TextView) rootView.findViewById(R.id.textViewId)).setText(mItem.rowIndex);
            ((TextView) rootView.findViewById(R.id.textViewShortName)).setText(mItem.shortName);
            ((TextView) rootView.findViewById(R.id.textViewStreet1)).setText(mItem.street1);
            ((TextView) rootView.findViewById(R.id.textViewStreet2)).setText(mItem.street2);
            ((TextView) rootView.findViewById(R.id.textViewCity)).setText(mItem.city);
            ((TextView) rootView.findViewById(R.id.textViewCounty)).setText(mItem.county);
            ((TextView) rootView.findViewById(R.id.textViewState)).setText(mItem.state);
            ((TextView) rootView.findViewById(R.id.textViewZip)).setText(mItem.zip);
            ((TextView) rootView.findViewById(R.id.textViewCountry)).setText(mItem.country);
            ((TextView) rootView.findViewById(R.id.textViewPhone)).setText(mItem.phone);
            ((TextView) rootView.findViewById(R.id.textViewFax)).setText(mItem.fax);
            ((TextView) rootView.findViewById(R.id.textViewEmail)).setText(mItem.email);
            ((TextView) rootView.findViewById(R.id.textViewWebSite)).setText(mItem.www);
            ((TextView) rootView.findViewById(R.id.textViewProviderId)).setText(mItem.npi);
            ((TextView) rootView.findViewById(R.id.textViewLatitude)).setText(mItem.latitude);
            ((TextView) rootView.findViewById(R.id.textViewLongitude)).setText(mItem.longitude);
            ((TextView) rootView.findViewById(R.id.textViewPidPrefix)).setText(mItem.pidPrefix);
            ((TextView) rootView.findViewById(R.id.textViewPidSuffixLengthSpecified)).setText(mItem.pidSuffixLengthSpecified);
            ((TextView) rootView.findViewById(R.id.textViewPidSuffixLength)).setText(mItem.pidSuffixLength);
        }
        // initial display
            /*
        else {
            DataSource d = new DataSource(parent, app.getSeed());
            d.open();
            if (app.getHospitalId() == -1){
                webServerList = d.getAllHospitals();
                if (webServerList.isEmpty() == true){
                    createInitialHospitalTable(d);
                    webServerList = d.getAllHospitals();
                }
                d.close();

                webServerNameList = new ArrayList<String>();
                if (webServerList.isEmpty() == false){
                    wbCurSel = webServerList.get(0);
                    app.setHospitalId(0);
                }
            }
            else {
                wbCurSel = d.getHospitalFromId(app.getHospitalId());
            }
            d.close();

            if (wbCurSel != null){

                ((TextView) rootView.findViewById(R.id.textViewHospital)).setText(mItem.name);
                ((TextView) rootView.findViewById(R.id.textViewId)).setText(mItem.rowIndex);
                ((TextView) rootView.findViewById(R.id.textViewShortName)).setText(mItem.shortName);
                ((TextView) rootView.findViewById(R.id.textViewStreet1)).setText(mItem.street1);
                ((TextView) rootView.findViewById(R.id.textViewStreet2)).setText(mItem.street2);
                ((TextView) rootView.findViewById(R.id.textViewCity)).setText(mItem.city);
                ((TextView) rootView.findViewById(R.id.textViewCounty)).setText(mItem.county);
                ((TextView) rootView.findViewById(R.id.textViewState)).setText(mItem.state);
                ((TextView) rootView.findViewById(R.id.textViewZip)).setText(mItem.zip);
                ((TextView) rootView.findViewById(R.id.textViewCountry)).setText(mItem.country);
                ((TextView) rootView.findViewById(R.id.textViewPhone)).setText(mItem.phone);
                ((TextView) rootView.findViewById(R.id.textViewFax)).setText(mItem.fax);
                ((TextView) rootView.findViewById(R.id.textViewEmail)).setText(mItem.email);
                ((TextView) rootView.findViewById(R.id.textViewWebSite)).setText(mItem.www);
                ((TextView) rootView.findViewById(R.id.textViewProviderId)).setText(mItem.npi);
                ((TextView) rootView.findViewById(R.id.textViewLatitude)).setText(mItem.latitude);
                ((TextView) rootView.findViewById(R.id.textViewLongitude)).setText(mItem.longitude);
                ((TextView) rootView.findViewById(R.id.textViewPidPrefix)).setText(mItem.pidPrefix);
                ((TextView) rootView.findViewById(R.id.textViewPidSuffixLengthSpecified)).setText(mItem.pidSuffixLengthSpecified);
                ((TextView) rootView.findViewById(R.id.textViewPidSuffixLength)).setText(mItem.pidSuffixLength);

            }
            d.close();
        }
            */
        return rootView;
    }

    private void createInitialHospitalTable(DataSource d) {
        /*
        Hospital w0 = new Hospital();
        w0.setName(Hospital.TT_NAME);
        w0.setShortName(Hospital.TT_SHORT_NAME);
        w0.setWebService(Hospital.TT_WEB_SERVICE);
        w0.setNameSpace(Hospital.TT_NAMESPACE);
        w0.setUrl(Hospital.TT_URL);
        w0.setId(d.createHospital(w0));

        Hospital w1 = new Hospital();
        w1.setName(Hospital.PL_MOBILE_NAME);
        w1.setShortName(Hospital.PL_MOBILE_SHORT_NAME);
        w1.setWebService(Hospital.PL_MOBILE_WEB_SERVICE);
        w1.setNameSpace(Hospital.PL_MOBILE_NAMESPACE);
        w1.setUrl(Hospital.PL_MOBILE_URL);
        w1.setId(d.createHospital(w1));

        Hospital w2 = new Hospital();
        w2.setName(Hospital.PL_STAGE_NAME);
        w2.setShortName(Hospital.PL_STAGE_SHORT_NAME);
        w2.setWebService(Hospital.PL_STAGE_WEB_SERVICE);
        w2.setNameSpace(Hospital.PL_STAGE_NAMESPACE);
        w2.setUrl(Hospital.PL_STAGE_URL);
        w2.setId(d.createHospital(w2));

        Hospital w3 = new Hospital();
        w3.setName(Hospital.PL_NAME);
        w3.setShortName(Hospital.PL_SHORT_NAME);
        w3.setWebService(Hospital.PL_WEB_SERVICE);
        w3.setNameSpace(Hospital.PL_NAMESPACE);
        w3.setUrl(Hospital.PL_URL);
        w3.setId(d.createHospital(w3));

        Hospital w4 = new Hospital();
        w4.setName(Hospital.TS_NAME);
        w4.setShortName(Hospital.TS_SHORT_NAME);
        w4.setWebService(Hospital.TS_WEB_SERVICE);
        w4.setNameSpace(Hospital.TS_NAMESPACE);
        w4.setUrl(Hospital.TS_URL);
        w4.setId(d.createHospital(w4));
        */
    }
}
