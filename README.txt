# Software 2 Scheduling Application
This application was built for the course C195 for WGU. It allows a user to to view, add, edit, and delete appointments
and customers for a company. Users can also generate relevant reports for appointment types, contact schedules, and
customer schedules. Robust input validation and data handling (for a database supplied by WGU) are built into the
application and supports English/French locale on the login page.

## Project Information
Author: Chance Crawford
Contact Info:
Student Application Version:
Date:

IDE: IntelliJ IDEA 2021.1.3 (Community Edition)
JDK Version: Java SE 11.0.10
JavaFX Version: JavaFX-SDK-11.0.2

## Directions for use:
Login with username and password **test** after launching the application. Navigation between different portions of the
application are in the top part of the page.

### Appointments
Upon login, an alert with generate with upcoming appointments or a message letting the user know there are none.
From here you can view appointments via month or week, starting with the month or week of. At the bottom are options
for adding, editing, or deleting appointments. Appointments cannot conflict with other appointments based on other
customer appointment times.

### Customers
The customer page is similar to the appointments page; customers can be added, edited, and deleted. Customers **cannot**
be deleted if they still have appointments.

### Reports
Reports to be viewed are selected via a dropdown box near the top of the page, below the navigation buttons. The three
types of reports a user can view are appointment types by month, contact schedules, and customer schedules. Contact and
customer schedules can be changed to specific members via another dropdown below the reports dropdown as well.

### Logout
Users can logout of the application via the **Logout** button in the upper right-hand corner.

## Customer Schedule Report
The additional report chosen was for customer schedules. It displays the appointment title, date, and time for all
of that customers appointments in a table view.

## MySQL Connector Driver
Version: mysql-connector-java-8.0.24