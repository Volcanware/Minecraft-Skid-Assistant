package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

import java.util.List;

public class CommandGameRule extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "gamerule";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.gamerule.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        final GameRules gamerules = this.getGameRules();
        final String s = args.length > 0 ? args[0] : "";
        final String s1 = args.length > 1 ? buildString(args, 1) : "";

        switch (args.length) {
            case 0:
                sender.addChatMessage(new ChatComponentText(joinNiceString(gamerules.getRules())));
                break;

            case 1:
                if (!gamerules.hasRule(s)) {
                    throw new CommandException("commands.gamerule.norule", s);
                }

                final String s2 = gamerules.getGameRuleStringValue(s);
                sender.addChatMessage((new ChatComponentText(s)).appendText(" = ").appendText(s2));
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gamerules.getInt(s));
                break;

            default:
                if (gamerules.areSameType(s, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1)) {
                    throw new CommandException("commands.generic.boolean.invalid", s1);
                }

                gamerules.setOrCreateGameRule(s, s1);
                func_175773_a(gamerules, s);
                notifyOperators(sender, this, "commands.gamerule.success");
        }
    }

    public static void func_175773_a(final GameRules p_175773_0_, final String p_175773_1_) {
        if ("reducedDebugInfo".equals(p_175773_1_)) {
            final byte b0 = (byte) (p_175773_0_.getGameRuleBooleanValue(p_175773_1_) ? 22 : 23);

            for (final EntityPlayerMP entityplayermp : MinecraftServer.getServer().getConfigurationManager().func_181057_v()) {
                entityplayermp.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(entityplayermp, b0));
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
        } else {
            if (args.length == 2) {
                final GameRules gamerules = this.getGameRules();

                if (gamerules.areSameType(args[0], GameRules.ValueType.BOOLEAN_VALUE)) {
                    return getListOfStringsMatchingLastWord(args, "true", "false");
                }
            }

            return null;
        }
    }

    /**
     * Return the game rule set this command should be able to manipulate.
     */
    private GameRules getGameRules() {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
}
