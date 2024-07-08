local function is_holding_sword()
    local item = inventory.get_item(player.get_held_item_slot())
    return item and item.name:lower():find('sword')
end

local blocking = false

modules.register("moofab", "Moof's AB", "Test AB", {
    on_update = function(e)
        if e.pre then
            if is_holding_sword() then
                if client.current_kill_aura_target() ~= -1 then
                    local target = client.current_kill_aura_target()
        
                    player.send_packet_no_event(0x02, {target, enums.interact_actions.INTERACT_AT})
                    player.send_packet_no_event(0x02, {target, enums.interact_actions.INTERACT})
                    player.send_packet_no_event(0x08, {types.vec3i.new(-1, -1, -1), 255, player.get_held_item_slot(), 0, 0, 0})

                    blocking = true
                end
            else
                blocking = false
            end
    
            if blocking and client.current_kill_aura_target() == -1 then
                player.send_packet_no_event(0x07, {enums.digging_action.RELEASE_USE_ITEM, types.vec3i.new(0,0,0), enums.facing.DOWN})
                blocking = false
            end
        end
    end
})