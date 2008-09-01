package org.eclipse.birt.report.data.oda.jpa;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import java.util.Properties;
//import java.net.*;
import javax.persistence.PersistenceException;


/**
 * This class implements IConnection interface of ODA.
 */

public class Connection implements IConnection
{

	private static boolean isOpen = false;
	private String persistenceUnit = null;
	private String path_application = null;
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#open(java.util.Properties)
	 */
	//public void open( Properties connProperties ) throws OdaException
	public void open( Properties connProperties ) throws OdaException
	{
	
	   //If the data source properties are changed the SessionFactory will
	   //be rebuilt which is expensive.  This was implemented this way as
	   //as an example of connecting data source properties to the open method.
		//if(isOpen)
			//return;
		if(JPAUtil.isOpenConnection())
			return;
		
		//if(JPAUtil.isEntityManagerFactoryValid())
			//return;
		try{
			persistenceUnit=connProperties.getProperty( "PERSISTENCE_UNIT" );
			path_application=connProperties.getProperty( "APP_JPA" )+"/";
						
			System.out.println("-  PersistenceUnit:"+path_application);
			System.out.println("-  JPA Application Directory:"+path_application+"/");
			JPAUtil.setApplication(path_application);
			//if(JPAUtil.getApplication()!="")
			JPAUtil.refreshURLs();
			//if(!JPAUtil.isOpenConnection())
			JPAUtil.constructEntityManagerFactory(persistenceUnit );
			//Object testSession = JPAUtil.currentSession();
		    JPAUtil.setConnection(this);
			Connection.isOpen = true;
		}catch(PersistenceException e){
			throw new OdaException( e.getLocalizedMessage());
		}catch(Exception e){
			throw new OdaException( e.getLocalizedMessage());
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#close()
	 */
	public void close( ) throws OdaException
	{
		//this.isOpen = false;
		/*try{
			JPAUtil.closeSession();
			JPAUtil.closeFactory();
		}catch(PersistenceException e){
			throw new OdaException( e.getLocalizedMessage());
		}
		catch(Exception e){
			throw new OdaException( e.getLocalizedMessage());
		}*/

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#isOpen()
	 */
	public boolean isOpen( ) throws OdaException
	{
		//return this.isOpen;
		return Connection.isOpen;
	}

	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType )
			throws OdaException
	{
		return new DataSetMetaData(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#createStatement(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType )
			throws OdaException
	{
		/*if ( !isOpen( ) )
			throw new OdaException( Messages.getString("Common.CONNECTION_HAS_NOT_OPEN") );  //$NON-NLS-1$
		*/
		//return new Statement(this);
		
		if ( !JPAUtil.isOpenConnection())
			throw new OdaException( Messages.getString("Common.CONNECTION_HAS_NOT_OPEN") );  //$NON-NLS-1$

		return new Statement(JPAUtil.getConnection());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#commit()
	 */
	public void commit( ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	public void setAppContext(Object obj) throws OdaException
    {
		throw new UnsupportedOperationException ();
    }
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IConnection#rollback()
	 */
	public void rollback( ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException
	{
		return 1;
	}
	
	
	
}