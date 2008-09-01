/**
 * Implements the user interface that specifies data source properties. This 
 * utility class specifies the page layout, sets up the controls that listen for user 
 * input, verifies the location of the JPA Application Directory, and sets up 
 * the name of Persistence Unit.    
 * @author  Alfonso Phocco Diaz
 * @version 1.0
 */

package org.eclipse.birt.report.data.oda.jpa.ui;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.SortedMap;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JpaPageHelper
{
    private WizardPage m_wizardPage;
    private PreferencePage m_propertyPage;

    private transient Text m_jpaAppLocation = null;
    private transient Button m_browsejpaButton = null;
    
    private transient Text m_PersistenceUnit = null;
    // private transient Combo m_PersistenceUnit = null;
    //private transient Text m_jpaConfig = null;
    //private transient Button m_browseJpaConfigButton = null;
    
    static final String DEFAULT_MESSAGE = "Select some existent PersitenceUnit JPA ";
    
    private static final int ERROR_FOLDER = 1;
    private static final int ERROR_EMPTY_PATH = 2;
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    
    JpaPageHelper( WizardPage page )
    {
        m_wizardPage = page;
    }

    JpaPageHelper( PreferencePage page )
    {
        m_propertyPage = page;
    }

    void createCustomControl( Composite parent )
    {
        Composite content = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout( 3, false );
        content.setLayout(layout);
        
        setupJpaLocation( content );
        setupPU( content );
    }

    String getJpaDir()
    {
        if( m_jpaAppLocation == null )
            return EMPTY_STRING;
        
        return m_jpaAppLocation.getText();
    }

    
    String getPU()
    {
        if( m_PersistenceUnit == null )
            return EMPTY_STRING;
        return m_PersistenceUnit.getText();
    }


    Properties collectCustomProperties( Properties props )
    {
        if( props == null )
            props = new Properties();
        
        // set custom driver specific properties
        //props.setProperty( "JPACONFIG", getConfig() );
        String jpa_dir=getJpaDir();
        //verifyPath(jpa_dir);	
		props.setProperty( "APP_JPA", jpa_dir );                            
        props.setProperty( "PERSISTENCE_UNIT", getPU() );

        return props;
    }
    

    void initCustomControl( Properties profileProps )
    {
        setPageComplete( true );
        setMessage( DEFAULT_MESSAGE, IMessageProvider.NONE );
        
        if( profileProps == null || profileProps.isEmpty() || 
            m_PersistenceUnit == null || m_jpaAppLocation==null )
            return;     // nothing to initialize
        
        /*Path of JPA Application*/
        String appPath = profileProps.getProperty( "APP_JPA" );
        if( appPath == null )
            appPath = EMPTY_STRING;
        //verifyPath(appPath);
        m_jpaAppLocation.setText( appPath );
        
        /*Persistence Unit, where exists as entitites for report */
        String persistenceUnit = profileProps.getProperty( "PERSISTENCE_UNIT" );
        if( persistenceUnit == null )
            persistenceUnit = EMPTY_STRING;
        m_PersistenceUnit.setText( persistenceUnit );
        
           
        verifyConfigLocation();
    }

    private void setupPU( Composite composite )
    {
        Label label = new Label( composite, SWT.NONE );
        label.setText("Insert Persistence Unit JPA :" ); //$NON-NLS-1$

        GridData data = new GridData( GridData.FILL_HORIZONTAL );

        m_PersistenceUnit = new Text( composite, SWT.BORDER );
        m_PersistenceUnit.setLayoutData( data );
        
        
        setPageComplete( true );
        m_PersistenceUnit.addModifyListener( 
            new ModifyListener()
            {    
                public void modifyText( ModifyEvent e )
                {
                    verifyPU();
                }
    
            } );

        /**
         *Here code for to locate persistence.xml and then parsing
         *and get all PersistenceUnit defined. 
         **/
    }//fin de SetupPU
    
    
    
     private void setupJpaLocation( Composite composite )
    {
        Label label = new Label( composite, SWT.NONE );
        label.setText( "Select JPA application Directory" ); //$NON-NLS-1$

        GridData data = new GridData( GridData.FILL_HORIZONTAL );

        m_jpaAppLocation = new Text( composite, SWT.BORDER );
        m_jpaAppLocation.setLayoutData( data );
        setPageComplete( false );

        m_browsejpaButton = new Button( composite, SWT.NONE );
        m_browsejpaButton.setText( "..." ); //$NON-NLS-1$
        m_browsejpaButton.addSelectionListener( 
            new SelectionAdapter()
            {
               /*
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                public void widgetSelected( SelectionEvent e )
                {
                    DirectoryDialog dialog = new DirectoryDialog( 
                    		m_jpaAppLocation.getShell() );
                    if( m_jpaAppLocation.getText() != null && 
                    		m_jpaAppLocation.getText().trim().length() > 0 )
                    {
                        dialog.setFilterPath( m_jpaAppLocation.getText() );
                    }

                    dialog.setMessage( "Select JPA application Directory" );
                    String selectedLocation = dialog.open();
                    if( selectedLocation != null )
                    {
                    	m_jpaAppLocation.setText( selectedLocation );
                    }
                }
            } );
    }

    
    /* Persitence.xml Verification */
    private int verifyConfigLocation()
    {
        int result = 0;
        //String persistence_xml=m_jpaAppLocation+"/META-INF/persistence.xml";
        String persistence_xml=getJpaDir()+"/META-INF/persistence.xml";
        //String persistence_xml=m_jpaAppLocation+"META-INF/persistence.xml";
        
        if( persistence_xml.trim().length() > 0 )
        {
            File f = new File( persistence_xml.trim() );
            if( f.exists() )
            {
                setMessage( DEFAULT_MESSAGE, IMessageProvider.NONE );
                setPageComplete( true );
            }
            else
            {
                setMessage( "Persistence.xml File Does not exist", IMessageProvider.ERROR ); //$NON-NLS-1$
                setPageComplete( false );
                result = ERROR_FOLDER;
            }
        }
        else
        {
            setMessage( "No Configuration File Entered, Using default JPA APP directory", IMessageProvider.ERROR ); //$NON-NLS-1$
            setPageComplete( true );
            result = ERROR_EMPTY_PATH;
        }
        return result;
    }//fin verifyConfigLocation


    private int verifyPU()
    {
        int result = 0;
        if( m_PersistenceUnit.getText().trim().length() > 0 )
        {
        	
        	/*Here could be using the ubication of persistnce.xml, then
        	 *parsing this,to  check if belong to this persistence.xml*/
            /*File f = new File( m_PersistenceUnit.getText().trim() );
            if( f.exists() )
            {
                setMessage( DEFAULT_MESSAGE, IMessageProvider.NONE );
                setPageComplete( true );
            }
            else
            {
                setMessage( "Configuration File Does not exist", IMessageProvider.ERROR ); //$NON-NLS-1$
                setPageComplete( false );
                result = ERROR_FOLDER;
            }*/
        }
        else
        {
            setMessage( "No Persistence Unit name Entered.", IMessageProvider.ERROR ); //$NON-NLS-1$
            setPageComplete( true );
            result = ERROR_EMPTY_PATH;
        }
        return result;
    }

 
    
    
    

    
    
    private void setPageComplete( boolean complete )
    {
        if( m_wizardPage != null )
            m_wizardPage.setPageComplete( complete );
        else if( m_propertyPage != null )
            m_propertyPage.setValid( complete );
    }
    
    private void setMessage( String newMessage, int newType )
    {
        if( m_wizardPage != null )
            m_wizardPage.setMessage( newMessage, newType );
        else if( m_propertyPage != null )
            m_propertyPage.setMessage( newMessage, newType );
    }
}
