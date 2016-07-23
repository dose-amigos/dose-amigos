package info.doseamigos.doseseries;

import info.doseamigos.meds.Med;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Represents a weekly schedule for a Medication.
 */
public class DoseSeries {

    private Long seriesId;

    private Med med;

    private List<Integer> daysOfWeek;

    private List<Date> timesOfDay;

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Med getMed() {
        return med;
    }

    public void setMed(Med med) {
        this.med = med;
    }

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<Date> getTimesOfDay() {
        return timesOfDay;
    }

    public void setTimesOfDay(List<Date> timesOfDay) {
        this.timesOfDay = timesOfDay;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
