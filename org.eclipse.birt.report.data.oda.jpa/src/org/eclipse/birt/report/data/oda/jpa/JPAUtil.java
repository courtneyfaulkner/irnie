package org.eclipse.birt.report.data.oda.jpa;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
//import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver;
import org.eclipse.core.runtime.Platform;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
//import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
//import javax.persistence.EntityNotFoundException;
//import javax.persistence.EntityExistException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.datatools.connectivity.oda.OdaException;

/*in Hibernate used something like: */
/*
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.EntityMode;
*/
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.osgi.framework.Bundle;
//import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.Path;

/*Used for extract Metadata of JPA entities using Reflection*/
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class JPAUtil {

	private static EntityManagerFactory emf=null;
	//private static Object configuration=null;

	//private static String JPAConfigFile = "";
	//private static String JPAMapDirectory = "";
	private static ClassLoader oldloader=null;
	private static ArrayList FileList = new ArrayList();//File's list 
	public static ArrayList URLList = new ArrayList();//URL's list
    public static ClassLoader changeLoader;
    public static ClassLoader pluginLoader;
    
    /*For manage easily share EntitiManagerFactory*/
	public static final ThreadLocal session = new ThreadLocal();

//<<<<<<< .mine
	//private static synchronized void initEntityManagerFactory( String persistenceUnit) throws PersistenceException {

//=======
    /*
     * like a HIbernate Plug-in init the Entity Manager factory 
     * isn't necessary get a name of persistence files because according to
     * specification is located into directory named META-INF, but is necessary
     * have like parameter the PersistenceUnit name  
     **/
	//private static synchronized void initEntityManagerFactory(String persistenceUnit,Map map) 
	private static synchronized void initEntityManagerFactory(String persistenceUnit) 
		throws PersistenceException {
		ClassLoader cl1;
		
		if( emf == null){
			Thread thread = Thread.currentThread();
			try{
			
				/*taked of Hibernate Plug-in*/	
				//Class.forName("org.hibernate.Configuration");
				//Configuration ffff = new Configuration();
				//Class.forName("org.apache.commons.logging.LogFactory");

				//oldloader = thread.getContextClassLoader();
				//Class thwy = oldloader.loadClass("org.hibernate.cfg.Configuration");
				//Class thwy2 = oldloader.loadClass("org.apache.commons.logging.LogFactory");
				//refreshURLs();		
				//ClassLoader changeLoader = new URLClassLoader( (URL [])URLList.toArray(new URL[0]),HibernateUtil.class.getClassLoader());
				
				ClassLoader testLoader = new URLClassLoader( (URL [])URLList.toArray(new URL[0]),pluginLoader);
				//changeLoader = new URLClassLoader( (URL [])URLList.toArray(new URL[0]));
				/*Set the context with testLoader ClassLoader(a URLClassLoader)*/
				thread.setContextClassLoader(testLoader);
				//Bundle jpabundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jpa" );
				/*In Hibernate Plug-in*/	
				//Class thwy2 = changeLoader.loadClass("org.hibernate.cfg.Configuration");
				//Class.forName("org.apache.commons.logging.LogFactory", true, changeLoader);
				//Class cls = Class.forName("org.hibernate.cfg.Configuration", true, changeLoader);
				//Configuration cfg=null;
				//cfg = new Configuration();
				//Object oo = cls.newInstance();
				//Configuration cfg = (Configuration)oo;
//				Configuration cfg = new Configuration();
				//buildConfig(jpafile,mapdir, cfg);	
				
				//z/ buildConfig(persistenceUnit);	


				//z/ Class driverClass = testLoader.loadClass(cfg.getProperty("connection.driver_class"));
				
				
				//Driver driver = (Driver) driverClass.newInstance( );
				//WrappedDriver wd = new WrappedDriver( driver, cfg.getProperty("connection.driver_class"));

				//boolean foundDriver = false;
				/*Enumeration drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					Driver nextDriver = (Driver)drivers.nextElement();
					if (nextDriver.getClass() == wd.getClass()) {
						if( nextDriver.toString().equals(wd.toString()) ){
							foundDriver = true;
							break;
						}
					}
				}				
				if( !foundDriver ){

					DriverManager.registerDriver( wd  ) ;
				}*/
				
				//sessionFactory = cfg.buildSessionFactory();
				System.out.println("PU: "+persistenceUnit);
				//System.out.println("Location: "+jpabundle.getLocation());
				File f=new File(CommonConstant.PERSISTENCE_XML);
				System.out.println("Ruta: "+f.getAbsolutePath());
				emf = Persistence.createEntityManagerFactory(persistenceUnit);
				//configuration = cfg;
				//JPAMapDirectory = mapdir;
				//JPAConfigFile = jpafile;
				
			}catch( Throwable e){
				e.printStackTrace();
				throw new PersistenceException( "No Session Factory Created "  +  e.getLocalizedMessage());

			}
			finally{
				//thread.setContextClassLoader(oldloader);
			}
		}
	}
	
	
	
	public static boolean isEntityManagerFactoryValid() {
		if( emf != null){
			return( true );
		}
		return( false );
	}


	public static synchronized void buildConfig(String persistenceUnit ) throws PersistenceException, IOException, Exception {
		 
		Bundle jpabundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jpa" );
		
		/*File cfgFile = new File(jpafile);
		File cfgDir = new File(mapdir);
		if( cfgDir != null && cfgDir.length() > 0){
			cfg.addDirectory(cfgDir);
		}
	    */
		
		/*URL jpafiles = FileLocator.find(jpabundle, new Path(CommonConstant.JPA_CLASSES), null);
		URL jpaURL = FileLocator.resolve(jpafiles);
		File jpaDirectory = new File(jpaURL.getPath());
		cfg.addDirectory(jpaDirectory);
		if( cfgFile != null && cfgFile.length() > 0){
			cfg.configure(cfgFile);
		}else{
			//File configFile = new File(jpaURL.getPath() + "/persistence.xml");
			File configFile = new File(jpaURL.getPath() + "/persistence.xml");
			cfg.configure(configFile);
		}*/
		return;
	}

	
	public static void constructEntityManagerFactory(String persistenceUnit) throws PersistenceException {

		/*if( jpafile == null){
			jpafile = "";
		}
		if( mapdir == null){
			mapdir = "";
		} */
		if( emf == null){
			//initEntityManagerFactory( jpafile, mapdir);
			initEntityManagerFactory( persistenceUnit);
			System.out.println("Initing Entity Manager Factory");
			return;
		}

		/*if(  JPAMapDirectory.equalsIgnoreCase(mapdir) && JPAConfigFile.equalsIgnoreCase(jpafile)){
			return;
		}*/
		System.out.println( "Session Configuration Changed, rebuilding");
		//Configuration changed need a rebuild.
		//Note this is very expensive      
		synchronized(emf) {
			EntityManager s = (EntityManager) session.get();
			if (s != null) {
				closeSession();
			}
			if (emf != null && emf.isOpen()){
				closeFactory();            	
			}
			emf = null;
			//initEntityManagerFactory( jpafile, mapdir);
			initEntityManagerFactory( persistenceUnit);
		}
	}


	public static EntityManager currentSession() throws PersistenceException {

		EntityManager s = (EntityManager) session.get();
		// Open a new Session, if this thread has none yet
		if (s == null) {
			if( emf == null){
				return null;
			}
			//s = emf.openSession();
			s = emf.createEntityManager();
			// Store it in the ThreadLocal variable
			session.set(s);
		}
		return s;
	}
	
	
	public static void closeFactory(){
		//more error checking needed
		emf.close();
		emf = null;
	}
	
	
	public static void closeSession() throws PersistenceException {
		EntityManager s = (EntityManager) session.get();
		if (s != null)
			s.close();
		session.set(null);
	}

	private static class WrappedDriver implements Driver
	{
		private Driver driver;
		private String driverClass;

		WrappedDriver( Driver d, String driverClass )
		{

			this.driver = d;
			this.driverClass = driverClass;
		}

		/*
		 * @see java.sql.Driver#acceptsURL(java.lang.String)
		 */
		public boolean acceptsURL( String u ) throws SQLException
		{
			boolean res = this.driver.acceptsURL( u );
			System.out.println( "WrappedDriver(" + driverClass + 
					").acceptsURL(" + u + ")returns: " + res);
			return res;
		}

		/*
		 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
		 */
		public java.sql.Connection connect( String u, Properties p ) throws SQLException
		{

			try
			{
				return this.driver.connect( u, p );
			}
			catch ( RuntimeException e )
			{
				throw new SQLException( e.getMessage( ) );
			}
		}

		/*
		 * @see java.sql.Driver#getMajorVersion()
		 */
		public int getMajorVersion( )
		{
			return this.driver.getMajorVersion( );
		}

		/*
		 * @see java.sql.Driver#getMinorVersion()
		 */
		public int getMinorVersion( )
		{
			return this.driver.getMinorVersion( );
		}

		/*
		 * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
		 */
		public DriverPropertyInfo[] getPropertyInfo( String u, Properties p )
		throws SQLException
		{
			return this.driver.getPropertyInfo( u, p );
		}

		/*
		 * @see java.sql.Driver#jdbcCompliant()
		 */
		public boolean jdbcCompliant( )
		{
			return this.driver.jdbcCompliant( );
		}

		/*
		 * @see java.lang.Object#toString()
		 */
			public String toString( )
		{
			return driverClass;
		}
	}//END WrappedDriver class    
	
	public static void refreshURLs()
	{
		Bundle jdbcbundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jdbc" );
		Bundle jpabundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jpa" );
		//Bundle tomcatPlgn = Platform.getBundle( "org.eclipse.tomcat" );
		
		FileList.clear();
		URLList.clear();
		if ( jdbcbundle == null )
			return;			// init failed

		//jpabundle.getEntryPaths(arg0)
		
		// List all files under "drivers" directory of the JDBC plugin
		Enumeration files = jdbcbundle.getEntryPaths( 
				OdaJdbcDriver.Constants.DRIVER_DIRECTORY );
		while ( files.hasMoreElements() )
		{
			String fileName = (String) files.nextElement();
			if ( isDriverFile( fileName ) )
			{
				if ( ! FileList.contains( fileName ))
				{
					// This is a new file not previously added to URL list
					FileList.add( fileName );
					URL fileURL = jdbcbundle.getEntry( fileName );
					URLList.add(fileURL);
					//File f=new File("");
					System.out.println("JDBC Plugin: found JAR/Zip file: " + 
							fileName + ": URL=  " + fileURL );
					try{
							File f=new File(fileURL.getFile());
					System.out.println("JDBC Plugin ABS: found JAR/Zip file: " + 
							fileName + ": URL=  " + f.getAbsolutePath() );
					}catch(Exception e){
						System.out.println(e.getMessage());
						
					}
				}
			}
		}

		
		
		
		/*Enumeration tfiles = tomcatPlgn.getEntryPaths("/" );
		while ( tfiles.hasMoreElements() )
		{
			String fileName = (String) tfiles.nextElement();
			//if ( fileName.equals("commons-logging-api.jar") || fileName.equals("commons-collections.jar") ){
			//	URL fileURL = tomcatPlgn.getEntry( fileName );
			//	URLList.add(fileURL);				
			//}
				
		}*/
		
//		URL jarURL = tomcatPlgn.getEntry( "commons-logging-api.jar" );
//		System.out.println("Hibernate Plugin: Tomcat plugin: URL = " + jarURL );		
//		URLList.add(jarURL);
//		jarURL = tomcatPlgn.getEntry( "commons-collections.jar" );
//		System.out.println("Hibernate Plugin: Tomcat plugin: URL = " + jarURL );		
//		URLList.add(jarURL);		
		
		
		// List all files under "jpaclassfiles" directory of this plugin
		/*Enumeration jpaFiles = jpabundle.getEntryPaths( 
				CommonConstant.JPA_CLASSES );
		while ( jpaFiles.hasMoreElements() )
		{
			String fileName = (String) jpaFiles.nextElement();
			if ( isDriverFile( fileName ) )
			{
				if ( ! FileList.contains( fileName ))
				{
					// This is a new file not previously added to URL list
					FileList.add( fileName );
					URL fileURL = jpabundle.getEntry( fileName );
					URLList.add(fileURL);

					System.out.println("JPA Plugin: found JAR/Zip file: " + 
							fileName + ": URL=" + fileURL );
				}
			}
		}
		*/
		
		// List all files under "hibclassfiles" directory of this plugin
/*		Enumeration hiblibs = hibbundle.getEntryPaths( 
				CommonConstant.HIBERNATE_LIBS );
		while ( hiblibs.hasMoreElements() )
		{
			String fileName = (String) hiblibs.nextElement();
			if ( isDriverFile( fileName ) )
			{
				if ( ! FileList.contains( fileName ))
				{
					// This is a new file not previously added to URL list
					FileList.add( fileName );
					URL fileURL = hibbundle.getEntry( fileName );
					URLList.add(fileURL);

					System.out.println("Hibernate Plugin: found JAR/Zip file: " + 
							fileName + ": URL=" + fileURL );
				}
			}
		}		
	*/

		Enumeration jpa_root = jpabundle.getEntryPaths("/");
		while ( jpa_root.hasMoreElements() )
		{
			String fileName = (String) jpa_root.nextElement();
			if ( isDriverFile( fileName ) )
			{
				if ( ! FileList.contains( fileName ))
				{
					// This is a new file not previously added to URL list
					FileList.add( fileName );
					URL fileURL = jpabundle.getEntry( fileName );
					URLList.add(fileURL);

					System.out.println("JPA root: found JAR/Zip file: " + 
							fileName + ": URL=" + fileURL );
				}
			}
		}		

		
		
		
		//URL fileMeta=jpabundle.getEntry(PU_meta);
	/*try{
		String PU_meta="C:\\App\\Java\\birt-runtime-2_3_0\\ReportEngine\\plugins\\org.eclipse.birt.report.data.oda.jpa_2.0.0\\";
		
		FileList.add(PU_meta);
		File f=new File(PU_meta);
		URL fileMeta=f.toURI().toURL();
		URLList.add(fileMeta);
		System.out.println("META: " + fileMeta.getPath());
		
		URL raiz=jpabundle.getEntry("/");
		FileList.add("/");
		URLList.add(raiz);
		
		
	}catch(Exception e){
		System.out.println(e.getMessage());
	}*/
		//FileList.add("C:\App\Java\birt-runtime-2_3_0\ReportEngine\plugins\org.eclipse.birt.report.data.oda.jpa_2.0.0");
		FileList.add( CommonConstant.PERSISTENCE_XML );
		URL fileURL3 = jpabundle.getEntry( CommonConstant.PERSISTENCE_XML );
		URLList.add(fileURL3);
		System.out.println("Persistence.xml: add folder for standalone: " + 
				CommonConstant.PERSISTENCE_XML  + ": URL=" + fileURL3 );
		/*FileList.add( CommonConstant.JPA_CLASSES );
		URL fileURL = jpabundle.getEntry( CommonConstant.JPA_CLASSES );
		URLList.add(fileURL);
		*/
		//FileList.add( CommonConstant.JPA_CLASSES );
		//Enumeration fileURL2 = jpabundle.getEntryPaths( CommonConstant.JPA_LIBS);
		//URLList.add(fileURL2);
		
		//System.out.println("JPA Plugin: add folder for standalone classes file: " + 
		//		CommonConstant.JPA_CLASSES + ": URL=" + fileURL );
		/*System.out.println("JPA Plugin: add folder for standalone lib file: " + 
				CommonConstant.JPA_LIBS + ": URL=" + fileURL2 );*/
	
		//Enumeration i=jpabundle.getEntryPaths("/");
		System.out.println("****** FileList*********");
		for(int i=0;i<FileList.size();i++){
			System.out.println("File: "+FileList.get(i));
		}
		
		System.out.println("\n****** URLList*********");
		for(int k=0;k<URLList.size();k++){
			System.out.println("URL: "+URLList.get(k));
		}

		return;
	}//END refreshURLs   method
	/*
	 *
	 */
	static boolean isDriverFile( String fileName )
	{
		String lcName = fileName.toLowerCase();
		return lcName.endsWith(".jar") || lcName.endsWith(".zip");
	}	
		
	/*********************************************************************************
	 * 
	 * 
	 *********************************************************************************/
	 
	/**
 	* @param object EntityBean 
 	*/
	public static String getBeanName(Object object) 
	{
		Class<?> target = object.getClass();
		return target.getName();
	}
	/**
	 * 
	 */
	public static String[] getFieldTypes(String bean) 
	{
		Object object = null;
		try {
			object = stringToClass(bean).newInstance();
		} catch (Exception ex) {
		}

		String[] fieldTypes = null;
		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(object.getClass(), Object.class);
		} catch (IntrospectionException iex) {
			System.out.println("Couldn't Introspection " + getBeanName(object));
		}
		PropertyDescriptor[] properties = bi.getPropertyDescriptors();
		fieldTypes = new String[properties.length];
		for (int i = 0; i < properties.length; i++) {
			Class<?> class_ = properties[i].getPropertyType();
			if (class_ == null)
				continue;
			fieldTypes[i] = class_.getName();
		}
		return fieldTypes;
	}
	/*
	 *  
	 */
	public static String getFieldType(String bean, String fieldName)
	throws OdaException
	{	
		String type = null;
		
		String[] fieldTypes = getFieldTypes(bean); 
		String[] fieldNames = getFieldNames(bean);	
		
		for(int i = 0 ; i<fieldTypes.length ;i++)
			if( fieldNames[i].compareTo(fieldName) == 0   )
				type = fieldTypes[i];
		if( type == null )
			throw new OdaException( "FIELD NOT FOUND : "+fieldName+" not found in "+bean ); //$NON-NLS-1$
			
		return type ;
	}
	/**
 	* 
 	*/
	public static String[] getFieldNames(String bean)
	throws OdaException
	{
		Object object = null;
		try {
			object = stringToClass(bean).newInstance();
		} catch (Exception ex) {
		}

		String[] fieldNames = null;
		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(object.getClass(), Object.class);
		} catch (IntrospectionException iex) {
			throw new OdaException( "Couldn't Introspection " + getBeanName(object) ); //$NON-NLS-1$
		}
		PropertyDescriptor[] properties = bi.getPropertyDescriptors();
		fieldNames = new String[properties.length];

		for (int i = 0; i < properties.length; i++) {
			fieldNames[i] = properties[i].getName();
		}
		return fieldNames;
	}
	/**
 	* 
 	*/
	private static Class<?> stringToClass(String className) 
	{
		Class<?> cl = null;
		try {
			cl = Class.forName(className);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return cl;
	}
	/*
	 * 
	 */
	public static List<Node> scanPersistenceXML(String pathPersistnceXML)
	throws Exception
	{	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder        builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(pathPersistnceXML);
	 
	    List<Node> nodes = new ArrayList<Node>() ;
	    // find all nodes
	    findAllNodes( doc.getChildNodes() , nodes );
	 
	    return nodes; 
	}
	/*
	 * Recursivity for find all persistence.xml nodes
	 */
	private static void findAllNodes(NodeList childNodes, List<Node> nodes){
		if( childNodes.getLength()<= 0 )
			return;
		for( int i = 0 ; i<childNodes.getLength() ; i++){
			String nodeName = childNodes.item(i).getNodeName();
			if( !nodeName.equals("#text") && !nodeName.equals("#comment"))
				nodes.add( childNodes.item(i) );
			findAllNodes( childNodes.item(i).getChildNodes(), nodes );
		}
	}
	/*
	 * 
	 */
	public static List<Node> findNodeByName( String pathPersistenceXML, String nameNode )
	throws Exception //throws OdaException
	{
		List<Node> nodes = new ArrayList<Node>();
		List<Node> temp = new ArrayList<Node>();
		temp = scanPersistenceXML(pathPersistenceXML);
		Iterator<Node> it = temp.iterator();
		while(it.hasNext()){
			Node node = it.next(); 
			if( node.getNodeName().equals(nameNode) )
				nodes.add(node);
		}
		if( nodes.size()<=0 )
			throw new OdaException( Messages.getString("JPAUtil.NODE_NOT_FOUND") );
		
		return nodes ; 
	}
	/*
	 * 
	 */
	public static String findEntityOnPersistenceXML( String entity, List<Node> classNodeList )
	throws OdaException
	{
		String answer = null ;
		for( Node node : classNodeList ){
			String s = node.getTextContent();
			StringTokenizer st = new StringTokenizer( s, ".");
			String token = "";
			// find the last token which should be the entity name
			while( st.hasMoreTokens() ) 
				token = st.nextToken();
			if( token.trim().equals(entity) ) 
				answer = s ;	
		}
		if( answer==null )
			throw new OdaException( Messages.getString("JPAUtil.NODE_NOT_FOUND") );
		return answer ; 
	}
	/*
	 * Get only the Type, for example:
	 * prepareFildType("java.lang.String")
	 * Answer : "String"
	 */
	public static String prepareFieldType( String largeType )
	{
		String type = "" ;
		StringTokenizer st = new StringTokenizer(largeType, ".");
		// find the last token which should has the type
		while( st.hasMoreTokens() )
			type = st.nextToken();
		return type; 
	}	
}// END JPAUtil class 
