SpeedValue = function()
    if nospeed or nospeedd then 
        return 0.45
    end
    if speed or speedd then 
        return 0.52
    end
    if speed2 or speedd2 then 
        return 0.59
    end
    if speed3 or speedd3 then 
        return 0.64
    end
    if speed4 or speedd4 then 
        return 0.69
    end
end
function isMoving()
	if input.is_key_down(17) or input.is_key_down(30) or input.is_key_down(31) or input.is_key_down(32) then
		return true
	else
		return false
	end
end
module_manager.register('Speed\194\1677 Bhop', {
    on_enable = function(t)
        if module_manager.is_module_on("scaffold") then
            player.message(".scaffold")
        end
        if module_manager.is_module_on("fixedscaf") then
            player.message(".fixedscaf")
        end
    end,
    on_pre_motion = function(t)
        nospeedd = player.base_speed() == 0.221
        nospeed = player.base_speed() == 0.2873
        speed = player.base_speed() == 0.34476
        speedd = player.base_speed() == 0.2652
        speed2 = player.base_speed() > 0.40221 and player.base_speed() < 0.40223
        speedd2 = player.base_speed() == 0.3094
        speed3 = player.base_speed() > 0.45967 and player.base_speed() < 0.45969
        speedd3 = player.base_speed() == 0.3536
        speed4 = player.base_speed() > 0.51713 and player.base_speed() < 0.51715
        speedd4 = player.base_speed() ==  0.3978
        if isMoving() then
            if player.on_ground() then
                player.jump()
                player.set_speed(SpeedValue())
            end
        else
            player.set_speed(0)
        end
        if module_manager.option('Speed\194\1677 Bhop', "Strafe") then
            player.set_speed(player.get_speed())
        end
    end
})
module_manager.register_boolean('Speed\194\1677 Bhop', "Strafe",false)