local ticks = 0
local function is_moving()
    local input = player:get_input()
    return not (input.x == 0 and input.y == 0)
end

local module = {
    on_tick = function()
        if player:has_effect(1) and is_moving() and player:is_on_ground() and modules:is_enabled("ScaffoldWalk") == true then
            player:set_motion_speed(0.32)

            if player:is_on_ground() then
                player:set_motion_y(0.419999986886978)
            end

            if client:is_key_down(57) == true then
                player:set_motion_speed(0)

            end


        end
    end

}


modules:register("Amplifier", "Amplifier", module)