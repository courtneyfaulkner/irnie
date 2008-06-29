/*******************************************************************************
 *******************************************************************************/

package org.eclipse.birt.report.data.oda.jpa;


import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * This class implements interface IResultSetMetaData of ODA
 */

public class ResultSetMetaData implements IResultSetMetaData
{

	private String[] columnName = null;
	private String[] columnType = null;
	private String[] columnLabel = null;
	private String[] columnClass = null;

	ResultSetMetaData( String[] cName, String[] cType, String[] cLabel, String[] cColumn )
			throws OdaException
	{
		if ( cName == null )
			throw new OdaException( Messages.getString("Common.ARGUMENT_CANNOT_BE_NULL") ); //$NON-NLS-1$
		this.columnName = cName;
		this.columnType = cType;
		this.columnLabel = cLabel;
		this.columnClass = cColumn;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount( ) throws OdaException
	{
		return this.columnName.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException
	{
		assertIndexValid( index );
		return this.columnName[index - 1].trim( );
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException
	{
		assertIndexValid( index );
		//"null" in lower case is the mark of "null value". We should not use
		// "equalsIgnoreCase"
		//here for "null" is not a keyword so that we cannot prevent user from
		// using "null" as labels of
		//certain columns.
		if ( this.columnLabel == null
				|| this.columnLabel[index - 1].equals( "null" ) ) //$NON-NLS-1$
			return this.getColumnName( index );

		return this.columnLabel[index - 1].trim( );
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException
	{
		assertIndexValid( index );
		//get the integer value of the data type specified
		return (this.columnType[index - 1] == null)? DataTypes.NULL : DataTypes.getType( columnType[index - 1] ) ;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
		assertIndexValid( index );
		return (this.columnType == null)? "NULL": columnType[index - 1].trim( );
	}

	
	public String getColumnClass( int index ) throws OdaException
	{
		assertIndexValid( index );
		return ( this.columnClass[index - 1] ) ;
	}	

	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException
	{
		return( 0 );
		//throw new UnsupportedOperationException ();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getPrecision(int)
	 */
	public int getPrecision( int index ) throws OdaException
	{
		return -1;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#getScale(int)
	 */
	public int getScale( int index ) throws OdaException
	{
		return -1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IResultSetMetaData#isNullable(int)
	 */
	public int isNullable( int index ) throws OdaException
	{
		return 0;
	}

	/**
	 * Evaluate whether the value of an index is valid
	 *
	 * @param index
	 *            the value of an index
	 * @throws OdaException
	 *             if the value is
	 */
	private void assertIndexValid( int index ) throws OdaException
	{
		if ( index > getColumnCount( ) || index < 1 )
			throw new OdaException( Messages.getString("ResultSetMetaData.INVALID_COLUMN_INDEX") + index ); //$NON-NLS-1$
	}
}