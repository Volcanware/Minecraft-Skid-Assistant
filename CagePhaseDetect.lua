has_item = function (itemCode)
    for i = 0, 36, 1 do
      if player.inventory.item_information(i) ~= nil and player.inventory.slot(i) ~= nil then
        if player.inventory.item_information(i) == itemCode and string.find(player.inventory.slot(i), "Kit") then
            return true
        end
    end
end
end

has_value = function(tab, val)
    for index, value in ipairs(tab) do
        if value == val then
            return true
        end
    end
    return false
end


local yLock = 0;
local hackerList = {}
local CagePhaseDetect = {

	on_player_join = function ()
		yLock = 0;
	end,

		on_pre_update = function()
			local pID = player.id()
			local list = world.entities()
			local x0, y0, z0 = player.position(pID)
			for i = 0, #list do
				local eid = list[i]
				local eidHP = world.health(eid)
				if not has_value(hackerList, eid) then
					if eid ~= pID and world.is_player(eid) and eidHP == 20 and eid ~= world.is_bot() and has_item('item.bow') then
						local x1, y1, z1 = world.position(eid)
						if yLock < y0 then
							yLock = y0
						end
						if y1 < yLock - 4 then
							table.insert(hackerList, eid)
							hackerName = world.name(eid)
							client.print(hackerName .. ' has phased [CHEATER]')
							player.message(".enemy add ".. hackerName)
							if module_manager.option('CagePhaseDetect', 'Snitch') then
								player.message(hackerName .. ' Has phased and is cheating!')
							end
						end
					end
				end
			end
		end,

}

module_manager.register('CagePhaseDetect', CagePhaseDetect)
module_manager.register_boolean('CagePhaseDetect', 'Snitch', false)
