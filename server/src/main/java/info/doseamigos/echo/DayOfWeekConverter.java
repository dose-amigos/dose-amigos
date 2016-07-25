package info.doseamigos.echo;

import java.time.DayOfWeek;

/**
 * I hate life.
 */
public class DayOfWeekConverter {

    public static DayOfWeek toDayOfWeek(String day) {
        if (day.equalsIgnoreCase("monday")) {
            return DayOfWeek.MONDAY;
        }
        if (day.equalsIgnoreCase("tuesday")) {
            return DayOfWeek.TUESDAY;
        }
        if (day.equalsIgnoreCase("wednesday")) {
            return DayOfWeek.WEDNESDAY;
        }
        if (day.equalsIgnoreCase("thurday")) {
            return DayOfWeek.THURSDAY;
        }
        if (day.equalsIgnoreCase("friday")) {
            return DayOfWeek.FRIDAY;
        }
        if (day.equalsIgnoreCase("saturday")) {
            return DayOfWeek.SATURDAY;
        }
        if (day.equalsIgnoreCase("sunday")) {
            return DayOfWeek.SUNDAY;
        }
        throw new RuntimeException("Bad day.");
    }
}
