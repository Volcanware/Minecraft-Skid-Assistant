local module = {
    on_move = function(event)
        if modules:is_enabled("scaffoldwalk") and client:is_key_down(57) then
            client:set_timer_speed(modules:get_setting("simpletower", "timer"))
            if player:get_motion_y() < modules:get_setting("simpletower", "minmotion") then
                if modules:get_setting("simpletower", "groundcheck") == true and player:is_on_ground() == false then
                    return
                end
                player:set_motion_y(modules:get_setting("simpletower", "jumpheight"))
            end
            if modules:get_setting("simpletower", "towermove") and (client:is_key_down(17) or client:is_key_down(30) or client:is_key_down(31) or client:is_key_down(32)) then
                player:set_speed(event, modules:get_setting("simpletower", "towermovespeed"))
            else
                player:set_speed(event, 0)
            end
        end
        if client:is_key_down(57) == false then
            client:set_timer_speed(1)
        end
    end

}
modules:register("simpletower", "simpletower", module) -- made by ABadName
modules:create_boolean_setting("simpletower", "towermove", "Tower Move", true)
modules:create_float_setting("simpletower", "towermovespeed", "Tower Move Speed", 0.21, 0.01, 1, 0.01)
modules:create_float_setting("simpletower", "timer", "Timer", 1, 0.1, 2, 0.05)
modules:create_float_setting("simpletower", "jumpheight", "Tower Motion", 0.42, 0.3, 1, 0.01)
modules:create_float_setting("simpletower", "minmotion", "Min Y Motion to Tower", 0.1, -0.05, 0.5, 0.01)
modules:create_boolean_setting("simpletower", "groundcheck", "Ground Check", false) -- by ABadName 