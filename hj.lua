local tick = 1

local module = {
    on_move = function(event)
        if tick == 1 then
            player:set_motion_y(-0.10)
            tick = 2
        elseif tick == 2 then
            player:set_motion_y(-0.16)
            tick = 1
        end
    end
}

modules:register("vglide", "vglide", module)