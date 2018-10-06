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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

/**
 * This class oversees webservices that should be run in the background as a
 * queued service, inculding, send, re-send, and deletes for patient records,
 * and updates of hospital and event lists
 *
 */
public class Webservice extends IntentService {
	private SharedPreferences settings;// use to get webservice and crednetials
	protected String webservice;// target server
	private String NAMESPACE;// http or https
	private String URL;// namespace + webservice + ?wsdl
	private DataSource s;
	private Handler handler;// use to send dialog
	private Toast toast;
	
	private WebServer webServer;
	
	TriagePic app;

	public Webservice() {
		super("Webservice");
		app = (TriagePic) this.getApplication();
	}

	/**
	 * Called by the system when the service is first created. Do not call this
	 * method directly. Prepares service and sets the handler,
	 * sharedpreferences, datasource and targer server.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
		s = new DataSource(this, app.getSeed());
		settings = this.getSharedPreferences("Info", 0);
//        String temp = settings.getString("webservice", WebServer.PL_NAME);
        String temp = settings.getString("webservice", WebServer.TT_NAME);

		webServer = new WebServer();
        /*
		if (temp.compareToIgnoreCase(WebServer.PL_STAGE_NAME) == 0) {
			webServer.setName(WebServer.PL_STAGE_NAME);
			webServer.setShortName(WebServer.PL_STAGE_SHORT_NAME);
			webServer.setWebService(WebServer.PL_STAGE_WEB_SERVICE);
			webServer.setNameSpace(WebServer.PL_STAGE_NAMESPACE);
			webServer.setUrl(WebServer.PL_STAGE_URL);
		}
		else if (temp.compareToIgnoreCase(WebServer.PL_MOBILE_NAME) == 0) {
			webServer.setName(WebServer.PL_MOBILE_NAME);
			webServer.setShortName(WebServer.PL_MOBILE_SHORT_NAME);
			webServer.setWebService(WebServer.PL_MOBILE_WEB_SERVICE);
			webServer.setNameSpace(WebServer.PL_MOBILE_NAMESPACE);
			webServer.setUrl(WebServer.PL_MOBILE_URL);
		}
		else if (temp.compareToIgnoreCase(WebServer.PL_NAME) == 0) {
			webServer.setName(WebServer.PL_NAME);
			webServer.setName(WebServer.PL_SHORT_NAME);
			webServer.setWebService(WebServer.PL_WEB_SERVICE);
			webServer.setNameSpace(WebServer.PL_NAMESPACE);
			webServer.setUrl(WebServer.PL_URL);
		}
		else if (temp.compareToIgnoreCase("PL") == 0) {
			webServer.setName(WebServer.PL_NAME);
			webServer.setName(WebServer.PL_SHORT_NAME);
			webServer.setWebService(WebServer.PL_WEB_SERVICE);
			webServer.setNameSpace(WebServer.PL_NAMESPACE);
			webServer.setUrl(WebServer.PL_URL);
		}
		*/
        if (temp.compareToIgnoreCase("TT") == 0) {
            webServer.setName(WebServer.TT_NAME);
            webServer.setName(WebServer.TT_SHORT_NAME);
            webServer.setWebService(WebServer.TT_WEB_SERVICE);
            webServer.setNameSpace(WebServer.TT_NAMESPACE);
            webServer.setUrl(WebServer.TT_URL);
        }
		else {
			Toast.makeText(this, "WebServer: " + temp + " is not found.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		webservice = webServer.getWebService();
		NAMESPACE = webServer.getNameSpace();
		URL = webServer.getUrl();		
	}

	/**
	 * Used for displaying messages from the service to the divice via the
	 * handler.
	 */
	private class DisplayToast implements Runnable {
		/** the message */
		private String text;

		/**
		 * Contsrutor for DisplayToast
		 * 
		 * @param text
		 *            a string message to be displayed.
		 */
		public DisplayToast(String text) {
			this.text = text;
		}

		/** the runnable method that handler will call to display the message. */
		public void run() {
			if (toast != null)
				toast.cancel();
			toast = Toast.makeText(Webservice.this, text, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * This method is invoked on the worker thread with a request to process.
	 * Only one Intent is processed at a time, but the processing happens on a
	 * worker thread that runs independently from other application logic. So,
	 * if this code takes a long time, it will hold up other requests to the
	 * same IntentService, but it will not hold up anything else. When all
	 * requests have been handled, the IntentService stops itself, so you should
	 * not call stopSelf().
	 * 
	 * @param intent
	 *            contains a boolean flag indicating which webservice to use. As
	 *            well as what other intent data should be stored.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getBooleanExtra("patient", false)) {
			report(intent.getLongExtra("patientId", 0));
			handler.post(new DisplayToast("recording report"));
		}

		if (intent.getBooleanExtra("check cred", false)) {
			handler.post(new DisplayToast("Checking Credentials"));
			int i = checkAuthentication(settings.getString("un", null),
					settings.getString("pw", null));
			Intent intented = new Intent();
			intented.setAction("check cred");
			intented.putExtra("check cred ret", i);
			sendBroadcast(intented);
		}

		if (intent.getBooleanExtra("update all", false)) {
			handler.post(new DisplayToast("updating hospital list"));
			updateHospitalInformation();
			handler.post(new DisplayToast("updating event list"));
			updateEventInformation();

			Intent intented = new Intent();
			intented.setAction("update all");
			sendBroadcast(intented);

		}

		if (intent.getBooleanExtra("update hospital", false)) {
			handler.post(new DisplayToast("updating hospital list"));
			updateHospitalInformation();
			updateEventInformation();
			Intent intented = new Intent();
			intented.setAction("update hospital");
			sendBroadcast(intented);
		}

		if (intent.getBooleanExtra("update event", false)) {
			handler.post(new DisplayToast("updating event list"));
			updateEventInformation();
			updateEventInformation();
			Intent intented = new Intent();
			intented.setAction("update event");
			sendBroadcast(intented);
		}

		if (intent.getBooleanExtra("delete", false)) {
			handler.post(new DisplayToast("Deleteing Report"));
			deletePatient(intent.getLongExtra("patientId", 0));

		}
		if (intent.getBooleanExtra("check pid", false)) {
			handler.post(new DisplayToast(
					"Checking Patient Id for clashes in System"));
			boolean res = pIDCheck(intent.getStringExtra("short"),
					intent.getStringExtra("name"),
					intent.getStringExtra("prefix"),
					intent.getLongExtra("id", 0));
			Intent intented = new Intent();
			intented.setAction("Check");
			intented.putExtra("result", res);
			sendBroadcast(intented);
		}
		
		if (intent.getBooleanExtra("ping", false)){
			ping();
		}
		
	}

	/**
	 * Pings the target server
	 * 
	 * param t
	 *            the time it takes to ping the system
	 * param connectionError
	 *            whether there is a simple connectionError or not
	 */
	public boolean ping() {
		// check basic connectivity
		ConnectivityManager cm = (ConnectivityManager) Webservice.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) { // try to ping the
			// server
			String METHOD_NAME = "ping";
			String SOAP_ACTION = NAMESPACE + METHOD_NAME;
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				HttpTransportSE ht = new HttpTransportSE(URL);
				long to = System.currentTimeMillis();
				ht.call(SOAP_ACTION, envelope);
				SoapObject response = (SoapObject) envelope.bodyIn;
				String s = response.getPropertyAsString(0);
				DateFormat formatter = new SimpleDateFormat(
						"yyyy:MMdd HH:mm:ss", Locale.US);
				formatter.parse(s);
				long tf = System.currentTimeMillis();
				return (tf - to) < 5000;

			} catch (Exception e) {
				if (e instanceof ConnectException) {
					handler.post(new DisplayToast(
							"There was a connection error."));
					return false;
				}
				e.printStackTrace();
				if (e instanceof SocketTimeoutException
						&& webservice.equals("10.42.0.1/")) {
					settings.edit().putString("webservice", "Pl").commit();
					webservice = "pl.nlm.nih.gov/";
					NAMESPACE = "https://" + webservice;
					URL = "https://" + webservice + "?wsdl";
					return ping();
				} else {
					handler.post(new DisplayToast(
							"There was a connection error"));
					return false;
				}
			}
		} else {// had no basic connectivity
			handler.post(new DisplayToast("Wifi is not connected"));
			return false;
		}
	}

	/**
	 * Clears the hospital data table and then refills it with new information
	 * 
	 * rather than one to get the hospital list and two more run for each
	 * hospital to get hospital information to remove the overhead that builds
	 * up on so many requests, or else get only the list of hospitals and then
	 * based on selection collect that hospitals information. The only limit is
	 * hospitals can't be switched off line which could cause a problem, though
	 * unlikely
	 */
	public void updateHospitalInformation() {
		if (ping()) {
			// get hospital uuid's
			String METHOD_NAME = "getHospitalList";
			String SOAP_ACTION = NAMESPACE + METHOD_NAME;
			ArrayList<Integer> uuid = new ArrayList<Integer>();
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // Register user
                // Start
                request.addProperty("token", app.getToken());
                // End

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);

				@SuppressWarnings("rawtypes")
				Vector response = (Vector) envelope.getResponse();
				JSONArray ja = new JSONArray(response.get(0).toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONObject j = ja.getJSONObject(i);
					uuid.add(j.getInt("hospital_uuid"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Load new hospital data
			if (uuid.size() > 0) {
				s.open();
				s.clearHospitals();
				for (int id : uuid) {
					METHOD_NAME = "getHospitalData";
					SOAP_ACTION = "https://" + webservice + "getHospitalData";
					try {
						SoapObject request = new SoapObject(NAMESPACE,
								METHOD_NAME);
						request.addProperty("hospital_uuid", id);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.setOutputSoapObject(request);

						HttpTransportSE ht = new HttpTransportSE(URL);
						ht.call(SOAP_ACTION, envelope);

						@SuppressWarnings("rawtypes")
						Vector response = (Vector) envelope.getResponse();
						Hospital h = new Hospital(id);
						// check for properly formatted strings and enter
						// strings
						// as hospital components.
						String temp = response.get(0).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.name = temp;

						temp = response.get(1).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.shortname = temp;

						temp = response.get(2).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.street1 = temp;

						temp = response.get(3).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.street2 = temp;

						temp = response.get(4).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.city = temp;

						temp = response.get(5).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.county = temp;

						temp = response.get(6).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.state = temp;

						temp = response.get(7).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.country = temp;

						temp = response.get(8).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.zip = temp;

						temp = response.get(9).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.phone = temp;

						temp = response.get(10).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.fax = temp;

						temp = response.get(11).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.email = temp;

						temp = response.get(12).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.www = temp;

						temp = response.get(13).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.npi = temp;

						temp = response.get(14).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.latitude = temp;

						temp = response.get(15).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.longitude = temp;

						// get addictional hospital info
						METHOD_NAME = "getHospitalPolicy";
						SOAP_ACTION = NAMESPACE + METHOD_NAME;
						request = new SoapObject(NAMESPACE, METHOD_NAME);
						request.addProperty("hospital_uuid", h.uuid);
						envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.setOutputSoapObject(request);

						ht = new HttpTransportSE(URL);
						ht.call(SOAP_ACTION, envelope);

						@SuppressWarnings("rawtypes")
						Vector respons = (Vector) envelope.getResponse();

						temp = respons.get(0).toString();
						if (temp.contains("anyType"))
							temp = "";
						h.pidPrefix = temp;
						h.pidSuffixVariable = Boolean.parseBoolean(respons.get(
								1).toString());
						h.pidSuffixFixedLength = Integer.parseInt(respons
								.get(2).toString());
						h.photoRequired = Boolean.parseBoolean(respons.get(3)
								.toString());
						h.honorNoPhotoRequset = Boolean.parseBoolean(respons
								.get(4).toString());
						h.photographerNameRequired = Boolean
								.parseBoolean(respons.get(5).toString());
						s.createHospital(h);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				s.close();
			}
		}
	}

	/** Clears the event data table and then refills it with new information */
	public void updateEventInformation() {
		if (ping()) {
			String METHOD_NAME = "getEventListUser";
			String SOAP_ACTION = NAMESPACE + METHOD_NAME;
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				request.addProperty("username", settings.getString("un", null));
				request.addProperty("password", settings.getString("pw", null));
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);

				@SuppressWarnings("rawtypes")
				Vector response = (Vector) envelope.getResponse();
				JSONArray ja = new JSONArray(response.get(0).toString());

				s.open();
				s.clearEvents();
				for (int i = 0; i < ja.length(); i++) {
					JSONObject j = ja.getJSONObject(i);

					/**
					 * Improve event search
					 * Exclude the closed events.
					 * version 9.0.5
					 */
				/*
					String closed = j.getString("closed").toString();
					if (!closed.equalsIgnoreCase("0")){
						continue;
					}
					*/
					Event e = new Event(j.optInt("incident_id", 0),
							j.optInt("parent_id", 0), j.getString("name"),
							j.getString("shortname"), j.getString("date"),
							j.getString("type"),
							(float) j.getDouble("latitude"),
							(float) j.getDouble("longitude"),
							j.getString("street"),
							j.getString("group"),
							j.optInt("closed", 0));
					s.createEvent(e);
				}
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * checks a user's role and then their authentication if their role is valid
	 * for TPoT use.
	 * 
	 * @param un
	 *            username to check
	 * @param pw
	 *            password to check
	 * @return either 9996 (indicating the method failed due to connectivity
	 *         issues), 9997 (indicating a non-admin, hs, or hsa user, and
	 *         should be denied access) or the error code of a successful
	 *         response (0 indicating the user is authenticated, other values
	 *         indicaing a number of errors)
	 */
	@SuppressWarnings("rawtypes")
	public int checkAuthentication(String un, String pw) {
		int ret = 0;
		if (ping()) {
			try {
				// check is member of valid user group
				String METHOD_NAME = "getUserGroup";
				String SOAP_ACTION = NAMESPACE + METHOD_NAME;
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				request.addProperty("username", un);
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);

				Vector response = (Vector) envelope.getResponse();
				int x = Integer.parseInt(response.get(0).toString());
				if (x != 1 && x != 5 && x != 6)
					ret = 9997; // not a viable user for TriagePic
				else {

					// check authentication
					METHOD_NAME = "checkUserAuth";
					SOAP_ACTION = "https://" + this.webservice + "checkUserAuth";

					request = new SoapObject(NAMESPACE, METHOD_NAME);
					request.addProperty("username", un);
					request.addProperty("password", pw);
					envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);

					response = (Vector) envelope.getResponse();

					// return authentication value
					ret = Integer.parseInt((response.get(1).toString()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			ret = 9996; // cannot check not online

		if (ret == 0)
			handler.post(new DisplayToast(this.getString(R.string.success)));
		else
			handler.post(new DisplayToast(Errors.getErrorMesg(Webservice.this,
					ret)));
		return ret;
	}

	/**
	 * Checks that a given patient/mass casualty id is valid for an event
	 * 
	 * @param shortname
	 *            the shortname of the event
	 * @param name
	 *            the full name of the event
//	 * @param pid
	 * @param id
	 * @return True if the id is in use, false if the id is not in use
	 * 
	 *         the next available id up from the tested id instead of letting
	 *         the user increment and recheck on their own each time
	 */
	public boolean pIDCheck(String shortname, String name, String prefix,
			long id) {
		boolean pIDCheck;
		// first check if value is already in use for an event on the device
		handler.post(new DisplayToast(
				"Checking on Device for Patient Id clashes"));
		s.open();
		pIDCheck = s.getPIDByEvent(name).contains(id);
		s.close();
		if (pIDCheck) {
			handler.post(new DisplayToast(
					"Patient Id clashes with one on device already"));
			return pIDCheck;
		}
		handler.post(new DisplayToast("Checking on " + this.webservice
				+ " for Patient Id Clashes"));
		// Second check the website
		if (ping()) {
			try {
				String METHOD_NAME = "getUuidByMassCasualtyId";
				String SOAP_ACTION = NAMESPACE + METHOD_NAME;
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				request.addProperty("mcid", prefix + id);
				request.addProperty("shortname", shortname);
				request.addProperty("username", settings.getString("un", null));
				request.addProperty("password", settings.getString("pw", null));
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);
				ht.call(SOAP_ACTION, envelope);

				@SuppressWarnings("rawtypes")
				Vector response = (Vector) envelope.getResponse();
				if (!response.get(1).toString().equals("407"))
					return true;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		/*
		 * if it wasn't on the device and the server was inaccessible assume id
		 * not in use.
		 */
		return false;
	}

	/**
	 * Deletes the given patient record
	 * 
	 * @param id
	 *            patient record row index
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void deletePatient(long id) {
		s.open();
		Patient p = s.getPatient(id);
		s.close();
		// if was sent delete from internet and database

		if (p.uuid != null) {
			handler.post(new DisplayToast("Testing Connection"));
			if (ping()) {
				String METHOD_NAME = "expirePerson";
				String SOAP_ACTION = NAMESPACE + METHOD_NAME;
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				request.addProperty("uuid", p.uuid);
				request.addProperty("explanation", "Testing Expire Function");
				request.addProperty("username", settings.getString("un", null));
				request.addProperty("password", settings.getString("pw", null));
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				HttpTransportSE ht = new HttpTransportSE(URL);
				try {
					ht.call(SOAP_ACTION, envelope);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}

				int i = 999999;
				try {
					i = Integer.parseInt(((Vector) envelope.getResponse()).get(
							1).toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SoapFault e) {
					e.printStackTrace();
				}

				if (i == 0) {
					handler.post(new DisplayToast("Record Deleted from "
							+ this.webservice));
					handler.post(new DisplayToast("Deleteing from Device"));
					s.open();
					s.deletePatient(p.rowIndex);
					s.close();
					handler.post(new DisplayToast("Record Deleted"));
					Intent intented = new Intent();
					intented.setAction("Deleted");
					sendBroadcast(intented);
					return;
				} else if (i == 413) {
					handler.post(new DisplayToast(
							"Record was already Deleted from "
									+ this.webservice));
					handler.post(new DisplayToast("Deleteing from Device"));
					s.open();
					s.deletePatient(p.rowIndex);
					s.close();
					handler.post(new DisplayToast("Record Deleted"));
					Intent intented = new Intent();
					intented.setAction("Deleted");
					sendBroadcast(intented);

				} else if (i == 410) {
					handler.post(new DisplayToast(
							"UUID does not match any record on "
									+ this.webservice));

					handler.post(new DisplayToast("Deleteing from Device"));
					s.open();
					s.deletePatient(p.rowIndex);
					s.close();
					handler.post(new DisplayToast("Record Deleted"));
					Intent intented = new Intent();
					intented.setAction("Deleted");
					sendBroadcast(intented);

				} else {

					handler.post(new DisplayToast("Could not Delete from "
							+ this.webservice + "/nError "
							+ Errors.getErrorMesg(this, i)));

				}

			} else {
				handler.post(new DisplayToast("Could not reach "
						+ this.webservice));
				return;
			}
		} else {
			handler.post(new DisplayToast("Deleteing from Device"));
			s.open();
			s.deletePatient(p.rowIndex);
			s.close();

			handler.post(new DisplayToast("Record Deleted"));
			Intent intented = new Intent();
			intented.setAction("Deleted");
			sendBroadcast(intented);
			return;
		}
	}

	/**
	 * Report or rereport a person Paramters are an object array, containing the
	 * patient, event, hospital, and context. If the patient object passed
	 * already has a uuid it will be rereported, if it does not it will be
	 * reported.
	 * 
	 * param p
	 *            the patient
	 * param e
	 *            the patient's event
	 * param h
	 *            the patient's hospital
	 */
	protected void report(long id) {
		s.open();
		Patient p = s.getPatient(id);
		Hospital h = s.getHospital(p.hospital);
		Event e = s.getEvent(p.event);
		s.close();

		if (ping()) {
			String xml = null;
			try {
				xml = Patient.toXML(p, e, h, Webservice.this);
			} catch (IllegalArgumentException e2) {
				e2.printStackTrace();
			} catch (IllegalStateException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			if (p.uuid == null) {
				try {
					String METHOD_NAME = "reportPerson";
					String SOAP_ACTION = NAMESPACE + METHOD_NAME;
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
					request.addProperty("personXML", xml);
					request.addProperty("eventShortname", e.shortname);
					request.addProperty("xmlFormat", "TRIAGEPIC1");
					request.addProperty("username",
							settings.getString("un", null));
					request.addProperty("password",
							settings.getString("pw", null));
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);

					@SuppressWarnings("rawtypes")
					final Vector response = (Vector) envelope.getResponse();
					p.error = Integer.parseInt(response.get(1).toString());
					p.uuid = response.get(0).toString();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {// reReport
				try {
					String METHOD_NAME = "reReportPerson";
					String SOAP_ACTION = NAMESPACE + METHOD_NAME;
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
					request.addProperty("uuid", p.uuid);
					request.addProperty("personXML", xml);
					request.addProperty("eventShortname", e.shortname);
					request.addProperty("xmlFormat", "TRIAGEPIC1");
					request.addProperty("username",
							settings.getString("un", null));
					request.addProperty("password",
							settings.getString("pw", null));
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(URL);
					ht.call(SOAP_ACTION, envelope);

					@SuppressWarnings("rawtypes")
					final Vector response = (Vector) envelope.getResponse();
					p.error = Integer.parseInt(response.get(0).toString());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else {
			p.error = 9996;
		}

		s.open();
		s.updatePatient(p);
		s.close();
		Intent intented = new Intent();
		intented.setAction("Sent");
		sendBroadcast(intented);
	}
}