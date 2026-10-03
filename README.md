# RESTAURANT ORDER AND BILLING SYSTEM

## Bill Kaun Bharega?

### “Pehle Khaao, Phir Socho.”

# STUDENT DETAILS

Name: Akhila Anish Das
Roll No.: 150096725016
Cohort: Larry Page
Course: B.Tech CSE
Institution: ITM Skills University
Project Type: Java Mini Project

# 1. PROJECT OVERVIEW

Restaurant Order and Billing System is a Java-based desktop application developed to digitize common restaurant operations.

The application provides a graphical interface for managing menu items, creating customer orders, calculating bills, generating PDF bills, tracking order status, and viewing sales summaries.

The project demonstrates the practical implementation of Object-Oriented Programming, Java Collections, Exception Handling, Streams, LocalDate, BigDecimal, File I/O, PDF generation, and Java Swing.

# 2. PROBLEM STATEMENT

A restaurant needs an efficient system to manage menu items, customer orders, quantities, discounts, taxes, bills, and order status.

Manual order management and billing can require additional time and may result in calculation or record-keeping errors.

This project provides a desktop-based solution that organizes these operations into a single application.

# 3. OBJECTIVES

• Maintain and manage restaurant menu items
• Create and modify customer orders
• Manage item quantities
• Calculate subtotal, discount, tax, and grand total
• Generate printable PDF bills
• Track order status
• Search and filter orders
• Display sales summaries
• Reduce manual work and calculation errors
• Demonstrate important Java programming concepts

# 4. CORE FEATURES

### Menu Management

Add, update, delete, and save restaurant menu items.

### Order Management

Create customer orders and modify existing orders.

### Billing System

Calculate subtotal, discount, tax, and final total automatically.

### Bill Generation

Generate and save printable PDF bills containing customer and order details.

### Order Status

Track the progress of orders using different order statuses.

### Sales Summary

Display completed order information and important sales statistics.

# 5. APPLICATION WORKFLOW

Menu → Select Items → Create Order → Calculate Bill → Apply Discount & Tax → Generate Bill → Update Order Status

# 6. JAVA CONCEPTS DEMONSTRATED

• Classes and Objects
• Constructors
• Encapsulation
• Inheritance
• Polymorphism
• Method Overloading and Overriding
• ArrayList and Collections
• Exception Handling
• Streams
• LocalDate
• BigDecimal
• File I/O
• Java Swing GUI

# 7. TECHNOLOGIES USED

Programming Language: Java

GUI Framework: Java Swing

Date Handling: LocalDate

Money Calculation: BigDecimal

Collections: ArrayList and Java Collections

Data Storage: File I/O

Bill Generation: PDF generation

Development Environment: Visual Studio Code

JDK Used: Java 26

# 8. PROJECT STRUCTURE

JAVA-AKHILA-CASE-STUDY-FINAL/

├── bills/
│   └── Generated PDF bills

├── data/
│   └── menu_data.txt

├── out/
│   └── Compiled Java class files

├── screenshots/
│   └── Project screenshots

├── BillKaunBharega.java

└── README.md

# 9. APPLICATION MODULES

## New Order

The New Order section allows the user to:

• Enter customer information
• Select menu items
• Select quantities
• Create an order
• Apply discounts
• Apply tax
• Calculate the final bill
• Generate a PDF bill
• Set the order status

## Orders

The Orders section displays created orders and their details.

It provides functionality for:

• Viewing orders
• Searching orders
• Filtering orders by status
• Selecting existing orders
• Modifying orders
• Updating order status
• Generating bills

## Menu Management

The Menu Management section provides a structured table for restaurant menu items.

Users can:

• Add menu items
• Edit menu items
• Delete menu items
• Save menu changes
• View items according to their categories

## Sales Summary

The Sales Summary section provides an overview of restaurant activity.

It displays information such as:

• Total Orders
• Revenue
• Subtotal
• Total Discount
• Total Tax
• Items Sold
• Average Order Value

# 10. ORDER STATUS

The application supports the following order statuses:

• Pending
• Preparing
• Ready
• Completed
• Cancelled

# 11. BILL CALCULATION

The application calculates the bill using the following flow:

Subtotal
↓
Discount
↓
Tax
↓
Grand Total

BigDecimal is used for monetary calculations to provide accurate financial values.

# 12. PDF BILL GENERATION

The application generates a printable PDF bill for customer orders.

The generated bill contains relevant information such as:

• Customer Name
• Order ID
• Date
• Purchased Items
• Quantity
• Item Price
• Subtotal
• Discount
• Tax
• Grand Total

Generated PDF bills are stored inside the:

bills/

folder.

# 13. DATA STORAGE

Menu information is stored locally in:

data/menu_data.txt

The application creates and uses the required data and bills directories for storing application information.

Menu information can persist between application sessions, while active order information is handled according to the application's session workflow.

# 14. VALIDATION AND EXCEPTION HANDLING

The application validates important user inputs including:

• Customer information
• Menu item selection
• Quantity
• Price values
• Discount values
• Tax values
• Order selection

Exception handling is used to handle invalid input and file-related problems.

# 15. OBJECT-ORIENTED PROGRAMMING

The project applies Object-Oriented Programming through classes representing different parts of the application.

The main classes include:

• BillKaunBharega
• MenuItemData
• Order
• OrderItem
• ValidationException
• SimplePDF

These classes work together to manage the graphical interface, menu information, orders, validation, calculations, and bill generation.

# 16. COLLECTIONS AND STREAMS

ArrayList and other Java Collections are used to manage menu items, orders, and order items.

Streams are used for operations such as:

• Filtering
• Searching
• Processing collections
• Calculating totals
• Generating sales-related information

# 17. DATE AND MONEY HANDLING

LocalDate is used for handling order dates.

BigDecimal is used for monetary calculations involving:

• Item prices
• Subtotal
• Discount
• Tax
• Grand Total

# 18. FILE I/O

File I/O is used to:

• Read menu information
• Save menu changes
• Create required folders
• Store generated bills
• Export application information

# 19. GUI

The application is developed using Java Swing.

The main application sections are:

• New Order
• Orders
• Menu Management
• Sales Summary

The GUI provides a single desktop interface for performing the major restaurant operations.

# 20. SCREENSHOTS

## 01 — Main Application

![Main Application](screenshots/01_Main_Application.png)

The main application screen provides access to the restaurant order and billing workflow.

## 02 — Menu Management

![Menu Management](screenshots/02_Menu_Management.png)

Shows the menu management interface used to add, edit, delete, save, and manage restaurant menu items.

## 03 — Create Order

![Create Order](screenshots/03_Create_Order.png)

Shows the process of selecting menu items and creating a customer order.

## 04 — Bill Calculation

![Bill Calculation](screenshots/04_Bill_Calculation.png)

Shows the calculated subtotal, discount, tax, and grand total.

## 05 — Generated PDF Bill

![Generated PDF Bill](screenshots/05_Generated_PDF_Bill.png)

Shows the generated printable PDF bill containing customer, order, item, and billing information.

## 06 — Orders Management

![Orders Management](screenshots/06_Orders_Management.png)

Shows the Orders section containing created restaurant orders and their information.

## 07 — Order Status

![Order Status](screenshots/07_Order_Status.png)

Shows the order status management functionality used to track order progress.

## 08 — Search and Filter Orders

![Search and Filter Orders](screenshots/08_Search_Filter_Orders.png)

Shows the search and filtering functionality available for managing existing orders.

## 09 — Sales Summary

![Sales Summary](screenshots/09_Sales_Summary.png)

Shows the sales summary containing important restaurant sales information and statistics.

## 10 — Project Folder Structure

![Project Folder Structure](screenshots/10_Project_Folder_Structure.png)

Shows the organized project structure containing the Java source file, data folder, bills folder, compiled output, screenshots, and README.

## 11 — Modify Order

![Modify Order](screenshots/11_Modify_Order.png)

Shows the functionality for selecting and modifying an existing customer order.

## 12 — Detailed Sales Summary

![Detailed Sales Summary](screenshots/12_Detailed_Sales_Summary.png)

Shows the detailed sales information generated by the application.

# 21. HOW TO DOWNLOAD AND RUN

## Requirements

Before running the project, install:

• Java Development Kit
• Visual Studio Code or another Java IDE
• A desktop operating system with Java support

The project was developed using JDK 26.

## Getting the Project

Download or clone this GitHub repository to your computer.

Open the project folder in Visual Studio Code or your preferred Java development environment.

## Running the Project

Open the project folder.

Make sure the Java source file and required project folders are present.

Compile and run:

BillKaunBharega.java

The application will open as a Java Swing desktop application.

The required data and bills folders can be created and used by the application during execution.

# 22. GUIDE FOR STUDENTS

This project can be used as a study reference for students learning Java desktop application development.

Recommended study order:

1. Understand the overall restaurant workflow.
2. Study the main Java application class.
3. Understand how the Swing GUI is created.
4. Study the menu item and order-related classes.
5. Follow how an order is created.
6. Understand the bill calculation process.
7. Study BigDecimal for monetary calculations.
8. Study LocalDate for date handling.
9. Understand how ArrayList and Collections are used.
10. Identify the use of Streams.
11. Study validation and exception handling.
12. Understand File I/O and menu data storage.
13. Study PDF bill generation.
14. Modify the project and experiment with additional features.

# 23. POSSIBLE EXTENSIONS

Students can further develop the project by adding features such as:

• Database integration
• User login and authentication
• Customer management
• Inventory management
• Advanced sales reports
• More detailed receipt customization
• Additional restaurant categories
• Improved reporting and analytics

# 24. PROJECT OUTCOME

• Digitizes restaurant order management
• Automates bill calculation
• Generates printable customer bills
• Supports order status management
• Provides sales summaries
• Demonstrates important Java programming concepts
• Provides a simple and user-friendly GUI
• Reduces manual work and calculation errors

# 25. CONCLUSION

The Restaurant Order and Billing System demonstrates how Java can be used to develop a practical desktop application based on a real-world restaurant management problem.

The project combines Object-Oriented Programming, Collections, Streams, Exception Handling, LocalDate, BigDecimal, File I/O, PDF generation, and Java Swing into one application.

The complete workflow covers menu management, order creation, bill calculation, bill generation, order status management, and sales summary.

# 26. PROJECT INFORMATION

Project Name: Restaurant Order and Billing System

Application Name: Bill Kaun Bharega?

Tagline: Pehle Khaao, Phir Socho.

Student: Akhila Anish Das

Roll No.: 150096725016

Cohort: Larry Page

Course: B.Tech CSE

Institution: ITM Skills University

Programming Language: Java

GUI: Java Swing

JDK: 26

Project Type: Java Mini Project

# 27. AUTHOR

Akhila Anish Das

B.Tech CSE
ITM Skills University

Academic Java Project — 2026
