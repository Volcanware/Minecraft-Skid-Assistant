local debugger = {
    on_send_packet = function(packet)
      if packet.packet_id == 0x0F and packet.uid then
        client.print('C0F - '..tostring(packet.uid)..' | window - '..tostring(packet.window_id)..' | accept - '..tostring(packet.accept)..' | cancel - '..tostring(packet.cancel))
      elseif packet.packet_id == 0x00 then
        client.print('C00 - '..tostring(packet.packet_id)..' | keepalive - '..tostring(packet.keep_alive_key))
      end
  end
  }
  
  module_manager.register('debugger', debugger)