local SmartKeepY = {
	on_pre_update = function()
	value = module_manager.option("scaffold", "Keep-Y")
	space = input.is_key_down(57)
	if module_manager.is_module_on("speed") then
		if not value then
			if not space then
				player.message(".scaffold Keep-Y")
			end
		else
			if space then
				player.message(".scaffold Keep-Y")
			end
		end
	else
		if value then
			player.message(".scaffold Keep-Y")
		end
	end
	end
}
module_manager.register('SmartKeepY',SmartKeepY)
