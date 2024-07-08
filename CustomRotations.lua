local prevAngle;
local hasAttacked;
local rots = {};

local wrapAngleTo180 = function(value)
    value = value % 360.0;
    if (value >= 180.0) then
        value = value - 360.0;
    end
    if (value < -180.0) then
        value = value + 360.0;
    end
    return value;
end

module_manager.register("Custom Rotations", {
    on_enable = function()
        hasAttacked = false;
        prevAngle = 0;
    end,

    on_pre_motion = function(event)
        local target = player.kill_aura_target();
        if (module_manager.is_module_on("killaura") and target ~= nil) then
            world.entities();
            local width, height = world.width_height(target);
            local x, y, z = world.position(target);
            local diff = { x - event.x,
                y + ((module_manager.option("Custom Rotations", "Pitch Correction") / 100.0) * height) -
                    (y + player.eye_height()), z - event.z };
            local angle = math.deg(math.atan(diff[3], diff[1]));
            local yaw = angle - 90.0;
            local pitch = -math.deg(math.atan(diff[2], player.distance_to_entity(target)));
            local cYaw = player.angles();
            if (hasAttacked or not module_manager.option("Custom Rotations", "Rotate on Attack")) then
                if (
                    module_manager.option("Custom Rotations", "Rotate on Attack") or
                        math.abs(angle - prevAngle) > module_manager.option("Custom Rotations", "Angle Step")) then
                    rots[1] = cYaw + wrapAngleTo180(yaw - cYaw);
                    rots[2] = wrapAngleTo180(pitch);
                    prevAngle = angle;
                    hasAttacked = false;
                end
            end
            event.yaw = rots[1];
            event.pitch = rots[2];
        end
        return event;
    end,

    on_send_packet = function(event)
        if (module_manager.is_module_on("killaura") and player.kill_aura_target() ~= nil) then
            if (event.packet_id == 0x02) then
                if (event.action == 2) then
                    hasAttacked = true;
                end
            end
        end
    end
});

module_manager.register_boolean("Custom Rotations", "Rotate on Attack", true);
module_manager.register_number("Custom Rotations", "Angle Step", 0, 180, 15);
module_manager.register_number("Custom Rotations", "Pitch Correction", 0, 100, 25);
