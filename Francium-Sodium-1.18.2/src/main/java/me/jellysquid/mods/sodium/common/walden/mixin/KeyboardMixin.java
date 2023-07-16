package me.jellysquid.mods.sodium.common.walden.mixin;

import net.minecraft.client.Keyboard;
import me.jellysquid.mods.sodium.common.walden.event.EventManager;
import me.jellysquid.mods.sodium.common.walden.event.events.KeyPressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin
{
	@Inject(at = @At("HEAD"), method = "onKey(JIIII)V", cancellable = true)
	private void onOnKey(long windowHandle, int keyCode, int scanCode,
	                     int action, int modifiers, CallbackInfo ci)
	{
		KeyPressListener.KeyPressEvent event = new KeyPressListener.KeyPressEvent(keyCode, scanCode, action, modifiers);
		EventManager.fire(event);
		if (event.isCancelled())
			ci.cancel();
	}
}