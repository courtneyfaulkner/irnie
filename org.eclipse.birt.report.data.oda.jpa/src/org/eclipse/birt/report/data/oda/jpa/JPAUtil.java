package org.eclipse.birt.report.data.oda.jpa;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver;
import org.eclipse.core.runtime.Platform;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.EntityNotFoundException;
//import javax.persistence.EntityExistException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/*in Hibernate used something like: */
/*
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.EntityMode;
*/

import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

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
     * isn't necessary get a name of peristence files because according to
     * specification is located into directory named META-INF, but is necessary
     * have like parameter the PersistenceUnit name  
     **/
	//private static synchronized void initEntityManagerFactory(String persistenceUnit,Map map) 
	private static synchronized void initEntityManagerFactory(String persistenceUnit) 
		throws PersistenceException {
//>>>>>>> .theirs
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



	/**
 	* @param object EntityBean 
 	*/
	public static String getBeanName(Object object) {
		Class target = object.getClass();
		return target.getName();
	}

	/**
	 * 
	 */
	public static String[] getFieldTypes(String bean) {
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
			Class class_ = properties[i].getPropertyType();
			if (class_ == null)
				continue;
			fieldTypes[i] = class_.getName();
		}
		return fieldTypes;
	}
	/*
	 * 
	 */
	public static String getFieldType(String bean, String fieldName) {
		
		String type = ""; 
		
		String[] fieldTypes = getFieldTypes(bean); 
		String[] fieldNames = getFieldNames(bean);	
		
		for(int i = 0 ; i<fieldTypes.length ;i++)
			if( fieldNames[i].compareTo(fieldName) == 0   )
				type = fieldTypes[i];
		
		return type ;
	}
	/**
 	* 
 	*/
	public static String[] getFieldNames(String bean) {

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
			System.out.println("Couldn't Introspection " + getBeanName(object));
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
	private static Class stringToClass(String className) {
		Class cl = null;
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
	public static List<String> scanPersistenceXML(String path)throws Exception{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder        builder = factory.newDocumentBuilder();
	    //Document doc = builder.parse("http://localhost:8080/chapter02/people.xml");
	    Document doc = builder.parse(path);
	    NodeList childNodes = doc.getChildNodes();
	    
	    List<String> entityClassList = new ArrayList<String>();
	    findNodeClass( childNodes , entityClassList );
	    
	    System.out.println("----- persistence.xml Entitys -----");
	    Iterator it = entityClassList.iterator() ; 
	    
	    while(it.hasNext())
	    	System.out.println( it.next() );
	    /*
	    for(String s : entityClasssList )
	    	System.out.println( s );
	    */
	    System.out.println("Scaning OK !!");
	    return entityClassList; 
	} 
	/*
	 * Recursividad para encontrar los nodos class
	 */
	private static void findNodeClass( NodeList childNodes,List<String> entityClassList ){
		if( childNodes.getLength() <= 0  )
			return ;
		for(int i = 0 ; i < childNodes.getLength(); i++){
			if( childNodes.item(i).getNodeName().toLowerCase().equals("class") )
				entityClassList.add( childNodes.item(i).getTextContent() );
			findNodeClass( childNodes.item(i).getChildNodes(), entityClassList );
		}//End for 
	}
	/*
	 * 
	 */
	public static String findEntityOnPersistenceXML( String entity, List<String> entityClassList ){
		String answer = "" ;
		for( String s : entityClassList ){
			StringTokenizer st = new StringTokenizer( s, ".");
			String token = "";
			// find the last token which should be the entity name
			while( st.hasMoreTokens() ) 
				token = st.nextToken();
			if( token.trim().equals(entity) ) 
				answer = s ;
			else
				///$$$$$$$%%%%%%%%%%% OJO DEBERIA LANZARSE UN EXCEPCION %%%%%%%%$$$$$$$$$$$
				///$$$$$$$%%%%%%%%%%% OJO DEBERIA LANZARSE UN EXCEPCION %%%%%%%%$$$$$$$$$$$
				System.out.println(""+entity+" do not exist into persistence.xml");
		}
			
		return answer ; 
	}





	//Get all properties for the given class
	public static  String[] getJPAProp( String className){
		 //In hibernate is like this:
		/*Session session = HibernateUtil.currentSession();
		SessionFactory sf = session.getSessionFactory();
		String[] hibClassProps = sf.getClassMetadata(className).getPropertyNames();
		return( jpaClassProps);*/

		
		/*EntityManagerFactory session = JPAUtil.currentSession();
		EntityManager em = session.getEntity();**/
		//In JPA es: ???
		/* Code here, about get Class metadata  
		return  ?; */
		String[] b={};
		return  b;
		
	}    

	//Get type for given property
	public static String  getJPAPropTypes(String className, String propName){

		
        //In hibernate is like this:
		/*org.hibernate.type.Type hibClassProps = sf.getClassMetadata(className).getPropertyType(propName);
		return (hibClassProps.getName());*/
				
		//In JPA es: ???
		/* Code here, about get Class metadata  
		 *return  ?; */
		 
		 EntityManager session = JPAUtil.currentSession();
		//EntityManagerFactory emf = session.getEntityMangerFactory();
		return new String();

	}    

	//Get type for given property
	public static Object  getJPAPropVal(Object instObj, String className, String propName){

		EntityManager session = JPAUtil.currentSession();
		//EntityManagerFactory emf = session.getEntityManagerFactory();
		/* In hibernate is like this: 
		SessionFactory sf = session.getSessionFactory();
		Object jpaObj = sf.getClassMetadata(className).getPropertyValue(instObj, propName, EntityMode.POJO);
		return(hibObj);
		*/
		
		//In JPA es: ???
		/* Code here, about get Class metadata  
		return  ?; */
		return new Object();

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
	}	    
	public static void refreshURLs()
	{
		Bundle jdbcbundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jdbc" );
		Bundle jpabundle = Platform.getBundle( "org.eclipse.birt.report.data.oda.jpa" );
		//Bundle tomcatPlgn = Platform.getBundle( "org.eclipse.tomcat" );
		
		
		
		FileList.clear();
		URLList.clear();
		if ( jdbcbundle == null )
			return;			// init failed

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

					System.out.println("JDBC Plugin: found JAR/Zip file: " + 
							fileName + ": URL=" + fileURL );
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
		Enumeration jpaFiles = jpabundle.getEntryPaths( 
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
		
		
		
		FileList.add( CommonConstant.JPA_CLASSES );
		URL fileURL = jpabundle.getEntry( CommonConstant.JPA_CLASSES );
		URLList.add(fileURL);	
		System.out.println("JPA Plugin: add folder for standalone classes file: " + 
				CommonConstant.JPA_CLASSES + ": URL=" + fileURL );
	


		return;
	}   
	static boolean isDriverFile( String fileName )
	{
		String lcName = fileName.toLowerCase();
		return lcName.endsWith(".jar") || lcName.endsWith(".zip");
	}	


}
