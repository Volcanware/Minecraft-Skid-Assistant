-- Define the module table
local currentrot = 0
local newrot = 0
local module = {
    -- Define the event handler for on_tick
    on_update = function(event)
        if modules:is_enabled("killaura") and event.pre then
            event.pitch = 90
            currentrot = newrot
            event.yaw = currentrot
            newrot = currentrot + 30
        end

    end
}

-- Register the module
modules:register("derp", "derp", module)