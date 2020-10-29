package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Date {
    public static final String MESSAGE_CONSTRAINTS =
            "Dates should be in the format yyyy-mm-dd";
    public static final String VALIDATION_REGEX = "\\d{3,}";
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("d LLL (EEE)");

    // For DB storage. See Json AdaptedMeeting for use.
    public final String value;
    public final LocalDate date;

    /**
     * Constructs a {@code Date}.
     *
     * @param date A valid date.
     */
    public Date(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_CONSTRAINTS);
        this.value = date;
        this.date = LocalDate.parse(date, INPUT_FORMAT);
    }

    /**
     * Returns true if a given string is a valid date.
     */
    public static boolean isValidDate(String test) {
        return test.length() == 10
                && test.charAt(4) == '-'
                && test.charAt(7) == '-';
        //todo: check if date exists
    }

    @Override
    public String toString() {
        return date.format(OUTPUT_FORMAT);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Date // instanceof handles nulls
                && date.equals(((Date) other).date)); // state check
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
