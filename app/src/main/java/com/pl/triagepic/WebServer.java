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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.pl.triagepic.Result.AddCommentResult;
import com.pl.triagepic.Result.ReportAbuseResult;
import com.pl.triagepic.Result.ReportResult;
import com.pl.triagepic.Result.ReservePIDResult;
import com.pl.triagepic.Result.Result;
import com.pl.triagepic.Result.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebServer {
    public static final String TAG = "WebServer";

    public static final String TIME_OUT = "Time Out!";

    public static final String TS_NAME = "TriageTrakStage";
    public static final String TS_SHORT_NAME = "TS";
    public static final String TS_WEB_SERVICE = "removed.com/";
    public static final String TS_NAMESPACE = "https://removed.com/plusWebServices/";
    public static final String TS_NAMESPACE_IMAGE = "https://stage.removed.com/";
    public static final String TS_URL = "https://stage.removed.com/plus1"; // soap end point

    public static final String END_POINT = "plus1#"; // 9.0.3
    public static final String NAMESPACE_PLUS = "plusWebServices/";

    public static final String TT_NAME = "TriageTrak";
    public static final String TT_SHORT_NAME = "TT";
    public static final String TT_WEB_SERVICE = "removed.com/";
    public static final String TT_NAMESPACE = "https://removed.com/plusWebServices/";
    public static final String TT_NAMESPACE_IMAGE = "https://removed.com/";
    public static final String TT_URL = "https://removed.com/plus1"; // soap end point 9.0.3

    private long id;
    private String name;
    private String shortName;
    private String webService;
    private String url;
    private String nameSpace;
    private String nameSpaceImage;

    private String token = "";
    private String tokenAnonymous = "";
    private int tokenStatus = TriagePic.TOKEN_UNKNOWN;

    private SharedPreferences settings;// use to get webservice and crednetials
    private String errorCode = "";
    private String errorMessage;
    private double timeElapsed;

    private Event event;
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    private Hospital hospital;
    public Hospital getHospital() {
        return hospital;
    }
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    private String returnString;

    private Cookie cookie = new Cookie();

    // get from reunite - start
    private boolean searchCountOnly;
    public void setSearchCountOnly(boolean searchCountOnly) {
        this.searchCountOnly = searchCountOnly;
    }

    private int curPageStart;
    public void setCurPageStart(int curPageStart) {
        this.curPageStart = curPageStart;
    }

    // Query
    private String query;
    public void setQuery(String query) {
        this.query = query;
    }
    public String getQuery() {
        return query;
    }
    // get from reunite - end

    private SearchResult searchResult;
    public SearchResult getSearchResult() {
        return searchResult;
    }
    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    private Result result;
    public Result getResult(){return result;}
    public void setResult(Result result){ this.result = result; }

    private AddCommentResult addCommentResult;
    public AddCommentResult getAddCommentResult() {
        return addCommentResult;
    }
    public void setAddCommentResult(AddCommentResult addCommentResult) {
        this.addCommentResult = addCommentResult;
    }

    private ReportAbuseResult reportAbuseResult;
    public ReportAbuseResult getReportAbuseResult() {
        return reportAbuseResult;
    }
    public void setReportAbuseResult(ReportAbuseResult reportAbuseResult) {
        this.reportAbuseResult = reportAbuseResult;
    }

    private List<Patient> patientList = new ArrayList<Patient>(); // added in for version 9
    public void setPatientList(List<Patient> patientList){this.patientList = patientList;}
    public List<Patient> getPatientList(){return patientList;}

    ReportResult reportResult;
    ReservePIDResult reservePIDResult;

    WebServer() {
        this.id = 0;
        this.name = TT_NAME;
        this.shortName = TT_SHORT_NAME;
        this.webService = TT_WEB_SERVICE;
        this.url = TT_URL;
        this.nameSpace = TT_NAMESPACE;
        this.nameSpaceImage = TT_NAMESPACE_IMAGE;
        this.token = "";
        this.tokenAnonymous = "";
        this.tokenStatus = TriagePic.TOKEN_UNKNOWN;

        this.event = new Event();
        this.hospital = new Hospital();

        searchResult = new SearchResult();
        addCommentResult = new AddCommentResult();
        reportAbuseResult = new ReportAbuseResult();
        this.setQuery("");
    }

    WebServer(WebServer w) {
        this.id = w.getId();
        this.name = w.getName();
        this.shortName = w.getShortName();
        this.webService = w.getWebService();
        this.url = w.getUrl();
        this.nameSpace = w.getNameSpace();
        this.nameSpaceImage = w.getNameSpaceImage();
        this.token = w.getToken();
        this.tokenAnonymous = w.getTokenAnonymous();
        this.tokenStatus = w.getTokenStatus();

        this.event = w.getEvent();
        this.hospital = w.getHospital();

        this.searchResult = w.getSearchResult();
        this.addCommentResult = w.getAddCommentResult();
        this.reportAbuseResult = w.getReportAbuseResult();
        this.setQuery(w.getQuery());
    }

    WebServer(String webService) {
        if (webService.equalsIgnoreCase(TT_WEB_SERVICE)) {
            this.id = 0;
            this.name = TT_NAME;
            this.shortName = TT_SHORT_NAME;
            this.webService = TT_WEB_SERVICE;
            this.url = TT_URL;
            this.nameSpace = TT_NAMESPACE;
            this.nameSpaceImage = TT_NAMESPACE_IMAGE;
        }
        else if (webService.equalsIgnoreCase(TS_WEB_SERVICE)){
            this.id = 1;
            this.name = TS_NAME;
            this.shortName = TS_SHORT_NAME;
            this.webService = TS_WEB_SERVICE;
            this.url = TS_URL;
            this.nameSpace = TS_NAMESPACE;
            this.nameSpaceImage = TS_NAMESPACE_IMAGE;
        }

        this.event = new Event();
        this.hospital = new Hospital();

        this.timeElapsed = 0;
        this.errorMessage = "";
        this.errorCode = "";

        searchResult = new SearchResult();

        this.setQuery("");
    }

    WebServer(String name, String shortName, String webService, String nameSpace, String url) {
        this.name = name;
        this.shortName = shortName;
        this.webService = webService;
        this.nameSpace = nameSpace;

        // added in version 9.0.3
        int index = this.nameSpace.lastIndexOf(WebServer.NAMESPACE_PLUS);
        if (index > 0){
            this.nameSpaceImage = this.nameSpace.substring(0, index);
        }
        else {
            this.nameSpaceImage = this.nameSpace;
        }

        this.url = url;
//        this.url = PL_URL; // for test
        this.timeElapsed = 0;
        this.errorMessage = "";
        this.errorCode = "";

        this.event = new Event();
        this.hospital = new Hospital();

        searchResult = new SearchResult();
        this.setQuery("");
    }

    public String getTokenAnonymous(){
        return tokenAnonymous;
    }
    public void setTokenAnonymous(String tokenAnonymous){
        this.tokenAnonymous = tokenAnonymous;
    }

    public int getTokenStatus(){
        return tokenStatus;
    }
    public void setTokenStatus(int tokenStatus){
        this.tokenStatus = tokenStatus;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    public double getTimeElapsed() {
        return this.timeElapsed;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }


    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setWebService(String webService) {
        this.webService = webService;
    }

    public String getWebService() {
        return this.webService;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }

    public String getNameSpaceImage() {
        return nameSpaceImage;
    }

    public void setNameSpaceImage(String nameSpaceImage) {
        this.nameSpaceImage = nameSpaceImage;
    }

    public String callRequestAnonToken(Context c) {
        final String METHOD_NAME = "requestAnonToken";
        final String soapAction = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    requestUserToken();
                }

                private void requestUserToken() {
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

//			    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//			        SoapObject result = null;
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try {
                        aht.debug = true;
                        aht.call(soapAction, envelope);
                        Log.e("Soap request ", aht.requestDump);
                        Log.e("Soap response ", aht.responseDump);
//                        result = (SoapPrimitive) envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                    }

                    SoapObject resultRequestSOAP = (SoapObject) envelope.bodyIn;
                    tokenAnonymous = resultRequestSOAP.getPropertyAsString("token").toString();
                    errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
                    errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");

                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures) {
            try {
                f.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                Toast.makeText(c, "Time out!", Toast.LENGTH_SHORT).show();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        if (errorCode.equalsIgnoreCase("0") == true){
            Toast.makeText(c, "tokenAnonymous is granted.", Toast.LENGTH_SHORT).show();
            tokenStatus = TriagePic.TOKEN_ANONYMOUS;
        }
        else {
            Toast.makeText(c, "tokenAnonymous is NOT granted.", Toast.LENGTH_SHORT).show();
        }

        return tokenAnonymous;
    }

    public String callRequestUserToken(Context c, final String un, final String pw) {
        final String METHOD_NAME = "requestUserToken";
        final String soapAction = this.nameSpace + WebServer.END_POINT + METHOD_NAME; // End poing test

        if (un.isEmpty()) {
            Log.e("Error: ", "Username is not defined.");
            return token;
        }
        if (pw.isEmpty()) {
            Log.e("Error: ", "Password is not defined.");
            return token;
        }

        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    requestUserToken();
                }

                private void requestUserToken() {
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    // Register user
                    // Start
                    request.addProperty("username", un.toString());
                    request.addProperty("password", pw.toString());
                    // End

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

//			    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//			        SoapObject result = null;
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try {
                        aht.debug = true;
                        aht.call(soapAction, envelope);
                        Log.e("Soap request ", aht.requestDump);
                        Log.e("Soap response ", aht.responseDump);
//                        result = (SoapPrimitive) envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                        token = "";
                        errorCode = SearchResult.MY_ERROR_CODE;
                        errorMessage = e.getMessage();
                        return;
                    }

                    SoapObject resultRequestSOAP = (SoapObject) envelope.bodyIn;
                    token = resultRequestSOAP.getPropertyAsString("token").toString();
                    errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
                    errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures) {
            try {
                f.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                Toast.makeText(c, "Time out!", Toast.LENGTH_SHORT).show();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return token;
    }

	public static boolean AmIConnected(Context c) {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}	
	
	public String callUpdateHospitalInformation(final Context c, final String seed) {
		String returnString = "";
		
		//limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			updateHospitalInformation(c, seed);
	    		}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(1000, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
    
	    // Display the result.
	    if (returnString.equalsIgnoreCase("Time out!")) {
	    	return this.TIME_OUT;
	    }
    	return returnString;		
	}

	public void updateHospitalInformation(Context c, String seed) {
		if (this.nameSpace.isEmpty()){
			Log.e("Error: ", "nameSpace is not defined.");
			return;
		}
		
		if (this.url.isEmpty()){
			Log.e("Error: ", "url is not defined.");
			return;
		}

		// get hospital uuid's
		String METHOD_NAME = "getHospitalList";
		String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
		ArrayList<Integer> uuid = new ArrayList<Integer>();
		try {
			SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
            request.addProperty("token", this.token);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(this.url);
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
		if (uuid.size() <= 0){
			Toast.makeText(c, "Hospital list is empty.", Toast.LENGTH_SHORT).show();
		}
		else /*(uuid.size() > 0)*/ {
			DataSource s = new DataSource(c, seed);
			s.open();
			s.clearHospitals();
			for (int id : uuid) {
				METHOD_NAME = "getHospitalData";
//				SOAP_ACTION = "https://" + webservice + METHOD_NAME;
				SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
				try {
					SoapObject request = new SoapObject(this.nameSpace,	METHOD_NAME);
                    request.addProperty("token", this.token);
                    request.addProperty("hospital_uuid", id);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(this.url);
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
                    Log.e("Hospital name:", h.name);

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
//					SOAP_ACTION = NAMESPACE + METHOD_NAME;
					SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
					request = new SoapObject(this.nameSpace, METHOD_NAME);
                    request.addProperty("token", this.token);
					request.addProperty("hospital_uuid", h.uuid);
					envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					ht = new HttpTransportSE(this.url);
					ht.call(SOAP_ACTION, envelope);

					@SuppressWarnings("rawtypes")
					Vector respons = (Vector) envelope.getResponse();

					temp = respons.get(0).toString();
					if (temp.contains("anyType"))
						temp = "";

                    // no '-'
                    if (temp.endsWith("-") == true){
                        temp = temp.substring(0, temp.length() - 1);
                    }

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

	public String callUpdateHospitalPolicy(final Context c, final String seed) {
		String returnString = "";
		
		//limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			updateHospitalPolicy(c, seed);
	    		}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(100, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
    
	    // Display the result.
	    if (returnString.equalsIgnoreCase("Time out!")) {
	    	return this.TIME_OUT;
	    }
    	return returnString;		
	}

	public void updateHospitalPolicy(Context c, String seed) {
		if (this.nameSpace.isEmpty()){
			Log.e("Error: ", "nameSpace is not defined.");
			return;
		}
		
		if (this.url.isEmpty()){
			Log.e("Error: ", "url is not defined.");
			return;
		}

		// get hospital uuid's
		String METHOD_NAME = "getHospitalPolicy";
		String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
		ArrayList<Integer> uuid = new ArrayList<Integer>();
		try {
			SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(this.url);
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
			DataSource s = new DataSource(c, seed);
			s.open();
			s.clearHospitals();
			for (int id : uuid) {
				METHOD_NAME = "getHospitalPolicy";
//				SOAP_ACTION = "https://" + webservice + METHOD_NAME;
				SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
				try {
					SoapObject request = new SoapObject(this.nameSpace,	METHOD_NAME);
					request.addProperty("hospital_uuid", id);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					HttpTransportSE ht = new HttpTransportSE(this.url);
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
//					SOAP_ACTION = NAMESPACE + METHOD_NAME;
					SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
					request = new SoapObject(this.nameSpace, METHOD_NAME);
					request.addProperty("hospital_uuid", h.uuid);
					envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.setOutputSoapObject(request);

					ht = new HttpTransportSE(this.url);
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

	public String callUpdateEventInformation(final String un, final String pw, final Context c, final String seed) {
		String returnString = "";
		
		//limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			updateEventInformation(un, pw, c, seed);
	    		}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(100, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
    
	    // Display the result.
	    if (returnString.equalsIgnoreCase("Time out!")) {
	    	return this.TIME_OUT;
	    }
    	return returnString;		
	}

    // v33
    public String callUpdateEventInformation(final Context c, final String seed) {
        String returnString = "";

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    updateEventInformation(c, seed);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        // Display the result.
        if (returnString.equalsIgnoreCase("Time out!")) {
            return this.TIME_OUT;
        }
        return returnString;
    }

	public void updateEventInformation(String sn, String pw, Context c, String seed){
		if (this.nameSpace.isEmpty()){
			Log.e("Error: ", "nameSpace is not defined.");
			return;
		}
		
		if (this.url.isEmpty()){
			Log.e("Error: ", "url is not defined.");
			return;
		}

        String METHOD_NAME = "getEventListUser";
		String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

		try {
			SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			if (sn.equalsIgnoreCase("guest")){
//                request.addProperty("username", "guest");
//                request.addProperty("password", "");
                request.addProperty("username", "hs");
                request.addProperty("password", "Try2FlyMe");
			}
			else {
				request.addProperty("username", sn);
				request.addProperty("password", pw);
			}
			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(this.url);
			ht.call(SOAP_ACTION, envelope);

			@SuppressWarnings("rawtypes")
			Vector response = (Vector) envelope.getResponse();
			JSONArray ja = new JSONArray(response.get(0).toString());

			DataSource s = new DataSource(c, seed);
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
                        j.optInt("parent_id", 0),
                        j.getString("name"),
                        j.getString("shortname"),
                        j.getString("date"),
                        j.getString("type"),
                        (float) j.getDouble("latitude"),
                        (float) j.getDouble("longitude"),
                        j.getString("street"),
                        j.getString("group"),
                        j.optInt("closed", 0));
                s.createEvent(e);
			}
			s.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(c, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("TriagePic", e.getMessage());
		}
	}

    // v33
    public void updateEventInformation(Context c, String seed){
        if (this.nameSpace.isEmpty()){
            Log.e("Error: ", "nameSpace is not defined.");
            return;
        }

        if (this.url.isEmpty()){
            Log.e("Error: ", "url is not defined.");
            return;
        }

        String METHOD_NAME = "getEventList";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

        try {
            SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            request.addProperty("token", this.token);
            envelope.setOutputSoapObject(request);

            HttpTransportSE ht = new HttpTransportSE(this.url);
            ht.call(SOAP_ACTION, envelope);

            @SuppressWarnings("rawtypes")
            Vector response = (Vector) envelope.getResponse();
            JSONArray ja = new JSONArray(response.get(0).toString());

            DataSource s = new DataSource(c, seed);
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
                        j.optInt("parent_id", 0),
                        j.getString("name"),
                        j.getString("shortname"),
                        j.getString("date"),
                        j.getString("type"),
                        (float) j.getDouble("latitude"),
                        (float) j.getDouble("longitude"),
                        j.getString("street"),
                        j.getString("group"),
                        j.optInt("closed", 0));
                s.createEvent(e);
            }
            s.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(c, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TriagePic", e.getMessage());
        }
    }

    // v33
    public void report(long id, Context c, String token, String seed) {
        if (this.nameSpace.isEmpty()){
            Log.e("Error: ", "nameSpace is not defined.");
            return;
        }

        if (this.url.isEmpty()){
            Log.e("Error: ", "url is not defined.");
            return;
        }

        DataSource s = new DataSource(c, seed);
        s.open();
        Patient p = s.getPatient(id);
        Hospital h = s.getHospital(p.hospital);
        Event e = s.getEvent(p.event);
        s.close();

        String xml = null;
        try {
            xml = Patient.toXML(p, e, h, c);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (p.uuid == null || p.uuid.isEmpty()) {
            try {
                String METHOD_NAME = "reportPerson";
                String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

                SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

                request.addProperty("token", token);
                request.addProperty("payload", xml);// personXML renamed to payload
                request.addProperty("payloadFormat", "TRIAGEPIC1"); // xmlFormat renamed to payloadFormat
                request.addProperty("shortname", e.shortname); // eventShortname renamed to shortname

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE ht = new HttpTransportSE(this.url);
                ht.call(SOAP_ACTION, envelope);

                @SuppressWarnings("rawtypes")
                final Vector response = (Vector) envelope.getResponse();
                p.error = Integer.parseInt(response.get(1).toString());
                p.uuid = response.get(0).toString();
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(c, "Error: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error: ", e1.getMessage());
            }
        } else {// reReport
            try {
                String METHOD_NAME = "reReportPerson";
                String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

                SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

                request.addProperty("token", token);
                request.addProperty("payload", xml);// personXML renamed to payload
                request.addProperty("payloadFormat", "TRIAGEPIC1"); // xmlFormat renamed to payloadFormat
                request.addProperty("shortname", e.shortname); // eventShortname renamed to shortname
                request.addProperty("uuid", p.uuid);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);

                HttpTransportSE ht = new HttpTransportSE(this.url);
                ht.call(SOAP_ACTION, envelope);

                @SuppressWarnings("rawtypes")
                final Vector response = (Vector) envelope.getResponse();
                p.error = Integer.parseInt(response.get(0).toString());
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(c, "Error: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error: ", e1.getMessage());
            }
        }

        s.open();
        s.updatePatient(p);
        s.close();
    }

    public ReportResult reportV34(long id, Context c, String token, String seed) {
        ReportResult reportResult = new ReportResult();
        reportResult.toDefault();

        if (this.nameSpace.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("nameSpace is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        if (this.url.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("url is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        // get the patient data from database
        DataSource s = new DataSource(c, seed);
        s.open();
        Patient p = s.getPatient(id);
        Hospital h = s.getHospital(p.hospital);
        Event e = s.getEvent(p.event);
        s.close();

        // define json data
        JSONObject json = new JSONObject();
        if (p.uuid == null || p.uuid.isEmpty()) {
            String METHOD_NAME = "report";
            String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

            SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
            request.addProperty("token", token.toString());

            try {
                json = Patient.toJSON(p, e, h, c);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
            request.addProperty("payload", json.toString());// must be toString(). Otherwise it won't work.
            request.addProperty("payloadFormat", "JSONPATIENT1"); //
            request.addProperty("shortname", e.shortname.toString()); // eventShortname renamed to shortname

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);

            HttpTransportSE ht = new HttpTransportSE(this.url);
            ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
            SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
            try {
                ht.debug = true;
                ht.call(SOAP_ACTION, envelope);
                Log.e("Soap request ", ht.requestDump);
                Log.e("Soap responce ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
            }
            catch (Exception e2) {
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(e2.getMessage().toString());
                Toast.makeText(c, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                return reportResult;
            }

            final Vector response;
            String errorMessage = "";
            try {
                response = (Vector) envelope.getResponse();
                reportResult.setErrorCode(response.get(1).toString());
                reportResult.setErrorMessage(response.get(2).toString());
                p.error = Integer.parseInt(response.get(1).toString());
                if (reportResult.getErrorCode().equalsIgnoreCase("0")) {
                    p.uuid = response.get(0).toString();
                }
                else {
                    p.uuid = "-1";
                }
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(soapFault.getMessage().toString());
                Log.e("Error in report: ", soapFault.getMessage());
                return reportResult;
            }
        }
        else {// reReport
            // Search existed photos first, if photos are found, need to remove them later.
            // version 9.0.2
            // start
//            ArrayList<Image> imagesToDelete = new ArrayList<>();
//            imagesToDelete.addAll(searchAllPhotosUploaded(p, e.shortname, token));
            // end

            String METHOD_NAME = "updateRecord";
            String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

            SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

            request.addProperty("token", token);

            try {
                json = Patient.toJSON(p, e, h, c);
                /*
                if (imagesToDelete.isEmpty()) {
                }
                else {
                    json = Patient.toJSON(p, e, h, c, imagesToDelete);
                }
                */
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
            request.addProperty("payload", json.toString());// personXML renamed to payload
            request.addProperty("payloadFormat", "JSONPATIENT1"); // xmlFormat renamed to payloadFormat
            request.addProperty("shortname", e.shortname); // eventShortname renamed to shortname
            request.addProperty("uuid", p.uuid);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);

            HttpTransportSE ht = new HttpTransportSE(this.url);
            ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
            SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
            try {
                ht.debug = true;
                ht.call(SOAP_ACTION, envelope);
                Log.e("Soap request ", ht.requestDump);
                Log.e("Soap responce ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
            } catch (Exception e2) {
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(e2.getMessage().toString());
                Toast.makeText(c, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                return reportResult;
            }

            final Vector response;
            String errorMessage = "";
            try {
                response = (Vector) envelope.getResponse();
                reportResult.setErrorCode(response.get(0).toString());
                reportResult.setErrorMessage(response.get(1).toString());
                p.error = Integer.parseInt(response.get(0).toString());
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                Log.e("Error in report: ", soapFault.getMessage());
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(soapFault.getMessage());
                return reportResult;
            }

        }

        if (!reportResult.getErrorCode().equalsIgnoreCase("0")) {
            Log.e("Error in report: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        // save to database
        s.open();
        p.boxId = Patient.SENT;
        s.updatePatient(p);
        s.close();

        return reportResult;
    }

    public ReportResult reportDeleteImageV34(long id, Context c, String token, String seed) {
        ReportResult reportResult = new ReportResult();
        reportResult.toDefault();

        if (this.nameSpace.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("nameSpace is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        if (this.url.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("url is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        // get the patient data from database
        DataSource s = new DataSource(c, seed);
        s.open();
        Patient p = s.getPatient(id);
        Hospital h = s.getHospital(p.hospital);
        Event e = s.getEvent(p.event);
        s.close();

        // define json data
        JSONObject json = new JSONObject();
        if (p.uuid == null || p.uuid.isEmpty()) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            String msg = "Patient UUID is not defined.";
            reportResult.setErrorMessage(msg);
            Log.e("Error in report: ", msg);
            return reportResult;
        }
        else {// reReport
            // Search existed photos first, if photos are found, need to remove them later.
            // version 9.0.2
            // start
            ArrayList<Image> imagesToDelete = new ArrayList<>();
            imagesToDelete.addAll(searchAllPhotosUploaded(p, e.shortname, token));
            if (imagesToDelete == null || imagesToDelete.isEmpty()){
                reportResult.setErrorCode("0");
                reportResult.setErrorMessage("No image is found.");
                return reportResult;
            }
            // end

            String METHOD_NAME = "updateRecord";
            String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

            SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

            request.addProperty("token", token);

            try {
                json = Patient.toJSON(p, e, h, c, imagesToDelete);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
            request.addProperty("payload", json.toString());// personXML renamed to payload
            request.addProperty("payloadFormat", "JSONPATIENT1"); // xmlFormat renamed to payloadFormat
            request.addProperty("shortname", e.shortname); // eventShortname renamed to shortname
            request.addProperty("uuid", p.uuid);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = false;
            envelope.setOutputSoapObject(request);

            HttpTransportSE ht = new HttpTransportSE(this.url);
            ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
            SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
            try {
                ht.debug = true;
                ht.call(SOAP_ACTION, envelope);
                Log.e("Soap request ", ht.requestDump);
                Log.e("Soap responce ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
            } catch (Exception e2) {
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(e2.getMessage().toString());
                Toast.makeText(c, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                return reportResult;
            }

            final Vector response;
            String errorMessage = "";
            try {
                response = (Vector) envelope.getResponse();
                reportResult.setErrorCode(response.get(0).toString());
                reportResult.setErrorMessage(response.get(1).toString());
                p.error = Integer.parseInt(response.get(0).toString());
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                Log.e("Error in report: ", soapFault.getMessage());
                reportResult.setErrorCode(Result.MY_ERROR_CODE);
                reportResult.setErrorMessage(soapFault.getMessage());
                return reportResult;
            }

        }

        if (!reportResult.getErrorCode().equalsIgnoreCase("0")) {
            Log.e("Error in report: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        // save to database
        s.open();
        p.boxId = Patient.SENT;
        s.updatePatient(p);
        s.close();

        return reportResult;
    }

    public ArrayList<Image> searchAllPhotosUploaded(Patient p, String eventShort, String token) {
        ArrayList<Image> imagesToDelete = null;
        Patient pFound = null;

        this.setToken(token);

        String pid = String.valueOf(p.getPid());
        if (pid.isEmpty()){
            return null;
        }

        // find the patient
        SearchResult searchResult = callSearchByPID(pid, eventShort);
        // if error happens

        JSONParserForPatient(eventShort, searchResult.getResultSet());
        for (int i = 0; i < patientList.size(); i++){
            Patient pp = patientList.get(i);
            if (p.getPid() == pp.getPid()){
                pFound = pp;
                break;
            }
        }
        if (pFound == null){
            return null;
        }

        if (pFound.getImages() == null){
            return null;
        }

        // get the image
        imagesToDelete = new ArrayList<>();
        imagesToDelete.addAll(pFound.getImages());
        return imagesToDelete;
    }

    public ReportResult callReport(final Long id, final Context c, final String token, final String seed) {
        String returnString = "";

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run() {
                    reportResult = reportV34(id, c, token, seed);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        // Display the result.
        return reportResult;
    }

    public ReportResult callReportDeleteImage(final Long id, final Context c, final String token, final String seed) {
        String returnString = "";

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    reportResult = reportDeleteImageV34(id, c, token, seed);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        // Display the result.
        return reportResult;
    }



    public ReportResult deleteAllPhotosUploaded(ArrayList<Image> images, Context c, String seed){
        ReportResult reportResult = new ReportResult();
        reportResult.toDefault();

        if (this.nameSpace.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("nameSpace is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        if (this.url.isEmpty()){
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage("url is not defined.");
            Log.e("Error: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        // get the patient data from database
        DataSource s = new DataSource(c, seed);
        s.open();
        Patient p = s.getPatient(id);
        Hospital h = s.getHospital(p.hospital);
        Event e = s.getEvent(p.event);
        s.close();

        // define json data
        JSONObject json = new JSONObject();
        // Search existed photos first, if photos are found, need to remove them later.
        // version 9.0.2
        // start
        ArrayList<Image> imagesToDelete = new ArrayList<>();
        imagesToDelete.addAll(searchAllPhotosUploaded(p, e.shortname, token));
        // end

        String METHOD_NAME = "updateRecord";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        request.addProperty("token", token);

        try {
            json = Patient.toJSON(p, e, h, c, imagesToDelete);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
        request.addProperty("payload", json.toString());// personXML renamed to payload
        request.addProperty("payloadFormat", "JSONPATIENT1"); // xmlFormat renamed to payloadFormat
        request.addProperty("shortname", e.shortname); // eventShortname renamed to shortname
        request.addProperty("uuid", p.uuid);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        } catch (Exception e2) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(e2.getMessage().toString());
            Toast.makeText(c, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
            return reportResult;
        }

        final Vector response;
        String errorMessage = "";
        try {
            response = (Vector) envelope.getResponse();
            reportResult.setErrorCode(response.get(0).toString());
            reportResult.setErrorMessage(response.get(1).toString());
            p.error = Integer.parseInt(response.get(0).toString());
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(soapFault.getMessage());
            return reportResult;
        }

        if (!reportResult.getErrorCode().equalsIgnoreCase("0")) {
            Log.e("Error in report: ", reportResult.getErrorMessage().toString());
            return reportResult;
        }

        return reportResult;
    }

    public ArrayList<Image> searchPatientImages(Patient p) {
        ArrayList<Image> images = null;

        if (p == null){
            return null;
        }

        images = p.getImages();
        if (images != null){
            for (int j = 0; j < images.size(); j++){
                Image img = images.get(j);
                img.downloadPatientPhoto();

                /**
                 * Simple is better.
                 */
                int outWidth;
                int outHeight;
                int inWidth = img.getBitmap().getWidth();
                int inHeight = img.getBitmap().getHeight();
                if(inWidth > inHeight){
                    outWidth = Image.MAX_SIZE;
                    outHeight = (inHeight * Image.MAX_SIZE) / inWidth;
                } else {
                    outHeight = Image.MAX_SIZE;
                    outWidth = (inWidth * Image.MAX_SIZE) / inHeight;
                }
                Bitmap resized = Bitmap.createScaledBitmap(img.getBitmap(), outWidth, outHeight, false);
//                        Bitmap resize = Bitmap.createScaledBitmap(img.getBitmap(), Image.MAX_SIZE, Image.MAX_SIZE, false);
                img.setBitmap(resized);
                img.setDigest(img.getDigestFromBitmap(resized));
                img.setEncoded(p.encodeBitmapToString(resized));

                images.set(j, img);
            }

            if (images != null) {
                p.setImages(images);
            }
        }

        return images;
    }

    public String callcheckAuthentication(final String un, final String pw, final Context c) {
		String returnString = "";
		
		//limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			if (checkAuthentication(un, pw) == 0){
	    				errorMessage = "";
	    			}
	    			else {
	    				errorMessage = "Authentication test is failed.";
	    			}
	    		}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(10000, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
    
	    // Display the result.
	    if (returnString.equalsIgnoreCase("Time out!")) {
	    	return this.TIME_OUT;
	    }
    	return returnString;		
	}

    public String resetUserPassword(final String token, final String emailAddress, final Context c) {
        errorMessage = "";

        if (emailAddress.length() == 0){
            errorCode = "-1";
            errorMessage = "Email address is empty!";
            return errorMessage;
        }

        if (this.nameSpace.isEmpty()){
            errorCode = "-1";
            errorMessage = "Name space is not defined!";
            return errorMessage;
        }

        final String METHOD_NAME = "resetUserPassword";
        final String NAME_SPACE = this.nameSpace;
        final String SOAP_ACTION = NAME_SPACE + WebServer.END_POINT + METHOD_NAME;

        // Better to use the threads.
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    errorMessage = resetUserPassword();
                }

                public String resetUserPassword() {
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    // Register user
                    // Start
                    request.addProperty("token", token.toString());
                    request.addProperty("email", emailAddress.toString());
                    // End

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

//			    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//			        SoapObject result = null;
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try
                    {
                        aht.debug = true;
                        aht.call(SOAP_ACTION, envelope);
                        Log.e("Soap request ", aht.requestDump);
                        Log.e("Soap response ", aht.responseDump);
                        result = (SoapPrimitive)envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                        errorMessage = e.getMessage().toString();
                        return errorMessage;
                    }

                    SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
                    errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
                    errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
                    return errorMessage;
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
                Toast.makeText(c, "Time out!", Toast.LENGTH_SHORT).show();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return errorMessage;
    }

	@SuppressWarnings("rawtypes")
	public int checkAuthentication(String un, String pw) {
		int ret = 0;
		try {
			// check is member of valid user group
			String METHOD_NAME = "getUserGroup";
//			String SOAP_ACTION = this.nameSpace + METHOD_NAME;
			String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

			SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);
			request.addProperty("username", un);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);

			HttpTransportSE ht = new HttpTransportSE(this.url);
			ht.call(SOAP_ACTION, envelope);

			Vector response = (Vector) envelope.getResponse();
			int x = Integer.parseInt(response.get(0).toString());
			if (x != 1 && x != 5 && x != 6)
				ret = 9997; // not a viable user for TriagePic
			else {

				// check authentication
				METHOD_NAME = "checkUserAuth";
//				SOAP_ACTION = "https://" + webservice + "checkUserAuth";
				SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

				request = new SoapObject(this.nameSpace, METHOD_NAME);
				request.addProperty("username", un);
				request.addProperty("password", pw);
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				ht = new HttpTransportSE(this.url);
				ht.call(SOAP_ACTION, envelope);

				response = (Vector) envelope.getResponse();

				// return authentication value
				ret = Integer.parseInt((response.get(1).toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

    public Cookie getSessionCookie(final Context c, final String token, String seed){
        this.token = token;

        if (this.nameSpace.isEmpty()){
            Log.e("Error: ", "nameSpace is not defined.");
            return cookie;
        }

        if (this.url.isEmpty()){
            Log.e("Error: ", "url is not defined.");
            return cookie;
        }

        if (this.token.isEmpty()){
            Log.e("Error: ", "token is not defined.");
            return cookie;
        }

        final String METHOD_NAME = "getSessionCookie";
        final String NAME_SPACE = this.nameSpace;
        final String SOAP_ACTION = NAME_SPACE + WebServer.END_POINT + METHOD_NAME;

        // Better to use the threads.
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    errorMessage = toGetSessionCookie();
                }

                public String toGetSessionCookie() {
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    // Register user
                    // Start
                    request.addProperty("token", token.toString());
                    // End

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

//			    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//			        SoapObject result = null;
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try
                    {
                        aht.debug = true;
                        aht.call(SOAP_ACTION, envelope);
                        Log.e("Soap request ", aht.requestDump);
                        Log.e("Soap response ", aht.responseDump);
//                        result = (SoapPrimitive)envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                        errorMessage = e.getMessage().toString();
                        return errorMessage;
                    }

                    SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;

                    cookie.setId(resultRequestSOAP.getPropertyAsString("SESSION_ID").toString());
                    cookie.setKey(resultRequestSOAP.getPropertyAsString("SESS_KEY").toString());
                    errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
                    errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
                    if (!errorCode.contentEquals("0")){
                        Toast.makeText(c, errorMessage, Toast.LENGTH_SHORT).show();;
                    }
                    return errorMessage;
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
                Toast.makeText(c, "Time out!", Toast.LENGTH_SHORT).show();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        if (errorCode.contentEquals("0")){
            return cookie;
        }
        else {
            cookie = new Cookie();
            return cookie;
        }
   }

    // this function is added in api 34
    public SearchResult callSearchCountV34(final Filters filters, final ViewSettings viewSettings, final String eventShort, final Context c){

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    searchResult = searchCountV34(filters, viewSettings, eventShort);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return searchResult;
    }

    // this function is added in api 34
    public SearchResult searchCountV34(final Filters filters, final ViewSettings viewSettings, final String eventShort){
        SearchResult sResult = new SearchResult();
        sResult.toDefault();

        if (this.token.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: token is empty.");
            Log.e("Error: ", "token is empty.");
            return sResult;
        }

        if (this.nameSpace.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: name space is not defined.");
            Log.e("Error: ", "nameSpace is not defined.");
            return sResult;
        }

        if (this.url.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: url is not defined.");
            Log.e("Error: ", "url is not defined.");
            return sResult;
        }

        // define json data
        JSONObject json = new JSONObject();
        try {
            json = filters.toJSON(filters, viewSettings);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }

        String METHOD_NAME = "search";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        // Register user
        // Start
        request.addProperty("token", token.toString());
        request.addProperty("eventShortname", eventShort.toString());
        if (searchCountOnly == true) {
            request.addProperty("query", "");
            request.addProperty("photo", "");
            request.addProperty("filters", json.toString());
            request.addProperty("pageStart", viewSettings.getPageStart());
            request.addProperty("perPage", viewSettings.getPageSize());
            request.addProperty("sortBy", "");
            request.addProperty("countOnly", searchCountOnly);
        }
        else if (viewSettings.getIsImageSearch() == ViewSettings.IMAGE_SEARCH){
            request.addProperty("query", "");
            request.addProperty("photo", viewSettings.getEncodedImage()); // fix the face search problem.
            request.addProperty("filters", json.toString());
            request.addProperty("pageStart", viewSettings.getPageStart());
            request.addProperty("perPage",viewSettings.getPageSize());
            request.addProperty("sortBy", viewSettings.getSortBy().toString());
            request.addProperty("countOnly", searchCountOnly);
        }
        else {
            request.addProperty("query", this.query.toString());
            request.addProperty("photo", "");
            request.addProperty("filters", json.toString());
            request.addProperty("pageStart", viewSettings.getPageStart());
            request.addProperty("perPage",viewSettings.getPageSize());
            request.addProperty("sortBy", "");
            request.addProperty("countOnly", searchCountOnly);
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e2) {
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Exception Error: " + e2.getMessage().toString());
        }

        if (sResult.getErrorCode() == Result.MY_ERROR_CODE){
            return sResult;
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        sResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            sResult.setResultSet(response.get(0).toString());
            sResult.setRecordsFound(response.get(1).toString());
            sResult.setTimeElapsed(response.get(2).toString());
            sResult.setErrorCode(response.get(3).toString());
            sResult.setErrorMessage(response.get(4).toString());
        }
        catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: exception error message " + soapFault.getMessage().toString());
        }

        return sResult;
    }

    public SearchResult callSearchByPID(final String pid, final String eventShort){
        searchResult.toDefault();

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    searchResult = searchByPID(pid, eventShort);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return searchResult;
    }


    public SearchResult searchByPID(final String pid, final String eventShort){
        SearchResult sResult = new SearchResult();
        sResult.toDefault();

        if (this.token.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: token is empty.");
            Log.e("Error: ", "token is empty.");
            return sResult;
        }

        if (this.nameSpace.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: name space is not defined.");
            Log.e("Error: ", "nameSpace is not defined.");
            return sResult;
        }

        if (this.url.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: url is not defined.");
            Log.e("Error: ", "url is not defined.");
            return sResult;
        }

        // define json data
        Filters filters = new Filters();
        filters.setDefaults();

        ViewSettings viewSettings = new ViewSettings();
        viewSettings.SetToDefault();

        JSONObject json = new JSONObject();
        try {
            json = filters.toJSON(filters, viewSettings);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }

        String METHOD_NAME = "search";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        // Register user
        // Start
        request.addProperty("token", token.toString());
        request.addProperty("eventShortname", eventShort.toString());
        request.addProperty("query", "*" + pid);
        request.addProperty("photo", "");
        request.addProperty("filters", json.toString());
        request.addProperty("pageStart", viewSettings.getPageStart());
        request.addProperty("perPage",viewSettings.getPageSize());
        request.addProperty("sortBy", "");
        request.addProperty("countOnly", false);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e2) {
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: exception error message " + e2.getMessage().toString());
        }

        // added in version 9.0.2
        if (sResult.getErrorCode() == Result.MY_ERROR_CODE){
            return sResult;
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        sResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            sResult.setResultSet(response.get(0).toString());
            sResult.setRecordsFound(response.get(1).toString());
            sResult.setTimeElapsed(response.get(2).toString());
            sResult.setErrorCode(response.get(3).toString());
            sResult.setErrorMessage(response.get(4).toString());
        }
        catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: exception error message " + soapFault.getMessage().toString());
        }

        return sResult;
    }

    // this function is added in api 34
    public SearchResult callSearchV34(final Filters filters, final ViewSettings viewSettings, final String eventShort, final Context c){

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    searchResult = searchV34(filters, viewSettings, eventShort);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return searchResult;
    }

    public SearchResult searchV34(final Filters filters, final ViewSettings viewSettings, final String eventShort) {
        SearchResult sResult = new SearchResult();
        sResult.toDefault();

        if (this.token.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: token is empty.");
            Log.e("Error: ", "token is empty.");
            return sResult;
        }

        if (this.nameSpace.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: name space is not defined.");
            Log.e("Error: ", "nameSpace is not defined.");
            return sResult;
        }

        if (this.url.isEmpty()){
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: url is not defined.");
            Log.e("Error: ", "url is not defined.");
            return sResult;
        }

        // define json data
        JSONObject json = new JSONObject();
        try {
            json = filters.toJSON(filters, viewSettings);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }

        String METHOD_NAME = "search";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        // Register user
        // Start
        request.addProperty("token", token.toString());
        request.addProperty("eventShortname", eventShort.toString());
        request.addProperty("query", "Logan"); // test
        request.addProperty("photo", "");
//        request.addProperty("photo", viewSettings.getPhotoSel()); // get all

//        request.addProperty("payload", json.toString());// must be toString(). Otherwise it won't work.
//        request.addProperty("payloadFormat", "JSONPATIENT1"); //

        request.addProperty("filters", json.toString());

        request.addProperty("pageStart", viewSettings.getPageStart());
        request.addProperty("perPage", viewSettings.getPageSize());
        request.addProperty("sortBy", viewSettings.getSortBy().toString());
        request.addProperty("countOnly", true);

        /*
        request.addProperty("pageStart", "0");
        request.addProperty("perPage", "15");
        request.addProperty("sortBy", "");
        request.addProperty("countOnly", false);
        */

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e2) {
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: exception error message " + e2.getMessage().toString());
        }

        // added in version 9.0.2
        if (sResult.getErrorCode() == Result.MY_ERROR_CODE){
            return sResult;
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        sResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            sResult.setResultSet(response.get(0).toString());
            sResult.setRecordsFound(response.get(1).toString());
            sResult.setTimeElapsed(response.get(2).toString());
            sResult.setErrorCode(response.get(3).toString());
            sResult.setErrorMessage(response.get(4).toString());
        }
        catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            sResult.setErrorCode(Result.MY_ERROR_CODE);
            sResult.setErrorMessage("Error: exception error message " + soapFault.getMessage().toString());
        }

        if (sResult.getErrorCode().equalsIgnoreCase("0")){
            if (searchCountOnly == false) {
                JSONParserForPatient(eventShort, sResult.getResultSet());
            }
        }
        return sResult;

    }

    public void JSONParserForPatient(String string, String recordsFoundString) {
        String toParseString = "{" + "\"" + string + "\":" + recordsFoundString + "}";

        patientList = new ArrayList<Patient>();

        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray  jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array
            Log.i("Watch", "jsonArray.length is: " + String.valueOf(jsonArray.length()));

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject

                Patient p = new Patient();

                p.setEvent(event.name);
                p.setHospital(hospital.name);
                p.extractJSON(o, this.nameSpaceImage); // 9.0.3
//                patientList.add(0, p);
                patientList.add(p);
                Log.i(TAG, p.getLastName() + ", " + p.getFirstName() + " has " + p.images.size() + "images.");
                Log.i(TAG, "TriageTrak has " + String.valueOf(patientList.size()) + " members.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string
    }

    public AddCommentResult callAddComment(final String token, final String uuid, final String comment, final String suggestedStatus, final String suggestedLocation, final String suggestedImage){
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    addCommentResult = addComment(token, uuid, comment, suggestedStatus, suggestedLocation, suggestedImage);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return addCommentResult;
    }

    public AddCommentResult addComment(final String token, final String uuid, final String comment, final String suggestedStatus, final String suggestedLocation, final String suggestedImage){
        addCommentResult.toDefault();

        if (this.token.isEmpty()){
            addCommentResult.setErrorCode(Result.MY_ERROR_CODE);
            addCommentResult.setErrorMessage("Error: token is empty.");
            Log.e("Error: ", "token is empty.");
            return addCommentResult;
        }

        if (uuid.isEmpty()){
            addCommentResult.setErrorCode(Result.MY_ERROR_CODE);
            addCommentResult.setErrorMessage("Error: uuid is not defined.");
            Log.e("Error: ", "uuid is not defined.");
            return addCommentResult;
        }

        if (comment.isEmpty() && suggestedStatus.isEmpty() && suggestedLocation.isEmpty() && suggestedImage.isEmpty()){
            addCommentResult.setErrorCode(Result.MY_ERROR_CODE);
            addCommentResult.setErrorMessage("Error: all comment, suggested status and suggested location are not defined.");
            Log.e("Error: ", "all comment, suggested status and suggested location are not defined.");
            return addCommentResult;
        }

        String METHOD_NAME = "addComment";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

       request.addProperty("token", token.toString());
        request.addProperty("uuid", uuid.toString());
        if (comment.isEmpty() == false) {
            request.addProperty("comment", comment.toString());
        }
        if (suggestedStatus.isEmpty() == false) {
            request.addProperty("suggested_status", suggestedStatus.toString());
        }
        if (suggestedLocation.isEmpty() == false){
            request.addProperty("suggested_location", suggestedLocation.toString());
        }
        if (suggestedImage.isEmpty() == false) {
            request.addProperty("suggested_image", suggestedImage.toString());
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e2) {
            addCommentResult.setErrorCode(Result.MY_ERROR_CODE.toString());
            addCommentResult.setErrorMessage("Error: exception error message " + e2.getMessage().toString());
            return  addCommentResult;
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        addCommentResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            addCommentResult.setErrorCode(response.get(0).toString());
            addCommentResult.setErrorMessage(response.get(1).toString());
        }
        catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            addCommentResult.setErrorCode(Result.MY_ERROR_CODE);
            addCommentResult.setErrorMessage("Error: exception error message " + soapFault.getMessage().toString());
        }

        return addCommentResult;
    }

    public ReportAbuseResult callReportAbuse(final String token, final String uuid, final String explanation){
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    reportAbuseResult = reportAbuse(token, uuid, explanation);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                returnString = "Time out!";
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return reportAbuseResult;
    }

    public ReportAbuseResult reportAbuse(final String token, final String uuid, final String explanation){
        reportAbuseResult.toDefault();

        if (this.token.isEmpty()){
            result.setErrorCode(Result.MY_ERROR_CODE);
            result.setErrorMessage("Error: token is empty.");
            Log.e("Error: ", "token is empty.");
            return reportAbuseResult;
        }

        if (uuid.isEmpty()){
            result.setErrorCode(Result.MY_ERROR_CODE);
            result.setErrorMessage("Error: uuid is not defined.");
            Log.e("Error: ", "uuid is not defined.");
            return reportAbuseResult;
        }

        if (explanation.isEmpty()){
            result.setErrorCode(Result.MY_ERROR_CODE);
            result.setErrorMessage("Error: explanation is not defined.");
            Log.e("Error: ", "Explanation is not defined.");
            return reportAbuseResult;
        }

        String METHOD_NAME = "reportAbuse";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        request.addProperty("token", token.toString());
        request.addProperty("uuid", uuid.toString());
        request.addProperty("explanation", explanation.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.e("Soap request ", ht.requestDump);
            Log.e("Soap response ", ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e2) {
            reportAbuseResult.setErrorCode(Result.MY_ERROR_CODE.toString());
            reportAbuseResult.setErrorMessage("Error: exception error message " + e2.getMessage().toString());
            return reportAbuseResult;
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        reportAbuseResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            reportAbuseResult.setErrorCode(response.get(0).toString());
            reportAbuseResult.setErrorMessage(response.get(1).toString());
        }
        catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e("Error in report: ", soapFault.getMessage());
            reportAbuseResult.setErrorCode(Result.MY_ERROR_CODE);
            reportAbuseResult.setErrorMessage("Error: exception error message " + soapFault.getMessage().toString());
        }

        return reportAbuseResult;
    }

    public ArrayList reservePatientIds(){
        ArrayList<Integer> listPatientIDs = new ArrayList<>();


        return listPatientIDs;
    }

    public ReservePIDResult callReservePatientIDs(final String token, final String hospital_uuid){
        reservePIDResult = new ReservePIDResult();
        ArrayList<Integer> listPatientIDs = new ArrayList<>();

        final String METHOD_NAME = "reservePatientIds";
        final String soapAction = this.nameSpace + WebServer.END_POINT + METHOD_NAME;

        if (token.isEmpty()) {
            String msg = "Token is not defined.";
            Log.e("Error: ", msg);
            reservePIDResult.setErrorCode(Result.MY_ERROR_CODE);
            reservePIDResult.setErrorMessage(msg);
            return reservePIDResult;
        }
        if (hospital_uuid.isEmpty()) {
            String msg = "Hospital UUID is not defined.";
            Log.e("Error: ", msg);
            reservePIDResult.setErrorCode(Result.MY_ERROR_CODE);
            reservePIDResult.setErrorMessage(msg);
            return reservePIDResult;
        }

        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    requestUserToken();
                }

                private void requestUserToken() {
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    // Register user
                    // Start
                    request.addProperty("token", token.toString());
                    request.addProperty("hospital_uuid", hospital_uuid.toString());
                    // End

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

//			    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//			        SoapObject result = null;
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try {
                        aht.debug = true;
                        aht.call(soapAction, envelope);
                        Log.e("Soap request ", aht.requestDump);
                        Log.e("Soap response ", aht.responseDump);
//                        result = (SoapPrimitive) envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                    }

                    SoapObject resultRequestSOAP = (SoapObject) envelope.bodyIn;
                    reservePIDResult.setErrorCode(resultRequestSOAP.getProperty(1).toString());
                    reservePIDResult.setErrorMessage(resultRequestSOAP.getProperty(2).toString());
                    if (reservePIDResult.getErrorCode().equalsIgnoreCase("0")){
                        String pidList = resultRequestSOAP.getProperty(0).toString();
                        String delimar = "[\",]+";
                        String tokens[] = pidList.split(delimar);
                        for (int i = 0; i < tokens.length; i++){
                            if (tokens[i].length() > 4){
                                reservePIDResult.getReservePIDs().add(tokens[i]);
                            }
                        }
                    }
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures) {
            String msg = "";
            try {
                f.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                msg = e.getMessage();
                reservePIDResult.setErrorCode(Result.MY_ERROR_CODE);
                reservePIDResult.setErrorMessage(msg);
            } catch (ExecutionException e) {
                e.printStackTrace();
                msg = e.getMessage();
                reservePIDResult.setErrorCode(Result.MY_ERROR_CODE);
                reservePIDResult.setErrorMessage(msg);
            } catch (TimeoutException e) {
                e.printStackTrace();
                msg = e.getMessage();
                reservePIDResult.setErrorCode(Result.MY_ERROR_CODE);
                reservePIDResult.setErrorMessage(msg);
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return reservePIDResult;
    }
}