package servlet;

import modele.Entreprise;
import modele.EntrepriseDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/api/auth")
public class AuthentificationServlet extends HttpServlet {
    private EntrepriseDAO entrepriseDAO;

    @Override
    public void init() throws ServletException {
        entrepriseDAO = new EntrepriseDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String codeEntreprise = request.getParameter("codeEntreprise");
            String nomUtilisateur = request.getParameter("nomUtilisateur");
            String motDePasse = request.getParameter("motDePasse");

            if (codeEntreprise == null || nomUtilisateur == null || motDePasse == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Paramètres manquants\"}");
                return;
            }

            Entreprise entreprise = entrepriseDAO.authentifierEntreprise(
                    codeEntreprise, nomUtilisateur, motDePasse);

            if (entreprise != null) {
                HttpSession session = request.getSession();
                session.setAttribute("entreprise", entreprise);
                session.setAttribute("idEntreprise", entreprise.getIdEntreprise());

                JSONObject json = new JSONObject();
                json.put("success", true);
                json.put("message", "Connexion réussie");
                json.put("entreprise", new JSONObject()
                        .put("id", entreprise.getIdEntreprise())
                        .put("nom", entreprise.getNomEntreprise())
                        .put("code", entreprise.getCodeEntreprise()));

                out.print(json.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"success\": false, \"message\": \"Identifiants incorrects\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}