-- global tracking variables
local stage = 1
local delay = 0
local use_ticks = 0
local target_entity, lowest_angle_entity, last_lowest_angle_entity = nil, nil, nil
local t_x, t_y, t_z

--global reference variables
local entities

-- global physics variables
local velocities = {['4'] = 0.32, ['5'] = 0.44, ['6'] = 0.56, ['7'] = 0.69, ['8'] = 0.82, ['9'] = 0.96, ['10'] = 1.15, ['11'] = 1.25, ['12'] = 1.40, ['13'] = 1.56, ['14'] = 1.71, ['15'] = 1.88, ['16'] = 2.06, ['17'] = 2.24, ['18'] = 2.41, ['19'] = 2.60, ['20'] = 2.81, ['21'] = 2.97, ['22'] = 3.00}
local g = 0.05
local v = 3.00

-- global smooth bezier rotation variables
local start_yaw, start_pitch
local target_yaw, target_pitch
local restore_yaw, restore_pitch
local set_yaw, set_pitch
local random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2
local bezier_t

-- aimbot functions
function distance_3d(x1, y1, z1, x2, y2, z2)
  return math.sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
end

function distance_2d(x1, y1, x2, y2)
  if x1 and y1 and x2 and y2 then
    return math.sqrt((x2 - x1)^2 + (y2 - y1)^2)
  end
end

function is_on_team(entityID)
  if entityID == nil then
    return nil
  end
  local list = world.entities()
  for i = 1, #list do
    if world.name(list[i]) == player.name() then
      display_name = world.display_name(list[i])
    end
  end
  if string.find(display_name, "\194\167") ~= nil then
    substring = string.sub(display_name, string.find(display_name, "\194\167"), string.find(display_name, "\194\167") +2)
    if string.find(world.display_name(entityID), substring) ~= nil then
      return true
    end
    return false
  end
end

function has_clear_view(entity_id)
  local e_x, e_y, e_z = world.position(entity_id)
  local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(entity_id)
  e_y = e_y + ((max_y - min_y) * module_manager.option('Auto-Projectile', 'Hitbox-Aim-Height'))
  local set_yaw, set_pitch = player.angles_for_cords(e_x, e_y, e_z)
  local hit_type = player.ray_cast(set_yaw, set_pitch, 160)
  if hit_type == 1 then
    return true
  elseif hit_type == 2 then
    hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(set_yaw, set_pitch, 160)
    if player.distance_to(hit_x, hit_y, hit_z) > player.distance_to_entity(entity_id) then
      return true
    end
  elseif hit_type == 3 then
    hit_type, ray_cast_entity_id = player.ray_cast(set_yaw, set_pitch, 160)
    if ray_cast_entity_id == entity_id then
      return true
    end
  end
  return false
end

function check_fov(entity_id)
  local e_yaw, e_pitch = player.angles_for_cords(world.position(entity_id))
  local yaw, pitch = player.angles()
  local yaw_diff = math.abs(yaw - e_yaw)
  if yaw_diff < module_manager.option('Bow-Aimbot', 'FOV') / 2 then
    return true
  else
    return false
  end
end

function get_lowest_angle_entity()
  local lowest_angle_id = nil
  local lowest_angle = 42069
  for i = 1, #entities, 1 do
    local entity = entities[i]
    local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(entity)
    local t_x, t_y, t_z = world.position(entity)
    if max_y ~= nil and min_y ~= nil then
      t_y = t_y + ((max_y - min_y) / 2)
    end
    local t_yaw, t_pitch = player.angles_for_cords(t_x, t_y, t_z)
    local yaw, pitch = player.angles()
    local yaw_diff, pitch_diff = math.abs(t_yaw - yaw), math.abs(t_pitch - pitch)
    local angle_diff = yaw_diff + pitch_diff
    if angle_diff < lowest_angle and entity ~= player.id() and not world.is_bot(entity) and world.name(entity) ~= 'unknown' and (world.is_player(entity) or not module_manager.option('Bow-Aimbot', 'Player-Check')) and (not module_manager.option('Bow-Aimbot', 'Team-Check') or not is_on_team(entity)) and (world.max_health(entity) ~= nil) and has_clear_view(entity) and (player.distance_to_entity(entity) < module_manager.option('Bow-Aimbot', 'Max-Target-Distance')) and (max_y ~= nil) and check_fov(entity) and not world.is_potion_active(entity, 14) and world.hurt_time(entity) <= module_manager.option('Bow-Aimbot', 'Max-Target-Hurttime') then
      lowest_angle_id = entity
      lowest_angle = angle_diff
    end
  end
  return lowest_angle_id
end

function solid_block(x, y, z)
  if world.block(x, y, z) ~= 'tile.air' and world.block(x, y, z) ~= 'tile.water' and world.block(x, y, z) ~= 'tile.deadbush' and world.block(x, y, z) ~= 'tile.reeds' and world.block(x, y, z) ~= 'tile.tallgrass' and world.block(x, y, z) ~= 'tile.doublePlant' and world.block(x, y, z) ~= 'tile.web' and world.block(x, y, z) ~= 'tile.lava' then
    return true
  else
    return false
  end
end

function is_in_water(entity_id)
  local e_x, e_y, e_z = world.position(entity_id)
  if world.block(e_x, math.floor(e_y), e_z) == 'tile.water' then
    return true
  else
    return false
  end
end

function ground_distance(entity_id)
  if is_in_water(entity_id) then
    return 0
  end
  local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(entity_id)
  for i = min_y, 0, -0.1 do --step size can cause lag at extremely high fall distances
    if solid_block(min_x, i, min_z) or solid_block(min_x, i, max_z) or solid_block(max_x, i, min_z) or solid_block(max_x, i, max_z) then
      return min_y - i
    end
  end
  return nil
end

function solve_projectile_3d(g, v, t_x, t_y, t_z)
  if g and v and t_x and t_y and t_z then
    local p_x, p_y, p_z = player.position()
    p_y = p_y + player.eye_height()
    local x = distance_2d(t_x, t_z, p_x, p_z)
    local y = t_y - p_y
    local radical = v^4-g*(g*(x^2)+2*y*(v^2))
    if radical >= 0 then
      local frac_first = ((v^2)+math.sqrt(radical))/(g*x)
      local frac_second = ((v^2)-math.sqrt(radical))/(g*x)
      local theta_first = math.atan(frac_first)
      local theta_second = math.atan(frac_second)
      local theta_first_deg = theta_first * 180 / math.pi
      local theta_second_deg = theta_second * 180 / math.pi
      local t_yaw, t_pitch = player.angles_for_cords(t_x, t_y, t_z)
      t_pitch = 0 - theta_second_deg
      return t_yaw, t_pitch
    end
  end
end

function time_of_flight(g, v, theta, h, t_x, t_y, t_z)
  local p_x, p_y, p_z = player.position()
  return distance_2d(p_x, p_z, t_x, t_z)/(math.cos(theta)*v)
end

function predict_cords(entity)
  local p_x, p_y, p_z = player.position()
  local e_x, e_y, e_z = world.position(entity)
  local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(entity)
  local t_x, t_y, t_z
  if max_y ~= nil and min_y ~= nil then
    e_y = e_y + ((max_y - min_y) * module_manager.option('Bow-Aimbot', 'Hitbox-Aim-Height'))
  end
  local l_e_x, l_e_y, l_e_z = world.prev_position(entity)
  if e_x ~= nil and e_y ~= nil and e_z ~= nil and l_e_x ~= nil and l_e_y ~= nil and l_e_z ~= nil and solve_projectile_3d(g, v, e_x, e_y, e_z) and p_x and p_y and p_z then
    t_yaw, t_pitch = solve_projectile_3d(g, v, e_x, e_y, e_z)
    local theta = (-1 * t_pitch) * (math.pi/180)
    local flight_ticks = time_of_flight(g, v, theta, p_y - e_y, e_x, e_y, e_z)
    if flight_ticks ~= nil then
      local x_adj = (e_x - l_e_x) * (flight_ticks * module_manager.option('Bow-Aimbot', 'Prediction-Mult') + module_manager.option('Bow-Aimbot', 'Prediction-Add'))
      local z_adj = (e_z - l_e_z) * (flight_ticks * module_manager.option('Bow-Aimbot', 'Prediction-Mult') + module_manager.option('Bow-Aimbot', 'Prediction-Add'))
      local y_adj = 0
      if ground_distance(entity) then
        y_adj = y_adj - ground_distance(entity)
      end
      t_x, t_y, t_z = e_x + x_adj, e_y + y_adj, e_z + z_adj
    else
      local x_adj = (e_x - l_e_x) * module_manager.option('Bow-Aimbot', 'Prediction-Add')
      local z_adj = (e_z - l_e_z) * module_manager.option('Bow-Aimbot', 'Prediction-Add')
      local y_adj = 0
      if ground_distance(entity) then
        y_adj = y_adj - ground_distance(entity)
      end
      t_x, t_y, t_z = e_x + x_adj, e_y + y_adj, e_z + z_adj
    end
  elseif l_e_x == nil or l_e_y == nil or l_e_z == nil then
    t_x, t_y, t_z = e_x, e_y, e_z
  end
  return t_x, t_y, t_z
end

-- rotation functions
function bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, extra_randomization)
  local random_yaw_1, random_pitch_1 = math.random(math.min(start_yaw, target_yaw) - extra_randomization, math.max(start_yaw, target_yaw) + extra_randomization), math.random(math.min(start_pitch, target_pitch) - extra_randomization, math.max(start_pitch, target_pitch) + extra_randomization)
  local random_yaw_2, random_pitch_2 = math.random(math.min(start_yaw, target_yaw) - extra_randomization, math.max(start_yaw, target_yaw) + extra_randomization), math.random(math.min(start_pitch, target_pitch) - extra_randomization, math.max(start_pitch, target_pitch) + extra_randomization)
  if distance_2d(start_yaw, start_pitch, random_yaw_1, random_pitch_1) > distance_2d(start_yaw, start_pitch, random_yaw_2, random_pitch_2) then
    local temp_yaw, temp_pitch = random_yaw_1, random_pitch_2
    random_yaw_1, random_pitch_1 = random_yaw_2, random_pitch_2
    random_yaw_2, random_pitch_2 = temp_yaw, temp_pitch
  end
  return random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2
end

function cubic_bezier_curve(t, x1, y1, x2, y2, x3, y3, x4, y4)
  local x_cord = (1-t)^3 * x1 + 3*(1 - t)^2 * t * x2 + 3*(1-t) * t^2 * x3 + t^3 * x4
  local y_cord = (1-t)^3 * y1 + 3*(1 - t)^2 * t * y2 + 3*(1-t) * t^2 * y3 + t^3 * y4
  if y_cord > 90 then y_cord = 90 end
  if y_cord < -90 then y_cord = -90 end
  return x_cord, y_cord
end

-- targethud functions
function drawn_relative(useTicks)
  local calculated_relative = math.max(0.0, math.min(useTicks, 22) / 22)
  return calculated_relative
end

function drawn_color(useTicks)
  return hsl_to_rgb(drawn_relative(useTicks) * 360 / 3, 1, 0.5);
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

function get_number_of_items(item_code)
  local number_of_items = 0
  if client.gui_name() ~= 'chest' then
    for i = 0, 44, 1 do
      if player.inventory.item_information(i) == item_code then
        number_of_items = number_of_items + player.inventory.get_item_stack(i)
      end
    end
  end
  return number_of_items
end

local BowAimbot = {
  on_pre_motion = function(motion)
    entities = world.entities()

    if client.gui_name() ~= 'chest' and player.inventory.item_information(35 + player.held_item_slot()) == 'item.bow' and player.using_item() then
      use_ticks = use_ticks + 1
    end
    if client.gui_name() ~= 'chest' and module_manager.is_module_on('fastbow') and player.inventory.item_information(35 + player.held_item_slot()) == 'item.bow' and player.on_ground() then
      use_ticks = 22
    end

    if module_manager.option('Bow-Aimbot', 'Rotation-Speed') >= 1 then
      stage = 3
    end

    drawn_percent_color = drawn_color(use_ticks)
    last_lowest_angle_entity = lowest_angle_entity
    lowest_angle_entity = get_lowest_angle_entity()

    if stage ~= 3 or client.gui_name() == 'chest' or player.inventory.item_information(35 + player.held_item_slot()) ~= 'item.bow' then
      target_entity = nil
    end

    if stage == 1 and delay <= 0 and lowest_angle_entity ~= nil and (use_ticks >= 1) then
      start_yaw, start_pitch = player.angles()
      local e_x, e_y, e_z = world.position(lowest_angle_entity)
      target_yaw, target_pitch = player.angles_for_cords(e_x,  e_y + (player.distance_to_entity(lowest_angle_entity)/2), e_z)
      random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2 = bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, module_manager.option('Bow-Aimbot', 'Rotation-Randomization'))
      restore_yaw, restore_pitch = player.angles()
      bezier_t = 0
      stage = 2
    end

    if stage == 2 and delay <= 0 and lowest_angle_entity ~= nil and (use_ticks >= 1) then
      local e_x, e_y, e_z = world.position(lowest_angle_entity)
      target_yaw, target_pitch = player.angles_for_cords(e_x,  e_y + (player.distance_to_entity(lowest_angle_entity)/2), e_z)
      bezier_t = bezier_t + module_manager.option('Bow-Aimbot', 'Rotation-Speed')
      if bezier_t > 1 then bezier_t = 1 end
      set_yaw, set_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      motion.yaw, motion.pitch = set_yaw, set_pitch
      --client.print(player.ticks_existed() .. ' ' .. stage)
      if not module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
        player.set_angles(set_yaw, set_pitch)
      end
      if set_yaw == target_yaw and set_pitch == target_pitch then
        delay = 1
        stage = 3
      end
    end

    if stage == 3 and lowest_angle_entity ~= nil and last_lowest_angle_entity ~= nil and lowest_angle_entity ~= last_lowest_angle_entity and module_manager.option('Bow-Aimbot', 'Rotation-Speed') < 1 then
      target_entity = nil
      motion.yaw, motion.pitch = set_yaw, set_pitch
      --client.print(player.ticks_existed() .. ' ' .. stage)
      if not module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
        player.set_angles(set_yaw, set_pitch)
      end
      start_yaw, start_pitch = set_yaw, set_pitch
      local e_x, e_y, e_z = world.position(lowest_angle_entity)
      target_yaw, target_pitch = player.angles_for_cords(e_x,  e_y + (player.distance_to_entity(lowest_angle_entity)/2), e_z)
      random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2 = bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, module_manager.option('Bow-Aimbot', 'Rotation-Randomization'))
      bezier_t = 0
      stage = 2
    elseif stage == 3 and (lowest_angle_entity == nil or use_ticks == 0) and module_manager.option('Bow-Aimbot', 'Rotation-Speed') < 1 then
      target_entity = nil
      start_yaw, start_pitch = set_yaw, set_pitch
      if module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
        target_yaw, target_pitch = player.angles()
      else
        target_yaw, target_pitch = restore_yaw, restore_pitch
      end
      random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2 = bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, module_manager.option('Bow-Aimbot', 'Rotation-Randomization'))
      bezier_t = 0
      stage = 4
    elseif stage == 3 and delay <= 0 and lowest_angle_entity ~= nil and use_ticks >= 1 then
      t_x, t_y, t_z = predict_cords(lowest_angle_entity)
      v = 3.00
      if use_ticks <= 22 then
        v = velocities[tostring(use_ticks)]
      end
      if use_ticks >= 4 and solve_projectile_3d(g, v, t_x, t_y, t_z) then
        target_entity = lowest_angle_entity
        set_yaw, set_pitch = solve_projectile_3d(g, v, t_x, t_y, t_z)
        motion.yaw, motion.pitch = set_yaw, set_pitch
        --client.print(player.ticks_existed() .. ' ' .. stage)
        if not module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
          player.set_angles(set_yaw, set_pitch)
        end
      elseif delay <= 0 and use_ticks >= 1 then
        local e_x, e_y, e_z = world.position(lowest_angle_entity)
        set_yaw, set_pitch = player.angles_for_cords(e_x, e_y + (player.distance_to_entity(lowest_angle_entity)/2), e_z)
        motion.yaw, motion.pitch = set_yaw, set_pitch
        --client.print(player.ticks_existed() .. ' ' .. stage)
        if not module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
          player.set_angles(set_yaw, set_pitch)
        end
      end
    end

    if stage == 4 and delay <= 0 then
      if module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
        target_yaw, target_pitch = player.angles()
      end
      bezier_t = bezier_t + module_manager.option('Bow-Aimbot', 'Rotation-Speed')
      if bezier_t > 1 then bezier_t = 1 end
      set_yaw, set_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      motion.yaw, motion.pitch = set_yaw, set_pitch
      --client.print(player.ticks_existed() .. ' ' .. stage)
      if not module_manager.option('Bow-Aimbot', 'Silent-Rotation') then
        player.set_angles(set_yaw, set_pitch)
      end
      if set_yaw == target_yaw and set_pitch == target_pitch then
        delay = 1
        stage = 1
      end
    end

    if client.gui_name() ~= 'chest' and not player.inventory.item_information(35 + player.held_item_slot()) == 'item.bow' or not player.using_item() then
      use_ticks = 0
    end

    delay = delay - 1
    return motion
  end,

  on_render_screen = function(screen, scale_factor)
    if module_manager.option('Bow-Aimbot', 'HUD') then
      height = 44
      width = 128
      optionY = module_manager.option('Bow-Aimbot','Pos-Y')
      optionX = module_manager.option('Bow-Aimbot','Pos-X')
      amount_arrow = get_number_of_items("item.arrow")
      if use_ticks > -1 and use_ticks <= 22 then
        output = use_ticks * (22 * use_ticks) / 106
      end
      if amount_arrow <= 8 then
        arrow_count_color = {255, 0, 0}
      elseif amount_arrow > 8 and amount_arrow <= 16 then
        arrow_count_color = {255 ,255, 0}
      else
        arrow_count_color = {0 , 232, 0}
      end
      if client.gui_name() == "chat" then
        target_entity = player.id()
      end
      if target_entity ~= nil and world.health(target_entity) then
        render.rect(optionX,optionY,optionX + width, optionY+height, 0, 0, 0, 135)
        render.rect(optionX - 1,optionY + .5,optionX + width / width - 1, optionY+height -.5, 0, 0, 0, 135)
        render.rect(optionX - 1.5,optionY + 1,optionX + width / width - 2, optionY+height -1., 0, 0, 0, 135)
        render.rect(optionX - 2,optionY + 2,optionX + width / width - 2.5, optionY+height -2, 0, 0, 0, 135)
        render.rect(optionX + width ,optionY + .5,optionX + width + 1, optionY+height -.5, 0, 0, 0, 135)
        render.rect(optionX + width + 1,optionY + 1,optionX + width + 1.5, optionY+height -1, 0, 0, 0, 135)
        render.rect(optionX + width + 1.5,optionY + 2,optionX + width + 2, optionY+height -2, 0, 0, 0, 135)
        render.string_shadow(world.name(target_entity), optionX + 32, optionY + 3, 255, 255, 255, 255)
        render.string_shadow("Dist: " .. math.floor(player.distance_to_entity(target_entity)), optionX + 32, optionY + 13, 255, 255, 255, 255)
        render.string_shadow("Drawn: ", optionX + 32, optionY + 23, 255, 255, 255, 255)
        render.string_shadow(math.floor(output) .. "%", optionX + 66, optionY + 23, drawn_percent_color[1]/1.1, drawn_percent_color[2]/1.1, drawn_percent_color[3]/1.1, 255)
        render.string_shadow("Arrows: ", optionX + 88 - render.get_string_width(amount_arrow), optionY + 34, 255, 255, 255, 255)
        if target_entity ~= player.id() then
          render.player_head(optionX + 1.5, optionY + 3, 28, target_entity)
        end
        render.string_shadow(amount_arrow, optionX + 128 - render.get_string_width(amount_arrow), optionY + 34, arrow_count_color[1], arrow_count_color[2], arrow_count_color[3], 255)
        render.string_shadow(math.floor(world.health(target_entity)), optionX + 1.5, optionY + 34, 255, 255, 255, 255)
        render.scale(1.2)
        render.string_shadow("\226\157\164", (optionX + 1.5) / 1.2 + render.get_string_width(math.floor(world.health(target_entity))), (optionY + 33) /1.2, 255, 96, 126, 255)
        render.scale(1/1.2)
      end
      if input.is_mouse_down(0) then if screen.mouse_x >= math.floor(optionX) and screen.mouse_x <= math.floor((optionX+width)) then
          if screen.mouse_y >= math.floor(optionY) and screen.mouse_y <= math.floor((optionY+height)) then
            if client.gui_name() == "chat" or client.gui_name() == "astolfo" then
              xPos = (screen.mouse_x - width /2)
              yPos = (screen.mouse_y - height  /2)
              player.message('.Bow-Aimbot Pos-X '..math.floor(xPos))
              player.message('.Bow-Aimbot Pos-Y '..math.floor(yPos))
            end
          end
        end
      end
    end

    if module_manager.option('Bow-Aimbot', 'Target-Tracer') and client.gui_name() ~= 'chat' then
      render.scale(1/screen.scale_factor)
      if target_entity ~= nil then

        local e_x, e_y, e_z = world.position(target_entity)
        local p_x, p_y, p_z = player.camera_position()
        local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(target_entity)
        if min_x ~= nil then
          local x, y, v = render.world_to_screen(e_x - p_x, e_y - p_y + (max_y - min_y) * module_manager.option('Bow-Aimbot', 'Hitbox-Aim-Height'), e_z - p_z)
    			if v < 1 then
    				render.line(screen.width/2*screen.scale_factor, screen.height/2*screen.scale_factor, x, y, module_manager.option('Bow-Aimbot', 'Tracer-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
    			end
        end
      end
      render.scale(screen.scale_factor)
    end

    if module_manager.option('Bow-Aimbot', 'Prediction-Tracer') and client.gui_name() ~= 'chat' then
      render.scale(1/screen.scale_factor)
      if target_entity ~= nil then

        local e_x, e_y, e_z = world.position(target_entity)
        local p_x, p_y, p_z = player.camera_position()
        local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(target_entity)
        if min_x and min_y and min_z and t_x and t_y and t_z then
          local x1, y1, v1 = render.world_to_screen(t_x - p_x, t_y - p_y, t_z - p_z)
          local x2, y2, v2 = render.world_to_screen(e_x - p_x, e_y - p_y + (max_y - min_y) * module_manager.option('Bow-Aimbot', 'Hitbox-Aim-Height'), e_z - p_z)
    			if v1 < 1 and v2 < 1 then
    				render.line(x2, y2, x1, y1, module_manager.option('Bow-Aimbot', 'Tracer-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
    			end
        end
      end
      render.scale(screen.scale_factor)
    end

    if module_manager.option('Bow-Aimbot', 'Target-ESP') and client.gui_name() ~= 'chat' then
      render.scale(1/screen.scale_factor)
      if target_entity ~= nil then

        local e_x, e_y, e_z = world.position(target_entity)
        local p_x, p_y, p_z = player.camera_position()
        local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(target_entity)
        if min_x ~= nil then
          local p1x, p1y, p1z = render.world_to_screen(min_x - p_x, max_y - p_y, min_z - p_z)
          local p2x, p2y, p2z = render.world_to_screen(min_x - p_x, max_y - p_y, max_z - p_z)
          local p3x, p3y, p3z = render.world_to_screen(max_x - p_x, max_y - p_y, min_z - p_z)
          local p4x, p4y, p4z = render.world_to_screen(max_x - p_x, max_y - p_y, max_z - p_z)
          local p5x, p5y, p5z = render.world_to_screen(min_x - p_x, min_y - p_y, min_z - p_z)
          local p6x, p6y, p6z = render.world_to_screen(min_x - p_x, min_y - p_y, max_z - p_z)
          local p7x, p7y, p7z = render.world_to_screen(max_x - p_x, min_y - p_y, min_z - p_z)
          local p8x, p8y, p8z = render.world_to_screen(max_x - p_x, min_y - p_y, max_z - p_z)
    			if p1z < 1 and p2z < 1 and p3z < 1 and p4z < 1 then
    				render.line(p1x, p1y, p2x, p2y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p1x, p1y, p3x, p3y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p2x, p2y, p4x, p4y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p3x, p3y, p4x, p4y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p5x, p5y, p6x, p6y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p5x, p5y, p7x, p7y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p6x, p6y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p7x, p7y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p1x, p1y, p5x, p5y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p2x, p2y, p6x, p6y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p3x, p3y, p7x, p7y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
            render.line(p4x, p4y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Target-Red'), module_manager.option('Bow-Aimbot', 'Target-Green'), module_manager.option('Bow-Aimbot', 'Target-Blue'), module_manager.option('Bow-Aimbot', 'Target-Alpha'))
    			end
        end
      end
      render.scale(screen.scale_factor)
    end

    if module_manager.option('Bow-Aimbot', 'Prediction-ESP') and client.gui_name() ~= 'chat' then
      render.scale(1/screen.scale_factor)
      if target_entity ~= nil then

        local e_x, e_y, e_z = world.position(target_entity)
        local p_x, p_y, p_z = player.camera_position()
        local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(target_entity)
        if min_x and min_y and min_z and t_x and t_y and t_z then
          min_x, min_y, min_z, max_x, max_y, max_z = min_x + t_x - e_x, min_y + e_y - t_y + ((max_y - min_y) * module_manager.option('Bow-Aimbot', 'Hitbox-Aim-Height')), min_z + t_z - e_z, max_x + t_x - e_x, max_y + e_y - t_y  + ((max_y - min_y) * module_manager.option('Bow-Aimbot', 'Hitbox-Aim-Height')), max_z + t_z - e_z
          local p1x, p1y, p1z = render.world_to_screen(min_x - p_x, max_y - p_y, min_z - p_z)
          local p2x, p2y, p2z = render.world_to_screen(min_x - p_x, max_y - p_y, max_z - p_z)
          local p3x, p3y, p3z = render.world_to_screen(max_x - p_x, max_y - p_y, min_z - p_z)
          local p4x, p4y, p4z = render.world_to_screen(max_x - p_x, max_y - p_y, max_z - p_z)
          local p5x, p5y, p5z = render.world_to_screen(min_x - p_x, min_y - p_y, min_z - p_z)
          local p6x, p6y, p6z = render.world_to_screen(min_x - p_x, min_y - p_y, max_z - p_z)
          local p7x, p7y, p7z = render.world_to_screen(max_x - p_x, min_y - p_y, min_z - p_z)
          local p8x, p8y, p8z = render.world_to_screen(max_x - p_x, min_y - p_y, max_z - p_z)
    			if p1z < 1 and p2z < 1 and p3z < 1 and p4z < 1 then
    				render.line(p1x, p1y, p2x, p2y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p1x, p1y, p3x, p3y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p2x, p2y, p4x, p4y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p3x, p3y, p4x, p4y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p5x, p5y, p6x, p6y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p5x, p5y, p7x, p7y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p6x, p6y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p7x, p7y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p1x, p1y, p5x, p5y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p2x, p2y, p6x, p6y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p3x, p3y, p7x, p7y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
            render.line(p4x, p4y, p8x, p8y, module_manager.option('Bow-Aimbot', 'ESP-Width'), module_manager.option('Bow-Aimbot', 'Prediction-Red'), module_manager.option('Bow-Aimbot', 'Prediction-Green'), module_manager.option('Bow-Aimbot', 'Prediction-Blue'), module_manager.option('Bow-Aimbot', 'Prediction-Alpha'))
    			end
        end
      end
      render.scale(screen.scale_factor)
    end

  end
}

module_manager.register('Bow-Aimbot', BowAimbot)
module_manager.register_number('Bow-Aimbot', 'FOV', 0.001, 360.001, 360.001)
module_manager.register_number('Bow-Aimbot', 'Hitbox-Aim-Height', 0.001, 1.001, 0.75)
module_manager.register_number('Bow-Aimbot', 'Prediction-Add', 0.001, 20.001, 5.001)
module_manager.register_number('Bow-Aimbot', 'Prediction-Mult', 0.001, 2.001, 1.001)
module_manager.register_number('Bow-Aimbot', 'Max-Target-Distance', 0, 100, 50)
module_manager.register_number('Bow-Aimbot', 'Max-Target-Hurttime', 0, 10, 10)
module_manager.register_number('Bow-Aimbot', 'Rotation-Speed', 0.001, 1.001, 0.25)
module_manager.register_number('Bow-Aimbot', 'Rotation-Randomization', 0, 50, 10)
module_manager.register_boolean('Bow-Aimbot', 'Silent-Rotation', true)
module_manager.register_boolean('Bow-Aimbot', 'Team-Check', true)
module_manager.register_boolean('Bow-Aimbot', 'Player-Check', true)
module_manager.register_boolean('Bow-Aimbot', 'HUD', true)
module_manager.register_boolean('Bow-Aimbot', 'Target-Tracer', true)
module_manager.register_boolean('Bow-Aimbot', 'Prediction-Tracer', true)
module_manager.register_boolean('Bow-Aimbot', 'Target-ESP', true)
module_manager.register_boolean('Bow-Aimbot', 'Prediction-ESP', true)
module_manager.register_number('Bow-Aimbot', 'Tracer-Width', 1, 10, 2)
module_manager.register_number('Bow-Aimbot', 'ESP-Width', 1, 10, 1)
module_manager.register_number('Bow-Aimbot', 'Target-Red', 0, 255, 255)
module_manager.register_number('Bow-Aimbot', 'Target-Green', 0, 255, 20)
module_manager.register_number('Bow-Aimbot', 'Target-Blue', 0, 255, 20)
module_manager.register_number('Bow-Aimbot', 'Target-Alpha', 0, 255, 200)
module_manager.register_number('Bow-Aimbot', 'Prediction-Red', 0, 255, 20)
module_manager.register_number('Bow-Aimbot', 'Prediction-Green', 0, 255, 255)
module_manager.register_number('Bow-Aimbot', 'Prediction-Blue', 0, 255, 20)
module_manager.register_number('Bow-Aimbot', 'Prediction-Alpha', 0, 255, 200)
module_manager.register_number('Bow-Aimbot', 'Pos-X', 0, 384000, 375)
module_manager.register_number('Bow-Aimbot', 'Pos-Y', 0, 216000, 300)
