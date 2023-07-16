package me.jellysquid.mods.sodium.common.walden.util;

import me.jellysquid.mods.sodium.common.walden.event.CancellableEvent;
import me.jellysquid.mods.sodium.common.walden.event.Event;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public enum MixinUtils
{
	;
	public static void fireEvent(Event<?> event)
	{
		EventManager.fire(event);
	}

	public static void fireCancellable(CancellableEvent<?> event, CallbackInfo ci)
	{
		EventManager.fire(event);
		if (event.isCancelled() && ci.isCancellable())
			ci.cancel();
	}
}
