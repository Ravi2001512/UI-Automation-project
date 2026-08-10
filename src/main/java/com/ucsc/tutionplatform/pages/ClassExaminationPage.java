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

    // =========================================================================
    // 1. XPATH SCOPES & STRING TEMPLATES
    // =========================================================================

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
    private static final String GRADE_ROW = GRADES_BLOCK + "//div[contains(@class,'grade-row')][not(contains(@class,'grade-row-head'))]";
    private static final String SAVED_MARKS = MARKS_PANEL + "//div[contains(@class,'marks-list-block')]";
    private static final String AVERAGE_RESULTS = AVERAGES_PANEL + "//div[contains(@class,'marks-list-block')]";

    // Dynamic XPath Templates
    private static final String INDEXED_GRADE_ROW = "(" + GRADE_ROW + ")[%d]";
    private static final String GRADE_INPUT_XPATH = INDEXED_GRADE_ROW + "/input[@placeholder='Grade']";
    private static final String GRADE_MAX_INPUT_XPATH = INDEXED_GRADE_ROW + "/input[@placeholder='Max']";
    private static final String GRADE_MIN_INPUT_XPATH = INDEXED_GRADE_ROW + "/input[@placeholder='Min']";
    private static final String GRADE_ROW_BY_LETTER = GRADE_ROW + "[./input[@placeholder='Grade'][@value=%s]]";

    private static final String EXAM_CARD_BY_TITLE = SNAPSHOT_CARD + "//article[contains(@class,'exam-card-row')][.//strong[normalize-space()=%s]]";
    private static final String EXAM_CARD_SELECT_BTN = EXAM_CARD_BY_TITLE + "//button[contains(@class,'exam-select-btn')]";
    private static final String EXAM_CARD_SUMMARY_LINE = EXAM_CARD_BY_TITLE + "//button//p[1]";
    private static final String EXAM_CARD_GRADE_PILLS = EXAM_CARD_BY_TITLE + "//div[contains(@class,'exam-grade-pills')]/span";
    private static final String EXAM_CARD_SPECIFIC_PILL = EXAM_CARD_BY_TITLE + "//div[contains(@class,'exam-grade-pills')]/span[starts-with(normalize-space(), %s)]";

    private static final String EXAM_SELECTION_ROW_BY_TITLE = EXAM_SELECT_CARD + "//article[contains(@class,'mark-row-card')][.//strong[normalize-space()=%s]]";
    private static final String EXAM_SELECTION_CHECKBOX = EXAM_SELECTION_ROW_BY_TITLE + "//input[@type='checkbox']";
    private static final String EXAM_SELECTION_GRADE_CHIP = EXAM_SELECTION_ROW_BY_TITLE + "//span[contains(@class,'chip')]";

    // =========================================================================
    // 2. STATIC LOCATORS
    // =========================================================================

    // --- Create Examination Form ---
    private final By examTypeSelect = By.xpath(labeledSelect(EXAM_FORM, "Exam Type"));
    private final By alYearInput = By.xpath(labeledInput(EXAM_FORM, "A/L Year"));
    private final By examNumberInput = By.xpath(labeledInput(EXAM_FORM, "Exam Number"));
    private final By examLocationSelect = By.xpath(labeledSelect(EXAM_FORM, "Exam Location"));
    private final By examTitleInput = By.xpath(labeledInput(EXAM_FORM, "Exam Title"));
    private final By createExamBtn = By.xpath(EXAM_FORM_ACTIONS + "/button[@type='submit']");
    private final By clearExamFormBtn = By.xpath(buttonWithText(EXAM_FORM_ACTIONS, "Clear"));

    // --- Grade Bands ---
    private final By addGradeBtn = By.xpath(buttonWithText(GRADES_BLOCK, "Add Grade"));
    private final By gradeRows = By.xpath(GRADE_ROW);

    // --- Exam Snapshot ---
    private final By examSnapshotHeading = By.xpath(SNAPSHOT_CARD + "//h3[normalize-space()='Exam and Grade Snapshot']");
    private final By examSnapshotSearchInput = By.cssSelector(
            "article.user-list-card .search-input-row input[placeholder='Search by exam title, type, A/L year, exam number, location, or grade band']");
    private final By examSnapshotClearBtn = By.xpath(SNAPSHOT_CARD + "//div[contains(@class,'search-input-row')]/button[normalize-space()='Clear']");
    private final By examCards = By.cssSelector("article.user-list-card .exam-snapshot-list > article.exam-card-row");
    private final By selectedExamCard = By.xpath(SNAPSHOT_CARD + "//article[contains(@class,'exam-card-row')][contains(@class,'selected')]");

    // --- Bulk Upload ---
    private final By bulkUploadExamSelect = By.xpath(labeledSelect(BULK_BLOCK, "Select Exam for Bulk Upload"));
    private final By downloadTemplateBtn = By.xpath(buttonWithText(BULK_BLOCK, "Download Template"));
    private final By studentIdCriteriaInput = By.xpath(BULK_BLOCK + "//input[@placeholder='T2830 T2820']");
    private final By xlsxFileInput = By.xpath(BULK_BLOCK + "//input[@type='file']");
    private final By uploadXlsxBtn = By.xpath(buttonWithText(BULK_BLOCK, "Upload XLSX"));

    // --- Single Entry & Saved Marks ---
    private final By marksExamSelect = By.xpath(labeledSelect(MARKS_FORM, "Select Exam"));
    private final By markInput = By.xpath(labeledInput(MARKS_FORM, "Mark"));
    private final By studentSearchInput = By.xpath(labeledInput(MARKS_FORM, "Search Student"));
    private final By studentSearchClearBtn = By.xpath(searchClearButton(MARKS_FORM + "//label[contains(@class,'full-span')]"));
    private final By searchStudentBtn = By.xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'student-mark-search-row')]", "Search Student"));
    private final By saveMarkBtn = By.xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'form-actions')]", "Save Mark"));
    private final By clearMarkFormBtn = By.xpath(buttonWithText(MARKS_FORM + "//div[contains(@class,'form-actions')]", "Clear"));
    private final By savedMarksHeading = By.xpath(SAVED_MARKS + "//h3");
    private final By savedMarksEmptyHint = By.xpath(SAVED_MARKS + "/p[contains(@class,'hint')]");
    private final By savedMarksList = By.xpath(SAVED_MARKS + "//div[contains(@class,'marks-list')]");
    private final By calculateGradeAndZScoreBtn = By.xpath(buttonContainingText(SAVED_MARKS, "Calculate Grade"));
    private final By printScoreBtn = By.xpath(buttonWithText(SAVED_MARKS, "Print Score"));
    private final By sendStatToStudentsBtn = By.xpath(buttonWithText(SAVED_MARKS, "Send Stat to Students"));

    // --- Student & Exam Selection ---
    private final By studentSelectionSearchInput = By.xpath(searchInput(STUDENT_SELECT_CARD));
    private final By studentSelectionSearchBtn = By.xpath(buttonWithText(STUDENT_SELECT_CARD, "Search"));
    private final By studentSelectionClearBtn = By.xpath(searchClearButton(STUDENT_SELECT_CARD));
    private final By studentSelectVisibleBtn = By.xpath(buttonWithText(STUDENT_SELECT_CARD, "Select Visible"));
    private final By studentSelectionNote = By.xpath(STUDENT_SELECT_CARD + "/p[contains(@class,'selection-note')]");
    private final By studentSelectionRows = By.xpath(STUDENT_SELECT_CARD + "//article[contains(@class,'mark-row-card')]");

    private final By examSelectionSearchInput = By.xpath(searchInput(EXAM_SELECT_CARD));
    private final By examSelectionClearBtn = By.xpath(searchClearButton(EXAM_SELECT_CARD));
    private final By examSelectVisibleBtn = By.xpath(buttonWithText(EXAM_SELECT_CARD, "Select Visible"));
    private final By examSelectionNote = By.xpath(EXAM_SELECT_CARD + "/p[contains(@class,'selection-note')]");
    private final By examSelectionRows = By.xpath(EXAM_SELECT_CARD + "//article[contains(@class,'mark-row-card')]");

    // --- Averages ---
    private final By calculateAveragesBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Calculate Averages"));
    private final By printAveragePdfBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Print Average PDF"));
    private final By sendStatBySmsBtn = By.xpath(buttonWithText(AVERAGES_PANEL, "Send Stat by SMS"));
    private final By averageResultsEmptyHint = By.xpath(AVERAGE_RESULTS + "/p[contains(@class,'hint')]");
    private final By averageResultsList = By.xpath(AVERAGE_RESULTS + "//div[contains(@class,'marks-list')]");

    // --- Navigation ---
    private final By classExaminationTab = By.xpath("//button[normalize-space()='Class Examination' or contains(text(),'Class Examination')]");

    // =========================================================================
    // 3. CONSTRUCTOR
    // =========================================================================

    public ClassExaminationPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // =========================================================================
    // 4. PRIVATE DYNAMIC LOCATOR GENERATORS
    // =========================================================================

    private By getGradeInputLocator(int row) {
        return By.xpath(String.format(GRADE_INPUT_XPATH, row));
    }

    private By getGradeMaxInputLocator(int row) {
        return By.xpath(String.format(GRADE_MAX_INPUT_XPATH, row));
    }

    private By getGradeMinInputLocator(int row) {
        return By.xpath(String.format(GRADE_MIN_INPUT_XPATH, row));
    }

    private By getRemoveGradeBtnLocator(int row) {
        String rowXpath = String.format(INDEXED_GRADE_ROW, row);
        return By.xpath(buttonWithText(rowXpath, "Remove"));
    }

    private By getRemoveGradeBtnLocator(String gradeLetter) {
        String rowXpath = String.format(GRADE_ROW_BY_LETTER, xpathLiteral(gradeLetter));
        return By.xpath(buttonWithText(rowXpath, "Remove"));
    }

    private By getExamCardLocator(String title) {
        return By.xpath(String.format(EXAM_CARD_BY_TITLE, xpathLiteral(title)));
    }

    private By getSelectExamForMarksLocator(String title) {
        return By.xpath(String.format(EXAM_CARD_SELECT_BTN, xpathLiteral(title)));
    }

    private By getExamSummaryLineLocator(String title) {
        return By.xpath(String.format(EXAM_CARD_SUMMARY_LINE, xpathLiteral(title)));
    }

    private By getExamGradePillLocator(String title, String gradeLetter) {
        String literalGrade = xpathLiteral(gradeLetter + ":");
        return By.xpath(String.format(EXAM_CARD_SPECIFIC_PILL, xpathLiteral(title), literalGrade));
    }

    private By getExamSelectionCheckboxLocator(String title) {
        return By.xpath(String.format(EXAM_SELECTION_CHECKBOX, xpathLiteral(title)));
    }

    private By getExamSelectionGradeCountChipLocator(String title) {
        return By.xpath(String.format(EXAM_SELECTION_GRADE_CHIP, xpathLiteral(title)));
    }

    // =========================================================================
    // 5. NAVIGATION & INITIALIZATION
    // =========================================================================

    public void navigateToClassExaminationTab() {
        try {
            WebElement tab = wait.until(ExpectedConditions.presenceOfElementLocated(classExaminationTab));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", tab);
            wait.until(ExpectedConditions.elementToBeClickable(classExaminationTab));
            tab.click();
        } catch (Exception e) {
            WebElement tab = driver.findElement(classExaminationTab);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        }
        waitForExamSnapshotVisible();
    }
    public void waitForExamSnapshotVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotHeading));
        wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput));
    }

    // =========================================================================
    // 6. EXAM SNAPSHOT & SEARCH
    // =========================================================================

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

    public void searchExamSnapshot(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(examSnapshotSearchInput));
        input.click();
        String selectAll = Keys.chord(Keys.CONTROL, "a");
        input.sendKeys(selectAll);
        input.sendKeys(Keys.BACK_SPACE);
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

    // =========================================================================
    // 7. EXAM CARD ACTIONS & VERIFICATIONS
    // =========================================================================

    public boolean isExamCardDisplayed(String title) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(getExamCardLocator(title))).isDisplayed();
    }

    public void clickSelectExamForMarks(String title) {
        wait.until(ExpectedConditions.elementToBeClickable(getSelectExamForMarksLocator(title))).click();
    }

    public String getExamSummaryLineText(String title) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(getExamSummaryLineLocator(title))).getText();
    }

    public List<String> getExamGradePillsText(String title) {
        By pillsLocator = By.xpath(String.format(EXAM_CARD_GRADE_PILLS, xpathLiteral(title)));
        return wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(getExamCardLocator(title), pillsLocator))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean isExamGradePillDisplayed(String title, String gradeLetter) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                getExamGradePillLocator(title, gradeLetter))).isDisplayed();
    }

    public int getVisibleExamCardsCount() {
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

    // =========================================================================
    // 8. EXAM CREATION & GRADE MANAGEMENT
    // =========================================================================

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
        sendKeys(getGradeInputLocator(row), grade);
        sendKeys(getGradeMaxInputLocator(row), max);
        sendKeys(getGradeMinInputLocator(row), min);
    }

    public void removeGradeByRow(int row) {
        click(getRemoveGradeBtnLocator(row));
    }

    public void removeGradeByLetter(String gradeLetter) {
        click(getRemoveGradeBtnLocator(gradeLetter));
    }

    public void clickCreateExam() {
        click(createExamBtn);
    }

    // =========================================================================
    // 9. BULK MARKS UPLOAD
    // =========================================================================

    public void selectExamForBulkUpload(String examValue) {
        WebElement examDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(bulkUploadExamSelect));

        wait.until(driver -> {
            Select s = new Select(driver.findElement(bulkUploadExamSelect));
            return s.getOptions().size() > 1;
        });

        Select select = new Select(examDropdown);
        select.selectByValue(examValue);
    }

    public void enterStudentIdCriteria(String studentIds) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(studentIdCriteriaInput));
        input.clear();
        input.sendKeys(studentIds);
    }

    public void uploadExcelFile(String filePath) {
        wait.until(ExpectedConditions.presenceOfElementLocated(xlsxFileInput)).sendKeys(filePath);
    }

    public void clickUploadXlsx() {
        wait.until(ExpectedConditions.elementToBeClickable(uploadXlsxBtn)).click();
    }

    // =========================================================================
    // 10. STUDENT / EXAM SELECTION & AVERAGES
    // =========================================================================

    public void selectAllVisibleStudents() {
        wait.until(ExpectedConditions.elementToBeClickable(studentSelectionSearchBtn));
        click(studentSelectionSearchBtn);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(studentSelectionRows));
            WebElement selectBtn = wait.until(ExpectedConditions.elementToBeClickable(studentSelectVisibleBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", selectBtn);
        } catch (TimeoutException e) {
            System.out.println("Warning: No visible students populated after clicking search.");
        }
    }

    public void selectAllVisibleExams() {
        wait.until(ExpectedConditions.elementToBeClickable(examSelectVisibleBtn));
        click(examSelectVisibleBtn);
    }

    public void selectExamByTitle(String title) {
        By checkbox = getExamSelectionCheckboxLocator(title);
        wait.until(ExpectedConditions.elementToBeClickable(checkbox));
        click(checkbox);
    }

    public String getExamSelectionGradeCountChipText(String title) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                getExamSelectionGradeCountChipLocator(title))).getText();
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
        return wait.until(ExpectedConditions.visibilityOfElementLocated(studentSelectVisibleBtn)).isDisplayed();
    }

    public boolean isStudentSelectVisibleBtnEnabled() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(studentSelectVisibleBtn)).isEnabled();
    }

    public boolean isExamAveragesPanelDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AVERAGES_PANEL))).isDisplayed();
    }

    public String getStudentSelectionCardTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(STUDENT_SELECT_CARD + "//p[contains(@class,'panel-kicker')]"))).getText();
    }

    public String getCalculateAveragesBtnText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(calculateAveragesBtn)).getText();
    }

    // =========================================================================
    // 11. HELPER METHODS
    // =========================================================================

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }
}