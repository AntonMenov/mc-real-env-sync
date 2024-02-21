package io.github.antonmenov.realenvsync;

import io.github.antonmenov.realenvsync.server.RealEnvSyncDateAndTimeService;
import io.github.antonmenov.realenvsync.user.RealEnvSyncCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.logging.Level;

public final class RealEnvSync extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            initialize();
        } catch (final Exception e) {
            getLogger().log(Level.SEVERE, e, () -> "Error enabling RealEnvSync plugin.");
            getPluginLoader().disablePlugin(this);
        }
    }

    private void initialize() {
        registerCommand("realenvsync", new RealEnvSyncCommand(new RealEnvSyncDateAndTimeService(getServer())));
    }

    private void registerCommand(final @NonNull String commandName, final @NonNull CommandExecutor commandExecutor) {
        final PluginCommand command = Objects.requireNonNull(getCommand(commandName));
        command.setExecutor(commandExecutor);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
