package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

public class ClearLabelCommand extends Command {
    public static final String COMMAND_WORD = "label clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Clears all labels of the person specified.\n"
            + "Parameters: NAME (must be name of person existing in ModDuke) "
            + "Example: " + COMMAND_WORD + " "
            + "Roy";

    private final Name targetName;

    /**
     * @param targetName the specified person
     */
    public ClearLabelCommand(Name targetName) {
        requireNonNull(targetName);
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        // check if Person exists in address book
        boolean isValidContact = model.hasPersonName(targetName);
        if (!isValidContact) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED);
        }

        List<Person> filteredList = model.getFilteredPersonList().stream()
                .filter(person -> person.isSameName(targetName)).collect(Collectors.toList());
        Person personToClear = filteredList.get(0);
        Person clearedPerson = createClearedPerson(personToClear); // clears all labels from Person

        model.setPerson(personToClear, clearedPerson);

        // update meeting book
        model.updatePersonInMeetingBook(personToClear, clearedPerson);

        // update module book
        model.updatePersonInModuleBook(personToClear, clearedPerson);

        return new CommandResult(String.format("All labels of person '%s' have been cleared!", targetName.toString()));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToClear}
     * but with cleared Labels.
     */
    private static Person createClearedPerson(Person personToClear) {
        assert personToClear != null;
        return new Person(personToClear.getName(), personToClear.getPhone(), personToClear.getEmail(), new HashSet<>());
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClearLabelCommand)) {
            return false;
        }

        // state check
        ClearLabelCommand e = (ClearLabelCommand) other;
        return targetName.equals(e.targetName);
    }
}
