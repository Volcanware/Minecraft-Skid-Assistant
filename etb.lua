local main = {
    on_render_screen = function(ctx)
            local test = 15
            local y = 0
            local x = 0
            render.draw_image("https://b.catgirlsare.sexy/1P1M8BsE4Z31.png", 0, 2, 32, 32)
            render.rect(1, 12 / 0.95 + test, 60 / 0.99, 68 / 0.90+ test, 0, 0, 0, 161) -- background
            render.rect(59 / 0.99, 12 / 0.95 + test, 60 / 0.99, 68 / 0.90+ test, 0, 0, 0, 255) -- linha direita
            render.rect(1, 12 / 0.95 + test, 2, 68 / 0.90+ test, 0, 0, 0, 255) -- linha esquerda
            render.rect(1, 71 / 0.95 + test, 59 / 0.99, 68 / 0.90+ test, 0, 0, 0, 255) -- linha de baixo
            render.rect(1, 12 / 0.95 + test, 59 / 0.99, 12 / 0.90+ test, 0, 0, 0, 255) -- linha de cima
            render.rect(2, 13 / 0.95 + test, 59 / 0.99, 23 / 0.90+ test, 255, 77, 76, 255) -- rect principal colorido
            render.string_shadow("2.0", 22 + x, 17 + y, 177, 177, 177, 255)
            render.string_shadow("Combat", 7, 30 + y, 255, 255, 255, 255)
            render.string_shadow("Render", 5, 43 + y, 159, 159, 159, 255)
            render.string_shadow("Movement", 5, 55 + y, 159, 159, 159, 255)
            render.string_shadow("Player", 5, 67 + y, 159, 159, 159, 255)
            render.string_shadow("World", 5, 79 + y, 159, 159, 159, 255)
            local facing = player.facing()
            local directions = {
                [3] = "N",
                [4] = "S",
                [5] = "W",
                [6] = "E"
            }
            if directions[facing] then
                render.string_shadow("[" .. directions[facing] .. "]", 38 + x, 17 + y, 255, 77, 76, 255)
            end
         end
}

module_manager.register('tabgui', main)
