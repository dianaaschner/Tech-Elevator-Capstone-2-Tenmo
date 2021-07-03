package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private UserService userService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL), new UserService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService, UserService userService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.transferService = transferService;
        this.userService = userService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        Double balance = accountService.getBalance(currentUser.getUser().getId());
        System.out.println("Your current account balance is: $" + balance);
    }

    private void viewTransferHistory() {
        Transfer[] transfers = transferService.listTransfersByUser(currentUser.getUser().getId());
        if (transfers != null) {
            System.out.println("--------------------------------------------");
            System.out.println("Transfers");
            System.out.println("ID			From/To			Amount");
            System.out.println("--------------------------------------------");
            for (Transfer transfer : transfers) {
                System.out.print(transfer.getTransferId());
                if (transfer.getAccountTo().equals(accountService.getAccountId(currentUser.getUser().getId()))) {
                    System.out.print("		From: " + accountService.getUsername(transfer.getAccountFrom()));
                } else if (transfer.getAccountFrom().equals(accountService.getAccountId(currentUser.getUser().getId()))) {
                    System.out.print("		To: " + accountService.getUsername(transfer.getAccountTo()));
                }
                System.out.println("		$" + transfer.getAmount());

            }
            System.out.println("---------");
            String transferId = console.getUserInput("Please enter transfer ID to view details (0 to cancel)");
            if (transferId.equals("0")) {
            } else {
                Transfer transfer = transferService.getTransferById(Integer.parseInt(transferId));
                System.out.println("--------------------------------------------");
                System.out.println("Transfer Details");
                System.out.println("--------------------------------------------");
                System.out.println(" Id: " + transfer.getTransferId());
                System.out.println(" From: " + accountService.getUsername(transfer.getAccountFrom()));
                System.out.println(" To: " + accountService.getUsername(transfer.getAccountTo()));
                System.out.println(" Type: " + transfer.getTransferType());
                System.out.println(" Status: " + transfer.getTransferStatus());
                System.out.println(" Amount: " + transfer.getAmount());
                System.out.println();
            }
        }
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
        User[] users = userService.findAll();
        if (users != null) {
            System.out.println("--------------------------------------------");
            System.out.println("Users");
            System.out.println("ID			Name");
            System.out.println("--------------------------------------------");
            for (User user : users) {
                System.out.println(user.userToString());
            }
            System.out.println("---------");
            String destinationAccount = console.getUserInput("Enter ID of user you are sending to (0 to cancel)");
            if (destinationAccount.equals("0")) {
            } else {
                String amount = console.getUserInput("Enter amount");
                if (Double.parseDouble(amount) > accountService.getBalance(currentUser.getUser().getId())) {
                    System.out.println();
                    System.out.println("Insufficient funds. Returning to main menu.");
                } else {
                    transferService.createTransfer(accountService.getAccountId(currentUser.getUser().getId()), accountService.getAccountId(Integer.parseInt(destinationAccount)), amount);
                }
            }
        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
