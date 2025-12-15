package servlet;

import modele.DatabaseConnectionExterne;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/api/dbstatus")
public class DbStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Connection conn = DatabaseConnectionExterne.getConnection();
        if (conn == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"ok\": false, \"message\": \"Connexion à la DB externe introuvable\"}");
            System.err.println("DBStatus: connexion externe introuvable");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT DATABASE() AS dbname, 1 AS ok");
            if (rs.next()) {
                String dbname = rs.getString("dbname");
                out.print("{\"ok\": true, \"database\": \"" + dbname + "\"}");
                System.out.println("DBStatus: connexion OK, base active=" + dbname);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"ok\": false, \"message\": \"Requête test a échoué\"}");
                System.err.println("DBStatus: requête test a échoué");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"ok\": false, \"message\": \"" + e.getMessage() + "\"}");
            System.err.println("DBStatus: erreur lors du test SQL: " + e);
        }
    }
}
