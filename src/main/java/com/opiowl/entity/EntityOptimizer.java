package com.opiowl.entity;

import com.opiowl.compat.ScheduledTask;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class EntityOptimizer implements Subsystem {
    private final JavaPlugin plugin;
    private final SchedulerAdapter schedulerAdapter;
    private final Set<UUID> sleepingEntities = ConcurrentHashMap.newKeySet();

    private ConfigSnapshot configSnapshot;
    private ScheduledTask task;
    private Mode mode = Mode.BALANCED;
    private boolean warnedFolia;

    public EntityOptimizer(JavaPlugin plugin, SchedulerAdapter schedulerAdapter) {
        this.plugin = plugin;
        this.schedulerAdapter = schedulerAdapter;
    }

    @Override
    public String getName() {
        return "EntityOptimizer";
    }

    @Override
    public void start() {
        scheduleTask();
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
        }
        sleepingEntities.clear();
    }

    @Override
    public void applyConfig(ConfigSnapshot config) {
        this.configSnapshot = config;
        scheduleTask();
    }

    @Override
    public void applyMode(Mode mode) {
        this.mode = mode;
    }

    private void scheduleTask() {
        if (task != null) {
            task.cancel();
        }
        if (configSnapshot == null || !configSnapshot.features().entityOptimizer()) {
            return;
        }
        int interval = configSnapshot.entity().mergeIntervalTicks();
        task = schedulerAdapter.runRepeating(this::runEntityPass, interval, interval);
    }

    private void runEntityPass() {
        if (configSnapshot == null || !configSnapshot.features().entityOptimizer()) {
            return;
        }
        if (schedulerAdapter.isFolia()) {
            if (!warnedFolia) {
                warnedFolia = true;
                plugin.getLogger().warning("EntityOptimizer is limited on Folia; skipping global entity scans.");
            }
            return;
        }
        mergeItems();
        mergeExperienceOrbs();
        sleepPassiveEntities();
    }

    private void mergeItems() {
        int maxMergePerTick = adjustedMergeBudget();
        double radius = configSnapshot.entity().mergeRadius();
        int merged = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Item item : world.getEntitiesByClass(Item.class)) {
                if (merged >= maxMergePerTick) {
                    return;
                }
                if (!item.isValid()) {
                    continue;
                }
                Collection<Entity> nearby = item.getNearbyEntities(radius, radius, radius);
                for (Entity entity : nearby) {
                    if (merged >= maxMergePerTick) {
                        return;
                    }
                    if (!(entity instanceof Item)) {
                        continue;
                    }
                    Item other = (Item) entity;
                    if (other == item || !other.isValid()) {
                        continue;
                    }
                    ItemStack stack = item.getItemStack();
                    ItemStack otherStack = other.getItemStack();
                    if (!stack.isSimilar(otherStack)) {
                        continue;
                    }
                    int maxStack = stack.getMaxStackSize();
                    int total = stack.getAmount() + otherStack.getAmount();
                    if (total <= maxStack) {
                        stack.setAmount(total);
                        item.setItemStack(stack);
                        other.remove();
                        merged++;
                    } else if (stack.getAmount() < maxStack) {
                        int needed = maxStack - stack.getAmount();
                        stack.setAmount(maxStack);
                        item.setItemStack(stack);
                        otherStack.setAmount(total - maxStack);
                        other.setItemStack(otherStack);
                        merged++;
                    }
                }
            }
        }
    }

    private void mergeExperienceOrbs() {
        int maxMergePerTick = adjustedMergeBudget();
        double radius = configSnapshot.entity().mergeRadius();
        int merged = 0;
        for (World world : Bukkit.getWorlds()) {
            for (ExperienceOrb orb : world.getEntitiesByClass(ExperienceOrb.class)) {
                if (merged >= maxMergePerTick) {
                    return;
                }
                if (!orb.isValid()) {
                    continue;
                }
                Collection<Entity> nearby = orb.getNearbyEntities(radius, radius, radius);
                for (Entity entity : nearby) {
                    if (merged >= maxMergePerTick) {
                        return;
                    }
                    if (!(entity instanceof ExperienceOrb)) {
                        continue;
                    }
                    ExperienceOrb other = (ExperienceOrb) entity;
                    if (other == orb || !other.isValid()) {
                        continue;
                    }
                    orb.setExperience(orb.getExperience() + other.getExperience());
                    other.remove();
                    merged++;
                }
            }
        }
    }

    private void sleepPassiveEntities() {
        int sleepDistance = configSnapshot.entity().sleepDistance();
        double sleepDistanceSq = sleepDistance * sleepDistance;

        for (World world : Bukkit.getWorlds()) {
            List<Player> players = world.getPlayers();
            for (Mob mob : world.getEntitiesByClass(Mob.class)) {
                if (!mob.isValid()) {
                    continue;
                }
                if (!(mob instanceof Animals) && !(mob instanceof Villager)) {
                    continue;
                }
                if (mob.getTarget() != null) {
                    wakeEntity(mob);
                    continue;
                }
                boolean nearPlayer = false;
                for (Player player : players) {
                    if (player.getLocation().distanceSquared(mob.getLocation()) <= sleepDistanceSq) {
                        nearPlayer = true;
                        break;
                    }
                }
                if (nearPlayer) {
                    wakeEntity(mob);
                } else {
                    sleepEntity(mob);
                }
            }
        }
    }

    private void sleepEntity(Mob mob) {
        UUID id = mob.getUniqueId();
        if (sleepingEntities.add(id)) {
            mob.setAI(false);
        }
    }

    private void wakeEntity(Mob mob) {
        UUID id = mob.getUniqueId();
        if (sleepingEntities.remove(id)) {
            mob.setAI(true);
        }
    }

    private int adjustedMergeBudget() {
        int base = configSnapshot.entity().maxMergePerTick();
        switch (mode) {
            case ULTRA_OPTIMIZED:
                return Math.min(base * 2, 4000);
            case EMERGENCY_LAG_RECOVERY:
                return Math.min(base * 3, 6000);
            default:
                return base;
        }
    }
}
