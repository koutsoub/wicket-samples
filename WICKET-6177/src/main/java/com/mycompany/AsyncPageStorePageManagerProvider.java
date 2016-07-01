package com.mycompany;

import org.apache.wicket.Application;
import org.apache.wicket.IPageManagerProvider;
import org.apache.wicket.page.IPageManager;
import org.apache.wicket.page.IPageManagerContext;
import org.apache.wicket.page.PageAccessSynchronizer;
import org.apache.wicket.page.PageStoreManager;
import org.apache.wicket.pageStore.*;
import org.apache.wicket.serialize.ISerializer;
import org.apache.wicket.settings.StoreSettings;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Bytes;

import java.io.File;

public class AsyncPageStorePageManagerProvider implements IPageManagerProvider {
    protected final Application application;
    final PageAccessSynchronizer pageAccessSynchronizer;

    /**
     * Constructor.
     *
     * @param application The application instance
     */
    public AsyncPageStorePageManagerProvider(Application application) {
        this.application = Args.notNull(application, "application");
        this.pageAccessSynchronizer = new PageAccessSynchronizer(application.getRequestCycleSettings().getTimeout());
    }

    @Override
    public IPageManager get(IPageManagerContext pageManagerContext) {
        IDataStore dataStore = newDataStore();

        StoreSettings storeSettings = getStoreSettings();

        if (dataStore.canBeAsynchronous()) {
            int capacity = storeSettings.getAsynchronousQueueCapacity();
            dataStore = new AsynchronousDataStore(dataStore, capacity);
        }

        IPageStore pageStore = newPageStore(dataStore);
        return pageAccessSynchronizer.adapt(new PageStoreManager(application.getName(), pageStore, pageManagerContext));

    }

    protected IPageStore newPageStore(IDataStore dataStore) {
        return new AsyncPageStore(application.getFrameworkSettings().getSerializer(), dataStore, 0, pageAccessSynchronizer);
    }

    protected IDataStore newDataStore() {
        StoreSettings storeSettings = getStoreSettings();
        Bytes maxSizePerSession = storeSettings.getMaxSizePerSession();
        File fileStoreFolder = storeSettings.getFileStoreFolder();

        return new DiskDataStore(application.getName(), fileStoreFolder, maxSizePerSession);
    }

    StoreSettings getStoreSettings() {
        return application.getStoreSettings();
    }
}
