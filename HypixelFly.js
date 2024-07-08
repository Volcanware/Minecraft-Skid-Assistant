var module = moduleManager.addModule("HypixelFly", "a", "HypixelFly");
player.message(".hide DamageBoost");
player.message(".hide DamageBoost");
player.message(".bind HypixelFly V");
var ticks = 0;

module.hook("eventMotion", function (event) {
    player.setMotionY(0.08);

    if(ticks == 2) {
       event.y = player.getY() - 22 - Math.random() * 5
    }
    

    if(ticks > 1) {
        player.setMovementSpeed(event, player.baseMoveSpeed())
    } else {
        if(player.baseMoveSpeed() == 0.40221999999999997) {
            player.setMovementSpeed(event, 0.03);
        } else {
            player.setMovementSpeed(event, 0.02);
        }
    }

    return event;
});

module.hook("eventTick", function (event) {

    ticks++;
    if(ticks == 6) {
        ticks = 0;
    }

    return event;
});