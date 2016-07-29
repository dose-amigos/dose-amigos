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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            for (Med med : meds) {

                //First Create new events based on the dose series for the week.
                DoseSeries series = doseSeriesService.getForMed(med);
                List<DoseEvent> newEvents = generateWeekEventsForSeries(series);

                //Now, we save each dose event to the database.
                List<DoseEvent> existingEvents = doseEventDao.getDoseEventForMedAfter(med,
                    DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());

                List<DoseEvent> eventsToAdd = new ArrayList<>();
                for (DoseEvent event : newEvents) {
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
                        eventsToAdd.add(event);
                    }
                }
                try {
                    doseEventDao.createMultiple(eventsToAdd, amigoUser);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public List<DoseEvent> generateWeekEventsForSeries(DoseSeries series) {
        int curDayOfWeek = DateTime.now().getDayOfWeek();
        Med med = series.getMed();
        List<DoseEvent> newEvents = new ArrayList<>();
        for (Date time : series.getTimesOfDay()) {
            for (int day : series.getDaysOfWeek()) {
                DateTime now = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
                DoseEvent newEvent = new DoseEvent();
                newEvent.setMed(med);
                DateTime dateTime = new DateTime(time);

                //Number of milliseconds since midnight of the time saved in the DB.
                int millisOfDay = dateTime.getMillisOfDay();

                //Math to determine number of days to add to today for next dose.
                //EXAMPLE today is Friday (6) and next dose is Sunday (1).
                // (1-6)%7 results in -5, add 7 and mod that by 7 to get 2, add 2 days to Sunday.
                //Note that this will add events for today if there are medications to take today.
                int daysToAdd = (((day-curDayOfWeek) % 7) + 7) % 7;

                Date newScheduledTime = now.plusDays(daysToAdd).plusMillis(millisOfDay).toDate();

                newEvent.setScheduledDateTime(newScheduledTime);
                newEvents.add(newEvent);
            }
        }
        return newEvents;
    }

    @Override
    public void markMissedEvents() {
        try {
            doseEventDao.markMissedEvents();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DoseEvent> getEventsForUserToday(AuthUser authUser, AmigoUser amigoUser) {
        List<AmigoUser> amigosForAuthUser = amigoUserService.getAmigosForAuthUser(authUser);
        amigosForAuthUser.add(authUser.getAmigoUser());
        if (!amigosForAuthUser.contains(amigoUser)) {
            throw new RuntimeException("You cannot see this amigo's doses.");
        }
        return doseEventDao.getEventsForUserToday(amigoUser);

    }

    @Override
    public List<DoseEvent> getWeeklyEventsForAuthUser(AuthUser authUser) {
        return doseEventDao.getEventsForUserWeekly(authUser.getAmigoUser());
    }

    @Override
    public List<DoseEvent> updateDoseEvents(AuthUser authUser, List<DoseEvent> doseEvents) {
        List<AmigoUser> amigosForAuthUser = amigoUserService.getAmigosForAuthUser(authUser);
        amigosForAuthUser.add(authUser.getAmigoUser());
        for (DoseEvent de : doseEvents) {
            if (!amigosForAuthUser.contains(de.getMed().getUser())) {
                throw new RuntimeException("You cannot modify this dose event.");
            }
            de.setActionDateTime(new Date(Instant.now().toEpochMilli()));
        }

        try {
            doseEventDao.updateDoseEvents(doseEvents);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return doseEvents;
    }

    @Override
    public List<DoseEvent> getDosesForPhone(AuthUser authUser, Date startDate, String dir) {
        List<DoseEvent> allEvents = doseEventDao.getEventsForUser(authUser.getAmigoUser(), startDate, dir);
        List<DoseEvent> toRet = new ArrayList<>(allEvents.size());
        //If there are events to look at, look at them.
        if (!allEvents.isEmpty()) {
            Date nextDoseTime = allEvents.get(0).getScheduledDateTime();
            for (DoseEvent de : allEvents) {
                long diff = abs(de.getScheduledDateTime().getTime() - nextDoseTime.getTime());
                //If the event is within an hour of the start dose time of the events, add it.
                //else break out of this loop as we ordered it by date and we're done
                if (diff < (1000*60*15)) {
                    toRet.add(de);
                } else {
                    break;
                }
            }
        }
        return toRet;
    }

    @Override
    public void addWeeklySeriesForDoseSeries(DoseSeries newSeries) {
        List<DoseEvent> eventsToAdd = generateWeekEventsForSeries(newSeries);
        try {
            doseEventDao.createMultiple(eventsToAdd, newSeries.getMed().getUser());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
