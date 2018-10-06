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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pl.triagepic.dummy.DummyEventContent;

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a {@link EventListFragmentActivity}
 * in two-pane mode (on tablets) or a {@link EventDetailFragmentActivity}
 * on handsets.
 */
public class EventDetailFragment extends Fragment {
    TriagePic app;
    Activity parent;

    String curSelDes = "";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyEventContent.DummyEventItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {
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
            curSelDes = getArguments().getString(ARG_ITEM_ID);
            mItem = DummyEventContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            /**
             * Initialization on the detail side.
             * Added in version 9.0.5.
             */
            if (curSelDes.equalsIgnoreCase("-1")){
                for (int i = 0; i < DummyEventContent.ITEM_MAP.size(); i++){
                    mItem = DummyEventContent.ITEM_MAP.get(String.valueOf(i));
                    if (app.getCurSelEvent().equalsIgnoreCase(mItem.name)){
                        break;
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_detail_more, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.textViewEvent)).setText(mItem.name);
            ((TextView) rootView.findViewById(R.id.textViewShortName)).setText(mItem.shortname);
            ((TextView) rootView.findViewById(R.id.textViewIncidentId)).setText(mItem.incident_id);
            ((TextView) rootView.findViewById(R.id.textViewDate)).setText(mItem.date);
            ((TextView) rootView.findViewById(R.id.textViewType)).setText(mItem.type);
            ((TextView) rootView.findViewById(R.id.textViewVisibility)).setText(mItem.closed.compareToIgnoreCase("0") == 0 ? Event.REPORTING_OPEN : Event.REPORTING_CLOSED);
            ((TextView) rootView.findViewById(R.id.textViewAddress)).setText(mItem.street);
            ((TextView) rootView.findViewById(R.id.textViewLatitude)).setText(mItem.latitude);
            ((TextView) rootView.findViewById(R.id.textViewLongitude)).setText(mItem.longitude);
            ((TextView) rootView.findViewById(R.id.textViewAddress)).setText(mItem.street);
      }
        return rootView;
    }

}
