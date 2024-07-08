-- pasted round function
    function round(num, dec)
    local shift = 10^(dec or 2)
    num = math.floor(num * shift + 0.5) / shift
    if num == math.floor(num) then num = tostring(num.."."..("0"):rep(dec)) end
    return num
  end
  
  -- lennox math cause slow
  function health_relative(entityID)
    health = world.health(entityID)
    local max_health = world.max_health(entityID)
    local calculated_relative = math.max(0.0, math.min(health, max_health) / max_health)
    return calculated_relative
  end
  
  function health_color(entityID)
    return hsl_to_rgb(health_relative(entityID) * 360 / 3, 1, 0.5);
  end
  
  function hsl_to_rgb(h, s, l)
      if s == 0 then return l,l,l end
  
      h, s, l = h / 360 * 6, math.min(math.max(0, s), 1), math.min(math.max(0, l), 1)
  
      local c = (1 - math.abs(2 * l - 1)) *s
      local x = (1 - math.abs(h % 2 - 1)) *c
      local m,r,g,b = (l - .5 * c), 0, 0, 0
  
      if h < 1 then
      r,g,b = c,x,0
      elseif h < 2 then
      r,g,b = x,c,0
      elseif h < 3 then
      r,g,b = 0,c,x
      elseif h < 4 then
      r,g,b = 0,x,c
      elseif h < 5 then
      r,g,b = x,0,c
      else
      r,g,b = c,0,x
      end
  
      return {math.ceil((r + m) *256), math.ceil((g + m) * 256), math.ceil((b + m) * 256)}
  end
  
  function darker(r,g,b, dark)
    local rR = math.max(math.min(r * dark, 255), 0)
    local gR = math.max(math.min(g * dark, 255), 0)
    local bR = math.max(math.min(b * dark, 255), 0)
    return {rR,gR,bR}
  end
  
  function exponential_anim(current, intended, speed, gain)
    local returning = current
  
    if current < intended then
      returning = current + (current - intended) * -speed * fps_multiplier() * gain
    elseif current > intended then
      returning = current - (current - intended) * speed * fps_multiplier()
    else
      returning = current
    end
  
    return returning
  end
  
  function fps_multiplier()
    return 60 / client.fps()
  end

  function f()
    local n, v = player.over_mouse()
    if n == 3 then return v end
    return nil
    end
  
local healthbar = 0
local hurttimething = 0
local besttargethudever = {
    on_render_screen = function(ctx)
        world.entities()
        health = world.health(target)
        target = f()
        
        -- target check
        if target == nil and client.gui_name() == "chat" or client.gui_name() == "astolfo" then
          target = player.id()
          health = world.health(target)
        elseif target == nil then
          return
        end

        local optionY = module_manager.option('RiseTargetHUD','Pos-Y')
        local optionX = module_manager.option('RiseTargetHUD','Pos-X')

        local width = 145
        local height = 55
        local x = (ctx.width - width) / 2
        local y = ctx.height / 2 + 50

        -- targethud misc
        local entity_name = world.name(target)
        local bar_width = width - 30

        local namestringwidth = render.get_string_width(entity_name)

        if health ~= nil then
            local calc_bar_width = bar_width * health_relative(target)
            local hurt_time_alpha = ((world.hurt_time(target) * 25) + 5)
            hurttimething = exponential_anim(hurttimething, hurt_time_alpha, 0.7, 1)
            healthbar = exponential_anim(healthbar, calc_bar_width, 0.4, 1)

            if namestringwidth > 56 then
                render.rect(optionX, optionY, optionX + width + namestringwidth * 0.3, optionY + height, 0, 0, 0, 70)
            elseif namestringwidth <= 56 then
                render.rect(optionX, optionY, optionX + width, optionY + height, 0, 0, 0, 70)
            end
            render.player_head(optionX + 5, optionY + 5, 33, target)
            render.rect(optionX + 5, optionY + 5, optionX + 38, optionY + 38, 102, 4, 6, hurttimething)
            render.string("Name ".. entity_name .."", optionX + 43, optionY + 12, 255, 255, 255, 255)
            render.string("Distance ".. round(player.distance_to_entity(target), 1) .."", optionX + 43, optionY + 22, 255, 255, 255, 255)
            render.string("Hurt ".. world.hurt_time(target) .."", optionX + 106, optionY + 22, 255, 255, 255, 255)
            render.rect(optionX + 5, optionY + height - 9, optionX + healthbar, optionY + height - 4, 20, 166, 172, 255)
            render.string(round(health, 1), optionX + healthbar + 5, optionY + height - 10.5, 255, 255, 255, 255)
        end

        if input.is_mouse_down(0) then if ctx.mouse_x >= math.floor(optionX) and ctx.mouse_x <= math.floor((optionX+width)) then
  					if ctx.mouse_y >= math.floor(optionY) and ctx.mouse_y <= math.floor((optionY+height)) then
  						if client.gui_name() == "chat" or client.gui_name() == "astolfo" then
  							xPos = (ctx.mouse_x - width /2)
  							yPos = (ctx.mouse_y - height /2)
  							player.message('.RiseTargetHUD Pos-X '..math.floor(xPos))
  							player.message('.RiseTargetHUD Pos-Y '..math.floor(yPos))
  						end
  					end
        end
    end
end
}

module_manager.register("RiseTargetHUD", besttargethudever)
module_manager.register_number('RiseTargetHUD', 'Pos-X', 0, 384000, 375)
module_manager.register_number('RiseTargetHUD', 'Pos-Y', 0, 216000, 300)