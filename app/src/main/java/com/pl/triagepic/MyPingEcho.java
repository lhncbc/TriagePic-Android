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

import android.os.Build;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by on 4/3/14.
 */
public class MyPingEcho {
//    private static final String SOAP_ACTION = "https://pl.nlm.nih.gov/?wsdl#pingEcho";
    private static final String METHOD_NAME = "pingEcho";
//    private static final String NAMESPACE = "http://pl.nlm.nih.gov/soap/plusWebServices/";
//    private static final String URL = "https://pl.nlm.nih.gov/?wsdl&api=34"; // 9.0.0

    public static final String ERR_MSG = "WARNING!\nNo network connection. The app is operating with limited features.";
    public static final String TIME_OUT = "Time Out";
    private String returnString = "";

    TriagePic app;
    private long timeStart = 0;
    private long timeEnd = 0;
    private long timeElapsed = 0;

    private String soapAction = "";
    private String methodName = "";
    private String nameSpace = "";
    private String url = "";
    private String endPoint = "";

    private String username = "";
    private String token = "";
    private String pingString = ""; //machine name;device id;app name;app version;operating system;device username;pl username
    private String latency = "-1";

    MyPingEcho(){
        methodName = METHOD_NAME;
        nameSpace = WebServer.TT_NAMESPACE;
        url = WebServer.TT_URL;
        soapAction = nameSpace + WebServer.END_POINT + methodName;
        token = "";
    }

    MyPingEcho(WebServer w){
        methodName = METHOD_NAME;
        nameSpace = w.getNameSpace();
        url = w.getUrl();
        soapAction = nameSpace + WebServer.END_POINT + methodName;
        token = w.getToken();
    }

    public void definePingString(){
        String s = "Machine Name " + getManufacturer();
        s += "; Build" + Build.ID;
        s += "; APP " + "TriagePic®";
        s += "; Version " + "6.0";
        s += "; Android " + android.os.Build.VERSION.RELEASE;
        s += "; Kernel Version " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "; Username " + username;
        pingString = s;
    }

    public String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        manufacturer = manufacturer.toUpperCase();
        String model = Build.MODEL;
        model = model.toString();
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public String verify(){
        if (soapAction.isEmpty()){
            return "no soapAction";
        }
        if (methodName.isEmpty()){
            return "no methodName";
        }
        if (nameSpace.isEmpty()){
            return "no nameSpace";
        }
        if (url.isEmpty()){
            return "no url";
        }
        if (token.isEmpty()){
            return "no token";
        }
        if (pingString.isEmpty()){
            return "no pingString";
        }
        if (latency.isEmpty()){
            return "no latency";
        }
        return "";
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public String getNameSpace(){
        return this.nameSpace;
    }
    public void setNameSpace(String nameSpace){
        this.nameSpace = nameSpace;
    }

    public String getMethodName(){
        return this.methodName;
    }
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }

    public String getSoapAction(){
        return this.soapAction;
    }
    public void setSoapAction(String soapAction){
        this.soapAction = soapAction;
    }

    public String getLatency(){
        return this.latency;
    }
    public void setLatency(String latency){
        this.latency = latency;
    }
    public String getPingString(){
        return this.pingString;
    }
    public void setPingString(String pingString){
        this.pingString = pingString;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){this.username = username;}
    public String getToken(){return this.token;}
    public void setToken(String token){
        this.token = token;
    }

    public String getLatencyTime(){
        return latency;
    }

    public String Call() {
        definePingString();
        verify();

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    timeStart = System.currentTimeMillis();
                    PingEcho();
                    timeEnd = System.currentTimeMillis();
                    timeElapsed = timeEnd - timeStart;
                    latency = String.valueOf(timeElapsed);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(10, TimeUnit.SECONDS);
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

    private void PingEcho() {
        SoapObject request = new SoapObject(nameSpace, methodName);
        request.addProperty("token", this.token);
        request.addProperty("pingString", this.pingString);
        request.addProperty("latency", this.latency);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        // Either will do.
        // But when you use ksoap2 version 2.6.4, HttpTransportSE is the must.
//    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
        HttpTransportSE aht = new HttpTransportSE(url);
        aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try
        {
            aht.debug = true;
            aht.call(soapAction, envelope);
            Log.e("Soap request ", aht.requestDump);
            Log.e("Soap responce ", aht.responseDump);
            result = (SoapPrimitive)envelope.getResponse();
        } catch (Exception e) {
            result = null;
        }

        SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
        String errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
        String errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
        String timeString = resultRequestSOAP.getPropertyAsString("time");
        returnString = "Time: \r\n" + timeString;
    }
}
