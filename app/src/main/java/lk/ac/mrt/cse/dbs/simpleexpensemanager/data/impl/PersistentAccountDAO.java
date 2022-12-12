package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.MyDatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
public class PersistentAccountDAO implements AccountDAO {

    private final MyDatabaseHelper my_project;

    private static final String TABLE_NAME = "account";
    private static final String COLUMN_PK = "account_no";
    private static final String COLUMN_NAME = "bank_name";
    private static final String COLUMN_HOLDER ="bank_acc_holder";
    private static final String COLUMN_BALANCE = "bank_balance";
    


    public PersistentAccountDAO(MyDatabaseHelper my_project) {
        this.my_project=my_project;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqldb = my_project.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery("SELECT "+ COLUMN_PK+" FROM "+TABLE_NAME,null);
        List<String> account_nums = new ArrayList<String>();
        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                account_nums.add(cursor.getString(cursor.getColumnIndex(COLUMN_PK)));
            }
        }
        cursor.close();
        return account_nums;
    }

    @Override
    public List<Account> getAccountsList() {

        SQLiteDatabase sqldb = my_project.getReadableDatabase() ;
        Cursor cursor = sqldb.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        List<Account> accounts = new ArrayList<Account>();
        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String account_no = cursor.getString(cursor.getColumnIndex(COLUMN_PK));
                String bank_name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String bank_acc_holder = cursor.getString(cursor.getColumnIndex(COLUMN_HOLDER));
                double bank_balance = cursor.getDouble(cursor.getColumnIndex(COLUMN_BALANCE));
                Account account = new Account(account_no, bank_name,bank_acc_holder,bank_balance);
                accounts.add(account);
        }
    }
        cursor.close();
        return accounts;
}
    @Override
    public Account getAccount(String account_no) throws InvalidAccountException {
        String[] condition = {COLUMN_PK, "=",account_no};
        SQLiteDatabase sqldb = my_project.getReadableDatabase();
        Cursor cursor = sqldb.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        if(cursor.getCount() == 0){
            throw new InvalidAccountException("Invalid Account Number");
        }
        String account_num = "";
        String bank_name = "";
        double bank_balance = 0;
        String bank_acc_holder = "";

        while(cursor.moveToNext()){
            account_num = cursor.getString(cursor.getColumnIndex(COLUMN_PK));
            bank_name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            bank_acc_holder = cursor.getString(cursor.getColumnIndex(COLUMN_HOLDER));
            bank_balance = cursor.getDouble(cursor.getColumnIndex(COLUMN_BALANCE));
            

        }

        cursor.close();
        Account account = new Account(account_num,bank_name,bank_acc_holder,bank_balance);
        return account;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PK, account.getAccountNo());
        contentValues.put(COLUMN_NAME, account.getBankName());
        contentValues.put(COLUMN_HOLDER, account.getAccountHolderName());
        contentValues.put(COLUMN_BALANCE, account.getBalance());
        

        my_project.insertData(TABLE_NAME, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int result = my_project.deleteData(TABLE_NAME,COLUMN_PK,accountNo);
        if(result == 0){
            throw new InvalidAccountException("Account number is invalid");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance = 0;
        double total = 0;
        try{
            Account acc = getAccount(accountNo);
            balance = acc.getBalance();
        }catch(Exception e){
            throw new InvalidAccountException("Invalid Account Number");
        }

        if (expenseType == ExpenseType.EXPENSE){
            if(balance < amount){
                throw new InvalidAccountException("Insufficient Account Balance");
            }
            total = balance-amount;
        }else{
            total = amount +balance;
        }
        String[] condition = {COLUMN_PK,"=",accountNo};
        ContentValues accContent = new ContentValues();
        accContent.put(COLUMN_BALANCE, total);
        boolean result = my_project.updateData("account",accContent,condition);
        if(!result){
            throw new InvalidAccountException("Account number is invalid");
        }
    }

}
