modules.register("mmctower", "MMC Tower", "Mine Men Club Tower", {
    
    on_packet_send = function(event)
        if event.packet_id == 0x08 and event.y == math.floor(player.get_position_y()-1.4) and client.is_key_down(57) and modules.is_enabled("scaffoldwalk") then
            player.set_motion_y(0.42)
        end
    end
})