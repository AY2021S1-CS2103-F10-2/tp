package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.Date;
import seedu.address.model.meeting.Time;
import seedu.address.model.person.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonAdaptedMeeting {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String date;
    private final String time;
    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedMeeting(@JsonProperty("name") String name, @JsonProperty("date") String date,
                             @JsonProperty("time") String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public JsonAdaptedMeeting(Meeting source) {
        name = source.getName().meetingName;
        date = source.getDate().value;
        time = source.getTime().value;
    }

    public Meeting toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, MeetingName.class.getSimpleName()));
        }
        if (!MeetingName.isValidName(name)) {
            throw new IllegalValueException(MeetingName.MESSAGE_CONSTRAINTS);
        }
        final MeetingName modelName = new MeetingName(name);

        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Date.class.getSimpleName()));
        }
        if (!Date.isValidDate(date)) {
            throw new IllegalValueException(Date.MESSAGE_CONSTRAINTS);
        }
        final Date modelDate = new Date(date);

        if (time == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Time.class.getSimpleName()));
        }
        if (!Time.isValidTime(time)) {
            throw new IllegalValueException(Time.MESSAGE_CONSTRAINTS);
        }
        final Time modelTime = new Time(time);

        return new Meeting(modelName, modelDate, modelTime);
    }

}
