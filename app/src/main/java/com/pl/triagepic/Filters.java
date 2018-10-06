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

import org.json.JSONObject;

public class Filters {
    public static final int GENDER_UNKNOWN = 0;
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int COMPLEX = 3;

    public static final int AGE_UNKNOWN = 1000;
    public static final int CHILD = 1001;
    public static final int ADULT = 1002;

    public static final int GREEN_ZONE = 0;
    public static final int BH_GREEN_ZONE = 1;
    public static final int YELLOW_ZONE = 2;
    public static final int RED_ZONE = 3;
    public static final int GRAY_ZONE = 4;
    public static final int BLACK_ZONE = 5;
    public static final int UNASSIGNED_ZONE = 6;

    // id;
    private int id;

    // gender
    private boolean male;
	private boolean female;
	private boolean complex;
	private boolean genderUnknown;	

    // age
    private boolean adult;
	private boolean child;
	private boolean ageUnknown;

    // zone
    private boolean greenZone;
    private boolean bhGreenZone;
    private boolean yellowZone;
    private boolean redZone;
    private boolean grayZone;
    private boolean blackZone;
    private boolean unassignedZone;

	Filters() {
        id = 0;

		male = true;
		female = true;
		complex = true;
		genderUnknown = true;

		adult = true;
		child = true;
        ageUnknown = true;

        greenZone = true;
        bhGreenZone = true;
        yellowZone = true;
        redZone = true;
        grayZone = true;
        blackZone = true;
        unassignedZone = true;
	}

	Filters(Filters o) {
        this.id = o.getId();

		this.male = o.getMale();
		this.female = o.getFemale();
		this.complex = o.getComplex();
		this.genderUnknown = o.getGenderUnknown();

		this.adult = o.getAdult();
		this.child = o.getChild();
		this.ageUnknown = o.getAgeUnknown();

		this.greenZone = o.getGreenZone();
		this.bhGreenZone = o.getBhGreenZone();
		this.yellowZone = o.getYellowZone();
		this.redZone = o.getRedZone();
		this.grayZone = o.getGrayZone();
        this.blackZone = o.getBlackZone();
        this.unassignedZone = o.getUnassignedZone();
	}
	
	public void setDefaults() {
		male = true;
		female = true;
		complex = true;
		genderUnknown = true;

		adult = true;
		child = true;
        ageUnknown = true;

        greenZone = true;
        bhGreenZone = true;
        yellowZone = true;
        redZone = true;
        grayZone = true;
        blackZone = true;
        unassignedZone = true;
	}

    public int getId(){return id;};
    public void setId(int id){this.id = id;}
	
	public boolean getMale(){
		return male;
	}
	public void setMale(boolean male){
		this.male = male;
	}
	
	public boolean getFemale(){
		return female;
	}
	public void setFemale(boolean female){
		this.female = female;
	}
	
	public boolean getComplex(){
		return complex;
	}
	public void setComplex(boolean complex){
		this.complex = complex;
	}

	public boolean getGenderUnknown(){
		return genderUnknown;
	}
	public void setGenderUnknown(boolean genderUnknown){
		this.genderUnknown = genderUnknown;
	}
	
	public boolean getAdult(){
		return adult;
	}
	public void setAdult(boolean adult){
		this.adult = adult;
	}

	public boolean getChild(){
		return child;
	}
	public void setChild(boolean child){
		this.child = child;
	}

	public boolean getAgeUnknown(){
		return ageUnknown;
	}
	public void setAgeUnknown(boolean ageUnknown){
		this.ageUnknown = ageUnknown;
	}

	public boolean getGreenZone(){
		return greenZone;
	}
	public void setGreenZone(boolean greenZone){
		this.greenZone = greenZone;
	}

    public boolean getBhGreenZone(){
        return bhGreenZone;
    }
    public void setBhGreenZone(boolean bhGreenZone){
        this.bhGreenZone = bhGreenZone;
    }

    public boolean getYellowZone(){
        return yellowZone;
    }
    public void setYellowZone(boolean yellowZone){
        this.yellowZone = yellowZone;
    }

    public boolean getRedZone(){
        return redZone;
    }
    public void setRedZone(boolean redZone){
        this.redZone = redZone;
    }

    public boolean getGrayZone(){
        return grayZone;
    }
    public void setGrayZone(boolean greyZone){
        this.grayZone = greyZone;
    }

    public boolean getBlackZone(){
        return blackZone;
    }
    public void setBlackZone(boolean blackZone){
        this.blackZone = blackZone;
    }

    public boolean getUnassignedZone(){
        return unassignedZone;
    }
    public void setUnassignedZone(boolean unassignedZone){
        this.unassignedZone = unassignedZone;
    }

    // JSONPATIENT1 FORMAT - added in version 9.0.0
    public static JSONObject toJSON(Filters filters){
        JSONObject json = new JSONObject();
        try {
            json.put("genderMale", filters.getMale());
            json.put("genderFemale", filters.getFemale());
            json.put("genderComplex", filters.getComplex());
            json.put("genderUnknown", filters.getGenderUnknown());

            json.put("ageChild", filters.getChild());
            json.put("ageAdult", filters.getAdult());
            json.put("ageUnknown", filters.getAgeUnknown());

            json.put("hospital", "1");
            json.put("hasImage", false);

            json.put("Green", filters.getGreenZone());
            json.put("BH Green", filters.getBhGreenZone());
            json.put("Yellow", filters.getYellowZone());
            json.put("Red", filters.getRedZone());
            json.put("Gray", filters.getGrayZone());
            json.put("Black", filters.getBlackZone());
            json.put("Unknown", filters.getUnassignedZone());

            /*
            json.put("genderMale", true);
            json.put("genderFemale", true);
            json.put("genderComplex", true);
            json.put("genderUnknown", true);

            json.put("ageChild", true);
            json.put("ageAdult", true);
            json.put("ageUnknown", true);

            json.put("hospital", "1");
            json.put("hasImage", true);

            json.put("Green", true);
            json.put("BH Green", true);
            json.put("Yellow", true);
            json.put("Red", true);
            json.put("Gray", true);
            json.put("Black", true);
            json.put("Unknown", true);
            */
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public static JSONObject toJSON(Filters filters, ViewSettings viewSettings){
        JSONObject json = new JSONObject();
        try {
            json.put("genderMale", filters.getMale());
            json.put("genderFemale", filters.getFemale());
            json.put("genderComplex", filters.getComplex());
            json.put("genderUnknown", filters.getGenderUnknown());

            json.put("ageChild", filters.getChild());
            json.put("ageAdult", filters.getAdult());
            json.put("ageUnknown", filters.getAgeUnknown());

            json.put("hospital", "1");
            if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
                json.put("hasImage", true);
            }
            else if (viewSettings.getPhotoSel() == ViewSettings.BOTH){
                json.put("hasImage", false);
            }
            else {
                json.put("hasImage", false);
            }

            json.put("Green", filters.getGreenZone());
            json.put("BH Green", filters.getBhGreenZone());
            json.put("Yellow", filters.getYellowZone());
            json.put("Red", filters.getRedZone());
            json.put("Gray", filters.getGrayZone());
            json.put("Black", filters.getBlackZone());
            json.put("Unknown", filters.getUnassignedZone());

            /*
            json.put("genderMale", true);
            json.put("genderFemale", true);
            json.put("genderComplex", true);
            json.put("genderUnknown", true);

            json.put("ageChild", true);
            json.put("ageAdult", true);
            json.put("ageUnknown", true);

            json.put("hospital", "1");
            json.put("hasImage", true);

            json.put("Green", true);
            json.put("BH Green", true);
            json.put("Yellow", true);
            json.put("Red", true);
            json.put("Gray", true);
            json.put("Black", true);
            json.put("Unknown", true);
            */
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

}
