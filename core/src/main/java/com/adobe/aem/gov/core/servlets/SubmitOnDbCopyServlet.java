package com.adobe.aem.gov.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;


import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

/**
* Servlet that writes some sample content into the response. It is mounted for
* all resources of a specific Sling resource type. The
* {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
* idempotent. For write operations use the {@link SlingAllMethodsServlet}.
*/
@Component(
    service = { Servlet.class },
    property={"sling.servlet.methods=POST",
            "sling.servlet.paths=/bin/submitOnDb"
            //"sling.servlet.resourceTypes="+ "gov/components/submitOnDb"
         }
)
    public class SubmitOnDbCopyServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 4498376868274173005L;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(target = "(&(objectclass=javax.sql.DataSource)(datasource.name=localhost))")
    private DataSource dataSource;

    @SlingObject
    private Resource myRes;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Reference
    Repository repository;

    @Override
    protected void doPost(final @NotNull SlingHttpServletRequest req,
                      final @NotNull SlingHttpServletResponse resp) throws IOException {

        String resPath = req.getParameter("path").replace("_jcr_content","jcr:content");
                //"/content/gov/it/key-desk/jcr:content/root/container/container_890427998/submitondb/myRecords";
        String name = "";
        String message = "";

    resourceResolver = req.getResourceResolver();
    myRes = Objects.requireNonNull(resourceResolver.getResource(resPath)).getChild("myRecords");
    int count= 0;

    log.debug("#########################################Sto nel doPost()###################################################");



   try(Connection con = getConnection()) {
       javax.jcr.Session session = repository.login( new SimpleCredentials("admin", "admin".toCharArray()));

       Iterator<Resource> recordsIterator = myRes.listChildren();

        while (recordsIterator.hasNext()) {
            Resource res = recordsIterator.next();
            count++;

            name = res.getValueMap().get("fullName").toString();
            message = res.getValueMap().get("message").toString();
           // con.setAutoCommit(true);
            String query = "INSERT INTO yuridb.users (name, message) VALUES (?, ?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, name);
            preparedStmt.setString (2, message);
            preparedStmt.execute();
            resp.getWriter().write("### Messagio inserito con successo!  Hai inserito per l'utente : " + name + " ### il messaggio: " + message + "\n");

            //Node root = session.getRootNode();
            Node node = session.getNode(res.getPath());
            node.remove();
            node.getSession().save();
            resp.getWriter().write("****** nodo eliminato");
        }

    } catch(SQLException | RepositoryException e) {
        resp.getWriter().write(e.getMessage());
        resp.getWriter().write("\n###  ERROR DATA : " + name + " ### " + message + "\n");
        log.debug("########ERROR: COULD NOT Connect to MYSQL################");

        log.error(e.getMessage());
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
        if(count == 0) {
            resp.getWriter().write("Inserisci almeno un record");
        } else {
            resp.getWriter().write(" done!");
        }
    }
    public Connection getConnection() {
    log.debug("### Getting Connection ");
    Connection con;
    try {

        con = dataSource.getConnection();
        log.debug("### got connection");
        return con;
    } catch(Exception e) {
        log.error("### not able to get connection " + e.getMessage());

    }
    return null;
    }

}
