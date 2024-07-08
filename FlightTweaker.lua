local tick = 0

local module = {
    on_enable = function(event)
        if modules:is_enabled("Flight") then
            client:message(".vclip -2")
        end
    end,

    on_tick = function()
        if tick > 0 then
            tick = tick + 1
        end
    end,

    on_update = function(event)
        if modules:is_enabled("Flight") and tick < 8 then
            client:set_timer_speed(0.2)
        else
            client:set_timer_speed(1.0)
            modules:set_state("Flight", false)
            modules:set_state("FlightTweaks", false)
            tick = 0 
        end
    end
}

modules:register("FlightTweaks", "FlightTweaks", module)
