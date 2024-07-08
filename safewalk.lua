tick = 0
tickfr = 0
gonnadoit = false
didit = true
local module = {
    on_tick = function()
        tick = tick + 1
    end;
    on_move = function(event)
        if player:get_pitch() >= 75 and world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name == "tile.air" and gonnadoit == false and player:is_on_ground() and inventory:get_item(player:get_held_item_slot()) ~= nil and string.match(inventory:get_item(player:get_held_item_slot()).name, "tile.") and client:is_key_down(31) then
            if modules:get_setting("safewalk", "spoofcrouch") == true then
                player:send_packet(0x0B, {player:get_id(), enums.entity_action.START_SNEAKING, 0})
            end
            if client:is_key_down(30) or client:is_key_down(32) then
                player:set_speed(event, player:get_speed() * 0.543)
            else
                player:set_speed(event, player:get_speed() * 0.6345)
            end
            gonnadoit = true
            didit = false
            tickfr = tick + 1
        end
        if player:is_on_ground() == false or client:is_key_down(31) == false or player:get_pitch() < 75 then
            if gonnadoit == true and modules:get_setting("safewalk", "spoofcrouch") == true then 
                player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
            end
            gonnadoit = false
            didit = true
        end
        if gonnadoit == true and tick >= tickfr then
            if didit == false and world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name == "tile.air" and player:is_on_ground() and client:is_key_down(31) then
                player:set_speed(event, 0)
            end
            if world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name ~= "tile.air" and gonnadoit == true then
                gonnadoit = false
                didit = true
                if modules:get_setting("safewalk", "spoofcrouch") == true then
                    player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
                end
            end
        end
    end;
    on_disable = function()
        tick = 0
        tickrn = 0
        if gonnadoit == true and modules:get_setting("safewalk", "spoofcrouch") == true then 
            player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
        end
        gonnadoit = false
        didit = true
    end 
}
modules:register("safewalk", "Safewalk", module)
modules:create_boolean_setting("safewalk", "spoofcrouch", "spoofcrouch", false) -- ABadName