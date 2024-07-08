function ToggleAuraBlock(block)
    if block then
        player.message(".killaura Auto-Block None")
    else
        player.message(".killaura Auto-Block Interact")
    end
end


local saferAuraBlock = {
    on_enable = function()
        current_hold = 0
        blocking = false
        ToggleAuraBlock(true)
    end,

    on_post_update = function()
        open_hold = module_manager.option("SaferAuraBlock", "OpenHold")
        block_hold = module_manager.option("SaferAuraBlock", "BlockHold")
        if module_manager.is_module_on("killaura") then
            if current_hold < (function() if not blocking then return block_hold else return open_hold end end)() then
                current_hold = current_hold + 1
            else
                current_hold = 0
                ToggleAuraBlock(not blocking)
                blocking = not blocking
            end
        else
            blocking = false
            ToggleAuraBlock(true)
        end
    end
}

module_manager.register("SaferAuraBlock", saferAuraBlock)
module_manager.register_number("SaferAuraBlock", "OpenHold", 2, 8, 5)
module_manager.register_number("SaferAuraBlock", "BlockHold", 1, 8, 1)