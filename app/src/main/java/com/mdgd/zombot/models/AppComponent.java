package com.mdgd.zombot.models;

import com.mdgd.zombot.models.captor.ScreenCaptorImpl;
import com.mdgd.zombot.models.logger.Logger;
import com.mdgd.zombot.models.prefs.Prefs;
import com.mdgd.zombot.models.prefs.cache.CachedPrefs;

public interface AppComponent {

    Logger getLogger();

    ScreenCaptorImpl getCaptor();

    Prefs getPrefs();

    CachedPrefs getCachedPrefs();
}
