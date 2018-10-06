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
 * This file is added in version 8.0.5
 */
public class PopularNames {
    public static boolean MALE = true;
    public static boolean FEMALE = false;
    public static int MAX_NAMES = 99;
    private boolean gender; // true for male and false for girl;
    public boolean getGender() {
        return this.gender;
    }
    public void setGender(boolean gender){
        this.gender = gender;
    }
    public String getRandomBoyName(){
        int index = getRandomNumber(0, MAX_NAMES);
        return boys[index];
    }
    public String getRandomGirlName(){
        int index = getRandomNumber(0, MAX_NAMES);
        return girls[index];
    }
    public String getRandomLastName() {
        int index = getRandomNumber(0, MAX_NAMES);
        return lastNames[index];
    }
    int getRandomNumber(int min, int max){
        return randomWithRange(min, max);
    }
    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private static String [] girls = {
            "Sophia",
            "Emma",
            "Olivia",
            "Isabella",
            "Mia",
            "Ava",
            "Lily",
            "Zoe",
            "Emily",
            "Chloe",
            "Layla",
            "Madison",
            "Madelyn",
            "Abigail",
            "Aubrey",
            "Charlotte",
            "Amelia",
            "Ella",
            "Kaylee",
            "Avery",
            "Aaliyah",
            "Hailey",
            "Hannah",
            "Addison",
            "Riley",
            "Harper",
            "Aria",
            "Arianna",
            "Mackenzie",
            "Lila",
            "Evelyn",
            "Adalyn",
            "Grace",
            "Brooklyn",
            "Ellie",
            "Anna",
            "Kaitlyn",
            "Isabelle",
            "Sophie",
            "Scarlett",
            "Natalie",
            "Leah",
            "Sarah",
            "Nora",
            "Mila",
            "Elizabeth",
            "Lillian",
            "Kylie",
            "Audrey",
            "Lucy",
            "Maya",
            "Annabelle",
            "Makayla",
            "Gabriella",
            "Elena",
            "Victoria",
            "Claire",
            "Savannah",
            "Peyton",
            "Maria",
            "Alaina",
            "Kennedy",
            "Stella",
            "Liliana",
            "Allison",
            "Samantha",
            "Keira",
            "Alyssa",
            "Reagan",
            "Molly",
            "Alexandra",
            "Violet",
            "Charlie",
            "Julia",
            "Sadie",
            "Ruby",
            "Eva",
            "Alice",
            "Eliana",
            "Taylor",
            "Callie",
            "Penelope",
            "Camilla",
            "Bailey",
            "Kaelyn",
            "Alexis",
            "Kayla",
            "Katherine",
            "Sydney",
            "Lauren",
            "Jasmine",
            "London",
            "Bella",
            "Adeline",
            "Caroline",
            "Vivian",
            "Juliana",
            "Gianna",
            "Skyler",
            "Jordyn"
    };
    private static String [] boys = {
            "Jackson",
            "Aiden",
            "Liam",
            "Lucas",
            "Noah",
            "Mason",
            "Jayden",
            "Ethan",
            "Jacob",
            "Jack",
            "Caden",
            "Logan",
            "Benjamin",
            "Michael",
            "Caleb",
            "Ryan",
            "Alexander",
            "Elijah",
            "James",
            "William",
            "Oliver",
            "Connor",
            "Matthew",
            "Daniel",
            "Luke",
            "Brayden",
            "Jayce",
            "Henry",
            "Carter",
            "Dylan",
            "Gabriel",
            "Joshua",
            "Nicholas",
            "Isaac",
            "Owen",
            "Nathan",
            "Grayson",
            "Eli",
            "Landon",
            "Andrew",
            "Max",
            "Samuel",
            "Gavin",
            "Wyatt",
            "Christian",
            "Hunter",
            "Cameron",
            "Evan",
            "Charlie",
            "David",
            "Sebastian",
            "Joseph",
            "Dominic",
            "Anthony",
            "Colton",
            "John",
            "Tyler",
            "Zachary",
            "Thomas",
            "Julian",
            "Levi",
            "Adam",
            "Isaiah",
            "Alex",
            "Aaron",
            "Parker",
            "Cooper",
            "Miles",
            "Chase",
            "Muhammad",
            "Christopher",
            "Blake",
            "Austin",
            "Jordan",
            "Leo",
            "Jonathan",
            "Adrian",
            "Colin",
            "Hudson",
            "Ian",
            "Xavier",
            "Camden",
            "Tristan",
            "Carson",
            "Jason",
            "Nolan",
            "Riley",
            "Lincoln",
            "Brody",
            "Bentley",
            "Nathaniel",
            "Josiah",
            "Declan",
            "Jake",
            "Asher",
            "Jeremiah",
            "Cole",
            "Mateo",
            "Micah",
            "Elliot"
    };
    private static String [] lastNames = {
            "Smith",
            "Johnson",
            "Williams",
            "Jones",
            "Brown",
            "Davis",
            "Miller",
            "Wilson",
            "Moore",
            "Taylor",
            "Anderson",
            "Thomas",
            "Jackson",
            "White",
            "Harris",
            "Martin",
            "Thompson",
            "Garcia",
            "Martinez",
            "Robinson",
            "Clark",
            "Rodriguez",
            "Lewis",
            "Lee",
            "Walker",
            "Hall",
            "Allen",
            "Young",
            "Hernandez",
            "King",
            "Wright",
            "Lopez",
            "Hill",
            "Scott",
            "Green",
            "Adams",
            "Baker",
            "Gonzalez",
            "Nelson",
            "Carter",
            "Mitchell",
            "Perez",
            "Roberts",
            "Turner",
            "Phillips",
            "Campbell",
            "Parker",
            "Evans",
            "Edwards",
            "Collins",
            "Stewart",
            "Sanchez",
            "Morris",
            "Rogers",
            "Reed",
            "Cook",
            "Morgan",
            "Bell",
            "Murphy",
            "Bailey",
            "Rivera",
            "Cooper",
            "Richardson",
            "Cox",
            "Howard",
            "Ward",
            "Torres",
            "Peterson",
            "Gray",
            "Ramirez",
            "James",
            "Watson",
            "Brooks",
            "Kelly",
            "Sanders",
            "Price",
            "Bennett",
            "Wood",
            "Barnes",
            "Ross",
            "Henderson",
            "Coleman",
            "Jenkins",
            "Perry",
            "Powell",
            "Long",
            "Patterson",
            "Hughes",
            "Flores",
            "Washington",
            "Butler",
            "Simmons",
            "Foster",
            "Gonzales",
            "Bryant",
            "Alexander",
            "Russell",
            "Griffin",
            "Diaz",
            "Hayes"
    };
}
