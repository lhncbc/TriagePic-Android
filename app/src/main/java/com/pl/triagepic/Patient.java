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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Patient {
    public static String TAG = "Patient";

	public static String SENT = "Sent";
	public static String DRAFTS = "Drafts";
	public static String DELETED = "Deleted";
    public static String OUTBOX = "Outbox";
    public static String TRIAGETRAK = "TriageTrak"; // added in version 9.0.0
	public static String ALL = "All";
	public static String UNKNOWEN = "Unknown";

    public static int SEVEN_TEEN_YEAR_OLD = 17;

//    public static int PHOTO_SIZE = 512;
    public static int PHOTO_SIZE = 426;
    public static int PHOTO_WIDTH = 426;
    public static int PHOTO_HEIGHT = 320;

	protected String lastName;
	protected String firstName;
	protected String event;
	protected String hospital;
	protected String uuid;
	protected String boxId = UNKNOWEN;
	protected Age age;
	protected Gender gender;
	protected Zone zone;
	protected MyZone myZone;
	protected String comments;
    public void setComments(String comments){this.comments = comments;}
    public String getComments(){return this.comments;}
	protected int error; // based see Strings file for error code definitions
	protected long rowIndex;
    protected String prefixPid;
    protected long patientId;
	protected String fpid;// formatted patientId
	protected long date;

    private boolean statusPhotoDownload;
    public boolean getStatusPhotoDownload(){
        return statusPhotoDownload;
    }
    public void setStatusPhotoDownload(boolean statusPhotoDownload){
        this.statusPhotoDownload = statusPhotoDownload;
    }

    private String lastUpdate;
    public String getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

	protected Drawable photo; // drawables used as images for test records

	protected ArrayList<Image> images;// assorted strings referencing images
    public ArrayList<Image> getImages(){return images;}
    public void setImages(ArrayList<Image> images){this.images = images;}

	protected boolean report;
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	public String getLastName(){
		return this.lastName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	public String getFirstName(){
		return this.firstName;
	}

    public void setAge(Age age){this.age = age;}
    public Age getAge() {return this.age;}

    public void setGender(Gender gender){this.gender = gender;}
    public Gender getGender() {return this.gender;}

	public void setEvent(String event){
		this.event = event;
	}
	public String getEvent(){
		return this.event;
	}
	
	public void setHospital(String hospital){
		this.hospital = hospital;
	}
	public String getHospital(){
		return this.hospital;
	}
	
	public void setPrefixPid(String prefixPid){
		this.prefixPid = prefixPid;
	}
	public String getPrefixPid(){
		return this.prefixPid;
	}

    public void setPid(long patientId){
		this.patientId = patientId;
	}
	public long getPid(){
		return this.patientId;
	}
	
	public void setFpid(String fpid){
		this.fpid = fpid;
	}
	public String getFpid(){
		return this.fpid;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}
	public String getUuid(){
		return this.uuid;
	}

	public void setBoxId(String boxId){
		this.boxId = boxId;
	}
	public String getBoxId(){
		return this.boxId;
	}
	
	public Patient(){
		lastName = "";
		firstName = "";
		event = "";
		hospital = "";
		uuid = "";
		boxId = UNKNOWEN;
		age = Age.UNKNOWN;
		gender = Gender.UNKNOWN;
		zone = zone.UNASSIGNED;
		myZone = MyZone.UNASSIGNED;
		comments = "";
		error = 0;
		rowIndex = 0;
		prefixPid = "";
		patientId = 0; 
		fpid = "";
		uuid = "";
		date = 0;
		photo = null; // drawables used as images for test records
		images = null;// assorted strings referencing images.
		report = false;

        lastUpdate = "";
        statusPhotoDownload = false;
	}

    public void Initialize(){
        lastName = "";
        firstName = "";
        event = "";
        hospital = "";
        uuid = "";
        boxId = UNKNOWEN;
        age = Age.UNKNOWN;
        gender = Gender.UNKNOWN;
        zone = zone.UNASSIGNED;
        myZone = MyZone.UNASSIGNED;
        comments = "";
        error = 0;
        rowIndex = 0;
        prefixPid = "";
        patientId = 0;
        fpid = "";
        uuid = "";
        date = 0;
        photo = null; // drawables used as images for test records
        images = null;// assorted strings referencing images.
        report = false;

        lastUpdate = "";
        statusPhotoDownload = false;
    }

	/**
	 * Uses Java's Random class to assign fields. There are seven defualt last
	 * names, 7 female first names, 7 male first names, 7 female images, and 7
	 * male images.
	 * 
	 * @param c
	 *            application context required to get image drawables
	 */
	public Patient(Context c) {
        Initialize();

		Random r = new Random();
		int fname = r.nextInt(6);
		int lname = r.nextInt(7);

		int a = r.nextInt(2);
		if (a == 0)
			age = Age.ADULT;
		else if (a == 1)
			age = Age.PEDIATRIC;

		a = r.nextInt(6);
		if (a == 0)
			zone = Zone.GREEN;
		else if (a == 1)
			zone = Zone.BH_GREEN;
		else if (a == 2)
			zone = Zone.YELLOW;
		else if (a == 3)
			zone = Zone.RED;
		else if (a == 4)
			zone = Zone.GRAY;
		else if (a == 5)
			zone = Zone.BLACK;

		a = r.nextInt(3);
		if (a == 0)
			gender = Gender.FEMALE;
		else if (a == 1)
			gender = Gender.MALE;
		else
			gender = Gender.UNKNOWN;

		a = r.nextInt(7);

		long temp = System.currentTimeMillis();
		if (r.nextInt(2) % 2 == 0) {
			long lo = r.nextLong();
			if (lo < temp)
				date = lo;
			else
				date = lo * 10000 / temp;
		} else
			date = temp;

		if (gender == Gender.FEMALE) {
			if (fname == 0)
				firstName = "Sally";
			else if (fname == 1)
				firstName = "Norma";
			else if (fname == 2)
				firstName = "Puala";
			else if (fname == 3)
				firstName = "Emily";
			else if (fname == 4)
				firstName = "Andrea";
			else if (fname == 5)
				firstName = "Jessica";
			else
				firstName = "Mary";
			if (a == 0)
				photo = c.getResources().getDrawable(R.drawable.lady1);
			if (a == 1)
				photo = c.getResources().getDrawable(R.drawable.lady2);
			if (a == 2)
				photo = c.getResources().getDrawable(R.drawable.lady3);
			if (a == 3)
				photo = c.getResources().getDrawable(R.drawable.lady4);
			if (a == 4)
				photo = c.getResources().getDrawable(R.drawable.lady5);
			if (a == 5)
				photo = c.getResources().getDrawable(R.drawable.lady6);
			if (a == 6)
				photo = c.getResources().getDrawable(R.drawable.questionhead);
		}

		if (gender == Gender.MALE) {
			if (fname == 0)
				firstName = "Stan";
			else if (fname == 1)
				firstName = "Ned";
			else if (fname == 2)
				firstName = "Paul";
			else if (fname == 3)
				firstName = "Eric";
			else if (fname == 4)
				firstName = "Andrew";
			else if (fname == 5)
				firstName = "Joshua";
			else
				firstName = "Gary";
			if (a == 0)
				photo = c.getResources().getDrawable(R.drawable.man1);
			if (a == 1)
				photo = c.getResources().getDrawable(R.drawable.man2);
			if (a == 2)
				photo = c.getResources().getDrawable(R.drawable.man3);
			if (a == 3)
				photo = c.getResources().getDrawable(R.drawable.man4);
			if (a == 4)
				photo = c.getResources().getDrawable(R.drawable.man5);
			if (a == 5)
				photo = c.getResources().getDrawable(R.drawable.man6);
			if (a == 6)
				photo = c.getResources().getDrawable(R.drawable.questionhead);
		}

		if (lname == 0)
			lastName = "Jones";
		else if (lname == 1)
			lastName = "Smith";
		else if (lname == 2)
			lastName = "Bonifant";
		else if (lname == 3)
			lastName = "Daweson";
		else if (lname == 4)
			lastName = "Hart";
		else if (lname == 5)
			lastName = "Moore";
		else if (lname == 6)
			lastName = "Unknown";
		else
			lastName = "Sue";
		hospital = "NLM (Testing)";
		report = false;
		event = "Test Patient Records";
		error = 0;
	}

	/**
	 * Initializes all fields but rowIndex, Photo, uuid and boxId
	 * 
	 * @param patientId
	 *            -long
	 * @param lastName
	 *            -string
	 * @param firstName
	 *            -string
	 * @param age
	 *            -Age
	 * @param gender
	 *            -Gender
	 * @param zone
	 *            -Zone
	 * @param event
	 *            -string
	 * @param hospital
	 *            -string
	 * @param date
	 *            -long
	 * @param error
	 *            -int
	 * @param images
	 *            -ArrayList<Image>
	 */
	public Patient(String prefixPid, long patientId, String fpid, String lastName, String firstName,
			Age age, Gender gender, Zone zone, String event, String hospital,
			long date, int error, ArrayList<Image> images) {

        Initialize();

		this.prefixPid = prefixPid;
		this.patientId = patientId;
		this.fpid = fpid;
		this.uuid = "";
		this.lastName = lastName;
		this.firstName = firstName;
		this.age = age;
		this.gender = gender;
		this.zone = zone;
		this.event = event;
		this.hospital = hospital;
		this.date = date;
		this.error = error;
		this.images = images;

        this.lastUpdate = "";
	}

	public Patient(String prefixPid, long patientId, String fpid, String lastName, String firstName,
			Age age, Gender gender, MyZone myZone, String event, String hospital,
			long date, int error, ArrayList<Image> images) {

        Initialize();

		this.prefixPid = prefixPid;
		this.patientId = patientId;
		this.fpid = fpid;
		this.uuid = "";
		this.lastName = lastName;
		this.firstName = firstName;
		this.age = age;
		this.gender = gender;
//		this.zone = zone;
		this.myZone = myZone;
		this.event = event;
		this.hospital = hospital;
		this.date = date;
		this.error = error;
		this.images = images;

        this.lastUpdate = "";
	}

	/**
	 * Compares two patients based on age, gender, fN, lN, ID, and zone
	 */
	public boolean equalsPatient(Patient p) {
		return 
				this.age == p.age 
				&& this.gender == p.gender
				&& this.firstName.equals(p.firstName)
				&& this.lastName.equals(p.lastName)
				&& this.prefixPid == p.prefixPid 
				&& this.patientId == p.patientId 
				&& this.fpid.equalsIgnoreCase(p.fpid) 
				&& this.zone == p.zone
				&& this.uuid == p.uuid
				&& this.boxId == p.boxId
				&& this.comments == p.comments;
	}

	/**
	 * This method uses and XML Serializer to create the xml report of the
	 * patient, and then returns its string value to be sent to PL via
	 * webservices
	 * 
	 * @param p
	 *            Patient: the patient being reported
	 * @param e
	 *            Event: the event the report belongs to
	 * @param h
	 *            Hospital: the hospital the report belongs to
	 * @param c
	 *            Context: required for getting the image byte array and the
	 *            username of the reporter
	 * @return a TriagePic xml report as required per pl soap webservices
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws java.io.IOException
	 * 
	 */

	public static String toXML(Patient p, Event e, Hospital h, Context c)
			throws IllegalArgumentException, IllegalStateException, IOException {
		XmlSerializer s = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssz");
		String date = format.format(new Date(p.date));
		s.setOutput(writer);
		s.startDocument("UTF-8", false);
		s.startTag("", "EDXLDistribution");
		s.attribute("", "xmlns", "urn:oasis:names:tc:emergency:EDXL:DE:1.0");

		s.startTag("", "distributionID");
		s.text("NPI " + h.npi + " " + date);
		s.endTag("", "distributionID");

		s.startTag("", "senderID");
		s.text(h.name + " " + h.email);
		s.endTag("", "senderID");

		s.startTag("", "dateTimeSent");
		s.text(date);
		s.endTag("", "dateTimeSent");

		s.startTag("", "distributionStatus");
		s.text(e.type);
		s.endTag("", "distributionStatus");

		s.startTag("", "distributionType");
		if (p.uuid == null)
			s.text("Report");
		else
			s.text("Update");
		s.endTag("", "distributionType");

		s.startTag("", "combinedConfidentiality");
		s.text("UNCLASSIFIED AND NOT SENSITIVE");
		s.endTag("", "combinedConfidentiality");

		s.startTag("", "keyword");

		s.startTag("", "valueListUrn");
		s.text("urn:oasis:names:tc:emergency:cap:1.1");
		s.endTag("", "valueListUrn");

		s.startTag("", "value");
		s.text("Health");
		s.endTag("", "value");

		s.startTag("", "value");
		s.text("Rescue");
		s.endTag("", "value");

		s.endTag("", "keyword");

		s.startTag("", "incidentID");
		s.text(String.valueOf(e.incident_id));
		s.endTag("", "incidentID");

		s.startTag("", "contentObject");

		s.startTag("", "contentDescription");
		s.text("LPF notification - disaster victim arrives at hospital triage station");
		s.endTag("", "contentDescription");

		s.startTag("", "xmlContent");

		s.startTag("", "lpfContent");

		s.startTag("", "version");
		s.text("1.3");
		s.endTag("", "version");

		s.startTag("", "login");

		s.startTag("", "userName");
		s.text(c.getSharedPreferences("Info", 0).getString("un", "Nousername"));
		s.endTag("", "userName");

		s.startTag("", "machineName");
		s.text(android.os.Build.MODEL);
		s.endTag("", "machineName");

		s.endTag("", "login");

		s.startTag("", "person");

//		if (h.pIDPrefix == null)
//			h.pIDPrefix = "Practice-";

//		String id = h.pIDPrefix + p.patientId;
//		String id = String.valueOf(p.prefixPid) + "-" + String.valueOf(p.patientId);
//        String id = String.valueOf(p.prefixPid) + "-" + p.fpid;
//  switch back '-'
        String id = p.prefixPid.toString() + "-" + p.fpid;
//        String id = p.prefixPid.toString() + p.fpid;
		s.startTag("", "personId");
		s.text(id);
		s.endTag("", "personId");

		s.startTag("", "eventName");
		s.text(e.shortname);
		s.endTag("", "eventName");

		s.startTag("", "eventLongName");
		s.text(e.name);
		s.endTag("", "eventLongName");

		s.startTag("", "organization");

		s.startTag("", "orgName");
		s.text(h.name);
		s.endTag("", "orgName");

		s.startTag("", "orgId");
		s.text(h.npi);
		s.endTag("", "orgId");

		s.endTag("", "organization");

		s.startTag("", "lastName");
		s.text(p.lastName);
		s.endTag("", "lastName");

		s.startTag("", "firstName");
		s.text(p.firstName);
		s.endTag("", "firstName");

		s.startTag("", "gender");
		s.text(p.gender.chara);
		s.endTag("", "gender");

		s.startTag("", "genderEnum");
		s.text("M, F, U, C");
		s.endTag("", "genderEnum");

		s.startTag("", "genderEnumDesc");
		s.text("Male; Female; Unknown; Complex(M/F)");
		s.endTag("", "genderEnumDesc");

		s.startTag("", "peds");
		s.text(p.age.xml);
		s.endTag("", "peds");

		s.startTag("", "pedsEnum");
		s.text("Y,N");
		s.endTag("", "pedsEnum");

		s.startTag("", "pedsEnumDesc");
		s.text("Pediatric patient? Yes, No");
		s.endTag("", "pedsEnumDesc");

//		String zone = p.zone.name;
		String zone = p.myZone.name.toString();
		s.startTag("", "triageCategory");
		s.text(zone);
		s.endTag("", "triageCategory");

		s.startTag("", "triageCategoryEnum");
		s.text("Green, BH Green, Yellow, Red, Gray, Black");
		s.endTag("", "triageCategoryEnum");

		s.startTag("", "triageCategoryEnumDesc");
		s.text("Treat eventually if needed; Treat for behavioral health; Treat soon; Treat...");
		s.endTag("", "triageCategoryEnumDesc");

        // add comments
        s.startTag("", "comments");
        s.text(p.comments);
        s.endTag("", "comments");

		s.endTag("", "person");

		s.endTag("", "lpfContent");

		s.endTag("", "xmlContent");

		s.endTag("", "contentObject");

		if (p.images != null) {
			h = null;
			e = null;
			format = null;
			date = null;
			int size = p.images.size();
			for (int j = 0; j < size; j++) {
				Image i = p.images.get(j);

                // changes made in version 9.0.0
                String encodedImage = i.getEncoded();

				s.startTag("", "contentObject");

				// image caption
				s.startTag("", "contentDescription");
/* old
                s.text(id
                        + " "
                        + zone
                        + (j == 0 ? " - " + i.getCaption() : "s" + j + " - "
                        + i.getCaption()));
                        */

               s.text(id
                        + " "
                        + zone
                        + " "
                        + (j == 0 ? " - " + i.getCaption() : "s" + String.valueOf(j) + " - "
                        + i.getCaption()));

				s.endTag("", "contentDescription");

				s.startTag("", "nonXMLContent");

				s.startTag("", "mimeType");
                s.text("image/jpeq");
				s.endTag("", "mimeType");

				s.startTag("", "size");
				s.text(i.getSize());
				s.endTag("", "size");

				s.startTag("", "digest");
				s.text(i.getDigest());
				s.endTag("", "digest");

				s.startTag("", "uri");
				s.text(i.getUri());
				s.endTag("", "uri");

				s.startTag("", "contentData");
				s.text(encodedImage);
				s.endTag("", "contentData");

				s.endTag("", "nonXMLContent");
				s.endTag("", "contentObject");
			}

		}

		s.endTag("", "EDXLDistribution");
		s.endDocument();

		String result = writer.toString();
		System.out.println(result);

        generateXMLFileOnSD("xmlLog.xml", result);

		return result;
	}

    public static void generateXMLFileOnSD(String fileName, String stringBody){
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "TriagePic");
            if (!root.exists()){
                root.mkdirs();
            }
            File xmlFile = new File(root, fileName);
            FileWriter writer = new FileWriter(xmlFile);
            writer.append(stringBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JSONPATIENT1 FORMAT - added in version 9.0.0
    public static JSONObject toJSON(Patient p, Event e, Hospital h, Context c){
        JSONObject json = new JSONObject();
        try {
            String pid;
            // changes made for temporary solution. Current the server wants to work in this way.
            if (p.prefixPid.contains("NLM")) {
                pid = p.prefixPid.toString() + "-" + p.fpid;
            }
            else {
                pid = p.prefixPid.toString() + p.fpid;
            }
            json.put("patientId", pid.toString());
            json.put("hospitalUUID", h.uuid);
            json.put("givenName", p.firstName);
            json.put("familyName",p.lastName);
            json.put("gender", p.gender.chara);
            boolean ped = true;
            if (p.age.xml == "Y"){
                ped = true;
            }
            else {
                ped = false;
            }
            json.put("ped", ped);
//          json.put("age", "will be a number");
            json.put("zone", p.myZone.name.toString());
            json.put("comment", p.comments);
//            json.put("creationDate", "2014-01-01 16:20:00 UTC");
//            json.put("expiryDate", "2022-01-01 16:20:00 UTC");

            // add image - start
            JSONArray jsonImgArr = new JSONArray();
            for (int j = 0; j < p.images.size(); j++) {
                Image i = p.images.get(j);

                // image - start
                JSONObject jsonImg = photoInfoInput(j, i, c);
                jsonImgArr.put(jsonImg);
                // image - end
            }
            json.put("addImage", jsonImgArr);
            // add image - end
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    // JSONPATIENT1 FORMAT - added in version 9.0.3
    public static JSONObject toJSON(Patient p, Event e, Hospital h, Context c, ArrayList<Image> imagesToDelete){
        JSONObject json = new JSONObject();
        try {
            String pid;
            // changes made for temporary solution. Current the server wants to work in this way.
            if (p.prefixPid.contains("NLM")) {
                pid = p.prefixPid.toString() + "-" + p.fpid;
            }
            else {
                pid = p.prefixPid.toString() + p.fpid;
            }
            json.put("patientId", pid.toString());
            json.put("hospitalUUID", h.uuid);
            json.put("givenName", p.firstName);
            json.put("familyName", p.lastName);
            json.put("gender", p.gender.chara);
            boolean ped = true;
            if (p.age.xml == "Y"){
                ped = true;
            }
            else {
                ped = false;
            }
            json.put("ped", ped);
//          json.put("age", "will be a number");
            json.put("zone", p.myZone.name.toString());
            json.put("comment", p.comments);
//            json.put("creationDate", "2014-01-01 16:20:00 UTC");
//            json.put("expiryDate", "2022-01-01 16:20:00 UTC");

            // add image - start
            // remove image
            JSONArray jsonRemoveImgArr = new JSONArray();
            for (int j = 0; j < imagesToDelete.size(); j++) {
                Image i = imagesToDelete.get(j);

                // image - start
                JSONObject jsonImg = photoDeleteInfoInput(j, i, c);
                jsonRemoveImgArr.put(jsonImg);
                // image - end
            }
            json.put("removeImage", jsonRemoveImgArr);
            // add image - end
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public void extractJSON(JSONObject o, final String nameSpaceImage) {
        try {
            // p_uuid
            String p_uuid = "";
            if (o.has("p_uuid")) {
                if (!o.isNull("p_uuid")) {
                    p_uuid = o.getString("p_uuid");
                }
            }
            this.setUuid(p_uuid);
            Log.i(TAG, "p_uuid = " + p_uuid);

            // family_name
            String family_name = "";
            if (o.has("family_name")) {
                if (!o.isNull("family_name")) {
                    family_name = o.getString("family_name");
                }
            }
            this.setLastName(family_name);
            Log.i(TAG, "family_name = " + family_name);

            // given_name
            String given_name = "";
            if (o.has("given_name")) {
                if (!o.isNull("given_name")) {
                    given_name = o.getString("given_name");
                }
            }
            this.setFirstName(given_name);
            Log.i(TAG, "given_name = " + given_name);

            // hospital_uuid
            String hospital_uuid = "";
            if (o.has("hospital_uuid")) {
                if (!o.isNull("hospital_uuid")) {
                    hospital_uuid = o.getString("hospital_uuid");
                }
            }
            Log.e("extractJSON", "hospital_uuid = " + hospital_uuid);

            String last_updated = "";
            if (o.has("last_updated")){
                if (!o.isNull("last_updated")){
                    last_updated = o.getString("last_updated");
                }
            }
            /*
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            try {
                Date date = sdf.parse(last_updated);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            */
//            this.setLastUpdate(last_updated);

            // location
            JSONObject location = new JSONObject();
            JSONObject gps = new JSONObject();
            JSONObject edxl = new JSONObject();

            if (o.has("location")) {
                if (!o.isNull("location")) {
                    location = o.getJSONObject("location");
                    // street1
                    String street1 = "";
                    if (location.has("street1")) {
                        if (!location.isNull("street1")) {
                            street1 = location.getString("street1");
                        }
                    }
                    String street2 = "";
                    if (location.has("street2")) {
                        if (!location.isNull("street2")) {
                            street2 = location.getString("street2");
                        }
                    }
                    String city = "";
                    if (location.has("city")) {
                        if (!location.isNull("city")) {
                            city = location.getString("city");
                        }
                    }
                    String region = "";
                    if (location.has("region")) {
                        if (!location.isNull("region")) {
                            region = location.getString("region");
                        }
                    }
                    String postal_code = "";
                    if (location.has("postal_code")) {
                        if (!location.isNull(postal_code)) {
                            postal_code = location.getString("postal_code");
                        }
                    }
                    String country = "";
                    if (location.has("country")) {
                        if (!location.isNull(country)) {
                            country = location.getString("country");
                        }
                    }
                    gps = new JSONObject();
                    String latitude = "";
                    String longitude = "";
                    if (location.has("gps")) {
                        if (!location.isNull("gps")) {
                            gps = location.getJSONObject("gps");

                            if (gps.has("latitude")) {
                                if (!gps.isNull("latitude")) {
                                    latitude = gps.getString("latitude");
                                }
                            }
                            if (gps.has("longitude")) {
                                if (!gps.isNull("longitude")) {
                                    longitude = gps.getString("longitude");
                                }
                            }
                        }
                    }
                }

                String opt_gender = "";
                if (o.has("opt_gender")) {
                    if (!o.isNull("opt_gender")) {
                        opt_gender = o.getString("opt_gender");
                    }
                }
                switch (opt_gender) {
                    case "mal":
                        this.gender = Gender.MALE;
                        break;
                    case "fml":
                        this.gender = Gender.FEMALE;
                        break;
                    default:
                        this.gender = Gender.UNKNOWN;
                        break;
                }

                /**
                 * read the age.
                 */
			/*
                String years_old = "";
                String min_age = "";
                String max_age = "";
                if (o.has("years_old")) {
                    if (!o.isNull("years_old")) {
                        years_old = o.getString("years_old");
                    }
                }
                // if years_old can be read
                if (!years_old.isEmpty()){
                    int old = Integer.valueOf(years_old);
                    if (old < SEVEN_TEEN_YEAR_OLD){
                        this.age = Age.PEDIATRIC;
                    }
                    else {
                        this.age = Age.ADULT;
                    }
                }
                // cannot read years_age
                else {
                    // read min_age
                    if (o.has("min_age")) {
                        if (!o.isNull("min_age")) {
                            min_age = o.getString("min_age");
                        }
                    }
                    // read max_age
                    if (o.has("max_age")) {
                        if (!o.isNull("max_age")) {
                            max_age = o.getString("max_age");
                        }
                    }
                    if (min_age.isEmpty() && max_age.isEmpty()){
                        this.age = Age.UNKNOWN;
                    }
                    else if (!min_age.isEmpty()) {
                        int min = Integer.parseInt(min_age);
                        if (min > SEVEN_TEEN_YEAR_OLD) {
                            this.age = Age.ADULT;
                        }
                    }
                    else if (!max_age.isEmpty()) {
                        int max = Integer.parseInt(max_age);
                        if (max <= SEVEN_TEEN_YEAR_OLD){
                            this.age = Age.PEDIATRIC;
                        }
                    }
                    else {
                        this.age = Age.ADULT;
                    }
                }
                */

				/**
				 * rewrite the age parse.
				 * changed in version 9.0.3
				 */
				String min_age = "";
				String max_age = "";
				int minAge = 0;
				int maxAge = 0;

				// read min_age
				if (o.has("min_age")) {
					if (!o.isNull("min_age")) {
						min_age = o.getString("min_age");
						minAge = Integer.parseInt(min_age);
					}
				}
				// read max_age
				if (o.has("max_age")) {
					if (!o.isNull("max_age")) {
						max_age = o.getString("max_age");
						maxAge = Integer.parseInt(max_age);
					}
				}

				if (min_age.isEmpty() && max_age.isEmpty()){
					this.age = Age.UNKNOWN;
				}
				else if (minAge > SEVEN_TEEN_YEAR_OLD) {
					this.age = Age.ADULT;
				}
				else if (maxAge <= SEVEN_TEEN_YEAR_OLD) {
					this.age = Age.PEDIATRIC;
				}
				else {
					this.age = Age.UNKNOWN;
				}

                // other_comments
                String other_comments = "";
                if (o.has("other_comments")) {
                    if (!o.isNull("other_comments")) {
                        other_comments = o.getString("other_comments");
                    }
                }
                this.setComments(other_comments);
                Log.i(TAG, "p_uuid: " + p_uuid + " family_name: " + family_name + " other_comments: " + other_comments);

                JSONArray edxlArr = new JSONArray();

                if (o.has("edxl")) {
                    edxlArr = o.getJSONArray("edxl");
                    for (int i = 0; i < 1; i++) { // always one element.
                        edxl = edxlArr.getJSONObject(i);

                        String mass_casualty_id = "";
                        if (edxl.has("mass_casualty_id")) {
                            if (!edxl.isNull("mass_casualty_id")) {
                                mass_casualty_id = edxl.getString("mass_casualty_id");
                            }
                        }
                        if (mass_casualty_id.isEmpty()){
                            this.prefixPid = "";
                            this.patientId = 0;
                        }
                        else if (!mass_casualty_id.contains("-")){
							/**
                             * no hyphen is the new format
							 * add codes here to fix the changes
							 */
							List<String>listStr = parseCasualtyId(mass_casualty_id);
                            this.prefixPid = listStr.get(0);
                            this.patientId = Integer.parseInt(listStr.get(1));
                        }
                        else if (mass_casualty_id.endsWith("-")){
                            String[] parts = mass_casualty_id.split("-");
                            this.prefixPid = parts[0];
                            this.patientId = 0;
                        }
                        else {
                            String[] parts = mass_casualty_id.split("-");
                            this.prefixPid = parts[0];
                            this.patientId = Integer.parseInt(parts[1]);
                        }

                        // triage_category
                        String triage_category = "";
                        if (edxl.has("triage_category")) {
                            if (!edxl.isNull("triage_category")) {
                                triage_category = edxl.getString("triage_category");
                            }
                        }
                        if (triage_category.equalsIgnoreCase("Green")){
                            this.myZone = MyZone.GREEN;
                        }
                        else if (triage_category.equalsIgnoreCase("BH Green")){
                            this.myZone = myZone.BH_GREEN;
                        }
                        else if (triage_category.equalsIgnoreCase("Yellow")){
                            this.myZone = myZone.YELLOW;
                        }
                        else if (triage_category.equalsIgnoreCase("Red")){
                            this.myZone = myZone.RED;
                        }
                        else if (triage_category.equalsIgnoreCase("Gray")){
                            this.myZone = myZone.GRAY;
                        }
                        else if (triage_category.equalsIgnoreCase("Black")){
                            this.myZone = myZone.BLACK;
                        }
                        else if (triage_category.equalsIgnoreCase("Unknown")){
                            this.myZone = myZone.UNASSIGNED;
                        }
                        else {
                            this.myZone = myZone.UNASSIGNED;
                        }
                    }
                }

                String str;
                if (o.has("images")){
                    ArrayList<Image> images = new ArrayList<>();
                    Log.i(TAG, "Has images: " + images.size());

                    edxlArr = o.getJSONArray("images");
                    int principalPos = 0;
                    for (int i = 0; i < edxlArr.length(); i++){
                        JSONObject imgO = edxlArr.getJSONObject(i);
                        Image img = new Image();

                        String url = "";
                        if (imgO.has("url")) {
                            if (!imgO.isNull("url")) {
                                url = imgO.getString("url");
                            }
                        }

                        img.setImageUrl(url, nameSpaceImage); // imageUriForFatch is defined here as well.

                        // this is to make sure the principle image is always the first image.
                        String principal = "";
                        if (imgO.has("principal")) {
                            if (!imgO.isNull("principal")) {
                                principal = imgO.getString("principal");
                                if (principal.equalsIgnoreCase("1")){
                                    principalPos = i;
                                }
                            }
                        }

                        String image_height = "";
                        if (imgO.has("image_height")) {
                            if (!imgO.isNull("image_height")) {
                                image_height = imgO.getString("image_height");
                            }
                        }
                        img.setImageHeight(image_height);

                        String image_width = "";
                        if (imgO.has("image_width")) {
                            if (!imgO.isNull("image_width")) {
                                image_width = imgO.getString("image_width");
                            }
                        }
                        img.setImageWidth(image_width);
                        images.add(img);
                    }

                    // if the principal is not the first one, switch it
                    if (principalPos > 0) {
                        Image img = images.get(principalPos);
                        images.remove(principalPos);
                        images.add(0, img);
                    }
                    this.setImages(images);
                }
                else {
                    str = "No images";
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

	private List<String> parseCasualtyId(String toParse) {
		final Pattern VALID_PATTERN = Pattern.compile("[0-9]+|[A-Z]+");
		List<String> chunks = new LinkedList<String>();
		Matcher matcher = VALID_PATTERN.matcher(toParse);
		while (matcher.find()) {
			chunks.add( matcher.group() );
		}
		return chunks;
	}

	public static String JSONParserForImageUrl(String string, String recordsFoundString) {
        String url = "";
        String toParseString = "{" + "\"" + string + "\":" + recordsFoundString + "}";
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray  jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject

                String p_uuid = o.getString("p_uuid");
                String images = o.getString("images");

                if (images.isEmpty() == false){ // To parser images
                    // take off "[" and "]".
                    images = "{" + "\"" + "images" + "\":" + images + "}";

                    JSONObject jsonObjImages;
                    try {
                        jsonObjImages = new JSONObject(images);
                        JSONArray  jsonArrayImages = jsonObjImages.getJSONArray("images"); // get all events as json objects from Events array
                        for (int j = 0; j < jsonArrayImages.length(); j++){
                            JSONObject oj = jsonArrayImages.getJSONObject(j); // create a single event jsonObject
                            url = oj.getString("url");
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                    return url;
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string
        return url;
    }


/*

            String pid = o.getString("patientId");
            if (pid.equalsIgnoreCase("null")){
                pid = "";
            }
            if (pid.isEmpty()){
                prefixPid = "";
                fpid = "";
            }
            String[] parts = pid.split("-");
            prefixPid = parts[0];
            fpid = parts[1];
            */




            ///////////
            // what got from v34 document
//            {"genderMale":true,"genderFemale":true,"genderComplex":true,"genderUnknown":true,"ageChild":true,"ageAdult":true,"ageUnknown":true,"hospital":"1","hasImage":false,"Green":false,"BH Green":false,"Yellow":true,"Red":false,"Gray":false,"Black":false,"Unknown":true}

            // what got from web server
//            {"p_uuid":"triagetrak.nlm.nih.gov\/person.3104219","full_name":"sa kum","family_name":"kum","given_name":"sa","alternate_names":null,"profile_urls":null,"incident_id":"1","hospital_uuid":"1","expiry_date":"2016-02-27T15:53:48-05:00","pet":null,"opt_status":"unk","last_updated":"2015-02-27T15:51:55-05:00","creation_time":"2015-02-27T15:48:21-05:00","location":{"street1":null,"street2":null,"neighborhood":null,"city":null,"region":null,"postal_code":null,"country":null,"gps":{"latitude":null,"longitude":null}},"birth_date":null,"opt_race":null,"opt_religion":null,"opt_gender":"mal","years_old":null,"min_age":"0","max_age":"17","last_seen":null,"last_clothing":null,"other_comments":"","rep_uuid":"3","rep_full_name":"Anonymous User","is_editable":1,"reporter_username":"anonymous","revisions_made":"2","images":[],"voice_notes":[],"person_notes":[],"edxl":[{"last_posted":"2015-02-27 15:51:56","login_account":null,"login_machine":null,"mass_casualty_id":"NLM-43432323","triage_category":"Green","sender_id":null,"distr_id":null}]}

//            {"p_uuid":"triagetrak.nlm.nih.gov\/person.3104219","full_name":"sa kum","family_name":"kum","given_name":"sa","alternate_names":null,"profile_urls":null,"incident_id":"1","hospital_uuid":"1","expiry_date":"2016-02-27T15:53:48-05:00","pet":null,"opt_status":"unk","last_updated":"2015-02-27T15:51:55-05:00","creation_time":"2015-02-27T15:48:21-05:00","location":{"street1":null,"street2":null,"neighborhood":null,"city":null,"region":null,"postal_code":null,"country":null,"gps":{"latitude":null,"longitude":null}},"birth_date":null,"opt_race":null,"opt_religion":null,"opt_gender":"mal","years_old":null,"min_age":"0","max_age":"17","last_seen":null,"last_clothing":null,"other_comments":"","rep_uuid":"3","rep_full_name":"Anonymous User","is_editable":1,"reporter_username":"anonymous","revisions_made":"2","images":[],"voice_notes":[],"person_notes":[],"edxl":[{"last_posted":"2015-02-27 15:51:56","login_account":null,"login_machine":null,"mass_casualty_id":"NLM-43432323","triage_category
            ////

        /*
        String pid = p.prefixPid.toString() + "-" + p.fpid;
        json.put("patientId", pid.toString());
        json.put("hospitalUUID", h.uuid);
        json.put("givenName", p.firstName);
        json.put("familyName",p.lastName);
        json.put("gender", p.gender.chara);
        boolean ped = true;
        if (p.age.xml == "Y"){
            ped = true;
        }
        else {
            ped = false;
        }
        json.put("ped", ped);
//          json.put("age", "will be a number");
        json.put("zone", p.myZone.name.toString());
        json.put("comment", p.comments);
//            json.put("creationDate", "2014-01-01 16:20:00 UTC");
//            json.put("expiryDate", "2022-01-01 16:20:00 UTC");
*/

    private static JSONObject photoInfoInput(int j, Image img, Context c) {
        JSONObject jsonImg = new JSONObject();
        try {
            jsonImg.put("primary", j == 0 ? true : false);
            jsonImg.put("data", img.getEncoded().toString());

            // add tag array - start
            JSONArray jsonTagArr = new JSONArray();
            JSONObject jsonTag = photoTagInput(img, c);
            jsonTagArr.put(jsonTag);
            jsonImg.put("tags", jsonTagArr);
            // add tag array - end
        }
        catch (Exception e){
            Toast.makeText(c, "Exception error in JSON for image defined: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return jsonImg;
    }

    private static JSONObject photoDeleteInfoInput(int j, Image img, Context c) {
        JSONObject jsonImg = new JSONObject();
        try {
            jsonImg.put("url", img.getImageUrl().toString());
        }
        catch (Exception e){
            Toast.makeText(c, "Exception error in get removing image url: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return jsonImg;
    }

    private static JSONObject photoTagInput(Image i, Context c) {
        JSONObject j = new JSONObject();
        try {
            Image.Rect r = i.getRect();

            /*
            j.put("x", r.getX());
            j.put("y", r.getY());
            j.put("w", r.getW());
            j.put("h", r.getH());
            */

            j.put("x", 0);
            j.put("y", 0);
            j.put("w", Image.MAX_SIZE);
            j.put("h", Image.MAX_SIZE);

            j.put("text", i.getCaption());
        }
        catch (Exception e){
            Toast.makeText(c, "Exception error in JSON for tag defined: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return j;
    }

    /**
	 * @return the date string for the table in queue
	 */
	protected String dateStringTable() {
		Date d = new Date(date);
		return new SimpleDateFormat("MMM dd\nhh:mm a").format(d);

	}

	/**
	 * @return the date string for the cards in queue
	 */
	protected String dateStringCard() {
		Date d = new Date(date);
		return new SimpleDateFormat("MMM dd hh:mm a").format(d);
	}

	/**
	 * @return string used for the left side textfield of the card view
	 */
	public String cardTextLeft() {

/*        return this.dateStringCard() + "\nPatient: " + this.prefixPid + "-" + this.patientId + "\n"
                + this.firstName + " " + this.lastName + "\n" + this.event
                + "\n" + this.hospital;
                */

        // non '-'
        return this.dateStringCard() + "\nPatient: " + this.prefixPid + this.patientId + "\n"
                + this.firstName + " " + this.lastName + "\n" + this.event
                + "\n" + this.hospital;
	}

	/**
	 * @param c
	 *            the activity calling this method's context, required to
	 *            convert the patient's error number into a String.
	 * @return string used for the rightside text field of the card view
	 */
	public String cardTextRight(Context c) {
		return gender.name + "\n" + 
				age.name + "\n" + 
//				zone.name + "\n"
				myZone.name + "\n"
				+ Errors.getErrorMesg(c, this.error);
	}

	/**
	 * The below are enumerations of patient data fields for Age, Gender and Zone
	 */

	/**
	 * There are two age types, Adult, and Pediatric, they have 4 fields, their
	 * enumerated int value, a string representation of thier enumeration, and
	 * two chars, table and XMl, table is the first char of the enumeration
	 * string for printing in small text fields like the table, and xml, for
	 * patient reports sent via webservices
	 */
	public static enum Age {
		ADULT(R.id.adult, "Adult", "A", "N"), //
		PEDIATRIC(R.id.peds, "Pediatrics", "P", "Y"),
		UNKNOWN(0, "Unknown", "U", "U");
		int i;
		String name, xml;
		String table;

		Age(int i, String name, String table, String xml) {
			this.i = i;
			this.name = name;
			this.table = table;
			this.xml = xml;
		}

		static Age getAge(int i) {
			for (Age a : Age.values())
				if (a.i == i)
					return a;
			return Age.ADULT;
		}
	}

	/**
	 * There are four gender types MALE, FEMALE, UNKNOWN, and COMPLEX( though
	 * not in use). Each gender has a enumerated int(for identifying genders
	 * from database), a string (for printing in views) and a String(for patient
	 * XML reports).
	 */
	public static enum Gender {
		MALE(R.id.male, "Male", "M"), //
		FEMALE(R.id.female, "Female", "F"), //
		UNKNOWN(R.id.gendergroup, "Unknown", "U");//
		int i;
		String name;
		String chara;

		Gender(int i, String name, String chara) {
			this.i = i;
			this.name = name;
			this.chara = chara;
		}

		static Gender getGender(int i) {
			for (Gender g : Gender.values())
				if (g.i == i)
					return g;
			return Gender.UNKNOWN;
		}
	}

	/**
	 * The zone the patient is sent to: either GREEN, GREEN BH, YELLOW, RED,
	 * GRAY, or BLACK (also UNASSIGNED, though not in use), each zone has 4
	 * fields the zones enumerated int, the zone's string name, the zone color
	 * value, and the the boolean of whether text with this zone's color as a
	 * background should be black or white.
	 * 
	 * zone button to say something like "ER" or "Morgue"
	 */
	public static enum Zone {
		GREEN(R.id.green, "Green", R.drawable.cell_shape_green, true), //
		BH_GREEN(R.id.green_bh, "BH Green", R.drawable.cell_shape_light_green, true), //
		YELLOW(R.id.yellow, "Yellow", R.drawable.cell_shape_yellow, true), //
		RED(R.id.red, "Red", R.drawable.cell_shape_red, false), //
		GRAY(R.id.gray, "Gray", R.drawable.cell_shape_gray, false), //
		BLACK(R.id.black, "Black", R.drawable.cell_shape_black, false), //
		UNASSIGNED(R.id.patient_zone_box, "UAS", R.drawable.cell_shape_white, false);//

		public int i;
        public String name;
        public int color;
        public boolean black;

        public int getI(){return i;}

		Zone(int i, String name, int color, boolean black) {
			this.i = i;
			this.name = name;
			this.color = color;
			this.black = black;
		}
		
		static Zone getZone(int i) {
			for (Zone z : Zone.values()){
				if (z.i == i){
					return z;
				}
			}
			return Zone.GREEN;
		}

        void setZone(Zone zone){
            this.i = zone.i;
            this.name = zone.name;
            this.color = zone.color;
            this.black = zone.black;
        }
	}

	public static enum MyZone{
		GREEN(0, "Green", R.drawable.cell_shape_green, true), //
		BH_GREEN(1, "BH Green", R.drawable.cell_shape_light_green, true), //
		YELLOW(2, "Yellow", R.drawable.cell_shape_yellow, true), //
		RED(3, "Red", R.drawable.cell_shape_red, false), //
		GRAY(4, "Gray", R.drawable.cell_shape_gray, false), //
		BLACK(5, "Black", R.drawable.cell_shape_black, false), //
		UNASSIGNED(6, "UAS", R.drawable.cell_shape_white, false);//

		final protected int i;
		final protected String name;
		final protected int color;
		final protected boolean black;

		MyZone(int i, String name, int color, boolean black) {
			this.i = i;
			this.name = name;
			this.color = color;
			this.black = black;
		}
		
		static MyZone getZone(int i) {
			for (MyZone z : MyZone.values()){
				if (z.i == i){
					return z;
				}
			}
			return MyZone.UNASSIGNED;
		}				
	}

	public String toString() {
		return "Event: " + this.event + //
				" Date: " + this.date + //
				" Error: " + this.error + //
				" First Name: " + this.firstName + //
				" Last Name: " + this.lastName + //
				" Hospital: " + this.hospital + //
//                " Patient Id: " + this.prefixPid + "-" + this.patientId + //
                // no '-'
                " Patient Id: " + this.prefixPid + "-" + this.patientId + //
				" Age:" + this.age + //
				" Images: " + this.images + //
				" Gender: " + this.gender;
	}

    // added version 9.0.0
    public static Bitmap decodeStringToBitmap(String str){
        Bitmap bm = null;
        if (str.length() <= 0){
            return bm;
        }
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        if (data.length > 0) {
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return bm;
    }

    // added version 9.0.0
    public static String encodeBitmapToString(Bitmap bm){
        String str;
        ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,50,byteArryPhoto);
        byte[] b = byteArryPhoto.toByteArray();
        str = Base64.encodeToString(b, Base64.DEFAULT);
        return str;
    }

    // added in version 9.0.0
    public static Bitmap uriToBitmap(String uri, int width, int height, Context c, boolean face){
        Bitmap bm = ReportPatientImageHandler.resizedBitmap(uri, width, height, c, false);
        return bm;
    }

    public static String uriToEncodedString(String uri, int width, int height, Context c, boolean face){
        Bitmap bm = uriToBitmap(uri, width, height, c, face);
        if (bm == null){
            return "";
        }
        String str = encodeBitmapToString(bm);
        return str;
    }
}