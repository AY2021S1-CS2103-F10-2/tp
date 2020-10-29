package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyMeetingBook;
import seedu.address.model.ReadOnlyModuleBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.testutil.MeetingBuilder;
import seedu.address.testutil.ModuleBuilder;


public class ViewMeetingCommandTest {

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        Set<Person> expectedMembers = new HashSet<>();
        expectedMembers.add(BOB);
        expectedMembers.add(ALICE);
        Module expectedModule = new ModuleBuilder().withName("CS2103").withMembers(expectedMembers).build();
        Meeting expectedMeeting = new MeetingBuilder().withName("CS2103 Meeting").withModule(expectedModule)
                .withDate("2020-10-03").withTime("10:00").withMembers(expectedMembers).build();

        // both null
        assertThrows(NullPointerException.class, () -> new ViewMeetingCommand(null,
                null));
        // meeting name null
        assertThrows(NullPointerException.class, () -> new ViewMeetingCommand(expectedModule.getModuleName(),
                null));
        // module name null
        assertThrows(NullPointerException.class, () -> new ViewMeetingCommand(null,
                expectedMeeting.getMeetingName()));
    }

    @Test
    public void execute_meetingAcceptedByModel_viewSuccessful() throws Exception {
        ViewMeetingCommandTest.ModelStubAcceptingMeetingToView modelStub =
                new ViewMeetingCommandTest.ModelStubAcceptingMeetingToView();
        Module validModule = new ModuleBuilder().withName("CS2103").build();
        Meeting validMeeting = new MeetingBuilder().withName("CS2103 Meeting").withModule(validModule)
                .withDate("2020-10-03").withTime("10:00").build();

        CommandResult commandResult =
                new ViewMeetingCommand(validModule.getModuleName(), validMeeting.getMeetingName()).execute(modelStub);

        assertEquals(String.format(ViewMeetingCommand.MESSAGE_VIEW_MEETING_SUCCESS, validMeeting),
                commandResult.getFeedbackToUser());
        assertEquals(validMeeting, modelStub.selectedMeeting);
    }

    @Test
    public void equals() {
        Set<Person> expectedMembers = new HashSet<>();
        expectedMembers.add(BOB);
        expectedMembers.add(ALICE);
        Module expectedModule = new ModuleBuilder().withName("CS2103").withMembers(expectedMembers).build();
        Meeting meetingOne = new MeetingBuilder().withName("CS2103 Meeting").withModule(expectedModule)
                .withDate("2020-10-03").withTime("10:00").withMembers(expectedMembers).build();
        Meeting meetingTwo = new MeetingBuilder().withName("MA1521 Meeting").withModule(expectedModule)
                .withDate("2020-11-12").withTime("12:00").withMembers(expectedMembers).build();

        ViewMeetingCommand viewMeetingOneCommand = new ViewMeetingCommand(expectedModule.getModuleName(),
                meetingOne.getMeetingName());

        ViewMeetingCommand viewMeetingTwoCommand = new ViewMeetingCommand(expectedModule.getModuleName(),
                meetingTwo.getMeetingName());

        // same object -> returns true
        assertTrue(viewMeetingOneCommand.equals(viewMeetingOneCommand));

        // same values -> returns true
        ViewMeetingCommand viewMeetingOneCopy = new ViewMeetingCommand(new ModuleName("CS2103"), new MeetingName(
                "CS2103 Meeting"));
        assertTrue(viewMeetingOneCopy.equals(viewMeetingOneCommand));

        // different types -> returns false
        assertFalse(viewMeetingOneCommand.equals(1));

        // null -> returns false
        assertFalse(viewMeetingOneCommand.equals(null));

        // different meeting -> returns false
        assertFalse(viewMeetingOneCommand.equals(viewMeetingTwoCommand));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPersonName(Name name) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getUpdatedFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getUpdatedFilteredPersonList(Predicate<Person> predicate,
                                                                   List<ModuleName> modules) throws CommandException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setMeetingBook(ReadOnlyMeetingBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyMeetingBook getMeetingBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addMeeting(Meeting meeting) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasMeeting(Meeting meeting) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Meeting getSelectedMeeting() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setMeeting(Meeting target, Meeting editedMeeting) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSelectedMeeting(Meeting target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updatePersonInMeetingBook(Person ...persons) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasMeetingName(MeetingName meetingName) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteMeeting(Meeting targetMeeting) {
            throw new AssertionError("This method should not be called.");
        }


        @Override
        public Path getMeetingBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setMeetingBookFilePath(Path meetingBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Meeting> getFilteredMeetingList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredMeetingList(Predicate<Meeting> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasModuleName(ModuleName moduleName) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyModuleBook getModuleBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setModuleBook(ReadOnlyModuleBook moduleBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getModuleBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setModuleBookFilePath(Path moduleBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Module> getFilteredModuleList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void getPersonsInModule(ModuleName moduleName) throws CommandException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updatePersonInModuleBook(Person ...persons) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredModuleList(Predicate<Module> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateModuleInMeetingBook(Module... modules) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setModule(Module target, Module editedModule) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteModule(Module target) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that always accept the meeting being viewed.
     */
    private class ModelStubAcceptingMeetingToView extends ViewMeetingCommandTest.ModelStub {
        private Module module = new ModuleBuilder().withName("CS2103").build();
        private Meeting selectedMeeting = new MeetingBuilder().withName("CS2103 Meeting").withModule(module)
                .withDate("2020-10-03").withTime("10:00").build();

        @Override
        public boolean hasModuleName(ModuleName moduleName) {
            requireNonNull(moduleName);
            return moduleName.equals(module.getModuleName());
        }

        @Override
        public ObservableList<Module> getFilteredModuleList() {
            ObservableList<Module> internalList = FXCollections.observableArrayList();
            internalList.add(module);
            return internalList;
        }

        @Override
        public ObservableList<Meeting> getFilteredMeetingList() {
            ObservableList<Meeting> internalList = FXCollections.observableArrayList();
            internalList.add(selectedMeeting);
            return internalList;
        }

        @Override
        public boolean hasMeeting(Meeting meeting) {
            requireNonNull(meeting);
            return meeting.isSameMeeting(selectedMeeting);
        }

        @Override
        public void setSelectedMeeting(Meeting meeting) {
            requireNonNull(meeting);
            selectedMeeting = meeting;
        }
    }
}
