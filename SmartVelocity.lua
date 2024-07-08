angle_mappings = {
    [0]={1, 0},
    [45]={1, -1},
    [90]={0, -1},
    [135]={-1, -1},
    [180]={-1, 0},
    [225]={-1, 1},
    [270]={0, 1},
    [315]={1, 1},
    [360]={1, 0},
}


function closestInteger(a, b)
    c1 = a - (a % b)
    c2 = (a + b) - (a % b)
    if a - c1 > c2 - a then
        return c2
    else
        return c1
    end
end

function inverseAngle(angle)
    -- Returns the inverse of an agle (x-180)

    if angle >= 180 then
        return angle - 180
    else
        return angle + 180
    end
end

function getPosteriorYaws(norm_yaw)
    -- Given normalized yaw, output closest angles in order
    if norm_yaw == 360 then
        norm_yaw = 0
    end

    right = norm_yaw + 45
    left = norm_yaw - 45

    if right >= 360 then
        right = 0 --Normalizing MC fun
    end

    if left < 0 then
        left = 360 + left
    end

    return inverseAngle(left), inverseAngle(norm_yaw), inverseAngle(right)

end

function angleToBlock(cur_x, cur_z, angle, dist)
    -- Returns X,Z coords from angle table
    sim_x = (dist * angle_mappings[angle][2]) + cur_x
    sim_z = (dist * angle_mappings[angle][1]) + cur_z
    --client.print(string.format("%s %s", sim_x, sim_z))
    return sim_x, sim_z
end

function isFall(player_y, sim_x, sim_z)
    --Checks if all tiles are are between player_y to player_y - fallDist
    fallDist = module_manager.option("SmartVelocity", "fall-distance")
    player_y = player_y - 1 -- get feet

    for sim_y=player_y,player_y-fallDist, -1 do
        if world.block(sim_x, sim_y, sim_z) ~= 'tile.air' then
            return false
        end
    end

    return true
end
    

function getDistanceToVoid(l_yaw,m_yaw,r_yaw, max_dist)
    cur_x, cur_y, cur_z = player.position()
    cur_y = math.floor(cur_y)
    -- Get midpoint of block
    cur_x = math.floor(cur_x) + 0.5
    cur_z = math.floor(cur_z) + 0.5

    cur_dist = 1

    while cur_dist <= max_dist do
        if isFall(cur_y, angleToBlock(cur_x, cur_z, l_yaw, cur_dist)) then
            return cur_dist
        end
        if isFall(cur_y, angleToBlock(cur_x, cur_z, m_yaw, cur_dist)) then
            return cur_dist
        end
        if isFall(cur_y, angleToBlock(cur_x, cur_z, r_yaw, cur_dist)) then
            return cur_dist
        end

        cur_dist = cur_dist + 1
    end

    return -1
end





local smartVelocity = {
    on_enable = function()
        cur_velo = module_manager.option("Velocity", "Horizontal")
        if not module_manager.is_module_on("Velocity") then
            player.message(".velocity")
        end
    end,

    on_pre_motion = function()
        max_distance = module_manager.option("SmartVelocity", "max-distance")
        min_velo = module_manager.option("SmartVelocity", "min-velocity")
        max_velo = module_manager.option("SmartVelocity", "max-velocity")

        step = (max_velo - min_velo) / max_distance

        p_yaw, _ = player.angles()
        base_yaw = closestInteger(p_yaw%360, 45)
        l, m, r = getPosteriorYaws(base_yaw)
        post_yaw = inverseAngle(base_yaw)

        dist_to_void = getDistanceToVoid(l,m,r, max_distance)
        if dist_to_void ~= -1 then
            new_velo = math.ceil(max_velo - (step*(max_distance-dist_to_void+1)))
        else
            new_velo = max_velo
        end
        
        if new_velo ~= cur_velo then
            player.message(string.format(".Velocity Horizontal %s", new_velo))
            --client.print(string.format("Set Velo to %s", new_velo))
            cur_velo = new_velo
        end


    end

}

module_manager.register("SmartVelocity", smartVelocity)
module_manager.register_number("SmartVelocity", "max-distance", 1, 7, 5)
module_manager.register_number("SmartVelocity", "y-offset", 1, 20, 1)

module_manager.register_number("SmartVelocity", "min-velocity", 50, 90, 77)
module_manager.register_number("SmartVelocity", "max-velocity", 55, 100, 98)
module_manager.register_number("SmartVelocity", "fall-distance", 2, 10, 5)