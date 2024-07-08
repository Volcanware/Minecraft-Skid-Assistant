local tTime

function hasTimeElapsed(time)
    return (client.time - tTime) >= time
end

local Mains = {
    on_enable = function ()
        tTime = client.time()
    end,

    on_pre_motion = function(event)
        local target = player.kill_aura_target()
        if target ~= nil then
                if (player.on_ground() or not player.on_ladder() or not player.is_in_cobweb() or not player.is_in_water() or not player.is_in_lava() or hasTimeElapsed(module_manager.option("Critic", "Hurttime"))) then 
                local x, y, z = player.position()
                local pos_x, pos_y, pos_z = player.position()
                local motion_x, motion_y, motion_z = player.motion()

                if(module_manager.option("Critic", "Motion1")) then
                    if player.on_ground() then
                        player.set_motion(motion_x, 0.32, motion_z)
                    end
                end 

                if(module_manager.option("Critic", "Motion2")) then
                    if player.on_ground() then
                        player.set_motion(motion_x, 0.2, motion_z)
                    end
                end 

                if(module_manager.option("Critic", "Motion3")) then
                    if player.on_ground() then
                        player.set_motion(motion_x, 0.18, motion_z)
                    end
                end

                if(module_manager.option("Critic", "AutoJump")) then
                    if player.on_ground() then
                        player.jump()
                    end
                end
                tTime = client.time()
            end 
        end
   end
}

module_manager.register("Critic", Mains)
module_manager.register_number("Critic", "Hurttime", 0, 20, 0)
module_manager.register_boolean("Critic", "Motion1", true)
module_manager.register_boolean("Critic", "Motion2", false)
module_manager.register_boolean("Critic", "Motion3", false)
module_manager.register_boolean("Critic", "AutoJump", false)