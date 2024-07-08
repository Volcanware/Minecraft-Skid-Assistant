local module = {
	on_tick = function()
		if modules:is_enabled("Scaffoldwalk") and modules:get_setting("ToggleAlerts", "sc") and not Scaffold then
			client:print("<grey>(<green>!<grey>) <red>Scaffold <white>has been <green>Enabled")
			Scaffold = true
		end

		if not modules:is_enabled("Scaffoldwalk") and modules:get_setting("ToggleAlerts", "sc") and Scaffold then
			client:print("<grey>(<red>!<grey>) <red>Scaffold <white>has been <red>Disabled")
			Scaffold = false
		end

		if modules:is_enabled("Killaura") and modules:get_setting("ToggleAlerts", "ka") and not Killaura then
			client:print("<grey>(<green>!<grey>) <red>Killaura <white>has been <green>Enabled")
			Killaura = true
		end

		if not modules:is_enabled("Killaura") and modules:get_setting("ToggleAlerts", "ka") and Killaura then
			client:print("<grey>(<red>!<grey>) <red>Killaura <white>has been <red>Disabled")
			Killaura = false
		end

		if modules:is_enabled("Speed") and modules:get_setting("ToggleAlerts", "speed1") and not bhop then
			client:print("<grey>(<green>!<grey>) <red>Bhop <white>has been <green>Enabled")
			bhop = true
		end

		if not modules:is_enabled("Speed") and modules:get_setting("ToggleAlerts", "speed1") and bhop then
			client:print("<grey>(<red>!<grey>) <red>Bhop <white>has been <red>Disabled")
			bhop = false
		end

		if modules:is_enabled("Noslowdown") and modules:get_setting("ToggleAlerts", "ns") and not Noslow then
			client:print("<grey>(<green>!<grey>) <red>Noslow <white>has been <green>Enabled")
			Noslow = true
		end

		if not modules:is_enabled("Noslowdown") and modules:get_setting("ToggleAlerts", "ns") and Noslow then
			client:print("<grey>(<red>!<grey>) <red>Noslow <white>has been <red>Disabled")
			Noslow = false
		end

		if modules:is_enabled("ChestStealer") and modules:get_setting("ToggleAlerts", "cs") and not Stealer then
			client:print("<grey>(<green>!<grey>) <red>Chest Stealer <white>has been <green>Enabled")
			Stealer = true
		end

		if not modules:is_enabled("ChestStealer") and modules:get_setting("ToggleAlerts", "cs") and Stealer then
			client:print("<grey>(<red>!<grey>) <red>Chest Stealer <white>has been <red>Disabled")
			Stealer = false
		end

		if modules:is_enabled("InventoryManager") and modules:get_setting("ToggleAlerts", "im") and not Manager then
			client:print("<grey>(<green>!<grey>) <red>Inventory Manager <white>has been <green>Enabled")
			Manager = true
		end

		if not modules:is_enabled("InventoryManager") and modules:get_setting("ToggleAlerts", "im") and Manager then
			client:print("<grey>(<red>!<grey>) <red>Inventory Manager <white>has been <red>Disabled")
			Manager = false
		end

		if modules:is_enabled("Flight") and modules:get_setting("ToggleAlerts", "fl") and not Fly then
			client:print("<grey>(<green>!<grey>) <red>Fly <white>has been <green>Enabled")
			Fly = true
		end

		if not modules:is_enabled("Flight") and modules:get_setting("ToggleAlerts", "fl") and Fly then
			client:print("<grey>(<red>!<grey>) <red>Fly <white>has been <red>Disabled")
			Fly = false
		end

		if modules:is_enabled("Longjump") and modules:get_setting("ToggleAlerts", "lj1") and not LJ then
			client:print("<grey>(<green>!<grey>) <red>LongJump <white>has been <green>Enabled")
			LJ = true
		end

		if not modules:is_enabled("Longjump") and modules:get_setting("ToggleAlerts", "lj1") and LJ then
			client:print("<grey>(<red>!<grey>) <red>LongJump <white>has been <red>Disabled")
			LJ = false
		end

		if modules:is_enabled("Nofall") and modules:get_setting("ToggleAlerts", "nf") and not Nofall then
			client:print("<grey>(<green>!<grey>) <red>Nofall <white>has been <green>Enabled")
			Nofall = true
		end

		if not modules:is_enabled("Nofall") and modules:get_setting("ToggleAlerts", "nf") and Nofall then
			client:print("<grey>(<red>!<grey>) <red>Nofall <white>has been <red>Disabled")
			Nofall = false
		end

		if modules:is_enabled("Timer") and modules:get_setting("ToggleAlerts", "ti") and not Timer then
			client:print("<grey>(<green>!<grey>) <red>Timer <white>has been <green>Enabled")
			Timer = true
		end

		if not modules:is_enabled("Timer") and modules:get_setting("ToggleAlerts", "ti") and Timer then
			client:print("<grey>(<red>!<grey>) <red>Timer <white>has been <red>Disabled")
			Timer = false
		end
	end
}

modules:register("ToggleAlerts", "Toggle Alerts", module)
modules:create_boolean_setting("ToggleAlerts", "im", "Inventory Manager", false)
modules:create_boolean_setting("ToggleAlerts", "cs", "Chest Stealer", false)
modules:create_boolean_setting("ToggleAlerts", "ka", "Killaura", true)
modules:create_boolean_setting("ToggleAlerts", "speed1", "Speed", true)
modules:create_boolean_setting("ToggleAlerts", "sc", "Scaffold", true)
modules:create_boolean_setting("ToggleAlerts", "ti", "Timer", false)
modules:create_boolean_setting("ToggleAlerts", "lj1", "LongJump", false)
modules:create_boolean_setting("ToggleAlerts", "ns", "No Slowdown", false)
modules:create_boolean_setting("ToggleAlerts", "fl", "Fly", true)
modules:create_boolean_setting("ToggleAlerts", "nf", "Nofall", true)