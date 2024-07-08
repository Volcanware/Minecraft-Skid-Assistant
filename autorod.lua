local rod_slot, current_slot, held_slot = nil, nil, nil
local delay, stage, throw_ticks, retract_ticks = 0, 0, 0, 0
local thrown = false
local e_x, e_y, e_z
local l_e_x, l_e_y, l_e_z
local start_yaw, start_pitch, target_yaw, target_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, restore_yaw, restore_pitch, current_yaw, current_pitch
local bezier_t = 0
local switch_hurt_time, macro_mode_throws

function tprint (tbl, indent)
  if not indent then indent = 0 end
  if tbl == nil then client.print('nil') return end
  for k, v in pairs(tbl) do
    formatting = string.rep("  ", indent) .. k .. ": "
    if type(v) == "table" then
      client.print(formatting)
      tprint(v, indent+1)
    elseif type(v) == 'boolean' then
      client.print(formatting .. tostring(v))
    else
      client.print(formatting .. v)
    end
  end
end

function get_closest_player()
  local entities = world.entities()
  local closest_entity_id = nil
  local closest_entity_distance = 42069 -- arbitrary large num that no entities will be closer than
  for i = 1, #entities do
    local entity_id = entities[i]
    local entity_distance = player.distance_to_entity(entity_id)
    if entity_distance < closest_entity_distance and entity_id ~= player.id() and not world.is_bot(entity_id) and world.name(entity_id) ~= 'unknown' and world.is_player(entity_id) and is_close_to_ground(entity_id) and (not module_manager.option('Auto-Rod', 'team-check') or not is_on_team(entity_id)) then
      closest_entity_id = entity_id
      closest_entity_distance = entity_distance
    end
  end
  return closest_entity_id
end

function equip_item_by_code(item_code, backup_slot)
  if client.gui_name() == 'chest' then
    return -1
  end
  for i = 44, 0, -1 do
    if player.inventory.item_information(i) == item_code then
      local item_slot
      if i < 36 then
        player.inventory.swap(i, backup_slot-1)
        item_slot = backup_slot-1
      else
        item_slot = i-36
      end
      return item_slot + 1
    end
  end
  return -1
end

function is_close_to_ground(entity_id)
  local entities = world.entities()
  local e_x, e_y, e_z = world.position(entity_id)
  if world.block(e_x, e_y - 1, e_z) ~= 'tile.air' or world.block(e_x, e_y - 2, e_z) ~= 'tile.air' then
    return true
  else
    return false
  end
end

function has_clear_view(entity_id)
  local entities = world.entities()
  local e_x, e_y, e_z = world.position(entity_id)
  local set_yaw, set_pitch = player.angles_for_cords(e_x, e_y + 1, e_z)
  local ray_cast = player.ray_cast(set_yaw, set_pitch, module_manager.option('Auto-Rod', 'max-distance'))
  if ray_cast == 3 then
    return true
  end
  return false
end

function is_on_team(entityID)
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

function has_item(item_name)
  if client.gui_name() == 'chest' then
    return false
  end
  for i = 44, 0, -1 do
    if player.inventory.item_information(i) == item_name and (not module_manager.option('Auto-Rod', 'Hotbar-Only') or i >= 36) then
      return true
    end
  end
  return false
end

function predict_cords(entity_id, e_x, e_y, e_z, l_e_x, l_e_y, l_e_z)
  local x_adj, z_adj = (e_x - l_e_x) * player.distance_to_entity(entity_id) * module_manager.option('Auto-Rod', 'predict'), (e_z - l_e_z) * player.distance_to_entity(entity_id) * module_manager.option('Auto-Rod', 'predict')
  local e_min_x, e_min_y, e_min_z, e_max_x, e_max_y, e_max_z = world.bounding_box(entity_id)
  return e_x + x_adj, e_y + (e_max_y - e_min_y)*1/2 + (player.distance_to_entity(entity_id) * player.distance_to_entity(entity_id))/150, e_z + z_adj
end

function check_miss(target_entity_id)
  local entities = world.entities()
  local target_distance = player.distance_to_entity(target_entity_id)
  for i = 1, #entities do
    local entity_id = entities[i]
    local entity_distance = player.distance_to_entity(entity_id)
    if world.name(entity_id) == 'unknown' and entity_distance > target_distance + module_manager.option('Auto-Rod', 'miss-detect-distance') and entity_distance < target_distance + module_manager.option('Auto-Rod', 'miss-detect-distance') + 1 and retract_ticks >= 10 and world.hurt_time(target_entity_id) == 0 then
      client.print('miss detected')
      return true
    end
  end
  return false
end

function check_fov(entity_id)
  local e_yaw, e_pitch = player.angles_for_cords(world.position(entity_id))
  local yaw, pitch = player.angles()
  while (yaw < 0 or e_yaw < 0) do
    yaw = yaw + 360
    e_yaw = e_yaw + 360
  end
  while yaw > e_yaw + 360 do
    yaw = yaw - 360
  end
  while yaw < e_yaw - 360 do
    yaw = yaw + 360
  end
  local yaw_diff = math.abs(yaw - e_yaw)
  if yaw_diff > 180 then
    yaw_diff = 360 - yaw_diff
  end
  local pitch_diff = math.abs(pitch - e_pitch)
  if yaw_diff < module_manager.option('Auto-Rod', 'h-fov') / 2 and pitch_diff < module_manager.option('Auto-Rod', 'v-fov') then
    return true
  else
    return false
  end
end

function cubic_bezier_curve(t, x1, y1, x2, y2, x3, y3, x4, y4)
  local x_cord = (1-t)^3 * x1 + 3*(1 - t)^2 * t * x2 + 3*(1-t) * t^2 * x3 + t^3 * x4
  local y_cord = (1-t)^3 * y1 + 3*(1 - t)^2 * t * y2 + 3*(1-t) * t^2 * y3 + t^3 * y4
  if y_cord > 90 then y_cord = 90 end
  if y_cord < -90 then y_cord = -90 end
  return x_cord, y_cord
end

local autorod = {
  on_enable = function()
    player.message('.killaura')
    player.message('.killaura')
    current_slot, held_slot = player.held_item_slot(), player.held_item_slot()
    delay, stage, macro_mode_throws = 0, 0, 0
    thrown = false
    current_yaw, current_pitch = player.angles()
  end,

  on_send_packet = function(packet)
    if packet.packet_id == 0x09 then
      current_slot = packet.slot + 1
    end
    if packet.packet_id == 0x08 and input.is_mouse_down(1) and current_slot ~= player.held_item_slot() then
      if module_manager.option('Auto-Rod', 'silent-switch') then
        player.send_packet(0x09, player.held_item_slot())
      else
        player.set_held_item_slot(held_slot - 2)
      end
      if module_manager.option('Auto-Rod', 'debug') then
        client.print(player.ticks_existed() .. ': autorod: switched back to held item slot')
      end
      retract_ticks = 0
      stage = 0
    end
    return packet
  end,

  on_disable = function()
    if current_slot == rod_slot then
      if module_manager.option('Auto-Rod', 'silent-switch') then
        player.send_packet(0x09, player.held_item_slot())
      else
        player.set_held_item_slot(held_slot - 2)
      end
      if module_manager.option('Auto-Rod', 'debug') then
        client.print(player.ticks_existed() .. ': autorod: switched back to held item slot')
      end
      retract_ticks = 0
    end
  end,

  on_pre_motion = function(motion)
    local closest_player_id = get_closest_player()

    if closest_player_id == nil or not has_item('item.fishingRod') or input.is_mouse_down(1) or player.distance_to_entity(closest_player_id) < module_manager.option('Auto-Rod', 'min-distance') or player.distance_to_entity(closest_player_id) > module_manager.option('Auto-Rod', 'max-distance') or player.kill_aura_target() ~= nil then
      if current_slot == rod_slot and stage ~= 0 then
        if module_manager.option('Auto-Rod', 'silent-switch') then
          player.send_packet(0x09, player.held_item_slot())
        else
          player.set_held_item_slot(held_slot - 2)
        end
        if module_manager.option('Auto-Rod', 'debug') then
          client.print(player.ticks_existed() .. ': autorod: switched back to held item slot')
        end
      end
      if module_manager.option('Auto-Rod', 'macro-mode') then
        player.message('.autorod')
      end
      retract_ticks = 0
      delay = math.max(math.random(module_manager.option('Auto-Rod', 'min-switch-delay'), module_manager.option('Auto-Rod', 'max-switch-delay')), 1)
      stage = 0
      thrown = false
      player.message('.autoheal other Andúril true')
    end

    if stage == 0 and delay <= 0 and closest_player_id ~= nil and player.distance_to_entity(closest_player_id) > module_manager.option('Auto-Rod', 'min-distance') and player.distance_to_entity(closest_player_id) < module_manager.option('Auto-Rod', 'max-distance') and player.kill_aura_target() == nil and is_close_to_ground(closest_player_id) and has_clear_view(closest_player_id) and has_item('item.fishingRod') and world.hurt_time(closest_player_id) < player.distance_to_entity(closest_player_id) and not module_manager.is_module_on('Legit-Scaff') and check_fov(closest_player_id) and player.fall_distance() < 3 then
      if module_manager.option('Auto-Rod', 'debug') then
        --client.print(player.ticks_existed() .. ': autorod: stage 0')
      end
      held_slot = player.held_item_slot()
      rod_slot = equip_item_by_code('item.fishingRod', module_manager.option('Auto-Rod', 'equip-slot'))
      if current_slot ~= rod_slot then
        if module_manager.option('Auto-Rod', 'silent-switch') then
          player.send_packet(0x09, rod_slot)
        else
          player.set_held_item_slot(rod_slot - 2)
        end
        if module_manager.option('Auto-Rod', 'debug') then
          client.print(player.ticks_existed() .. ': autorod: switched to rod slot')
        end
      end
      l_e_x, l_e_y, l_e_z = world.position(closest_player_id)
      start_yaw, start_pitch = player.angles()
      target_yaw, target_pitch = player.angles_for_cords(predict_cords(closest_player_id, l_e_x, l_e_y, l_e_z, l_e_x, l_e_y, l_e_z))
      random_yaw_1, random_pitch_1 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
      random_yaw_2, random_pitch_2 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
      bezier_t = 0
      restore_yaw, restore_pitch = player.angles()
      delay = 1
      stage = 1
    end

    if stage == 1 and delay <= 0 and current_slot == rod_slot then
      if module_manager.option('Auto-Rod', 'debug') then
        --client.print(player.ticks_existed() .. ': autorod: stage 1')
      end
      e_x, e_y, e_z = world.position(closest_player_id)
      target_yaw, target_pitch = player.angles_for_cords(predict_cords(closest_player_id, e_x, e_y, e_z, l_e_x, l_e_y, l_e_z))
      bezier_t = bezier_t + module_manager.option('Auto-Rod', 'rotation-incriment')
      if bezier_t > 1 then bezier_t = 1 end
      current_yaw, current_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      if not module_manager.option('Auto-Rod', 'silent-rotations') then
        player.set_angles(current_yaw, current_pitch)
      end
      motion.yaw, motion.pitch = current_yaw, current_pitch
      --client.print(player.ticks_existed() .. ': rotating to target (' .. bezier_t .. ')')
      if bezier_t >= 1 then
        thrown = true
        player.message('.autoheal other Andúril false')
        stage = 2
      end
      delay = 1
      l_e_x, l_e_y, l_e_z = world.position(closest_player_id)
    end

    if stage == 2 and delay <= 0 then
      if module_manager.option('Auto-Rod', 'silent-switch') then
        player.send_packet(0x08, rod_slot)
        player.send_packet(0x0a)
      else
        player.right_click_mouse()
      end
      if module_manager.option('Auto-Rod', 'debug') then
        client.print(player.ticks_existed() .. ': autorod: used item')
      end
      throw_ticks = 0
      macro_mode_throws = macro_mode_throws + 1
      delay = math.random(module_manager.option('Auto-Rod', 'min-throw-ticks'), module_manager.option('Auto-Rod', 'max-throw-ticks'))
      switch_hurt_time = 10 - math.random(module_manager.option('Auto-Rod', 'min-switch-delay'), module_manager.option('Auto-Rod', 'max-switch-delay'))
      start_yaw, start_pitch = current_yaw, current_pitch
      if module_manager.option('Auto-Rod', 'silent-rotations') then
        target_yaw, target_pitch = player.angles()
      else
        target_yaw, target_pitch = restore_yaw + math.random(-1, 1), restore_pitch + math.random(-1, 1)
      end
      random_yaw_1, random_pitch_1 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
      random_yaw_2, random_pitch_2 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
      bezier_t = 0
      delay = math.random(module_manager.option('Auto-Rod', 'min-throw-ticks'), module_manager.option('Auto-Rod', 'max-throw-ticks'))
      stage = 3
    end

    if stage == 3 then
      if bezier_t ~= nil then bezier_t = bezier_t + module_manager.option('Auto-Rod', 'rotation-incriment') end
      if bezier_t ~= nil and bezier_t > 1 then bezier_t = 1 end
      if bezier_t ~= nil and bezier_t <= 1 then
        if module_manager.option('Auto-Rod', 'silent-rotations') then
          target_yaw, target_pitch = player.angles()
          if bezier_t + module_manager.option('Auto-Rod', 'rotation-incriment') >= 1 and target_yaw == restore_yaw and target_pitch == restore_pitch then --randomize angles a bit to prevent being the exact same as before rotation for throw
            target_yaw, target_pitch = target_yaw + math.random(-1, 1), target_pitch + math.random(-1, 1)
            player.set_angles(target_yaw, target_pitch)
          end
        end
        --client.print(player.ticks_existed() .. ': rotating back to crosshair (' .. bezier_t .. ')')
        current_yaw, current_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
        if not module_manager.option('Auto-Rod', 'silent-rotations') then
          player.set_angles(current_yaw, current_pitch)
        end
        motion.yaw, motion.pitch = current_yaw, current_pitch
        if bezier_t == 1 then
          bezier_t = nil
        end
      end
      if (world.hurt_time(closest_player_id) == switch_hurt_time or delay <= 0 or check_miss(closest_player_id)) and bezier_t == nil then
        if module_manager.option('Auto-Rod', 'debug') then
          --client.print(player.ticks_existed() .. ': autorod: stage 2')
        end
        if current_slot == rod_slot then
          if (module_manager.option('Auto-Rod', 'silent-switch') and player.held_item_slot() ~= rod_slot) or (not module_manager.option('Auto-Rod', 'silent-switch') and held_slot ~= rod_slot) then
            if module_manager.option('Auto-Rod', 'silent-switch') then
              player.send_packet(0x09, player.held_item_slot())
            else
              player.set_held_item_slot(held_slot - 2)
            end
            if module_manager.option('Auto-Rod', 'debug') then
              client.print(player.ticks_existed() .. 'stage: ' .. stage .. ': autorod: switched back to held item slot')
            end
          else
            if module_manager.option('Auto-Rod', 'silent-switch') then
              player.send_packet(0x09, (current_slot % 9) + 1)
            else
              player.set_held_item_slot((current_slot % 9) + 1 - 2)
            end
            if module_manager.option('Auto-Rod', 'debug') then
              client.print(player.ticks_existed() .. 'stage: ' .. stage .. ': autorod: switched back to held item slot')
            end
          end
        end
        retract_ticks = 0
        delay = math.random(module_manager.option('Auto-Rod', 'min-switch-delay'), module_manager.option('Auto-Rod', 'max-switch-delay'))
        thrown = false
        player.message('.autoheal other Andúril true')
        if module_manager.option('Auto-Rod', 'macro-mode') and module_manager.option('Auto-Rod', 'macro-mode-throws') <= macro_mode_throws then
          player.message('.autorod')
        end
        stage = 0
      end
    end

    return motion
  end,

  on_pre_update = function()
    delay = delay - 1
    throw_ticks, retract_ticks = throw_ticks + 1, retract_ticks + 1
  end

}

module_manager.register('Auto-Rod', autorod)
module_manager.register_number('Auto-Rod', 'min-distance', 0.01, 15.01, 4.01)
module_manager.register_number('Auto-Rod', 'max-distance', 0.01, 15.01, 12.01)
module_manager.register_number('Auto-Rod', 'min-throw-ticks', 10, 60, 30)
module_manager.register_number('Auto-Rod', 'max-throw-ticks', 10, 60, 40)
module_manager.register_number('Auto-Rod', 'predict', 0.01, 3.01, 1.25)
module_manager.register_number('Auto-Rod', 'miss-detect-distance', 0.01, 3.01, 1.51)
module_manager.register_number('Auto-Rod', 'h-fov', 0, 360, 120)
module_manager.register_number('Auto-Rod', 'v-fov', 0, 180, 60)
module_manager.register_number('Auto-Rod', 'rotation-incriment', 0.01, 1.01, 0.4)
module_manager.register_number('Auto-Rod', 'min-switch-delay', 0, 9, 2)
module_manager.register_number('Auto-Rod', 'max-switch-delay', 0, 9, 5)
module_manager.register_number('Auto-Rod', 'equip-slot', 1, 9, 5)
module_manager.register_boolean('Auto-Rod', 'silent-switch', false)
module_manager.register_boolean('Auto-Rod', 'silent-rotations', true)
module_manager.register_boolean('Auto-Rod', 'team-check', true)
module_manager.register_boolean('Auto-Rod', 'macro-mode', false)
module_manager.register_number('Auto-Rod', 'macro-mode-throws', 1, 5, 2)
module_manager.register_boolean('Auto-Rod', 'Hotbar-Only', false)
module_manager.register_boolean('Auto-Rod', 'debug', false)
