function isValidEntity(entity_id)
    player_id = player.id()
    if world.is_player(entity_id) and not world.is_bot(entity_id) and entity_id ~= player_id and world.name(entity_id) ~= 'unknown' then
        return true
    end
    return false
end


function getReachFromStep(step_ct, step)
    diff = MAX_REACH - MIN_REACH
    step_increment = diff / step_ct

    return MIN_REACH + (step_increment * step)
end

function getDynamicHealthThreshold(health_threshold, step_ct, step)
    -- Returns the health threshold to increase reach on
    -- If we increase reaching starting at 15, and step is 0, we increase at 15
    -- If step is 1, we increase at 11.25 (15 - (15/4)*1)
    return math.max(health_threshold - ((health_threshold / step_ct) * step), 5)
end


function getCurrentlyAttackedEntity()
    entities = world.entities()
    is_entity, entity_id = player.over_mouse()
    if is_entity == 3 and isValidEntity(entity_id) then
        return entity_id
    end
end

function toggleKBBoost(doToggle)
    if module_manager.option("SmartReach", "DynamicKnockback") then
        is_kb_enabled = module_manager.is_module_on("knockback")

        if doToggle then
            if not is_kb_enabled then
                player.message(".knockback")
            end
        else
            if is_kb_enabled then
                player.message(".knockback")
            end
        end
    end
end

function getAbsoluteDistance(entity_id)
    -- Returns absolute distance between player and entity

    p_x, p_y, p_z = player.position()
    e_x, e_y, e_z = world.position(entity_id)

    res = math.pow(p_x - e_x, 2) + math.pow(p_y - e_y, 2) + math.pow(p_z - e_z, 2)
    return math.sqrt(res)

end

        



local current_reach = MIN_REACH

local smartReach = {


    on_enable = function()
        enabled = true
        player.message(string.format(".reach Reach %f", module_manager.option("SmartReach", "Min_Reach")/10))
        toggleKBBoost(true)

        combo = 0
        prev_combo = 0
        current_step = 0

    end,


    on_pre_update = function()
        MIN_REACH = module_manager.option("SmartReach", "Min_Reach")/10
        MAX_REACH = module_manager.option("SmartReach", "Max_Reach")/10
        low_health_increase = module_manager.option("SmartReach", "LowHealthIncrease")
        combo_increase = module_manager.option("SmartReach", "ComboIncrease")
        exceed_opponenet = module_manager.option("SmartReach", "ExceedOpponentRange")

        reach_steps = module_manager.option("SmartReach", "IncreaseSteps")

        should_increase = false
        should_decrease = false
        should_reset = false


        -- Combo based reach increase
        current_entity = getCurrentlyAttackedEntity()

        if combo_increase and current_entity then
            hurt_time = world.hurt_time(current_entity)

            if hurt_time == 0 and combo > 0 then
                prev_combo = 0
                combo = 0
                toggleKBBoost(true)
                should_reset = true
            end
            if hurt_time == 9 then
                combo = combo + 1
            end

            if combo > 0 and combo > prev_combo then
                prev_combo = combo
                should_increase = true
                toggleKBBoost(false)
            elseif combo == 0 and current_step > 0 then
                toggleKBBoost(true)
                should_reset = true
            end
        end

       
        -- Health based reach increase
        if low_health_increase then
            health_threshold = module_manager.option("SmartReach", "HealthThreshold")
            p_health = player.health()

            if p_health <= health_threshold then
                should_reset = false --Override combo based reach reset
                dynamic_threshold = getDynamicHealthThreshold(health_threshold, reach_steps, current_step)
                if p_health <= dynamic_threshold then
                    should_increase = true
                elseif p_health > getDynamicHealthThreshold(health_threshold, reach_steps, current_step-1) then
                    should_decrease = true
                end
            else
                should_decrease = true
            end
        end


        if should_increase and current_step < reach_steps then
            current_step = current_step + 1
            new_reach = getReachFromStep(reach_steps, current_step)
            player.message(string.format(".reach Reach %f", new_reach))
            --client.print(string.format("Increased Reach to %f", new_reach))
        elseif should_decrease and current_step > 0 then
            current_step = current_step - 1
            toggleKBBoost(true)
            new_reach = getReachFromStep(reach_steps, current_step)
            player.message(string.format(".reach Reach %f", new_reach))
            --client.print(string.format("Decreased Reach to %f", new_reach))

        elseif should_reset then
            current_step = 0
            player.message(string.format(".reach Reach %f", MIN_REACH))
        end
    end

}



module_manager.register("SmartReach", smartReach)
module_manager.register_number("SmartReach", "Min_Reach", 30, 42, 34)
module_manager.register_number("SmartReach", "Max_Reach", 30, 42, 38)
module_manager.register_boolean("SmartReach", "LowHealthIncrease", true)
module_manager.register_boolean("SmartReach", "ComboIncrease", true)
module_manager.register_number("SmartReach", "IncreaseSteps", 2, 8, 4)
module_manager.register_number("SmartReach", "HealthThreshold", 5, 18, 15)
module_manager.register_boolean("SmartReach", "DynamicKnockback", true)
