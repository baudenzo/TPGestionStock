<?php
session_start();
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action'])) {
    header('Content-Type: application/json');
    
    $action = $_POST['action'];
    
    if ($action === 'login') {
        $codeEntreprise = $_POST['codeEntreprise'] ?? '';
        $nomUtilisateur = $_POST['nomUtilisateur'] ?? '';
        $motDePasse = $_POST['motDePasse'] ?? '';
        
        try {
            $pdo = getConnection();
            $sql = "SELECT e.*, u.id_utilisateur, u.nom_utilisateur 
                    FROM entreprises e 
                    JOIN utilisateurs_externes u ON e.id_entreprise = u.id_entreprise 
                    WHERE e.code_entreprise = :code 
                    AND u.nom_utilisateur = :user 
                    AND u.mot_de_passe = :pass";
            
            $stmt = $pdo->prepare($sql);
            $stmt->execute([
                'code' => $codeEntreprise,
                'user' => $nomUtilisateur,
                'pass' => $motDePasse
            ]);
            
            $result = $stmt->fetch();
            
            if ($result) {
                $_SESSION['entreprise_id'] = $result['id_entreprise'];
                $_SESSION['entreprise_nom'] = $result['nom_entreprise'];
                $_SESSION['utilisateur'] = $result['nom_utilisateur'];
                
                echo json_encode([
                    'success' => true,
                    'message' => 'Connexion réussie',
                    'entreprise' => $result['nom_entreprise']
                ]);
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Identifiants incorrects'
                ]);
            }
        } catch (PDOException $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Erreur : ' . $e->getMessage()
            ]);
        }
        exit;
    }
    
    if ($action === 'getProduits') {
        if (!isset($_SESSION['entreprise_id'])) {
            echo json_encode(['success' => false, 'message' => 'Non connecté']);
            exit;
        }
        
        try {
            $pdo = getConnection();
            $sql = "SELECT * FROM produits_externes WHERE id_entreprise = :id ORDER BY code_produit";
            $stmt = $pdo->prepare($sql);
            $stmt->execute(['id' => $_SESSION['entreprise_id']]);
            $produits = $stmt->fetchAll();
            
            foreach ($produits as &$produit) {
                $stockProjecte = $produit['stock_actuel'] - 
                    ($produit['consommation_journaliere'] * $produit['delai_livraison_jours']);
                
                $produit['necessiteReappro'] = $stockProjecte <= $produit['stock_securite'];
                $produit['quantiteReappro'] = max(0, $produit['stock_maximum'] - $stockProjecte);
                $produit['pourcentage'] = ($produit['stock_actuel'] / $produit['stock_maximum']) * 100;
                
                if ($produit['stock_actuel'] <= $produit['stock_securite']) {
                    $produit['niveau'] = 'CRITIQUE';
                } elseif ($produit['stock_actuel'] <= $produit['stock_securite'] + 10) {
                    $produit['niveau'] = 'ATTENTION';
                } else {
                    $produit['niveau'] = 'NORMAL';
                }
            }
            
            echo json_encode([
                'success' => true,
                'produits' => $produits
            ]);
        } catch (PDOException $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Erreur : ' . $e->getMessage()
            ]);
        }
        exit;
    }
    
    if ($action === 'getReappros') {
        if (!isset($_SESSION['entreprise_id'])) {
            echo json_encode(['success' => false, 'message' => 'Non connecté']);
            exit;
        }
        
        try {
            $pdo = getConnection();
            $sql = "SELECT r.*, p.code_produit, p.description_produit 
                    FROM reapprovisionnements r 
                    JOIN produits_externes p ON r.id_produit_externe = p.id_produit 
                    WHERE p.id_entreprise = :id AND r.statut = 'EN_COURS' 
                    ORDER BY r.date_livraison_prevue";
            
            $stmt = $pdo->prepare($sql);
            $stmt->execute(['id' => $_SESSION['entreprise_id']]);
            $reappros = $stmt->fetchAll();
            
            echo json_encode([
                'success' => true,
                'reappros' => $reappros
            ]);
        } catch (PDOException $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Erreur : ' . $e->getMessage()
            ]);
        }
        exit;
    }
    
    if ($action === 'creerReappro') {
        if (!isset($_SESSION['entreprise_id'])) {
            echo json_encode(['success' => false, 'message' => 'Non connecté']);
            exit;
        }
        
        $idProduit = $_POST['idProduit'] ?? 0;
        
        try {
            $pdo = getConnection();
            
            $sql = "SELECT * FROM produits_externes WHERE id_produit = :id";
            $stmt = $pdo->prepare($sql);
            $stmt->execute(['id' => $idProduit]);
            $produit = $stmt->fetch();
            
            if (!$produit) {
                echo json_encode(['success' => false, 'message' => 'Produit introuvable']);
                exit;
            }
            
            $stockProjecte = $produit['stock_actuel'] - 
                ($produit['consommation_journaliere'] * $produit['delai_livraison_jours']);
            $quantite = max(0, $produit['stock_maximum'] - $stockProjecte);
            
            $dateLivraison = date('Y-m-d H:i:s', strtotime("+{$produit['delai_livraison_jours']} days"));
            
            $sql = "INSERT INTO reapprovisionnements 
                    (id_produit_externe, quantite_commandee, date_livraison_prevue, 
                     stock_au_moment_commande, consommation_estimee) 
                    VALUES (:id, :qte, :date, :stock, :conso)";
            
            $stmt = $pdo->prepare($sql);
            $stmt->execute([
                'id' => $idProduit,
                'qte' => $quantite,
                'date' => $dateLivraison,
                'stock' => $produit['stock_actuel'],
                'conso' => $produit['consommation_journaliere']
            ]);
            
            echo json_encode([
                'success' => true,
                'message' => 'Réapprovisionnement créé',
                'quantite' => $quantite,
                'dateLivraison' => $dateLivraison
            ]);
        } catch (PDOException $e) {
            echo json_encode([
                'success' => false,
                'message' => 'Erreur : ' . $e->getMessage()
            ]);
        }
        exit;
    }
    
    if ($action === 'logout') {
        session_destroy();
        echo json_encode(['success' => true]);
        exit;
    }
}

$isConnected = isset($_SESSION['entreprise_id']);
$entrepriseNom = $_SESSION['entreprise_nom'] ?? '';
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Fournisseur - Gestion Stock</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    
    <div id="loginSection" style="<?= $isConnected ? 'display: none;' : '' ?>">
        <div class="login-container">
            <h1>🏭 Dashboard Fournisseur</h1>
            <form id="loginForm">
                <div class="form-group">
                    <label for="codeEntreprise">Code Entreprise</label>
                    <input type="text" id="codeEntreprise" required placeholder="Ex: TECH001">
                </div>
                <div class="form-group">
                    <label for="nomUtilisateur">Nom d'utilisateur</label>
                    <input type="text" id="nomUtilisateur" required placeholder="Votre identifiant">
                </div>
                <div class="form-group">
                    <label for="motDePasse">Mot de passe</label>
                    <input type="password" id="motDePasse" required placeholder="••••••••">
                </div>
                <button type="submit" class="btn-primary">
                    <span id="loginText">Se connecter</span>
                    <span id="loginLoader" class="loader" style="display: none;"></span>
                </button>
            </form>
        </div>
    </div>

    <div id="dashboardSection" style="<?= $isConnected ? '' : 'display: none;' ?>">
        <div class="header">
            <h2 id="entrepriseNom"><?= htmlspecialchars($entrepriseNom) ?></h2>
            <div class="header-actions">
                <button class="btn-secondary" onclick="actualiser()">🔄 Actualiser</button>
                <button class="btn-secondary" onclick="deconnecter()">🚪 Déconnexion</button>
            </div>
        </div>

        <div class="stats-container">
            <div class="stat-card">
                <h3>Total Produits</h3>
                <div class="value" id="totalProduits">0</div>
            </div>
            <div class="stat-card">
                <h3>Alertes Critiques</h3>
                <div class="value" id="alertesCritiques" style="color: #f44336;">0</div>
            </div>
            <div class="stat-card">
                <h3>Réappros en cours</h3>
                <div class="value" id="reapprosEnCours" style="color: #ff9800;">0</div>
            </div>
            <div class="stat-card">
                <h3>Stock Total</h3>
                <div class="value" id="stockTotal" style="color: #4caf50;">0</div>
            </div>
        </div>

        <div class="products-container">
            <h3>📦 Niveaux de stock</h3>
            <div id="produitsListe"></div>
        </div>

        <div class="reappro-container">
            <h3>🚚 Réapprovisionnements en cours</h3>
            <div id="reapprosListe"></div>
        </div>
    </div>

    <div id="alertModal" class="modal">
        <div class="modal-content">
            <div class="alert-icon">⚠️</div>
            <h2 style="text-align: center; color: #f44336; margin-bottom: 20px;">Alertes Stock Critique</h2>
            <div id="alertesList" style="max-height: 300px; overflow-y: auto;"></div>
            <button class="btn-primary" onclick="fermerModal()" style="margin-top: 20px;">Fermer</button>
        </div>
    </div>

    <script src="script.js"></script>
</body>
</html>