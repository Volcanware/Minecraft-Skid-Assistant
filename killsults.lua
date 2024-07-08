ci = 1
cr = 1
playerlist = {}


insults = {"Thou art a pitiful adversary, <Player Name>.",
"<Player Name>, are you not trying? or just bad",
"What an effortless conquest, <Player Name>.",
"Your incompetence precedes you, <Player Name>.",
"<Player Name> takes the 'L'",
"I expected more from you, <Player Name>.",
"I have vanquished mere mortals greater than thee, <Player Name>.",
"Thou art unworthy of my time and skill, <Player Name>.",
"<Player Name>, I'm not hacking, you're just bad",
"A victory so simple, even a child could achieve it, <Player Name>.",
"<Player Name>, how's spectating?",
"I hardly broke a sweat defeating thee, <Player Name>.",
"I am unimpressed by thy lack of performance, <Player Name>.",
"<Player Name> isn't very good at the game",
"Thy defeat was but a foregone conclusion, <Player Name>.",
"<Player Name>, get good",
"<Player Name> how did you click the install button with an aim that bad?",
"<Player Name> sorry I didn't notice you there",
"My skills are unmatched, and <Player Name> learned that the hard way.",
"<Player Name> didn't stand a chance against my epic moves.",
"<Player Name> was obliterated by my superior strategy.",
"<Player Name> sent back to the respawn screen with ease.",
"Never gonna give <Player Name> up, Never gonna let <Player Name> down",
"What does <Player Name>'s IQ and their girlfriend have in common? They're both below 5.",
"<Player Name> became Transgender just to join the 50% a day later",
"Drink hand sanitizer so we can get rid of <Player Name>",
"Even the MC Virgins are less virgin than <Player Name>",
"<Player Name>'s free trial of life has expired",
"<Player Name> is socially awkward",
"I bet <Player Name> believes in the flat earth",
"<Player Name> is the reason why society is failing",
"The air could've took <Player Name> away because of how weak <Player Name> is",
"Even Kurt Cobain is more alive than <Player Name> with his wounds from a shotgun and heroin in his veins",
"<Player Name> is breaking down more than Nirvana after Kurt Cobain's death",
"Does <Player Name> buy their groceries at the dollar store?",
"Does <Player Name> need some pvp advice?",
"I'd smack <Player Name>, but that would be animal abuse",
"I don't cheat, <Player Name> just needs to click faster",
"Welcome to my rape dungeon! population: <Player Name>!",
"<Player Name> pressed the wrong button when they installed Minecraft?",
"If the body is 70% water than how is <Player Name>'s body 100% salt?",
"zvofig is sexier than <Player Name>",
"Oh, <Player Name> is recording? Well I am too",
"<Player Name> is the type of person who would brute force interpolation",
"<Player Name> go drown in your own salt",
"<Player Name> is literally heavier than Overflow",
"Excuse me <Player Name>, I don't speak retard",
"Hmm, the problem <Player Name>'s having looks like a skin color issue",
"<Player Name> I swear I'm on Lunar Client",
"Hey! Wise up <Player Name>! Don't waste your time without zvofig",
"<Player Name> didn't even stand a chance"}

rawinsults = {"However difficult life may seem, there is always something you can do, and succeed at.",
"oops mate I was afk sorry for killing you though",
"I'm sorry, I don't speak clueless fluently, could you repeat that in English?",
"I expected more.",
"Why would I be cheating when I am recording?",
"Pay to lose",
"Are you afraid of me",
"Download Moon to kick ass while listening to some badass music!",
"Why Moon? Cause it is the addition of pure skill and incredible intellectual abilities.",
"I am not racist, but I only like Moon users.",
"Wow! My combo is Moon'n!",
"What should I choose? Moon or Moon?",
"Bigmama and Moonmama.",
"Moon client is your new home.",
"In need of a cute present for Christmas? Moon is all you need!",
"I have a good Moon config, don't blame me.",
"Moon never dies.",
"#NoHaxJustMoon.",
"Do like Tenebrous?",
"Did I really just forget that melody? Mo mo mo mo Moon.",
"Order free baguettes with Moon client.",
"Moon best utility client.",
"Hypixel wants to know Moon owner's location [Accept] [Deny].",
"I am not hacking, just using zvofig.",
"Beauty is not in the face; beauty is in Moon",
"Imagine using anything but zvofig",
"No hax just beta testing the anti-cheat with Moon",
"Don't forget to report me on the forums!",
}





local module = {
    

    on_packet_receive = function(event)
        if event.packet_id == 0x02 then
            if event.message:find(':') then
            
            else    
                if string.lower(string.gsub(event.message, player:get_name(), "")):find("resources") and string.lower(string.gsub(event.message, player:get_name(), "")):find("on") and string.lower(string.gsub(event.message, player:get_name(), "")):find("gather") then
                    if modules:get_setting("Killsults", "debug") == true then client:print('Skywars started') end
                    playerlist = {}
                    players = world:get_players()
                    if players then
                        for i = 1, #players do
                            if modules:get_setting("Killsults", "debug") == true then client:print(i) end
                            if world:get_players()[i] ~= nil then
                                local tplayer = world:get_name(world:get_players()[i])
                                --client:print(i)
                                if tplayer and playerlist then
                                    --local stripped = tplayer:gsub("§%x", "")
                                    table.insert(playerlist, tplayer)
                                    
                                    --client:print(stripped .. ' added to the player list')
                                end
                            end
                        end
                    end
                end
                if string.lower(string.gsub(event.message, player:get_name(), "")):find("bed") and string.lower(string.gsub(event.message, player:get_name(), "")):find("protect") and string.lower(string.gsub(event.message, player:get_name(), "")):find("your") then
                    
                    if modules:get_setting("Killsults", "debug") == true then client:print('Bedwars started') end
                    playerlist = {}
                    players = world:get_players()
                    if players then
                        for i = 1, #players do
                            if modules:get_setting("Killsults", "debug") == true then client:print(i) end
                            if world:get_players()[i] ~= nil then
                                local tplayer = world:get_name(world:get_players()[i])
                                --client:print(i)
                                if tplayer and playerlist then
                                    --local stripped = tplayer:gsub("§%x", "")
                                    table.insert(playerlist, tplayer)
                                    
                                    --client:print(stripped .. ' added to the player list')
                                end
                            end
                        end
                    end
                end
                if string.lower(string.gsub(event.message, player:get_name(), "")):find("opponent") then
                    if modules:get_setting("Killsults", "debug") == true then client:print('Duels started') end
                    playerlist = {}
                    players = world:get_players()
                    if players then
                        for i = 1, #players do
                            if modules:get_setting("Killsults", "debug") == true then client:print(i) end
                            if world:get_players()[i] ~= nil then
                                local tplayer = world:get_name(world:get_players()[i])
                                --client:print(i)
                                if tplayer and playerlist then
                                    --local stripped = tplayer:gsub("§%x", "")
                                    table.insert(playerlist, tplayer)
                                    
                                    --client:print(stripped .. ' added to the player list')
                                end
                            end
                        end
                    end
                end   


                if string.gsub(event.message, player:get_name(), ""):find("by") then
                    if event.message:find(player:get_name()) then
                    
                        local fplayer = player:get_name()
                        local player = string.gsub(fplayer, "by", "")
                        local ostr = event.message
                        local str = string.gsub(ostr, fplayer, player)
                        local reversed_str = string.reverse(str)
                        local reversed_player = string.reverse(player)
                        local pos = reversed_str:find("yb")
                        local p_pos = reversed_str:find(reversed_player)

                        if ostr:find("BED") then
                            if modules:get_setting("Killsults", "debug") == true then client:print('Bed destroyed.') end
                        else
                            if p_pos < pos then
                                math.randomseed(client:time())
                                local random_number = math.random(111111, 999999)
                                local choice = math.random(1, 10)
                                local oplayer = ''
                                for i = 1, #playerlist do
                                    if ostr and fplayer and playerlist then
                                        if string.gsub(ostr, fplayer, ""):find(playerlist[i]) then
                                            if modules:get_setting("Killsults", "debug") == true then client:print('killed ' .. playerlist[i]) end
                                            
                                            oplayer = playerlist[i]
                                            break
                                        end
                                    end
                                end
                                if oplayer ~= '' then
                                    if choice then
                                        if choice < 9 then
                                            if insults and ci then            
                                                if modules:get_setting("Killsults", "bypass") == true then        
                                                    if modules:get_setting("Killsults", "random order") == true then
                                                        math.randomseed(client:time())
                                                        number = math.random(1, #insults)
                                                        
                                                        client:message(string.gsub(insults[number], "<Player Name>", oplayer) .. ' [' .. tostring(random_number) .. ']')
                                                    else
                                                        client:message(string.gsub(insults[ci], "<Player Name>", oplayer) .. ' [' .. tostring(random_number) .. ']')
                                                    end
                                                else
                                                    if modules:get_setting("Killsults", "random order") == true then
                                                        math.randomseed(client:time())
                                                        number = math.random(1, #insults)
                                                        
                                                        client:message(string.gsub(insults[number], "<Player Name>", oplayer))
                                                    else
                                                        client:message(string.gsub(insults[ci], "<Player Name>", oplayer))
                                                    end
                                                end
                                                if modules:get_setting("Killsults", "debug") == true then client:print('Sent a personalized insult.') end
                                                if modules:get_setting("Killsults", "debug") == true then client:print(ci) end
                                                
                                                ci = ci + 1
                                                if ci > #insults then
                                                    ci = 1
                                                end  
                                            end
                                        else
                                            if rawinsults and cr then
                                                if modules:get_setting("Killsults", "bypass") == true then     
                                                    if modules:get_setting("Killsults", "random order") == true then
                                                        math.randomseed(client:time())
                                                        number = math.random(1, #rawinsults)
                                                        
                                                        client:message(rawinsults[number] .. ' [' .. tostring(random_number) .. ']')
                                                    else
                                                        client:message(rawinsults[cr] .. ' [' .. tostring(random_number) .. ']')
                                                    end
                                                    
                                                else
                                                    if modules:get_setting("Killsults", "random order") == true then
                                                        math.randomseed(client:time())
                                                        number = math.random(1, #rawinsults)
                                                        
                                                        client:message(rawinsults[number])
                                                    else
                                                        client:message(rawinsults[cr])
                                                    end
                                                    
                                                end
                                                if modules:get_setting("Killsults", "debug") == true then client:print('Sent a anonymous insult.') end
                                                if modules:get_setting("Killsults", "debug") == true then client:print(cr) end
                                                
                                                cr = cr + 1
                                                if cr > #insults then
                                                    cr = 1
                                                end
                                            end
                                        end
                                    end
                                else
                                    if modules:get_setting("Killsults", "debug") == true then client:print('Couldnt find victim, looking again...') end
                                    players = world:get_players()
                                    for i = 1, #players do
                                        local tplayer = world:get_name(world:get_players()[i])
                                        if modules:get_setting("Killsults", "debug") == true then client:print(i) end
                                        
                                        if string.gsub(ostr, fplayer, ""):find(tplayer) then
                                            if modules:get_setting("Killsults", "debug") == true then client:print('found ' .. tplayer) end
                                            
                                            oplayer = tplayer
                                            break
                                        end
                                    end
                                    if oplayer ~= '' then
                                        if choice then
                                            if choice < 9 then
                                                if insults and ci then        
                                                    if modules:get_setting("Killsults", "bypass") == true then       
                                                        
                                                        if modules:get_setting("Killsults", "random order") == true then
                                                            math.randomseed(client:time())
                                                            number = math.random(1, #insults)
                                                            
                                                            client:message(string.gsub(insults[number], "<Player Name>", oplayer) .. ' [' .. tostring(random_number) .. ']')
                                                        else
                                                            client:message(string.gsub(insults[ci], "<Player Name>", oplayer) .. ' [' .. tostring(random_number) .. ']')
                                                        end
                                                    else
                                                        
                                                        if modules:get_setting("Killsults", "random order") == true then
                                                            math.randomseed(client:time())
                                                            number = math.random(1, #insults)
                                                            
                                                            client:message(string.gsub(insults[number], "<Player Name>", oplayer))
                                                        else
                                                            client:message(string.gsub(insults[ci], "<Player Name>", oplayer))
                                                        end
                                                    end
                                                    if modules:get_setting("Killsults", "debug") == true then client:print('Sent a personalized insult.') end
                                                    if modules:get_setting("Killsults", "debug") == true then client:print(ci) end
                                                    
                                                    ci = ci + 1
                                                    if ci > #insults then
                                                        ci = 1
                                                    end  
                                                end
                                            else
                                                if rawinsults and cr then
                                                    if modules:get_setting("Killsults", "bypass") == true then
                                                        if modules:get_setting("Killsults", "random order") == true then
                                                            math.randomseed(client:time())
                                                            number = math.random(1, #rawinsults)
                                                            
                                                            client:message(rawinsults[number] .. ' [' .. tostring(random_number) .. ']')
                                                        else
                                                            client:message(rawinsults[cr] .. ' [' .. tostring(random_number) .. ']')
                                                        end
                                                        
                                                    else
                                                        if modules:get_setting("Killsults", "random order") == true then
                                                            math.randomseed(client:time())
                                                            number = math.random(1, #rawinsults)
                                                            
                                                            client:message(rawinsults[number])
                                                        else
                                                            client:message(rawinsults[cr])
                                                        end
                                                        
                                                    end
                                                    if modules:get_setting("Killsults", "debug") == true then client:print('Sent a anonymous insult.') end
                                                    if modules:get_setting("Killsults", "debug") == true then client:print(cr) end
                                                    
                                                    cr = cr + 1
                                                    if cr > #insults then
                                                        cr = 1
                                                    end
                                                end
                                            end
                                        end
                                    else
                                        if modules:get_setting("Killsults", "bypass") == true then
                                            if modules:get_setting("Killsults", "random order") == true then
                                                math.randomseed(client:time())
                                                number = math.random(1, #rawinsults)
                                                
                                                client:message(rawinsults[number] .. ' [' .. tostring(random_number) .. ']')
                                            else
                                                client:message(rawinsults[cr] .. ' [' .. tostring(random_number) .. ']')
                                            end
                                        else

                                            if modules:get_setting("Killsults", "random order") == true then
                                                math.randomseed(client:time())
                                                number = math.random(1, #rawinsults)
                                                client:message(rawinsults[number])
                                            else
                                                client:message(rawinsults[cr])
                                            end
                                        end
                                        if modules:get_setting("Killsults", "debug") == true then client:print('Sent a anonymous insult.') end
                                        if modules:get_setting("Killsults", "debug") == true then client:print(cr) end
                                        cr = cr + 1
                                        if cr > #insults then
                                            cr = 1
                                        end  
                                        if modules:get_setting("Killsults", "debug") == true then client:print('Couldnt find victim once again.') end 
                                    end
                                end

                            if modules:get_setting("Killsults", "debug") == true then client:print('your kill.') end
                            else
                            if modules:get_setting("Killsults", "debug") == true then client:print('your death.') end
                            end
                        end
                    end
                end

            end
        end
    end
    
}

-- Register
modules:register("Killsults", "Killsults", module)
modules:create_boolean_setting("Killsults", "random order", "random order", false)
modules:create_boolean_setting("Killsults", "debug", "debug", false)
modules:create_boolean_setting("Killsults", "bypass", "bypass", false)

