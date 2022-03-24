package com.adobe.aem.gov.core.servlets;


import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Date;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = { Servlet.class }, 
          property={"sling.servlet.methods=*", "sling.servlet.paths=/bin/messages"})

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
        List<HashMap> messages = new ArrayList<>();
       
        try {
            Statement st = con.createStatement(); 
            String query = "SELECT * FROM users ORDER BY date DESC LIMIT 10";
            log.debug("###  Got Result Set" + query);
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) { 
                HashMap<String,String> singleMessage = new HashMap<>();
                singleMessage.put("name",rs.getString("name"));
                singleMessage.put("message",rs.getString("message"));
                singleMessage.put("date",rs.getDate("date").toString());
                messages.add(singleMessage);
            }
            
        } catch(SQLException e) {
        // TODO Auto-generated catch block
        log.debug(e.getMessage());
        }
        Gson gson = new Gson();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(messages));
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("### Messagio inserito con successo");
        Connection con = getConnection();
        String name = req.getParameter("name");
        String message = req.getParameter("message");
        try {
            con.setAutoCommit(true);
            String query = "INSERT INTO yuridb.users (name, message) VALUES (?, ?)";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, name.replace("\n", "").replace("\r", "").trim());
            preparedStmt.setString (2, message.replace("\n", "").replace("\r", "").trim()); 
            preparedStmt.execute();
            
            resp.getWriter().write("###  Hai inserito per l'utente : " + name + " ### il messaggio: " + message + "\n");
            con.close();
          } catch(SQLException e) {
            resp.getWriter().write(e.getMessage());
            resp.getWriter().write("\n###  ERROR DATA : " + name + " ### " + message + "\n");
            // TODO Auto-generated catch block
            log.debug(e.getMessage());
          }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
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