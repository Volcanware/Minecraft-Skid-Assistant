function solid_block(x, y, z)
  if world.block(x, y, z) ~= 'tile.air' and world.block(x, y, z) ~= 'tile.water' and world.block(x, y, z) ~= 'tile.deadbush' and world.block(x, y, z) ~= 'tile.reeds' and world.block(x, y, z) ~= 'tile.tallgrass' and world.block(x, y, z) ~= 'tile.doublePlant' and world.block(x, y, z) ~= 'tile.web' and world.block(x, y, z) ~= 'tile.lava' then
    return true
  else
    return false
  end
end

function ground_distance()
  local p_id = player.id()
  local entities = world.entities()
  local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(p_id)
  for i = min_y, 0, -0.1 do --step size can cause lag at extremely high fall distances
    if solid_block(min_x, i, min_z) or solid_block(min_x, i, max_z) or solid_block(max_x, i, min_z) or solid_block(max_x, i, max_z) then
      return min_y - i
    end
  end
  return nil
end

function ground_cords()
  local p_id = player.id()
  local entities = world.entities()
  local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(p_id)
  local ground_x, ground_y, ground_z
  local p_x, p_y, p_z = player.position()
  for i = min_y, 0, -0.1 do
    if solid_block(p_x, i, p_z) and solid_block(min_x, i, min_z) and solid_block(min_x, i, max_z) and solid_block(max_x, i, min_z) and solid_block(max_x, i, max_z) then
      return p_x, i, p_z
    elseif solid_block(min_x, i, min_z) then
      ground_x, ground_y, ground_z = min_x, i, min_z
      break
    elseif solid_block(min_x, i, max_z) then
      ground_x, ground_y, ground_z = min_x, i, max_z
      break
    elseif solid_block(max_x, i, min_z) then
      ground_x, ground_y, ground_z = max_x, i, min_z
      break
    elseif solid_block(max_x, i, max_z) then
      ground_x, ground_y, ground_z = max_x, i, max_z
      break
    end
  end
  return math.floor(ground_x) + 0.5, ground_y, math.floor(ground_z) + 0.5
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

function has_item(item_name)
  if client.gui_name() == 'chest' then
    return false
  end
  for i = 44, 0, -1 do
    if player.inventory.item_information(i) == item_name and (not module_manager.option('Auto-MLG', 'Hotbar-Only') or i >= 36) then
      return true
    end
  end
  return false
end

function cubic_bezier_curve(t, x1, y1, x2, y2, x3, y3, x4, y4)
  local x_cord = (1-t)^3 * x1 + 3*(1 - t)^2 * t * x2 + 3*(1-t) * t^2 * x3 + t^3 * x4
  local y_cord = (1-t)^3 * y1 + 3*(1 - t)^2 * t * y2 + 3*(1-t) * t^2 * y3 + t^3 * y4
  if y_cord > 90 then y_cord = 90 end
  if y_cord < -90 then y_cord = -90 end
  return x_cord, y_cord
end

local delay, stage = 0, 1
local bucket_slot, current_slot
local water_x, water_y, water_z
local hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z
local aura_status, sprint_status, autorod_status
local start_yaw, start_pitch, target_yaw, target_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, current_yaw, current_pitch, restore_yaw, restore_pitch
local bezier_t

local automlg = {
  on_enable = function()
    current_slot = player.held_item_slot()
    delay, stage = 0, 1
  end,

  on_pre_motion = function(motion)
    local m_x, m_y, m_z = player.motion()

    if module_manager.option('Auto-MLG', 'no-water-nofall') and not has_item('item.bucketWater') and not module_manager.is_module_on('nofall') then
      player.message('.nofall')
    elseif module_manager.option('Auto-MLG', 'no-water-nofall') and has_item('item.bucketWater') and module_manager.is_module_on('nofall') then
      player.message('.nofall')
    elseif module_manager.is_module_on('nofall') and not module_manager.option('Auto-MLG', 'no-water-nofall') and module_manager.is_module_on('nofall') then
      client.print('nofall disabled as it is not needed with automlg and could conflict')
      player.message('.nofall')
    end

    if stage == 1 and delay <= 0 and player.fall_distance() > module_manager.option('Auto-MLG', 'fall-distance') and ground_distance() ~= nil and ground_distance() < (m_y * -3) and (has_item('item.bucketWater') or (player.is_potion_active(12) and has_item('item.bucketLava'))) then
      aura_status = module_manager.is_module_on('killaura')
      if aura_status then
        player.message('.killaura')
      end
      autorod_status = module_manager.is_module_on('Auto-Rod')
      if autorod_status then
        player.message('.Auto-Rod')
      end
      if has_item('item.bucketWater') then
        bucket_slot = equip_item_by_code('item.bucketWater', module_manager.option('Auto-MLG', 'equip-slot'))
      elseif player.is_potion_active(12) and has_item('item.bucketLava') then
        bucket_slot = equip_item_by_code('item.bucketLava', module_manager.option('Auto-MLG', 'equip-slot'))
      end
      stage = 2
    end

    if stage == 2 and delay <= 0 and player.fall_distance() > module_manager.option('Auto-MLG', 'fall-distance') and ground_distance() ~= nil and ground_distance() < (m_y * -1) and (has_item('item.bucketWater') or (player.is_potion_active(12) and has_item('item.bucketLava'))) then
      if current_slot ~= bucket_slot then
        player.send_packet(0x09, bucket_slot)
      end
      stage = 3
    end

    if stage == 3 and delay <= 0 then
      water_x, water_y, water_z = ground_cords()
      local ground_yaw, ground_pitch = player.angles_for_cords(water_x, water_y, water_z)
      hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(ground_yaw, ground_pitch, 6)
      motion.yaw, motion.pitch = ground_yaw, ground_pitch
      --client.print(player.ticks_existed() .. ' setting to place')
      --send = true
      sprint_status = module_manager.is_module_on('sprint')
      if sprint_status and module_manager.option('Auto-MLG', 'no-sprint-pickup') then
        player.message('.sprint')
      end
      restore_yaw, restore_pitch = player.angles()
      delay = 1
      stage = 4
    end

    if stage == 4 and delay <= 0 then
      if delay == 0 then
        if hit_type == 2 then
          player.send_packet(0x08, block_x, block_y, block_z, side_hit, bucket_slot, hit_x - math.floor(hit_x), hit_y - math.floor(hit_y), hit_z - math.floor(hit_z))
        else
          player.send_packet(0x08, water_x, water_y, water_z, 1, bucket_slot, 0.5, 1, 0.5)
        end
        player.send_packet(0x08, bucket_slot)
      end
      local water_yaw, water_pitch = player.angles_for_cords(water_x, water_y, water_z)
      motion.yaw, motion.pitch = water_yaw, water_pitch
      --client.print(player.ticks_existed() .. ' setting after place')
      water_y = water_y + 0.6
      delay = module_manager.option('Auto-MLG', 'pickup-delay') - 1
      stage = 5
    end

    if stage == 5 then
      local water_yaw, water_pitch = player.angles_for_cords(water_x, water_y, water_z)
      motion.yaw, motion.pitch = water_yaw, water_pitch
      if delay <= 0 then
        hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(water_yaw, water_pitch, 6)
        start_yaw, start_pitch = water_yaw, water_pitch
        target_yaw, target_pitch = player.angles()
        random_yaw_1, random_pitch_1 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
        random_yaw_2, random_pitch_2 = math.random(math.min(start_yaw, target_yaw), math.max(start_yaw, target_yaw)), math.random(math.min(start_pitch, target_pitch), math.max(start_pitch, target_pitch))
        bezier_t = 0
        delay = 1
        stage = 6
      end
      --client.print(player.ticks_existed() .. ' setting waiting for pickup')
    end

    if stage == 6 and delay <= 0 then
      if delay == 0 then
        if hit_type == 2 then
          player.send_packet(0x08, block_x, block_y, block_z, side_hit, bucket_slot, hit_x - math.floor(hit_x), hit_y - math.floor(hit_y), hit_z - math.floor(hit_z))
        else
          player.send_packet(0x08, water_x, water_y, water_z, 1, bucket_slot, 0.5, 1, 0.5)
        end
        player.send_packet(0x08, bucket_slot)
      end
      target_yaw, target_pitch = player.angles()
      if bezier_t + module_manager.option('Auto-MLG', 'rotation-incriment') >= 1 and target_yaw == restore_yaw and target_pitch == restore_pitch then --randomize angles a bit to prevent being the exact same as before rotation for throw
        target_yaw, target_pitch = target_yaw + math.random(-1, 1), target_pitch + math.random(-1, 1)
        player.set_angles(target_yaw, target_pitch)
      end
      bezier_t = bezier_t + module_manager.option('Auto-MLG', 'rotation-incriment')
      if bezier_t > 1 then bezier_t = 1 end
      current_yaw, current_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      motion.yaw, motion.pitch = current_yaw, current_pitch
      --client.print(player.ticks_existed() .. ' setting back to crosshair')
      if bezier_t >= 1 then
        delay = 1
        stage = 7
      end
    end

    if stage == 7 and delay <= 0 then
      if current_slot ~= player.held_item_slot() then
        player.send_packet(0x09, player.held_item_slot())
      end
      if aura_status then
        player.message('.killaura')
      end
      if autorod_status then
        player.message('.Auto-Rod')
      end
      if sprint_status and module_manager.option('Auto-MLG', 'no-sprint-pickup') then
        player.message('.sprint')
      end
      stage = 1
    end

    return motion
  end,

  on_pre_update = function()
    delay = delay - 1
  end,

  on_send_packet = function(packet)
    if packet.packet_id == 0x09 then
      current_slot = packet.slot + 1
    end
    return packet
  end,
}

module_manager.register('Auto-MLG', automlg)
module_manager.register_number('Auto-MLG', 'equip-slot', 1, 9, 4)
module_manager.register_number('Auto-MLG', 'fall-distance', 2.99, 12.01, 2.99)
module_manager.register_number('Auto-MLG', 'pickup-delay', 1, 10, 4)
module_manager.register_boolean('Auto-MLG', 'no-sprint-pickup', false)
module_manager.register_number('Auto-MLG', 'rotation-incriment', 0.01, 1.01, 0.21)
module_manager.register_boolean('Auto-MLG', 'no-water-nofall', false)
module_manager.register_boolean('Auto-MLG', 'Hotbar-Only', false)
