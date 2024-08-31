// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import net.minecraft.util.IChatComponent;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.command.server.CommandPublishLocalServer;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandOp;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandTeleport;

public class ServerCommandManager extends CommandHandler implements IAdminCommand
{
    public ServerCommandManager() {
        this.registerCommand(new CommandTime());
        this.registerCommand(new CommandGameMode());
        this.registerCommand(new CommandDifficulty());
        this.registerCommand(new CommandDefaultGameMode());
        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandToggleDownfall());
        this.registerCommand(new CommandWeather());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandTeleport());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandReplaceItem());
        this.registerCommand(new CommandStats());
        this.registerCommand(new CommandEffect());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandParticle());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandShowSeed());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandMessage());
        this.registerCommand(new CommandBroadcast());
        this.registerCommand(new CommandSetSpawnpoint());
        this.registerCommand(new CommandSetDefaultSpawnpoint());
        this.registerCommand(new CommandGameRule());
        this.registerCommand(new CommandClearInventory());
        this.registerCommand(new CommandTestFor());
        this.registerCommand(new CommandSpreadPlayers());
        this.registerCommand(new CommandPlaySound());
        this.registerCommand(new CommandScoreboard());
        this.registerCommand(new CommandExecuteAt());
        this.registerCommand(new CommandTrigger());
        this.registerCommand(new CommandAchievement());
        this.registerCommand(new CommandSummon());
        this.registerCommand(new CommandSetBlock());
        this.registerCommand(new CommandFill());
        this.registerCommand(new CommandClone());
        this.registerCommand(new CommandCompare());
        this.registerCommand(new CommandBlockData());
        this.registerCommand(new CommandTestForBlock());
        this.registerCommand(new CommandMessageRaw());
        this.registerCommand(new CommandWorldBorder());
        this.registerCommand(new CommandTitle());
        this.registerCommand(new CommandEntityData());
        if (MinecraftServer.getServer().isDedicatedServer()) {
            this.registerCommand(new CommandOp());
            this.registerCommand(new CommandDeOp());
            this.registerCommand(new CommandStop());
            this.registerCommand(new CommandSaveAll());
            this.registerCommand(new CommandSaveOff());
            this.registerCommand(new CommandSaveOn());
            this.registerCommand(new CommandBanIp());
            this.registerCommand(new CommandPardonIp());
            this.registerCommand(new CommandBanPlayer());
            this.registerCommand(new CommandListBans());
            this.registerCommand(new CommandPardonPlayer());
            this.registerCommand(new CommandServerKick());
            this.registerCommand(new CommandListPlayers());
            this.registerCommand(new CommandWhitelist());
            this.registerCommand(new CommandSetPlayerTimeout());
        }
        else {
            this.registerCommand(new CommandPublishLocalServer());
        }
        CommandBase.setAdminCommander(this);
    }
    
    @Override
    public void notifyOperators(final ICommandSender sender, final ICommand command, final int flags, final String msgFormat, final Object... msgParams) {
        boolean flag = true;
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        if (!sender.sendCommandFeedback()) {
            flag = false;
        }
        final IChatComponent ichatcomponent = new ChatComponentTranslation("chat.type.admin", new Object[] { sender.getCommandSenderName(), new ChatComponentTranslation(msgFormat, msgParams) });
        ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
        ichatcomponent.getChatStyle().setItalic(true);
        if (flag) {
            for (final EntityPlayer entityplayer : minecraftserver.getConfigurationManager().func_181057_v()) {
                if (entityplayer != sender && minecraftserver.getConfigurationManager().canSendCommands(entityplayer.getGameProfile()) && command.canCommandSenderUseCommand(sender)) {
                    final boolean flag2 = sender instanceof MinecraftServer && MinecraftServer.getServer().func_183002_r();
                    final boolean flag3 = sender instanceof RConConsoleSource && MinecraftServer.getServer().func_181034_q();
                    if (!flag2 && !flag3 && (sender instanceof RConConsoleSource || sender instanceof MinecraftServer)) {
                        continue;
                    }
                    entityplayer.addChatMessage(ichatcomponent);
                }
            }
        }
        if (sender != minecraftserver && minecraftserver.worldServers[0].getGameRules().getGameRuleBooleanValue("logAdminCommands")) {
            minecraftserver.addChatMessage(ichatcomponent);
        }
        boolean flag4 = minecraftserver.worldServers[0].getGameRules().getGameRuleBooleanValue("sendCommandFeedback");
        if (sender instanceof CommandBlockLogic) {
            flag4 = ((CommandBlockLogic)sender).shouldTrackOutput();
        }
        if (((flags & 0x1) != 0x1 && flag4) || sender instanceof MinecraftServer) {
            sender.addChatMessage(new ChatComponentTranslation(msgFormat, msgParams));
        }
    }
}
