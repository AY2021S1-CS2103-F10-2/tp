package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;

public class DeleteMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meeting delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the meeting identified by the [MODULE] and name used in the displayed meeting list.\n"
            + "Parameters: "
            + PREFIX_MODULE + "MODULE "
            + PREFIX_NAME + "MEETING NAME "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_MODULE + "CS2103 "
            + PREFIX_NAME + "weekly meeting";

    public static final String MESSAGE_DELETE_MEETING_SUCCESS = "Deleted Meeting: %1$s";

    private final ModuleName targetModuleName;
    private final MeetingName targetMeetingName;

    /**
     * @param targetModuleName name of the module that the soon to be deleted meeting belongs to
     * @param targetMeetingName name of the soon to be deleted meeting
     */
    public DeleteMeetingCommand(ModuleName targetModuleName, MeetingName targetMeetingName) {
        this.targetModuleName = targetModuleName;
        this.targetMeetingName = targetMeetingName;
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
        Meeting meetingToDelete = null;
        for (Meeting meeting : model.getFilteredMeetingList()) {
            if (meeting.getModule().equals(module) && meeting.getMeetingName().equals(targetMeetingName)) {
                isValidMeeting = true;
                meetingToDelete = meeting;
            }
        }
        if (!isValidMeeting) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEETING_DISPLAYED);
        }

        assert meetingToDelete != null;

        model.deleteMeeting(meetingToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_MEETING_SUCCESS, meetingToDelete), false,
                false, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteMeetingCommand // instanceof handles nulls
                && targetMeetingName.equals(((DeleteMeetingCommand) other).targetMeetingName)); // state check
    }
}
