client.print('§ec§l[TowerTimer]')
client.print('towertimer by huys ty for downloading')

local towertimer = 0
local towermovetimer = 0


module_manager.register("towertimer", {
	on_pre_player_input = function(event)
        if module_manager.is_module_on('scaffold') then
            towertimer = module_manager.option('towertimer', "tower-timer-speed")
            towermovetimer = module_manager.option('towertimer', "towermove-timer-speed")
            if input.is_key_down(57) then
                if module_manager.option('scaffold', 'Tower') then
                    if input.is_key_down(17) then
                        if module_manager.option('towertimer', "towermove") then
                            world.set_timer(towermovetimer)
                        end
                    else
                        if input.is_key_down(31) then
                            if module_manager.option('towertimer', "towermove") then
                                world.set_timer(towermovetimer)
                            end
                        else
                            if module_manager.option('towertimer', "tower") then
                                world.set_timer(towertimer)
                            else
                                world.set_timer(1)
                            end
                        end
                    end
                end
            else
                world.set_timer(1)
            end
        else
		end
    end
})

module_manager.register_number('towertimer', 'tower-timer-speed', 1.01, 5.01, 1.01)
module_manager.register_number('towertimer', 'towermove-timer-speed', 1.01, 5.01, 1.01)
module_manager.register_boolean('towertimer', 'tower', false)
module_manager.register_boolean('towertimer', 'towermove', false)
