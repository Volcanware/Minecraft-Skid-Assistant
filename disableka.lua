local module = {
    on_update = function()
        if client:is_key_down(38) == true then
            if modules:get_setting("katoggle", "ka") == true and modules:is_enabled("killaura") == true then
                modules:set_state("killaura", false)
                client:print("disabled ka")
            end
            if modules:get_setting("katoggle", "invman") == true and modules:is_enabled("Inventorymanager") == true then
                modules:set_state("Inventorymanager", false)
                client:print("disabled inv manager")
            end
            if modules:get_setting("katoggle", "chest") == true and modules:is_enabled("Cheststealer") == true then
                modules:set_state("Cheststealer", false)
                client:print("disabled cheststealer")
            end
            if modules:get_setting("katoggle", "scaffold") == true and modules:is_enabled("scaffoldwalk") == true then
                modules:set_state("Scaffoldwalk", false)
                client:print("disabled scaffold")
            end
        end
    end
}
modules:register("katoggle", "katoggle", module)
modules:create_boolean_setting("katoggle", "ka", "disable killaura", true)
modules:create_boolean_setting("katoggle", "invman", "disable inv manager", true)
modules:create_boolean_setting("katoggle", "chest", "disable chest stealer", true)
modules:create_boolean_setting("katoggle", "scaffold", "disable scaffold", true) -- made with <3 by ABadName