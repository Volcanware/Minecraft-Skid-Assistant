package dev.utils.thealtening.service;

import dev.utils.thealtening.service.AlteningServiceType;
import dev.utils.thealtening.service.FieldAdapter;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServiceSwitcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MINECRAFT_SESSION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService";
    private static final String MINECRAFT_AUTHENTICATION_SERVICE_CLASS = "com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication";
    private final FieldAdapter minecraftSessionServer = new FieldAdapter("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
    private final FieldAdapter userAuthentication = new FieldAdapter("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");

    public ServiceSwitcher() {
        try {
            String[] whitelistedDomains = new String[]{".minecraft.net", ".mojang.com", ".thealtening.com"};
            this.minecraftSessionServer.updateFieldIfPresent("WHITELISTED_DOMAINS", whitelistedDomains);
        }
        catch (Throwable t) {
            LOGGER.warn((Object)t);
        }
    }

    public AlteningServiceType switchToService(AlteningServiceType service) {
        try {
            String authServer = service.getAuthServer();
            FieldAdapter userAuth = this.userAuthentication;
            userAuth.updateFieldIfPresent("BASE_URL", authServer);
            userAuth.updateFieldIfPresent("ROUTE_AUTHENTICATE", new URL(authServer + "authenticate"));
            userAuth.updateFieldIfPresent("ROUTE_INVALIDATE", new URL(authServer + "invalidate"));
            userAuth.updateFieldIfPresent("ROUTE_REFRESH", new URL(authServer + "refresh"));
            userAuth.updateFieldIfPresent("ROUTE_VALIDATE", new URL(authServer + "validate"));
            userAuth.updateFieldIfPresent("ROUTE_SIGNOUT", new URL(authServer + "signout"));
            String sessionServer = service.getSessionServer();
            FieldAdapter userSession = this.minecraftSessionServer;
            userSession.updateFieldIfPresent("BASE_URL", sessionServer + "session/minecraft/");
            userSession.updateFieldIfPresent("JOIN_URL", new URL(sessionServer + "session/minecraft/join"));
            userSession.updateFieldIfPresent("CHECK_URL", new URL(sessionServer + "session/minecraft/hasJoined"));
        }
        catch (Exception e) {
            LOGGER.warn((Object)e);
            return AlteningServiceType.MOJANG;
        }
        return service;
    }
}