package seedu.address.model.person;

import java.util.List;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class FullNameMatchesKeywordPredicate implements PersonPredicate {
    private final List<String> keywords;

    public FullNameMatchesKeywordPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> person.getName().fullName.toLowerCase().equals(keyword.toLowerCase()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FullNameMatchesKeywordPredicate // instanceof handles nulls
                && keywords.equals(((FullNameMatchesKeywordPredicate) other).keywords)); // state check
    }

}
