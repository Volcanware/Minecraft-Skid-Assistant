-- compiled from stuff by xzto :-D

function set_module_state(mod, state)
    if module_manager.is_module_on(mod) ~= state then
        player.message("." .. mod)
    end
end

function dump(o)
    if type(o) == 'table' then
       local s = '{ '
       for k,v in pairs(o) do
          if type(k) ~= 'number' then k = '"'..k..'"' end
          s = s .. '['..k..'] = ' .. dump(v) .. ','
       end
       return s .. '} '
    else
       return tostring(o)
    end
end

function contains(table, val)
    for i = 1, #table do
       if table[i] == val then 
          return true
       end
    end
    return false
end

function is_holding_sword()
    return player.held_item() and (string.match(player.held_item(), 'Sword') or string.match(player.held_item(), 'Spireblade'))
end

local packets = {}

function release_packets() -- for releasing all the blinked packets
    for i = 1, #packets do
        packet = packets[i]
        id, fields = packet[1], packet[2]
        if id == 0x0F then
            player.send_packet(0x0F, 0, fields[1], true)

        elseif id == 0x00 then
            player.send_packet(0x00, fields[1])

        elseif id == 0x07 then
            player.send_packet(0x07)

        elseif id == 0x08 then
            player.send_packet(0x08)

        elseif id == 0x09 then
            player.send_packet(0x09, fields[1])

        elseif id == "Attack" then
            player.swing_item()
            player.send_packet(0x02, fields[1], 2)

        elseif id == "Interact" then
            player.send_packet(0x02, fields[1], 3, {world.position(fields[1])}) 
            player.send_packet(0x02, fields[1], 1)

        elseif id == 0x04 and not flag then
            local packet_x, packet_y, packet_z, packet_ground = fields[1],fields[2],fields[3],fields[4]
            player.send_packet(0x04, packet_x,packet_y,packet_z, packet_ground)
        end
    end
    packets = {}
    flag = false
end

local ticks = 0
local unblock = 0

local prevAngle
local hasAttacked
local rots = {}

function wrapAngleTo180(value)
    value = value % 360.0
    if (value >= 180.0) then
        value = value - 360.0
    end
    if (value < -180.0) then
        value = value + 360.0
    end
    return value
end
--><
function EnemyTarget() -- finds the closest entity to you thats a player
    local entities = world.entities()
    local target = nil
    for i = 1, #entities do
        --if entities[i] ~= player.id() and (world.is_player(entities[i]) or world.is_bot(entities[i])) then
        if entities[i] ~= player.id() and world.is_player(entities[i]) then
            if target == nil then
                target = entities[i]
            else
                if player.distance_to_entity(entities[i]) < player.distance_to_entity(target) then
                    target = entities[i]
                end
            end
        end
    end
    return target
end

function targetting() -- checks if the enemy is in distance to be attacked
    world.entities()
    local exists = world.name(EnemyTarget())
    if exists then
        if EnemyTarget() ~= nil and is_holding_sword() then
            if player.distance_to_entity(EnemyTarget()) <= module_manager.option('SilentAura', "Block-Range") then
                if not (module_manager.is_module_on("Bridger") or module_manager.is_module_on("Blink") or module_manager.is_module_on("BedAura")) then
                    return true
                else
                    return false
                end
            else
                return false
            end
        else
            return false
        end
    else
        return false
    end
end

local switch_timer = 0
local status = nil
local enemy_hp = nil
local string = nil
local blink = 0
local swapped = false

module_manager.register('SilentAura', {
    on_enable = function(t)
        set_module_state("killaura",true) -- for animations
        blink = 0
        flag = false
        swapped = false
        hasAttacked = false
        prevAngle = 0
        switch_timer = 0
    end,
    on_disable = function(t)
        set_module_state("killaura",false) -- for animations
        if module_manager.option('SilentAura', "Auto-Block") then 
            release_packets()
            player.send_packet_no_event(0x07)
            player.set_held_item_slot(player.held_item_slot() - 2)
            blinking = false 
            blocking = false
        end
    end,
    on_render_screen = function(t)
        -- made by xzto kinda ass
        if module_manager.option('SilentAura', "Target-Info") then
            world.entities()
            local exists = world.name(EnemyTarget())
            if exists and targetting() then
                if player.health() > world.health(EnemyTarget()) then
                    status = " \194\167aW"
                elseif player.health() < world.health(EnemyTarget()) then
                    status = " \194\167cL"
                else
                    status = " \194\1676T"
                end
                if world.health(EnemyTarget()) >= 15 then
                    enemy_hp = ' \194\167a'
                elseif world.health(EnemyTarget()) < 15 and world.health(EnemyTarget()) >= 10 then
                    enemy_hp = ' \194\167e'
                elseif world.health(EnemyTarget()) < 10 and world.health(EnemyTarget()) >= 5 then
                    enemy_hp = ' \194\1676'
                elseif world.health(EnemyTarget()) < 5 then
                    enemy_hp = ' \194\167c'
                end
                string = tostring("\194\167f"..world.display_name(EnemyTarget()))..enemy_hp..tostring(math.floor(world.health(EnemyTarget())))..".0"..status -- all the info you need
                render.string_shadow(string,t.width / 2 - render.get_string_width(string) / 2, t.height / 2 - 20, 255,255,255,255)
            end
        end
    end,
    on_post_player_input = function(t)
        if blocking then
        end
    end,
    on_pre_update = function(t)
        player.message(".killaura block-range "..tostring(module_manager.option('SilentAura', "Block-Range")))
        --client.print(world.name(EnemyTarget()))
        --client.print(targetting())
        world.entities()
        if not targetting() then
            EnemyTarget()
        end
        if module_manager.option('SilentAura', "Auto-Block") then -- ab from frog
            if targetting() then
                if is_holding_sword() then

                    ticks = ticks + 1
                    local exists = world.name(EnemyTarget())

                    if ticks == 1 then
                        if module_manager.option('SilentAura', "Blink-Ticks") == 3 then

                            blinking = false
                            release_packets()

                            player.send_packet(0x08)
                    
                            blocking = true

                        else

                            if module_manager.option('SilentAura', "C09") then

                                if swapped then
                                    table.insert(packets, {0x09, {player.held_item_slot()}})
                                    --player.send_packet(0x09,player.held_item_slot())
                                    swapped = false
                                end

                            end

                            blinking = false
                            release_packets()

                            if exists and player.distance_to_entity(EnemyTarget()) <= module_manager.option('SilentAura', "Attack-Range")  then
                                player.swing_item()
                                player.send_packet(0x02, EnemyTarget(), 2)
                            end

                            player.send_packet(0x02, EnemyTarget(), 1)

                            player.send_packet(0x08)

                            blocking = true

                        end

                    elseif ticks == 2 then

                        if module_manager.option('SilentAura', "Blink-Ticks") == 3 then
                    
                            blinking = true

                            if module_manager.option('SilentAura', "C09") then
                                table.insert(packets, {0x09, {player.held_item_slot() % 8 + 1}})
                                --player.send_packet(0x09,player.held_item_slot() % 8 + 1)
                                swapped = true
                            else
                                table.insert(packets, {0x07})
                            end

                        else

                            blinking = true

                            if module_manager.option('SilentAura', "C09") then
                                table.insert(packets, {0x09, {player.held_item_slot() % 8 + 1}})
                                --player.send_packet_no_event(0x09,player.held_item_slot() % 8 + 1)
                                swapped = true
                            else
                                player.send_packet(0x07)
                                --table.insert(packets, {0x07})
                            end

                            ticks = 0

                        end

                    elseif ticks == 3 then

                        if module_manager.option('SilentAura', "C09") then
                            if swapped then
                                table.insert(packets, {0x09, {player.held_item_slot()}})
                                --player.send_packet(0x09,player.held_item_slot())
                                swapped = false
                            end
                        end

                        table.insert(packets, {"Interact", {EnemyTarget()}})

                        if exists and player.distance_to_entity(EnemyTarget()) <= module_manager.option('SilentAura', "Attack-Range")  then
                            table.insert(packets, {"Attack", {EnemyTarget()}})
                        end
                        
                        ticks = 0
                    end
                    unblock = 0

                else
                    ticks = 0
                    blinking = false
                    release_packets()
                end
            else
                ticks = 0
                unblock = unblock + 1
                if unblock == 2 then
                    player.send_packet(0x07)
                    blinking = false
                    player.set_held_item_slot(player.held_item_slot() - 2)
                    release_packets()
                    blocking = false
                end
            end
        elseif targetting() then
            if math.random() > 0.4 then
                if player.distance_to_entity(EnemyTarget()) <= module_manager.option('SilentAura', "Attack-Range") then
                    player.swing_item()
                    player.send_packet(0x02, EnemyTarget(), 2)
                end
            end
        end
    end,

    on_pre_motion = function(t)
        world.entities()
        x,y,z = t.x,t.y,t.z
        ground = t.on_ground
        yaw,pitch = t.yaw,t.pitch
        if module_manager.option('SilentAura', "Rotations") then -- tweaked from a custom rotation script (dont know the original creator)
            local exists = world.name(EnemyTarget())
            if exists and targetting() and player.distance_to_entity(EnemyTarget()) <= module_manager.option('SilentAura', "Rotation-Range") then
                world.entities()
                local width, height = world.width_height(EnemyTarget())
                local x, y, z = world.position(EnemyTarget())
                local diff = { x - t.x,
                y - t.y + (math.random() * (module_manager.option('SilentAura', "Pitch-Random") * 0.01)) 
                            -- ^ alan wood bypass $$$
                - module_manager.option('SilentAura', "Pitch-Correction"), z - t.z }
                local angle = math.deg(math.atan(diff[3], diff[1]))
                local yaw = angle - 90.0
                local pitch = -math.deg(math.atan(diff[2], player.distance_to_entity(EnemyTarget())))
                local cYaw = player.angles()
                if math.abs(angle - prevAngle) > module_manager.option('SilentAura', "Yaw-Step") then -- how much the yaw needs to change to rotate again
                    rots[1] = cYaw + wrapAngleTo180(yaw - cYaw)
                    prevAngle = angle
                    hasAttacked = false
                end
                rots[2] = wrapAngleTo180(pitch)
                t.yaw = rots[1]
                t.pitch = rots[2]    
            end
        end
        return t
    end,

    on_receive_packet = function(t) -- flag check when blinking
        if t.packet_id == 0x08 then
            blinking = false
            flag = true
            release_packets()
            --packets = {}
        end
    end,

    on_send_packet = function(t) -- blink for ab
        if blinking then
            if t.packet_id == 0x0F then

                if module_manager.option('SilentAura', "C0F") then
                    table.insert(packets, {0x0F, {t.uid}})
                    t.cancel = true
                end

            elseif t.packet_id == 0x00 then

                if module_manager.option('SilentAura', "C00") then
                    table.insert(packets, {0x00, {t.keep_alive_key}}) -- dont need to cancel keepalive
                    t.cancel = true
                end

            elseif t.packet_id >= 3 and t.packet_id <= 6 then
                event_yaw, event_pitch = player.angles()
                table.insert(packets, {0x04, {x, y, z, player.on_ground()}})
                t.cancel = true

            elseif module_manager.option('SilentAura', "All") then -- cancel every other packet when blinking
                t.cancel = true

            end
        end

        return t
    end
})
module_manager.register_boolean('SilentAura', "Rotations",true)
module_manager.register_boolean('SilentAura', "Auto-Block",true)
module_manager.register_number('SilentAura', "Blink-Ticks", 2, 3, 2)
module_manager.register_number('SilentAura', "Attack-Range", 3.01, 6.01, 3.15)
module_manager.register_number('SilentAura', "Rotation-Range", 3.01, 8.01, 6.01)
module_manager.register_number('SilentAura', "Block-Range", 3.01, 12.01, 10.01)
module_manager.register_number('SilentAura', "Yaw-Step", 1, 180, 15)
module_manager.register_number('SilentAura', "Pitch-Correction", 0.01, 2.01, 0.25) -- 0 to aim for head, 2 to aim for feet
module_manager.register_number('SilentAura', "Pitch-Random", 0, 100, 25) -- $v$
module_manager.register_boolean('SilentAura', "Target-Info",true)
module_manager.register_boolean('SilentAura', "C09",true)
module_manager.register_boolean('SilentAura', "C0F",true)
module_manager.register_boolean('SilentAura', "C00",true)
module_manager.register_boolean('SilentAura', "All",true)