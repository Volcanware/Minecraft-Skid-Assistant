module = script.registerModule("BetterBhop",MOVEMENT)

module.addStringProperty("Mode","Mode","Watchdog","Watchdog", "Watchdog-Combat-Test", "Dev")

module.onEvent("playerPreUpdateEvent",function(event){
    if(player.isOnGround() & player.isMoving()){
      if (module.getProperty("Mode").getString() == "Watchdog") {
         player.jump()
         player.setSpeed(0.485)
      }
   }
    if (module.getProperty("Mode").getString() == "Watchdog-Combat-Test") {
      var entities = world.getEntityList();
      var i;
         if (player.isMoving()) {
            for(i = 0;i < entities.length;i++){
            if (!player.isOnGround() && player.getDistanceToEntity(entities[i]) < 3 && player.getFallDistance() > 0.2 && player.getFallDistance() < 1 && entity_util.getName(entities[i]) != player.getName()) {
               if (player.getHurtTime() > 0) {
                  player.setSpeed(0.4)
               }
               if (player.getHurtTime() == 0) {
                  player.setSpeed(0.2)
               }
               if (player.getFallDistance() > 0.5) {
                  player.setMotionY(-0.17)
               }
            }
         }
         if (player.isOnGround()) {
            player.jump()
            player.setSpeed(0.485)
         }
      }
   }
})