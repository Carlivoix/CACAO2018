package abstraction.eq4TRAN;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq3PROD.echangesProdTransfo.ContratFeveV3;
import abstraction.eq3PROD.echangesProdTransfo.IAcheteurFeveV4;
import abstraction.eq4TRAN.VendeurChoco.GPrix2;
import abstraction.eq4TRAN.VendeurChoco.Labellise;
import abstraction.eq4TRAN.VendeurChoco.Vendeur;
import abstraction.eq5TRAN.appeldOffre.DemandeAO;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChocoTer;
import abstraction.eq7TRAN.echangeTRANTRAN.ContratPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IAcheteurPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IVendeurPoudre;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

public class SousActeur implements Acteur, 
ITransformateur, IAcheteurFeveV4,
IVendeurChocoBis,
IAcheteurPoudre,
IVendeurPoudre,
IvendeurOccasionnelChocoTer, Labellise{
	
	//Indicateur du chiffre d'affaire
	private Indicateur chiffreDAffaire ;
	
	//Indicateur de notre solde bancaire
	private Indicateur solde ; 
	
	//Journal rendant compte de nos activités et de l'évolution de nos indicateurs
	private Journal JournalSousActeur;
	
	//Rôle de vendeur que nous incarnerons à chaque next() et qui se mettra à jour à cette même fréquence
	private Vendeur vendeur;
	
	//On crée une liste pour ranger nos stocks
	
	private ArrayList<Indicateur> Stocks;
	private ArrayList<Indicateur> Production;
	private List<ContratFeveV3> contratFeveEnCours ; 
	private ContratPoudre[] contratPoudreEnCoursEq7TRAN ;
	private ContratPoudre[] contratPoudreEnCoursEq5TRAN;
	private String nomPME ; 
	private int taillePME ;
	private double label;
	private int[] demandeFèves ;
	// Indiquer un identifiant de Sous-Acteur ???

	/**
	 *  Constructeur sous acteur par initialisation de ses différentes variables d'instance
	 * @param JournalSousActeur
	 * @param Stocks
	 * @param Production
	 * @param solde
	 * @param label
	 */
	public SousActeur(Journal JournalSousActeur, ArrayList<Indicateur> Stocks, ArrayList<Indicateur> Production, int solde, int taillePME, double label, String nomPME, int[] demandeFèves) {
		this.JournalSousActeur = JournalSousActeur ;
		this.solde = new Indicateur("solde", this,solde);
		this.Stocks=Stocks;
		this.Production=Production;
		this.taillePME = (int)(11 + (Math.random() * (250 - 11))) ;	
		this.label=label;
		this.chiffreDAffaire=new Indicateur("Chiffre d'Affaire",this,0);
		this.nomPME = nomPME ; 

		Monde.LE_MONDE.ajouterActeur(this);
		this.demandeFèves = demandeFèves ; 

		}
	
	
	public String getNom() {
		return "Eq4TRAN" + this.nomPME ;
	}

	public void sell(int q) {

	}
	
	// Tous ces getters et setters vraiment utiles ?
	public Indicateur getSolde() {
		return solde;
	}
	public void setSolde(Indicateur solde) {
		this.solde = solde;
	}
	public Journal getJournalSousActeur() {
		return JournalSousActeur;
	}
	public void setJournalSousActeur(Journal JournalSousActeur) {
		this.JournalSousActeur = JournalSousActeur;
	}
	public Vendeur getVendeur() {
		return vendeur;
	}
	public void setVendeur(Vendeur vendeur) {
		this.vendeur = vendeur;
	}
	public List<ContratFeveV3> getContratFeveEnCours() {
		return this.contratFeveEnCours ;
	}
	public void setContratFeveEnCours(List<ContratFeveV3> contratFeveEnCours) {
		this.contratFeveEnCours = contratFeveEnCours;
	}
	public ContratPoudre[] getContratPoudreEnCoursEq7TRAN() {
		return contratPoudreEnCoursEq7TRAN ;
	}
	public void setContratPoudreEnCoursEq7TRAN(ContratPoudre[] contratPoudreEnCoursEq7TRAN) {
		this.contratPoudreEnCoursEq7TRAN = contratPoudreEnCoursEq7TRAN;
	}
	public ContratPoudre[] getContratPoudreEnCoursEq5TRAN() {
		return contratPoudreEnCoursEq5TRAN;
	}
	public void setContratPoudreEnCoursEq5TRAN(ContratPoudre[] contratPoudreEnCoursEq5TRAN) {
		this.contratPoudreEnCoursEq5TRAN = contratPoudreEnCoursEq5TRAN;
	}
	public int getTaillePME() {
		return this.taillePME ;
	}

	public ArrayList<Indicateur> getProduction() {
		return Production;
	}

	public void setProduction(ArrayList<Indicateur> production) {
		Production = production;
	}

	// Etienne Raveau
	// Getter pour accéder à la liste de nos stocks
	public ArrayList<Indicateur> getStocks(){
		return Stocks;
	}

	public void setStocks(ArrayList<Indicateur> l ) {
		this.Stocks=l;
	}
	/*
	 * @author Noémie 
	 * Implémentation des méthodes de l'interface IAcheteurFeveV4
	 */
	
	// Avant il faut,  récupérer offre publique & discuter de ce que l'on veut
	
	@Override 
	public void sendOffrePubliqueV3(List<ContratFeveV3> offrePublique) {
		this.contratFeveEnCours = offrePublique ; 
	}
	
	
	/*
	 * @author Charles, Noémie 
	 * 
	 * On cherche qui vend le moins cher la qualité que l'on souhaite
	 * On envoie aux producteurs notre demande
	 */
	@Override
	public List<ContratFeveV3> getDemandePriveeV3() {
		int[] demande= this.demandeFèves ; 
		 
		double[] prixMin= { 100000.0 , 100000.0 , 100000.0 } ;
		int[] min= {-1,-1,-1};
		int[] max= {-1,-1,-1};
		for (int i=0;i<this.contratFeveEnCours.size();i++) {
			int qualite=this.contratFeveEnCours.get(i).getQualite();
			if (this.contratFeveEnCours.get(i).getOffrePublique_Prix()<prixMin[qualite]) {
				prixMin[qualite]=this.contratFeveEnCours.get(i).getOffrePublique_Prix();
				if (min[qualite]!=-1) {
					max[qualite]=i;
				}
				min[qualite]=this.contratFeveEnCours.get(i).getQualite();
			}
		}
		for (int j=0;j<3;j++) {
			this.contratFeveEnCours.get(min[j]).setDemande_Quantite(Math.min(demande[min[j]],this.contratFeveEnCours.get(min[j]).getOffrePublique_Quantite()/3));
			if (max[j]!=-1) {
				this.contratFeveEnCours.get(max[j]).setDemande_Quantite(demande[min[j]]-Math.min(demande[min[j]],this.contratFeveEnCours.get(min[j]).getOffrePublique_Quantite()/3));
			}
		}
		return this.contratFeveEnCours;
	}
	
	/*
	 * @author Noémie 
	 * Rien car concerne l'acteur fictif
	 */
	@Override
	public void sendContratFictifV3(List<ContratFeveV3> listContrats) {
	}


	/*
	 * @author Noémie , Charles 
	 */
	@Override
	public void sendOffreFinaleV3(List<ContratFeveV3> offreFinale) {
		this.contratFeveEnCours = offreFinale ;
		for (ContratFeveV3 c: offreFinale) {
			c.setReponse(true);
		}
	}
	

	/*
	 * @author Noémie, Charles
	 *
	 *  Stratégie à réécrire pour l'acceptation ou non des contrats 
	 *  et revoir comment ranger les contrats selon le producteur 
	 *  
	 *  MàJ des stocks et des comptes dès que l'on accepte un contrat 
	 */
	
	@Override
	
	public List<ContratFeveV3> getResultVentesV3() {
		for (ContratFeveV3 contrat : this.contratFeveEnCours) {
			double coutTotal = contrat.getProposition_Prix()*contrat.getProposition_Quantite() ;
			this.getJournalSousActeur().ajouter("L'équipe 4 a acheté : "+contrat.getProposition_Quantite()+ " de fèves " +Marchandises.getQualite(contrat.getQualite())+ " pour "+ contrat.getProposition_Prix());
				//if (1.5*coutTotal < this.solde.getValeur()) {
					if (true) {
					contrat.setReponse(true);
					//màj du solde 
					double ancienSolde = this.getSolde().getValeur() ; 
					this.solde.setValeur(this, ancienSolde - coutTotal ) ; 
					//màj des stocks
					for(int j=0;j<3;j++) {
						if(contrat.getQualite() == j) {
							this.getProduction().get(j+3).setValeur(this, contrat.getProposition_Quantite()); 
							double ancienStock = this.getStocks().get(j+3).getValeur();
							this.getStocks().get(j+3).setValeur(this, ancienStock + this.getProduction().get(j+3).getValeur());
						}
					}
				}
			
		}
		
		return this.contratFeveEnCours ; 
	}
	

	// Etienne Raveau
	// Getter permettant d'accéder à la quantité disponible d'un produit 
	public int getQuantite(int IDProduit) {
		return vendeur.getQte(IDProduit);
	}

	//Etienne Raveau
	// Setter qui met à jour  l'indicateur de stock pour un certain produit
	public void setQuantite(int IDProduit, int quantite) {
		getStocks().get(IDProduit-1).setValeur(this, quantite);
	}

	/*
	 * @Etienne
	 */
	@Override
	public ArrayList<Integer> getStock() {
		return vendeur.getStock();
	}

	// Etienne Raveau
	@Override
	public GPrix2 getPrix() {
		return vendeur.getPrix();
	}


	//Etienne Raveau
	@Override
	public ArrayList<ArrayList<Integer>> getLivraison(ArrayList<ArrayList<Integer>> commandes) {
		ArrayList<ArrayList<Integer>> livraison = new ArrayList<ArrayList<Integer>>();
		livraison.addAll(vendeur.getLivraison(commandes));  //On remplit notre livraison selon la méthode implémentée dans Vendeur
		//On met à jour nos stocks en fonction de la livraison effectuée
		for(int i=1;i<6;i++) {
			this.getJournalSousActeur().ajouter("L'équipe 4 a vendu : " + livraison.get(0).get(i) + " de " + Marchandises.getMarchandise(i+1) +" à "+ "Eq1DIST" + " pour "+ vendeur.getPrix().getPrixProduit(livraison.get(0).get(i), i+1));
			this.getJournalSousActeur().ajouter("L'équipe 4 a vendu : " + livraison.get(0).get(i) + " de " + Marchandises.getMarchandise(i+1) +" à "+ "Eq6DIST" + " pour "+ vendeur.getPrix().getPrixProduit(livraison.get(0).get(i), i+1));
			int quantite = getQuantite(i+1)-livraison.get(0).get(i)-livraison.get(1).get(i);
			setQuantite(i+1,quantite);
			getStock().set(i, quantite);
		}
		double s = 0.0;
		//On calcule la valeur du chiffre d'affaire généré par cette livraison
		for(int i=0;i<2;i++) {
			for(int j=0;j<6;j++) {
				s+=livraison.get(i).get(j)*vendeur.getPrix().getPrixProduit(livraison.get(i).get(j), j+1);
			}
		}
		solde.setValeur(this, s); //On met à jour notre solde bancaire
		return livraison;
	}

	@Override
	public ContratPoudre[] getCataloguePoudre(IAcheteurPoudre acheteur) {
		
		return null;
	}

	@Override
	public ContratPoudre[] getDevisPoudre(ContratPoudre[] demande, IAcheteurPoudre acheteur) {
		
		return null;
	}

	@Override
	public void sendReponsePoudre(ContratPoudre[] devis, IAcheteurPoudre acheteur) {
		

	}

	@Override
	public ContratPoudre[] getEchangeFinalPoudre(ContratPoudre[] contrat, IAcheteurPoudre acheteur) {
		
		return null;
	}
	
	

	@Override
	public void next() {
		
		
	}
	

	
	/*
	 * Calcule puis débite les coûts fixes et variables  la solde du SousActeur
	 * 
	 * Coûts fixes: 
	 * Salaires, coûts de maintenance
	 * Plus une PME est grande, plus ses charges fixes sont 
	 * élevées et moins ses charges variables sont élevées
	 * Coûts variables :
	 * coûts de matières premirères autre que les fèves 
	 * Plus une PME est grande, moins ses charges variables 
	 * sont élevées, avec les coûts d'échelle
	 * Coûts aléatoires :
	 * Coûts liés aux accidents etc 
	 */
	
	public void coutsSupplementaires() {
		double CA = this.chiffreDAffaire.getValeur() ; 
		double soldeActuel = this.solde.getValeur() ;
		double chargesFixes = 0 ;
		double chargesVariables = 0 ;
		double chargesaléatoire=Math.random()*0.1;
		if (this.taillePME < 50 ) {
			chargesFixes = 1500 ;
			chargesVariables = 0.4*CA ; 
		} else if ((50 <=this.taillePME)&&(this.taillePME < 150 )) {
			chargesFixes = 2500 ;
			chargesVariables = 0.35*CA ;
		} else {
			chargesFixes = 4000 ; 
			chargesVariables = 0.3*CA ; 
		}
		this.solde.setValeur(this, soldeActuel - chargesFixes - chargesVariables-chargesaléatoire);
	}

	public double getLabel() {
		return label;
	}

	public void setLabel(double label) {
		this.label = label;
	}

	/**
	 * 
	 * @author Noémie 
	 * Selon ce que vend le SousActeur:
	 * Qualité = 1 = Chocolats BQ
	 * ...
	 * Qualité = 6 = Tablettes HQ
	 * On ne vend pas de Chocolats BQ 
	 * 
	 */
	@Override
	public double getReponseTer(DemandeAO d) {
		if (d.getQualite()==1) {
			return Double.MAX_VALUE ; 
		}
		else {
			if (d.getQuantite() < this.getStocks().get(d.getQualite()-1).getValeur()) {
				double prix = this.getPrix().getPrixProduit(d.quantite, d.qualite) ;
				return prix*d.quantite*1.2 ; 
			} else {
				return Double.MAX_VALUE ; 
			}
			
		}
	}


	@Override
	public void envoyerReponseTer(Acteur acteur, int quantite, int qualite, double prix) {
			double ancienSolde = this.solde.getValeur() ; 
			this.solde.setValeur(this, ancienSolde + prix);
			int ancienStock = (int)this.Stocks.get(qualite-1).getValeur() ; 
			this.Stocks.get(qualite-1).setValeur(this, ancienStock - quantite );
			this.getStock().set(qualite-1, ancienStock-quantite);
			this.getJournalSousActeur().ajouter(this.getNom()+" a vendu " + quantite + "de qualité " + Marchandises.getQualite(qualite)+ " à " + acteur.getNom());
		
	}
	
	
 }
