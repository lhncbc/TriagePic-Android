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

public class DummyHospitalContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyHospitalItem> ITEMS = new ArrayList<DummyHospitalItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyHospitalItem> ITEM_MAP = new HashMap<String, DummyHospitalItem>();

    static {
        // Add 3 sample items.
        /*
        addItem(new DummyItem("1", "Item 1"));
        addItem(new DummyItem("2", "Item 2"));
        addItem(new DummyItem("3", "Item 3"));
        */
    }

    // change it to public
    public static void addItem(DummyHospitalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyHospitalItem {
        public String id;
        public String rowIndex;
        public String name;
        public String shortName;
        public String street1;
        public String street2;
        public String city;
        public String county;
        public String state;
        public String zip;
        public String country;
        public String phone;
        public String fax;
        public String email;
        public String www;
        public String npi;
        public String latitude;
        public String longitude;
        public String pidPrefix;
        public String pidSuffixLengthSpecified;
        public String pidSuffixLength;

        public DummyHospitalItem(
                String id,
                String rowIndex,
                String name,
                String shortName,
                String street1,
                String street2,
                String city,
                String county,
                String state,
                String zip,
                String country,
                String phone,
                String fax,
                String email,
                String www,
                String npi,
                String latitude,
                String longitude,
                String pidPrefix,
                String pidSuffixLengthSpecified,
                String pidSuffixLength) {
                this.id = id;
                this.rowIndex = rowIndex;
                this.name = name;
                this.shortName = shortName;
                this.street1 = street1;
                this.street2 = street2;
                this.city = city;
                this.county = county;
                this.state = state;
                this.zip = zip;
                this.country = country;
                this.phone = phone;
                this.fax = fax;
                this.email = email;
                this.www = www;
                this.npi = npi;
                this.latitude = latitude;
                this.longitude = longitude;
                this.pidPrefix = pidPrefix;
                this.pidSuffixLengthSpecified = pidSuffixLengthSpecified;
                this.pidSuffixLength = pidSuffixLength;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
