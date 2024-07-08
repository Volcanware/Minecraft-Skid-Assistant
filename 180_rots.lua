local module = {
    on_update = function(event)
        if not event.pre or not modules:is_enabled("ScaffoldWalk") then
            return;
        end

        event.yaw = event.yaw % 360 - 180;
        event.pitch = 87.4;
    end
};

modules:register("180_rots", "180_rots", module); -- made by oad#4558