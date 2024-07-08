tick = 0

local module = {
    on_packet_receive = function(event) -- skidded from utsarg
        if event.packet_id == 0x12 then -- skidded from utsarg
            if event.entity_id ~= player:get_id() then -- skidded from utsarg 
                return -- skidded from utsarg
            end -- skidded from utsarg
            tick = 1
        end
    end;
    on_tick = function()
        if tick > 0 then
            tick = tick + 1
        end
        client:print(tick)
        if tick == 2 and modules:get_setting("vulcanlj", "safe") == false then
            modules:set_state("Blink", true)
            client:set_timer_speed(3)
            player:jump()
            player:set_motion_y(0.42)
        end
        if tick == 12 then
            player:jump()
            player:set_motion_y(0.58)
        end
        if tick == 22 then
            client:set_timer_speed(0.1)
            player:jump()
            player:set_motion_y(0.58)
        end
        if tick == 23 then
            client:set_timer_speed(1)
            modules:set_state("vulcanlj", false)
            modules:set_state("Blink", false)
        end
    end;
    on_disable = function()
        client:set_timer_speed(1)
        player:set_motion_y(0)
        tick = 0
        modules:set_state("Blink", false)
    end;
    on_move = function(event)
        if tick == 0 then
            player:set_speed(event, 0)
        end
        if tick > 1 then
            player:set_sprinting(true)
        end
    end
}
modules:register("vulcanlj", "vulcanlj", module)
modules:create_boolean_setting("vulcanlj", "safe", "safe", false) -- made by ABadName, (slightly)skidded from utsarg