package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "save-all";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.save.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        sender.addChatMessage(new ChatComponentTranslation("commands.save.start"));

        if (minecraftserver.getConfigurationManager() != null) {
            minecraftserver.getConfigurationManager().saveAllPlayerData();
        }

        try {
            for (int i = 0; i < minecraftserver.worldServers.length; ++i) {
                if (minecraftserver.worldServers[i] != null) {
                    final WorldServer worldserver = minecraftserver.worldServers[i];
                    final boolean flag = worldserver.disableLevelSaving;
                    worldserver.disableLevelSaving = false;
                    worldserver.saveAllChunks(true, null);
                    worldserver.disableLevelSaving = flag;
                }
            }

            if (args.length > 0 && "flush".equals(args[0])) {
                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart"));

                for (int j = 0; j < minecraftserver.worldServers.length; ++j) {
                    if (minecraftserver.worldServers[j] != null) {
                        final WorldServer worldserver1 = minecraftserver.worldServers[j];
                        final boolean flag1 = worldserver1.disableLevelSaving;
                        worldserver1.disableLevelSaving = false;
                        worldserver1.saveChunkData();
                        worldserver1.disableLevelSaving = flag1;
                    }
                }

                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd"));
            }
        } catch (final MinecraftException minecraftexception) {
            notifyOperators(sender, this, "commands.save.failed", minecraftexception.getMessage());
            return;
        }

        notifyOperators(sender, this, "commands.save.success");
    }
}
