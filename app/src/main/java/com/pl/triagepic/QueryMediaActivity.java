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
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QueryMediaActivity extends Activity {
	static final int IMAGE_PREVIEW = 1;
	/** Called when the activity is first created. */

    TriagePic app;
//    String webServer = "";

    String dateArray[];
	private int curPos = 0;
	LinearLayout layout;
	GridView gv;
	Bitmap bitmap1[];
	ContentResolver cr;
	MyAsyncTask myAsyncTask;
	Cursor cursor;

	private ProgressDialog progressDialog = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        /**
         * make changes to reduce the time
         * version 6.1.4
         */
        cr = getContentResolver();
        long timeStart = 0;
        long timeEnd = 0;
        long timeElapsed = 0;
        timeStart = System.currentTimeMillis();

        // 22 and above. Right now only Lollipop 5.1.1 use this one.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        }
        else { // 21 and lower
            cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
        }

        timeEnd = System.currentTimeMillis();
        timeElapsed = timeEnd - timeStart;
        Toast.makeText(this, "Searching images took " + String.valueOf(timeElapsed) + " ms.", Toast.LENGTH_SHORT).show();

        int cursorCount = cursor.getCount();
		bitmap1 = new Bitmap[cursorCount];
		dateArray = new String[cursorCount];

		layout = new LinearLayout(this);
//		layout.setOrientation(1);
		gv = new GridView(getApplicationContext());
		gv.setNumColumns(4);
		myAsyncTask = new MyAsyncTask();
		myAsyncTask.execute(cursor);
	}

	class MyAdapter extends BaseAdapter {

		public int getCount() {
			return dateArray.length;
		}

		public Object getItem(int pos) {
			return dateArray[pos];
		}

		public long getItemId(int pos) {
			return pos;
		}

		public View getView(int pos, View convertView, ViewGroup arg2) {
			View v;
			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();

				v = li.inflate(R.layout.image_view, null);
			} 
			else {
				v = convertView;
			}
			
			ImageView iv = (ImageView) v.findViewById(R.id.icon_image);
			iv.setImageBitmap(bitmap1[pos]);				

			return v;
		}
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
	
	@Override
	public void onBackPressed() {
        int len = dateArray.length;

	    Intent i = new Intent();
		i.putExtra("fileFullName", "");    			
		setResult(QueryMediaActivity.this.RESULT_CANCELED, i);
		QueryMediaActivity.this.finish();
		super.onBackPressed();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMAGE_PREVIEW){
			if (resultCode == ImagePreviewActivity.RESULT_CANCELED){
		    	return;
		    }
		    Intent i = new Intent();
			i.putExtra("fileFullName", dateArray[curPos]);
			setResult(QueryMediaActivity.this.RESULT_OK, i);
			QueryMediaActivity.this.finish();
		}
	}

	class MyAsyncTask extends AsyncTask<Cursor, Void, String> {

		@Override
		protected void onPreExecute() {
            progressDialog = new ProgressDialog(QueryMediaActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage("Loading images, please wait...");  
            progressDialog.setCancelable(false);  
            progressDialog.setIndeterminate(false);  
            progressDialog.show();
		}

		@Override
		protected String doInBackground(Cursor... cursor1) {
			Cursor cs = cursor1[0];
			int i = 0;
			while (cs.moveToNext()) {
//				String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
				int columnIndex = cs.getColumnIndex(MediaStore.Images.ImageColumns._ID);
				String id = cs.getString(columnIndex);
				long idC = Long.parseLong(id);
				Uri newUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, idC);

				// Get the full path
				int column_index = cs.getColumnIndex(MediaStore.Images.Media.DATA);
				String str =  cs.getString(column_index);
				Bitmap bmp = Shrinkmethod(str, 200,200);
				
				if (bmp != null){
                    bitmap1[i] = bmp;
                    dateArray[i] = str;
                    i++;
				}
			}

			return "COMPLETE";
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
        	progressDialog.dismiss();
			MyAdapter myAdapter = new MyAdapter();
			gv.setAdapter(myAdapter);
	        /**
	         * On Click event for Single Gridview Item
	         * */
	        gv.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	curPos = position;
	            	StartImagePreviewActivity(dateArray[curPos]);
	            }

				private void StartImagePreviewActivity(String file) {
					Intent i = new Intent(QueryMediaActivity.this, ImagePreviewActivity.class);
					i.putExtra("fileFullName", file);    			
					startActivityForResult(i, IMAGE_PREVIEW);	
				}
	        });			
			
			layout.addView(gv);
			setContentView(layout);
		}
	}
	
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
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
		    Toast.makeText(QueryMediaActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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