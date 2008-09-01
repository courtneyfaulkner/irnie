

package org.eclipse.birt.report.data.oda.jpa.ui;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage;
import org.eclipse.swt.widgets.Composite;

public class JpaPropertyPage extends DataSourceEditorPage
{
 
    private JpaPageHelper m_pageHelper;

    public JpaPropertyPage()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage#collectCustomProperties(java.util.Properties)
     */
    public Properties collectCustomProperties( Properties profileProps )
    {
        /* 
         * Optionally assigns a custom designer state, for inclusion
         * in the ODA design session response, using
         *      setResponseDesignerState( DesignerState customState ); 
         */

        if( m_pageHelper == null )
            return profileProps;

        return m_pageHelper.collectCustomProperties( profileProps );
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage#createAndInitCustomControl(org.eclipse.swt.widgets.Composite, java.util.Properties)
     */
    protected void createAndInitCustomControl( Composite parent, Properties profileProps )
    {
        if( m_pageHelper == null )
            m_pageHelper = new JpaPageHelper( this );

        m_pageHelper.createCustomControl( parent );

        /* 
         * Optionally hides the Test Connection button, using
         *      setPingButtonVisible( false );  
         */

        /* 
         * Optionally restores the state of a previous design session.
         * Obtains designer state, using
         *      getInitializationDesignerState(); 
         */
        m_pageHelper.initCustomControl( profileProps );
        
        if( ! isSessionEditable() )
            getControl().setEnabled( false );
    }
    
    
}
