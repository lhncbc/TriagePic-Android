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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
	// database
	private static final String DATABASE_NAME = "patientreports.db";
	private static final int DATABASE_VERSION = 112;

    // 112 - add encoded image search
    // 109 - add curPage in ViewSettings table
    // 106 - correct HH green to BH green
    // 105 - added filters view settings
    // 104 - added filters view settings
    // 103 - added Base64 encoded string. Changed made in version 9.0.0

	// Patient Table
	protected static final String TABLE_REPORTS = "Reports";
	protected static final String COL_ID = "ID"; // 0
	protected static final String COL_PREFIX_PID = "Prefix_PID"; //1 
	protected static final String COL_PID = "Patient_ID"; //2 
	protected static final String COL_FPID = "FPID"; //3 
	protected static final String COL_LN = "Last_Name"; // 4
	protected static final String COL_FN = "First_Name"; //5 
	protected static final String COL_AGE = "Age"; // 6
	protected static final String COL_GEN = "Gender"; // 7 
	protected static final String COL_ZONE = "Zone"; // 8
	protected static final String COL_EVENT = "Event"; // 9
	protected static final String COL_HOSPITAL = "Hospital"; // 10
	protected static final String COL_DATE = "Date"; // 11
	protected static final String COL_ERROR = "Error"; // 12
	protected static final String COL_URI = "Uri"; // 13
	protected static final String COL_CAPTION = "Caption"; // 14
	protected static final String COL_SIZE = "Size"; // 15
	protected static final String COL_DIGEST = "Digest"; // 16
	protected static final String COL_UUID = "Uuid"; // 17
	protected static final String COL_BOX_ID = "Box_ID";	// 18
    protected static final String COL_COMMENTS = "Comments";	// 19
    protected static final String COL_ENCODED = "Encoded";	// 20 // added in version 9.0.0
	// database creation sql statement
	private static final String PATIENT_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_REPORTS + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_PREFIX_PID + " integer, " +
	/* 2 */COL_PID + " integer, " +
	/* 3 */COL_FPID + " text, " +
	/* 4 */COL_LN + " text, " +
	/* 5 */COL_FN + " text, " +
	/* 6 */COL_AGE + " integer, " +
	/* 7 */COL_GEN + " integer, " +
	/* 8 */COL_ZONE + " integer, " +
	/* 9 */COL_EVENT + " text, " +
	/* 10 */COL_HOSPITAL + " text, " +
	/* 11 */COL_DATE + " integer, " +
	/* 12 */COL_ERROR + " integer, " +
	/* 13 */COL_URI + " text, " +
	/* 14 */COL_CAPTION + " text, " +
	/* 15 */COL_SIZE + " text, " +
	/* 16 */COL_DIGEST + " text, " +
	/* 17 */COL_UUID + " text, " +
	/* 18 */COL_BOX_ID + " text, " +
	/* 19 */COL_COMMENTS + " text, " +
	/* 20 */COL_ENCODED + " text);";

	// Event Table
	protected static final String TABLE_EVENTS = "Events";
	// COL_ID
	protected static final String COL_INCI_ID = "Incident_ID";
	protected static final String COL_PARENT_ID = "Parent_ID";
	protected static final String COL_NAME = "Name";
	protected static final String COL_SNAME = "Short_Name";
	protected static final String COL_EVENT_DATE = "Date";
	protected static final String COL_TYPE = "Type";
	protected static final String COL_LAT = "Latitude";
	protected static final String COL_LONG = "Longetude";
	protected static final String COL_STREET = "Street";
	protected static final String COL_GROUP = "Groups";
	protected static final String COL_CLOSED = "Closed";

	private static final String EVENT_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_EVENTS + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_INCI_ID + " integer, " +
	/* 2 */COL_PARENT_ID + " integer, " +
	/* 3 */COL_NAME + " text, " +
	/* 4 */COL_SNAME + " text, " +
	/* 5 */COL_EVENT_DATE + " text, " +
	/* 6 */COL_TYPE + " text, " +
	/* 7 */COL_LAT + " integer, " +
	/* 8 */COL_LONG + " integer, " +
	/* 9 */COL_STREET + " text, " +
	/* 10 */COL_GROUP + " text, " +
	/* 11 */COL_CLOSED + " integer);";

	// Hospital Table
	protected static final String TABLE_HOSPITALS = "Hospitals";
	// COL_ID
	// COL_UUID
	// COL_NAME
	// COL_SNAME
	protected static final String COL_STREET1 = "Street_1";
	protected static final String COL_STREET2 = "Street_2";
	protected static final String COL_CITY = "City";
	protected static final String COL_COUNTY = "County";
	protected static final String COL_STATE = "State";
	protected static final String COL_COUNTRY = "Country";
	protected static final String COL_ZIP = "Zip";
	protected static final String COL_PHONE = "Phone";
	protected static final String COL_FAX = "Fax";
	protected static final String COL_EMAIL = "Email";
	protected static final String COL_WWW = "WWW";
	protected static final String COL_NPI = "NPI";
	// COL_LAT
	// COL_LONG
	protected static final String COL_PIDPREFIX = "PID_Prefix";
	protected static final String COL_PIDSUFVAR = "PID_Suffix_Var";
	protected static final String COL_PIDSUFLENG = "PID_Suffix_length";
	protected static final String COL_PHOTOREQ = "Photo_Req";
	protected static final String COL_HONOR_PHOTOREQ = "Honor_Photo_Req";
	protected static final String COL_PHTGRAPHER = "Photographer";

	protected static final String HOSPITAL_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_HOSPITALS + " (" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_UUID + " integer, " +
	/* 2 */COL_NAME + " text, " +
	/* 3 */COL_SNAME + " text, " +
	/* 4 */COL_STREET1 + " text, " +
	/* 5 */COL_STREET2 + " text, " +
	/* 6 */COL_CITY + " text, " +
	/* 7 */COL_COUNTY + " text, " +
	/* 8 */COL_STATE + " text, " +
	/* 9 */COL_COUNTRY + " text, " +
	/* 10 */COL_ZIP + " text, " +
	/* 11 */COL_PHONE + " text, " +
	/* 12 */COL_FAX + " text, " +
	/* 13 */COL_EMAIL + " text, " +
	/* 14 */COL_WWW + " text, " +
	/* 15 */COL_NPI + " text, " +
	/* 16 */COL_LAT + " text, " +
	/* 17 */COL_LONG + " text, " +
	/* 18 */COL_PIDPREFIX + " text, " +
	/* 19 */COL_PIDSUFVAR + " text,  " +
	/* 20 */COL_PIDSUFLENG + " integer, " +
	/* 21 */COL_PHOTOREQ + "  text, " +
	/* 22 */COL_HONOR_PHOTOREQ + " text, " +
	/* 23 */COL_PHTGRAPHER + " text);";
	
	// Image Table
	protected static final String TABLE_IMAGES = "Images";
//	protected static final String COL_ID = "ID"; // 0
//	protected static final String COL_PID = "Patient_ID"; //1 
	protected static final String COL_SQUENCE = "Squence";	// 2
	protected static final String COL_FACE_DETECTED = "FaceDetected";	// 3
	protected static final String COL_RECT_X = "Rect_X";	// 4
	protected static final String COL_RECT_Y = "Rect_Y";	// 5
	protected static final String COL_RECT_H = "Rect_H";	// 6
	protected static final String COL_RECT_W = "Rect_W";	// 7
//	protected static final String COL_URI = "Uri"; 	// 8
//	protected static final String COL_CAPTION = "Caption"; 	// 9
//	protected static final String COL_SIZE = "Size"; 	// 10
//	protected static final String COL_DIGEST = "Digest"; 	// 11
//	protected static final String COL_ENCODED = "Encoded"; 	// 12 // 9.0.0

	private static final String IMAGE_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_IMAGES + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_PID + " integer, " +
	/* 2 */COL_SQUENCE + " integer, " +
	/* 3 */COL_FACE_DETECTED + " integer, " +
	/* 4 */COL_RECT_X + " integer, " +
	/* 5 */COL_RECT_Y + " integer, " +
	/* 6 */COL_RECT_H + " integer, " +
	/* 7 */COL_RECT_W + " integer, " +
	/* 8 */COL_URI + " text, " +
	/* 9 */COL_CAPTION + " text, " +
	/* 10 */COL_SIZE + " text, " +
	/* 11 */COL_DIGEST + " text, " +
	/* 12 */COL_ENCODED + " text);";

	// Web Server Table
	protected static final String TABLE_WEBSERVER = "WebServer";
//	protected static final String COL_ID = "ID"; // 0
//	protected static final String COL_NAME = "Name"; //1 
	protected static final String COL_SHORT_NAME = "ShortName";	// 2
	protected static final String COL_WEB_SERVICE = "WebService";	// 3
	protected static final String COL_NAMESPACE = "NameSpace";	// 4
	protected static final String COL_URL = "url"; 	// 5

	private static final String WEBSERVER_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_WEBSERVER + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_NAME + " text, " +
	/* 2 */COL_SHORT_NAME + " text, " +
	/* 3 */COL_WEB_SERVICE + " text, " +
	/* 4 */COL_NAMESPACE + " text, " +
	/* 5 */COL_URL + " text);";

	// Authentication
	protected static final String TABLE_AUTHENTICATION = "Authentication";
//	protected static final String COL_ID = "ID"; // 0
	protected static final String COL_USERNAME = "Username"; //1 
	protected static final String COL_PASSWORD = "Password";	// 2
    protected static final String COL_TOKEN = "Token";	// 3
    protected static final String COL_WEB_SERVER_ID = "WebServerId";	// 4
    protected static final String COL_HOSPITAL_ID = "HospitalId";	// 5
    protected static final String COL_EVENT_ID = "EventId";	// 6

	private static final String AUTHENTICATION_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_AUTHENTICATION + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_USERNAME + " text, " +
	/* 2 */COL_PASSWORD + " text, " +
	/* 3 */COL_TOKEN + " text, " +
	/* 4 */COL_WEB_SERVER_ID + " integer, " +
	/* 5 */COL_HOSPITAL_ID + " integer, " +
	/* 6 */COL_EVENT_ID + " integer);";

    // filters
    protected static final String TABLE_FILTERS = "Filters";
//	protected static final String COL_ID = "ID"; // 0
    protected static final String COL_SEARCH_MALE = "SearchMale"; //1
    protected static final String COL_SEARCH_FEMALE = "SearchFemale"; //2
    protected static final String COL_SEARCH_COMPLEX = "SearchComplex"; //3
    protected static final String COL_SEARCH_UNKNOWN_GENDER = "SearchUnknownGender"; //4
    protected static final String COL_SEARCH_CHILD = "SearchChild"; //5
    protected static final String COL_SEARCH_ADULT = "SearchAdult"; //6
    protected static final String COL_SEARCH_UNKNOWN_AGE = "SearchUnknownAge"; //7
    protected static final String COL_SEARCH_GREEN_ZONE = "SearchGreenZone"; //8
    protected static final String COL_SEARCH_BH_GREEN_ZONE = "SearchBhGreenZone"; //9
    protected static final String COL_SEARCH_YELLOW_ZONE = "SearchYellowZone"; //10
    protected static final String COL_SEARCH_RED_ZONE = "SearchRedZone"; //11
    protected static final String COL_SEARCH_GRAY_ZONE = "SearchGrayZone"; //12
    protected static final String COL_SEARCH_BLACK_ZONE = "SearchBlackZone"; //13
    protected static final String COL_SEARCH_UNASSIGNED_ZONE = "SearchUnassignedZone"; //14

    private static final String FILTERS_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_FILTERS + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_SEARCH_MALE + " integer, " +
	/* 2 */COL_SEARCH_FEMALE + " integer, " +
	/* 3 */COL_SEARCH_COMPLEX + " integer, " +
	/* 4 */COL_SEARCH_UNKNOWN_GENDER + " integer, " +
	/* 5 */COL_SEARCH_CHILD + " integer, " +
	/* 6 */COL_SEARCH_ADULT + " integer, " +
	/* 7 */COL_SEARCH_UNKNOWN_AGE + " integer, " +
	/* 8 */COL_SEARCH_GREEN_ZONE + " integer, " +
	/* 9 */COL_SEARCH_BH_GREEN_ZONE + " integer, " +
	/*10 */COL_SEARCH_YELLOW_ZONE + " integer, " +
	/*11 */COL_SEARCH_RED_ZONE + " integer, " +
	/*12 */COL_SEARCH_GRAY_ZONE + " integer, " +
	/*13 */COL_SEARCH_BLACK_ZONE + " integer, " +
	/*14 */COL_SEARCH_UNASSIGNED_ZONE + " integer);";

    // view settings
    protected static final String TABLE_VIEW_SETTINGS = "ViewSettings";
    //	protected static final String COL_ID = "ID"; // 0
    protected static final String COL_VIEW_PHOTO = "ViewPhoto"; //1
    protected static final String COL_PAGE_SIZE = "PageSize"; //2
    protected static final String COL_CUR_PAGE = "CurPage"; //3
    protected static final String COL_IS_IMAGE_SEARCH = "isImageSearch"; //4
    protected static final String COL_ENCODED_IMAGE = "encodedImage"; //5

    private static final String VIEW_SETTINGS_TABLE_CREATE = "create table "
	/* Indicies */+ TABLE_VIEW_SETTINGS + "(" +
	/* 0 */COL_ID + " integer primary key autoincrement, " +
	/* 1 */COL_VIEW_PHOTO + " integer, " +
	/* 2 */COL_PAGE_SIZE + " integer, " +
	/* 3 */COL_CUR_PAGE + " integer, " +
	/* 4 */COL_IS_IMAGE_SEARCH + " integer, " +
	/* 5 */COL_ENCODED_IMAGE + " text);";

    protected Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(PATIENT_TABLE_CREATE);
		db.execSQL(EVENT_TABLE_CREATE);
		db.execSQL(HOSPITAL_TABLE_CREATE);
		db.execSQL(IMAGE_TABLE_CREATE);
		db.execSQL(WEBSERVER_TABLE_CREATE);
        db.execSQL(AUTHENTICATION_TABLE_CREATE);
        db.execSQL(FILTERS_TABLE_CREATE);
        db.execSQL(VIEW_SETTINGS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOSPITALS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEBSERVER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHENTICATION);
        db.execSQL("DROP TABLE IF EXISTS " + FILTERS_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " + VIEW_SETTINGS_TABLE_CREATE);
		onCreate(db);
	}
}
