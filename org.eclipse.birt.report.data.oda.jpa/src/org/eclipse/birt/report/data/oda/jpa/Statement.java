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
    public static String[] extractColumns(final String qry) {
       	int fromPosition = qry.toLowerCase().indexOf("from");
        int selectPosition = qry.toLowerCase().indexOf("select");
           if (selectPosition >= 0) {
           	String columns = qry.substring(selectPosition + 6,fromPosition);
               StringTokenizer st = new StringTokenizer(columns,",");
               List columnList = new ArrayList();
               while (st.hasMoreTokens()) {
                   columnList.add(st.nextToken().trim());
               }
               return (String[]) columnList.toArray(new String[0]);
           } else {
           	return null;
           }
    }
	/**
	 * 
	 */    
	public void setQuery(String query){
		this.query = query ;
	}

	/**
	 * 
	 */
	
	
	
    public void prepare( String qry ) // throws OdaException
	{
		//Query qry = null;
		//Test the connection
		//testConnection( );
		//holds the column types
		ArrayList arColsType = new ArrayList();
		ArrayList arCols = new ArrayList();
	    ArrayList arColClass = new ArrayList();
	    
	  
		//holds the column names, also used for labels
		String[] props = null;	// tiene nombre de los fields
		String[] props2 = null;	// tiene tipos  de los fields
		try{
           
			//Session hibsession = HibernateUtil.currentSession();
			//Create a Hibernate Query	
			qry = qry.replaceAll("[\\n\\r]+"," ");
			qry = qry.trim();
			setQuery(qry);
			//qry = hibsession.createQuery(query);
			
			//Get the list of return types from the query
			//Type[] qryReturnTypes = qry.getReturnTypes();
			// obtengo los entity de la consulta JPQL ya colocada con el setQuery
			String[] qryReturnTypes = getReturnTypes();	
			
			//When specifing the HQL "from object" the returned type is a Hibernate EntityType
			//When the columns are specified the returned values are normal data types
			//The first half of this if statment handles the EntityType, the else handles the
			//other case.
			//We are not handling multipe result sets.
			if( qryReturnTypes.length > 0 ){ //&& qryReturnTypes[0].isEntityType()){
				//### implementar el isEntityType()
				for(int j=0; j< qryReturnTypes.length; j++){
					//Get the classname and use utility function to retrieve data types
					String clsName=qryReturnTypes[j];
					//props holds the column names
					//props = HibernateUtil.getHibernateProp(clsName);
					props = JPAUtil.getFieldNames( clsName ) ; // obtiene el nombre de todos los fields del bean
					props2 = JPAUtil.getFieldTypes(clsName ) ; // obtiene el tipo de todos los fields del bean
					
					for( int x = 0; x < props.length; x++){
					//	String propType = HibernateUtil.getHibernatePropTypes(clsName, props[x]);
						//Verify that the data type is valid
						// if( DataTypes.isValidType(propType)){	//## implementar "isValidType"
							arColsType.add(props2[x]);	// almacena los tipos de los field
							//Only do this on Entity Types so we dont have a name collision
							arCols.add(props[x]);		// almacena el nombre del field
							arColClass.add(clsName);	// almacena los nobres de los beans 
						//}else{
						//	throw new OdaException( Messages.getString("Statement.SOURCE_DATA_ERROR") );
						//}
					}
				}
			}else{
				
				// por que pone como condicion (por que solo a la posicin [0])el en if "isEntityType" 
				// sino se viene para aqui
				// eso quiere decir que tambien acepta otras tablas que no sean entitys con HB
				//Extract the column names from the query
				props = extractColumns( this.query );
				//Get the return types from the Type[]
				for(int t=0; t < qryReturnTypes.length; t++){
					//Verify that the data type is valid
					//if( DataTypes.isValidType(qryReturnTypes[t].getName())){
						arColsType.add( qryReturnTypes[t] );
						arCols.add(props[t]);
					//}else{
						//throw new OdaException( Messages.getString("Statement.SOURCE_DATA_ERROR") );
					//}					
				}
				
			}
		}catch(Exception e){
			//throw new OdaException( e.getLocalizedMessage() );
		}
		//this example does not enforce unique column names
		//Create a new ResultSetMetaData object passing in the column names and data types
		
		//Have to remove . which BIRT does not allow
		//almacena el nombre de los fields de todos los entitys
		String[] arLabels = (String[])arCols.toArray(new String[arCols.size()]);
		for(int j=0; j < arLabels.length; j++){
			arLabels[j] = arLabels[j].replace('.', ':');
		}
		//(String[])arCols.toArray(new String[arCols.size()])
		
		// la creacion del ResultSetMetaData queda igual
		// por ahora llamo a mifuncion imprimir con todos los parametros del ResultSetMetadata
		printResultSetMetaData( arLabels,
				(String[])arColsType.toArray(new String[arColsType.size()]),
				arLabels, 
				(String[])arColClass.toArray(new String[arColClass.size()])
		);
		/*
		this.resultSetMetaData = new ResultSetMetaData( arLabels,
				(String[])arColsType.toArray(new String[arColsType.size()]),
				arLabels, 
				(String[])arColClass.toArray(new String[arColClass.size()])
					);
		*/			
		//query is saved for execution
		// this.query = query;		// arriba ya le hago un setQuery
		
	}
	/**
	 * 
	 */	
	public void printResultSetMetaData( String[] labelsField, String[] fieldType, 
										String[] labelsField2, String[] classEntity){
		System.out.println("Entity\tFieldType\tFieldName\tlabel");
		for(int i =0 ; i<labelsField.length ; i++)
			System.out.println(""+classEntity[i]+"\t"+fieldType[i]+"\t"+
								labelsField[i]+"\t"+labelsField2[i]);
	
	}
	/**
 	* return the entitys of the query 
 	*/
	public String[] getReturnTypes(){
		String qry = this.query ;
		 int fromPosition = qry.toLowerCase().indexOf("from");
         int wherePosition = qry.toLowerCase().indexOf("where");
         
         if(wherePosition<0)	// if do not exist sentence where
         	wherePosition = qry.length();
         
         System.out.println("posicion del from: "+fromPosition);
         System.out.println("posicion del where: "+wherePosition);
         
         if (fromPosition >= 0) {
           	String entitys = qry.substring(fromPosition + 4,wherePosition);
           		System.out.println(entitys);
               
            StringTokenizer st = new StringTokenizer(entitys,",");
            StringTokenizer st2 = null ;
            List columnList = new ArrayList();
           	
            while (st.hasMoreTokens()) {
            	st2 = new StringTokenizer( st.nextToken().trim() );
            	columnList.add( st2.nextToken().trim() );
            }
            return (String[]) columnList.toArray(new String[0]);
         } else {
           	return null;
         }
	}	
	
	
	
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

			
	  
   	//public IResultSet executeQuery( ) throws OdaException
   	public IResultSet executeQuery( ) // throws OdaException
	{		
		String[] qryReturnTypes = null;
		List rst = null;

		try{
			String qryStr = this.query;
			qryStr = qryStr.replaceAll("[\\n\\r]+"," ");			
			qryStr.trim();
			//Create the Hibernate query, notice that we are using the query prepared
			//Session hibsession = HibernateUtil.currentSession();
			EntityManager em = JPAUtil.currentSession();
			// Query qry = hibsession.createQuery(qryStr);
			Query qry = em.createQuery(qryStr) ;
			//use the query list method to return the resuls in a List object
			rst = qry.getResultList();	// resultado de la consulta JPQL 
			qryReturnTypes = getReturnTypes();		
			
		}catch(Exception e){
			//throw new OdaException( e.getLocalizedMessage() );
		}	
		
		//create a new ResultSet Ojbect passing in the row set and the meta data and the
		//Hibernate query return types
		// por ahora no retornamos nada pero contruimos el ResultSet con todos sus parametros
		// return new ResultSet( rst, getMetaData(), qryReturnTypes );
		
		// por ahora no la Metadata ya que la imprimimos con printResultSetMetadata 
		printResultSet( rst, qryReturnTypes );
		return new ResultSet(rst, this.getMetaData(),);
	}
	
	public void printResultSet (List list, String[] qryReturnTypes){
		
		System.out.println("**********************************");
		System.out.println("Consulta JPQL:");
		System.out.println(query);
		System.out.println("Resultados de la consulta JPQL:");
		Object[]registro  = null ; 
		for(int i = 0 ; i<list.size() ; i++){
			registro = (String[])list.get(i); 
			for(int j = 0 ; j<registro.length ;j++)
				System.out.print(""+registro[j]+"\t");
			System.out.println();
		}
		
		System.out.println("**********************************");
		System.out.println("Imprimiendo los entitys de qryReturnTypes:");
		for(int k = 0 ; k<qryReturnTypes.length ; k++)
			System.out.println(" - "+qryReturnTypes[k] );
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


