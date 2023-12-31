// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import model.Filter.TransactionFilter;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    @Test
    public void testFilterByAmount() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Add multiple transactions with different categories
        Transaction transaction1 = new Transaction(50.0, "food");
        Transaction transaction2 = new Transaction(100.0, "entertainment");
        Transaction transaction3 = new Transaction(75.0, "travel");
        model.addTransaction(transaction1);
        model.addTransaction(transaction2);
        model.addTransaction(transaction3);

        // Apply amount filter and check that only matching transactions are returned
        double filterAmount = 100.0;
        TransactionFilter filter = new AmountFilter(filterAmount);
        List<Transaction> filteredTransactions = filter.filter(model.getTransactions());
        assertEquals(1, filteredTransactions.size());
        Transaction filteredTransaction = filteredTransactions.get(0);
        assertEquals(transaction2, filteredTransaction);
    }

    @Test
    public void testFilterByCategory() {
       // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Add multiple transactions with different categories
        Transaction transaction1 = new Transaction(50.0, "food");
        Transaction transaction2 = new Transaction(100.0, "entertainment");
        Transaction transaction3 = new Transaction(75.0, "travel");
        model.addTransaction(transaction1);
        model.addTransaction(transaction2);
        model.addTransaction(transaction3);

        // Apply category filter and check that only matching transactions are returned
        String filterCategory = "entertainment";
        TransactionFilter filter = new CategoryFilter(filterCategory);
        List<Transaction> filteredTransactions = filter.filter(model.getTransactions());
        assertEquals(1, filteredTransactions.size()); // Only one transaction should match
        Transaction filteredTransaction = filteredTransactions.get(0);
        assertEquals(transaction2, filteredTransaction); // Check that the correct transaction is returned
    }

    @Test
    public void testUndoEmptyList() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Attempt to undo if the list is empty. The Undo button should be disabled.
        assertFalse(view.getUndoBtn().isEnabled());
    }
    
    @Test
    public void testUndoAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Add a transaction and check that it is added to the model
        //Transaction transaction = new Transaction(50.0, "food");
        controller.addTransaction(50, "food" );
        assertEquals(2,view.getTableModel().getRowCount());
        //assertEquals(1, model.getTransactions().size());

        assertEquals(50.0, getTotalCost(), 0.01);

        // Remove the transaction and check that it is removed from the model
        controller.undo(0);
        //assertEquals(0, model.getTransactions().size());
        assertEquals(1,view.getTableModel().getRowCount());
        // check that the total cost is 0
        assertEquals(0, getTotalCost(), 0.01);
        

        
    }

    
}
