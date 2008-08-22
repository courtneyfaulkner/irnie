/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.data.oda.jpa;


import java.sql.Types;
import java.util.HashMap;

import  org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * This class hosts the information of data types that are supported by flat
 * file driver
 */

public final class DataTypes
{

	public static final int INT = Types.INTEGER;
	public static final int DOUBLE = Types.DOUBLE;
	public static final int STRING = Types.VARCHAR;
	public static final int DATE = Types.DATE;
	public static final int TIME = Types.TIME;
	public static final int TIMESTAMP = Types.TIMESTAMP;
	public static final int BLOB = Types.BLOB;
	public static final int BIGDECIMAL = Types.NUMERIC;
	public static final int NULL = Types.NULL;
	
	public static final int LONG = Types.BIGINT;

	private static HashMap typeStringIntPair = new HashMap( );

	static
	{
		typeStringIntPair.put( "INTEGER", new Integer( INT ) ); //$NON-NLS-1$
		typeStringIntPair.put( "INT", new Integer( INT ) ); //$NON-NLS-1$
		typeStringIntPair.put( "DOUBLE", new Integer( DOUBLE ) ); //$NON-NLS-1$
		typeStringIntPair.put( "STRING", new Integer( STRING ) ); //$NON-NLS-1$
		typeStringIntPair.put( "DATE", new Integer( DATE ) ); //$NON-NLS-1$
		typeStringIntPair.put( "TIME", new Integer( TIME ) ); //$NON-NLS-1$
		typeStringIntPair.put( "TIMESTAMP", new Integer( TIMESTAMP ) ); //$NON-NLS-1$
		typeStringIntPair.put( "BLOB", new Integer( BLOB ) ); //$NON-NLS-1$
		typeStringIntPair.put( "BIGDECIMAL", new Integer( BIGDECIMAL ) ); //$NON-NLS-1$
		typeStringIntPair.put( "NULL", new Integer ( NULL ) );
		
		typeStringIntPair.put( "LONG", new Integer ( LONG ) );
	}

	/**
	 * Return the int which stands for the type specified by input argument
	 *
	 * @param typeName
	 *            the String value of a Type
	 * @return the int which stands for the type specified by input typeName
	 * @throws OdaException
	 *             Once the input arguement is not a valid type name
	 */
	public static int getType( String typeName ) throws OdaException
	{
		String preparedTypeName = typeName.trim( ).toUpperCase( );
		if ( typeStringIntPair.containsKey( preparedTypeName ) )
			return ( (Integer) typeStringIntPair.get( preparedTypeName ) ).intValue( );
		throw new OdaException( Messages.getString("DataTypes.TYPE_NAME_INVALID") + typeName); //$NON-NLS-1$
	}

	/**
	 * Evalute whether an input String is a valid type that is supported by driver
	 *
	 * @param typeName
	 * @return
	 */
	public static boolean isValidType( String typeName )
	{
		return typeStringIntPair.containsKey( typeName.trim( ).toUpperCase( ) );
	}

	private DataTypes( )
	{
	}
}