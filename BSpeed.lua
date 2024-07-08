-- BSpeed.lua made by blockman3063, Sunal
---------------------- BSpeed V13.1-----------------------
--Fixed Strafe
--Improved FastFall, Float
--------------------------------------------------------
local function printdebug(message)
    if modules.get_setting("BSpeed", "debug") then
        client.print(message)
    end
end
client.print("Thank for using BSpeed V13.1")
client.print("Mode0=Hop, Mode1=groundspeed")
--Thanks for sunal for printdebug funtion
local speeds
local posY = 0
local mx = 0
local mz = 0
local realpos = 0
local lagbackcheck = false
local lagbackticks = 0
local speedlevel = 0
local airtick = 0
local groundspeedstate = false
local notliquidtick = 0
local function speedmulti(multiple)
    player.set_motion_x(player.get_motion_x()*multiple)
    player.set_motion_z(player.get_motion_z()*multiple)
end
local function speedvalue(v0,v1,v2)
    if player.has_effect(1) then
        if  player.get_effect(1).amplifier == 0 then
            return v1
        elseif player.get_effect(1).amplifier >= 1 then
            return v2
        end
    else
        return v0
    end
end
modules.register("BSpeed", "BSpeed", "BSpeed", {
    on_enable = function(event)
        modules.set_state("Speed", false)
        modules.set_setting("sprint" , "omni" , true)
        lagbackcheck = false
        lagbackticks = 0
        notliquidtick = 15
    end,
    on_disable = function(event)
        client.set_timer_speed(1)
        modules.set_state("blink", false)
        modules.set_state("Speed", false) 
        if modules.get_setting("BSpeed", "fstop") then
            player.set_motion_x(player.get_motion_x()*0.3)
            player.set_motion_z(player.get_motion_z()*0.3)
            printdebug("FastStoped!")
        end
    end,
--Thanks for sunal for LagBackCheck
    on_packet_receive = function (event)
        if event.packet_id == 0x08 and not lagbackcheck and modules.get_setting("BSpeed", "lbcheck") then
            lagbackcheck = true
            player.set_motion_x(player.get_motion_x()*0.5)
            player.set_motion_z(player.get_motion_z()*0.5)
            client.print("Disabled due to flag!")
        end
    end,
    on_tick = function (event)
        if world.get_block_at(player.get_position_x(),player.get_position_y()-0.5,player.get_position_z()+0.2).full_block and world.get_block_at(player.get_position_x(),player.get_position_y()-0.5,player.get_position_z()-0.2).full_block and world.get_block_at(player.get_position_x()+0.2,player.get_position_y()-0.5,player.get_position_z()).full_block and world.get_block_at(player.get_position_x()-0.2,player.get_position_y()-0.5,player.get_position_z()).full_block then
            groundspeedstate = true
        else
            groundspeedstate = false
        end
    mx = -math.sin(player.get_yaw())
    mz = math.cos(player.get_yaw())
    airtick = airtick + 1
    if player.is_on_ground() then
        airtick = 0
    end
        if lagbackcheck then
            lagbackticks = lagbackticks + 1
            if lagbackticks == 40 then
                lagbackcheck = false
                lagbackticks = 0
                client.print("ReEnabled!")
            end
        end
    end,
    on_update = function(event) 
        if lagbackcheck then
            client.set_timer_speed(1)
            modules.set_state("blink", false)
            return
        end
        if modules.is_enabled("ScaffoldWalk") and modules.get_setting("BSpeed", "nosca") then
            return
        end
        if player.is_in_water() or player.is_in_lava() then
            notliquidtick = 0
            modules.set_state("blink", false)
            if modules.get_setting("BSpeed", "liqspeed") and player.is_moving() and player.is_in_water() then
                    player.set_motion_speed(0.16)
                    printdebug("Boosted in liquid!")
            elseif modules.get_setting("BSpeed", "debug") and player.is_moving() then
                client.print("Temporarily disabled speed in liquid!")
            end
        else
            if player.is_moving() then
                notliquidtick = notliquidtick + 1
                if notliquidtick < 6 then
                    return
                end
                -- Hop
                if modules.get_setting("BSpeed", "mode") == 0 then
                if client.is_key_down(57) then
                    printdebug("Disabled GroundBoost due to space pressed!")
                    else
                    if player.is_on_ground() then
                        if modules.get_setting("BSpeed", "LowJump") then
                            player.set_motion_y(0.4191)
                        else
                            player.jump()
                        end
                        player.set_motion_speed(speedvalue(0.485,0.51,0.6))
                    end
                end
                if modules.get_setting("BSpeed", "airboost") then
                    if player.get_motion_y() < 0.1 and player.get_motion_y() > 0.05 then
                        speedmulti(1.008)             
                        printdebug("AirBoost!-Stage2")
                    end
                end 
                if modules.get_setting("BSpeed", "fastF") then
                    if player.get_motion_y() > 0 and player.get_motion_y() < 0.05 then
                        player.set_motion_y(0)
                    end
                end
                if modules.get_setting("BSpeed", "StrafeT") and player.get_motion_y() < -0.2 and player.get_motion_y() > -0.5 then
                    if airtick == 8 and world.get_block_at(player.get_position_x(),player.get_position_y()-0.9,player.get_position_z()).full_block and player.get_motion_y() < - 0.3 and player.get_motion_y() > - 0.31 then
                        player.set_motion_y(-0.2)
                        player.set_motion_speed(player.get_speed())
                        printdebug("beeppp!")
                    elseif airtick == 9 then
                        player.set_motion_speed(player.get_speed())
                    end
                end
                if modules.get_setting("BSpeed", "float") then
                    if airtick == 9 and world.get_block_at(player.get_position_x(),player.get_position_y()-0.6,player.get_position_z()).full_block and player.get_motion_y() < - 0.27 and player.get_motion_y() > - 0.38 then
                        player.set_motion_y(0)
                        if modules.get_setting("BSpeed", "StrafeT") then
                            player.set_motion_speed(speedvalue(0.29,0.33,0.34))
                        else
                            player.set_motion_speed(speedvalue(0.29,0.31,0.32))
                        end
                        printdebug("beep!")
                    end
                end
                if player.get_hurt_time() == 9 and modules.get_setting("BSpeed", "dmgboost") then
                    player.set_motion_x(player.get_motion_x()*1.005)
                    player.set_motion_z(player.get_motion_z()*1.005)
                    printdebug("DamageBoost!")
                end
                if modules.get_setting("BSpeed", "DmgStrafe") and player.get_hurt_time() == 9 then
                    player.set_motion_speed(player.get_speed())
                    printdebug("Strafed on damage")
                end
                end
                -- ground
                if modules.get_setting("BSpeed", "mode") == 1 then
                    if groundspeedstate then
                        if player.is_on_ground() then
                            if player.collided_horizontally() then
                                player.set_motion_y(0.42)
                            else
                            player.set_motion_y(0.02)
                            player.set_motion_speed(speedvalue(0.2320000000123348921,0.2500000000123348921,0.300000000123348921))
                            end
                        else
                            player.set_motion_speed(speedvalue(0.2320000000123348921+ 0.053,0.3000000000123348921+ 0.03,0.300000000123348921+ 0.06))
                        end
                        if player.collided_horizontally() and airtick == 2 then
                            player.set_motion_y(-0.03)
                        end
                    end
                end
                -- ？？
                if modules.get_setting("BSpeed", "mode") == 2 then
                    if player.is_on_ground() then
                        player.set_motion_y(0.42)
                        player.set_motion_speed(speedvalue(0.485,0.51,0.6))
                    end
                        client.print(airtick)
                        client.print(player.get_motion_y())
                    if player.get_motion_y() < - 0.35  then
                        player.set_motion_y(player.get_motion_y()-0.1)
                    end
                    --if airtick == 7 then
                    --    player.set_motion_y(-0.2285)
                    --end
                end
            end
        end
        if player.is_moving() == false then
            if modules.get_setting("BSpeed", "fstop") then
            player.set_motion_x(player.get_motion_x()*0.3)
            player.set_motion_z(player.get_motion_z()*0.3)
            printdebug("FastStoped!")
            end
        end
    end,
    

})
modules.create_integer_setting("BSpeed","mode","Mode",0,0,2,1)
modules.create_boolean_setting("BSpeed", "fastF", "FastFall", true) 
modules.create_boolean_setting("BSpeed", "StrafeT", "Strafe", true)
modules.create_boolean_setting("BSpeed", "DmgStrafe", "DamageStrafe", false)
modules.create_boolean_setting("BSpeed", "dmgboost", "DamageBoost", false)
modules.create_boolean_setting("BSpeed", "lowjump", "LowJump", true)
modules.create_boolean_setting("BSpeed", "liqspeed", "LiquidSpeed", true)
modules.create_boolean_setting("BSpeed", "airboost", "AirBoost", true)
modules.create_boolean_setting("BSpeed", "float", "Float", false)
modules.create_boolean_setting("BSpeed", "fstop", "FastStop", false)
modules.create_boolean_setting("BSpeed", "lbcheck", "LagBackCheck", true)
modules.create_boolean_setting("BSpeed", "nosca", "NoScaffold", true)
modules.create_boolean_setting("BSpeed", "debug", "Debug", false)

client.message(".bind bspeed lmenu")