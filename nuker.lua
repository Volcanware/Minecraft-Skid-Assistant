-- Hypixel bed nuker by BadName
-- Based off a bed nuker script made by SRK1D

local start = false
local targetblock = nil
local thegooditem = nil
local breakingblock = nil
local xOffset = 5
local yOffset = 3
local zOffset = 5
local pickslot = nil

local thegooditem = nil
local breakingblock = nil
local dontmindthisitsjusthere = nil
local start_slot = nil

function scan_surrounding()
    local plr_position_temp = player:get_position()
    local plr_position = types.vec3d:new(math.floor(plr_position_temp.x), math.floor(plr_position_temp.y), math.floor(plr_position_temp.z))

    for y = -modules:get_setting("hypixelnuke", "XOffset"), modules:get_setting("hypixelnuke", "YOffset") do
        for x = -modules:get_setting("hypixelnuke", "XOffset"), modules:get_setting("hypixelnuke", "XOffset") do
            for z = -modules:get_setting("hypixelnuke", "ZOffset"), modules:get_setting("hypixelnuke", "ZOffset") do
                local name = world:get_block_at(plr_position.x + x, plr_position.y + y, plr_position.z + z).name
                if name == "tile.bed" or name == "Bed" then
                    if not string.match(world:get_block_at(player:get_position_x() + x, player:get_position_y() + y + 1, player:get_position_z() + z).name, "air") then
                        targetblock = types.vec3d:new(math.floor(player:get_position_x() + x)+0.5, math.floor(player:get_position_y() + y + 1)+0.5, math.floor(player:get_position_z() + z)+0.5)
                        return types.vec3i:new(plr_position.x + x, plr_position.y + y + 1, plr_position.z + z)
                    else
                        targetblock = types.vec3d:new(math.floor(player:get_position_x() + x)+0.5, math.floor(player:get_position_y() + y), math.floor(player:get_position_z() + z)+0.5)
                        return types.vec3i:new(plr_position.x + x, plr_position.y + y, plr_position.z + z)
                    end
                end
            end
        end
    end
    return nil
end

local cModule = {
    on_tick = function()
        if start == true then
            player:swing_item()
        end
        if (targetblock ~= nil and breakingblock ~= world:get_block_at(targetblock).name) or targetblock == nil then
            breakingblock = nil
            targetblock = nil
            start = false
        end
        if scan_surrounding() == nil then
            breakingblock = nil
            targetblock = nil
            start = false
        end
        if targetblock ~= nil and string.match(world:get_block_at(targetblock).name, "air") then
            breakingblock = nil
            targetblock = nil
            start = false
        end
        xOffset = modules:get_setting("hypixelnuke", "XOffset")
        yOffset = modules:get_setting("hypixelnuke", "YOffset")
        zOffset = modules:get_setting("hypixelnuke", "ZOffset")

        if scan_surrounding() ~= nil and start ~= true and targetblock ~= nil and not string.match(world:get_block_at(targetblock).name, "air") then
            start = true
            breakingblock = world:get_block_at(targetblock).name
            player:send_packet(0x07, {enums.digging_action.START_DESTROY_BLOCK, scan_surrounding(), enums.facing.UP})
            player:send_packet(0x07, {enums.digging_action.STOP_DESTROY_BLOCK, scan_surrounding(), enums.facing.UP})
            client:print(world:get_block_at(targetblock).name)

            start_slot = player:get_held_item_slot()

            function get_axe_slot()
                for i = 0, 9 do
                    local item = inventory:get_item(i)
                    if item ~= nil and item.name:find('hatchet') ~= nil then
                        return i
                    end
                end
                return nil
            end

            function get_shear_slot()
                for i = 0, 9 do
                    local item = inventory:get_item(i)
                    if item ~= nil and item.name:find('shears') ~= nil then
                        return i
                    end
                end
                return nil
            end

            function get_pickaxe_slot()
                for i = 0, 9 do
                    local item = inventory:get_item(i)
                    if item ~= nil and item.name:find('pickaxe') ~= nil then
                        return i
                    end
                end
                return nil
            end

            if world:get_block_at(targetblock).name == "End Stone" or world:get_block_at(targetblock).name == "Stained Clay" then
                client:print("Slot is in: " .. get_pickaxe_slot())
                player:set_held_item(get_pickaxe_slot())
                client:print("Autotool pick")
            end

            if world:get_block_at(targetblock).name == "Wooden Planks" then
                client:print(inventory:get_item(7))
                client:print(get_axe_slot())
                client:print("Slot is in: " .. get_axe_slot())
                player:set_held_item(get_axe_slot())
            end

            if world:get_block_at(targetblock).name == "Wool" then
                client:print(get_shear_slot())
                player:set_held_item(get_shear_slot())
            end

        end
    end;
    on_update = function(event)
        if start and event.pre and targetblock ~= nil then
            modules:set_state("killaura", false)
            player:set_sprinting(false)
            event.yaw = player:rotations_to(targetblock).x
            event.pitch = player:rotations_to(targetblock).y
        end
    end;
    
    on_disable = function()
        targetblock = nil
        start = false
        pickslot = nil
    end
}
modules:register("hypixelnuke", "hypixelnuke", cModule)
modules:create_integer_setting("hypixelnuke", "XOffset", "X radius", 3, 0, 5, 1)
modules:create_integer_setting("hypixelnuke", "YOffset", "Y radius", 2, 0, 5, 1)
modules:create_integer_setting("hypixelnuke", "ZOffset", "Z radius", 3, 0, 5, 1)