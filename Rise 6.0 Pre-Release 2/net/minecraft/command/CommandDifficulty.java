package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

import java.util.List;

public class CommandDifficulty extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "difficulty";
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
        return "commands.difficulty.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length <= 0) {
            throw new WrongUsageException("commands.difficulty.usage");
        } else {
            final EnumDifficulty enumdifficulty = this.getDifficultyFromCommand(args[0]);
            MinecraftServer.getServer().setDifficultyForAllWorlds(enumdifficulty);
            notifyOperators(sender, this, "commands.difficulty.success", new ChatComponentTranslation(enumdifficulty.getDifficultyResourceKey()));
        }
    }

    protected EnumDifficulty getDifficultyFromCommand(final String p_180531_1_) throws CommandException {
        return !p_180531_1_.equalsIgnoreCase("peaceful") && !p_180531_1_.equalsIgnoreCase("p") ? (!p_180531_1_.equalsIgnoreCase("easy") && !p_180531_1_.equalsIgnoreCase("e") ? (!p_180531_1_.equalsIgnoreCase("normal") && !p_180531_1_.equalsIgnoreCase("n") ? (!p_180531_1_.equalsIgnoreCase("hard") && !p_180531_1_.equalsIgnoreCase("h") ? EnumDifficulty.getDifficultyEnum(parseInt(p_180531_1_, 0, 3)) : EnumDifficulty.HARD) : EnumDifficulty.NORMAL) : EnumDifficulty.EASY) : EnumDifficulty.PEACEFUL;
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "peaceful", "easy", "normal", "hard") : null;
    }
}
