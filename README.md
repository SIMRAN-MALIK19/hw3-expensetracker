# Solution files for hw2
We have included a new feature in our application which is used to delete any transaction from the transaction history. 
We have added a button called "undo" using which we can delete any row. We simply have to select a row and click on the "undo" button. 
This makes a call to the controller code which has the undo method that uses the model.removeTransaction() to delete the specific transaction. Once the transaction is removed from the list, the view is refreshed and the total cost is also updated.

We have also included test cases to test the robustness of the application. We are testing different features of the application by providing different inputs under different condition to check if the application responds as expected.