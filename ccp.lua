local socialcr = 0
local ticks = 500
local ccpsimulator = {
    on_render_screen = function(ctx)

        local optionY = module_manager.option('SocialCredit','Pos-Y')
        local optionX = module_manager.option('SocialCredit','Pos-X')

        local width = 205
        local height = 50
        render.rect(optionX, optionY, optionX + width, optionY + height, 0, 0, 0, 100)
        render.scale(1.5)
        render.string_shadow("Social Credit: ".. socialcr .."", (optionX + 5)/1.5, (optionY + 5)/1.5, 255, 255, 255, 255)
        render.scale(1/1.5)

        if socialcr > 0 then
            render.string_shadow("Good work citizen!", optionX + 5, optionY + 20, 0, 255, 0, 255)
        elseif socialcr <= 0 then
            render.string_shadow("BAD WORK CITIZEN! EARN SOCIAL CREDIT!", optionX + 5, optionY + 20, 255, 0, 0, 255)
        end

        local chance = math.random(1, 10000)
        if chance == 50 then
            socialcr = socialcr + 100
        end

        if input.is_mouse_down(0) then if ctx.mouse_x >= math.floor(optionX) and ctx.mouse_x <= math.floor((optionX+width)) then
            if ctx.mouse_y >= math.floor(optionY) and ctx.mouse_y <= math.floor((optionY+height)) then
                if client.gui_name() == "chat" or client.gui_name() == "astolfo" then
                    xPos = (ctx.mouse_x - width /2)
                    yPos = (ctx.mouse_y - height /2)
                    player.message('.SocialCredit Pos-X '..math.floor(xPos))
                    player.message('.SocialCredit Pos-Y '..math.floor(yPos))
                end
            end
        end
    end
end
}


module_manager.register("SocialCredit", ccpsimulator)
module_manager.register_number('SocialCredit', 'Pos-X', 0, 384000, 375)
module_manager.register_number('SocialCredit', 'Pos-Y', 0, 216000, 300)