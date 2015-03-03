package com.blackMonster.suzik.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.ui.Screens.ContactsData;

import java.util.ArrayList;

public class ContactsFilterErrorTable {

	private static final String TAG = "ContactsFilterErrorTable";

    private static final String C_NO = "pnumber";
	private static final String C_NAME = "contactName";
	private static final String C_STATUS= "filterStatus";
    private static final String C_LOCK= "lock";
    private static final String C_TYPE= "type";


    private static final String TABLE = "ContactsFilterErrorTable";

	public static void createTable(SQLiteDatabase db) {
		String sql = String
				.format("create table %s"
						+ "(%s text primary key,%s text,%s INTEGER,%s INTEGER,%s INTEGER)",
						TABLE,C_NO,C_NAME,C_STATUS,C_LOCK,C_TYPE);
		db.execSQL(sql);
	}


	public static ArrayList<ContactsData> getAllData(Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
	    ArrayList<ContactsData> errorContactFilterList=new ArrayList<ContactsData>();

		Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {


                String number=cursor.getString(cursor.getColumnIndex(C_NO));
               // String fnumber="+"+number;
                String contactName= cursor.getString(cursor.getColumnIndex(C_NAME));
                Boolean filterStatus;
                if(cursor.getInt(cursor.getColumnIndex(C_STATUS))==1){
                    filterStatus=true;
                    Log.d(TAG,"read status true");

                }
                else
                {
                    filterStatus=false;
                    Log.d(TAG,"read status false");

                }
                Boolean lock;
                if(cursor.getInt(cursor.getColumnIndex(C_LOCK))==1){
                    lock=true;
                    Log.d(TAG,"read lock true");
                }
                else
                {
                    lock=false;
                    Log.d(TAG,"read lock false");


                }
                int type=cursor.getInt(cursor.getColumnIndex(C_TYPE));

                errorContactFilterList.add(new ContactsData(number,contactName,filterStatus,type));
                Log.d(TAG,number+contactName+filterStatus+lock+type+"");
                cursor.moveToNext();

			}

			cursor.close();

		}

        Log.d(TAG,"$$$$$$$$$$$$$$$   :"+errorContactFilterList.size());

		return errorContactFilterList;
	}

	public static void insert(ContactsData data, Context context) {
		Log.d(TABLE, "insert");
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        data.setType(ContactsData.TYPE_ERROR);

		ContentValues values = new ContentValues();
        Log.d("TAG",data.toString());
		values.put(C_NO,data.getNumber());
		values.put(C_NAME,data.getContactName());
        if(data.getFilterStatus()){
            values.put(C_STATUS,1);
         Log.d(TAG,"C_STATUStrue ins");
        }
        else{
            values.put(C_STATUS,0);
            Log.d(TAG,"C_STATUSfalse ins");

        }
        if(data.isLocked()){
            values.put(C_LOCK,1);
            Log.d(TAG,"C_LOCKtrue ins");

        }
        else{
            values.put(C_LOCK,0);
            Log.d(TAG,"C_LOCKfalse ins");

        }
        values.put(C_TYPE, data.getType());


        db.insert(TABLE, null, values);

	}
   public static boolean remove(String number, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
       Log.d(TAG,"removed 1"+number);
        return db.delete(TABLE, C_NO+ "='"+number+"'", null) > 0;
    }

	static boolean removeAll( Context context) {
		SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        Log.d(TAG,"removed all");
		return db.delete(TABLE,null, null) > 0;
	}
	


		
		
		



}
