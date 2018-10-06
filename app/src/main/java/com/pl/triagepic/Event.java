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

public class Event {
    public static final String DEFAULT_EVENT_NAME = "Test";
    public static final String DEFAULT_EVENT_SHORT_NAME = "test";
    public static final String DEFAULT_DATE = "2014-05-17";
    public static final String DEFAULT_TYPE = "TEST";
    public static final String DEFAULT_STREET = "8600 Rockville Pike, Bethesda, MD 20894, USA";
    public static final double DEFAULT_LATI = 38.99553;
    public static final double DEFAULT_LONGI = -77.09657;

	public static final String REPORTING_OPEN = "Reporting OPEN";
	public static final String REPORTING_CLOSED = "Reporting CLOSED";

	long rowIndex;
	long incident_id;
	long parent_id;
	String name;
	String shortname;
	String date;
	String type;
	double latitude;
	double longitude;
	String street;
	String group;
	Integer closed;

    Event(){
        this.rowIndex = 0;
        this.incident_id = 0;
        this.parent_id = 0;
        this.name = "";
        this.shortname = "";
        this.date = "";
        this.type = "";
        this.latitude = 0;
        this.longitude = 0;
        this.street = "";
        this.group ="";
        this.closed = 0;
    }

    void toDefault(){
        this.rowIndex = 0;
        this.incident_id = 0;
        this.parent_id = 0;
        this.name = DEFAULT_EVENT_NAME;
        this.shortname = DEFAULT_EVENT_SHORT_NAME;
        this.date = DEFAULT_DATE;
        this.type = DEFAULT_TYPE;
        this.latitude = DEFAULT_LATI;
        this.longitude = DEFAULT_LONGI;
        this.street = "";
        this.group ="";
        this.closed = 0;
    }

	/**
	 * Event constructor
	 * 
	 * @param icid
	 *            : Integer value specific to the incident
	 * @param parid
	 *            : Integer value of the parent event
	 * @param name
	 *            : String of event name
	 * @param shortname
	 *            : String event name shortened
	 * @param date
	 *            : String date in format YYYY-MM-DD
	 * @param type
	 *            : String -either "REAL" or "TEST" indicating if the even was
	 *            an actual disaster or any type of test, trial, demo etc
	 * @param lat
	 *            : Long longitude of event location
	 * @param lon
	 *            : Long latitude of event location
	 * @param street
	 *            : String the full street address of the event location if
	 *            known
	 * @param group
	 *            : String of Integer comma separated list of user group_id�s
	 *            with access to this event: null if public
	 * @param closed
	 *            : a non-zero values denotes that the event is closed to
	 *            reporting
	 */
	Event(Integer icid, Integer parid, String name, String shortname,
			String date, String type, float lat, float lon, String street,
			String group, Integer closed) {

		this.incident_id = icid;
		this.parent_id = parid;
		this.name = name;
		this.shortname = shortname;
		this.date = date;
		this.type = type;
		this.latitude = lat;
		this.longitude = lon;
		this.street = street;
		this.group = group;
		this.closed = closed;
	}

	@Override
	public String toString() {
		return "Incident_id: " + this.incident_id + "\nParent_id:  "
				+ this.parent_id + "\nName: " + name + "\nShortname: "
				+ shortname + "\nDate: " + date + "\nType: " + type
				+ "\nLatitude: " + this.latitude + "\nLongitude: "
				+ this.longitude + "\nStreet: " + street + "\nGroup: " + group
				+ "\nClosed: " + closed;
	}

	/**
	 * Compares events for equality based their long name.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Event) {
			Event b = (Event) obj;
			return this.name.equals(b.name);
		}

		return false;

	}
}