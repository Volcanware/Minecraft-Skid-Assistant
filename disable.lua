local tick = false
local ticks = 0
local setback = 0
local s1 = false
local s2 = false
local turnAngle = 0
local rot = true
local tik = 0
local setbac = 0

local module = {
    on_packet_send = function(event) 
        if event.packet_id == 0x0B and modules.get_setting("disable", "keepsprint") then
            event.cancel = true
            tick = true
        end
        if event.packet_id == 0x0f and player.get_hurt_time() >= 0 and modules.get_setting("disable", "velocity") then
            event.cancel = true
        else
            event.cancel = false 
        end
        if event.packet_id == 0x0F and modules.get_setting("disable", "reach") then
            event.cancel = true
        end
        if event.packet_id == 0x0F and modules.get_setting("disable", "motion") then
            event.cancel = true
            s2 = true
        end
    end,
    on_packet_receive = function(event)
        if event.packet_id == 0x08 and modules.get_setting("disable", "mushc") and setback < 3 then
            event.cancel = true
        end
        if event.packet_id == 0x08 and modules.get_setting("disable", "mushc") then
            setback = setback + 1
        end
        if event.packet_id == 0x08 and modules.get_setting("disable", "mush") then
            client.print("Teleport: ".. setbac)
            setbac = setbac + 1
        end
    end,

    on_tick = function(event)
        if tick and modules.get_setting("disable", "keepsprint") then
            player.send_packet(0x0B, {player.get_id(), enums.entity_action.STOP_SPRINTING})
            tick = false
        end
        if ticks >= 1 then
            ticks = 0
        end
        if setback >= 3 then
            setback = 0
        end
        ticks = ticks + 1
        tik = tik + 1
    end, 
    on_update = function(event)
        if turnAngle ~= 0 then
            event.yaw = (event.yaw + turnAngle) % 360
        end
        if tik >= modules.get_setting("disable", "mushdelay")and modules.get_setting("disable", "mush") then
            player.send_packet(0x04, {player.get_position_x(), player.get_position_y()+-100, player.get_position_z(), false})
            tik = 0
        end
        if modules.get_setting("disable", "omnisprint") and rot then
            if client.is_key_down(30) and client.is_key_down(17) then
                turnAngle = -45
            elseif client.is_key_down(32) and client.is_key_down(17) then
                turnAngle = 45 
            elseif client.is_key_down(30) and client.is_key_down(31) then
                turnAngle = -135
            elseif client.is_key_down(32) and client.is_key_down(31) then
                turnAngle = 135 
            elseif client.is_key_down(31) then
                turnAngle = 180 
            elseif client.is_key_down(30) then
                turnAngle = -90  
            elseif client.is_key_down(32) then
                turnAngle = 90
            else
                turnAngle = 0
            end 
        elseif rot then
            TurnAngle = 0
        end
        if modules.get_setting("disable", "omnisprint") and (modules.is_enabled("scaffoldwalk") or modules.is_enabled("killaura"))  then
            rot = false
            modules.set_setting("sprint", "omni", false)
        elseif modules.get_setting("disable", "omnisprint") and not (modules.is_enabled("scaffoldwalk") or modules.is_enabled("killaura")) then
            rot = true
            modules.set_setting("sprint", "omni", true)
        end
        if s1 == false and modules.get_setting("disable", "omnisprint") then
            client.print("Wont work with killaura/Scaffold")
            s1 = true
        end
    end
}

modules.register("disable", "Disabler", module);
modules.create_boolean_setting("disable", "keepsprint", "Vulcan Keep-Sprint", true)
modules.create_boolean_setting("disable", "reach", "Vulcan Reach (broken)", false)
modules.create_boolean_setting("disable", "velocity", "Vulcan Velocity", false)
modules.create_boolean_setting("disable", "Strafe", "Vulcan Strafe?", false)
modules.create_boolean_setting("disable", "motion", "Vulcan Motion?", false)
modules.create_boolean_setting("disable", "omnisprint", "Head-Rotate Omni-Sprint", false)
modules.create_boolean_setting("disable", "mush", "Negativity Motion", false)
modules.create_integer_setting("disable", "mushdelay", "Teleport", 39, 1, 100, 1)
modules.create_boolean_setting("disable", "mushc", "Test", false)