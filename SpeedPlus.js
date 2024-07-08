var script = initScript({
    name: "Speed+",
    description: "Speed Module with Newer Bypasses",
    author: "[BETA]"
})

var mode = script.modeSetting("Mode", "NCP", "NCP", "WatchDog", "Verus", "Dev")

script.onMotion(function (event) {
    if (event.isPre() && mode.getMode() == "NCP") {
        if (player.onGround()) {
            player.jump();
            player.setSpeed(0.485);
        }
        else {
        if (player.fallDistance() > 0.6 && player.fallDistance() < 1) {
            player.setMotionY(-0.8)
        }
        else player.setSpeed(0.25);
        }
    }
    if (event.isPre() && player.onGround() && mode.getMode() == "WatchDog") {
        player.jump()
        player.setSpeed(0.485);
    }
    if (event.isPre() && mode.getMode() == "Verus") {
        if (player.onGround()) {
            player.jump()
            player.setSpeed(0.45);
        }
        else player.setSpeed(0.28);
    }
})