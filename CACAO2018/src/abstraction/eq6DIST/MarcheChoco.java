package abstraction.eq6DIST; // Karel Kédémos , Victor Signes, Léopold Petitjean
import java.util.ArrayList;

import abstraction.eq4TRAN.IVendeurChoco;
import abstraction.eq4TRAN.VendeurChoco.GPrix;
import abstraction.eq4TRAN.VendeurChoco.GQte;
import abstraction.fourni.Acteur;
import abstraction.fourni.Monde;

public class MarcheChoco  {
	private ArrayList<GQte> stock;
	private ArrayList<GPrix> prix;
	private ArrayList <Acteur> distributeurs;
	private ArrayList <Acteur> transformateurs;
	
	public MarcheChoco() {
		
	this.distributeurs= new ArrayList<Acteur>();
	this.distributeurs.add((Monde.LE_MONDE.getActeur("Eq6DIST")));
	this.distributeurs.add((Monde.LE_MONDE.getActeur("Eq1DIST")));
	this.transformateurs= new ArrayList<Acteur>();
	this.transformateurs.add( Monde.LE_MONDE.getActeur("Eq4TRAN"));
	this.transformateurs.add( Monde.LE_MONDE.getActeur("Eq5TRAN"));
	this.transformateurs.add( Monde.LE_MONDE.getActeur("Eq7TRAN"));
	this.stock= new ArrayList <GQte>();
	this.prix= new ArrayList <GPrix>();
	
	
	for (Acteur i : this.transformateurs) {
		IVendeurChoco ibis= (IVendeurChoco) i;
		this.prix.add(ibis.getPrix());
		this.stock.add(ibis.getStock());
			
	}	
	}
	public void next() {
		MarcheChoco MC = new MarcheChoco();
		ArrayList<GQte> commande=  new ArrayList<GQte>();
		for (Acteur i : MC.distributeurs) {
			IAcheteurChoco ibis = (IAcheteurChoco) i;
			//commande.add(ibis.getCommande(MC.prix, MC.stock));
		}
		ArrayList<ArrayList<GQte>> livraison = new ArrayList<ArrayList<GQte>>();
		for(int j =0; j<3;j++) {
			ArrayList<GQte> Livraisoni =new ArrayList<GQte>(); 
			for (int i=0 ; i<commande.size() ; i++) {
				Livraisoni.add(commande.get(i));
			}
			livraison.add(Livraisoni);		
		}
		int l=0;
		ArrayList<ArrayList<GQte>> Delivery = new ArrayList<ArrayList<GQte>>();
		for (Acteur i : MC.transformateurs)	{
			IVendeurChoco ibis = (IVendeurChoco) i;
			//Delivery.add(ibis.getLivraison(livraison.get(l)));
			l++;
		}
		l=0;
		for (int j=0;j<2;j++) {
			int qBonbonBQj=0;
			int qBonbonMQj=0;
			int qBonbonHQj=0;
			int qTabletteBQj=0;
			int qTabletteMQj=0;
			int qTabletteHQj=0;
			for (int i=0; i<Delivery.size(); i++) {
				qBonbonBQj=qBonbonBQj+Delivery.get(i).get(j).getqBonbonBQ();
				qBonbonMQj=qBonbonMQj+Delivery.get(i).get(j).getqBonbonMQ();
				qBonbonHQj=qBonbonHQj+Delivery.get(i).get(j).getqBonbonHQ();
				qTabletteBQj=qTabletteBQj+Delivery.get(i).get(j).getqTabletteBQ();
				qTabletteMQj=qTabletteMQj+Delivery.get(i).get(j).getqTabletteMQ();
				qTabletteHQj=qTabletteHQj+Delivery.get(i).get(j).getqTabletteHQ();
			}
			GQte Deliveryj= new GQte(qBonbonBQj,qBonbonMQj,qBonbonHQj,qTabletteBQj,qTabletteMQj,qTabletteHQj);
		}
		for (Acteur i : MC.distributeurs) {
			
		}
	}
	
}