/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package motorph.motorph_automatic_payroll_system;

/**
 *
 * @author 
 */
import java.io.*;
import java.util.Scanner;
public class MotorPH_Automatic_Payroll_System {
    
    //declaring global scanner for whole program
    static Scanner input = new Scanner (System.in);
    

    //------declaring global variables using arrays-------------//

    // Maximum number of employees the system can handle
    static final int MAX = 1000; //1000 max example only

    // Arrays to store employee basic information
    static String[] empID = new String[MAX];       // Stores employee IDs
    static String[] empName = new String[MAX];     // Stores employee full names
    static String[] empBday = new String[MAX];     //Stores Birthday
    static double[] hourlyRate = new double[MAX];  // Stores hourly rate of each employee

    //3D arrays for store store payroll data structured as employee, month, and day
    static double[][][] cutoff1Hours = new double[MAX][13][15]; 
    static double[][][] cutoff2Hours = new double[MAX][13][16];

    static double[][][] cutoff1Gross = new double[MAX][13][15];
    static double[][][] cutoff2Gross = new double[MAX][13][16];

    static int empCount = 0;   // Counts how many employees were loaded
    static int payrollYear = 0; // Stores the year extracted from attendance

    public static void main(String[] args){ 
        

        try {
                //Read employee information file
                readEmployeeFile("EmployeeDetails.csv");
           
           
                //Read attendance file and log in /log out 
                readAttendanceFile("AttendanceRecords.csv");
        } catch (Exception e) {
            System.out.println("Error reading files: " + e.getMessage());
            return; 
        }
            
        
        //---------LOGIN ACCOUNT MENU--------------//
        while (true){
            
        String Correctusername = "employee";
        String Correctusername1 = "payroll";
        int Correctpassword = 12345;
        
        System.out.println("========LOGIN ACCOUNT MENU==========");
        System.out.print("Enter Username: ");
        String Username = input.next();
        input.nextLine();
        if (Username.isEmpty()) {
            System.out.println("Username cannot be empty");
            continue; 
        }
        if (Username.contains(" ")) {
            System.out.println("Username cannot contain spaces");
            continue; 
        }
        if(Username.length()>9){
            System.out.println("Username cannot be more than 9 characters");
            continue; 
        }
        else if(Username.length()<5){
            System.out.println("Username cannot be less than 5 characters");
            continue; 
        }   
        System.out.print("Enter Password: ");
        String passInput = input.nextLine();
        if (!passInput.matches("\\d{5}")) {
        System.out.println("Password must be a 5-digit number");
        continue;
        }
        int Password = Integer.parseInt(passInput);
        


        if(Username.equals(Correctusername) && Password == Correctpassword){
            System.out.println("====================================");
            System.out.println("-----------Log in Successful--------");
            System.out.println("-------------  Welcome   -----------");
            System.out.println("====================================");
            EmployeeMenu();
            
            
        }
        else if (Username.equals(Correctusername1) && Password == Correctpassword){
            System.out.println("====================================");
            System.out.println("-----------Log in Successful--------");
            System.out.println("-------------  Welcome   -----------");
            System.out.println("====================================");
            PayrollMenu();
            
        
        }
        else {
            System.out.println("====================================");
            System.out.println("Incorrect Username and/or Password  ");
            System.out.println("Exiting the Program.................");
            System.out.println("====================================");
            break; 
        }    
        
        }//end of while (true)
        
       
    } // end of main String []

    //for EmployeeMenu()
    static void EmployeeMenu(){
    int choice = 0;

    do {
        System.out.println("\n=======Employee Display Menu======");
        System.out.println("1. Enter Employee ID number");
        System.out.println("2. Exit the program ");
        System.out.println("====================================");
        System.out.print("Choose (1 or 2): ");

        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
            
        if (choice >=3){
            System.out.println("!!!Invalid Choice,please choose only number 1 or 2 ");
        }
        }//end for choice 
        else {
            System.out.println("Invalid input! Numbers only.");
            input.next(); 
            continue;
        }

        if (choice == 1){
            System.out.print("Enter your employee ID number: ");
            String id = input.nextLine();
            
            if (id.isEmpty()) {
                System.out.println("Employee ID cannot be empty");
            }
            else if (id.contains(" ")) {
                System.out.println("Employee ID cannot contain spaces");
            }
            else if(id.length()>9){
                System.out.println("Employee ID cannot be more than 9 characters");
                
            }
            else if(id.length()<5){
                System.out.println("Employee ID cannot be less than 5 characters");
            }
            else {
                boolean found = false;
                for (int i = 0; i < empCount; i++) {
                    if (empID[i].equals(id)) {
                        System.out.println("Employee #: " + empID[i]);
                        System.out.println("Name: " + empName[i]);
                        System.out.println("Birthday: " + empBday[i]);
                        System.out.println("Rate: " + hourlyRate[i]);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Employee not found.");
                }
            }//end of else statement

            
        }//end of choice 1

    } while(choice != 2);
}//End of Employee Menu
    
    static void PayrollMenu() {

    int choice = 0;

    do {

        System.out.println("\n========= PAYROLL MENU ==============");
        System.out.println("1. Payroll Process");
        System.out.println("2. Exit the program");
        System.out.print("Choose (1 or 2): ");

        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
        } 
        else {
            System.out.println("Invalid input! Numbers only.");
            input.next();
            continue;
        }

        switch (choice) {

            case 1 -> processPayroll();

            case 2 -> System.out.println("Exiting Payroll Menu...");

            default -> System.out.println("Invalid choice.");
        }

    } while (choice != 2);
}// end of payroll menu
    
    //------For process payroll display-------//
    static void processPayroll() {

    System.out.println("\n1. Process payroll (One Employee)");
    System.out.println("2. Process All Employee Payroll");
    System.out.print("Choose (1 or 2): ");

    if (!input.hasNextInt()) {
        System.out.println("Invalid input!");
        input.next();
        return;
    }

    int subChoice = input.nextInt();
    input.nextLine();

    switch (subChoice) {

        case 1 -> {

            System.out.print("Enter Employee number: ");
            String searchID = input.nextLine();

            int index = findEmployee(searchID);

            if (index == -1) {
                System.out.println("Employee not found.");
                return;
            }

            System.out.println("\n=================================");
            System.out.println("Employee #: " + empID[index]);
            System.out.println("Name: " + empName[index]);
            System.out.println("Birthday: " + empBday[index]);
            System.out.println("Rate: " + hourlyRate[index]);

            for (int month = 6; month <= 12; month++) {
                printPayroll(index, month);
            }
        }

        case 2 -> displayPayroll();

        default -> System.out.println("Invalid option.");
    }
}


    // ================= READ EMPLOYEE FILE =================
    public static void readEmployeeFile(String file) throws Exception {

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            if (empCount >= MAX) {
                System.out.println("Maximum employee limit reached.");
                break;
            }

            String[] data = line.split(",");

            if (data.length < 4) {
                System.out.println("Invalid data line: " + line);
                continue;
            }

            empID[empCount] = data[0].trim(); // Employee ID from employee file
            empName[empCount] = data[1].trim();// Employee name from employee file
            empBday[empCount] = data[2].trim();// Employee birthday from employee file

            try {
                hourlyRate[empCount] = Double.parseDouble(data[3].trim());
            } catch (NumberFormatException e) {
                hourlyRate[empCount] = 0;
            }

            empCount++;
            System.out.println("Employees loaded: " + empCount);
        }
    }
    
}

    // ================= READ ATTENDANCE FILE =================
    public static void readAttendanceFile(String file) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            br.readLine(); 
            
            while ((line = br.readLine()) != null) {
                
                String[] data = line.split(",");
                if (data.length < 5) continue;
                
                String id = data[0].trim();  // Employee ID from attendance
                
                // Find employee index in our employee array
                int index = findEmployee(id);
                
                // If employee not found, skip
                if (index == -1) continue;
                
                // ------------------ DATE PART ------------------
                String[] dateParts = data[2].split("/"); // date from attendance
                
                int month = Integer.parseInt(dateParts[0].trim()); // month from attendance
                int day = Integer.parseInt(dateParts[1].trim()); //day from attendance
                int year = Integer.parseInt(dateParts[2].trim()); //year from attendance
                
                payrollYear = year;  // Store year 
                
                // Only allow June–December
                if (month < 6 || month > 12) continue;
                
                String TimeIn = data [3].trim();
                String TimeOut = data [4].trim();
                
                if (data.length < 5) continue;
                double worked = calculateWorkedHours(TimeIn, TimeOut);
                // Compute gross salary
                double gross = worked * hourlyRate[index];

                // ------------------ CUTOFF Section------------------
                if (day >= 1 && day <= 15) {
                    int d = day - 1;
                    cutoff1Hours[index][month][d] = worked;
                    cutoff1Gross[index][month][d] = gross;
                }
                else if (day >= 16 && day <= 31) {
                    int d = day - 16;
                    cutoff2Hours[index][month][d] = worked;
                    cutoff2Gross[index][month][d] = gross;
                }
            }//end of while
        }//end of try
    }//end of read attendance file 
    
    public static double applyGracePeriod(double in){
        if (in <= 8.1667)
            return 8;
        return in;
    }
    // ================= CONVERT TIME =================
    public static double convertToDecimal(String time) {

        String[] t = time.split(":");

        int hour = Integer.parseInt(t[0]);
        int minute = Integer.parseInt(t[1]);

        // Convert minutes to decimal
        return hour + (minute / 60.0); 
    }
    public static double calculateWorkedHours(String TimeIn,String TimeOut){

    double in = convertToDecimal(TimeIn);
    double out = convertToDecimal(TimeOut);

    // Grace period until 8:10
    if (in <= 8.1667) 
        in = 8;

    // Only count between 8 AM to 5 PM
    if (in < 8) in = 8;
    if (out > 17) out = 17;

    double worked = out - in;

    // Lunch break
    if (worked > 4)
        worked = worked - 1; // -1 for lunch break

    if (worked < 0)
        worked = 0;

    if (worked > 8)
        worked = 8;

   

    return worked;
}

    // ================= DISPLAY PAYROLL =================
    public static void displayPayroll() {
        for (int i = 0; i < empCount; i++) {

            for (int month = 6; month <= 12; month++) {

        printPayroll(i, month);
             }//end of for loop for employee
    }//end of for loop for month
        }//end of display payroll

    //===========================PRINT PAYROLL ============================//
    static void printPayroll(int i, int month) { 
        System.out.println("\n=================================");
        System.out.println("Employee #: " + empID[i]);
        System.out.println("Name: " + empName[i]);
        System.out.println("Birthday: " + empBday[i]);
        System.out.println("Rate: " + hourlyRate[i]);
        System.out.println("Month: " + getMonthName(month) + " " + payrollYear);

        // -------- CUTOFF 1 --------
        //Dispay result for cutoff1
        System.out.println("\nCutoff Date: " + getMonthName(month) + " 1 to 15, " + payrollYear);
        
        // declaring variable for as double
        double cutoff1HoursWorked = 0;
        double cutoff1GrossSalary = 0;

        for(int d = 0; d < 15; d++){
            cutoff1HoursWorked += cutoff1Hours[i][month][d];
            cutoff1GrossSalary += cutoff1Gross[i][month][d];
        }
        double cutoff1Net = cutoff1GrossSalary;

        System.out.printf("Total Hours Worked: %.2f\n", cutoff1HoursWorked);
        System.out.printf("Gross Salary: %.2f\n", cutoff1GrossSalary);
        System.out.printf("Net Salary: %.2f\n", cutoff1Net);

        // -------- CUTOFF 2 --------
        //Dispay result for cutoff2
        System.out.println("\nCutoff Date: " + getMonthName(month) + " 16 to 30/31, " + payrollYear);

        double cutoff2HoursWorked = 0;
        double cutoff2GrossSalary = 0;

        for(int d = 0; d < 16; d++){
            cutoff2HoursWorked += cutoff2Hours[i][month][d];
            cutoff2GrossSalary += cutoff2Gross[i][month][d];
        }

        // declaring Deductions 
        double sss = computeSSS(cutoff2GrossSalary);
        double phil = computePhilHealth(cutoff2GrossSalary);
        double pagibig = computePagibig(cutoff2GrossSalary);
        double tax = computeTax(cutoff2GrossSalary);
        
        //total deductions for government contributions 
        double totalDeduction = sss + phil + pagibig + tax;
        double cutoff2Net = cutoff2GrossSalary - totalDeduction;
        
        
        
        System.out.printf("Total Hours Worked: %.2f\n", cutoff2HoursWorked);
        System.out.printf("Gross Salary: %.2f\n", cutoff2GrossSalary);
        System.out.println("\nEach Deduction:");
        System.out.printf("SSS: %.2f\n", sss);
        System.out.printf("PhilHealth: %.2f\n" , phil);
        System.out.printf("Pag-IBIG: %.2f\n" ,pagibig);
        System.out.printf("Tax: %.2f\n",tax);
        System.out.printf("Total Deductions: %.2f\n" ,totalDeduction);
        System.out.printf("Net Salary: %.2f\n", cutoff2Net);
        
        // -------- TOTAL MONTHLY NET --------
        double totalMonthlyNet = cutoff1Net + cutoff2Net;

        System.out.println("\n=================================");
        System.out.printf("Total Net Salary for the Month: %.2f\n", totalMonthlyNet);
        System.out.println("=================================");
}//end of print payroll
        
    

    // ================= FIND EMPLOYEE =================
    public static int findEmployee(String id) {

        // Linear search in empID array
        for (int i = 0; i < empCount; i++) {
            if (empID[i].trim().equals(id.trim()))
                return i;  
                }
            

        return -1;  
    }

    

    // ================= GET MONTH NAME =================
    public static String getMonthName(int month) {

        String[] months = {"", "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"};

        return months[month];
    }

    // ================= DEDUCTION METHODS =================
    // SSS contribution based on gross salary / Minimum contribution is 135, Maximum contribution is 1125.
    public static double computeSSS(double gross) {
        double salary = gross;
        if (salary < 3250)
        return 135.00;
    else if (salary < 3750)
        return 157.50;
    else if (salary < 4250)
        return 180.00;
    else if (salary < 4750)
        return 202.50;
    else if (salary < 5250)
        return 225.00;
    else if (salary < 5750)
        return 247.50;
    else if (salary < 6250)
        return 270.00;
    else if (salary < 6750)
        return 292.50;
    else if (salary < 7250)
        return 315.00;
    else if (salary < 7750)
        return 337.50;
    else if (salary < 8250)
        return 360.00;
    else if (salary < 8750)
        return 382.50;
    else if (salary < 9250)
        return 405.00;
    else if (salary < 9750)
        return 427.50;
    else if (salary < 10250)
        return 450.00;
    else if (salary < 10750)
        return 472.50;
    else if (salary < 11250)
        return 495.00;
    else if (salary < 11750)
        return 517.50;
    else if (salary < 12250)
        return 540.00;
    else if (salary < 12750)
        return 562.50;
    else if (salary < 13250)
        return 585.00;
    else if (salary < 13750)
        return 607.50;
    else if (salary < 14250)
        return 630.00;
    else if (salary < 14750)
        return 652.50;
    else if (salary < 15250)
        return 675.00;
    else if (salary < 15750)
        return 697.50;
    else if (salary < 16250)
        return 720.00;
    else if (salary < 16750)
        return 742.50;
    else if (salary < 17250)
        return 765.00;
    else if (salary < 17750)
        return 787.50;
    else if (salary < 18250)
        return 810.00;
    else if (salary < 18750)
        return 832.50;
    else if (salary < 19250)
        return 855.00;
    else if (salary < 19750)
        return 877.50;
    else if (salary < 20250)
        return 900.00;
    else if (salary < 20750)
        return 922.50;
    else if (salary < 21250)
        return 945.00;
    else if (salary < 21750)
        return 967.50;
    else if (salary < 22250)
        return 990.00;
    else if (salary < 22750)
        return 1012.50;
    else if (salary < 23250)
        return 1035.00;
    else if (salary < 23750)
        return 1057.50;
    else if (salary < 24250)
        return 1080.00;
    else if (salary < 24750)
        return 1102.50;
    else
        return 1125.00;
        
        
        }
    // PhilHealth contribution / Minimum contribution is 300, Maximum contribution is 1800.
    public static double computePhilHealth(double gross) {

        double contribution;

    if (gross <= 10000){ // Minimum contribution = 300
        contribution = 300;
    }
    else if (gross >= 60000){ // Maximum contribution = 1800
        contribution = 1800;
    }
    else{
        contribution = gross * 0.03; // 3% of gross salary
    }

    return contribution;
}
    // Pag-IBIG contribution / Maximum contribution is 100.
    public static double computePagibig(double gross) {
        double contribution;

    if (gross <= 1500){
        contribution = gross * 0.01; // 1% of gross salary
    }
    else{
        contribution = gross * 0.02; // 2% of gross salary
    }

    // maximum contribution is 100
    if(contribution > 100){
        contribution = 100;
    }

    return contribution;
    }
    
    // Tax brackets based on Philippine tax rates
    public static double computeTax(double gross) {
        double MonthIncomeBase = gross; 
        
        if (MonthIncomeBase <= 20832) {
        return 0;
    }
    else if (MonthIncomeBase <= 33333) {
        return (MonthIncomeBase - 20833) * 0.20;
    }
    else if (MonthIncomeBase <= 66667) {
        return 2500 + (MonthIncomeBase - 33333) * 0.25;
    }
    else if (MonthIncomeBase <= 166667) {
        return 10833 + (MonthIncomeBase - 66667) * 0.30;
    }
    else if (MonthIncomeBase <= 666667) {
        return 40833.33 + (MonthIncomeBase - 166667) * 0.32;
    }
    else {
        return 200833.33 + (MonthIncomeBase - 666667) * 0.35;
    }
        }

    
           
}//end of MotorPH_Automatic_Payroll_System



