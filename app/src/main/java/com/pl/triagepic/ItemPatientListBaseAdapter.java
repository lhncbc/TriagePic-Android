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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pl.triagepic.Patient.Age;

import java.util.ArrayList;

public class ItemPatientListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemPatientView> itemDetailsrrayList;
	
	private Integer[] imgid = {
			R.drawable.ic_menu_camera,
			R.drawable.ic_menu_compose,
			R.drawable.ic_menu_crop,
			R.drawable.ic_menu_gallery
			};
	
	private Integer[] zoneImgId = {
			R.drawable.cell_shape_green,
			R.drawable.cell_shape_light_green,
			R.drawable.cell_shape_yellow,
			R.drawable.cell_shape_red,
			R.drawable.cell_shape_gray,
			R.drawable.cell_shape_black,
			R.drawable.cell_shape_white
			};

	private LayoutInflater l_Inflater;

	public ItemPatientListBaseAdapter(Context context, ArrayList<ItemPatientView> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

    public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
        final View view = convertView;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_patient, null);
			holder = new ViewHolder();
			holder.ck_itemCurSel = (CheckBox) convertView.findViewById(R.id.checkBoxSel);
            holder.itemProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.imageViewPhoto);
            holder.txt_itemName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.txt_itemPageNumber = (TextView) convertView.findViewById(R.id.textPageNumber);
			holder.txt_itemLine1 = (TextView) convertView.findViewById(R.id.textViewLine1);
			holder.txt_itemLine2 = (TextView) convertView.findViewById(R.id.textViewLine2);
			holder.vZone = (View) convertView.findViewById(R.id.viewZone);
			holder.txt_itemComments = (TextView) convertView.findViewById(R.id.textViewComments);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.pos = position;	
		holder.ck_itemCurSel.setChecked(itemDetailsrrayList.get(position).getCheckStatus());
		holder.ck_itemCurSel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
				if (isChecked){
//					holder.txt_itemName.setTextColor(Color.RED);
					
				}
				else{
//					holder.txt_itemName.setTextColor(Color.WHITE);					
				}
				itemDetailsrrayList.get(holder.pos).setCheckStatus(isChecked);

             //   android.widget.CheckBox cb = ((CheckBox)view).getApplicationWindowToken().queryLocalInterface()findViewById(R.id.checkBoxSelAll);

             //   MainActivity ma =

				//CheckBox ckCheckAll = getApplicationContext().findViewById(R.id.checkBoxSelAll);
				//ckCheckAll.setChecked(false);

			}			
		});

		String strName = itemDetailsrrayList.get(position).getName();
		strName.trim();
		if (strName.equalsIgnoreCase("unknown") == true){
			strName = "Unknown";
		}
		if (strName.isEmpty() == true){
			strName = "Unknown";
		}
        if (strName.compareToIgnoreCase(" ") == 0){
            strName = "Unknown";
        }
		holder.txt_itemName.setText(strName);

        String strPageNumber = itemDetailsrrayList.get(position).getPageNumber();
        strPageNumber.trim();
        holder.txt_itemPageNumber.setText(strPageNumber);

		String strGender = itemDetailsrrayList.get(position).getGender();
		if (strGender.equalsIgnoreCase("unknown") == true){
			strGender = "?";
		}
		String strAge = itemDetailsrrayList.get(position).getAge();
		if (strAge.equalsIgnoreCase(Age.ADULT.name) == true){
			strAge = "18+";
		}
		else if (strAge.equalsIgnoreCase(Age.PEDIATRIC.name) == true){
			strAge = "0-17";
		}
		else {
			strAge = "?";			
		}		
		String strPrefixPid = itemDetailsrrayList.get(position).getPrefixPid();
		if (strPrefixPid.equalsIgnoreCase("unknown") == true){
			strPrefixPid = "?";
		}
		String strPid = itemDetailsrrayList.get(position).getPatienId();
		if (strPid.equalsIgnoreCase("unknown") == true || strPid.equalsIgnoreCase("0")){
			strPid = "?";
		}
//		holder.txt_itemLine1.setText(
//				"Gender: " + strGender + "; " + 
//				"Age: " + strAge + "; " + 
//				"PID: " + strPrefixPid + "-" + strPid 
//		);
		if (strPrefixPid.isEmpty() || strPrefixPid.equalsIgnoreCase("0")){
			holder.txt_itemLine1.setText(
					"Gender: " + itemDetailsrrayList.get(position).getGender() + "; " + 
					"Age: " + itemDetailsrrayList.get(position).getAge() + "; " + 
					"ID: " + itemDetailsrrayList.get(position).getId() + "; " + 
					"PID: " + itemDetailsrrayList.get(position).getPatienId()
			);
		}
		else {
			holder.txt_itemLine1.setText(
					"Gender: " + itemDetailsrrayList.get(position).getGender() + "; " + 
					"Age: " + itemDetailsrrayList.get(position).getAge() + "; " + 
					"ID: " + itemDetailsrrayList.get(position).getId() + "; " +
//                            "PID: " + strPrefixPid + "-" + itemDetailsrrayList.get(position).getFpid()
                            // no '-'
							/**
                             * not fpid, use patient id
							 * 8.0.9
							 */
//							"PID: " + strPrefixPid + itemDetailsrrayList.get(position).getFpid()
					"PID: " + strPrefixPid + itemDetailsrrayList.get(position).getPatienId()
			);
		}
		holder.txt_itemLine2.setText(
				"Hospital: " + itemDetailsrrayList.get(position).getHospital() + "; " + 
				"Event: " + itemDetailsrrayList.get(position).getEvent() 
		);
		
//		holder.vZone.setBackgroundResource(itemDetailsrrayList.get(position).getZone().color);
		holder.vZone.setBackgroundResource(itemDetailsrrayList.get(position).getMyZone().color);
		holder.txt_itemComments.setText(itemDetailsrrayList.get(position).getMyZone().name);

        ArrayList<Image> images = itemDetailsrrayList.get(position).getImages();
		holder.itemImage.setImageDrawable(itemDetailsrrayList.get(position).getPhoto());

		if (images.size() <= 0) {
            holder.itemProgressBar.setVisibility(View.GONE);
            holder.itemImage.setVisibility(View.VISIBLE);
			holder.itemImage.setImageResource(R.drawable.questionhead);
		}
		else {
            holder.itemProgressBar.setVisibility(View.GONE);
            holder.itemImage.setVisibility(View.VISIBLE);
            holder.itemImage.setImageDrawable(itemDetailsrrayList.get(position).getPhoto());
		}
//		holder.itemImage.setScaleType(ScaleType.FIT_XY);
		holder.itemImage.setScaleType(ScaleType.CENTER_CROP);
		return convertView;
	}
	
	static class ViewHolder {
		int pos;
		CheckBox ck_itemCurSel;
        ProgressBar itemProgressBar;
		ImageView itemImage;
		TextView txt_itemName;
        TextView txt_itemPageNumber;
		TextView txt_itemLine1;
		TextView txt_itemLine2;
		TextView txt_itemComments;
		View vZone;
	}
}
