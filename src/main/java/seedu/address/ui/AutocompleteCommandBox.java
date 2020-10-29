package seedu.address.ui;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class AutocompleteCommandBox extends CommandBox {

    public static final String AC_MODE_STYLE_CLASS = "autocomplete-mode";

    private boolean isAutocompleteMode = false;
    private boolean hasSetPrefix = false;
    private String modeType = "";
    private int autoCompletePos;
    private int commandPrefixPos;

    /**
     * Creates a {@code AutocompleteCommandBox} with the given {@code CommandExecutor}.
     */
    public AutocompleteCommandBox(CommandExecutor commandExecutor) {
        super(commandExecutor);
        disableFocusTraversal();
        setupBlockOtherKeystrokesInAcMode();
        setupExitKeys();
    }

    /**
     * Setups Autocompletion Generator for stipulated command prefix.
     */
    public void setupAutocompletionListeners(String commandPrefix, Supplier<List<String>> suggestionsDataGenerator) {
        setupAutocompleteTriggers(commandPrefix);
        setupAutocompleteActionKeys(commandPrefix, suggestionsDataGenerator);
    }
    private void setupAutocompleteTriggers(String commandPrefix) {
        this.getCommandTextField().caretPositionProperty().addListener((unused1, unused2, newPosition) -> {
            String userInput = this.getCommandTextField().getText();
            int caretPos = newPosition.intValue();
            int prefixLength = commandPrefix.length();
            if (caretPos == 0) {
                toggleAutocompleteModeOff();
                hasSetPrefix = false;
                return;
            }

            if (userInput.length() >= prefixLength && caretPos >= prefixLength - 1) {
                String substring = userInput.substring(caretPos - prefixLength);
                if (!isAutocompleteMode && substring.equals(commandPrefix)) {
                    commandPrefixPos = caretPos;
                    toggleAutocompleteModeOn(commandPrefix);
                    autoCompletePos = newPosition.intValue();
                }
                if (isAutocompleteMode) {
                    if (caretPos < autoCompletePos) {
                        toggleAutocompleteModeOff();
                        hasSetPrefix = false;
                    }
                }
            }
        });
    }
    private void setupAutocompleteActionKeys(String commandPrefix, Supplier<List<String>> suggestionsDataRetriever) {
        Suggestions suggestions = new Suggestions();
        this.getCommandTextField().addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (isAutocompleteMode
                    && modeType.equals(commandPrefix)
                    && event.getCode() == KeyCode.TAB) {
                if (!hasSetPrefix) {
                    suggestions.setList(suggestionsDataRetriever.get());
                    String prefix = this.getCommandTextField().getText().substring(autoCompletePos);
                    suggestions.setPrefix(prefix);
                    hasSetPrefix = true;
                }
                if (event.isShiftDown()) {
                    // Shift + TAB : Previous Suggestion
                    String prev = suggestions.prevSuggestion();
                    int endIndex = this.getCommandTextField().caretPositionProperty().getValue();
                    this.getCommandTextField().replaceText(autoCompletePos, endIndex, prev);
                } else {
                    // TAB : Next Suggestion
                    String next = suggestions.nextSuggestion();
                    int endIndex = this.getCommandTextField().caretPositionProperty().getValue();
                    this.getCommandTextField().replaceText(autoCompletePos, endIndex, next);
                }
                if (suggestions.isBackToPrefix()) {
                    hasSetPrefix = false;
                }
                event.consume();
            }
        });
    }
    private void setupBlockOtherKeystrokesInAcMode() {
        this.getCommandTextField().addEventFilter(KeyEvent.ANY, (event) -> {
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
        this.getCommandTextField().addEventFilter(KeyEvent.KEY_PRESSED, (event -> {
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
        this.getCommandTextField().addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
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
        this.getCommandTextField().getStyleClass().remove(AC_MODE_STYLE_CLASS);
    }
    private void removePrefixFromCompletion() {
        String currentText = getCommandTextField().getText();
        String result = currentText.substring(0 , commandPrefixPos - modeType.length())
                + currentText.substring(commandPrefixPos);
        int caretPosition = getCommandTextField().getCaretPosition();
        getCommandTextField().setText(result);
        getCommandTextField().positionCaret(caretPosition - modeType.length());
    }

    /**
     * Sets the command box style to indicate a Autocomplete Mode.
     */
    private void setStyleToIndicateAutocompleteMode() {
        ObservableList<String> styleClass = this.getCommandTextField().getStyleClass();

        if (styleClass.contains(AC_MODE_STYLE_CLASS)) {
            return;
        }

        styleClass.add(AC_MODE_STYLE_CLASS);
    }

    /**
     * Class that generates suggestions based on given prefix and given list.
     */
    private class Suggestions {
        private List<String> fullList;
        private List<String> suggestions;
        private String prefix = "";
        private int index = 0;

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

        public void setList(List<String> fullList) {
            this.fullList = fullList;
            this.suggestions = fullList.stream().collect(Collectors.toList());
            this.suggestions.add(0, prefix);
        }

        public void setPrefix(String prefix) {
            this.index = 0;
            this.prefix = prefix;
            suggestions = fullList.stream().filter(x -> x.toLowerCase().startsWith(prefix.toLowerCase()))
                    .collect(Collectors.toList());
            suggestions.sort((o1, o2) -> o1.compareTo(o2));
            suggestions.add(0, prefix);
        }

        public boolean isBackToPrefix() {
            return index == 0;
        }
    }
}

