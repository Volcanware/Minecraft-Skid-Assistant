--=========================================================
--General use functions. Converted gradient() from js to lua by catch
--=========================================================

local X = 0
local kills = 0
local is_mouse_hovering = function(mouse_x, mouse_y, x1, y1, x2, y2)
	if mouse_x > x1 and mouse_x < x2 and mouse_y > y1 and mouse_y < y2 then
		return true
	else
		return false
	end
end
--imagine no standard number.tofixed() function.
local tofixed = function(s, n)
	local value = ''
	for i = 1, n do
		value = value .. string.char(string.byte(s, i))
	end
	return value
end

local gradient = function(stops, render)
	local greatest = 1
	local lowest = 0
	if render < 0 then
		return stops[0][1], stops[0][2], stops[0][3]
	end
	if render > 1 then
		return stops[1][1], stops[1][2], stops[1][3]
	end
	for i, v in pairs(stops) do
		local at = i
		if render < at and at < greatest then
			greatest = at
		elseif render > at and at > lowest then
			lowest = at
		end
		if at == render then
			return stops[at][1], stops[at][2], stops[at][3]
		end
	end
	local start = stops[lowest]
	local final = stops[greatest]
	local width = greatest - lowest
	local offset = (render - lowest) / width
	local percent_of_start = 1 - offset
	local percent_of_final = math.min(1, offset)
	local r = math.min(255, (start[1] * percent_of_start) + (final[1] * percent_of_final))
	local g = math.min(255, (start[2] * percent_of_start) + (final[2] * percent_of_final))
	local b = math.min(255, (start[3] * percent_of_start) + (final[3] * percent_of_final))
	return r, g, b
end
--current unused function. will likely use it in the future
local draw_gradient_rect = function(x, y, width, height, color, options)
	local rect = {
		x = x,
		y = y,
		width = width,
		height = height
	}
	local x1 = rect.x
	local y1 = rect.y
	local x2 = rect.x + rect.width
	local y2 = rect.y + rect.height
	local width = x2 - x1
	local height = y2 - x1
	local box_size = 1
	local cos = math.cos(options.angle)
	local sin = math.sin(options.angle)
	local alpha = options.alpha
	for x = 0, width do
		for y = 0, height do
			local px = x / width
			local py = y / height
			local pt = px * cos + py + sin + options.offset
			local r, g, b = gradient(color, pt % 1)
			render.rect(x1 + x, y1 + y, x1 + x + 1, y1 + y + 1, r, g, b, options.alpha)
		end
	end
end
--============================
--Actual script
--============================
local prev = {
	x1 = nil,
	y1 = nil,
	z1 = nil
}
local keystrokes_frames = {
	w = 95,
	a = 95,
	s = 95,
	d = 95,
	space = 95,
	lmb = 95,
	rmb = 95
}
local bps = 0
--gradients
local gradients = {
	Rainbow = {
		[0] = {245, 66, 66},
		[0.16666666666] = {212, 245, 66},
		[0.33333333333] = {66, 245, 66},
		[0.5] = {66, 245, 245},
		[0.66666666666] = {66, 66, 245},
		[0.83333333] = {245, 66, 242},
		[1] = {245, 66, 66}
	},
	Calm = {
		[0] = {28, 176, 248},
		[0.166666666666] = {0, 193, 254},
		[0.333333333333] = {0, 207, 236},
		[0.5] = {0, 218, 197},
		[0.666666666666] = {0, 225, 139},
		[0.833333333333] = {56, 228, 65},
		[1] = {28, 178, 248}
	},
	Astolfo = {
		[0] = {50, 220, 255},
		[0.3] = {189, 40, 252},
		[0.5] = {255, 128, 162},
		[0.6] = {189, 40, 252},
		[1] = {50, 220, 255}
	},
	Instagram = {
		[0] = {100, 44, 242},
		[0.25] = {253, 29, 96},
		[0.5] = {252, 176, 69},
		[0.75] = {253, 29, 96},
		[1] = {100, 44, 242}
	},
	TikTok = {
		[0] = {252, 4, 4},
		[0.25] = {0, 0, 0},
		[0.5] = {0, 212, 255},
		[0.75] = {0, 0, 0},
		[1] = {252, 4, 4}
	}
}
--elements
local username = {
	id = 'username',
	text = function()
		return player.name()
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 6,
	anchor_y = 132,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(player.name()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local coordinates = {
	id = 'coordinates',
	text = function()
		local x, y, z = player.position()
		return math.floor(x) .. ', ' .. math.floor(y) .. ', ' .. math.floor(z)
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 6,
	anchor_y = 156,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(self.text()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local fps = {
	id = 'fps',
	text = function()
		local fps = client.fps()
		return 'FPS: ' .. fps
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 78,
	anchor_y = 6,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(self.text()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local speed = {
	id = 'bps',
	text = function()
		local str = tofixed(bps, 4)
		return str .. ' blocks/sec'
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 6,
	anchor_y = 180,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(self.text()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local config = {
	id = 'config',
	text = function()
		return client.config()
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 126,
	anchor_y = 6,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(self.text()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local kill_count = {
	id = 'kill_count',
	text = function()
		return kills
	end,
	text_offset_x = 5,
	text_offset_y = 5,
	anchor_x = 126,
	anchor_y = 27,
	x2 = function(self)
		return self.anchor_x + render.get_string_width(self.text()) + 10
	end,
	y2 = function(self)
		return self.anchor_y + 18
	end,
	r = function()
		return 5
	end,
	g = function()
		return 5
	end,
	b = function()
		return 5
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		return 255
	end,
	text_g = function()
		return 255
	end,
	text_b = function()
		return 255
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1
}
local keystrokes_w = {
	id = 'keystrokes_w',
	text = function()
		return 'W'
	end,
	text_offset_x = 7,
	text_offset_y = 6,
	anchor_x = 42,
	anchor_y = 18,
	x2 = function(self)
		return self.anchor_x + 6 + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_key_down(17) then
			if keystrokes_frames.w > 0 then
				return 160 + keystrokes_frames.w
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_key_down(17) then
			if keystrokes_frames.w > 0 then
				return 160 + keystrokes_frames.w
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_key_down(17) then
			if keystrokes_frames.w > 0 then
				keystrokes_frames.w = keystrokes_frames.w - 3
				return 160 + keystrokes_frames.w + 3
			else
				return 160
			end
		else
			keystrokes_frames.w = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_key_down(17) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_key_down(17) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_key_down(17) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.5
}
local keystrokes_a = {
	id = 'keystrokes_a',
	text = function()
		return 'A'
	end,
	text_offset_x = 7,
	text_offset_y = 6,
	anchor_x = 18,
	anchor_y = 42,
	x2 = function(self)
		return self.anchor_x + 6 + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_key_down(30) then
			if keystrokes_frames.a > 0 then
				return 160 + keystrokes_frames.a
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_key_down(30) then
			if keystrokes_frames.a > 0 then
				return 160 + keystrokes_frames.a
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_key_down(30) then
			if keystrokes_frames.a > 0 then
				keystrokes_frames.a = keystrokes_frames.a - 3
				return 160 + keystrokes_frames.a + 3
			else
				return 160
			end
		else
			keystrokes_frames.a = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_key_down(30) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_key_down(30) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_key_down(30) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.5
}
local keystrokes_s = {
	id = 'keystrokes_s',
	text = function()
		return 'S'
	end,
	text_offset_x = 7,
	text_offset_y = 6,
	anchor_x = 42,
	anchor_y = 42,
	x2 = function(self)
		return self.anchor_x + 6 + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_key_down(31) then
			if keystrokes_frames.s > 0 then
				return 160 + keystrokes_frames.s
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_key_down(31) then
			if keystrokes_frames.s > 0 then
				return 160 + keystrokes_frames.s
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_key_down(31) then
			if keystrokes_frames.s > 0 then
				keystrokes_frames.s = keystrokes_frames.s - 3
				return 160 + keystrokes_frames.s + 3
			else
				return 160
			end
		else
			keystrokes_frames.s = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_key_down(31) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_key_down(31) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_key_down(31) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.5
}
local keystrokes_d = {
	id = 'keystrokes_d',
	text = function()
		return 'D'
	end,
	text_offset_x = 7,
	text_offset_y = 6,
	anchor_x = 66,
	anchor_y = 42,
	x2 = function(self)
		return self.anchor_x + 6 + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_key_down(32) then
			if keystrokes_frames.d > 0 then
				return 160 + keystrokes_frames.d
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_key_down(32) then
			if keystrokes_frames.d > 0 then
				return 160 + keystrokes_frames.d
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_key_down(32) then
			if keystrokes_frames.d > 0 then
				keystrokes_frames.d = keystrokes_frames.d - 3
				return 160 + keystrokes_frames.d + 3
			else
				return 160
			end
		else
			keystrokes_frames.d = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_key_down(32) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_key_down(32) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_key_down(32) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.5
}
local keystrokes_lmb = {
	id = 'keystrokes_lmb',
	text = function()
		return 'LMB'
	end,
	text_offset_x = 7,
	text_offset_y = 4,
	anchor_x = 18,
	anchor_y = 66,
	x2 = function(self)
		return self.anchor_x + render.get_string_width('LMB') + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_mouse_down(0) then
			if keystrokes_frames.lmb > 0 then
				return 160 + keystrokes_frames.lmb
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_mouse_down(0) then
			if keystrokes_frames.lmb > 0 then
				return 160 + keystrokes_frames.lmb
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_mouse_down(0) then
			if keystrokes_frames.lmb > 0 then
				keystrokes_frames.lmb = keystrokes_frames.lmb - 3
				return 160 + keystrokes_frames.lmb + 3
			else
				return 160
			end
		else
			keystrokes_frames.lmb = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_mouse_down(0) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_mouse_down(0) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_mouse_down(0) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.2
}
local keystrokes_rmb = {
	id = 'keystrokes_rmb',
	text = function()
		return 'RMB'
	end,
	text_offset_x = 7,
	text_offset_y = 4,
	anchor_x = 54,
	anchor_y = 66,
	x2 = function(self)
		return self.anchor_x + render.get_string_width('RMB') + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_mouse_down(1) then
			if keystrokes_frames.rmb > 0 then
				return 160 + keystrokes_frames.rmb
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_mouse_down(1) then
			if keystrokes_frames.rmb > 0 then
				return 160 + keystrokes_frames.rmb
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_mouse_down(1) then
			if keystrokes_frames.rmb > 0 then
				keystrokes_frames.rmb = keystrokes_frames.rmb - 3
				return 160 + keystrokes_frames.rmb + 3
			else
				return 160
			end
		else
			keystrokes_frames.rmb = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_mouse_down(1) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_mouse_down(1) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_mouse_down(1) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.2
}
local keystrokes_space = {
	id = 'keystrokes_space',
	text = function()
		return 'SPACE'
	end,
	text_offset_x = 19,
	text_offset_y = 4,
	anchor_x = 18,
	anchor_y = 86,
	x2 = function(self)
		return self.anchor_x + render.get_string_width('SPACE') + (self.text_offset_x * 2)
	end,
	y2 = function(self)
		return self.anchor_y + 9 + (self.text_offset_y * 2)
	end,
	r = function()
		if input.is_key_down(57) then
			if keystrokes_frames.space > 0 then
				return 160 + keystrokes_frames.space
			else
				return 160
			end
		else
			return 40
		end
	end,
	g = function()
		if input.is_key_down(57) then
			if keystrokes_frames.space > 0 then
				return 160 + keystrokes_frames.space
			else
				return 160
			end
		else
			return 40
		end
	end,
	b = function()
		if input.is_key_down(57) then
			if keystrokes_frames.space > 0 then
				keystrokes_frames.space = keystrokes_frames.space - 3
				return 160 + keystrokes_frames.space + 3
			else
				return 160
			end
		else
			keystrokes_frames.space = 95
			return 40
		end
	end,
	toggled = true,
	has_text = true,
	text_r = function()
		if input.is_key_down(57) then
			return 5
		else
			return 255
		end
	end,
	text_g = function()
		if input.is_key_down(57) then
			return 5
		else
			return 255
		end
	end,
	text_b = function()
		if input.is_key_down(57) then
			return 5
		else
			return 255
		end
	end,
	text_a = function()
		return 255
	end,
	grabbed = false,
	text_scale_factor = 1.2
}
--register elements
local elements = {
	username,
	coordinates,
	fps,
	speed,
	config,
	keystrokes_w,
	keystrokes_a,
	keystrokes_s,
	keystrokes_d,
	keystrokes_lmb,
	keystrokes_rmb,
	keystrokes_space,
	kill_count
}
--===================================
--Modules
--===================================
local was_enabled = false
local x_offset = 0
local y_offset = 0
local options = {
	alpha = 255,
	angle = 0,
	offset = 0,
	get_gradient = function()
		local g = ''
		for i, v in pairs(gradients) do
			g = i
			if module_manager.option('CustomHud', i) then
				break
			end
		end
		return g
	end
}
local gui = {
	on_render_screen = function(data)
		X = X + (data.partial_ticks / 350)
		X = X % 1
		local rG, gG, bG = gradient(gradients[options.get_gradient()], X)
		for i, k in pairs(elements) do
			if is_mouse_hovering(data.mouse_x, data.mouse_y, k.anchor_x, k.anchor_y, k:x2(), k:y2()) then
				render.rect(k.anchor_x - 1, k.anchor_y - 3, k:x2() - 1, k:y2() - 3, k.r() + 25, k.g() + 25, k.b() + 25, 230)
				if k.toggled then
					render.line(k.anchor_x - 1, k.anchor_y - 3, k:x2() - 1, k.anchor_y - 3, 2, rG, gG, bG, 255) -- top
					render.line(k.anchor_x - 1, k.anchor_y - 3, k.anchor_x - 1, k:y2() - 3, 2, rG, gG, bG, 255) -- left
					render.line(k:x2() - 1, k.anchor_y - 3, k:x2() - 1, k:y2() - 3, 2, rG, gG, bG, 255) --right
					render.line(k.anchor_x - 1, k:y2() - 3, k:x2() - 1, k:y2() - 3, 2, rG, gG, bG, 255) --bottom
				end
				if k.has_text then
					render.scale(k.text_scale_factor)
					render.string(
						k.text(),
						(k.anchor_x + k.text_offset_x) / k.text_scale_factor - 1 / k.text_scale_factor,
						(k.anchor_y + k.text_offset_y) / k.text_scale_factor - 3 / k.text_scale_factor,
						k.text_r(),
						k.text_g(),
						k.text_b(),
						k.text_a()
					)
					render.scale(1 / k.text_scale_factor)
				end
			else
				render.rect(k.anchor_x, k.anchor_y, k:x2(), k:y2(), k.r(), k.g(), k.b(), 230)
				if k.toggled then
					render.line(k.anchor_x, k.anchor_y, k:x2(), k.anchor_y, 2, rG, gG, bG, 255) -- top
					render.line(k.anchor_x, k.anchor_y, k.anchor_x, k:y2(), 2, rG, gG, bG, 255) -- left
					render.line(k:x2(), k.anchor_y, k:x2(), k:y2(), 2, rG, gG, bG, 255) --right
					render.line(k.anchor_x, k:y2(), k:x2(), k:y2(), 2, rG, gG, bG, 255) --bottom
				end
				if k.has_text then
					render.scale(k.text_scale_factor)
					render.string(
						k.text(),
						(k.anchor_x + k.text_offset_x) / k.text_scale_factor,
						(k.anchor_y + k.text_offset_y) / k.text_scale_factor,
						k.text_r(),
						k.text_g(),
						k.text_b(),
						k.text_a()
					)
					render.scale(1 / k.text_scale_factor)
				end
			end
		end
	end,
	on_click = function(data)
		for i, k in pairs(elements) do
			if is_mouse_hovering(data.mouse_x, data.mouse_y, k.anchor_x, k.anchor_y, k:x2(), k:y2()) and data.mouse_button == 0 then
				if k.toggled then
					k.toggled = false
				else
					k.toggled = true
				end
			end
			if is_mouse_hovering(data.mouse_x, data.mouse_y, k.anchor_x, k.anchor_y, k:x2(), k:y2()) then
				k.grabbed = true
				x_offset = data.mouse_x - k.anchor_x
				y_offset = data.mouse_y - k.anchor_y
			end
		end
	end,
	on_drag_click = function(data)
		for i, k in pairs(elements) do
			if k.grabbed then
				local x = data.mouse_x - x_offset
				local y = data.mouse_y - y_offset
				if (x % 6 < 2 or y % 6 < 2) and module_manager.option('HudEditor', 'Snapping') then
					k.anchor_x = x - x % 6
					k.anchor_y = y - y % 6
				else
					k.anchor_x = data.mouse_x - x_offset
					k.anchor_y = data.mouse_y - y_offset
				end
			end
		end
	end,
	on_mouse_released = function()
		for i, k in pairs(elements) do
			if k.grabbed then
				k.grabbed = false
			end
		end
	end,
	on_gui_close = function()
		player.message('.hudeditor')
		if was_enabled then
			player.message('.CustomHud')
		end
	end
}
--editor module
local editor = {
	on_enable = function()
		render.show_gui(gui)
		if module_manager.is_module_on('CustomHud') then
			player.message('.CustomHud')
			was_enabled = true
		end
	end
}

--main
local main = {
	on_render_screen = function(data)
		X = X + (data.partial_ticks / 350)
		X = X % 1
		local rG, gG, bG = gradient(gradients[options.get_gradient()], X)
		for i, k in pairs(elements) do
			if k.toggled then
				--main box
				render.rect(k.anchor_x, k.anchor_y, k:x2(), k:y2(), k.r(), k.g(), k.b(), 230)
				--border
				render.line(k.anchor_x, k.anchor_y, k:x2(), k.anchor_y, 2, rG, gG, bG, 255) -- top
				render.line(k.anchor_x, k.anchor_y, k.anchor_x, k:y2(), 2, rG, gG, bG, 255) -- left
				render.line(k:x2(), k.anchor_y, k:x2(), k:y2(), 2, rG, gG, bG, 255) --right
				render.line(k.anchor_x, k:y2(), k:x2(), k:y2(), 2, rG, gG, bG, 255) --bottom
				if k.has_text then
					render.scale(k.text_scale_factor)
					render.string(
						k.text(),
						(k.anchor_x + k.text_offset_x) / k.text_scale_factor,
						(k.anchor_y + k.text_offset_y) / k.text_scale_factor,
						k.text_r(),
						k.text_g(),
						k.text_b(),
						k.text_a()
					)
					render.scale(1 / k.text_scale_factor)
				end
			end
		end
	end,
	on_pre_update = function()
		local x, y, z = player.position()
		prev.x1 = x
		prev.y1 = y
		prev.z1 = z
	end,
	on_post_update = function()
		local x, y, z = player.position()
		bps = math.sqrt(math.abs(x - prev.x1) ^ 2 + math.abs(z - prev.z1) ^ 2) / 0.05
	end,
	on_send_packet = function(packet)
		if packet.message then
			if packet.message:match('!anchors') then
				local anchors = ''
				for i, e in pairs(elements) do
					anchors = anchors .. e.id .. ':(' .. e.anchor_x .. ',' .. e.anchor_y .. '), '
				end
				client.print(anchors)
				packet.cancel = true
			end
			if packet.message:match('!fix keystrokes') then
				packet.cancel = true
				local x = keystrokes_w.anchor_x
				local y = keystrokes_w.anchor_y
				keystrokes_a.anchor_x = x - 24
				keystrokes_a.anchor_y = y + 24
				keystrokes_s.anchor_x = x
				keystrokes_s.anchor_y = y + 24
				keystrokes_d.anchor_x = x + 24
				keystrokes_d.anchor_y = y + 24
				keystrokes_lmb.anchor_x = x - 24
				keystrokes_lmb.anchor_y = y + 48
				keystrokes_rmb.anchor_x = x + 12
				keystrokes_rmb.anchor_y = y + 48
				keystrokes_space.anchor_x = x - 24
				keystrokes_space.anchor_y = y + 68
			end
		end
		return packet
	end,
	on_receive_packet = function(packet)
		if packet.message ~= nil then
			local stripped = string.gsub(packet.message, '(\194\167%w)', '')
			if string.match(stripped, 'Kill') and string.gmatch(stripped, '1 Skywars Experience') then
				kills = kills + 1
			end
		end
	end
}
module_manager.register('HudEditor', editor)
module_manager.register('CustomHud', main)
module_manager.register_boolean('CustomHud', 'Astolfo', true)
module_manager.register_boolean('CustomHud', 'Calm', false)
module_manager.register_boolean('CustomHud', 'Rainbow', false)
module_manager.register_boolean('CustomHud', 'Instagram', false)
module_manager.register_boolean('CustomHud', 'TikTok', false)
module_manager.register_boolean('HudEditor', 'Snapping', true)
