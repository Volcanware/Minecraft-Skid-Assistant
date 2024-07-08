-- made by xzto

local name = "Bridger"

http.load("https://raw.githubusercontent.com/CluePortal/ZarScriptHelper/main/MoreFunctions.lua")
local blockX, blockY, blockZ, blockFacing
local hitX, hitY, hitZ
local oldSlot
local swapped = false
local lastMS = client.time()
local swapdelay = 0
local startYLevel = 0
function set_module_state(mod, state)
    if module_manager.is_module_on(mod) ~= state then
        player.message("." .. mod)
    end
end

local directions = {
    { facing = 1, x = 0, y = -1, z = 0 },
    { facing = 2, x = 0, y = 1, z = 0 },
    { facing = 3, x = 0, y = 0, z = -1 },
    { facing = 4, x = 0, y = 0, z = 1 },
    { facing = 5, x = -1, y = 0, z = 0 },
    { facing = 6, x = 1, y = 0, z = 0 }
}
function getBlockBoundingBox(posX, posY, posZ)
    maxY = posY + 1
    minY = posY
    maxX = posX + 1
    maxZ = posZ + 1
    minX = posX
    minZ = posZ
    return minX, minY, minZ, maxX, maxY, maxZ
end
local red = 0
local green = 0
local blue = 0
local stage = 1
local rainbow = 1
function renderOutline(minX, minY, minZ, maxX, maxY, maxZ, sc)
    px, py, pz = player.camera_position()
    dMinX = minX-px
    dMinY = minY-py
    dMinZ = minZ-pz
    dMaxX = maxX-px
    dMaxY = maxY-py
    dMaxZ = maxZ-pz
    
    x1, y1, z1 = render.world_to_screen(dMaxX,dMinY,dMinZ)
    x2, y2, z2 = render.world_to_screen(dMaxX,dMaxY,dMinZ)
    x3, y3, z3 = render.world_to_screen(dMinX,dMinY,dMaxZ)
    x4, y4, z4 = render.world_to_screen(dMinX,dMaxY,dMaxZ)
    x5, y5, z5 = render.world_to_screen(dMinX,dMinY,dMinZ)
    x6, y6, z6 = render.world_to_screen(dMinX,dMaxY,dMinZ)
    x7, y7, z7 = render.world_to_screen(dMaxX,dMinY,dMaxZ)
    x8, y8, z8 = render.world_to_screen(dMaxX,dMaxY,dMaxZ)
    
    render.scale(1/sc.scale_factor)
    
    lineWidth = 2
    if z1<1 and z2<1 and z3<1 and z4<1 and z5<1 and z6<1 and z7<1 and z8<1 then
        render.line(x1,y1,x2,y2,lineWidth,red, green, blue,255)
        render.line(x3,y3,x4,y4,lineWidth,red, green, blue,255)
        render.line(x5,y5,x6,y6,lineWidth,red, green, blue,255)
        render.line(x7,y7,x8,y8,lineWidth,red, green, blue,255)
        render.line(x1,y1,x5,y5,lineWidth,red, green, blue,255)
        render.line(x2,y2,x6,y6,lineWidth,red, green, blue,255)
        render.line(x3,y3,x7,y7,lineWidth,red, green, blue,255)
        render.line(x4,y4,x8,y8,lineWidth,red, green, blue,255)
        render.line(x5,y5,x3,y3,lineWidth,red, green, blue,255)
        render.line(x6,y6,x4,y4,lineWidth,red, green, blue,255)
        render.line(x7,y7,x1,y1,lineWidth,red, green, blue,255)
        render.line(x8,y8,x2,y2,lineWidth,red, green, blue,255)
    end
    
    render.scale(sc.scale_factor)
end

function wrapAngleTo180(value)
    value = value % 360.0;
    if (value >= 180.0) then
        value = value - 360.0;
    end
    if (value < -180.0) then
        value = value + 360.0;
    end
    return value;
end

function fixGCD(rotation)
    return fixedRot
end
function grabBlockSlot()
    local slot = -1
    local highestStack = -1
    local didGetHotbar = false
    for i = 1, 9 do
        local name = player.inventory.item_information(35 + i)
        local size = player.inventory.get_item_stack(35 + i)           
        if size ~= nil and name ~= nil and string.match(name, "tile") and size > 0 then
            if size > highestStack then
                highestStack = size 
                slot = i
                if slot == getLastHotbar then
                    didGetHotbar = true
                end
            end
        end
    end
    return slot
end
local starty = 0
local invalidBlocks = {
    "tile.sand",
    "tile.gravel",
    "tile.anvil",
    "tile.chest",
    "tile.enderChest",
    "tile.anvil",
    "tile.dropper",
    "tile.ladder",
    "tile.dispenser",
    "Rail",
    "tile.rail",
    "tile.sapling",
    "tile.web",
    "tile.workbench",
    "tile.furnace",
    "tile.cactus",
    "tile.jukebox",
    "tile.fenceIron",
    "tile.thinGlass",
    "Gate",
    "tile.doublePlant",
    "Fence",
    "fence",
    "gate",
    "tile.deadbush",
    "tile.flower2",
    "tile.musicBlock",
    "tile.mushroom",
    "slab",
    "tile.torch",
    "tile.notGate",
    "tile.lever",
    "tile.button",
    "pressurePlate",
    "weightedPlate",
    "stairs",
    "tile.hopper",
    "tile.endPortalFrame",
    "tile.enchantmentTable",
    "tile.tripWireSource",
    "tile.waterlily",
    "tile.vine",
    "tile.daylightDetector",
    "tile.woolCarpet",
    "tile.trapdoor",
    "tile.ironTrapdoor",
    "tile.banner"
}

function canPlaceBlock(heldItemName)
    if heldItemName ~= nil then
        for i = 1, #invalidBlocks do
            local block = invalidBlocks[i]
            if string.find(heldItemName, block) or heldItemName == "tile.clay" then
                return false
            end
        end
    end
    return true
end

function getTotalBlocks()
    if client.gui_name() == "none" then
        local total_blocks = 0
        for slot_id = 5, 44 do
            local name = player.inventory.item_information(slot_id)
            if name ~= nil and string.match(name, "tile") and canPlaceBlock(name) then
                local item_count = player.inventory.get_item_stack(slot_id)
                total_blocks = total_blocks + item_count
            end
        end
        return total_blocks
    end
end

function offSetPos(x, y, z, facing) 
    if not module_manager.option(name, "KeepY-Sprint") or input.is_key_down(57) then
        if facing == 1 then
            return x, y - 1, z
        elseif facing == 2 then
            return x, y + 1, z
        elseif facing == 3 then
            return x, y, z - 1
        elseif facing == 4 then
            return x, y, z + 1
        elseif facing == 5 then
            return x - 1, y, z
        elseif facing == 6 then
            return x + 1, y, z
        end
    else
        if facing == 1 then
            return x, starty - 1, z
        elseif facing == 2 then
            return x, starty + 1, z
        elseif facing == 3 then
            return x, starty, z - 1
        elseif facing == 4 then
            return x, starty, z + 1
        elseif facing == 5 then
            return x - 1, starty, z
        elseif facing == 6 then
            return x + 1, starty, z
        end
    end
end

function toOpposite(facing)
    if facing == 1 then
        return 2
    elseif facing == 2 then
        return 1
    elseif facing == 3 then
        return 4
    elseif facing == 4 then
        return 3
    elseif facing == 5 then
        return 6
    elseif facing == 6 then
        return 5
    end
end
function getEyePosition()
    local x, y, z = player.position()
    return x, y + player.eye_height(), z
end
local airticks = 0
local gticks = 0
function isMoving()
	if input.is_key_down(17) or input.is_key_down(30) or input.is_key_down(31) or input.is_key_down(32) then
		return true
	else
		return false
	end
end
function block()
    local n, v, x, y, z = player.over_mouse()
    if n == 2 then
        return v, x, y, z
    end
    return nil
end
function ExtrafindBlocks()
    local enumFacings = {
        down = 1,
        up = 2,
        north = 3,
        south = 4,
        west = 5,
        east = 6
    }
    local rawX, rawY, rawZ = player.position()
    local x, y, z = math.floor(rawX), math.floor(rawY), math.floor(rawZ)
    if world.block(x, y - 1, z) == "tile.air" then
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX, offSetY, offSetZ = offSetPos(x, y - 1, z, enumFacing)
                if world.block(offSetX, offSetY, offSetZ) ~= "tile.air" then
                    return offSetX, offSetY, offSetZ, toOpposite(enumFacing)
                end
            end
        end
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX1, offSetY1, offSetZ1 = offSetPos(x, y - 1, z, enumFacing)
                if world.block(offSetX1, offSetY1, offSetZ1) == "tile.air" then
                    for _, enumFacing2 in pairs(enumFacings) do
                        if enumFacing2 ~= 2 then
                            local offSetX2, offSetY2, offSetZ2 = offSetPos(offSetX1, offSetY1, offSetZ1, enumFacing2)
                            if world.block(offSetX2, offSetY2, offSetZ2) ~= "tile.air" then
                                return offSetX2, offSetY2, offSetZ2, toOpposite(enumFacing2)
                            end
                        end
                    end
                end
            end
        end
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX1, offSetY1, offSetZ1 = offSetPos(x, y - 2, z, enumFacing)
                if world.block(offSetX1, offSetY1, offSetZ1) == "tile.air" then
                    for _, enumFacing2 in pairs(enumFacings) do
                        if enumFacing2 ~= 2 then
                            local offSetX2, offSetY2, offSetZ2 = offSetPos(offSetX1, offSetY1, offSetZ1, enumFacing2)
                            if world.block(offSetX2, offSetY2, offSetZ2) ~= "tile.air" then
                                return offSetX2, offSetY2, offSetZ2, toOpposite(enumFacing2)
                            end
                        end
                    end
                end
            end
        end
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX1, offSetY1, offSetZ1 = offSetPos(x, y - 3, z, enumFacing)
                if world.block(offSetX1, offSetY1, offSetZ1) == "tile.air" then
                    for _, enumFacing3 in pairs(enumFacings) do
                        if enumFacing3 ~= 2 then
                            local offSetX3, offSetY3, offSetZ3 = offSetPos(offSetX1, offSetY1, offSetZ1, enumFacing3)
                            if world.block(offSetX3, offSetY3, offSetZ3) ~= "tile.air" then
                                return offSetX3, offSetY3, offSetZ3, toOpposite(enumFacing3)
                            end
                        end
                    end
                end
            end
        end
    end
end
function findBlocks(ylvl)
    local enumFacings = {
        down = 1,
        up = 2,
        north = 3,
        south = 4,
        west = 5,
        east = 6
    }
    local rawX, rawY, rawZ = player.position()
    local x, y, z = math.floor(rawX), math.floor(rawY), math.floor(rawZ)
    if world.block(x, ylvl, z) == "tile.air" then
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX, offSetY, offSetZ = offSetPos(x, ylvl, z, enumFacing)
                if world.block(offSetX, offSetY, offSetZ) ~= "tile.air" then
                    return offSetX, offSetY, offSetZ, toOpposite(enumFacing)
                end
            end
        end
        for _, enumFacing in pairs(enumFacings) do
            if enumFacing ~= 2 then
                local offSetX1, offSetY1, offSetZ1 = offSetPos(x, ylvl, z, enumFacing)
                if world.block(offSetX1, offSetY1, offSetZ1) == "tile.air" then
                    for _, enumFacing2 in pairs(enumFacings) do
                        if enumFacing2 ~= 2 then
                            local offSetX2, offSetY2, offSetZ2 = offSetPos(offSetX1, offSetY1, offSetZ1, enumFacing2)
                            if world.block(offSetX2, offSetY2, offSetZ2) ~= "tile.air" then
                                return offSetX2, offSetY2, offSetZ2, toOpposite(enumFacing2)
                            end
                        end
                    end
                end
            end
        end
    end
end

function getRotationsToPos(sX, sY, sZ, eX, eY, eZ)
    local d0 = eX - sX
    local d1 = eY - sY
    local d2 = eZ - sZ
    local d3 = math.sqrt(d0 * d0 + d2 * d2)
    local f = (math.atan2(d2, d0) * 180 / math.pi) - 90
    local f1 = (-(math.atan2(d1, d3) * 180 / math.pi))
    return f, f1
end

function DistanceToGround()
    for i = 1, 20 do
        local x,y,z = player.position()
        if not player.on_ground() and world.block(x, y - (i / 10), z) ~= "tile.air" then
            return (i / 10)
        end
    end
end

function getDirection()
    local yaw, pitch = player.angles()
    --client.print(yaw, pitch)
	local forward, strafing = player.strafe()
    if forward == 0 and strafing == 0 then
        return yaw
    end
    local strafingYaw
    local reversed = forward < 0
    if forward > 0 then
        strafingYaw = 90 * 0.5
    elseif reversed then
        strafingYaw = 90 * -0.5
    else
        strafingYaw = 90 * 1
    end
    if reversed then
        yaw = yaw + 180
    end
    
    if strafing > 0 then
        yaw = yaw - strafingYaw
    elseif strafing < 0 then
        yaw = yaw + strafingYaw
    end
    return yaw
end

function getCoord(facing, coord)
    for _, dir in ipairs(directions) do
        if dir.facing == facing then
            if coord == "x" then
                return dir.x
            elseif coord == "y" then
                return dir.y
            elseif coord == "z" then
                return dir.z
            end
        end
    end
end
function renderBlockOutline(event, x, y, z)
	local minX, minY, minZ, maxX, maxY, maxZ = renderHelper.getBlockBoundingBox(x, y, z)
	renderHelper.renderOutline(minX, minY, minZ, maxX, maxY, maxZ, event, red, green, blue, 255, 5)
end

function renderBlockOutlineYellow(event, x, y, z)
	local minX, minY, minZ, maxX, maxY, maxZ = renderHelper.getBlockBoundingBox(x, y, z)
	renderHelper.renderOutline(minX, minY, minZ, maxX, maxY, maxZ, event, red, green, blue, 255, 5)
end

local airticks = 0
local switched = false
local switch = false
local hx, hz = 0, 0
local px,py,pz = 0, 0, 0
local ox, oy, oz = 0, 0, 0
local blok1 = 0
local blok2 = 0
local blok3 = 0
local i = 1
local rot2_Yaw, rot2_Pitch = 0, 0
local changed = false
local started = 0
local rand = false

function place()
    blockX, blockY, blockZ, blockFacing = findBlocks(startYLevel)
    if blockX == nil or blockY == nil or blockZ == nil or blockFacing == nil then return end
    local x,y,z = player.position()
    hx, hz = player.angles()
    rot2_Yaw, rot2_Pitch = player.angles_for_cords(blockX, blockY + module_manager.option(name, "Aim"), blockZ)
    i = i + 1
    changed = true
    if math.random() > 0.9 then
        rand = true
    else
        rand = false
    end
    if module_manager.option(name, "Rotations") == 3 then
        player.message(".KeepYScaffold Aim "..tostring(-math.floor(math.random() * 5)))
    end
    player.swing_item()
    player.place_block(player.held_item_slot(), blockX, blockY, blockZ, blockFacing, (blockX + 0.5) + getCoord(blockFacing, "x") * 0.5, (blockY + 0.5) + getCoord(blockFacing, "y") * 0.5, (blockZ + 0.5) + getCoord(blockFacing, "z") * 0.5)
end

function ExtraPlace()
    blockX, blockY, blockZ, blockFacing = ExtrafindBlocks()
    if blockX == nil or blockY == nil or blockZ == nil or blockFacing == nil then return end
    local x,y,z = player.position()
    hx, hz = player.angles()
    rot2_Yaw, rot2_Pitch = player.angles_for_cords(blockX, blockY + module_manager.option(name, "Aim"), blockZ)
    i = i + 1
    if module_manager.option(name, "Rotations") == 3 then
        player.message(".KeepYScaffold Aim "..tostring(-math.floor(math.random() * 5)))
    end
    if math.random() > 0.9 then
        rand = true
    else
        rand = false
    end
    if not player.on_ground() then
        started = started + 1
    end
    player.swing_item()
    player.place_block(player.held_item_slot(), blockX, blockY, blockZ, blockFacing, (blockX + 0.5) + getCoord(blockFacing, "x") * 0.5, (blockY + 0.5) + getCoord(blockFacing, "y") * 0.5, (blockZ + 0.5) + getCoord(blockFacing, "z") * 0.5)
end

function isMoving()
	if input.is_key_down(17) or input.is_key_down(30) or input.is_key_down(31) or input.is_key_down(32) then
		return true
	else
		return false
	end
end

local ongroundticks = 0
local offgroundticks = 0
local multi = 0.94
local placed = false
local ticks = 0
local offground = 0
local vertical = false
local no = false
local verticalticks = 0
local towers = false
local set = false
local posticks = 0
local places = 0
local sticks = 0
local space = 0
local ongroundticks = 0
local offgroundticks = 0
local wait = 0
local stop = false
local r = 0
local rend = false
local flags = 0
local timse = 0
local sett = false
local o = 0
local startx,starty,startz = 0, 0, 0
local gx, gy, gz = player.position()
local temp = 0
local a = 0
local index = 0
local g = 0
local allah = 8
local amount = "0"

module_manager.register(name, {
    on_render_screen = function(t)
        if stage == 1 then
            if green < 255 then
                green = green + rainbow
            else
                stage = 2
            end
        elseif stage == 2 then
            if red > 0 then
                red = red - rainbow
            else
                stage = 3
            end
        elseif stage == 3 then
            if blue < 255 then
                blue = blue + rainbow
            else
                stage = 4
            end
        elseif stage == 4 then
            if green > 0 then
                green = green - rainbow
            else
                stage = 5
            end
        elseif stage == 5 then
            if red < 255 then
                red = red + rainbow
            else
                stage = 6
            end
        elseif stage == 6 then
            if blue > 0 then
                blue = blue - rainbow
            else
                stage = 1
            end
        end
        if player.inventory.get_item_stack(35 + player.held_item_slot()) ~= nil then
            if player.inventory.get_item_stack(35 + player.held_item_slot()) > 32 then
                vari = "\194\167a"
            elseif player.inventory.get_item_stack(35 + player.held_item_slot()) < 32 and player.inventory.get_item_stack(35 + player.held_item_slot()) > 16 then
                vari = "\194\1676"
            elseif player.inventory.get_item_stack(35 + player.held_item_slot()) < 16 then
                vari = "\194\167c"
            end
            amount = tostring(player.inventory.get_item_stack(35 + player.held_item_slot()))
        else
            amount = "0"
            vari = "\194\167c"
        end
        text = vari..amount.." \194\167fBlocks"
        if module_manager.option(name, "Block-Counter") then
            render.string_shadow(text,t.width / 2 - render.get_string_width(text) / 2 + 47, t.height / 2 - 3 , red,green,blue,255)
        end
    end,
    on_pre_motion = function(t)
        local towerx,towery,towerz = player.motion()
        player.set_sprinting(true)
        if player.on_ground() then
            extra = false
            if not input.is_key_down(57) and isMoving() then
                player.jump()
                player.set_speed(player.get_speed())
                if module_manager.option(name,"Low-Hop") and started > 2 then
                    t.y = t.y + 10^-14
                end
            end
            airticks = 0
        else
            airticks = airticks + 1
        end
        local px, py, pz = player.position()
        local ox, oy, oz = player.prev_position()
        if input.is_key_down(57) then
            space = 1
            if math.floor(py - 2) > startYLevel then
                startYLevel = math.floor(py - 2)
            end
        elseif space == 1 then
            space = 2
            if space == 2 then
                player.set_speed(0)
                started = 0
                space = 0
            end
        end
        if py < startYLevel then
            startYLevel = math.floor(py - 2)
        end
        local mx, my, mz = player.motion()
        local slot = grabBlockSlot()
        if slot == -1 then return end
        if swap then
            swapdelay = swapdelay + 1
        end
        if swapdelay >= 3 then
            player.set_held_item_slot(slot - 2)
            swapped = true
            swapdelay = 0
        end
        if module_manager.option(name, "Proxy") and input.is_key_down(57) then
            towering = true
            slow = 0
        end
        if module_manager.option(name, "Rotations") == 1 then
            t.yaw = getDirection() - 180
            if input.is_key_down(57) then
                t.pitch = 83
            else
                t.pitch = 80
            end
        elseif module_manager.option(name, "Rotations") == 2 then
            rand = false
            if rand then
                t.yaw = getDirection() - 180 + (rot2_Yaw * 0.01)
            else
                t.yaw = getDirection() - 180 - (rot2_Yaw * 0.01)
            end
            t.pitch = rot2_Pitch
        elseif module_manager.option(name, "Rotations") == 3 then
            if rand then
                t.yaw = getDirection() - 180 + (rot2_Yaw * 0.01)
            else
                t.yaw = getDirection() - 180 - (rot2_Yaw * 0.01)
            end
            t.pitch = rot2_Pitch
        end
        if player.hurt_time() == 0 and player.on_ground() then
            player.set_motion(tx,0,tz)
        end
        return t
    end,

    on_enable = function()
        started = 0
        local px, py, pz = player.position()
        startYLevel = math.floor(py - 2)
        --set_module_state("scaffold", true)
        set_module_state("antifireball",false)
        set_module_state("Sprint",false)
        px,py,pz = player.position()
        starty = py
        swapped = false
        sett = false
        flags = 20
        r = 0
        rend = false
        local yaw, pitch = player.angles()
        oldSlot = player.held_item_slot()
        local startslot = grabBlockSlot()
        local slot = grabBlockSlot()
        if slot == -1 then return end
        player.set_held_item_slot(slot - 2)
        swapped = true
        gticks = 0
    end,

    on_pre_update = function(t)
        if started < 1 then
            if player.on_ground() then
                player.jump()
                startYLevel = startYLevel + 1
                started = 0
            end
            ExtraPlace()
        else
            if not input.is_key_down(57) then
                place()
            else
                ExtraPlace()
            end
            if DistanceToGround() ~= nil then
                if DistanceToGround() < 1.5 and not extra and player.fall_distance() > 0 then
                    ExtraPlace()
                    if player.on_ground() then
                        extra = true
                    end
                end
            end
        end
        px,py,pz = player.position()
        if player.hurt_time() > 0 then
            groundcheck = true
            gticks = 0
            offground = false
            ground = true
        end
    end,

    on_disable = function()
        set_module_state("Sprint",true)
        --set_module_state("scaffold", false)
        player.set_held_item_slot(oldSlot - 2)
        towering = false
        hasGoneUp = false
        swapdelay = 0
    end,

    on_player_move = function(t)
        if started < 1 and module_manager.option(name,"Safety") then
            player.set_motion(0,t.y,0)
        --elseif player.is_potion_active(1) or input.is_key_down(57) then
        else
            if player.is_potion_active(1) then
                player.set_motion(t.x * ((module_manager.option(name,"Speed") - 5) / 1000 ), t.y, t.z * ((module_manager.option(name,"Speed") - 5) / 1000) )
            else
                player.set_motion(t.x * (module_manager.option(name,"Speed") / 1000), t.y, t.z * (module_manager.option(name,"Speed") / 1000))
            end
        end
        if module_manager.option(name,"Low-Hop") and started > 2 then
            if player.hurt_time() < 9 then
                if player.on_ground() then
                    offground = 0
                else
                    offground = offground + 1
                    if offground == 4 then
                        player.set_motion(t.x, 0.05, t.z)
                    elseif offground == 6 then
                        player.set_motion(t.x, t.y -0.1, t.z)
                    end
                end
            end
        end
    end,

    on_receive_packet = function(t)
        if t.packet_id == 0x08 then
            flags = 0
        end
    end,

    on_send_packet = function(t)
        if t.packet_id == 0x08 then
            swapdelay = 0
            swap = true
        end
    end
})
module_manager.register_number(name, "Rotations", 1, 3, 1)
module_manager.register_number(name, "Aim", -9, 2, -4)
module_manager.register_number(name, "Speed", 1, 1000, 999)
module_manager.register_boolean(name, "Low-Hop", true)
module_manager.register_boolean(name, "Block-Counter", true)
module_manager.register_boolean(name, "Safety", true)