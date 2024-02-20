package io.github.antonmenov.realenvsync.utils;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.value.qual.IntRange;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static io.github.antonmenov.realenvsync.plugin.Constants.MID_FULL_MOON_OFFSETDATETIME;
import static io.github.antonmenov.realenvsync.plugin.Constants.MINECRAFT_DAY_LENGTH;
import static io.github.antonmenov.realenvsync.plugin.Constants.MOON_CYCLE_DURATION;
import static io.github.antonmenov.realenvsync.plugin.Constants.MOON_PHASE_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeConvertTest {

    /**
     * These tests are expected to work only with default constants values.
     */
    @Nested
    class SyntheticTest {

        @Test
        void givenFullMoonDateAndTimeSix_whenConvertingToTicks_calculatesCorrectly() {
            // Given
            final LocalDate fullMoonDate = MID_FULL_MOON_OFFSETDATETIME.toLocalDate();
            final LocalTime time = LocalTime.of(6, 0);
            final OffsetDateTime fullMoonDateAtTime = OffsetDateTime.of(fullMoonDate, time, ZoneOffset.UTC);

            // When
            final int expected = 0;
            final int actual = TimeConvert.convertToTicks(fullMoonDateAtTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenFullMoonDateAndTimeSixWithTwelveTimezone_whenConvertingToTicks_calculatesTicksConsideringTimezone() {
            // Given
            final LocalDate fullMoonDate = MID_FULL_MOON_OFFSETDATETIME.toLocalDate();
            final LocalTime time = LocalTime.of(6, 0);

            final OffsetDateTime fullMoonDateAtTime = OffsetDateTime.of(fullMoonDate, time, ZoneOffset.UTC);

            // When
            final int expected = 12000;
            final OffsetDateTime offsetDateTime = fullMoonDateAtTime.withOffsetSameInstant(ZoneOffset.ofHours(12));
            final int actual = TimeConvert.convertToTicks(offsetDateTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenFullMoonDateAndTimeZero_whenConvertingToTicks_calculatesCorrectly() {
            // Given
            final LocalDate fullMoonDate = MID_FULL_MOON_OFFSETDATETIME.toLocalDate();
            final LocalTime time = LocalTime.of(0, 0);
            final OffsetDateTime fullMoonDateAtTime = OffsetDateTime.of(fullMoonDate, time, ZoneOffset.UTC);

            // When
            final int expected = 18000;
            final int actual = TimeConvert.convertToTicks(fullMoonDateAtTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenFullMoonDateAndTimeBeforeSix_whenConvertingToTicks_calculatesCorrectly() {
            // Given
            final LocalDate fullMoonDate = MID_FULL_MOON_OFFSETDATETIME.toLocalDate();
            final LocalTime time = LocalTime.of(5, 59, 59, 999_999_999);
            final OffsetDateTime fullMoonDateAtTime = OffsetDateTime.of(fullMoonDate, time, ZoneOffset.UTC);

            // When
            final int expected = 23999;
            final int actual = TimeConvert.convertToTicks(fullMoonDateAtTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenDateInFarFuture_whenConvertingToTicks_calculatesTheSame() {
            // Given
            final LocalDate date = LocalDate.of(2024, 2, 20);
            final LocalTime time = LocalTime.of(21, 53, 36);
            final OffsetDateTime dateTime = OffsetDateTime.of(date, time, ZoneOffset.UTC); // 183893
            final @Positive short cycles = 360; // Must be even to get the same time
            final long bigSeconds = Math.round(cycles * MOON_CYCLE_DURATION.toSeconds());
            final OffsetDateTime futureDateTime = dateTime.plusSeconds(bigSeconds);

            // When
            final int expected = 183893;
            final int actual = TimeConvert.convertToTicks(futureDateTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenDateInFarPast_whenConvertingToTicks_calculatesTheSame() {
            // Given
            final LocalDate date = LocalDate.of(2024, 2, 20);
            final LocalTime time = LocalTime.of(21, 53, 36);
            final OffsetDateTime dateTime = OffsetDateTime.of(date, time, ZoneOffset.UTC); // 183893
            final @Positive short cycles = 360; // Must be even to get the same time
            final long bigSeconds = Math.round(cycles * MOON_CYCLE_DURATION.toSeconds());
            final OffsetDateTime pastDateTime = dateTime.minusSeconds(bigSeconds);

            // When
            final int expected = 183893;
            final int actual = TimeConvert.convertToTicks(pastDateTime);

            // Then
            assertEquals(expected, actual);
        }

        @Test
        void givenEndFullMoonDateTime_whenConvertingToTicks_thenTicksAreCalculatedCorrectly() {
            // Given
            final Duration amountToAdd = MOON_PHASE_DURATION.dividedBy(2);
            final OffsetDateTime dateTime = MID_FULL_MOON_OFFSETDATETIME.plus(amountToAdd);

            // When

            /*
            middle phase time ticks: 11900

            days until next phase: 3.6875 / 2 = 1.84375
            we're interested only in time ticks, so: 1.84375 - 1 = 0.84375
            time ticks at this time: 0.84375 * 24000 = 20250

            unadjusted time ticks at this time: 11900 + 20250 = 32150
            adjusted time ticks at this time: 32150 mod 24000 = 8150

            we expect moon phase to become 1.
            8150 + (24000 * 1) = 32150
             */
            final int expectedTicks = 32150;
            final int actualTicks = TimeConvert.convertToTicks(dateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }

        @Test
        void givenEndedFullMoonDateTime_whenConvertingToTicks_thenTicksAreCalculatedCorrectly() {
            // Given
            final Duration amountToAdd = MOON_PHASE_DURATION.dividedBy(2);
            final OffsetDateTime dateTime = MID_FULL_MOON_OFFSETDATETIME.plus(amountToAdd).minusSeconds(1);

            // When

            /*
            middle phase time ticks: 11900

            days until next phase: 3.6875 / 2 = 1.84375
            second before next phase: 1.84375 - (1 / 60 / 60 / 24) approximately = 1.843738

            we're interested only in time ticks, so: 1.843738 - 1 = 0.843738
            time ticks at this time: 0.843738 * 24000 = 20249.712 => floored to x.0 => 20249

            unadjusted time ticks at this time: 11900 + 20249 = 32149
            adjusted time ticks at this time: 32149 mod 24000 = 8149

            we expect moon phase to remain 0.
            8149 + (24000 * 0) = 8149
             */
            final int expectedTicks = 8149;
            final int actualTicks = TimeConvert.convertToTicks(dateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }

        @Test
        void givenStartedFullMoonDateTime_whenConvertingToTicks_thenTicksAreCalculatedCorrectly() {
            // Given
            final Duration amountToAdd = MOON_PHASE_DURATION.dividedBy(2);
            final OffsetDateTime dateTime = MID_FULL_MOON_OFFSETDATETIME.minus(amountToAdd);

            // When

            /*
            middle phase time ticks: 11900

            days since this phase: 3.6875 / 2 = 1.84375

            we're interested only in time ticks, so: 1.84375 - 1 = 0.84375
            time ticks at this time: 0.84375 * 24000 = 20250

            unadjusted time ticks at this time: 11900 - 20250 = -8350
            adjusted time ticks at this time: -8350 mod 24000 = 15650

            we expect moon phase to remain 0.
            15650 + (24000 * 0) = 15650
             */
            final int expectedTicks = 15650;
            final int actualTicks = TimeConvert.convertToTicks(dateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }

        @Test
        void givenBeforeStartFullMoonDateTime_whenConvertingToTicks_thenTicksAreCalculatedCorrectly() {
            // Given
            final Duration amountToAdd = MOON_PHASE_DURATION.dividedBy(2);
            final OffsetDateTime dateTime = MID_FULL_MOON_OFFSETDATETIME.minus(amountToAdd).minusSeconds(1);

            // When

            /*
            middle phase time ticks: 11900

            days until next phase: 3.6875 / 2 = 1.84375
            second before next phase: 1.84375 - (1 / 60 / 60 / 24) approximately = 1.843738

            we're interested only in time ticks, so: 1.843738 - 1 = 0.843738
            time ticks at this time: 0.843738 * 24000 = 20249.712 => floored to x.0 => 20249

            unadjusted time ticks at this time: 11900 - 20249 = -8349
            adjusted time ticks at this time: -8349 mod 24000 = 15649

            we expect moon phase to become 7.
            15649 + (24000 * 7) = 15649 + 168000 = 183649
             */
            final int expectedTicks = 183649;
            final int actualTicks = TimeConvert.convertToTicks(dateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }
    }

    @Nested
    class RealTest {

        /**
         * Since the moon phase is not integer, the time adjustment for the moon phase is calculated.
         *
         * @param phaseNumber The phase number.
         * @return The time adjustment for the moon phase.
         */
        @IntRange(from = 0, to = 23999)
        private static short getCurrentCycleTimeAdjustmentTicks(final @IntRange(from = 0, to = 8) byte phaseNumber) {
            final OffsetTime adjustedTime = getOffsetPhaseCurrentCycleDateTimeAt(phaseNumber).toOffsetTime();
            return (short) (TimeConvert.convertToTicks(adjustedTime) % MINECRAFT_DAY_LENGTH);
        }

        /**
         * Returns the datetime of the current moon cycle in given phase number.
         * <p>
         * Special scenarios are not considered.
         *
         * @param phaseNumber The phase number.
         * @return The datetime of the current moon cycle in given phase number.
         */
        private static @NonNull OffsetDateTime getOffsetPhaseCurrentCycleDateTimeAt(final @IntRange(from = 0, to = 8) byte phaseNumber) {
            final Duration adjustment = MOON_PHASE_DURATION.multipliedBy(phaseNumber);
            return MID_FULL_MOON_OFFSETDATETIME.plus(adjustment);
        }

        @Test
        void givenMidWaningGibbousPhaseDateTime_whenConvertingToTicks_thenTicksIncludeMoonPhase() {
            // Given
            final byte phaseNumber = 1;
            final int phaseDayAdjustmentTicks = MINECRAFT_DAY_LENGTH * phaseNumber;
            final int timeAdjustmentTicks = getCurrentCycleTimeAdjustmentTicks(phaseNumber);
            final OffsetDateTime waningGibbousDateTime = getOffsetPhaseCurrentCycleDateTimeAt(phaseNumber);

            // When
            final int expectedTicks = phaseDayAdjustmentTicks + timeAdjustmentTicks;
            final int actualTicks = TimeConvert.convertToTicks(waningGibbousDateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }

        @Test
        void givenMidWaxingGibbousPhaseDateTime_whenConvertingToTicks_thenTicksIncludeMoonPhase() {
            // Given
            final byte phaseNumber = 7;
            final int phaseDayAdjustmentTicks = MINECRAFT_DAY_LENGTH * phaseNumber;
            final int timeAdjustmentTicks = getCurrentCycleTimeAdjustmentTicks(phaseNumber);
            final OffsetDateTime midWaxingGibbousDateTime = getOffsetPhaseCurrentCycleDateTimeAt(phaseNumber);

            // When
            final int expectedTicks = phaseDayAdjustmentTicks + timeAdjustmentTicks;
            final int actualTicks = TimeConvert.convertToTicks(midWaxingGibbousDateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }

        @Test
        void givenNextMidFullMoonPhaseDateTime_whenConvertingToTicks_thenTicksIncludeFirstMoonPhase() {
            // Given
            final byte phaseNumber = 8;
            final int phaseDayAdjustmentTicks = MINECRAFT_DAY_LENGTH * phaseNumber;
            final int timeAdjustmentTicks = getCurrentCycleTimeAdjustmentTicks(phaseNumber);
            final OffsetDateTime midWaxingGibbousDateTime = getOffsetPhaseCurrentCycleDateTimeAt(phaseNumber);

            // When
            final int expectedTicks = (phaseDayAdjustmentTicks + timeAdjustmentTicks) % 192000;
            final int actualTicks = TimeConvert.convertToTicks(midWaxingGibbousDateTime);

            // Then
            assertEquals(expectedTicks, actualTicks);
        }
    }
}
