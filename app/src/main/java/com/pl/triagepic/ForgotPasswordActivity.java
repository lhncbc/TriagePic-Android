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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

public class ForgotPasswordActivity extends Activity implements View.OnClickListener{
    private static final String METHOD_NAME = "resetUserPassword";

    private static final int WEB_SERVER_SELECTION = 1;

    TriagePic app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    WebServer ws;

    String errorCode = "";
    String errorMessage = "";
    EditText editEmailAddress;
	Button buttonSendEmailAddress;
    Button buttonCancel;

    Button buttonSelectWebServer;
    TextView textViewWebServerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
		
		app = ((TriagePic)this.getApplication());
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

        Intent sender = getIntent();
        webServer = sender.getExtras().getString("webServer");
        if (webServer.isEmpty()){

        }

        buttonSelectWebServer = (Button)findViewById(R.id.buttonSelectWebServer);
        textViewWebServerName = (TextView)findViewById(R.id.textViewWebServerName);

        buttonSelectWebServer.setOnClickListener(this);

		editEmailAddress = (EditText) findViewById(R.id.editEmailAddress);
        buttonSendEmailAddress = (Button) findViewById(R.id.buttonSendEmailAddress);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        buttonSendEmailAddress.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

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
		switch (v.getId()){
        case R.id.buttonSelectWebServer:
            selectWebServer();
            break;
		case R.id.buttonSendEmailAddress:
            sendEmailAddress();
			break;
        case R.id.buttonCancel:
            Cancel();
            break;
        default:
            break;
		}
	}

    private void Cancel() {
        Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void sendEmailAddress() {
        if (textViewWebServerName.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Web server name is not defined!")
                    .setCancelable(false)
                    .setTitle("Error")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editEmailAddress.getWindowToken(), 0);

            errorMessage = ws.resetUserPassword(app.getTokenAnonymous().toString(), editEmailAddress.getText().toString(), this);
            if (errorMessage.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Email address is sent. Please check your email.")
                        .setCancelable(false)
                        .setTitle("Success")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                editEmailAddress.setText("");
                                ForgotPasswordActivity.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(errorMessage + "\nIs your email address correct?")
                        .setCancelable(false)
                        .setTitle("Error")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                errorCode = "";
                                errorMessage = "";
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void selectWebServer() {
        Intent i = new Intent(this, WebServerActivity.class);
        startActivityForResult(i, WEB_SERVER_SELECTION);
    }


	private void ResetUserPassword() {
		if (editEmailAddress.getText().length() == 0){
			errorCode = "-1";
			errorMessage = "Email address is empty!";
			return;
		}
		
		// Better to use the threads. 
	    //limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			resetUserPassword();
	    		}

				private void resetUserPassword() {
			    	SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

			    	// Register user
			    	// Start
			    	request.addProperty("email", editEmailAddress.getText().toString());
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
			    		aht.call(soapAction, envelope);
						Log.e("Soap request ", aht.requestDump);
						Log.e("Soap response ", aht.responseDump);
						result = (SoapPrimitive)envelope.getResponse();  
						envelope.getResponse();  
			    	} catch (Exception e) {
			    		result = null;
			    	}
			    	
			       	SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
			    	errorCode = resultRequestSOAP.getPropertyAsString("errorCode");
			    	errorMessage = resultRequestSOAP.getPropertyAsString("errorMessage");
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
				Toast.makeText(ForgotPasswordActivity.this, "Time out!", Toast.LENGTH_SHORT).show();					
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread		
		
		
	}
	private void MyMessageBox(String message, String buttonText) {
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
		
		if (app.isTablet() == true){
			return;
		}

		String currentOrientation = "";
		int orientation = _getScreenOrientation();
		if (orientation == Configuration.ORIENTATION_LANDSCAPE){
			currentOrientation = "orientation is landscape";
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else if (orientation == Configuration.ORIENTATION_PORTRAIT){
			currentOrientation = "orientation is portrait";			
		}
		else {
			currentOrientation = "orientation is unknown";						
		}
		
	    Toast.makeText(this, currentOrientation, Toast.LENGTH_SHORT).show();		
	}
	
	private int _getScreenOrientation(){    
	    return getResources().getConfiguration().orientation;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forgot_password_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPing:
                testWebServer();
                break;
            default:
                break;
        }
        return true;
    }

    private void testWebServer() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editEmailAddress.getWindowToken(), 0);

        String serverName = textViewWebServerName.getText().toString();
        if (serverName.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            Intent i = new Intent(this, LatencyActivity.class);
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
