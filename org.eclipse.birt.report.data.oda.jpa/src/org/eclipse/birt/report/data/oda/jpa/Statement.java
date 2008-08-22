/**
 * @(#)Statement.java
 */

package org.eclipse.birt.report.data.oda.jpa;

import org.eclipse.birt.report.data.oda.jpa.DataTypes;
import org.eclipse.birt.report.data.oda.jpa.JPAUtil;
import org.eclipse.birt.report.data.oda.jpa.Messages;
import org.eclipse.birt.report.data.oda.jpa.ResultSet;
import org.eclipse.birt.report.data.oda.jpa.ResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.hibernate.Session;
import org.hibernate.type.Type;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


	

public class Statement  implements IQuery
{
	
	private IConnection connection = null;
	private IResultSetMetaData resultSetMetaData = null;
	private static String query;
	private int m_maxRows;
	
    public Statement() {
    	
    }    
    /**
	 * 
	 */    
	private void setQuery(String query){
		this.query = query ;
	}
	/**
	 * 
	 */	
    public void prepare( String qry ) throws OdaException
	{
		//Test the connection
		testConnection( );
		
		List<String> arColsType = new ArrayList<String>();	// holds the  column types
		List<String> arColsName = new ArrayList<String>();	// holds the  column name
	    List<String> arColEntity = new ArrayList<String>();	// holds the  column class
	    	  	
		// reading all persistence.xml class Nodes 
		List<Node> classNodes = JPAUtil.findNodeByName(ClassLoaderProxy.PERSISTENCE_XML, "class");
		try{
			qry = qry.replaceAll("[\\n\\r]+"," ").trim();
			
			List<List<String>> columnList = new ArrayList<List<String>>();
			columnList = extractColumns(qry);
			
			List<List<String>> entityList = new ArrayList<List<String>>();
			entityList = getReturnEntities(qry);
			//NOTA: por ahora supongo que siempre habra columas en el SQL 
			for(List<String> column : columnList){
				String entityName = "";
				String entityAlias = column.get(0);
				String columnName = column.get(1);
				
				for(List<String> entity : entityList ){
					if( entity.get(1).equals(entityAlias) )
						entityName = entity.get(0);
				}
				//the entityNameLarge is used only for the Bean Introspection 
				String entityNameLarge = JPAUtil.findEntityOnPersistenceXML(entityName, classNodes);
				String columnTypeLarge = JPAUtil.getFieldType( entityNameLarge, columnName );
				String columnType = JPAUtil.prepareFieldType(columnTypeLarge);
				//Verify that the data type is valid
				if( DataTypes.isValidType(columnType))
				{
					arColsType.add(columnType); 
					arColsName.add(columnName);
					arColEntity.add(entityName);
				}else{
					throw new OdaException( Messages.getString("Statement.SOURCE_DATA_ERROR") );
				}
			}//End "for" of column
		}catch(Exception e){
			throw new OdaException( e.getLocalizedMessage() );
		}			
		//Have to remove . which BIRT does not allow
		String[] arLabels = (String[])arColsName.toArray(new String[arColsName.size()]);
		
		for(int j=0; j < arLabels.length; j++){
			arLabels[j] = arLabels[j].replace('.', ':');
		}
		// save the query
		setQuery(qry);
	
		this.resultSetMetaData = 
			new ResultSetMetaData( 
			arLabels,	
			(String[])arColsType.toArray(new String[arColsType.size()]),
			arLabels, 
			(String[])arColEntity.toArray(new String[arColEntity.size()]) 
		);
					
	} // END prepare 
	/*
     * 
     */
    public static List< List<String> > extractColumns(final String qry)
    throws OdaException
    {   
       	int selectPosition = qry.toLowerCase().indexOf("select");
       	int fromPosition = qry.toLowerCase().indexOf("from");
           
           if (selectPosition >= 0) {
           	   String columns = qry.substring(selectPosition + 6,fromPosition);
               StringTokenizer st = new StringTokenizer(columns,",");
               StringTokenizer st2 = null;
               
               List<List<String> > columnList = new ArrayList< List<String> >();
               
               while (st.hasMoreTokens()) {
            	   st2 = new StringTokenizer( st.nextToken().trim(), "." );
            	   List<String>column = new ArrayList<String>();
                   column.add( st2.nextToken() );
                   column.add( st2.nextToken() );
                   columnList.add(column);
               }
               /*
               for(List<String> l : columnList){
            	   for(String s: l)
            		   System.out.print(s+" ");
            	   System.out.println();   
               }
               */
               return columnList ;
           } else {
        	   throw new OdaException( Messages.getString("Statement.QUERY_STRUCTURE_ERROR") );        	   
        	   return null;
           }
    }
	/**
 	* return the entities of the query 
 	*/
	public List< List<String> > getReturnEntities( String query )
	throws OdaException
	{	
		String qry = query ;
		 int fromPosition = qry.toLowerCase().indexOf("from");
         int wherePosition = qry.toLowerCase().indexOf("where");
         
         if(wherePosition<0)	// if do not exist the where sentence 
         	wherePosition = qry.length();
         
         if (fromPosition >= 6) {
           	String entitys = qry.substring(fromPosition + 4,wherePosition);
            StringTokenizer st = new StringTokenizer(entitys,",");
            StringTokenizer st2 = null ;
            List< List<String> >entityList = new ArrayList<List<String>>();
            
            while (st.hasMoreTokens()) {
            	st2 = new StringTokenizer( st.nextToken().trim() );
            	
            	List<String> entity = new ArrayList<String>();
            	entity.add( st2.nextToken().trim() );
            	entity.add( st2.nextToken().trim() );
            	
            	entityList.add( entity );
            }
            /*
            for(List<String> l : entityList){
            	for(String s: l)
            		System.out.print( s+" " );
            	System.out.println( );
            }
            */
            return entityList;
         } else {
        	throw new OdaException( Messages.getString("Statement.QUERY_STRUCTURE_ERROR") );
           	return null;
         }
	} 
	/**
	 * 
	 *
	 */
   	public IResultSet executeQuery( ) throws OdaException
	{		
		String[] qryReturnEntities = null;
		List<String> rst = null;
		try{
			String qryStr = this.query;
			qryStr = qryStr.replaceAll("[\\n\\r]+"," ");			
			qryStr.trim();
			EntityManager em = JPAUtil.currentSession();
			Query qry = em.createQuery(qryStr) ;
			//use the query list method to return the results in a List object
			rst = (List<String>)qry.getResultList();	
			
			List<List<String>> entitiesList = getReturnEntities(this.query);
			List<String>list = new ArrayList<String>();
			
			for(List<String> entity : entitiesList)
				list.add( entity.get(0) );
			
			qryReturnEntities = (String[])list.toArray(new String[list.size()]);
			
		}catch(Exception e){
			throw new OdaException( e.getLocalizedMessage() );
		}
		printResultSet( rst, qryReturnEntities );
		//create a new ResultSet Object passing in the row set and the meta data and the
		//query return entities
		return new ResultSet( rst, getMetaData(), qryReturnEntities );
	}
   	/*
   	 * 
   	 */
	public void printResultSet (List list, String[] qryReturnTypes){
		/*
		Iterator iter = list.iterator();
		System.out.println("******** Resultado de Consulta *************");
		while(iter.hasNext())
			System.out.println(iter.next());
		*/
		 
		System.out.println("************** Printing Query Result ****************");
		System.out.println("-----------------------------------------------");
		for(int i = 1 ; i<= resultSetMetaData.getColumnCount() ; i++ )
			System.out.print(""+resultSetMetaData.getColumnName(i)+"\t" );
		System.out.println("");
		System.out.println("-----------------------------------------------");
		
		Object[] registro  = null ; 
		for(int i = 0 ; i<list.size() ; i++){
			if( resultSetMetaData.getColumnCount() == 1 )
				System.out.print(""+list.get(i)+"\t");
			else{
				registro = (Object[])list.get(i);
				for(int j = 0 ; j< registro.length ;j++)
					System.out.print(""+registro[j]+"\t");
			}	
			System.out.println();
		}
	}
	/*****************************************************************************
	 *
	 *
	 *****************************************************************************/
	 
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#close()
	 */
	public void close( ) throws OdaException
	{
		connection = null;
		resultSetMetaData = null;
		m_maxRows = 0;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setMaxRows(int)
	 */
	public void setMaxRows( int max ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#getMaxRows()
	 */
	public int getMaxRows( ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty( String name, String value ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#getMetaData()
	 */
	public IResultSetMetaData getMetaData( ) throws OdaException
	{
		return this.resultSetMetaData;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setInt(java.lang.String, int)
	 */
	public void setInt( String parameterName, int value ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setInt(int, int)
	 */
	public void setInt( int parameterId, int value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setDouble(java.lang.String,
	 *      double)
	 */
	public void setDouble( String parameterName, double value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setDouble(int, double)
	 */
	public void setDouble( int parameterId, double value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setBigDecimal(java.lang.String,
	 *      java.math.BigDecimal)
	 */
	public void setBigDecimal( String parameterName, BigDecimal value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setBigDecimal(int,
	 *      java.math.BigDecimal)
	 */
	public void setBigDecimal( int parameterId, BigDecimal value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setString(java.lang.String,
	 *      java.lang.String)
	 */
	public void setString( String parameterName, String value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setString(int,
	 *      java.lang.String)
	 */
	public void setString( int parameterId, String value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setDate(java.lang.String,
	 *      java.sql.Date)
	 */
	public void setDate( String parameterName, Date value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setDate(int, java.sql.Date)
	 */
	public void setDate( int parameterId, Date value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setBoolean(java.lang.String,
	 *      boolean)
	 */
	public void setBoolean( String parameterName, boolean value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setBoolean(int, boolean)
	 */
	public void setBoolean( int parameterId, boolean value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setNull(java.lang.String,
	 *      )
	 */
	public void setNull( String parameterName ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setNull(int)
	 */
	public void setNull( int parameterId ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setTime(java.lang.String,
	 *      java.sql.Time)
	 */
	public void setTime( String parameterName, Time value ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setTime(int, java.sql.Time)
	 */
	public void setTime( int parameterId, Time value ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setTimestamp(java.lang.String,
	 *      java.sql.Timestamp)
	 */
	public void setTimestamp( String parameterName, Timestamp value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setTimestamp(int,
	 *      java.sql.Timestamp)
	 */
	public void setTimestamp( int parameterId, Timestamp value )
			throws OdaException
	{
		throw new UnsupportedOperationException ();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#findInParameter(java.lang.String)
	 */
	public int findInParameter( String parameterName ) throws OdaException
	{
		throw new UnsupportedOperationException ();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#getParameterMetaData()
	 */
	public IParameterMetaData getParameterMetaData( ) throws OdaException
	{
		//try
		//{
		//	return new ParameterMetaData();
		//}
		//catch ( OdaException e )
		//{
		//	throw new OdaException( e.getLocalizedMessage() );
		//}
		//throw new UnsupportedOperationException ();
		return(null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#clearInParameters()
	 */
	public void clearInParameters() throws OdaException
	{
		throw new UnsupportedOperationException ();
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#setSortSpec(org.eclipse.birt.data.oda.SortSpec)
	 */
	public void setSortSpec( SortSpec sortBy ) throws OdaException
	{
		throw new UnsupportedOperationException ();

	}
	
	private void testConnection( ) throws OdaException
	{
		if ( connection.isOpen( ) == false )
			throw new OdaException( Messages.getString("Common.CONNECTION_HAS_NOT_OPEN") ); //$NON-NLS-1$
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.birt.data.oda.IQuery#getSortSpec()
	 */
	public SortSpec getSortSpec( ) throws OdaException
	{
		//Sorting will be handled by BIRT
		return( null );
	}

	public void setAppContext(Object obj) throws OdaException
    {
		throw new UnsupportedOperationException ();
    }

}


