package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.AmigoUserService;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.doseseries.DoseSeries;
import info.doseamigos.doseseries.DoseSeriesService;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedService;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link DoseEventService}.
 */
public class DefaultDoseEventService implements DoseEventService {

    private final AmigoUserService amigoUserService;
    private final MedService medService;
    private final DoseSeriesService doseSeriesService;
    private final DoseEventDao doseEventDao;

    @Inject
    public DefaultDoseEventService(
        @Nonnull AmigoUserService amigoUserService,
        @Nonnull MedService medService,
        @Nonnull DoseSeriesService doseSeriesService,
        @Nonnull DoseEventDao doseEventDao
    ) {
        this.amigoUserService = requireNonNull(amigoUserService);
        this.medService = requireNonNull(medService);
        this.doseSeriesService = requireNonNull(doseSeriesService);
        this.doseEventDao = requireNonNull(doseEventDao);
    }

    @Override
    public List<DoseEvent> getUpcomingEventsForUser(AuthUser authUser, AmigoUser amigoUser, Date startDate) {
        return null;
    }

    @Override
    public List<DoseEvent> getAllDoseEventsForMedAfter(Med med, Date beginDate) {
        return doseEventDao.getDoseEventForMedAfter(med, beginDate);
    }

    @Override
    public void generateDoseEventsForAllUsers() {
        List<AmigoUser> amigos = amigoUserService.getAllAmigosInSystem();
        for (AmigoUser amigoUser : amigos) {
            List<Med> meds = medService.medsForUserSystemCommand(amigoUser);
            int curDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            for (Med med : meds) {
                //First Create new events based on the dose series for the week.
                List<DoseEvent> newEvents = new ArrayList<>();
                DoseSeries series = doseSeriesService.getForMed(med);
                for (Date time : series.getTimes()) {
                    for (int day : series.getDays()) {
                        DateTime now = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
                        DoseEvent newEvent = new DoseEvent();
                        newEvent.setMed(med);
                        DateTime dateTime = new DateTime(time);

                        //Number of milliseconds since midnight of the time saved in the DB.
                        int millisOfDay = dateTime.getMillisOfDay();

                        //Math to determine number of days to add to today for next dose.
                        //EXAMPLE today is Friday (6) and next dose is Sunday (1).
                        // (1-6)%7 results in -5, add 7 and mod that by 7 to get 2, add 2 days to Sunday.
                        int daysToAdd = (((day-curDayOfWeek) % 7) + 7) % 7;

                        Date newScheduledTime = now.plusDays(daysToAdd).plusMillis(millisOfDay).toDate();

                        newEvent.setScheduledDateTime(newScheduledTime);
                        newEvents.add(newEvent);
                    }
                }

                //Now, we save each dose event to the database.
                List<DoseEvent> existingEvents = doseEventDao.getDoseEventForMedAfter(med,
                    DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
                for (DoseEvent event : newEvents) {
                    try {
                        boolean added = false;
                        //If any existing event is within a minute, we'll assume the event was already added.
                        for (DoseEvent existingEvent : existingEvents) {
                            long difference = existingEvent.getScheduledDateTime().getTime()
                                - event.getScheduledDateTime().getTime();
                            if (abs(difference) < 60000) {
                                added = true;
                            }
                        }
                        if (!added) {
                            doseEventDao.create(event);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public List<DoseEvent> getEventsForUserToday(AuthUser authUser, AmigoUser amigoUser) {
        //TODO validation. Ensure auth user can see this amigo user.

        return doseEventDao.getEventsForUserToday(amigoUser);

    }

    @Override
    public List<DoseEvent> updateDoseEvents(AuthUser authUser, List<DoseEvent> doseEvents) {
        //TODO validation.  Ensure that authUser can modify these dose events.

        try {
            doseEventDao.updateDoseEvents(doseEvents);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return doseEvents;
    }
}
