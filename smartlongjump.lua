local SmartLongJump = {
	on_pre_update = function()
		if module_manager.is_module_on('Speed') and module_manager.is_module_on('Scaffold') then
			player.message('.speed')
	end
end
}

module_manager.register('SmartLongJump', SmartLongJump)