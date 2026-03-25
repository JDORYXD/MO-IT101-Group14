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

    //------ Global Scanner -------------
    static Scanner input = new Scanner(System.in);

    //------ Global arrays -------------
    static final int MAX = 1000; //1000 max example only
    static String[] empID = new String[MAX]; // employee ID array
    static String[] empName = new String[MAX];// employee name array
    static String[] empBday = new String[MAX];// employee birthday array
    static double[] hourlyRate = new double[MAX];// employee hourly rate array

    // 3D arrays: [employee][month][day]
    static double[][][] cutoff1Hours = new double[MAX][13][15]; // cutoff 1 hours: [employee][month][day 1-15]
    static double[][][] cutoff2Hours = new double[MAX][13][16]; // cutoff 2 hours: [employee][month][day 16-31]
    static double[][][] cutoff1Gross = new double[MAX][13][15]; // cutoff 1 gross: [employee][month][day 1-15]
    static double[][][] cutoff2Gross = new double[MAX][13][16]; // cutoff 2 gross: [employee][month][day 16-31]

    static int empCount = 0; // Keeps track of the number workers were loaded from the CSV file
    static int payrollYear = 0; // Saves the salary year based on the timesheets.

    public static void main(String[] args) {

        try {
            readEmployeeFile("EmployeeDetails.csv");
            readAttendanceFile("AttendanceRecords.csv");
        } catch (Exception e) {
            System.out.println("Error reading files: " + e.getMessage());
            return;
        }

        //--------- LOGIN MENU --------------
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
    // Reads employee details from CSV, handles missing/invalid data, and stores in arrays
    //====================================================================
    static void readEmployeeFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error: Employee file not found or invalid path: " + filePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] rowData = parseCSVLine(line);
                if (rowData.length < 19) {
                    System.out.println("Skipping incomplete employee record.");
                    continue;   
                }
                if (empCount >= MAX) {
                    break;
                }

                try {
                    empID[empCount] = rowData[0].trim();
                    String lastname = rowData[1].trim();
                    String firstname = rowData[2].trim();
                    empName[empCount] = firstname + " " + lastname;
                    empBday[empCount] = rowData[3].trim();

                    String rateStr = rowData[rowData.length - 1].trim().replace(",", "");
                    hourlyRate[empCount] = Double.parseDouble(rateStr);

                    empCount++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numeric value in employee file. Skipping row.");

                }
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

    //====================================================================
    // Time calculation with grace period, lunch break, and max hours
    //====================================================================
    static double calculateWorkedHours(String timeIn, String timeOut) {
        if (timeIn.isEmpty() || timeOut.isEmpty()) {
            return 0;
        }

        double in = convertToDecimal(timeIn);
        double out = convertToDecimal(timeOut);

        // Apply grace period: arrivals at or before 8:10 treated as 8:00 AM
        if (in <= 8.1667) {
            in = 8.0;
        }
        // Cap working hours within official schedule (8:00 AM - 5:00 PM)
        if (in < 8) {
            in = 8;
        }
        if (out > 17) {
            out = 17;
        }

        double worked = out - in;

        if (worked < 0) {
            worked = 0;
        }
        // Deduct 1 hour for lunch if employee worked across 12–1 PM
        if (in < 12 && out > 13) {
            worked -= 1;  // -1 for lunch break
        }
        if (worked > 8) {
            worked = 8; //max 8 hours
        }

        return worked;
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

    //====================================================================
    // Find employee index by ID
    //====================================================================
    static int findEmployee(String id) {
        for (int i = 0; i < empCount; i++) {
            if (empID[i] != null && empID[i].equals(id.trim())) {
                return i;
            }
        }
        return -1;
    }

    //====================================================================
    // Get month name from number (1-12)
    //====================================================================
    static String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"};
        return (month >= 1 && month <= 12) ? months[month] : "Unknown";
    }

    //====================================================================
    // Contribution approximations (semi-monthly basis)
    //====================================================================
    public static double computeSSS(double semiGross) {
        double monthly = semiGross * 2.0;

        double sss;

        if (monthly < 3250) {
            sss = 135.00;
        } else if (monthly >= 24750) {
            sss = 1125.00;
        } else {
            int step = (int) ((monthly - 3250) / 500);
            sss = 135.00 + (step * 22.5);
        }

        return Math.round((sss / 2) * 100.0) / 100.0;
    }

    static double computePhilHealth(double semiGross) {
        double monthly = semiGross * 2;
        double monthlyPremium;

        if (monthly <= 10000) {
            monthlyPremium = 300;
        } else if (monthly >= 60000) {
            monthlyPremium = 1800;
        } else {
            monthlyPremium = monthly * 0.03;
        }

        // Employee share (50%)
        double employeeMonthly = monthlyPremium / 2;

        // Convert to semi-monthly
        double employeeSemi = employeeMonthly / 2;

        return Math.round(employeeSemi * 100.0) / 100.0;
    }

    static double computePagibig(double semiGross) {
        double monthly = semiGross * 2;
        double monthlyContrib = (monthly <= 1500) ? monthly * 0.01 : monthly * 0.02;
        double employeeMonthly = Math.min(monthlyContrib, 200);
        return Math.round(employeeMonthly / 2 * 100.0) / 100.0;
    }

    static double computeTax(double semiTaxable) {
        double monthly = semiTaxable * 2.0;
        double tax;

        if (monthly <= 20832) {
            tax = 0;
        } else if (monthly < 33333) {
            tax = (monthly - 20833) * 0.20;
        } else if (monthly < 66667) {
            tax = 2500 + (monthly - 33333) * 0.25;
        } else if (monthly < 166667) {
            tax = 10833 + (monthly - 66667) * 0.30;
        } else if (monthly < 666667) {
            tax = 40833.33 + (monthly - 166667) * 0.32;
        } else {
            tax = 200833.33 + (monthly - 666667) * 0.35;
        }

        // convert to semi-monthly
        return Math.round((tax / 2) * 100.0) / 100.0;
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
                processPayroll(); // call payroll processing function
              
            } else if (choice != 2) {
                System.out.println("Invalid choice. Please choose only number (1 or 2)");
            }

        } while (choice != 2);

        System.out.println("Exiting Payroll Menu...");
    }

    //====================================================================
    // Process Payroll - handles payroll processing for one or all employees
    //====================================================================
    static void processPayroll() {
        System.out.println("\n1. Process One Employee");
        System.out.println("2. Process All Employees");
        System.out.print("Choose (1 or 2): ");

        if (!input.hasNextInt()) {
            System.out.println("Invalid input.");
            input.nextLine();
            return;
        }
        int sub = input.nextInt();
        input.nextLine();

        switch (sub) {
            case 1 -> {
                System.out.print("Enter Employee ID: ");
                String id = input.nextLine().trim();
                int index = findEmployee(id);
                if (index == -1) {
                    System.out.println("Employee not found.");
                    return;
                }
                for (int m = 6; m <= 12; m++) {
                    printPayroll(index, m);
                }
            }
            case 2 ->
                displayPayroll(); // call function to display payroll for all employees and months
            default ->
                System.out.println("Invalid option.");
        }
    }

    //====================================================================
    // Display Payroll for all employees and months - calls printPayroll for each employee and month 
    //====================================================================
    static void displayPayroll() {
        for (int i = 0; i < empCount; i++) {
            for (int m = 6; m <= 12; m++) {
                printPayroll(i, m);
            }
        }
    }

    //====================================================================
    // Print Payroll details for a specific employee and month, including cutoff 1 and cutoff 2 calculations
    //====================================================================
    static void printPayroll(int i, int month) {
        System.out.println("\n=================================");
        System.out.println("Employee #: " + empID[i]);
        System.out.println("Name: " + empName[i]);
        System.out.println("Birthday: " + empBday[i]);
        System.out.println("Rate: " + hourlyRate[i]);
        System.out.println("Month: " + getMonthName(month) + " " + payrollYear);

        // -------- CUTOFF 1 --------
        // Display cutoff 1 details (hours, gross, net) for the first 15 days of the month
        System.out.println("\nCutoff Date: " + getMonthName(month) + " 1 to 15, " + payrollYear);

        double cutoff1HoursWorked = 0;
        double cutoff1GrossSalary = 0;
        for (int d = 0; d < 15; d++) {
            cutoff1HoursWorked += cutoff1Hours[i][month][d];// sum hours for cutoff 1 from day 1 to 15
            cutoff1GrossSalary += cutoff1Gross[i][month][d];// sum gross for cutoff 1 from day 1 to 15
        }
        // No deductions applied in cutoff 1 
        double cutoff1Net = cutoff1GrossSalary;

        System.out.printf("Total Hours Worked: %.2f\n", cutoff1HoursWorked);
        System.out.printf("Gross Salary: %.2f\n", cutoff1GrossSalary);
        System.out.printf("Net Salary: %.2f\n", cutoff1Net);

        // -------- CUTOFF 2 --------
        // Display cutoff 2 details (hours, gross, deductions, net) for the last 16 days of the month
        System.out.println("\nCutoff Date: " + getMonthName(month) + " 16 to 30/31, " + payrollYear);

        double cutoff2HoursWorked = 0;
        double cutoff2GrossSalary = 0;
        for (int d = 0; d < 16; d++) {
            cutoff2HoursWorked += cutoff2Hours[i][month][d];// sum hours for cutoff 2 from day 16 to end of month
            cutoff2GrossSalary += cutoff2Gross[i][month][d];// sum gross for cutoff 2 from day 16 to end of month
        }

        // Calculate deductions based on cutoff 2 gross salary
        double sssContribution = computeSSS(cutoff2GrossSalary);
        double philhealthContribution = computePhilHealth(cutoff2GrossSalary);
        double pagibigContribution = computePagibig(cutoff2GrossSalary);
        double taxable = cutoff2GrossSalary - sssContribution - philhealthContribution - pagibigContribution;
        double tax = computeTax(taxable);
        double totalDeduction = sssContribution + philhealthContribution + pagibigContribution + tax;

        // Total deductions for cutoff 2
        double cutoff2Net = cutoff2GrossSalary - totalDeduction;

        // Display cutoff 2 details including deductions and net salary
        System.out.printf("Total Hours Worked: %.2f\n", cutoff2HoursWorked);
        System.out.printf("Gross Salary: %.2f\n", cutoff2GrossSalary);
        System.out.println("\nEach Deduction:");
        System.out.printf("SSS: %.2f\n", sssContribution);
        System.out.printf("PhilHealth: %.2f\n", philhealthContribution);
        System.out.printf("Pag-IBIG: %.2f\n", pagibigContribution);
        System.out.printf("Tax: %.2f\n", tax);
        System.out.printf("Total Deductions: %.2f\n", totalDeduction);
        System.out.printf("Net Salary: %.2f\n", cutoff2Net);

        // -------- TOTAL MONTHLY NET --------
        // Calculate and display total net salary for the month by summing cutoff 1 and cutoff 2 net salaries
        double totalMonthlyNet = cutoff1Net + cutoff2Net;
        System.out.println("\n=================================");
        System.out.printf("Total Net Salary for the Month: %.2f\n", totalMonthlyNet);
        System.out.println("=================================");
    }
}




