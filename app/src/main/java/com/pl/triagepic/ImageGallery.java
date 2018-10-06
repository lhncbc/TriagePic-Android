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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Where reporters select already in app images
 * 
 */
public class ImageGallery extends Activity implements OnItemClickListener {
	private GridView gridGallery;
	private ArrayList<Image> images;
	private Bitmap[] bitmaps;
	private ImageAdapter adapter;
    private ProgressDialog progressDialog;

    private static final int COLUMNS = 6;

    /**
	 * Obtains all app Image files and sets them in an ArrayAdapter for this
	 * page, While a background thread loads images refreshing the adapter when
	 * it loads an image
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);

		gridGallery = (GridView) findViewById(R.id.gallery_grid);

		File dir = getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
		images = new ArrayList<Image>();

		for (File f : dir.listFiles()){
            if (f.isFile()){
                String str = f.getAbsolutePath();
                str = str.toLowerCase();
                // need to verify the file format
                if (str.endsWith("png") || str.endsWith("jpg")|| str.endsWith("jpeg"))
                {
                    images.add(new Image(f.getAbsolutePath(), null, null, null, null));
                }
            }
		}
        if (images.size() == 0){
            Toast.makeText(this, "No image is found in TriagePic gallery.", Toast.LENGTH_SHORT).show();
        }

		bitmaps = new Bitmap[images.size()];

		adapter = new ImageAdapter();

		gridGallery.setAdapter(adapter);
		gridGallery.setOnItemClickListener(this);
		gridGallery.setNumColumns(COLUMNS);

//        loadImages();
        new loadImagesAsyncTask().execute();
	}

    private void loadImages() {
        // load the images from local driver.
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = ReportPatientImageHandler.resizedBitmap(images.get(i).getUri(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, ImageGallery.this, true);
            ImageGallery.this.runOnUiThread(new Runnable() {
                public void run() {
                    ImageGallery.this.adapter.notifyDataSetChanged();
                }
            });
        }
    }

    //To use the AsyncTask, it must be subclassed
    private class loadImagesAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(ImageGallery.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Retrieving images, please wait...");
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
                loadImages();
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

        }
    }

    /**
	 * selects an image and returns its uri to the ReportPatient activity
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Image number " + String.valueOf(position) + " is selected.", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
		String str = images.get(position).getUri();
        i.setData(Uri.parse(str));
		this.setResult(RESULT_OK, i);
		finish();
	}

	/**
	 * Creates an adapter for handling the gallery
	 * 
	 * @author bonifantmc
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {
		/**
		 * If the bitmap has loaded it is used, if not the defualt image is used
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;
			System.gc();
			int width = parent.getWidth() / COLUMNS;
			if (convertView == null) {
				image = new ImageView(parent.getContext());

				image.setLayoutParams(new AbsListView.LayoutParams(width, width));
				image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else
				image = (ImageView) convertView;

			if (bitmaps[position] != null)
				image.setImageBitmap(bitmaps[position]);
			else
				image.setImageDrawable(ImageGallery.this.getResources()
						.getDrawable(R.drawable.questionhead));
			return image;
		}

		public int getCount() {
			return bitmaps.length;
		}

		public Object getItem(int position) {
			return bitmaps[position];
		}

		public long getItemId(int position) {
			return position;
		}
	}
}
