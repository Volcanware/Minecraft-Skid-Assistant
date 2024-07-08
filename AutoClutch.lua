--------------------------------------------------------------------------------------
-------------------------------Mappings & Constants-----------------------------------
--------------------------------------------------------------------------------------
local DIRECTIONS = {
    {0, -1, 0},
    {0, 1, 0},
    {0, 0, -1},
    {0, 0, 1},
    {-1, 0, 0},
    {1, 0, 0}
}

--------------------------------------------------------------------------------------
----------------------------General Utility Functions---------------------------------
--------------------------------------------------------------------------------------
function print(string, ...) if ... then client.print(string.format(string, ...)) else client.print(string) end end


local function generateSearchTables(range)
    searchTable = {}
    for x=-range, range do
        for y=-range, range do
            table.insert(searchTable, {x, y})
        end
    end
    return searchTable
end

local function getDistance(x1, y1, z1, x2, y2, z2)
    local x = (x2-x1)^2
    local y = (y2-y1)^2
    local z = (z2-z1)^2
    return math.sqrt(x+y+z)
end


local function distanceFromEdge(yaw)
    local function radians(angle) return angle*(math.pi/180) end
    local yaw = math.abs(yaw) % 90
    return 0.2071067812*math.sin(2*radians(yaw)) + 0.5
end

local function shallowcopy(orig)
    local orig_type = type(orig)
    local copy
    if orig_type == 'table' then
        copy = {}
        for orig_key, orig_value in pairs(orig) do
            copy[orig_key] = orig_value
        end
    else -- number, string, boolean, etc
        copy = orig
    end
    return copy
end


--------------------------------------------------------------------------------------
----------------------------Astolfo Utility Functions---------------------------------
--------------------------------------------------------------------------------------

local function debug() return module_manager.option("AutoClutch", "debug") end


local function getPlayer()
    return setmetatable({
        -- Core Attributes
        x=nil,
        y=nil,
        z=nil,
        m_x=nil,
        m_y=nil,
        m_z=nil,
        yaw=nil,
        pitch=nil,
        player=player,
        -- Essential Functions
        update = function (self)
            self.x, self.y, self.z = player.position()
            self.m_x, self.m_y, self.m_z = player.motion()
            self.yaw, self.pitch = player.angles()
        end,
        -- Utility Functions
        predictFallLocation = function(self, ticks_from)
            -- See https://minecraft.fandom.com/wiki/Transportation#Vertical_transportation
            if not ticks_from then ticks_from = 10 end
            local GRAVITY = 0.08
            local x = self.x
            local y = self.y
            local z = self.z
            local m_y = self.m_y

            for i=0, ticks_from do
                m_y = (m_y-GRAVITY)*0.98
                x = x + self.m_x
                y = y + m_y
                z = z + self.m_z
                if world.block(x, y, z) ~= 'tile.air' then break end
            end
            return x, y, z
        end,
        isOverDrop = function(self, check_dist)
            if not check_dist then check_dist = 20 end
            local rc = self:rayCast(0, 90, check_dist)

            return not rc.h_y or (self.y - rc.h_y) > check_dist
        end,

        willFall = function(self, look_ahead)
            if not look_ahead then look_ahead = 20 end

            if self.m_y > 0.1 or self.player.on_ground() then return false end -- False flag for jump up

            return world.block(self:predictFallLocation(look_ahead)) == 'tile.air'
            
        end,

        matchHotbarSlot = function(self, pattern)
            local orig_type = type(pattern)
            for i = 36, 44 do
                slot = self.player.inventory.slot(i)
                if slot then
                    if orig_type == 'table' then
                        for _, item in ipairs(pattern) do
                            if string.match(slot, item) then
                                return i-36
                            end
                        end
                    elseif orig_type == 'string' then
                        if string.match(slot, pattern) then
                            return i-36
                        end
                    end
                end
            end
        end,

        entityInFOV = function(self, entity_id, fov)
            local e_x, e_y, e_z = world.position(entity_id)
            local pitch_for_entity, _ = self.player.angles_for_cords(e_x, e_y, e_z)
            local res = math.abs(pitch_for_entity-self.pitch) % 360
            if res >= 180 then
                res = math.abs(res - 360)
            end
    
            return res <= (fov/2)
        end,
            
        -- Wrappers
        rayCast = function(self, yaw, pitch, distance)
            local res, side, b_x, b_y, b_z, h_x, h_y, h_z = self.player.ray_cast(yaw, pitch, distance)
            if res ~= 2 then return {res=1} end
            return {
                res=res,
                side=side,
                b_x=b_x,
                b_y=b_y,
                b_z=b_z,
                h_x=h_x,
                h_y=h_y,
                h_z=h_z,
                print = function (self)
                    if self.res == 1 then client.print("No hit")
                    else
                        client.print(string.format("%s %s %s %s %s %s %s",
                    self.side, self.b_x, self.b_y, self.b_z, self.h_x, self.h_y, self.h_z))
                    end
                end,
                hitting = function (self, x, y, z)
                    return (
                        math.abs(x-self.h_x) < 1 and
                        math.abs(y-self.h_y) < 1 and
                        math.abs(z-self.h_z) < 1
                    )
                end
            }
        end,

        overMouse = function(self)
            local res, side, b_x, b_y, b_z, h_x, h_y, h_z = self.player.over_mouse()
            if res ~= 2 then return {res=1} end
            return {
                res=res,
                side=side,
                b_x=b_x,
                b_y=b_y,
                b_z=b_z,
                h_x=h_x,
                h_y=h_y,
                h_z=h_z
            }
        end,
    }, 
    {
        __index = function(table, key)
            return table.player[key]
        end,
    })
end

--------------------------------------------------------------------------------------
----------------------------Astolfo Specific Functions--------------------------------
--------------------------------------------------------------------------------------


local function getReachableBlocks(searchTable, plyr, y_range)
    local function isBlockValid(b_x, b_y, b_z, plyr, distance)
        local block = world.block(b_x, b_y, b_z)
        if block == 'tile.air' or block == 'tile.water' then
            return false

        elseif distance > 4.5 or b_y >= plyr.y  then
            return false
        end

        local needed_yaw, needed_pitch = player.angles_for_cords(b_x, b_y, b_z)


        --if debug() then client.print(string.format("%s at %s, %s, %s | Time: %s", world.block(b_x, b_y, b_z), b_x, b_y, b_z, client.time())) end


        return true
    end

    local reachable_blocks = {}


    for _, offset in ipairs(searchTable) do
        for y_level=-1, -y_range, -1 do
            local b_x, b_y, b_z = plyr.x + offset[1], plyr.y + y_level, plyr.z + offset[2]
            local distance = getDistance(plyr.x, plyr.y, plyr.z, b_x, b_y, b_z)

            if isBlockValid(b_x, b_y, b_z, plyr, distance) then
                local blockInfo = {
                    x=math.floor(b_x)+0.5,
                    y=math.floor(b_y),
                    z=math.floor(b_z)+0.5,
                    ["distance"]=distance,
                    print = function(self)
                        client.print(string.format("%s %s %s", self.x, self.y, self.z))
                    end
                }
                table.insert(reachable_blocks, blockInfo)
            end
        end
    end

    return reachable_blocks
end

local function getBestBlock(reachableBlocks)
    local cur_yaw, cur_pitch = player.angles()
    local p_x, p_y, p_z = player.position()

    local function correctYaw(yaw) return ((yaw%360)+360)%360 end

    local function blockChoiceFormula(distance, angle_distance, y_distance)
        return (distance*2 + angle_distance^0) - y_distance*1.2
    end

    local function getAngleDiff(block)
        STEPS = 50
        needed_yaw, needed_pitch = player.angles_for_cords(block.x, block.y, block.z)
        return math.abs(correctYaw(needed_yaw)-correctYaw(cur_yaw)) + math.abs(cur_pitch-needed_pitch)
        -- Draw line between current and needed yaw, correct for angles differences with
        -- ((theta%360)+360)%360 (??). Iterate through angle in steps till ray cast does not
        -- give us the block
    end
        
    local largest_distance = 1000
    local best_block = nil
    for _, block in ipairs(reachableBlocks) do
        angleDiff = getAngleDiff(block)
        --client.print(string.format("%s %s %s | dist: %s",block.x, block.y, block.z, angleDiff))
        heuristic = blockChoiceFormula(block.distance, angleDiff, p_y-block.y)
        if heuristic < largest_distance then
            largest_distance = heuristic
            best_block = block
        end
    end

    return best_block
end


local function correctPitch(new_yaw, plyr, block)
    local function isHitting(over, block) return end

    local xz = math.sqrt((plyr.x - block.x)^2 + (plyr.z - block.z)^2)
    local v_dist = plyr.y - (block.y+1) + 1.62
    local block_offset = distanceFromEdge(new_yaw)
    --client.print(string.format("XZ: %s | V-Dist: %s | Edge Dist: %s", xz, v_dist, distanceFromEdge(plyr.yaw)))

    --client.print(string.format("%s %s %s", over.h_x, over.h_y, over.h_z))
    local new_pitch = math.max(0, math.atan(v_dist/(xz-block_offset))*(180/math.pi))
    local rc = plyr:rayCast(new_yaw%360, new_pitch, 10)
    while rc.res == 2 and not rc:hitting(block.x, block.y, block.z) do
        new_pitch = new_pitch + 0.1
        rc = plyr:rayCast(new_yaw, new_pitch, 10)
    end
    return new_pitch

end

--------------------------------------------------------------------------------------
-----------------------------------Main Logic-----------------------------------------
--------------------------------------------------------------------------------------


local step = 0

local autoClutch = {
    on_enable = function()
        search_table = generateSearchTables(4)
        local is_clutching = false
        local prev_slot
        local switch_slot
        local cur_side
        local cur_rc
        local block

        p = getPlayer()
    end,

    on_pre_motion = function()
        if not search_table then
            search_table = generateSearchTables(4)
        end
        
        p:update()
        if input.is_key_down(33) and not p.on_ground() and p:isOverDrop(5) then
            if step == 0 then
                -- Switch slots
                prev_slot = p.held_item_slot()
                switch_slot = p:matchHotbarSlot({'Wool', 'Clay'})
                if switch_slot ~= nil and prev_slot ~= switch_slot then
                    switch_slot = math.max(-1, switch_slot - 1)
                    p.set_held_item_slot(switch_slot)
                end
                step = 1
            end
            local reachable_blocks = getReachableBlocks(search_table, p, 4)
            local block = getBestBlock(reachable_blocks)

            -- Phase 1
            if step == 1 then
                if block then
                    -- We found a block set our angle
                    --client.print(string.format("Best Block is %s %s %s",block.x, block.y, block.z))
                    yaw, pitch = p.angles_for_cords(block.x, block.y+1, block.z)
                    
                    hold_yaw = yaw
                    pitch = correctPitch(hold_yaw, p, block)

                    p.set_angles(hold_yaw, pitch)

                    rc = p:rayCast(hold_yaw, pitch, 5)
                    step = 1
                    
                else
                    step = 0
                end
            
            end
            if step == 2 then
                cur_rc = p:rayCast(yaw, pitch, 5)
                local control_ct = 100

                if cur_rc.res == 2 then
                    if cur_rc.b_x ~= rc.b_x or cur_rc.b_z ~= rc.b_z then
                        -- We've placed a new block, start the while loop to move down
                        rc = cur_rc
                        while (rc.res == 2 and cur_rc.res == 2) and rc.side == cur_rc.side and control_ct > 0 do
                            pitch = pitch + 0.2
                            cur_rc = p:rayCast(yaw, pitch, 5)
                            control_ct = control_ct - 1
                        end
                        -- We've hit a new side at this pitch, update the angle
                        p.set_angles(hold_yaw, pitch)
                    end
                else
                    step = 1
                end
                if module_manager.option("AutoClutch", "click-mouse") then player.right_click_mouse() end
            end
            --if debug() then client.print(string.format("Found %s reachable blocks at %s", #reachable_blocks, client.time())) end
        else
            if step and step > 0 then
                --p.set_held_item_slot(prev_slot-1)--Subtract one because thanks zarzel
                p.set_angles(hold_yaw, 10)
            end
            step = 0
            cur_rc = nil
        end
    end,

    on_post_motion = function()
        if step > 0 then
            if module_manager.option("AutoClutch", "click-mouse") then player.right_click_mouse() end
        end
    end,

}


--------------------------------------------------------------------------------------
----------------------------Module Manager Registration-------------------------------
--------------------------------------------------------------------------------------


module_manager.register("AutoClutch", autoClutch)
module_manager.register_boolean("AutoClutch", "click-mouse", true)
module_manager.register_number("AutoClutch", "toggle-key", 19, 51, 33)
module_manager.register_boolean("AutoClutch", "legit", false)

module_manager.register_number("AutoClutch", "check-distance", 5, 20, 5)

module_manager.register_boolean("AutoClutch", "debug", true)