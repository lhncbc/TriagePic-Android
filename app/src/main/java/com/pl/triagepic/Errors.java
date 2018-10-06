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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Errors {
	/**
	 * displays an error code in a alert dialog
	 * 
	 * @param i
	 *            : int error code id
	 * @param c
	 *            : Context to create error and get error message
	 */
	protected static void errorAlert(int i, Context c) {
		new AlertDialog.Builder(c)
				.setMessage("Error " + Errors.getErrorMesg(c, i))
				.setCancelable(false)
				.setPositiveButton("Okay.",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	/**
	 * Obtain an error message in resources based on error id, most error codes
	 * are from pl webservices, a few are unique to TPoT
	 * 
	 * @param c
	 *            : Context to obtain resources and look up error message
	 * @param error
	 *            : int the error id
	 * @return String the error message
	 */
	protected static String getErrorMesg(Context c, int error) {
		if (error == 0)
			return c.getString(R.string.pl_error_0);

		if (error == 1)
			return c.getString(R.string.pl_error_1);

		if (error == 2)
			return c.getString(R.string.pl_error_2);

		if (error == 3)
			return c.getString(R.string.pl_error_3);

		if (error == 4)
			return c.getString(R.string.pl_error_4);

		if (error == 5)
			return c.getString(R.string.pl_error_5);

		if (error == 6)
			return c.getString(R.string.pl_error_6);

		if (error == 7)
			return c.getString(R.string.pl_error_7);

		if (error == 8)
			return c.getString(R.string.pl_error_8);

		if (error == 9)
			return c.getString(R.string.pl_error_9);

		if (error == 10)
			return c.getString(R.string.pl_error_10);

		if (error == 11)
			return c.getString(R.string.pl_error_11);

		if (error == 12)
			return c.getString(R.string.pl_error_12);

		if (error == 13)
			return c.getString(R.string.pl_error_13);

		if (error == 20)
			return c.getString(R.string.pl_error_20);

		if (error == 100)
			return c.getString(R.string.pl_error_100);

		if (error == 200)
			return c.getString(R.string.pl_error_200);

		if (error == 201)
			return c.getString(R.string.pl_error_201);

		if (error == 300)
			return c.getString(R.string.pl_error_300);

		if (error == 301)
			return c.getString(R.string.pl_error_301);

		if (error == 302)
			return c.getString(R.string.pl_error_302);

		if (error == 400)
			return c.getString(R.string.pl_error_400);

		if (error == 402)
			return c.getString(R.string.pl_error_402);

		if (error == 403)
			return c.getString(R.string.pl_error_403);

		if (error == 405)
			return c.getString(R.string.pl_error_405);

		if (error == 406)
			return c.getString(R.string.pl_error_406);

		if (error == 407)
			return c.getString(R.string.pl_error_407);

		if (error == 408)
			return c.getString(R.string.pl_error_408);

		if (error == 410)
			return c.getString(R.string.pl_error_410);

		if (error == 411)
			return c.getString(R.string.pl_error_411);

		if (error == 412)
			return c.getString(R.string.pl_error_412);

		if (error == 413)
			return c.getString(R.string.pl_error_413);

		if (error == 414)
			return c.getString(R.string.pl_error_414);

		if (error == 415)
			return c.getString(R.string.pl_error_415);

		if (error == 416)
			return c.getString(R.string.pl_error_416);

		if (error == 417)
			return c.getString(R.string.pl_error_417);

		if (error == 418)
			return c.getString(R.string.pl_error_418);

		if (error == 419)
			return c.getString(R.string.pl_error_419);

		if (error == 420)
			return c.getString(R.string.pl_error_420);

		if (error == 421)
			return c.getString(R.string.pl_error_421);

		if (error == 422)
			return c.getString(R.string.pl_error_422);

		if (error == 423)
			return c.getString(R.string.pl_error_423);

		if (error == 424)
			return c.getString(R.string.pl_error_424);

		if (error == 425)
			return c.getString(R.string.pl_error_425);
		if (error == 9991)
			return c.getString(R.string.tpot_error_9991);
		if (error == 9993)
			return c.getString(R.string.tpot_error_9993);
		if (error == 9994)
			return c.getString(R.string.tpot_error_9994);
		if (error == 9995)
			return c.getString(R.string.tpot_error_9995);
		if (error == 9996)
			return c.getString(R.string.tpot_error_9996);
		if (error == 9997)
			return c.getString(R.string.tpot_error_9997);
		if (error == 9998)
			return c.getString(R.string.pl_error_9998);
		if (error == 9999)
			return c.getString(R.string.pl_error_9999);
		return null;
	}
}