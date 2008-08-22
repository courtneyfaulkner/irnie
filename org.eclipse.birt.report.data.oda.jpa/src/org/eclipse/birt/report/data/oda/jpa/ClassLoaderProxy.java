package org.eclipse.birt.report.data.oda.jpa;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

//Estos son arcchivos de utileria de netbeans
/*import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;*/

//Librerias de Eclipse para manejo de archivos
//import
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClassLoaderProxy extends ClassLoader 
  {
    private static final String CLASS = ClassLoaderProxy.class.getName();
    private static final Logger logger = Logger.getLogger(CLASS);
    
    private static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();
        
    private static final XPath XPATH = XPATH_FACTORY.newXPath();
        
    private static final XPathExpression XPATH_ENTITY_PU_NODE;
    private static final XPathExpression XPATH_ENTITY_CLASS_TEXT;
    
    public static final String PERSISTENCE_XML = "bin/META-INF/persistence.xml";
    
    private static final String MASTER_URL_SUFFIX = "it-tidalwave-bluemarine-persistence.jar!/" + PERSISTENCE_XML;
    
    private final FileSystem mfs = FileUtil.createMemoryFileSystem();
    
    private URL persistenceXMLurl;
    
    enum Filter
      {
        MASTER
          {
            public boolean filter (URL url)
              {
                return url.toExternalForm().endsWith(MASTER_URL_SUFFIX);
              }
          },
          
        OTHERS
          {
            public boolean filter (URL url)
              {
                return !url.toExternalForm().endsWith(MASTER_URL_SUFFIX);
              }
          };
        
        public abstract boolean filter (URL url);
      }    
        
    static
      {
        try
          {
            XPATH_ENTITY_PU_NODE = XPATH.compile("//persistence/persistence-unit");
            XPATH_ENTITY_CLASS_TEXT = XPATH.compile("//persistence/persistence-unit/class/text()");
          }
        catch (XPathExpressionException e) 
          {
            throw new ExceptionInInitializerError(e);
          }
      }
    
    public ClassLoaderProxy (final ClassLoader parent)
      {
        super(parent);
      }

    @Override
    public Enumeration<URL> getResources (final String name) 
      throws IOException 
      {
        if (PERSISTENCE_XML.equals(name))
          {
            if (persistenceXMLurl == null)
              {
                try 
                  {
                    final String persistenceXml = scanPersistenceXML();
                    logger.fine("persistence.xml " + persistenceXml);
                    
                    // FIXME: this must be fixed 
//                    final FileObject file = mfs.getRoot().createFolder("META-INF").createData("persistence.xml");
//                    final PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.getOutputStream()));
//                    pw.print(persistenceXml);
//                    pw.close();
//                    persistenceXMLurl = file.getURL();
                    
                    // The base directory must be empty since Hibernate will scan it searching for classes.
                    final File file = new File(System.getProperty("java.io.tmpdir") + "/blueMarinePU/" + PERSISTENCE_XML);
                    file.getParentFile().mkdirs();
                    final PrintWriter pw = new PrintWriter(new FileWriter(file));
                    pw.print(persistenceXml);
                    pw.close();
                    persistenceXMLurl = new URL("file://" + file.getAbsolutePath());
                    logger.info("URL: " + persistenceXMLurl);
                  } 
                catch (ParserConfigurationException e) 
                  {
                    throw new IOException(e.toString());
                  } 
                catch (SAXException e) 
                  {
                    throw new IOException(e.toString());
                  } 
                catch (XPathExpressionException e) 
                  {
                    throw new IOException(e.toString());
                  } 
                catch (TransformerConfigurationException e)
                  {
                    throw new IOException(e.toString());
                  } 
                catch (TransformerException e) 
                  {
                    throw new IOException(e.toString());
                  } 
              }
            
            return new Enumeration<URL>() 
              {
                URL url = persistenceXMLurl;
                
                public boolean hasMoreElements() 
                  {
                    return url != null;
                  }

                public URL nextElement() 
                  {
                    final URL url2 = url;
                    url = null;
                    return url2;
                  }
              };
          }
        
        return super.getResources(name);
      }
    
    /***************************************************************************
     *
     * Scans for all the <code>persistence.xml</code> file in modules and applies
     * them.
     *
     **************************************************************************/
    private String scanPersistenceXML()
      throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerConfigurationException, TransformerException
      {
        logger.info("scanPersistenceXML()");
        final DocumentBuilder builder = DOC_BUILDER_FACTORY.newDocumentBuilder();
        DOC_BUILDER_FACTORY.setNamespaceAware(true); 
        
        final URL masterURL = findPersistenceXMLs(Filter.MASTER).iterator().next();
        logger.fine(String.format(">>>> master persistence.xml: %s", masterURL));
        final Document masterDocument = builder.parse(masterURL.toExternalForm());
        final Node puNode = (Node)XPATH_ENTITY_PU_NODE.evaluate(masterDocument, XPathConstants.NODE);
              
        for (final URL url : findPersistenceXMLs(Filter.OTHERS))
          {
            logger.info(String.format(">>>> other persistence.xml: %s", url));
            final Document document = builder.parse(url.toExternalForm());
            final NodeList nodes = (NodeList)XPATH_ENTITY_CLASS_TEXT.evaluate(document, XPathConstants.NODESET);
              
            for (int i = 0; i < nodes.getLength(); i++) 
              {
                final String entityClassName = nodes.item(i).getNodeValue();
                logger.info(String.format(">>>>>>>> entity class: %s", entityClassName));
                
                if (i == 0)
                  {
                    puNode.appendChild(masterDocument.createTextNode("\n"));
                    puNode.appendChild(masterDocument.createComment(" from " + url.toExternalForm().replaceAll(".*/cluster/modules/", "") + " "));
                    puNode.appendChild(masterDocument.createTextNode("\n"));
                  }
                
                final Node child = masterDocument.createElement("class");
                child.appendChild(masterDocument.createTextNode(entityClassName));
                puNode.appendChild(child);
                puNode.appendChild(masterDocument.createTextNode("\n"));
              }
          }
        
        return toString(masterDocument);
      }
    
    /***************************************************************************
     *
     **************************************************************************/
    private Collection<URL> findPersistenceXMLs (final Filter filter)
      throws IOException
      {
        final Collection<URL> result = new ArrayList<URL>();
        
        for (final Enumeration<URL> e = super.getResources(PERSISTENCE_XML); e.hasMoreElements(); )
          {
            final URL url = e.nextElement();
            
            if (filter.filter(url))
              {
                result.add(url);
              }
          }
        
        return result;
      }
    
    /***************************************************************************
     *
     **************************************************************************/
    private static String toString (final Node node) 
      throws TransformerConfigurationException, TransformerException 
      {
        final Source source = new DOMSource(node);
        final StringWriter stringWriter = new StringWriter();
        final Result result = new StreamResult(stringWriter);
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        return stringWriter.getBuffer().toString();
      }
  }
