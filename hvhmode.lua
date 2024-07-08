local hvhmode = {
    on_enable = function()

      if module_manager.is_module_on('Auto-Rod') then
        player.message('.Auto-Rod')
      end

      local entities = world.entities()

      if module_manager.option('hvhmode', 'AutoEnemy') then
        local aura_id = player.kill_aura_target()
        if aura_id ~= nil and world.is_player(aura_id) and not world.is_bot(aura_id) then
          player.message('.enemies add ' .. world.name(aura_id))
        end
      end

      if module_manager.option('hvhmode', 'KillAura') then
        if not module_manager.is_module_on('killaura') then
          player.message('.killaura')
        end
        player.message('.killaura Mode SINGLE')
        player.message('.killaura Delay Random 0')
        player.message('.killaura Delay Miss-Chance 0')
        player.message('.killaura Rotations FOV 360')
        player.message('.killaura Range 5')
        player.message('.killaura Strafe Range 1.6')
        if module_manager.option('qol', 'rmb-block') then
          player.message('.qol rmb-block false')
        end
        player.message('.killaura Auto-Block WATCHDOG')
        player.message('.killaura Strafe Enable true')
        player.message('.killaura Strafe Form CIRCLE')
        player.message('.killaura Strafe Mode ON_SPACE')
        player.message('.killaura Strafe Points 11')
      end

      if module_manager.option('hvhmode', 'NoSlow') then
        if not module_manager.is_module_on('noslow') then
          player.message('.noslow')
        end
      end

      if module_manager.is_module_on('timer') then
        player.message('.timer')
      end
      if module_manager.option('hvhmode', 'Timer') then
        player.message('.timer Speed 1.6')
      end

      if module_manager.option('hvhmode', 'Criticals') then
        if not module_manager.is_module_on('criticals') then
          player.message('.criticals')
        end
        player.message('.criticals Safe false')
        player.message('.criticals Ticks 8')
      end

      if module_manager.option('hvhmode', 'Speed') then
        if not module_manager.is_module_on('speed') then
          player.message('.speed')
        end
        player.message('.Speed Damage-Booster .3')
        player.message('.Speed Mode WATCHDOG')
        player.message('.Speed Time-Out Mode NONE')
        player.message('.Speed Watchdog Slow-Strafe false')
      end

      if module_manager.option('hvhmode', 'AutoHeal') then
        if not module_manager.is_module_on('autoheal') then
          player.message('.autoheal')
        end
        player.message('.autoheal Potions Resistance true')
        player.message('.autoheal Potions Speed true')
      end

      if module_manager.option('hvhmode', 'Velocity') then
        if not module_manager.is_module_on('velocity') then
          player.message('.velocity')
        end
        player.message('.velocity Mode PACKET')
        player.message('.velocity Horizontal 0')
        player.message('.velocity Vertical 0')
      end

      if module_manager.option('hvhmode', 'Inventory') then
        player.message('.autoarmor Inventory false')
        player.message('.inventorymanager General Drop-Mode Always')
      end

    end
}

module_manager.register('hvhmode', hvhmode)
module_manager.register_boolean('hvhmode', 'AutoEnemy', true)
module_manager.register_boolean('hvhmode', 'KillAura', true)
module_manager.register_boolean('hvhmode', 'NoSlow', true)
module_manager.register_boolean('hvhmode', 'Timer', true)
module_manager.register_boolean('hvhmode', 'Criticals', true)
module_manager.register_boolean('hvhmode', 'Speed', true)
module_manager.register_boolean('hvhmode', 'AutoHeal', true)
module_manager.register_boolean('hvhmode', 'Velocity', true)
module_manager.register_boolean('hvhmode', 'Inventory', true)
