local death_status
local last_death_status
local rmouse_status
local last_rmouse_status
local rmouse_ticks
local last_rmouse_ticks
local double_clicked
local last_double_clicked
local fastplace_status
local ground_ticks = -1
local space_ticks = -1
local ground_y
local last_ground_y
local inventorymanager_status = false
local p_x, p_y, p_z
local last_reload_x, last_reload_y, last_reload_z
local target_hurt_time = false
local inventory_status
local antifall_status
local aura_status
local noslow_status

function has_blocks()
  for i = 0, 44, 1 do
    local item_name = player.inventory.item_information(i)
    if item_name ~= nil and string.match(item_name, 'tile.') and not string.match(item_name, 'chest') then
      return true
    end
  end
  return false
end

function find_biggest_block_stack()
  local biggest_stack_index
  local biggest_stack = 0
  for i = 0, 44, 1 do
    local stack_size = player.inventory.get_item_stack(i)
    if stack_size ~= nil and stack_size > biggest_stack and string.match(player.inventory.item_information(i), 'tile.') and not string.match(player.inventory.item_information(i), 'chest') then
      biggest_stack_index = i
      biggest_stack = stack_size
    end
  end
  return biggest_stack_index
end


local qol = {
  on_enable = function()
    death_status = player.dead()
    last_death_status = player.dead()
    last_rmouse_status = input.is_mouse_down(1)
    rmouse_status = last_rmouse_status
    rmouse_ticks = 42069
    last_rmouse_ticks = 42069
    double_clicked = false
    last_double_clicked = false
    fastplace_status = module_manager.is_module_on('fastplace')
    local x, y, z = player.position()
    ground_y = y
    last_ground_y = y
    last_reload_x, last_reload_y, last_reload_z = player.position()
  end,

  on_pre_update = function()

    if module_manager.option('qol', 'allow-kb-weapon') then
      if client.gui_name() ~= 'chest' then
        local item_name, display_name, item_damage, efficiency, enchants_table = player.inventory.item_information(35 + player.held_item_slot())
        if enchants_table ~= nil and enchants_table['19'] ~= nil then
          player.message('.autotool With-KillAura false')
        else
          player.message('.autotool With-KillAura true')
        end
      end
    end

    if module_manager.option('qol', 'anvil-fix') then
      if client.gui_name() == 'chest' and client.chest_name() ~= 'Chest' and not inventorymanager_status then
        inventorymanager_status = module_manager.is_module_on('inventorymanager')
        if inventorymanager_status then
          player.message('.inventorymanager')
        end
      end
      if client.gui_name() ~= 'chest' and inventorymanager_status then
        player.message('.inventorymanager')
        inventorymanager_status = false
      end
    end

    if module_manager.option('qol', 'death-waypoints') then
      last_death_status = death_status
      death_status = player.dead()
      if not last_death_status and death_status then
        player.message('.waypoints add death')
      end
    end

    if module_manager.option('qol', 'easy-fastplace') or module_manager.option('qol', 'easy-noslow') then
      last_rmouse_status = rmouse_status
      rmouse_status = input.is_mouse_down(1)
      if not last_rmouse_status and rmouse_status then
        last_rmouse_ticks = rmouse_ticks
        rmouse_ticks = 0
      end
      last_double_clicked = double_clicked
      if rmouse_ticks == 0 and last_rmouse_ticks < module_manager.option('qol', 'easy-enable-delay') then
        double_clicked = true
        fastplace_status = module_manager.is_module_on('fastplace')
        if not fastplace_status and module_manager.option('qol', 'easy-fastplace') then
          player.message('.fastplace')
        end
        noslow_status = module_manager.is_module_on('Hypixel-NoSlow')
        if not noslow_status and module_manager.option('qol', 'easy-noslow') then
          player.message('.Hypixel-NoSlow')
        end
      end
      if not rmouse_status then
        double_clicked = false
      end
      if module_manager.is_module_on('fastplace') and not fastplace_status and last_double_clicked and not double_clicked and module_manager.option('qol', 'easy-fastplace') then
        player.message('.fastplace')
      end
      if module_manager.is_module_on('Hypixel-NoSlow') and not noslow_status and last_double_clicked and not double_clicked and module_manager.option('qol', 'easy-noslow') then
        player.message('.Hypixel-NoSlow')
      end
      rmouse_ticks = rmouse_ticks + 1
      last_rmouse_ticks = last_rmouse_ticks + 1
    end

    if module_manager.option('qol', 'gapple-no-aura') then
      if client.gui_name() ~= 'chest' then
        local item_name = player.inventory.item_information(35 + player.held_item_slot())
        if item_name ~= nil and item_name == 'item.appleGold' and input.is_mouse_down(1) and module_manager.is_module_on('killaura') then
          aura_status = module_manager.is_module_on('killaura')
          if aura_status then
            player.message('.killaura')
          end
        elseif aura_status and not input.is_mouse_down(1) then
          player.message('.killaura')
          aura_status = false
        end
      end
    end

    if module_manager.option('qol', 'headhitter') then
      if input.is_key_down(57) and player.on_ground() and last_ground_y == ground_y and space_ticks >= 1 and ground_ticks >= 1 and client.gui_name() ~= 'chest' and client.gui_name() ~= 'chat' then
        player.jump()
      end
      if player.on_ground() then
        ground_ticks = ground_ticks + 1
        local x, y, z = player.position()
        last_ground_y = ground_y
        ground_y = y
      else
        ground_ticks = 0
      end
      if input.is_key_down(57) then
        space_ticks = space_ticks + 1
      else
        space_ticks = 0
      end
    end

    if module_manager.option('qol', 'rmb-block') then
      if input.is_mouse_down(1) then
        player.message('.autoclicker Block Enable true')
        player.message('.killaura Auto-Block WATCHDOG')
      else
        player.message('.autoclicker Block Enable false')
        player.message('.killaura Auto-Block NONE')
      end
    end

    if module_manager.option('qol', 'smart-knockback') then
      local aura_target = player.kill_aura_target()
      local entities = world.entities()
      if world.health(aura_target) ~= nil and player.health() ~= nil then
        if (input.is_mouse_down(0) or (player.health() < world.health(aura_target) - 0.5)) and not module_manager.is_module_on('knockback') then
          player.message('.knockback')
        end
        if world.hurt_time(aura_target) == module_manager.option('knockback', 'Hurt-Time') and module_manager.is_module_on('knockback') then
          target_hurt_time = true
        end
        if world.hurt_time(aura_target) == module_manager.option('knockback', 'Hurt-Time') - 1 and target_hurt_time and module_manager.is_module_on('knockback') then
          player.message('.knockback')
        end
      end

      if (not module_manager.is_module_on('killaura') or world.health(aura_target) == nil) and module_manager.is_module_on('knockback') then
        player.message('.knockback')
      end
    end

    if module_manager.option('qol', 'space-no-antifall') then
      if module_manager.is_module_on('antifall') and input.is_key_down(57) then
        antifall_status = module_manager.is_module_on('antifall')
        player.message('.antifall')
      elseif not input.is_key_down(57) and antifall_status and not module_manager.is_module_on('antifall') then
        player.message('.antifall')
        antifall_status = false
      end
    end

    if module_manager.option('qol', 'speed-strafe-fix') then
      if (player.sprinting() and player.base_speed() >= 0.4) or (not player.sprinting() and player.base_speed() >= 0.3) then
        player.message('.speed Watchdog Slow-Strafe true')
      else
        player.message('.speed Watchdog Slow-Strafe false')
      end
    end

    if module_manager.option('qol', 'x-ray-chunk-border') then
      if module_manager.is_module_on('xray') and not module_manager.is_module_on('chunkborders') and module_manager.option('xray', 'ESP', 'Enable') then
        player.message('.chunkborders')
      elseif (not module_manager.is_module_on('xray') or not module_manager.option('xray', 'ESP', 'Enable')) and module_manager.is_module_on('chunkborders') then
        player.message('.chunkborders')
      end
    end

    if module_manager.option('qol', 'x-ray-reload') then
      if module_manager.is_module_on('xray') and (player.distance_to(last_reload_x, last_reload_y, last_reload_z) > module_manager.option('qol', 'x-ray-reload-distance')) then
        world.refresh_chunks()
        last_reload_x, last_reload_y, last_reload_z = player.position()
      end
    end

    if module_manager.option('qol', 'zoom-scale') then
      if input.is_key_down(46) then
        player.message('.esp Name Scale 2')
        player.message('.esp Items Item-Scale 2')
      else
        player.message('.esp Name Scale 1')
        player.message('.esp Items Item-Scale 1')
      end
    end

  end,

}

module_manager.register('qol', qol)
module_manager.register_boolean('qol', 'allow-kb-weapon', false)
module_manager.register_boolean('qol', 'anvil-fix', false)
module_manager.register_boolean('qol', 'death-waypoints', false)
module_manager.register_boolean('qol', 'easy-fastplace', false)
module_manager.register_number('qol', 'easy-fastplace-delay', 1, 10, 5)
module_manager.register_boolean('qol', 'easy-noslow', false)
module_manager.register_number('qol', 'easy-enable-delay', 1, 10, 5)
module_manager.register_boolean('qol', 'gapple-no-aura', false)
module_manager.register_boolean('qol', 'headhitter', false)
module_manager.register_boolean('qol', 'rmb-block', false)
module_manager.register_boolean('qol', 'smart-knockback', false)
module_manager.register_boolean('qol', 'space-no-antifall', false)
module_manager.register_boolean('qol', 'x-ray-chunk-border', false)
module_manager.register_boolean('qol', 'x-ray-reload', false)
module_manager.register_number('qol', 'x-ray-reload-distance', 10, 50, 20)
module_manager.register_boolean('qol', 'zoom-scale', false)
