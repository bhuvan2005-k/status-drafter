# How to Run the Status Drafter Tool

Follow these steps to get the project up and running on your local machine.

## Prerequisites
1.  **Java 21**: Verify with `java -version`.
2.  **MySQL Server**: Ensure MySQL is running on your machine.
3.  **Maven**: (Optional if using IDE) or use the command line if installed.

---

## Step 1: Running the App (Zero Setup)
I have configured the project to use an **H2 In-Memory Database** by default. This means you can run it immediately without installing MySQL.

1.  Open the project in your IDE (IntelliJ/Eclipse).
2.  Run `StatusDrafterApplication.java`.
3.  The database will auto-seed with a test user and parsing rules.

---

## Step 2: (Optional) MySQL Setup
If you prefer to use MySQL:
1.  See `schema.sql` for table creation.
2.  Update `src/main/resources/application.properties` to uncomment the MySQL section.

## Step 3: Build & Run
### Option A: Using Maven (Command Line)
If you have Maven installed, run:
```powershell
mvn clean install
mvn spring-boot:run
```

### Option B: Using an IDE (Recommended)
1.  Open **IntelliJ IDEA** or **Eclipse**.
2.  Import the project as a **Maven Project** (Point to the `pom.xml` file).
3.  Wait for dependencies to download.
4.  Run the `StatusDrafterApplication.java` file.

---

## Step 4: Verify the Setup
Once the application starts (usually on `http://localhost:8080`), you can test it:

1.  **Health Check**:
    Open `http://localhost:8080/api/v1/status/test` in your browser. You should see "Status Drafter Tool is Online!".

2.  **Submit a Status (using Postman or cURL)**:
    ```bash
    curl -X POST http://localhost:8080/api/v1/status \
         -H "Content-Type: application/json" \
         -d '{
               "userId": 1,
               "rawContent": "Yesterday I worked on the login page for 2h. Today I will fix bugs. No blockers."
             }'
    ```

3.  **Get Daily Report**:
    Open `http://localhost:8080/api/v1/status/daily`
