package org.eclipse.birt.report.data.oda.jpa;

import java.util.Properties;

import javax.persistence.PersistenceException;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;

/**
 * This class implements IConnection interface of ODA.
 */

public class Connection implements IConnection {

	private static boolean isOpen = false;
	private String persistenceUnit = null;
	private String path_application = null;

	public void open(Properties connProperties) throws OdaException {

		// If the data source properties are changed the SessionFactory will
		// be rebuilt which is expensive. This was implemented this way as
		// as an example of connecting data source properties to the open
		// method.
		if (JPAUtil.isOpenConnection())
			return;

		try {
			persistenceUnit = connProperties.getProperty("PERSISTENCE_UNIT");
			path_application = connProperties.getProperty("APP_JPA") + "/";

			System.out.println("-  PersistenceUnit:" + path_application);
			System.out.println("-  JPA Application Directory:"
					+ path_application + "/");
			JPAUtil.setApplication(path_application);
			JPAUtil.refreshURLs();
			JPAUtil.constructEntityManagerFactory(persistenceUnit);
			JPAUtil.setConnection(this);
			Connection.isOpen = true;
		} catch (PersistenceException e) {
			throw new OdaException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new OdaException(e.getLocalizedMessage());
		}
	}

	public void close() throws OdaException {

	}

	public boolean isOpen() throws OdaException {
		return Connection.isOpen;
	}

	public IDataSetMetaData getMetaData(String dataSetType) throws OdaException {
		return new DataSetMetaData(this);
	}

	public IQuery newQuery(String dataSetType) throws OdaException {
		if (!JPAUtil.isOpenConnection())
			throw new OdaException(
					Messages.getString("Common.CONNECTION_HAS_NOT_OPEN")); //$NON-NLS-1$

		return new Statement(JPAUtil.getConnection());
	}

	public void commit() throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void setAppContext(Object obj) throws OdaException {
		throw new UnsupportedOperationException();
	}

	public void rollback() throws OdaException {
		throw new UnsupportedOperationException();
	}

	public int getMaxQueries() throws OdaException {
		return 1;
	}

	public void setLocale(ULocale arg0) throws OdaException {

	}
}