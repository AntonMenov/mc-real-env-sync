package io.github.antonmenov.realenvsync.user;

import io.github.antonmenov.realenvsync.server.RealEnvSyncDateAndTimeService;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import static io.github.antonmenov.realenvsync.server.RealEnvSyncDateAndTimeService.Action;
import static io.github.antonmenov.realenvsync.server.RealEnvSyncDateAndTimeService.Result;

public class RealEnvSyncCommand implements CommandExecutor {

    private final RealEnvSyncDateAndTimeService service;

    public RealEnvSyncCommand(final @NonNull RealEnvSyncDateAndTimeService service) {
        this.service = service;
    }

    public static @Nullable Action actionFrom(final @NonNull String name) {
        return switch (name) {
            case "time" -> Action.TIME;
            case "datetime" -> Action.DATETIME;
            default -> null;
        };
    }

    @Override
    public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command,
                             final @NonNull String label, final String @NonNull [] args) {
        if (args.length < 2 || args.length > 3) {
            return false;
        } else if (!(sender instanceof Player) && args.length == 2) {
            sender.sendMessage("Only players can use this command with 2 arguments.");
            return true;
        }

        final String actionArg = args[0];
        final @Nullable Action action = actionFrom(actionArg);
        if (action == null) {
            return false;
        }

        final @NonNull Temporal temporal;
        final String value = args[1];
        if (value.equals("now")) {
            temporal = switch (action) {
                case TIME -> LocalTime.now();
                case DATETIME -> LocalDateTime.now();
            };
        } else if (action == Action.TIME) {
            try {
                temporal = LocalTime.parse(value);
            } catch (final DateTimeParseException e) {
                sender.sendMessage("Invalid time format.");
                return true;
            }
        } else if (action == Action.DATETIME) {
            try {
                temporal = LocalDateTime.parse(value);
            } catch (final DateTimeParseException e) {
                sender.sendMessage("Invalid datetime format.");
                return true;
            }
        } else {
            return false;
        }

        if (args.length == 2) {
            final Player player = (Player) sender;
            final @NonNull World world = player.getWorld();
            return handleInput(sender, world.getName(), action, temporal);
        }

        final String worldName = args[2];
        return handleInput(sender, worldName, action, temporal);
    }

    private boolean handleInput(final @NonNull CommandSender sender, final @NonNull String worldName,
                                final @NonNull Action action, final @NonNull Temporal temporal) {
        final Result result = service.handleTwoArgument(worldName, action, temporal);
        switch (result.type()) {
            case INVALID_ACTION -> {
                sender.sendMessage("Invalid action.");
                return true;
            }
            case TIME_SET -> {
                if (temporal.with(LocalTime::from) instanceof LocalTime with) {
                    if (result.ticks() == null) {
                        throw new IllegalStateException("Ticks are null.");
                    }

                    final LocalTime time = with.truncatedTo(ChronoUnit.SECONDS);
                    sender.sendMessage("Minecraft time is set to " + result.ticks() + " for " + time);
                    return true;
                }
                throw new IllegalStateException("Unexpected state: " + temporal.getClass());
            }
            case DATETIME_SET -> {
                if (temporal.with(LocalDateTime::from) instanceof LocalDateTime with) {
                    if (result.ticks() == null) {
                        throw new IllegalStateException("Ticks are null.");
                    }

                    final LocalDateTime dateTime = with.truncatedTo(ChronoUnit.SECONDS);
                    sender.sendMessage("Minecraft time is set to " + result.ticks() + " for " + dateTime);
                    return true;
                }
                throw new IllegalStateException("Unexpected state: " + temporal.getClass());
            }
            default -> {
                return false;
            }
        }
    }
}
