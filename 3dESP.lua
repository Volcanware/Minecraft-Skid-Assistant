function hsvToRgb( hue, saturation, value )
	if saturation == 0 then return value;
	end;
	local hue_sector = math.floor( hue / 60 );
	local hue_sector_offset = ( hue / 60 ) - hue_sector;
	local p = value * ( 1 - saturation );
	local q = value * ( 1 - saturation * hue_sector_offset );
	local t = value * ( 1 - saturation * ( 1 - hue_sector_offset ) );
	if hue_sector == 0 then return value, t, p;
	elseif hue_sector == 1 then return q, value, p;
	elseif hue_sector == 2 then return p, value, t;
	elseif hue_sector == 3 then return p, q, value;
	elseif hue_sector == 4 then return t, p, value;
	elseif hue_sector == 5 then return value, p, q;
	end;
end;

local espRGB = 1
local espDelay = 1
local e = world.entities()


local threedesp = {


    on_render_screen = function(t)
        local list = world.entities()
        render.scale(1/t.scale_factor)
        for i=1, #list do
            if list[i] ~= player.id()--[[ and world.is_player(list[i])]] then
                local px,py,pz = player.camera_position()
                local tx, ty, tz = world.position(list[i])
                local bbminX, bbminY, bbminZ, bbmaxX, bbmaxY, bbmaxZ = world.bounding_box(list[i])

                local p1x, p1y, p1z = render.world_to_screen(bbminX-px, bbminY-py, bbminZ-pz)
                local p2x, p2y, p2z = render.world_to_screen(bbmaxX-px, bbminY-py, bbminZ-pz)
                local p3x, p3y, p3z = render.world_to_screen(bbminX-px, bbminY-py, bbmaxZ-pz)
                local p4x, p4y, p4z = render.world_to_screen(bbmaxX-px, bbminY-py, bbmaxZ-pz)
                local p5x, p5y, p5z = render.world_to_screen(bbminX-px, bbmaxY-py, bbminZ-pz)
                local p6x, p6y, p6z = render.world_to_screen(bbmaxX-px, bbmaxY-py, bbminZ-pz)
                local p7x, p7y, p7z = render.world_to_screen(bbminX-px, bbmaxY-py, bbmaxZ-pz)
                local p8x, p8y, p8z = render.world_to_screen(bbmaxX-px, bbmaxY-py, bbmaxZ-pz)


                local width = module_manager.option('3DESP', 'Width')
                --local espWidth = 10 - (player.distance_to_entity(list[i]) / 50)
                local h = module_manager.option('3DESP', 'Hue')
                local s = module_manager.option('3DESP', 'Saturation')/100
                local v = module_manager.option('3DESP', 'Value')/100
                local r, g, b
                
                
                 
                 
                
                r, g, b = hsvToRgb(i*2, s, v)
                r, g, b = 255*r, 255*g, 255*b
                
                
                if world.is_enemy(i) then r, g, b = 255, 0, 0 end
                if world.is_friend(i) then r, g, b = 0, 255, 0 end




                if p1z<1 and p2z<1 and p3z<1 and p4z<1 and p5z<1 and p6z<1 and p7z<1 and p8z<1 then

                    render.line(p1x, p1y, p2x, p2y, width, r, g, b, 255)
                    render.line(p1x, p1y, p3x, p3y, width, r, g, b, 255)
                    render.line(p1x, p1y, p5x, p5y, width, r, g, b, 255)

                    render.line(p2x, p2y, p4x, p4y, width, r, g, b, 255)
                    render.line(p2x, p2y, p6x, p6y, width, r, g, b, 255)

                    render.line(p3x, p3y, p4x, p4y, width, r, g, b, 255)
                    render.line(p3x, p3y, p7x, p7y, width, r, g, b, 255)

                    render.line(p4x, p4y, p8x, p8y, width, r, g, b, 255)

                    render.line(p7x, p7y, p8x, p8y, width, r, g, b, 255)


                    render.line(p5x, p5y, p6x, p6y, width, r, g, b, 255)
                    render.line(p5x, p5y, p7x, p7y, width, r, g, b, 255)

                    render.line(p6x, p6y, p8x, p8y, width, r, g, b, 255)
                end


            end
        end
        render.scale(t.scale_factor)
        return t
    end



}

module_manager.register('3DESP', threedesp)
module_manager.register_boolean('3DESP', 'Rainbow', false)
module_manager.register_number('3DESP', 'Hue', 1, 359, 1)
module_manager.register_number('3DESP', 'Saturation', 1, 100, 100)
module_manager.register_number('3DESP', 'Value', 1, 100, 100)
module_manager.register_number('3DESP', 'Width', 1, 10, 2)