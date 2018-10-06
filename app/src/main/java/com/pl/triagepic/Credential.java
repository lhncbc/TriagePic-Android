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
import android.content.Context;
import android.content.SharedPreferences;

public class Credential {
    public static final int USING_MANUAL_INPUT = 1;
    public static final int USING_BARCODE_SCANNER = 2;
    public static final int USING_AUTO = 3;

    private boolean bAuthStatus = false;
    private String username = TriagePic.GUEST;
    private String password = "";
    private String token = "";
    private String webServer = WebServer.TT_NAME;
    private long webServerId = 0;
    private long hospitalId = -1;
    private long eventId = -1;
    private String curSelEvent = "";
    private String curSelEventShortName = "";
    private String curSelHospital = "";
    private Activity a;
    private int pidInputTool;

    public Credential(Activity a){
        this.a = a;
        reset();// initial
        getAuthStatus();
        getUsernamePreferences();
        getPasswordPreferences();
        getTokenPreferences();
        getAuthStatus();
        getWebServer();
        getWebServerIdPreferences();
        getHospitalIdPreferences();
        getCurSelHospitalPreferences();
        getEventIdPreferences();
        getCurSelEventPreferences();
        getCurSelEventShortNamePreferences();
    }

    public void cleanAll(){
        reset();
        saveAllUserPreferences();
    }

    public void saveAllUserPreferences() {
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("bAuthStatus", bAuthStatus);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.putString("webServer", webServer);
        editor.putLong("webServerId", webServerId);
        editor.putLong("hospitalId", hospitalId);
        editor.putLong("eventId", eventId);
        editor.putString("curSelEvent", curSelEvent);
        editor.putString("curSelEventShortName", curSelEventShortName);
        editor.putString("curSelHospital", curSelHospital);
        editor.commit();
    }

    public int getPidInputTool(){return this.pidInputTool;}
    public void setPidInputTool(int pidInputTool){this.pidInputTool = pidInputTool;}
    public int getPidInputToolReference(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.pidInputTool = sharedPreferences.getInt("pidInputTool", Credential.USING_BARCODE_SCANNER);
        return this.pidInputTool;
    }
    public void savePidInputToolPreferences(int pidInputTool) {
        this.pidInputTool = pidInputTool;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pidInputTool", pidInputTool);
        editor.commit();
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getToken(){
        return token;
    }

    public boolean getAuthStatus(){
// 			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true && token.isEmpty()){
// 				bAuthStatus = false;
// 			}
// 			else {
// 				bAuthStatus = true;
// 			}
        return bAuthStatus;
    }

    public String getWebServer(){
        return webServer;
    }

    public long getWebServerId() {return webServerId;}

    public long getHospitalId() {return hospitalId;}
    public long getEventId() {return eventId;}

    public void reset() {
        bAuthStatus = false;
        username = TriagePic.GUEST;
        password = "";
        token = "";
// 			saveUserPreferences(username, password);
        webServer = WebServer.TT_NAME;
        webServerId = 0;
        hospitalId = -1;
        eventId = -1;
        hospitalId = -1;
// 			saveWebServerPreferences(webServer);
        curSelEvent = "";
        curSelEventShortName = "";
        curSelHospital = "";
        pidInputTool = Credential.USING_BARCODE_SCANNER;
    }

    public boolean verifyAuthStatus(){
        boolean bResult = false;
        if (bAuthStatus == false){
            bResult = false;
        }
        else if (username.equalsIgnoreCase(TriagePic.GUEST) == true){
            bResult = false;
        }
        else if (password.isEmpty() == true){
            bResult = false;
        }
        else if (token.isEmpty() == true){
            bResult = false;
        }
        else {
// 				if (CheckUserAuth() == true){
// 					bResult = true;
// 				}
        }
        return bResult;
    }
    public void saveUserPreferences(String username, String password, String token){
        this.username = username;
        this.password = password;
        this.token = token;
/*
 			String encrapedUsername;
 			if (this.username.isEmpty() == true){
 				encrapedUsername = null;
 			}
 			else {
 				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
 		    }

 			String encrapedPassword;
 			if (this.password.isEmpty() == true){
 				encrapedPassword = null;
 			}
 			else {
 				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
 		    }
 		    */

        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.commit();
    }

    public void saveAuthStatusPreferences(boolean bAuthStatus){
        this.bAuthStatus = bAuthStatus;

        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("bAuthStatus", bAuthStatus);
        editor.commit();
    }

    public void saveUsernamePreferences(String username){
        this.username = username;

            /*
 			String encrapedUsername;
 			if (this.username.isEmpty() == true){
 				encrapedUsername = null;
 			}
 			else {
 				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
 		    }
 		    */

        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.commit();
    }

    public void savePasswordPreferences(String password){
        this.password = password;
/*
 			String encrapedPassword;
 			if (this.password.isEmpty() == true){
 				encrapedPassword = null;
 			}
 			else {
 				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
 		    }
 		    */

        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public void saveTokenPreferences(String token){
        this.token = token;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public void saveWebServerPreferences(String webServer){
        this.webServer = webServer;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("webServer", this.webServer);
        editor.commit();
    }

    public void saveWebServerIdPreferences(long webServerId){
        this.webServerId = webServerId;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("webServerId", this.webServerId);
        editor.commit();
    }

    public void saveHospitalIdPreferences(long hospitalId){
        this.hospitalId = hospitalId;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("hospitalId", this.hospitalId);
        editor.commit();
    }

    public void saveEventIdPreferences(long eventId){
        this.eventId = eventId;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("eventId", this.eventId);
        editor.commit();
    }

    public void saveCurSelEventPreferences(String curSelEvent){
        this.curSelEvent = curSelEvent;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("curSelEvent", this.curSelEvent);
        editor.commit();
    }

    public void saveCurSelEventShortNamePreferences(String curSelEventShortName){
        this.curSelEventShortName = curSelEventShortName;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("curSelEventShortName", this.curSelEventShortName);
        editor.commit();
    }

    public void saveCurSelHospitalPreferences(String curSelHospital){
        this.curSelHospital = curSelHospital;
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("curSelHospital", this.curSelHospital);
        editor.commit();
    }

    public boolean getAuthStatusPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.bAuthStatus = sharedPreferences.getBoolean("bAuthStatus", false);
        return this.bAuthStatus;
    }

    public String getUsernamePreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.username = sharedPreferences.getString("username", "guest");
            /*
 			String encryptedUsername = sharedPreferences.getString("username", "guest");
 			if (encryptedUsername.equalsIgnoreCase("Guest") == true){
 				this.username = new String("Guest");
 			}
 			else {
 				this.username = new String(Base64.decode(encryptedUsername, Base64.DEFAULT ) );
 			}
 			*/
        return this.username;
    }

    public String getPasswordPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.password = sharedPreferences.getString("password", "");

            /*
            String encryptedPassword = sharedPreferences.getString("password", "");
 			if (encryptedPassword.isEmpty() == true){
 				this.password = new String("");
 			}
 			else {
 				this.password = new String(Base64.decode(encryptedPassword, Base64.DEFAULT ) );
 			}
 			*/
        return this.password;
    }

    public String getTokenPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.token = sharedPreferences.getString("token", "");
        return this.token;
    }

    public String getWebServerPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.webServer = sharedPreferences.getString("webServer", WebServer.TT_NAME);
        return this.webServer;
    }

    public long getWebServerIdPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.webServerId = sharedPreferences.getLong("webServerId", 0);
        return this.webServerId;
    }

    public long getHospitalIdPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.hospitalId = sharedPreferences.getLong("hospitalId", 0);
        return this.hospitalId;
    }

    public long getEventIdPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.eventId = sharedPreferences.getLong("eventId", 0);
        return this.eventId;
    }

    public String getCurSelEventPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.curSelEvent = sharedPreferences.getString("curSelEvent", "");
        return this.curSelEvent;
    }

    public String getCurSelEventShortNamePreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.curSelEventShortName = sharedPreferences.getString("curSelEventShortName", "");
        return this.curSelEventShortName;
    }

    public String getCurSelHospitalPreferences(){
        SharedPreferences sharedPreferences = a.getPreferences(Context.MODE_PRIVATE);
        this.curSelHospital = sharedPreferences.getString("curSelHospital", "");
        return this.curSelHospital;
    }
}

