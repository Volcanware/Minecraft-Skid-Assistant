local module = {
    on_tick = function()
        if player:is_on_ground() and (client:is_key_down(17) or client:is_key_down(30) or client:is_key_down(31) or client:is_key_down(32)) then
            player:jump()
        end
        if player:get_motion_y() <= 0.2 and player:get_motion_y() >= -0.05 then
            player:set_motion_y(-0.5)
        end
    end;
    on_disable = function()
        client:set_timer_speed(1)
    end;
    on_packet_receive = function(event)
        if event.packet_id == 0x08 then
            client:print("<yellow>Disabled speed due to flag")
            modules:set_state("matrix", false)
        end
    end

}
modules:register("matrix", "matrix", module)