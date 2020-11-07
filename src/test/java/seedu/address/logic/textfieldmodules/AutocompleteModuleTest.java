package seedu.address.logic.textfieldmodules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class AutocompleteModuleTest {

    private String sampleCmdPrefix = "test/";
    private List<String> sampleNameList = Arrays.asList("Alice", "Bravo", "Charlie");

    private TextField textField;

    @BeforeAll
    private static void setHeadless() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    @Start
    private void start(Stage stage) {
        textField = new TextField();
        stage.setScene(new Scene(new StackPane(textField)));
        stage.show();

        // Setup Module
        AutocompleteModule ac = AutocompleteModule.attachTo(textField);
        ac.addSuggestions(sampleCmdPrefix, () -> sampleNameList);
    }

    @Test
    public void textFieldWithAutocompleteModule_getFirstSuggestionWithEmptyPrefix_success(FxRobot robot) {
        // Simulate user trigger ac
        robot.clickOn(textField)
                .write(sampleCmdPrefix)
                .press(KeyCode.TAB)
                .release(KeyCode.TAB);

        String expected = sampleCmdPrefix + sampleNameList.get(0);
        String actual = textField.getText();

        assertEquals(expected, actual);
    }
    @Test
    public void textFieldWithAutocompleteModule_getFirstSuggestionWithNonEmptyPrefix_success(FxRobot robot) {
        String prefix = "br";
        String firstResultMatchingPrefix = sampleNameList.stream()
                .filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                .findFirst()
                .orElse("");

        // Simulate user trigger ac
        robot.clickOn(textField)
                .write(sampleCmdPrefix + prefix)
                .press(KeyCode.TAB)
                .release(KeyCode.TAB);

        String expected = sampleCmdPrefix + firstResultMatchingPrefix;
        String actual = textField.getText();

        assertEquals(expected, actual);
    }
    @Test
    public void textFieldWithAutocompleteModule_lockInFirstSuggestions_success(FxRobot robot) {
        String prefix = "br";
        String firstResultMatchingPrefix = sampleNameList.stream()
                .filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                .findFirst()
                .orElse("");

        // Simulate user trigger ac
        robot.clickOn(textField)
                .write(sampleCmdPrefix + prefix)
                .press(KeyCode.TAB)
                .release(KeyCode.TAB)
                .press(KeyCode.ENTER)
                .release(KeyCode.ENTER);

        String expected = firstResultMatchingPrefix;
        String actual = textField.getText();

        assertEquals(expected, actual);
    }
    @Test
    public void textFieldWithAutocompleteModule_getFirstSuggestionWithPrefixWithNoMatch_prefixReturned(FxRobot robot) {
        String prefix = "Ergot";
        String firstResultMatchingPrefix = sampleNameList.stream()
                .filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                .findFirst()
                .orElse("");

        // Simulate user trigger ac
        robot.clickOn(textField)
                .write(sampleCmdPrefix + prefix)
                .press(KeyCode.TAB)
                .release(KeyCode.TAB);

        String expected = sampleCmdPrefix + prefix;
        String actual = textField.getText();

        assertEquals(expected, actual);
    }
    @Test
    public void textFieldWithAutocompleteModule_iterateThroughAllSuggestionsForward_success(FxRobot robot) {
        String prefix = "br";

        // Simulate user trigger ac
        robot.clickOn(textField).write(sampleCmdPrefix + prefix);

        for (int i = 0; i < sampleNameList.size() + 1; i++) {
            robot.press(KeyCode.TAB);
            robot.release(KeyCode.TAB);
        }

        String expected = sampleCmdPrefix + prefix;
        String actual = textField.getText();

        assertEquals(expected, actual);
    }
    @Test
    public void textFieldWithAutocompleteModule_iterateThroughAllSuggestionsBackward_success(FxRobot robot) {
        String prefix = "br";
        String firstResultMatchingPrefix = sampleNameList.stream()
                .filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                .findFirst()
                .orElse("");

        // Simulate user trigger ac
        robot.clickOn(textField).write(sampleCmdPrefix + prefix);

        for (int i = 0; i < sampleNameList.size() + 1; i++) {
            robot.press(KeyCode.SHIFT, KeyCode.TAB);
            robot.release(KeyCode.SHIFT, KeyCode.TAB);
        }

        String expected = sampleCmdPrefix + prefix;
        String actual = textField.getText();

        assertEquals(expected, actual);
    }


}
