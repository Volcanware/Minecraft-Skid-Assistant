local tick = 0

local module = {
    on_packet_receive = function(event) 
        if event.packet_id == 0x12 then 
            if event.entity_id ~= player:get_id() then  
                return
            end
            if tick == 0 then
                tick = 1
            end
        end
    end;
    on_tick = function()
        if player:is_on_ground() and tick == 0 then
            player:jump()
        end
        if tick > 0 and tick < 29 then
            tick = tick + 1
        end
        if tick == 2 then
            player:jump()
            player:set_motion_y(0.52)
        end
    end;
    on_move = function(event)
        if player:is_on_ground() and tick > 2 then
            modules:set_state("vulcanlong", false)
        end
        if tick == 0 then
            player:set_speed(event, 0)
        end
        if tick > 2 and tick < 9 and player:get_motion_y() < 0.185 then
            player:jump()
            player:set_motion_y(0.52)
        end
        if (tick > 9 and tick < 25) and player:get_motion_y() < 0.25 then
            player:set_motion_y(0.61)
        end
        if tick == 28 or tick == 29 then
            tick = 30
            player:set_motion_y(-0.16)
        elseif tick == 30 then
            tick = 29
            player:set_motion_y(-0.1)
        end
    end;
    on_disable = function()
        tick = 0
    end;
    on_update = function(event)
        if event.pre and tick == 0 then
            event.pitch = -90
        end
    end
}
modules:register("vulcanlong", "vulcanlong", module)