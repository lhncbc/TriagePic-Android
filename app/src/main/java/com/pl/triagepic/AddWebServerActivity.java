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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddWebServerActivity extends Activity implements View.OnClickListener {
    WebServer w;

    Button buQuit;
    Button buExample;
//    Button buPing;
    Button buSave;

    EditText etWebServerName;
    EditText etShortName;
    EditText etWebService;
    EditText etNameSpace;
    EditText etUrl;

    TextView tvName;

    String errorMessage = "";

    TriagePic app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_web_server);

        app = (TriagePic) this.getApplication();
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        Initialize();
    }

    private void Initialize() {
        etWebServerName = (EditText) findViewById(R.id.editWebServerName);
        etShortName = (EditText) findViewById(R.id.editShortName);
        etWebService = (EditText) findViewById(R.id.editWebService);
        etNameSpace = (EditText) findViewById(R.id.editNameSpace);
        etUrl = (EditText) findViewById(R.id.editUrl);

        buQuit = (Button) findViewById(R.id.buttonQuit);
        buQuit.setOnClickListener(this);

        buExample = (Button) findViewById(R.id.buttonExample);
        buExample.setOnClickListener(this);

//        buPing = (Button) findViewById(R.id.buttonPing);
//        buPing.setOnClickListener(this);

        buSave = (Button) findViewById(R.id.buttonSave);
        buSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonQuit:
                this.finish();
                break;
            case R.id.buttonExample:
                SetExample();
                break;
            /*
            case R.id.buttonPing:
                SaveData();
                VerifyData();
                if (errorMessage.isEmpty() == false) {
                    return;
                }
                PingTest();
                break;
                */
            case R.id.buttonSave:
                SaveData();
                VerifyData();
                if (errorMessage.isEmpty() == false) {
                    return;
                }
                SaveToDatabase();
                this.finish();
                break;
            default:
                break;
        }
    }

    private void SaveToDatabase() {
        DataSource s = new DataSource(this, app.getSeed());
        s.open();
        s.createWebServer(w);
        s.close();
    }

    private void SetExample() {
        etWebServerName.setText("My Example");
        etShortName.setText("eg");
        etWebService.setText(WebServer.TT_WEB_SERVICE);
        etNameSpace.setText(WebServer.TT_NAMESPACE);
        etUrl.setText(WebServer.TT_URL);
    }

    private void VerifyData() {
        errorMessage = "";
        if (w.getName().isEmpty()) {
            errorMessage = "Name is not defined.";
        } else if (w.getShortName().isEmpty()) {
            errorMessage = "Short name is not defined.";
        } else if (w.getWebService().isEmpty()) {
            errorMessage = "Web service is not defined.";
        } else if (w.getNameSpace().isEmpty()) {
            errorMessage = "Name space is not defined.";
        } else if (w.getUrl().isEmpty()) {
            errorMessage = "Url is not defined.";
        }

        if (errorMessage.isEmpty()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMessage)
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

    private void PingTest() {
        MyPingEcho myPingEcho = new MyPingEcho(w);
        String returnString = "";

        if (app.getAuthStatus() == true){
            myPingEcho.setToken(app.getToken());
            myPingEcho.setUsername(app.getUsername());
            returnString = myPingEcho.Call();
        }
        else if (app.getTokenStatus() == TriagePic.TOKEN_ANONYMOUS){
            myPingEcho.setToken(app.getTokenAnonymous());
            myPingEcho.setUsername(app.getUsername());
            returnString = myPingEcho.Call();
        }
        else {
            Toast.makeText(this, "Error: token is not defined.", Toast.LENGTH_SHORT).show();
        }

        if (returnString.equalsIgnoreCase(MyPingEcho.TIME_OUT) == true || returnString.isEmpty() == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(myPingEcho.ERR_MSG)
                    .setCancelable(true)
                    .setTitle("Latency")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(myPingEcho.getLatency() + "ms")
                    .setCancelable(true)
                    .setTitle("Latency")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void SaveData() {
        w = new WebServer();
        w.setName(etWebServerName.getText().toString());
        w.setShortName(etShortName.getText().toString());
        w.setWebService(etWebService.getText().toString());
        w.setNameSpace(etNameSpace.getText().toString());
        w.setUrl(etUrl.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_web_server_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPing:
                testLatency();
                break;
            default:
                break;

        }
        return true;
    }

    private void testLatency() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etWebServerName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etShortName.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etWebService.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etNameSpace.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(etUrl.getWindowToken(), 0);

        SaveData();
        VerifyData();

        if (!errorMessage.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        } else {
            Intent i = new Intent(AddWebServerActivity.this, LatencyActivity.class);
            i.putExtra("webServer", etWebServerName.getText().toString());
            startActivity(i);
        }
    }
}
