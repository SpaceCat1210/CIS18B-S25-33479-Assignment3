import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// This class is for when people input a negative amount it would throw an error saying you cant do that.
class NegativeDepositException extends Exception
{
    public NegativeDepositException(String message)
    {
        super(message);
    }
}
// A class for when people try to take out more money then they have in the balance itll say an error.
class OverdrawException extends Exception
{
    public OverdrawException(String message)
    {
        super(message);
    }
}
// This class makes sure that a person has an account before letting them do anything else.
class InvalidAccountOperationException extends Exception
{
    public InvalidAccountOperationException(String message)
    {
        super(message);
    }
}
// This interface lets objects be notified when changes happen in the bank account class.
interface Observer
{
    void update(String message);
}

class TransactionLogger implements Observer
{
    public void update(String message){
        System.out.println("Log: " + message);
    }
}

class BankAccount
{
    private String accountName;
    private int balance;
    private boolean isActive;
    List<Observer> observers = new ArrayList<>();
//Here is where the NegativeDepositException is in place, so people cant input a negative ammount into the account.
    public BankAccount(String accountName, int initialBalance) throws NegativeDepositException
    {
        if (initialBalance < 0) throw new NegativeDepositException("Balance cannot be negative.");
        this.accountName = accountName;
        this.balance = initialBalance;
        this.isActive = true;
    }

    public void addObserver(Observer observer)
    {
        observers.add(observer);
    }

    void notifyObservers(String message)
    {
        for (Observer observer : observers)
        {
            observer.update(message);
        }
    }
// Here is the code for when the person deposits into the account, it checks for a negative amount and if the account is closed then it tells the observers about the information.
    public void deposit(int amount) throws NegativeDepositException, InvalidAccountOperationException
    {
        if (!isActive) throw new InvalidAccountOperationException("Account is closed, cannot deposit.");
        if (amount < 0) throw new NegativeDepositException("Cannot deposit a negative amount.");
        balance += amount;
        notifyObservers("Deposited $" + amount + " New balance: $" + balance);
    }
// Here is the code for a withdraw, checks for a overdraw amount and if the account is closed (The rest of these will have the InvalidAccountOperationException and notifyObservers).
    public void withdraw(int amount) throws OverdrawException, InvalidAccountOperationException
    {
        if (!isActive) throw new InvalidAccountOperationException("Account is closed, cannot withdraw.");
        if (amount > balance) throw new OverdrawException("You are too broke. Current balance: $" + balance);
        balance -= amount;
        notifyObservers("Withdrew $" + amount + " New balance: $" + balance);
    }
// Shows the balance
    public int getBalance() throws InvalidAccountOperationException{
        if (!isActive) throw new InvalidAccountOperationException("Account is Closed.");
        return balance;
    }
// Closes the account 
    public void closeAccount()
    {
        isActive = false;  
        notifyObservers("Account closed.");
    }

    public boolean isActive()
    {
        return isActive;
    }

    public String getAccountName()
    {
        return accountName;
    }
}
// This is the Decorator class in the script which applies more rules to the script.
class SecureBankAccount extends BankAccount {
    BankAccount decoratedAccount;

    public SecureBankAccount(BankAccount decoratedAccount) throws NegativeDepositException, InvalidAccountOperationException
    {
        super(decoratedAccount.getAccountName(), decoratedAccount.getBalance());
        this.decoratedAccount = decoratedAccount;
    }
// Adds a limit to how much you can withdraw in the account. 
    public void withdraw(int amount) throws OverdrawException, InvalidAccountOperationException
    {
        if (amount > 500) throw new OverdrawException("Cannot take out more the $500 per transaction.");
        decoratedAccount.withdraw(amount);
    }

    public void deposit(int amount) throws NegativeDepositException, InvalidAccountOperationException
    {
        decoratedAccount.deposit(amount);
    }

    public int getBalance() throws InvalidAccountOperationException
    {
        return decoratedAccount.getBalance();
    }

    public void closeAccount()
    {
        decoratedAccount.closeAccount();
    }

    public boolean isActive()
    {
        return decoratedAccount.isActive();
    }
}
//
// For the Bank Application I just reused the first assignments code that I submmited and added while and try with the appropriate changes to max the code up top.
// The class runs the program letting people input the amounts and the account they want to create.
//
public class BankApplication {
    public static String header = "Welcome to Simple Bank System\n1. Create Account\n2. Deposit Money\n3. Withdraw Money\n4. Check Balance\n5. Close Account\n6 Exit\n\nEnter your choice: ";
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        BankAccount account = null;

        while (true) {
            System.out.println(header);
            int option = scan.nextInt();
            scan.nextLine();

            try {
                // Creates the Account, forces people to do this first before doing anything else.
                if (option == 1) {
                System.out.println("Enter account holder name: ");
                String accountName = scan.nextLine();
                System.out.println("Enter initial deposit: ");
                int initialDeposit = scan.nextInt();
                account = new BankAccount(accountName, initialDeposit);
                TransactionLogger logger = new TransactionLogger();
                account = new SecureBankAccount(account);
                System.out.println("Account created successfully for " + accountName + " with balance $" + initialDeposit + "!\n");
                } else if (option == 2) {
                // Added so the person needs to make an account first before doing any action.
                if (account == null || !account.isActive()) 
                {
                    throw new InvalidAccountOperationException("No account found. Please create an account first.");
                }
                System.out.println("Enter amount to deposit: ");
                int depositAmount = scan.nextInt();
                account.deposit(depositAmount);
                System.out.println("Deposit successful! New balance: " + account.getBalance());
                } else if (option == 3) {
                if (account == null || !account.isActive()) 
                {
                    throw new InvalidAccountOperationException("No account found. Please create an account first.");
                }
                System.out.println("Enter amount to Withdraw: ");
                int withdrawAmount = scan.nextInt();
                account.withdraw(withdrawAmount);
                System.out.println("Withdrawal successful! New balance: $" + account.getBalance());
                } else if (option == 4) {
                if (account == null || !account.isActive()) 
                {
                    throw new InvalidAccountOperationException("No account found. Please create an account first.");
                }
                System.out.println("Your balance is " + account.getBalance());
                } else if (option == 5) {
                if (account == null || !account.isActive())
                {
                    throw new InvalidAccountOperationException("No active account found.");
                }
                account.closeAccount();
                System.out.println("Account closed.");
                } else if (option == 6) {
                System.out.println("Thank you for using Simple Bank System. Goodbye!");
                break;
                } else {
                System.out.println("invaild option. Please try again.");
                }
                //Catches any exceptions
            } catch (NegativeDepositException | OverdrawException | InvalidAccountOperationException e)
            {
            System.out.println("Error: " + e.getMessage());
            }   
            
        } 
        scan.close();
    }   
}