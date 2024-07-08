var module = moduleManager.addModule("BlocksmcDisabler", "BlocksmcDisabler", "BlocksmcDisabler");

module.hook("eventTick", function () {
    if(player.ticksExisted() % 60 == 0) {
        player.sendPacket(0x04, player.getX(), player.getY() -0.5, player.getZ(), false)
        player.sendPacket(0x03, false)
    }
})