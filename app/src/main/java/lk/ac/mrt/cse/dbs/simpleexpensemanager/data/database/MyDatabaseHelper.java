package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "200188J.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_1 = "account";
    private static final String TABLE_NAME_2 = "transactions";
    private static final int DEFAULT_LIMIT = 0;

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_1 +
                " (account_no TEXT PRIMARY KEY ,"+
                "bank_name TEXT  ,"+
                "bank_acc_holder TEXT, "+
                "bank_balance REAL"
                +")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_2 +
                " (transaction_no INTEGER  PRIMARY KEY AUTOINCREMENT,"+
                "account_no TEXT  ,"+
                "date TEXT, "+
                "expense_type TEXT ,"+
                "amount REAL"
                +")");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_2);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String table_name, ContentValues contentValues){
        android.database.sqlite.SQLiteDatabase my_project = this.getWritableDatabase();
        long result;
        try{
            result = my_project.insertOrThrow(table_name, null,contentValues);
        }catch(Exception e){
            result = -1;
            System.out.println("Data Insertion Error");
        }

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateData(String table_name,ContentValues content, String[ ] condition){
        android.database.sqlite.SQLiteDatabase my_project = this.getWritableDatabase();
        String cond = condition[0]+" "+condition[1]+" ? ";
        String[] args = {condition[2]};

        long result;
        try{
            result = my_project.update(table_name, content,cond,args);
        }catch (Exception e){

            result = -1;
        }

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Integer deleteData(String table_name, String column, String id){
        android.database.sqlite.SQLiteDatabase my_project = this.getWritableDatabase();
        return my_project.delete(table_name, column+" = ?", new String[] {id});
    }


}
