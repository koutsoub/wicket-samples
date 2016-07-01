package com.mycompany;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Martin Makundi
 */
public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	/**
	 * @param parameters
	 */
	public HomePage(final PageParameters parameters) {
		super(parameters);
		add(new Label("version", getApplication().getFrameworkSettings()
				.getVersion()));

		add(new Link<Void>("link") {

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onClick()
			 */
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}

			/**
			 * @see org.apache.wicket.markup.html.link.Link#onComponentTag(org.apache.wicket.markup.ComponentTag)
			 */
			@Override
			protected void onComponentTag(final ComponentTag tag) {
				tag.put("onclick", "document.getElementById('holder').innerHTML = 'Loading ... (slow because serialization is blocking)';return true;"); 
				super.onComponentTag(tag);
			}
		});
	}

	/**
	 * @param s
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		System.out
				.println("Let's simulate a situation where we have a heavy component on the page.");
		for (int i = 0; i < 8; i++) {
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
				System.out.print((i + 1) + " seconds ");
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		System.out.println();
	}
}
