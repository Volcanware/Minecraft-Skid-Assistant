package net.minecraft.command;

public class SyntaxErrorException extends CommandException {
    public SyntaxErrorException() {
        this("commands.generic.snytax");
    }

    public SyntaxErrorException(final String message, final Object... replacements) {
        super(message, replacements);
    }
}
