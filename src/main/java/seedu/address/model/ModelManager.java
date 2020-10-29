package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final MeetingBook meetingBook;
    private final ModuleBook moduleBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Meeting> filteredMeetings;
    private final FilteredList<Module> filteredModules;
    private Meeting selectedMeeting;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyMeetingBook meetingBook,
                        ReadOnlyModuleBook moduleBook, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, meetingBook, userPrefs, moduleBook);

        logger.fine("Initializing with address book: " + addressBook
                + " and meetingBook " + meetingBook
                + " and moduleBook " + moduleBook
                + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.meetingBook = new MeetingBook(meetingBook);
        this.moduleBook = new ModuleBook(moduleBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredMeetings = new FilteredList<>(this.meetingBook.getMeetingList());
        filteredModules = new FilteredList<>(this.moduleBook.getModuleList());

        // set selected meeting to first meeting in list on launch
        if (filteredMeetings.size() > 0) {
            selectedMeeting = filteredMeetings.get(0);
        }
    }

    public ModelManager() {
        this(new AddressBook(), new MeetingBook(), new ModuleBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    @Override
    public Path getMeetingBookFilePath() {
        return userPrefs.getMeetingBookFilePath();
    }

    @Override
    public void setMeetingBookFilePath(Path meetingBookFilePath) {
        requireNonNull(meetingBookFilePath);
        userPrefs.setMeetingBookFilePath(meetingBookFilePath);
    }

    @Override
    public Path getModuleBookFilePath() {
        return userPrefs.getModuleBookFilePath();
    }

    @Override
    public void setModuleBookFilePath(Path moduleBookFilePath) {
        requireNonNull(moduleBookFilePath);
        userPrefs.setMeetingBookFilePath(moduleBookFilePath);
    }


    //=========== AddressBook ================================================================================

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasPersonName(Name name) {
        requireNonNull(name);
        return addressBook.hasPersonName(name);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Meetings ===================================================================================
    @Override
    public void setMeetingBook(ReadOnlyMeetingBook meetingBook) {
        this.meetingBook.resetData(meetingBook);
    }

    @Override
    public ReadOnlyMeetingBook getMeetingBook() {
        return meetingBook;
    }

    @Override
    public boolean hasMeeting(Meeting meeting) {
        requireNonNull(meeting);
        return meetingBook.hasMeeting(meeting);
    }

    @Override
    public boolean hasMeetingName(MeetingName meetingName) {
        requireNonNull(meetingName);
        return meetingBook.hasMeetingName(meetingName);
    }

    @Override
    public void deleteMeeting(Meeting targetMeeting) {
        meetingBook.removeMeeting(targetMeeting);
        // set selected meeting to be null if it is deleted
        if (targetMeeting.equals(selectedMeeting)) {
            setSelectedMeeting(null);
        }
    }

    @Override
    public void addMeeting(Meeting meeting) {
        meetingBook.addMeeting(meeting);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setMeeting(Meeting target, Meeting editedMeeting) {
        requireAllNonNull(target, editedMeeting);
        // replace selecting meeting to be the edited meeting if it has been edited
        if (target.equals(selectedMeeting)) {
            setSelectedMeeting(editedMeeting);
        }
        meetingBook.setMeeting(target, editedMeeting);
    }

    @Override
    public void setSelectedMeeting(Meeting target) {
        logger.fine("Setting selected meeting");
        selectedMeeting = target;
    }

    @Override
    public Meeting getSelectedMeeting() {
        logger.fine("Retrieving selected meeting");
        return selectedMeeting;
    }

    @Override
    public void updatePersonInMeetingBook(Person... persons) {
        requireNonNull(persons);
        Person personToUpdate = persons[0];
        boolean isReplacement = persons.length > 1;

        filteredMeetings.stream().filter(meeting -> meeting.getParticipants()
                .contains(personToUpdate)).forEach(meeting -> {
                    Set<Person> updatedMembers = new HashSet<>(meeting.getParticipants());
                    logger.fine("Removing contact from relevant meeting.");
                    updatedMembers.remove(personToUpdate);
                    if (isReplacement) {
                        assert persons.length == 2;
                        Person editedPerson = persons[1];
                        logger.fine("Adding edited contact to relevant meeting.");
                        updatedMembers.add(editedPerson);
                    }
                    Meeting updatedMeeting = new Meeting(meeting.getModule(), meeting.getMeetingName(),
                            meeting.getDate(), meeting.getTime(), updatedMembers, meeting.getAgendas(),
                            meeting.getNotes());
                    setMeeting(meeting, updatedMeeting);
                });
    }

    //=========== Modules ===================================================================================
    @Override
    public void addModule(Module module) {
        moduleBook.addModule(module);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    /**
     * Returns true if a module with the same module name as {@code module} exists in the address book.
     */
    @Override
    public boolean hasModuleName(ModuleName moduleName) {
        requireNonNull(moduleName);
        return moduleBook.hasModuleName(moduleName);
    }

    @Override
    public ReadOnlyModuleBook getModuleBook() {
        return moduleBook;
    }

    @Override
    public void setModuleBook(ReadOnlyModuleBook newBook) {
        this.moduleBook.resetData(newBook);
    }

    @Override
    public void updatePersonInModuleBook(Person ...persons) {
        requireNonNull(persons);
        Person personToUpdate = persons[0];
        boolean isReplacement = persons.length > 1;
        filteredModules.stream()
                .filter(module -> module.getClassmates().contains(personToUpdate))
                .forEach(module -> {
                    Set<Person> updatedClassmates = new HashSet<>(module.getClassmates());
                    logger.fine("Removing contact from relevant module.");
                    updatedClassmates.remove(personToUpdate);
                    if (isReplacement) {
                        assert persons.length == 2;
                        Person editedPerson = persons[1];
                        logger.fine("Adding edited contact to relevant module.");
                        updatedClassmates.add(editedPerson);
                    }
                    Module updatedModule = new Module(module.getModuleName(), updatedClassmates);
                    moduleBook.setModule(module, updatedModule);
                });
    }

    @Override
    public void updateModuleInMeetingBook(Module... modules) {
        requireNonNull(modules);
        Module moduleToUpdate = modules[0];
        boolean isReplacement = modules.length > 1;

        List<Meeting> meetingList = filteredMeetings.stream().filter(meeting -> meeting.getModule()
                .equals(moduleToUpdate)).collect(Collectors.toList());
        for (Meeting filteredMeeting: meetingList) {
            Set<Person> updatedMembers = new HashSet<>();
            if (isReplacement) {
                assert modules.length == 2;
                Module editedmodule = modules[1];
                for (Person person : editedmodule.getClassmates()) {
                    if (filteredMeeting.getParticipants().contains(person)) {
                        updatedMembers.add(person);
                    }
                }
            }
            if (updatedMembers.size() == 0) {
                deleteMeeting(filteredMeeting);
            } else {
                Meeting updatedMeeting = new Meeting(modules[1], filteredMeeting.getMeetingName(),
                        filteredMeeting.getDate(), filteredMeeting.getTime(), updatedMembers,
                        filteredMeeting.getAgendas(), filteredMeeting.getNotes());
                setMeeting(filteredMeeting, updatedMeeting);
            }
        }
    }

    @Override
    public void setModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        moduleBook.setModule(target, editedModule);
    }

    @Override
    public void deleteModule(Module target) {
        moduleBook.removeModule(target);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    /**
     * Returns an unmodifiable view of the updated list of {@code Person} without changing the internal list
     * of {@code versionedAddressBook}
     * @param predicate Predicate to filter and update list
     * @return filtered list of {@code Person} that match the given Predicate
     */
    @Override
    public ObservableList<Person> getUpdatedFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        return new FilteredList(filteredPersons, predicate);
    }

    /**
     * Returns an unmodifiable view of the updated list of {@code Person} without changing the internal list
     * of {@code versionedAddressBook}
     * @param predicate Predicate to filter and update list
     * @param modules List of {@code ModuleName} of Modules
     * @return filtered list of {@code Person} that match the given Predicate or are in the given modules
     */
    @Override
    public ObservableList<Person> getUpdatedFilteredPersonList(Predicate<Person> predicate, List<ModuleName> modules)
            throws CommandException {
        requireNonNull(predicate);
        List<Module> moduleList = new ArrayList<>();
        for (ModuleName name : modules) {
            Module m = moduleBook.getModule(name)
                    .orElseThrow(() -> new CommandException(
                            String.format("Module %s does not exist.", name.toString())));
            moduleList.add(m);
        }
        Predicate<Person> combined = x -> predicate.test(x)
                || moduleList.stream()
                .anyMatch(m -> m.getClassmates().contains(x));
        return new FilteredList(filteredPersons, combined);
    }

    //=========== Filtered Meeting List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Meeting> getFilteredMeetingList() {
        return filteredMeetings;
    }

    @Override
    public void updateFilteredMeetingList(Predicate<Meeting> predicate) {
        requireNonNull(predicate);
        filteredMeetings.setPredicate(predicate);
    }

    //=========== Filtered Module List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Module> getFilteredModuleList() {
        return filteredModules;
    }

    @Override
    public void getPersonsInModule(ModuleName moduleName) throws CommandException {
        requireAllNonNull(moduleName);
        Optional<Module> module = moduleBook.getModule(moduleName);
        if (module.isEmpty()) {
            throw new CommandException("This module does not exist");
        } else {
            Module mod = module.get();
            Predicate<Person> predicate = person -> mod.getClassmates().contains(person);
            updateFilteredPersonList(predicate);
        }
    }

    @Override
    public void updateFilteredModuleList(Predicate<Module> predicate) {
        requireNonNull(predicate);
        filteredModules.setPredicate(predicate);
    }


    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && meetingBook.equals(other.meetingBook)
                && moduleBook.equals(other.moduleBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPersons.equals(other.filteredPersons);
    }

}
