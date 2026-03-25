# MotorPH Automatic Payroll System

A Java-based automatic payroll system that manages employee information, attendance records, and payroll calculations. This system demonstrates core programming skills, file handling, and payroll computation logic including SSS, PhilHealth, Pag-IBIG, and tax deductions.

---

## 📌 Team Details

### Orlando Eria – Payroll Computation and Program Flow
*Implemented the primary payroll computation tasks, such as calculating to net pay, gross pay, deductions, and employee worked hours. contributed to the program's general flow, which uses attendance records to compute payroll.

*Processed employee attendance information, including time-in and time-out data used to calculate work hours

### John Vincent Illano – Payroll Computation Support and Program Flow
*Assisted in implementing parts of the payroll computation module and contributed to structuring the program flow to ensure the payroll process runs correctly from data input to salary computation.

*Created the program menu and handled user input validation so that users could interact with the payroll system and start payroll processing.


## 💻 Program Details

The **MotorPH Automatic Payroll System** is a Java console-based application that automates payroll processing. The system reads employee information and attendance records from CSV files and calculates employee salaries based on their recorded working hours.

The program determines the total hours worked by each employee within a selected payroll cutoff period (1–15 or 16–31 of the month). Using the employee’s hourly rate, it calculates the gross salary and automatically applies government deductions such as **SSS, PhilHealth, Pag-IBIG, and withholding tax** to produce the final net salary.

The system allows users to log in either as an **employee** to view payroll details or as **payroll personnel** to process and review payroll data.

---

## 💻 Program Features

- Employee login system with username and 5-digit password  
- Payroll login for processing and reviewing employee payroll  
- Handles up to 1000 employees  
- Reads employee details from `EmployeeDetails.csv`  
- Reads attendance records from `AttendanceRecords.csv`  
- Calculates working hours, gross salary, and net salary  
- Handles cutoff periods (1–15, 16–31 of each month)  
- Computes deductions: SSS, PhilHealth, Pag-IBIG, and tax  
- Displays payroll by employee and by month  

---

## 📂 Program Files

- `MotorPH_Automatic_Payroll_System.java` – main program file  
- `EmployeeDetails.csv` – contains employee ID, name, birthday, and hourly rate  
- `AttendanceRecords.csv` – contains attendance logs with time-in and time-out  

---

## 📅 Project Plan (Estimation,Project Plan ,QA testing)

The project plan and development tasks were discussed and organized internally by the team during the development process.

https://docs.google.com/spreadsheets/d/13hAkgWlgDkVVQ-ZN1YtAlJpFTIJS0ifu/edit?usp=sharing&ouid=117006809110779909774&rtpof=true&sd=true
---

## ▶️ How to Run the Program

Follow these steps to run the MotorPH Automatic Payroll System.

### 1. Open the Project
Download or clone the repository and open the project using **NetBeans IDE**.

### 2. Verify Project Files
Make sure the following files are included in the project:

- `src/` – contains the Java source code
- `AttendanceRecords.csv` – contains employee attendance data
- `EmployeeDetails.csv` – contains employee information
- `nbproject/` – NetBeans project configuration files
- `build.xml` – project build configuration
- `manifest.mf` – project manifest file

### 3. Run the Program
1. Open the project in **NetBeans**.
2. Locate the main file inside the `src` folder:

```
src/motorph/motorph_automatic_payroll_system/MotorPH_Automatic_Payroll_System.java
```

3. Right-click the file and select **Run File**, or run the project normally.

### 4. Login to the System

When the program starts, the **Login Account Menu** will appear.

Use the following credentials:

Employee Account  
Username: `employee`  
Password: `12345`

Payroll Account  
Username: `payroll`  
Password: `12345`

### 5. Employee Menu

If logged in as **employee**, you can:

- Enter your **Employee ID**
- View employee information such as:
  - Employee Number
  - Name
  - Birthday
  - Hourly Rate

### 6. Payroll Menu

If logged in as **payroll**, you can:

1. **Process payroll for one employee**
   - Enter the employee ID
   - The system will display payroll details for each month

2. **Process payroll for all employees**
   - The system will display payroll records for every employee

### 7. Payroll Calculations

The program automatically calculates:

- Total working hours
- Gross salary
- Government deductions:
  - SSS
  - PhilHealth
  - Pag-IBIG
  - Withholding Tax
- Net salary

Payroll is displayed per **cutoff period (1–15 and 16–31)** and shows the **total monthly net salary**.
