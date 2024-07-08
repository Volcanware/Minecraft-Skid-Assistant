local motion = 1
local tick = 0

local module = {
    on_tick = function()
        if tick >= 0 then
            tick = tick + 1
        end
        if motion == 1 then
            client:set_timer_speed(0.1)
            client:message(".vclip -2.5")
            motion = 2
        elseif motion == 2 then
            client:set_timer_speed(10)
            client:message(".vclip 10")
        end
        if motion == 3 then
            client:set_timer_speed(1)
            player:set_motion_y(-0.1)
        end
        if motion == 4 then
            client:set_timer_speed(1)
            player:set_motion_y(-0.16)
        end 
        if tick >= 9 then 
            if motion == 3 or motion == 2 then
                motion = 4
            elseif motion == 4 then
                motion = 3
            end
        end
        if tick >= 9 and player:is_on_ground() then
            modules:set_state("vulcanup", false)
        end
    end;
    on_disable = function()
        motion = 1
        tick = 0
        client:set_timer_speed(1)
    end
}
modules:register("vulcanup", "vulcanup", module)