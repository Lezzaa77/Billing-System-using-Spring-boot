# Billing System (Java + Spring Boot + MySQL)

A simple billing / stock system:

- Sign up and log in (passwords stored with BCrypt)
- **Home page** that lists every item under its category, with category totals
- **Add item**, **Update item** and **Delete item** as separate pages (each has a "Back to Home" button)
- **Sell item** page: records a sale and reduces stock automatically
- **Total sales report**: summary, sales by category, top items, sales by day, optional date range.
  Use **Download PDF** (a real PDF file) or **Print**.

Built with: Java 17, Spring Boot 3.3 (Spring MVC, Thymeleaf, Spring Security, Spring Data JPA/Hibernate), MySQL 8, Maven.

---------------------------------------------------------------------

## 1. Install the tools (once)

| Tool | Version | Check it works |
|---|---|---|
| JDK (Java) | 17 or newer (21 is fine) | `java -version` and `javac -version` |
| Apache Maven | 3.9 or newer | `mvn -v` |
| MySQL Server | 8.x (MariaDB 10.5+ also works) | `mysql --version` |
| VS Code | any recent | - |

VS Code extensions (Extensions icon on the left, search by name, click Install):
1. **Extension Pack for Java** (Microsoft)
2. **Spring Boot Extension Pack** (VMware) - optional, but gives a "Spring Boot Dashboard" with a Run button

Tips for Windows:
- After installing the JDK, make sure the `JAVA_HOME` environment variable points to the JDK folder
  (for example `C:\Program Files\Java\jdk-17`).
- Maven has no installer: download the "Binary zip archive" from https://maven.apache.org/download.cgi, extract it
  (for example to `C:\apache-maven-3.9.9`), and add its `bin` folder to the `Path` environment variable.
- **Close and re-open VS Code** after changing environment variables.

## 2. Make sure MySQL is running and note the root password

- Windows: Start menu > Services > "MySQL80" should say Running (or use MySQL Workbench / XAMPP control panel).
- You do NOT need to create the database or the tables. The app creates the database `billing_spring` and the tables
  `users`, `items` and `sales` by itself the first time it starts.

Open `src/main/resources/application.properties` and set your MySQL password:

```
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
(Leave `password=` empty only if your MySQL root user really has no password.)

## 3. Run it in VS Code

1. Unzip `billing-spring.zip`.
2. VS Code > **File > Open Folder...** > choose the `billing-spring` folder (the one that contains `pom.xml`).
3. Wait until the bottom-right status bar stops showing "Java: Importing / Loading" (the first time Maven downloads
   the libraries; this needs internet and can take a few minutes).
4. Open the terminal inside VS Code (**Terminal > New Terminal**) and run:

   ```
   mvn spring-boot:run
   ```
   (Or open `src/main/java/com/billing/BillingApplication.java` and click **Run** above `main`.)
5. Wait for the line `Started BillingApplication in ... seconds`.
6. Open **http://localhost:8080** in your browser.
7. To stop the app press `Ctrl + C` in the terminal.

## 4. Use the app

1. Click **Sign up**, fill in name, email and password (at least 6 characters), click **Create Account**.
2. **Login** with that email and password. You land on the **Home** page.
3. The first run adds 4 sample items and 3 sample sales so you can see something (see "Settings" to switch this off).
4. **Home**: all items grouped by category with quantity, price and stock value. Each row has **Update** and **Delete**.
5. **Add Item**: fill Item ID, Name, Category (pick an existing one or type a new one), Quantity, Unit Price,
   Description > **Save Item**. Item IDs must be unique.
6. **Update**: click Update on a row, change the fields, click **Update Item**.
7. **Delete**: click Delete on a row, check the details, click **Yes, Delete**. Past sales stay in the report.
8. **Sell**: choose an item, quantity and an optional customer name > **Record Sale**. Stock goes down. Selling
   more than the stock is refused.
9. **Report**: shows total revenue, number of sales, units sold, average sale, sales by category, top 10 items and
   sales by day. Pick From / To dates and click **Show** to filter. Click **Download PDF** to get a PDF file,
   or **Print**.
10. Every page except Home has **Back to Home**. **Logout** is in the top bar.

## 5. Build one runnable file (optional)

```
mvn clean package
java -jar target/billing-system-1.0.0.jar
```
You can copy that single jar to another computer that has Java 17 and MySQL and run the same command.
The settings can be overridden without editing files, for example:
```
java -jar target/billing-system-1.0.0.jar --spring.datasource.password=secret --server.port=9090
```

## 6. Settings (`src/main/resources/application.properties`)

| Setting | What it does |
|---|---|
| `spring.datasource.url` | MySQL address and database name (`billing_spring`). Change `localhost:3306` if MySQL is elsewhere. |
| `spring.datasource.username` / `password` | MySQL login |
| `server.port` | Web port (default 8080) |
| `billing.seed-sample-data` | `true` = add sample items/sales on an empty database; `false` = start empty |
| `spring.jpa.hibernate.ddl-auto` | `update` = create / update tables automatically |

To start again from nothing: stop the app, run `DROP DATABASE billing_spring;` in MySQL, start the app again.

## 7. File by file

**Project root**
- `pom.xml` - Maven file: Java version, Spring Boot version and the libraries used (web, thymeleaf, data-jpa, security, validation, mysql driver).

**`src/main/resources`**
- `application.properties` - database login, port, sample-data switch.
- `messages.properties` - friendly messages when someone types text in a number box.
- `static/css/style.css` - the simple styling (also hides buttons when printing).
- `templates/fragments/layout.html` - shared `<head>` and top bar (Home / Add Item / Sell / Report / Logout).
- `templates/fragments/item-fields.html` - the item input boxes shared by the Add and Update pages.
- `templates/login.html`, `signup.html` - login and sign-up pages.
- `templates/home.html` - totals, buttons and the item list grouped by category.
- `templates/add-item.html`, `update-item.html`, `delete-item.html` - the three separate item pages (each with Back to Home).
- `templates/sell.html` - record a sale + recent sales.
- `templates/report.html` - the total sales report (date filter, Download PDF, Print).

**`src/main/java/com/billing`**
- `BillingApplication.java` - starts the app.
- `config/SecurityConfig.java` - which pages need login, login/logout settings, BCrypt password encoder.
- `config/DataSeeder.java` - adds the sample data on first start.
- `model/AppUser.java`, `Item.java`, `Sale.java` - database tables `users`, `items`, `sales`.
  A sale keeps its own copy of the item name, category and price, so reports stay correct if the item changes later.
- `repository/UserRepository.java`, `ItemRepository.java`, `SaleRepository.java` - database queries.
  `ItemRepository.findByIdForUpdate` locks the item row while selling.
- `dto/ItemForm.java`, `SignupForm.java`, `SaleForm.java` - form fields with their validation rules.
- `dto/CategoryGroup.java` - one category with its items and totals (home page).
- `dto/SalesReport.java` - the report numbers (summary, by category, top items, by day).
- `service/UserService.java`, `CustomUserDetailsService.java` - register users; look users up for login.
- `service/ItemService.java` - add / update / delete items, group by category.
- `service/SaleService.java` - the sale itself: check stock, reduce stock, save the sale, all in one transaction.
- `service/ReportService.java` - the report SQL (JdbcTemplate) and the period text.
- `service/BusinessException.java` - "expected" errors such as "Not enough stock".
- `controller/AuthController.java` - `/login`, `/signup`.
- `controller/HomeController.java` - `/home`.
- `controller/ItemController.java` - `/items/add`, `/items/{id}/edit`, `/items/{id}/delete`.
- `controller/SaleController.java` - `/sell`.
- `controller/ReportController.java` - `/report` and `/report/pdf`.
- `controller/GlobalModelAttributes.java` - puts the logged-in user's name in the top bar.
- `util/SimplePdf.java`, `ReportPdf.java` - a small PDF writer (no extra library) and the report layout.

## 8. Troubleshooting

| Message / problem | Fix |
|---|---|
| `mvn` is not recognized | Maven is not installed or its `bin` folder is not in `Path`. Fix, then restart VS Code. |
| `invalid target release: 17` / `release version 17 not supported` | Your JDK is older than 17. Install JDK 17+ and set `JAVA_HOME`. |
| `Access denied for user 'root'@'localhost'` | Wrong MySQL password in `application.properties`. |
| `Communications link failure` / `Connection refused` | MySQL is not running, or the port in `spring.datasource.url` is wrong. |
| `Port 8080 was already in use` | Close the other program, or set `server.port=9090` and open http://localhost:9090. |
| Maven cannot download libraries | Check your internet connection / proxy / firewall. |
| Page says "Whitelabel Error Page" | Look at the terminal where the app runs; the real error is printed there. |
| Logged out unexpectedly after restart | Normal: login sessions are kept in memory and cleared when the app restarts. |

## 9. Notes

- This project was written without access to a real MySQL server or the Maven download servers, so it has not been
  run end to end by its author. The Java code compiles against the framework API shapes, the PDF writer and all
  templates were tested separately. If anything fails on your machine, copy the red error from the terminal and
  send it - it is normally a one-line fix.
- Passwords are stored as BCrypt hashes; forms are protected with CSRF tokens; all SQL uses parameters.


---
----
## -- Task	What to do --
- Start database	- Start MySQL in XAMPP
- Start project	- mvn spring-boot:run
- Open website -	http://localhost:8080
- Stop project -	Ctrl + C
- Stop database -	Stop MySQL in XAMPP


Important: You only need to run git push when you make changes and want to upload those changes to GitHub. You don't need GitHub to run your project locally.
