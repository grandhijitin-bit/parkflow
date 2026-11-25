package util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting and parsing date-time values.
 */
public final class DateTimeUtil {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a");

    private DateTimeUtil() {
        // utility class
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime parseIso(String text) {
        if (text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text.trim())) {
            return null;
        }
        try {
            return LocalDateTime.parse(text.trim(), ISO_FORMATTER);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(text.trim(), DISPLAY_FORMATTER);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static String formatDuration(Duration duration) {
        if (duration == null) {
            return "0m";
        }
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString().trim();
    }
}
