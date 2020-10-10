package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.person.Name;

/**
 * Adds a meeting to the meeting book.
 */
public class AddMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meeting add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a meeting to the meeting book. "
            + "Parameters: "
            + PREFIX_NAME + "MEETING NAME "
            + PREFIX_DATE + "DATE "
            + PREFIX_TIME + "TIME "
            + "[" + PREFIX_MEMBERS + "MEMBERS]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "CS2103 weekly meeting "
            + PREFIX_DATE + "2020-09-20 "
            + PREFIX_TIME + "10:00 "
            + PREFIX_MEMBERS + "Alex "
            + PREFIX_MEMBERS + "Roy";

    public static final String MESSAGE_SUCCESS = "New meeting added: %1$s";
    public static final String MESSAGE_DUPLICATE_MEETING = "This meeting already exists in the meeting book";
    public static final String MESSAGE_NONEXISTENT_PERSON = "This person:%s does not exist in the address book";

    private final Meeting toAdd;

    /**
     * Creates an AddMeetingCommand to add the specified {@code Meeting}
     */
    public AddMeetingCommand(Meeting meeting) {
        requireNonNull(meeting);
        toAdd = meeting;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasMeeting(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }

        for (Name name : toAdd.getMembers()) {
            if (!model.hasPersonName(name)) {
                throw new CommandException(String.format(MESSAGE_NONEXISTENT_PERSON, name));
            }
        }

        model.addMeeting(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddMeetingCommand // instanceof handles nulls
                && toAdd.equals(((AddMeetingCommand) other).toAdd));
    }
}

