
local WatchDog2024 = {
    on_pre_motion = function(ctx)
        if player.on_ground() and not player.is_sneaking() and not player.using_item() then 
            player.jump()
            player.set_speed(0.485)
        end
     end
}
module_manager.register("WatchDog2024", WatchDog2024)