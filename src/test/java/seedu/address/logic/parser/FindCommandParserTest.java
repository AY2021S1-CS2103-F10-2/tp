package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonHasTagsAndKeywordInNamePredicate;
import seedu.address.model.person.PersonHasTagsPredicate;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        // no arguments
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // name prefix but no name argument
        assertParseFailure(parser, " n/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // tag prefix but no tag argument
        assertParseFailure(parser, "t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // name and tag prefix but no name argument
        assertParseFailure(parser, " n/ t/tag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // name and tag prefix but no tag argument
        assertParseFailure(parser, " n/name t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // name and tag prefix but no arguments
        assertParseFailure(parser, " n/ t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Alice");
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("tag"));
        FindCommand expectedFindCommand =
                new FindCommand(new PersonHasTagsAndKeywordInNamePredicate(nameList, tagList));
        assertParseSuccess(parser, " n/Alice t/tag", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/Alice \n t/tag\t", expectedFindCommand);
    }

    @Test
    public void parse_multipleNamesAndMultipleTags_returnsFindCommand() {
        // no leading and trailing whitespaces
        Set<String> nameSet = new HashSet<>();
        nameSet.add("Alice");
        nameSet.add("Bob");
        nameSet.add("Candy");
        ArrayList<String> nameList = new ArrayList<>(nameSet);
        Set<Tag> tagSet = new HashSet<>();
        tagSet.add(new Tag("tag"));
        tagSet.add(new Tag("person"));
        ArrayList<Tag> tagList = new ArrayList<>(tagSet);
        FindCommand expectedFindCommand =
                new FindCommand(new PersonHasTagsAndKeywordInNamePredicate(nameList, tagList));
        assertParseSuccess(parser, " n/Alice n/Bob n/Candy t/tag t/person", expectedFindCommand);
    }

    @Test
    public void parse_singleNameAndNoTag_returnsFindCommand() {
        // no leading and trailing whitespaces
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Alice");
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(nameList));
        assertParseSuccess(parser, " n/Alice ", expectedFindCommand);
    }

    @Test
    public void parse_multipleNamesAndNoTag_returnsFindCommand() {
        // no leading and trailing whitespaces
        Set<String> nameSet = new HashSet<>();
        nameSet.add("Alice");
        nameSet.add("Bob");
        nameSet.add("Candy");
        ArrayList<String> nameList = new ArrayList<>(nameSet);
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(nameList));
        assertParseSuccess(parser, " n/Alice n/Bob n/Candy", expectedFindCommand);
    }

    @Test
    public void parse_noNameAndSingleTag_returnsFindCommand() {
        // no leading and trailing whitespaces
        ArrayList<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("tag"));
        FindCommand expectedFindCommand =
                new FindCommand(new PersonHasTagsPredicate(tagList));
        assertParseSuccess(parser, " t/tag ", expectedFindCommand);
    }

    @Test
    public void parse_noNameAndMultipleTags_returnsFindCommand() {
        // no leading and trailing whitespaces
        Set<Tag> tagSet = new HashSet<>();
        tagSet.add(new Tag("tag"));
        tagSet.add(new Tag("person"));
        ArrayList<Tag> tagList = new ArrayList<>(tagSet);
        FindCommand expectedFindCommand =
                new FindCommand(new PersonHasTagsPredicate(tagList));
        assertParseSuccess(parser, " t/tag t/person ", expectedFindCommand);
    }
}
