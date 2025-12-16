let produits = [];
let reappros = [];

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    const dashboardSection = document.getElementById('dashboardSection');
    if (dashboardSection && dashboardSection.style.display !== 'none') {
        chargerDonnees();
        setInterval(chargerDonnees, 30000);
    }
});

async function handleLogin(e) {
    e.preventDefault();
    
    const loginText = document.getElementById('loginText');
    const loginLoader = document.getElementById('loginLoader');
    loginText.style.display = 'none';
    loginLoader.style.display = 'inline-block';

    const formData = new FormData();
    formData.append('action', 'login');
    formData.append('codeEntreprise', document.getElementById('codeEntreprise').value);
    formData.append('nomUtilisateur', document.getElementById('nomUtilisateur').value);
    formData.append('motDePasse', document.getElementById('motDePasse').value);

    try {
        const response = await fetch('index.php', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (data.success) {
            afficherNotification('Connexion réussie ! Bienvenue ' + data.entreprise, 'success');
            
            setTimeout(() => {
                document.getElementById('loginSection').style.display = 'none';
                document.getElementById('dashboardSection').style.display = 'block';
                document.getElementById('entrepriseNom').textContent = '🏭 ' + data.entreprise;
                chargerDonnees();
                setInterval(chargerDonnees, 30000);
            }, 1000);
        } else {
            afficherNotification(data.message, 'error');
        }
    } catch (error) {
        afficherNotification('Erreur de connexion', 'error');
    } finally {
        loginText.style.display = 'inline';
        loginLoader.style.display = 'none';
    }
}

async function chargerDonnees() {
    await chargerProduits();
    await chargerReappros();
    verifierAlertes();
}

async function chargerProduits() {
    try {
        const formData = new FormData();
        formData.append('action', 'getProduits');
        
        const response = await fetch('index.php', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();

        if (data.success) {
            produits = data.produits;
            afficherProduits();
            mettreAJourStats();
        }
    } catch (error) {
        console.error('Erreur chargement produits:', error);
    }
}

function afficherProduits() {
    const container = document.getElementById('produitsListe');
    container.innerHTML = '';

    produits.forEach((produit, index) => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.style.animationDelay = (index * 0.1) + 's';

        let barClass = 'stock-normal';
        if (produit.niveau === 'CRITIQUE') barClass = 'stock-critical';
        else if (produit.niveau === 'ATTENTION') barClass = 'stock-warning';

        card.innerHTML = `
            <div class="product-header">
                <span class="product-title">${escapeHtml(produit.description_produit)}</span>
                <span class="product-code">${escapeHtml(produit.code_produit)}</span>
            </div>
            <div class="stock-bar-container">
                <div class="stock-bar ${barClass}" style="width: 0%">
                    ${produit.stock_actuel} / ${produit.stock_maximum}
                </div>
            </div>
            <div class="product-details">
                <span>🛡️ Sécurité: ${produit.stock_securite}</span>
                <span>📊 Conso/jour: ${produit.consommation_journaliere}</span>
                <span>🚚 Délai: ${produit.delai_livraison_jours}j</span>
            </div>
            ${produit.necessiteReappro ? 
                `<button class="btn-reappro" onclick="declencherReappro(${produit.id_produit})">
                    Réapprovisionner (${Math.round(produit.quantiteReappro)} unités)
                </button>` : 
                '<button class="btn-reappro" disabled>✅ Stock OK</button>'
            }
        `;

        container.appendChild(card);

        setTimeout(() => {
            const bar = card.querySelector('.stock-bar');
            bar.style.width = produit.pourcentage + '%';
        }, 100);
    });
}

async function chargerReappros() {
    try {
        const formData = new FormData();
        formData.append('action', 'getReappros');
        
        const response = await fetch('index.php', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();

        if (data.success) {
            reappros = data.reappros;
            afficherReappros();
            mettreAJourStats();
        }
    } catch (error) {
        console.error('Erreur chargement réappros:', error);
    }
}

function afficherReappros() {
    const container = document.getElementById('reapprosListe');
    
    if (reappros.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999; font-style: italic; padding: 20px;">Aucun réapprovisionnement en cours</p>';
        return;
    }

    container.innerHTML = '';

    reappros.forEach((reappro, index) => {
        const card = document.createElement('div');
        card.className = 'reappro-card';
        card.style.animationDelay = (index * 0.1) + 's';

        const dateLivraison = new Date(reappro.date_livraison_prevue);
        const maintenant = new Date();
        const joursRestants = Math.ceil((dateLivraison - maintenant) / (1000 * 60 * 60 * 24));

        card.innerHTML = `
            <div class="reappro-badge">📦 ${escapeHtml(reappro.code_produit)}</div>
            <h4>${escapeHtml(reappro.description_produit)}</h4>
            <p><strong>Quantité:</strong> ${reappro.quantite_commandee} unités</p>
            <p><strong>Commandé le:</strong> ${new Date(reappro.date_commande).toLocaleDateString('fr-FR')}</p>
            <p><strong>Livraison prévue:</strong> ${dateLivraison.toLocaleDateString('fr-FR')} 
               <span style="background: rgba(255,255,255,0.3); padding: 3px 10px; border-radius: 10px; margin-left: 10px;">
                   ⏰ ${joursRestants > 0 ? joursRestants + ' jour(s)' : 'Aujourd\'hui !'}
               </span>
            </p>
        `;

        container.appendChild(card);
    });
}

function mettreAJourStats() {
    animerCompteur('totalProduits', produits.length);
    
    const critiques = produits.filter(p => p.niveau === 'CRITIQUE').length;
    animerCompteur('alertesCritiques', critiques);
    
    animerCompteur('reapprosEnCours', reappros.length);
    
    const stockTotal = produits.reduce((sum, p) => sum + parseInt(p.stock_actuel), 0);
    animerCompteur('stockTotal', stockTotal);
}

function animerCompteur(elementId, valeurFinale) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const valeurInitiale = parseInt(element.textContent) || 0;
    const duree = 1000;
    const pas = 16;
    const increment = (valeurFinale - valeurInitiale) / (duree / pas);
    let valeurActuelle = valeurInitiale;

    const timer = setInterval(() => {
        valeurActuelle += increment;
        if ((increment > 0 && valeurActuelle >= valeurFinale) || 
            (increment < 0 && valeurActuelle <= valeurFinale)) {
            element.textContent = valeurFinale;
            clearInterval(timer);
        } else {
            element.textContent = Math.round(valeurActuelle);
        }
    }, pas);
}

async function declencherReappro(idProduit) {
    afficherNotification('Déclenchement du réapprovisionnement...', 'warning');

    const formData = new FormData();
    formData.append('action', 'creerReappro');
    formData.append('idProduit', idProduit);

    try {
        const response = await fetch('index.php', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (data.success) {
            const date = new Date(data.dateLivraison).toLocaleDateString('fr-FR');
            afficherNotification(`✅ Réappro créé ! ${Math.round(data.quantite)} unités - Livraison: ${date}`, 'success');
            setTimeout(() => chargerDonnees(), 1500);
        } else {
            afficherNotification(data.message, 'error');
        }
    } catch (error) {
        afficherNotification('Erreur lors du réapprovisionnement', 'error');
    }
}

function actualiser() {
    afficherNotification('Actualisation des données...', 'warning');
    chargerDonnees();
}

function verifierAlertes() {
    const produitsCritiques = produits.filter(p => p.niveau === 'CRITIQUE');
    
    if (produitsCritiques.length > 0) {
        const modal = document.getElementById('alertModal');
        const liste = document.getElementById('alertesList');
        
        liste.innerHTML = produitsCritiques.map(p => `
            <div style="background: #ffebee; padding: 15px; margin: 10px 0; border-radius: 10px; border-left: 4px solid #f44336; animation: slideInLeft 0.5s ease-out;">
                <strong>${escapeHtml(p.code_produit)}</strong> - ${escapeHtml(p.description_produit)}<br>
                <span style="color: #f44336;">⚠️ Stock critique: ${p.stock_actuel} / ${p.stock_securite}</span>
            </div>
        `).join('');

        modal.style.display = 'flex';
    }
}

function fermerModal() {
    document.getElementById('alertModal').style.display = 'none';
}

function afficherNotification(message, type) {
    const notif = document.createElement('div');
    notif.className = `notification ${type}`;
    notif.textContent = message;
    document.body.appendChild(notif);

    setTimeout(() => {
        notif.classList.add('fade-out');
        setTimeout(() => notif.remove(), 500);
    }, 3000);
}

async function deconnecter() {
    afficherNotification('Déconnexion...', 'warning');
    
    const formData = new FormData();
    formData.append('action', 'logout');
    
    try {
        await fetch('index.php', {
            method: 'POST',
            body: formData
        });
    } catch (error) {
        console.error('Erreur déconnexion:', error);
    }
    
    setTimeout(() => {
        window.location.reload();
    }, 1000);
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}