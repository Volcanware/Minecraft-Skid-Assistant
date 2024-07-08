module_manager.register("nofallchecker", {
    on_pre_update = function()
        if module_manager.is_module_on("scaffold2") and module_manager.is_module_on('vulcankiller') then
            player.message(".vulcankiller")
        elseif not module_manager.is_module_on("scaffold2") and not module_manager.is_module_on('vulcankiller') then
            player.message('.vulcankiller')
        end
    end
})
