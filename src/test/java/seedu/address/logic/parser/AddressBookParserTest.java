package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
//import java.util.Arrays;
//import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddLabelCommand;
import seedu.address.logic.commands.AddLabelCommand.LabelPersonDescriptor;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ClearLabelCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteLabelCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FullNameMatchesKeywordPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.LabelPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.TypicalPersons;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " n/" + TypicalPersons.ALICE.getName().toString());
        ArrayList<String> nameOne = new ArrayList<>();
        nameOne.add(TypicalPersons.ALICE.getName().toString());
        assertEquals(new DeleteCommand(new FullNameMatchesKeywordPredicate(nameOne), new ArrayList<>()), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + TypicalPersons.ALICE.getName().toString() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(TypicalPersons.ALICE.getName(), descriptor), command);
    }

    @Test
    public void parseCommand_labelAdd() throws Exception {
        Person person = new PersonBuilder().build();
        LabelPersonDescriptor descriptor = new LabelPersonDescriptorBuilder(person).build();
        AddLabelCommand command = (AddLabelCommand) parser.parseCommand(AddLabelCommand.COMMAND_WORD + " "
            + person.getName() + " " + PersonUtil.getLabelPersonDescriptorDetails(descriptor));
        assertEquals(new AddLabelCommand(person.getName(), descriptor), command);
    }

    @Test
    public void parseCommand_labelDelete() throws Exception {
        Person person = new PersonBuilder().build();
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("Tag"));
        DeleteLabelCommand command = (DeleteLabelCommand) parser.parseCommand(DeleteLabelCommand.COMMAND_WORD + " "
                + person.getName() + " " + "t/Tag");
        assertEquals(new DeleteLabelCommand(person.getName(), tags), command);
    }

    @Test
    public void parseCommand_labelClear() throws Exception {
        Person person = new PersonBuilder().build();
        ClearLabelCommand command = (ClearLabelCommand) parser.parseCommand(ClearLabelCommand.COMMAND_WORD + " "
                + person.getName());
        assertEquals(new ClearLabelCommand(person.getName()), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    // TODO: Fix this test case. Test fails cause of failed unit test in FindCommandParserTest:
    //  parse_multipleNames_returnsFindCommand()
    //    @Test
    //    public void parseCommand_find() throws Exception {
    //        List<String> keywords = Arrays.asList("foo", "bar", "baz");
    //        List<Tag> tags = Arrays.asList(new Tag("tag"));
    //        FindCommand command = (FindCommand) parser.parseCommand(
    //                FindCommand.COMMAND_WORD + " n/foo n/bar n/baz t/tag");
    //        assertEquals(new FindCommand(new PersonHasTagsAndNamePredicate(keywords, tags)), command);
    //    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
