local wasenabled
local ticks = 0

local module = {
    
    on_packet_receive = function(event)
        if event.packet_id == 0x08 then
            if event.y >= 94 then
                client:print("Pit S08")
                modules:set_state("Killaura", false)
                modules:set_state("Speed", false) 
                player:right_click() --for hypixel false flag machine 3000 (i flag legit)
                player:right_click() --for hypixel false flag machine 3000 (i flag legit)
            else
                if modules:is_enabled("Speed") then
                    wasenabled = true
                    modules:set_state("Speed", false)
                    client:print("Recieved S08! Disabling speed...")
                else
                    wasenabled = false
                    client:print("Recieved S08")
                    if modules:get_setting("PitAutoDisable", "exp") then
                        player:right_click() --for hypixel false flag machine 3000 (i flag legit)
                        player:right_click() --for hypixel false flag machine 3000 (i flag legit)
                    end
                end
            end
        end
    end,

    on_tick = function()

        if wasenabled then
            ticks = ticks + 1
            if ticks == 100 then
                client:print("Enabling speed again...")
                ticks = 0
                wasenabled = false
                modules:set_state("Speed", true)
            end
        else
            ticks = 0
        end

    end,

    on_enable = function()
        ticks = 0
    end

}
-- Register
modules:register("PitAutoDisable", "PitAutoDisable", module)
modules:create_boolean_setting("PitAutoDisable", "exp", "Unflag Killaura", true)