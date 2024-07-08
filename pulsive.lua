local renderBatch = render.batched.render_batch
local rectType = enums.template_type.RECT
local roundedRectType = enums.template_type.ROUNDED_RECT
local textType = enums.template_type.TEXT

local kills = 0
local wins = 0
local prev_player = ""
local time = 0
local times = {
    seconds = 0,
    minutes = 0,
    hours = 0
}

local minutes = 0
local hours = 0

local winMessages = {"You won!"}

function reset()
    time = client.time()
    prev_player = player.get_name()
    kills = 0
    wins = 0;
end

function gradient_color(color1, color2, delay, cycleTime, alpha)
    local fraction = (math.sin(2 * math.pi * (client.time() + delay) / cycleTime) + 1) / 2
    fraction = math.max(0, math.min(1, fraction))

    local r1 = math.floor((color1 / 65536) % 256)
    local g1 = math.floor((color1 / 256) % 256)
    local b1 = math.floor(color1 % 256)

    local r2 = math.floor((color2 / 65536) % 256)
    local g2 = math.floor((color2 / 256) % 256)
    local b2 = math.floor(color2 % 256)

    local interpolatedColor = (math.floor(r1 + fraction * (r2 - r1) + 0.5) * 65536) +
                            (math.floor(g1 + fraction * (g2 - g1) + 0.5) * 256) +
                            math.floor(b1 + fraction * (b2 - b1) + 0.5)

    local alpha = 255 * alpha

    return interpolatedColor + alpha * 256 * 256 * 256
end

function drawFrame(title, x, y, width, height)
    -- Frame and transparent top
    renderBatch(
        roundedRectType,
        {
            types.template.rounded_rect.new(x, y - 10, width, 15, 3, 0x60000000),
            types.template.rounded_rect.new(x, y, width, height, 3, 0xFF1F1F1F)
        }
    )

    -- No rounding on top corners
    renderBatch(
        rectType,
        {
            types.template.rect.new(x + 0.5, y, width - 1, 5, 0xFF1F1F1F)
        }
    )

    -- Text
    local titleX = x + (width - font.width(enums.custom_font_type.VOLTE_SEMI_BOLD, 7, title)) / 2
    renderBatch(
        textType,
        {
            types.template.text.new(enums.custom_font_type.VOLTE_SEMI_BOLD, title, 7, titleX, y - 9, 0xFFFFFFFF, false)
        }
    )
end

function drawEffectFrame(x, y, width, height)
    renderBatch(
        roundedRectType,
        {
            types.template.rounded_rect.new(x, y - 10, width, height + 10, 3, 0xFF000000)
        }
    )
end

local head = { {0xFF2F200D, 0xFF2B1E0D, 0xFF2F1F0F, 0xFF281C0B, 0xFF241808, 0xFF261A0A, 0xFF2B1E0D, 0xFF2A1D0D}, {0xFF2B1E0D, 0xFF2B1E0D, 0xFF2B1E0D, 0xFF332411, 0xFF422A12, 0xFF3F2A15, 0xFF2C1E0E, 0xFF281C0B}, {0xFF2B1E0D, 0xFFB6896C, 0xFFBD8E72, 0xFFC69680, 0xFFBD8B72, 0xFFBD8E74, 0xFFAC765A, 0xFF342512}, {0xFFAA7D66, 0xFFB4846D, 0xFFAA7D66, 0xFFAD806D, 0xFF9C725C, 0xFFBB8972, 0xFF9C694C, 0xFF9C694C}, {0xFFB4846D, 0xFFFFFFFF, 0xFF2521AB, 0xFFB57B67, 0xFFBB8972, 0xFF2521AB, 0xFFFFFFFF, 0xFFAA7D66}, {0xFF9C6346, 0xFFB37B62, 0xFFB78272, 0xFF6A4030, 0xFF6A4030, 0xFFBE886C, 0xFFA26A47, 0xFF805334}, {0xFF905E43, 0xFF965F40, 0xFF41210C, 0xFF8A4C3D, 0xFF8A4C3D, 0xFF45220E, 0xFF8F5E3E, 0xFF815339}, {0xFF6F452C, 0xFF6D432A, 0xFF41210C, 0xFF421D0A, 0xFF45220E, 0xFF45220E, 0xFF83553B, 0xFF7A4E33} }

function renderImage(centerX, centerY, colors, scale, resolution)
    local renderBatch = render.batched.render_batch
    local templateType = enums.template_type.RECT
    local templateRect = types.template.rect.new

    local batch = {}
    local rectangleCount = 0

    local startX = centerX - scale * (resolution / 2)
    local startY = centerY - scale * (resolution / 2)

    for j, col in ipairs(colors) do
        local startRectIndex = 1
        local prevColor = col[startRectIndex]

        for i = 2, resolution do
            if col[i] ~= prevColor then
                if prevColor ~= 0x000000 then
                    local x = startX + (startRectIndex - 1) * scale
                    local y = startY + (j - 1) * scale
                    local width = (i - startRectIndex) * scale
                    local rect = templateRect(x, y, width, scale, prevColor)
                    table.insert(batch, rect)
                    rectangleCount = rectangleCount + 1
                end
                startRectIndex = i
                prevColor = col[i]
            end
        end

        if prevColor ~= 0x000000 then
            local x = startX + (startRectIndex - 1) * scale
            local y = startY + (j - 1) * scale
            local width = (resolution - startRectIndex + 1) * scale
            local rect = templateRect(x, y, width, scale, prevColor)
            table.insert(batch, rect)
            rectangleCount = rectangleCount + 1
        end
    end

    renderBatch(templateType, batch)
end

function round(num)
    return math.floor(num + 0.5)
end

local function lerp(min, max, fraction)
    return (max - min) * fraction + min
end

local currentWidth = 0

function drawTargetHud(target, effect)
    local x = modules.get_setting("pulsive", "target-x")
    local y = modules.get_setting("pulsive", "target-y")

    local health = world.get_health(target)
    local roundHealth = round(health)
    local maxHealth = world.get_max_health(target)
    local name 

    if health ~= 0 then
        name = world.get_name(target)
    end

    local headScale = 2.75
    local imageSize = 8 * headScale
    local halfImageSize = imageSize / 2
    local padding = 3.5

    local headX = x + halfImageSize + padding
    local headY = y + halfImageSize + padding

    if name ~= nil and health ~= nil then

        local height = imageSize + (padding * 3) + 7
        local nameWidth = font.width(enums.custom_font_type.INTER_MEDIUM, 10, name)
        local healthWidth = font.width(enums.custom_font_type.TAHOMA, 7.5, "Health: " .. tostring(roundHealth))
        local textWidth = math.max(nameWidth, healthWidth)
        local width = round(imageSize + textWidth + (padding * 3) + 15)

        local healthBarWidth = (width - padding * 2 + 1) * (health / maxHealth)
        local healthBarX = x + padding - 0.5
        local healthBarY = headY + halfImageSize + padding

        local textX = headX + halfImageSize + padding

        local color1 = modules.get_setting("pulsive", "primary-color")
        local color2 = modules.get_setting("pulsive", "secondary-color")

        local fadeTime = modules.get_setting("pulsive", "time")

        if not effect then
            currentWidth = lerp(currentWidth, healthBarWidth, 0.04)

            if tostring(currentWidth) == "nan" then
                currentWidth = 0
            end

            drawFrame("target hud", x, y, width, height)

            renderImage(headX, headY, head, headScale, 8)

            renderBatch(
                roundedRectType,
                {
                    types.template.rounded_rect.new(healthBarX, healthBarY, currentWidth, 7, 2.5, gradient_color(color1, color2, -20, fadeTime, 1))
                }
            )

            renderBatch(
                textType,
                {
                    types.template.text.new(enums.custom_font_type.INTER_MEDIUM, name, 10, textX, y + padding + 1, 0xFFFFFFFF, false)
                }
            )

            renderBatch(
                textType,
                {
                    types.template.text.new(enums.custom_font_type.TAHOMA, "Health: " .. tostring(roundHealth), 7.5, textX, y + padding + 13, 0xFF656565, false)
                }
            )
        else
            drawEffectFrame(x, y, width, height)
        end
    end
end

function drawSessionInfo(effect)
    local x = modules.get_setting("pulsive", "session-x")
    local y = modules.get_setting("pulsive", "session-y")

    local timeString = times.hours .. "h " .. times.minutes .. "m " .. times.seconds .. "s"
    local killString = "You have gotten " .. kills .. " kills"
    local winsString = "Games won " .. wins .. " times"

    local width = font.width(enums.custom_font_type.TAHOMA, 7.5, killString) + 50
    local height = font.height(enums.custom_font_type.INTER_MEDIUM, 12, timeString) + font.height(enums.custom_font_type.TAHOMA, 7.5, "W") * 2 + 18

    if not effect then
        drawFrame("session information", x, y, width, height)

        renderBatch(
            textType,
            {
                types.template.text.new(enums.custom_font_type.INTER_MEDIUM, timeString, 12, x + 9, y + 8, 0xFFFFFFFF, false)
            }
        )

        renderBatch(
            textType,
            {
                types.template.text.new(enums.custom_font_type.TAHOMA, killString, 7.5, x + 9, y + 21, 0xFF656565, false),
                types.template.text.new(enums.custom_font_type.TAHOMA, winsString, 7.5, x + 9, y + 30, 0xFF656565, false)
            }
        )
    else
        drawEffectFrame(x, y, width, height)
    end
end

local function calcshift(locx, locz, distSq)
    local localX = player.get_position_x()
    local localZ = player.get_position_z()

    local x = locx - localX
    local z = locz - localZ

    local calc = math.atan2(x, z) * 57.2957795131
    local angle = (player.get_yaw() + calc) * 0.01745329251

    local hypotenuse = distSq 
    local xpos = -hypotenuse * math.sin(angle)
    local ypos = -hypotenuse * math.cos(angle)
    return xpos, ypos
end


local playerpos = {}

function drawRadar(effect)
    local width = 130
    local height = 80

    local x = modules.get_setting("pulsive", "radar-x")
    local y = modules.get_setting("pulsive", "radar-y")

    if not effect then
        local players = world.get_players()
        playerpos = {} 

        for i = 1, #players do
            local distance = player.distance_to_entity(players[i])
            if distance ~= 0 then
                dist = math.sqrt(distance) 
                local xpos, ypos = calcshift(world.get_position_x(players[i]), world.get_position_z(players[i]), dist)
                table.insert(playerpos, {x = xpos, y = ypos, prevx = 0, prevy = 0})
            end
        end

        local radarX = x + 2
        local radarY = y + 2
        local radarHeight = 72
        local radarWidth = 86
        local playerSize = 4

        local endWidth = width - radarWidth - 2

        drawFrame("radar", x, y, width, height)

        local playerCount = 0

        local logoWidth = 20
        local ringWidth = 17
        local ringWidth2 = 14
        local ringWidth3 = 11
        local centerWidth = 8

        render.batched.render_batch(

            enums.template_type.ROUNDED_RECT,
            {
                types.template.rounded_rect.new(radarX + radarWidth / 2 - playerSize / 2, radarY + radarHeight / 2 - playerSize / 2, 4, 4, 1, 0xFFFFFFFF)
            }
        )

        local circles = {
            { diameter = 20, color = 0xFF656565 }, -- Outer circle
            { diameter = 17, color = 0xFF1F1F1F }, -- Middle circle
            { diameter = 14, color = 0xFF656565 }, -- Inner circle
            { diameter = 11, color = 0xFF1F1F1F }, -- Fourth circle
            { diameter = 8, color = 0xFF656565 } -- Fifth circle
        }
        
        local circleBatch = {}
        
        local startX = radarX + radarWidth + 2
        local startY = y + height - 32
        local spacing = 2
        local yOffset = 0
        
        for i, circle in ipairs(circles) do
            local x = startX + (endWidth / 2 - circle.diameter / 2)
            local y = startY + yOffset
            local radius = circle.diameter / 2 - 1
            table.insert(circleBatch, types.template.rounded_rect.new(x, y, circle.diameter, circle.diameter, radius, circle.color))
            yOffset = yOffset + 1.5
        end
        
        render.batched.render_batch(enums.template_type.ROUNDED_RECT, circleBatch)

        for _, pos in pairs(playerpos) do
            if math.abs(pos.x) <= radarWidth / 2 and math.abs(pos.y) <= radarHeight / 2 then
                
                local px = radarX + (radarWidth / 2) + pos.x - (playerSize / 2)
                local py = radarY + (radarHeight / 2) + pos.y - (playerSize / 2)
                
                render.batched.render_batch(

                    enums.template_type.ROUNDED_RECT,
                    {
                        types.template.rounded_rect.new(px, py, 4, 4, 1, 0xFF909090)
                    }
                )

                playerCount = playerCount + 1
            end
        end

        local countString = tostring(playerCount)
        local countWidth = font.width(enums.custom_font_type.INTER_MEDIUM, 12, countString)
        local playerWidth = font.width(enums.custom_font_type.TAHOMA, 7.5, "People")

        renderBatch(
            textType,
            {
                types.template.text.new(enums.custom_font_type.INTER_MEDIUM, countString, 12, radarX + radarWidth + 2 + (endWidth / 2 - countWidth / 2), radarY + 10, 0xFFFFFFFF, false)
            }
        )

        renderBatch(
            textType,
            {
                types.template.text.new(enums.custom_font_type.TAHOMA, "People", 7.5, radarX + radarWidth + 2 + (endWidth / 2 - playerWidth / 2), radarY + 24, 0xFF656565, false)
            }
        )
    else
        drawEffectFrame(x, y, width, height)
    end
end

local minutes = 0
local hours = 0
function padZero(number)
    if number < 10 then
        return "0" .. number
    else
        return tostring(number)
    end
end

function drawWatermark(effect)
    local x = modules.get_setting("pulsive", "watermark-x")
    local y = modules.get_setting("pulsive", "watermark-y")

    local name = player.get_name()
    local fps = client.fps()

    local renderString = "PL    " .. name .. "    " .. fps .. " fps    " .. padZero(hours) .. ":" .. padZero(minutes)
    local stringWidth = font.width(enums.custom_font_type.VOLTE_SEMI_BOLD, 8, renderString)
    local stringHeight = font.height(enums.custom_font_type.VOLTE_SEMI_BOLD, 8, renderString)

    local nameWidth = font.width(enums.custom_font_type.VOLTE_SEMI_BOLD, 8, "PL  ")
    local userWidth = nameWidth + font.width(enums.custom_font_type.VOLTE_SEMI_BOLD, 8, "  " .. name .. "  ")
    local fpsWidth = userWidth + font.width(enums.custom_font_type.VOLTE_SEMI_BOLD, 8, "  " .. fps .. " fps  ")

    if not effect then
        render.batched.render_batch(enums.template_type.ROUNDED_RECT , {
            types.template.rounded_rect.new(x, y, stringWidth + 10, stringHeight + 5, 3, 0xFF1F1F1F),
        })

        render.batched.render_batch(enums.template_type.RECT , {
            types.template.rect.new(x + 4.5 + nameWidth, y + 3.5, 1, stringHeight - 1.5, 0xFF656565),
            types.template.rect.new(x + 4.5 + userWidth, y + 3.5, 1, stringHeight - 1.5, 0xFF656565),
            types.template.rect.new(x + 4.5 + fpsWidth, y + 3.5, 1, stringHeight - 1.5, 0xFF656565),
        })
    
        render.batched.render_batch(
            enums.template_type.TEXT,
            {
                types.template.text.new(enums.custom_font_type.VOLTE_SEMI_BOLD , renderString, 8, x + 5 , y + 2.5, 0xFFFFFFFF, false),
            }
        )
    else
        render.batched.render_batch(enums.template_type.ROUNDED_RECT , {
            types.template.rounded_rect.new(x, y, stringWidth + 10, stringHeight + 5, 3, 0xFF000000),
        })
    end
end

modules.register("pulsive", "Pulsive HUD", "Pulsive 5.0 HUD Recreation", {
    on_update = function ()
        if player.get_name() ~= prev_player then
            reset()
        end
    
        local diff = client.time() - time
        if diff > 0 then
            times.seconds = math.floor((diff / 1000) % 60)
            times.minutes = math.floor((diff / (60 * 1000)) % 60)
            times.hours = math.floor((diff / (60 * 60 * 1000)))
        end

        local time = client.time()
        minutes = math.floor((time / (60 * 1000)) % 60)
        hours = math.floor(time / (60 * 60 * 1000) % 24)
    end,

    on_overlay_render = function(e)
        if e.pre then
            if client.current_kill_aura_target() ~= -1 and modules.get_setting("pulsive", "target-hud") then
                drawTargetHud(client.current_kill_aura_target(), false)
            end

            if modules.get_setting("pulsive", "session-info") then
                drawSessionInfo(false)
            end

            if modules.get_setting("pulsive", "radar") then
                drawRadar(false)
            end

            if modules.get_setting("pulsive", "watermark") then
                drawWatermark(false)
            end
        end
    end,

    on_effect_render = function()
        if client.current_kill_aura_target() ~= -1 and modules.get_setting("pulsive", "target-hud") then
            drawTargetHud(client.current_kill_aura_target(), true)
        end

        if modules.get_setting("pulsive", "session-info") then
            drawSessionInfo(true)
        end

        if modules.get_setting("pulsive", "radar") then
            drawRadar(true)
        end

        if modules.get_setting("pulsive", "watermark") then
            drawWatermark(true)
        end
    end,

    on_packet_receive = function (e)
        if e.packet_id == 0x02 then
            local message = e.message
            for _, winMessage in ipairs(winMessages) do
                if string.find(message, winMessage) then
                    wins = wins + 1
                    break
                end
            end

            if (string.find(message, "killed by") or string.find(message, "thrown into the void by") or string.find(message, "knocked off a cliff by")) and string.find(message, player.get_name() .. ".") then
                kills = kills + 1
            end
        end
    end,

    on_packet_send = function (e)
        if e.packet_id == 0x01 then
            local message = e.message
            if string.find(message, "!reset") then
                reset()
                e.cancel = true
            end
        end
    end
})
modules.create_text_setting("pulsive", "watermark-div", "-------------------------------------", "Watermark")
modules.create_boolean_setting("pulsive", "watermark", "Enabled", true)
modules.create_integer_setting("pulsive", "watermark-x", "X Pos", 10, 0, 1000, 1)
modules.create_integer_setting("pulsive", "watermark-y", "Y Pos", 10, 0, 550, 1)

modules.create_text_setting("pulsive", "target-div", "-------------------------------------", "Target HUD")
modules.create_boolean_setting("pulsive", "target-hud", "Enabled", true)
modules.create_integer_setting("pulsive", "target-x", "TargetHUD X", 510, 0, 1000, 1)
modules.create_integer_setting("pulsive", "target-y", "TargetHUD Y", 300, 0, 550, 1)

modules.create_text_setting("pulsive", "session-div", "-------------------------------------", "Session Info")
modules.create_boolean_setting("pulsive", "session-info", "Enabled", true)
modules.create_integer_setting("pulsive", "session-x", "X Pos", 10, 0, 1000, 1)
modules.create_integer_setting("pulsive", "session-y", "Y Pos", 40, 0, 550, 1)

modules.create_text_setting("pulsive", "radar-div", "-------------------------------------", "Radar")
modules.create_boolean_setting("pulsive", "radar", "Enabled", true)
modules.create_integer_setting("pulsive", "radar-x", "X Pos", 10, 0, 1000, 1)
modules.create_integer_setting("pulsive", "radar-y", "Y Pos", 115, 0, 550, 1)

modules.create_text_setting("pulsive", "theme-div", "-------------------------------------", "Theming")
modules.create_color_setting("pulsive", "primary-color", "Primary Color", 0xff4674eb)
modules.create_color_setting("pulsive", "secondary-color", "Secondary Color", 0xff9db4f0)
modules.create_integer_setting("pulsive", "time", "Fade Time", 2000, 1000, 6000, 100)