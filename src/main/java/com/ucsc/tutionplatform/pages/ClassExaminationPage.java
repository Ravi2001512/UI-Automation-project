package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassExaminationPage extends BasePage {

    private final WebDriverWait wait;

    // ---------------------------------------------------------------- scopes
    private static final String EXAM_FORM = "//article[contains(@class,'detail-card')]//form[contains(@class,'detail-form')]";
    private static final String GRADES_BLOCK = EXAM_FORM + "//div[contains(@class,'grades-block')]";
    private static final String EXAM_FORM_ACTIONS = EXAM_FORM + "/div[contains(@class,'form-actions')]";

    private static final String SNAPSHOT_CARD = "//article[contains(@class,'user-list-card')]";

    private static String panel(String kicker) {
        return "//section[contains(@class,'marks-entry-panel')]"
                + "[.//p[contains(@class,'panel-kicker')][normalize-space()=" + xpathLiteral(kicker) + "]]";
    }

    private static final String MARKS_PANEL = panel("Exam Marks");
    private static final String AVERAGES_PANEL = panel("Exam Averages");
    private static final String BULK_BLOCK = MARKS_PANEL + "//div[contains(@class,'exam-import-block')]";
    private static final String MARKS_FORM = MARKS_PANEL + "//form[contains(@class,'detail-form')]";

    private static String nestedCard(String kicker) {
        return AVERAGES_PANEL + "//article[contains(@class,'nested-card')]"
                + "[.//p[contains(@class,'panel-kicker')][normalize-space()=" + xpathLiteral(kicker) + "]]";
    }

    private static final String STUDENT_SELECT_CARD = nestedCard("Student Selection");
    private static final String EXAM_SELECT_CARD = nestedCard("Exam Selection");

    // Create Examination form
    private By examTypeSelect = By.xpath(labeledSelect(EXAM_FORM, "Exam Type"));
    private By alYearInput = By.xpath(labeledInput(EXAM_FORM, "A/L Year"));
    private By examNumberInput = By.xpath(labeledInput(EXAM_FORM, "Exam Number"));
    private By examLocationSelect = By.xpath(labeledSelect(EXAM_FORM, "Exam Location"));
    private By examTitleInput = By.xpath(labeledInput(EXAM_FORM, "Exam Title"));

    private By createExamBtn = By.xpath(EXAM_FORM_ACTIONS + "/button[@type='submit']");
    private By clearExamFormBtn = By.xpath(buttonWithText(EXAM_FORM_ACTIONS, "Clear"));

    // Grade bands
    private By addGradeBtn = By.xpath(buttonWithText(GRADES_BLOCK, "Add Grade"));

    private static final String GRADE_ROW = GRADES_BLOCK
            + "//div[contains(@class,'grade-row')][not(contains(@class,'grade-row-head'))]";

    private By gradeRows = By.xpath(GRADE_ROW);

    public By gradeInput(int row) {
        return By.xpath("(" + GRADE_ROW + ")[" + row + "]/input[@placeholder='Grade']");
    }

    public By gradeMaxInput(int row) {
        return By.xpath("(" + GRADE_ROW + ")[" + row + "]/input[@placeholder='Max']");
    }

    public By gradeMinInput(int row) {
        return By.xpath("(" + GRADE_ROW + ")[" + row + "]/input[@placeholder='Min']");
    }

    public By removeGradeBtn(int row) {
        return By.xpath(buttonWithText("(" + GRADE_ROW + ")[" + row + "]", "Remove"));
    }

    public By removeGradeBtn(String gradeLetter) {
        String row = GRADE_ROW + "[./input[@placeholder='Grade'][@value=" + xpathLiteral(gradeLetter) + "]]";
        return By.xpath(buttonWithText(row, "Remove"));
    }

    // Defined Exams & Snapshot
    private By examSnapshotHeading = By.xpath(SNAPSHOT_CARD + "//h3[normalize-space()='Exam and Grade Snapshot']");
    private By examSnapshotSearchInput = By.cssSelector(
            "article.user-list-card .search-input-row input[placeholder='Search by exam title, type, A/L year, exam number, location, or grade band']");
    private By examSnapshotClearBtn = By
            .xpath(SNAPSHOT_CARD + "//div[contains(@class,'search-input-row')]/button[normalize-space()='Clear']");
    private By examCards = By.cssSelector("article.user-list-card .exam-snapshot-list > article.exam-card-row");
    private By selectedExamCard = By
            .xpath(SNAPSHOT_CARD + "//article[contains(@class,'exam-card-row')][contains(@class,'selected')]");

    private String examCardXpath(String title) {
        return SNAPSHOT_CARD + "//article[contains(@class,'exam-card-row')]"
                + "[.//strong[normalize-space()=" + xpathLiteral(title) + "]]";
    }

    public By examCard(String title) {
        return By.xpath(examCardXpath(title));
    }

    public By selectExamForMarks(String title) {
        return By.xpath(examCardXpath(title) + "//button[contains(@class,'exam-select-btn')]");
    }

    public By examSummaryLine(String title) {
        return By.xpath(examCardXpath(title) + "//button//p[1]");
    }

    public By examGradePills(String title) {
        return By.xpath(examCardXpath(title) + "//div[contains(@class,'exam-grade-pills')]/span");
    }

    public By examGradePill(String title, String gradeLetter) {
        return By.xpath(examCardXpath(title) + "//div[contains(@class,'exam-grade-pills')]"
                + "/span[starts-with(normalize-space(), " + xpathLiteral(gradeLetter + ":") + ")]");
    }

    // Exam Marks — bulk upload
    private By bulkUploadExamSelect = By.xpath(labeledSelect(BULK_BLOCK, "Select Exam for Bulk Upload"));
    private By downloadTemplateBtn = By.xpath(buttonWithText(BULK_BLOCK, "Download Template"));
    private By studentIdCriteriaInput = By.xpath(BULK_BLOCK + "//input[@placeholder='T2830 T2820']");
    private By xlsxFileInput = By.xpath(BULK_BLOCK + "//input[@type='file']");
    private By uploadXlsxBtn = By.xpath(buttonWithText(BULK_BLOCK, "Upload XLSX"));

    // Exam Marks — single entry
    private By marksExamSelect = By.xpath(labeledSelect(MARKS_FORM, "Select Exam"));
    private By markInput = By.xpath(labeledInput(MARKS_FORM, "Mark"));
    private By studentSearchInput = By.xpath(labeledInput(MARKS_FORM, "Search Student"));
    private By studentSearchClearBtn = By
            .xpath(searchClearButton(MARKS_FORM + "//label[contains(@class,'full-span')]"));
    private By searchStudentBtn = By
            .xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'student-mark-search-row')]", "Search Student"));

    private By saveMarkBtn = By
            .xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'form-actions')]", "Save Mark"));
    private By clearMarkFormBtn = By
            .xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'form-actions')]", "Clear"));

    // Saved marks
    private static final String SAVED_MARKS = MARKS_PANEL + "//div[contains(@class,'marks-list-block')]";
    private By savedMarksHeading = By.xpath(SAVED_MARKS + "//h3");
    private By savedMarksEmptyHint = By.xpath(SAVED_MARKS + "/p[contains(@class,'hint')]");
    private By savedMarksList = By.xpath(SAVED_MARKS + "//div[contains(@class,'marks-list')]");

    private By calculateGradeAndZScoreBtn = By.xpath(buttonContainingText(SAVED_MARKS, "Calculate Grade"));
    private By printScoreBtn = By.xpath(buttonWithText(SAVED_MARKS, "Print Score"));
    private By sendStatToStudentsBtn = By.xpath(buttonWithText(SAVED_MARKS, "Send Stat to Students"));

    // Exam Averages panel
    private By calculateAveragesBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Calculate Averages"));
    private By printAveragePdfBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Print Average PDF"));
    private By sendStatBySmsBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Send Stat by SMS"));

    // Student selection
    private By studentSelectionSearchInput = By.xpath(searchInput(STUDENT_SELECT_CARD));
    private By studentSelectionSearchBtn = By.xpath(buttonWithText(STUDENT_SELECT_CARD, "Search"));
    private By studentSelectionClearBtn = By.xpath(searchClearButton(STUDENT_SELECT_CARD));
    private By studentSelectVisibleBtn = By.xpath(buttonWithText(STUDENT_SELECT_CARD, "Select Visible"));
    private By studentSelectionNote = By.xpath(STUDENT_SELECT_CARD + "/p[contains(@class,'selection-note')]");
    private By studentSelectionRows = By.xpath(STUDENT_SELECT_CARD + "//article[contains(@class,'mark-row-card')]");

    // Exam selection
    private By examSelectionSearchInput = By.xpath(searchInput(EXAM_SELECT_CARD));
    private By examSelectionClearBtn = By.xpath(searchClearButton(EXAM_SELECT_CARD));
    private By examSelectVisibleBtn = By.xpath(buttonWithText(EXAM_SELECT_CARD, "Select Visible"));
    private By examSelectionNote = By.xpath(EXAM_SELECT_CARD + "/p[contains(@class,'selection-note')]");
    private By examSelectionRows = By.xpath(EXAM_SELECT_CARD + "//article[contains(@class,'mark-row-card')]");

    private String examSelectionRowXpath(String title) {
        return EXAM_SELECT_CARD + "//article[contains(@class,'mark-row-card')]"
                + "[.//strong[normalize-space()=" + xpathLiteral(title) + "]]";
    }

    public By examSelectionCheckbox(String title) {
        return By.xpath(examSelectionRowXpath(title) + "//input[@type='checkbox']");
    }

    public By examSelectionGradeCountChip(String title) {
        return By.xpath(examSelectionRowXpath(title) + "//span[contains(@class,'chip')]");
    }

    // Average results
    private static final String AVERAGE_RESULTS = AVERAGES_PANEL + "//div[contains(@class,'marks-list-block')]";
    private By averageResultsEmptyHint = By.xpath(AVERAGE_RESULTS + "/p[contains(@class,'hint')]");
    private By averageResultsList = By.xpath(AVERAGE_RESULTS + "//div[contains(@class,'marks-list')]");

    // Navigation & Actions
    private By classExaminationTab = By
            .xpath("//button[normalize-space()='Class Examination' or contains(text(),'Class Examination')]");

    // Constructor
    public ClassExaminationPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateToClassExaminationTab() {
        wait.until(ExpectedConditions.elementToBeClickable(classExaminationTab));
        click(classExaminationTab);
        waitForExamSnapshotVisible();
    }

    public void waitForExamSnapshotVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotHeading));
        wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput));
    }

    public String getExamSnapshotSearchPlaceholder() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput))
                .getAttribute("placeholder");
    }

    public String getExamSnapshotSearchValue() {
        return Objects.toString(
                wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput))
                        .getAttribute("value"),
                "");
    }

    public boolean isExamSnapshotClearButtonEnabled() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(examSnapshotClearBtn)).isEnabled();
    }

    public boolean isExamSnapshotClearButtonDisabled() {
        return !isExamSnapshotClearButtonEnabled();
    }

    // --- Search & Clear Methods (Maintained + Aliased) ---
    public void searchExamSnapshot(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput));

        // Ensure element has focus
        input.click();

        // Clear using OS-level keystrokes to ensure framework captures the change
        String selectAll = Keys.chord(Keys.CONTROL, "a");
        input.sendKeys(selectAll);
        input.sendKeys(Keys.BACK_SPACE);

        // Send the negative search term and wait for framework debounce naturally
        input.sendKeys(keyword);
    }

    public void enterExamSnapshotSearch(String searchText) {
        searchExamSnapshot(searchText);
    }

    public void clickExamSnapshotClear() {
        wait.until(ExpectedConditions.elementToBeClickable(examSnapshotClearBtn));
        click(examSnapshotClearBtn);
    }

    public void clearExamSnapshotSearch() {
        clickExamSnapshotClear();
    }

    // --- Count Methods (Maintained + Aliased) ---
    public int getVisibleExamCardsCount() {
        // Only count elements that are visually rendered
        return (int) driver.findElements(examCards).stream()
                .filter(WebElement::isDisplayed)
                .count();
    }

    public int getVisibleExamCardCount() {
        return getVisibleExamCardsCount();
    }

    public List<String> getVisibleExamCardTexts() {
        return driver.findElements(examCards).stream()
                .filter(WebElement::isDisplayed)
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    public boolean allVisibleExamCardsContain(String searchText) {
        String normalizedSearchText = normalize(searchText);
        return getVisibleExamCardTexts().stream()
                .allMatch(cardText -> normalize(cardText).contains(normalizedSearchText));
    }

    // --- Explicit Wait Assertion Helpers ---
    public void waitForAllVisibleExamCardsToContain(String searchText) {
        String normalizedSearchText = normalize(searchText);
        wait.until(driver -> {
            List<String> cardTexts = getVisibleExamCardTexts();
            return !cardTexts.isEmpty()
                    && cardTexts.stream().allMatch(cardText -> normalize(cardText).contains(normalizedSearchText));
        });
    }

    public void waitForVisibleExamCardCount(int expectedCount) {
        wait.until(driver -> getVisibleExamCardCount() == expectedCount);
    }

    public void waitForAtLeastVisibleExamCardCount(int minimumCount) {
        wait.until(driver -> getVisibleExamCardCount() >= minimumCount);
    }

    public void waitForNoVisibleExamCards() {
        wait.until(driver -> getVisibleExamCardCount() == 0);
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    public void selectExamType(String examType) {
        wait.until(ExpectedConditions.elementToBeClickable(examTypeSelect));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                examTypeSelect,
                By.xpath(".//option[normalize-space()='" + examType + "']")
        ));
        selectDropdown(examTypeSelect, examType);
    }

    public void enterAlYear(String year) {
        sendKeys(alYearInput, year);
    }

    public void enterExamNumber(String examNumber) {
        sendKeys(examNumberInput, examNumber);
    }

    public void selectExamLocation(String location) {
        wait.until(ExpectedConditions.elementToBeClickable(examLocationSelect));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                examLocationSelect,
                By.xpath(".//option[normalize-space()='" + location + "']")
        ));
        selectDropdown(examLocationSelect, location);
    }

    public void enterExamTitle(String title) {
        sendKeys(examTitleInput, title);
    }

    public void clickAddGrade() {
        click(addGradeBtn);
    }

    public void enterGradeDetails(int row, String grade, String max, String min) {
        sendKeys(gradeInput(row), grade);
        sendKeys(gradeMaxInput(row), max);
        sendKeys(gradeMinInput(row), min);
    }

    public void clickCreateExam() {
        click(createExamBtn);
    }

    public void selectAllVisibleStudents() {
        wait.until(ExpectedConditions.elementToBeClickable(studentSelectionSearchBtn));
        click(studentSelectionSearchBtn);

        try {
            // Wait for student rows to load
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(studentSelectionRows));

            WebElement selectBtn = shortWait.until(ExpectedConditions.elementToBeClickable(studentSelectVisibleBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();",
                    selectBtn);
        } catch (TimeoutException e) {
            // Gracefully ignore if no students exist in the DB instead of crashing
            System.out.println("Warning: No visible students populated after clicking search.");
        }
    }

    public void selectAllVisibleExams() {
        wait.until(ExpectedConditions.elementToBeClickable(examSelectVisibleBtn));
        click(examSelectVisibleBtn);
    }

    public void selectExamByTitle(String title) {
        By checkbox = examSelectionCheckbox(title);
        wait.until(ExpectedConditions.elementToBeClickable(checkbox));
        click(checkbox);
    }

    public void clickCalculateAverages() {
        wait.until(ExpectedConditions.elementToBeClickable(calculateAveragesBtn));
        click(calculateAveragesBtn);
    }

    public boolean isAverageResultsDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(averageResultsList)).isDisplayed();
    }

    public String getAverageResultsText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(averageResultsList)).getText();
    }

    public boolean isStudentSelectVisibleBtnDisplayed() {
        return driver.findElement(studentSelectVisibleBtn).isDisplayed();
    }

    public boolean isStudentSelectVisibleBtnEnabled() {
        return driver.findElement(studentSelectVisibleBtn).isEnabled();
    }

    public boolean isExamAveragesPanelDisplayed() {
        return driver.findElement(By.xpath(AVERAGES_PANEL)).isDisplayed();
    }

    public String getStudentSelectionCardTitle() {
        return driver.findElement(By.xpath(STUDENT_SELECT_CARD + "//p[contains(@class,'panel-kicker')]")).getText();
    }

    public String getCalculateAveragesBtnText() {
        return driver.findElement(calculateAveragesBtn).getText();
    }
    public void selectExamForBulkUpload(String examValue) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait until exam dropdown is available
        WebElement examDropdown = wait.until(
                driver -> driver.findElement(bulkUploadExamSelect)
        );

        Select select = new Select(examDropdown);

        // Wait until exam options are loaded
        wait.until(driver -> {
            Select s = new Select(driver.findElement(bulkUploadExamSelect));
            return s.getOptions().size() > 1;
        });

        // Select using the option VALUE
        select = new Select(driver.findElement(bulkUploadExamSelect));
        select.selectByValue(examValue);
    }

    public void enterStudentIdCriteria(String studentIds) {
        driver.findElement(studentIdCriteriaInput).clear();
        driver.findElement(studentIdCriteriaInput).sendKeys(studentIds);
    }

    public void uploadExcelFile(String filePath) {
        driver.findElement(xlsxFileInput).sendKeys(filePath);
    }

    public void clickUploadXlsx() {
        driver.findElement(uploadXlsxBtn).click();
    }

}