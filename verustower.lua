local module = {
    on_move = function(event)
        if modules:is_enabled("Scaffoldwalk") then
            if client:is_key_down(17) and modules:get_setting("verustower", "ArtificialSpeed") == true then
                if player:is_on_ground() == true then
                    player:set_speed(event, modules:get_setting("verustower", "HSpeedGround") / 20)
                end
                if player:is_on_ground() == false then
                    player:set_speed(event, modules:get_setting("verustower", "HSpeedAir") / 20)
                end
            end
            if client:is_key_down(57) and player:is_on_ground() then
                player:jump()
                player:set_motion_y(modules:get_setting("verustower", "TowerHeight"))
            end
        end
    end,
    on_tick = function()
        if modules:is_enabled("Scaffoldwalk") == true and player:is_on_ground() and client:is_key_down(57) == false and client:is_key_down(17) == true then
            player:jump()
            player:set_motion_y(modules:get_setting("verustower", "ScaffoldBoost"))
        end
    end
}
modules:register("verustower", "Verus Tower(fr)", module)
modules:create_boolean_setting("verustower", "ArtificialSpeed", "Artificial Speed(H)", true)
modules:create_float_setting("verustower", "HSpeedAir", "HSpeedAir (bps)", 8, 2, 15, 0.2)
modules:create_float_setting("verustower", "HSpeedGround", "HSpeedGround (bps)", 13.6, 2, 15, 0.2)
modules:create_float_setting("verustower", "TowerHeight", "Tower Height(jump)", 1.17, 0.1, 1.5, 0.01)
modules:create_float_setting("verustower", "ScaffoldBoost", "ScaffoldBoost", 0.01, 0.01, 0.58, 0.01)