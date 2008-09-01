package org.eclipse.birt.report.data.oda.jpa.ui;


import java.io.File;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.eclipse.datatools.connectivity.oda.design.Properties;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IQuery;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;

import org.eclipse.birt.report.data.oda.jpa.*;

public class JpaJpqlSelectionPage extends DataSetWizardPage 
{
    private transient Text queryText = null;
    private static Connection connection;
    private static boolean isOpen=false;
    //private transient DataSetHandle dataSetHandle = null;
    private transient Button queryButton = null;
    private boolean m_initialized = false;
    //private String  m_jpaconfig;
    private String m_PersistenceUnit;//persistence unit name 
    private String m_jpaAppDir;// JPA Application Directory

    private static String DEFAULT_MESSAGE = Messages.getString("wizard.defaultMessage.selectJpaClass"); //$NON-NLS-1$
    //private transient IPropertyPageContainer propertyContainer = null;

    /**
     * Default constructor
     */
    public JpaJpqlSelectionPage()
    {
        this(Messages.getString("wizard.title.jpql")); //$NON-NLS-1$
    }

    /**
     * @param pageName
     */
    public JpaJpqlSelectionPage(String pageName)
    {
        super(pageName);
        setTitle(pageName);
        setMessage(DEFAULT_MESSAGE);
    }

    
    public JpaJpqlSelectionPage( String pageName, String title,
            ImageDescriptor titleImage )
    {
        super( pageName, title, titleImage );
    }    
    
    protected DataSetDesign collectDataSetDesign( DataSetDesign design )
    {
        if( ! hasValidData() )
            return design;
        design.setQueryText( queryText.getText() );
        savePage( design );
        return design;
    }    
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSetWizardPage#collectResponseState()
     */
    protected void collectResponseState()
    {        
        super.collectResponseState();
        /* 
         * Optionally assigns custom response state, for inclusion
         * in the ODA design session response, using
         *      setResponseSessionStatus( SessionStatus status )
         *      setResponseDesignerState( DesignerState customState ); 
         */
    }    
    

  
    private boolean hasValidData()
    {
        if( queryText == null )
            return false;

        if( isPageComplete() )
        {
            return true;
        }
        setMessage( "Error Reading Query" ); 
        return false;
    }
  
    
    public void createPageCustomControl( Composite parent )
    {
        setControl( createPageControl( parent ) );
        initializeControl();
    }

    private void initializeControl()
    {

    	Properties dataSourceProps = getInitializationDesign().getDataSourceDesign().getPublicProperties();
        
        m_PersistenceUnit = dataSourceProps.getProperty("PERSISTENCE_UNIT" );
        m_jpaAppDir=dataSourceProps.getProperty("APP_JPA");
          	
        /* 
         * Optionally restores the state of a previous design session.
         * Obtains designer state, using
         *      getInitializationDesignerState(); 
         */

        DataSetDesign dataSetDesign = getInitializationDesign();
        if( dataSetDesign == null )
            return; // nothing to initialize

        String queryTextTmp = dataSetDesign.getQueryText();
        if( queryTextTmp == null )
            return; // nothing to initialize

        queryText.setText(queryTextTmp);
        this.m_initialized = false;
       	setMessage( "", NONE );
       

    } 


    /*
	 * @see org.eclipse.birt.report.designer.ui.dialogs.properties.IPropertyPage#createPageControl(org.eclipse.swt.widgets.Composite)
	 */
    public Control createPageControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        
        
		Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.getString("wizard.title.selectColumns")); //$NON-NLS-1$
        
        GridData data = new GridData(GridData.FILL_BOTH);


        queryText = new Text(composite,SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        queryText.setLayoutData(data);
        queryText.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e)
            {
            	if( m_initialized == false){
            		setPageComplete(true);
            		m_initialized = true;            		
            	}else{
            		setPageComplete(false);
            	}
            }
        });
        
        setPageComplete(true);
  
		Composite cBottom = new Composite( composite, SWT.NONE );
		cBottom.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		cBottom.setLayout( new RowLayout( ) );
        
        queryButton = new Button(cBottom, SWT.NONE);
        //queryButton.setSize(50,10);
		queryButton.setText(Messages.getString("wizard.title.verify"));//$NON-NLS-1$
		//queryButton.setLayoutData(cBottom);

		//connection=new Connection();
		// Add listener to the find button
		queryButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
					verifyQuery();
			   }
			 });        

        return composite;
    }

    boolean verifyQuery(){

    	setMessage( "Verifying Query", INFORMATION );
		setPageComplete( false );
		queryButton.setEnabled(false);
    	//Makes a connection to the ODA runtime
    	//Connection conn = new Connection( );
		//Connection conn = connection;
		
		try
		{
	        java.util.Properties prop = new java.util.Properties();
	        if( m_PersistenceUnit == null)m_PersistenceUnit = "default";
	        if( m_jpaAppDir == null)m_jpaAppDir = "";
	        
	        prop.put( "PERSISTENCE_UNIT", m_PersistenceUnit );
	        prop.put( "APP_JPA", m_jpaAppDir);
	        /*if(!isOpen){        
	        	conn.open( prop );
	        	isOpen=true;
	        }*/
	        //else
	        	//conn= new Connection();
	        Connection conn = null;
			if(!JPAUtil.isOpenConnection()){
				conn=new Connection();
				conn.open(prop);
			}
			else
				conn=JPAUtil.getConnection();
			
	        
	        
			IQuery query = conn.newQuery( "" );
			//does not actually run the query, just uses Hibernate to prepare metadata
			query.prepare( queryText.getText() );

			int columnCount = query.getMetaData( ).getColumnCount( );
			System.out.println("columnCount =" + columnCount);

			if ( columnCount == 0 ){
				setMessage( "No Columns Selected", INFORMATION );
				setPageComplete(false);
				return false;
			}
			setPageComplete(true);
		   	setMessage( "Query Verified", INFORMATION );

			return true;
		}
		catch ( OdaException e )
		{
			System.out.println( e.getMessage());
			setMessage( e.getLocalizedMessage( ), ERROR );
			
			setPageComplete(false);
			return false;
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage());
			setMessage( e.getLocalizedMessage( ), ERROR );
			setPageComplete(false);
			return false;
		}

		finally
		{
			try
			{
				queryButton.setEnabled(true);
				//System.out.println("Close Verify");
				//conn.close( );
			}
			catch ( /*Oda*/Exception e )
			{
				System.out.println( e.getMessage());
				setMessage( e.getLocalizedMessage( ), ERROR );
				setPageComplete(false);
				return false;
			}

		}

 	
    }

 

    /*
     * @see org.eclipse.birt.report.designer.ui.dialogs.properties.IPropertyPage#canLeave()
     * overridden from super
     */
    public boolean canLeave()
    {
        if(!isPageComplete())
        {
            setMessage(Messages.getString("error.selectColumns"), ERROR); 
            return false;
        }
        return true;
    }


    
    private void savePage( DataSetDesign dataSetDesign )
    {
        // obtain query's result set metadata, and update
        // the dataSetDesign with it
    	//org.eclipse.birt.report.data.oda.jpa.Activator.getDefault().stop(JPAUtil.context);
    	System.out.println("Saving page");
        IConnection conn = null;
        try
        {
            IDriver jpaDriver = new JPADriver();
            //Connection conn = jpaDriver.getConnection( "");
            /*if(isOpen)
            	conn=connection;
            else
            	conn = jpaDriver.getConnection( "");
            */
            
            //IConnection conn = null;
            /*if(!JPAUtil.isOpenConnection()){
				conn=jpaDriver.getConnection( "");
				conn.open(prop);
			}
			else
				conn=JPAUtil.getConnection();*/
			
            IResultSetMetaData metadata = 
                getResultSetMetaData( dataSetDesign.getQueryText(), conn );
            setResultSetMetaData( dataSetDesign, metadata );
        }
        catch( OdaException e )
        {
            // no result set definition available, reset in dataSetDesign
            dataSetDesign.setResultSets( null );
        }
        finally
        {
        	//System.out.println("close save");
            //closeConnection( conn );
            
        }
        
        /*
         * See DesignSessionUtil for more convenience methods
         * to define a data set design instance.  
         */     

        /*
         * Since this flatfile driver does not support
         * query parameters and properties, there are
         * no data set parameters and public/private properties
         * to specify in the data set design instance
         */
    }    
    
    
    private void closeConnection( IConnection conn )
    {
        try
        {
            if( conn != null );
                //conn.close();
        }
        catch( /*Oda*/Exception e )
        {
            // ignore
        }
    }    
    
    
    private IResultSetMetaData getResultSetMetaData( 
            String queryText,
            IConnection conn ) throws OdaException
    {
 
        java.util.Properties prop = new java.util.Properties();
        if( m_PersistenceUnit == null) m_PersistenceUnit = "default";
        if( m_jpaAppDir == null) m_jpaAppDir = "";

        prop.put( "PERSISTENCE_UNIT", m_PersistenceUnit );			
        prop.put( "APP_JPA", m_jpaAppDir);		        
        
        //System.out.println("Obteniendo ResulSetMEtadata y abriendo conexion");
        
        JPADriver jpaDriver=new JPADriver();
        if(!JPAUtil.isOpenConnection()){
			conn=jpaDriver.getConnection( "");
			conn.open(prop);
		}
		else
			conn=JPAUtil.getConnection();
        
        /*if(!isOpen){
        	conn.open( prop );
        	isOpen=true;
        }*/
		IQuery query = conn.newQuery( null );

		//Do not need to run query just prepare it.
		//System.out.println("Preparando Consulta");
		query.prepare( queryText );
		//System.out.println("GEt Meta Data");

		return query.getMetaData();
	}    
    
    private void setResultSetMetaData( DataSetDesign dataSetDesign,
            IResultSetMetaData md ) throws OdaException
    {
        ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign( md );

        ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE
                .createResultSetDefinition();
        
        resultSetDefn.setResultSetColumns( columns );

		//System.out.println("ResulSetDefn Design SET");
        // no exception; go ahead and assign to specified dataSetDesign
        dataSetDesign.setPrimaryResultSet( resultSetDefn );
        dataSetDesign.getResultSets().setDerivedMetaData( true );
    }   
    
}


