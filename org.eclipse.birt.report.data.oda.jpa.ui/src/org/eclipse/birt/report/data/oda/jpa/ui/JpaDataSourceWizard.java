package org.eclipse.birt.report.data.oda.jpa.ui;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class JpaDataSourceWizard extends DataSourceWizardPage{

    private JpaPageHelper m_pageHelper;
    private Properties m_jpaProperties;
    
    public JpaDataSourceWizard( String pageName )
    {
        super( pageName );
        setMessage( "Changing the configuration will cause a rebuilding of the JPA Session Factory \n It is advisable to set once for a system If left blank jpafiles directory will be used" );
 
    
    }


    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPageCustomControl( Composite parent )
    {
        if( m_pageHelper == null )
            m_pageHelper = new JpaPageHelper( this );
        m_pageHelper.createCustomControl( parent );
        m_pageHelper.initCustomControl( m_jpaProperties );   // in case init was called before create 

        /* 
         * Optionally hides the Test Connection button, using
         *      setPingButtonVisible( false );  
         */
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#initPageCustomControl(java.util.Properties)
     */
    public void setInitialProperties( Properties dataSourceProps )
    {
        m_jpaProperties = dataSourceProps;
        if( m_pageHelper == null )
            return;     // ignore, wait till createPageCustomControl to initialize
        m_pageHelper.initCustomControl( m_jpaProperties );        
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#collectCustomProperties()
     */
    public Properties collectCustomProperties()
    {
        /* 
         * Optionally assign a custom designer state, for inclusion
         * in the ODA design session response, using
         * setResponseDesignerState( DesignerState customState ); 
         */
        
        if( m_pageHelper != null ) 
            return m_pageHelper.collectCustomProperties( m_jpaProperties );

        return ( m_jpaProperties != null ) ?
                    m_jpaProperties : new Properties();
    }

}
