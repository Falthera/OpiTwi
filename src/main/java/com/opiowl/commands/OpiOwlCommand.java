package com.opiowl.commands;

import com.opiowl.OpiOwlPlugin;
import com.opiowl.config.ConfigManager;
import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.OptimizationGovernor;
import com.opiowl.profiling.LagReport;
import com.opiowl.profiling.MetricsSnapshot;
import com.opiowl.profiling.Profiler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class OpiOwlCommand implements CommandExecutor, TabCompleter {
    private final OpiOwlPlugin plugin;
    private final OptimizationGovernor governor;
    private final Profiler profiler;
    private final ConfigManager configManager;

    public OpiOwlCommand(OpiOwlPlugin plugin, OptimizationGovernor governor, Profiler profiler,
                         ConfigManager configManager) {
        this.plugin = plugin;
        this.governor = governor;
        this.profiler = profiler;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("opiowl.admin")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "status":
                handleStatus(sender);
                return true;
            case "profile":
                handleProfile(sender);
                return true;
            case "lagreport":
                handleLagReport(sender);
                return true;
            case "reload":
                handleReload(sender);
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("status");
            options.add("profile");
            options.add("lagreport");
            options.add("reload");
            return options;
        }
        return List.of();
    }

    private void handleStatus(CommandSender sender) {
        Mode mode = governor.getCurrentMode();
        MetricsSnapshot snapshot = profiler.getLatestSnapshot();
        sender.sendMessage("OpiOwl Mode: " + mode);
        if (snapshot == null) {
            sender.sendMessage("No metrics collected yet.");
            return;
        }
        sender.sendMessage(String.format("TPS: %.2f MSPT: %.2f Players: %d Entities: %d Chunks: %d",
            snapshot.getTps(), snapshot.getMspt(), snapshot.getPlayerCount(), snapshot.getEntityCount(),
            snapshot.getLoadedChunkCount()));
    }

    private void handleProfile(CommandSender sender) {
        LagReport report = profiler.buildReport();
        sender.sendMessage("Profile: " + report.summaryLine());
    }

    private void handleLagReport(CommandSender sender) {
        LagReport report = profiler.buildReport();
        try {
            File reportFile = report.writeTo(plugin.getDataFolder());
            sender.sendMessage("Lag report written to " + reportFile.getName());
        } catch (Exception ex) {
            sender.sendMessage("Failed to write lag report: " + ex.getMessage());
        }
    }

    private void handleReload(CommandSender sender) {
        ConfigSnapshot snapshot = configManager.reload();
        governor.applyConfig(snapshot);
        profiler.applyConfig(snapshot);
        sender.sendMessage("OpiOwl configuration reloaded.");
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("Usage: /opiowl <status|profile|lagreport|reload>");
    }
}
