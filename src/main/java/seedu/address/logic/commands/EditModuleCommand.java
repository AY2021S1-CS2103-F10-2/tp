package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARTICIPANT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_MEETINGS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_MODULES;

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
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

public class EditModuleCommand extends Command {

    public static final String COMMAND_WORD = "module edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the module identified "
            + "by the name of the module in the displayed module list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: "
            + PREFIX_MODULE + "MODULE "
            + "[" + PREFIX_NEWNAME + "NEW_MODULENAME] "
            + "[" + PREFIX_PARTICIPANT + "NEW_MEMBERS]...\n"
            + "Example: " + COMMAND_WORD + " CS2103 n/CS2103T ";

    public static final String MESSAGE_EDIT_MODULE_SUCCESS = "Edited Module: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the address book.";
    public static final String MESSAGE_NONEXISTENT_PERSON = "The following person(s): %s are not in your contacts";

    private final ModuleName targetModuleName;
    private final EditModuleCommand.EditModuleDescriptor editModuleDescriptor;

    /**
     * @param targetModuleName name of the module that the soon to be edited meeting belongs to
     * @param targetModuleName name of the soon to be edited meeting
     * @param editModuleDescriptor details to edit the meeting with
     */
    public EditModuleCommand(ModuleName targetModuleName,
                             EditModuleCommand.EditModuleDescriptor editModuleDescriptor) {
        requireNonNull(targetModuleName);
        requireNonNull(editModuleDescriptor);

        this.targetModuleName = targetModuleName;
        this.editModuleDescriptor = new EditModuleCommand.EditModuleDescriptor(editModuleDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        boolean isValidModule = model.hasModuleName(targetModuleName);
        if (!isValidModule) {
            throw new CommandException(Messages.MESSAGE_INVALID_MODULE_DISPLAYED);
        }

        List<Module> filteredModuleList = model.getFilteredModuleList().stream()
                .filter(module -> module.isSameName(targetModuleName)).collect(Collectors.toList());
        Module moduleToEdit = filteredModuleList.get(0);

        assert moduleToEdit != null;

        Module editedModule = createEditedModule(moduleToEdit, editModuleDescriptor, model);

        if (moduleToEdit.isSameModule(editedModule)) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }
        model.setModule(moduleToEdit, editedModule);
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        model.updateModuleInMeetingBook(moduleToEdit, editedModule);
        model.updateFilteredMeetingList(PREDICATE_SHOW_ALL_MEETINGS);
        return new CommandResult(String.format(MESSAGE_EDIT_MODULE_SUCCESS, editedModule), false, false,
                true);
    }

    /**
     * Creates and returns a {@code Module} with the details of {@code moduleToEdit}
     * edited with {@code editModuleDescriptor}.
     */
    private static Module createEditedModule(Module moduleToEdit,
                                               EditModuleCommand.EditModuleDescriptor editModuleDescriptor,
                                               Model model) throws CommandException {
        assert moduleToEdit != null;

        ModuleName updatedModuleName = editModuleDescriptor.getModuleName().orElse(moduleToEdit.getModuleName());
        Set<Name> updatedMemberNames = editModuleDescriptor.getMemberNames().orElse(null);
        Set<Person> updatedMembers = getUpdatedMembers(moduleToEdit, updatedMemberNames, model);

        return new Module(updatedModuleName, updatedMembers);
    }

    private static Set<Person> getUpdatedMembers(Module moduleToEdit,
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
            updatedMembers = moduleToEdit.getClassmates();
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
        if (!(other instanceof EditModuleCommand)) {
            return false;
        }

        // state check
        EditModuleCommand e = (EditModuleCommand) other;
        return targetModuleName.equals(e.targetModuleName)
                && editModuleDescriptor.equals(e.editModuleDescriptor);
    }

    /**
     * Stores the details to edit the module with. Each non-empty field value will replace the
     * corresponding field value of the module.
     */
    public static class EditModuleDescriptor {
        private ModuleName moduleName;
        private Set<Name> memberNames;

        public EditModuleDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditModuleDescriptor(EditModuleCommand.EditModuleDescriptor toCopy) {
            setModuleName(toCopy.moduleName);
            setMemberNames(toCopy.memberNames);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(moduleName, memberNames);
        }

        public void setModuleName(ModuleName moduleName) {
            this.moduleName = moduleName;
        }

        public Optional<ModuleName> getModuleName() {
            return Optional.ofNullable(moduleName);
        }

        /**
         * Sets {@code members} to this object's {@code members}.
         * A defensive copy of {@code members} is used internally.
         */
        public void setMemberNames(Set<Name> memberNames) {
            this.memberNames = (memberNames != null) ? new HashSet<>(memberNames) : null;
        }

        /**
         * Returns an unmodifiable person set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Name>> getMemberNames() {
            return (memberNames != null) ? Optional.of(Collections.unmodifiableSet(memberNames)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditModuleCommand.EditModuleDescriptor)) {
                return false;
            }

            // state check
            EditModuleCommand.EditModuleDescriptor e = (EditModuleCommand.EditModuleDescriptor) other;

            return getModuleName().equals(e.getModuleName())
                    && getMemberNames().equals(e.getMemberNames());
        }
    }
}

