package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEMBERS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Date;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.Time;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public static final String MESSAGE_NONEXISTENT_PERSON = "The following person(s): %s are not in your contacts";

    private final MeetingName meetingName;
    private final Date date;
    private final Time time;
    private final Set<Name> nameList;

    /**
     * Creates an AddMeetingCommand to add the specified {@code Meeting}
     */
    public AddMeetingCommand(MeetingName meetingName, Date date, Time time, Set<Name> nameList) {
        requireNonNull(meetingName);
        requireNonNull(date);
        requireNonNull(time);
        requireNonNull(nameList);
        this.meetingName = meetingName;
        this.date = date;
        this.time = time;
        this.nameList = nameList;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasMeetingName(meetingName)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }

        List<Name> nonExistentPersonNames = new ArrayList<>();
        for (Name name : nameList) {
            if (!model.hasPersonName(name)) {
                nonExistentPersonNames.add(name);
            }
        }

        if (!nonExistentPersonNames.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Name name : nonExistentPersonNames) {
                sb.append(name + ", ");
            }
            String nonExistentPersonNamesString = sb.substring(0, sb.length() - 2);
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_PERSON, nonExistentPersonNamesString));
        }

        Set<Person> personList = new HashSet<>();
        for (Name name : nameList) {
            List<Person> filteredList = model.getFilteredPersonList().stream()
                    .filter(person -> person.isSameName(name)).collect(Collectors.toList());
            personList.addAll(filteredList);
        }

        Meeting toAdd = new Meeting(meetingName, date, time, personList);

        model.addMeeting(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddMeetingCommand // instanceof handles nulls
                && meetingName.equals(((AddMeetingCommand) other).meetingName)
                && date.equals(((AddMeetingCommand) other).date)
                && time.equals(((AddMeetingCommand) other).time)
                && nameList.equals(((AddMeetingCommand) other).nameList));
    }
}

