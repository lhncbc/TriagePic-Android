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
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class TriagePic extends Application{
    private static TriagePic instance;
    private static String TAG = TriagePic.class.getName();
    public Waiter waiter;

	public static final boolean QUICK_START_DEFAULT = false;

    public static final String GUEST = "Guest";

    public static final int TOKEN_UNKNOWN = -1;
    public static final int TOKEN_AUTH = 1;
    public static final int TOKEN_ANONYMOUS = 2;

    // global settings - start
    public static final boolean RUN_ON_TABLET_ONLY = false; // true for tablet only, false for cell phone
    // global settings - end

    private boolean isTablet;
    private boolean authStatus;
    private long authId;
	private String username;
	private String password;
    private String token;
    private String tokenAnonymous;
    private int tokenStatus;
    private boolean isAnonymous;
	private long webServerId;
//	private String webServerName;	
	private boolean quickStart;
    private boolean developer;
	private boolean offLineMode;
	private String seed;

    private String curEncodedImage;
    public String getCurEncodedImage(){return this.curEncodedImage;}
    public void setCurEncodedImage(String curEncodedImage){this.curEncodedImage = curEncodedImage;}

    private Filters filters;
    public Filters getFilters() {
        return filters;
    }
    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    private ViewSettings viewSettings;
    public ViewSettings getViewSettings() {
        return viewSettings;
    }
    public void setViewSettings(ViewSettings viewSettings) {
        this.viewSettings = viewSettings;
    }

    private boolean isImageEncrypted;
    public void setIsImageEncrypted(boolean isImageEncrypted){
        this.isImageEncrypted = isImageEncrypted;
    }
    public boolean getIsImageEncrypted(){
        return isImageEncrypted;
    }

    private String curSelEvent;
    public String getCurSelEvent(){
        return this.curSelEvent;
    }
    public void setCurSelEvent(String curSelEvent){
        this.curSelEvent = curSelEvent;
    }

    private String curSelEventShortName;
    public String getCurSelEventShortName(){
        return this.curSelEventShortName;
    }
    public void setCurSelEventShortName(String curSelEventShortName){
        this.curSelEventShortName = curSelEventShortName;
    }

    // it is incident ID
    private long curSelEventId;
    public long getCurSelEventId(){
        return this.curSelEventId;
    }
    public void setCurSelEventId(long curSelEventId){
        this.curSelEventId = curSelEventId;
    }

    private WebServer curSelWebServer;
    public void setCurSelWebServer(WebServer ws){
        this.curSelWebServer = ws;
    }
    public WebServer getCurSelWebServer(){
        return this.curSelWebServer;
    }

	private String curSelHospital;
	public String getCurSelHospital(){
		return this.curSelHospital;
	}
	public void setCurSelHospital(String curSelHospital){
		this.curSelHospital = curSelHospital;
	}

    private String curSelHospitalShortName;
    public String getCurSelHospitalShortName(){
        return this.curSelHospitalShortName;
    }
    public void setCurSelHospitalShortName(String curSelHospitalShortName){
        this.curSelHospitalShortName = curSelHospitalShortName;
    }

    private long curSelHospitalId;
    public long getCurSelHospitalId() {return curSelHospitalId;}
    public void setCurSelHospitalId(long curSelHospitalId){this.curSelHospitalId = curSelHospitalId;}

	public String getSeed(){
		return this.seed;
	}
	public void setSeed(String seed){
		this.seed = seed;
	}
	
	private Uri curUri;
	public Uri getCurUri(){
		return this.curUri;
	}
	public void setCurUri(Uri curUri){
		this.curUri = curUri;
	}
	
	private Intent cameraIntent;
	public Intent getCameraIntent(){
		return this.cameraIntent;
	}
	public void setCameraIntent(Intent cameraIntent){
		this.cameraIntent = cameraIntent;
	}

    private ArrayList<String> reservePIDs;
    public ArrayList<String> getReservePIDs() {
        return reservePIDs;
    }
    public void setReservePIDs(ArrayList<String> reservePIDs) {
        this.reservePIDs = reservePIDs;
    }

    public TriagePic(String username, String password, String token, String tokenAnonymous, int tokenStatus, long webServerId, boolean quickStart, String develop){
        super();

		if (password.isEmpty() == true){
			this.authStatus = false;
			this.webServerId = -1;
		}
		else if (username.equalsIgnoreCase("Guest") == true){
			this.authStatus = false;
			this.webServerId = -1;
		}
		else {
			this.authStatus = true;
			this.webServerId = webServerId;
		}
		this.authId = -1;
		this.username = username;
		this.password = password;
//		this.webServerName = webServerName;
		this.quickStart = quickStart;
        this.developer = developer;
		this.offLineMode = true;

        this.token = token;
        this.tokenAnonymous = tokenAnonymous;
        this.tokenStatus = tokenStatus;

        this.isTablet = false;

        this.curSearchCount = "";

        this.reservePIDs.clear();
	}
/*
	public TriagePic(String username, String password, String webServer, boolean quickStart){
		super();
		if (password.isEmpty() == true){
			this.authStatus = false;
		}
		else if (username.equalsIgnoreCase("Guest") == true){
			this.authStatus = false;
		}
		else {
			this.authStatus = true;
		}
		this.authId = -1;
		this.username = username;
		this.password = password;
		this.webServerName = webServerName;
		this.quickStart = quickStart;
		this.offLineMode = true;
	}
*/	
	public TriagePic(){
		super();
		this.authId = -1;
		this.username = "Guest";
		this.password = "";
        this.token = "";
        this.tokenAnonymous = "";
        this.tokenStatus = TOKEN_UNKNOWN;
		this.webServerId = -1;
		this.quickStart = false;
        this.developer = false;
		this.offLineMode = true;
        this.isTablet = false;
        this.curSelWebServer = null;

        this.curSearchCount = "";
        this.reservePIDs = new ArrayList<String>();
	}
	
	public TriagePic(TriagePic t){
		super();
		this.authId = t.getAuthId();
		this.username = t.getUsername();
        this.password = t.getPassword();
        this.token = t.getToken();
        this.tokenAnonymous = t.getTokenAnonymous();
        this.tokenStatus = t.getTokenStatus();
		this.webServerId = t.getWebServerId();
		this.quickStart = t.getQuickStart();
        this.developer = t.getDeveloper();
		this.offLineMode = t.getOffLineMode();
        this.isTablet = t.isTablet();
        this.curSelWebServer = t.getCurSelWebServer();

        this.setCurSearchCount(t.getCurSearchCount());
        this.setReservePIDs(t.getReservePIDs());
	}


	public void setAuthId(long authId){
		this.authId = authId;
	}
	public long getAuthId() {
		return this.authId;
	}

	public long getWebServerId(){
		return webServerId;
	}
	public void setWebServerId(long webServerId){
		this.webServerId = webServerId;
	}

	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
		this.password = password;
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

    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

	public boolean getQuickStart(){
		return quickStart;
	}
	public void setQuickStart(boolean quickStart){
		this.quickStart = quickStart;
	}

	public boolean getAuthStatus(){
		return authStatus;
	}
	public void setAuthStatus(boolean authStatus){
		this.authStatus = authStatus;
	}
	
	public boolean getOffLineMode(){
		return this.offLineMode;
	}
	public void setOffLineMode(boolean offLineMode){
		this.offLineMode = offLineMode;
	}

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "Starting application"+this.toString());
        waiter = new Waiter(Waiter.FIFTEEN_MUNITES); // will be 15 mins 15 * 60 * 1000
        waiter.start();
    }

    public void exit() {
        exit();
    }

    public void touch()
    {
        waiter.touch();
    }

    public boolean detectMobileDevice(Context context){
        isTablet = false;
        if ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            isTablet = true;
        }
        return isTablet;
    }

    public void setIsTablet(final boolean b){
        isTablet = b;
    }

    public boolean isTablet(){
        return isTablet;
    }

    public void setScreenOrientation(Activity c){
        int currentOrientation = c.getResources().getConfiguration().orientation;
        // cell phone
        if (isTablet == false){
            if (currentOrientation == Configuration.ORIENTATION_UNDEFINED ||currentOrientation == Configuration.ORIENTATION_LANDSCAPE  ){
                c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        // tablet
        else {
            if (currentOrientation == Configuration.ORIENTATION_UNDEFINED || currentOrientation == Configuration.ORIENTATION_PORTRAIT){
                c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    public static synchronized TriagePic getInstance(){
        if(instance==null){
            instance=new TriagePic();
        }
        return instance;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    private String totalCount = "";

    private String curSearchCount = "";
    public String getCurSearchCount() {
        return curSearchCount;
    }
    public void setCurSearchCount(String curSearchCount) {
        this.curSearchCount = curSearchCount;
    }

    public boolean getDeveloper() {
        return developer;
    }
    public void setDeveloper(boolean developer) {
        this.developer = developer;
    }
}
