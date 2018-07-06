package abstraction.eq1DIST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import abstraction.eq4TRAN.Eq4TRAN;
import abstraction.eq4TRAN.IVendeurChocoBis;
import abstraction.eq4TRAN.VendeurChoco.GPrix2;
import abstraction.eq5TRAN.Eq5TRAN;
import abstraction.eq5TRAN.appeldOffre.DemandeAO;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChoco;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChocoBis;
import abstraction.eq6DIST.IAcheteurChoco;
import abstraction.eq6DIST.IAcheteurChocoBis;
import abstraction.eq7TRAN.Eq7TRAN;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;



  
public class Eq1DIST implements Acteur, InterfaceDistributeurClient, IAcheteurChocoBis {
	private Stock stock;
	private Journal journal;
	private Indicateur[] stocks;
	private Indicateur solde;
	private String nom;
	private Indicateur[] nombreVentes;
	private Indicateur[] nombreAchatsOccasionnels;
	private Indicateur[] nombreAchatsContrat;
	private Indicateur efficacite;
	
	private Indicateur PrixChocoMdG;
	private Indicateur PrixChocoHdG;
	private Indicateur PrixConfMdG;
	private Indicateur PrixConfHdG;


	public Eq1DIST()  {
		double[][] PartsdeMarche= {{0.7,0.49,0,0,0.42,0},
				                   {0,0.21,0.7,0,0.28,0.7},
				                   {0.3,0.3,0.3,0,0.3,0.3}};
		Journal client = new Journal("Clients Finaux");
		Monde.LE_MONDE.ajouterJournal(client);
		Monde.LE_MONDE.ajouterActeur(new Client(PartsdeMarche,client));
		this.stock = new Stock(0,50000,25000,0,35000,15000); 
		this.nombreAchatsOccasionnels = new Indicateur[6];
		this.nombreAchatsContrat = new Indicateur[6];
		this.nombreVentes = new Indicateur[6];
		this.stocks = new Indicateur[6];
		stocks[0]= new Indicateur();
		this.solde = new Indicateur("Solde de "+ this.getNom(), this,500000);
		Monde.LE_MONDE.ajouterIndicateur(this.solde);
		this.efficacite = new Indicateur("Efficacité de "+ this.getNom(), this,0);
		this.PrixChocoMdG=new Indicateur("Prix Choco MdG de "+this.getNom(),this,1.5);
		Monde.LE_MONDE.ajouterIndicateur(this.PrixChocoMdG);
		this.PrixChocoHdG=new Indicateur("Prix Choco HdG de "+this.getNom(),this,3.0);
		Monde.LE_MONDE.ajouterIndicateur(this.PrixChocoHdG);
		this.PrixConfMdG=new Indicateur("Prix Confiseries MdG de "+this.getNom(),this,2.6);
		Monde.LE_MONDE.ajouterIndicateur(this.PrixConfMdG);
		this.PrixConfHdG=new Indicateur("Prix Confiseries HdG de "+this.getNom(),this,4.1);
		Monde.LE_MONDE.ajouterIndicateur(this.PrixConfHdG);
			
			this.journal= new Journal("Journal de Eq1DIST");
			journal.ajouter("Absentéisme");
			Monde.LE_MONDE.ajouterJournal(this.journal);
	}
	@Override
	public String getNom() {
		return "Eq1DIST";
	}

	@Override
	public void next() {
		
		this.venteOccalim();
		this.venteOccaspe();
		
	}
	
	public int[] venteOccalim() {
		// on fait une demande occasionnelle si on dépasse un seuil limite de stock
		int[] vente = {0,0,0,0,0,0};
		int[] stocklim = {0,120000,30000,0,40000,20000};
		List<IvendeurOccasionnelChocoBis> vendeursOcca = new ArrayList<IvendeurOccasionnelChocoBis>();
		for (Acteur a : Monde.LE_MONDE.getActeurs()) {
			if (a instanceof IvendeurOccasionnelChocoBis) {
				vendeursOcca.add((IvendeurOccasionnelChocoBis) a);
			}
		}		
		for (int i =0; i<this.stock.getstock().size();i++) {
			if (this.stock.getstock().get(i).total()<stocklim[i]) {
				DemandeAO d= new DemandeAO(stocklim[i]-this.stock.getstock().get(i).total(),i+1);
				ArrayList<Integer> prop= new ArrayList<Integer>();
				for (IvendeurOccasionnelChocoBis v : vendeursOcca) {
					prop.add(v.getReponseBis(d));
				}
				int a=Integer.MAX_VALUE;
				int n=0;
				for(int ind=0; ind<prop.size(); ind++){
			  		if(a>prop.get(ind)){
			  			a=prop.get(ind);
			  			n=ind;
			  		}
				}
				if (a!=Double.MAX_VALUE){
				  	this.stock.ajouter(d.getQuantite(),i);
				  	solde.setValeur(this, solde.getValeur()-a);
				  	vendeursOcca.get(n).envoyerReponseBis(d.getQuantite(),d.getQualite(),a);
				  	vente[i] = stocklim[i]-this.stock.getstock().get(i).total(); // dans le next on utilise cette variable pour connaitre la somme de nos vente occasionelles
				} 
							 
			}
		}
		return vente;
	}
	
	public int[] venteOccaspe() {
		// on fait une demande occasionnelle en prevision des mois de forte consomation
		int[] vente  = {0,0,0,0,0,0};
		if(Monde.LE_MONDE.getStep()%12==2
				||Monde.LE_MONDE.getStep()%12==3
				||Monde.LE_MONDE.getStep()%12==4
				||Monde.LE_MONDE.getStep()%12==5
				||Monde.LE_MONDE.getStep()%12==18
				||Monde.LE_MONDE.getStep()%12==19
				||Monde.LE_MONDE.getStep()%12==20
				||Monde.LE_MONDE.getStep()%12==21) {
			int[] stockspe = {0,29877,13125,0,21875,9375};
			List<IvendeurOccasionnelChocoBis> vendeursOcca = new ArrayList<IvendeurOccasionnelChocoBis>();
			for (int i =0; i<this.stock.getstock().size();i++) {
					DemandeAO d= new DemandeAO(stockspe[i],i+1);
					ArrayList<Integer> prop= new ArrayList<Integer>();
					for (IvendeurOccasionnelChocoBis v : vendeursOcca) {
						prop.add(v.getReponseBis(d));
					}
					int a=Integer.MAX_VALUE;
					int n=0;
					for(int ind=0; ind<prop.size(); ind++){
					  		if(a>prop.get(ind)){
					  			a=prop.get(ind);
					  			n=ind;
					  		}
					  }
					 if (a!=Double.MAX_VALUE){
					  	this.stock.ajouter(d.getQuantite(),i);
					  	solde.setValeur(this, solde.getValeur()-a);
					  	vendeursOcca.get(n).envoyerReponseBis(d.getQuantite(),d.getQualite(),a);
					  	vente[i]=stockspe[i];
					  } 
					 
			}
		}
		return vente;
	}

	@Override
	public GrilleQuantite commander(GrilleQuantite Q) {
		int[] res = new int[6];
		double[] prix = {Double.MAX_VALUE,this.PrixChocoMdG.getValeur(),this.PrixChocoHdG.getValeur(),Double.MAX_VALUE,this.PrixConfMdG.getValeur(),this.PrixConfHdG.getValeur()};
		for (int i =0; i <6;i++) {
			int f = this.stock.getstock().get(i).total()-Q.getValeur(i);
			if (f>=0) {
				res[i] = Q.getValeur(i);
				this.stock.retirer(Q.getValeur(i),i+1);
			}
			else {
				res[i] = this.stock.getstock().get(i).total();
				this.stock.retirer(this.stock.getstock().get(i).total(),i+1);
				}
				solde.setValeur(this, solde.getValeur()+res[i]*prix[i]);
		}
		// mise a jour de l'efficacite en fonction de ce qu'on a pu vendre
		// selon ce qu'on nous avait demande
		double somme = 0;
		double a = 0;
		for (int i=0; i<res.length; i++) {
			a = a + (Q.getValeur(i)-res[i]);
			somme = somme + Q.getValeur(i);
		}
		solde.setValeur(this, 1-(double) a/somme);
		
		return new GrilleQuantite(res);
		
	}
	
	
	
	public ArrayList<ArrayList<Integer>> getCommande(ArrayList<GPrix2> Prix, ArrayList<ArrayList<Integer>> Stock) {
		int[] demande;
		demande = new int[6];
		demande[0]=0;
		demande[1]=39834; // changer les indices
		demande[2]=17500;
		demande[3]=0;
		demande[4]=29167;
		demande[5]=12500;
		double st = 
			ArrayList<ArrayList<Integer>> commandeFinale = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> listeT = new ArrayList<Integer>() ;
			String act = "" ;
			ArrayList<Acteur> acteurs=Monde.LE_MONDE.getActeurs();
			ArrayList<IVendeurChocoBis> transfo = new ArrayList<IVendeurChocoBis>();
			for (Acteur a : acteurs) {
				if(a instanceof IVendeurChocoBis) {
					transfo.add((IVendeurChocoBis) a);
				}}
				double[] m = new double[6];
				for (int i =0;i<6;i++) {
				
				while ( m[i]!=1){
				
					ArrayList<Double> prix ;
					prix = new ArrayList<Double>();
					for (int j =0; j<transfo.size();j++) {
						prix.add(transfo.get(j).getPrix().getPrixProduit(demande[i],i));
					}
					listeT = listeTriee(prix);
					
						if(Stock.get(listeT.indexOf(0)).get(i)>= 0.6*demande[i]){
							commandeFinale.get(listeT.indexOf(0)).set(i,(((int)0.6*demande[i]))) ;
							m[i]+=0.6;
							if(Stock.get(listeT.indexOf(1)).get(i)>= 0.3*demande[i]) {
								commandeFinale.get(listeT.indexOf(1)).set(i,((int)0.3*demande[i]));
								m[i]+=0.3;
								if(Stock.get(listeT.indexOf(2)).get(i)>= 0.1*demande[i]) {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(0.1*demande[i])));
									m[i]+=0.1;
								}
								else {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(Stock.get(listeT.indexOf(2)).get(i))));
									m[i]=1;
								}
							}
							else {
								commandeFinale.get(listeT.indexOf(1)).set(i,((int)(Stock.get(listeT.indexOf(1)).get(i))));
								m[i]+=Stock.get(listeT.indexOf(1)).get(i)/demande[i];
								if(Stock.get(listeT.indexOf(2)).get(i)>= (1-m[i])*demande[i]) {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)((1-m[i])*demande[i])));
								}
								else {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(Stock.get(listeT.indexOf(2)).get(i))));
									m[i]=1;
								}
							}
						}
						else {
							commandeFinale.get(listeT.indexOf(0)).set(i,((int)(Stock.get(listeT.indexOf(0)).get(i))));
							m[i]+= Stock.get(listeT.indexOf(0)).get(i)/demande[i];
							if(Stock.get(listeT.indexOf(1)).get(i)>= (0.9-m[i])*demande[i]) {
								commandeFinale.get(listeT.indexOf(1)).set(i,((int)((0.9-m[i])*demande[i])));
								m[i] = 0.9;
								if(Stock.get(listeT.indexOf(2)).get(i)>= 0.1*demande[i]) {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(0.1*demande[i])));
									m[i]+=0.1;
								}
								else {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(Stock.get(listeT.indexOf(2)).get(i))));
									m[i]=1;
								}
							}
							else {
								commandeFinale.get(listeT.indexOf(1)).set(i,((int)(Stock.get(listeT.indexOf(1)).get(i))));
								m[i]+=Stock.get(listeT.indexOf(1)).get(i)/demande[i];
								if(Stock.get(listeT.indexOf(2)).get(i)>= (1-m[i])*demande[i]) {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)((1-m[i])*demande[i])));
								}
								else {
									commandeFinale.get(listeT.indexOf(2)).set(i,((int)(Stock.get(listeT.indexOf(2)).get(i))));
									m[i]=1;
								}
						}
						
								
							}
				}}
							
						
			return commandeFinale;
		}
	
	public void main(String[] Args) {
		ArrayList<GPrix2> Prix = new ArrayList<GPrix2>() ;
		ArrayList<ArrayList<Integer>> Stock = new ArrayList<ArrayList<Integer>>();
		Double[] interval = {0.0,10.0,50.0,100.0,250.0,500.0,750.0,1000.0};
		Double[] prix2 = {4.0, 3.975, 3.95, 3.9, 3.875, 3.85, 3.825, 3.8};
		Double[] prix3 = {4.5, 3.975, 3.55, 3.2, 3.125, 3.12, 3.36, 3.7};
		Double[] prix4 = {4.7, 3.12, 3.74, 3.3, 3.147, 3.85, 3.52, 3.82};
		ArrayList<Double[]> p = new ArrayList<Double[]>();
		p.add(prix2);
		p.add(prix3);
		p.add(prix4);
		p.add(prix2);
		p.add(prix3);
		p.add(prix4);
		ArrayList<Double[]> i = new ArrayList<Double[]>();
		i.add(interval);
		i.add(interval);
		i.add(interval);
		i.add(interval);
		i.add(interval);
		i.add(interval);
		GPrix2 prix = new GPrix2(i,p);
		
		
		
	}
	
	public ArrayList<Integer> listeTriee(ArrayList<Double> prix){
		ArrayList<Double> copie = new ArrayList<Double>();
		for (int i=0;i<3;i++) {
			copie.add(prix.get(i));
		}
		Collections.sort(copie);
		ArrayList<Integer> min = new ArrayList<Integer>();
		min.add(prix.indexOf(copie.get(0)));
		min.add(prix.indexOf(copie.get(1)));
		min.add(prix.indexOf(copie.get(2)));
		return min;
				}
		
		

	/* public ArrayList<ArrayList<Integer>> getCommande(ArrayList<GPrix2> Prix, ArrayList<ArrayList<Integer>> Stock) {
		ArrayList<ArrayList<Integer>> commandeFinale;
		commandeFinale = new ArrayList<ArrayList<Integer>>();
		
		 * System.out.println("appelee ...");
		 * double[]demande;
		demande = new double[6];
		demande[0]=0;
		demande[1]=39834;
		demande[2]=17500;
		demande[3]=0;
		demande[4]=29167;
		demande[5]=12500;
		double[] p;
		p= new double[3];
		double somme;
		
		for (int i=0;i<6;i++){
			somme = 0;
			for (int h=0;h<3;h++) {
				somme += stock[h][i]; 
			}
			for (int j=0;j<3;j++) {			
			p[i]=stock[j][i]/somme;
			if(p[i]*demande[i]<= stock[j][i]){
				commandeFinale.get(j).set(i, ((int)(p[i]*demande[i])));
			}
			else {
				commandeFinale.get(j).set(i,((int)(stock[0][i])));
			}

	}
			}
		 
		return commandeFinale;
		}*/
	 
	@Override
	public void livraison(ArrayList<Integer> livraison, double paiement) {
		for (int i=0;i<livraison.size();i++) {
			this.stock.retirer(livraison.get(i), i+1);
			stocks[i].setValeur(this, this.stock.getstock().get(i).total());
			solde.setValeur(this,solde.getValeur()+paiement);
		}
		
	}
	
	@Override
	public double[] getPrix() {
		// TODO Auto-generated method stub
		return new double[] {0,this.PrixChocoMdG.getValeur(),this.PrixChocoHdG.getValeur(),
				0,this.PrixConfMdG.getValeur(),this.PrixConfHdG.getValeur()};
	}
	
	/**
	 *
	 * @param double[]
	 *            PrixAchat (tableau des prix d'achats (1x6) chocoBdG,chocoMdG,chocoHdG,confBdG,confMdG,confHdG )
	 * 
	 * @return change les prix de ventes de façon à avoir une marge de 16%
	 */
	private void changerPrix(double[] PrixAchat) {
		this.PrixChocoMdG.setValeur(this, PrixAchat[1]*1.16);
		this.PrixChocoHdG.setValeur(this, PrixAchat[2]*1.16);
		this.PrixConfMdG.setValeur(this, PrixAchat[4]*1.16);
		this.PrixConfHdG.setValeur(this, PrixAchat[5]*1.16);
		this.journal.ajouter("Changement des prix : \n"
				+ "Prix chocolat milieu de gamme = "+this.PrixChocoMdG.getValeur()+"\n"
				+ "Prix chocolat haut de gamme = "+this.PrixChocoHdG.getValeur()+"\n"
				+ "Prix confiseries milieu de gamme = "+this.PrixConfMdG.getValeur()+"\n"
				+ "Prix confiseries haut de gamme = "+this.PrixConfHdG.getValeur()+"\n");
	}
}
