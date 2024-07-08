
-- Made By Frog <3 

local ground_distance = function()
    local x,y,z = player.position()
    for i = y, 0, -0.1 do
        if world.block(x, i, z) ~= 'tile.air' then
            return y - i
        end
    end
    return -1
end

local set_module_state = function(mod, state)
    if module_manager.is_module_on(mod) ~= state then
        player.message("." .. mod)
    end
end
local blinking = false
local wason = false
local ticks = 0
local xz,lastY,zx = 0,0,0
module_manager.register('Anti Void\194\1677 Anti-Stall', {
    on_disable = function()
        blinking = false
        wason = false
        ticks = 0
        cutie = false
    end,
    on_pre_motion = function(t)
        local dist = ground_distance()
        local blinkdist = module_manager.option('Anti Void\194\1677 Anti-Stall', 'Distance')
        local side_hit = player.over_mouse()
        if player.on_ground() then
            xz,lastY,zx = player.position()
        end
        if module_manager.option('Anti Void\194\1677 Anti-Stall', 'Enable') then
            x,y,z = player.position()
            if lastY - 1.5 < y and player.kill_aura_target() == nil and dist == -1 and not falling and not player.on_ground() and not (side_hit == 2 and input.is_mouse_down(1)) and not module_manager.is_module_on('Long-Jump')  and not module_manager.is_module_on('NoFall') and not module_manager.is_module_on('Bridger') then
                set_module_state('blink', true) --                      uses blink to reduce distance & tp u back  
                set_module_state('antidesync', true)
                blinking = true
                falling = true
                player.message('.killaura Entities Players false') -- removes targetting player in killaura to stop
                --                                                     hits from not registering after unblinking
            
            elseif falling and (player.on_ground() or dist ~= -1) then
                falling = false
                cutie = false
                blinking = false
                set_module_state('blink', false)
                set_module_state('antidesync', false)
                player.message('.killaura Entities Players true')
            
            elseif falling and player.fall_distance() > blinkdist and dist == -1 and not cutie then
                local x,y,z = player.position()
                cutie = true -- so if u fail it doesn't spam c04
                player.send_packet_no_event(0x04, x, -420, z, false) -- sends an invalid position packet to make the
                set_module_state('blink', false) --                               server lag u back faster
                set_module_state('antidesync', false)
                if not module_manager.is_module_on("killaura") and wason == true then
                    player.message('.killaura')
                end
            end
        end
        if module_manager.is_module_on("blink") then
            ticks = ticks + 1
        else
            ticks = 0
        end
    end,
    on_render_screen = function(t)
        if blinking and player.fall_distance() > ((module_manager.option('Anti Void\194\1677 Anti-Stall', 'Distance') / 2) - 1)and module_manager.is_module_on("Blink") then
            --render.scale(1)
            --render.string_shadow("Saving You", (t.width/2 - 25)/1, (t.height/2 + )/1, 255, 255, 255, 255)
            --render.scale(1/1)
        end
    end,
    on_send_packet = function(t)

        if blinking and (t.packet_id == 0x07 or t.packet_id == 0x08 or t.packet_id == 0x0A or t.packet_id == 0x02) then
            t.cancel = true
        end
        return t
    end
})

module_manager.register_boolean('Anti Void\194\1677 Anti-Stall', '\194\1678      values below 15 not', false)
module_manager.register_boolean('Anti Void\194\1677 Anti-Stall', '\194\1678           recommended', false)
module_manager.register_number('Anti Void\194\1677 Anti-Stall', 'Distance', 1, 20, 15)
module_manager.register_boolean('Anti Void\194\1677 Anti-Stall', "Enable", false)