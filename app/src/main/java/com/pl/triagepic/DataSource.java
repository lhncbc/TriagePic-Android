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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DataSource {
    public static String TAG = "DataSource";

    private String seed;
    private SQLiteDatabase database;
    private Database dbHelper;
    private String[] allColumnsReports = {
            Database.COL_ID,
            Database.COL_PREFIX_PID,
            Database.COL_PID,
            Database.COL_FPID,
            Database.COL_LN,
            Database.COL_FN,
            Database.COL_AGE,
            Database.COL_GEN,
            Database.COL_ZONE,
            Database.COL_EVENT,
            Database.COL_HOSPITAL,
            Database.COL_DATE,
            Database.COL_ERROR,
            Database.COL_URI,
            Database.COL_CAPTION,
            Database.COL_SIZE,
            Database.COL_DIGEST,
            Database.COL_UUID,
            Database.COL_BOX_ID,
            Database.COL_COMMENTS,
            Database.COL_ENCODED
    };
    private String[] allColumnsEvents = {
            Database.COL_ID,
            Database.COL_INCI_ID,
            Database.COL_PARENT_ID,
            Database.COL_NAME,
            Database.COL_SNAME,
            Database.COL_EVENT_DATE,
            Database.COL_TYPE,
            Database.COL_LAT,
            Database.COL_LONG,
            Database.COL_STREET,
            Database.COL_GROUP,
            Database.COL_CLOSED
    };
    private String[] allColumnsHospitals = {
            Database.COL_ID,
            Database.COL_UUID,
            Database.COL_NAME,
            Database.COL_SNAME,
            Database.COL_STREET1,
            Database.COL_STREET2,
            Database.COL_CITY,
            Database.COL_COUNTY,
            Database.COL_STATE,
            Database.COL_COUNTRY,
            Database.COL_ZIP,
            Database.COL_PHONE,
            Database.COL_FAX,
            Database.COL_EMAIL,
            Database.COL_WWW,
            Database.COL_NPI,
            Database.COL_LAT,
            Database.COL_LONG,
            Database.COL_PIDPREFIX,
            Database.COL_PIDSUFVAR,
            Database.COL_PIDSUFLENG,
            Database.COL_PHOTOREQ,
            Database.COL_HONOR_PHOTOREQ,
            Database.COL_PHTGRAPHER
    };

    private String[] allColumnsImages = {
            Database.COL_ID,
            Database.COL_PID,
            Database.COL_SQUENCE,
            Database.COL_FACE_DETECTED,
            Database.COL_RECT_X,
            Database.COL_RECT_Y,
            Database.COL_RECT_H,
            Database.COL_RECT_W,
            Database.COL_URI,
            Database.COL_CAPTION,
            Database.COL_SIZE,
            Database.COL_DIGEST,
            Database.COL_ENCODED
    };

    private String[] allColumnsWebServers = {
            Database.COL_ID,
            Database.COL_NAME,
            Database.COL_SHORT_NAME,
            Database.COL_WEB_SERVICE,
            Database.COL_NAMESPACE,
            Database.COL_URL,
    };

    private String[] allColumnsAuthentication = {
            Database.COL_ID,
            Database.COL_USERNAME,
            Database.COL_PASSWORD,
            Database.COL_TOKEN,
            Database.COL_WEB_SERVER_ID,
            Database.COL_HOSPITAL_ID,
            Database.COL_EVENT_ID
    };

    private String[] allColumnsFilters = {
            Database.COL_ID,
            Database.COL_SEARCH_MALE,
            Database.COL_SEARCH_FEMALE,
            Database.COL_SEARCH_COMPLEX,
            Database.COL_SEARCH_UNKNOWN_GENDER,
            Database.COL_SEARCH_CHILD,
            Database.COL_SEARCH_ADULT,
            Database.COL_SEARCH_UNKNOWN_AGE,
            Database.COL_SEARCH_GREEN_ZONE,
            Database.COL_SEARCH_BH_GREEN_ZONE,
            Database.COL_SEARCH_YELLOW_ZONE,
            Database.COL_SEARCH_RED_ZONE,
            Database.COL_SEARCH_GRAY_ZONE,
            Database.COL_SEARCH_BLACK_ZONE,
            Database.COL_SEARCH_UNASSIGNED_ZONE
    };

    private String[] allColumnsViewSettings = {
            Database.COL_ID,
            Database.COL_VIEW_PHOTO,
            Database.COL_PAGE_SIZE,
            Database.COL_CUR_PAGE,
            Database.COL_IS_IMAGE_SEARCH,
            Database.COL_ENCODED_IMAGE
    };

    public DataSource(Context context, String seed) {
        dbHelper = new Database(context);
        this.seed = seed;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * This method takes a patient and stores its fields in the Android SQLite
     * database and then returns a patient object updated with its table row id
     * included
     *
     * @param p the patient to be stored
     * @return the record patient record
     */

    public Patient createPatientPlusTriageTrak(Patient p) throws ParseException {

        Log.i(TAG, p.getLastName() + ", " + p.getFirstName());

        if (true) {
            return createPatientPlus(p);
        }

        ContentValues values = new ContentValues();
        values.put(Database.COL_PREFIX_PID, p.prefixPid);
        values.put(Database.COL_PID, p.patientId);
        values.put(Database.COL_FPID, p.fpid);
        /*
		values.put(Database.COL_LN, triagePicEncrypt(p.lastName));
		values.put(Database.COL_FN, triagePicEncrypt(p.firstName));
		*/
        values.put(Database.COL_LN, p.lastName);
        values.put(Database.COL_FN, p.firstName);

        values.put(Database.COL_AGE, p.age.i);
        values.put(Database.COL_GEN, p.gender.i);
        values.put(Database.COL_ZONE, p.myZone.i);
        /*
		values.put(Database.COL_EVENT, triagePicEncrypt(p.event));
		values.put(Database.COL_HOSPITAL, triagePicEncrypt(p.hospital));
		*/
        values.put(Database.COL_EVENT, p.event);
        values.put(Database.COL_HOSPITAL, p.hospital);

        /**
         * The date format is ISO 8601. Since the existed class SimpleDateFormat does not do well to parse the string. I need to write it on my own.
         * example: "2015-04-14-T11:08:59-04:00"
         */
        if (p.getLastUpdate() != null && !p.getLastUpdate().isEmpty()) {
            String year = p.getLastUpdate().substring(0, 4);
            String month = p.getLastUpdate().substring(5, 7);
            String day = p.getLastUpdate().substring(8, 10);
            String hour = p.getLastUpdate().substring(11, 13);
            String minute = p.getLastUpdate().substring(14, 16);
            String second = p.getLastUpdate().substring(17, 19);

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            cal.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
            p.date = cal.getTime().getTime();
        }
        else {
            p.date = 0;
        }

        values.put(Database.COL_DATE, p.date);

        values.put(Database.COL_ERROR, p.error);
        values.put(Database.COL_UUID, p.uuid);
        values.put(Database.COL_BOX_ID, p.boxId);
        values.put(Database.COL_COMMENTS, p.comments);

        String strUri = Image.setUri(p.images);
        String strCap = Image.setCaptions(p.images);
        String strSize = Image.setSizes(p.images);
        String strDigest = Image.setDigests(p.images);
        String strEncoded = Image.setEncodeds(p.images);

        Log.i(TAG, "Uri = " + strUri);
        Log.i(TAG, "Cap = " + strCap);
        Log.i(TAG, "Size = " + strSize);
        Log.i(TAG, "Digest = " + strDigest);
        Log.i(TAG, "Encoded = " + strEncoded);

        values.put(Database.COL_URI, Image.setUri(p.images));
        values.put(Database.COL_CAPTION, Image.setCaptions(p.images));
        values.put(Database.COL_SIZE, Image.setSizes(p.images));
        values.put(Database.COL_DIGEST, Image.setDigests(p.images));
        values.put(Database.COL_ENCODED, Image.setEncodeds(p.images)); // added in version 9.0.0

        long insertId = database.insert(Database.TABLE_REPORTS, null, values);
        if (insertId <= 0) {
            return null;
        }

        p.rowIndex = insertId;

        return p;
    }

    public Patient createPatientPlus(Patient p) {
        Log.i(TAG, p.getLastName() + ", " + p.getFirstName());

        ContentValues values = new ContentValues();
        values.put(Database.COL_PREFIX_PID, p.prefixPid);
        values.put(Database.COL_PID, p.patientId);
        values.put(Database.COL_FPID, p.fpid);
        /*
		values.put(Database.COL_LN, triagePicEncrypt(p.lastName));
		values.put(Database.COL_FN, triagePicEncrypt(p.firstName));
		*/
        values.put(Database.COL_LN, p.getLastName());
        values.put(Database.COL_FN, p.getFirstName());

        values.put(Database.COL_AGE, p.age.i);
        values.put(Database.COL_GEN, p.gender.i);
        values.put(Database.COL_ZONE, p.myZone.i);
        /*
		values.put(Database.COL_EVENT, triagePicEncrypt(p.event));
		values.put(Database.COL_HOSPITAL, triagePicEncrypt(p.hospital));
		*/
        values.put(Database.COL_EVENT, p.event);
        values.put(Database.COL_HOSPITAL, p.hospital);

        values.put(Database.COL_DATE, p.date);
        values.put(Database.COL_ERROR, p.error);

        String strUri = Image.setUri(p.images);
        String strCap = Image.setCaptions(p.images);
        String strSize = Image.setSizes(p.images);
        String strDigest = Image.setDigests(p.images);
        String strEncoded = Image.setEncodeds(p.images);

        Log.i(TAG, "Uri = " + strUri);
        Log.i(TAG, "Cap = " + strCap);
        Log.i(TAG, "Size = " + strSize);
        Log.i(TAG, "Digest = " + strDigest);
        Log.i(TAG, "Encoded = " + strEncoded);

        values.put(Database.COL_URI, Image.setUri(p.images));
        values.put(Database.COL_CAPTION, Image.setCaptions(p.images));
        values.put(Database.COL_SIZE, Image.setSizes(p.images));
        values.put(Database.COL_DIGEST, Image.setDigests(p.images));
        values.put(Database.COL_UUID, p.uuid);
        values.put(Database.COL_BOX_ID, p.boxId);
        /*
		values.put(Database.COL_COMMENTS, triagePicEncrypt(p.comments));
		*/
        values.put(Database.COL_COMMENTS, p.comments);
        values.put(Database.COL_ENCODED, Image.setEncodeds(p.images)); // added in version 9.0.0
        /*
        if (p.images.size() == 0){
            values.put(Database.COL_ENCODED, ""); // added in version 9.0.0
        }
        */
        /*
        else {
            Image img = p.images.get(0);
            ArrayList<Image> imgList = new ArrayList<>();
            imgList.add(img);
            values.put(Database.COL_ENCODED, Image.setEncodeds(imgList)); // added in version 9.0.0
        }
        */

        long insertId = database.insert(Database.TABLE_REPORTS, null, values);
        if (insertId <= 0){
            return null;
        }

        p.rowIndex = insertId;

        return p;
    }

    /**
     * This takes a patient's row id, looks it up in the table and then removes
     * said patient from the table.
     *
     * @param id the row id of the patient to be removed
     */
    public void deletePatient(Long id) {
        database.delete(Database.TABLE_REPORTS, Database.COL_ID + " = " + id,
                null);
    }

    public void deleteAllPatients() {
        database.delete(Database.TABLE_REPORTS, null, null);
    }

    public void deleteAllPatientsBoxType(String boxType){
        database.delete(Database.TABLE_REPORTS, Database.COL_BOX_ID + " = '" + boxType + "'", null);
    }

    /**
     * takes a given patient, looks them up by the patient's row index and
     * updates that row with this patient's values
     *
     * @param p the patient to update
     */
    public void updatePatient(Patient p) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_PREFIX_PID, p.prefixPid);
        values.put(Database.COL_PID, p.patientId);
        values.put(Database.COL_FPID, p.fpid);
        /*
        values.put(Database.COL_LN, triagePicEncrypt(p.lastName));
        values.put(Database.COL_FN, triagePicEncrypt(p.firstName));
        */
        values.put(Database.COL_LN, p.lastName);
        values.put(Database.COL_FN, p.firstName);

        values.put(Database.COL_AGE, p.age.i);
        values.put(Database.COL_GEN, p.gender.i);
        values.put(Database.COL_ZONE, p.myZone.i);
        /*
		values.put(Database.COL_EVENT, triagePicEncrypt(p.event));
		values.put(Database.COL_HOSPITAL, triagePicEncrypt(p.hospital));
		*/
        values.put(Database.COL_EVENT, p.event);
        values.put(Database.COL_HOSPITAL, p.hospital);

        values.put(Database.COL_DATE, p.date);
        values.put(Database.COL_ERROR, p.error);
        values.put(Database.COL_URI, Image.setUri(p.images));
        values.put(Database.COL_CAPTION, Image.setCaptions(p.images));
        values.put(Database.COL_SIZE, Image.setSizes(p.images));
        values.put(Database.COL_DIGEST, Image.setDigests(p.images));
        values.put(Database.COL_UUID, p.uuid);
        values.put(Database.COL_BOX_ID, p.boxId);
        /*
		values.put(Database.COL_COMMENTS, triagePicEncrypt(p.comments));
		*/
        values.put(Database.COL_COMMENTS, p.comments);
        values.put(Database.COL_ENCODED, Image.setEncodeds(p.images)); // added in version 9.0.0

        database.update(Database.TABLE_REPORTS, values, Database.COL_ID + " = "
                + p.rowIndex, null);
    }

    public void updatePatientID(long id) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_ID, id);
        database.update(Database.TABLE_REPORTS, values, Database.COL_ID + " = "
                + id, null);
    }

    public void updatePatientBox(long id, String boxId) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_BOX_ID, boxId);
        database.update(Database.TABLE_REPORTS, values, Database.COL_ID + " = "
                + id, null);
    }

    /**
     * @return ArrayList of all patients in the database
     */
    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<Patient>();

        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient patient = new Patient();
            patient = new Patient();
            patient = cursorToPatient(cursor);
            patients.add(patient);
            cursor.moveToNext();
        }
        return patients;
    }

    public long getLastId() {
        Cursor cursor = database.query(Database.TABLE_REPORTS, allColumnsReports, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            return 0;
        }
        cursor.moveToLast();
        if (cursor.isNull(0) == true) {
            return 0;
        }
        Patient patient = new Patient();
        patient = cursorToPatient(cursor);
        return patient.getPid();
    }

    public ArrayList<Patient> getAllPatientsDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, null, null, null, null, Database.COL_ID + " desc");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                deletePatient(p.patientId);

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    public ArrayList<Patient> verifyAllPatientsDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, null, null, null, null, Database.COL_ID + " desc");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                deletePatient(p.patientId);

                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    public ArrayList<Patient> getAllPatientsSentDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(
                Database.TABLE_REPORTS,
                allColumnsReports,
                Database.COL_BOX_ID + " = '" + Patient.SENT + "'",
                null,
                null,
                null,
                Database.COL_ID + " desc"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                p = null;

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    public ArrayList<Patient> getAllPatientsDraftsDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(
                Database.TABLE_REPORTS,
                allColumnsReports,
                Database.COL_BOX_ID + " = '" + Patient.DRAFTS + "'",
                null,
                null,
                null,
                Database.COL_ID + " desc"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                p = null;

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    public ArrayList<Patient> getAllPatientsDeletedDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(
                Database.TABLE_REPORTS,
                allColumnsReports,
                Database.COL_BOX_ID + " = '" + Patient.DELETED + "'",
                null,
                null,
                null,
                Database.COL_ID + " desc"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                p = null;

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    // added in version 9.0.0 for search
    public ArrayList<Patient> getAllPatientsTriageTrakDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(
                Database.TABLE_REPORTS,
                allColumnsReports,
                Database.COL_BOX_ID + " = '" + Patient.TRIAGETRAK + "'",
                null,
                null,
                null,
//                Database.COL_ID + " desc"
                Database.COL_ID + " asc"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                p = null;
                Log.d(TAG, "Exception Error in getAllPatientsTriageTrakDesc(): " + e.getMessage());

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    public ArrayList<Patient> getAllPatientsOutboxDesc() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(
                Database.TABLE_REPORTS,
                allColumnsReports,
                Database.COL_BOX_ID + " = '" + Patient.OUTBOX + "'",
                null,
                null,
                null,
                Database.COL_ID + " desc"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Patient p = new Patient();
            // need exception handing here. added in version 9.0.0
            try {
                p = cursorToPatient(cursor);
            } catch (Exception e) {
                p = null;

                // Bad file. Need to continue. changed in version 9.0.2
                cursor.moveToNext();
                continue;
            }
            patients.add(p);
            cursor.moveToNext();
        }
        return patients;
    }

    /**
     * Looks up a given patient in the database
     *
     * @param rowId the index of the patient requested
     * @return the patient requested
     */
    public Patient getPatient(long rowId) {
        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getLong(0) == rowId) {
                Patient p = new Patient();
                p = cursorToPatient(cursor);
                return p;
            }
            cursor.moveToNext();
        }
        return null;
    }

    public Patient getPatientById(long id) {
        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, Database.COL_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Patient p = new Patient();
        p = cursorToPatient(cursor);
        cursor.close();
        return p;
    }

    /**
     * Retrieves a patient from a given row
     *
     * @param cursor cursor at row to create patient
     * @return the created patient
     */
    private Patient cursorToPatient(Cursor cursor) {
        Image img = new Image();
        img.setImages(
                cursor.getString(13),
                cursor.getString(16),
                cursor.getString(20),
                cursor.getString(14),
                cursor.getString(14));

        Patient p = new Patient(
//		/* prefix */cursor.getLong(1),
		/* prefix */cursor.getString(1),
		/* pid */cursor.getLong(2),
		/* fpid */cursor.getString(3),

//		/* ln */triagePicDecrypt(cursor.getString(4)),
//		/* fn */triagePicDecrypt(cursor.getString(5)),
		/* ln */cursor.getString(4),
		/* fn */cursor.getString(5),

                // zl
                // start
//		/* Age */Age.getAge(cursor.getInt(4)),
//		/* Gender */Gender.getGender(cursor.getInt(5)),
//		/* Zone */Zone.getZone(cursor.getInt(6)),
		/* Age */Patient.Age.getAge(cursor.getInt(6)),
		/* Gender */Patient.Gender.getGender(cursor.getInt(7)),
//		/* Zone */Patient.Zone.getZone(cursor.getInt(6)),
		/* Zone */Patient.MyZone.getZone(cursor.getInt(8)),
                // end

//		/* event */triagePicDecrypt(cursor.getString(9)),
//		/* hospital */triagePicDecrypt(cursor.getString(10)),
		/* event */cursor.getString(9),
		/* hospital */cursor.getString(10),

		/* date */cursor.getLong(11),
		/* error */cursor.getInt(12),

        // 9.0.0
		// Images
        Image.setImages(
                cursor.getString(13),
                cursor.getString(16),
                cursor.getString(20),
                cursor.getString(14),
                cursor.getString(14)
        )
        ); // 9.0.0

        Log.i(TAG, p.getLastName() + ", " + p.getFirstName() + " " + "*13*" + cursor.getString(13) + ";" + "*16*" + cursor.getString(16) + ";" + "*20*" + cursor.getString(20) + ";" + "*14*" + cursor.getString(14));

        p.rowIndex = cursor.getLong(0);
        p.uuid = cursor.getString(17);
        p.boxId = cursor.getString(18);

//        p.comments = triagePicDecrypt(cursor.getString(19));
        p.comments = cursor.getString(19);

        p.report = true;

        /*
        Took out. It is rather slower. Not all the images need to be generated.
        // added in version 9.0.0
        // The bitmap is not stored in database. Generate it here now.
        for (int i = 0; i < p.images.size(); i++){
            Image img = p.images.get(i);
            String encoded = img.getEncoded();
            if (!encoded.isEmpty()){
                Bitmap b = Patient.decodeStringToBitmap(encoded);
                img.setBitmap(b);
                p.images.set(i, img);
            }
        }
        */

        return p;
    }

    /**
     * Obtains all used Patient Ids for a given event
     *
     * @param event : the event selected
     * @return ArrayList<Long> of patient Ids
     */
    protected ArrayList<Long> getPIDByEvent(String event) {
        ArrayList<Long> result = new ArrayList<Long>();
        Cursor cursor = database.query(Database.TABLE_REPORTS,
                allColumnsReports, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(7).equals(event))
                result.add(cursor.getLong(1));
            cursor.moveToNext();
        }
        return result;
    }

    /**
     * This method takes an event and stores its fields in the Android SQLite
     * database and then returns a event object updated with its table row id
     * included
     *
     * @param e
     * @return the Event created
     */
    public Event createEvent(Event e) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_INCI_ID, e.incident_id);
        values.put(Database.COL_PARENT_ID, e.parent_id);
        values.put(Database.COL_NAME, e.name);
        values.put(Database.COL_SNAME, e.shortname);
        values.put(Database.COL_EVENT_DATE, e.date);
        values.put(Database.COL_TYPE, e.type);
        values.put(Database.COL_LAT, e.latitude);
        values.put(Database.COL_LONG, e.longitude);
        values.put(Database.COL_STREET, e.street);
        values.put(Database.COL_GROUP, e.group);
        values.put(Database.COL_CLOSED, e.closed);
        long insertId = database.insert(Database.TABLE_EVENTS, null, values);
        e.rowIndex = insertId;
        Cursor cursor = database.query(Database.TABLE_EVENTS, allColumnsEvents,
                Database.COL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Event newEvent = cursorToEvent(cursor);
        cursor.close();
        return newEvent;
    }

    /**
     * Obtains all the Events in the Database
     *
     * @return
     */
    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<Event>();

        Cursor cursor = database.query(Database.TABLE_EVENTS, allColumnsEvents,
                null, null, null, null, Database.COL_EVENT_DATE + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event event = cursorToEvent(cursor);
            events.add(event);
            cursor.moveToNext();
        }
        return events;
    }

    /**
     * returns an event based on where the cursor currently is located.
     *
     * @param cursor
     * @return
     */
    private Event cursorToEvent(Cursor cursor) {
        Event e = new Event(cursor.getInt(1), cursor.getInt(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getFloat(7), cursor.getFloat(8),
                cursor.getString(9), cursor.getString(10), cursor.getInt(11));
        e.rowIndex = cursor.getLong(0);
        return e;
    }

    /**
     * returns the Names of all events in the database
     *
     * @return
     */
    public ArrayList<String> getEventsList() {
        ArrayList<String> events = new ArrayList<String>();
        Cursor cursor = database.query(Database.TABLE_EVENTS, allColumnsEvents,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            events.add(cursor.getString(3));
            cursor.moveToNext();
        }

        // put default first
        // start
        int defaultPos = -1;
        for (int i = 0; i < events.size(); i++) {
            String str = events.get(i);
            if (str.compareToIgnoreCase(Event.DEFAULT_EVENT_NAME) == 0) {
                defaultPos = i;
                break;
            }
        }
        if (defaultPos != -1) {
            events.remove(defaultPos);
            events.add(0, Event.DEFAULT_EVENT_NAME);
        }
        // end

        return events;
    }

    /**
     * obtain a specific event based on its name
     *
     * @param s
     * @return
     */
    public Event getEvent(String s) {
        Event e = null;
        if (s != null) {
            Cursor cursor = database.query(Database.TABLE_EVENTS,
                    allColumnsEvents, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (s.equals(cursor.getString(3))) {
                    e = cursorToEvent(cursor);
                    break;
                }
                cursor.moveToNext();
            }
        }
        return e;
    }

    /**
     * empties the event table
     */
    public void clearEvents() {
        ArrayList<Event> events = getAllEvents();
        for (Event e : events)
            database.delete(Database.TABLE_EVENTS, Database.COL_ID + " = "
                    + e.rowIndex, null);
    }

    public void deleteAllEvents() {
        database.delete(Database.TABLE_EVENTS, null, null);
    }

    /**
     * This method takes a hospital and stores its fields in the Android SQLite
     * database and then returns a hospital object updated with its table row id
     * included
     *
     * @param
     * @r
     */
    public Hospital createHospital(Hospital h) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_UUID, h.uuid);
        values.put(Database.COL_NAME, h.name);
        values.put(Database.COL_SNAME, h.shortname);
        values.put(Database.COL_STREET1, h.street1);
        values.put(Database.COL_STREET2, h.street2);
        values.put(Database.COL_CITY, h.city);
        values.put(Database.COL_COUNTY, h.county);
        values.put(Database.COL_STATE, h.state);
        values.put(Database.COL_COUNTRY, h.country);
        values.put(Database.COL_ZIP, h.zip);
        values.put(Database.COL_PHONE, h.phone);
        values.put(Database.COL_FAX, h.fax);
        values.put(Database.COL_EMAIL, h.email);
        values.put(Database.COL_WWW, h.www);
        values.put(Database.COL_NPI, h.npi);
        values.put(Database.COL_LAT, h.latitude);
        values.put(Database.COL_LONG, h.longitude);
        values.put(Database.COL_PIDPREFIX, h.pidPrefix);
        values.put(Database.COL_PIDSUFVAR, h.pidSuffixVariable);
        values.put(Database.COL_PIDSUFLENG, h.pidSuffixFixedLength);
        values.put(Database.COL_PHOTOREQ, h.photoRequired);
        values.put(Database.COL_HONOR_PHOTOREQ, h.honorNoPhotoRequset);
        values.put(Database.COL_PHTGRAPHER, h.photographerNameRequired);
        long insertId = database.insert(Database.TABLE_HOSPITALS, null, values);
        h.rowIndex = insertId;
        Cursor cursor = database.query(Database.TABLE_HOSPITALS,
                allColumnsHospitals, Database.COL_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Hospital newHospital = cursorToHospital(cursor);
        cursor.close();
        return newHospital;
    }

    /**
     * obtains all hospitals in the database
     *
     * @return
     */
    public ArrayList<Hospital> getAllHospitals() {
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();

        Cursor cursor = database.query(Database.TABLE_HOSPITALS,
                allColumnsHospitals, null, null, null, null, Database.COL_NAME + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Hospital hospital = cursorToHospital(cursor);
            if (hospital.name.compareToIgnoreCase("enter name here") != 0) {
                hospitals.add(hospital);
            }
            cursor.moveToNext();
        }
        return hospitals;
    }

    /**
     * obtains the hospital at the give cursor row in the database
     *
     * @param cursor
     * @return
     */

    private Hospital cursorToHospital(Cursor cursor) {
        Hospital h = new Hospital(cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13),
                cursor.getString(14),
                cursor.getString(15),
                cursor.getString(16),
                cursor.getString(17),
                cursor.getString(18),
                cursor.getString(19),
                cursor.getInt(20),
                cursor.getString(21),
                cursor.getString(22),
                cursor.getString(23));

        h.rowIndex = cursor.getLong(0);
        return h;
    }

    /**
     * obtains all hospital names in the database
     *
     * @return
     */
    protected ArrayList<String> getHospitalList() {
        ArrayList<String> hospitals = new ArrayList<String>();
        Cursor cursor = database.query(Database.TABLE_HOSPITALS,
                allColumnsHospitals, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String str = cursor.getString(2);
            if (str.equalsIgnoreCase("enter name here") == false) {
                hospitals.add(str);
            }
            cursor.moveToNext();
        }

        // put default first
        // start
        int defaultPos = -1;
        for (int i = 0; i < hospitals.size(); i++) {
            String str = hospitals.get(i);
            if (str.compareToIgnoreCase(Hospital.DEFAULT_NAME) == 0) {
                defaultPos = i;
                break;
            }
        }
        if (defaultPos != -1) {
            hospitals.remove(defaultPos);
            hospitals.add(0, Hospital.DEFAULT_NAME);
        }
        // end

        return hospitals;

    }

    /**
     * obtain a specific hospital based on its name
     *
     * @param s
     * @return
     */
    public Hospital getHospital(String s) {
        Hospital h = null;
        if (s != null) {
            Cursor cursor = database.query(Database.TABLE_HOSPITALS,
                    allColumnsHospitals, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String str = cursor.getString(2);
                if (s.equalsIgnoreCase(str)) {
                    return cursorToHospital(cursor);
                }
                cursor.moveToNext();
            }
        }
        return h;
    }

    /**
     * remove all hospitals from database.
     */
    public void clearHospitals() {
        ArrayList<Hospital> hospitals = getAllHospitals();
        for (Hospital h : hospitals)
            database.delete(Database.TABLE_HOSPITALS, Database.COL_ID + " = "
                    + h.rowIndex, null);
    }

    public void deleteAllHospitals() {
        database.delete(Database.TABLE_HOSPITALS, null, null);
    }

	/*
	*	Image table
	*/

    public Image createImage(Image i) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_PID, i.getPid());
        values.put(Database.COL_SQUENCE, i.getSquence());
        values.put(Database.COL_FACE_DETECTED, i.getNumberOfFacesDetected());
        values.put(Database.COL_RECT_X, i.getRect().getX());
        values.put(Database.COL_RECT_Y, i.getRect().getY());
        values.put(Database.COL_RECT_H, i.getRect().getH());
        values.put(Database.COL_RECT_W, i.getRect().getW());
        values.put(Database.COL_URI, i.getUri());
        values.put(Database.COL_CAPTION, i.getCaption());
        values.put(Database.COL_SIZE, i.getSize());
        values.put(Database.COL_DIGEST, i.getDigest());
        values.put(Database.COL_ENCODED, i.getEncoded()); // 9.0.0
        long insertId = database.insert(Database.TABLE_IMAGES, null, values);
        i.setId(insertId);
        Cursor cursor = database.query(Database.TABLE_IMAGES,
                allColumnsImages, Database.COL_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Image newImage = cursorToImage(cursor);
        cursor.close();
        return newImage;
    }

    private Image cursorToImage(Cursor cursor) {
        Image i = new Image(
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12)); // 9.0.0
        i.setId(cursor.getLong(0));
        return i;
    }

    public ArrayList<Image> getAllImages(long pid) {
        ArrayList<Image> list = new ArrayList<Image>();

        Cursor cursor = database.query(Database.TABLE_IMAGES,
                allColumnsImages, Database.COL_PID + "=" + String.valueOf(pid), null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Image img = cursorToImage(cursor);
            list.add(img);
            cursor.moveToNext();
        }

        return list;
    }

    public Image getImage(long pid, int squence) {
        Image i = null;
        Cursor cursor = database.query(Database.TABLE_IMAGES,
                allColumnsImages, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int n1 = cursor.getInt(1);
            int n2 = cursor.getInt(2);
            if (pid == n1 && squence == n2) {
                return cursorToImage(cursor);
            }
            cursor.moveToNext();
        }
        return i;
    }

    public void deleteImageById(long id) {
        database.delete(Database.TABLE_IMAGES, Database.COL_ID + " = "
                + id, null);
    }

    public void deleteImageByPid(long pid) {
        database.delete(Database.TABLE_IMAGES, Database.COL_PID + " = "
                + pid, null);
    }

    public void deleteAllImages() {
        database.delete(Database.TABLE_IMAGES, null, null);
    }
	
	/*
	*	Web Server table
	*/

    public long createWebServer(WebServer i) {
        ContentValues values = new ContentValues();
//		values.put(Database.COL_ID, i.getId());
        values.put(Database.COL_NAME, i.getName());
        values.put(Database.COL_SHORT_NAME, i.getShortName());
        values.put(Database.COL_WEB_SERVICE, i.getWebService());
        values.put(Database.COL_NAMESPACE, i.getNameSpace());
        values.put(Database.COL_URL, i.getUrl());
        long insertId = database.insert(Database.TABLE_WEBSERVER, null, values);
        return insertId;
    }

    public long createWebServer(WebServer i, Context c) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_ID, i.getId());
        values.put(Database.COL_NAME, i.getName());
        values.put(Database.COL_SHORT_NAME, i.getShortName());
        values.put(Database.COL_WEB_SERVICE, i.getWebService());
        values.put(Database.COL_NAMESPACE, i.getNameSpace());
        values.put(Database.COL_URL, i.getUrl());
        long insertId = database.insert(Database.TABLE_WEBSERVER, null, values);
        return insertId;
    }

    private WebServer cursorToWebServer(Cursor cursor) {
        WebServer i = new WebServer(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));
        i.setId(cursor.getLong(0));
        return i;
    }

    public WebServer getWebServerFromId(long id) {
        WebServer w = null;
        Cursor cursor = database.query(Database.TABLE_WEBSERVER,
                allColumnsWebServers, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int n = cursor.getInt(0);
            if (id == n) {
                w = cursorToWebServer(cursor);
                return w;
            }
            cursor.moveToNext();
        }
        return w;
    }

    public WebServer getWebServerFromName(String name) {
        WebServer w = null;
        Cursor cursor = database.query(Database.TABLE_WEBSERVER,
                allColumnsWebServers, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String str = cursor.getString(1);
            if (name.equalsIgnoreCase(str) == true) {
                w = cursorToWebServer(cursor);
                return w;
            }
            cursor.moveToNext();
        }
        return w;
    }

    public void deleteWebServerById(long id) {
        database.delete(Database.TABLE_WEBSERVER, Database.COL_ID + " = " + String.valueOf(id), null);
    }

    public void deleteWebServerByName(String name) {
        database.delete(Database.TABLE_WEBSERVER, Database.COL_NAME + " = " + name, null);
    }

    public void deleteWebServerByWebService(String webService) {
        database.delete(Database.TABLE_WEBSERVER, Database.COL_WEB_SERVICE + " = " + webService, null);
    }

    public void deleteAllWebServers() {
        database.delete(Database.TABLE_WEBSERVER, null, null);
    }

    public ArrayList<WebServer> getAllWebServers() {
        ArrayList<WebServer> WebServers = new ArrayList<WebServer>();

        Cursor cursor = database.query(Database.TABLE_WEBSERVER, allColumnsWebServers,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WebServer w = cursorToWebServer(cursor);
            WebServers.add(w);
            cursor.moveToNext();
        }
        return WebServers;
    }

	/*
	 * Authentication
	 */

    public long createAuthentication(String username, String password, String token, int webServerId, int HospitalId, int EventId) {
        ContentValues values = new ContentValues();
        /*
		values.put(Database.COL_USERNAME, triagePicEncrypt(username));
		values.put(Database.COL_PASSWORD, triagePicEncrypt(password));
		*/
        values.put(Database.COL_USERNAME, username);
        values.put(Database.COL_PASSWORD, password);

        values.put(Database.COL_TOKEN, token);
        values.put(Database.COL_WEB_SERVER_ID, webServerId);

        values.put(Database.COL_HOSPITAL_ID, HospitalId);
        values.put(Database.COL_EVENT_ID, EventId);

        long insertId = database.insert(Database.TABLE_AUTHENTICATION, null, values);
        return insertId;
    }

    public String getUsername(int id) {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, Database.COL_ID + " = " + id, null, null, null, null);
        if (cursor == null) {
//            return triagePicDecrypt(str);
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(1);
            cursor.moveToNext();
        }
//        return triagePicDecrypt(str);
        return str;
    }

    public String getPassword(int id) {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, Database.COL_ID + " = " + id, null, null, null, null);
        if (cursor == null) {
//            return triagePicDecrypt(str);
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(2);
            cursor.moveToNext();
        }
//        return triagePicDecrypt(str);
        return str;
    }

    public String getToken(int id) {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, Database.COL_ID + " = " + id, null, null, null, null);
        if (cursor == null) {
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(3);
            cursor.moveToNext();
        }
        return str;
    }

    public String getUsername() {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
//            return triagePicDecrypt(str);
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(1);
            cursor.moveToNext();
        }
//        return triagePicDecrypt(str);
        return str;
    }

    public String getPassword() {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
//            return triagePicDecrypt(str);
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(2);
            cursor.moveToNext();
        }
//        return triagePicDecrypt(str);
        return str;
    }

    public String getToken() {
        String str = "";
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
            return str;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            str = cursor.getString(3);
            cursor.moveToNext();
        }
        return str;
    }

    public long getAuthId() {
        long authId = -1;
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
            return authId;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            authId = cursor.getInt(0);
            cursor.moveToNext();
        }
        return authId;
    }

    public int getWebServerId() {
        int webServerId = -1;
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
            return webServerId;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            webServerId = cursor.getInt(4);
            cursor.moveToNext();
        }
        return webServerId;
    }

    public int getHospitalId() {
        int hospitalId = -1;
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
            return hospitalId;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            hospitalId = cursor.getInt(5);
            cursor.moveToNext();
        }
        return hospitalId;
    }

    public int getEventId() {
        int eventId = -1;
        Cursor cursor = database.query(Database.TABLE_AUTHENTICATION,
                allColumnsAuthentication, null, null, null, null, null);
        if (cursor == null) {
            return eventId;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            eventId = cursor.getInt(6);
            cursor.moveToNext();
        }
        return eventId;
    }

    public void setEventId(int eventId, int id) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_EVENT_ID, eventId);
        database.update(Database.TABLE_AUTHENTICATION, values, Database.COL_ID + " = '" + String.valueOf(id) + "'", null);
    }

    public void setHospitalId(int hospitalId, int id) {
        ContentValues values = new ContentValues();
        values.put(Database.COL_HOSPITAL_ID, hospitalId);
        database.update(Database.TABLE_AUTHENTICATION, values, Database.COL_ID + " = '" + String.valueOf(id) + "'", null);
    }

    public void updateUsernamePassword(String username, String password, String token, int webServerId, int hospitalId, int eventId, long id) {
        ContentValues values = new ContentValues();
        /*
		values.put(Database.COL_USERNAME, triagePicEncrypt(username));
        values.put(Database.COL_PASSWORD, triagePicEncrypt(password));
        */
        values.put(Database.COL_USERNAME, username);
        values.put(Database.COL_PASSWORD, password);

        values.put(Database.COL_TOKEN, token);
        values.put(Database.COL_WEB_SERVER_ID, webServerId);

        values.put(Database.COL_HOSPITAL_ID, hospitalId);
        values.put(Database.COL_EVENT_ID, eventId);

        database.update(Database.TABLE_AUTHENTICATION, values, Database.COL_ID + " = '" + String.valueOf(id) + "'", null);
    }

    public void deleteAllUsers() {
        database.delete(Database.TABLE_AUTHENTICATION, null, null);
    }

    public String triagePicEncrypt(String in) {
        String out = "";
        try {
            out = MyEncrypt.encrypt(seed, in);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.getMessage());
            out = "";
        }
        return out;
    }

    public String triagePicDecrypt(String in) {
        String out = "";
        try {
            out = MyEncrypt.decrypt(seed, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /*
	 * Filters
	 */
    public long createFilters(Filters filters) {
        ContentValues values = new ContentValues();

        values.put(Database.COL_SEARCH_MALE, filters.getMale());
        values.put(Database.COL_SEARCH_FEMALE, filters.getFemale());
        values.put(Database.COL_SEARCH_COMPLEX, filters.getComplex());
        values.put(Database.COL_SEARCH_UNKNOWN_GENDER, filters.getGenderUnknown());

        values.put(Database.COL_SEARCH_CHILD, filters.getChild());
        values.put(Database.COL_SEARCH_ADULT, filters.getAdult());
//        values.put(Database.COL_SEARCH_UNKNOWN_AGE, filters.getAgeUnknown());
        values.put(Database.COL_SEARCH_UNKNOWN_AGE, false);

        values.put(Database.COL_SEARCH_GREEN_ZONE, filters.getGreenZone());
        values.put(Database.COL_SEARCH_BH_GREEN_ZONE, filters.getBhGreenZone());
        values.put(Database.COL_SEARCH_YELLOW_ZONE, filters.getYellowZone());
        values.put(Database.COL_SEARCH_RED_ZONE, filters.getRedZone());
        values.put(Database.COL_SEARCH_GRAY_ZONE, filters.getGrayZone());
        values.put(Database.COL_SEARCH_BLACK_ZONE, filters.getBlackZone());
        values.put(Database.COL_SEARCH_UNASSIGNED_ZONE, filters.getUnassignedZone());

        long insertId = database.insert(Database.TABLE_FILTERS, null, values);
        return insertId;
    }

    public void updateFilters(int id, Filters filters) {
        ContentValues values = new ContentValues();

        values.put(Database.COL_SEARCH_MALE, filters.getMale());
        values.put(Database.COL_SEARCH_FEMALE, filters.getFemale());
        values.put(Database.COL_SEARCH_COMPLEX, filters.getComplex());
        values.put(Database.COL_SEARCH_UNKNOWN_GENDER, filters.getGenderUnknown());

        values.put(Database.COL_SEARCH_CHILD, filters.getChild());
        values.put(Database.COL_SEARCH_ADULT, filters.getAdult());
        values.put(Database.COL_SEARCH_UNKNOWN_AGE, filters.getAgeUnknown());

        values.put(Database.COL_SEARCH_GREEN_ZONE, filters.getGreenZone());
        values.put(Database.COL_SEARCH_BH_GREEN_ZONE, filters.getBhGreenZone());
        values.put(Database.COL_SEARCH_YELLOW_ZONE, filters.getYellowZone());
        values.put(Database.COL_SEARCH_RED_ZONE, filters.getRedZone());
        values.put(Database.COL_SEARCH_GRAY_ZONE, filters.getGrayZone());
        values.put(Database.COL_SEARCH_BLACK_ZONE, filters.getBlackZone());
        values.put(Database.COL_SEARCH_UNASSIGNED_ZONE, filters.getUnassignedZone());

        database.update(Database.TABLE_FILTERS, values, Database.COL_ID + " = '" + String.valueOf(id) + "'", null);
    }

    public boolean getBooleanFromCursor(int columnIndex, Cursor cursor) {
        if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
            return false;
        } else {
            return true;
        }
    }

    private Filters cursorToFilters(Cursor cursor) {
        Filters f = new Filters();

        f.setId(cursor.getInt(0));

        f.setMale(getBooleanFromCursor(1, cursor));
        f.setFemale(getBooleanFromCursor(2, cursor));
        f.setComplex(getBooleanFromCursor(3, cursor));
        f.setGenderUnknown(getBooleanFromCursor(4, cursor));

        f.setChild(getBooleanFromCursor(5, cursor));
        f.setAdult(getBooleanFromCursor(6, cursor));
        f.setAgeUnknown(getBooleanFromCursor(7, cursor));

        f.setGreenZone(getBooleanFromCursor(8, cursor));
        f.setBhGreenZone(getBooleanFromCursor(9, cursor));
        f.setYellowZone(getBooleanFromCursor(10, cursor));
        f.setRedZone(getBooleanFromCursor(11, cursor));
        f.setGrayZone(getBooleanFromCursor(12, cursor));
        f.setBlackZone(getBooleanFromCursor(13, cursor));
        f.setUnassignedZone(getBooleanFromCursor(14, cursor));

        return f;
    }

    public Filters getFilters(){
        Filters f = new Filters();
        Cursor cursor = database.query(Database.TABLE_FILTERS, allColumnsFilters,
                null, null, null, null, null);
        if (cursor == null) {
            return f;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            f = new Filters(cursorToFilters(cursor));
            cursor.moveToNext();
        }
        return f;
    }

    // delete filters
    public void deleteFilters() {
        database.delete(Database.TABLE_FILTERS, null, null);
    }

    /*
	 * View settings
	 */
    // create view settings
    public long createViewSettings(ViewSettings viewSettings) {
        ContentValues values = new ContentValues();
        if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY) {
            values.put(Database.COL_VIEW_PHOTO, ViewSettings.PHOTO_ONLY);
        }
        else {
            values.put(Database.COL_VIEW_PHOTO, ViewSettings.BOTH);
        }

        switch (viewSettings.getPageSize()) {
            case ViewSettings.PAGE_SIZE_5:
            case ViewSettings.PAGE_SIZE_10:
            case ViewSettings.PAGE_SIZE_15:
            case ViewSettings.PAGE_SIZE_20:
            case ViewSettings.PAGE_SIZE_24:
            case ViewSettings.PAGE_SIZE_25:
            case ViewSettings.PAGE_SIZE_30:
            case ViewSettings.PAGE_SIZE_35:
            case ViewSettings.PAGE_SIZE_40:
                values.put(Database.COL_PAGE_SIZE, viewSettings.getPageSize());
                break;
            default:
                values.put(Database.COL_PAGE_SIZE, ViewSettings.PAGE_SIZE_10);
                break;
        }

        values.put(Database.COL_CUR_PAGE, viewSettings.getCurPage());

        values.put(Database.COL_IS_IMAGE_SEARCH, viewSettings.getIsImageSearch());
        values.put(Database.COL_ENCODED_IMAGE, viewSettings.getEncodedImage());

        long insertId = database.insert(Database.TABLE_VIEW_SETTINGS, null, values);
        return insertId;
    }

    // update view settings
    public void updateViewSettings(int id, ViewSettings viewSettings) {
        ContentValues values = new ContentValues();
        if (viewSettings.getPhotoSel() == ViewSettings.NO_PHOTO) {
            values.put(Database.COL_VIEW_PHOTO, ViewSettings.NO_PHOTO);
        }
        else if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY) {
            values.put(Database.COL_VIEW_PHOTO, ViewSettings.PHOTO_ONLY);
        }
        else {
            values.put(Database.COL_VIEW_PHOTO, ViewSettings.BOTH);
        }

        switch (viewSettings.getPageSize()) {
            case ViewSettings.PAGE_SIZE_5:
            case ViewSettings.PAGE_SIZE_10:
            case ViewSettings.PAGE_SIZE_15:
            case ViewSettings.PAGE_SIZE_20:
            case ViewSettings.PAGE_SIZE_24:
            case ViewSettings.PAGE_SIZE_25:
            case ViewSettings.PAGE_SIZE_30:
            case ViewSettings.PAGE_SIZE_35:
            case ViewSettings.PAGE_SIZE_40:
                values.put(Database.COL_PAGE_SIZE, viewSettings.getPageSize());
                break;
            default:
                values.put(Database.COL_PAGE_SIZE, ViewSettings.PAGE_SIZE_10);
                break;
        }

        values.put(Database.COL_CUR_PAGE, viewSettings.getCurPage());

        values.put(Database.COL_IS_IMAGE_SEARCH, viewSettings.getIsImageSearch());
        values.put(Database.COL_ENCODED_IMAGE, viewSettings.getEncodedImage());

        database.update(Database.TABLE_VIEW_SETTINGS, values, Database.COL_ID + " = '" + String.valueOf(id) + "'", null);
    }

    private ViewSettings cursorToViewSettings(Cursor cursor) {
        ViewSettings vs = new ViewSettings();
        vs.SetToDefault();
        vs.setId(cursor.getInt(0));
        vs.setPhotoSel(cursor.getInt(1));
        vs.setPageSize(cursor.getInt(2));
        vs.setCurPage(cursor.getInt(3));
        vs.setIsImageSearch(cursor.getInt(4));
        vs.setEncodedImage(cursor.getString(5));
        return vs;
    }

    public ViewSettings getViewSettings(){
        ViewSettings vs = new ViewSettings();
        vs.SetToDefault();
        Cursor cursor = database.query(Database.TABLE_VIEW_SETTINGS, allColumnsViewSettings,
                null, null, null, null, null);
        if (cursor == null) {
            return vs;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            vs = cursorToViewSettings(cursor);
            cursor.moveToNext();
        }
        return vs;
    }

    // delete view settings
    public void deleteViewSettings() {
        database.delete(Database.TABLE_VIEW_SETTINGS, null, null);
    }
}
