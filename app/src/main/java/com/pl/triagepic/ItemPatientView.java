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

import android.graphics.drawable.Drawable;

import com.pl.triagepic.Patient.MyZone;

import java.util.ArrayList;

public class ItemPatientView {
	private boolean curSel;

	private long id;
	private String prefixPid;
	private String patientId;
	private String fpid;
	private String uuid;
	private String name;
	private String age;
	private String gender;
	private Drawable photo;
	private ArrayList<Image> images;

	private String comment;
	private String event;
	private String hospital;
	private MyZone myZone;
	
	private boolean isChecked;

    private String pageNumber;

	public void ItemPatientView() {
		isChecked = false;
	}
	
	public void ItemPatientView(boolean isChecked){
		this.isChecked = isChecked;
	}

	ItemPatientView(){
		images = new ArrayList<Image>();
	}
	
	public void setId(long id){
		this.id = id;
	}
	public long getId() {
		return this.id;
	}
	
	public void setUuid(String uuid){
		this.uuid = uuid;
	}
	public String getUuid() {
		return this.uuid;
	}

	public ArrayList<Image> getImages() {
		return this.images;
	}
	public void setImages(ArrayList<Image> images){
		this.images = images;
	}
	
	public boolean getCurSel() {
		return curSel;
	}
	public void setCurSel(boolean curSel){
		this.curSel = curSel;
	}
	
	public String getPrefixPid() {
		return this.prefixPid;
	}
	public void setPrefixPid(String prefixPid) {
		this.prefixPid = prefixPid;
	}
	
	public String getPatienId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getFpid() {
		return fpid;
	}
	public void setFpid(String fpid) {
		this.fpid = fpid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	

	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}	

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}	
	
	public Drawable getPhoto() {
		return photo;
	}
	public void setPhoto(Drawable photo){
		this.photo = photo;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}

	public MyZone getMyZone() {
		return myZone;
	}
	public void setMyZone(MyZone myZone) {
		this.myZone = myZone;
	}

	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	
	public boolean getCheckStatus() {
		return isChecked;
	}
	public void setCheckStatus(boolean isChecked) {
		this.isChecked = isChecked;
	}

    public void setPageNumber(String pageNumber){this.pageNumber = pageNumber;}
    public String getPageNumber(){return this.pageNumber;}
}
