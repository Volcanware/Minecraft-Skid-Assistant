local module = {
    on_enable = function()
        player:jump() -- Make the player jump
        modules:set_state("JumpModule", false) -- Disable the module
    end
}

-- Register the module
modules:register("JumpModule", "Jump Module", module)