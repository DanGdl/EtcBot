package com.mdgd.zombot.models;

import android.content.Context;

import com.mdgd.zombot.models.captor.ScreenCaptorImpl;
import com.mdgd.zombot.models.logger.Logger;
import com.mdgd.zombot.models.logger.LoggerImpl;
import com.mdgd.zombot.models.prefs.Prefs;
import com.mdgd.zombot.models.prefs.PrefsImpl;
import com.mdgd.zombot.models.prefs.cache.CachedPrefs;
import com.mdgd.zombot.models.prefs.cache.CachedPrefsImpl;

public class AppComponentImpl implements AppComponent {
    private final Context appCtx;
    private final Logger logger;
    private final Prefs prefs;
    private final CachedPrefs prefsCache;

    public AppComponentImpl(Context appCtx) {
        this.appCtx = appCtx;

        logger = new LoggerImpl(appCtx);
        prefs = new PrefsImpl(appCtx);
        prefsCache = new CachedPrefsImpl(prefs);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public ScreenCaptorImpl getCaptor() {
        return new ScreenCaptorImpl(appCtx);
    }

    @Override
    public Prefs getPrefs() {
        return prefs;
    }

    @Override
    public CachedPrefs getCachedPrefs() {
        return prefsCache;
    }
}
