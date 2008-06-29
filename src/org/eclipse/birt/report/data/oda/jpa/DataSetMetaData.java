
/*******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.data.oda.hibernate;


import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * This class implement IDataSetMetaData interface of Oda.
 */

public class DataSetMetaData implements IDataSetMetaData
{
	private IConnection connection;

	public DataSetMetaData(IConnection conn)
	{
		connection = conn;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getConnection()
	 */
	public IConnection getConnection( ) throws OdaException
	{
		return connection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getDataSourceObjects(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public IResultSet getDataSourceObjects( String catalog, String schema,
			String object, String version ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getDataSourceMajorVersion()
	 */
	public int getDataSourceMajorVersion( ) throws OdaException
	{
		return CommonConstant.DRIVER_MAJOR_VERSION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getDataSourceMinorVersion()
	 */
	public int getDataSourceMinorVersion( ) throws OdaException
	{
		return CommonConstant.DRIVER_MINOR_VERSION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getDataSourceProductName()
	 */
	public String getDataSourceProductName( ) throws OdaException
	{
		return CommonConstant.DRIVER_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getDataSourceProductVersion()
	 */
	public String getDataSourceProductVersion( ) throws OdaException
	{
		String pv = new Integer(CommonConstant.DRIVER_MAJOR_VERSION).toString() + "." + new Integer(CommonConstant.DRIVER_MINOR_VERSION).toString(); 
		return pv;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getSQLStateType()
	 */
	public int getSQLStateType( ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsNamedResultSets()
	 */
	public boolean supportsNamedResultSets( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsInParameters()
	 */
	public boolean supportsInParameters( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#supportsOutParameters()
	 */
	public boolean supportsOutParameters( ) throws OdaException
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.data.oda.IDataSetMetaData#getSortMode()
	 */
	public int getSortMode( )
	{
		return (sortModeNone);
		//throw new UnsupportedOperationException ();
	}

}
