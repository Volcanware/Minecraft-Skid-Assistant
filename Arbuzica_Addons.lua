local function is_moving()
    local input = player:get_input()
    return not (input.x == 0 and input.y == 0)
end

local module = {
    on_tick = function()
        local Damage_Boost = modules:get_setting("Arbuzica Addons", "Damage_Boost")
        local Speed_Amplifier = modules:get_setting("Arbuzica Addons", "Speed_Amplifier")
        local Air_Velocity = modules:get_setting("Arbuzica Addons", "Air_Velocity")
        local Sex = modules:get_setting("Arbuzica Addons", "Sex")
        --Speed Amplifier
        if Speed_Amplifier == true and player:has_effect(1) and is_moving() and player:is_on_ground() and modules:is_enabled("ScaffoldWalk") == true then
            player:set_motion_speed(0.34)
            player:set_motion_y(0.419999986886978) -- only works on alpha
        end
        --boost
         if player:get_hurt_time() == 9 and Damage_Boost == true and modules:is_enabled("ScaffoldWalk") == true then
            player:set_motion_speed(.6) --patched
         end
--air velocity
         if not player:is_on_ground() and Air_Velocity == true then
            modules:set_setting("Velocity", "vertical-mod", 0)
        end

        if player:is_on_ground() and Air_Velocity == true then
            modules:set_setting("Velocity", "vertical-mod", 100)
        end
--sex?????
if Sex == true then
    client:print("<rainbow> WTF MOOM SEX?!?!?!?!?!??")
end
local SessionInfoFix = modules:get_setting("Arbuzica Addons", "SessionInfoFix")
        if modules:get_setting("Arbuzica Addons", "SessionInfoFix") and SessionInfoFix then
        client:message("/language english")
        modules:set_setting ("Arbuzica Addons", "SessionInfoFix", false)
        end
    end,
    on_packet_receive = function(event)
        local autoplay = modules:get_setting("Arbuzica Addons", "Autoplay")
        local AntiGuiOpen = modules:get_setting("Arbuzica Addons", "AntiGuiOpen")
        if event.packet_id == 0x02 and autoplay == true then
                if string.find(event.message, "You won!") or string.find(event.message, "You died!") then
                    client:message("/play solo_insane")
                end
        end
    if event.packet_id == 0x02 and AntiGuiOpen == true then
        if string.find(event.message, "Sending you to") then
            modules:set_state("InventoryManager", false)
            modules:set_state("ChestStealer", false)
            client:print("disabled")
        end
    end
    if event.packet_id == 0x02 and AntiGuiOpen == true then
        if string.find(event.message, "Cagesz") then
            modules:set_state("InventoryManager", true)
            modules:set_state("ChestStealer", true)
            client:print("enabled")
        end
    end
end
}

modules:register("Arbuzica Addons", "Arbuzica Addons", module)
modules:create_text_setting("Arbuzica Addons", "Speed Amplifier", "-------------------------------------------", "Scaffold")
modules:create_boolean_setting("Arbuzica Addons", "Speed_Amplifier", "Speed Amplifier", false)
modules:create_boolean_setting("Arbuzica Addons", "Damage_Boost", "Damage Boost", false)
modules:create_text_setting("Arbuzica Addons", "Speed Amplifier", "-------------------------------------------", "Combat")
modules:create_boolean_setting("Arbuzica Addons", "Air_Velocity", "Air Velocity", false)
modules:create_text_setting("Arbuzica Addons", "Speed Amplifier", "-------------------------------------------", "Misc")
modules:create_boolean_setting("Arbuzica Addons", "Sex", "Sex", false)
modules:create_boolean_setting("Arbuzica Addons", "Autoplay", "Autoplay", false)
modules:create_boolean_setting("Arbuzica Addons", "AntiGuiOpen", "AntiGuiOpen", false)
modules:create_boolean_setting("Arbuzica Addons", "SessionInfoFix", "SessionInfoFix", false)