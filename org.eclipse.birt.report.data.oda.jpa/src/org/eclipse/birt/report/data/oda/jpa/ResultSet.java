package org.eclipse.birt.report.data.oda.jpa;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * This class implements IResultSet interface of ODA
 */
public class ResultSet implements IResultSet {
	private static final int CURSOR_INITIAL_VALUE = -1;
	private IResultSetMetaData resultSetMetaData = null;

	private int maxRows = 0;
	private int cursor = CURSOR_INITIAL_VALUE;
	private Iterator rowiter = null;
	private Object currentRow = null;
	private String[] qryReturnEntity = null;

	// Boolean which marks whether it is successful of last call to getXXX();
	private boolean wasNull = false;

	/**
	 * Construct
	 * 
	 * @param sData
	 *            a two dimensions array which host the data extracted from a
	 *            table.
	 * @param rSMD
	 *            the metadata of sData
	 */
	ResultSet(List rs, IResultSetMetaData rSMD, String[] qryReturnTypes) {
		// Row Data
		this.rowiter = rs.iterator();

		// Metadata
		this.resultSetMetaData = rSMD;
		// interp result set as Entity or Array
		this.qryReturnEntity = qryReturnEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getMetaData() This method does
	 * not appear to be called by the BIRT framework The getMetaData call used
	 * on IQuery is called instead
	 */
	public IResultSetMetaData getMetaData() throws OdaException {
		return this.resultSetMetaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#close()
	 */
	public void close() throws OdaException {
		this.cursor = 0;
		this.rowiter = null;
		this.resultSetMetaData = null;
		this.currentRow = null;
		this.qryReturnEntity = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#setMaxRows(int)
	 */
	public void setMaxRows(int max) throws OdaException {
		this.maxRows = max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#next()
	 */
	public boolean next() throws OdaException {
		if (this.maxRows <= 0 ? false : cursor >= this.maxRows - 1) {
			cursor = CURSOR_INITIAL_VALUE;
			return false;
		}
		if (rowiter.hasNext() == false) {

			cursor = CURSOR_INITIAL_VALUE;
			return false;
		}
		currentRow = rowiter.next();
		cursor++;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getRow()
	 */
	public int getRow() throws OdaException {
		testFetchStarted();
		return this.cursor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getString(int)
	 */
	public String getString(int index) throws OdaException {
		String result;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			result = null;
		} else {
			result = rObj.toString();
		}
		this.wasNull = (result == null);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws OdaException {
		String result;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			result = null;
		} else {
			result = rObj.toString();
		}
		this.wasNull = (result == null);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getInt(int)
	 */
	public int getInt(int index) throws OdaException {
		int result;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			result = 0;
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Integer) rObj).intValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws OdaException {
		int result;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			result = 0;
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Integer) rObj).intValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getInt(int)
	 */
	public Long getLong(int index) throws OdaException {
		Long result;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			result = new Long(0);
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Long) rObj).longValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getInt(java.lang.String)
	 */
	public Long getLong(String columnName) throws OdaException {
		Long result;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			result = new Long(0);
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Long) rObj).longValue();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDouble(int)
	 */
	public double getDouble(int index) throws OdaException {
		double result;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			result = 0;
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Double) rObj).doubleValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws OdaException {
		double result;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			result = 0;
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = ((Double) rObj).doubleValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int index) throws OdaException {
		BigDecimal result;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			result = new BigDecimal(0);
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (BigDecimal) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws OdaException {
		BigDecimal result = null;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			result = new BigDecimal(0);
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (BigDecimal) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDate(int)
	 */
	public Date getDate(int index) throws OdaException {
		Date result = null;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Date) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws OdaException {
		Date result = null;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Date) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDate(int)
	 */
	public boolean getBoolean(int index) throws OdaException {
		Boolean result = null;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Boolean) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getDate(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws OdaException {
		Boolean result = null;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Boolean) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getTime(int)
	 */
	public Time getTime(int index) throws OdaException {
		Time result = null;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Time) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws OdaException {
		Time result = null;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Time) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int index) throws OdaException {
		Timestamp result = null;
		testFetchStarted();
		Object rObj = getResult(index);
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Timestamp) rObj;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws OdaException {
		Timestamp result = null;
		testFetchStarted();
		Object rObj = getResult(findColumn(columnName));
		if (rObj == null) {
			this.wasNull = true;
		} else {
			this.wasNull = false;
			result = (Timestamp) rObj;
		}

		return result;
	}

	public IBlob getBlob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public IBlob getBlob(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public IClob getClob(int index) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public IClob getClob(String columnName) throws OdaException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#wasNull()
	 */
	public boolean wasNull() throws OdaException {
		return this.wasNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.oda.IResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws OdaException {
		for (int i = 1; i <= this.getMetaData().getColumnCount(); i++) {
			if (columnName.trim().equalsIgnoreCase(
					this.getMetaData().getColumnName(i).trim())) {
				return i;
			}
		}
		throw new OdaException(
				Messages.getString("ResultSet.COLUMN_NOT_FOUND") + columnName); //$NON-NLS-1$
	}

	/**
	 * Test if the cursor has been initialized
	 * 
	 * @throws OdaException
	 *             Once the cursor is stll not initialized
	 */
	private void testFetchStarted() throws OdaException {
		if (this.cursor < 0)
			throw new OdaException(
					Messages.getString("ResultSet.CURSOR_HAS_NOT_BEEN_INITIALIZED")); //$NON-NLS-1$
	}

	/*
	 * 
	 */
	private Object getResult(int rstcol) throws OdaException {
		Object obj = this.currentRow;
		Object value = null;

		try {
			if (getMetaData().getColumnCount() > 1) {
				Object[] values = (Object[]) obj;
				value = values[rstcol - 1];
			} else {

				if (getMetaData().getColumnCount() == 1) {
					value = obj;
				} else {
					Object[] values = (Object[]) obj;
					value = values[rstcol - 1];
				}
			}
		} catch (Exception e) {
			throw new OdaException(e.getLocalizedMessage());
		}
		return (value);
	}

	public Object getObject(int arg0) throws OdaException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String arg0) throws OdaException {
		// TODO Auto-generated method stub
		return null;
	}
}// End ResultSet