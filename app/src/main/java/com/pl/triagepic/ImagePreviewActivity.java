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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * preview the image to add to the missing people report.
 */

public class ImagePreviewActivity extends Activity implements View.OnClickListener{
	static final int IMAGE_PREVIEW = 1;
	
    TriagePic app;
    String webServer = "";

    ImageView imageViewlarge;
	Button buttonSelect;
    Button buttonCancel;
	String imageFullName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);
		
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

//		webServer = app.getWebServer();

		Initialize();
	}

	private void Initialize() {
		imageViewlarge = (ImageView) findViewById(R.id.imageViewlarge);
        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        buttonSelect.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

	    Intent sender = getIntent();
	    imageFullName = sender.getExtras().getString("fileFullName").trim();
	    	    
        Bitmap bmpPhoto = Shrinkmethod(imageFullName, 600,600);
        imageViewlarge.setImageBitmap(bmpPhoto);
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

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.buttonSelect:
			Select();
            break;
            case R.id.buttonCancel:
                CancelSelectedImage();
                break;
            default:
                break;


		}
	}

    private void CancelSelectedImage() {
        Intent i = new Intent();
//		i.putExtra("resultFromImageView", "");
        setResult(ImagePreviewActivity.this.RESULT_CANCELED, i);
        ImagePreviewActivity.this.finish();
    }

    @Override
	public void onBackPressed() {
		super.onBackPressed();
	    Intent i = new Intent();
//		i.putExtra("resultFromImageView", "");    			
		setResult(ImagePreviewActivity.this.RESULT_CANCELED, i);
		ImagePreviewActivity.this.finish();
	}

	private void Select() {
	    Intent i = new Intent();
//		i.putExtra("resultFromImageView", imageFullName);    			
		setResult(ImagePreviewActivity.this.RESULT_OK, i);
		ImagePreviewActivity.this.finish();
	}
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        return true;
	}

    // Email to us
	private void ContactUs() {
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
		    Toast.makeText(ImagePreviewActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	private String getDiviceInfo() {
		// Get the info first
		String s="\n\n\n\n\n\n\n\nMy Device Info:";
		s += "\nModel: " + getManafacturer();
		s += "\nAndroid Ver: " + Build.VERSION.RELEASE;
		s += "\nKernel Ver: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")";
		s += "\nBuild Num: " + Build.ID;
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
    
}
