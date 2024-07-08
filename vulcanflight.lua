local tick = -1
local module = {
    on_enable = function(event)
        if not player:is_on_ground() or string.match(world:get_block_at(player:get_position_x(), player:get_position_y()-0.4, player:get_position_z()).name, "air") then
            client:print("You need to be on the ground to fly.")
            modules:set_state("VulcanFlight", false)
            return
        end
        client:message(".vclip -2.5")
        
        tick = 0
    end,
    on_tick = function()
        if tick < -1 and player:is_on_ground() then
            modules:set_state("vulcanflight", false)
        end
        if tick >= 0 then
            tick = tick + 1
        end
        if tick == -2 then
            player:set_motion_y(-0.1)
            tick = -3
        elseif tick == -3 then
            player:set_motion_y(-0.16)
            tick = -2
        end
    end,
    on_move = function(event)
        if tick > 0 and tick <= 8 then
            player:set_speed(event, 7)
            player:set_motion_y(7)
        elseif tick > 8 then
            tick = -2
        end
    end
}
modules:register("VulcanFlight", "Vulcan Flight", module)