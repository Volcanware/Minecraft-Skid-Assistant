local gticks = 0
local function moving()
    local input = player:get_input()
    return not (input.x == 0 and input.y == 0)
end

local module = {
    on_enable = function()
        gticks = 0
    end,

    on_tick = function()
        if player:is_on_ground() then
            gticks = gticks + 1
        else
            gticks = 0
        end
    end,

    on_update = function(event)
        if gticks <= modules:get_setting("shitcrits", "st") then
            if modules:get_setting("shitcrits", "st") and modules:is_enabled("Killaura") then
                if gticks >= 1 then --prevents funny fall dmg
                    event.ground = false
                end
            elseif not modules:get_setting("shitcrits", "st") then --honestly idk if this shit is even written correctly i really cant be asked tbh
                if gticks >= 1 then --prevents funny fall dmg
                    event.ground = false
                end
            end
        end
    end

}
-- Register
modules:register("shitcrits", "shitcrits", module)
modules:create_integer_setting("shitcrits", "st", "Spoof Ticks", 10, 5, 30, 1)
modules:create_boolean_setting("shitcrits", "check", "Killaura Check", true)