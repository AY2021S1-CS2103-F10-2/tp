package seedu.address.model.person;

import java.util.List;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the names given
 * and a {@code Person}'s {@code Tags} matches all of the tags given.
 */
public class PersonHasTagsAndNamePredicate implements PersonPredicate {
    private final List<String> names;
    private final List<Tag> tags;

    @SuppressWarnings("CheckStyle")
    public PersonHasTagsAndNamePredicate(List<String> names, List<Tag> tags) {
        this.names = names;
        this.tags = tags;
    }

    @Override
    public boolean test(Person person) {
        return names.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword))
                ||
                person.getTags().containsAll(tags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PersonHasTagsAndNamePredicate // instanceof handles nulls
                && names.equals(((PersonHasTagsAndNamePredicate) other).names)
                && tags.equals(((PersonHasTagsAndNamePredicate) other).tags)); // state check
    }

}
