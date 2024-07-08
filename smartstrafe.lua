local SmartStrafe = {
	on_pre_update = function(ctx)
		if module_manager.is_module_on("Speed") or module_manager.is_module_on("HypixelSpeed") then
			player.message(".killaura Strafe Enable true")
		else
			player.message(".killaura Strafe Enable false")
		end
	end
}

module_manager.register("SmartStrafe", SmartStrafe)