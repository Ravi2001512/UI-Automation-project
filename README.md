# Tuition Platform UI Automation Framework

![Allure Report](https://img.shields.io/badge/Allure%20Report-90.38%25%20Passing-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.45.0-blue)
![TestNG](https://img.shields.io/badge/TestNG-7.12.0-red)
![Build](https://img.shields.io/badge/Build-Maven-green)

An End-to-End (E2E) UI Automation Testing Framework built for the **Tuition Management Platform (UCSC Group A)** admin web application. Built with Java, Selenium WebDriver, TestNG, Maven, and integrated with Allure Reporting for CI/CD test execution analytics.

---

## 🔗 Repository Details

* **Repository URL:** `https://github.com/Ravi2001512/UI-Automation-project.git`
* **Local Workspace Path:** `D:\UCSC\UI-Automation-project`

---

## 📊 Test Execution Summary & Allure Analytics

The automation suite is configured to generate detailed execution analytics via **Allure Reports** integrated into **Jenkins CI/CD**.

| Metric | Overview |
| :--- | :--- |
| **Total Test Cases Executed** | **52 Tests** |
| **Overall Pass Rate** | **90.38%** |
| **UI Automation Regression Suite** | **47 Tests** |
| **Default Suite** | **4 Tests** |
| **Categorized Defects** | 1 Product Defect • 4 Test Defects |

---

## 🛠️ Tech Stack & Key Libraries

* **Programming Language:** Java 17 (JDK)
* **Automation Engine:** Selenium WebDriver (`4.45.0`)
* **Test Runner:** TestNG (`7.12.0`)
* **Design Pattern:** Page Object Model (POM)
* **Build & Dependency Management:** Apache Maven
* **Logging & Reporting:** SLF4J & Allure Test Report (`2.x`)
* **Continuous Integration:** Jenkins CI/CD Job (`UI-Automation`)

---

## 📁 Project Structure

The project strictly follows the **Page Object Model (POM)** architectural pattern:

UI-Automation-project
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── ucsc
│   │               └── tutionplatform
│   │                   └── pages
│   │                       ├── BasePage.java
│   │                       ├── ClassExaminationPage.java
│   │                       ├── LoginPage.java
│   │                       └── UserPage.java
│   └── test
│       └── java
│           └── com
│               └── ucsc
│                   └── tutionplatform
│                       └── tests
│                           ├── BaseTest.java
│                           ├── classexaminations
│                           │   ├── ExaminationBaseTest.java
│                           │   ├── CalculateAverageTest.java
                           │   ├── CreateExaminationTest.java
                           │   ├── ExamSelectionTest.java
                           │   ├── SearchExamDetailsTest.java
                           │   └── SingleUploadTest.java
│                           ├── login
│                           │   └── LoginPageTest.java
│                           └── userdetails
│                               ├── UserBaseTest.java
│                               ├── CreateUserNewTest.java
│                               ├── CreateUserTest.java
│                               ├── EditUserTest.java
│                               └── SearchUserTest.java
├── pom.xml
└── testng.xml
🧪 Module & Test Suite Breakdown
1. Class Examination Module (32 Tests)
CalculateAverageTest: Verifies dynamic selection of visible students and accurate mark average calculations.

CreateExaminationTest: Automated end-to-end creation of new exams with subject boundaries and parameters.

ExamSelectionTest: Validates dynamic UI selection, checkboxes, and bulk action controls.

SearchExamDetailsTest: Tests exam filter triggers, search fields, and card data rendering.

SingleUploadTest: Validates individual mark submissions and form validations.

2. User Details Module (13 Tests)
CreateUserNewTest & CreateUserTest: Tests creation of system users and role assignments.

EditUserTest: Modifies user attributes and updates records via inline/modal forms.

SearchUserTest: Comprehensive search filter testing across user tables.

3. Login Module (2 Tests)
LoginPageTest: Tests authentication flows, valid/invalid credentials, and dynamic token persistence.

🚀 Getting Started
Prerequisites
Java Development Kit (JDK 17+)

Apache Maven 3.8+

Google Chrome Browser (matching ChromeDriver requirements)

1. Clone the Repository
Bash
git clone [https://github.com/Ravi2001512/UI-Automation-project.git](https://github.com/Ravi2001512/UI-Automation-project.git)
cd UI-Automation-project
2. Run Test Suite
Run the full regression test suite via Maven:

Bash
mvn clean test
Or run a specific TestNG XML suite:

Bash
mvn test -DsuiteXmlFile=testng.xml
3. Generate & View Allure Report Locally
After running the tests, execute:

Bash
mvn allure:report
mvn allure:serve

<img width="946" height="413" alt="image" src="https://github.com/user-attachments/assets/683912da-9c65-4c5c-9852-a10dbf637e6e" />

<img width="938" height="386" alt="image" src="https://github.com/user-attachments/assets/be3bc991-339a-4f69-b289-7792b73cc1d1" />
