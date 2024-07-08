local SmartLongJump = {
	on_pre_update = function()
		if module_manager.is_module_on('Scaffold') and module_manager.is_module_on('LongJump') then
			player.message('.Scaffold')
	end
end
}

module_manager.register('SmartLongJump', SmartLongJump)