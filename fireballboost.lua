local v_delay, h_delay = -1, -1
local m_x, m_y, m_z
local l_m_x, l_m_y, l_m_z

local fireballboost = {
  on_pre_update = function()
    m_x, m_y, m_z = player.motion()

    if m_y > 0.7 and m_y > l_m_y then
      v_delay = module_manager.option('fireballboost', 'v-boost-ticks')
      h_delay = module_manager.option('fireballboost', 'h-boost-ticks')
    end

    if v_delay >= 0 then
      m_y = module_manager.option('fireballboost', 'v-boost')
    end

    if h_delay >= 0 then
      local yaw, pitch = player.angles()
      m_x = -1 * math.sin(yaw * math.pi/180) * module_manager.option('fireballboost', 'h-boost')
      m_z = math.cos(yaw * math.pi/180) * module_manager.option('fireballboost', 'h-boost')
    end

    player.set_motion(m_x, m_y, m_z)

    l_m_x, l_m_y, l_m_z = player.motion()
    v_delay = v_delay - 1
    h_delay = h_delay - 1
  end
}

module_manager.register('fireballboost', fireballboost)
module_manager.register_number('fireballboost', 'v-boost', 0.01, 5.01, 3.71)
module_manager.register_number('fireballboost', 'v-boost-ticks', 1, 20, 7)
module_manager.register_number('fireballboost', 'h-boost', 0.01, 5.01, 1.51)
module_manager.register_number('fireballboost', 'h-boost-ticks', 1, 20, 10)
