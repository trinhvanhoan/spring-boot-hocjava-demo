package hocjava.dto;

import java.time.LocalDate;

public interface ContactCountByDate {
	LocalDate getDate();
    Long getCount();
    Long getCountDone();

    default String getDayOfWeekLabel() {
    	LocalDate date = getDate();
        if (date == null) return "";

        return switch (date.getDayOfWeek()) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
}
