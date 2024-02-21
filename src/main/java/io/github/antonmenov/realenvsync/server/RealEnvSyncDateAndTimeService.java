package io.github.antonmenov.realenvsync.server;

import io.github.antonmenov.realenvsync.utils.TimeConvert;
import org.bukkit.Server;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

public class RealEnvSyncDateAndTimeService {

    private final Server server;

    public RealEnvSyncDateAndTimeService(final @NonNull Server server) {
        this.server = server;
    }

    public @NonNull Result handleTwoArgument(final @NonNull String worldName, final @NonNull Action action,
                                             final @NonNull Temporal temporal) {
        final @Nullable World world = server.getWorld(worldName);
        if (world == null) {
            return new Result(ResultType.INVALID_WORLD, null);
        }

        switch (action) {
            case TIME -> {
                if (temporal instanceof LocalTime parse) {
                    final int time = TimeConvert.convertToTicks(parse.atOffset(ZoneOffset.UTC));
                    world.setTime(time);
                    return new Result(ResultType.TIME_SET, time);
                }
            }
            case DATETIME -> {
                if (temporal instanceof LocalDateTime parse) {
                    final int time = TimeConvert.convertToTicks(parse.atOffset(ZoneOffset.UTC));
                    world.setFullTime(time);
                    return new Result(ResultType.DATETIME_SET, time);
                }
            }
        }
        return new Result(ResultType.INVALID_ACTION, null);
    }

    public enum Action {
        TIME, DATETIME
    }

    public enum ResultType {
        TIME_SET, DATETIME_SET, INVALID_ACTION, INVALID_WORLD
    }

    public record Result(@NonNull ResultType type, @Nullable Integer ticks) {
    }
}
