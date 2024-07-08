local antisniperkey = ""
local hypixelkey = "c673f290-5d38-4679-8753-23322e9e5259"
-- put your antisniper key and hypixel api key above

local retrievebind = 5
local quickbind = 20
-- find the right bind for you at https://minecraft.fandom.com/el/wiki/Key_codes

local pickslot = 4
local axeslot = 3
local shearslot = 56
local woolslot = 18
local swordslot = 2
-- find the right bind for you at https://minecraft.fandom.com/el/wiki/Key_codes

local color = {
    dark_red = '\194\1674',
    red = '\194\167c',
    gold = '\194\1676',
    yellow = '\194\167e',
    dark_green = '\194\1672',
    green = '\194\167a',
    aqua = '\194\167b',
    dark_aqua = '\194\1673',
    dark_blue = '\194\1671',
    blue = '\194\1679',
    light_purple = '\194\167d',
    dark_purple = '\194\1675',
    white = '\194\167f',
    gray = '\194\1677',
    dark_gray = '\194\1678',
    black = '\194\1670'
}

local format = {
    obfuscated = '\194\167k',
    bold = '\194\167l',
    strikethrough = '\194\167m',
    underline = '\194\167n',
    italic = '\194\167o',
    reset = '\194\167r'
}

local sendmsg = true
local firstdelay = 0
local setname

local foundMapName = ""
local foundMapHeight = 0

local cpscap = 0

local commandexecuted = false
local botsencountered

local char_to_hex = function(c)
    return string.format("%%%02X", string.byte(c))
  end
  
  local function urlencode(url)
    if url == nil then
      return
    end
    url = url:gsub("\n", "\r\n")
    url = string.gsub(url, "([^%w _ %- . ~])", char_to_hex)
    url = url:gsub(" ", "+")
    return url
  end

function round(num, dec)
    local shift = 10^(dec or 2)
    num = math.floor(num * shift + 0.5) / shift
    if num == math.floor(num) then num = tostring(num.."."..("0"):rep(dec)) end
    return num
end

local coloredPOTIONS = {
    [1]  = {name = color.yellow .. "Speed 2" .. format.reset,             id = "speed", duration = 45},
    [2]  = {name = color.blue .. "Slowness" .. format.reset,          id = "slowness", duration = 5},
    [3]  = {name = color.gold .. "Haste" .. format.reset,             id = "haste", duration = 0},
    [4]  = {name = color.dark_gray .. "Mining Fatigue 1" .. format.reset,    id = "mining_fatigue", duration = 10},
    [5]  = {name = color.red .. "Strength" .. format.reset,          id = "strength", duration = 0},
    [6]  = {name = color.light_purple .. "Instant Health" .. format.reset,    id = "instant_health", duration = 0},
    [7]  = {name = color.dark_purple .. "Instant Damage" .. format.reset,    id = "instant_damage", duration = 0},
    [8]  = {name = color.aqua .. "Jump Boost 5" .. format.reset,        id = "jump_boost", duration = 45},
    [9]  = {name = color.dark_green .. "Nausea" .. format.reset,            id = "nausea", duration = 0},
    [10] = {name = color.green .. "Regeneration" .. format.reset,      id = "regeneration", duration = 0},
    [11] = {name = color.black .. "Resistance" .. format.reset,        id = "resistance", duration = 0},
    [12] = {name = color.gold .. "Fire Resistance" .. format.reset,   id = "fire_resistance", duration = 0},
    [13] = {name = color.aqua .. "Water Breathing" .. format.reset,   id = "water_breathing", duration = 0},
    [14] = {name = color.gray .. "Invisibility 1" .. format.reset,      id = "invisibility", duration = 30},
    [15] = {name = color.dark_blue .. "Blindness" .. format.reset,         id = "blindness", duration = 8},
    [16] = {name = color.dark_aqua .. "Night Vision" .. format.reset,      id = "night_vision", duration = 0},
    [17] = {name = color.red .. "Hunger" .. format.reset,            id = "hunger", duration = 0},
    [18] = {name = color.dark_red .. "Weakness" .. format.reset,          id = "weakness", duration = 0},
    [19] = {name = color.green .. "Poison" .. format.reset,            id = "poison", duration = 0},
    [20] = {name = color.dark_purple .. "Wither" .. format.reset,            id = "wither", duration = 0},
    [21] = {name = color.red .. "Health Boost" .. format.reset,      id = "health_boost", duration = 0},
    [22] = {name = color.yellow .. "Absorption" .. format.reset,        id = "absorption", duration = 0},
    [23] = {name = color.yellow .. "Saturation" .. format.reset,        id = "saturation", duration = 0}
}

local POTIONS = {
    [1]  = {name = "Speed 2",             id = "speed", duration = 45},
    [2]  = {name = "Slowness",          id = "slowness", duration = 5},
    [3]  = {name = "Haste",             id = "haste", duration = 0},
    [4]  = {name = "Mining Fatigue 1",    id = "mining_fatigue", duration = 10},
    [5]  = {name = "Strength",          id = "strength", duration = 0},
    [6]  = {name = "Instant Health",    id = "instant_health", duration = 0},
    [7]  = {name = "Instant Damage",    id = "instant_damage", duration = 0},
    [8]  = {name = "Jump Boost 5",        id = "jump_boost", duration = 45},
    [9]  = {name = "Nausea",            id = "nausea", duration = 0},
    [10] = {name = "Regeneration",      id = "regeneration", duration = 0},
    [11] = {name = "Resistance",        id = "resistance", duration = 0},
    [12] = {name = "Fire Resistance",   id = "fire_resistance", duration = 0},
    [13] = {name = "Water Breathing",   id = "water_breathing", duration = 0},
    [14] = {name = "Invisibility 1",      id = "invisibility", duration = 30},
    [15] = {name = "Blindness",         id = "blindness", duration = 8},
    [16] = {name = "Night Vision",      id = "night_vision", duration = 0},
    [17] = {name = "Hunger",            id = "hunger", duration = 0},
    [18] = {name = "Weakness",          id = "weakness", duration = 0},
    [19] = {name = "Poison",            id = "poison", duration = 0},
    [20] = {name = "Wither",            id = "wither", duration = 0},
    [21] = {name = "Health Boost",      id = "health_boost", duration = 0},
    [22] = {name = "Absorption",        id = "absorption", duration = 0},
    [23] = {name = "Saturation",        id = "saturation", duration = 0}
}

local ticks = 0
local seconds = 0

local potionStartTimes = {}

function update()
    ticks = ticks + 1
    if ticks % 20 == 0 then
        seconds = seconds + 1
    end
    for i, v in ipairs(POTIONS) do
        if player.is_potion_active(i) then
            if not potionStartTimes[i] then
                potionStartTimes[i] = seconds
            end
        else
            potionStartTimes[i] = nil
        end
    end
end

local function getPotions()
    local out = {}
    if not module_manager.option('Bedwars-Tools', 'Potion-Colors') then
        for i, v in ipairs(POTIONS) do
            if player.is_potion_active(i) then
                local timeelapsed = ""
                if v.id == "haste" or v.id == "night_vision" then
                    timeelapsed = " - *:**"
                elseif v.id == "mining_fatigue" or v.id == "jump_boost" or v.id == "speed" or v.id == "invisibility" or v.id == "slowness" or v.id == "blindness" then
                    if potionStartTimes[i] then
                        local elapsedSeconds = math.abs(v.duration - (seconds - potionStartTimes[i]))
                        local minutes = math.abs(math.floor(elapsedSeconds / 60))
                        local seconds = math.abs(elapsedSeconds % 60)
                        timeelapsed = string.format(" - %d:%02d", minutes, seconds)
                    else
                        potionStartTimes[i] = seconds
                    end
                end
                table.insert(out, v.name .. timeelapsed)
            else
                potionStartTimes[i] = nil
            end
        end
        return out
    end
    if module_manager.option('Bedwars-Tools', 'Potion-Colors') then
        for i, v in ipairs(coloredPOTIONS) do
            if player.is_potion_active(i) then
                local timeelapsed = ""
                if v.id == "haste" or v.id == "night_vision" then
                    timeelapsed = " - *:**"
                elseif v.id == "mining_fatigue" or v.id == "jump_boost" or v.id == "speed" or v.id == "invisibility" or v.id == "slowness" or v.id == "blindness" then
                    if potionStartTimes[i] then
                        local elapsedSeconds = math.abs(v.duration - (seconds - potionStartTimes[i]))
                        local minutes = math.abs(math.floor(elapsedSeconds / 60))
                        local seconds = math.abs(elapsedSeconds % 60)
                        timeelapsed = string.format(" - %d:%02d", minutes, seconds)
                    else
                        potionStartTimes[i] = seconds
                    end
                end
                table.insert(out, v.name .. timeelapsed)
            else
                potionStartTimes[i] = nil
            end
        end
        return out
    end
end

local commands; commands = {
    help = {
        args = {
            count = 0,
            desc = 'Helps'
        },
        desc = 'Help',
        command = function()
            client.print(color.gray .. format.strikethrough .. "-------------------------")
            client.print(color.yellow .. "Bedwars Tools Information")
            client.print("")
            client.print(color.red .. "Item Alert" .. color.yellow .. ": Prints" .. color.yellow .. " the" .. color.yellow .. " name" .. color.yellow .. " of" .. color.yellow .. " the" .. color.yellow .. " entity " .. color.yellow .. "holding" .. color.yellow .. " the" .. color.yellow .. " enabled " .. color.yellow .. "item(s).")
            client.print("")
            client.print(color.red .. "Armor Detector" .. color.yellow .. ": Prints the " .. color.yellow ..  "name " .. color.yellow ..  "the " .. color.yellow .. "entity " .. color.yellow .. "when it " .. color.yellow .. "buys the " .. color.yellow .. "certain armor.")
            client.print("")
            client.print(color.red .. "Resource Alert" .. color.yellow .. ": Shows" .. color.yellow .. " whenever" .. color.yellow .. " the " .. color.yellow .. "enabled" .. color.yellow .. " item(s)" .. color.yellow .. " is" .. color.yellow .. " changed" .. color.yellow .. " in" .. color.yellow .. " your " .. color.yellow .. "inventory.")
            client.print("")
            client.print(color.red .. "Quick Chest" .. color.yellow .. ": Automatically" .. color.yellow .. " puts" .. color.yellow .. " item(s) " .. color.yellow .. "in " ..color.yellow .. "and out of " .. color.yellow .. "chest.")
            client.print("")
            client.print(color.red .. "Auto Upgrade" .. color.yellow .. ": Automatically" .. color.yellow .. " buys" .. color.yellow .. " diamond " .. color.yellow .. "upgrades" .. color.yellow .. " for " .. color.yellow .. "you.")            
            client.print("")
            client.print(color.red .. "/mapinfo" .. color.yellow .. ": Returns map name " .. color.yellow ..  "and map build "  .. color.yellow .. "limit.")
            client.print("")
            client.print(color.red .. "Map Height" .. color.yellow .. ": Renders the map " .. color.yellow ..  "height.")
            client.print("")
            client.print(color.red .. "/queue, /q" .. color.yellow .. ": Automatically queue bedwars" .. color.yellow ..  ". "  .. color.yellow .. "Allowed arguments: 1, 2, 3, 4, 24")
            client.print("")
            client.print(color.red .. "Quick Slot" .. color.yellow .. ": Switches to the " .. color.yellow ..  "slot of the " .. color.yellow ..  "item, " .. color.yellow .. "not " .. color.yellow .. "the slot. " .. color.yellow .. "Set binds " .. color.yellow .. "in the file.")
            client.print("")
            client.print(color.red .. "Potion Hud" .. color.yellow .. ": Shows effects " .. color.yellow .. "you " .. color.yellow .. "have on the" .. " " .. color.yellow .. "left " .. color.yellow .. "side of " .. color.yellow .. "your screen")
            client.print("")
            client.print(color.gray .. format.strikethrough .. "-------------------------")
        end
    }
}

local commandPrefix = '/bwt '
local commandPrefixerror = '/bwt'

local tag = color.gray ..'['.. color.light_purple..'BWT' ..color.gray..'] ' ..color.yellow..'Alert: ' ..format.reset
local cmtag = color.gray ..'['.. color.light_purple..'BWT' ..color.gray..'] ' .. format.reset
local debugtag = color.gray ..'['.. color.light_purple..'D' ..color.gray..'] ' ..color.dark_aqua.. 'AutoUpgrade: ' ..format.reset

local onemaps = false
local fourmaps = false

local openticks = 0
local openedchest = false

local last_held_item = {}

local processedEntitiesChain = {}
local processedEntitiesIron = {}
local processedEntitiesDiamond = {}

local prev_item_names = {}
local prev_item_counts = {}
for i = 9, 44 do
    prev_item_names[i] = "none"
    prev_item_counts[i] = 0
end

local prev_item_counts_monitored = {}
prev_item_counts_monitored["Emerald"] = 0
prev_item_counts_monitored["Diamond"] = 0
prev_item_counts_monitored["Iron"] = 0
prev_item_counts_monitored["Gold"] = 0

function check_inventory()
    local current_item_counts_monitored = {}
    current_item_counts_monitored["Gold"] = 0
    current_item_counts_monitored["Emerald"] = 0
    current_item_counts_monitored["Diamond"] = 0
    current_item_counts_monitored["Iron"] = 0
    
    for i = 9, 44 do
        local item_name = player.inventory.slot(i)
        local item_count = player.inventory.get_item_stack(i)
        
        if item_name ~= prev_item_names[i] or item_count ~= prev_item_counts[i] then
            prev_item_names[i] = item_name
            prev_item_counts[i] = item_count
        end
        
        if item_name == "Emerald" then
            current_item_counts_monitored["Emerald"] = current_item_counts_monitored["Emerald"] + item_count
        elseif item_name == "Diamond" then
            current_item_counts_monitored["Diamond"] = current_item_counts_monitored["Diamond"] + item_count
        elseif item_name == "Iron Ingot" then
            current_item_counts_monitored["Iron"] = current_item_counts_monitored["Iron"] + item_count
        elseif item_name == "Gold Ingot" then
            current_item_counts_monitored["Gold"] = current_item_counts_monitored["Gold"] + item_count
        end
    end
    
    for item_name, current_count in pairs(current_item_counts_monitored) do
        local prev_count = prev_item_counts_monitored[item_name]
        if prev_count ~= nil and current_count ~= prev_count then
            local diff = current_count - prev_count
            if diff ~= 0 and module_manager ~= nil and module_manager.option("Bedwars-Tools", item_name) then
                local prefix = ""
                local suffix = ""
                if item_name == "Emerald" then
                    colorcheck = color.green
                elseif item_name == "Diamond" then
                    colorcheck = color.aqua
                elseif item_name == "Iron" then
                    colorcheck = color.white
                elseif item_name == "Gold" then
                    colorcheck = color.gold
                end
                if diff < 0 then
                    prefix = color.red .. "[-]"
                else
                    prefix = color.green .. "[+]"
                end
                if math.abs(diff) > 1 then
                    troll = "s"
                else
                    troll = ""
                end
                suffix = " " .. math.abs(diff) .. " " .. colorcheck .. item_name .. troll
                client.print(prefix .. format.reset .. suffix )
            end
        end
        prev_item_counts_monitored[item_name] = current_count
    end
end

function quickc()
    if client.chest_name() == "Chest" or client.chest_name() == "Ender Chest" then
      for slot_id = 27, 62 do
        local item_name = player.inventory.item_information(slot_id)
        if not module_manager.option('Bedwars-Tools', 'Resources-Only') then
            if item_name ~= "none" and item_name ~= "item.swordWood" and item_name ~= "item.pickaxeIron" and item_name ~= "item.pickaxeWood" and item_name ~= "item.pickaxeGold" and item_name ~= "item.pickaxeDiamond" and item_name ~= "item.hatchetIron" and item_name ~= "item.hatchetGold" and item_name ~= "item.hatchetWood" and item_name ~= "item.hatchetStone" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name ~= "item.compass" then 
                player.inventory.click(slot_id, 1, 1)
            end
        end
        if module_manager.option('Bedwars-Tools', 'Resources-Only') then
            if item_name ~= "none" and item_name ~= "item.swordWood" and item_name ~= "item.pickaxeIron" and item_name ~= "item.pickaxeWood" and item_name ~= "item.pickaxeGold" and item_name ~= "item.pickaxeDiamond" and item_name ~= "item.hatchetIron" and item_name ~= "item.hatchetGold" and item_name ~= "item.hatchetWood" and item_name ~= "item.hatchetStone" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name ~= "item.compass" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name == "item.ingotIron" or item_name == "item.ingotGold" or item_name == "item.emerald" or item_name == "item.diamond"then 
                player.inventory.click(slot_id, 1, 1)
            end
        end
    end
end
end

function retrievec()
    if client.chest_name() == "Chest" or client.chest_name() == "Ender Chest" then
        for slot_id = 0, 26 do
          local item_name = player.inventory.item_information(slot_id)
          if not module_manager.option('Bedwars-Tools', 'Retrieve-Resources-Only') then
              if item_name ~= "none" and item_name ~= "item.swordWood" and item_name ~= "item.pickaxeIron" and item_name ~= "item.pickaxeWood" and item_name ~= "item.pickaxeGold" and item_name ~= "item.pickaxeDiamond" and item_name ~= "item.hatchetIron" and item_name ~= "item.hatchetGold" and item_name ~= "item.hatchetWood" and item_name ~= "item.hatchetStone" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name ~= "item.compass" then 
                  player.inventory.click(slot_id, 1, 1)
              end
          end
          if module_manager.option('Bedwars-Tools', 'Retrieve-Resources-Only') then
              if item_name ~= "none" and item_name ~= "item.swordWood" and item_name ~= "item.pickaxeIron" and item_name ~= "item.pickaxeWood" and item_name ~= "item.pickaxeGold" and item_name ~= "item.pickaxeDiamond" and item_name ~= "item.hatchetIron" and item_name ~= "item.hatchetGold" and item_name ~= "item.hatchetWood" and item_name ~= "item.hatchetStone" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name ~= "item.compass" and item_name ~= "item.hatchetDiamond" and item_name ~= "item.shears" and item_name == "item.ingotIron" or item_name == "item.ingotGold" or item_name == "item.emerald" or item_name == "item.diamond"then 
                  player.inventory.click(slot_id, 1, 1)
              end
          end
      end
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

function set_hotbar_slot(slot)
	player.set_held_item_slot(slot - 2)
end

function totald()
    if client.chest_name() == "Upgrades & Traps" then   
        local total_diamonds = 0
        for slot_id = 54, 89 do
            local item_name = player.inventory.item_information(slot_id)
            if item_name == 'item.diamond' then
                local item_count = player.inventory.get_item_stack(slot_id)
                total_diamonds = total_diamonds + item_count
            end
        end
        return total_diamonds
    end
end

local function split(s, delimiter)
    local t = {};
    for match in (s..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(t, match)
    end
    return t;
end

local function getnthword(string, wordnumber)
    local words = split(string, ' ')
    return words[wordnumber]
end

local onetwomap = {
	{ name = 'Acropolis', height = 100 },
	{ name = 'Amazon', height = 93 },
	{ name = 'Babylon', height = 107 },
	{ name = 'Bio-Hazard', height = 95 },
	{ name = 'Hollow', height = 88 },
	{ name = 'Ironclad', height = 87 },
	{ name = 'Lightstone', height = 95 },
	{ name = 'Mirage', height = 86 },
	{ name = 'Orbit', height = 96 },
	{ name = 'Playground', height = 100 },
	{ name = 'Rooted', height = 95 },
	{ name = 'Scorched Sands', height = 92 },
	{ name = 'Siege', height = 108 },
	{ name = 'Steampunk', height = 99 },
	{ name = 'Aqil', height = 90 },
	{ name = 'Crypt', height = 95 },
	{ name = 'Dragonstar', height = 100 },
	{ name = 'Gateway', height = 128 },
	{ name = 'Harvest', height = 83 },
	{ name = 'Lighthouse', height = 110 },
	{ name = 'Lucky Rush', height = 84 },
	{ name = 'Meso', height = 95 },
	{ name = 'Pernicious', height = 90 },
	{ name = 'Serenity', height = 93 },
	{ name = 'Sky Rise', height = 90 },
	{ name = 'Solace', height = 100 },
	{ name = 'Speedway', height = 90 },
	{ name = 'Zarzul', height = 114 },
    { name = 'Yue', height = 101 },
    { name = 'Pavilion', height = 97 }
}

local fourthreemap = {
	{ name = 'Archway', height = 86 },
	{ name = 'Ashore', height = 94 },
	{ name = 'Build Site', height = 96 },
	{ name = 'Chained', height = 90 },
	{ name = 'Enchanted', height = 100 },
	{ name = 'Extinction', height = 95 },
	{ name = 'Fang Outpost', height = 99 },
    { name = 'Horizon', height = 100 },
	{ name = 'Fort Doon', height = 139 },
	{ name = 'Frogiton', height = 90 },
	{ name = 'Graveship', height = 123 },
	{ name = 'Tigris', height = 101 },
	{ name = 'Usagi', height = 96 },
	{ name = 'Rise', height = 96 },
	{ name = 'Stonekeep', height = 75 },
	{ name = 'Aquarium', height = 110 },
	{ name = 'Artemis', height = 96 },
	{ name = 'Carapace', height = 94 },
	{ name = 'Catalyst', height = 101 },
	{ name = 'Deposit', height = 81 },
	{ name = 'Dreamgrove', height = 115 },
	{ name = 'Eastwood', height = 100 },
	{ name = 'Invasion', height = 115 },
	{ name = 'Jurassic', height = 94 },
	{ name = 'Kubo', height = 91 },
	{ name = 'Lectus', height = 90 },
	{ name = 'Obelisk', height = 114 },
	{ name = 'Paladin', height = 98 },
	{ name = 'Unturned', height = 92 },
    { name = 'Gardens', height = 101 }
}

local check = {
    ne = '\194\167c',
    e = '\194\167e',
    p = '\194\167a',
}

--ne: not enough
--e: enough
--p: purhcased

function logiccheck()
    local total_diamonds = totald()
        if client.chest_name() == "Upgrades & Traps" then
        local _, prot, _, _, _ = player.inventory.item_information(11)
        local _, sharp, _, _, _ = player.inventory.item_information(10)
        local _, trap, _, _, _ = player.inventory.item_information(23)
        local protslot = 11
        local sharpslot = 10
        local trap = 23

        -- 8 teams logic
        if onemaps == true then
            -- sharp checks

            -- sharp check 1 (8SS0P0C1)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P0C1")
                end
                
            end
            -- sharp check 2 (8SS0P1C2)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P1C2")
                end
                
            end
            -- sharp check 3 (8SS0P2C3)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P2C3")
                end
            end
            -- sharp check 4 (8SS0P3C4)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 16 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P3C4")
                end
            end
            -- sharp check 5 (8SS0P4C5)
            if sharp == check.e .. "Sharpened Swords" and prot == check.p .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P4C5")
                end
            end
            -- sharp check 6 (8SS0P4C6)
            if sharp == check.e .. "Sharpened Swords" and prot == check.ne .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P4C6")
                end
            end
            -- sharp check 7 (8SS0P2C7)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P2C7")
                end
            end
            -- sharp check 8 (8SS0P2C8)
            if sharp == check.e .. "Sharpened Swords" and prot == check.ne .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P2C8")
                end
            end
            -- sharp check 9 (8SS0P2C9)
            if sharp == check.e .. "Sharpened Swords" and prot == check.ne .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 4 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 8SS0P2C9")
                end
            end
            -- prot checks

            -- prot check 1 (8PS0P0C1)
            if prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 2 and sharp == check.ne .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot I | Check: 8PS0P0C1")
                end
                
            end
            -- prot check 2 (8PS1P0C2)
            if prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 2 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot I | Check: 8PS1P0C2")
                end
                
            end
            -- prot check 3 (8PS1P1C3)
            if prot == check.e .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 4 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot II | Check: 8PS1P1C3")
                end
            end
            -- prot check 4 (8PS1P2C4)
            if prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 8 and sharp == check.ne .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot III | Check: 8PS1P2C4")
                end
            end
            -- prot check 5 (8PS1P3C5)
            if prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 16 and sharp == check.ne .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot IV | Check: 8PS1P3C5")
                end
            end
            -- prot check 6 (8PS1P1C6)
            if prot == check.e .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 4 and sharp == check.ne .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot II | Check: 8PS1P1C6")
                end
            end
            -- prot check 7 (8PS1P2C7)
            if prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 8 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot III | Check: 8PS1P2C7")
                end
            end
            -- prot check 8 (8PS1P3C8)
            if prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 16 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot IV | Check: 8PS1P3C8")
                end
            end
        end

        -- 4 teams logic 
        if fourmaps == true then
            --sharp checks

            -- sharp check 1 (4SS0P0C1)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P0C1")
                end
            end
            -- sharp check 2 (4SS0P1C2)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 10 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P1C2")
                end
            end
            -- sharp check 3 (4SS0P2C3)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 20 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P2C3")
                end
            end
            -- sharp check 4 (4SS0P3C4)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 30 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P3C4")
                end
            end
            -- sharp check 5 (4SS0P4C5)
            if sharp == check.e .. "Sharpened Swords" and prot == check.p .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P4C5")
                end
            end
            -- sharp check 6 (4SS0P4C6)
            if sharp == check.e .. "Sharpened Swords" and prot == check.ne .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P4C6")
                end
            end
            -- sharp check 7 (4SS0P2C7)
            if sharp == check.e .. "Sharpened Swords" and prot == check.p .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P2C7")
                end
            end
            -- sharp check 8 (4SS0P2C8)
            if sharp == check.e .. "Sharpened Swords" and prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P2C8")
                end
            end
            -- sharp check 9 (4SS0P2C9)
            if sharp == check.e .. "Sharpened Swords" and prot == check.ne .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 8 then
                player.inventory.click(sharpslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Sharp | Check: 4SS0P2C9")
                end
            end
            -- prot checks

            -- prot check 1 (4PS0P0C1)
            if prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 5 and sharp == check.ne .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot I | Check: 4PS0P0C1")
                end
            end
            -- prot check 2 (4PS1P0C2)
            if prot == check.e .. "Reinforced Armor I" and total_diamonds and total_diamonds >= 5 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot I | Check: 4PS1P0C2")
                end
            end
            -- prot check 4 (4PS1P2C3)
            if prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 20 and sharp == check.e .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot III | Check: 4PS1P2C4")
                end
            end
            -- prot check 5 (4PS1P3C4)
            if prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 30 and sharp == check.e .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot IV | Check: 4PS1P3C5")
                end
            end
            -- prot check 6 (4PS1P1C5)
            if prot == check.e .. "Reinforced Armor II" and total_diamonds and total_diamonds >= 10 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot II | Check: 4PS1P1C6")
                end
            end
            -- prot check 7 (4PS1P2C6)
            if prot == check.e .. "Reinforced Armor III" and total_diamonds and total_diamonds >= 20 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot III | Check: 4PS1P2C7")
                end
            end
            -- prot check 8 (4PS1P3C7)
            if prot == check.e .. "Reinforced Armor IV" and total_diamonds and total_diamonds >= 30 and sharp == check.p .. "Sharpened Swords" then
                player.inventory.click(protslot, 0, 0)
                if module_manager.option('Bedwars-Tools', 'Debug') then
                    client.print(debugtag .. "Attempted to buy Prot IV | Check: 4PS1P3C8")
                end
            end
        end
    end
end

local main = {
    on_pre_update = function()
        update()
        local players = world.entities()
        for i, entity in ipairs(players) do
            if world.is_player(entity) and not world.is_bot(entity) then
                local entity_name = world.display_name(entity)
                local tItem = world.held_item(entity)
                if module_manager.option('Bedwars-Tools', 'Diamond-Sword') then
                    if string.match(tItem, "Diamond Sword") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.aqua.. "Diamond Sword")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Ender Pearl') then
                    if string.match(tItem, "Ender Pearl") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has an " ..color.blue.. "Ender Pearl")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Obsidian') then
                    if string.match(tItem, "Obsidian") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has " ..color.black.. "Obsidian")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Fireball') then
                    if string.match(tItem, "Fireball") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.red.. "Fireball") 
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Bow') then
                    if string.match(tItem, "Bow") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.white.. "Bow")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'TNT') then
                    if string.match(tItem, "TNT") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.red.. "T" .. color.white .. "N" .. color.red .. "T")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Bridge-Egg') then
                    if string.match(tItem, "Bridge Egg") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.white.. "Bridge Egg")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Invis-Pot') then
                    if string.match(tItem, "Invisibility Potion") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has an " ..color.white.. "Invis Pot")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Jump-Pot') then
                    if string.match(tItem, "Jump V Potion") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.white.. "Jump Pot")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Speed-Pot') then
                    if string.match(tItem, "Speed II Potion") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.aqua.. "Speed Pot")
                        end
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Golem') then
                    if string.match(tItem, "Dream Defender") and not string.match(entity_name, world.display_name(player.id())) then
                        if not last_held_item[entity] or last_held_item[entity] ~= tItem then
                            client.print(tag ..  tostring(entity_name) .. color.gray.. " has a " ..color.white.. "Golem Spawn Egg")
                        end
                    end
                end
                last_held_item[entity] = tItem
            end
        end
        if client.gui_name() == "none" or client.gui_name() == "inventory" or client.gui_name() == "chat" or client.gui_name() == "astolfo" then
            check_inventory()
        end
        if client.gui_name() ~= "chest" then
            openticks = 0
            openedchest = false
        end
        if client.chest_name() ~= "Upgrades & Traps" then
            firstdelay = 0 
        end
        if client.chest_name() == "Chest" or client.chest_name() == "Ender Chest" and openedchest == false then
            if module_manager.option('Bedwars-Tools', 'Always') then
                openticks = openticks + 1
                ticksopened = module_manager.option('Bedwars-Tools', 'Open-Delay-Ticks')
                if openticks == ticksopened then
                    quickc()
                    openticks = 0
                    openedchest = true
                end
            end
            if module_manager.option('Bedwars-Tools', 'Middle-Click-Only') then
                if input.is_mouse_down(2) then
                    quickc()
                end
            end
            if module_manager.option('Bedwars-Tools', 'Chest-on-Bind') then
                if input.is_key_down(quickbind) then
                    quickc()
                end
            end
        end
        if client.chest_name() == "Upgrades & Traps" and module_manager.option('Bedwars-Tools', 'Smart') then
            cpscap = cpscap + 1
            firstdelay = firstdelay + 1
            upgradesopendelay = module_manager.option('Bedwars-Tools', 'First-Open-Delay')
            if firstdelay == upgradesopendelay then
                logiccheck()
                clickdelay = module_manager.option('Bedwars-Tools', 'Click-Delay')
            end
            if cpscap == clickdelay then
                logiccheck()
                cpscap = 0
            end
        end
        if sendmsg == true then
            player.message("/map")
            sendmsg = false
        end
        if module_manager.option('Bedwars-Tools', 'Retrieve-on-Bind') then
            if input.is_key_down(retrievebind) then
                retrievec()
            end
        end
        if module_manager.option('Bedwars-Tools', 'Quick-Block') then
            if input.is_key_down(woolslot) then
                setname = "cloth"
                slotkey = woolslot
                if setname ~= nil then
                    local slot = findHotbarSlot(setname)
            
                    if slot ~= -1 and input.is_key_down(slotkey) and client.gui_name() == "none" then
                        set_hotbar_slot(slot)
                    end 
                end
            end
        end
        if module_manager.option('Bedwars-Tools', 'Quick-Sword') then
            if input.is_key_down(swordslot) then
                setname = "sword"
                slotkey = swordslot
                if setname ~= nil then
                    local slot = findHotbarSlot(setname)
            
                    if slot ~= -1 and input.is_key_down(slotkey) and client.gui_name() == "none" then
                        set_hotbar_slot(slot)
                    end 
                end
            end
        end
        if module_manager.option('Bedwars-Tools', 'Quick-Pick') then
            if input.is_key_down(pickslot) then
                setname = "pickaxe"
                slotkey = pickslot
                if setname ~= nil then
                    local slot = findHotbarSlot(setname)
            
                    if slot ~= -1 and input.is_key_down(slotkey) and client.gui_name() == "none" then
                        set_hotbar_slot(slot)
                    end 
                end
            end
        end
        if module_manager.option('Bedwars-Tools', 'Quick-Axe') then
            if input.is_key_down(axeslot) then
                setname = "hatchet"
                slotkey = axeslot
                if setname ~= nil then
                    local slot = findHotbarSlot(setname)
            
                    if slot ~= -1 and input.is_key_down(slotkey) and client.gui_name() == "none" then
                        set_hotbar_slot(slot)
                    end 
                end
            end
        end
        if module_manager.option('Bedwars-Tools', 'Quick-Shears') then
            if input.is_key_down(shearslot) then
                setname = "shears"
                slotkey = shearslot
                if setname ~= nil then
                    local slot = findHotbarSlot(setname)
            
                    if slot ~= -1 and input.is_key_down(slotkey) and client.gui_name() == "none" then
                        set_hotbar_slot(slot)
                    end 
                end
            end
        end
        
        local list = world.entities()

        for i = 1, #list do

            local entindex = list[i]
            local inventory = world.inventory(entindex)
            if world.is_player(entindex) and not world.is_bot(entindex) then
                if module_manager.option('Bedwars-Tools', 'Chain-Armor') then
                    if inventory ~= nil and inventory.leggings == "item.leggingsChain" and not processedEntitiesChain[entindex] and not string.match(world.display_name(entindex), world.display_name(player.id())) then
                        processedEntitiesChain[entindex] = true
                        client.print(tag .. world.display_name(entindex) .. " " .. color.gray .. "has" .. " " .. color.gray .. "purchased" .. " " .. color.aqua .. "Chainmail" .. " " .. color.aqua .. "Armor")
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Iron-Armor') then
                    if inventory ~= nil and inventory.leggings == "item.leggingsIron" and not processedEntitiesIron[entindex] and not string.match(world.display_name(entindex), world.display_name(player.id())) then
                        processedEntitiesIron[entindex] = true
                        client.print(tag .. world.display_name(entindex) .. " " .. color.gray .. "has" .. " " .. color.gray .. "purchased" .. " " .. color.white .. "Iron" .. " " .. color.white .. "Armor")
                    end
                end
                if module_manager.option('Bedwars-Tools', 'Diamond-Armor') then
                    if inventory ~= nil and inventory.leggings == "item.leggingsDiamond" and not processedEntitiesDiamond[entindex] and not string.match(world.display_name(entindex), world.display_name(player.id())) then
                        processedEntitiesDiamond[entindex] = true
                        client.print(tag .. world.display_name(entindex) .. " " .. color.gray .. "has" .. " " .. color.gray .. "purchased" .. " " .. color.dark_aqua .. "Diamond" .. " " .. color.dark_aqua .. "Armor")
                    end
                end
            end
        end
    end,

    on_player_join = function()
        sendmsg = true
        processedEntitiesChain = {}
        processedEntitiesIron = {}
        processedEntitiesDiamond = {}
    end,

    on_receive_packet = function(t)
        if t.packet_id == 0x02 then
            local message = t.message
            for i = 1, #fourthreemap do
                if string.find(message, fourthreemap[i].name) and string.match(message, "You are currently") then
                    onemaps = false
                    fourmaps = true
                    foundMapName = fourthreemap[i].name
                    foundMapHeight = fourthreemap[i].height
                    if module_manager.option('Bedwars-Tools', 'Debug') then
                        client.print(debugtag .. "Using four teams logic.")
                    end
                end
            end
            for i = 1, #onetwomap do
                if string.find(message, onetwomap[i].name) then
                    onemaps = true
                    fourmaps = false
                    foundMapName = onetwomap[i].name
                    foundMapHeight = onetwomap[i].height
                    if module_manager.option('Bedwars-Tools', 'Debug') and string.match(message, "You are currently") then
                        client.print(debugtag .. "Using eight teams logic.")
                    end
                end
            end
        end
    end,
    
    on_send_packet = function(packet)
		if module_manager.is_module_on('chatfilter') then 
            player.message('.chatfilter')
            client.print(tag .. color.red..'Disabled ChatFilter, as commands don\'t work if it is on.')
        end
		if packet.packet_id == 0x01 then
			local prefix = string.sub(packet.message, 1, string.len(commandPrefix))
			if prefix == commandPrefix then
				packet.cancel = true
                local message = string.sub(packet.message, string.len(commandPrefix)+1)
				local command = getnthword(message, 1)
                local allargs = string.sub(message, #command+2)
                local args = split(allargs, ' ')
                local executed = false

                for k, v in pairs(commands) do
                    
                    if string.lower(command) == tostring(k) then
                        local out
                        local cmdargs = v.args.count
                        if cmdargs == -1 then
                            if allargs == '' then client.print(color.red.."Invalid Syntax. You need to provide at least 1 argument."); return packet end
                            out = allargs
                        elseif cmdargs == -2 then
                            if args[1] == '' then client.print(color.red.."Invalid Syntax. You need to provide at least 1 argument."); return packet end
                            out = args
                        elseif cmdargs == -3 then
                            out = args
                        elseif cmdargs > 1 then
                            if #args ~= cmdargs then
                                client.print(color.red.."Invalid Syntax. Use " ..color.dark_aqua .. "/bwt help" .. color.red .. " for "..  color.red .. "help.")
                                return packet
                            end
                            out = args
                        elseif cmdargs == 1 then
                            if #args ~= cmdargs or allargs == '' then
                                client.print(color.red.."Invalid Syntax. Use " ..color.dark_aqua .. "/bwt help" .. color.red .. " for "..  color.red .. "help.")
                                return packet
                            end
                            out = tostring(args[1])
                        end
                        v.command(out)
                        executed = true
                    end
                end
                if not executed then client.print(color.red.."Invalid Command. Use " ..color.dark_aqua .. "/bwt help" .. color.red .. " for "..  color.red .. "help.") end
			end
            local prefixerror = string.sub(packet.message, 1, string.len(commandPrefixerror))
            if packet.message == commandPrefixerror and packet.message ~= "/bwt help" then
                packet.cancel = true
                local message = string.sub(packet.message, string.len(commandPrefix)+1)
				local command = getnthword(message, 1)
                local allargs = string.sub(message, #command+2)
                local args = split(allargs, ' ')
                client.print(color.dark_aqua .. "Bedwars Tools " .. color.yellow .. "by unloged - v2.0")
                client.print(color.gray .. format.strikethrough .. "-------------------------")
                client.print(color.red .. "Use " .. color.dark_aqua .. "/bwt help " .. color.red .. "for help")
                client.print(color.gray .. format.strikethrough .. "-------------------------")
                client.print(color.red .. "Powered by " .. color.dark_aqua .. "antisniper.net")
                client.print(color.gray .. format.strikethrough .. "-------------------------")
            end
            if packet.message == "/mapinfo" then
                packet.cancel = true
                sendmsg = true
                if foundMapHeight ~= 0 then
                    client.print(color.yellow .. foundMapName .. color.gray.. " -> " .. color.yellow .. "Build Limit: " .. color.light_purple .. foundMapHeight)
                else
                    client.print(color.red .. "Could not identify map.")
                end
            end
            if string.sub(packet.message, 1, 4) == "/sc " then
                packet.cancel = true
                local ign = string.sub(packet.message, 5)
                http.get_async("https://api.antisniper.net/v2/player/winstreak?key=" .. antisniperkey .. "&player=" .. ign, {
                    run = function(winstreak_text)
                        if string.match(winstreak_text, '{"success": true, "data": null}') or string.match(winstreak_text, '{"success": false, "cause": "Malformed [player]"}') then
                            client.print(color.red .. "Invalid username.")
                        end
                        local overall_winstreak = tonumber(string.match(winstreak_text, '"overall_winstreak": (%d+)'))
                        local capsign = string.match(winstreak_text, '"ign": "([%w_]+)"')
                        http.get_async("https://api.antisniper.net/v2/convert/hypixel?key=" .. antisniperkey .. "&player=" .. ign, {
                            run = function(convert_text)
                                if string.match(convert_text, '{"success": false, "cause": "Missing one or more fields [key]"}') or string.match(convert_text, '{"success": false, "cause": "Access is forbidden, usually due to an invalid API key being used."}') then
                                    client.print(color.red .. "Invalid AntiSniper API Key.")
                                end
                                local uuid = string.match(convert_text, '"uuid": "([%w%-]+)"')
                                http.get_async("https://api.hypixel.net/player?key=" .. hypixelkey .. "&uuid=" .. uuid .. "&game=bedwars", {
                                    run = function(checks_text)
                                        if string.match(checks_text, '{"success":false,"cause":"Invalid API key"}') then
                                            client.print(color.red .. "Invalid Hypixel API Key.")
                                        else
                                            local star = tonumber(string.match(checks_text, '"bedwars_level":(%d+)'))
                                            local wins = tonumber(string.match(checks_text, '"wins_bedwars":(%d+)'))
                                            local losses = tonumber(string.match(checks_text, '"losses_bedwars":(%d+)'))
                                            local fk = tonumber(string.match(checks_text, '"final_kills_bedwars":(%d+)'))
                                            local fd = tonumber(string.match(checks_text, '"final_deaths_bedwars":(%d+)'))
                                            local bl = tonumber(string.match(checks_text, '"beds_lost_bedwars":(%d+)'))
                                            local bb = tonumber(string.match(checks_text, '"beds_broken_bedwars":(%d+)'))
                                            client.print('Username: ' .. color.yellow .. capsign)
                                            client.print("Star: " .. color.yellow .. star)
                                            client.print('WS: ' .. color.yellow .. overall_winstreak)
                                            client.print("Wins: " .. color.yellow .. wins)
                                            client.print("Losses: " .. color.yellow .. losses)
                                            if fd ~= 0.00 then 
                                                client.print('FKDR: ' .. color.yellow .. round(fk/fd, 2))
                                            elseif fd == 0.00 then
                                                client.print('FKDR: ' .. color.yellow .. fk)
                                            else
                                                client.print('FKDR: ' .. color.yellow .. fk)
                                            end
                                            if bl ~= 0.00 then 
                                                client.print('BBLR: ' .. color.yellow .. round(bb/bl, 2))
                                            elseif bl == 0.00 then
                                                client.print('BBLR: ' .. color.yellow .. bb)
                                            else
                                                client.print('BBLR: ' .. color.yellow .. bb)
                                            end
                                        end
                                    end
                                })
                            end
                        })
                    end
                })
            end
            if packet.message == "/sc" or packet.message == "/sc " or packet.message == "/sc" or packet.message == "/sc " then
                packet.cancel = true
                client.print(color.red.."Invalid Syntax. Use " ..color.dark_aqua .. "/bwt help" .. color.red .. " for "..  color.red .. "help.")
            end
            if packet.message == "/q" or packet.message == "/queue" then
                packet.cancel = true
                client.print(color.red.."Invalid Syntax. Use " ..color.dark_aqua .. "/bwt help" .. color.red .. " for "..  color.red .. "help.")
            end
            if packet.message == "/q 1" or packet.message == "/queue 1" then
                packet.cancel = true
                player.message('/play bedwars_eight_one')
            end
            if packet.message == "/q 2" or packet.message == "/queue 2" then
                packet.cancel = true
                player.message('/play bedwars_eight_two')
            end
            if packet.message == "/q 3" or packet.message == "/queue 3" then
                packet.cancel = true
                player.message('/play bedwars_four_three')
            end
            if packet.message == "/q 4" or packet.message == "/queue 4" then
                packet.cancel = true
                player.message('/play bedwars_four_four')
            end
            if packet.message == "/q 24" or packet.message == "/queue 24" then
                packet.cancel = true
                player.message('/play bedwars_two_four')
            end
		end
		return packet
	end,

    on_render_screen = function(t)
        if module_manager.option('Bedwars-Tools', 'Toggle') then
            local scalefactor = module_manager.option('Bedwars-Tools', 'Scale-Factor')
            local currentPotions = getPotions()
            local spacing = 10
            local top = (t.height - spacing*#currentPotions) / 2
            for i, v in ipairs(currentPotions) do
                render.scale(scalefactor)
                render.string_shadow(v, 8, top + (i-1)*spacing + 45, 255, 255, 255, 255)
                render.scale(1/scalefactor)
            end
        end
    end
}

local height = {    
    on_render_screen = function()
        if module_manager.option('Height-Indicator', 'Toggle') then
        xoption = module_manager.option('Height-Indicator', 'X')
        yoption =module_manager.option('Height-Indicator', 'Y')
        scalefactor = module_manager.option('Height-Indicator', 'Size')
        R = module_manager.option('Height-Indicator', 'Text-R')
        G =module_manager.option('Height-Indicator', 'Text-G')
        B = module_manager.option('Height-Indicator', 'Text-B')
        A = 255
        if foundMapHeight ~= 0 then
            if module_manager.option('Height-Indicator', 'Shadow') then
                render.scale(scalefactor)
                render.string_shadow(foundMapHeight, xoption, yoption, R, G, B, A)
                render.scale(1/scalefactor)
            else
                render.scale(scalefactor)
                render.string(foundMapHeight, xoption, yoption, R, G, B, A)
                render.scale(1/scalefactor)
            end
        else
            if module_manager.option('Height-Indicator', 'Shadow') then
                render.scale(scalefactor)
                render.string_shadow("0", xoption, yoption, R, G, B, A)
                render.scale(1/scalefactor)
            else
                render.scale(scalefactor)
                render.string("0", xoption, yoption, R, G, B, A)
                render.scale(1/scalefactor)
            end
        end
        if module_manager.option('Height-Indicator', 'Show-Y-Coords') then
            local x, y, z = player.position()
            yxoption = module_manager.option('Height-Indicator', 'Y-X')
            yyoption =module_manager.option('Height-Indicator', 'Y-Y')
            yscalefactor = module_manager.option('Height-Indicator', 'Y-Size')
            YR = module_manager.option('Height-Indicator', 'Y-R')
            YG =module_manager.option('Height-Indicator', 'Y-G')
            YB = module_manager.option('Height-Indicator', 'Y-B')
            YA = 255
            if module_manager.option('Height-Indicator', 'Y-Shadow') then
                render.scale(yscalefactor)
                render.string_shadow(math.floor(y), yxoption, yyoption, YR, YB, YG, YA)
                render.scale(1/yscalefactor)
            else
                render.scale(yscalefactor)
                render.string(math.floor(y), yxoption, yyoption, YR, YB, YG, YA)
                render.scale(1/yscalefactor)
            end
        end
    end
    if module_manager.option('Height-Indicator', 'COMBINE') then
        local x, y, z = player.position()
        cxoption = module_manager.option('Height-Indicator', 'Combine-X')
        cyoption =module_manager.option('Height-Indicator', 'Combine-Y')
        cscalefactor = module_manager.option('Height-Indicator', 'Combine-Size')
        CR = module_manager.option('Height-Indicator', 'Combine-R')
        CG =module_manager.option('Height-Indicator', 'Combine-G')
        CB = module_manager.option('Height-Indicator', 'Combine-B')
        CA = 255
        player.message('.Height-Indicator Show-Y-Coords false')
        player.message('.Height-Indicator Toggle false')
        if module_manager.option('Height-Indicator', 'Combine-Shadow') then
            render.scale(cscalefactor)
            render.string_shadow(math.floor(y) .. "/" .. foundMapHeight, cxoption, cyoption, CR, CB, CG, CA)
            render.scale(1/cscalefactor)
        else
            render.scale(cscalefactor)
            render.string(math.floor(y) .. "/" .. foundMapHeight, cxoption, cyoption, CR, CB, CG, CA)
            render.scale(1/cscalefactor)
        end
    end
end
}

local statoverlay = {
    on_render_screen = function()
        local r = 255
        local g = 0
        local b = (math.sin(os.clock() * 2) * 0.5 + 0.5) * 255

        -- stats text box
        render.scale(0.9)
        render.rect(15, 15, 250, 30, 0, 0, 0, 150)
        render.line(15, 15, 250, 15, 2, 255, 255, 255, 255)
        render.line(15, 30, 250, 30, 2, 255, 255, 255, 255)
        render.line(15, 15, 15, 30, 2, 255, 255, 255, 255)
        render.line(250, 15, 250, 30, 2, 255, 255, 255, 255)
        render.scale(1/0.9)

        -- main box entites
        render.scale(0.9)
        render.rect(15, 30, 250, 200, 0, 0, 0, 150)
        render.scale(1/0.9)

        -- stats
        render.scale(0.8)
        render.string_shadow('ign                                           star   fkdr   ws   bblr', 23, 22, r, g, b, 255)
        render.scale(1/0.8)

        -- show stats stuff
        render.scale(0.8)
        render.string_shadow(world.display_name(player.id()) .. "                                           -   -   -   -", 23, 50, 255, g, b, 255)
        render.scale(1/0.8)
    end
}

-- main 

module_manager.register('Bedwars-Tools', main)

module_manager.register_boolean('Bedwars-Tools', '--[Item Alert]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Diamond-Sword', true)
module_manager.register_boolean('Bedwars-Tools', 'Ender-Pearl', true)
module_manager.register_boolean('Bedwars-Tools', 'Bridge-Egg', true)
module_manager.register_boolean('Bedwars-Tools', 'Obsidian', true)
module_manager.register_boolean('Bedwars-Tools', 'Fireball', true)
module_manager.register_boolean('Bedwars-Tools', 'Invis-Pot', true)
module_manager.register_boolean('Bedwars-Tools', 'Jump-Pot', true)
module_manager.register_boolean('Bedwars-Tools', 'Speed-Pot', true)
module_manager.register_boolean('Bedwars-Tools', 'Golem', true)
module_manager.register_boolean('Bedwars-Tools', 'Bow', true)
module_manager.register_boolean('Bedwars-Tools', 'TNT', true)

module_manager.register_boolean('Bedwars-Tools', '--[Armor Alert]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Chain-Armor', true)
module_manager.register_boolean('Bedwars-Tools', 'Iron-Armor', false)
module_manager.register_boolean('Bedwars-Tools', 'Diamond-Armor', true)

module_manager.register_boolean('Bedwars-Tools', '--[Resource Alert]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Iron', true)
module_manager.register_boolean('Bedwars-Tools', 'Gold', true)
module_manager.register_boolean('Bedwars-Tools', 'Diamond', true)
module_manager.register_boolean('Bedwars-Tools', 'Emerald', true)

module_manager.register_boolean('Bedwars-Tools', '--[Quick Chest]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Resources-Only', true)
module_manager.register_boolean('Bedwars-Tools', 'Always', true)
module_manager.register_number('Bedwars-Tools', 'Open-Delay-Ticks', 1, 100, 5) -- only works with always
module_manager.register_boolean('Bedwars-Tools', 'Middle-Click-Only', false)
module_manager.register_boolean('Bedwars-Tools', 'Chest-on-Bind', false)
module_manager.register_boolean('Bedwars-Tools', 'Retrieve-on-Bind', false)
module_manager.register_boolean('Bedwars-Tools', 'Retrieve-Resources-Only', true)

module_manager.register_boolean('Bedwars-Tools', '--[Auto Upgrade]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Smart', true)
module_manager.register_number('Bedwars-Tools', 'Click-Delay', 1, 20, 5)
module_manager.register_number('Bedwars-Tools', 'First-Open-Delay', 1, 20, 1)
module_manager.register_boolean('Bedwars-Tools', 'Debug', false)

module_manager.register_boolean('Bedwars-Tools', '--[Quick Slot]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Quick-Block', true)
module_manager.register_boolean('Bedwars-Tools', 'Quick-Sword', true)
module_manager.register_boolean('Bedwars-Tools', 'Quick-Axe', true)
module_manager.register_boolean('Bedwars-Tools', 'Quick-Pick', true)
module_manager.register_boolean('Bedwars-Tools', 'Quick-Shears', true)

module_manager.register_boolean('Bedwars-Tools', '--[Potion Hud]--', false)
module_manager.register_boolean('Bedwars-Tools', 'Toggle', true)
module_manager.register_boolean('Bedwars-Tools', 'Potion-Colors', false)
module_manager.register_number("Bedwars-Tools", "Scale-Factor", 0.51, 1.01, 0.81)

--module_manager.register_boolean('Bedwars-Tools', '--[Stats & Queue]--', false)
--module_manager.register_boolean('Bedwars-Tools', 'Show-Nicks', true)
--module_manager.register_boolean('Bedwars-Tools', 'Show-FKDRS', true)
--module_manager.register_number('Bedwars-Tools', 'FKDR-Min', 5, 1000, 100)
--module_manager.register_boolean('Bedwars-Tools', 'Show-WS', true)
--module_manager.register_number('Bedwars-Tools', 'WS-Min', 50, 1000, 100)

-- height indicator

module_manager.register('Height-Indicator', height)
module_manager.register_boolean('Height-Indicator', '--[Map Height]--', false)
module_manager.register_boolean('Height-Indicator', 'Toggle', true)
module_manager.register_boolean('Height-Indicator', 'Shadow', true)
module_manager.register_number('Height-Indicator', 'Size', 0.51, 1.01, 0.65)
module_manager.register_number('Height-Indicator', 'X', 0, 1000, 486)
module_manager.register_number('Height-Indicator', 'Y', 0, 1000, 490)
module_manager.register_number('Height-Indicator', 'Text-R', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Text-G', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Text-B', 0, 255, 255)

module_manager.register_boolean('Height-Indicator', '--[Y Coords]--', false)
module_manager.register_boolean('Height-Indicator', 'Show-Y-Coords', true)
module_manager.register_boolean('Height-Indicator', 'Y-Shadow', true)
module_manager.register_number('Height-Indicator', 'Y-Size', 0.51, 1.01, 0.65)
module_manager.register_number('Height-Indicator', 'Y-X', 0, 1000, 500)
module_manager.register_number('Height-Indicator', 'Y-Y', 0, 1000, 490)
module_manager.register_number('Height-Indicator', 'Y-R', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Y-G', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Y-B', 0, 255, 255)

module_manager.register_boolean('Height-Indicator', '--[Combine]--', false)
module_manager.register_boolean('Height-Indicator', 'COMBINE', true)
module_manager.register_boolean('Height-Indicator', 'Combine-Shadow', true)
module_manager.register_number('Height-Indicator', 'Combine-Size', 0.51, 1.01, 0.65)
module_manager.register_number('Height-Indicator', 'Combine-X', 0, 1000, 486)
module_manager.register_number('Height-Indicator', 'Combine-Y', 0, 1000, 490)
module_manager.register_number('Height-Indicator', 'Combine-R', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Combine-G', 0, 255, 255)
module_manager.register_number('Height-Indicator', 'Combine-B', 0, 255, 255)

module_manager.register_boolean('Bedwars-Tools', '-------------------------------', false)
module_manager.register_boolean('Bedwars-Tools', 'v2.0 (DEV)', false)

-- overlay

--module_manager.register('Overlay', statoverlay)

-- Syuto & chatgpt+

-- STATS POWERED AND PROVIDED BY ANTISNIPER.NET