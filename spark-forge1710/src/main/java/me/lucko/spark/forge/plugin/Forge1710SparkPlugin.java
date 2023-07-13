/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.forge.plugin;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.forge.Forge1710CommandSender;
import me.lucko.spark.forge.Forge1710SparkMod;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Forge1710SparkPlugin implements SparkPlugin, ICommand {

    protected final ScheduledExecutorService scheduler;
    protected final SparkPlatform platform;
    private final Forge1710SparkMod mod;
    private final Logger logger;

    protected Forge1710SparkPlugin(final Forge1710SparkMod mod) {
        this.mod = mod;
        this.logger = LogManager.getLogger("spark");
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("spark-forge-async-worker");
            thread.setDaemon(true);
            return thread;
        });
        this.platform = new SparkPlatform(this);
    }

    public void enable() {
        this.platform.enable();
    }

    public void disable() {
        this.platform.disable();
        this.scheduler.shutdown();
    }

    public abstract boolean hasPermission(ICommandSender sender, String permission);

    @Override
    public String getVersion() {
        return this.mod.getVersion();
    }

    @Override
    public Path getPluginDirectory() {
        return this.mod.getConfigDirectory();
    }

    @Override
    public void executeAsync(final Runnable task) {
        this.scheduler.execute(task);
    }

    @Override
    public void log(final Level level, final String msg) {
        if (level == Level.INFO) {
            this.logger.info(msg);
        } else if (level == Level.WARNING) {
            this.logger.warn(msg);
        } else if (level == Level.SEVERE) {
            this.logger.error(msg);
        } else {
            throw new IllegalArgumentException(level.getName());
        }
    }

    // implement ICommand

    @Override
    public String getCommandName() {
        return this.getCommandName();
    }

    @Override
    public String getCommandUsage(final ICommandSender iCommandSender) {
        return "/" + this.getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList(this.getCommandName());
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) {
        this.platform.executeCommand(new Forge1710CommandSender(sender, this), args);
    }

    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args) {
        return this.platform.tabCompleteCommand(new Forge1710CommandSender(sender, this), args);
    }

    @Override
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return this.platform.hasPermissionForAnyCommand(new Forge1710CommandSender(sender, this));
    }

    @Override
    public boolean isUsernameIndex(final String[] strings, final int i) {
        return false;
    }

    @Override
    public int compareTo(final Object o) {
        return this.getCommandName().compareTo(((ICommand) o).getCommandName());
    }
}
