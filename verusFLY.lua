function isMoving()
	if input.is_key_down(17) or input.is_key_down(30) or input.is_key_down(31) or input.is_key_down(32) then
		return true
	else
		return false
	end
end
local fbcheck = function()
    local ent = world.entities()
    for i = 1, #ent do
        local fbEnt = ent[i]
        if not world.is_player(fbEnt) and world.name(fbEnt) == "Fireball" then
            local distance = player.distance_to_entity(fbEnt)
            if distance <= 5 then
                return true
            end
        end
    end
    return false
end
local delay = 0
module_manager.register("fly", {
    on_enable = function()
        start = true
        dontboost = false
        disablevelo = false
        delay = 0
    end,

    on_disable = function()
        if not module_manager.is_module_on("Velocity") and module_manager.option("fly", "OFF") then
            player.message(".velocity")
        end
    end,

    on_pre_motion = function()

        speed = module_manager.option("fly", "speed")
 
        if not module_manager.is_module_on("Velocity") then
            if start and not dontboost and isMoving() and not module_manager.is_module_on("flight") and not module_manager.is_module_on("timer") and player.hurt_time() == module_manager.option("fly", "hurttime") then
                player.set_speed(speed)
                player.message('.flight')
                player.message('.timer')
                start = false
            end
        end
        if not start then
            delay = delay + 1
        end
        if delay == 1 then
            if not module_manager.is_module_on("Velocity") and module_manager.option("fly", "OFF") then
                player.message(".velocity")
                disablevelo = true
            end
        end
        if delay == module_manager.option("fly", "delay") then
            if disablevelo and module_manager.is_module_on("Velocity") and module_manager.option("fly", "OFF") then
                player.message(".velocity")
            end
            start = true
            delay = 0
            if module_manager.option("fly", "noti") then client.print("You can fly again") end
        end
        if delay > 1 and delay < module_manager.option("fly", "delay") then -- this is for if you want velo to stay on after you damage boost
            if disablevelo and not module_manager.is_module_on("Velocity") and module_manager.option("fly", "OFF") then
                player.message(".velocity")
                disablevelo = false
            end
        end
        if not module_manager.is_module_on("Velocity") and fbcheck() then -- FB CHECK
            dontboost = true
        end
        if player.hurt_time() == 8 then -- FB CHECK
            dontboost = false
        end
    end

})

module_manager.register_number("fly", "hurttime",9,9,9)
module_manager.register_number("fly", "delay",1,200,75)
module_manager.register_boolean("fly", "turn-off", false)
module_manager.register_boolean("fly", "noti", true)
--- made by tanner