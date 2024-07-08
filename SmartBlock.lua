function isHoldingSword()
    return string.match(player.held_item(), "Sword")
end

local smartBlock = {
    on_enable = function()
        if not module_manager.is_module_on("AutoClicker") then
            player.message(".autoclicker")
        end
        click_time = 0
    end,

    on_post_motion = function()
        player_health = player.health()
        health_threshold = module_manager.option("SmartBlock", "healthThreshold")
        delay_blocking = module_manager.option("SmartBlock", "delay-blocking")
        delay_ticks = module_manager.option("SmartBlock", "delay-ticks")

        start_chance = module_manager.option("SmartBlock", "startChance")
        end_chance = module_manager.option("SmartBlock", "endChance")

        step = (end_chance - start_chance) / (health_threshold - 5)

        if input.is_mouse_down(0) then
            click_time = click_time + 1
        else
            click_time = 0
        end

        if player_health <= health_threshold then
            diff = health_threshold - player_health
            new_block = math.min(end_chance, math.ceil(start_chance + (diff*step)))

            player.message(string.format(".autoclicker Block Chance %s", new_block))
        elseif delay_blocking and click_time < delay_ticks then
            player.message(string.format(".autoclicker Block Chance 1"))
        else
            player.message(string.format(".autoclicker Block Chance %s", start_chance))
        end
    end



}

module_manager.register("SmartBlock", smartBlock)
module_manager.register_number("SmartBlock", "startChance", 0, 70, 35)
module_manager.register_number("SmartBlock", "endChance", 1, 100, 85)
module_manager.register_number("SmartBlock", "healthThreshold", 10, 20, 18)
module_manager.register_boolean("SmartBlock", "delay-blocking", true)
module_manager.register_number("SmartBlock", "delay-ticks", 5, 15, 6)
