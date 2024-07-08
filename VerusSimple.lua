local ticks = 0
local lticks = 0
local autism = false
local damaged = false

local function moving()
    local input = player:get_input()
    return not (input.x == 0 and input.y == 0)
end

local module = {
    on_move = function(event)
        if moving() and damaged and ticks <= 26 then
            player:set_speed(event, modules:get_setting("VerusSimple", "Speed"))
        end
        if not damaged then
            player:set_speed(event, 0)
        end
        if moving() and damaged and ticks >= 27 then
            player:set_motion_y(0)
            player:set_speed(event, player:get_base_move_speed() * 1.40042069)--four twenty 6ix 9ine
        end
    end,
    on_tick = function()
        if damaged then
            ticks = ticks + 1
        end
        if damaged and ticks >= 1 and ticks <= 27 then
            if ticks - lticks == 2 then
                lticks = ticks
                player:jump()
            end
        end
    end,
    on_packet_receive = function(event)
        if event.packet_id == 0x12 then
            if event.entity_id ~= player:get_id() then -- thank u oad
                return
            end
            if damaged and player:is_on_ground() and ticks >= 2 then
                modules:toggle("VerusSimple", false)
            end
            damaged = true
            if damaged and not autism then --autism was for smth else ignore
                player:jump()
                player:set_motion_y(2)
            end
        end
    end,
    on_enable = function(ctx)
        ticks = 0
        damaged = false
        lticks = 2
    end
}
-- Register
modules:register("VerusSimple", "Verus Longjump", module)
modules:create_float_setting("VerusSimple", "Speed", "Boost Speed", 8, 0.4, 8, 0.1)