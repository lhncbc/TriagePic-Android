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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This is the image class it defines an image as its caption, uri, sha1hash
 * digest, and size (all strings). It also has methods to take an ArrayList of
 * images and return a comma separeted string of any given field from an array
 * list of Strings for storing the information in the database as well as a
 * method to then take these strings from the database and convert them back
 * into an ArrayList of images.
 * 
 */
public class Image {
    public final static String TAG = "Image";

	// Added for face recognision.
	private static int MAX_NUMBER_OF_FACES_TO_DETECT = 10; 
	private static double PIForth = Math.PI / 4.0;
	public static int WIDTH_MINIMUM_IMAGE = 64;
	public static int HEIGHT_MINIMUM_IMAGE = 64;
	public static int CROP_WIDTH = 256;
	public static int CROP_HEIGHT = 256;
    public static int MAX_SIZE = 256;

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private long id;
	private long pid;
	private int squence;
	private Bitmap bitmap;
//	private Bitmap bitmapWithBoundingBox;
	private String caption;
    private String encoded;
	private String uri;
	private Uri uriReal;
	private String digest;
	private String securedDigest;
	private String size;
	private String fileName;
    private String encFileName;
	private String imageXml;
	private String imageTxt;
	private String imageRawTxt;
	private int numberOfFacesDetected;
	private FaceDetector.Face[] faces;
	private Rect rect;
    private String seed;

    /**
     * added in version 6.1.3
     */
    private String imageUrl = "";
    private String imageUrlForFetch = "";
    private String imageWidth = "";
    private String imageHeight = "";

	Image(){
		this.id = 0;
		this.pid = 0;
		this.squence = 0;
		this.bitmap = null;
		this.caption = "";
        this.encoded = "";
		this.uri = "";
		this.uriReal = null;
		this.digest = "";
		this.securedDigest = "";
		this.size = "";
//		this.bitmapWithBoundingBox = null;
		this.fileName = "";
        this.encFileName = "";
		this.imageXml = "";
		this.imageTxt = "";
		this.imageRawTxt = "";
		this.numberOfFacesDetected = 0;
		this.faces = null;
		this.rect = new Rect();
        this.seed = "";
	}

	Image(Image img){
		this.id = img.getId();
		this.pid = img.getPid();
		this.squence = img.getSquence();
		this.bitmap = img.getBitmap();
		this.caption = img.getCaption();
        this.encoded = img.getEncoded();
		this.uri = img.getUri();
		this.uriReal = img.getUriReal();
		this.digest = img.getDigest();
		this.securedDigest = img.getSecuredDigest();
		this.size = img.getSize();
//		this.bitmapWithBoundingBox = img.getBitmapWithBoundingBox();
		this.fileName = img.getFileName();
        this.encFileName = img.getEncFileName();
		this.imageXml = img.getImageXml();
		this.imageTxt = img.getImageTxt();
		this.imageRawTxt = img.getImageRawTxt();
		this.numberOfFacesDetected = img.getNumberOfFacesDetected();
		for (int i = 0; i < this.numberOfFacesDetected; i++){
			this.faces[i] = img.getFaces()[i];
		}
		this.rect = new Rect();
		this.setRect(img.getRect());
        this.setSeed(img.getSeed());
	}
	
	Image(int pid, int squence, int nFaces, int x, int y, int h, int w, String uri, String caption, String size, String digest, String encoded){
		this.pid = pid;
		this.squence = squence;
		this.numberOfFacesDetected = nFaces;
		Rect r = new Rect();
		r.setX(x);
		r.setY(y);
		r.setH(h);
		r.setW(w);
		this.setRect(r);
		this.uri = uri;
//		this.uriReal = uriReal;
		this.caption = caption;
        this.encoded = encoded;
		this.size = size;
		this.digest = digest;
        this.seed = "";
	}

	void Equals(Image img){
		this.id = img.getId();
		this.pid = img.getPid();
		this.squence = img.getSquence();
		this.bitmap = img.getBitmap();
		this.caption = img.getCaption();
        this.encoded = img.getEncoded();
		this.uri = img.getUri();
		this.uriReal = img.getUriReal();
        this.digest = img.getDigest();
		this.size = img.getSize();
//		this.bitmapWithBoundingBox = img.getBitmapWithBoundingBox();
		this.fileName = img.getFileName();
        this.encFileName = img.getEncFileName();
		this.imageXml = img.getImageXml();
		this.imageTxt = img.getImageTxt();
		this.imageRawTxt = img.getImageRawTxt();
		this.numberOfFacesDetected = img.getNumberOfFacesDetected();
		for (int i = 0; i < this.numberOfFacesDetected; i++){
			this.faces[i] = img.getFaces()[i];
		}
		this.setRect(img.getRect());
        this.seed = img.getSeed();
	}
		
//	public void setBitmapWithBoundingBox(Bitmap bitmapWithBoundingBox){
//		this.bitmapWithBoundingBox = bitmapWithBoundingBox;
//	}
//	public Bitmap getBitmapWithBoundingBox(){
//		return this.bitmapWithBoundingBox;
//	}
	
	public void setId(long id){
		this.id = id;
	}
	public long getId(){
		return this.id;
	}

	public void setPid(long pid){
		this.pid = pid;
	}
	public long getPid(){
		return this.pid;
	}

	public void setSquence(int squence){
		this.squence = squence;
	}
	public int getSquence(){
		return this.squence;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return this.fileName;
	}

    public void setEncFileName(String encFileName){
        this.encFileName = encFileName;
    }
    public String getEncFileName(){
        return this.encFileName;
    }

	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	public void setCaption(String caption){
		this.caption = caption;
	}
	public String getCaption(){
		return this.caption;
	}

    public void setEncoded(String encoded){
        this.encoded = encoded;
    }
    public String getEncoded(){
        return this.encoded;
    }

    public void setUri(String uri){
		this.uri = uri;
	}
	public String getUri(){
		return this.uri;
	}
	
	public void setUriReal(Uri uriReal){
		this.uriReal = uriReal;
	}
	public Uri getUriReal(){
		return this.uriReal;
	}

	public void setDigest(String digest){
		this.digest = digest;
	}
	public String getDigest(){
		return this.digest;
	}

	public void setSecuredDigest(String securedDigest){
		this.securedDigest = securedDigest;
	}
	public String getSecuredDigest(){
		return this.securedDigest;
	}

	public void setSize(String size){
		this.size = size;
	}
	public String getSize(){
		return this.size;
	}

	public void setImageXml(String imageXml){
		this.imageXml = imageXml;
	}
	public String getImageXml() {
		return this.imageXml;
	}
	
	public void setImageTxt(String imageTxt){
		this.imageTxt = imageTxt;
	}
	public String getImageTxt() {
		return this.imageTxt;
	}

	public void setImageRawTxt(String imageRawTxt){
		this.imageRawTxt = imageRawTxt;
	}
	public String getImageRawTxt() {
		return this.imageRawTxt;
	}

	public void setNumberOfFacesDetected(int numberOfFacesDetected){
		this.numberOfFacesDetected = numberOfFacesDetected;
	}
	public int getNumberOfFacesDetected() {
		return this.numberOfFacesDetected;
	}
	
	public void setFaces(FaceDetector.Face[] faces){
		this.faces = faces;
	}
	public FaceDetector.Face[] getFaces(){
		return this.faces;
	}
	
	public void setRect(Rect rect){
		this.rect = rect;
	}
	public Rect getRect(){
		return this.rect;
	}

    public void setSeed(String seed){this.seed = seed;}
    public String getSeed() {return this.seed;}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageUrl(String imageUrl, final String nameSpaceImage) {
        this.imageUrl = imageUrl;
        if (this.imageUrl.isEmpty()){
            this.imageUrlForFetch = "";
            return;
        }

        this.imageUrlForFetch = nameSpaceImage + this.imageUrl; // 9.0.3
        Uri u = Uri.parse(this.imageUrlForFetch);
        uri = u.toString();

        return;
    }

    public String getImageUrlForFetch() {
        return imageUrlForFetch;
    }

    public void setImageUrlForFetch(String imageUrlForFetch) {
        this.imageUrlForFetch = imageUrlForFetch;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void createBitmap(){
		if (uri.isEmpty() == true){
			return;
		}
		BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
		bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		bitmap = null;
		bitmap = BitmapFactory.decodeFile(uri, bitmapFatoryOptions);
		if (bitmap == null){
			Log.i(TAG, "Failed to create the bitmap: " + uri);
		}
		else {
			Log.i(TAG, "Create bitmap from image: " + uri);
		}
	}

    public void downloadPatientPhoto(){
        if (imageUrlForFetch.endsWith("null") == true){
            bitmap = null;
        }
        if (imageUrlForFetch.isEmpty()){
            bitmap = null;
        }
        bitmap = getImageFromUrlInAsyncWay();
    }

    /**
     * input: image url
     * output: bitmap
     */
    public Bitmap getImageFromUrlInAsyncWay(){
        bitmap = null;
        // Better to use the threads.
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    getBitmapFromURL(imageUrlForFetch);
                }

                private void getBitmapFromURL(String src) {
                    bitmap = null;
                    try {
                        // if it is null
                        if (src.endsWith("null") == true){
                            bitmap = null;
                        }

                        Log.e("src", src);
                        URL url = new URL(src);

                        Log.d(TAG, "URL: " + url.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        Log.d(TAG, "Connection is buit. URL: " + url.toString());

                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setReadTimeout(10000);
                        connection.connect();

                        InputStream input = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(input);

                        Log.e("Bitmap","returned");
//			            return photo;
//			            return myBitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Exception", e.getMessage());
                        bitmap = null;
//			            return null;
                    }
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread
	    return bitmap;
    }


    public void DetectFaces() {
		System.gc();
		faces = new FaceDetector.Face[MAX_NUMBER_OF_FACES_TO_DETECT];
//        faces = new Face[MAX_NUMBER_OF_FACES_TO_DETECT];
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_NUMBER_OF_FACES_TO_DETECT);
		numberOfFacesDetected = fd.findFaces(bitmap, faces);
		Log.e("Detectd faces: ", String.valueOf(numberOfFacesDetected));
		
		if (numberOfFacesDetected <= 0){
			return;
		}
		
		if (numberOfFacesDetected == 1){
			rect = FaceToRect(faces[0]);
		}
//		rects = new Rect[MAX_NUMBER_OF_FACES_TO_DETECT];
//        for(int i = 0; i < numberOfFacesDetected; i++){
//        	rects[i] = FaceToRect(faces[i]);
//       }
    }
	
	public void FaceFrame() {
		if (bitmap == null){
			numberOfFacesDetected = 0;
			return;
		}
		numberOfFacesDetected = 1;
		rect.x = 6; 
		rect.y = 6;
		rect.w = bitmap.getWidth() - 6;
		rect.h = bitmap.getHeight() - 6;
	}
	
//	public void createBitmapWithBoundingBox(){
//		bitmapWithBoundingBox = Bitmap.createBitmap(bitmap, 1, 1, bitmap.getWidth()-1, bitmap.getHeight()-1);
//		DrawCanvas(bitmapWithBoundingBox);	
//	}
	
	public void DrawCanvas(Bitmap bmp) {
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(bmp, 0,0, null);
		Paint myPaint = new Paint();
        myPaint.setColor(Color.GREEN);
        myPaint.setStyle(Paint.Style.STROKE); 
        myPaint.setStrokeWidth(1); // changed from 3 to 1

        for(int i = 0; i < numberOfFacesDetected; i++)
        {
//        	Face f = faces[i];
//        	Rect rect = FaceToRect(f);
//        	Rect rect = rects[i];
        	canvas.drawRect(
        			rect.x, 
        			rect.y, 
        			rect.x + rect.w, 
        			rect.y + rect.h,
        			myPaint
        			);
        }
	}
	/*	
	public void DrawCanvas() {
		Canvas canvas = new Canvas(bitmapWithBoundingBox);
		canvas.drawBitmap(bitmapWithBoundingBox, 0,0, null);
		Paint myPaint = new Paint();
        myPaint.setColor(Color.GREEN);
        myPaint.setStyle(Paint.Style.STROKE); 
        myPaint.setStrokeWidth(3);

        for(int i = 0; i < numberOfFacesDetected; i++)
        {
//        	Face f = faces[i];
//        	Rect rect = FaceToRect(f);
//        	Rect rect = rects[i];
        	canvas.drawRect(
        			rect.x, 
        			rect.y, 
        			rect.x + rect.w, 
        			rect.y + rect.h,
        			myPaint
        			);
        }
	}
*/
	public void WriteXml(){
	    // build request object 
        try {
            //Creating an personXML Document
            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Creating the XML tree            
            //create the root element and add it to the document
            Node image = doc.createElement("image");
            doc.appendChild(image);

            // Node xmlFormat
//            Node xmlFormat = doc.createElement("xmlFormat");
//            xmlFormat.setTextContent("REUNITE4");
//            image.appendChild(xmlFormat);

            // file name
            Node fileNameNode = doc.createElement("fileName");
            fileNameNode.setTextContent(fileName);
            image.appendChild(fileNameNode);

            // Node face number
            Node faceNumber = doc.createElement("faceNumber");
            faceNumber.setTextContent(String.valueOf(numberOfFacesDetected));
            image.appendChild(faceNumber);
            
            for (int i = 0; i < numberOfFacesDetected; i++){
            	Face curFace = faces[i];
            	float curConfidence = curFace.confidence();
            	float curEyeDistance = curFace.eyesDistance();
                PointF curMidPoint = new PointF();
                curFace.getMidPoint(curMidPoint);
                int curPose = 0;
                curFace.pose(curPose);
                
                // Node face
                Node faceNode = doc.createElement("face");
                
                // confidence
                Node confidenceNode = doc.createElement("confidence");
                confidenceNode.setTextContent(String.valueOf(curConfidence));
                faceNode.appendChild(confidenceNode);
                
                // eye distance
                Node eyeDistanceNode = doc.createElement("eyeDistance");
                eyeDistanceNode.setTextContent(String.valueOf(curEyeDistance));
                faceNode.appendChild(eyeDistanceNode);
                
                // Start midpoint
                Node midPointNode = doc.createElement("midPoint");
                
                // Node x
                Node xNode = doc.createElement("x");
                xNode.setTextContent(String.valueOf(curMidPoint.x));
                midPointNode.appendChild(xNode);
                
                // Node y
                Node yNode = doc.createElement("y");
                yNode.setTextContent(String.valueOf(curMidPoint.y));
                midPointNode.appendChild(yNode);

                faceNode.appendChild(midPointNode);
                // end midpoint
                
                // pose
                Node poseNode = doc.createElement("pose");
                
                // Node nodeposeEulerX
                Node nodeEulerX = doc.createElement("eulerX");
                nodeEulerX.setTextContent(String.valueOf(curFace.pose(FaceDetector.Face.EULER_X)));
                poseNode.appendChild(nodeEulerX);
                
                // Node nodeposeEulerY
                Node nodeEulerY = doc.createElement("eulerY");
                nodeEulerY.setTextContent(String.valueOf(curFace.pose(FaceDetector.Face.EULER_Y)));
                poseNode.appendChild(nodeEulerY);
                
                // Node nodeposeEulerZ
                Node nodeEulerZ = doc.createElement("eulerZ");
                nodeEulerZ.setTextContent(String.valueOf(curFace.pose(FaceDetector.Face.EULER_Z)));
                poseNode.appendChild(nodeEulerZ);
                
                faceNode.appendChild(poseNode);                
                // end of pose
                
                image.appendChild(faceNode);
                // end of face            	
            }
      
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            imageXml = sw.toString();
            
        } catch (Exception e) {
            System.out.println(e);
        }
	}
	
	public void WriteRawText(){
	    // build request object 
        try {
        	if (numberOfFacesDetected == 0){
            	imageRawTxt = fileName;       	        	
        	}
        	else {
            	imageRawTxt = fileName + "\t";       	
        	}
            for (int i = 0; i < numberOfFacesDetected; i++){
            	Face curFace = faces[i];
            	float curConf = curFace.confidence();
                float eyeDis = curFace.eyesDistance();
                PointF curMidPoint = new PointF();
                curFace.getMidPoint(curMidPoint);
                float curPoseX = curFace.pose(FaceDetector.Face.EULER_X);
                float curPoseY = curFace.pose(FaceDetector.Face.EULER_Y);
                float curPoseZ = curFace.pose(FaceDetector.Face.EULER_Z);
                
                if (i == numberOfFacesDetected - 1){
                	imageRawTxt += String.valueOf(curConf) + "\t" + 
           					String.valueOf(eyeDis) + "\t" + 
           					String.valueOf(curMidPoint.x) + "\t" + 
           					String.valueOf(curMidPoint.y) + "\t" +
           					String.valueOf(curPoseX) + "\t" +
           					String.valueOf(curPoseY) + "\t" +
           					String.valueOf(curPoseZ); 
                }
                else {
                	imageRawTxt += String.valueOf(curConf) + "\t" + 
           					String.valueOf(eyeDis) + "\t" + 
           					String.valueOf(curMidPoint.x) + "\t" + 
           					String.valueOf(curMidPoint.y) + "\t" +
           					String.valueOf(curPoseX) + "\t" +
           					String.valueOf(curPoseY) + "\t" +
           					String.valueOf(curPoseZ) + "\t";                 	
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }		
	}
	
	public void WriteTxt(){
	    // build request object 
        try {
        	if (numberOfFacesDetected == 0){
            	imageTxt = fileName;       	
        	}
        	else {
            	imageTxt = fileName + "\t";       	
        	}
            for (int i = 0; i < numberOfFacesDetected; i++){
            	Face curFace = faces[i];
            	
            	Rect curRect = new Rect();
            	Rect leftEyeRect = new Rect();
            	Rect rightEyeRect = new Rect();
            	
            	curRect = FaceToRect(curFace);
            	leftEyeRect = LeftEyeToRect(curFace, curRect);
            	rightEyeRect = RightEyeToRect(curFace, curRect);

            	float f = curFace.pose(curFace.EULER_Y);
            	if (f >= PIForth) {
            		// Profile
            		if (i == numberOfFacesDetected - 1){
                		imageTxt += "p[" + 0 + "," + 0 + ";" + 0 + "," + 0 + "]";
            		}
            		else {
                		imageTxt += "p[" + 0 + "," + 0 + ";" + 0 + "," + 0 + "]\t";
            		}
            	}
            	else {
            		if (i == numberOfFacesDetected - 1){
                		imageTxt += "f{" + 
                					"[" + String.valueOf((int)curRect.x) + "," + String.valueOf((int)curRect.y) + ";" + String.valueOf((int)curRect.w) + "," + String.valueOf((int)curRect.h) + "]\t" + 
                					"i[" + String.valueOf((int)leftEyeRect.x) + "," + String.valueOf((int)leftEyeRect.y) + ";" + String.valueOf((int)leftEyeRect.w) + "," + String.valueOf((int)leftEyeRect.h) + "]\t" + 
                					"i[" + String.valueOf((int)rightEyeRect.x) + "," + String.valueOf((int)rightEyeRect.y) + ";" + String.valueOf((int)rightEyeRect.w) + "," + String.valueOf((int)rightEyeRect.h) + "]}";
            		}
            		else {
                		imageTxt += "f{" + 
            					"[" + String.valueOf((int)curRect.x) + "," + String.valueOf((int)curRect.y) + ";" + String.valueOf((int)curRect.w) + "," + String.valueOf((int)curRect.h) + "]\t" + 
            					"i[" + String.valueOf((int)leftEyeRect.x) + "," + String.valueOf((int)leftEyeRect.y) + ";" + String.valueOf((int)leftEyeRect.w) + "," + String.valueOf((int)leftEyeRect.h) + "]\t" + 
            					"i[" + String.valueOf((int)rightEyeRect.x) + "," + String.valueOf((int)rightEyeRect.y) + ";" + String.valueOf((int)rightEyeRect.w) + "," + String.valueOf((int)rightEyeRect.h) + "]}\t";
            		}
            	}
            }
        } catch (Exception e) {
            System.out.println(e);
        }
	}	

	/**
	 * 
	 * @param uri
	 *            location of image in TPoT memory. Required for image to exist
	 * @param digest
	 *            SHA1 of image implicitly required
	 * @param size
	 *            Pixel width*pixel height, implicitly required
	 * @param caption
	 *            comment on image optional
	 */
	Image(String uri, String digest, String encoded, String size, String caption) {
		this.uri = uri;
        this.digest = digest;
		this.size = size;
		this.caption = caption;
        this.encoded = encoded;

        if (uri.isEmpty() == true){
        	fileName = "";
        }
        else {
        	int lastSlash = uri.lastIndexOf("/");
        	fileName = uri.substring(lastSlash + 1);
        	fileName.trim();
        }
        
		faces = new FaceDetector.Face[MAX_NUMBER_OF_FACES_TO_DETECT];
		
		this.numberOfFacesDetected = 0;
		this.rect = new Rect();
	}

	Image(String uri, String digest, String encoded, String size, String caption, int numberOfFacesDetected, Rect rect) {
		this.uri = uri;
        this.digest = digest;
		this.size = size;
		this.caption = caption;
        this.encoded = encoded;

        if (uri.isEmpty() == true){
        	fileName = "";
        }
        else {
        	int lastSlash = uri.lastIndexOf("/");
        	fileName = uri.substring(lastSlash + 1);
        	fileName.trim();
        }
        
		faces = new FaceDetector.Face[MAX_NUMBER_OF_FACES_TO_DETECT];
		
		this.numberOfFacesDetected = numberOfFacesDetected;
		this.rect = new Rect();
		if (this.numberOfFacesDetected > 0){
			this.setRect(rect);
		}
	}

	/**
	 * 
	 * @param images
	 *            ArrayList of Images
	 * @return csv of image uris from the ArrayList
	 */
	static protected String setUri(ArrayList<Image> images) {
		if (images != null && images.size() > 0) {
			String uris = "";
			for (Image i : images)
				if (i.uri != null) {
					uris += "," + i.uri;
				}

            // get rid the first character "'", if there is
//			uris.replaceFirst(",", "");
            if (!uris.isEmpty()){
                if (uris.charAt(0) == ','){
                    uris = uris.substring(1);
                }
            }
			return uris;
		}
		return null;

	}

	/**
	 * 
	 * @param images
	 *            : ArrayList of Images
	 * @return csv of of image captions from the ArrayList
	 */
    static protected String setCaptions(ArrayList<Image> images) {
        if (images != null && images.size() > 0) {
            String captions = "";
            for (Image i : images)
                if (i.uri != null)
                    captions += "," + (i.caption != null ? i.caption : " ");
//            captions.replaceFirst(",", "");
            if (!captions.isEmpty()){
                if (captions.charAt(0) == ','){
                    captions = captions.substring(1);
                }
            }

            return captions;
        }
        return null;
    }

    // added in version 9.0.0
    static protected String setEncodeds(ArrayList<Image> images) {
        if (images != null && images.size() > 0) {
            String encodeds = "";
            for (Image i : images)
                if (i.uri != null)
                    encodeds += "," + (i.encoded != null ? i.encoded : " ");
//            encodeds.replaceFirst(",", "");
            if (!encodeds.isEmpty()){
                if (encodeds.charAt(0) == ','){
                    encodeds = encodeds.substring(1);
                }
            }
            return encodeds;
        }
        return null;
    }

    /**
	 * 
	 * @param images
	 *            : ArrayList of Images
	 * @return csv of image sizes from the ArrayList
	 */
	static protected String setSizes(ArrayList<Image> images) {
		if (images != null && images.size() > 0) {
			String sizes = "";
			for (Image i : images)
				if (i.uri != null)
					sizes += "," + (i.size != null ? i.size : " ");
//			sizes.replaceFirst(",", "");
            if (!sizes.isEmpty()){
                if (sizes.charAt(0) == ','){
                    sizes = sizes.substring(1);
                }
            }
			return sizes;
		}
		return null;
	}

	/**
	 * 
	 * @param images
	 *            : ArrayList of Images
	 * @return csv of of image digests from the ArrayList
	 */
	static protected String setDigests(ArrayList<Image> images) {
		if (images != null && images.size() > 0) {
			String digests = "";
			for (Image i : images)
				if (i.uri != null){
					digests += "," + (i.digest != null ? i.digest : " ");
//					digests += "," + " "; // test
				}
//			digests.replaceFirst(",", "");
            if (!digests.isEmpty()){
                if (digests.charAt(0) == ','){
                    digests = digests.substring(1);
                }
            }
			return digests;
		}
		return null;
	}

	static protected String setSecuredDigests(ArrayList<Image> images) {
		if (images != null && images.size() > 0) {
			String securedDigests = "";
			for (Image i : images)
				if (i.uri != null)
					securedDigests += "," + (i.getSecuredDigest() != null ? i.getSecuredDigest() : " ");
//			securedDigests.replaceFirst(",", "");
            if (!securedDigests.isEmpty()){
                if (securedDigests.charAt(0) == ','){
                    securedDigests = securedDigests.substring(1);
                }
            }
			return securedDigests;
		}
		return null;
	}

	/**
	 * /** Creates an arraylist of images based on the given string inputs.
	 * Returns an empty list if it couldn't parse any images
	 * 
	 * @param uris
	 *            : String csv of uris
	 * @param captions
	 *            : String csv of captions
	 * @param sizes
	 *            : String csv of sizes
	 * @param digests
	 *            : String csv of digests
	 * @return : ArrayList of Images
	 */
	static protected ArrayList<Image> setImages(String uris, String digests, String encodeds,
			String sizes, String captions) {
		ArrayList<Image> images = new ArrayList<Image>();
		if (uris != null) {
			String[] uri = uris.split(",");
            String[] caption = captions.split(",");
            String[] encoded = encodeds.split(",");
            String[] size = sizes.split(",");
            String[] digest = digests.split(",");
			for (int i = 0; i < uri.length; i++) {
				if (uri[i].contains(".jpg") || uri[i].contains("content"))
					images.add(new Image(uri[i], digest[i], encoded[i], size[i], caption[i]));
			}
		}
		return images;
	}

	@Override
	public String toString() {
		return "Uri: " + (uri != null ? uri : "NULL") + "\nCaption: "
                + (caption != null ? caption : "NULL") + "\nEncoded: "
                + (encoded != null ? encoded : "NULL") + "\nSize: "
				+ (size != null ? size : "NULL") + "\nDigest: "
                + (digest != null ? digest : "NULL" + "\n");
    }

	public String getTriagePicDir(Context c){
//		File dir = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE);
//        MediaStore.EXTRA_OUTPUT
//		return dir.getPath();
        return MediaStore.EXTRA_OUTPUT;
	}
	
	public static File createTriagePicImagefile(Context c){
		File dir = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
		File f = new File(dir, System.currentTimeMillis() + ".jpg");

//        File dir = new File(MediaStore.EXTRA_OUTPUT);
//        File f = new File(dir, System.currentTimeMillis() + ".jpg");

        return f;
	}
	
	static void saveBitmapToFile(Bitmap b, File f) throws IOException{
        FileOutputStream out = null;
		try {
	        out = new FileOutputStream(f, true);
            b.compress(Bitmap.CompressFormat.PNG, 50, out);
		}
        finally {
            out.flush();
            out.close();
        }

    }

	public static String getDigestFromBitmap(Bitmap picture) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (picture.compress(Bitmap.CompressFormat.PNG, 50, stream) == false){
            return "";
        }
		byte[] arr = stream.toByteArray();
//		picture.recycle();

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		byte[] res = md.digest(arr);

		StringBuilder sb = new StringBuilder();
		for (byte b : res)
			sb.append(String.format("%02X", b));
		System.out.println(sb);
		return sb.toString();
	}

    public static String getEncodedFromBitmap(Bitmap picture){
        String str;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (picture.compress(Bitmap.CompressFormat.PNG, 50, stream) == false){
            return "";
        }
        byte[] b = stream.toByteArray();
        str = Base64.encodeToString(b, Base64.DEFAULT);
        return str;
    }

    public static String getSecuredDigestFromBitmap(Bitmap picture, String seed) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		picture.compress(Bitmap.CompressFormat.PNG, 50, stream);
		byte[] arr = stream.toByteArray();
//		picture.recycle();
		
		MyEncrypt enc = new MyEncrypt(seed);
        String str = "";
		try {
			str = enc.encrypt(seed, arr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		for (byte b : str.getBytes())
			sb.append(String.format("%02X", b));
		System.out.println(sb);
		return sb.toString();
	}	

	public static String getSizeFromPath(String path, Context c) {
		InputStream in = null;
		try {
			if (path.contains("content"))
				in = c.getContentResolver().openInputStream(Uri.parse(path));
			else
				in = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/* Decode image size */
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, o);
		int size = o.outWidth * o.outHeight;
		return String.valueOf(size);
	}	
	
	public class Rect {
		private float x;
		private float y;
		private float w;
		private float h;
		
		Rect(){
			this.x = 0;
			this.y = 0;
			this.w = 0;
			this.h = 0;
		}
		
		Rect(Rect r){
			this.setX(r.getX());
			this.setY(r.getY());
			this.setH(r.getH());
			this.setW(r.getW());
		}
		
		Rect(float x, float y, float h, float w){
			this.setX(x);
			this.setY(y);
			this.setH(h);
			this.setW(w);
		}

		public void setX(float x){
			this.x = x;
		}
		public void setY(float y){
			this.y = y;
		}
		public void setH(float h){
			this.h = h;
		}
		public void setW(float w){
			this.w = w;
		}
		
		public float getX(){
			return this.x;
		}
		public float getY(){
			return this.y;
		}
		public float getH(){
			return this.h;
		}
		public float getW(){
			return this.w;
		}
	}
	
	public Rect FaceToRect(Face f){
		Rect r = new Rect();
    	PointF midPoint = new PointF();
    	f.getMidPoint(midPoint);
    	
    	float a = (float) 2.3;
    	float b = (float) 2.3;
    	
    	r.w = a * f.eyesDistance(); 
    	r.h = b * f.eyesDistance(); 
    	
    	PointF startPoint = new PointF();
    	r.x = (float) (midPoint.x - r.w/2.0);
    	r.y = (float) (midPoint.y - r.h/3.0);    
    	
    	return r;
	}
	
	public Rect LeftEyeToRect(Face f, Rect faceR){
		Rect r = new Rect();
    	PointF midPoint = new PointF();
    	f.getMidPoint(midPoint);
    	
    	PointF eye = new PointF();
    	eye.x = (float) (midPoint.x - 0.5 * f.eyesDistance());
    	eye.y = midPoint.y;
    	
//    	r.w = f.eyesDistance() / 4;
//    	r.h = f.eyesDistance() / 8;
    	r.w = (float) (0.66666666 * f.eyesDistance());
    	r.h = r.w;
    	
    	r.x = (float) (eye.x - 0.5 * r.w);
    	r.y = (float) (eye.y - 0.5 * r.h);
    	
    	r.x = r.x - faceR.x;
    	r.y = r.y - faceR.y;
    	
    	return r;
	}
	
	public Rect RightEyeToRect(Face f, Rect faceR){
		Rect r = new Rect();
    	PointF midPoint = new PointF();
    	f.getMidPoint(midPoint);
    	
    	PointF eye = new PointF();
    	eye.x = (float) (midPoint.x + 0.5 * f.eyesDistance());
    	eye.y = midPoint.y;
    	
//    	r.w = f.eyesDistance() / 4;
//    	r.h = f.eyesDistance() / 8;
    	r.w = (float) (0.66666666 * f.eyesDistance());
    	r.h = r.w;

    	r.x = (float) (eye.x - 0.5 * r.w);
    	r.y = (float) (eye.y - 0.5 * r.h);
    	
    	r.x = r.x - faceR.x;
    	r.y = r.y - faceR.y;
    	
    	return r;
	}	
	
	public static byte[] generateKey(String password) throws Exception
	{
	    byte[] keyStart = password.getBytes("UTF-8");

	    KeyGenerator kgen = KeyGenerator.getInstance("AES");
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
	    sr.setSeed(keyStart);
	    kgen.init(128, sr);
	    SecretKey skey = kgen.generateKey();
	    return skey.getEncoded();
	}

    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }
    
    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }
    
    public void saveEncryptToFile(byte [] yourByteArrayContainigDataToEncrypt) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "your_folder_on_sd", "file_name");
        BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        byte[] yourKey = null;
		try {
			yourKey = generateKey("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        byte[] filesBytes = null;
		try {
			filesBytes = encodeFile(yourKey, yourByteArrayContainigDataToEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			bos.write(filesBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}    
    }    

    public FileInputStream fileToInputString(String inputFile) {
        File file = new File(inputFile);
        FileInputStream fins = null;
        try {
            // create FileInputStream object
            fins = new FileInputStream(file);
            byte fileContent[] = new byte[(int)file.length()];
            
            // Reads up to certain bytes of data from this input stream into an array of bytes.
            fins.read(fileContent);
            
            //create string from byte array
            String s = new String(fileContent);
            System.out.println("File content: " + s);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        }
        finally {
            // close the streams using close method
            try {
                if (fins != null) {
                    fins.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
        return fins;
    }

    public void DeleteAllImages(Context c){
        File fPath = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
//        File fPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String localPath = fPath.getPath();
        File dir = new File(localPath);
        if (dir.isDirectory() == false){
            String err = "Error: \"" + dir + "\" is not a directory.";
            Toast.makeText(c, err, Toast.LENGTH_SHORT).show();
            Log.i("EncryptAllImages", err);
            return;
        }
        String msg = "Working directory: \"" + dir + "\".";
        Log.i("DeleteAllImages", msg);

        // get the image file list
        /*
        if (dir.listFiles().length == 0){
            Toast.makeText(c, "No image is found in " + Environment.DIRECTORY_PICTURES + localPath, Toast.LENGTH_SHORT).show();
            Log.i("Error", "No image is found in " + Environment.DIRECTORY_PICTURES + localPath);
            return;
        }
        */

        ArrayList<Image> images = new ArrayList<Image>();

        for (File f : dir.listFiles()){
            if (f.isDirectory()){
                continue;
            }
            Image img = new Image(f.getAbsolutePath(), null, null, null, null);
            if (img.uri.endsWith("jpeg")){
                images.add(img);
            }
            else if (img.uri.endsWith("jpg"))
            {
                images.add(img);
            }
            else if (img.uri.endsWith("png"))
            {
                images.add(img);
            }
            else if (img.uri.endsWith("_enc"))
            {
                images.add(img);
            }
        }
        msg = "Images found \"" + Long.toString(images.size()) + "\".";
        Log.i("DeleteAllImages", msg);

        // encrypt all images
        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);
            String file = img.uri;
            if (file.isEmpty() == false){
                Log.i("Start to delete", Long.toString(i + 1) + "/" + Long.toString(images.size()) + " " + file);
                DeleteImage(file);
                Log.i("Done", Long.toString(i + 1) + "/" + Long.toString(images.size()) + " " + file);
            }
        }
    }

    public void EncryptAllImages(Context c, String seed) {
        this.seed = seed;

        File fPath = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);

//        File fPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String localPath = fPath.getPath();
        File dir = new File(localPath);
        if (dir.isDirectory() == false){
            String err = "Error: \"" + dir + "\" is not a directory.";
            Toast.makeText(c, err, Toast.LENGTH_SHORT).show();
            Log.i("EncryptAllImages", err);
            return;
        }
        String msg = "Working directory: \"" + dir + "\".";
        Log.i("EncryptAllImages", msg);

        ArrayList<Image> images = new ArrayList<Image>();

        for (File f : dir.listFiles()){
            if (f.isDirectory()){
                continue;
            }
            Image img = new Image(f.getAbsolutePath(), null, null, null, null);
            if (img.uri.endsWith("jpeg")){
                images.add(img);
            }
            else if (img.uri.endsWith("jpg"))
            {
                images.add(img);
            }
            else if (img.uri.endsWith("png"))
            {
                images.add(img);
            }
        }
        msg = "Images found \"" + Long.toString(images.size()) + "\".";
        Log.i("EncryptAllImages", msg);

        // encrypt all images
        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);
            String file = img.uri;
            if (file.isEmpty() == false){
                Log.i("Start to encrypt", Long.toString(i + 1) + "/" + Long.toString(images.size()) + " " + file);
                EncryptImage(file);
                Log.i("Done", Long.toString(i + 1) + "/" + Long.toString(images.size()) + " " + file);
            }
        }
    }

    public void DecryptAllImages(Context c, String seed) {
        this.seed = seed;

        // get the directory
        File fPath = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);

        String localPath = fPath.getPath();
        File dir = new File(localPath);
        if (dir.isDirectory() == false){
            String err = "Error: \"" + dir + "\" is not a directory.";
            Toast.makeText(c, err, Toast.LENGTH_SHORT).show();
            Log.i("DecryptAllImages", err);
            return;
        }
        String msg = "Working directory: \"" + dir + "\".";
        Log.i("DecryptAllImages", msg);

        ArrayList<Image> images = new ArrayList<Image>();

        for (File f : dir.listFiles()){
            if (f.isDirectory()){
                continue;
            }
            Image img = new Image(f.getAbsolutePath(), null, null, null, null);
            if (img.uri.endsWith("_enc")){
                images.add(img);
            }
        }
        msg = "Images to be decrypted are \"" + Long.toString(images.size()) + "\".";
        Log.i("DecryptAllImages", msg);

        // decrypt all images
        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);
            String file = img.uri;
            if (file.isEmpty() == false){
                Log.i("Start to decrypt", Long.toString(i + 1) + "/" + Long.toString(images.size()) + " " + file);
                DecryptImage(file);
                Log.i("Done", Long.toString(i) + "/" + Long.toString(images.size()) + " " + file);
            }
        }
    }

    private String EncryptImage() {
        String err;
        Log.i("Running", "EncryptImage() - start");
        FileInputStream fis;
        FileOutputStream fos;

        try {
            File fi = new File(fileName);
            if (fi.isFile()){
                try {
                    encFileName = this.getEncryptedFileName(fileName);

                    File fo = new File(encFileName);
                    fo.createNewFile();
                    fo.canWrite();
                }
                catch (Exception  e) {
                    e.getMessage();
                }

                fis = new FileInputStream(fileName);
                fos = new FileOutputStream(encFileName);
                encrypt(seed, fis, fos);

                fi.delete();
            }
            else {
                err = "File \"" + fileName + "\" is not existed.";
                Log.e("Error", err);
                return err;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("Throw", e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    private String DeleteImage(String imageFile){
        fileName = imageFile;

        String err;
        File fi = new File(fileName);
        if (fi.isFile()){
            try {
                fi.delete();
            }
            catch(Exception e){
                Log.e("Error", e.getMessage());
                return e.getMessage();
            }
        }
        else {
            err = "File \"" + fileName + "\" is not existed.";
            Log.e("Error", err);
            return err;
        }
        return null;
    }

    private String EncryptImage(String imageFile) {
        fileName = imageFile;

        String err;
        Log.i("Running", "EncryptImage() - start");

        FileInputStream fis;
        FileOutputStream fos;

        try {
            File fi = new File(fileName);
            if (fi.isFile()){
                try {
                    encFileName = this.getEncryptedFileName(fileName);

                    File fo = new File(encFileName);
                    fo.createNewFile();
                    fo.canWrite();
                }
                catch (Exception  e) {
                    e.getMessage();
                }

                fis = new FileInputStream(fileName);
                fos = new FileOutputStream(encFileName);
                encrypt(seed, fis, fos);

                fi.delete();
            }
            else {
                err = "File \"" + fileName + "\" is not existed.";
                Log.e("Error", err);
                return err;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("Throw", e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    private String DecryptImage() {
        String err;
        Log.i("Running", "DecryptImage() - start");
        FileInputStream fis2;
        FileOutputStream fos2;
        try {
            fileName = this.getDecryptedFileName(encFileName);

            File fi = new File(encFileName);
            if (fi.isFile()){
                try {
                    File fo = new File(fileName);
                    fo.createNewFile();
                    fo.canWrite();
                }
                catch (Exception  e) {
                    e.getMessage();
                }

                fis2 = new FileInputStream(encFileName);
                fos2 = new FileOutputStream(fileName);
                decrypt(seed, fis2, fos2);

                fi.delete();
            }
            else {
                err = "File \"" + fileName + "\" is not existed.";
                Log.e("Error", err);
                return err;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("Throw", e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    private String DecryptImage(String decryptedFile) {
        encFileName = decryptedFile;

        String err;
        Log.i("Running", "DecryptImage() - start");

        FileInputStream fis2;
        FileOutputStream fos2;
        try {
            fileName = this.getDecryptedFileName(encFileName);

            File fi = new File(encFileName);
            if (fi.isFile()){
                try {
                    File fo = new File(fileName);
                    fo.createNewFile();
                    fo.canWrite();
                }
                catch (Exception  e) {
                    e.getMessage();
                }

                fis2 = new FileInputStream(encFileName);
                fos2 = new FileOutputStream(fileName);
                decrypt(seed, fis2, fos2);

                fi.delete();
            }
            else {
                err = "File \"" + fileName + "\" is not existed.";
                Log.e("Error", err);
                return err;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("Throw", e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    public static String getEncryptedFileName(String fileName){
        String encryptedFileName = "";
        if (fileName.isEmpty()){
            return "";
        }
        encryptedFileName = fileName + "_enc";
        return encryptedFileName;
    }

    private String getDecryptedFileName(String fileName) {
        String decryptedFileName = "";
        if (fileName.isEmpty()){
            return "";
        }
        int length = fileName.length();
        decryptedFileName = fileName.substring(0, length - 4);
        return decryptedFileName;
    }

    public static void encrypt(String key, InputStream is, OutputStream os) throws Throwable {
        encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os);
    }

    public static void decrypt(String key, InputStream is, OutputStream os) throws Throwable {
        encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
    }

    public static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws Throwable {

        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");

        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            doCopy(cis, os);
        } else if (mode == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            doCopy(is, cos);
        }
    }

    public static void doCopy(InputStream is, OutputStream os) throws IOException {
        try {
            byte[] bytes = new byte[64];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                os.write(bytes, 0, numBytes);
            }
        }
        finally {
            is.close();
            os.close();
        }
    }

    public void resizeImageFile() {
        if (bitmap == null) {
            return;
        }

        if (uri.isEmpty() == true) {
            return;
        }

        File f = new File(uri);
        if (f.exists() == false) {
            return;
        }

        try {
            saveBitmapToFile(bitmap, f);
        } catch (IOException e) {
            e.getMessage();
        }
    }


}