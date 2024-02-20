package io.github.antonmenov.realenvsync.utils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.value.qual.IntRange;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

import static io.github.antonmenov.realenvsync.plugin.Constants.MOON_CYCLE_DURATION;
import static io.github.antonmenov.realenvsync.plugin.Constants.MID_FULL_MOON_OFFSETDATETIME;
import static io.github.antonmenov.realenvsync.plugin.Constants.MINECRAFT_DAY_LENGTH;
import static io.github.antonmenov.realenvsync.plugin.Constants.MINECRAFT_DAY_START_OFFSET;
import static io.github.antonmenov.realenvsync.plugin.Constants.MOON_PHASE_DURATION;

public class TimeConvert {

    private TimeConvert() {
        throw new IllegalStateException("Utility class");
    }

    public static int convertToTicks(final @NonNull OffsetDateTime dateTime) {
        final @NonNull OffsetDateTime adjustedFullMoonDateTime = adjustFullMoonTime(dateTime);
        final Duration timeUntilFullMoon = Duration.between(dateTime, adjustedFullMoonDateTime);
        final short minecraftTime = calculateMinecraftLocalTimeAt(dateTime.toOffsetTime());
        final byte moonPhase = calculateMoonPhase(timeUntilFullMoon);
        final int timeAdjustmentForMoonPhase = MINECRAFT_DAY_LENGTH * moonPhase;
        return minecraftTime + timeAdjustmentForMoonPhase;
    }

    public static int convertToTicks(final @NonNull OffsetTime time) {
        return calculateMinecraftLocalTimeAt(time);
    }

    private static @NonNull OffsetDateTime adjustFullMoonTime(final @NonNull OffsetDateTime currentTime) {
        OffsetDateTime adjustedFullMoon = MID_FULL_MOON_OFFSETDATETIME;
        while (adjustedFullMoon.isAfter(currentTime)) {
            adjustedFullMoon = adjustedFullMoon.minus(MOON_CYCLE_DURATION);
        }
        while (adjustedFullMoon.isBefore(currentTime)) {
            adjustedFullMoon = adjustedFullMoon.plus(MOON_CYCLE_DURATION);
        }
        return adjustedFullMoon.withOffsetSameInstant(currentTime.getOffset());
    }

    private static @IntRange(from = 0, to = 7) byte calculateMoonPhase(final Duration timeUntilFullMoon) {
        final Duration halfPhaseDuration = MOON_PHASE_DURATION.dividedBy(2);
        final Duration timeUntilNextCycle = MOON_CYCLE_DURATION.minus(timeUntilFullMoon);
        final Duration adjustedTime = halfPhaseDuration.plus(timeUntilNextCycle);

        final long phaseCount = adjustedTime.dividedBy(MOON_PHASE_DURATION);
        return (byte) (phaseCount % 8);
    }

    private static @IntRange(from = 0, to = 23999) short calculateMinecraftLocalTimeAt(final OffsetTime time) {
        final int secondOfDay = time.toLocalTime().toSecondOfDay();
        final double ticksInSecond = 5d / 18;
        final double currentTicks = secondOfDay * ticksInSecond;

        final long minecraftTime2 = (long) (currentTicks + MINECRAFT_DAY_START_OFFSET); // Ticks are floored to x.0
        return (short) (minecraftTime2 % MINECRAFT_DAY_LENGTH);
    }
}
