package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.support.annotation.Nullable;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.MyDatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager {
    private MyDatabaseHelper my_project;

    public PersistentExpenseManager(@Nullable Context context) {
        this.my_project = new MyDatabaseHelper(context);
        try {
            setup();
        }
        catch (Exception exception)
        {
            System.out.println("Setup error");
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {
        AccountDAO newAccountDAO = new PersistentAccountDAO(this.my_project);
        setAccountsDAO(newAccountDAO);

        TransactionDAO newTransactionDAO = new PersistentTransactionDAO(this.my_project);
        setTransactionsDAO(newTransactionDAO);
    }
}
