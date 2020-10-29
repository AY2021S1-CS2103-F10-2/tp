package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGENDA;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPANT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.commons.SpecialName;
import seedu.address.model.meeting.Date;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.Time;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Adds a meeting to the meeting book.
 */
public class AddMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meeting add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a meeting to the meeting book. "
            + "Parameters: "
            + PREFIX_MODULE + "MODULE "
            + PREFIX_NAME + "MEETING NAME "
            + PREFIX_DATE + "DATE "
            + PREFIX_TIME + "TIME "
            + "[" + PREFIX_PARTICIPANT + "PARTICIPANTS]... "
            + "[" + PREFIX_AGENDA + "AGENDAS]... "
            + "[" + PREFIX_NOTE + "NOTES]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_MODULE + "CS2103 "
            + PREFIX_NAME + "weekly meeting "
            + PREFIX_DATE + "2020-09-20 "
            + PREFIX_TIME + "10:00 "
            + PREFIX_PARTICIPANT + "Alex "
            + PREFIX_PARTICIPANT + "Roy "
            + PREFIX_AGENDA + "Discuss project direction "
            + PREFIX_NOTE + "Alex will be coming late ";

    public static final String MESSAGE_SUCCESS = "New meeting added: %1$s";
    public static final String MESSAGE_DUPLICATE_MEETING = "The meeting [%s] %s already exists in the meeting book";
    public static final String MESSAGE_NONEXISTENT_PERSON = "The following person(s): %s are not in the module %s";
    public static final String MESSAGE_NONEXISTENT_MODULE = "The given module is not in your module list";

    private final ModuleName moduleName;
    private final MeetingName meetingName;
    private final Date date;
    private final Time time;
    private final Set<Name> nameList;
    private final Set<SpecialName> agendaList;
    private final Set<SpecialName> noteList;


    /**
     * Creates an AddMeetingCommand to add a meeting with specified params
     */
    public AddMeetingCommand(ModuleName moduleName, MeetingName meetingName, Date date, Time time, Set<Name> nameList,
                             Set<SpecialName> agendaList, Set<SpecialName> noteList) {
        requireAllNonNull(moduleName, meetingName, date, time, nameList);
        this.moduleName = moduleName;
        this.meetingName = meetingName;
        this.date = date;
        this.time = time;
        this.nameList = nameList;
        this.agendaList = agendaList;
        this.noteList = noteList;
    }

    /**
     * Creates an AddMeetingCommand to add the specified {@code Meeting}
     * For Testing.
     */
    public AddMeetingCommand(Meeting toAdd) {
        requireAllNonNull(toAdd);
        this.moduleName = toAdd.getModule().getModuleName();
        this.meetingName = toAdd.getMeetingName();
        this.date = toAdd.getDate();
        this.time = toAdd.getTime();
        this.agendaList = toAdd.getAgendas();
        this.noteList = toAdd.getNotes();
        this.nameList = toAdd.getParticipants().stream().map(Person::getName).collect(Collectors.toSet());
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasModuleName(moduleName)) {
            throw new CommandException(MESSAGE_NONEXISTENT_MODULE);
        }

        List<Module> filteredModuleList = model.getFilteredModuleList().stream()
                .filter(module -> module.isSameName(moduleName)).collect(Collectors.toList());
        Module module = filteredModuleList.get(0);

        for (Meeting meeting : model.getFilteredMeetingList()) {
            if (meeting.getModule().equals(module) && meeting.getMeetingName().equals(meetingName)) {
                throw new CommandException(
                        String.format(MESSAGE_DUPLICATE_MEETING, module.getModuleName(), meetingName));
            }
        }

        List<Name> nonExistentPersonNames = new ArrayList<>();
        for (Name name : nameList) {
            if (!module.hasClassmate(name)) {
                nonExistentPersonNames.add(name);
            }
        }

        if (!nonExistentPersonNames.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Name name : nonExistentPersonNames) {
                sb.append(name + ", ");
            }
            String nonExistentPersonNamesString = sb.substring(0, sb.length() - 2);
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_PERSON, nonExistentPersonNamesString,
                    moduleName));
        }

        Set<Person> personSet = new HashSet<>();
        for (Name name : nameList) {
            List<Person> filteredList = model.getFilteredPersonList().stream()
                    .filter(person -> person.isSameName(name)).collect(Collectors.toList());
            personSet.addAll(filteredList);
        }

        Meeting toAdd = new Meeting(module, meetingName, date, time, personSet, agendaList, noteList);

        model.addMeeting(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddMeetingCommand // instanceof handles nulls
                && moduleName.equals(((AddMeetingCommand) other).moduleName)
                && meetingName.equals(((AddMeetingCommand) other).meetingName)
                && date.equals(((AddMeetingCommand) other).date)
                && time.equals(((AddMeetingCommand) other).time)
                && nameList.equals(((AddMeetingCommand) other).nameList));
    }
}

