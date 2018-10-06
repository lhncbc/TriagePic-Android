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
import android.widget.Toast;

import org.json.JSONObject;

public class SearchSettings {
    public Filters filters;
    public ViewSettings viewSettings;

    public void setFilters(Filters filters){this.filters = filters;}
    public Filters getFilters(){return filters;}

    public void setViewSettings(ViewSettings viewSettings){this.viewSettings = viewSettings;}
    public ViewSettings getViewSettings(){return viewSettings;}

    // JSONPATIENT1 FORMAT - added in version 9.0.0
    private static JSONObject toJSON(Filters filters, ViewSettings viewSettings, Context c){
        JSONObject json = new JSONObject();
        try {
            json.put("genderMale", new Boolean(filters.getMale()));
            json.put("genderFemale", new Boolean(filters.getFemale()));
            json.put("genderComplex", new Boolean(filters.getComplex()));
            json.put("genderUnknown", new Boolean(filters.getGenderUnknown()));

            json.put("ageChild", new Boolean(filters.getChild()));
            json.put("ageAdult", new Boolean(filters.getAdult()));
            json.put("ageUnknown", new Boolean(filters.getAgeUnknown()));

            boolean hasImage;
            if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
                hasImage = true;
            }
            else {
                hasImage = false;
            }
            json.put("hasImage", hasImage);
            json.put("hospital", "1"); // always one at a time.
//            json.put("hospital", new Integer(hospital));

            json.put("Green", new Boolean(filters.getGreenZone()));
            json.put("BH Green", new Boolean(filters.getBhGreenZone()));
            json.put("Yellow", new Boolean(filters.getYellowZone()));
            json.put("Red", new Boolean(filters.getRedZone()));
            json.put("Gray", new Boolean(filters.getGrayZone()));
            json.put("Black", new Boolean(filters.getBlackZone()));
            json.put("Unknown", new Boolean(filters.getUnassignedZone()));
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }
}
