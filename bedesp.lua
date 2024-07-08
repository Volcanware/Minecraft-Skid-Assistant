bedBlocks = {}

module_manager.register("BedESP", {
	on_enable = function()
		bedBlocks = {}
	end,
	on_receive_packet = function(e)
		if e.packet_id == 2 then
			message = string.gsub(e.message, '(\194\167%w)', '')
			if message == "The game starts in 1 second!" then
				bedBlocks = findBeds()
			end
		end
	end,
	on_render_screen = function(e)
		for _,v in pairs(bedBlocks) do
			x = v["x"]
			y = v["y"]
			z = v["z"]
			if world.block(x,y,z) == "tile.bed" then
				minX, minY, minZ, maxX, maxY, maxZ = getBlockBoundingBox(x,y,z)
				renderOutline(minX, minY, minZ, maxX, maxY, maxZ, e)
			end
		end
	end
})

function renderOutline(minX, minY, minZ, maxX, maxY, maxZ, sc)
	red = 255
	green = 0
	blue = 255
	px, py, pz = player.camera_position()
	dMinX = minX-px
	dMinY = minY-py
	dMinZ = minZ-pz
	dMaxX = maxX-px
	dMaxY = maxY-py
	dMaxZ = maxZ-pz
	
	x1, y1, z1 = render.world_to_screen(dMaxX,dMinY,dMinZ)
	x2, y2, z2 = render.world_to_screen(dMaxX,dMaxY,dMinZ)
	x3, y3, z3 = render.world_to_screen(dMinX,dMinY,dMaxZ)
	x4, y4, z4 = render.world_to_screen(dMinX,dMaxY,dMaxZ)
	x5, y5, z5 = render.world_to_screen(dMinX,dMinY,dMinZ)
	x6, y6, z6 = render.world_to_screen(dMinX,dMaxY,dMinZ)
	x7, y7, z7 = render.world_to_screen(dMaxX,dMinY,dMaxZ)
	x8, y8, z8 = render.world_to_screen(dMaxX,dMaxY,dMaxZ)
	
	render.scale(1/sc.scale_factor)
	
	lineWidth = 2
	if z1<1 and z2<1 and z3<1 and z4<1 and z5<1 and z6<1 and z7<1 and z8<1 then
		render.line(x1,y1,x2,y2,lineWidth,red,green,blue,255)
		render.line(x3,y3,x4,y4,lineWidth,red,green,blue,255)
		render.line(x5,y5,x6,y6,lineWidth,red,green,blue,255)
		render.line(x7,y7,x8,y8,lineWidth,red,green,blue,255)
		render.line(x1,y1,x5,y5,lineWidth,red,green,blue,255)
		render.line(x2,y2,x6,y6,lineWidth,red,green,blue,255)
		render.line(x3,y3,x7,y7,lineWidth,red,green,blue,255)
		render.line(x4,y4,x8,y8,lineWidth,red,green,blue,255)
		render.line(x5,y5,x3,y3,lineWidth,red,green,blue,255)
		render.line(x6,y6,x4,y4,lineWidth,red,green,blue,255)
		render.line(x7,y7,x1,y1,lineWidth,red,green,blue,255)
		render.line(x8,y8,x2,y2,lineWidth,red,green,blue,255)
	end
	
	render.scale(sc.scale_factor)
end

function findBeds()
	local height = 10 -- Make this higher if no beds show up
	local startX,startY,startZ = player.position()
	local radius = 150
	startX = 0
	startY = 80
	startZ = 0
	beds = {}
	for x = startX-radius, radius+startX, 1 do
		for y = startY-height, height+startY, 1 do
			for z = startZ-radius, radius+startZ, 1 do
				currentBlock = world.block(x, y, z)
				if currentBlock == "tile.bed" then
					bed = {}
					bed["x"] = math.floor(x)
					bed["y"] = math.floor(y)
					bed["z"] = math.floor(z)
					table.insert(beds,bed)
				end
			end
		end
	end
	return beds
end

function getBlockBoundingBox(posX, posY, posZ)
	bedHeight = 0.5625 -- 9 pixels high so (1 / 16) * 9
	maxY = posY + bedHeight
	minY = posY
	maxX = posX
	maxZ = posZ
	minX = posX + 1
	minZ = posZ + 1
	return minX, minY, minZ, maxX, maxY, maxZ
end