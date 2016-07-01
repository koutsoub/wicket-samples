package com.mycompany;

import org.apache.wicket.DefaultPageManagerProvider;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.pageStore.IPageStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.serialize.ISerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class
 */
public class WicketApplication extends WebApplication
{
	private final ExecutorService executor = Executors.newFixedThreadPool(4);

	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	public void init()
	{
		super.init();
		final ISerializer pageSerializer = getFrameworkSettings().getSerializer();
//		DefaultPageManagerProvider pageManagerProvider = new DefaultPageManagerProvider(this) {
//			@Override
//			protected IPageStore newPageStore(final IDataStore dataStore) {
//				return new AsyncPageStore(pageSerializer, dataStore, 0);
//			}
//		};
//		setPageManagerProvider(pageManagerProvider);
		setPageManagerProvider(new AsyncPageStorePageManagerProvider(this));
	}

	public static WicketApplication get() {
		return (WicketApplication) WebApplication.get();
	}

	public void submit(Runnable task) {
		executor.submit(task);
	}

	@Override
	protected void onDestroy() {
		executor.shutdown();
		super.onDestroy();
	}
}
