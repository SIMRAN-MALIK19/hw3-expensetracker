package controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;
import view.ExpenseTrackerView;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /**
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    view.enableUndoButton();
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}

  }
  //method to remove a transaction
  public void undo(int row){
    List<Transaction> transactions = model.getTransactions();
    if(transactions!=null)
    {
      if (row >= 0) {
        model.removeTransaction(transactions.get(row)); // call the remove transactrion method to remove the selected transaction
        refresh();
        JOptionPane.showMessageDialog(view, "Selected transaction removed");
        view.toFront();
      }
    }
    if(transactions.isEmpty()){
      JOptionPane.showMessageDialog(view, "Nothing to undo"); //if there are no transactions in the list
      view.toFront();
    }

  }
 
}
