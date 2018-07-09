package abstraction.eq5TRAN;

import abstraction.eq3PROD.echangesProdTransfo.ContratFeveV3;
import abstraction.eq3PROD.echangesProdTransfo.IAcheteurFeveV4;
import abstraction.eq3PROD.echangesProdTransfo.IMarcheFeve;
import abstraction.eq4TRAN.IVendeurChocoBis;
import abstraction.eq4TRAN.VendeurChoco.GPrix2;
import abstraction.eq5TRAN.appeldOffre.DemandeAO;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChocoTer;
import abstraction.eq5TRAN.util.Marchandises;
import abstraction.eq7TRAN.echangeTRANTRAN.ContratPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IAcheteurPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IVendeurPoudre;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static abstraction.eq5TRAN.util.Marchandises.*;

/**
 * @author Juliette Gorline (chef)
 * @author Francois Le Guernic
 * @author Maxim Poulsen
 * @author Thomas Schillaci (lieutenant)
 * 
 * TODO LIST

 * - Systeme de fidelite client/fournisseur


 

 * - Constante mutlipicatrice a droite / ecouler stocks - reste du monde
 */
public class Eq5TRAN implements Acteur, IAcheteurPoudre, IVendeurPoudre, IvendeurOccasionnelChocoTer, IAcheteurFeveV4, IVendeurChocoBis {

    // cf Marchandises.java pour obtenir l'indexation
    private Indicateur[] productionSouhaitee; // ce qui sort de nos machines en kT
    private Indicateur[] achatsSouhaites; // ce qu'on achète aux producteurs en kT
    private float facteurStock; // facteur lié aux risques= combien d'itérations on peut tenir sans réception de feves/poudre
    private Indicateur[] stocksSouhaites; // margeStock = facteurStock * variationDeStockParIteration, en kT
    private Indicateur[] stocks; // les vrais stocks en kT
    private ContratFeveV3 contratFeveBQEq2; // Le contrat avec l'équipe 2 pour les fèves BQ
    private ContratFeveV3 contratFeveMQEq2; // Le contrat avec l'équipe 2 pour les fèves MQ
    private ContratFeveV3 contratFeveMQEq3; // Le contrat avec l'équipe 3 pour les fèves MQ
    private  List<ContratFeveV3> contratsPrécédents ; // la liste des contrats précédents

    private Indicateur banque; // en milliers d'euros
    private Indicateur[] prix; // en €/T

    private Journal journal;

    private int[] dureesPeremption; // durees en nombre de next

    /*
     * On regarde pour chaque marchandise sur toute une duree de sa duree de peremption
     * Si on a respecte les objectifs fixes sur chaque next de la periode
     * Sinon on perd 100/(duree du next)% de la marchandise
     */
    private boolean[] respectObjectifs;

    /**
     * Permet de savoir si il y a grève ou non
     */
    private int indicateurGreve;

    /**
     * Nous multiplions nos ventes aux distributeurs par cette constante pour simuler le reste du monde
     * Sont pris en compte nos stocks et notre compte en banque
     */
    private float[] constantesMultiplicatrices;

    public Eq5TRAN() {

        /**
         * GESTION DES INDICATEURS
         * @author Thomas Schillaci
         */

        int nbMarchandises = Marchandises.getNombreMarchandises();
        productionSouhaitee = new Indicateur[nbMarchandises];
        achatsSouhaites = new Indicateur[nbMarchandises];
        facteurStock = 3;
        stocksSouhaites = new Indicateur[nbMarchandises];
        stocks = new Indicateur[nbMarchandises];
        prix = new Indicateur[nbMarchandises];

        prix[FEVES_BQ] = new Indicateur("Eq5 - Prix de feves BQ", this, 1000);
        prix[FEVES_MQ] = new Indicateur("Eq5 - Prix de feves MQ", this, 2000);
        prix[TABLETTES_BQ] = new Indicateur("Eq5 - Prix de tablettes BQ", this, 5_000_000);
        prix[TABLETTES_MQ] = new Indicateur("Eq5 - Prix de tablettes MQ", this, 7_500_000);
        prix[TABLETTES_HQ] = new Indicateur("Eq5 - Prix de tablettes HQ", this, 10_000_000);
        prix[POUDRE_BQ] = new Indicateur("Eq5 - Prix de poudre BQ", this, 0);
        prix[POUDRE_MQ] = new Indicateur("Eq5 - Prix de poudre MQ", this, 5_000_000);
        prix[POUDRE_HQ] = new Indicateur("Eq5 - Prix de poudre HQ", this, 0);
        prix[FRIANDISES_MQ] = new Indicateur("Eq5 - Prix de friandises MQ", this, 7_500_000);

        productionSouhaitee[FEVES_BQ] = new Indicateur("Eq5 - Production souhaitee de feves BQ", this, 0);
        productionSouhaitee[FEVES_MQ] = new Indicateur("Eq5 - Production souhaitee de feves MQ", this, 0);
        productionSouhaitee[TABLETTES_BQ] = new Indicateur("Eq5 - Production souhaitee de tablettes BQ", this, 345.0/24);
        productionSouhaitee[TABLETTES_MQ] = new Indicateur("Eq5 - Production souhaitee de tablettes MQ", this, 575.0/24);
        productionSouhaitee[TABLETTES_HQ] = new Indicateur("Eq5 - Production souhaitee de tablettes HQ", this, 115.0/24);
        productionSouhaitee[POUDRE_BQ] = new Indicateur("Eq5 - Production souhaitee de poudre BQ", this, 360.0/24);
        productionSouhaitee[POUDRE_MQ] = new Indicateur("Eq5 - Production souhaitee de poudre MQ", this, 50.0/24);
        productionSouhaitee[POUDRE_HQ] = new Indicateur("Eq5 - Production souhaitee de poudre HQ", this, 0);
        productionSouhaitee[FRIANDISES_MQ] = new Indicateur("Eq5 - Production souhaitee de friandises MQ", this, 115.0/24);

        achatsSouhaites[FEVES_BQ] = new Indicateur("Eq5 - Achats souhaites de feves BQ", this, 2520.0/24);
        achatsSouhaites[FEVES_MQ] = new Indicateur("Eq5 - Achats souhaites de feves MQ", this, 5880.0/24);
        achatsSouhaites[TABLETTES_BQ] = new Indicateur("Eq5 - Achats souhaites de tablettes BQ", this, 0);
        achatsSouhaites[TABLETTES_MQ] = new Indicateur("Eq5 - Achats souhaites de tablettes MQ", this, 0);
        achatsSouhaites[TABLETTES_HQ] = new Indicateur("Eq5 - Achats souhaites de tablettes HQ", this, 0);
        achatsSouhaites[POUDRE_BQ] = new Indicateur("Eq5 - Achats souhaites de poudre BQ", this, 0);
        achatsSouhaites[POUDRE_MQ] = new Indicateur("Eq5 - Achats souhaites de poudre MQ", this, 0);
        achatsSouhaites[POUDRE_HQ] = new Indicateur("Eq5 - Achats souhaites de poudre HQ", this, 115.0/24);
        achatsSouhaites[FRIANDISES_MQ] = new Indicateur("Eq5 - Achats souhaites de friandises MQ", this, 0);

        for (int i = 0; i < nbMarchandises; i++) {
            stocksSouhaites[i] = new Indicateur("Eq5 - Stocks souhaites de " + Marchandises.getMarchandise(i), this, productionSouhaitee[i].getValeur() + achatsSouhaites[i].getValeur());
            stocks[i] = new Indicateur("Eq5 - Stocks de " + Marchandises.getMarchandise(i), this, stocksSouhaites[i].getValeur()); // on initialise les vrais stocks comme étant ce que l'on souhaite avoir pour la premiere iteration
        }

        banque = new Indicateur("Eq5 - Banque", this, 14_000_000); // environ benefice 2017 sur nombre d'usines

        Monde.LE_MONDE.ajouterIndicateur(banque);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_BQ]);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_MQ]);
        Monde.LE_MONDE.ajouterIndicateur(stocks[TABLETTES_HQ]);
        Monde.LE_MONDE.ajouterIndicateur(stocks[FRIANDISES_MQ]);

        /**
         * GESTION DES JOURNAUX
         * @author Thomas Schillaci
         */

        journal = new Journal("Journal Eq5");
        Monde.LE_MONDE.ajouterJournal(journal);

        /**
         * GESTION DES CONTRATS AVEC LA PRODUCTION
         * @author Francois le Guernic
         */

        contratFeveBQEq2 = new ContratFeveV3(this, "Eq2PROD", 0);
        contratFeveMQEq2 = new ContratFeveV3(this, "Eq2PROD", 1);
        contratFeveMQEq3 = new ContratFeveV3(this, "Eq3PROD", 1);

        /**
         * GESTION DE LA PEREMPTION
         * @author Maxim Poulsen, Thomas Schillaci
         */

        dureesPeremption = new int[nbMarchandises];
        dureesPeremption[FEVES_BQ] = 42;
        dureesPeremption[FEVES_MQ] = (int) (42 * 0.95f);
        dureesPeremption[TABLETTES_BQ] = 6;
        dureesPeremption[TABLETTES_MQ] = (int) (6 * 0.95f);
        dureesPeremption[TABLETTES_HQ] = (int) (6 * 0.90f);
        dureesPeremption[POUDRE_BQ] = 48;
        dureesPeremption[POUDRE_MQ] = (int) (48 * 0.95f);
        dureesPeremption[POUDRE_HQ] = (int) (48 * 0.90f);
        dureesPeremption[FRIANDISES_MQ] = (int) (6 * 0.95f);

        respectObjectifs = new boolean[nbMarchandises];
        for (int i = 0; i < Marchandises.getNombreMarchandises(); i++) respectObjectifs[i] = true;

        /**
         * GESTION DES GREVES
         * @author Juliette, Thomas
         */

        indicateurGreve=0;

        /**
         * GESTION DU RESTE DU MONDE COTE DISTRIBUTEURS
         */

        constantesMultiplicatrices = new float[nbMarchandises];
        constantesMultiplicatrices[TABLETTES_BQ] = 431.25f;
        constantesMultiplicatrices[TABLETTES_MQ] = 1.75f;
        constantesMultiplicatrices[TABLETTES_HQ] = 38.35f;
        constantesMultiplicatrices[FRIANDISES_MQ] = 46;
    }

    @Override
    public String getNom() {
        return "Eq5TRAN";
    }

    @Override
    public void next() {
        journal.ajouter("------------------------------- Eq5 step " + Monde.LE_MONDE.getStep() + " -------------------------------");

        production();
        depensesRH();
        payerSalaires();
        achatPoudre();
        indicateurGreve = Monde.LE_MONDE.getStep();
    }

    /**
     * @author Thomas Schillaci
     */
    public void production() {
        roulementStocks();

        production(POUDRE_BQ, TABLETTES_BQ,10);
        production(POUDRE_MQ, TABLETTES_MQ,10);
        production(POUDRE_HQ, TABLETTES_HQ,10);
        production(POUDRE_MQ, FRIANDISES_MQ,0.7f);
        production(FEVES_BQ, POUDRE_BQ,0.7f);
        production(FEVES_MQ, POUDRE_MQ,0.7f);
    }

    /**
     * @author Maxim Poulsen, Thomas Schillaci
     */
    public void roulementStocks() {
        for (int i = 0; i < Marchandises.getNombreMarchandises(); i++) {
            if (Monde.LE_MONDE.getStep() % dureesPeremption[i] == 0 && !respectObjectifs[i]) {
                float perte = 1.0f / dureesPeremption[i];
                stocks[i].setValeur(this, stocks[i].getValeur() * (1.0f - perte));
                respectObjectifs[i] = true; // on reinitialise les indicateurs sur les objectifs
                journal.ajouter("L'equipe 5 vient de perdre " + perte * 100 + "% de son stock de " + Marchandises.getMarchandise(i) + " à cause d'une mauvaise gestion des durées de péremption");
            }
        }
    }

    /**
     * @author Thomas Schillaci
     * Transforme la merch1 en merch2
     * @param tauxDeConversion rapport 1kg de merch2 par le nombre de kg de merch1 necessaires pour produire ce kilo de merch1
     */
    public void production(int merch1, int merch2, float tauxDeConversion) {
        double quantite = Math.max(0, Math.min(stocks[merch1].getValeur() * tauxDeConversion, productionSouhaitee[merch2].getValeur() * (isPeriodeFetes() ? 3 : 1)));
        if (isGreves()) quantite *= 0.3;
        if (quantite < productionSouhaitee[merch2].getValeur()) {
            respectObjectifs[merch1] = false;
            journal.ajouter("L'eq. 5 n'a pas pu produire assez de " + Marchandises.getMarchandise(merch2) + " par manque de stock de " + Marchandises.getMarchandise(merch1));
        }
        stocks[merch1].setValeur(this, stocks[merch1].getValeur() - quantite);
        stocks[merch2].setValeur(this, stocks[merch2].getValeur() + quantite);
        depenser(quantite*0.01f*prix[merch2].getValeur());
    }

    /**
     * @author Thomas Schillaci
     * N.B. accepte les valeurs négatives pour encaisser
     */
    public void depenser(double depense) {
        double resultat = banque.getValeur() - depense;
        banque.setValeur(this, resultat);
        if (resultat < 0)
            journal.ajouter("L'equipe 5 est a decouvert !\nCompte en banque: " + banque.getValeur() + "€");
    }

    /*
     * @author Juliette, Thomas Schillaci
     */
    public boolean isGreves() {
        if(Math.abs(indicateurGreve)==Monde.LE_MONDE.getStep()+1) return indicateurGreve>0;

        double probaGreve = 12.0 / 365; //12 jours de grèves par an
        if (Math.random() < probaGreve) {
            journal.ajouter("Les salariés de Nestlé sont en grève");
            indicateurGreve=Monde.LE_MONDE.getStep()+1;
            return true;
        }
        indicateurGreve=-Monde.LE_MONDE.getStep()-1;
        return false;
    }

    /*
     * @author Juliette
     */
    public void depensesRH() {
        int numeroNext = Monde.LE_MONDE.getStep();
        if (numeroNext % 24 == 0 && this.banque.getValeur() > 30000) { // tous les ans on donne 10000€ pour des aménagements pour les salariés
            this.banque.setValeur(this, this.banque.getValeur() - 10000);
        }
    }

    /*
     * @author Juliette
     */
    public void payerSalaires() {
        if (isGreves() == false) {
            depenser((782 * 2400) / 24);
        }
    }

    /*
     * @author Juliette
     */
    public boolean isPeriodeFetes() {
        int numeroNext = Monde.LE_MONDE.getStep();
        if (numeroNext % 24 >= 18 && numeroNext <= 21) { // achats des distributeurs en octobre et novembre
            return true;
        }
        if (numeroNext % 24 >= 3 && numeroNext <= 6) { //achats des distributeurs en février et mars
            return true;
        }
        return false;

    }
    /*
     * @author Juliette
     */
    public void achatPoudre() {
    	IVendeurPoudre equipe7 = (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq7TRAN");
    	ContratPoudre[] catalogue = equipe7.getCataloguePoudre(this);
    	double besoinPoudre = 210-this.stocks[POUDRE_HQ].getValeur();
    	ContratPoudre[] demande = new ContratPoudre[3];
    	demande[0]= new ContratPoudre();
    	demande[1]= new ContratPoudre();
    	    	//Si l'équipe 7 n'a pas assez de stocks, on demande le maximum disponible
        if(catalogue[2]!=null) {
            if(catalogue[2].getQuantite()<210) {
                demande[2] = new ContratPoudre(2,catalogue[2].getQuantite(),catalogue[2].getPrix(),this,equipe7,false);
            }

            else {
                demande[2]=new ContratPoudre(2,(int)besoinPoudre,catalogue[2].getPrix(),this,equipe7,false);
            }
        }
        ContratPoudre[] devis = equipe7.getDevisPoudre(demande, this);
        if(demande[2]!=null && devis[2]!=null) {
            if(devis[2].getPrix()==demande[2].getPrix() && devis[2].getQuantite()==demande[2].getQuantite()) {
                devis[2].setReponse(true);
                equipe7.sendReponsePoudre(devis, this);
                depenser(devis[2].getPrix());
                this.stocks[POUDRE_HQ].setValeur(this, this.stocks[POUDRE_HQ].getValeur()+devis[2].getQuantite());
                journal.ajouter("L'équipe 5 a acheté "+devis[2].getQuantite()+" tonnes de poudre HQ à l'équipe 7");
            }
        }
    	equipe7.getEchangeFinalPoudre(demande, this);
    }
    
    



    /**
     * @author Juliette et Thomas
     */
    @Override
    public ContratPoudre[] getCataloguePoudre(IAcheteurPoudre acheteur) {
        if (stocks[POUDRE_MQ].getValeur() == 0) return new ContratPoudre[0];

        ContratPoudre[] catalogue = new ContratPoudre[3];
        catalogue[0] = new ContratPoudre();
        catalogue[2] = new ContratPoudre(); // on ne vend que de la poudre MQ
        catalogue[1] = new ContratPoudre(1, (int)stocks[POUDRE_MQ].getValeur()*1000, prix[POUDRE_MQ].getValeur()*Math.pow(10, -3), acheteur, this, false);
       
        return catalogue;

    }

    /**
     * @author Juliette
     * V1 : on n'envoie un devis que si la qualité demandée est moyenne (la seule que nous vendons) et que nous avons assez de stocks
     */
    @Override
    public ContratPoudre[] getDevisPoudre(ContratPoudre[] demande, IAcheteurPoudre acheteur) {
        ContratPoudre[] devis = new ContratPoudre[demande.length];
        for (int i = 0; i < demande.length; i++) {
            if (demande[i].getQualite() != 1 || demande[i].getQuantite() > stocks[POUDRE_MQ].getValeur()*1000) {
                devis[i] = new ContratPoudre();
            } else {
                devis[i] = new ContratPoudre(demande[i].getQualite(), demande[i].getQuantite(), prix[POUDRE_MQ].getValeur()*Math.pow(10, -3), acheteur, this, false);
            }
        }

        return devis;
    }

    /**
     * @author Juliette
     * V1 : si la réponse est cohérente avec la demande initiale, nos stocks et nos prix, on répond oui
     */
    @Override
    public void sendReponsePoudre(ContratPoudre[] devis, IAcheteurPoudre acheteur) {
        ContratPoudre[] reponse = new ContratPoudre[devis.length];
        for (int i = 0; i < devis.length; i++) {
            if (devis[i].getQualite() == 1 && devis[i].getQuantite() < stocks[POUDRE_MQ].getValeur()*1000 && devis[i].getPrix() >= prix[POUDRE_MQ].getValeur()*Math.pow(10, -3)) {
                reponse[i] = new ContratPoudre(devis[i].getQualite(), devis[i].getQuantite(), devis[i].getPrix(), devis[i].getAcheteur(), devis[i].getVendeur(), true);
            } else {
                reponse[i] = new ContratPoudre(devis[i].getQualite(), devis[i].getQuantite(), devis[i].getPrix(), devis[i].getAcheteur(), devis[i].getVendeur(), false);
            }
        }
    }

    /**
     * @author Juliette
     * Pour la V1 on suppose que le contrat est entièrement honnoré
     */
    @Override
    public ContratPoudre[] getEchangeFinalPoudre(ContratPoudre[] contrat, IAcheteurPoudre acheteur) {
        ContratPoudre[] echangesEffectifs = new ContratPoudre[contrat.length];
        for (int i = 0; i < contrat.length; i++) {
            echangesEffectifs[i] = contrat[i];
            depenser(-contrat[i].getPrix()*contrat[i].getQuantite()*1000);
            stocks[i+6].setValeur(this, stocks[i+6].getValeur()-contrat[i].getQuantite()*1000);
            if(contrat[i].getQuantite()!=0) {
            	journal.ajouter("");
                journal.ajouter("L'équipe 5 a vendu "+ contrat[i].getQuantite()+" tonnes de "+ Marchandises.getMarchandise(i+6) + " pour "+contrat[i].getPrix()*contrat[i].getQuantite()+" €"+" à l'équipe "+ ((Acteur)acheteur).getNom());
            }
         
        }
        
        return echangesEffectifs;
    }

    /**
     * @author Juliette
     * Dans cette méthode, nous sommes ACHETEURS
     * Methode permettant de récupérer les devis de poudre correspondant à nos demandes et de décider si on les accepte ou non
     */
    private void getTousLesDevisPoudre(ContratPoudre[] demande) {
        List<Acteur> listeActeurs = Monde.LE_MONDE.getActeurs();

        List<ContratPoudre[]> devis = new ArrayList<ContratPoudre[]>();

        for (Acteur acteur : listeActeurs) {
            if (acteur instanceof IVendeurPoudre) {
                devis.add(((IVendeurPoudre) acteur).getDevisPoudre(demande, this));
            }
        }
        for (ContratPoudre[] contrat : devis) {
            for (int j = 0; j < 3; j++) {
                if (contrat[j].equals(demande[j])) {
                    contrat[j].setReponse(true);
                }
            }
            contrat[0].getVendeur().sendReponsePoudre(contrat, this);
        }


    }

    /**
     * @author François Le Guernic
     *
     */
    public double prixActualiseFeveMQ () {
    	IMarcheFeve marche =  (IMarcheFeve)Monde.LE_MONDE.getActeur("Marche");
    	float ventes = 0 ;
    	float quantites = 0 ;

    	for (ContratFeveV3  c : marche.getContratPrecedent()) {
    		if ( c.getQualite()==1) { ventes += c.getProposition_Quantite()*c.getProposition_Prix() ; quantites += c.getProposition_Quantite() ; }
    	}    	
    	if ( ventes ==0 || quantites == 0 ) return 2000;
    	
    	return ventes/quantites ;
    }

   
    public double prixActualiseFeveBQ () {
    	
        float ventes = 0 ;
    	float quantites = 0 ;

    	for (ContratFeveV3  c : this.contratsPrécédents) {
    		if ( c.getQualite()==0) { ventes += c.getProposition_Quantite()*c.getProposition_Prix() ; quantites += c.getProposition_Quantite() ; }
    	}
    	
    	if ( ventes == 0 || quantites ==0 ) return 1000;
     	
    	return ventes/quantites ;
    }





    /**
     * @author François Le Guernic
     */
    @Override
    public void sendOffrePubliqueV3(List<ContratFeveV3> offrePublique) {
        /* On achète des fèves de BQ ( seulement à équipe 2 ) et de MQ ( à équipes 2 et 3 ) aux équipes de producteur
         * Pour récupérer les offres qui nous intéressent, on stockent les informations en mémoire dans les variables
         * d'instance
         */
        for (ContratFeveV3 c : offrePublique) {
            String vendeur = ((Acteur) c.getProducteur()).getNom();
            int qualite = c.getQualite();
            if (vendeur.equals("Eq2PROD") && qualite == 0) {
                contratFeveBQEq2.setOffrePublique_Quantite(c.getOffrePublique_Quantite());
                contratFeveBQEq2.setOffrePublique_Prix(c.getOffrePublique_Prix());
            } else if (vendeur == "Eq2PROD" && qualite == 1) {
                contratFeveMQEq2.setOffrePublique_Quantite(c.getOffrePublique_Quantite());
                contratFeveMQEq2.setOffrePublique_Prix(c.getOffrePublique_Prix());
            } else if (vendeur == "Eq3PROD" && qualite == 1) {
                contratFeveMQEq3.setOffrePublique_Quantite(c.getOffrePublique_Quantite());
                contratFeveMQEq3.setOffrePublique_Prix(c.getOffrePublique_Prix());
            }


        }
    }

    /**
     * @author Francois Le Guernic
     */
    @Override
    public List<ContratFeveV3> getDemandePriveeV3() {
        /* Par convention, dans la liste de  contrats, on aura dans l'ordre :
         * - le contrat pour les fèves BQ à l'équipe 2
         * - le contrat pour les fèves MQ à l'équipe 2
         * - le contrat pour les fèves MQ à l'équipe 3
         */
        List<ContratFeveV3> demandesPrivee = new ArrayList<ContratFeveV3>() ;
        demandesPrivee.add(this.contratFeveBQEq2) ; demandesPrivee.add(this.contratFeveMQEq2) ;demandesPrivee.add(this.contratFeveMQEq3);
        this.contratFeveBQEq2.setDemande_Prix(this.prixActualiseFeveBQ());
        this.contratFeveBQEq2.setDemande_Quantite((int) achatsSouhaites[FEVES_BQ].getValeur()*1000);
        this.contratFeveMQEq2.setDemande_Prix(this.prixActualiseFeveBQ());
        this.contratFeveMQEq2.setDemande_Quantite((int) (achatsSouhaites[FEVES_MQ].getValeur() * 0.3*1000));
        // On répartit nos achats de MQ en 30 % à l'équipe 2 et 70 % à l'équipe 3
        this.contratFeveMQEq3.setDemande_Prix(this.prixActualiseFeveMQ());
        this.contratFeveMQEq3.setDemande_Quantite((int) (achatsSouhaites[FEVES_MQ].getValeur() * 0.7*1000));

        return demandesPrivee;
    }

    @Override
    public void sendContratFictifV3(List<ContratFeveV3> listContrats) {
    	this.contratsPrécédents = listContrats ;
    }

    /**
     * @author François Le Guernic
     */
    @Override
    public void sendOffreFinaleV3(List<ContratFeveV3> offreFinale) {
        // On actualise nos trois variables d'instance avec les attributs QuantiteProposition et PrixProposition

        for (ContratFeveV3 c : offreFinale) {
            String vendeur = ((Acteur) c.getProducteur()).getNom();
            int qualite = c.getQualite();
            if (vendeur.equals("Eq2PROD") && qualite == 0) {
                contratFeveBQEq2.setProposition_Quantite(c.getProposition_Quantite());
                contratFeveBQEq2.setProposition_Prix(c.getProposition_Prix());
            } else if (vendeur.equals("Eq2PROD") && qualite == 1) {
                contratFeveMQEq2.setProposition_Quantite(c.getProposition_Quantite());
                contratFeveMQEq2.setProposition_Prix(c.getProposition_Prix());
            } else if (vendeur.equals("Eq3PROD") && qualite == 1) {
                contratFeveMQEq3.setProposition_Quantite(c.getProposition_Quantite());
                contratFeveMQEq3.setDemande_Prix(c.getProposition_Prix());
            }

        }
    }

    /**
     * @author François Le Guernic
     */
    public List<ContratFeveV3> getResultVentesV3() {
        List<ContratFeveV3> listeContrat = new ArrayList<ContratFeveV3>();
        listeContrat.add(this.contratFeveBQEq2);
        listeContrat.add(this.contratFeveMQEq2);
        listeContrat.add(contratFeveMQEq3);
        
        for (ContratFeveV3 c : listeContrat) {
            if ((c.getProposition_Prix() <= c.getDemande_Prix()) && c.getProposition_Quantite() <= c.getDemande_Quantite() && c.getProposition_Quantite()!=0) {
                c.setReponse(true); 
                journal.ajouter(c.toString3());
                this.depenser(c.getProposition_Prix()); 
                this.stocks[c.getQualite()].setValeur(this, this.stocks[c.getQualite()].getValeur()+c.getProposition_Quantite()*Math.pow(10,-3));
            } else {
                c.setReponse(false); journal.ajouter(c.toString3());
            }
        }

        return listeContrat; 
    }

    /**
     * @author Maxim
     */
    @Override
    public double getReponseTer(DemandeAO d) {
        switch (d.getQualite()) {
            case 1: {
                journal.ajouter("Eq5 renvoie MAX_VALUE à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                return Double.MAX_VALUE;
            }
            case 2:
                if (d.getQuantite() < stocks[FRIANDISES_MQ].getValeur()*(1_000_000 / 0.2)) {
                    journal.ajouter("Eq5 renvoie " + 1.1 * prix[FRIANDISES_MQ].getValeur() * d.getQuantite() * 0.2 / 1_000_000 + "€ à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                    return (1.1 * prix[FRIANDISES_MQ].getValeur() * d.getQuantite()/ (1_000_000 / 0.2));
                }
            case 3: {
                journal.ajouter("Eq5 renvoie MAX_VALUE à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                return Double.MAX_VALUE;
            }
            case 4:
                if (d.getQuantite() < stocks[TABLETTES_BQ].getValeur()*(1_000_000 / 0.2)) {
                    journal.ajouter("Eq5 renvoie " + 1.1 * prix[TABLETTES_BQ].getValeur() * d.getQuantite()* 0.2 / 1_000_000 + "€ à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                    return (1.1 * prix[TABLETTES_BQ].getValeur() * d.getQuantite()/ (1_000_000 / 0.2));
                }
            case 5:
                if (d.getQuantite() < stocks[TABLETTES_MQ].getValeur()*(1_000_000 / 0.2)) {
                    journal.ajouter("Eq5 renvoie " + 1.1 * prix[TABLETTES_MQ].getValeur() * d.getQuantite()* 0.2 / 1_000_000 + "€ à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                    return (1.1 * prix[TABLETTES_MQ].getValeur() * d.getQuantite()/ (1_000_000 / 0.2));
                }
            case 6:
                if (d.getQuantite() < stocks[TABLETTES_HQ].getValeur()*(1_000_000 / 0.2)) {
                    journal.ajouter("Eq5 renvoie " + 1.1 * prix[TABLETTES_HQ].getValeur() * d.getQuantite() * 0.2 / 1_000_000+ "€ à getReponse("+d.getQuantite()+","+d.getQualite()+")");
                    return (1.1 * prix[TABLETTES_HQ].getValeur() * d.getQuantite()/ (1_000_000 / 0.2));
                }
        }
        return Double.MAX_VALUE;
    }

    /**
     * @author Maxim
     */

    @Override
    public void envoyerReponseTer(Acteur acteur, int quantite, int qualite, double prix) {
        this.depenser(-prix);
        this.stocks[qualite].setValeur(this, this.stocks[qualite].getValeur() - quantite *(0.2/1_000_000));
        journal.ajouter("Eq5 a vendu "+quantite+" barres de chocolat de qualite " +qualite + " pour "+prix+"€ à "+acteur.getNom());
    }

    /**
     * @author Thomas Schillaci
     */
    @Override
    public ArrayList<Integer> getStock() {
        ArrayList<Integer> res = new ArrayList<Integer>();
        res.add(0);
        res.add((int) (stocks[FRIANDISES_MQ].getValeur() * 1_000_000 / 0.2));
        res.add(0);
        res.add((int) (stocks[TABLETTES_BQ].getValeur() * 1_000_000 / 0.2));
        res.add((int) (stocks[TABLETTES_MQ].getValeur() * 1_000_000 / 0.2));
        res.add((int) (stocks[TABLETTES_HQ].getValeur() * 1_000_000 / 0.2));
        return res;
    }

    /**
     * @author Thomas Schillaci
     */
    @Override
    public GPrix2 getPrix() {
        ArrayList<Double[]> intervalles = new ArrayList<Double[]>();
        intervalles.add(new Double[0]);
        intervalles.add(new Double[]{stocks[FRIANDISES_MQ].getValeur()*1_000_000 / 0.2 / 2});
        intervalles.add(new Double[0]);
        intervalles.add(new Double[]{stocks[TABLETTES_BQ].getValeur()*1_000_000 / 0.2 / 2});
        intervalles.add(new Double[]{stocks[TABLETTES_MQ].getValeur()*1_000_000 / 0.2 / 2});
        intervalles.add(new Double[]{stocks[TABLETTES_HQ].getValeur()*1_000_000 / 0.2 / 2});

        ArrayList<Double[]> prixAssocies = new ArrayList<Double[]>();
        prixAssocies.add(new Double[0]);
        prixAssocies.add(new Double[]{prix[FRIANDISES_MQ].getValeur()*0.2/1_000_000});
        prixAssocies.add(new Double[0]);
        prixAssocies.add(new Double[]{prix[TABLETTES_BQ].getValeur()*0.2/1_000_000});
        prixAssocies.add(new Double[]{prix[TABLETTES_MQ].getValeur()*0.2/1_000_000});
        prixAssocies.add(new Double[]{prix[TABLETTES_HQ].getValeur()*0.2/1_000_000});

        return new GPrix2(intervalles, prixAssocies);
    }

    @Override
    public ArrayList<ArrayList<Integer>> getLivraison(ArrayList<ArrayList<Integer>> commandes) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<ArrayList<Integer>>();

        ArrayList<Integer> eq1 = new ArrayList<Integer>();

        eq1.add(0);
        eq1.add((int)Math.min(commandes.get(0).get(1), stocks[FRIANDISES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq1.add(0);
        eq1.add((int) Math.min(commandes.get(0).get(3), stocks[TABLETTES_BQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq1.add((int) Math.min(commandes.get(0).get(4), stocks[TABLETTES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq1.add((int) Math.min(commandes.get(0).get(5), stocks[TABLETTES_HQ].getValeur() * 1_000_000 / 0.2 / 2));
        
        ArrayList<Integer> eq6 = new ArrayList<Integer>();

        eq6.add(0);
        eq6.add((int)Math.min(commandes.get(1).get(1), stocks[FRIANDISES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq6.add(0);
        eq6.add((int) Math.min(commandes.get(1).get(3), stocks[TABLETTES_BQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq6.add((int) Math.min(commandes.get(1).get(4), stocks[TABLETTES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        eq6.add((int) Math.min(commandes.get(1).get(5), stocks[TABLETTES_HQ].getValeur() * 1_000_000 / 0.2 / 2));

        ArrayList<Integer> fictif = new ArrayList<Integer>();

        fictif.add(0);
        fictif.add((int)Math.min(commandes.get(2).get(1), stocks[FRIANDISES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        fictif.add(0);
        fictif.add((int) Math.min(commandes.get(2).get(3), stocks[TABLETTES_BQ].getValeur() * 1_000_000 / 0.2 / 2));
        fictif.add((int) Math.min(commandes.get(2).get(4), stocks[TABLETTES_MQ].getValeur() * 1_000_000 / 0.2 / 2));
        fictif.add((int) Math.min(commandes.get(2).get(5), stocks[TABLETTES_HQ].getValeur() * 1_000_000 / 0.2 / 2));

        res.add(eq1);
        res.add(eq6);
        res.add(fictif);

        String[] produits = new String[]{"","FRIANDISES_MQ","","TABLETTES_BQ","TABLETTES_MQ","TABLETTES_HQ"};

        for(int i=0; i<6; i++) {
            double prixAssocie = prix[Marchandises.getIndex(produits[i])].getValeur() / 1_000_000 * 0.2;
            if(eq1.get(i)!=0) {
                journal.ajouter("L'eq5 vient de vendre " + eq1.get(i) + " " + produits[i] + " pour " + prixAssocie * eq1.get(i) + "€");
                depenser(-prixAssocie * eq1.get(i));
            }
            if(eq6.get(i)!=0) {
                journal.ajouter("L'eq5 vient de vendre " + eq6.get(i) + " " + produits[i] + " pour " + prixAssocie * eq6.get(i) + "€");
                depenser(-prixAssocie * eq6.get(i));
            }
            if(fictif.get(i)!=0) {
                journal.ajouter("L'eq5 vient de vendre " + fictif.get(i) + " " + produits[i] + " pour " + prixAssocie * fictif.get(i) + "€");
                depenser(-prixAssocie * fictif.get(i));
            }
        }

        return res;
    }
}
