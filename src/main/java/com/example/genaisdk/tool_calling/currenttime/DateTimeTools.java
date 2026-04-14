package com.example.genaisdk.tool_calling.currenttime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeTools {

    private static final Logger log = LoggerFactory.getLogger(DateTimeTools.class);

    @Tool(
            description = "Get the current date and time in the user's timezone."
    )
    public String getCurrentDateTimeWithoutZone() {
        // For simplicity, returning a static date-time string.
        // In a real implementation, you would fetch the current date-time based on the timezone.
        log.info("DateTimeTools is invoked - getCurrentDateTime");
        return LocalDateTime.now()
                .atZone(java.util.TimeZone.getDefault().toZoneId())
                .toString();
    }

    @Tool(
            description = "Get the current date and time in the specific timezone."
    )
    public String getCurrentDateTime(String timeZone) {

        log.info("DateTimeTools is invoked - getCurrentDateTime time Zone: {}", timeZone);
        try {
            var zoneId = ZoneId.of(timeZone);
            var zonedDateTime = LocalDateTime.now().atZone(zoneId);
            return zonedDateTime.toString();
        } catch (Exception e) {
            log.error("Invalid time zone provided: {}", timeZone, e);
            return "Invalid time zone provided: " + timeZone;
        }

    }
}
