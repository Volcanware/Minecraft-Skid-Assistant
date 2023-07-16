package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import javax.annotation.Nullable;

import dev.client.tenacity.Tenacity;
import dev.client.tenacity.config.DragManager;
import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.module.impl.render.HudMod;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.objects.Dragging;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RoundedUtil;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.DecelerateAnimation;
import dev.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat extends GuiScreen implements ITabCompleter
{
    private static final Logger LOGGER = LogManager.getLogger();
    private String historyBuffer = "";

    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private TabCompleter tabCompleter;

    /** Chat entry field */
    protected GuiTextField inputField;
    Animation resetButtonHover;
    public static Animation openingAnimation = new DecelerateAnimation(175, 1.0, Direction.BACKWARDS);
    ArraylistMod arraylistMod = (ArraylistMod) Tenacity.INSTANCE.getModuleCollection().get(ArraylistMod.class);

    /**
     * is the text that appears when you press the chat key and the input box appears pre-filled
     */
    private String defaultInputFieldText = "";

    public GuiChat()
    {
    }

    public GuiChat(String defaultText)
    {
        this.defaultInputFieldText = defaultText;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        openingAnimation = new DecelerateAnimation(175, 1.0);
        Iterator<Dragging> iterator = DragManager.draggables.values().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.resetButtonHover = new DecelerateAnimation(250, 1.0);
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(256);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
        this.tabCompleter = new GuiChat.ChatTabCompleter(this.inputField);
                return;
            }
            Dragging dragging = iterator.next();
            if (dragging.hoverAnimation.getDirection().equals((Object)Direction.BACKWARDS)) continue;
            dragging.hoverAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.tabCompleter.resetRequested();

        if (keyCode == 15)
        {
            this.tabCompleter.complete();
        }
        else
        {
            this.tabCompleter.resetDidComplete();
        }

        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 200)
            {
                this.getSentHistory(-1);
            }
            else if (keyCode == 208)
            {
                this.getSentHistory(1);
            }
            else if (keyCode == 201)
            {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            }
            else if (keyCode == 209)
            {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            }
            else
            {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        ITextComponent itextcomponent;
        if (mouseButton == 0 && this.handleComponentClick(itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY()))) {
            return;
        }
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hoveringResetButton = HoveringUtil.isHovering((float)this.width / 2.0f - 100.0f, 20.0f, 200.0f, 20.0f, mouseX, mouseY);
        if (hoveringResetButton && mouseButton == 0) {
            Iterator<Dragging> iterator = DragManager.draggables.values().iterator();
            while (iterator.hasNext()) {
                Dragging dragging2 = iterator.next();
                dragging2.setX(dragging2.initialXVal);
                dragging2.setY(dragging2.initialYVal);
            }
            return;
        }
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            if (dragging.getModule().equals(this.arraylistMod)) {
                dragging.onClickArraylist(this.arraylistMod, mouseX, mouseY, mouseButton);
            } else {
                dragging.onClick(mouseX, mouseY, mouseButton);
            }
        });
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            dragging.onRelease(state);
        });
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
        if (shouldOverwrite)
        {
            this.inputField.setText(newChatText);
        }
        else
        {
            this.inputField.writeText(newChatText);
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int msgPos)
    {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != this.sentHistoryCursor)
        {
            if (i == j)
            {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == j)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (openingAnimation.finished(Direction.BACKWARDS)) {
            this.mc.displayGuiScreen(null);
            return;
        }
        Gui.drawRect2(2.0, (double)this.height - 14.0 * openingAnimation.getOutput(), this.width - 4, 12.0, Integer.MIN_VALUE);
        this.inputField.yPosition = (int) ((double)this.height - 12.0 * openingAnimation.getOutput());
        this.inputField.drawTextBox();
        ITextComponent itextcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
            this.handleComponentHover(itextcomponent, mouseX, mouseY);
        }
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            if (dragging.getModule().equals(this.arraylistMod)) {
                dragging.onDrawArraylist(this.arraylistMod, mouseX, mouseY);
            } else {
                dragging.onDraw(mouseX, mouseY);
            }
        });
        HudMod hudMod = (HudMod)Tenacity.INSTANCE.getModuleCollection().get(HudMod.class);
        Color[] colors = hudMod.getClientColors();
        boolean hovering = HoveringUtil.isHovering((float)this.width / 2.0f - 50.0f, 20.0f, 100.0f, 20.0f, mouseX, mouseY);
        this.resetButtonHover.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        float alpha = (float)(0.5 + 0.5 * this.resetButtonHover.getOutput());
        Color color = ColorUtil.interpolateColorsBackAndForth(15, 1, colors[0], colors[1], HudMod.hueInterpolation.isEnabled());
        RoundedUtil.drawRoundOutline((float)this.width / 2.0f - 50.0f, 20.0f, 100.0f, 20.0f, 10.0f, 2.0f, new Color(40, 40, 40, (int)(255.0f * alpha)), color);
        FontUtil.tenacityBoldFont20.drawCenteredString("Reset Draggables", (float)this.width / 2.0f, 20.0f + FontUtil.tenacityBoldFont20.getMiddleOfBox(20.0f), -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Sets the list of tab completions, as long as they were previously requested.
     */
    public void setCompletions(String... newCompletions)
    {
        this.tabCompleter.setCompletions(newCompletions);
    }

    public static class ChatTabCompleter extends TabCompleter
    {
        private final Minecraft clientInstance = Minecraft.getMinecraft();

        public ChatTabCompleter(GuiTextField p_i46749_1_)
        {
            super(p_i46749_1_, false);
        }

        public void complete()
        {
            super.complete();

            if (this.completions.size() > 1)
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : this.completions)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);
                }

                this.clientInstance.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
            }
        }

        @Nullable
        public BlockPos getTargetBlockPos()
        {
            BlockPos blockpos = null;

            if (this.clientInstance.objectMouseOver != null && this.clientInstance.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                blockpos = this.clientInstance.objectMouseOver.getBlockPos();
            }

            return blockpos;
        }
    }
}
