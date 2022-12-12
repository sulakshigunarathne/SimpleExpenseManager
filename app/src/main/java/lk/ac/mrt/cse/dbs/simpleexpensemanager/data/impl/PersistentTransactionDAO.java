package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.MyDatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private MyDatabaseHelper my_project;

    private static final String TABLE_NAME = "transactions";
    private static final String COLUMN_AccNo = "account_no";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_EXPENSE = "expense_type";
    private static final String COLUMN_AMOUNT ="amount";

    public PersistentTransactionDAO(MyDatabaseHelper my_project) {
        this.my_project=my_project;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        if(expenseType == ExpenseType.EXPENSE){
            PersistentAccountDAO persistentAccountDAO = new PersistentAccountDAO(this.my_project);
            try {
                Account user = persistentAccountDAO.getAccount(accountNo);
                if(user.getBalance() < amount){
                    return;
                }
            }catch (Exception e){
                System.out.println("Invalid Account");
            }
        }
        String Str_Date = date.toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AccNo, accountNo);
        contentValues.put(COLUMN_DATE, Str_Date);
        contentValues.put(COLUMN_EXPENSE, getStringExpense(expenseType));
        contentValues.put(COLUMN_AMOUNT, amount);
        this.my_project.insertData(TABLE_NAME, contentValues);
    }

    private String getStringExpense(ExpenseType expenseType) {
        if(expenseType == ExpenseType.EXPENSE){
            return "Expense";
        }else{
            return "Income";
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase sqldb = my_project.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        if(cursor.getCount() != 0) {

        while (cursor.moveToNext()) {
            String string_date =cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            String accountNo = cursor.getString(cursor.getColumnIndex(COLUMN_AccNo));
            String expenseType = cursor.getString(cursor.getColumnIndex(COLUMN_EXPENSE));
            double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));
            Date date = null;
            try {
                date = gettingDate(string_date);}
            catch (ParseException e) {
                e.printStackTrace();}

            Transaction transaction = new Transaction(date,accountNo,getExpense(expenseType),amount );
            transactions.add(transaction);
        }
    }
    cursor.close();
        return transactions;}

    public ExpenseType getExpense(String expense){
        if(expense.equals("Expense")){
            return ExpenseType.EXPENSE;
        }else{
            return ExpenseType.INCOME;
        }
    }



    private Date gettingDate(String string_date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = new Date();
        date = dateFormat.parse(string_date);
        return date;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase sqldb = my_project.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery("SELECT * FROM "+TABLE_NAME+" LIMIT "+ "? ",new String[]{Integer.toString(limit)});
        List<Transaction> transactions = new ArrayList<Transaction>();
        if(cursor.getCount() != 0) {

            while (cursor.moveToNext()) {
                String dateS = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String accountNo = cursor.getString(cursor.getColumnIndex(COLUMN_AccNo));
                String expenseType = cursor.getString(cursor.getColumnIndex(COLUMN_EXPENSE));
                double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));
                Date date = null;
                try {
                    date = gettingDate(dateS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Transaction transaction = new Transaction(date,accountNo,getExpense(expenseType),amount );
                transactions.add(transaction);
            }
        }
        cursor.close();
        return transactions;
    }

}
