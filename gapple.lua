local find = {
    on_tick = function(event)
        local found_items = {}
        local slotz = modules:get_setting("AutoGapple", "slotz")
        for i = 0, 35 do
            local item = inventory:get_item(i)
            if item and string.find(item.name, "appleGold") then
                -- Do something if item name includes "appleGold"
                table.insert(found_items, {slot = i, size = item.stack_size})
            end
        end

        if #found_items > 0 then
            -- Find the item with the highest stack size
            local max_stack_item = found_items[1]
            for i = 2, #found_items do
                if found_items[i].size > max_stack_item.size then -- bug impl https://discord.com/channels/968569296412356629/1056187788208377948/1095169210012938270
                    max_stack_item = found_items[i]
                end
            end
            if max_stack_item.slot ~= slotz then
                client:print(max_stack_item.slot .. " " .. slotz)
                if max_stack_item.slot <= 8 then
                --    client:print("Debug Hotbar")
              --      inventory:window_click(1, max_stack_item.slot, 0, 1)
              --      client:print("Debug Hotbar1 ".. max_stack_item.slot)
               --     inventory:window_click(1, max_stack_item.slot, 0, 1)
              --      client:print("Debug Hotbar2 ")
                    -- Do something if the item is in the hotbar
                else
                   client:print("Debug item ")
                    inventory:swap(max_stack_item.slot, slotz)
                    modules:toggle("AutoGapple")

                    -- Print the slot number and stack size of the item with the highest stack size
                    -- client:print("Found appleGold at slot " .. max_stack_item.slot .. " with stack size " .. max_stack_item.size)
                end
                modules:toggle("AutoGapple")
            end
        end
    end
}

modules:register("AutoGapple", "AutoGapple", find)
modules:create_integer_setting("AutoGapple", "slotz", "Slot", 6, 0, 8, 1)



-- local find = {
--     on_tick = function(event)
--             local found_items = {}
--             local slotz = modules:get_setting("AutoGapple", "slotz")
--             for i = 0, 35 do
--                 local item = inventory:get_item(i)
--                 if item and string.find(item.name, "appleGold") then
--                     -- Do something if item name includes "appleGold"
--                     table.insert(found_items, {slot = i, size = item.stack_size})
--                 end
--             end
--
--             if #found_items > 0 then
--                 -- Find the item with the highest stack size
--                 local max_stack_item = found_items[1]
--                 for i = 2, #found_items do
--                     if found_items[i].size > max_stack_item.size then -- bug impl https://discord.com/channels/968569296412356629/1056187788208377948/1095169210012938270
--                         max_stack_item = found_items[i]
--                         window_click(1, 1, 1, 1)
--                         window_click(1, 2, 1, 1)
-- end
--                 end
--             --    client:print(max_stack_item.slot .. " " .. slotz)
--                 if max_stack_item.slot <= 8 then
--
--
--                 end
--
--                 else
-- inventory:swap(max_stack_item.slot, slotz)
-- modules:toggle("AutoGapple")
--
--                 -- Print the slot number and stack size of the item with the highest stack size
--           --      client:print("Found appleGold at slot " .. max_stack_item.slot .. " with stack size " .. max_stack_item.size)
--             else
--                modules:toggle("AutoGapple")
--             end
--         end
-- }
--
-- modules:register("AutoGapple", "AutoGapple", find)
-- modules:create_integer_setting("AutoGapple", "slotz", "Slot", 6, 0, 8, 1)
