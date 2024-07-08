local set_module_state = function(mod, state)
    if module_manager.is_module_on(mod) ~= state then
        player.message("." .. mod)
    end
end
local is_holding_sword = function()
    local held_item = player.held_item()
    return held_item and string.match(held_item, 'Sword')
end

local blocking = false
local safenofallWasOn = false

module_manager.register("Autoblock", {

    on_pre_update = function()
        if player.kill_aura_target() ~= nil then
            if player.distance_to_entity(player.kill_aura_target()) <= 3 then
                player.message(".killaura rotations Enable true")
            else
                player.message(".killaura rotations Enable false")
            end
        else
            player.message(".killaura rotations Enable false")
        end
    end,

    on_pre_motion = function()
        player.message(".killaura Delay Miss-Chance 100")
        player.message(".killaura Swing NO_SWING")
        player.message(".killaura range 5")

        if player.kill_aura_target() ~= nil and is_holding_sword() then

            player.message(".blink entity false")
            player.message(".hide blink")

            safenofallWasOn = module_manager.is_module_on("blinknofall")
            set_module_state("blinknofall", false)
            player.message(".killaura auto-block fake")
            if blocking then
                set_module_state("blink", true)
                player.send_packet(0x07, 6, {0, 0, 0}, 1)
                blocking = false

            else
                if player.distance_to_entity(player.kill_aura_target()) <= 3 then
                    player.swing_item()
                    player.send_packet(0x02, player.kill_aura_target(), 2)
                end
                player.send_packet(0x02, player.kill_aura_target(), 1)
                player.send_packet(0x08)
                set_module_state("blink", false)
                blocking = true
            end
        else
            if safenofallWasOn then
                set_module_state("blinknofall", true)
                safenofallWasOn = false
            end
            player.message(".blink entity true")
            player.message(".show blink")
            player.message(".killaura auto-block none")
            if blocking then
                if is_holding_sword() then
                    player.send_packet(0x07, 6, {0, 0, 0}, 1)
                end
                set_module_state("blink", false)
                blocking = false
            end
        end

    end,

    on_disable = function()
        set_module_state("blink", false)
        if safenofallWasOn then
            set_module_state("blinknofall", true)
        end
        player.message(".blink entity true")
        player.message(".show blink")
    end
})