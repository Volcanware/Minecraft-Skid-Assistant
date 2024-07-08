local ycoord = 0
local module = {
    on_tick = function()
        if modules:is_enabled("scaffoldwalk") and client:is_key_down(57) and player:is_on_ground() then
            player:set_motion_y(0.42)
        end
    end;
    on_enable = function()
        ycoord = 0
    end;
    on_move = function(event)
        if modules:is_enabled("scaffoldwalk") and player:is_on_ground() then
            ycoord = player:get_position_y()
        end
        if modules:is_enabled("scaffoldwalk") and player:is_on_ground() ~= true and (player:get_position_y() - ycoord) >= 0.44 then
            player:set_motion_y(0)
        end
        if modules:is_enabled("scaffoldwalk") and modules:get_setting("anothertower", "towermove") and client:is_key_down(57) and (client:is_key_down(17) or client:is_key_down(30) or client:is_key_down(31) or client:is_key_down(32)) then
            player:set_speed(event, modules:get_setting("anothertower", "towermovespeed"))
        elseif modules:is_enabled("scaffoldwalk") and modules:get_setting("anothertower", "towermove") == false and client:is_key_down(57) and (client:is_key_down(17) or client:is_key_down(30) or client:is_key_down(31) or client:is_key_down(32)) then
            player:set_speed(event, 0)
        end
    end
}
modules:register("anothertower", "anothertower", module) -- made by ABadName
modules:create_boolean_setting("anothertower", "towermove", "Tower Move", true)
modules:create_float_setting("anothertower", "towermovespeed", "Tower Move Speed", 0.21, 0.01, 0.5, 0.01)