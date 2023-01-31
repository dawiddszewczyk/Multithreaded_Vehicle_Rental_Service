# Introduction
Focused on multithreading university project of "Vehicle Rental Service" which has been made using JDK 18.0.1, JavaFX, CSS, Hibernate (with MySQL) in client-server architecture.
# How to use?
Project should be downloaded and imported to IDE with the use of Maven. Additionaly the connection with mySQL database server should be estabilished (project has been created with the use of Xampp and MySQLWorkbench Community Edition). After the connection estabilishment the database should be imported with the use of **.sql** files that are added to the project.

There are two versions of the **.sql** files prepared, for Xampp and MySQLWorkbench. Vehicles that are going to be used in the application should be added from the database level. In the directory containing **.sql** files, there have been added a **.txt** file with code to initialize three vehicles.

This is almost the end of DB configuration. At the very end it is required to edit the **hibernate.cfg.xml** file in src/main/resources/hibernate_configs directory, **connection.url**, **connection.username** and **connection.password** should be edited.

![image](https://user-images.githubusercontent.com/106389146/209480738-342b1901-151c-4c42-ac2d-813ca748601b.png)

Here, the path to the user's SQL server instance as well as connection login/password should be provided. URL's port should be edited (by default it is 3306 if there is only one DB server software installed with the default configuration) and the DB name. In the project it is named "wypozyczalnia" by default.

In case of Xampp, be default the server login credentials are "root" as username and "" as password (no password) as demonstrated in the screenshot.

In order to launch the application from IDE level it is required to launch the server first as a Java application using **MainServer.java** and after that client instance can be launched multiple times (for as long as resources allow to do that) using **App.java** with GUI. Client instance should be launched using Maven, adding correct launching parameters in "run configuration".

### The example of application launching using Eclipse IDE for Enterprise Java and Web Developers:
![image](https://user-images.githubusercontent.com/106389146/209480588-8b104d1a-99a2-42d8-83c8-e321c2ba2f68.png)

In the screenshot there can be seen parameters in the field "Goals": **clean javafx:run**

# How does it work?
### Logging in/home screen
![image](https://user-images.githubusercontent.com/106389146/209480920-a8ccc4e4-5383-4261-b014-77c3589f7f80.png)

After launching the application, this window is going to be shown. After the account has been created there is a possibility of logging in after providing the correct email and password. In case of providing invalid credentials the appropriate information will be shown. There is also a possibility of quitting the application with the use of button "Wyjście" or moving to registration part with the use of "Register" button.
### Registration
![image](https://user-images.githubusercontent.com/106389146/209480952-be14ad9c-aefb-45b6-b730-c76c5077d69b.png)

In this window there is a possibility of registration. User is able to provide required credentials and finishing the registration process by pressing the "Zarejestruj" button or moving backwards by pressing the "Cofnij" button. In case of providing invalid credentials, user is going to be informed. After the successful registration user will be moved to the login screen with confirmation.
![image](https://user-images.githubusercontent.com/106389146/209480956-54be3b38-5642-4c30-8067-6106f825c7b5.png)

### After logging in, vehicle list
![image](https://user-images.githubusercontent.com/106389146/209480971-f3322e2b-2c62-486f-8905-aefb85406ec3.png)

User is going to receive the list of available (and not currently used) vehicles. In reality, every vehicle would be marked with stickers with certain ID value. User needs to find certain vehicle by providing it's ID in text field and by pressing the button "Wyszukaj".
After finishing the process, user needs to double click on the certain vehicle record and confirm the rental.

![image](https://user-images.githubusercontent.com/106389146/209480974-4530d2e8-9067-48c9-923c-96159a137984.png)

There is also a possibility of refreshing the vehicle list by clicking "Odśwież" or quitting the app by clicking "Wyjdź".
User has a possibility of viewing his username and debit:

![image](https://user-images.githubusercontent.com/106389146/209480981-32db5064-ca7c-4e2f-a4c4-cb3639809f4f.png)

### Rental process
After picking the certain vehicle and confirmation, the vehicle will be shown with it's related information. After each second, user is going to be charged for a certain price and the state of vehicle's battery/range will be adjusted.

![image](https://user-images.githubusercontent.com/106389146/209480990-b05fed3a-8c95-449e-a0c3-e1a3c44536c5.png)

Rental can be finished by pressing "Skończ" and confirming. After unidentified application shutdown, user and vehicle statuses are going to be adjusted server-side.






