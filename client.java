package com.company;

import java.net.Socket;
import java.util.*;
import java.io.*;

public class client {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        InputStreamReader inputStreamReader;
        OutputStreamWriter outputStreamWriter;
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;

        try {

            socket = new Socket("localhost", 6666);
            System.out.println("client start");

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            System.out.println("Sign Up = 1");
            System.out.println("Log In = 2");
            System.out.println("Forgot Password = 3");
            System.out.println("Exit = 4");

            Scanner sc = new Scanner(System.in);

            String serverMsg = bufferedReader.readLine();

            System.out.println("Server : "+ serverMsg);

            String choiceString = sc.nextLine();
            int choice = Integer.parseInt(choiceString);

            bufferedWriter.write(choiceString);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            if(choice==1){
                while (true){

                    String Msg = bufferedReader.readLine();
                    System.out.println("Server : " + Msg);

                    if(Msg.equalsIgnoreCase("Account created !!!")){
                        break;
                    }

                    String input = sc.nextLine();
                    bufferedWriter.write(input);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

            }
            else if(choice==2) {
                    while (true){

                        String Msg = bufferedReader.readLine();
                        System.out.println("Server : " + Msg);

                        if(Msg.equalsIgnoreCase("Login successfully !!")){
                            break;
                        }

                        String input = sc.nextLine();
                        bufferedWriter.write(input);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    }

                    while (true) {

                        System.out.println();
                        System.out.println("Add amount  = 1");
                        System.out.println("Withdraw amount  = 2");
                        System.out.println("Get Details = 3");
                        System.out.println("Delete account = 4");
                        System.out.println("Search transaction = 5");
                        System.out.println("Transfer Money = 6");
                        System.out.println("Request for Loan = 7");
                        System.out.println();

                        while (true) {

                            serverMsg = bufferedReader.readLine();
                            System.out.println("server : " + serverMsg);

                            if (serverMsg.equalsIgnoreCase("Exit")) {
                                break;
                            }
                            String input;

                            if (serverMsg.equalsIgnoreCase("Choose a optionForSearch")) {

                                System.out.println("search transaction by Custom date = 1");
                                System.out.println("search transaction of Last Month = 2");
                                System.out.println("search transaction of Last Six Month = 3");
                                System.out.println("search transaction by ranges = 4");

                                input = scanner.nextLine();

                                bufferedWriter.write(input);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                continue;
                            }

                            if (serverMsg.equalsIgnoreCase("Alltransaction")) {

                                String allTransaction = bufferedReader.readLine();

                                if (allTransaction.isEmpty()) {
                                    System.out.println("No transaction to show ");
                                    continue;
                                }

                                String[] parts = allTransaction.split("#");
                                for (String transaction : parts) {
                                    System.out.println(transaction);
                                }

                                continue;
                            }

                            if(serverMsg.equalsIgnoreCase("Loan")){
                                String allLoans = bufferedReader.readLine();

                                String[] parts = allLoans.split("#");

                              for(String loan:parts){
                                  String []parts2 = loan.split("/");

                                  System.out.println(parts2[0] + " " + parts2[1]);

                              }
                                continue;
                            }

                            input = scanner.nextLine();

                            if (input.equalsIgnoreCase("true")) {

                                bufferedWriter.write(input);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();

                                break;
                            }

                            bufferedWriter.write(input);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                        }
                        if (serverMsg.equalsIgnoreCase("false") || serverMsg.equalsIgnoreCase("Exit")) {
                            break;
                        }
                    }
            }
            else if(choice==3){

                while (true){

                    String Msg = bufferedReader.readLine();
                    System.out.println("Server : " + Msg);

                    if(Msg.equalsIgnoreCase("Password changed !!!")){
                        break;
                    }

                    String input = sc.nextLine();
                    bufferedWriter.write(input);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                }

            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        assert socket != null;
        socket.close();
    }

}


