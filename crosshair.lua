local Line = render.line


local Crosshair = {
    color = {255, 0, 120},
    CrosshairMath = {
        rotateDegree = 0,
        DEG2RAD = function(a)
            return a * math.pi / 180
        end,
        RAD2DEG = function(a)
            return a * 180 / math.pi
        end,
    },

    drawCrosshair = function(self, renderWidth, renderHeight)

        if renderWidth ~= nil and renderHeight ~= nil then
    
            local hmSize = module_manager.option('Swastika Crosshair', "Crosshair Size")
    
            local fixedValue = hmSize >= 100 and 100 or hmSize
    
            local size = (renderHeight / (101 - fixedValue))
        
            if self.CrosshairMath.rotateDegree > 90 then self.CrosshairMath.rotateDegree = 0; end
    
            for i = 1, 4 do
                local pos0 = math.atan(size / size)
                local pos1 = size * math.sin(self.CrosshairMath.DEG2RAD(self.CrosshairMath.rotateDegree + (i * 90)))
                local pos2 = size * math.cos(self.CrosshairMath.DEG2RAD(self.CrosshairMath.rotateDegree + (i * 90)))
                local pos3 = (size / math.cos(pos0)) * math.sin(self.CrosshairMath.DEG2RAD(self.CrosshairMath.rotateDegree + (i * 90) + self.CrosshairMath.RAD2DEG(pos0)))
                local pos4 = (size / math.cos(pos0)) * math.cos(self.CrosshairMath.DEG2RAD(self.CrosshairMath.rotateDegree + (i * 90) + self.CrosshairMath.RAD2DEG(pos0)))
    
                Line(renderWidth, (renderHeight), (renderWidth) + pos1, (renderHeight) - pos2, 2,
                self.color[1],
                self.color[2],
                self.color[3],
                255)
                Line(renderWidth + pos1, renderHeight - pos2, renderWidth + pos3, renderHeight -pos4, 2,
                self.color[1],
                self.color[2],
                self.color[3],
                255)
    
            end
    
            self.CrosshairMath.rotateDegree = self.CrosshairMath.rotateDegree + module_manager.option('Swastika Crosshair', "Crosshair Speed") / 10
        end
    end
}


local FreautCrosshair = {
    on_render_screen = function(param)
        local width, height = param.width, param.height

        Crosshair:drawCrosshair(width /2, height /2)
    end,
}

module_manager.register('Swastika Crosshair', FreautCrosshair)
module_manager.register_number('Swastika Crosshair', "Crosshair Size", 1, 100, 15)
module_manager.register_number('Swastika Crosshair', "Crosshair Speed", 1, 100, 5)