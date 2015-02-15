package com.blackMonster.suzik.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.ui.Screens.ContactsData;

import java.util.ArrayList;

/**
 * Created by home on 2/14/2015.
 */
public class MyContactsFilterAdapter extends BaseAdapter  {
    ArrayList<ContactsData> contactsFilterList;
    Context context;
    LayoutInflater inflater;

    public MyContactsFilterAdapter(ArrayList<ContactsData> contactsFilterList,Context context) {
        this.contactsFilterList = contactsFilterList;
        this.context=context;
        inflater = LayoutInflater.from(context.getApplicationContext());
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


        checkBox.setOnClickListener(new MyClickListener(this,contactsFilterList.get(position)));

        return convertView;
    }


}
