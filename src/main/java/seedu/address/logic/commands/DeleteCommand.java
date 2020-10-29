package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonPredicate;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "contact delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the persons who have the specified names (case-insensitive), modules or tags.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "FULL_NAME] "
            + "[" + PREFIX_MODULE + "MODULE] "
            + "[" + PREFIX_TAG + "TAG]\n"
            + "Example: " + COMMAND_WORD + " n/Roy Chan t/friend";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted People: %1$s";

    private final PersonPredicate predicate;
    private final List<ModuleName> moduleNames;

    /**
     * @param predicate the predicate based on the names, modules and tags given
     */
    public DeleteCommand(PersonPredicate predicate, List<ModuleName> moduleNames) {
        this.predicate = predicate;
        this.moduleNames = moduleNames;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> people;
        if (moduleNames.isEmpty()) {
            // finds Persons that match the predicate only
            people = new ArrayList<>(model.getUpdatedFilteredPersonList(predicate));
        } else {
            // finds Persons that match the predicate and Persons that have the given Modules
            people = new ArrayList<>(model.getUpdatedFilteredPersonList(predicate, moduleNames));
        }
        if (people.isEmpty()) {
            throw new CommandException("No contact has the given arguments.");
        }

        List<Person> peopleCopy = new ArrayList<>(people);

        // Update address book

        people.stream().forEach(p -> {
            model.deletePerson(p); // delete in AddressBook
            model.updatePersonInMeetingBook(p); // delete in MeetingBook
            model.updatePersonInModuleBook(p); // delete in ModuleBook
        });

        String deletedNames = peopleCopy.stream()
                .map(p -> p.getName().toString())
                .reduce("", (x, y) -> x + y + ", ");
        deletedNames = deletedNames.substring(0, deletedNames.length() - 2);

        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, deletedNames), false, false,
                true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && predicate.equals(((DeleteCommand) other).predicate)
                && moduleNames.equals(((DeleteCommand) other).moduleNames)); // state check
    }
}
