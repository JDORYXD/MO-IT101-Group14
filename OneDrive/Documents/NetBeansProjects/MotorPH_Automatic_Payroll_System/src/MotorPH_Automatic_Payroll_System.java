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

    //------ Global Scanner -------------//
    static Scanner input = new Scanner(System.in);

    //------ Global arrays -------------//
    static final int MAX = 1000; //1000 max example only
    static String[] empID = new String[MAX]; // employee ID array
    static String[] empName = new String[MAX];// employee name array
    static String[] empBday = new String[MAX];// employee birthday array
    static double[] hourlyRate = new double[MAX];// employee hourly rate array

    // 3D arrays: [employee][month][day]
    static double[][][] cutoff1Hours = new double[MAX][13][15];// cutoff 1 hours: [employee][month][day 1-15]
    static double[][][] cutoff2Hours = new double[MAX][13][16]; // cutoff 2 hours: [employee][month][day 16-31]
    static double[][][] cutoff1Gross = new double[MAX][13][15];// cutoff 1 gross: [employee][month][day 1-15]
    static double[][][] cutoff2Gross = new double[MAX][13][16];// cutoff 2 gross: [employee][month][day 16-31]

    static int empCount = 0;// Keeps track of the number workers were loaded from the CSV file
    static int payrollYear = 0;// Saves the salary year based on the timesheets.

    public static void main(String[] args) {

        try {
            readEmployeeFile("EmployeeDetails.csv");
            readAttendanceFile("AttendanceRecords.csv");
        } catch (Exception e) {
            System.out.println("Error reading files: " + e.getMessage());
            return;
        }

        //--------- LOGIN MENU --------------//
        while (true) {
            String correctEmpUsername = "employee";
            String correctPayUsername = "payroll";
            int correctPassword = 12345;

            System.out.println("\n======== LOGIN ACCOUNT MENU =========");
            System.out.print("Enter Username: ");
            String username = input.nextLine().trim();

            if (username.isEmpty() || username.contains(" ") || username.length() < 5 || username.length() > 9) {
                System.out.println("Invalid username format.");
                continue;
            }

            System.out.print("Enter Password (5 digits): ");
            String passInput = input.nextLine();
            if (!passInput.matches("\\d{5}")) {
                System.out.println("Password must be exactly 5 digits.");
                continue;
            }
            int password = Integer.parseInt(passInput);

            if (username.equals(correctEmpUsername) && password == correctPassword) {
                System.out.println("Login Successful - Welcome Employee ");
                employeeMenu();
            } else if (username.equals(correctPayUsername) && password == correctPassword) {
                System.out.println("Login Successful - Welcome Payroll Staff ");
                payrollMenu();
            } else {
                System.out.println("Incorrect credentials. Exiting...");
                break;
            }
        }
        System.out.println("Program ending. Closing input...");
        input.close();

    }

    //====================================================================
    // CSV Parsing - handles quoted fields and commas within quotes
    //====================================================================
    private static String[] parseCSVLine(String line) {
        String[] fields = new String[30];   // safe max fields
        int count = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                if (count < 30) {
                    fields[count] = current.toString().trim();
                    count++;
                }
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        // Last field
        if (count < 30) {
            fields[count] = current.toString().trim();
            count++;
        }

        // Clean quotes from each field if wrapped
        for (int j = 0; j < count; j++) {
            String field = fields[j];
            if (field != null && field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"")) {
                fields[j] = field.substring(1, field.length() - 1).trim();
            }
        }

        // Manual array copy (required - no built-in methods used)
        String[] result = new String[count];
        for (int j = 0; j < count; j++) {
            result[j] = fields[j];
        }
        return result;
    }

    //====================================================================
    // Employee Menu - allows employee to view their details by entering ID
    //====================================================================
    static void employeeMenu() {
        int choice;

        do {
            System.out.println("\n======= Employee Display Menu ======");
            System.out.println("1. Enter Employee ID number");
            System.out.println("2. Exit the program ");
            System.out.print("Choose (1 or 2): ");

            while (!input.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number (1 or 2 only).");
                System.out.print("Choose (1 or 2  ): ");
                input.nextLine();
            }

            choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                System.out.print("Enter your employee ID number: ");
                String id = input.nextLine().trim();

                if (id.isEmpty() || id.contains(" ") || id.length() > 9 || id.length() < 5) {
                    System.out.println("Invalid Employee ID format.");
                    continue;
                }

                int index = findEmployee(id);
                if (index != -1) {
                    System.out.println("Employee #: " + empID[index]);
                    System.out.println("Name: " + empName[index]);
                    System.out.println("Birthday: " + empBday[index]);
                    System.out.println("Rate: " + hourlyRate[index]);
                } else {
                    System.out.println("Employee not found.");
                }
            } else if (choice != 2) {
                System.out.println("!!!Invalid Choice, please choose only number 1 or 2 ");
            }

        } while (choice != 2);

        System.out.println("Exiting Employee Menu...");
    }
    
     //====================================================================
    // Payroll Menu - allows payroll to process payroll for one or all employees
    //====================================================================
    static void payrollMenu() {
        int choice;

        do {
            System.out.println("\n========= PAYROLL MENU ==============");
            System.out.println("1. Payroll Process");
            System.out.println("2. Exit the program");
            System.out.print("Choose (1 or 2): ");

            while (!input.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number (1 or 2) only.");
                System.out.print("Choose (1 or 2): ");
                input.nextLine(); // clear input
            }

            choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                processPayroll();
              /// call payroll processing function
            } else if (choice != 2) {
                System.out.println("Invalid choice. Please choose only number (1 or 2)");
            }

        } while (choice != 2);

        System.out.println("Exiting Payroll Menu...");
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
                hourlyRate[empCount] = Double.parseDouble(data[data.length - 1].trim());
            } catch (NumberFormatException e) {
                hourlyRate[empCount] = 0;
            }

            empCount++;
            //System.out.println("Employees loaded: " + empCount);
        }
    }
    
}

    //====================================================================
    // Reads attendance records, calculates hours and gross pay, and stores in cutoff arrays
    //====================================================================
    static void readAttendanceFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error: Attendance file not found: " + filePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Uncomment next line if attendance file has header:
            // br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] rowData = parseCSVLine(line);
                if (rowData.length < 6) {
                    System.out.println("Skipping malformed attendance row.");
                    continue;
                }

                String id = rowData[0].trim();
                String dateStr = rowData[3].trim();
                String timeIn = rowData[4].trim();
                String timeOut = rowData[5].trim();

                int index = findEmployee(id);
                if (index == -1) {
                    continue;
                }

                String[] dateParts = dateStr.split("/");
                if (dateParts.length != 3) {
                    continue;
                }

                int month, day, year;

                try {
                    month = Integer.parseInt(dateParts[0].trim());
                    day = Integer.parseInt(dateParts[1].trim());
                    year = Integer.parseInt(dateParts[2].trim());

                } catch (NumberFormatException e) {
                    System.out.println("Invalid date format. Skipping row.");
                    continue;
                }
                if (month < 1 || month > 12 || day < 1 || day > 31) {
                    System.out.println("Invalid date values. Skipping...");
                    continue;
                }

                payrollYear = year;

                if (month < 6 || month > 12) {
                    continue;
                }
                if (timeIn.isEmpty() || timeOut.isEmpty()) {
                    System.out.println("Missing time data. Skipping...");
                    continue;
                }

                double worked = calculateWorkedHours(timeIn, timeOut);
                if (worked <= 0) {
                    continue;
                }

                double gross = worked * hourlyRate[index];

                if (day >= 1 && day <= 15) {
                    int d = day - 1;
                    cutoff1Hours[index][month][d] = worked; // store hours for cutoff 1 (day 1-15)
                    cutoff1Gross[index][month][d] = gross;  // store gross for cutoff 1 (day 1-15)
                } else if (day >= 16 && day <= 31) {
                    int d = day - 16;
                    if (d < 16) {
                        cutoff2Hours[index][month][d] = worked; // store hours for cutoff 2 (day 16-31)
                        cutoff2Gross[index][month][d] = gross;  // store gross for cutoff 2 (day 16-31)
                    }
                }
            }
        }
    }

    
    public static double applyGracePeriod(double in){
        if (in <= 8.1667)
            return 8;
        return in;
    }
    //====================================================================
    // Convert "HH:MM" to decimal hours (e.g., "8:30" -> 8.5)
    //====================================================================
    static double convertToDecimal(String time) {
        String[] parts = time.split(":");
        if (parts.length != 2) {
            return 0;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hour + (minutes / 60.0);
        } catch (NumberFormatException e) {
            return 0;
        }
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
        //Display result for cutoff2
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




