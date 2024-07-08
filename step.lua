

local module = {
    
    on_tick = function(event)
        local onlyGround = modules:get_setting("step","ground");

        if onlyGround then
            if player:is_on_ground() and player:collided_horizontally() then
                local y_offset = modules:get_setting("step","offset");
                local pos = types.vec3d:new(0,0,0)
                pos.x = player:get_position_x()
                pos.y = player:get_position_y() + y_offset
                pos.z = player:get_position_z()

                player:set_position(pos)
            end
        end
        
        if not onlyGround then
            if player:collided_horizontally() then
                local y_offset = modules:get_setting("step","offset");
                local pos = types.vec3d:new(0,0,0)
                pos.x = player:get_position_x()
                pos.y = player:get_position_y() + y_offset
                pos.z = player:get_position_z()

                player:set_position(pos)
            end
        end
            

        
    end
}

-- Register
modules:register("step", "Step", module)
modules:create_float_setting("step", "offset", "Yoffset",0.75, 0.0,2.0, .25)
modules:create_boolean_setting("step", "ground", "Only on ground", true)
