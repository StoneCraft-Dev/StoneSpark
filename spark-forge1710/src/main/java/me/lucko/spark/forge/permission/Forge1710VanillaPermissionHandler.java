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

import cpw.mods.fml.common.FMLCommonHandler;
import javax.annotation.Nonnull;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Forge1710VanillaPermissionHandler implements Forge1710PermissionHandler {
    @Override
    public boolean hasPermission(@Nonnull final ICommandSender sender,
            @Nonnull final String permission) {
        if (sender instanceof EntityPlayerMP) {
            final EntityPlayerMP player = (EntityPlayerMP) sender;

            if (!this.isOp(player)) {
                final String serverOwner = MinecraftServer.getServer().getServerOwner();

                if (player.getGameProfile().getName() != null && serverOwner != null) {
                    return serverOwner.equals(player.getGameProfile().getName());
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isOp(final EntityPlayer player) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
                .canSendCommands(player.getGameProfile());
    }
}
