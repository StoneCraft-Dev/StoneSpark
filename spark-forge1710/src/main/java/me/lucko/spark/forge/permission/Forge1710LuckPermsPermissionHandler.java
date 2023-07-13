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

package me.lucko.spark.forge.permission;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class Forge1710LuckPermsPermissionHandler implements Forge1710PermissionHandler {

    private final LuckPerms luckPerms;

    public Forge1710LuckPermsPermissionHandler() {
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public boolean hasPermission(@Nonnull final ICommandSender sender,
            @Nonnull final String permission) {
        if (sender instanceof EntityPlayerMP) {
            return this.getUser(((EntityPlayerMP) sender).getUniqueID()).getCachedData()
                    .getPermissionData().checkPermission(permission).asBoolean();
        }

        return true;
    }

    private User getUser(final UUID uuid) {
        User user = this.luckPerms.getUserManager().getUser(uuid);

        if (user == null) {
            final UserManager userManager = this.luckPerms.getUserManager();
            final CompletableFuture<User> userFuture = userManager.loadUser(uuid);

            user = userFuture.join();
        }

        return user;
    }
}
