function tablelength(T)
  local count = 0
  for _ in pairs(T) do count = count + 1 end
  return count
end

has_value = function(tab, val)
    for index, value in ipairs(tab) do
        if value == val then
            return true
        end
    end
    return false
end

-- slighty changed anim bar pasted from NotDuckie
local animatedrainbow = 1
local function drawAnimatedRainbow(x, y, width, height, steps)
    local hI = 55/steps
    for i=1, steps do
        local a = width/steps

        local rgb = hsl_to_rgb((hI*i) + animatedrainbow / 2, 1, 0.6)
        render.rect(x+(a*i)-a, y, x+(a*i)+1, y+height, rgb[1], rgb[2], rgb[3], 255)
    end
end

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

function exponential_anim(current, intended, speed)
  local returning = current

  if current < intended then
    returning = current + (current - intended) * -speed * fps_multiplier()
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

local target
local animated_health_bar = 0
local animated_hurt_time_bar = 0;
local animated_hurt_time = 0;
local xPos
local yPos
local new_target
local targetList = {}


function f()
  local n, v = player.over_mouse()
  if n == 3 then return v end
  return nil
  end

local TargetHUD = {
  on_enable = function()
    player.message('.killaura Render HUD false')
  end,


  on_pre_update = function ()
    if animatedrainbow < 359 then
        animatedrainbow = animatedrainbow - 5
    else
        animatedrainbow = 359
    end
  end,

  on_render_screen = function(ctx, partial_ticks, width, height, scale_factor, mouse_x, mouse_y)
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
    -- targethud position settings
    local optionY = module_manager.option('TargetHUDs','Pos-Y')
    local optionX = module_manager.option('TargetHUDs','Pos-X')
    local shift = 0;

    -- targethud bounds
    local width = 200
    local height = 80
    local x = (ctx.width - width) / 2
    local y = ctx.height / 2 + 50

    -- targethud misc
    local entity_name = world.name(target)
    local bar_width = width - 20

    if health ~= nil then
      if module_manager.option("TargetHUDs", "Default HUD") then
        height = 55;
        width = 150
        bar_width = width
        -- hurttime and health bar
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local hurt_time_width = width * (world.hurt_time(target) / 9)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.3)
        animated_hurt_time_bar = exponential_anim(animated_hurt_time_bar, hurt_time_width, 0.4, 1)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.4, 1)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- background rect
        render.rect(optionX, optionY, optionX+ width, optionY + height + 3, 0, 0, 0, 170)
        -- render entity name
        render.string_shadow(entity_name, optionX + 5, optionY + 5, 255, 255, 255, 255)
        --render health bar background
        render.rect(optionX, optionY + 54, optionX + 150, optionY + 58, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 255)
        -- render the health bar
        render.rect(optionX, optionY + 54, optionX + math.ceil(animated_health_bar), optionY + 58, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity head
        if health >= 4 then
          render.player_head(optionX + math.ceil(animated_health_bar) - 29.5, optionY + height -31, 30, target)
        end
        if health >= 10 then
          -- render left health text
          render.string_shadow(round(health, 1), optionX + math.ceil(animated_health_bar) - 65, optionY +36, 255, 255, 255, 255)
          -- render left heart char
          render.scale(1.5)
          render.string_shadow(" \226\157\164" , math.ceil(optionX/1.5) + math.ceil(animated_health_bar/1.5) - 33, math.ceil(optionY/1.5) + 22, health_bar_color[1]/ 1.1, health_bar_color[2]/ 1.1, health_bar_color[3], 255)
          render.scale(1/1.5)
        elseif health < 10 then
          -- render right health text
          render.string_shadow(round(health, 1), optionX + math.ceil(animated_health_bar) + 17, optionY +36, 255, 255, 255, 255)
          -- render right heart char
          render.scale(1.5)
          render.string_shadow("\226\157\164" , math.ceil(optionX/1.5) + math.ceil(animated_health_bar/1.5) + 2, math.ceil(optionY/1.5) + 22, health_bar_color[1]/ 1.1, health_bar_color[2]/ 1.1, health_bar_color[3], 255)
          render.scale(1/1.5)
        end
        -- render winning or losing icon
        currentHealth = player.health()
        if health < currentHealth then
        render.scale(2)
        render.string_shadow("\234\156\178", optionX/2 + 66, optionY/2 + 1.5, 108, 195, 18, 255)
        render.scale(1/2)
        elseif health > currentHealth then
          render.scale(2)
          render.string_shadow("\226\154\160", optionX/2 + 66, optionY/2 + 1.5, 255, 51, 0, 255)
          render.scale(1/2)
        elseif health == currentHealth then
          render.scale(2)
          render.string_shadow("\226\159\129", optionX/2 + 66, optionY/2 + 1.5, 255, 255, 0, 255)
          render.scale(1/2)
        end
      elseif module_manager.option("TargetHUDs", "Novoline HUD") then
        height = 38;
        width = math.max(75, 75 + render.get_string_width(entity_name));
        x = (ctx.width - width) / 2
        y = ctx.height / 2 + 50

        -- health bar
        bar_width = width - 43
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.08)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- new target check
        if target ~= new_target then
          new_target = target
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- darker background
        render.rect(optionX - 1 ,optionY - 1.5,optionX + width + 1.5, optionY+height + 2.5, 29, 29, 29, 255)
        -- lighter background
        render.rect(optionX,optionY,optionX + width + .5, optionY+height + 1, 40, 40, 40, 255)
        -- render entity name
        render.string_shadow(entity_name, optionX + 41, optionY+ 4, 255, 255, 255, 255)
        -- render background health bar
        render.rect(optionX + 41, optionY + 15, optionX + 41 + bar_width, optionY + 24, 29, 29, 29, 255)
        -- render the health bar backdrop
        render.rect(optionX + 41, optionY + 15, optionX + 41 + animated_health_bar, optionY + 24, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 255)
        -- render the health bar
        render.rect(optionX + 41, optionY + 15, optionX + 41 + calc_bar_width, optionY + 24, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity head
        render.player_head(optionX + 1, optionY + 1, 37, target)
        -- render health text
        render.string_shadow(round(health, 1), optionX + 41, optionY + 29, 255, 255, 255, 255)
        -- render health char
        kek = render.get_string_width(round(health, 1))
        render.scale(1.15)
        render.string_shadow(" \226\157\164", (optionX + 41 + kek) / 1.15, (optionY + 28) /1.15, health_bar_color[1]/ 1.1, health_bar_color[2] / 1.1, health_bar_color[3], 255)
        render.scale(1/1.15)
      elseif module_manager.option("TargetHUDs", "Novoline New HUD") then
        height = 35;
        width = math.max(75, 75 + render.get_string_width(entity_name));
        healthLength = render.get_string_width(math.ceil(round((health /2) * 10,1)))
        x = (ctx.width - width) / 2
        y = ctx.height / 2 + 50

        -- health bar
        bar_width = width - 41
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color =  health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.08)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- new target check
        if target ~= new_target then
          new_target = target
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- darker background
        render.rect(optionX - 1 ,optionY - 1.5,optionX + width + 1.5, optionY+height + 2.5, 29, 29, 29, 255)
        -- lighter background
        render.rect(optionX,optionY,optionX + width + .5, optionY+height + 1, 40, 40, 40, 255)
        -- render entity name
        render.string_shadow(entity_name, optionX + 39, optionY+ 4, 255, 255, 255, 255)
        -- render background health bar
        render.rect(optionX + 38.5, optionY + 16.5, optionX + 38.5 + bar_width, optionY + 27.5, 29, 29, 29, 255)
        -- render the health bar backdrop
        render.rect(optionX + 38.5, optionY + 16.5, optionX + 38.5 + animated_health_bar, optionY + 27.5, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 255)
        -- render the health bar
        render.rect(optionX + 38.5, optionY + 16.5, optionX + 38.5 + calc_bar_width, optionY + 27.5, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity head
        render.player_head(optionX + 1, optionY + 1, 34.5, target)
        -- render health text
        render.string_shadow(round((health /2) * 10,1) .. "%", optionX + 38.5 + bar_width / 2 - healthLength + 2, optionY + 18, 255, 255, 255, 255)
      elseif module_manager.option("TargetHUDs", "Moon HUD") then
        height = 32;
        width = math.max(115, 60 + render.get_string_width(entity_name));
        x = (ctx.width - width) / 2
        y = ctx.height / 2 + 50
        default = {255, 255, 255}

        -- health bar
        bar_width = width - 36
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3],  0.5)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.3, 1)

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          default = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
        end

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- white bar
        render.rect(optionX - .5 ,optionY - 1,optionX + width + .5, optionY+ height - 32, default[1], default[2], default[3], 255)
        -- lighter background
        render.rect(optionX - .5,optionY,optionX + width + .5, optionY+height, 0, 0, 0, 125)
        -- render entity name
        render.scale(1.15)
        render.string(entity_name, optionX /1.15 + 30, optionY/1.15 + 3, 255, 255, 255, 255)
        render.scale(1/1.15)
        -- render background health bar
        render.rect(optionX + 33.5, optionY + 25.5, optionX + 34.5 + bar_width, optionY + 27.5, 15, 15, 15, 180)
        -- render the health bar
        render.rect(optionX + 34, optionY + 26, optionX + 34 + animated_health_bar, optionY + 27, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity player
        render.player(optionX + 18, optionY + 30, 14, 0, 0, target)
        -- render health text
        render.scale(0.91)
        render.string(round(health, 1).. " HP", optionX/0.91 + 38, optionY/0.91 + 17, 255, 255, 255, 255)
        render.scale(1/0.91)
      elseif module_manager.option("TargetHUDs",'Astolfo Old HUD') then
        height = 47
        width = 122

        -- health bar
        bar_width = width - 30
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.3, 1)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
        end

        -- lighter background
        render.rect(optionX - .5,optionY,optionX + width + .5, optionY+height, 0, 0, 0, 125)
        -- render entity name
        render.string_shadow(entity_name, optionX + 25, optionY + 5, 255, 255, 255, 255)
        -- render the health bar
        render.rect(optionX + 25, optionY + 15, optionX + 25 + animated_health_bar, optionY + 26, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity player
        render.player(optionX + 12, optionY + 43, 19, 0, 0, target)
         --render health text
        render.string_shadow((round(health / 2, 1)), optionX + 62, optionY + 16, health_bar_color[1]/ 1.1, health_bar_color[2]/ 1.1, health_bar_color[3], 255)
      elseif module_manager.option("TargetHUDs", 'Astolfo Large HUD') then
        height = 70
        width = 199

        -- health bar
        bar_width = width - 5
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.3, 1)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- lighter background
        render.rect(optionX - .5,optionY,optionX + width + .5, optionY+height, 0, 0, 0, 125)
        -- render entity name
        render.string_shadow(entity_name, optionX + 45, optionY + 5, 255, 255, 255, 255)
        -- render the health bar background
        render.rect(optionX + 2.5, optionY + 60, optionX + 197, optionY + 66.5, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 255)
        -- render the health bar
        render.rect(optionX + 2.5, optionY + 60, optionX + 2.5 + animated_health_bar, optionY + 66.5, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render the entity player
        render.player(optionX + 21, optionY + 56, 27, -25, 0, target)
         --render health text
        render.scale(2)
        scale = 2
        render.string_shadow((round(health / 2, 1) .. " \226\157\164"), optionX /2 + 22, optionY /2 + 9, health_bar_color[1]/ 1.1, health_bar_color[2]/ 1.1, health_bar_color[3], 255)
        render.scale(1/2)
      elseif module_manager.option("TargetHUDs", 'Exhibition HUD') then
        height = 35;
        width = math.max(110, 45 + render.get_string_width(entity_name));
        x = (ctx.width - width) / 2
        y = ctx.height / 2 + 50

        -- health bar
        bar_width = 62
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- light dark border outline
        render.rect(optionX - 4 ,optionY - 4.5,optionX + width + 4.5, optionY+height + 5.5, 10, 10, 10, 255)
        -- light border outline
        render.rect(optionX - 3.5 ,optionY - 4,optionX + width + 4, optionY+height + 5, 60, 60, 60, 255)
        -- light border
        render.rect(optionX - 3 ,optionY - 3.5,optionX + width + 3.5, optionY+height + 4.5, 40, 40, 40, 255)
        -- light background outline
        render.rect(optionX - 1.5 ,optionY - 2,optionX + width + 2, optionY+height + 3, 60, 60, 60, 255)
        -- dark inside background
        render.rect(optionX - 1 ,optionY - 1.5,optionX + width + 1.5, optionY+height + 2.5, 22, 22, 22, 255)
        --render background health bar
        render.rect(optionX + 37.5, optionY + 12, optionX + 37.5  + bar_width, optionY+height -19, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 155)
        --render the health bar
        render.rect(optionX + 37.5, optionY + 12, optionX + 37.5 + calc_bar_width, optionY+height -19 , health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        width2 = 79
        render.rect(optionX + .5,optionY,optionX + width2 - 42.5, optionY+height + 1, 10, 10, 10, 255)
        -- entity render square light inside
        render.rect(optionX + 1,optionY + .5,optionX + width2 - 43, optionY+height + .5, 48, 48, 48, 255)
        -- entity render square dark inside
        render.rect(optionX + 1.5,optionY + 1,optionX + width2 - 43.5, optionY+height -.2, 17, 17, 17, 255)
        --render entity name
        render.string_shadow(entity_name, optionX + 38, optionY+ 2, 255, 255, 255, 255)
        --1 black bar
        render.rect(optionX + 37.5,optionY + 12,optionX + width2 - 41, optionY+height -19, 0, 0, 0, 255)
        --2 black bar
        render.rect(optionX + 44.5,optionY + 12,optionX + width2 - 34, optionY+height -19, 0, 0, 0, 255)
        --3 black bar
        render.rect(optionX + 50.5,optionY + 12,optionX + width2 - 28, optionY+height -19, 0, 0, 0, 255)
        --4 black bar
        render.rect(optionX + 56.5,optionY + 12,optionX + width2 - 22, optionY+height -19, 0, 0, 0, 255)
        --5 black bar
        render.rect(optionX + 62.5,optionY + 12,optionX + width2 - 16, optionY+height -19, 0, 0, 0, 255)
        --6 black bar
        render.rect(optionX + 68.5,optionY + 12,optionX + width2 - 10, optionY+height -19, 0, 0, 0, 255)
        --7 black bar
        render.rect(optionX + 74.5,optionY + 12,optionX + width2 - 4, optionY+height -19, 0, 0, 0, 255)
        --8 black bar
        render.rect(optionX + 80.5,optionY + 12,optionX + width2 + 2, optionY+height -19, 0, 0, 0, 255)
        --9 black bar
        render.rect(optionX + 86.5,optionY + 12,optionX + width2 + 8, optionY+height -19, 0, 0, 0, 255)
        --10 black bar
        render.rect(optionX + 92.5,optionY + 12,optionX + width2 + 14, optionY+height -19, 0, 0, 0, 255)
        --11 black bar
        render.rect(optionX + 99,optionY + 12,optionX + width2 + 20.5, optionY+height -19, 0, 0, 0, 255)
        --bottom black bar
        render.rect(optionX + 37.5,optionY + 15.5,optionX + width2 + 20.5, optionY+height -19, 0, 0, 0, 255)
        --top black bar
        render.rect(optionX + 37.5,optionY + 12,optionX + width2 + 20.5, optionY+height -22.5, 0, 0, 0, 255)
        --render entity
        render.player(optionX + 18.5, optionY + 33.5, 16, 0, 0, target)
        --render health text and distance to entity
        render.scale(.6)
        render.string_shadow("HP: " ..round(health /2, 1) .. " \226\157\152 Dist: ".. math.floor(player.distance_to_entity(target)), optionX/.6 + 65, optionY/.6 + 30, 255, 255, 255, 255)
        render.scale(1/.6)
      elseif module_manager.option('TargetHUDs', 'Flux HUD') then
        height = 29.5

        -- render multiple huds
        max_targets = module_manager.option('killaura', 'Max-Targets')
        range = module_manager.option('killaura', 'Block-Range')
        if world.health(target) ~= nil then
          if not has_value(targetList, target) then
            table.insert(targetList, target)
          end
          if target == nil or module_manager.is_module_on("killaura") and client.gui_name() == "chat" or client.gui_name() == "astolfo" then
            if not has_value(targetList, target) then
              table.insert(targetList, player.id())
            end
          else
            for index, data in ipairs(targetList) do
              if data == player.id() then
                table.remove(targetList, index)
              end
            end
          end
            for index, data in ipairs(targetList) do
              if not world.is_invisible(data) then
                distanceToPlayer = player.distance_to_entity(data)
              end
              if world.health(data) == 0 or world.health(data) == nil or distanceToPlayer > range then
                table.remove(targetList, index)
              end
            end
            for index, data in ipairs(targetList) do
              if tablelength(targetList) > max_targets then
                table.remove(targetList, index)
              end
            end
          for index, data in ipairs(targetList) do
            distanceToPlayer = player.distance_to_entity(data)
            if index ~= nil and distanceToPlayer <= range then
              entity_name = world.name(data)
              local health = world.health(data)
              width = math.max(95, 60 + render.get_string_width(entity_name));
              bar_width = width - 39.5
              local calc_bar_width = bar_width * health_relative(data)
              local health_bar_color = health_color(data)
              local fake_armor_bar = math.min(bar_width, render.get_string_width(entity_name))
              animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, .10)

              if module_manager.option('TargetHUDs', 'Custom Color') then
                health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
                --darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
              end

              local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)

              -- bar out check
              if animated_health_bar > bar_width then
                animated_health_bar = calc_bar_width
              end

              shift = shift + 50
              -- right bar background
              render.rect(optionX + width - .5,shift + optionY + .5,optionX + width, shift + optionY+height - 2, 42, 42, 40, 180)
              -- right outer bar background
              render.rect(optionX + width,shift + optionY + 1,optionX + width + .5, shift + optionY+height - 2.5, 42, 42, 40, 180)
              -- left bar background
              render.rect(optionX - .5,shift + optionY + .5,optionX + width / width - 1, shift + optionY+height - 2, 42, 42, 40, 180)
              -- left outer bar background
              render.rect(optionX - 1,shift + optionY + 1,optionX + width / width - 1.5, shift + optionY+height - 2.5, 42, 42, 40, 180)
              -- top bar outline background
              render.rect(optionX,shift + optionY,optionX + width - .5, shift + optionY+height - 29, 42, 42, 40, 100)
              -- top bar outline shadow background
              render.rect(optionX + .5,shift + optionY - .5,optionX + width - 1, shift + optionY+height - 29.5, 42, 42, 40, 50)
              render.rect(optionX + 1,shift + optionY - .5,optionX + width - 1.5, shift + optionY+height - 29.5, 42, 42, 40, 35)
              -- bottom bar outline background
              render.rect(optionX,shift + optionY + 27.5,optionX + width - .5, shift + optionY+height - 1.5, 42, 42, 40, 100)
              -- bottom bar outline shadow background
              render.rect(optionX + .5,shift + optionY + height - 1.5,optionX + width - 1, shift + optionY+height - 1, 42, 42, 40, 50)
              render.rect(optionX + 1,shift + optionY + height - 1.5,optionX + width - 1.5, shift + optionY+height - 1, 42, 42, 40, 35)
              -- left bar outline background
              render.rect(optionX - 1, shift + optionY + 1,optionX + width / width - 1.5, shift + optionY+height - 2.5, 42, 42, 40, 100)
              -- right bar outline background
              render.rect(optionX + width, shift + optionY + 1,optionX + width  +.5, shift + optionY+height - 2.5, 42, 42, 40, 100)
              -- left bar shadow outline background
              render.rect(optionX - 1.5, shift + optionY + 1.5,optionX + width / width - 2, shift + optionY+height - 3, 42, 42, 40, 50)
              render.rect(optionX - 1.5, shift + optionY + 2,optionX + width / width - 2, shift + optionY+height - 3.5, 42, 42, 40, 35)
              -- right bar shadow outline background
              render.rect(optionX + width + .5, shift + optionY + 1.5,optionX + width  + 1, shift + optionY+height - 3, 42, 42, 40, 50)
              render.rect(optionX + width + .5, shift + optionY + 2,optionX + width + 1, shift + optionY+height - 3.5, 42, 42, 40, 35)
              -- center background
              render.rect(optionX,shift + optionY,optionX + width - .5, shift + optionY+height - 1.5, 42, 42, 40, 200)

              -- top right dot background
              render.rect(optionX + width - 1,shift + optionY,optionX + width - .5, shift + optionY+height - 29, 42, 42, 40, 235)
              -- top right inside dot background
              render.rect(optionX + width - 1.5,shift + optionY,optionX + width - 1, shift + optionY+height - 29, 42, 42, 40, 75)
              -- top inside right dot background
              render.rect(optionX + width - .5,shift + optionY + .5,optionX + width, shift + optionY+height - 28.5, 42, 42, 40, 100)
              render.rect(optionX + width - .5,shift + optionY + .5,optionX + width, shift + optionY+height - 28.5, 42, 42, 40, 75)
              -- top bottom right dot background
              render.rect(optionX + width,shift + optionY + 1,optionX + width + .5, shift + optionY+height - 28, 42, 42, 40, 235)
              -- top bottom right inside dot background
              render.rect(optionX + width,shift + optionY + 1.5,optionX + width + .5, shift + optionY+height - 27.5, 42, 42, 40, 75)
              -- top right outside shadow dot background
              render.rect(optionX + width - .5,shift + optionY ,optionX + width , shift + optionY+height - 29, 42, 42, 40, 125)
              -- top bottom right outside shadow dot background
              render.rect(optionX + width,shift + optionY + .5,optionX + width + .5, shift + optionY+height - 28.5, 42, 42, 40, 125)
              -- bottom right dot background
              render.rect(optionX + width - 1, shift + optionY + height - 2,optionX + width - .5, shift + optionY+height - 1.5, 42, 42, 40, 235)
              -- bottom right inside dot background
              render.rect(optionX + width - 1.5, shift + optionY + height - 2, optionX + width - 1, shift + optionY+height - 1.5, 42, 42, 40, 75)
              -- bottom inside right dot background
              render.rect(optionX + width - .5,shift + optionY + height - 2.5,optionX + width, shift + optionY+height - 2, 42, 42, 40, 100)
              render.rect(optionX + width - .5,shift + optionY + height - 2.5,optionX + width, shift + optionY+height - 2, 42, 42, 40, 75)
              -- bottom right dot background
              render.rect(optionX + width,shift + optionY + height - 3,optionX + width + .5, shift + optionY+height - 2.5, 42, 42, 40, 235)
              -- bottom right inside dot background
              render.rect(optionX + width,shift + optionY + height - 3.5,optionX + width + .5, shift + optionY+height - 3, 42, 42, 40, 75)
              -- bottom right outside shadow dot background
              render.rect(optionX + width - .5, shift + optionY + height - 2, optionX + width, shift + optionY+height - 1.5, 42, 42, 40, 125)
              -- bottom top right outside shadow dot background
              render.rect(optionX + width, shift + optionY + height - 2.5, optionX + width + .5, shift + optionY+height - 2, 42, 42, 40, 125)

              -- top left dot background
              render.rect(optionX,shift + optionY,optionX + width / width - .5, shift + optionY+height - 29, 42, 42, 40, 235)
              -- top left inside dot background
              render.rect(optionX + .5,shift + optionY,optionX + width / width, shift + optionY+height - 29, 42, 42, 40, 75)
              -- top inside left dot background
              render.rect(optionX- .5,shift + optionY + .5,optionX + width / width - 1, shift + optionY+height - 28.5, 42, 42, 40, 100)
              render.rect(optionX - .5,shift + optionY + .5,optionX + width / width - 1, shift + optionY+height - 28.5, 42, 42, 40, 75)
              -- top bottom left dot background
              render.rect(optionX - 1,shift + optionY + 1,optionX + width / width - 1.5, shift + optionY+height - 28, 42, 42, 40, 235)
              -- top bottom left inside dot background
              render.rect(optionX - 1,shift + optionY + 1.5,optionX + width / width - 1.5, shift + optionY+height - 27.5, 42, 42, 40, 75)
              -- top bottom left outside shadow dot background
              render.rect(optionX - 1,shift + optionY + .5,optionX + width / width - 1.5, shift + optionY+height - 28.5, 42, 42, 40, 125)
              -- top left outside shadow dot background
              render.rect(optionX - .5,shift + optionY ,optionX + width / width - 1, shift + optionY+height - 29, 42, 42, 40, 125)

              -- bottom left dot background
              render.rect(optionX, shift + optionY + height - 2,optionX + width / width - .5, shift + optionY+height - 1.5, 42, 42, 40, 235)
              -- bottom left inside dot background
              render.rect(optionX + .5,shift + optionY + height - 2,optionX + width / width, shift + optionY+height - 1.5, 42, 42, 40, 75)
              -- bottom inside left dot background
              render.rect(optionX - .5,shift + optionY + height - 2.5,optionX + width / width - 1, shift + optionY+height - 2, 42, 42, 40, 100)
              render.rect(optionX - .5,shift + optionY + height - 2.5,optionX + width / width - 1, shift + optionY+height - 2, 42, 42, 40, 75)
              -- bottom top bottom left dot background
              render.rect(optionX - 1,shift + optionY + height - 3,optionX + width / width - 1.5, shift + optionY+height - 2.5, 42, 42, 40, 235)
              -- bottom top bottom left inside dot background
              render.rect(optionX - 1,shift + optionY + height - 3.5,optionX + width / width - 1.5, shift + optionY+height - 3, 42, 42, 40, 75)
              -- bottom top bottom left outside shadow dot background
              render.rect(optionX - 1,shift + optionY + height - 2.5,optionX + width / width - 1.5, shift + optionY+height - 2, 42, 42, 40, 125)
              -- bottom top left outside shadow dot background
              render.rect(optionX - .5,shift + optionY + height - 2,optionX + width / width - 1, shift + optionY+height - 1.5, 42, 42, 40, 125)
              -- render entity name
              render.scale(0.8)
              render.string_shadow(entity_name, optionX/0.8+ 37, shift/0.8 + optionY/0.8 + 6, 255, 255, 255, 255)
              render.scale(1/0.8)
              -- render the health bar background
              render.rect(optionX + 36,shift + optionY + 14.5, optionX + width - 3.5, shift + optionY + 17.5, 0, 0, 0, 100)
              -- render the health bar background animation
              render.rect(optionX + 36,shift + optionY + 14.5, optionX + 36 + animated_health_bar, shift + optionY + 17.5, darker_health_bar_color[1], darker_health_bar_color[2], darker_health_bar_color[3], 255)
              -- render the health bar
              render.rect(optionX + 36,shift + optionY + 14.5, optionX + 36 + calc_bar_width, shift + optionY + 17.5, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
              -- render shield bar background
              render.rect(optionX + 36,shift + optionY + 21.5, optionX + width - 4, shift + optionY + 24.5, 0, 0, 0, 150)
              -- render fake shield bar
              render.rect(optionX + 36,shift + optionY + 21.5, optionX + 36 + fake_armor_bar, shift + optionY + 24.5, 61, 132, 234, 255)
              --render health text
              render.scale(0.7)
              render.string(round(health,1).." hp", optionX/0.7 + width/0.7 - render.get_string_width(math.ceil(round(health,1)))- 28, shift/0.7 + optionY/0.7 + 8, 255, 255, 255, 255)
              render.scale(1/0.7)
              --render heart
              render.rect(optionX + 31,shift + optionY + height - 15,optionX + 32, shift + optionY+height - 12, 255, 255, 255, 255)
              render.rect(optionX + 30.5,shift + optionY + height - 15.5,optionX + 31, shift + optionY+height - 12.5, 255, 255, 255, 255)
              render.rect(optionX + 30,shift + optionY + height - 15.5,optionX + 30.5, shift + optionY+height - 13, 255, 255, 255, 255)
              render.rect(optionX + 29.5,shift + optionY + height - 15.5,optionX + 30, shift + optionY+height - 13.5, 255, 255, 255, 255)
              render.rect(optionX + 29,shift + optionY + height - 14.5,optionX + 29.5, shift + optionY+height - 14, 255, 255, 255, 255)
              render.rect(optionX + 32,shift + optionY + height - 15.5,optionX + 32.5, shift + optionY+height - 12.5, 255, 255, 255, 255)
              render.rect(optionX + 32.5,shift + optionY + height - 15.5,optionX + 33, shift + optionY+height - 13, 255, 255, 255, 255)
              render.rect(optionX + 33,shift + optionY + height - 15.5,optionX + 33.5, shift + optionY+height - 13.5, 255, 255, 255, 255)
              render.rect(optionX + 33.5,shift + optionY + height - 14.5,optionX + 34, shift + optionY+height - 14, 255, 255, 255, 255)
              --render shadow for heart
              render.rect(optionX + 31,shift + optionY + height - 12,optionX + 32, shift + optionY+height - 11.5, 255, 255, 255, 185)
              render.rect(optionX + 30.5,shift + optionY + height - 12.5,optionX + 32.5, shift + optionY+height - 12, 255, 255, 255, 200)
              render.rect(optionX + 30,shift + optionY + height - 14.5,optionX + 33, shift + optionY+height - 12.5, 255, 255, 255, 225)
              render.rect(optionX + 29.5,shift + optionY + height - 13.5,optionX + 33.5, shift + optionY+height - 13, 255, 255, 255, 230)
              render.rect(optionX + 29,shift + optionY + height - 14,optionX + 34, shift + optionY+height - 13.5, 255, 255, 255, 205)
              render.rect(optionX + 29,shift + optionY + height - 15,optionX + 34, shift + optionY+height - 14.5, 255, 255, 255, 205)
              render.rect(optionX + 31,shift + optionY + height - 15.5,optionX + 32, shift + optionY+height - 15, 255, 255, 255, 205)
              render.rect(optionX + 30,shift + optionY + height - 16,optionX + 30.5, shift + optionY+height - 15.5, 255, 255, 255, 125)
              render.rect(optionX + 29.5,shift + optionY + height - 16,optionX + 30, shift + optionY+height - 15.5, 255, 255, 255, 50)
              render.rect(optionX + 30.5,shift + optionY + height - 16,optionX + 31, shift + optionY+height - 15.5, 255, 255, 255, 100)
              render.rect(optionX + 32.5,shift + optionY + height - 16,optionX + 33, shift + optionY+height - 15.5, 255, 255, 255, 125)
              render.rect(optionX + 33,shift + optionY + height - 16,optionX + 33.5, shift + optionY+height - 15.5, 255, 255, 255, 50)
              render.rect(optionX + 32,shift + optionY + height - 16,optionX + 32.5, shift + optionY+height - 15.5, 255, 255, 255, 100)
              render.rect(optionX + 29,shift + optionY + height - 15.5,optionX + 34, shift + optionY+height - 15, 255, 255, 255, 65)
              render.rect(optionX + 29,shift + optionY + height - 13.5,optionX + 34, shift + optionY+height - 13, 255, 255, 255, 50)
              render.rect(optionX + 29.5,shift + optionY + height - 13,optionX + 33.5, shift + optionY+height - 12.5, 255, 255, 255, 40)
              render.rect(optionX + 30,shift + optionY + height - 12.5,optionX + 33, shift + optionY+height - 12, 255, 255, 255, 30)
              --render shield
              render.rect(optionX + 31,shift + optionY + height - 9,optionX + 32, shift + optionY+height - 4.5, 255, 255, 255, 255)
              render.rect(optionX + 30.5,shift + optionY + height - 8.5,optionX + 31.5, shift + optionY+height - 5, 255, 255, 255, 255)
              render.rect(optionX + 30,shift + optionY + height - 8.5,optionX + 32, shift + optionY+height - 5.5, 255, 255, 255, 255)
              render.rect(optionX + 29.5,shift + optionY + height - 8.5,optionX + 30.5, shift + optionY+height - 6.5, 255, 255, 255, 255)
              render.rect(optionX + 32,shift + optionY + height - 8.5,optionX + 32.5, shift + optionY+height - 5, 255, 255, 255, 255)
              render.rect(optionX + 32.5,shift + optionY + height - 8.5,optionX + 33, shift + optionY+height - 5.5, 255, 255, 255, 255)
              render.rect(optionX + 32,shift + optionY + height - 8.5,optionX + 33.5, shift + optionY+height - 6.5, 255, 255, 255, 255)
              --render shadow for shield
              render.rect(optionX + 31,shift + optionY + height - 5,optionX + 32, shift + optionY+height - 4, 255, 255, 255, 125)
              render.rect(optionX + 30.5,shift + optionY + height - 5,optionX + 31, shift + optionY+height - 4.5, 255, 255, 255, 195)
              render.rect(optionX + 30,shift + optionY + height - 5.5,optionX + 30.5, shift + optionY+height - 5, 255, 255, 255, 195)
              render.rect(optionX + 29.5,shift + optionY + height - 6,optionX + 30, shift + optionY+height - 5.5, 255, 255, 255, 125)
              render.rect(optionX + 29.5,shift + optionY + height - 6.5,optionX + 30, shift + optionY+height - 6, 255, 255, 255, 195)
              render.rect(optionX + 29,shift + optionY + height - 7,optionX + 29.5, shift + optionY+height - 6.5, 255, 255, 255, 45)
              render.rect(optionX + 29,shift + optionY + height - 8.5,optionX + 29.5, shift + optionY+height - 7, 255, 255, 255, 65)
              render.rect(optionX + 30,shift + optionY + height - 9,optionX + 30.5, shift + optionY+height - 8.5, 255, 255, 255, 125)
              render.rect(optionX + 30.5,shift + optionY + height - 9,optionX + 31, shift + optionY+height - 8.5, 255, 255, 255, 195)
              render.rect(optionX + 32,shift + optionY + height - 9,optionX + 32.5, shift + optionY+height - 8.5, 255, 255, 255, 195)
              render.rect(optionX + 32.5,shift + optionY + height - 9,optionX + 33, shift + optionY+height - 8.5, 255, 255, 255, 125)
              render.rect(optionX + 33.5,shift + optionY + height - 7,optionX + 34, shift + optionY+height - 6.5, 255, 255, 255, 45)
              render.rect(optionX + 33.5,shift + optionY + height - 8.5,optionX + 34, shift + optionY+height - 7, 255, 255, 255, 65)
              render.rect(optionX + 33,shift + optionY + height - 6,optionX + 33.5, shift + optionY+height - 5.5, 255, 255, 255, 125)
              render.rect(optionX + 33,shift + optionY + height - 6.5,optionX + 33.5, shift + optionY+height - 6, 255, 255, 255, 195)
              render.rect(optionX + 32,shift + optionY + height - 5,optionX + 32.5, shift + optionY+height - 4.5, 255, 255, 255, 195)
              render.rect(optionX + 32.5,shift + optionY + height - 5.5,optionX + 33, shift + optionY+height - 5, 255, 255, 255, 195)
              -- green square for player_head
              render.rect(optionX + .5,shift + optionY + 1.5,optionX + 25.5, shift + optionY+height - 3, 105, 255, 105, 255)
              -- render the entity player_head
              render.player_head(optionX + 1, shift + optionY + 2, 24, data)
            end
          end
      end
      elseif module_manager.option('TargetHUDs', 'PowerX HUD') then
        height = 28
        width = math.max(100.5, 30 + render.get_string_width(entity_name));

        -- health bar
        bar_width = width - 45.5
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.10, 100)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- new target check
        if target ~= new_target then
          new_target = target
          animated_health_bar = calc_bar_width
        end

        -- render left bar
        render.rect(optionX - 1,optionY -1 ,optionX + width / width - 1.5, optionY+height + 1, 128, 128, 128, 150)
        -- render right bar
        render.rect(optionX + width,optionY -1 ,optionX + width + .5, optionY+height + 1, 128, 128, 128, 150)
        -- render bottom
        render.rect(optionX - 0.5,optionY + 28.5,optionX + width, optionY+height + 1, 128, 128, 128, 150)
        -- render top bar
        render.rect(optionX - 0.5,optionY - 1,optionX + width, optionY+height - 28.5, 128, 128, 128, 150)
        -- render background
        render.rect(optionX - 0.5,optionY - .5,optionX + width, optionY+height + .5, 0, 0, 0, 150)
        -- render inside square right bar
        render.rect(optionX + 27.5,optionY + .5,optionX + 28, optionY+height - .5, 105, 105, 105, 150)
        -- render inside square bottom
        render.rect(optionX + .5,optionY + 27.5, optionX + 28, optionY+height - .1, 105, 105, 105, 150)
        -- render inside square top bar
        render.rect(optionX + .5,optionY - .1,optionX + 28, optionY+height - 27.5, 105, 105, 105, 150)
        -- render inside square left bar
        render.rect(optionX + .1,optionY - .1,optionX + .5, optionY+height - .1, 105, 105, 105, 150)
        -- render entity name
        render.scale(0.9)
        render.string_shadow(entity_name, optionX/0.9 + 16 + width/0.9 / 2 - render.get_string_width(entity_name) / 2, optionY/0.9 + 4, 255, 255, 255, 255)
        render.scale(1/0.9)
        --render heart
        render.scale(0.8)
        render.string_shadow("\226\157\164", optionX/0.8 + 40, optionY/0.8 + 16, 255, 96, 126, 255)
        render.scale(1/0.8)
        --render health bar background line
        render.rect(optionX + 40.5,optionY + 14.5,optionX + width - 4, optionY+height - 10.5, 0, 0, 0, 100)
        --render health bar background
        render.rect(optionX + 41,optionY + 15,optionX + width - 4.5, optionY+height - 11, 128, 128, 128, 150)
        -- render the health bar yellow shit
        render.rect(optionX + 41, optionY + 15, optionX + 41 + animated_health_bar, optionY+height - 11, 230, 173, 84, 255)
        -- render health bar
        render.rect(optionX + 41, optionY + 15, optionX + 41 + calc_bar_width, optionY+height - 11, 12, 207, 124, 255)
        --render armor bar background line
        render.rect(optionX + 40.5,optionY + 22.5,optionX + width - 33, optionY + 25.5, 0, 0, 0, 100)
        --render armor bar background
        render.rect(optionX + 41,optionY + 23,optionX + width - 33.5, optionY + 25, 128, 128, 128, 150)
        --render armor bar
        render.rect(optionX + 41,optionY + 23,optionX + width - 33.5, optionY + 25, 0, 180, 255, 255)
        -- render blue heart and health
        render.scale(0.8)
        render.string_shadow("\226\157\164", optionX/0.8 + width/0.8 - 34, optionY/0.8 + 25, 0, 153, 255, 255)
        render.string_shadow(round(health,1), optionX/0.8 + width/0.8 - render.get_string_width(math.ceil(round(health,1))) / 2 - 18, optionY/0.8 + 25, 153, 153, 153, 255)
        render.scale(1/0.8)
        -- render shield
        render.scale(1)
        render.string_shadow("\226\172\146", optionX/1 + 32, optionY/1 + 19, 153, 153, 153, 255)
        render.scale(1/1)
        -- render player head
        render.player_head(optionX + 1.5, optionY + 1, 26, target)
      elseif module_manager.option('TargetHUDs', 'Woman HUD') then
        height = 42
        width = 119

        -- health bar
        bar_width = width - 16
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.10, 100)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- new target check
        if target ~= new_target then
          new_target = target
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- render background
        render.rect(optionX - 1,optionY - 1,optionX + width, optionY+height, 23, 23, 25, 203)
        -- render player name
        render.string_shadow(entity_name, optionX + 27, optionY + 2, 255, 255, 255, 255)
        -- render health bar
        render.rect(optionX + 12, optionY + 29.5, optionX + 12 + animated_health_bar, optionY+height -9.5, health_bar_color[1], health_bar_color[2], health_bar_color[3], 255)
        -- render head
        render.player_head(optionX + .5, optionY + .5, 25, target)
        -- render heart
        render.string_shadow("\226\157\164", optionX + 1, optionY + 27.6, 255, 85, 85, 255)

        -- render winning stat
        currentHealth = player.health()
        if health < currentHealth then
				      render.string_shadow('Winning', optionX + 27, optionY + 16, 108, 195, 18, 255);
			  elseif health > currentHealth then
				      render.string_shadow('Losing', optionX + 27, optionY + 16, 255, 51, 0, 255);
			  elseif health == currentHealth then
				      render.string_shadow('Neutral', optionX + 27, optionY + 16, 255, 255, 0, 255);
			  end
      elseif module_manager.option('TargetHUDs', 'Rise HUD') then
        height = 48;
        width = math.max(128, 55 + render.get_string_width(entity_name));
        x = (ctx.width - width) / 2
        y = ctx.height / 2 + 50

        -- health bar
        bar_width = width - 24
        local calc_bar_width = bar_width * health_relative(target)
        local health_bar_color = health_color(target)
        local darker_health_bar_color = darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        animated_health_bar = exponential_anim(animated_health_bar, calc_bar_width, 0.08)

        -- bar out check
        if animated_health_bar > bar_width then
          animated_health_bar = calc_bar_width
        end

        -- new target check
        if target ~= new_target then
          new_target = target
          animated_health_bar = calc_bar_width
        end

        -- custom color check
        if module_manager.option('TargetHUDs', 'Custom Color') then
          health_bar_color = {module_manager.option('TargetHUDs', "R"), module_manager.option('TargetHUDs', "G"), module_manager.option('TargetHUDs', "B")}
          darker_health_bar_color =  darker(health_bar_color[1], health_bar_color[2], health_bar_color[3], 0.5)
        end

        -- render center background
        render.rect(optionX,optionY,optionX + width, optionY+height, 0, 0, 0, 125)
        -- render left bar background
        render.rect(optionX - 1,optionY + .5,optionX + width / width - 1, optionY+height -.5, 0, 0, 0, 125)
        -- render second left bar background
        render.rect(optionX - 1.5,optionY + 1,optionX + width / width - 2, optionY+height -1., 0, 0, 0, 125)
        -- render third left bar background
        render.rect(optionX - 2,optionY + 2,optionX + width / width - 2.5, optionY+height -2, 0, 0, 0, 125)
        -- render right bar background
        render.rect(optionX + width ,optionY + .5,optionX + width + 1, optionY+height -.5, 0, 0, 0, 125)
        -- render second right bar background
        render.rect(optionX + width + 1,optionY + 1,optionX + width + 1.5, optionY+height -1, 0, 0, 0, 125)
        -- render third right bar background
        render.rect(optionX + width + 1.5,optionY + 2,optionX + width + 2, optionY+height -2, 0, 0, 0, 125)
        -- render entity name
        render.scale(0.9)
        render.string("Name " .. entity_name, optionX/0.9 + 40, optionY/0.9+ 14, 255, 255, 255, 255)
        render.scale(1/0.9)
        -- render distance and hurt ticks
        render.scale(0.9)
        render.string("Distance " .. round(player.distance_to_entity(target),1) .. " Hurt " .. math.floor(world.hurt_time(target)), optionX/0.9 + 40, optionY/0.9+ 26, 255, 255, 255, 255)
        render.scale(1/0.9)
        -- render the health bar
        --drawAnimatedRainbow(optionX + 2, optionY + 39, height, 2 + animated_health_bar - 349, 100)
        drawAnimatedRainbow(optionX+2, optionY + 39, animated_health_bar - 1, height - 43, 100)
        -- render the entity head
        hurt_time = math.max(7, world.hurt_time(target))
        animated_hurt_time = exponential_anim(animated_hurt_time, hurt_time, 1)
        render.player_head(hurt_time + optionX - 5, hurt_time + optionY - 2, 44 - hurt_time * 2, target)
        -- render hurt time head overlay
        if hurt_time == 7 then
          hurt_time = 0
        end
        render.rect(hurt_time + optionX - 5,hurt_time + optionY - 2, optionX + width - 89 - hurt_time, optionY+height - 6 - hurt_time, 255, 0, 0, hurt_time * 10)
        -- render health text
        render.scale(0.9)
        render.string(round(health, 1), (optionX + 6 + animated_health_bar) /0.9, optionY/0.9 + 42, 255, 255, 255, 255)
        render.scale(1/0.9)
      elseif module_manager.option('TargetHUDs', 'Moon New HUD') then
        client.print("wip")
        module_manager.set_option('TargetHUDs', 'Moon New HUD', false)
      end


      if module_manager.option("TargetHUDs", 'Novoline New HUD') or module_manager.option("TargetHUDs", 'Novoline HUD') then
        if client.gui_name() == "chat" or client.gui_name() == "astolfo" then
          if ctx.mouse_x >= math.floor(optionX) and ctx.mouse_x <= math.floor((optionX+width)) then
            if ctx.mouse_y >= math.floor(optionY) and ctx.mouse_y <= math.floor((optionY+height)) then
              alpha = 150
              -- light top border
              render.rect(optionX - 1.5 ,optionY - height / 15,optionX + width + 2, optionY - height /25, 255, 255, 255, 255)
              -- light bottom border
              render.rect(optionX - 1.5 ,optionY + (height / 15) * 16 ,optionX + width + 2, optionY + height + 3.5, 255, 255, 255, 255)
              -- light left border
              render.rect(optionX - 2 ,optionY - 2,optionX + width / width - 2, optionY+height + 3, 255, 255, 255, 255)
              -- light right border
              render.rect(optionX + width + 1.5 ,optionY - 2,optionX + width + 2.5, optionY+height + 3, 255, 255, 255, 255)
              -- light left top dot
              render.rect(optionX - 2 ,optionY - 2.5,optionX + width - 112, optionY-height /25, 255, 255, 255, alpha)
              -- light right top dot
              render.rect(optionX + 112.5 ,optionY - 2.5,optionX + width + 2.5, optionY-height /25, 255, 255, 255, alpha)
              -- light left bottom dot
              render.rect(optionX -2 ,optionY + (height / 15) * 16,optionX + width - 112, optionY+height + 3.5, 255, 255, 255, alpha)
              -- light right bottom dot
              render.rect(optionX + 112.5 ,optionY + (height / 15) * 16,optionX + width + 2.5, optionY+height + 3.5, 255, 255, 255, alpha)
              -- darker background
              render.rect(optionX - 1 ,optionY - 1.5,optionX + width + 1.5, optionY+height + 2.5, 0, 0, 0, 150)
            end
          end
        end
      end

      if module_manager.option('TargetHuds', 'Flux HUD') then
        lock = 50
      else
        lock = 0
      end
      --Royalty logic cause lazy
      if input.is_mouse_down(0) then if ctx.mouse_x >= math.floor(optionX) and ctx.mouse_x <= math.floor((optionX+width)) then
  					if ctx.mouse_y >= math.floor(lock + optionY) and ctx.mouse_y <= math.floor((lock + optionY+height)) then
  						if client.gui_name() == "chat" or client.gui_name() == "astolfo" then
  							xPos = (ctx.mouse_x - width /2)
  							yPos = (ctx.mouse_y - lock - height  / 2)
  							player.message('.TargetHUDs Pos-X '..math.floor(xPos))
  							player.message('.TargetHUDs Pos-Y '..math.floor(yPos))
  						end
  					end
  				end
  			end
      end
    end,

}

module_manager.register('TargetHUDs', TargetHUD)
module_manager.register_boolean('TargetHUDs', 'Default HUD', true)
module_manager.register_boolean('TargetHUDs', 'Novoline HUD', false)
module_manager.register_boolean('TargetHUDs', 'Novoline New HUD', false)
module_manager.register_boolean('TargetHUDs', 'Moon HUD', false)
module_manager.register_boolean('TargetHUDs', 'Moon New HUD', false)
module_manager.register_boolean('TargetHUDs', 'Exhibition HUD', false)
module_manager.register_boolean('TargetHUDs', 'Astolfo Old HUD', false)
module_manager.register_boolean('TargetHUDs', 'Astolfo Large HUD', false)
module_manager.register_boolean('TargetHUDs', 'Flux HUD', false)
module_manager.register_boolean('TargetHUDs', 'PowerX HUD', false)
module_manager.register_boolean('TargetHUDs', 'Woman HUD', false)
module_manager.register_boolean('TargetHUDs', 'Rise HUD', false)
module_manager.register_boolean('TargetHUDs', 'Custom Color', false)
module_manager.register_number('TargetHUDs', 'R', 0, 255, 147)
module_manager.register_number('TargetHUDs', 'G', 0, 255, 112)
module_manager.register_number('TargetHUDs', 'B', 0, 255, 219)
module_manager.register_number('TargetHUDs', 'Pos-X', 0, 384000, 375)
module_manager.register_number('TargetHUDs', 'Pos-Y', 0, 216000, 300)
