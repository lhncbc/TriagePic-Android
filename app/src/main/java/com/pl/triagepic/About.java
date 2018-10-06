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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class About extends Activity {
    private static final String ABOUT_TRIAGEPIC = "About TriagePic®";
    private static final String ABOUT_TRIAGETRAK = "About TriageTrak";
    private static final String ABOUT_LPF = "About Lost Person Finder";
	private static final String ABOUT_NLM = "About National Library of Medicine";
	private static final String ABOUT_NIH = "About National Institutes of Health";
    private static final String ABOUT_HHS = "About U.S. Department of Health & Human Services";
    private static final String ABOUT_BHEPP = "About Bethesda Hospitals' Emergency Preparedness Partnership";
    private static final String ABOUT_SUBURBAN = "About Suburban Hospital";
    private static final String ABOUT_WALTER_REED = "About Walter Reed National Military Medical Center";
    private static final String ABOUT_CLINIC = "About National Institutes of Health Clinical Center";
    private static final String ABOUT_SAHANA = "About Sahana Software Foundation";

	protected static final ArrayList<ItemAboutView> image_details = null;
	ListView lvAboutEx;
	String strAboutCompanies[];
	ItemAboutListBaseAdapter adapterEx;
	TriagePic app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		setContentView(R.layout.about);

		InitializeEx();		
		
	}

	private void InitializeEx() {
		strAboutCompanies = new String[] {
                ABOUT_TRIAGEPIC,
                ABOUT_TRIAGETRAK,
				ABOUT_LPF,
				ABOUT_NLM,
                ABOUT_CLINIC,
				ABOUT_NIH,
				ABOUT_HHS,
                ABOUT_BHEPP,
                ABOUT_SUBURBAN,
                ABOUT_WALTER_REED,
                ABOUT_SAHANA
		};

        ArrayList<ItemAboutView> image_details = GetSearchResults();
        lvAboutEx = (ListView) findViewById(R.id.lvAboutEx);
        lvAboutEx.setAdapter(new ItemAboutListBaseAdapter(About.this, image_details));
        
        lvAboutEx.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvAboutEx.getItemAtPosition(position);
        		ItemAboutView obj_itemDetails = (ItemAboutView)o;
        		String strName = obj_itemDetails.getName();
        		
    			if (strName.equalsIgnoreCase(ABOUT_TRIAGEPIC) == true){
    				AboutTriagePic();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_TRIAGETRAK) == true){
    				AboutTriageTrak();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_LPF) == true){
    				AboutUs();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_NLM) == true){
    				AboutNLM();
    			}
                else if (strName.equalsIgnoreCase(ABOUT_NIH) == true){
                    AboutNIH();
                }
    			else if (strName.equalsIgnoreCase(ABOUT_HHS) == true){
    				AboutHHS();
    			}
                else if (strName.equalsIgnoreCase(ABOUT_BHEPP) == true){
                    AboutBhepp();
                }
                else if (strName.equalsIgnoreCase(ABOUT_SUBURBAN) == true){
                    AboutSuburban();
                }
                else if (strName.equalsIgnoreCase(ABOUT_WALTER_REED) == true){
                    AboutWalterReed();
                }
                else if (strName.equalsIgnoreCase(ABOUT_CLINIC) == true){
                    AboutClinic();
                }
                else if (strName.equalsIgnoreCase(ABOUT_SAHANA) == true){
                    AboutSahana();
                }
                else if (strName.equalsIgnoreCase("Contact Us") == true){
    				ContactUs();			
    			}
    			else {
    			    Toast.makeText(About.this, "Not implemented.", Toast.LENGTH_SHORT).show();							
    			}
        	}



            private void AboutBhepp() {
       			Intent i = new Intent(About.this, AboutBhepp.class);
    			startActivity(i);
			}

			private void ContactUs() {
    		}

    		private void AboutNIH() {
    			Intent i = new Intent(About.this, AboutNih.class);
    			startActivity(i);																					
    		}

    		private void AboutHHS() {
    			Intent i = new Intent(About.this, AboutHhs.class);
    			startActivity(i);																		
    			
    		}

    		private void AboutNLM() {
    			Intent i = new Intent(About.this, AboutNlm.class);
    			startActivity(i);
    		}

    		private void AboutUs() {
    			Intent i = new Intent(About.this, AboutLpf.class);
    			startActivity(i);															
    		}

    		private void AboutTriageTrak() {
    			Intent i = new Intent(About.this, AboutTriageTrak.class);
    			startActivity(i);																		
    		}

    		private void AboutTriagePic() {
    			Intent i = new Intent(About.this, AboutTriagePicActivity.class);
    			startActivity(i);
    		}

            private void AboutSuburban() {
                Intent i = new Intent(About.this, AboutSuburbanActivity.class);
                startActivity(i);
            }

            private void AboutWalterReed() {
                Intent i = new Intent(About.this, AboutWalterReedActivity.class);
                startActivity(i);
            }

            private void AboutClinic() {
                Intent i = new Intent(About.this, AboutClinicActivity.class);
                startActivity(i);
            }

            private void AboutSahana() {
                Intent i = new Intent(About.this, AboutSahanaActivity.class);
                startActivity(i);
            }



        });				
	}


    private ArrayList<ItemAboutView> GetSearchResults(){
    	ArrayList<ItemAboutView> results = new ArrayList<ItemAboutView>();
	    	
    	ItemAboutView item_details;    	
    	for (int i = 0; i < strAboutCompanies.length; i++)
    	{
    		item_details = new ItemAboutView();
    		item_details.setName(strAboutCompanies[i]);
    		item_details.setImageNumber(i + 1);
    		results.add(item_details);
    	}
    	return results;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (app.isTablet() == true){
			return;
		}

		String currentOrientation = "";
		int orientation = _getScreenOrientation();
		if (orientation == Configuration.ORIENTATION_LANDSCAPE){
			currentOrientation = "orientation is landscape";
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else if (orientation == Configuration.ORIENTATION_PORTRAIT){
			currentOrientation = "orientation is portrait";			
		}
		else {
			currentOrientation = "orientation is unknown";						
		}
		
	    Toast.makeText(this, currentOrientation, Toast.LENGTH_SHORT).show();		
	}
	
	private int _getScreenOrientation(){    
	    return getResources().getConfiguration().orientation;
	}	
}
