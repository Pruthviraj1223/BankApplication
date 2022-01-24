package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


class Customer  {

    public HashMap<String,HashMap<String,ArrayList<String>>> customerDetail = hashMapFromTextFile();

    public HashMap<String,String> customerInfo;

    public HashMap<String,ArrayList<String>> Loan = readLoanFromFile();

    Customer() throws IOException {
    }

    public boolean isNumber(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }

    public boolean isString(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }

    public String getTimeDate(){

        String timeDate = "";
        LocalTime timeObj = LocalTime.now();
        LocalDate dateObj = LocalDate.now();
        String date = String.valueOf(dateObj);
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("hh=mm");
        String ldtString = FOMATTER.format(timeObj);
        timeDate = timeDate.concat(date).concat("$").concat(ldtString);

        return timeDate;
    }

    public int getBalance(ArrayList<String> a){

        int totalBalance=0;

        for (String s : a) {

            int balance = Integer.parseInt(s.substring(17, s.length() - 1));
            String type = s.substring(s.length() - 1);

            if (type.equals("+")) {
                totalBalance = totalBalance + balance;
            } else if (type.equals("-")) {
                totalBalance = totalBalance - balance;
            }
        }
        return totalBalance;
    }


    public HashMap<String,HashMap<String,ArrayList<String>>> hashMapFromTextFile() throws IOException {

        HashMap<String, HashMap<String,ArrayList<String>>> map
                = new HashMap<>();

        HashMap<String,String> customerInfoMap = new HashMap<>();

        BufferedReader bufferedReaderOfUser;
        BufferedReader bufferedReaderOfTransaction;

        String filePathOfUser = "/home/pruthviraj/userDetails.txt";
        File fileOfUser = new File(filePathOfUser);

        //name:pass:forgot
        //name:transaction_history

        String filePathOfTransaction = "/home/pruthviraj/transaction.txt";
        File fileOfTransaction = new File(filePathOfTransaction);

        bufferedReaderOfUser = new BufferedReader(new FileReader(fileOfUser));
        bufferedReaderOfTransaction = new BufferedReader(new FileReader(fileOfTransaction));

        String lineOfUser;
        String lineOfTransaction;

        while ((lineOfUser = bufferedReaderOfUser.readLine()) != null && (lineOfTransaction = bufferedReaderOfTransaction.readLine()) != null) {

            String[] partsFromUser = lineOfUser.split(":");
            String name = partsFromUser[0];
            String pass = partsFromUser[1];
            String forgot = partsFromUser[2];

            HashMap<String,ArrayList<String>> innerMap = new HashMap<>();

            String[] partsFromTransaction = lineOfTransaction.split(":");
            String[] trans = partsFromTransaction[1].split(",");

            ArrayList<String> transaction = new ArrayList<>();

            Collections.addAll(transaction, trans);

            innerMap.put(pass,transaction);
            map.put(name, innerMap);

            customerInfoMap.put(name,forgot);
        }
        customerInfo = customerInfoMap;

        return map;
    }

    public HashMap<String,ArrayList<String>> readLoanFromFile() throws IOException {

        HashMap<String,ArrayList<String>> tempLoan = new HashMap<>();

        BufferedReader bufferedReader;

        String filePath = "/home/pruthviraj/Loan.txt";
        File file = new File(filePath);

        bufferedReader = new BufferedReader(new FileReader(file));

        String line;

        while((line=bufferedReader.readLine())!=null){

            String []parts = line.split(":");
            String name = parts[0];

            String []trans = parts[1].split(",");

            ArrayList<String> list = new ArrayList<>();

            Collections.addAll(list,trans);

            tempLoan.put(name,list);
        }

        return tempLoan;

    }

    public void createCustomer(String name, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {

        if(!customerDetail.containsKey(name))
        {
                bufferedWriter.write("Enter Password ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                String password = bufferedReader.readLine();

                int balance=0;

                while(balance<500){
                    bufferedWriter.write("Enter Initial balance (minimum balance is 500)");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    balance = Integer.parseInt(bufferedReader.readLine());
                }

                bufferedWriter.write("Enter your childhood name");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                String forgot = bufferedReader.readLine();

                customerInfo.put(name,forgot);

                HashMap<String, ArrayList<String>> inner = new HashMap<>();
                ArrayList<String> transaction = new ArrayList<>();

                String timeDate = getTimeDate();

                transaction.add(timeDate.concat("$").concat(String.valueOf(balance)).concat("+"));

                inner.put(password, transaction);
                customerDetail.put(name, inner);

                bufferedWriter.write("Account created !!!");
                bufferedWriter.newLine();
        }
        else{
            bufferedWriter.write("User with same name exist ");
        }
        bufferedWriter.flush();
    }

    public boolean login(String name,String pass){

        System.out.println("customer detail  = " + customerDetail);

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        return customerDetail.containsKey(name) && inner.containsKey(pass);

    }

    public void deleteCustomer(String name, String pass, BufferedWriter bufferedWriter) throws IOException {

        HashMap<String,ArrayList<String>> temp = customerDetail.get(name);

        if(customerDetail.containsKey(name) && temp.containsKey(pass))
        {
            customerDetail.remove(name);
            bufferedWriter.write("Account deleted successfully ");
        }
        else{
            bufferedWriter.write("Your data is not in the database ");
        }
        bufferedWriter.flush();
    }


    public void displayDetails(String name, String pass,BufferedReader bufferedReader ,BufferedWriter bufferedWriter) throws IOException {

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        if (customerDetail.containsKey(name) && inner.containsKey(pass)){
            bufferedWriter.write("Customer name is " + name + " Password is "+ pass + " and Balance is " + getBalance(inner.get(pass)));
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedReader.readLine();

            if(Loan.get(name)!=null){

                ArrayList<String> LoanList = Loan.get(name);
                String allLoans ="";

                for(String loan:LoanList){
                   String []parts = loan.split("\\$");
                    allLoans = allLoans.concat(parts[2]).concat("/").concat(parts[3]).concat("#");
                }

                bufferedWriter.write("Loan");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                bufferedWriter.write(allLoans);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }
        else{
            bufferedWriter.write("No data found");
        }
        bufferedWriter.flush();
    }

    public void forgotPassword(String name,BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {

        String forgot = customerInfo.get(name);

        bufferedWriter.write("Enter you childhood name");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        String userAnswer = bufferedReader.readLine();

        if(userAnswer.equalsIgnoreCase(forgot)){

            bufferedWriter.write("Enter your new password");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String newPass = bufferedReader.readLine();

            String passFromDatabase = String.valueOf(customerDetail.get(name).keySet());
            passFromDatabase = passFromDatabase.substring(1, passFromDatabase.length() - 1);

            HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

            ArrayList<String> list = inner.get(passFromDatabase);

            inner.put(newPass,list);

            customerDetail.put(name,inner);

            bufferedWriter.write("Password changed !!!");
            bufferedWriter.newLine();
            bufferedWriter.flush();

        }else{
            bufferedWriter.write("Answer is not matched.");
            bufferedWriter.flush();
        }
    }
}

class  Bank extends Customer {

    Bank() throws IOException {
    }

    public void depositMoney(String name, String password, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {

        HashMap<String, ArrayList<String>> inner = customerDetail.get(name);

        if (customerDetail.containsKey(name) && inner.containsKey(password)) {

            bufferedWriter.write("Enter amount you want to deposit ");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String balance = bufferedReader.readLine();

            if (isNumber(balance)) {

                int intBal = Integer.parseInt(balance);
                ArrayList<String> transaction = inner.get(password);

                int newBal = getBalance(transaction);
                newBal = newBal + intBal;

                String timeDate = getTimeDate();

                String newTransaction = timeDate.concat("$").concat(String.valueOf(intBal));
                newTransaction = newTransaction.concat("+");

                transaction.add(newTransaction);
                inner.put(password, transaction);

                customerDetail.put(name, inner);

                bufferedWriter.write("New balance is " + newBal + " ");

            } else {
                bufferedWriter.write("Enter valid number!! ");
            }

            bufferedWriter.flush();

        } else {
            bufferedWriter.write("Incorrect username or password ");
            bufferedWriter.flush();
        }
    }

    public void withdrawMoney(String name, String password, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {

        HashMap<String, ArrayList<String>> temp = customerDetail.get(name);

        if (customerDetail.containsKey(name) && temp.containsKey(password)) {

            bufferedWriter.write("Enter amount you want to withdraw ");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String balance = bufferedReader.readLine();

            if (isNumber(balance)) {

                int intBal = Integer.parseInt(balance);
                ArrayList<String> transaction = temp.get(password);

                int newBal = getBalance(transaction);
                newBal = newBal - intBal;

                if (newBal < 500) {
                    bufferedWriter.write("Can not do this transaction. Insufficient balance ");
                    bufferedWriter.flush();
                } else {

                    String timeDate = getTimeDate();
                    String newTransaction = timeDate.concat("$").concat(String.valueOf(intBal));
                    newTransaction = newTransaction.concat("-");

                    transaction.add(newTransaction);
                    temp.put(password, transaction);
                    customerDetail.put(name, temp);

                    bufferedWriter.write("New balance is " + newBal + " ");
                    bufferedWriter.flush();
                }
            } else {
                bufferedWriter.write("Enter a valid number ");
                bufferedWriter.flush();
            }
        } else {
            bufferedWriter.write("Incorrect username or password ");
            bufferedWriter.flush();
        }
    }

    public void transferMoney(String name, String password, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {

        try {

            HashMap<String, ArrayList<String>> tempOfUser = customerDetail.get(name);

            if (customerDetail.containsKey(name) && tempOfUser.containsKey(password)) {

                bufferedWriter.write("Enter payee's name ");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String payee = bufferedReader.readLine();

                bufferedWriter.write("Enter amount you want to transfer ");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                String amount = bufferedReader.readLine();

                String passFromDatabase = String.valueOf(customerDetail.get(payee).keySet());
                passFromDatabase = passFromDatabase.substring(1, passFromDatabase.length() - 1);

                HashMap<String, ArrayList<String>> tempOfPayee = customerDetail.get(payee);

                if (isNumber(amount)) {

                    int intBal = Integer.parseInt(amount);

                    ArrayList<String> transactionOfPayee = tempOfPayee.get(passFromDatabase);
                    ArrayList<String> transactionOfUser = tempOfUser.get(password);

                    int balanceOfUser = getBalance(transactionOfUser);

                    balanceOfUser = balanceOfUser - Integer.parseInt(amount);

                    if(balanceOfUser<0){
                        bufferedWriter.write("In sufficient balance ");
                        bufferedWriter.flush();
                    }
                    else {
                        String timeDate = getTimeDate();

                        String transactionFormat = timeDate.concat("$").concat(String.valueOf(intBal));

                        String newTransactionOfUser = transactionFormat.concat("-");

                        String newTransactionOfPayee = transactionFormat.concat("+");

                        transactionOfUser.add(newTransactionOfUser);
                        tempOfUser.put(password, transactionOfUser);

                        customerDetail.put(name, tempOfUser);

                        transactionOfPayee.add(newTransactionOfPayee);
                        tempOfPayee.put(passFromDatabase, transactionOfPayee);

                        customerDetail.put(payee, tempOfPayee);

                        bufferedWriter.write("Amount transfer !! ");
                        bufferedWriter.flush();
                    }

                } else {
                    bufferedWriter.write("Enter valid number!! ");
                    bufferedWriter.flush();
                }


            } else {
                bufferedWriter.write("Enter valid credentials!! ");
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            bufferedWriter.write("Payee is not in data base ");
            bufferedWriter.flush();
        }
    }

    public void searchTransaction(String name, String password,BufferedReader bufferedReader,BufferedWriter bufferedWriter) throws IOException {

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        String allTransaction = "";

        if (customerDetail.containsKey(name) && inner.containsKey(password)) {

            bufferedWriter.write("Enter date ");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String searchDate = bufferedReader.readLine();

            bufferedWriter.write("Enter month ");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String searchMonth = bufferedReader.readLine();

            bufferedWriter.write("Enter year ");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String searchYear = bufferedReader.readLine();

            ArrayList<String> transactions = inner.get(password);

            for (String transaction : transactions) {
                String year = transaction.substring(0, 4);
                String month = transaction.substring(5, 7);
                String date = transaction.substring(8, 10);

                String newDate = year + "-" + month + "-" + date;

                if (date.equals(searchDate) && month.equals(searchMonth) && year.equals(searchYear)) {
                    allTransaction = allTransaction.concat(newDate).concat(" ").concat(transaction.substring(17)).concat("#");
                }
            }

            bufferedWriter.write("Alltransaction");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            bufferedWriter.write(allTransaction);
            bufferedWriter.newLine();

        } else {
            bufferedWriter.write("Invalid username or password ");
        }
        bufferedWriter.flush();
    }

    public void searchTransactionByRanges(String name, String pass, Date startDate, Date endDate, BufferedWriter bufferedWriter) throws ParseException, IOException {

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        String allTransaction = "";

        if (customerDetail.containsKey(name) && inner.containsKey(pass)) {

            if (startDate.before(endDate) && endDate.after(startDate) || startDate.equals(endDate)) {

                ArrayList<String> transactions;

                transactions = customerDetail.get(name).get(pass);

                for (String transaction : transactions) {

                    String year = transaction.substring(0, 4);
                    String month = transaction.substring(5, 7);
                    String date = transaction.substring(8, 10);

                    String newDate = year + "-" + month + "-" + date;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateObj = simpleDateFormat.parse(newDate);

                    if ((dateObj.before(endDate) && dateObj.after(startDate)) || dateObj.equals(startDate) || dateObj.equals(endDate)) {
                        allTransaction = allTransaction.concat(newDate).concat("  ").concat(transaction.substring(17)).concat("#");
                    }
                }
                bufferedWriter.write("Alltransaction");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                bufferedWriter.write(allTransaction);
                bufferedWriter.newLine();

            } else {
                bufferedWriter.write("Error in start date and end date");
                bufferedWriter.flush();
            }
        }else{
            bufferedWriter.write("Incorrect name or password . ");
            bufferedWriter.flush();
        }

    }

    public Date validateDate(String date,BufferedWriter bufferedWriter) throws IOException {
        Date date1;

            try {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException e) {
                bufferedWriter.write("Enter date in particular format : YYYY-MM-DD");
                bufferedWriter.flush();
                return null;
            }
        return date1;
    }

    public void searchLastMonth(String name, String pass, BufferedWriter bufferedWriter) throws IOException, ParseException {

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        if(customerDetail.containsKey(name) && inner.containsKey(pass)) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            String startdate = dateTimeFormatter.format(now.minusMonths(1));
            String enddate = dateTimeFormatter.format(now);

            Date startDate = validateDate(startdate, bufferedWriter);
            Date endDate = validateDate(enddate, bufferedWriter);

            if(startDate!=null && endDate!=null) {
                searchTransactionByRanges(name, pass, startDate, endDate, bufferedWriter);
            }
            else{
                bufferedWriter.write("Incorrect date .");
                bufferedWriter.flush();
            }
        }else{
            bufferedWriter.write("Incorrect name or password . ");
            bufferedWriter.flush();
        }
    }

    public void searchLastSixMonth(String name, String pass, BufferedWriter bufferedWriter) throws IOException, ParseException {

        HashMap<String,ArrayList<String>> inner = customerDetail.get(name);

        if(customerDetail.containsKey(name) && inner.containsKey(pass)) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            String startDate = dateTimeFormatter.format(now.minusMonths(6));
            String endDate = dateTimeFormatter.format(now);

            Date startDateObj = validateDate(startDate, bufferedWriter);
            Date endDateObj = validateDate(endDate, bufferedWriter);

            if(startDateObj!=null && endDateObj!=null) {
                searchTransactionByRanges(name, pass, startDateObj, endDateObj, bufferedWriter);
            }
            else{
                bufferedWriter.write("Incorrect date .");
                bufferedWriter.flush();
            }
        }
        else{
            bufferedWriter.write("Incorrect name or password . ");
            bufferedWriter.flush();
        }
    }

    public void loan(String name,String pass,BufferedReader bufferedReader,BufferedWriter bufferedWriter) throws IOException {

        HashMap<String,ArrayList<String>> inner =  customerDetail.get(name);

        if(customerDetail.containsKey(name) && inner.containsKey(pass)) {

            int amount;

            bufferedWriter.write("Enter amount");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            amount = Integer.parseInt(bufferedReader.readLine());

            double rate = 6.25;

            double time;

            bufferedWriter.write("Rate for loan is 6.25% [Enter - Ok]");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedReader.readLine();

            bufferedWriter.write("Enter duration");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            time = Double.parseDouble(bufferedReader.readLine());

            rate = rate / (12 * 100);

            double newTime = time * 12;

            double emi = (amount * rate * Math.pow(1 + rate, newTime)) / (Math.pow(1 + rate, newTime) - 1);

            ArrayList<String> transaction = inner.get(pass);

            String timeDate = getTimeDate();

            String newTransaction = timeDate.concat("$").concat(String.valueOf(amount)).concat("+");

            String newTransactionLoan = timeDate.concat("$").concat(String.valueOf(time)).concat("$").concat(String.valueOf(amount)).concat("+");

            if(Loan.get(name)==null){

                ArrayList<String> LoanList= new ArrayList<>();

                LoanList.add(newTransactionLoan);

                Loan.put(name, LoanList);

            }
            else {
                ArrayList<String> LoanList = Loan.get(name);

                LoanList.add(newTransactionLoan);

                Loan.put(name, LoanList);
            }
            transaction.add(newTransaction);

            bufferedWriter.write("Loan approved . Your EMI wil be " + emi + " . ");

        }else{
            bufferedWriter.write("Incorrect credentials");
        }
        bufferedWriter.flush();

    }

    public void writeDataUserDetails() throws IOException {

        String filepath = "/home/pruthviraj/userDetails.txt";
        File file = new File(filepath);

        BufferedWriter bufferedWriter;

        bufferedWriter = new BufferedWriter(new FileWriter(file));

        Iterator<String> iteratorOfName = customerDetail.keySet().iterator();
        Iterator<HashMap<String, ArrayList<String>>> iteratorOfInnerMap = customerDetail.values().iterator();

        for (int i = 0; i < customerDetail.size(); i++) {
            String name = iteratorOfName.next();

            HashMap<String, ArrayList<String>> inner = iteratorOfInnerMap.next();

            Iterator<String> iteratorOfPass = inner.keySet().iterator();
            String pass = iteratorOfPass.next();

            String forgot = customerInfo.get(name);

            bufferedWriter.write(name + ":" + pass + ":" +forgot);
            bufferedWriter.newLine();
        }

        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public void writeDataTransaction() {

        String filepath = "/home/pruthviraj/transaction.txt";

        File file = new File(filepath);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry :
                    customerDetail.entrySet()) {

                String name = entry.getKey();

                HashMap<String, ArrayList<String>> inner = entry.getValue();

                for (Map.Entry<String, ArrayList<String>> innerEntry : inner.entrySet()) {

                    bufferedWriter.write(name + ":");

                    for (String transaction : innerEntry.getValue()) {
                        bufferedWriter.write(transaction + ",");
                    }
                }
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeDataLoan(){

        String filepath = "/home/pruthviraj/Loan.txt";

        File file = new File(filepath);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            for (Map.Entry<String, ArrayList<String>> entry :
                    Loan.entrySet()) {

                String name = entry.getKey();

                ArrayList<String> inner = entry.getValue();

                    bufferedWriter.write(name + ":");

                    for (String transaction : inner) {
                        bufferedWriter.write(transaction + ",");
                    }

                bufferedWriter.newLine();
            }

            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

    public class BankApplication {

        public static void main(String[] args) throws IOException, ParseException {

            ServerSocket serverSocket;

            InputStreamReader inputStreamReader;
            OutputStreamWriter outputStreamWriter;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;

            System.out.println("server start");

            try {
                serverSocket = new ServerSocket(6666);
                System.out.println("waiting for client");

                Socket s = serverSocket.accept();

                System.out.println("Connection establish");

                inputStreamReader = new InputStreamReader(s.getInputStream());
                outputStreamWriter = new OutputStreamWriter(s.getOutputStream());

                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);


            } catch (IOException e) {
                e.printStackTrace();
            }

            Bank Customer = new Bank();

                assert bufferedWriter != null;

                bufferedWriter.write("Choose option ");
                bufferedWriter.newLine();
                bufferedWriter.flush();

                int option = Integer.parseInt(bufferedReader.readLine());

                if(option==1){

                    String name;

                    bufferedWriter.write("Enter Username ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    name = bufferedReader.readLine();

                    if (Customer.isString(name)) {
                        Customer.createCustomer(name, bufferedReader, bufferedWriter);
                    } else {
                        bufferedWriter.write("Enter valid name or password ");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }

                }else if(option==2){

                    String name,pass;

                    bufferedWriter.write("Enter Username");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    name = bufferedReader.readLine();

                    bufferedWriter.write("Enter Password");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    pass = bufferedReader.readLine();

                    boolean log;

                    log = Customer.login(name, pass);

                    if(log) {

                        bufferedWriter.write("Login successfully !!");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        int ch;

                        boolean ans;

                        do {

                            bufferedWriter.write("Choose option ");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            ch = Integer.parseInt(bufferedReader.readLine());

                            switch (ch) {

                                case 1 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.depositMoney(name, pass, bufferedReader, bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Enter valid name or password ");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                    }

                                }
                                case 2 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.withdrawMoney(name, pass, bufferedReader, bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Enter valid name or password ");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();
                                    }
                                }
                                case 3 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.displayDetails(name, pass, bufferedReader ,bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Invalid name or password ");
                                        bufferedWriter.flush();
                                    }
                                }
                                case 4 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.deleteCustomer(name, pass, bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Enter valid name or password ");
                                        bufferedWriter.flush();
                                    }
                                }

                                case 5 -> {

                                    bufferedWriter.write("Choose a optionForSearch");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();

                                    String optionForSearch = bufferedReader.readLine();

                                    int choice = Integer.parseInt(optionForSearch);

                                    if (choice == 1) {

                                        Customer.searchTransaction(name, pass, bufferedReader, bufferedWriter);

                                    } else if (choice == 2) {

                                        Customer.searchLastMonth(name, pass, bufferedWriter);

                                    } else if (choice == 3) {

                                        Customer.searchLastSixMonth(name, pass, bufferedWriter);

                                    } else if (choice == 4) {

                                        bufferedWriter.write("Enter start date [yyyy-MM-dd]");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();

                                        String startDate = bufferedReader.readLine();

                                        bufferedWriter.write("Enter end date [yyyy-MM-dd]");
                                        bufferedWriter.newLine();
                                        bufferedWriter.flush();

                                        String endDate = bufferedReader.readLine();

                                        Date startDateObj = Customer.validateDate(startDate, bufferedWriter);
                                        Date endDateObj = Customer.validateDate(endDate, bufferedWriter);

                                        if (startDateObj == null || endDateObj == null) {

                                            bufferedWriter.write("Enter date in [YYYY-MM-DD] format");
                                            bufferedWriter.flush();

                                        } else {
                                            Customer.searchTransactionByRanges(name, pass, startDateObj, endDateObj, bufferedWriter);
                                        }
                                    }

                                }

                                case 6 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.transferMoney(name, pass, bufferedReader, bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Enter valid name or password ");
                                        bufferedWriter.flush();
                                    }
                                }

                                case 7 -> {

                                    if (Customer.isString(name) && Customer.isString(pass)) {
                                        Customer.loan(name, pass, bufferedReader, bufferedWriter);
                                    } else {
                                        bufferedWriter.write("Enter valid name or password ");
                                        bufferedWriter.flush();
                                    }
                                }

                                default -> bufferedWriter.write("Nothing is out here ");
                            }
                            try {

                                bufferedWriter.write(" Do you want to perform any operation ??   true/false ");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                ans = Boolean.parseBoolean(bufferedReader.readLine());

                            } catch (Exception e) {

                                ans = true;

                                bufferedWriter.write("Invalid . ");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                            }

                        } while (ans);
                    }else{
                        bufferedWriter.write("Login fail");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }else if(option==3){

                    String name;

                    bufferedWriter.write("Enter Username");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    name = bufferedReader.readLine();

                    if(Customer.isString(name)){
                        Customer.forgotPassword(name,bufferedReader,bufferedWriter);
                    }else{
                        bufferedWriter.write("Enter valid name ");
                        bufferedWriter.flush();
                    }
                }

                Customer.writeDataUserDetails();
                Customer.writeDataTransaction();
                Customer.writeDataLoan();

                bufferedWriter.write("Exit");
                bufferedWriter.newLine();
                bufferedWriter.flush();

        }
    }
