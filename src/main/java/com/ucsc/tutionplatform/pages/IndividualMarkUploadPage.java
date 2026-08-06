package com.ucsc.tutionplatform.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page object for:
 * Class Examination -> Exam Marks -> Individual Upload
 *
 * Flow:
 * 1. Select exam
 * 2. Search student
 * 3. Select student result
 * 4. Enter mark
 * 5. Save mark
 * 6. Verify updated Saved Marks row
 */
public class IndividualMarkUploadPage extends BasePage {

    private final WebDriverWait wait;

    private static final String STUDENT_SEARCH_PLACEHOLDER =
            "Search by student ID, username, name, mobile, or NIC";

    private static final String MARKS_FORM =
            "//input[@placeholder='"
                    + STUDENT_SEARCH_PLACEHOLDER
                    + "']/ancestor::form[1]";

    private static final String SAVED_MARKS =
            "//div[contains(@class,'marks-list-block')]";

    private final By studentSearchInput = By.cssSelector(
            "input[placeholder='Search by student ID, username, name, mobile, or NIC']"
    );

    private final By searchStudentButton = By.xpath(
            "//input[@placeholder='Search by student ID, username, name, mobile, or NIC']"
                    + "/ancestor::form[1]"
                    + "//button[normalize-space()='Search Student']"
    );

    private final By markInput = By.cssSelector(
            "input[type='number'][min='0'][max='100'][step='0.01']"
    );

    private final By saveMarkButton = By.xpath(
            "//input[@type='number' and @min='0' and @max='100']"
                    + "/ancestor::form[1]"
                    + "//button[normalize-space()='Save Mark']"
    );

    private final By individualMarkMessage = By.xpath(
            "//*[@role='alert' or "
                    + "contains(@class,'toast') or "
                    + "contains(@class,'message') or "
                    + "contains(@class,'error')]"
                    + "[normalize-space()]"
    );

    public IndividualMarkUploadPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
    }

    public void waitUntilDisplayed() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        studentSearchInput
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        markInput
                )
        );
    }

    public void selectExamByValue(String examValue) {

        By examDropdown = By.xpath(
                "//select[option[@value="
                        + xpathLiteral(examValue)
                        + "]]"
        );

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(
                        examDropdown
                )
        );

        Select select = new Select(dropdown);
        select.selectByValue(examValue);

        wait.until(currentDriver -> {
            WebElement currentDropdown =
                    currentDriver.findElement(examDropdown);

            String selectedValue =
                    new Select(currentDropdown)
                            .getFirstSelectedOption()
                            .getAttribute("value");

            return examValue.equals(selectedValue);
        });
    }

    public void selectExamByVisibleText(String examName) {

        By examDropdown = By.xpath(
                "//select[option[normalize-space()="
                        + xpathLiteral(examName)
                        + "]]"
        );

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(
                        examDropdown
                )
        );

        new Select(dropdown).selectByVisibleText(examName);
    }
    public void searchStudent(String studentId) {
        WebElement searchField = wait.until(
                ExpectedConditions.elementToBeClickable(
                        studentSearchInput
                )
        );

        searchField.click();
        searchField.sendKeys(
                Keys.chord(Keys.CONTROL, "a")
        );
        searchField.sendKeys(studentId);

        WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        searchStudentButton
                )
        );

        searchButton.click();

        wait.until(currentDriver ->
                !findStudentResultCards(studentId).isEmpty()
        );
    }

    public void selectStudent(String studentId) {
        WebElement studentCard = wait.until(
                currentDriver -> {
                    List<WebElement> studentCards =
                            findStudentResultCards(studentId);

                    for (WebElement card : studentCards) {
                        if (card.isDisplayed()
                                && card.isEnabled()) {
                            return card;
                        }
                    }

                    return null;
                }
        );

        scrollToElement(studentCard);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        studentCard
                )
        ).click();
    }

    public void enterMark(String mark) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(
                        markInput
                )
        );

        input.click();
        input.sendKeys(
                Keys.chord(Keys.CONTROL, "a")
        );
        input.sendKeys(mark);

        wait.until(currentDriver ->
                mark.equals(
                        currentDriver.findElement(markInput)
                                .getAttribute("value")
                )
        );
    }

    public void clickSaveMark() {
        WebElement saveButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        saveMarkButton
                )
        );

        scrollToElement(saveButton);
        saveButton.click();
    }

    public void saveIndividualMark(
            String examValue,
            String studentId,
            String mark
    ) {
        waitUntilDisplayed();
        selectExamByValue(examValue);
        searchStudent(studentId);
        selectStudent(studentId);
        enterMark(mark);
        clickSaveMark();
    }

    public boolean isSavedMarkDisplayedForStudent(
            String studentId
    ) {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            savedMarkRow(studentId)
                    )
            ).isDisplayed();

        } catch (Exception exception) {
            return false;
        }
    }

    public BigDecimal getSavedMarkValue(String studentId) {
        List<WebElement> rows =
                driver.findElements(savedMarkRow(studentId));

        for (WebElement row : rows) {
            if (!row.isDisplayed()) {
                continue;
            }

            BigDecimal mark =
                    extractMarkFromText(row.getText());

            if (mark != null) {
                return mark;
            }
        }

        throw new AssertionError(
                "Could not find the saved mark value for student: "
                        + studentId
        );
    }

    public String getVisibleMessage() {
        List<WebElement> messages =
                driver.findElements(
                        individualMarkMessage
                );

        for (WebElement message : messages) {
            if (message.isDisplayed()) {
                return message.getText().trim();
            }
        }

        return "";
    }

    private List<WebElement> findStudentResultCards(
            String studentId
    ) {
        By studentResultCard = By.xpath(
                MARKS_FORM
                        + "//*[self::article or self::div]"
                        + "[contains(@class,'mark-row-card') or "
                        + "contains(@class,'student-card') or "
                        + "contains(@class,'student-result') or "
                        + "contains(@class,'search-result')]"
                        + "[contains(normalize-space(.),"
                        + xpathLiteral(studentId)
                        + ")]"
        );

        List<WebElement> results =
                driver.findElements(studentResultCard);

        if (!results.isEmpty()) {
            return results;
        }

        By fallbackStudentResult = By.xpath(
                MARKS_FORM
                        + "//*[self::article or self::div or self::button]"
                        + "[contains(normalize-space(.),"
                        + xpathLiteral(studentId)
                        + ")]"
                        + "[not(ancestor::div[contains(@class,'marks-list-block')])]"
        );

        return driver.findElements(
                fallbackStudentResult
        );
    }

    private By savedMarkRow(String studentId) {
        return By.xpath(
                SAVED_MARKS
                        + "//*[self::article or self::div]"
                        + "[contains(@class,'mark-row-card') or "
                        + "contains(@class,'marks-row') or "
                        + "contains(@class,'card')]"
                        + "[contains(normalize-space(.),"
                        + xpathLiteral(studentId)
                        + ")]"
        );
    }

    private void scrollToElement(
            WebElement element
    ) {
        JavascriptExecutor javascriptExecutor =
                (JavascriptExecutor) driver;

        javascriptExecutor.executeScript(
                "arguments[0].scrollIntoView("
                        + "{block:'center', inline:'nearest'}"
                        + ");",
                element
        );
    }

    public BigDecimal waitForSavedMarkUpdate(
            String studentId,
            String expectedMark
    ) {
        BigDecimal expected = new BigDecimal(expectedMark);

        return wait.until(currentDriver -> {
            List<WebElement> rows =
                    currentDriver.findElements(savedMarkRow(studentId));

            for (WebElement row : rows) {
                if (!row.isDisplayed()) {
                    continue;
                }

                BigDecimal displayedMark =
                        extractMarkFromText(row.getText());

                if (displayedMark != null
                        && displayedMark.compareTo(expected) == 0) {
                    return displayedMark;
                }
            }

            return null;
        });
    }
    private BigDecimal extractMarkFromText(String rowText) {
        Matcher matcher = Pattern.compile(
                "(?i)\\bMark\\s*[:\\-]?\\s*(-?\\d+(?:\\.\\d+)?)"
        ).matcher(rowText);

        if (!matcher.find()) {
            return null;
        }

        return new BigDecimal(matcher.group(1));
    }
    public boolean isMarkInputValid() {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(markInput)
        );

        return Boolean.parseBoolean(
                input.getDomProperty("validity") == null
                        ? "false"
                        : String.valueOf(
                        ((JavascriptExecutor) driver).executeScript(
                                "return arguments[0].checkValidity();",
                                input
                        )
                )
        );
    }
    public String getMarkValidationMessage() {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(markInput)
        );

        return (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;",
                input
        );
    }
    public boolean isSpecificMarkDisplayed(
            String studentId,
            String expectedMark
    ) {
        BigDecimal expected = new BigDecimal(expectedMark);

        List<WebElement> rows =
                driver.findElements(savedMarkRow(studentId));

        for (WebElement row : rows) {
            if (!row.isDisplayed()) {
                continue;
            }

            BigDecimal displayedMark =
                    extractMarkFromText(row.getText());

            if (displayedMark != null
                    && displayedMark.compareTo(expected) == 0) {
                return true;
            }
        }

        return false;
    }

}