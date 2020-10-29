---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="450" />

The ***Architecture Diagram*** given above explains the high-level design of the App. Given below is a quick overview of each component.

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/se-edu/addressbook-level3/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.

</div>

**`Main`** has two classes called [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

Each of the four components,

* defines its *API* in an `interface` with the same name as the Component.
* exposes its functionality using a concrete `{Component Name}Manager` class (which implements the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component (see the class diagram given below) defines its API in the `Logic.java` interface and exposes its functionality using the `LogicManager.java` class which implements the `Logic` interface.

![Class Diagram of the Logic Component](images/LogicClassDiagram.png)

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `contact delete Alex Yeoh`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

The sections below give more details of each component.

### UI component

![Structure of the UI Component](images/UiClassDiagram.png)

**API** :
[`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Listens for changes to `Model` data so that the UI can be updated with the modified data.

### Logic component

![Structure of the Logic Component](images/LogicClassDiagram.png)

**API** :
[`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

1. `Logic` uses the `AddressBookParser` class to parse the user command.
1. This results in a `Command` object which is executed by the `LogicManager`.
1. The command execution can affect the `Model` (e.g. adding a person).
1. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.
1. In addition, the `CommandResult` object can also instruct the `Ui` to perform certain actions, such as displaying help to the user.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("contact delete Alex Yeoh")` API call.

![Interactions Inside the Logic Component for the `contact delete Alex Yeoh` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

### Model component

![Structure of the Model Component](images/ModelClassDiagram.png)

**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user’s preferences.
* stores the address book data.
* exposes an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.


<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique `Tag`, instead of each `Person` needing their own `Tag` object.<br>
![BetterModelClassDiagram](images/BetterModelClassDiagram.png)

</div>


### Storage component

![Structure of the Storage Component](images/StorageClassDiagram.png)

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save the address book data in json format and read it back.

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Autocomplete Feature

This autocomplete mechanism is facilitated by `AutocompleteCommandBox`. It extends `CommandBox` with an autocomplete mode, which is a state stored internally as `isAutocompleteMode`. This feature also adds 
a new private class `Suggestions` to facilitate suggestion generation.
This new `AutocompleteCommandBox` class exposes one public function:

* `setupAutocompletionListeners(String commandPrefix, Suppler<List<Strings>> data)` — Attaches a new autocomplete listener which triggers autocomplete mode with `commandPrefix` and generates suggestions from `data` supplier.

![Structure of the UI Component](images/AutocompleteCommandBoxClassDiagram.png)

The following acitivity diagram gives a high level overview of the Autocomplete mechanism:

![AutocompleteActivityDiagram](images/AutocompleteActivityDiagram.png)

From this diagram we see that there is 2 states of the mechanism:

* `isAutocompleteMode` — Triggered by commandPrefix
* `hasSetPrefix` — Set using `Tab` / `Shift-Tab`

Prefix in the context of the autocomplete class refers to the string we use to filter out suggestions. For example, the prefix
'ja' would give me 'jay', 'jason' as possible suggestions.

#### Sample scenario : Generating name suggestions

Given below is an example usage scenario and how the autocomplete mechanism behaves at each step.

##### Initialization

Initialisation Code Snippet : 
```
 AutocompleteCommandBox commandBox = new AutocompleteCommandBox(cmdExecutor);
 commandBox.setupAutocompletionListeners("cname/", () -> logic.getFilteredPersonList().stream()
         .map(p -> p.getName().fullName).collect(Collectors.toList()));

```
The above code snippet will first initialise the new `AutocompleteCommandBox` object and attach an autocompletion listener, the following sequence diagram describes the processes.
See here that the commandPrefix is set to `cname/` and we are generating suggestions from the person list.


![AutocompleteActivityDiagram](images/AutocompleteInitializationSequenceDiagram.png)

Refer to the Side Note in this section on why `disableFocusTraversal()` is required.

##### Triggering Autocomplete Mode

After the autocomplete listener has been attached, users can trigger Autocomplete mode by typing in command prefix. In this case its `cname/`, and upon typing this prefix
the command box text will turn yellow signalling that the user is in autocomplete mode. In this mode, anything the user types after the command prefix till the point the user presses `TAB` will be
considered the `prefix` that will be used to generate suggestions. After `TAB` is used to set the prefix, pressing `TAB` or `Shift-TAB` will allow users to cycle through the suggestions.
Below is the sequence diagram for this flow.

![AutocompleteActivityDiagram](images/AutocompleteFlowSequenceDiagram.png)

##### Exiting Autocomplete Mode

There are two ways to exit autocomplete mode : by pressing `Enter` or `Backspace`. Below's sequence diagram illustrates the difference between the two.

![AutocompleteActivityDiagram](images/AutocompleteExitFlowSequenceDiagram0.png)
![AutocompleteActivityDiagram](images/AutocompleteExitFlowSequenceDiagram1.png)

From the diagram, we see that pressing `backspace` only unsets the prefix but does not take the user out of the autocomplete mode, allowing user to adjust their prefix to generate more accurate suggestions.
On the other hand, pressing `Enter` allows the user to lock in their suggestion, taking user out of the autocomplete mode and removing the command prefix.

#### Design consideration:

##### Aspect: Autocomplete Trigger

* **Alternative 1 (current choice):** Check substring from caret position
  * Pros: Able to support names with spaces.
  * Cons:
      * Slightly more difficult to implement, as there are more edge cases.
      * Unable to support editing of suggestions.

* **Alternative 2:** Using regex to match pattern (e.g. `.*<CMD_PREFIX>\S*`)
  * Pros: 
      * Less complex code. (Lesser Conditionals)
      * Able to support moving caret around to adjust suggestion
  * Cons: Unable to support names with spaces as space is the delimiter.

#### Side Note

Because we iterate through autocompletion suggestions using `Tab` and `Shift-Tab` which conflicts with the inbuilt
focus traversals commands. We have to disable it using the `AutocompleteCommandBox#DisableFocusTraversal()` operation.

### Clearing all Contacts

The mechanism to clear all contacts is facilitated by `ClearCommand`. It extends `Command` and implements the following methods:

* `ClearCommand#execute` - Resets the AddressBook to a new empty AddressBook.

This operation is exposed in the `LogicManager` class as `LogicManager#execute`.

#### Resetting the AddressBook

Execution Code Snippet :

`model.setAddressBook(new AddressBook());`

The above code snippet sets the AddressBook in the `model` to a new `AddressBook` object. Thus, resetting the AddressBook.

Given below is the sequence diagram of how the mechanism behaves when called using the `contact clear` command.

![ClearSequenceDiagram](images/ClearSequenceDiagram.png)

### Listing all Contacts

The mechanism to list all contacts is facilitated by `ListCommand`. It extends `Command` and implements the following methods:

* `ListCommand#execute` - Displays all Persons in the AddressBook.

This operation is exposed in the `LogicManager` class as `LogicManager#execute`.

#### Displaying all Persons in Modduke

Execution Code Snippet :

`model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);`

The above code snippet updates the `FilteredList` of Persons in the `model` using the `PREDICATE_SHOW_ALL_PERSONS`. This fills the `FilteredList` with all Persons in the AddressBook and displays it.

Given below is the sequence diagram of how the mechanism behaves when called using the `contact list` command.

![ListSequenceDiagram](images/ListSequenceDiagram.png)

### Deleting Contacts

The mechanism to delete contacts is facilitated by `DeleteCommand`. It extends `Command` and implements the following methods:

* `DeleteCommand#execute` - Deletes Persons in the AddressBook according to the user input.

This operation is exposed in the `LogicManager` class as `LogicManager#execute`.

#### Parsing the User Input

The parsing of user input for `DeleteCommand` is facilitated by `DeleteCommandParser`. It extends `Parser` and implements the following methods:

* `DeleteCommandParser#parse` - Parses the user input and returns the appropriate DeleteCommand

##### Checking for Argument Prefixes

Code Snippet :

```
if (arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_TAG, PREFIX_MODULE)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_TAG)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_MODULE)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_MODULE, PREFIX_TAG)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_NAME)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_TAG)) {
    // implementation
} else if (arePrefixesPresent(argMultimap, PREFIX_MODULE)) {
    // implementation
} else {
    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
}

```
The above code snippet will check the prefixes present in the argument. It starts by checking if all valid prefixes are present,
then a combination of 2 prefixes, then lone prefixes and throws a `ParseException` if there are no prefixes present.

##### Choosing a Predicate

In order to find the Persons in the AddressBook who match the given arguments, `DeleteCommandParser` will pass the appropriate Predicate into
`DeleteCommand`. These are Predicates available and the code snippets of their `test` methods:
* `FullNameMatchesKeywordPredicate` - Finds Persons whose full names match the given arguments following the `n/` prefix
```
return keywords.stream()
        .anyMatch(keyword -> person.getName().fullName.toLowerCase().equals(keyword.toLowerCase()));
```
* `PersonHasTagsPredicate` - Finds Persons who have the tags that match the given arguments following the `t/` prefix
```
return tags.stream()
        .anyMatch(tag -> person.getTags().contains(tag));
```
* `PersonHasTagsAndNamePredicate` - Finds Persons whose full names match the given arguments following the `n/` prefix or have the tags that match the given arguments following the `t/` prefix
```
return names.stream()
        .anyMatch(keyword -> person.getName().fullName.toLowerCase().equals(keyword.toLowerCase()))
        ||
        tags.stream()
        .anyMatch(tag -> person.getTags().contains(tag));
```

#### Checking if Person is in a Module

In order to find Persons who are in the given Modules, a List of `ModuleNames` is passed into the `DeleteCommand` by the `DeleteCommandParser`.
Then the `DeleteCommand#execute` method calls `model#GetUpdatedFilteredPersonList` with its `predicate` and the List to retrieve Persons in the give Modules.

Retrieving Modules Code Snippet :

```
List<Module> moduleList = new ArrayList<>();
for (ModuleName name : modules) {
    Module m = moduleBook.getModule(name)
            .orElseThrow(() -> new CommandException(
                    String.format("Module %s does not exist.", name.toString())));
    moduleList.add(m);
}
```
The above code snippet is from `ModelManager#GetUpdatedFilteredPersonList` and will look through the `moduleBook` for the given Module. If the Module exists, it adds it to the List module.

Combining Predicates Code Snippet :

```
Predicate<Person> combined = x -> predicate.test(x)
        || moduleList.stream()
        .anyMatch(m -> m.getClassmates().contains(x));
return new FilteredList(filteredPersons, combined);
```
The above code snippet is from `ModelManager#GetUpdatedFilteredPersonList` and it creates a new `Predicate` that checks if a Person passes
the predicate passed into the method or is in any of the Modules in the List module. Then it uses this `Predicate` to obtain a `FilteredList`
of Persons that satisfy the `Predicate`.

##### No given Modules

If there are no given Modules, then the `DeleteCommand#execute` method calls `model#GetUpdatedFilteredPersonList` with its `predicate` only.

Obtaining FilteredList Code Snippet :
```
return new FilteredList(filteredPersons, predicate);
```

The above code snippet is from `ModelManager#GetUpdatedFilteredPersonList` and it will simply use the given `predicate` to
obtain a `FilteredList` of Persons that satisfy the `Predicate`.

#### Deleting the Filtered Persons

Once the `DeleteCommand` has retrieved the `FilteredList` of Persons, it will delete all Persons in that `FilteredList` from Modduke.

Deleting Persons Code Snippet :
```
people.stream().forEach(p -> {
    model.deletePerson(p); // delete in AddressBook
    model.updatePersonInMeetingBook(p); // delete in MeetingBook
    model.updatePersonInModuleBook(p); // delete in ModuleBook
});
```
The above code snippet will iterate through all Persons in the `FilteredList` and delete them from the `AddressBook`, `MeetingBook` and `ModuleBook`.

#### Activity Diagram

Given below is the activity diagram of how the mechanism behaves when called using the `contact delete` command.

![DeleteActivityDiagram](images/DeleteActivityDiagram.png)

### Copying Email Address/Phone Number of Contacts

The mechanism to copy information from contacts is facilitated by `CopyCommand`. It extends `Command` and implements the following methods:

* `CopyCommand#execute` - Copy email addresses or phone numbers of Persons in the AddressBook according to the user input.

This operation is exposed in the `LogicManager` class as `LogicManager#execute`.

#### Parsing the User Input

The parsing of user input for `CopyCommand` is facilitated by `CopyCommandParser`. It extends `Parser` and implements the following methods:

* `CopyCommandParser#parse` - Parses the user input and returns the appropriate CopyCommand

The mechanism used to parse user input is very similar to that of `DeleteCommandParser`, except that `CopyCommandParser`
also identifies the preamble in the arguments.

##### Identifying the Preamble

Identifying Preamble Code Snippet :

```
boolean isEmail;
String preamble = argMultimap.getPreamble().trim().toLowerCase();
if (preamble.equals("email")) {
    isEmail = true;
} else if (preamble.equals("phone")) {
    isEmail = false;
} else {
    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
}
```
The above code snippet will check the preamble in the argument. It assigns the boolean `isEmail` to `true` if the user
wants to copy email addresses, `false` if the user wants to copy phone numbers and throws a `ParseException` otherwise.

#### Retrieving the Filtered Persons

The mechanism used by `CopyCommand` to obtain the `FilteredList` of Persons is identical to that of `DeleteCommand`.

#### Copying Information from the Filtered Persons

Once the `CopyCommand` has retrieved the `FilteredList` of Persons, it will copy information from all Persons in that `FilteredList` from Modduke.

Obtaining Information from Persons Code Snippet :
```
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
```
The above code snippet will check if the user wants to copy email adresses or phone numbers using the `isEmail` boolean.
Then it iterates through the `FilteredList` of Persons and obtains the relavant information as Strings. Then it combines
the Strings into a single String.

Copying Information to Clipboard Code Snippet :
```
StringSelection selection = new StringSelection(results);
Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
clipboard.setContents(selection, selection);
```
The above code snippet will then create a new `StringSelection` object using the single String of information and copy
the `StringSelection` into the user's system clipboard.

#### Activity Diagram

Given below is the activity diagram of how the mechanism behaves when called using the `copy` command.

![CopyActivityDiagram](images/CopyActivityDiagram.png)

### Finding Contacts

The mechanism to find contacts is facilitated by `FindCommand`. It extends `Command` and implements the following methods:

* `FindCommand#execute` - Display Persons in the AddressBook according to the user input.

This operation is exposed in the `LogicManager` class as `LogicManager#execute`.

#### Parsing the User Input

The parsing of user input for `FindCommand` is facilitated by `FindCommandParser`. It extends `Parser` and implements the following methods:

* `FindCommandParser#parse` - Parses the user input and returns the appropriate FindCommand

The mechanism used to parse user input is very similar to that of `DeleteCommandParser`, except that `FindCommandParser`
does not look for the `m/` prefix when parsing the arguments. So it does not pass a List of Modules into the `FindCommand`.

#### Displaying the Filtered Persons

In order to display the Filtered Persons, the `FindCommand` calls `model#updateFilteredPersonList` with its `predicate`.

Update Filtered Person List Code Snippet :
```
filteredPersons.setPredicate(predicate);
```
The above code snippet is from `ModelManager#updateFilteredPersonList` and it sets predicate of the `filteredPersons` of the model
to the given predicate. Doing so will display the Persons who satisfy the conditions specified in the user input.

#### Activity Diagram

Given below is the activity diagram of how the mechanism behaves when called using the `find` command.

![FindActivityDiagram](images/FindActivityDiagram.png)

### Labelling Contacts

The mechanism to label contacts is facilitated by `AddLabelCommand`, `ClearLabelCommand` and `DeleteLabelCommand`. They
extends `Command` and implement the following methods:

* `AddLabelCommand#execute` - Adds the specified labels to the specified Person in the AddressBook according to the user input.
* `ClearLabelCommand#execute` - Clears all labels of the specified Person in the AddressBook in the user input.
* `DeleteLabelCommand#execute` - Deletes the specified labels from the specified Person in the AddressBook according to the user input.
These operations are exposed in the `LogicManager` class as `LogicManager#execute`.

#### Parsing User Input

The parsing of user input for `AddLabelCommand`, `ClearLabelCommand` and `DeleteLabelCommand` is facilitated by
`AddLabelCommandParser`, `ClearLabelCommandParser` and `DeleteLabelCommandParser` respectively. They extend `Parser`
and implement the following methods:

* `AddLabelCommandParser#parse` - Parses the user input and returns the appropriate AddLabelCommand
* `ClearLabelCommandParser#parse` - Parses the user input and returns the appropriate ClearLabelCommand
* `DeleteLabelCommandParser#parse` - Parses the user input and returns the appropriate DeleteLabelCommand

All three parsers are identical except that they return their respective commands and `AddLabelCommandParser` and `DeleteLabelCommandParser`
parses for the `t/` prefix in the arguments.

##### Obtaining a Name Object

All three parsers call `ParserUtil#parseName` to obtain a `Name` object based on the given name in the user input.
`AddLabelCommandParser` and `DeleteLabelCommandParser` pass in the preamble of the argument into `ParserUtil#parseName`,
while `ClearLabelCommandParser` passes in the entire argument.

Parse Name Code Snippet :
```
String trimmedName = name.trim();
if (!Name.isValidName(trimmedName)) {
    throw new ParseException(Name.MESSAGE_CONSTRAINTS);
}
return new Name(trimmedName);
```
The above code snippet if from `ParserUtil#parseName` and it will check if the given name is in a valid format. If it is
it creates and returns a new `Name` object with the given name. Otherwise, it throws a `ParseException`. This `Name` object
is passed into the respective command object that will be returned by each parser.

##### Obtaining a List of Tags

This is only applicable to `AddLabelCommandParser` and `DeleteLabelCommandParser`. Both parsers obtain a List of 
Strings that follow the `t/` prefix from the argument. Then it obtains a Set of Tags Strings.

Obtaining Set of Tag Strings Code Snippet:
```
if (tags.size() == 1 && tags.contains("")) {
    return Optional.empty();
}
Collection<String> tagSet = tags;
return Optional.of(ParserUtil.parseTags(tagSet));
```
The above code snippet checks for an empty String in the List of Strings. If it has an empty String, then it returns
an empty `Optional` object. Otherwise, it returns an `Optional` of the Set with the Strings in the given List.

Then both parsers check if the `Optional` object is empty. If it is, a `ParseException` is thrown. Otherwise, `AddLabelCommandParser`
passes the Set of Strings inside the `Optional` into a `LabelPersonDescriptor` which is passed into the `AddLabelCommand`
while `DeleteLabelCommandParser` passes the Set of Strings into the `DeleteCommand`. These commands are the commands
that will be returned by each parser respectively.

#### Modifying the Specified Person

`AddLabelCommand`, `ClearLabelCommand` and `DeleteLabelCommand` will first check if there is a Person with the `Name` object
given by their parsers using `model#hasPersonName`. If there does not exist a Person, then a `CommandException` is thrown.
Otherwise, the Person with the name is obtained from the AddressBook. This Person is then modified by each command accordingly.
* `AddLabelCommand` - Adds tags to the Person based on the `LabelPersonDescriptor` given by `AddLabelCommandParser`
Adding Tags Code Snippet :
```
Set<Tag> updatedTags = new HashSet<>(personToLabel.getTags());

if (labelPersonDescriptor.getTags().isPresent()) {
    updatedTags.addAll(labelPersonDescriptor.getTags().get());
}

return new Person(personToLabel.getName(), personToLabel.getPhone(), personToLabel.getEmail(), updatedTags);
```
* `ClearLabelCommand` - Clears all tags of the Person
```
return new Person(personToClear.getName(), personToClear.getPhone(), personToClear.getEmail(), new HashSet<>());
```
* `DeleteLabelCommand` - Deletes all tags from the Person based on the Set of Strings given by `DeleteLabelCommandParser`.
Throws a `CommandException` if the Person does not have a specifed tag.
```
if (tags.stream().allMatch(tag -> personToEdit.getTags().contains(tag))) {
    Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
    updatedTags.removeAll(tags);
    return new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(), updatedTags);
} else {
    throw new CommandException(
            String.format("The person '%s' does not have all the tags provided.",
                    personToEdit.getName().toString()));
}
```

#### Updating Modduke

Once they have obtained the modified Person, they replace the original Person with the modified one in the `AddressBook`,
`MeetingBook` and `ModuleBook`.

Updating Modduke Code Snippet :
```
model.setPerson(personToLabel, labelledPerson);

model.updatePersonInMeetingBook(personToLabel, labelledPerson);

model.updatePersonInModuleBook(personToLabel, labelledPerson);
```
The above code snippet updates the Person in each of the three books.

#### Sequence Diagram

Given below is the sequence diagram of how the mechanism behaves when called using the `label add` command.

![AddLabelSequenceDiagram](images/AddLabelSequenceDiagram.png)

Given below is the sequence diagram of how the mechanism behaves when called using the `label clear` command.

![ClearLabelSequenceDiagram](images/ClearLabelSequenceDiagram.png)

Given below is the sequence diagram of how the mechanism behaves when called using the `label delete` command.

![DeleteLabelSequenceDiagram](images/DeleteLabelSequenceDiagram.png)

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**

Anybody → Students → University Students → NUS Students → NUS Students handling multiple projects

* needs to keep track of contacts for various people (Professors, TA, Groupmates)
* needs to schedule school-related appointments
* needs to keep track of school-related appointments
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**:

* seamless contact management which is faster than a typical mouse/GUI driven app
* convenient scheduling of project meetings and consultations, making planning a work week effortless
* effective visualisation of schedules and meetings with the application's timeline dashboard

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                                                                                                       | So that I can…​                                                                                                 |
| -------- | ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------ |
| `* * *`  | New user                                   | See usage instructions                                                                                                   | Have easy reference when I forget how to use the App                                                               |
| `* * *`  | NUS Student                                | Add contacts of people around me                                                                                         | Find them quickly and ensure that I don’t lose anyone's contact information                                        |
| `* * *`  | NUS Student                                | Delete a contact                                                                                                         | Remove old or unwanted contacts                                                                                    |
| `* * *`  | NUS Student                                | Edit an existing contact                                                                                                 | Change their contact details if it has changed                                                                     |
| `* * *`  | NUS Student                                | View my entire list of contacts                                                                                          | Select who I want to contact                                                                                       |
| `* * *`  | NUS Student                                | Clear all contacts                                                                                                       | Reset my contacts                                                                                                  |
| `* * *`  | NUS Student                                | Label my contacts based on the individual's relationship with me (e.g. TA, Professor, Classmate)                         | Easily identify the contacts relevant to my query                                                                  |
| `* * *`  | NUS Student                                | Create meetings for events such as projects or assignments                                                               | I can keep track of commitments and upcoming work                                                                  |
| `* * *`  | NUS Student                                | Add relevant contacts to a meeting                                                                                       | Keep track of who is participating in the meeting and their contact information                                    |
| `* * *`  | Forgetful NUS Student                      | Assign a meeting a timeslot and date                                                                                     | Track exactly when I am supposed to meet                                                                           |
| `* * *`  | NUS Student with many meetings             | View all scheduled meetings                                                                                              | Have an overview of all my meetings                                                                                |
| `* * *`  | NUS Student                                | Create consultations with professors                                                                                     | Track when I have set up meetings with professors and TA’s                                                         |
| `* * *`  | NUS Student                                | Add contacts to a consultation                                                                                           | Keep track of which professor I am consulting and access his/her contact details easily                            |
| `* * *`  | NUS Student                                | Assign a consultation a timeslot and date                                                                                | Keep track of when my upcoming consultations are                                                                   |
| `* * *`  | NUS Student taking many modules            | Create modules                                                                                                           | Add new modules whenever needed                                                                                    |
| `* * *`  | NUS Student taking many modules            | View relevant groups of contacts by modules                                                                              | I can easily keep track of contact details of individuals in different modules                                     |
| `* * `   | NUS Student                                | Hide private contact details                                                                                             | Minimize chances of someone else seeing them by accident                                                           |
| `*    `  | Student who likes to personalise stuff     | Customise the layout of the App                                                                                          | I can organise relevant information in personalised way that I find easy to access                                 |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `Modduke` and the **Actor** is the `user`, unless specified otherwise)

**UC01: Add a Contact**

**MSS**

1.  User requests to add a contact to the list
2.  Modduke adds the contact

 Use case ends.

**Extensions**

* 1a. Name, phone number or email field is missing.

  * 1a1. Modduke shows an error message.

    Use case ends.

* 1b. Contact with the same name already exists.

  * 1b1. Modduke shows an error message.

    Use case ends.


**UC02: Delete a Contact**

**MSS**

1.  User requests to list contacts
2.  Modduke shows a list of contacts
3.  User requests to delete a specific contact in the list
4.  Modduke deletes the contact

 Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The given name is invalid.

  * 3a1. Modduke shows an error message.

    Use case resumes at step 2.


**UC03: Edit a Contact**

**MSS**

1.  User requests to list contacts
2.  Modduke shows a list of contacts
3.  User requests to edit a specific contact in the list
4.  Modduke edits the contact

 Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. No optional field is provided.

  * 3a1. Modduke shows an error message.

    Use case resumes at step 2.

* 3b. The given name is invalid.

  * 3b1. Modduke shows an error message.

    Use case resumes at step 2.


**UC04: View Contacts**

**MSS**

1.  User requests to list contacts
2.  Modduke shows a list of contacts

 Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

**UC05: Label a Contact**

**MSS**

1.  User requests to list contacts
2.  Modduke shows a list of contacts
3.  User requests to label a specific contact in the list
4.  Modduke labels the contact

 Use case ends.

**Extensions**

* 2a. The list is empty.

    Use case ends.

* 3a. The given name is invalid.

  * 3a1. Modduke shows an error message.

    Use case resumes at step 2.

* 3b. No tags are provided.

  * 3b1. Modduke shows an error message.

    Use case resumes at step 2.


**UC06: Clear all Contacts**

**MSS**

1.  User requests to clear all existing contacts.
2.  Modduke deletes all existing contacts.

 Use case ends.

**UC07: Clear Contacts**

**MSS**

1.  User makes request to clear all contacts
2.  Modduke clears all contacts

    Use case ends.

**UC08: Create Meeting**

**MSS**

1.  User makes request to create a meeting
2.  Modduke accepts request and creates meeting

    Use case ends.

**Extensions**

* 1a. Meeting Name is missing.

  * 1a1. Modduke shows an error message.

    Use case ends

* 1b. Meeting with the same name already exists.

  * 1b1. Modduke shows an error message.

    Use case ends.

**UC09: Set Time/Date for Meeting**

**MSS**

1.  User makes request to edit a specific meeting
2.  Modduke accepts request and makes changes to meeting

    Use case ends.

**Extensions**

* 1a. Meeting Name is missing.

  * 1a1. Modduke shows an error message.

    Use case ends.

* 1b. Meeting with the same name already exists.

  * 1b1. Modduke shows an error message.

    Use case ends.

**UC10: View all Meeting**

**MSS**

1.  User makes request to show all meetings
2.  Modduke accepts request and displays all meetings

    Use case ends.

**UC11: Create Consult**

**MSS**

1.  User makes request to create a consult
2.  Modduke accepts request and creates consult

    Use case ends.

**Extensions**

* 1a. Consult Name is missing.

  * 1a1. Modduke shows an error message.

    Use case ends

* 1b. Consult with the same name already exists.

  * 1b1. Modduke shows an error message.

    Use case ends.

**UC12: Set Time/Date for Consult**

**MSS**

1.  User makes request to edit a specific consult
2.  Modduke accepts request and makes changes to consult

    Use case ends.

**Extensions**

* 1a. Consult Name is missing.

  * 1a1. Modduke shows an error message.

    Use case ends.

* 1b. Consult with the same name already exists.

  * 1b1. Modduke shows an error message.

    Use case ends.



*{More to be added}*

### Non-Functional Requirements

1. The product should only be for a single user rather than multi-user.
2. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
3. Increments to the code should be made every week with a consistent delivery rate.
4. The data should be stored locally and should be in a human editable text file, instead of using a DBMS.
5. The software should follow the Object-oriented paradigm primarily.
6. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
7. Should work without requiring an installer.
8. Should be able to function without having to rely on being heavily connected to a network.
9. The use of third-party frameworks and libraries should be avoided.
10. JAR files should not exceed 100Mb and PDF files should not exceed 15Mb/file.
11. All features should be easy to test. (i.e., do not depend heavily on remote APIs, do not have audio-related features and do not require creating user accounts before usage)
12. The system should repond within two seconds.
13. Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **.vcf,.csv files**: A format of files that contains contact information from users phones
* **CLI**: CLI is the Command Line Interface where you can type in commands and get an output
* **TA**: Teaching assistant
* **Consultation**: A meeting between students and a professor or TA
* **Meeting**: A general purpose appointment between students


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: List all contacts using the `contact list` command. Multiple contacts in the list.

   1. Test case: `contact delete Alex Yeoh`<br>
      Expected: Contact Alex Yeoh is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `contact delete blah`<br>
      Expected: No contact is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `contact delete`, `contact delete x`, `...` (where x is a name not in the list of contacts)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
