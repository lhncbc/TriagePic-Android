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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ZoneActivity extends Activity {
    private static final String ABOUT_LPF = "About Lost Person Finder";
	private static final String ABOUT_NLM = "About National Library of Medicine";
	private static final String ABOUT_NIH = "About National Institutes of Health";
	private static final String ABOUT_HHS = "About U.S. Department of Health & Human Services";

	protected static final ArrayList<ItemZoneView> image_details = null;
	ListView lvAboutEx;
	String strAboutCompanies[];
	ItemZoneListBaseAdapter adapterEx;

    TriagePic app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zone_list);

        app = (TriagePic) this.getApplication();
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		InitializeEx();		
		
	}

	private void InitializeEx() {
		strAboutCompanies = new String[] {
				"Green",
				"BH Green",
				"Yellow",
				"Red",
				"Gray",
				"Black"
		};
		
		ArrayList<ItemZoneView> image_details = GetSearchResults();
        lvAboutEx = (ListView) findViewById(R.id.listViewZone);
        lvAboutEx.setAdapter(new ItemZoneListBaseAdapter(ZoneActivity.this, image_details));
        
        lvAboutEx.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvAboutEx.getItemAtPosition(position);
        		ItemZoneView obj_itemDetails = (ItemZoneView)o;
        		String strName = obj_itemDetails.getName();
        		
    			if (strName.equalsIgnoreCase("black") == true){
    			    Toast.makeText(ZoneActivity.this, "Black", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("gray") == true){
    			    Toast.makeText(ZoneActivity.this, "Gray", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("green") == true){
    			    Toast.makeText(ZoneActivity.this, "Green", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("light green") == true){
    			    Toast.makeText(ZoneActivity.this, "Light Green", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("red") == true){
    			    Toast.makeText(ZoneActivity.this, "Red", Toast.LENGTH_SHORT).show();							
    			}	
    			else if (strName.equalsIgnoreCase("yellow") == true){
    			    Toast.makeText(ZoneActivity.this, "Yellow", Toast.LENGTH_SHORT).show();							
    			}	
    			else {
    			    Toast.makeText(ZoneActivity.this, "Not implemented.", Toast.LENGTH_SHORT).show();							
    			}
        	}
        });
	}

    private ArrayList<ItemZoneView> GetSearchResults(){
    	ArrayList<ItemZoneView> results = new ArrayList<ItemZoneView>();
	    	
    	ItemZoneView item_details;    	
    	for (int i = 0; i < strAboutCompanies.length; i++)
    	{
    		item_details = new ItemZoneView();
    		item_details.setName(strAboutCompanies[i]);
    		item_details.setImageNumber(i + 1);
    		results.add(item_details);
    	}
    	return results;
	}

}
