module = script.registerModule("PacketDamage",EXPLOITS);

module.onEvent("enable",function(){
		var i;
		for(i = 0;i <= 3/0.0625;i++){
				connection.sendPacket("0x04",player.getX(),player.getY() + 0.0625,player.getZ(),false);
				connection.sendPacket("0x04",player.getX(),player.getY(),player.getZ(),false);
		}
		connection.sendPacket("0x03",true);
		client.toggleModule("PacketDamage");
})