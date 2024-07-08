modules.register("mmcspammer", "MMC Spammer", "Mine Men Club Ez Spam", {
    
    on_tick() function(event)
        ticks + 1
        if ticks = 120 and message1 = 0 then
            message(message: "https://www.youtube.com/@volcanhacks/videos subscribe and I show staff me hacking"): void
            ticks = 0
            message1 = 1
        end
    end
        if ticks = 120 and message1 = 1 and message2 = 0 then
            message(message: "want free alts uwu"): void
            ticks = 0
            message2 = 1
        end
    end
        if ticks = 120 and message1 = 1 and message2 = 1 then
            message(message: "Selling Free Wins"): void
            ticks = 0
            message2 = 0
            message1 = 0
        end
    end
})