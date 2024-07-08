-- Define the module table
local module = {
    -- Define the event handler for on_tick
    on_tick = function()
        -- Perform actions here that need to be executed on every tick
        -- Example: Print a message to the console
        client:print("Tick!")
    end,

    -- Define the event handler for on_enable
    on_enable = function()
        -- Perform actions here when the module is enabled
        -- Example: Print a message to the console
        client:print("Module enabled!")
    end,

    -- Define the event handler for on_disable
    on_disable = function()
        -- Perform actions here when the module is disabled
        -- Example: Print a message to the console
        client:print("Module disabled!")
    end
}

-- Register the module
modules:register("ExampleModule", "Example Module", module)