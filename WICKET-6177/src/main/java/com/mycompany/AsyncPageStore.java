package com.mycompany;

import org.apache.wicket.Application;
import org.apache.wicket.page.IManageablePage;
import org.apache.wicket.page.IPageManager;
import org.apache.wicket.page.PageAccessSynchronizer;
import org.apache.wicket.pageStore.DefaultPageStore;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.serialize.ISerializer;

/**
 *
 */
public class AsyncPageStore extends DefaultPageStore {

    private final PageAccessSynchronizer pageAccessSynchronizer;

    /**
     * Construct.
     *
     * @param pageSerializer the {@link ISerializer} that will be used to convert pages from/to byte arrays
     * @param dataStore      the {@link IDataStore} that actually stores the pages
     * @param cacheSize      the number of pages to cache in memory before passing them to
     *                       {@link IDataStore#storeData(String, int, byte[])}
     */
    public AsyncPageStore(final ISerializer pageSerializer, final IDataStore dataStore, final int cacheSize, final PageAccessSynchronizer pageAccessSynchronizer) {
        super(pageSerializer, dataStore, cacheSize);
        this.pageAccessSynchronizer = pageAccessSynchronizer;
    }



    @Override
    public void storePage(final String sessionId, final IManageablePage page) {
        pageAccessSynchronizer.lockPage(page.getPageId());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                byte[] data = serializePage(page);
                if (data != null)
                {
                    int pageId = page.getPageId();

                    storePageData(sessionId, pageId, data);
                    pageAccessSynchronizer.unlockPage(pageId);
                }
            }
        };
        WicketApplication application = WicketApplication.get();
        application.submit(task);
    }
}
