local module = {
    on_tick = function(event)
        if modules:is_enabled("Timer") == false then
            client:set_timer_speed(1)
        end
    end
}
modules:register("TimerFix", "Timer Fix", module)
