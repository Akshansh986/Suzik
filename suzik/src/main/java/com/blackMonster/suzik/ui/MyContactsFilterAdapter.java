package com.blackMonster.suzik.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.ui.Screens.ContactsData;
import com.blackMonster.suzik.ui.Screens.TimelineFilterActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by home on 2/14/2015.
 */
public class MyContactsFilterAdapter extends BaseAdapter  {
    private static final String TAG = "MyContactsFilterAdapter";
    public static int count;
    ArrayList<ContactsData> contactsFilterList;
    ArrayList<ContactsData> checkedContactsFilterList=new ArrayList<ContactsData>();
    ArrayList<ContactsData> uncheckedContactsFilterList=new ArrayList<ContactsData>();
    ArrayList<ContactsData> errorContactsFilterList=new ArrayList<ContactsData>();


    Context context;
    LayoutInflater inflater;

    public MyContactsFilterAdapter(ArrayList<ContactsData> contactsFilterList,Context context) {
        this.contactsFilterList=contactsFilterList;
        errorContactsFilterList= ContactsFilterErrorTable.getAllData(context);
        ContactsFilterErrorTable.removeAll(context);

        for(int i=0;i<errorContactsFilterList.size();i++)
            Log.d(TAG, "ERROR LIST" + errorContactsFilterList.get(i).getContactName() + errorContactsFilterList.get(i).getFilterStatus());




        this.contactsFilterList.removeAll(errorContactsFilterList) ;

        for(int i=0;i<this.contactsFilterList.size();i++)
            Log.d(TAG,"Contacts-ERROR LIST"+this.contactsFilterList.get(i).getContactName()+this.contactsFilterList.get(i).getFilterStatus());

        errorContactsFilterList.addAll(contactsFilterList);


        for(int i=0;i<errorContactsFilterList.size();i++)
            Log.d(TAG,"FINAL LIST"+errorContactsFilterList.get(i).getContactName()+errorContactsFilterList.get(i).getFilterStatus());

        this.contactsFilterList = errorContactsFilterList;


        count=getFilterCount();
        this.context=context;
        inflater = LayoutInflater.from(context.getApplicationContext());
        setLockOnData();

    }

    private void sortListAlphabetically(ArrayList<ContactsData> contactsFilterList) {
        Collections.sort(this.contactsFilterList);
   }

    private int getFilterCount() {
        int filtercount=0;
        for(int i=0;i<contactsFilterList.size();i++){
           if(contactsFilterList.get(i).getFilterStatus())
           {   checkedContactsFilterList.add(contactsFilterList.get(i));
               filtercount++;
           }
            else{
               uncheckedContactsFilterList.add(contactsFilterList.get(i));
           }

        }

        Log.d(TAG,filtercount+"");
        return filtercount;
    }

    public void setLockOnData() {
        if(contactsFilterList.size()<=TimelineFilterActivity.NOOFCONTACTS){
            for(int i=0;i<contactsFilterList.size();i++){
                contactsFilterList.get(i).setLock(true);

            }
        }
        else{
            if(count<=TimelineFilterActivity.NOOFCONTACTS){
                lockData();
            }
            else{
               for(int i=0;i<contactsFilterList.size();i++){
                    contactsFilterList.get(i).setLock(false);

                }
            }
        }

     }

    @Override
    public int getCount() {
        return contactsFilterList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactsFilterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.my_contacts_filter_row, null);
        }

        TextView textView=((TextView) convertView.findViewById(R.id.name));
        textView.setText(contactsFilterList.get(position).getContactName());
        CheckBox checkBox=(CheckBox)convertView.findViewById(R.id.status);
        checkBox.setChecked(contactsFilterList.get(position).getFilterStatus());



            if (contactsFilterList.get(position).isLocked()) {
                textView.setTextColor(R.color.locked);
                convertView.findViewById(R.id.contact_row).setBackgroundResource(R.color.lockedbg);
                convertView.findViewById(R.id.lockicon).setVisibility(View.VISIBLE);
                ((ImageView) convertView.findViewById(R.id.lockicon)).setImageResource(R.drawable.lockicon);
                checkBox.setClickable(false);
                convertView.setHasTransientState(true);

            } else {
                convertView.findViewById(R.id.lockicon).setVisibility(View.GONE);

                if (convertView.hasTransientState()) {
                    convertView.setHasTransientState(false);
                }
                checkBox.setOnClickListener(new MyClickListener(context, this, contactsFilterList.get(position)));
            }

        return convertView;

    }


    public void unlockData() {
        for(int i=0;i<contactsFilterList.size();i++){
                contactsFilterList.get(i).setLock(false);

        }
//notifyDataSetChanged();
    }

    public void lockData() {
        for(int i=0;i<contactsFilterList.size();i++){
            if(contactsFilterList.get(i).getFilterStatus()) {
                contactsFilterList.get(i).setLock(true);
            }
            else
            {  contactsFilterList.get(i).setLock(false);


            }
        }
    //    notifyDataSetChanged();
    }


    public void addToCheckedList(ContactsData data) {
        checkedContactsFilterList.add(data);
        uncheckedContactsFilterList.remove(data);
        Collections.sort(checkedContactsFilterList);
        Collections.sort(uncheckedContactsFilterList);
         ArrayList<ContactsData> newList= new ArrayList<ContactsData>();
         newList.addAll(checkedContactsFilterList);

        /*ContactsData seperator= new ContactsData("DSD","dsd",false);
        seperator.setLock(true);
        newList.add(seperator);
        */
        newList.addAll(uncheckedContactsFilterList);
        contactsFilterList=newList;
    }

    public void addToUncheckedList(ContactsData data) {

            uncheckedContactsFilterList.add(data);
            checkedContactsFilterList.remove(data);
             Collections.sort(checkedContactsFilterList);
            Collections.sort(uncheckedContactsFilterList);
        ArrayList<ContactsData> newList= new ArrayList<ContactsData>();
        newList.addAll(checkedContactsFilterList);
       /* ContactsData seperator= new ContactsData("DSD","dsd",false);
        seperator.setLock(true);
        newList.add(seperator);*/
        newList.addAll(uncheckedContactsFilterList);
        contactsFilterList=newList;

    }
}
