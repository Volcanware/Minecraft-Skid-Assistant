package dev.client.tenacity;

import dev.client.tenacity.config.DragManager;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.ModuleCollection;
import dev.client.tenacity.ui.notifications.NotificationManager;
import dev.client.tenacity.ui.sidegui.SideGui;
import dev.client.tenacity.utils.client.ReleaseType;
import dev.client.tenacity.utils.objects.Dragging;
import dev.event.Event;
import dev.event.EventProtocol;
import dev.utils.Utils;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.command.CommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Tenacity implements Utils
{
    INSTANCE;

    public static final String NAME = "Tenacity";
    public static final String VERSION = "4.0";
    public static final ReleaseType RELEASE;
    public static final Logger LOGGER;
    public static final File DIRECTORY;
    private final EventProtocol<Event> eventProtocol = new EventProtocol();
    private final NotificationManager notificationManager = new NotificationManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SideGui sideGui = new SideGui();
    private ModuleCollection moduleCollection = new ModuleCollection();
    private CommandHandler commandHandler;

    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.PUBLIC ? " (" + RELEASE.getName() + ")" : "");
    }

    public final Color getClientColor() {
        return new Color(236, 133, 209);
    }

    public final Color getAlternateClientColor() {
        return new Color(28, 167, 222);
    }

    public SideGui getSideGui() {
        return this.sideGui;
    }

    public EventProtocol<Event> getEventProtocol() {
        return this.eventProtocol;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public ModuleCollection getModuleCollection() {
        return this.moduleCollection;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public void setModuleCollection(ModuleCollection moduleCollection) {
        this.moduleCollection = moduleCollection;
    }

    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public boolean isToggled(Class<? extends Module> c) {
        Module m = Tenacity.INSTANCE.moduleCollection.get(c);
        return m != null && m.isToggled();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }

    static {
        RELEASE = ReleaseType.BETA;
        LOGGER = LogManager.getLogger((String)NAME);
        DIRECTORY = new File(Tenacity.mc.mcDataDir, NAME);
    }
}