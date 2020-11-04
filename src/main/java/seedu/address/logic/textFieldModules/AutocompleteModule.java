package seedu.address.logic.textFieldModules;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.w3c.dom.Text;
import seedu.address.ui.CommandBox;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class AutocompleteModule {
    public static final String AC_MODE_STYLE_CLASS = "autocomplete-mode";
    private HashMap<String, Suggestions> suggestionsList;

    private boolean isAutocompleteMode = false;
    private boolean hasSetPrefix = false;
    private String modeType = "";
    private int commandPrefixPos;
    
    private TextField attachedTextField;

    private AutocompleteModule(TextField textField) {
        attachedTextField = textField;
        suggestionsList = new HashMap<>();

        disableFocusTraversal();
        setupAutocompleteTriggers();
        setupAutocompleteActionKeys();
        setupBlockOtherKeystrokesInAcMode();
        setupExitKeys();
    }

    public static AutocompleteModule attachTo(TextField tf) {
        return new AutocompleteModule(tf);
    }

    /**
     * Setups Autocompletion Generator for stipulated command prefix.
     */

    public void addSuggestions(String commandPrefix, Supplier<List<String>> suggestionsDataGenerator) {
        requireNonNull(commandPrefix);
        requireNonNull(suggestionsDataGenerator);

        Suggestions suggestions = new Suggestions(suggestionsDataGenerator);
        suggestionsList.put(commandPrefix, suggestions);
    }
    private void setupAutocompleteTriggers() {
        attachedTextField.caretPositionProperty().addListener((unused1, unused2, newPosition) -> {
            String userInput = attachedTextField.getText();
            int caretPos = newPosition.intValue();
            if (caretPos == 0) {
                toggleAutocompleteModeOff();
                hasSetPrefix = false;
                return;
            }

            for (String cmdP : suggestionsList.keySet().toArray(new String[]{})) {
                int prefixLength = cmdP.length();
                if (userInput.length() >= prefixLength && caretPos >= prefixLength) {
                    String substring = userInput.substring(caretPos - prefixLength);
                    if (!isAutocompleteMode && substring.equals(cmdP)) {
                        commandPrefixPos = caretPos;
                        toggleAutocompleteModeOn(cmdP);
                    }
                    if (isAutocompleteMode) {
                        if (caretPos < commandPrefixPos) {
                            toggleAutocompleteModeOff();
                            hasSetPrefix = false;
                        }
                    }
                }
            }
        });
    }
    private void setupAutocompleteActionKeys() {
        attachedTextField.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (isAutocompleteMode && event.getCode() == KeyCode.TAB) {
                Suggestions suggestions = suggestionsList.get(modeType);
                if (!hasSetPrefix) {
                    String prefix = attachedTextField.getText().substring(commandPrefixPos);
                    suggestions.setPrefix(prefix);
                    hasSetPrefix = true;
                }
                if (event.isShiftDown()) {
                    // Shift + TAB : Previous Suggestion
                    String prev = suggestions.prevSuggestion();
                    int endIndex = attachedTextField.caretPositionProperty().getValue();
                    attachedTextField.replaceText(commandPrefixPos, endIndex, prev);
                } else {
                    // TAB : Next Suggestion
                    String next = suggestions.nextSuggestion();
                    int endIndex = attachedTextField.caretPositionProperty().getValue();
                    attachedTextField.replaceText(commandPrefixPos, endIndex, next);
                }
                if (suggestions.isBackToPrefix()) {
                    hasSetPrefix = false;
                }
                event.consume();
            }
        });
    }
    private void setupBlockOtherKeystrokesInAcMode() {
        attachedTextField.addEventFilter(KeyEvent.ANY, (event) -> {
            if (hasSetPrefix
                    && event.getCode() != KeyCode.TAB
                    && event.getCode() != KeyCode.ENTER
            ) {
                event.consume();
            }
        });
    }
    private void setupExitKeys() {
        // Exit Autocomplete Mode when backspace / enter is pressed.
        attachedTextField.addEventFilter(KeyEvent.KEY_PRESSED, (event -> {
            if (isAutocompleteMode && (
                    event.getCode() == KeyCode.BACK_SPACE
                            || event.getCode() == KeyCode.ENTER)) {
                hasSetPrefix = false;
                if (event.getCode() == KeyCode.BACK_SPACE) {
                    return;
                }
                toggleAutocompleteModeOff();
                removePrefixFromCompletion();
                event.consume();
            }
        }));
    }
    private void disableFocusTraversal() {
        attachedTextField.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (!isAutocompleteMode) {
                if (event.getCode() == KeyCode.TAB) {
                    event.consume();
                }
                return;
            }
        });
    }
    private void toggleAutocompleteModeOn(String commandPrefix) {
        if (isAutocompleteMode) {
            return;
        }
        setStyleToIndicateAutocompleteMode();
        isAutocompleteMode = true;
        modeType = commandPrefix;
    }
    private void toggleAutocompleteModeOff() {
        if (!isAutocompleteMode) {
            return;
        }
        isAutocompleteMode = false;
        attachedTextField.getStyleClass().remove(AC_MODE_STYLE_CLASS);
    }
    private void removePrefixFromCompletion() {
        String currentText = attachedTextField.getText();
        String result = currentText.substring(0 , commandPrefixPos - modeType.length())
                + currentText.substring(commandPrefixPos);
        int caretPosition = attachedTextField.getCaretPosition();
        attachedTextField.setText(result);
        attachedTextField.positionCaret(caretPosition - modeType.length());
    }

    public HashMap<String, Suggestions> getSuggestionsList() {
        return suggestionsList;
    }

    /**
     * Sets the command box style to indicate a Autocomplete Mode.
     */
    private void setStyleToIndicateAutocompleteMode() {
        ObservableList<String> styleClass = attachedTextField.getStyleClass();

        if (styleClass.contains(AC_MODE_STYLE_CLASS)) {
            return;
        }

        styleClass.add(AC_MODE_STYLE_CLASS);
    }

    /**
     * Class that generates suggestions based on given prefix and given list.
     */
    public class Suggestions {
        private List<String> suggestions;
        private String prefix = "";
        private int index = 0;
        private final Supplier<List<String>> listSupplier;

        public Suggestions(Supplier<List<String>> listSupplier) {
            this.listSupplier = listSupplier;
            this.suggestions = listSupplier.get().stream().sorted().collect(Collectors.toList());
        }

        private void nextIndex() {
            index = index + 1 < suggestions.size() ? index + 1 : 0;
        }

        private void prevIndex() {
            index = index - 1 < 0 ? suggestions.size() - 1 : index - 1;
        }

        public String nextSuggestion() {
            if (!hasSuggestion()) {
                return prefix;
            }
            nextIndex();
            return suggestions.get(index);
        }

        public String prevSuggestion() {
            if (!hasSuggestion()) {
                return prefix;
            }
            prevIndex();
            return suggestions.get(index);
        }

        public boolean hasSuggestion() {
            return suggestions.size() != 0;
        }

        public void setPrefix(String prefix) {
            this.index = 0;
            this.prefix = prefix;
            suggestions = listSupplier.get().stream().filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                    .collect(Collectors.toList());
            suggestions.sort((o1, o2) -> o1.compareTo(o2));
            suggestions.add(0, prefix);
        }

        public boolean isBackToPrefix() {
            return index == 0;
        }

        public List<String> getFilteredList() {
            return suggestions;
        }

        public Supplier<List<String>> getListSupplier() {
            return listSupplier;
        }
    }
}
