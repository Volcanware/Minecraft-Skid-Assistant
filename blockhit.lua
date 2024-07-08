function isHoldingSword()
    return string.match(player.held_item(), "Sword")
end

function get_lowest_angle_entity()
  local entities = world.entities()
  local lowest_angle_id = nil
  local lowest_angle = 42069
  for i = 0, #entities, 1 do
    local entity = entities[i]
    local min_x, min_y, min_z, max_x, max_y, max_z = world.bounding_box(entity)
    local t_x, t_y, t_z = world.position(entity)
    if max_y ~= nil and min_y ~= nil then
      t_y = t_y + ((max_y - min_y) / 2)
    end
    local t_yaw, t_pitch = player.angles_for_cords(t_x, t_y, t_z)
    local yaw, pitch = player.angles()
    local yaw_diff, pitch_diff = math.abs(t_yaw - yaw), math.abs(t_pitch - pitch)
    local angle_diff = yaw_diff + pitch_diff
    if angle_diff < lowest_angle and entity ~= player.id() and not world.is_bot(entity) and world.name(entity) ~= 'unknown' and (world.is_player(entity) or not module_manager.option('toggleblock', 'Player-Check')) and (not module_manager.option('toggleblock', 'Team-Check') or not is_on_team(entity)) and (world.max_health(entity) ~= nil)  and (player.distance_to_entity(entity) < 6.0) and check_fov(entity) then
      lowest_angle_id = entity
      lowest_angle = angle_diff
    end
  end
  return lowest_angle_id
end

function check_fov(entity_id)
  local entities = world.entities()
  local e_yaw, e_pitch = player.angles_for_cords(world.position(entity_id))
  local yaw, pitch = player.angles()
  local yaw_diff = math.abs(yaw - e_yaw)
  if yaw_diff < module_manager.option('toggleblock', 'FOV') / 2 then
    return true
  else
    return false
  end
end

function is_on_team(entityID)
  if entityID == nil then
    return nil
  end
  local list = world.entities()
  for i = 1, #list do
    if world.name(list[i]) == player.name() then
      display_name = world.display_name(list[i])
    end
  end
  if string.find(display_name, "\194\167") ~= nil then
    substring = string.sub(display_name, string.find(display_name, "\194\167"), string.find(display_name, "\194\167") +2)
    if string.find(world.display_name(entityID), substring) ~= nil then
      return true
    end
    return false
  end
end


local toggleblock = {
	on_enable = function()
		player.message(string.format(".autoclicker Block Chance %s", 99))
		--player.message('.autoclicker block Enable')
		if not module_manager.is_module_on("AutoClicker") then
			player.message(".autoclicker")
		end
	end,	
	on_pre_update = function()
		enemy=get_lowest_angle_entity()
		if enemy~=nil then 
			if check_fov(enemy) then 
				if player.distance_to_entity(enemy)>module_manager.option('toggleblock', 'distance') then 
					module_manager.set_option("Autoclicker","Block","Enable",false)  
				else 
					module_manager.set_option("Autoclicker","Block","Enable",true) 
				end 
			end
		else 
			module_manager.set_option("Autoclicker","Block","Enable",false)  
		end 
		
	end,
					
	on_disable = function()
		module_manager.set_option("Autoclicker","Block","Enable",false)
	end 		
}
module_manager.register("toggleblock", toggleblock)
module_manager.register_number('toggleblock', 'distance', 3.001, 4.501, 3.701)
module_manager.register_number('toggleblock', 'FOV', 0.001, 360.001, 40.001)
module_manager.register_boolean('toggleblock', 'Team-Check', true)
module_manager.register_boolean('toggleblock', 'Player-Check', true)