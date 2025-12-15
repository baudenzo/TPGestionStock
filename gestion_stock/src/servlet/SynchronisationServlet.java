package servlet;

import service.SynchronisationService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/api/sync")
public class SynchronisationServlet extends HttpServlet {
    private SynchronisationService syncService;

    @Override
    public void init() throws ServletException {
        syncService = new SynchronisationService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("idEntreprise") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Non authentifié\"}");
                return;
            }

            int idEntreprise = (int) session.getAttribute("idEntreprise");
            String type = request.getParameter("type");

            boolean succes = false;
            String message = "";

            if ("push".equals(type)) {
                succes = syncService.pushVersBaseExterne(idEntreprise);
                message = succes ? "Synchronisation PUSH réussie" : "Erreur lors du PUSH";
            } else if ("pull".equals(type)) {
                succes = syncService.pullDepuisBaseExterne(idEntreprise);
                message = succes ? "Synchronisation PULL réussie" : "Erreur lors du PULL";
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Type de sync invalide\"}");
                return;
            }

            JSONObject json = new JSONObject();
            json.put("success", succes);
            json.put("message", message);

            out.print(json.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}