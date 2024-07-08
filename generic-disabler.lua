function tprint (tbl, indent)
  client.print('')
  if not indent then indent = 1 end
  if tbl == nil then client.print('nil') return end
  for k, v in pairs(tbl) do
    formatting = string.rep("  ", indent) .. k .. ": "
    if type(v) == "table" then
      client.print(formatting)
      tprint(v, indent+1)
    elseif type(v) == 'boolean' or 'function' then
      client.print(formatting .. tostring(v))
    else
      client.print(formatting .. v)
    end
  end
end


--player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
--player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
local queue_0x0F = {}
local queue_burst_0x0F = {}
local burst_0x0F_time = 0
local amount_recv_0x0F = 0
local queue_0x00 = {}
local queue_burst_0x00 = {}
local burst_0x00_time = 0
local amount_recv_0x00 = 0

function send_0x00_queue()
  if queue_burst_0x00[1] ~= nil then
    for i = 1, #queue_burst_0x00, 1 do
      local sending_packet = queue_burst_0x00[i]
      player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
    end
  end
  if queue_0x00[1] ~= nil then
    for i = 1, #queue_0x00, 1 do
      local sending_packet = queue_burst_0x00[i]
      player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
    end
  end
  queue_0x00 = {}
  queue_burst_0x00 = {}
  burst_0x00_time = client.time()
  amount_recv_0x00 = 0
end

function send_0x0F_queue()
  if queue_burst_0x0F[1] ~= nil then
    for i = 1, #queue_burst_0x0F, 1 do
      local sending_packet = queue_burst_0x0F[i]
      player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
    end
  end
  if queue_0x0F[1] ~= nil then
    for i = 1, #queue_0x0F, 1 do
      local sending_packet = queue_burst_0x0F[i]
      player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
    end
  end
  queue_0x0F = {}
  queue_burst_0x0F = {}
  burst_0x0F_time = client.time()
  amount_recv_0x0F = 0
end



local genericdisabler = {
  on_enable = function()
    queue_0x0F = {}
    queue_burst_0x0F = {}
    burst_0x0F_time = client.time()
    amount_recv_0x0F = 0
    queue_0x00 = {}
    queue_burst_0x00 = {}
    burst_0x00_time = client.time()
    amount_recv_0x00 = 0
  end,

  on_player_join = function()
    queue_0x0F = {}
    queue_burst_0x0F = {}
    burst_0x0F_time = client.time()
    amount_recv_0x0F = 0
    queue_0x00 = {}
    queue_burst_0x00 = {}
    burst_0x00_time = client.time()
    amount_recv_0x00 = 0
  end,

  on_disable = function()
    send_0x00_queue()
    send_0x0F_queue()
  end,

  on_send_packet = function(packet)

    if packet.packet_id == 0x0F and packet.uid < 0 then
      packet['time'] = client.time()
      table.insert(queue_0x0F, packet)
      amount_recv_0x0F = amount_recv_0x0F + 1
      packet.cancel = true
    elseif packet.packet_id == 0x00 then
      packet['time'] = client.time()
      table.insert(queue_0x00, packet)
      amount_recv_0x00 = amount_recv_0x00 + 1
      packet.cancel = true
    end

    if (queue_0x00[1] ~= nil) and (client.time() - queue_0x00[1].time > module_manager.option('generic-disabler', '0x00-min-delay')) and (client.time() - burst_0x00_time > module_manager.option('generic-disabler', '0x00-burst-delay')) then
      queue_burst_0x00 = {}
      local burst_amount = 0
      while (queue_0x00[1] ~= nil) and (client.time() - queue_0x00[1].time > module_manager.option('generic-disabler', '0x00-min-delay')) and ((module_manager.option('generic-disabler', '0x00-send-rate') >= 1) or (burst_amount < math.max(module_manager.option('generic-disabler', '0x00-send-rate') * amount_recv_0x00, module_manager.option('generic-disabler', '0x00-min-send-amount')))) do
        table.insert(queue_burst_0x00, queue_0x00[1])
        table.remove(queue_0x00, 1)
        burst_amount = burst_amount + 1
      end

      local cancel_amount = 0
      local double_send_amount = 0
      local double_switch_amount = 0
      local burst_switch_amount = 0
      local burst_rand_amount = 0
      local key_invert_amount = 0
      local key_rand_amount = 0
      local min_delay, max_delay
      local actual_send_amount = 0

      if queue_burst_0x00[1] ~= nil and queue_burst_0x00[1].time ~= nil then
        max_delay = client.time() - queue_burst_0x00[1].time
      end
      while queue_burst_0x00[1] ~= nil do
        local cancel = false
        local modified = false
        local sending_packet

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-switch-chance') and queue_burst_0x00[2] ~= nil then
          sending_packet = queue_burst_0x00[2]
          table.remove(queue_burst_0x00, 2)
          burst_switch_amount = burst_switch_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        elseif not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-burst-rand-chance') and queue_burst_0x00[1] ~= nil then
          local random_index = math.random(1, #queue_burst_0x00)
          sending_packet = queue_burst_0x00[random_index]
          table.remove(queue_burst_0x00, random_index)
          burst_rand_amount = burst_rand_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        else
          sending_packet = queue_burst_0x00[1]
          table.remove(queue_burst_0x00, 1)
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-cancel-chance') then
          cancel = true
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        -- 0x00-double-send-chance
        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-double-send-chance') then
          player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
          actual_send_amount = actual_send_amount + 1
          double_send_amount = double_send_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        -- 0x00-double-switch-chance
        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-double-switch-chance') then
          if queue_burst_0x00[1] ~= nil then
            player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
            local sending_packet_2 = queue_burst_0x00[1]
            table.remove(queue_burst_0x00, 1)
            player.send_packet_no_event(0x00, sending_packet_2.keep_alive_key)
            actual_send_amount = actual_send_amount + 2
            double_switch_amount = double_switch_amount + 1
            if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
              modified = true
            end
          end
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-key-invert-chance') then
          player.send_packet_no_event(0x00, -1 * sending_packet.keep_alive_key)
          actual_send_amount = actual_send_amount + 1
          key_invert_amount = key_invert_amount + 1
          if module_manager.option('generic-disabler', '0x00-key-invert-cancel') then
            cancel = true
          end
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x00-key-rand-chance') then
          player.send_packet_no_event(0x00, math.random(module_manager.option('generic-disabler', '0x00-key-rand-min'), module_manager.option('generic-disabler', '0x00-key-rand-max')))
          actual_send_amount = actual_send_amount + 1
          key_rand_amount = key_rand_amount + 1
          if module_manager.option('generic-disabler', '0x00-key-rand-cancel') then
            cancel = true
          end
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end


        if not cancel then
          player.send_packet_no_event(0x00, sending_packet.keep_alive_key)
          actual_send_amount = actual_send_amount + 1
        else
          cancel_amount = cancel_amount + 1
        end

        min_delay = client.time() - sending_packet.time

      end

      if module_manager.option('generic-disabler', '0x00-debug') then
        client.print('')
        client.print(amount_recv_0x00 .. ' 0x00 received in last burst window')
        client.print(burst_amount .. ' 0x00 in last burst batch, ' .. #queue_0x00 .. ' still in queue')
        client.print(actual_send_amount .. ' 0x00 packets actually sent, with delay [' .. tostring(min_delay) .. ', ' .. tostring(max_delay) .. ']')
        client.print('    ' .. cancel_amount .. ' canceled')
        client.print('    ' .. double_send_amount .. ' double sent')
        client.print('    ' .. double_switch_amount .. ' double switched')
        client.print('    ' .. burst_switch_amount .. ' sent one spot early')
        client.print('    ' .. burst_rand_amount .. ' sent in random order')
        client.print('    ' .. key_invert_amount .. ' key inverted')
        client.print('    ' .. key_rand_amount .. ' key randomized')
      end

      if #queue_0x00 > module_manager.option('generic-disabler', '0x00-max-queue-size') then
        send_0x00_queue()
        if module_manager.option('generic-disabler', '0x00-debug') then
          client.print('sent 0x00 backlog as it exceeded max size set')
        end
      end

      burst_0x00_time = client.time()
      amount_recv_0x00 = 0

    end






    if (queue_0x0F[1] ~= nil) and (client.time() - queue_0x0F[1].time > module_manager.option('generic-disabler', '0x0F-min-delay')) and (client.time() - burst_0x0F_time > module_manager.option('generic-disabler', '0x0F-burst-delay')) then
      queue_burst_0x0F = {}
      local burst_amount = 0
      while (queue_0x0F[1] ~= nil) and (client.time() - queue_0x0F[1].time > module_manager.option('generic-disabler', '0x0F-min-delay')) and ((module_manager.option('generic-disabler', '0x0F-send-rate') >= 1) or (burst_amount < math.max(module_manager.option('generic-disabler', '0x0F-send-rate') * amount_recv_0x0F, module_manager.option('generic-disabler', '0x0F-min-send-amount')))) do
        table.insert(queue_burst_0x0F, queue_0x0F[1])
        table.remove(queue_0x0F, 1)
        burst_amount = burst_amount + 1
      end

      local cancel_amount = 0
      local double_send_amount = 0
      local double_switch_amount = 0
      local burst_switch_amount = 0
      local burst_rand_amount = 0
      local uid_invert_amount = 0
      local uid_rand_amount = 0
      local min_delay, max_delay
      local actual_send_amount = 0

      if queue_burst_0x0F[1] ~= nil and queue_burst_0x0F[1].time ~= nil then
        max_delay = client.time() - queue_burst_0x0F[1].time
      end
      while queue_burst_0x0F[1] ~= nil do
        local cancel = false
        local modified = false
        local sending_packet

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-burst-switch-chance') and queue_burst_0x0F[2] ~= nil then
          sending_packet = queue_burst_0x0F[2]
          table.remove(queue_burst_0x0F, 2)
          burst_switch_amount = burst_switch_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        elseif not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-burst-rand-chance') and queue_burst_0x0F[1] ~= nil then
          local random_index = math.random(1, #queue_burst_0x0F)
          sending_packet = queue_burst_0x0F[random_index]
          table.remove(queue_burst_0x0F, random_index)
          burst_rand_amount = burst_rand_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        else
          sending_packet = queue_burst_0x0F[1]
          table.remove(queue_burst_0x0F, 1)
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-cancel-chance') then
          cancel = true
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        -- 0x0F-double-send-chance
        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-double-send-chance') then
          player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
          actual_send_amount = actual_send_amount + 1
          double_send_amount = double_send_amount + 1
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        -- 0x0F-double-switch-chance
        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-double-switch-chance') then
          if queue_burst_0x0F[1] ~= nil then
            player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
            local sending_packet_2 = queue_burst_0x0F[1]
            table.remove(queue_burst_0x0F, 1)
            player.send_packet_no_event(0x0F, sending_packet_2.window_id, sending_packet_2.uid, sending_packet_2.accept)
            actual_send_amount = actual_send_amount + 2
            double_switch_amount = double_switch_amount + 1
            if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
              modified = true
            end
          end
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-uid-invert-chance') then
          player.send_packet_no_event(0x0F, sending_packet.window_id, -1 * sending_packet.uid, sending_packet.accept)
          actual_send_amount = actual_send_amount + 1
          uid_invert_amount = uid_invert_amount + 1
          if module_manager.option('generic-disabler', '0x0F-uid-invert-cancel') then
            cancel = true
          end
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end

        if not modified and math.random(1, 100) <= module_manager.option('generic-disabler', '0x0F-uid-rand-chance') then
          player.send_packet_no_event(0x0F, sending_packet.window_id, math.random(module_manager.option('generic-disabler', '0x0F-uid-rand-min'), module_manager.option('generic-disabler', '0x0F-uid-rand-max')), sending_packet.accept)
          actual_send_amount = actual_send_amount + 1
          uid_rand_amount = uid_rand_amount + 1
          if module_manager.option('generic-disabler', '0x0F-uid-rand-cancel') then
            cancel = true
          end
          if not module_manager.option('generic-disabler', 'allow-repeat-modify') then
            modified = true
          end
        end


        if not cancel then
          player.send_packet_no_event(0x0F, sending_packet.window_id, sending_packet.uid, sending_packet.accept)
          actual_send_amount = actual_send_amount + 1
        else
          cancel_amount = cancel_amount + 1
        end

        min_delay = client.time() - sending_packet.time

      end

      if module_manager.option('generic-disabler', '0x0F-debug') then
        client.print('')
        client.print(amount_recv_0x0F .. ' 0x0F received in last burst window')
        client.print(burst_amount .. ' 0x0F in last burst batch, ' .. #queue_0x0F .. ' still in queue')
        client.print(actual_send_amount .. ' 0x0F packets actually sent, with delay [' .. tostring(min_delay) .. ', ' .. tostring(max_delay) .. ']')
        client.print('    ' .. cancel_amount .. ' canceled')
        client.print('    ' .. double_send_amount .. ' double sent')
        client.print('    ' .. double_switch_amount .. ' double switched')
        client.print('    ' .. burst_switch_amount .. ' sent one spot early')
        client.print('    ' .. burst_rand_amount .. ' sent in random order')
        client.print('    ' .. uid_invert_amount .. ' uid inverted')
        client.print('    ' .. uid_rand_amount .. ' uid randomized')
      end

      if #queue_0x0F > module_manager.option('generic-disabler', '0x0F-max-queue-size') then
        send_0x0F_queue()
        if module_manager.option('generic-disabler', '0x0F-debug') then
          client.print('sent 0x0F backlog as it exceeded max size set')
        end
      end

      burst_0x0F_time = client.time()
      amount_recv_0x0F = 0

    end

    return packet
  end
}

-- enough here to make reach disabler for mmc, combat disabler for mineplex, timer disabler for hypixel, combat disabler for mush, and more
module_manager.register('generic-disabler', genericdisabler)
module_manager.register_number('generic-disabler', '0x00-min-delay', 0, 10000, 0) -- the minimum delay of an 0x00 packet
module_manager.register_number('generic-disabler', '0x00-burst-delay', 0, 10000, 0) -- how long 0x00 packets will be held before being sent in burst, minimum delay plus burst delay is max 0x00 delay
module_manager.register_number('generic-disabler', '0x00-send-rate', 0.001, 1.001, 1.001) -- how fast packets will be sent relative to being recerived, a value of 1 means that packets are sent until the delay thresholds are hit, values below 1 mean that packets are sent more slowly than received, causing the packet queues to grow.
module_manager.register_number('generic-disabler', '0x00-min-send-amount', 0, 10, 0) -- max number of backlogged packets before queue is cleared by sending all
module_manager.register_number('generic-disabler', '0x00-max-queue-size', 0, 10000, 10000) -- max number of backlogged packets before queue is cleared by sending all
module_manager.register_number('generic-disabler', '0x00-switch-chance', 0, 100, 0) -- chance to switch the order of any specific 0x00 packet with the next in burst queue, if there is a next packet
module_manager.register_number('generic-disabler', '0x00-burst-rand-chance', 0, 100, 0) -- chance to send a random legitimate 0x00 packet from burst out of order
module_manager.register_number('generic-disabler', '0x00-cancel-chance', 0, 100, 0) -- chance to cancel any specific 0x00 packet sending
module_manager.register_number('generic-disabler', '0x00-double-send-chance', 0, 100, 0) -- chance to double-send a packet
module_manager.register_number('generic-disabler', '0x00-double-switch-chance', 0, 100, 0) -- chance to send packet, send next packet, then send the intial packet again
module_manager.register_number('generic-disabler', '0x00-key-invert-chance', 0, 100, 0) -- chance to invert the 0x00 keep-alive-key
module_manager.register_boolean('generic-disabler', '0x00-key-invert-cancel', false) -- whether original packet should be canceled when inverted key version is sent
module_manager.register_number('generic-disabler', '0x00-key-rand-chance', 0, 100, 0) -- chance to send random key 0x00 with any original 0x00 packet sending
module_manager.register_number('generic-disabler', '0x00-key-rand-min', -10000, 10000, -10000) -- low end of random 0x00 range, for each random packet sent
module_manager.register_number('generic-disabler', '0x00-key-rand-max', -10000, 10000, 10000) -- high end of random 0x00 range, for each random packet sent
module_manager.register_boolean('generic-disabler', '0x00-key-rand-cancel', false) -- whether the original 0x00 packet should be canceled when a random one is sent
module_manager.register_boolean('generic-disabler', '0x00-debug', false) -- print info on 0x00 sent and received

module_manager.register_number('generic-disabler', '0x0F-min-delay', 0, 10000, 0) -- the minimum delay of an 0x0F packet
module_manager.register_number('generic-disabler', '0x0F-burst-delay', 0, 10000, 0) -- how long 0x0F packets will be held before being sent in burst, minimum delay plus burst delay is max 0x0F delay
module_manager.register_number('generic-disabler', '0x0F-min-send-amount', 0, 10, 0) -- max number of backlogged packets before queue is cleared by sending all
module_manager.register_number('generic-disabler', '0x0F-send-rate', 0.001, 1.001, 1.001) -- how fast packets will be sent relative to being recerived, a value of 1 means that packets are sent until the delay thresholds are hit, values below 1 mean that packets are sent more slowly than received, causing the packet queues to grow.
module_manager.register_number('generic-disabler', '0x0F-max-queue-size', 0, 10000, 10000) -- max number of backlogged packets before queue is cleared by sending all
module_manager.register_number('generic-disabler', '0x0F-burst-switch-chance', 0, 100, 0) -- chance to switch the order of any specific 0x0F packet with the next in burst queue, if there is a next packet
module_manager.register_number('generic-disabler', '0x0F-burst-rand-chance', 0, 100, 0) -- chance to send a random legitimate 0x0F packet from burst out of order
module_manager.register_number('generic-disabler', '0x0F-cancel-chance', 0, 100, 0) -- chance to cancel any specific 0x0F packet sending
module_manager.register_number('generic-disabler', '0x0F-double-send-chance', 0, 100, 0) -- chance to double-send a packet
module_manager.register_number('generic-disabler', '0x0F-double-switch-chance', 0, 100, 0) -- chance to send packet, send next packet, then send the intial packet again
module_manager.register_number('generic-disabler', '0x0F-uid-invert-chance', 0, 100, 0) -- chance to invert the 0x0F uid
module_manager.register_boolean('generic-disabler', '0x0F-uid-invert-cancel', false) -- whether original packet should be canceled when inverted key version is sent
module_manager.register_number('generic-disabler', '0x0F-uid-rand-chance', 0, 100, 0) -- chance to send random 0x0F uid with any original 0x0F packet sending
module_manager.register_number('generic-disabler', '0x0F-uid-rand-min', -10000, 10000, -10000) -- low end of random 0x0F range, for each random packet sent
module_manager.register_number('generic-disabler', '0x0F-uid-rand-max', -10000, 10000, 10000) -- high end of random 0x0F range, for each random packet sent
module_manager.register_boolean('generic-disabler', '0x0F-uid-rand-cancel', false) -- whether the original 0x0F packet should be canceled when a random one is sent
module_manager.register_boolean('generic-disabler', '0x0F-debug', false) -- print info on 0x0F sent and received

module_manager.register_boolean('generic-disabler', 'allow-repeat-modify', false) -- whether a packet can be modified by more than one function of the disabler, for example, this would allow it to be inverted after being sent out of order
