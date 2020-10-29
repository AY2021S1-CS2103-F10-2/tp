package seedu.address.model.person;

import java.util.List;

import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the names given
 * and a {@code Person}'s {@code Tags} matches all of the tags given.
 */
public class PersonHasTagsAndKeywordInNamePredicate implements PersonPredicate {
    private final List<String> names;
    private final List<Tag> tags;

    /**
     * Constructs a {@code PersonHasTagsAndKeywordInNamePredicate}.
     *
     * @param names to be searched for.
     * @param tags to be searched for.
     */
    public PersonHasTagsAndKeywordInNamePredicate(List<String> names, List<Tag> tags) {
        this.names = names;
        this.tags = tags;
    }

    @Override
    public boolean test(Person person) {
        return names.stream()
                .anyMatch(keyword -> person.getName().fullName.toLowerCase().contains(keyword.toLowerCase()))
                ||
                tags.stream()
                        .anyMatch(tag -> person.getTags().contains(tag));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PersonHasTagsAndKeywordInNamePredicate // instanceof handles nulls
                && names.equals(((PersonHasTagsAndKeywordInNamePredicate) other).names)
                && tags.equals(((PersonHasTagsAndKeywordInNamePredicate) other).tags)); // state check
    }

}
