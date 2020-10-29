package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.FullNameMatchesKeywordPredicate;
import seedu.address.model.person.PersonHasTagsAndNamePredicate;
import seedu.address.model.person.PersonHasTagsPredicate;
import seedu.address.model.tag.Tag;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Alice");
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("tag"));
        ArrayList<ModuleName> moduleNames = new ArrayList<>();
        moduleNames.add(new ModuleName("CS2103"));

        // only name prefix, email
        DeleteCommand expectedDeleteCommand =
                new DeleteCommand(new FullNameMatchesKeywordPredicate(nameList),
                        new ArrayList<ModuleName>());
        assertParseSuccess(parser, "email n/Alice", expectedDeleteCommand);

        // only tag prefix, email
        expectedDeleteCommand =
                new DeleteCommand(new PersonHasTagsPredicate(tagList),
                        new ArrayList<ModuleName>());

        assertParseSuccess(parser, "email t/tag", expectedDeleteCommand);

        // only module prefix, phone
        expectedDeleteCommand =
                new DeleteCommand(new PersonHasTagsPredicate(new ArrayList<>()),
                        moduleNames);

        assertParseSuccess(parser, "phone m/CS2103", expectedDeleteCommand);

        // name and tag prefix, phone
        expectedDeleteCommand =
                new DeleteCommand(new PersonHasTagsAndNamePredicate(nameList, tagList),
                        new ArrayList<ModuleName>());

        assertParseSuccess(parser, "phone n/Alice t/tag", expectedDeleteCommand);

        // name and module prefix, email
        expectedDeleteCommand =
                new DeleteCommand(new FullNameMatchesKeywordPredicate(nameList),
                        moduleNames);

        assertParseSuccess(parser, "email n/Alice m/CS2103", expectedDeleteCommand);

        // tag and module prefix, email
        expectedDeleteCommand = new DeleteCommand(new PersonHasTagsPredicate(tagList),
                moduleNames);

        assertParseSuccess(parser, "email m/CS2103 t/tag", expectedDeleteCommand);

        // all prefix, phone
        expectedDeleteCommand =
                new DeleteCommand(new PersonHasTagsAndNamePredicate(nameList, tagList),
                        moduleNames);

        assertParseSuccess(parser, "phone n/Alice t/tag m/CS2103", expectedDeleteCommand);

        // multiple whitespaces between keywords
        expectedDeleteCommand =
                new DeleteCommand(new PersonHasTagsAndNamePredicate(nameList, tagList),
                        moduleNames);

        assertParseSuccess(parser, " email \n m/CS2103 \n n/Alice \n t/tag\t", expectedDeleteCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no input
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty name prefix
        assertParseFailure(parser, "n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty tag prefix
        assertParseFailure(parser, "t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty module prefix
        assertParseFailure(parser, "m/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty name prefix with other prefixes
        assertParseFailure(parser, " n/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty tag prefix with other prefixes
        assertParseFailure(parser, " m/CS2103 t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // empty module prefix with other prefixes
        assertParseFailure(parser, " m/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

        // multiple empty prefixes
        assertParseFailure(parser, " n/ t/ m/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
