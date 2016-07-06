package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.meds.Med;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Returns Mocked Objects for testing Purposes.
 */
public class MockDoseEventService implements DoseEventService {
    @Override
    public List<DoseEvent> getEventsForUser(AuthUser authUser) {
        Calendar someDate = Calendar.getInstance();
        someDate.set(2016, 6, 20, 12, 30);
        return Arrays.asList(
            new DoseEvent(
                1,
                new AmigoUser(
                    1L,
                    "John Doe"
                ),
                Arrays.asList(
                    new Med(
                        1L,
                        new AmigoUser(
                            1L,
                            "John Doe"
                        ),
                        1,
                        "Allegra"
                    )
                ),
                someDate.getTime(),
                EventType.TAKEN
            )
        );
    }
}
