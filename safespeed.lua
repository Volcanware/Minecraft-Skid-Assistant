module_manager.register("safespeed", {
	on_pre_player_input = function(event)
		if input.is_key_down(17) then
			if player.on_ground() then
				return player.jump()
			else
				return player.set_sprinting(sprinting)
			end
		end
	end,
	on_pre_update = function(event)
		if input.is_key_down(17) then
			if player.on_ground() then
				return player.set_speed(0.24)
			end
		end
	end
})

