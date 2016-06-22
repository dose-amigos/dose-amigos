package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Mock service for the Meds logic.
 */
public class MockMedService implements MedService{
    @Override
    public Med addByName(String name) {
        return new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            name
        );
    }

    @Override
    public List<Med> medsForUser(AmigoUser user) {
        Med med1 = new Med(1, user, 1, "Allegra");
        Med med2 = new Med(2, user, 2, "Ibuprofen");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            med1.setLastTaken(simpleDateFormat.parse("27-06-2016 12:00"));
            med2.setLastTaken(simpleDateFormat.parse("27-06-2016 12:00"));
            med1.setNextScheduled(simpleDateFormat.parse("28-06-2016 12:00"));
            med2.setNextScheduled(simpleDateFormat.parse("28-06-2016 12:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Arrays.asList(med1, med2);
    }

    @Override
    public Med updateMed(Med medInfo) {
        medInfo.setMedId(1);
        medInfo.setUser(new AmigoUser(1, "Bobby King"));
        medInfo.setLastTaken(new Date());
        medInfo.setNextScheduled(new Date());
        return medInfo;
    }
}
