package abstraction.eq1DIST;

import java.util.ArrayList;

import abstraction.eq4TRAN.Eq4TRAN;
import abstraction.eq4TRAN.VendeurChoco.GPrix;
import abstraction.eq4TRAN.VendeurChoco.GPrix2;
import abstraction.eq4TRAN.VendeurChoco.GQte;
import abstraction.eq5TRAN.Eq5TRAN;
import abstraction.eq5TRAN.appeldOffre.DemandeAO;
import abstraction.eq5TRAN.appeldOffre.IvendeurOccasionnelChoco;
import abstraction.eq6DIST.IAcheteurChoco;
import abstraction.eq6DIST.IAcheteurChocoBis;
import abstraction.eq7TRAN.Eq7TRAN;
import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;



  
public class Eq1DIST implements Acteur, InterfaceDistributeurClient, IAcheteurChocoBis {
	private int[][] stock;
	private double banque;
	private Journal journal;
	private Indicateur[] stocks;
	private Indicateur solde;
	private String nom;
	private Indicateur[] nombreVentes;
	private Indicateur[] nombreAchatsOccasionnels;
	private Indicateur[] nombreAchatsContrat;
	private Indicateur efficacite;


public Eq1DIST()  {
	double[][] PartsdeMarche= {{0.7,0.49,0,0,0.42,0},
			                   {0,0.21,0.7,0,0.28,0.7},
			                   {0.3,0.3,0.3,0,0.3,0.3}};
	Journal client = new Journal("Clients Finaux");
	Monde.LE_MONDE.ajouterJournal(client);
	Monde.LE_MONDE.ajouterActeur(new Client(PartsdeMarche,client));
	int[][] stock= new int[2][3];
	stock[0][0] = 0 ;
	stock[0][1] = 50000 ;
	stock[0][2] = 25000;
	stock[1][0] = 0 ;
	stock[1][1] = 35000;
	stock[1][2] = 15000;
	this.nombreAchatsOccasionnels = new Indicateur[6];
	this.nombreAchatsContrat = new Indicateur[6];
	this.nombreVentes = new Indicateur[6];
	this.stocks = new Indicateur[6];
	this.solde = new Indicateur("Solde de "+ this.getNom(), this,0);
	this.efficacite = new Indicateur("Efficacité de "+ this.getNom(), this,0);
	this.stock=stock;
		
		this.journal= new Journal("Journal de Eq1DIST");
		journal.ajouter("Absentéisme");
		Monde.LE_MONDE.ajouterJournal(this.journal);
}
	@Override
	public String getNom() {
		// TODO Auto-generated method stub
		return "Eq1DIST";
	}

	@Override
	public void next() {
		int[][] stocklim = new int[][] {
			{0,120000,30000},
			{0,40000,20000}
		};
		
		for (int i =0; i<3;i++) {
			for (int j=0;j<3;j++) {
				if (stock[i][j]<stocklim[i][j]) {
					DemandeAO d= new DemandeAO(stocklim[i][j]-stock[i][j],i*j);
					ArrayList<Double> prop= new ArrayList<Double>();
					prop.add(((Eq4TRAN) Monde.LE_MONDE.getActeur("Eq4TRAN")).getReponseBis(d));
					prop.add((double) 4);
					prop.add(((Eq5TRAN) Monde.LE_MONDE.getActeur("Eq5TRAN")).getReponseBis(d));
					prop.add((double) 5);
					prop.add(((Eq7TRAN) Monde.LE_MONDE.getActeur("Eq7TRAN")).getReponseBis(d));
					prop.add((double) 7);
					int a=prop.get(0);
					double n= (prop.get(1));
					 for(int ind=1; ind<3; ind++){
					  		if(a>prop.get(ind+2)){
					  			a=prop.get(ind+2);
					  			n=prop.get(ind+3);
					  		}
					  }
					 if (a!=Double.MAX_VALUE){
					  		this.stock[i][j]+=d.getQuantite();
					  		this.banque-=a;
					  		String l = "Eq"+((int) n)+"TRAN";
					  		((IvendeurOccasionnelChoco) Monde.LE_MONDE.getActeur(l)).envoyerReponseBis(d.getQuantite(),d.getQualite(),a);
					  } 
					 
				}
					
			}
		}	
		if(Monde.LE_MONDE.getStep()%12==2
				||Monde.LE_MONDE.getStep()%12==2
				||Monde.LE_MONDE.getStep()%12==3
				||Monde.LE_MONDE.getStep()%12==4
				||Monde.LE_MONDE.getStep()%12==5
				||Monde.LE_MONDE.getStep()%12==18
				||Monde.LE_MONDE.getStep()%12==19
				||Monde.LE_MONDE.getStep()%12==20
				||Monde.LE_MONDE.getStep()%12==21) {
			int[][] stockspe= new int[][] {
				{0,29877,13125},
				{0,21875,9375}
			};
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					DemandeAO d= new DemandeAO(stockspe[i][j],i*j);
					ArrayList<Double> prop= new ArrayList<Double>();
					prop.add(((Eq4TRAN) Monde.LE_MONDE.getActeur("Eq4TRAN")).getReponseBis(d));
					prop.add(((Eq5TRAN) Monde.LE_MONDE.getActeur("Eq5TRAN")).getReponseBis(d));
					prop.add(((Eq7TRAN) Monde.LE_MONDE.getActeur("Eq7TRAN")).getReponseBis(d));
				}
			}
		}
	}

	@Override
	public GrilleQuantite commander(GrilleQuantite Q) {
		int[] res = new int[6];
		double[][] prix = new double[][] {
			{0.9,1.5,3.0},
			{1.0,2.6,4.1}
		};
		for (int i =0; i <2;i++) {
			for (int j = 0; j <3;j++) {
				int f = this.stock[i][j]-Q.getValeur(3*i+j);
				if (f>=0) {
					res[3*i+j] = Q.getValeur(3*i+j);
					this.stock[i][j]-=Q.getValeur(3*i+j);
				}
				else {
					res[3*i+j] = stock[i][j];
					this.stock[i][j]=0;
					}
				this.banque += res[3*i+j]*prix[i][j];
			}
		}
		return new GrilleQuantite(res);
	}
	
	
	public void livraison(GQte d,double solde) {
		for(int i=0; i<3;i++) {
			stock[0][0] += d.getqTabletteBQ();
			stock[0][1] += d.getqTabletteMQ();
			stock[0][2] += d.getqTabletteHQ();
			stock[1][0] += d.getqBonbonBQ();
			stock[1][1] += d.getqBonbonMQ();
			stock[1][2] += d.getqBonbonHQ();
		}
		this.banque -= solde;
	}
	@Override
	public ArrayList<ArrayList<Integer>> getCommande(ArrayList<GPrix2> Prix, ArrayList<ArrayList<Integer>> Stock) {
		double[]demande;
		demande = new double[6];
		double[] p;
		double somme;
		ArrayList<ArrayList<Integer>> commandeFinale;
		commandeFinale = new ArrayList<ArrayList<Integer>>();
		for (int i=0;i<6;i++){
			for (int j=0;j<3;j++) {	
			somme = 0;
			double a4;
			double a5;
			double a7;
			for (int j=0;j<3;j++){
				somme += stock[j][i];
			}
			p[i]=stock[j][i]/somme;
			if(p[i]*demande[i]<= stock[j][i]){
				commandeFinale.get(j).get(i).add(p[i]*demande[i]);
			}
			else {
				commandeFinale.get(j).get(i).add(stock[0][i]);
			}

	}}}
	@Override
	public void livraison(ArrayList<Integer> livraison, double paiement) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public double[] getPrix() {
		// TODO Auto-generated method stub
		return null;
	}



	

	

}
