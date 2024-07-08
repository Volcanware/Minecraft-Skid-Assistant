
        -- health indicator made by xzto

local name = 'Velocity\194\1677 0.0% 0.0%'
local a = 20
local health = 0
local ticks = 0
local notnil = false
local vari 
local color 
local anti = {
    on_pre_motion = function(t)
        if player.hurt_time() > 0 then
            color =  ' \194\167c'
        else
            color = ' \194\167a'
        end
        ticks = ticks + 1
        health = player.health()
        if post < health then
            hel = health - post
            dmg = false
            a = ticks
        end
        if post > health then
            hel = post - health
            dmg = true
            a = ticks
        end
        if hel == nil then
            notnil = false
        else
            notnil = true
        end
		if health >= 15 then -- makes the color different depending on your health
			vari = ' \194\167a'
		elseif health < 15 and health >= 10 then
			vari = ' \194\167e'
		elseif health < 10 and health >= 5 then
			vari = ' \194\1676'
        elseif health < 5 then
		  	vari = ' \194\167c'
		end
    end,
    on_post_motion = function(t)
        post = player.health()
    end,
    on_render_screen = function(t)
        render.scale(1)
        if vari ~= nil then
            render.string_shadow(vari..tostring(math.floor(health)), (t.width / 2) - (render.get_string_width(vari..tostring(math.floor(health))) / 2) - module_manager.option(name, "Offset"), (t.height/2 + 24)/1, 255,255,255, 255)
        end
        render.scale(1/1)
        if module_manager.option(name, 'Hurttime') then
            render.scale(1)
            render.string_shadow("Hurttime:"..color..tostring(player.hurt_time()), 5, 5, 255, 255, 255, 255)
            render.scale(1/1) 
        end
    end,
    on_receive_packet = function(t)
        if t.packet_id == 0x12 and t.entity_id == player.id() and module_manager.option(name, "Debug") then
            client.print("\194\167cKnockback tick: "..tostring(player.ticks_existed()))
        end
    end
}

module_manager.register(name, anti)
module_manager.register_boolean(name, "Hurttime", false) --hurttime indicator
module_manager.register_boolean(name, "Debug", false)
module_manager.register_number(name, "Offset", -4,3,0)


module_manager.register("Target Strafe\194\1677 Adaptive", {}) -- for arraylist


module_manager.register('AntiKnockBack', { -- velo
    on_receive_packet = function(t)
        player.message(".velocity vertical 0")
        player.message(".velocity horizontal 0")
        if t.packet_id == 0x12 and t.entity_id == player.id() then
            local mx,my,mz = player.motion()
            t.cancel = true
            player.set_motion(mx, t.motion_y / 8000, mz)
        end
    end
})
