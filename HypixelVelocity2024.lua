local WatchDogVelocity = {
    on_pre_motion = function(ctx)
        if player.hurt_time() > 0 and player.ticks_existed() % 1 == 0  then 
            local x, y, z = player.motion()
            player.set_motion(x / 3, y, z / 3)
        end
     end
}
module_manager.register("WatchDogVelocity", WatchDogVelocity)