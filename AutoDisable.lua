local tick = 0

local module = {
    on_enable = function(event)
        if modules:is_enabled("Flight") and modules:is_enabled("FlightTweaks") then
            client:message(".vclip -2")
        end
    end,

    on_tick = function(event)
        if modules:is_enabled("Flight") and modules:is_enabled("FlightTweaks") then
            tick = tick + 1
            if tick >= 8 then
                modules:set_state("Flight", false)
                modules:set_state("FlightTweaks", false)
                tick = 0
            end
        else
            tick = 0
        end
    end
}

modules:register("AutoDisable", "AutoDisable", module)
