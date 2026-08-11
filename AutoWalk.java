package volcanware.anarchyclef.altomenu.modules.Bot;

import volcanware.anarchyclef.altomenu.Mod;
import volcanware.anarchyclef.eventbus.EventHandler;

//Todo
// Create an experimental mode that uses Baritone to walk in a straight line in the fastest way possible
// Support Jumping Over Blocks that get in our way
// Support Using Rockets if using an Elytra
// Add Yaw/Pitch Lock
// Support Disabling module on Disconnect or Death
// Support Horses and Boats
// Support Detecting when we are against a wall for too long and toggle off the module then send a chat output saying we couldn't get past the wall
public class AutoWalk extends Mod {

    public AutoWalk() {
        super("AutoWalk", "Auto Walk", Mod.Category.BOT);
    }

    @EventHandler
    public boolean onShitTick() {
        mc.options.forwardKey.setPressed(true);
        return false;
    }

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
        super.onDisable();
    }
}
