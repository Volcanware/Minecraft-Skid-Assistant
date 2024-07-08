local tTime

function hasTimeElapsed(time)
    return (client.time - tTime) >= time
end

local Mains = {
    on_enable = function ()
        tTime = client.time()
    end,

    on_pre_update = function(event)
        local target = player.kill_aura_target()
        local distance = player.distance_to_entity()
        if target ~= nil then
            if module_manager.option('AutoBlock', "1") then
                player.use_item()
            end
            if module_manager.option('AutoBlock', '2') then --PLEASE FIX THIS PROBLEM!I CANT HANDLE IT(Attempt to Compare __le on Boolean and Number)
                if player.Hurttime() >0 then
                    player.right_click_mouse()
                end
            end
        end
    end
}

module_manager.register('AutoBlock', Mains)
module_manager.register_boolean('AutoBlock', '1', true)
module_manager.register_boolean('AutoBlock', '2', false)
