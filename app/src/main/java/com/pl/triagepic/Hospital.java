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

/**
 * A basic class to create Hospital Objects to easilly carry hospital
 * information
 *
 */
public class Hospital {
    public static final String DEFAULT_NAME = "NLM";
    public static final String DEFAULT_SHORT_NAME = "NLM";
    public static final String DEFAULT_STREET_1 = "9000 Rockville Pike";
    public static final String DEFAULT_STREET_2 = "";
    public static final String DEFAULT_CITY = "Bethesda";
    public static final String DEFAULT_COUNTY = "Montgomery";
    public static final String DEFAULT_STATE = "MD";
    public static final String DEFAULT_ZIP = "20892";
    public static final String DEFAULT_COUNTRY = "USA";
    public static final String DEFAULT_PID_PREFIX = "NLM"; // "911" is changed to "NLM" in version 8.0.2
    public static final boolean DEFAULT_PID_SUFFIX_VARIABLE = false;
    public static final int DEFAULT_PID_SUFFIX_FIXED_LENGTH = -1;
    public static final String DEFAULT_WWW = "www.nlm.nih.gov";
    public static final String DEFAULT_NPI = "1234567890";
    public static final double DEFAULT_LATITUDE = 38.995523;
    public static final double DEFAULT_LONGITUDE = -77.096597;
    public static final long DEFAULT_UUID = 1;

	public static final int MAXIMUM_SUFFIX_LENGTH = 8;

	long rowIndex;
	long uuid;
	String name;
	String shortname;
	String street1;
	String street2;
	String city;
	String county;
	String state;
	String country;
	String zip;
	String phone;
	String fax;
	String email;
	String www;
	String npi;
	String latitude;
	String longitude;
	// policy
	String pidPrefix;
	boolean pidSuffixVariable;
	int pidSuffixFixedLength;
	boolean photoRequired;
	boolean honorNoPhotoRequset;
	boolean photographerNameRequired;
	DataSource s;

    Hospital(){
        this.rowIndex = 0;
        this.uuid = 0;
        this.name = "";
        this.shortname = "";
        this.street1 = "";
        this.street2 = "";
        this.city = "";
        this.county = "";
        this.state = "";
        this.country = "";
        this.zip = "";
        this.phone = "";
        this.fax = "";
        this.email = "";
        this.www = "";
        this.npi = "";
        this.latitude = "";
        this.longitude = "";
        // policy
        this.pidPrefix = "";
        this.pidSuffixVariable = false;
        this.pidSuffixFixedLength = 0;
        this.photoRequired = false;
        this.honorNoPhotoRequset = false;
        this.photographerNameRequired = false;
    }

    public void toDefault(){
        this.name = DEFAULT_NAME;
        this.shortname = DEFAULT_SHORT_NAME;
        this.street1 = DEFAULT_STREET_1;
        this.street2 = DEFAULT_STREET_2;
        this.city = DEFAULT_CITY;
        this.county = DEFAULT_COUNTY;
        this.state = DEFAULT_STATE;
        this.zip = DEFAULT_ZIP;
        this.country = DEFAULT_COUNTRY;
        this.pidPrefix = DEFAULT_PID_PREFIX;
        this.pidSuffixVariable = DEFAULT_PID_SUFFIX_VARIABLE;
        this.pidSuffixFixedLength = DEFAULT_PID_SUFFIX_FIXED_LENGTH;
        this.www = DEFAULT_WWW;
        this.npi = DEFAULT_NPI;
        this.uuid = DEFAULT_UUID;
    }

    private int prefixNumber;
	public int getPrefixNumber(){
		return prefixNumber;
	}
	public void setPrefixNumber(int prefixNumber){
		this.prefixNumber = prefixNumber;
	}
	public void setPrefix(String pidPrefix){
		this.pidPrefix = pidPrefix;
		if (this.pidPrefix.isEmpty()){
			this.prefixNumber = 0;
			return;
		}
		pidPrefix = pidPrefix.replaceAll("[^\\d.]", "");
	}
	public void removeNonNumericCharInPrefix(){
		this.pidPrefix = pidPrefix.replaceAll("[^\\d.]", "");
	}

	/**
	 * Main Hospital Constructor
	 * 
	 * @param uuid
	 *            : Integer unique id of hospital
	 * @param name
	 *            : String full hospital name
	 * @param shortname
	 *            : shortened hospital name
	 * @param street1
	 *            : String 1st line of hospital street address
	 * @param street2
	 *            : String 2nd line of hospital street address
	 * @param city
	 *            : String city is located in
	 * @param county
	 *            : String county hospital is located in
	 * @param state
	 *            : String state hospital is located in
	 * @param country
	 *            : String country hospital is located in
	 * @param zip
	 *            : String zip/postal code hospital uses
	 * @param phone
	 *            : String hospitals main phone number
	 * @param fax
	 *            : String hospitals main fax number
	 * @param email
	 *            : String hospitals main email address
	 * @param www
	 *            : String hospital website address
	 * @param npi
	 *            : String National Provider Identifier
	 * @param latitude
	 *            : String latitude of hospitals location
	 * @param longitude
	 *            : String longitude of hospitals location
	 * @param pIDPrefix
	 *            : String Patient Id prefix length
	 * @param pIDSuffixVariable
	 *            Boolean: whether or not the patient id the hospital uses has a
	 *            variable suffix length
	 * @param pIDSuffixFixedLength
	 *            : int length of suffix if suffix is fixed
	 * @param photoRequired
	 *            : boolean whether the hospital requires a photo or not
	 * @param honorNoPhotoRequset
	 *            : boolean whether the hospital honors patients requesting no
	 *            photos
	 * @param photographerNameRequired
	 *            : boolean whether the hospital requires the photographer's
	 *            name
	 */
	Hospital(int uuid, String name, String shortname, String street1,
			String street2, String city, String county, String state,
			String country, String zip, String phone, String fax, String email,
			String www, String npi, String latitude, String longitude,
			String pIDPrefix, String pIDSuffixVariable,
			int pIDSuffixFixedLength, String photoRequired,
			String honorNoPhotoRequset, String photographerNameRequired) {
		this.uuid = uuid;
		this.name = name;
		this.shortname = shortname;
		this.street1 = street1;
		this.street2 = street2;
		this.city = city;
		this.county = county;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.www = www;
		this.npi = npi;
		this.latitude = latitude;
		this.longitude = longitude;
		this.pidPrefix = pIDPrefix;
		this.pidSuffixFixedLength = pIDSuffixFixedLength;
		if (pIDSuffixVariable.equals("true"))
			this.pidSuffixVariable = true;
		else
			this.pidSuffixVariable = false;
		if (photoRequired.equals("true"))
			this.photoRequired = true;
		else
			this.photoRequired = false;
		if (honorNoPhotoRequset.equals("true"))
			this.honorNoPhotoRequset = true;
		else
			this.honorNoPhotoRequset = false;
		if (photographerNameRequired.equals("true"))
			this.photographerNameRequired = true;
		else
			this.photographerNameRequired = false;

	}

	/**
	 * minor hospital constructor
	 * 
	 * @param uuid
	 *            : int unique id of hospital
	 */
	Hospital(int uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {

		return "uuid: " + uuid + "\nname: " + name + "\nshortname: "
				+ shortname + "street1: " + street1 + "\nStreet2: " + street2
				+ "\nCity: " + city + "Country: " + county + "\nState: "
				+ state + "\nCountry: " + country + "\n zip: " + zip
				+ "\nPhone: " + phone + "\nFax: " + fax + "\nEmail: " + email
				+ "\nwww: " + www + "\nnpi: " + npi + "\nLatitude: " + latitude
				+ "\nLongitude: " + longitude + "\npIDPrefix: " + pidPrefix
				+ "\npIDSufficVariable: " + pidSuffixVariable
				+ "\npIDSuffixFixedLength: " + pidSuffixFixedLength
				+ "\nPhotoRequired: " + photoRequired
				+ "\nHonorNoPhotoRequest: " + honorNoPhotoRequset
				+ "\nPhotographerNameRequired: " + photographerNameRequired;
	}

	/**
	 * compares hospitals for equality based on their unique identifiers
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Hospital) {
			Hospital b = (Hospital) obj;
			return this.uuid == b.uuid;
		}
		return false;
	}
}