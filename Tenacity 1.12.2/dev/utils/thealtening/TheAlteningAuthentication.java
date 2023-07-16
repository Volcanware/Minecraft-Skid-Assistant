package dev.utils.thealtening;

import dev.utils.thealtening.SSLController;
import dev.utils.thealtening.service.AlteningServiceType;
import dev.utils.thealtening.service.ServiceSwitcher;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

public final class TheAlteningAuthentication {
    private static TheAlteningAuthentication instance;
    private final ServiceSwitcher serviceSwitcher = new ServiceSwitcher();
    private final SSLController sslController = new SSLController();
    private AlteningServiceType service;

    private TheAlteningAuthentication(@Nonnull AlteningServiceType service) throws Throwable {
        if (service == null) {
            throw new NullPointerException("service is marked non-null but is null");
        }
        this.updateService(service);
    }

    public void updateService(@Nonnull AlteningServiceType service) {
        if (service == null) {
            throw new NullPointerException("service is marked non-null but is null");
        }
        if (this.service == service) {
            return;
        }
        switch (service) {
            case MOJANG: {
                this.sslController.enableCertificateValidation();
                break;
            }
            case THEALTENING: {
                this.sslController.disableCertificateValidation();
                break;
            }
        }
        this.service = this.serviceSwitcher.switchToService(service);
    }

    public static TheAlteningAuthentication mojang() {
        return TheAlteningAuthentication.withService(AlteningServiceType.MOJANG);
    }

    public static TheAlteningAuthentication theAltening() {
        return TheAlteningAuthentication.withService(AlteningServiceType.THEALTENING);
    }

    private static TheAlteningAuthentication withService(@Nonnull AlteningServiceType service) {
        if (service == null) {
            throw new NullPointerException("service is marked non-null but is null");
        }
        if (instance == null) {
            try {
                instance = new TheAlteningAuthentication(service);
            }
            catch (KeyManagementException | NoSuchAlgorithmException e) {
                SSLController.log.warn((Object)e);
            }
            catch (Throwable t) {
                SSLController.log.warn("Unexpected error occurred while executing...", t);
            }
        } else {
            if (instance.getService() == service) return instance;
            instance.updateService(service);
        }
        return instance;
    }

    @Nonnull
    public AlteningServiceType getService() {
        return this.service;
    }
}