local module = {
    on_packet_send = function(event)
        if event.packet_id == 0x0f then
            if player:get_hurt_time() > 0 then
                event.cancel = true
            end
            
        end
end
}
modules:register("VulcanVelocity", "Vulcan Velocity", module)
