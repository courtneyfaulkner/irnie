package org.eclipse.birt.report.data.oda.jpa;

/**
 * This is the class which hosts the definitions of package-wide constants.
 */

final class CommonConstant
{
	public static final String DELIMITER_COMMA = ","; 
	public static final String DELIMITER_SPACE = " "; 
	public static final String DELIMITER_DOUBLEQUOTE = "\""; 
	public static final String KEYWORD_SELECT = "SELECT"; 
	public static final String KEYWORD_FROM = "FROM"; 
	public static final String KEYWORD_AS = "AS"; 
	public static final String KEYWORD_ASTERISK = "*";
	public static final String DRIVER_NAME = "ODA JPA DRIVER";
	/* Here are located the classes and JPA config files 
	 * obs: here must be the directory: META-INF, here is exactly located 
	 * the JPA config files	 */
	public static final String JPA_CLASSES = "jpafiles";	
	//public static final String JPA_CONFIG = "jpafiles";
	public static final String JPA_LIBS = "lib";	

	public static final int DRIVER_MAJOR_VERSION = 0;
	public static final int DRIVER_MINOR_VERSION = 1;
	public static final int ODA_MAJOR_VERSION = 2;
	public static final int ODA_MINOR_VERSION = 0;
	public static final int MaxConnections = 0;
	public static final int MaxStatements = 0;


	/**
	 * Private contructure which ensure the non-instantiatial of the class
	 *
	 */
	private CommonConstant( )
	{
	}
}