http.load("https://raw.githubusercontent.com/CluePortal/ZarScriptHelper/main/MoreFunctions.lua")

local calc = 1
local nimi = 'BedAura'
local notsaid = true
local saidpick = true
local mining = false
local C02 = false
local saidaxe = true
local toolsswapped
local tag = color.gray ..'['.. color.light_purple..'BWT' ..color.gray..'] ' ..color.yellow..'Alert: ' ..format.reset
local debugtag = color.gray ..'['.. color.light_purple..'D' ..color.gray..'] ' ..color.dark_aqua..'BedAura: ' ..format.reset

local red = 0
local green = 0
local blue = 0
local stage = 1
local rainbow = 5

local bed = nil
local setGround = 0

local breaking_block_ticks = 0
local breaking_bed_ticks = 0
local breaking_render_ticks = 0

local reenable = 0

function is_holding_sword()
    return player.held_item() ~= nil and string.match(player.held_item(), 'Sword')
end

local function setModuleState(module, state)
    player.message(module_manager.is_module_on(module) ~= state and "."..module or nil)
end

function inRange(x, y, z, range)
    local playerX, playerY, playerZ = player.position()
    local distance = math.sqrt((x - playerX)^2 + (y - playerY)^2 + (z - playerZ)^2)
    return distance <= range
end
local set_module_state = function(mod, state)
    if module_manager.is_module_on(mod) ~= state then
        player.message("." .. mod)
    end
end
function findNearestBed(radius)
	local startX, startY, startZ = player.position()

	local bed_blocks = {}

	for x = startX - radius, radius + startX do
		for y = startY - radius, radius + startY do
			for z = startZ - radius, radius + startZ do
				if checkForBed(x, y, z) then
					local found_bed_x = math.floor(x)
					local found_bed_y = math.floor(y)
					local found_bed_z = math.floor(z)
					table.insert(bed_blocks, { x = found_bed_x, y = found_bed_y, z = found_bed_z })
				end
			end
		end
	end

	-- Not enough range to get second bed block, just find it manually
	if #bed_blocks == 1 then
		local bed_part = findSecondBedPart(bed_blocks[1])

		if bed_part ~= nil then
			table.insert(bed_blocks, bed_part)
		end
	end

	if #bed_blocks > 0 then
		local defense = findBedCover(bed_blocks)
			
		return { bed_parts = bed_blocks, defense = defense }
	else
		return nil
	end
end

function findSecondBedPart(coords)
	local offsets = {
		{ x = -1, z = 0 },
		{ x = 1, z = 0 },
		{ x = 0, z = 1 },
		{ x = 0, z = -1 }
	}

	for i = 1, #offsets do
		local offset = offsets[i]

		local new_x = coords.x + offset.x
		local new_z = coords.z + offset.z

		if checkForBed(new_x, coords.y, new_z) then
			return { x = new_x, y = coords.y, z = new_z }
		end
	end

	return nil
end

function checkForBed(x, y, z)
	local block = world.block(x, y, z)

	return block ~= nil and block == "tile.bed"
end

function findBedCover(bed_blocks)
	local bed_cover = {}

	for i = 1, #bed_blocks do
		local location = bed_blocks[i]

		local x, y, z = location.x, location.y, location.z

		table.insert(bed_cover, { x = x, y = y + 1, z = z, facing = 2 })
		table.insert(bed_cover, { x = x + 1, y = y, z = z, facing = 6 })
		table.insert(bed_cover, { x = x, y = y, z = z + 1, facing = 4 })
		table.insert(bed_cover, { x = x - 1, y = y, z = z, facing = 5 })
		table.insert(bed_cover, { x = x, y = y, z = z - 1, facing = 3 })
	end

	local blocks = { "air", "water", "ladder", "stainedGlass", "cloth", "wood", "log", "clayHardenedStained", "whiteStone", "obsidian" }

	for i = 1, #blocks do
		local block_name = blocks[i]

		-- Search for current def block around bed
		local defense = searchForDefense(bed_cover, block_name)

		-- Blocks are sorted by priority/hardness of breaking, we can break this block
		if defense ~= nil then
			if block_name == "air" or block_name == "ladder" or block_name == "water" then
				return { location = nil, facing = defense.facing }
			else
				return { location = { x = defense.x, y = defense.y, z = defense.z }, facing = defense.facing }
			end
		end
	end

	local first_cover = bed_cover[1]

	return { location = { x = first_cover.x, y = first_cover.y, z = first_cover.z }, facing = first_cover.facing }
end

function searchForDefense(bed_cover, bed_defense_block)
	for i = 1, #bed_cover do
		local location = bed_cover[i]
		local block = world.block(location.x, location.y, location.z)

		if block ~= nil and string.match(block, bed_defense_block) then
			return location
		end
	end

	return nil
end

function isBedFound()
	return bed ~= nil
end

function onGround()
	if (module_manager.option(nimi, "Spoof ground") and setGround <= 3 and player.fall_distance() < 1.5) or player.on_ground() then
		return true
	else
		return false
	end
end

function findHotbarSlot(item)
	for i = 1, 9 do
        local name = player.inventory.item_information(35 + i)

        if name ~= nil and string.match(name, item) then
            return i
        end
    end

    return -1
end

function resetSlot()
	if (not player.using_item() and player.kill_aura_target() == nil) and toolsswapped then
    	player.send_packet(0x09, player.held_item_slot())
		toolsswapped = false
	end
end

function resetNuker()
	setGround = 0
	breaking_block_ticks = 0 
	breaking_bed_ticks = 0
	breaking_render_ticks = 0
	reenable = 0
	notsaid = true
	saidaxe = true
	saidpick = true
	bed = nil
	sent = true
	
	toolsswapped = false
	resetSlot()
end

function renderBlockOutline(event, x, y, z)
	local minX, minY, minZ, maxX, maxY, maxZ = renderHelper.getBlockBoundingBox(x, y, z)
	renderHelper.renderOutline(minX, minY, minZ, maxX, maxY, maxZ, event, red, green, blue, 255, 5)
end

function getDefenseBlock()
	local defense = bed.defense.location

	if defense == nil then
		return nil
	end

	local block = world.block(defense.x, defense.y, defense.z)

	if block == "tile.air" or block == "tile.ladder" or block == "tile.water" then
		return nil
	end
	return block
end
local gspoof = false
local wason = false
local block_breaker = {
	on_enable = function()
		gspoof = false
		resetNuker()
	end,

	on_disable = function()
		setGround = 0
		reenable = 0
		if toolsswapped then
			resetSlot()
			toolsswapped = false
		end
		if not module_manager.is_module_on('autotool') and autotool then
			player.message('.autotool')
			autotool = false
		end
		notsaid = true
		saidaxe = true
		saidpick = true
	end,

	on_pre_motion = function(event)
		if module_manager.option(nimi, 'Re enable KA') then
			set_module_state("killaura",false)
		end
		if isBedFound() then
			local pos
			if module_manager.option(nimi, 'Spoof ground') then
				if gspoof then
					event.on_ground = true
					gspoof = false
				end
			end
			if getDefenseBlock() ~= nil then
				pos = bed.defense.location
			else
				pos = bed.bed_parts[1]
			end

			if not sent then
				reenable = reenable + 1
			end
			if reenable == 1 then
				
				reenable = 0
			end
			local yaw, pitch = player.angles_for_cords(pos.x, pos.y, pos.z)
			if sent then
				event.yaw = yaw
				event.pitch = pitch
				sent = false
			end
		end
		return event
	end,

	on_pre_update = function()
		if isBedFound() then
			
		end

		local found_bed = findNearestBed(module_manager.option(nimi, 'Range'))

		if found_bed == nil then
			resetNuker()
			return
		end

		if onGround() and not player.is_in_water() and not player.is_potion_active(4) then
			calc = 1
		elseif onGround() and not player.is_in_water() and player.is_potion_active(4) then
			calc = 3.40909090909
		elseif (not onGround() or player.is_in_water()) and not player.is_potion_active(4) then
			calc = 5.09090909091
		elseif player.is_potion_active(4) and (not onGround() or player.is_in_water()) then
			calc = 17.0454545455
		end

		if bed == nil then
			bed = found_bed
		end

		local defense = bed.defense
		local defense_location = defense.location
		local block = getDefenseBlock()

		local bed_position = bed.bed_parts[1]

		-- Bed is already broken, stop nuker
		if world.block(bed_position.x, bed_position.y, bed_position.z) ~= "tile.bed" then
			resetNuker()
			return
		end

		if module_manager.is_module_on('autotool') then
			player.message('.autotool')
			autotool = true
		end

		if module_manager.is_module_on('SafeNofall') and module_manager.is_module_on("Blink") and module_manager.is_module_on("Nofall") then
			return
		end

		-- If the block above bed is not air then we break it
		if block ~= nil then
			local toolName

			if string.match(block, "wood") or string.match(block, "log") then
				toolName = 'hatchet'
			elseif block == 'tile.whiteStone' or string.match(block, "tile.clayHardenedStained") then
				toolName = 'pickaxe'
			elseif block == 'tile.stainedGlass' then
				toolName = "a8hs8awyd7wsd6aw5ds6dawsdaws6d5aws"
            elseif block == 'tile.obsidian' then
                toolName = 'pickaxe'
				if notsaid then
                	client.print(tag.. color.gray.. "This Team Has " ..color.black.. "Obsidian")
					notsaid = false
				end
			elseif string.match(block, 'tile.cloth') then
				toolName = 'shears'
				resetvalue = 5
			else 
				toolName = "a8hs8awyd7wsd6aw5ds6dawsdaws6d5aws"
			end
			local slot = findHotbarSlot(toolName)
			if slot ~= -1 then
				smartcheck = player.inventory.slot(35 + slot)
				if block == 'tile.whiteStone' then
					if smartcheck == "Wooden Pickaxe" then
						resetvalue = 22
					elseif smartcheck == "Iron Pickaxe" then
						resetvalue = 8
					elseif smartcheck == "Golden Pickaxe" then
						resetvalue = 4
					elseif smartcheck == "Diamond Pickaxe" then
						resetvalue = 5
					end
				elseif block == 'tile.clayHardenedStained' then
					if smartcheck == "Wooden Pickaxe" then
						resetvalue = 9
					elseif smartcheck == "Iron Pickaxe" then
						resetvalue = 3
					elseif smartcheck == "Golden Pickaxe" or smartcheck == "Diamond Pickaxe" then
						resetvalue = 2
					end
				elseif block == 'tile.obsidian' then
					if smartcheck == "Wooden Pickaxe" then
						resetvalue = 1250
					elseif smartcheck == "Iron Pickaxe" then
						resetvalue = 455
					elseif smartcheck == "Golden Pickaxe" then
						resetvalue = 227
					elseif smartcheck == "Diamond Pickaxe" then
						resetvalue = 83
					end
				elseif string.match(block, "wood") or string.match(block, "log") then
					if smartcheck == "Wooden Axe" then
						resetvalue = 15
					elseif smartcheck == "Stone Axe" then
						resetvalue = 10
					elseif smartcheck == "Iron Axe" then
						resetvalue = 5
					elseif smartcheck == "Diamond Axe" then
						resetvalue = 3
					end
				end
			else
				if module_manager.option(nimi, 'Missing tool') then
					if toolName == "pickaxe" and saidpick then
						client.print(debugtag .. "Missing pickaxe")
						saidpick = false
					elseif toolName == "hatchet" and saidaxe then
						client.print(debugtag .. "Missing axe")
						saidaxe = false
					end
				end
				if string.match(block, 'tile.cloth') then
					resetvalue = 24
				elseif string.match(block, "wood") or string.match(block, "log") then
					resetvalue = 60
				elseif block == 'tile.stainedGlass' then
					resetvalue = 9
				else
					resetvalue = 20
				end
			end
			if inRange(defense_location.x, defense_location.y, defense_location.z, module_manager.option(nimi, 'Range')) then
				if breaking_block_ticks >= math.floor(resetvalue * calc) then
					if slot ~= -1 and player.held_item_slot() ~= slot then
						player.send_packet(0x09, slot)
						toolsswapped = true
					end
					reenable = 0
					gspoof = true
					player.send_packet_no_event(0x07, 3, defense_location.x, defense_location.y, defense_location.z, 2)
					sent = true					
					breaking_block_ticks = -5
				elseif breaking_block_ticks == 0 then
					if toolsswapped then
						resetSlot()
						toolsswapped = false
					end
					breaking_render_ticks = 0
					reenable = 0
					gspoof = true
					player.send_packet_no_event(0x07, 1, defense_location.x, defense_location.y, defense_location.z, 2)
					sent = true
				end
				breaking_block_ticks = breaking_block_ticks + 1
				breaking_render_ticks = breaking_render_ticks + 1
			end
		elseif inRange(bed_position.x, bed_position.y, bed_position.z, module_manager.option(nimi, 'Range')) then
			if toolsswapped then
				resetSlot()
				toolsswapped = false
			end
			-- If the block above bed is air then we can just nuke bed
			if breaking_bed_ticks == 0 then
				reenable = 0
				gspoof = true
				player.swing_item()
				player.send_packet_no_event(0x07, 1, bed_position.x, bed_position.y, bed_position.z, 2)
				sent = true
			elseif breaking_bed_ticks >= math.floor(6 * calc) then
				reenable = 0
				gspoof = true
				player.swing_item()
				player.send_packet_no_event(0x07, 3, bed_position.x, bed_position.y, bed_position.z, 2)
				sent = true
				breaking_bed_ticks = -5
			end
			breaking_bed_ticks = breaking_bed_ticks + 1
		end
	end,

	on_render_screen = function(e)
		if stage == 1 then
			if green < 255 then
				green = green + rainbow
			else
				stage = 2
			end
		elseif stage == 2 then
			if red > 0 then
				red = red - rainbow
			else
				stage = 3
			end
		elseif stage == 3 then
			if blue < 255 then
				blue = blue + rainbow
			else
				stage = 4
			end
		elseif stage == 4 then
			if green > 0 then
				green = green - rainbow
			else
				stage = 5
			end
		elseif stage == 5 then
			if red < 255 then
				red = red + rainbow
			else
				stage = 6
			end
		elseif stage == 6 then
			if blue > 0 then
				blue = blue - rainbow
			else
				stage = 1
			end
		end
		if not isBedFound() then
			return
		end

		local y, color_type
		if getDefenseBlock() ~= nil then
			local defense = bed.defense.location
			if inRange(defense.x, defense.y, defense.z, module_manager.option(nimi, 'Range')) and resetvalue ~= nil then
				renderBlockOutline(e, defense.x, defense.y, defense.z)
				if module_manager.option(nimi, "Show break progress") then
					local value = (breaking_render_ticks/(resetvalue * calc)) * 100
					local str = math.max(0, math.min(math.floor(value), 100)) .. "%"
					render.string_shadow(str, (e.width/2 - render.get_string_width(math.max(0, math.min(math.floor(value), 100)))/2), e.height/2 + 5, 255, 255, 255, 255)
				end
			end
		elseif module_manager.option(nimi, "Render bed outline") then
			local parts = bed.bed_parts[1]
			renderBlockOutline(e, parts.x, parts.y, parts.z)
		end
	end,

	on_send_packet = function(t)
		if t.packet_id == 0x09 then
			if t.slot == player.held_item_slot() - 1 then
				if (player.using_item() or player.kill_aura_target() ~= nil) and is_holding_sword() then
					toolsswapped = false
				end
			end
		end
		return t
	end
}

module_manager.register(nimi, block_breaker)
module_manager.register_number(nimi, 'Range', 3.01, 8.01, 5.5)
module_manager.register_boolean(nimi, 'Spoof ground', true)
module_manager.register_boolean(nimi, 'Show break progress', true)
module_manager.register_boolean(nimi, 'Render bed outline', false)
module_manager.register_boolean(nimi, 'Missing tool', true)

-- by kambet, editted by unloged
-- 8/3/2023