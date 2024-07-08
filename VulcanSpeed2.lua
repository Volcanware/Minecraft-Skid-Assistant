local function is_moving() 
    return player:get_input().x ~= 0 
        or player:get_input().y ~= 0
end

local ticksOffGround = 0
local initialY = 0

local module = {
    on_tick = function()
    local id = player:get_id()
    ticksOffGround = ticksOffGround + 1
    if(is_moving()) then
        if(player:is_on_ground()) then
            player:set_motion_speed(player:get_speed() - .01)
            initialY = player:get_position_y()
            player:jump()
            ticksOffGround = 0
        elseif (player:get_position_y() > initialY and ticksOffGround <= 1 and world:get_block_at(world:get_position_x(id), world:get_position_y(id)-.5, world:get_position_z(id)).fullBlock) then
            local teleport = types.vec3d:new(player:get_position_x(), initialY, player:get_position_z())
            player:set_position_and_update(teleport)
        elseif (ticksOffGround == 5) then
            player:set_motion_y(-.17)
        end
        if (player:get_speed() < .215) then
            player:set_motion_speed(.215)
        end
    end
end
}
modules:register("VulcanSpeed2", "Vulcan Speed v2", module)
