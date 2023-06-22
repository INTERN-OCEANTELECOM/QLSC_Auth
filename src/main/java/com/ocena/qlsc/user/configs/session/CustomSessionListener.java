package com.ocena.qlsc.user.configs.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.concurrent.ScheduledExecutorService;

public class CustomSessionListener implements HttpSessionListener {
    private ScheduledExecutorService executorService;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // ...
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        if (executorService != null) {
            executorService.shutdownNow(); // Abort the ScheduledExecutorService when the session is destroyed
        }
    }
}
