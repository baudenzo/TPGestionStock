package servlet;

import modele.Entreprise;
import modele.EntrepriseDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.io.FileInputStream;
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

        // Dev auto-login: if config.properties contains dev.mode=true and request comes
        // from localhost,
        // create a dev session and return success immediately. DO NOT ENABLE IN
        // PRODUCTION.
        try {
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                p.load(fis);
            } catch (Exception e) {
                // ignore - absence of config file means dev mode disabled
            }
            String devMode = p.getProperty("dev.mode", "false");
            String remote = request.getRemoteAddr();
            if ("true".equalsIgnoreCase(devMode)
                    && ("127.0.0.1".equals(remote) || "::1".equals(remote) || "0:0:0:0:0:0:0:1".equals(remote))) {
                Entreprise localEnt = new Entreprise();
                localEnt.setIdEntreprise(0);
                localEnt.setNomEntreprise("DEV - AutoLogin");
                localEnt.setCodeEntreprise("LOCAL-DEV");

                HttpSession session = request.getSession();
                session.setAttribute("entreprise", localEnt);
                session.setAttribute("idEntreprise", localEnt.getIdEntreprise());

                JSONObject json = new JSONObject();
                json.put("success", true);
                json.put("message", "Auto-login dev activé");
                json.put("entreprise", new JSONObject()
                        .put("id", localEnt.getIdEntreprise())
                        .put("nom", localEnt.getNomEntreprise())
                        .put("code", localEnt.getCodeEntreprise()));
                out.print(json.toString());
                return;
            }
        } catch (Exception e) {
            // don't prevent normal flow if something goes wrong reading the config
            System.err.println("Erreur vérification dev.mode: " + e.getMessage());
        }

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