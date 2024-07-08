modules.register("mmcspeed", "MineMen Club Speed", "Speed module for MineMen Club", {
    on_update = function(event)
        if event.pre and player.is_on_ground() and player.is_moving() and not client.is_key_down(57) then
            player.jump()
        end
    end;
    on_move = function()
        if player.get_hurt_time() > 0 then
            can_speed = nil
        elseif player.is_on_ground() then
            can_speed = true
        end
        if can_speed and player.get_hurt_time() < 1 then
            player.set_motion_speed(player.get_speed())
        end
    end
})