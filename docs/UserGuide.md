---
layout: page
title: User Guide
---

Modduke is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, Modduke can get your module management tasks done faster than traditional GUI apps.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `11` or above installed in your Computer.

1. Download the latest `modduke.jar` from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Double-click the file to start the app. The GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * **`contact list`** : Lists all contacts.

   * **`contact add`**`n/John Doe p/98765432 e/johnd@example.com` : Adds a contact named `John Doe` to the Address Book.

   * **`contact delete John Doe`** : Deletes `John Doe` from the contact list.

   * **`contact clear`** : Deletes all contacts.

   * **`exit`** : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

</div>


### Adding a contact : `contact add`

Adds a contact to Modduke.

Format: `contact add n/NAME p/PHONE_NUMBER e/EMAIL`

Note: All fields are required. No duplicate names.

Example:
* `contact add n/John Doe p/98765432 e/johnd@example.com`

### Deleting a contact : `contact delete`

Deletes the specified contact from Modduke.

Format: `contact delete CONTACT_NAME`

* Deletes the contact with the specified name.

Examples:
* `contact delete Roy` deletes `Roy` contact from Modduke.

### Editing a contact : `contact edit`

Edits an existing contact in Modduke.

Format: `contact edit CONTACT_NAME [n/NEW_NAME] [p/PHONE] [e/EMAIL] [m/MODULE]`

Note: At least one optional field must be provided

Examples:
*  `contact edit John Doe p/91234567 e/johndoe@example.com` Edits the phone number and email address of John Doe to be `91234567` and `johndoe@example.com` respectively.
*  `contact edit Roy Chan n/Betsy Crower` Edits the name of Roy Chan to be `Betsy Crower` and clears all existing tags.

### Clearing all contacts : `contact clear`

Deletes all existing contacts.

Format: NA

Note: Once cleared, contacts are permanently deleted.

### Viewing all contacts : `contact list`

Shows a list of all persons in the address book.

Format: `contact list`

### Creating a Module : `module add`

Creates a Module with a given name and members .

Format: `module add n/MODULE_NAME [m/MEMBER_NAMES]…`

Note: A Module can have more than 1 member separated by “,” but can only have one name. Members can be optional.


### Adding a tag to a user : `label add`

Adds a label to a contact

Format: ` label add c/CONTACT_NAME [t/TAG_NAMES]…`

* Only 1 contact name can be used at a time but multiple tags can be added.
* Tag names are to be separated by a ",".

Examples:
* `label add c/Jay t/2103, teamproject`

### Scheduling a meeting: `meeting add`

Schedules a meeting at a given date and time with specified members and a provided meeting name

Format: `meeting add n/MEETING_NAME d/MEETING_DATE t/MEETING_TIME m/MEMBERS`

* Creates a meeting with the provided meeting name
* All the fields must be provided
* Date is in the YYYY-MM-dd format and time is in the HH:mm format
* There can be multiple members separated by a ",".

Examples:
*  `meeting add n/CS2103 weekly meeting d/2020-09-20 t/10:00 m/Jay, Roy, Jerryl, Yeeloon, Ekam`

### Editing a meeting: `meeting edit`

Edits a given meeting. Listed below are the meeting details that can be changed:
1. Name
2. Date
3. Time
4. Contacts

Format: `meeting edit MEETING_NAME [n/NEW_NAME] [d/NEW_DATE] [t/NEW_TIME] [cD/CONTACTS]… [cA/CONTACTS]…`

* Edits any of the details of the meeting 
* `n/NEW_NAME`, `d/NEW_DATE`, `t/NEW_TIME`, `cD/CONTACTS` and `cA/CONTACTS` are all optional fields, but at least one of the optional fields must be provided
* Date is in the YYYY-MM-dd format and time is in the HH:mm format
* If there is more than one contact to be added or deleted, they should be separated by “,”

Examples:
* `meeting edit CS2103 Meeting n/CS2103 Team Project Meeting d/2020:09:27 t/14:00 cD/Ekam, Jay cA/Bob`

### Listing all Meetings : `meeting list`

Views all of the existing meetings.

Format: NA

### View all meetings : consult list

View all the existing consults

Format: `list`

### Creating a Consultation : `consult add`

Creates a new consultation with given ConsultName.

Format: `consult add n/CONSULT_NAME d/CONSULT_DATE t/CONSULT_TIME m/MEMBERS`

* CONSULT_NAME is a required field.
* [d/CONSULT_DATE], [t/CONSULT_TIME], [m/MEMBERS] are optional fields
* Multiple members can join one consultation. 
* [d/CONSULT_DATE] is in the YYYY:MM:dd format and [t/CONSULT_TIME] is in the HH:mm format.

### Editing a Consultation : `consult edit`

Edits any of the details of a consult

Format: `consult edit CONSULT_NAME [n/NEW_NAME] [d/NEW_DATE] [t/NEW_TIME] [cD/CONTACTS]… [cA/CONTACTS]…`

* [n/NEW_NAME], [d/NEW_DATE], [t/NEW_TIME], [cD/CONTACTS] and [cA/CONTACTS] are all optional fields, 
* At least one of the optional fields must be provided.
* If there is more than one contact to be added or deleted in [cA/CONTACTS], they should be separated by “,”

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Archiving data files `[coming in v2.0]`

_{explain the feature here}_

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous ModDuke home folder.

**Q**: Can I retrieve my contacts after I have delete them?<br>
**A**: No. Contacts are permanently deleted and cannot be retrieved after.

**Q**: If I face an error/bug, where can I seek assistance?<br>
**A**: You can head to the **[ModDuke GitHub Issues page](https://github.com/AY2021S1-CS2103-F10-2/tp/issues)** and create or find your issue there.

**Q**: Are commands case-sensitive?<br>
**A**: Yes

**Q**: How can I import contacts from my existing devices e.g. Mobile Phones / Email?<br>
**A**: We are currently working on importing .vcf contacts, stay tuned!

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add Contact** | `contact add [n/NAME] [p/PHONE_NUMBER] [e/EMAIL]` <br> e.g., `contact add n/Jay p/22224444 e/jay@example.com`
**Clear Contacts** | `contact clear`
**Delete Contact** | `contact delete CONTACT_NAME`<br> e.g., `delete Jay`
**Edit Contact** | `contact edit CONTACT_NAME [n/NEW_NAME] [p/PHONE] [e/EMAIL]` <br> e.g.,`contact edit Jay n/Roy e/roy@example.com`
**List Contacts** | `contact list`
**Add Module** | `module add [n/MODULE_NAME] [m/MEMBER_NAMES]`<br> e.g., `module add n/CS2103 m/Jay, Roy`
**List Modules** | `module list  [n/MODULE_NAME]`<br> e.g., `module list n/CS2103`
**Add Label** | `label add [c/CONTACT_NAME] [t/TAG_NAMES]…` <br> e.g., `label add c/Bobby Bob t/friend`
**Add Meeting** | `meeting add [n/MEETING_NAME] [d/MEETING_DATE] [t/MEETING_TIME] [m/MEMBERS]…` <br> e.g., `meeting add n/CS2103 Meeting d/2020:09:23 t/10:00 m/Ekam, Jay, Jerryl, Roy`
**Edit Meeting** |  `meeting edit MEETING_NAME [n/NEW_NAME] [d/NEW_DATE] [t/NEW_TIME] [cD/CONTACTS]… [cA/CONTACTS]…` <br> e.g., `meeting edit CS2103 Meeting n/CS2103 Team Project Meeting d/2020:09:27 t/14:00 cD/Ekam, Jay cA/Bob`
**List Meetings** | `meeting list`
**Add Consults** | `consult add n/CONSULT_NAME [d/CONSULT_DATE] [t/CONSULT_TIME] [m/MEMBERS]` <br> e.g., `add n/CS2103 Consult d/2020:09:25 t/13:00 m/Vineeth, Ekam, Jay, Jerryl, Roy`
**Edit Consults** | `consult edit CONSULT_NAME [n/NEW_NAME] [d/NEW_DATE] [t/NEW_TIME] [cD/CONTACTS] [cA/CONTACTS]` <br> e.g., `consult edit CS2103 Consult n/CS2103 Consult with Prof Damith d/2020:09:28 t/15:00 cD/Vineeth cA/Prof Damith`
**List Consults** | `consult list`
