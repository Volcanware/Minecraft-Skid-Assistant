local delay, stage = 0, 1
local current_slot, bucket_slot
local aura_status, autorod_status, ignite_status, auto_projectile_status

local start_yaw, start_pitch
local target_yaw, target_pitch
local random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2

local hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z
local water_x, water_y, water_z

local restore_slot, restore_yaw, restore_pitch

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

function in_shallow_lava()
  local x, y, z = player.position()
  if world.block(x, y, z) == 'tile.lava' and world.block(x, y - 1, z) ~= 'tile.lava' then
    return true
  else
    return false
  end
end

function has_item(item_name)
  if client.gui_name() == 'chest' then
    return false
  end
  for i = 44, 0, -1 do
    if player.inventory.item_information(i) == item_name and (not module_manager.option('Extinguish', 'Hotbar-Only') or i >= 36) then
      return true
    end
  end
  return false
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

function toggle_conflicting_modules_off()
  aura_status = module_manager.is_module_on('killaura')
  if aura_status then
    player.message('.killaura')
  end
  autorod_status = module_manager.is_module_on('Auto-Rod')
  if autorod_status then
    player.message('.Auto-Rod')
  end
  ignite_status = module_manager.is_module_on('ignite')
  if ignite_status then
    player.message('.ignite')
  end
  auto_projectile_status = module_manager.is_module_on('Auto-Projectile')
  if auto_projectile_status then
    player.message('.Auto-Projectile')
  end
end

function distance_2d(x1, y1, x2, y2)
  return math.sqrt(math.pow(x1 - x2, 2) + math.pow(y1 - y2, 2))
end

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

function toggle_conflicting_modules_on()
  if aura_status then
    player.message('.killaura')
  end
  if autorod_status then
    player.message('.Auto-Rod')
  end
  if ignite_status then
    player.message('.ignite')
  end
  if auto_projectile_status then
    player.message('.Auto-Projectile')
  end
end

local Extinguish = {

  on_enable = function()
    stage = 1
    current_slot = player.held_item_slot()
  end,

  on_send_packet = function(packet)
    if packet.packet_id == 0x09 then
      current_slot = packet.slot + 1
    end
    return packet
  end,

  on_pre_motion = function(motion)
    local has_fire_res = player.is_potion_active(12)

    if stage == 1 and delay <= 0 and has_item('item.bucketWater') and ((not has_fire_res) or (in_shallow_lava() and module_manager.option('Extinguish', 'Lava-When-Fire-Res'))) and ((player.burning() and (player.kill_aura_target() == nil or module_manager.option('Extinguish', 'Fire-When-Target'))) or (in_shallow_lava() and (player.kill_aura_target() == nil or module_manager.option('Extinguish', 'Lava-When-Target')))) then
      bucket_slot = equip_item_by_code('item.bucketWater', module_manager.option('Extinguish', 'Slot'))
      start_yaw, start_pitch = player.angles()
      target_yaw, target_pitch = start_yaw, 90
      random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2 = bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, module_manager.option('Extinguish', 'Rotation-Randomization'))
      restore_yaw, restore_pitch = player.angles()
      bezier_t = 0
      hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(target_yaw, target_pitch, 6)
      if hit_type == 2 then
        toggle_conflicting_modules_off()
        delay = 1
        stage = 2
      end
    end

    if stage == 2 and delay <= 0 then
      yaw, pitch = player.angles()
      target_yaw, target_pitch = yaw, 90
      bezier_t = bezier_t + module_manager.option('Extinguish', 'Rotation-Speed')
      if bezier_t > 1 then bezier_t = 1 end
      local set_yaw, set_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      motion.yaw, motion.pitch = set_yaw, set_pitch
      if not module_manager.option('Extinguish', 'Silent-Rotation') then
        player.set_angles(set_yaw, set_pitch)
      end
      if set_yaw == target_yaw and set_pitch == target_pitch then
        if not module_manager.option('Extinguish', 'Silent-Switch') then
          restore_slot = player.held_item_slot()
          player.set_held_item_slot(bucket_slot - 2)
        elseif current_slot ~= bucket_slot then
          player.send_packet(0x09, bucket_slot)
        end
        hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(target_yaw, target_pitch, 6)
        if hit_type == 2 then
          water_x, water_y, water_z = block_x + 0.5, block_y + 0.5, block_z + 0.5
          stage = 3
          delay = 1
        else
          toggle_conflicting_modules_on()
          stage = 1
        end
      end
    end

    if stage == 3 and delay <= 0 then
      if hit_type == 2 then
        player.send_packet(0x08, block_x, block_y, block_z, side_hit, bucket_slot, hit_x - math.floor(hit_x), hit_y - math.floor(hit_y), hit_z - math.floor(hit_z))
      end
      player.send_packet(0x08, bucket_slot)
      delay = module_manager.option('Extinguish', 'Pickup-Delay')
      stage = 4
    end

    if stage == 4 then
      target_yaw, target_pitch = player.angles_for_cords(water_x, water_y, water_z)
      motion.yaw, motion.pitch = target_yaw, target_pitch
      if not module_manager.option('Extinguish', 'Silent-Rotation') then
        player.set_angles(player.angles_for_cords(water_x, water_y, water_z))
      end
      if delay == 0 then
        hit_type, side_hit, block_x, block_y, block_z, hit_x, hit_y, hit_z = player.ray_cast(target_yaw, target_pitch, 6)
        start_yaw, start_pitch = player.angles_for_cords(water_x, water_y, water_z)
        if not module_manager.option('Extinguish', 'Silent-Rotation') then
          target_yaw, target_pitch = restore_yaw, restore_pitch
        else
          target_yaw, target_pitch = player.angles()
        end
        random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2 = bezier_midpoints(start_yaw, start_pitch, target_yaw, target_pitch, module_manager.option('Extinguish', 'Rotation-Randomization'))
        bezier_t = 0
        delay = 1
        stage = 5
      end
    end

    if stage == 5 and delay <= 0 then
      if delay == 0 then
        if hit_type == 2 then
          player.send_packet(0x08, block_x, block_y, block_z, side_hit, bucket_slot, hit_x - math.floor(hit_x), hit_y - math.floor(hit_y), hit_z - math.floor(hit_z))
        end
        player.send_packet(0x08, bucket_slot)
      end
      if not module_manager.option('Extinguish', 'Silent-Rotation') then
        target_yaw, target_pitch = restore_yaw, restore_pitch
      else
        target_yaw, target_pitch = player.angles()
      end
      bezier_t = bezier_t + module_manager.option('Extinguish', 'Rotation-Speed')
      if bezier_t > 1 then bezier_t = 1 end
      local set_yaw, set_pitch = cubic_bezier_curve(bezier_t, start_yaw, start_pitch, random_yaw_1, random_pitch_1, random_yaw_2, random_pitch_2, target_yaw, target_pitch)
      motion.yaw, motion.pitch = set_yaw, set_pitch
      if not module_manager.option('Extinguish', 'Silent-Rotation') then
        player.set_angles(set_yaw, set_pitch)
      end
      if set_yaw == target_yaw and set_pitch == target_pitch then
        stage = 6
        delay = 1
      end
    end

    if stage == 6 then
      if not module_manager.option('Extinguish', 'Silent-Switch') then
        player.set_held_item_slot(restore_slot - 2)
      elseif current_slot ~= player.held_item_slot() then
        player.send_packet(0x09, player.held_item_slot())
      end
      toggle_conflicting_modules_on()
      delay = module_manager.option('Extinguish', 'Retry-Delay')
      stage = 1
    end

    delay = delay - 1
    return motion
  end,
}

module_manager.register('Extinguish', Extinguish)
module_manager.register_number('Extinguish', 'Slot', 1, 9, 4)
module_manager.register_number('Extinguish', 'Rotation-Speed', 0.001, 1.001, 0.25)
module_manager.register_number('Extinguish', 'Rotation-Randomization', 0, 50, 10)
module_manager.register_boolean('Extinguish', 'Silent-Switch', false)
module_manager.register_boolean('Extinguish', 'Silent-Rotation', true)
module_manager.register_number('Extinguish', 'Pickup-Delay', 1, 10, 3)
module_manager.register_number('Extinguish', 'Retry-Delay', 1, 100, 20)
module_manager.register_boolean('Extinguish', 'Fire-When-Target', false)
module_manager.register_boolean('Extinguish', 'Lava-When-Target', true)
module_manager.register_boolean('Extinguish', 'Lava-When-Fire-Res', true)
module_manager.register_boolean('Extinguish', 'Hotbar-Only', true)
