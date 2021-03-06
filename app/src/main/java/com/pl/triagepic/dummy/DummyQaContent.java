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

package com.pl.triagepic.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyQaContent {
    private final static String Q0 = "Where to download TriagePic®?";
    private final static String A0 = "You can go to \"Lost Person Finder\" or Google Play to download TriagePic®.";
    private final static String Q1 = "Do we need to install \"ZXing Barcode Scanner\"?";
    private final static String A1 = "Yes. \"ZXing Barcode Scanner\" is a free APP which is used to scan patient\'s ID numbers in TriagePic®";
    private final static String Q2 = "Where to download \"ZXing Barcode Scanner\"?";
    private final static String A2 = "You can go to Google Play and download \"ZXing Barcode Scanner\".";
    private final static String Q3 = "How to start TriagePic®?";
    private final static String A3 =
            "1.\t\tPress the TriagePic® icon to ;launch TriagePic®;\n" +
                    "2.\t\tFor the first time user, the login screen will show up after start up;\n" +
                    "3.\t\tSelect the Web Server ;\n" +
                    "4.\t\tTest the Web Server availability;\n" +
                    "5.\t\tSelect hospital;\n" +
                    "6.\t\tSelect event;\n" +
                    "7.\t\tEnter username and password;\n" +
                    "8.\t\tAfter authentication is verified, you will come to the home screen.\n";
    private final static String Q4 = "What are these items listed in the left panel in home screen?";
    private final static String A4 = "Left side of screen:\n" +
            "There are four folders to hold records:\n" +
            "Drafts - where to save intermediate work;\n" +
            "Sent - where to save the records sent to the Web Server;\n" +
            "Outbox - the records are completed and verified, but not sent yet due to the internet availability;\n" +
            "Deleted - unwanted records. These records may be resend back to Draft or Outbox.\n";
    private final static String Q5 = "What are these items listed in the right panel in home screen?";
    private final static String A5 = "Right side of screen:\n" +
            "List items of records in selected boxes (one of Draft, Sent, Outbox or Deleted);\n" +
            "Click one item will lead you to more details of patient record (worksheet).\n";
    private final static String Q6 = "What are these menu commands in home screen?";
    private final static String A6 = "Menu commands:\n" +
            "New - to create an new record;\n" +
            "Delete - to delete a selected record; \n" +
            "Latency - to detect the Web Server availability;\n" +
            "Check sent files - to check the files (records) uploaded;\n" +
            "Email us - to send feedback via email;\n" +
            "Tutorials - to be completed;\n" +
            "Developer tools - to be removed after officially release; \n" +
            "User info - Displays username;\n" +
            "Login - to login;\n" +
            "Logout - to logout;\n" +
            "About - to be added more info about this app.\n";
    private final static String Q7 = "How to input a patient\'s information in report screen?";
    private final static String A7 = "Hospital – to select hospital;\n" +
            "Event – to select event;\n" +
            "Zone – to select zone for each patient;\n" +
            "Patient ID – to input patient ID;\n" +
            "Last name – input last name;\n" +
            "First name – input first name;\n" +
            "Sex - male or female;\n" +
            "Age - adult or child;\n" +
            "UUID - for uploaded record, this displays the url for more info on Web Server;\n" +
            "Comments - for comments;\n" +
            "Photo – for patient’s photos;\n" +
            "Caption - to input caption for each photo displayed. \n";
    private final static String Q8 = "What are these commands displayed on report screen?";
    private final static String A8 = "Send – to send the completed record;\n" +
            "Drafts – to save the work in the middle;\n" +
            "Outbox – to save the completed record for upload later;\n" +
            "Camera – to take patient’s photo;\n" +
            "Gallery – to retrieve the patient’s photo previously stored in local disk;\n" +
            "Primary – to set the current photo as primary photo;\n" +
            "Crop – to crop the photo image;\n" +
            "Latency – to check the Web Server availability.\n";

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add 4 sample items.
        addItem(new DummyItem("0", Q0, A0));
        addItem(new DummyItem("1", Q1, A1));
        addItem(new DummyItem("2", Q2, A2));
        addItem(new DummyItem("3", Q3, A3));
        addItem(new DummyItem("4", Q4, A4));
        addItem(new DummyItem("5", Q5, A5));
        addItem(new DummyItem("6", Q6, A6));
        addItem(new DummyItem("7", Q7, A7));
        addItem(new DummyItem("8", Q8, A8));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String q;
        public String a;

        public DummyItem(String id, String q, String a) {
            this.id = id;
            this.q = q;
            this.a = a;
        }

        @Override
        public String toString() {
            return q;
        }
    }

}
