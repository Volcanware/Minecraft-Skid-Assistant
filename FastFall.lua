-- Define the module table

local module = {
    on_tick = function()
        -- Check if the player's fall distance is greater than or equal to 0.5 blocks
        if player:get_fall_distance() >= 0.3 then
            -- Set the player's Y velocity to 2
            player:set_motion_y(-1.6)
        end
    end
}

modules:register("FastFall", "FastFall", module)