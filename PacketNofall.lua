local module = {
    on_tick = function(event)
        if player:get_fall_distance() >= 2 then
            player:send_packet(0x03, {true});
        end
    end
}
modules:register("PacketNofall", "Packet Nofall", module)