local ycoord = 300
local module = {
    on_tick = function()
        if modules.is_enabled("scaffoldwalk") and client.is_key_down(57) and (player.is_on_ground() or player:get_motion_y() <= modules.get_setting("versatower", "minymot")) then
            player.set_motion_y(modules.get_setting("versatower", "towermot"))
        end
    end;
    on_enable = function()
        ycoord = 300
    end;
    on_move = function(event)
        if modules.is_enabled("scaffoldwalk") and player.is_on_ground() then
            ycoord = player.get_position_y()
        end
        if modules.is_enabled("scaffoldwalk") and not player.is_on_ground() and (player.get_position_y() - ycoord) >= modules.get_setting("versatower", "maxdeltay") and client.is_key_down(57) and modules.get_setting("versatower", "setymoti") then
            player.set_motion_y(modules.get_setting("versatower", "setymot"))
        end
        if modules.is_enabled("scaffoldwalk") and client.is_key_down(57) and (client.is_key_down(17) or client.is_key_down(30) or client.is_key_down(31) or client.is_key_down(32)) then
            player.set_speed(event, modules.get_setting("versatower", "towermovespeed"))
        end
    end
}
modules.register("versatower", "VersaTower", module) -- made by ABadName
modules.create_float_setting("versatower", "towermovespeed", "Tower Move Speed", 0.21, 0, 0.5, 0.005)
modules.create_float_setting("versatower", "towermot", "Tower Motion", 0.42, 0.05, 1, 0.01)
modules.create_float_setting("versatower", "minymot", "Min Y Motion", -0.18, -1, 1, 0.01)
modules.create_boolean_setting("versatower", "setymoti", "Set Y Motion", true)
modules.create_float_setting("versatower", "setymot", "Set Y Motion", -0.1, -1, 1, 0.01)
modules.create_float_setting("versatower", "maxdeltay", "Max Delta Y(coord based)", 0.43, 0, 2, 0.02)