package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CopyCommand;
import seedu.address.model.module.ModuleName;
import seedu.address.model.person.FullNameMatchesKeywordPredicate;
import seedu.address.model.person.PersonHasTagsAndNamePredicate;
import seedu.address.model.person.PersonHasTagsPredicate;
import seedu.address.model.tag.Tag;

public class CopyCommandParserTest {

    private CopyCommandParser parser = new CopyCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        // no arguments
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // name prefix but no name argument
        assertParseFailure(parser, " n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // tag prefix but no tag argument
        assertParseFailure(parser, "t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // name and tag prefix but no name argument
        assertParseFailure(parser, " n/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // name and tag prefix but no tag argument
        assertParseFailure(parser, " n/name t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
        // name and tag prefix but no arguments
        assertParseFailure(parser, " n/ t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsCopyCommand() {
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Alice");
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("tag"));
        ArrayList<ModuleName> moduleNames = new ArrayList<>();
        moduleNames.add(new ModuleName("CS2103"));

        // only name prefix, email
        CopyCommand expectedCopyCommand =
                new CopyCommand(new FullNameMatchesKeywordPredicate(nameList), true,
                        new ArrayList<ModuleName>());
        assertParseSuccess(parser, "email n/Alice", expectedCopyCommand);

        // only tag prefix, email
        expectedCopyCommand =
                new CopyCommand(new PersonHasTagsPredicate(tagList), true,
                        new ArrayList<ModuleName>());

        assertParseSuccess(parser, "email t/tag", expectedCopyCommand);

        // only module prefix, phone
        expectedCopyCommand =
                new CopyCommand(new PersonHasTagsPredicate(new ArrayList<>()), false,
                        moduleNames);

        assertParseSuccess(parser, "phone m/CS2103", expectedCopyCommand);

        // name and tag prefix, phone
        expectedCopyCommand =
                new CopyCommand(new PersonHasTagsAndNamePredicate(nameList, tagList), false,
                        new ArrayList<ModuleName>());

        assertParseSuccess(parser, "phone n/Alice t/tag", expectedCopyCommand);

        // name and module prefix, email
        expectedCopyCommand =
                new CopyCommand(new FullNameMatchesKeywordPredicate(nameList), true,
                        moduleNames);

        assertParseSuccess(parser, "email n/Alice m/CS2103", expectedCopyCommand);

        // tag and module prefix, email
        expectedCopyCommand = new CopyCommand(new PersonHasTagsPredicate(tagList), true,
                moduleNames);

        assertParseSuccess(parser, "email m/CS2103 t/tag", expectedCopyCommand);

        // all prefix, phone
        expectedCopyCommand =
                new CopyCommand(new PersonHasTagsAndNamePredicate(nameList, tagList), false,
                        moduleNames);

        assertParseSuccess(parser, "phone n/Alice t/tag m/CS2103", expectedCopyCommand);

        // multiple whitespaces between keywords
        expectedCopyCommand =
                new CopyCommand(new PersonHasTagsAndNamePredicate(nameList, tagList), true,
                        moduleNames);

        assertParseSuccess(parser, " email \n m/CS2103 \n n/Alice \n t/tag\t", expectedCopyCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no input
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty name prefix
        assertParseFailure(parser, "n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty tag prefix
        assertParseFailure(parser, "t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty module prefix
        assertParseFailure(parser, "m/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty name prefix with other prefixes
        assertParseFailure(parser, " n/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty tag prefix with other prefixes
        assertParseFailure(parser, " m/CS2103 t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // empty module prefix with other prefixes
        assertParseFailure(parser, " m/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));

        // multiple empty prefixes
        assertParseFailure(parser, " n/ t/ m/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, CopyCommand.MESSAGE_USAGE));
    }
}
