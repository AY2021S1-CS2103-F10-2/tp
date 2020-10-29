package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonPredicate;

public class CopyCommand extends Command {
    public static final String COMMAND_WORD = "copy";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Copies the email/phone number of the persons "
            + "who have the specified names (case-insensitive), modules or tags.\n"
            + "Parameters: email/phone (depending on what is to be copied) "
            + "[" + PREFIX_NAME + "FULL_NAME] "
            + "[" + PREFIX_MODULE + "MODULE] "
            + "[" + PREFIX_TAG + "TAG]\n"
            + "Example: " + COMMAND_WORD + " "
            + "email "
            + PREFIX_TAG + "classmate";
    public static final String MESSAGE_EMAIL_COPIED_TO_CLIPBOARD = "Contact emails have been copied to your clipboard.";
    public static final String MESSAGE_PHONE_COPIED_TO_CLIPBOARD =
            "Contact phone numbers have been copied to your clipboard.";
    private final PersonPredicate predicate;
    private final boolean isEmail;
    private final List<ModuleName> moduleNames;

    /**
     * @param predicate the predicate based on the names, modules and tags given
     * @param isEmail boolean to check if email or phone number is to be copied
     */
    public CopyCommand(PersonPredicate predicate, boolean isEmail, List<ModuleName> moduleNames) {
        this.predicate = predicate;
        this.isEmail = isEmail;
        this.moduleNames = moduleNames;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> people;
        if (moduleNames.isEmpty()) {
            // finds Persons that match the predicate only
            people = model.getUpdatedFilteredPersonList(predicate);
        } else {
            // finds Persons that match the predicate and Persons that have the given Modules
            people = model.getUpdatedFilteredPersonList(predicate, moduleNames);
        }

        if (people.isEmpty()) {
            throw new CommandException("No contact has the given arguments.");
        }

        String results;
        if (isEmail) {
            // gets email addresses from Persons in people
            results = people.stream()
                    .map(p -> p.getEmail().toString())
                    .reduce("", (x, y) -> x + " " + y);
        } else {
            // gets phone numbers from Persons in people
            results = people.stream()
                    .map(p -> p.getPhone().toString())
                    .reduce("", (x, y) -> x + " " + y);
        }

        // copies results to clipboard
        StringSelection selection = new StringSelection(results);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        if (isEmail) {
            return new CommandResult(MESSAGE_EMAIL_COPIED_TO_CLIPBOARD);
        } else {
            return new CommandResult(MESSAGE_PHONE_COPIED_TO_CLIPBOARD);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof CopyCommand) {
            CopyCommand that = (CopyCommand) other;
            return predicate.equals(that.predicate)
                    && isEmail == that.isEmail
                    && moduleNames.equals(that.moduleNames);
        } else {
            return false;
        }
    }
}
