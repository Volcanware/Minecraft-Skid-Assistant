local ticksOffGround = 0
local islegit = true;
local id = 0

local function is_air()
    if world:get_block_at(world:get_position_x(id), world:get_position_y(id)-1.5, world:get_position_z(id)).name == "tile.air" then
        
        return true
    else
        
    return false
    end
end

local function is_moving() 
    return player:get_input().x ~= 0 
        or player:get_input().y ~= 0
end
local module = {
    on_tick = function()
        id = player:get_id()
        if is_moving() then
        if player:is_on_ground() == true then
            ticksOffGround = 0
            starting = player:get_position_y()
        else
            ticksOffGround = ticksOffGround + 1
        end
        if player:is_on_ground() == true then
        if islegit == true then
            islegit = false
        else
            islegit = true
        end
        player:set_motion_speed(.246)
        player:jump()
        elseif ticksOffGround > 3 then
            if islegit == false then
                if is_air() then
                    player:set_motion_y(-100)
                else
                    player:set_motion_y(-.27)
                end
                
            else
                player:set_motion_y(-100)
                
            end
            

        end
    end
end
}
modules:register("VulcanSpeed", "Vulcan Speed", module)
