local auratoggle = {
  on_enable = function()
    if not module_manager.is_module_on('killaura') then
      player.message('.killaura')
    end
    if not module_manager.is_module_on('velocity') then
      player.message('.velocity')
    end
  end,

  on_disable = function()
    if module_manager.is_module_on('killaura') then
      player.message('.killaura')
    end
    if module_manager.is_module_on('velocity') then
      player.message('.velocity')
    end
  end,
}

module_manager.register('auratoggle', auratoggle)
