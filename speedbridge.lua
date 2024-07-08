local safewalk_toggled = false
local omnisprint_toggled = false
local delay, click_delay = -1, -1

local speedbridgev2 = {
  on_pre_update = function()
    local x, y, z = player.position()
    local yaw, pitch = player.angles()
    local block_below = world.block(x, y - 1, z)

    if player.on_ground() and block_below == 'tile.air' and not input.is_key_down(17) then
      player.set_sneaking(true)
      if click_delay <= 0 then
        click_delay = math.random(module_manager.option('speedbridge', 'min-click-delay'), module_manager.option('speedbridge', 'max-click-delay'))
      end
      delay = math.random(module_manager.option('speedbridge', 'min-unshift-delay'), module_manager.option('speedbridge', 'max-unshift-delay'))
      if not safewalk_toggled and not module_manager.is_module_on('safewalk') then
      end
      if not omnisprint_toggled and module_manager.option('sprint', 'Omnidirectional') then
      end
    elseif not input.is_key_down(42) and delay <= 0 then
        player.set_sneaking(false)
    end

    if pitch < 65 or not input.is_mouse_down(1) then
      if safewalk_toggled and module_manager.is_module_on('safewalk') then
      end
      if omnisprint_toggled and not module_manager.option('sprint', 'Omnidirectional') then
      end
    end
    delay = delay - 1
    click_delay = click_delay - 1
  end
}

module_manager.register('speedbridgev2', speedbridgev2)
module_manager.register_boolean('speedbridge', 'toggle-safewalk', true)
module_manager.register_number('speedbridge', 'min-unshift-delay', 0, 10, 2)
module_manager.register_number('speedbridge', 'max-unshift-delay', 0, 10, 4)
module_manager.register_number('speedbridge', 'min-click-delay', 0, 10, 1)
module_manager.register_number('speedbridge', 'max-click-delay', 0, 10, 3)
