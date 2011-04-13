package org.eclipse.birt.report.data.oda.jpa;

import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.birt.report.data.oda.jpa"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		Thread thread = Thread.currentThread();
		ClassLoader oldloader = thread.getContextClassLoader();
		
		/*Only in Hibernate*/
		//context.getBundle().loadClass("org.hibernate.proxy.HibernateProxy");
		
		//JPAUtil.refreshURLs();		
		JPAUtil.refreshURLs();		
		//JPAUtil.pluginLoader = JPAUtil.class.getClassLoader();
		JPAUtil.pluginLoader = JPAUtil.class.getClassLoader();
		//ClassLoader changeLoader = new URLClassLoader( (URL [])URLList.toArray(new URL[0]),JPAUtil.class.getClassLoader());
		//ClassLoader changeLoader = new URLClassLoader( (URL [])URLList.toArray(new URL[0]),thread.getContextClassLoader());
		//ClassLoader changeLoader = new URLClassLoader( (URL [])JPAUtil.URLList.toArray(new URL[0]), JPAUtil.class.getClassLoader());
		
		
		//ClassLoader changeLoader = new URLClassLoader( (URL [])JPAUtil.URLList.toArray(new URL[0]));
		ClassLoader changeLoader = new URLClassLoader( (URL [])JPAUtil.URLList.toArray(new URL[0]));
		thread.setContextClassLoader(changeLoader);	
		
		JPAUtil.changeLoader = changeLoader;
		//JPAUtil.constructSessionFactory("", "");	

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		JPAUtil.closeSession();
		JPAUtil.closeFactory();		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
