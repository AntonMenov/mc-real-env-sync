package io.github.antonmenov.realenvsync.domain;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class Constants {

    public static final @NonNull OffsetDateTime MID_FULL_MOON_OFFSETDATETIME = OffsetDateTime.of(
            2024, 1, 25, 17, 54, 0, 0, ZoneOffset.UTC);
    public static final @NonNull Duration MOON_CYCLE_DURATION = Duration.ofDays(29).plusHours(12);
    public static final @NonNull Duration MOON_PHASE_DURATION = MOON_CYCLE_DURATION.dividedBy(8);
    public static final short MINECRAFT_DAY_LENGTH = 24_000;
    public static final short MINECRAFT_DAY_START_OFFSET = 18_000;
}
