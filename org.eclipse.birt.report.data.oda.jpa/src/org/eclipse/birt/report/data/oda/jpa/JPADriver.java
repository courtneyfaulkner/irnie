/*******************************************************************************
 *  
 *
 *******************************************************************************/

package org.eclipse.birt.report.data.oda.jpa;


import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.LogConfiguration;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * This class implements IDriver interface of ODA.
 */

public final class JPADriver implements IDriver
{

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IDriver#getConnection(java.lang.String)
	 */
	public IConnection getConnection( String connectionClassName )
			throws OdaException
	{
		return new Connection( );
	}

	public void setAppContext(Object obj)
      		throws OdaException
    {
		//no implementation
		//This method could be used to retrieve information passed in from the
		//application context.
    }

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDriver#getMaxConnections()
	 */
	public int getMaxConnections() throws OdaException
	{
		//No limit is imposed on the connections
		return(0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDriver#setLogConfiguration(org.eclipse.birt.data.oda.LogConfiguration)
	 */
	public void setLogConfiguration(LogConfiguration logConfig)
			throws OdaException
	{
		// no op since this driver does not use ODA logging facility
	}
}