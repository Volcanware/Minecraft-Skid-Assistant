local module = {
    on_update = function(event)
        if not event.pre or not modules.is_enabled("ScaffoldWalk") then
            return;
        end
        event.pitch = 90;
        if client.is_key_down(31) == true or client.is_key_down(30) == true or client.is_key_down(32) == true or client.is_key_down(17) == true then
            event.pitch = 81.5;
        end
        if client.is_key_down(30) == true then
            if client.is_key_down(31) == false and client.is_key_down(17) == false then
                event.yaw = event.yaw % 360 + 90;
            end
            if client.is_key_down(31) == true then
                event.yaw = event.yaw % 360 + 45 
            end
            if client.is_key_down(17) == true then
                event.yaw = event.yaw % 360 + 135
            end
        end
        if client.is_key_down(32) == true then
            if client.is_key_down(31) == false and client.is_key_down(17) == false then
                event.yaw = event.yaw % 360 + 270;
            end
            if client.is_key_down(31) == true then
                event.yaw = event.yaw % 360 + 315
            end
            if client.is_key_down(17) == true then
                event.yaw = event.yaw % 360 + 225
            end
        end
        if client.is_key_down(31) == false and client.is_key_down(30) == false and client.is_key_down(32) == false then
            event.yaw = event.yaw + 180;
        end
    end
};
modules.register("sillyrots", "sillyrots", module)
--
--           0
--      -45     +45
--   -90           +90
--     -135     +135
--        -/+180
--
--            0
--     +315       +45
--   +270           +90
--    +225        +135
--          +180