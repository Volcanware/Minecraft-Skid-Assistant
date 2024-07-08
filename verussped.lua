module_manager.register("veruskiller", {
on_pre_motion = function()
											if player.on_ladder() then
		player.set_motion(0, 0.1, 0)
			player.set_speed(5)
										elseif player.is_in_cobweb() then
				player.set_speed(1.2)
									elseif player.is_in_water() and input.is_key_down(17) and player.collided_horizontally() then
					player.set_speed(0.25)
								elseif player.is_in_water() and input.is_key_down(17) then
						player.set_speed(0.6)
							elseif player.on_ground() and input.is_key_down(17) then
							player.set_speed(0.23)
						elseif player.collided_horizontally() and input.is_key_down(17) then
								player.set_motion(0, 0.5, 0)
					elseif not input.is_key_down(17) and not player.on_ground() then
									player.set_speed(0.32)
				elseif not input.is_key_down(17) then
										player.set_speed(0.18)
		elseif not player.on_ground() then
												player.set_speed(0.375)
end
end
})
