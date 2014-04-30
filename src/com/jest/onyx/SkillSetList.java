 package com.jest.onyx;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

 public class SkillSetList extends ListActivity { 

     static final String[] COUNTRIES = new String[] {

         "Elbow Angle", "Release Speed", "Release Angle"
     };

     /** Called when the activity is first created. */
     @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setListAdapter(new ArrayAdapter < String > (this,
                 android.R.layout.simple_list_item_1, COUNTRIES));
         getListView().setTextFilterEnabled(true);
     }

     @Override
     protected void onListItemClick(ListView l, View v, int position, long id) {
         // TODO Auto-generated method stub
         super.onListItemClick(l, v, position, id);
         
//         Intent intent = new Intent(getBaseContext(), SkillListDetail.class);
//         intent.putExtra("SKILL_ID", id);
//         startActivity(intent);

         startActivity(new Intent(this, SkillListDetail.class));
     }

 }