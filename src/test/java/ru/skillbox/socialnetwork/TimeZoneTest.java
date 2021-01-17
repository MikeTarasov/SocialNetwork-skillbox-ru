package ru.skillbox.socialnetwork;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeZoneTest {

    @Test
    void testZone() {
        Date now = new Date();
        Long l = now.getTime();

        LocalDateTime date = LocalDateTime.now();
        assertEquals(java.util.Date
                .from(date.atZone(ZoneId.of("Europe/Moscow"))
                        .toInstant()).getTime(), l);
    }
}
