package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;

public class DeleteModuleCommand extends Command {

    public static final String COMMAND_WORD = "module delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the module identified by the module name\n"
            + "Parameters: "
            + PREFIX_MODULE + "MODULE "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_MODULE + "CS2103 ";

    public static final String MESSAGE_DELETE_MODULE_SUCCESS = "Deleted Module: %1$s";

    private final ModuleName targetModuleName;

    /**
     * @param targetModuleName name of the module that is to be deleted.
     */
    public DeleteModuleCommand(ModuleName targetModuleName) {
        this.targetModuleName = targetModuleName;
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
        assert filteredMeetingList.size() == 1;
        Module module = filteredMeetingList.get(0);

        assert module != null;

        model.deleteModule(module);
        model.updateModuleInMeetingBook(module);
        return new CommandResult(String.format(MESSAGE_DELETE_MODULE_SUCCESS, module), false, false,
                true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteModuleCommand // instanceof handles nulls
                && targetModuleName.equals(((DeleteModuleCommand) other).targetModuleName)); // state check
    }
}
