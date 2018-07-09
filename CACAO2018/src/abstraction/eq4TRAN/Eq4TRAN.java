package abstraction.eq4TRAN;

import abstraction.eq3PROD.echangesProdTransfo.ContratFeveV3;
import abstraction.eq3PROD.echangesProdTransfo.IAcheteurFeveV4;
import abstraction.eq4TRAN.DISTFictif.DISTFictif;
import abstraction.eq4TRAN.VendeurChoco.Vendeur;
import abstraction.eq5TRAN.Eq5TRAN;
import abstraction.eq7TRAN.echangeTRANTRAN.ContratPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IAcheteurPoudre;
import abstraction.eq7TRAN.echangeTRANTRAN.IVendeurPoudre;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

import java.util.ArrayList;

/**
 * 
 * @author Noémie Rigaut
 *
 */

public class Eq4TRAN implements Acteur { 

	public Acteur Eq4TRAN ; 

	/** Déclaration des indicateurs pour le Journal
	 *  @author Mickaël
	 */
	
	
	public SousActeur PME1;
	public SousActeur PME2;
	public SousActeur PME3;
	
	//Indicateurs de stock et de production
	private Indicateur stockTabBQ_Eq4;
	private Indicateur stockTabMQ_Eq4;
	private Indicateur stockTabHQ_Eq4;
	private Indicateur stockChocBQ_Eq4 ;
	private Indicateur stockChocMQ_Eq4;
	private Indicateur stockChocHQ_Eq4;
	private Indicateur prodTabBQ_Eq4 ;
	private Indicateur prodTabMQ_Eq4 ;
	private Indicateur prodTabHQ_Eq4 ;
	private Indicateur prodChocMQ_Eq4;
	private Indicateur prodChocHQ_Eq4;
	//Indicateur de notre solde bancaire
	private Indicateur solde ; 

	//Journal rendant compte de nos activités et de l'évolution de nos indicateurs
	private Journal JournalEq4 = new Journal("JournalEq4") ;
	// Rôle de vendeur que nous incarnerons à chaque next() et qui se mettra à jour à cette même fréquence
	private Vendeur vendeur;
	// On crée une liste pour ranger nos stocks
	private ArrayList<Indicateur> Stocks;
	
	private ArrayList<ContratFeveV3> contratFevesEnCours ; 
	
	
	public Eq4TRAN() {
		/**
		 * @Mickaël
		 * Initialise des tableaux d'indicateurs 
		 */
		
		int soldeSA=1000;
		ArrayList<Indicateur> Prod_PME1=new ArrayList<>();
		ArrayList<Indicateur> Stocks_PME1 = new ArrayList<>();
		ArrayList<Indicateur> Prod_PME2 = new ArrayList<>();
		ArrayList<Indicateur> Stocks_PME2 = new ArrayList<>();
		ArrayList<Indicateur> Prod_PME3= new ArrayList<>();
		ArrayList<Indicateur> Stocks_PME3= new ArrayList<>();
		
		/**
		 *  On construit les 3 Sous-Acteurs 
		 *  
		 */
		int[] demandeFèvesPME1 = {0,1000,0} ; 
		int[] demandeFèvesPME2 = {500,1300,0} ;
		int[] demandeFèvesPME3 = {0,0,1000} ; 
		this.PME1 = new SousActeur(new Journal("JournalPME1"),Stocks_PME1,Prod_PME1,soldeSA,50,Vendeur.COMMERCE_EQUITABLE,"PME1",demandeFèvesPME1);
		this.PME2 = new SousActeur(new Journal("JournalPME2"),Stocks_PME2,Prod_PME2,soldeSA,135,Vendeur.PRODUCTEUR_LOCAL,"PME2",demandeFèvesPME2);
		this.PME3= new SousActeur(new Journal("JournalPME3"),Stocks_PME3,Prod_PME3,soldeSA,220,Vendeur.LUXE,"PME3",demandeFèvesPME3);
		
		/**
		 * @author Mickaël, Etienne
		 * On initialise les stocks ainsi que la production pour chaque Sous-Acteur.
		 * Ici, on peut modifier les valeurs des stocks au départ 
		 */	
				Prod_PME1.add(new Indicateur("",this.PME1,0));
				Stocks_PME1.add(new Indicateur("",this.PME1,0));
				ArrayList<Integer> stocks_PME1 = new ArrayList<>();
				stocks_PME1.add(0);
				for(int i=1;i<6;i++) {
					Stocks_PME1.add(new Indicateur("PME 1 - stockProduit"+(i+1),this.PME1,1000));
					Prod_PME1.add(new Indicateur("PME 1 - prodProduit"+(i+1),this.PME1,1000));
					stocks_PME1.add((int)(new Indicateur("PME 1 - stockProduit"+(i+1),this.PME1,1000).getValeur()));
				}
				this.PME1.setVendeur(new Vendeur(stocks_PME1));
				
				
			    Prod_PME2.add(new Indicateur("",this.PME2,0));
				Stocks_PME2.add(new Indicateur("",this.PME2,0));
				ArrayList<Integer> stocks_PME2 = new ArrayList<>();
				stocks_PME2.add(0);
				for(int i=1;i<6;i++) {
					Stocks_PME2.add(new Indicateur("PME 2 - stockProduit"+(i+1),this.PME2,1000));
					Prod_PME2.add(new Indicateur("PME 2 - prodProduit"+(i+1),this.PME2,1000));
					stocks_PME2.add((int)(new Indicateur("PME 2 - stockProduit"+(i+1),this.PME2,1000).getValeur()));
				}
				this.PME2.setVendeur(new Vendeur(stocks_PME2));
				
				
				Prod_PME3.add(new Indicateur("",this.PME3,0));
				Stocks_PME3.add(new Indicateur("",this.PME3,0));
				ArrayList<Integer> stocks_PME3= new ArrayList<>();
				stocks_PME3.add(0);
				for(int i=1;i<6;i++) {
					Stocks_PME3.add(new Indicateur("PME 3 - stockProduit"+(i+1),this.PME3,1000));
					Prod_PME3.add(new Indicateur("PME 3 - prodProduit"+(i+1),this.PME3,1000));
					stocks_PME3.add((int)(new Indicateur("PME 3 - stockProduit"+(i+1),this.PME3,1000).getValeur()));
				}
				this.PME3.setVendeur(new Vendeur(stocks_PME3));
			
		/**
		 * @Mickaël
		 * On initialise les contrats d'échange de POUDRE avec les autres transformateurs 
		 * Les Contrats sont valables pour UN NEXT = sur deux semaines
		 */
				
		
		/**
		 * Acteur 1 labellisé commerce équitable 
		 */
				
		ContratPoudre[] contratPoudreEnCoursEq7TRAN_PME1 = new ContratPoudre[3];
		ContratPoudre[] contratPoudreEnCoursEq5TRAN_PME1 = new ContratPoudre[3];
		// Changer prix contrats à ceux définis dans devis des eq5 et eq7
		
		IVendeurPoudre Eq5TRAN = (IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq5TRAN");
		IVendeurPoudre Eq7TRAN = (IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN");
		// Contrats Poudre Basse Qualité
		contratPoudreEnCoursEq5TRAN_PME1[0] = new ContratPoudre(0,0,0.0, (IAcheteurPoudre)this.PME1, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME1[0] = new ContratPoudre(0,0,0.0, (IAcheteurPoudre)this.PME1,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		// Contrats Poudre Moyenne Qualité
		contratPoudreEnCoursEq5TRAN_PME1[1] = new ContratPoudre(1,375,Eq5TRAN.getCataloguePoudre(PME1)[1].getPrix(), (IAcheteurPoudre)this.PME1, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME1[1] = new ContratPoudre(1,50 ,Eq7TRAN.getCataloguePoudre(PME1)[1].getPrix(), (IAcheteurPoudre)this.PME1,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		// Contrats Poudre Haute Qualité
		contratPoudreEnCoursEq5TRAN_PME1[2] = new ContratPoudre(2,0,0.0, (IAcheteurPoudre)this.PME1, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME1[2] = new ContratPoudre(2,0,0.0, (IAcheteurPoudre)this.PME1,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);


		this.PME1.setContratPoudreEnCoursEq5TRAN(contratPoudreEnCoursEq5TRAN_PME1);
		this.PME1.setContratPoudreEnCoursEq7TRAN(contratPoudreEnCoursEq7TRAN_PME1);
		
		/**
		 * Acteur 2 labellisé Local
		 */

		ContratPoudre[] contratPoudreEnCoursEq7TRAN_PME2 = new ContratPoudre[3];
		ContratPoudre[] contratPoudreEnCoursEq5TRAN_PME2 = new ContratPoudre[3];
		contratPoudreEnCoursEq5TRAN_PME2[0] = new ContratPoudre(0,2000/24,Eq5TRAN.getCataloguePoudre(PME2)[0].getPrix(), (IAcheteurPoudre)this.PME2, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME2[0] = new ContratPoudre(0,0,0, (IAcheteurPoudre)this.PME2, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq5TRAN_PME2[1] = new ContratPoudre(1,10000/24,Eq5TRAN.getCataloguePoudre(PME2)[1].getPrix(), (IAcheteurPoudre)this.PME2, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME2[1] = new ContratPoudre(1,8000/24,Eq7TRAN.getCataloguePoudre(PME2)[1].getPrix(), (IAcheteurPoudre)this.PME2,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq5TRAN_PME2[2] = new ContratPoudre(2,0,0.0, (IAcheteurPoudre)this.PME2, (IVendeurPoudre)Monde.LE_MONDE.getActeur("Eq5TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME2[2] = new ContratPoudre(2,0,0.0, (IAcheteurPoudre)this.PME2,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		
		
		this.PME2.setContratPoudreEnCoursEq5TRAN(contratPoudreEnCoursEq5TRAN_PME2);
		this.PME2.setContratPoudreEnCoursEq7TRAN(contratPoudreEnCoursEq7TRAN_PME2);
		
		/**
		 * Acteur 3 labellisé luxe, n'achète que de la qualité de luxe à l'éq 7 
		 */
		ContratPoudre[] contratPoudreEnCoursEq7TRAN_PME3= new ContratPoudre[3];
		ContratPoudre[] contratPoudreEnCoursEq5TRAN_PME3= new ContratPoudre[3];
		contratPoudreEnCoursEq5TRAN_PME3[0] = new ContratPoudre(0,0,Eq5TRAN.getCataloguePoudre(PME3)[0].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME3[0] = new ContratPoudre(0,0,Eq7TRAN.getCataloguePoudre(PME3)[0].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq5TRAN_PME3[1] = new ContratPoudre(1,0,Eq5TRAN.getCataloguePoudre(PME3)[1].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME3[1] = new ContratPoudre(1,0,Eq7TRAN.getCataloguePoudre(PME3)[1].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq5TRAN_PME3[2] = new ContratPoudre(2,18000/24,Eq5TRAN.getCataloguePoudre(PME3)[2].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);
		contratPoudreEnCoursEq7TRAN_PME3[2] = new ContratPoudre(2,18000/24,Eq7TRAN.getCataloguePoudre(PME3)[2].getPrix(), (IAcheteurPoudre)this.PME3,(IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN"),false);

		
		this.PME3.setContratPoudreEnCoursEq5TRAN(contratPoudreEnCoursEq5TRAN_PME3);
		this.PME3.setContratPoudreEnCoursEq7TRAN(contratPoudreEnCoursEq7TRAN_PME3);
		
		
		//**
		this.contratFevesEnCours=new ArrayList<ContratFeveV3>();
		for(int i=0;i<3;i++) {
			this.contratFevesEnCours.add(new ContratFeveV3((IAcheteurFeveV4)this.PME1 , "Eq2PROD" , i )) ;
			this.contratFevesEnCours.add(new ContratFeveV3((IAcheteurFeveV4)this.PME2, "Eq3PROD",i));	
		}
	
		//On initialise les indicateurs à 1000(arbitraire)
		
		/**
		 * Sommateur des stocks des 3 PME pour avoir les stocks de Eq4TRAN
		 */

		stockTabBQ_Eq4 = new Indicateur("Eq4 - stockTabBQ",this,this.PME1.getStock().get(1)+this.PME2.getStock().get(1)+this.PME3.getStock().get(1)) ;
		stockTabMQ_Eq4 = new Indicateur("Eq4 - stockTabMQ",this,this.PME1.getStock().get(2)+this.PME2.getStock().get(2)+this.PME3.getStock().get(2)) ;
		stockTabHQ_Eq4 = new Indicateur("Eq4 - stockTabHQ",this,this.PME1.getStock().get(3)+this.PME2.getStock().get(3)+this.PME3.getStock().get(3)) ;
		stockChocMQ_Eq4 = new Indicateur("Eq4 - stockChocMQ",this,this.PME1.getStock().get(4)+this.PME2.getStock().get(4)+this.PME3.getStock().get(4)) ;
		stockChocHQ_Eq4 = new Indicateur("Eq4 - stockChocHQ",this,this.PME1.getStock().get(5)+this.PME2.getStock().get(5)+this.PME3.getStock().get(5)) ;
		prodTabBQ_Eq4 = new Indicateur("Eq4 - prodTabBQ",this,this.PME1.getProduction().get(1).getValeur()+this.PME2.getProduction().get(1).getValeur()+this.PME3.getProduction().get(1).getValeur()) ;
		prodTabMQ_Eq4 = new Indicateur("Eq4 - prodTabMQ",this,this.PME1.getProduction().get(2).getValeur()+this.PME2.getProduction().get(2).getValeur()+this.PME3.getProduction().get(2).getValeur()) ;
		prodTabHQ_Eq4 = new Indicateur("Eq4 - prodTabHQ",this,this.PME1.getProduction().get(3).getValeur()+this.PME2.getProduction().get(3).getValeur()+this.PME3.getProduction().get(3).getValeur()) ;
		prodChocMQ_Eq4 = new Indicateur("Eq4 - prodChocMQ",this,this.PME1.getProduction().get(4).getValeur()+this.PME2.getProduction().get(4).getValeur()+this.PME3.getProduction().get(4).getValeur()) ;
		prodChocHQ_Eq4 = new Indicateur("Eq4 - prodChocHQ",this,this.PME1.getProduction().get(5).getValeur()+this.PME2.getProduction().get(5).getValeur()+this.PME3.getProduction().get(5).getValeur()) ;

		
		solde = new Indicateur("solde",this,1000) ;
		//On crée la liste qui range nos stocks
		ArrayList<Indicateur> Stocks = new ArrayList<>();
		//On remplit cette liste avec nos stocks
		Stocks.add(new Indicateur("",this,0));
		Stocks.add(stockChocMQ_Eq4);
		Stocks.add(stockChocHQ_Eq4);
		Stocks.add(stockTabBQ_Eq4);
		Stocks.add(stockTabMQ_Eq4);
		Stocks.add(stockTabHQ_Eq4);
		this.Stocks=Stocks;
		// On s'initialise en tant que vendeur
		ArrayList<Integer> stocks1 = new ArrayList<>();
		stocks1.add(0);
		stocks1.add((int)stockChocMQ_Eq4.getValeur());
		stocks1.add((int)stockChocHQ_Eq4.getValeur());
		stocks1.add((int)stockTabBQ_Eq4.getValeur());
		stocks1.add((int)stockTabMQ_Eq4.getValeur());
		stocks1.add((int)stockTabHQ_Eq4.getValeur());
		vendeur = new Vendeur(stocks1);
		

		/**
		 * On ajoute nos indicateurs et notre journal de production et de vente dans la fenêtre principale du Monde
		 * 
		 * @author Mickaël, Etienne
		 */
		Monde.LE_MONDE.ajouterIndicateur(stockChocMQ_Eq4);
		Monde.LE_MONDE.ajouterIndicateur(stockChocHQ_Eq4);
		Monde.LE_MONDE.ajouterIndicateur(stockTabBQ_Eq4);
		Monde.LE_MONDE.ajouterIndicateur(stockTabMQ_Eq4);
		Monde.LE_MONDE.ajouterIndicateur(stockTabHQ_Eq4);
		Monde.LE_MONDE.ajouterIndicateur(solde) ; 
		// Journal pour l'acteur global Eq4Tran ?
		Monde.LE_MONDE.ajouterJournal(this.PME1.getJournalSousActeur());
		Monde.LE_MONDE.ajouterJournal(this.PME2.getJournalSousActeur());
		Monde.LE_MONDE.ajouterJournal(this.PME3.getJournalSousActeur());
		
		new DISTFictif();
	}

	@Override
	public String getNom() {
		return "Eq4TRAN";
	}


	public void next() {
		
		ArrayList<SousActeur> nosPME = new ArrayList<>();
		nosPME.add(PME1);
		nosPME.add(PME2);
		nosPME.add(PME3);
		
		for(SousActeur acteur : nosPME) {
	
			/**
			 * @Mickaël
			 * on doit faire la stratégie marketing
			 */
			
			//Dupliquer pour chaque pme
			
			IVendeurPoudre Eq5TRAN = (IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq5TRAN");
			IVendeurPoudre Eq7TRAN = (IVendeurPoudre) Monde.LE_MONDE.getActeur("Eq7TRAN");
			ContratPoudre[] DevisPoudreEq5_1= new ContratPoudre[3];
			ContratPoudre[] DevisPoudreEq5_2= new ContratPoudre[3];
			ContratPoudre[] DevisPoudreEq7_1= new ContratPoudre[3];
			ContratPoudre[] DevisPoudreEq7_2= new ContratPoudre[3];
			
			// On demande les devis pour tous les contrats pour comparer les prix entre les deux transformateurs.
			DevisPoudreEq5_1 = Eq5TRAN.getDevisPoudre(acteur.getContratPoudreEnCoursEq5TRAN(), (IAcheteurPoudre)acteur);
			DevisPoudreEq5_2 = Eq5TRAN.getDevisPoudre(acteur.getContratPoudreEnCoursEq7TRAN(), (IAcheteurPoudre)acteur);
			DevisPoudreEq7_1 = Eq7TRAN.getDevisPoudre(acteur.getContratPoudreEnCoursEq5TRAN(), (IAcheteurPoudre)acteur);
			DevisPoudreEq7_2 = Eq7TRAN.getDevisPoudre(acteur.getContratPoudreEnCoursEq7TRAN(), (IAcheteurPoudre)acteur);
			
			for (int i=0;i<3;i++) {
				if(DevisPoudreEq5_1[i].getPrix()<DevisPoudreEq7_1[i].getPrix() && DevisPoudreEq5_1[i].getPrix()>0) {
					acteur.getContratPoudreEnCoursEq5TRAN()[i] = DevisPoudreEq5_1[i];
				} else if (DevisPoudreEq7_1[i].getPrix()<DevisPoudreEq5_1[i].getPrix() && DevisPoudreEq7_1[i].getPrix()>0) {
					acteur.getContratPoudreEnCoursEq5TRAN()[i] = DevisPoudreEq7_1[i];
					acteur.getContratPoudreEnCoursEq5TRAN()[i].setVendeur(Eq5TRAN);
				}
				
				if(DevisPoudreEq5_2[i].getPrix()<DevisPoudreEq7_2[i].getPrix() && DevisPoudreEq5_2[i].getPrix()>0) {
					acteur.getContratPoudreEnCoursEq7TRAN()[i] = DevisPoudreEq5_1[i];
					acteur.getContratPoudreEnCoursEq7TRAN()[i].setVendeur(Eq7TRAN);
				} else if (DevisPoudreEq7_2[i].getPrix()<DevisPoudreEq5_2[i].getPrix() && DevisPoudreEq7_2[i].getPrix()>0) {
					acteur.getContratPoudreEnCoursEq7TRAN()[i] = DevisPoudreEq7_2[i];
				}
				
				}
			

				Eq5TRAN.sendReponsePoudre(acteur.getContratPoudreEnCoursEq5TRAN(), (IAcheteurPoudre)acteur);
				Eq7TRAN.sendReponsePoudre(acteur.getContratPoudreEnCoursEq7TRAN(), (IAcheteurPoudre)acteur);
	
			ContratPoudre[] contratfinalEq5TRAN = new ContratPoudre[3];
			ContratPoudre[] contratfinalEq7TRAN = new ContratPoudre[3];
			contratfinalEq5TRAN = Eq5TRAN.getEchangeFinalPoudre(acteur.getContratPoudreEnCoursEq5TRAN(), (IAcheteurPoudre)acteur);
			contratfinalEq7TRAN = Eq7TRAN.getEchangeFinalPoudre(acteur.getContratPoudreEnCoursEq7TRAN(), (IAcheteurPoudre)acteur);
	
			ArrayList<ContratPoudre> contratPoudreEnCours = new ArrayList<ContratPoudre>();
			contratPoudreEnCours.add(contratfinalEq5TRAN[0]);
			contratPoudreEnCours.add(contratfinalEq5TRAN[1]);
			contratPoudreEnCours.add(contratfinalEq5TRAN[2]);
			contratPoudreEnCours.add(contratfinalEq7TRAN[0]);
			contratPoudreEnCours.add(contratfinalEq7TRAN[1]);
			contratPoudreEnCours.add(contratfinalEq7TRAN[2]);
	
			for(int i = 0 ; i < contratPoudreEnCours.size() ; i++ ) {
	
				/**
				 * On récupère les qtés de poudre achetée
				 * On les transforme en produits
				 * Puis on les stocke
				 */
				Acteur a;
				
				if(contratPoudreEnCours.get(i).getReponse()) {
					for(int j=0;j<=2;j++) {
						if (contratPoudreEnCours.get(i).getQualite() == j) {
							if(i<3) a = Monde.LE_MONDE.getActeur("Eq5TRAN");
							else a = Monde.LE_MONDE.getActeur("Eq7TRAN");
							acteur.getProduction().get(j).setValeur(acteur, contratPoudreEnCours.get(i).getQuantite());
							acteur.getJournalSousActeur().ajouter("L'équipe 4 a acheté : " + contratPoudreEnCours.get(i).getQuantite()+" de Poudre" +Marchandises.getQualite(i)+ " à " + a.getNom() + " pour " + contratPoudreEnCours.get(i).getQuantite());
							double ancienStockChoc = acteur.getStocks().get(j).getValeur() ;
							acteur.getStocks().get(j).setValeur(acteur, ancienStockChoc + acteur.getProduction().get(j).getValeur());
							solde.setValeur(acteur, solde.getValeur()-contratPoudreEnCours.get(i).getPrix()*contratPoudreEnCours.get(i).getQuantite());
						} 
				}
			} 
	
			// On se transforme désormais en vendeur, le réapprovisionnement de nos stocks ayant été effectué
			// Nous allons alors vendre des marchandises aux distributeurs par l'intermédiaire du MarchéChoco()
				
			ArrayList<Integer> stocks = new ArrayList<>();
			stocks.add(0); // Nono met l'indicateur de stock ChocBQ
			for(int k=1;k<6;k++) {
				stocks.add(acteur.getStock().get(k));
			}
			acteur.setVendeur(new Vendeur(stocks));
			}	
			
		}

		for(int i=0;i<this.Stocks.size();i++) {
			Stocks.get(i).setValeur(Eq4TRAN, this.PME1.getStocks().get(i).getValeur()+this.PME2.getStocks().get(i).getValeur()+this.PME3.getStocks().get(i).getValeur());
		}
		
		this.solde.setValeur(Eq4TRAN, this.PME1.getSolde().getValeur()+this.PME2.getSolde().getValeur()+this.PME3.getSolde().getValeur());
		
		// On met à jour le journal des trois acteurs
		journalEq4(this.PME1);
		journalEq4(this.PME2);
		journalEq4(this.PME3);
		
		//On prend en compte les coûts supplémentaires
		PME1.coutsSupplementaires();
		PME2.coutsSupplementaires();
		PME3.coutsSupplementaires();
		
	}

	public void journalEq4(SousActeur j) {
		j.getJournalSousActeur().ajouter("Stock des tablettes Haute Qualité = "+j.getQuantite(6));
		j.getJournalSousActeur().ajouter("Stock des tablettes Moyenne Qualité = "+j.getQuantite(5));
		j.getJournalSousActeur().ajouter("Stock des tablettes Basse Qualité = "+j.getQuantite(4));
		j.getJournalSousActeur().ajouter("Stock des chocolats Moyenne Qualité = "+j.getQuantite(2));
		j.getJournalSousActeur().ajouter("Stock des chocolats Haute Qualité = "+j.getQuantite(3));
		// Les Sous-Acteurs produisent ils vraiment des choses ? (rajouter alors getQuantitePorduite() et setter dans 
		// Sous-Acteur
		
		/*j.getJournalEq4().ajouter("Production des tablettes Basse Qualité = "+j.get);
		j.getJournalEq4().ajouter("Production des tablettes Moyenne Qualité = "+j.getProdTabBQ());
		j.getJournalEq4().ajouter("Production des tablettes Haute Qualité = "+j.getProdTabBQ());
		j.getJournalEq4().ajouter("Production de chocolats Moyenne Qualité = "+j.getProdChocMQ());
		j.getJournalEq4().ajouter("Production de chocolats Haute Qualité = "+j.getProdChocHQ()); */

	}

	public Journal getJournalVentes() {
		return vendeur.getVentes();
	}

}
