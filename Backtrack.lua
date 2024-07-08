-- you thought nigga | you successfully got hazed by the scripters inc. association | discord.gg/pestel
-- but for downing enjoy syutos shitty scaffold fix - Pestel/The Ultimate Pullback


module_manager.register("Backtrack", {
    on_player_move = function(ctx)
        player.convert_speed(ctx, 0)
        ctx.y = -0.0784
        return ctx
    end
})