local function setModuleState(module, state)
    player.message(module_manager.is_module_on(module) ~= state and "."..module or nil)
end
function enablemodules()
    if kawason then
        setModuleState('KillAura', true)
    end
    if bedaurawason then
        setModuleState('BedAura', true)
    end
    if antifbwason then
        setModuleState('AntiFireball', true)
    end
    if velowason then
        setModuleState('Velo', true)
    end
    kawason = false bedaurawason = false antifbwason = false velowason = false
end
function disablemodules()
    if module_manager.is_module_on("KillAura") then
        kawason = true
    end
    if module_manager.is_module_on("BedAura") then
        bedaurawason = true
    end
    if module_manager.is_module_on("AntiFireball") then
        antifbwason = true
    end
    if module_manager.is_module_on("Velo") then
        velowason = true
    end
    setModuleState('KillAura', false) setModuleState('BedAura', false) setModuleState('AntiFireball', false) setModuleState('Velo', false)
end
function onGround()
	if player.on_ground() then
		return true
	else
		return false
	end
end
local function voidcheck()
    local x,y,z = player.position()
    for i = y, 0, -1 do
        if world.block(x, i, z) ~= 'tile.air' then
            return false
        end
    end
    return true
end
function falldist()
    function maxdist()
        local maxdist = 11
        for i = 1, maxdist do
            local x,y,z = player.position()
            if world.block(x, y-i, z) ~= "tile.air" then
                return false
            end
        end
        if not voidcheck() then
            return true
        end
    end
    local falldist = 3
    for i = 1, falldist do
        local x,y,z = player.position()
        if world.block(x, y-i, z) ~= "tile.air" then
            return false
        end
    end
    if not maxdist() then
        return true
    end
end
local last_ground_change_y = 0
module_manager.register("vulcannofall", {
on_pre_update = function()
	local x,y,z = player.position()
if player.is_on_edge() and onGround() and input.is_key_down(17) and falldist() then
	player.set_motion(0, -19, 0) checkground = false voidspoof = true spoofground = true
end
if not onGround() or onGround() and not player.is_on_edge() then
	checkground = true
end
if checkground and onGround() then
	setModuleState('inventory', false) checkground = false checkvoiddist = false voidspoof = false spoofground = false
end
if spoofground and not module_manager.is_module_on("inventory") then
	spoofground = false
end
if spoof and voidcheck() and voidspoof then
	last_ground_change_y = y
	voidspoof = false
elseif spoof and voidcheck() and last_ground_change_y - y > 15 then
	checkvoiddist = true
end
end,

on_pre_motion = function(t)
if spoof and voidcheck() and checkvoiddist then
	spoofground = false setModuleState('inventory', false) checkvoiddist = false
end
if spoofground or spoof then
	t.on_ground = true
end
return t
end,

on_render_screen = function(t)
if spoofground then
	render.scale(2)
	render.string_shadow("ticklingvulcan", (t.width/2 - 14)/2, (t.height/2 + 6)/2, 255, 255, 255, 255)
	render.scale(1/2)
	end
end
})
-- made by Tanner