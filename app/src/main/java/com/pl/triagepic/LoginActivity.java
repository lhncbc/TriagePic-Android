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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {    private static final int WEB_SERVER_SELECTION = 1;
    private static final int PICK_EVENT_REQUEST = 2;
    private static final int PICK_HOSPITAL_REQUEST = 3;
   
    TriagePic app;
    String webServerName = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    String soapActionChechAuth = "";
    WebServer ws;
    
    String currentUsername = "Guest";
    String currentPassword = "";
    String currentToken = "";
    String username = "";
 	String password = "";
    String token = "";
	
	String valid;
	String errorCode;
	String errorMessage;
	String returnString = "";
	
	Button buttonSelectWebServer;
//	Button buttonSelectEvent;
//	Button buttonSelectHospital;
//	Button buttonQuitAndReturn;
//	Button buttonOffLineMode;
//	Button buttonTestWebServer;
    Button buttonCancel;
	Button buttonLogin;
	CheckBox ckShow;

	EditText etUsername;
	EditText etPassword;
	TextView textViewWebServerName;
//	TextView textViewEvent;
//	TextView textViewHospital;

    ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        app = ((TriagePic)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        if (isLogin() == false){
            Initialize();
        }
        else {
            Toast.makeText(this, "You are already login.", Toast.LENGTH_SHORT).show();
            finish();
        }
	}

    public boolean isLogin(){
        boolean result = false;
        String u = app.getUsername();
        String p = app.getPassword();
        boolean a = app.getAuthStatus();

        if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST)){
            // return false
            if (a == true){
                app.setAuthStatus(false);
            }
            result = false;
        }
        else {
            // return true
            if (a == false){
                app.setAuthStatus(true);
            }
            result = true;
        }
        return result;
    }

	private void Initialize() {
		ws = new WebServer();
		if (app.getWebServerId() != -1){
			ws.setId(app.getWebServerId());
			
			DataSource s = new DataSource(this, app.getSeed());
			s.open();
			ws = s.getWebServerFromId(ws.getId());
			s.close();
		}

        if (ws != null){
            isLogin(this);
        }

		buttonSelectWebServer = (Button)findViewById(R.id.buttonSelectWebServer);
//		buttonSelectEvent = (Button)findViewById(R.id.buttonSelectEvent);
//		buttonSelectHospital = (Button)findViewById(R.id.buttonSelectHospital);
		
//		buttonQuitAndReturn = (Button)findViewById(R.id.buttonQuitAndReturn);
//		buttonOffLineMode = (Button)findViewById(R.id.buttonOffLineMode);
//		buttonTestWebServer = (Button)findViewById(R.id.buttonTestWebServer);
        buttonCancel = (Button)findViewById(R.id.buttonCancel);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

		ckShow = (CheckBox)findViewById(R.id.ckShow);
		
		etUsername = (EditText)findViewById(R.id.editUsername); 
		etPassword = (EditText)findViewById(R.id.editPassword);
		
		textViewWebServerName = (TextView)findViewById(R.id.textViewWebServerName);
//		textViewEvent = (TextView)findViewById(R.id.textViewEvent);
//		textViewHospital = (TextView)findViewById(R.id.textViewHospital);

		buttonSelectWebServer.setOnClickListener(this);
//		buttonSelectEvent.setOnClickListener(this);
//		buttonSelectHospital.setOnClickListener(this);
		
//		buttonQuitAndReturn.setOnClickListener(this);
//		buttonOffLineMode.setOnClickListener(this);
//		buttonTestWebServer.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

		ckShow.setOnClickListener(this);

		if (app.getUsername().isEmpty() == false && app.getUsername().equalsIgnoreCase(TriagePic.GUEST) == false){
			etUsername.setText(app.getUsername());
		}
		if (ws == null){
			textViewWebServerName.setText("");
		}
        else if (app.getWebServerId() == -1){
            textViewWebServerName.setText("");
        }
		else {
			textViewWebServerName.setText(ws.getName());
            ws.setId(app.getWebServerId());
		}
		
//		textViewEvent.setText(app.getCurSelEvent());
//		textViewHospital.setText(app.getCurSelHospital());
//		textViewEvent.setText(app.getCurSelEvent());
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonSelectWebServer:
			selectWebServer();
			break;
        case R.id.buttonCancel:
            cancel();
            break;
        case R.id.buttonLogin:
            login();
            break;
		case R.id.ckShow:
			ShowPassword();
			break;
		default:
				break;
		}
	}

    private void cancel() {
        if (app.getAuthStatus() == false){
            logout();
        }
        else {
            super.onBackPressed();
            LoginActivity.this.finish();
        }
    }

    private void forgotUsername() {
        String serverName = textViewWebServerName.getText().toString();
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            String msg = "The web server is not selected.";
            builder.setMessage(msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Intent i = new Intent(this, ForgotUsernameActivity.class);
            i.putExtra("webServer", textViewWebServerName.getText());
            startActivity(i);
        }
    }

    private void forgotPassword() {
        String serverName = textViewWebServerName.getText().toString();
        /*
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            String msg = "The web server is not selected.";
            builder.setMessage(msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
        */
            Intent i = new Intent(this, ForgotPasswordActivity.class);
            i.putExtra("webServer", serverName.toString());
            startActivity(i);
//        }
    }

    private void registerNewUser() {
        String serverName = textViewWebServerName.getText().toString();
        /*
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            String msg = "The web server is not selected.";
            builder.setMessage(msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        */
//        else {
            Intent i = new Intent(this, RegisterActivity.class);
            i.putExtra("webServer", serverName.toString());
            startActivity(i);
//        }
    }

    private void forgotUsernamePassword() {
        forgotPassword();
    }

	private void selectWebServer() {
//        Intent i = new Intent(LoginActivity.this, WebServerActivity.class);
//        startActivityForResult(i, WEB_SERVER_SELECTION);

        Intent i = new Intent(LoginActivity.this, WebServerListFragmentActivity.class);
        startActivityForResult(i, WEB_SERVER_SELECTION);
	}

	private void testWebServer() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

		String serverName = textViewWebServerName.getText().toString();
		if (serverName.isEmpty()){
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			String msg = "The web server is not selected.";
			builder.setMessage(msg)
				   .setIcon(android.R.drawable.ic_dialog_alert)
			       .setCancelable(true)
			       .setTitle("Error")
			       .setNegativeButton("Close", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                return;
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();
		}
		else {
			Intent i = new Intent(LoginActivity.this, LatencyActivity.class);
			i.putExtra("webServer", textViewWebServerName.getText());
			startActivity(i);
		}
	}

	private void offLineMode() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
		
		if (isLogin(LoginActivity.this) == false){
			Toast.makeText(LoginActivity.this, "You are already off line.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setMessage("You are selecting to work off line.")
			   .setTitle("Are you sure?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		       			app.setUsername(TriagePic.GUEST);
		       			app.setPassword("");
		       			app.setAuthStatus(false);
		    			DataSource s = new DataSource(LoginActivity.this, app.getSeed());
		    			s.open();
		    			long authId = s.getAuthId();
		    			s.updateUsernamePassword(TriagePic.GUEST, "", "", -1, -1, -1, authId);
		    			s.close();
		       			
		       			Toast.makeText(LoginActivity.this, "You are now off line.", Toast.LENGTH_SHORT).show();
		       			LoginActivity.this.finish();
		            }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void quitAndReturn() {
		this.finish();
	}

	private void ShowPassword() {

		if (ckShow.isChecked() == true){
			etPassword.setTransformationMethod(null);
		}
		else {
			etPassword.setTransformationMethod(new PasswordTransformationMethod());			
		}
	}

	private void Cancel() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
		
	}

	private void login() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
		mgr.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
		
		if (isLogin(LoginActivity.this) == true){
			Toast.makeText(LoginActivity.this, "You are already logged in.", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
        String returnMsg = VerifyEntry();
		if (returnMsg.isEmpty() == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(returnMsg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    })
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            LoginActivity.this.finish();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
		}
        else {
            ws.setErrorMessage("");
            ws.callRequestUserToken(this, etUsername.getText().toString(), etPassword.getText().toString());
            if (ws.getErrorCode().equalsIgnoreCase("0")){
                String u = etUsername.getText().toString();
                String p = etPassword.getText().toString();
                String t = ws.getToken();
                app.setAuthStatus(true);
                app.setUsername(u);
                app.setPassword(p);
                app.setToken(t);
                app.setTokenStatus(TriagePic.TOKEN_AUTH);
                app.setWebServerId(ws.getId());
                DataSource s = new DataSource(this, app.getSeed());
                s.open();
                long authId = s.getAuthId();
                s.updateUsernamePassword(u, p, t, (int)ws.getId(), -1, -1, authId);
                s.close();
                String msg = "Welcome \"" + app.getUsername() + "\"!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                this.finish();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(ws.getErrorMessage())
                        .setCancelable(true)
                        .setTitle("Error")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == WEB_SERVER_SELECTION){
            TriagePic t = TriagePic.getInstance();
            app.setWebServerId(t.getWebServerId());
            Initialize();
		}
		else if (requestCode == PICK_EVENT_REQUEST){
            Initialize();
		}
		else if (requestCode == PICK_HOSPITAL_REQUEST) {
            Initialize();
		}
		
	}

	@Override
	public void onBackPressed() {
		if (app.getAuthStatus() == false){
			logout();
		}
		else {
			super.onBackPressed();
            LoginActivity.this.finish();
		}
	}

	private void logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setMessage("You selected not login. You are about to exit the program.")
			   .setTitle("Are you sure?")
		       .setCancelable(false)
			   .setIcon(android.R.drawable.ic_dialog_alert)
		       .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                       LoginActivity.this.finish();
                       return;
                   }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                return;
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void AreYouSureToExit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setMessage("Login was successful")
			   .setTitle(currentUsername)
		       .setCancelable(false)
			   .setIcon(android.R.drawable.ic_dialog_alert)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
	        			Intent backData = new Intent();
	        			backData.putExtra("username", currentUsername);
                        backData.putExtra("password", currentPassword);
                        backData.putExtra("token", currentToken);

	        			// Save to cridential 
	        			SaveToCredentialData(currentUsername, currentPassword, currentToken);

	        			setResult(LoginActivity.this.RESULT_CANCELED, backData);
	        			LoginActivity.this.finish();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

    protected void SaveToCredentialData(String currentUsername, String currentPassword, String currentToken) {
		app = ((TriagePic)this.getApplication());
		app.setUsername(currentUsername);
        app.setPassword(currentPassword);
        app.setToken(currentToken);
        app.setAuthStatus(true);

		Credential credential = new Credential();
		credential.setUsernamePreferences(currentUsername);
        credential.setPasswordPreferences(currentPassword);
        credential.setTokenPreferences(currentToken);
	}

	public String VerifyEntry() {
        String result = "";
		if (textViewWebServerName.getText().length() == 0) {
            result = "Web Server is not defined!";
			return result;
		}
/*
		if (textViewHospital.getText().length() == 0) {
			result = "Hospital is not defined!";
			return result;
		}
		
		if (textViewEvent.getText().length() == 0) {
			result = "Event is not defined!";
			return result;
		}
*/
		if (etUsername.getText().length() == 0) {
			result = "Username is empty!";
			return result;
		}
		
		String u = etUsername.getText().toString();
		if (u.equalsIgnoreCase(TriagePic.GUEST)) {
			result = "\"Guest\" cannot be uername!";
			return result;
		}

        String s = etPassword.getText().toString();
		if (etPassword.getText().length() == 0) {
			result = "Password is empty!";
			return result;
		}
		return result;
	}

	private void CleanEntry() {

		etUsername.setText("");
		etPassword.setText("");		
	}

  public class Credential {
        public static final String GUEST = "Guest";

        private boolean bAuthStatus = false;
		private String username = GUEST;
		private String password = "";
        private String token = "";
		private String webServer = WebServer.TT_NAME;
		private boolean quickStart = false;
		
		Credential(){
			reset();// initial
			getUsernamePreferences();
			getPasswordPreferences();
            getTokenPreferences();
			getAuthStatus();
			getWebServer();
			getQuickStartPreferences();
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
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true && token.isEmpty() == true){
				bAuthStatus = false;
			}
			else {
				bAuthStatus = true;				
			}
			return bAuthStatus;
		}
		
		public boolean getQuickStart(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true && token.isEmpty() == true){
				quickStart = false;
			}
			else {
				quickStart = true;				
			}
			return quickStart;			
		}
		
		public String getWebServer(){
			return webServer;
		}

		public void reset() {
			bAuthStatus = false;
			username = GUEST;
			password = "";
            token = "";
//			saveUserPreferences(username, password);
			ws.setName(WebServer.TT_NAME);
			ws.setNameSpace(WebServer.TT_NAMESPACE);
			ws.setShortName(WebServer.TT_SHORT_NAME);
			ws.setUrl(WebServer.TT_URL);
			ws.setWebService(WebServer.TT_WEB_SERVICE);
			quickStart = false;
		}
		
		private void saveQuickStartPreferences(boolean quickStart) {
			this.quickStart = quickStart;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("quickStart", this.quickStart);
			editor.commit();
		}

		private void saveWebServerPreferences(String webServer) {
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
		}

		public void saveUserPreferences(String username, String password, String token){
			this.username = username;
			this.password = password;
            this.token = token;

			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("token", token);
			editor.commit();
		}
		
		private void setUsernamePreferences(String username){
			this.username = username;
			
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", username);
			editor.commit();
		}
		  
		private void setPasswordPreferences(String password){
			this.password = password;

			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("password", password);
			editor.commit();
		}

        private void setTokenPreferences(String token){
            this.token = token;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.commit();
        }

        private void setWebServerPreferences(String webServer){
            this.webServer = webServer;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("webServer", this.webServer);
            editor.commit();
        }

		String getUsernamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.username = sharedPreferences.getString("username", "guest");
			return this.username;
		}
	
		String getPasswordPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.password = sharedPreferences.getString("password", "");
			return this.password;
		}

        String getTokenPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.token = sharedPreferences.getString("token", "");
            return this.token;
        }

		private String getWebServerPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.webServer = sharedPreferences.getString("webServer", WebServer.TT_NAME);
			return this.webServer;
		}
		
		private boolean getQuickStartPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.quickStart = sharedPreferences.getBoolean("quickStart", TriagePic.QUICK_START_DEFAULT);
			return this.quickStart;
		}
    }	

    public boolean isLogin(Context c){
    	String u = "";
    	String p = "";
        String t = "";
    	DataSource s = new DataSource(c, app.getSeed());
    	s.open();
    	try {
    		u = s.getUsername();
    		p = s.getPassword();
            t = s.getToken();
    	}
    	catch(android.content.ActivityNotFoundException ex) {
    		u = TriagePic.GUEST;
    		p = "";
            t = "";
    	}
    	s.close();
    	
    	if (u.isEmpty() || u.equalsIgnoreCase(TriagePic.GUEST)){
    		app.setUsername(u);
    		app.setPassword(p);
            app.setToken(t);
    		app.setAuthStatus(false);
    		return false;
    	}
   		app.setUsername(u);
		app.setPassword(p);
        app.setToken(t);
		app.setAuthStatus(true);
   	
    	return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.itemTest:
//                Test();
//                break;
//            case R.id.itemGetAnonymousToken:
//                getAnonymousToken();
//                break;
            case R.id.itemQuestionAndAnswer:
                questionAndAnswer();
                break;
            case R.id.itemPing:
                if (app.getTokenStatus() == TriagePic.TOKEN_UNKNOWN){
                    getAnonymousToken();
                    if (errorMessage.isEmpty()){
                        testWebServer();
                    }
                }
                else {
                    testWebServer();
                }
                break;
            case R.id.itemForgot:
                if (app.getTokenStatus() == TriagePic.TOKEN_UNKNOWN){
                    getAnonymousToken();
                    if (errorMessage.isEmpty()){
                        forgotUsernamePassword();
                    }
                }
                else {
                    forgotUsernamePassword();
                }
                break;
            case R.id.itemNewUser:
                if (app.getTokenStatus() == TriagePic.TOKEN_UNKNOWN){
                    getAnonymousToken();
                    if (errorMessage.isEmpty()){
                        registerNewUser();
                    }
                }
                else {
                    registerNewUser();
                }
                break;
            case R.id.itemAbout:
                about();
                break;
            default:
                break;
        }
        return true;
    }

    private void getAnonymousToken() {
        errorMessage = "";
        String serverName = textViewWebServerName.getText().toString();
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            errorMessage = "The web server is not selected.";
            builder.setMessage(errorMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setTitle("Error")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            WebServer ws = new WebServer();
            if (app.getWebServerId() != -1){
                ws.setId(app.getWebServerId());

                DataSource s = new DataSource(this, app.getSeed());
                s.open();
                ws = s.getWebServerFromId(ws.getId());
                s.close();
            }

            if (ws == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                errorMessage = "The web server is not selected.";
                builder.setMessage(errorMessage)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setTitle("Error")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                return;
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                String anonymousToken = ws.callRequestAnonToken(this);
                if (!anonymousToken.isEmpty()){
                    app.setTokenAnonymous(anonymousToken);
                    app.setTokenStatus(TriagePic.TOKEN_ANONYMOUS);
                }
            }
        }
    }

    private void questionAndAnswer() {
        Intent i = new Intent(this, QuestionAndAnswerActivity.class);
        startActivity(i);
    }

    private void about() {
        Intent openAbout = new Intent(this, About.class);
        startActivity(openAbout);
    }

    private void testLatency() {
        WebServer ws = new WebServer();
        if (app.getWebServerId() != -1){
            ws.setId(app.getWebServerId());

            DataSource s = new DataSource(this, app.getSeed());
            s.open();
            ws = s.getWebServerFromId(ws.getId());
            s.close();
        }

        Intent i = new Intent(this, LatencyActivity.class);
        i.putExtra("webServer", ws.getWebService());
        startActivity(i);
    }
}