package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGENDA;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPANT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_MEETINGS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.Messages;
import seedu.address.commons.util.CollectionUtil;
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

public class EditMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meeting edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the meeting identified "
            + "by the name of the meeting in the displayed meeting list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: "
            + PREFIX_MODULE + "MODULE "
            + PREFIX_NAME + "MEETING NAME "
            + "[" + PREFIX_NEWNAME + "NEW_MEETINGNAME] "
            + "[" + PREFIX_DATE + "NEW_DATE] "
            + "[" + PREFIX_TIME + "NEW_TIME] "
            + "[" + PREFIX_PARTICIPANT + "NEW_MEMBERS]... "
            + "[" + PREFIX_AGENDA + "NEW_AGENDA]... "
            + "[" + PREFIX_NOTE + "NEW_NOTE]...\n"
            + "Example: " + COMMAND_WORD + " m/CS2103 n/Project Meeting "
            + PREFIX_DATE + "2020-10-10 "
            + PREFIX_TIME + "11:30"
            + PREFIX_AGENDA + "Discuss project direction"
            + PREFIX_NOTE + "Alex will be coming late";

    public static final String MESSAGE_EDIT_MEETING_SUCCESS = "Edited Meeting: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MEETING = "This meeting already exists in the address book.";
    public static final String MESSAGE_NONEXISTENT_PERSON = "The following person(s): %s are not in your contacts";

    private final ModuleName targetModuleName;
    private final MeetingName targetMeetingName;
    private final EditMeetingCommand.EditMeetingDescriptor editMeetingDescriptor;

    /**
     * @param targetModuleName name of the module that the soon to be edited meeting belongs to
     * @param targetMeetingName name of the soon to be edited meeting
     * @param editMeetingDescriptor details to edit the meeting with
     */
    public EditMeetingCommand(ModuleName targetModuleName, MeetingName targetMeetingName,
                              EditMeetingCommand.EditMeetingDescriptor editMeetingDescriptor) {
        requireNonNull(targetModuleName);
        requireNonNull(targetMeetingName);
        requireNonNull(editMeetingDescriptor);

        this.targetModuleName = targetModuleName;
        this.targetMeetingName = targetMeetingName;
        this.editMeetingDescriptor = new EditMeetingCommand.EditMeetingDescriptor(editMeetingDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        boolean isValidModule = model.hasModuleName(targetModuleName);
        if (!isValidModule) {
            throw new CommandException(Messages.MESSAGE_INVALID_MODULE_DISPLAYED);
        }

        List<Module> filteredMeetingList = model.getFilteredModuleList().stream()
                .filter(module -> module.isSameName(targetModuleName)).collect(Collectors.toList());
        Module module = filteredMeetingList.get(0);

        boolean isValidMeeting = false;
        Meeting meetingToEdit = null;
        for (Meeting meeting : model.getFilteredMeetingList()) {
            if (meeting.getModule().equals(module) && meeting.getMeetingName().equals(targetMeetingName)) {
                isValidMeeting = true;
                meetingToEdit = meeting;
            }
        }
        if (!isValidMeeting) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEETING_DISPLAYED);
        }

        assert meetingToEdit != null;

        Meeting editedMeeting = createEditedMeeting(meetingToEdit, editMeetingDescriptor, model);

        if (!meetingToEdit.isSameMeeting(editedMeeting) && model.hasMeeting(editedMeeting)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }

        model.setMeeting(meetingToEdit, editedMeeting);
        model.updateFilteredMeetingList(PREDICATE_SHOW_ALL_MEETINGS);
        return new CommandResult(String.format(MESSAGE_EDIT_MEETING_SUCCESS, editedMeeting), false,
                false, true);
    }

    /**
     * Creates and returns a {@code Meeting} with the details of {@code meetingToEdit}
     * edited with {@code editMeetingDescriptor}.
     */
    private static Meeting createEditedMeeting(Meeting meetingToEdit,
                                               EditMeetingCommand.EditMeetingDescriptor editMeetingDescriptor,
                                               Model model) throws CommandException {
        assert meetingToEdit != null;

        MeetingName updatedMeetingName = editMeetingDescriptor.getMeetingName().orElse(meetingToEdit.getMeetingName());
        Date updatedDate = editMeetingDescriptor.getDate().orElse(meetingToEdit.getDate());
        Time updatedTime = editMeetingDescriptor.getTime().orElse(meetingToEdit.getTime());
        Set<Name> updatedMemberNames = editMeetingDescriptor.getMemberNames().orElse(null);
        Set<SpecialName> updatedAgendas = editMeetingDescriptor.getAgendas().orElse(meetingToEdit.getAgendas());
        Set<SpecialName> updatedNotes = editMeetingDescriptor.getNotes().orElse(meetingToEdit.getNotes());
        Set<Person> updatedMembers = getUpdatedMembers(meetingToEdit, updatedMemberNames, model);

        return new Meeting(meetingToEdit.getModule(), updatedMeetingName, updatedDate, updatedTime, updatedMembers,
                updatedAgendas, updatedNotes);
    }

    private static Set<Person> getUpdatedMembers(Meeting meetingToEdit,
                                                 Set<Name> updatedMemberNames,
                                                 Model model) throws CommandException {
        Set<Person> updatedMembers = new HashSet<>();

        if (updatedMemberNames != null) {
            List<Name> nonExistentPersonNames = new ArrayList<>();
            for (Name name : updatedMemberNames) {
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

            for (Name name : updatedMemberNames) {
                List<Person> filteredList = model.getFilteredPersonList().stream()
                        .filter(person -> person.isSameName(name)).collect(Collectors.toList());
                updatedMembers.addAll(filteredList);
            }
        } else {
            updatedMembers = meetingToEdit.getParticipants();
        }
        assert updatedMembers != null;
        return updatedMembers;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditMeetingCommand)) {
            return false;
        }

        // state check
        EditMeetingCommand e = (EditMeetingCommand) other;
        return targetModuleName.equals(e.targetModuleName)
                && targetMeetingName.equals(e.targetMeetingName)
                && editMeetingDescriptor.equals(e.editMeetingDescriptor);
    }

    /**
     * Stores the details to edit the meeting with. Each non-empty field value will replace the
     * corresponding field value of the meeting.
     */
    public static class EditMeetingDescriptor {
        private MeetingName meetingName;
        private Date date;
        private Time time;
        private Set<Name> memberNames;
        private Set<SpecialName> agendas;
        private Set<SpecialName> notes;

        public EditMeetingDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditMeetingDescriptor(EditMeetingCommand.EditMeetingDescriptor toCopy) {
            setMeetingName(toCopy.meetingName);
            setDate(toCopy.date);
            setTime(toCopy.time);
            setMemberNames(toCopy.memberNames);
            setAgendas(toCopy.agendas);
            setNotes(toCopy.notes);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(meetingName, date, time, memberNames, agendas, notes);
        }

        public void setMeetingName(MeetingName meetingName) {
            this.meetingName = meetingName;
        }

        public Optional<MeetingName> getMeetingName() {
            return Optional.ofNullable(meetingName);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Optional<Date> getDate() {
            return Optional.ofNullable(date);
        }

        public void setTime(Time time) {
            this.time = time;
        }

        public Optional<Time> getTime() {
            return Optional.ofNullable(time);
        }

        /**
         * Sets {@code members} to this object's {@code members}.
         * A defensive copy of {@code members} is used internally.
         */
        public void setMemberNames(Set<Name> memberNames) {
            this.memberNames = (memberNames != null) ? new HashSet<>(memberNames) : null;
        }

        /**
         * Sets {@code agendas} to this object's {@code agendas}.
         * A defensive copy of {@code agendas} is used internally.
         */
        public void setAgendas(Set<SpecialName> agendas) {
            this.agendas = (agendas != null) ? new HashSet<>(agendas) : null;
        }

        /**
         * Sets {@code notes} to this object's {@code notes}.
         * A defensive copy of {@code notes} is used internally.
         */
        public void setNotes(Set<SpecialName> notes) {
            this.notes = (notes != null) ? new HashSet<>(notes) : null;
        }

        /**
         * Returns an unmodifiable person set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Name>> getMemberNames() {
            return (memberNames != null) ? Optional.of(Collections.unmodifiableSet(memberNames)) : Optional.empty();
        }

        /**
         * Returns an unmodifiable agenda set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<SpecialName>> getAgendas() {
            return (agendas != null) ? Optional.of(Collections.unmodifiableSet(agendas)) : Optional.empty();
        }

        /**
         * Returns an unmodifiable note set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<SpecialName>> getNotes() {
            return (notes != null) ? Optional.of(Collections.unmodifiableSet(notes)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditMeetingCommand.EditMeetingDescriptor)) {
                return false;
            }

            // state check
            EditMeetingCommand.EditMeetingDescriptor e = (EditMeetingCommand.EditMeetingDescriptor) other;

            return getMeetingName().equals(e.getMeetingName())
                    && getDate().equals(e.getDate())
                    && getTime().equals(e.getTime())
                    && getMemberNames().equals(e.getMemberNames());
        }
    }
}
