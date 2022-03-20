package com.adobe.aem.gov.core.servlets;


import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import javax.sql.DataSource;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = { Servlet.class }, property={"sling.servlet.methods=get", "sling.servlet.paths=/bin/messages"})

@ServiceDescription("Get Meggases Servlet")

public class MessagesServlet extends SlingAllMethodsServlet {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(target = "(&(objectclass=javax.sql.DataSource)(datasource.name=localhost))")
    private DataSource dataSource;

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        log.debug("### inside my Messages Servlet");
        Connection con = getConnection();
        try {
            Statement st = con.createStatement();
            String query = "SELECT * FROM yuridb.users";
            log.debug("###  Got Result Set" + query);
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                resp.getWriter().write("Name: " + rs.getString("name") + "\n");
                resp.getWriter().write("Message: " + rs.getString("message") + "\n");
            }
        } catch(SQLException e) {
        // TODO Auto-generated catch block
        log.debug(e.getMessage());
        }
       
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("### inside my Inserting data into yuridb");
        Connection con = getConnection();
        String name = req.getParameter("name");
        String message = req.getParameter("message");
        try {
            con.setAutoCommit(true);

            String query = "INSERT INTO yuridb.users (name, message) VALUES (?, ?)";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, name);
            preparedStmt.setString (2, message);
            preparedStmt.execute();

            resp.getWriter().write("###  INSERT DATA : " + name + " ### " + message + "\n");
            con.close();
          } catch(SQLException e) {
            resp.getWriter().write(e.getMessage());
            resp.getWriter().write("\n###  ERROR DATA : " + name + " ### " + message + "\n");
            // TODO Auto-generated catch block
            log.debug(e.getMessage());
          }
        resp.setContentType("text/plain");
        resp.getWriter().write("done!");
    }


    public Connection getConnection() {
        log.debug("### Getting Connection ");
        Connection con = null;
        try {

          con = dataSource.getConnection();
          log.debug("### got connection");
          return con;
        } catch(Exception e) {
          log.debug("### not able to get connection " + e.getMessage());

        }
        return null;
    }



}