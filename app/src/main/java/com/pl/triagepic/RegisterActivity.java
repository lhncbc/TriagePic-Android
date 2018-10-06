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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends Activity implements View.OnClickListener {
    private static final String METHOD_NAME = "registerUser";
    private static final String SOAP_REQUEST = "Soap request ";
    private static final String SOAP_RESPONSE = "Soap responce ";

    private static final int WEB_SERVER_SELECTION = 1;

    TriagePic app;
    String webServer = "";
    String webServerLocal = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    WebServer ws;

    Button buRegister;
    Button buCancel;
    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etUsername;
    EditText etPassword1;
    EditText etPassword2;
    CheckBox ckShow;

    EditText editEmailAddress;

    Button buttonSelectWebServer;
    TextView textViewWebServerName;

    // returned message
    private String registered;
    private String errorCode;
    private String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        app = ((TriagePic) this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        Initialize();
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

        buRegister = (Button) findViewById(R.id.buttonRegister);
        buCancel = (Button) findViewById(R.id.buttonCancel);
        etFirstName = (EditText) findViewById(R.id.editTexFirstName);
        etLastName = (EditText) findViewById(R.id.editTexLastName);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etUsername = (EditText) findViewById(R.id.editTextUsername);
        etPassword1 = (EditText) findViewById(R.id.editTextPassword1);
        etPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        ckShow = (CheckBox) findViewById(R.id.checkBoxShow);

        editEmailAddress = (EditText) findViewById(R.id.editPassword);
        buttonSelectWebServer = (Button) findViewById(R.id.buttonSelectWebServer);
        textViewWebServerName = (TextView) findViewById(R.id.textViewWebServerName);

        buttonSelectWebServer.setOnClickListener(this);

        buRegister.setOnClickListener(this);
        buCancel.setOnClickListener(this);
        ckShow.setOnClickListener(this);

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
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSelectWebServer:
                selectWebServer();
                break;
            case R.id.checkBoxShow:
                ShowPassword();
                break;
            case R.id.buttonRegister:
                Register();
                break;
            case R.id.buttonCancel:
                Cancel();
                break;
            default:
                // It won't.
                break;
        }
    }

    private void Cancel() {
        Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void selectWebServer() {
        Intent i = new Intent(this, WebServerActivity.class);
        startActivityForResult(i, WEB_SERVER_SELECTION);
    }

    private void ShowPassword() {
        if (ckShow.isChecked() == true) {
            etPassword1.setTransformationMethod(null);
            etPassword2.setTransformationMethod(null);
        } else {
            etPassword1.setTransformationMethod(new PasswordTransformationMethod());
            etPassword2.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    private void Register() {
        // Changed in 7.0.3
        // Verify the rules
        if (VerifyPasswords() == true) {
            if (app.getTokenAnonymous().isEmpty()) {
                getAnonymousToken();
                if (errorMessage.isEmpty()){
                    RegisterUserThreadVersion();
                }
            }
            else {
                RegisterUserThreadVersion();
            }

            if (registered.equalsIgnoreCase("true") == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("Registration is successful! \nWe are sending an email to you. \nPlease check your email and click the link to active your account.")
                        .setCancelable(true)
                        .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(RegisterActivity.this, "Registration is successful!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                CleanEntry();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage(errorMessage)
                        .setCancelable(true)
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(RegisterActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                System.exit(0);
                            }
                        })
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(RegisterActivity.this, "Retry", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void CleanEntry() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etUsername.setText("");
        etPassword1.setText("");
        etPassword2.setText("");
    }

    private void RegisterUserThreadVersion() {
        // Better to use the threads.
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    RegisterUser();
                }

                private void RegisterUser() {
                    nameSpace = ws.getNameSpace();
                    url = ws.getUrl();

                    soapAction = nameSpace + METHOD_NAME;
                    SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

                    // Register user
                    // Start
                    request.addProperty("token", app.getTokenAnonymous()); // using anonymous token. version 7.0.3
                    request.addProperty("username", etUsername.getText().toString());
                    request.addProperty("emailAddress", etEmail.getText().toString());
                    request.addProperty("password", etPassword1.getText().toString());
                    request.addProperty("givenName", etFirstName.getText().toString());
                    request.addProperty("familyName", etLastName.getText().toString());
                    // End

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = false;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE aht = new HttpTransportSE(url); // Either will do.
                    aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
                    SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
                    try {
                        aht.debug = true;
                        aht.call(soapAction, envelope);
                        Log.i(SOAP_REQUEST, aht.requestDump);
                        Log.i(SOAP_RESPONSE, aht.responseDump);
                        result = (SoapPrimitive) envelope.getResponse();
                        envelope.getResponse();
                    } catch (Exception e) {
                        result = null;
                    }

                    SoapObject resultRequestSOAP = (SoapObject) envelope.bodyIn;
                    registered = resultRequestSOAP.getPropertyAsString("registered").toString();
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
                Toast.makeText(RegisterActivity.this, "Time out!", Toast.LENGTH_SHORT).show();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread
    }

    private boolean VerifyPasswords() {
        if (textViewWebServerName.getText().length() == 0) {
            MyErrorMessageBox("Web server is not defined!", "Retry");
            return false;
        }
        if (etFirstName.getText().length() == 0) {
            MyErrorMessageBox("First name is empty!", "Retry");
            return false;
        }
        if (etLastName.getText().length() == 0) {
            MyErrorMessageBox("Last name is empty!", "Retry");
            return false;
        }
        if (etUsername.getText().length() == 0) {
            MyErrorMessageBox("Username is empty!", "Retry");
            return false;
        }
        if (etEmail.getText().length() == 0) {
            MyErrorMessageBox("Email is empty!", "Retry");
            return false;
        }
        if (etPassword1.getText().length() == 0) {
            MyErrorMessageBox("Password is empty!", "Retry");
            return false;
        }
        if (etPassword2.getText().length() == 0) {
            MyErrorMessageBox("Confirmation password is empty!", "Retry");
            return false;
        }

        // Verify password
        // Identical
        String strP1 = etPassword1.getText().toString();
        String strP2 = etPassword2.getText().toString();

        if (strP1.equals(strP2) == false) {
            MyErrorMessageBox("Confirmation password is not identical!", "Retry");
            return false;
        }

        // length of passwords
        if (strP1.length() < 8) {
            MyErrorMessageBox("Minimum length of the password is 8 characters!", "Retry");
            return false;
        }
        if (strP1.length() > 16) {
            MyErrorMessageBox("Maximum length of the password is 16 characters!", "Retry");
            return false;
        }

        // At least one char is uppercase
        boolean found = false;
        for (int i = 0; i < strP1.length(); i++) {
            if (strP1.charAt(i) >= 'A' && strP1.charAt(i) <= 'Z') {
                found = true;
                break;
            }
        }
        if (found == false) {
            MyErrorMessageBox("Must have at least one uppercase character!", "Retry");
            return false;
        }

        // Must have at least one lowercase character
        found = false;
        for (int i = 0; i < strP1.length(); i++) {
            if (strP1.charAt(i) >= 'a' && strP1.charAt(i) <= 'z') {
                found = true;
                break;
            }
        }
        if (found == false) {
            MyErrorMessageBox("Must have at least one lowercase character!", "Retry");
            return false;
        }

        // Must have at least one numeral (0-9)
        found = false;
        for (int i = 0; i < strP1.length(); i++) {
            if (strP1.charAt(i) >= '0' && strP1.charAt(i) <= '9') {
                found = true;
                break;
            }
        }
        if (found == false) {
            MyErrorMessageBox("Must have at least one numeral (0-9)!", "Retry");
            return false;
        }

        // Password cannot contain your username
        String strUsername = etUsername.getText().toString();
        if (strUsername.indexOf(etUsername.toString(), 0) >= 0) {
            MyErrorMessageBox("Password cannot contain your username!", "Retry");
            return false;
        }
        return true;
    }

    private void MyErrorMessageBox(String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        return;
    }

    private int _getScreenOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            default:
                break;
        }
        return true;
    }

    private void getAnonymousToken() {
        errorMessage = "";
        String serverName = textViewWebServerName.getText().toString();
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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


    private void testWebServer() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etFirstName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etLastName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etPassword1.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etPassword2.getWindowToken(), 0);

        String serverName = textViewWebServerName.getText().toString();
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
            Intent i = new Intent(RegisterActivity.this, LatencyActivity.class);
            i.putExtra("webServer", textViewWebServerName.getText());
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WEB_SERVER_SELECTION) {
            Initialize();
        }
    }
}
