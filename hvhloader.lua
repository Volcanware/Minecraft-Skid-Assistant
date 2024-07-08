local hvhloader = {
    on_enable = function()
      if module_manager.is_module_on('hvhmode') then
        player.message('.config load 5kr411')
      else
        player.message('.hvhloader')
        player.message('.config save 5kr411')
        player.message('.hvhmode')
      end
    end
}

module_manager.register('hvhloader', hvhloader)
