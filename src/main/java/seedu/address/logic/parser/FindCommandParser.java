package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonHasTagsAndKeywordInNamePredicate;
import seedu.address.model.person.PersonHasTagsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG);

        if (arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_TAG)) {
            Set<String> nameSet = ParserUtil.parseAllNames(argMultimap.getAllValues(PREFIX_NAME));
            Set<Tag> tagSet = parseTagsForFind(argMultimap.getAllValues(PREFIX_TAG)).orElse(new HashSet<>());
            if ((nameSet.size() == 1 && nameSet.contains("")) || tagSet.isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }
            return new FindCommand(
                    new PersonHasTagsAndKeywordInNamePredicate(new ArrayList<>(nameSet), new ArrayList<>(tagSet)));
        } else if (arePrefixesPresent(argMultimap, PREFIX_NAME)) {
            Set<String> nameSet = ParserUtil.parseAllNames(argMultimap.getAllValues(PREFIX_NAME));
            if (nameSet.size() == 1 && nameSet.contains("")) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }
            return new FindCommand(
                    new NameContainsKeywordsPredicate(new ArrayList<>(nameSet)));
        } else if (arePrefixesPresent(argMultimap, PREFIX_TAG)) {
            Set<Tag> tagSet = parseTagsForFind(argMultimap.getAllValues(PREFIX_TAG)).orElse(new HashSet<>());
            return new FindCommand(
                    new PersonHasTagsPredicate(new ArrayList<>(tagSet)));
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForFind(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }
}
