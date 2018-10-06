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

/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * •	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 * •	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * •	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 *
 */

package com.pl.triagepic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SearchByPhotoActivity extends Activity implements View.OnClickListener{
    static final int CROP_IMAGE = 6;
    private static final int SEARCH_BY_PHOTO = 9;

    private static final int GPS_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static final int RUN_GPS = 100;
    public static final int RUN_MAP = 101;
    public static final int RUN_CAMERA = 102;

    TriagePic app;

    Button buttonCamera;
    Button buttonGallery;
    Button buttonDeleteImage;
    Button buttonStartSearch;

    ImageView imageViewPhoto;

    private ReportPatientImageHandler camera;
    Intent cameraIntent;
    ReportPatientImageHandler cameraHandler;

    private ArrayList<Image> images;
    private int curImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_by_photo);

        app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        Initialize();
    }

    private void Initialize() {
        images = new ArrayList<Image>();

        camera = new ReportPatientImageHandler(this);

        cameraHandler = new ReportPatientImageHandler(this);

        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(this);

        buttonGallery = (Button) findViewById(R.id.buttonGallery);
        buttonGallery.setOnClickListener(this);

        buttonDeleteImage = (Button) findViewById(R.id.buttonDeleteImage);
        buttonDeleteImage.setOnClickListener(this);

        buttonStartSearch = (Button) findViewById(R.id.buttonStartSearch);
        buttonStartSearch.setOnClickListener(this);

        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCamera:
                new checkPermissionAsyncTask(RUN_CAMERA, this).execute();
//                takePhoto();
                break;
            case R.id.buttonGallery:
                addPhotoFromGallery();
                break;
            case R.id.buttonDeleteImage:
                deleteImage();
                break;
            case R.id.buttonStartSearch:
                startSearch();
                break;
            default:
                break;

        }

    }

    private void startSearch(){
        if (images.isEmpty() == true){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No photo is selected.")
                    .setCancelable(true)
                    .setTitle("Warning")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Toast.makeText(this, "A photo is selected.", Toast.LENGTH_SHORT).show();

            String encodedPhoto = "ENCODED_PHOTO";
            app.setCurEncodedImage(images.get(0).getEncoded().toString());
            Intent i = new Intent();
            i.putExtra("ENCODED_PHOTO", encodedPhoto);
            setResult(SEARCH_BY_PHOTO, i);
            this.finish();
        }
    }

    private void deleteImage() {
        if (images.isEmpty()){
            Toast.makeText(this, "No photo is found.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_crop)
                .setTitle("Warning")
                .setMessage("Are you sure you want to remove the selected photo?")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        images.clear();
                        imageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void takePhoto() {
        images.clear(); // clean the list.
        cameraIntent = camera.startCamera();
        startActivityForResult(cameraIntent, ReportPatientImageHandler.IMAGE_CAPTURE);
        app.setCameraIntent(cameraIntent);
    }

    private void addPhotoFromGallery() {
        images.clear(); // clean the list.
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ReportPatientImageHandler.IMAGE_FIND_EX);
    }

    @Override
    public void onBackPressed() {
        if (images.size() <= 0){
            String encodedPhoto = "";
            app.setCurEncodedImage(encodedPhoto);
            Intent i = new Intent();
            i.putExtra("ENCODED_PHOTO", encodedPhoto);
            setResult(SEARCH_BY_PHOTO, i);

            SearchByPhotoActivity.super.onBackPressed();
       }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have selected photo. Please delete the photo and quite.")
                    .setCancelable(true)
                    .setTitle("Warning")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ReportPatientImageHandler.IMAGE_CAPTURE) { // Camera
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            Image image = new Image();
            image = camera.onActivityResult(requestCode, resultCode, app.getCameraIntent());

            // changes made in version 9.0.0
            image.resizeImageFile();
            image.DetectFaces();

            if (images == null) {
                images = new ArrayList<Image>();
                image.setSquence(curImageIndex + 1);
                images.add(image);
            }
            else{
                image.setSquence(0);
                images.add(image);
            }

            curImageIndex = 0;

            setMyImage();

            Toast.makeText(this, "Photo is taken and added.", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == ReportPatientImageHandler.IMAGE_FIND_EX){ // get image back from device galleryif
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            Image image = new Image();
            image = camera.onActivityResult(requestCode, resultCode, data);

            // changes made in version 9.0.0
            image.DetectFaces();

            if (images == null) {
                images = new ArrayList<Image>();
                image.setSquence(curImageIndex + 1);
                images.add(image);
            }
            else{
                image.setSquence(0);
                images.add(image);
            }
            curImageIndex = 0;

            setMyImage();
            Toast.makeText(this, "Image \"" + image.getFileName() + "\" is added.", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == CROP_IMAGE){
            processCroppedImage(requestCode,  resultCode, data);
        }
    }

    public void processCroppedImage(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED){
            String errorMsg = "This photo cannot be cropped.";
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchByPhotoActivity.this);
            builder.setMessage(errorMsg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Warning")
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            // image is cropped successfully
            Bundle extras = data.getExtras();
            Bitmap bmp = extras.getParcelable("data");

            // Create new file name
            File fileNew = null;

            // if this is very first time, create the file twice
            File dir = this.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
            if (dir.listFiles().length == 0){
                fileNew = Image.createTriagePicImagefile(SearchByPhotoActivity.this);

                // Save bmp file to image
                try {
                    Image.saveBitmapToFile(bmp, fileNew);
                }
                catch (IOException e){
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            fileNew = Image.createTriagePicImagefile(SearchByPhotoActivity.this);

            // Save bmp file to image
            try {
                Image.saveBitmapToFile(bmp, fileNew);
            }
            catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (!fileNew.exists()){
                String errorMsg = "Failed to create the image file.";
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchByPhotoActivity.this);
                builder.setMessage(errorMsg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setTitle("Warning")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                Toast.makeText(this, "Cropped image is saved!", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(SearchByPhotoActivity.this);
                builder.setMessage("The cropped image is saved successfully. \nDo you want to add the cropped image in now, or later?")
                        .setIcon(android.R.drawable.ic_menu_crop)
                        .setCancelable(true)
                        .setTitle("Question")
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(SearchByPhotoActivity.this, ImageGallery.class);
                                startActivityForResult(i, ReportPatientImageHandler.IMAGE_PRIV_GAL);
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }
    }

    public void setMyImage() {
        if (images.size() > 0) {
            Image i = images.get(curImageIndex);

            Bitmap tmp = Bitmap.createBitmap(i.getBitmap(), 1, 1, i.getBitmap().getWidth()-1, i.getBitmap().getHeight()-1);
            Canvas canvas = new Canvas(tmp);
            canvas.drawBitmap(tmp, 0,0, null);

            if (i.getNumberOfFacesDetected() <= 0){
                imageViewPhoto.setImageDrawable(new BitmapDrawable(getResources(), i.getBitmap()));
            }
            else {
                Paint facePaint = new Paint();
                facePaint.setColor(Color.GREEN);
                facePaint.setStyle(Paint.Style.STROKE);
                facePaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

                Image.Rect r = i.getRect();
                canvas.drawRect(
                        r.getX(),
                        r.getY(),
                        r.getX() + r.getW(),
                        r.getY() + r.getH(),
                        facePaint
                );
            }

            // Draw frame to tell primary or secondary photo
            Paint primaryPaint = new Paint();
            primaryPaint.setColor(Color.WHITE);
            primaryPaint.setStyle(Paint.Style.STROKE);
            primaryPaint.setStrokeWidth(1);

            imageViewPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
        } else {
            imageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionhead));
        }
    }

    /**
     * Permissions
     * starting from SDK 23, need to enable permission in running time.
     * two permissions are needed:
     * fine location
     * external
     *
     * version 9.0.6-beta
     * code 9000601
     */

    public class checkPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
        private boolean permission = false;
        private int func;
        private Context c;

        ProgressDialog progressDialog;

        checkPermissionAsyncTask(int func, Context c) {
            this.func = func;
            this.c = c;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage("Checking permission...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token
            synchronized (this) {
                permission = checkPermission(func, c);
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            //close the progress dialog
            if (permission == false) {
                new requestPermissionAsyncTask(func, c).execute();
            } else { // enabled
                takePhoto();
            }
        }

        private boolean checkPermission(int func, Context c) {
            int result;
            result = c.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public class requestPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int func;
        private Context c;

        ProgressDialog progressDialog;

        requestPermissionAsyncTask(int func, Context c) {
            this.func = func;
            this.c = c;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage("Requesting permission...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token
            synchronized (this) {
                requestPermission(func);
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }

    }

    public void requestPermission(int func) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show();
        }
    }
}
