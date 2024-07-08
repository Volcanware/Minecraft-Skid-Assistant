local tick = 0
local tickfr = 0
local gonnadoit = false
local didit = true
local sneaking = false
local var = nil
local variable = nil

local module = {
    on_packet_send = function(event)
        if event.packet_id == 0x08 then
            var = types.vec3d:new(event.x, event.y, event.z)
            variable = player:ray_cast(player:get_yaw(), player:get_pitch(), 4.2)
            if var ~= nil and variable ~= nil and var == variable.pos and sneaking == true and not player:is_sneaking() then
                player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
                sneaking = false
            end
        end
    end;
    on_tick = function()
        tick = tick + 1
    end;
    on_move = function(event)
        if sneaking == true and not player:is_sneaking() and (client:is_key_down(30) or client:is_key_down(32)) and client:is_key_down(31) then
            player:set_speed(event, 0.0915)
        elseif sneaking == true and not player:is_sneaking() and (not client:is_key_down(30) or not client:is_key_down(32)) and client:is_key_down(31) then
            player:set_speed(event, 0.065)
        end
        if player:is_on_ground() == false or client:is_key_down(31) == false or player:get_pitch() < 75 then
            if gonnadoit == true and sneaking == true and not player:is_sneaking() then 
                player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
                sneaking = false
            end
            tick = 0
            gonnadoit = false
            didit = true
        end
        if player:get_pitch() >= 70 and world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name == "tile.air" and gonnadoit == false and player:is_on_ground() and inventory:get_item(player:get_held_item_slot()) ~= nil and string.match(inventory:get_item(player:get_held_item_slot()).name, "tile.") and client:is_key_down(31) then
            if sneaking == false and not player:is_sneaking() then
                player:send_packet(0x0B, {player:get_id(), enums.entity_action.START_SNEAKING, 0})
                sneaking = true
            end
            if client:is_key_down(30) or client:is_key_down(32) then
                if modules:get_setting("legitscaffold", "setrots") then
                    player:set_pitch(76.5)
                end
            else
                player:set_pitch(79.5)
            end
            gonnadoit = true
            didit = false
            tickfr = tick + 1
        end
        if gonnadoit == true and tick >= tickfr then
--            if didit == false and world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name == "tile.air" and player:is_on_ground() and client:is_key_down(31) then
--                player:set_speed(event, 0)
--            end
            if world:get_block_at(player:get_position_x(), player:get_position_y() - 0.4, player:get_position_z()).name ~= "tile.air" and gonnadoit == true then
                gonnadoit = false
                didit = true
                if sneaking == true and not player:is_sneaking() then
                    player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
                    sneaking = false
                end
            end
        end
    end;
    on_disable = function()
        tick = 0
        tickrn = 0
        if gonnadoit == true and sneaking == true and not player:is_sneaking() then 
            player:send_packet(0x0B, {player:get_id(), enums.entity_action.STOP_SNEAKING, 0})
            sneaking = false
        end
        gonnadoit = false
        didit = true
    end 
}
modules:register("legitscaffold", "Legit Scaffold", module)
modules:create_boolean_setting("legitscaffold", "setrots", "Auto-Rotate", true) -- BadName