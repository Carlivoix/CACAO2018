package abstraction.eq4TRAN.VendeurChoco;

import java.util.ArrayList;
import java.util.Collections;

import abstraction.eq4TRAN.IVendeurChocoBis;
import abstraction.fourni.Journal;

/**
 * 
 * @author Etienne
 *
 */
public class Vendeur implements IVendeurChocoBis{
	/*
	 * classe implémentant les méthodes nécessaires à l'interface IVendeurChoco
	 */
	
	public static double COMMERCE_EQUITABLE=1.27;
	public static double PRODUCTEUR_LOCAL=1.96;
	public static double BIO=1.78;
	public static double LUXE=1.53;
	public static double ARTISANAL=3.78;
	
	private ArrayList<Integer> stocks;
	public Journal ventes = new Journal("Eq4 - Ventes");
	
	// Constructeur créant un Vendeur avec une liste de 6 paramètres : les stocks des 6 produits
	public Vendeur(ArrayList<Integer> quantites) {
		ArrayList<Integer> Stocks = new ArrayList<>();
		for(int i=0;i<6;i++) {
			Stocks.add(i,(quantites.get(i)>=0) ? quantites.get(i) : 0);
		}
		stocks=Stocks;
	}
	
	// Constructeur sans paramètre initialisant les stocks à 0
	public Vendeur() {
		stocks = new ArrayList<>(Collections.nCopies(6, 0));
	}

	
	// Getters et setters des stocks du Vendeur
	
	public int getQte(int IDProduit) {
		return getStock().get(IDProduit-1);
	}

	public void setQte(int IDProduit, int quantite) {
		getStock().set(IDProduit, quantite);
	}
	
	// Implémente getStock
	public ArrayList<Integer> getStock(){
		return stocks;
	}
	
	// Rendre utilisable pour autres transformateurs
	//Implémente getPrix 
	public GPrix2 getPrix() {
		ArrayList<Double[]> intervalles = new ArrayList<>();
		Double[] interval = {10.0,50.0,100.0,250.0,500.0,750.0,1000.0};
		for(int i=0;i<6;i++) {
			// On considère les mêmes intervalles pour chaque produit dans un premier temps
			intervalles.add(interval);
		}
		ArrayList<Double[] > prix = new ArrayList<>();
		//Discuter de la stratégie d'etagement des prix
		//On définit nos prix selon les quantités et pour chaque produit
		Double[] prix4 = {0.695, 0.650, 0.625, 0.6, 0.575, 0.55, 0.525};
		Double[] prix5 = {1.1, 1.075, 1.05, 1.025, 1.0, 0.975, 0.95};
		Double[] prix6 = {1.975, 1.95, 1.9, 1.875, 1.85, 1.825, 1.8};
		Double[] prix1 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		Double[] prix2 = {3.975, 3.95, 3.9, 3.875, 3.85, 3.825, 3.8};
		Double[] prix3 = {6.375, 6.35, 6.325, 6.3, 6.275, 6.25, 6.2};
		//on concatène tous les tableaux de prix dans un Tableau GPrix2
		prix.add(prix1);
		prix.add(prix2);
		prix.add(prix3);
		prix.add(prix4);
		prix.add(prix5);
		prix.add(prix6);
		return new GPrix2(intervalles, prix);
	}
	
	// A améliorer pour maximiser les livraisons.
	
	// Implémente getLivraison() (Attention : ne gère ni les stocks ni le solde de l'Acteur qui utilise Vendeur)
	public ArrayList<ArrayList<Integer>> getLivraison(ArrayList<ArrayList<Integer>> commandes) {
		ArrayList<Integer> commande1 = commandes.get(0);
		// On considère que commande1 correspond à la commande de l'équipe eq1DIST
		ArrayList<Integer> commande2 = commandes.get(1);
		ArrayList<Integer> commande3 = commandes.get(2);
		ArrayList<ArrayList<Integer>> Livraison = new ArrayList<>();
		for(int i=0;i<6;i++) {
			// On regarde si un distributeur n'a pas envoyé de commande, auquel cas on lui renvoie une livraison nulle.
			if(commande1==null) {
				if(commande2.get(i)+commande3.get(i)>getQte(i+1)) {
					commande3.set(i, 0);
					commande2.set(i, getQte(i+1));
				}
			}
			if(commande2==null) {
				if(commande1.get(i)+commande3.get(i)>getQte(i+1)) {
					commande3.set(i, 0);
					commande1.set(i, getQte(i+1));
				}
			}
			if(commande3==null) {
				if(commande1.get(i)+commande2.get(i)>getQte(i+1)) {
					commande2.set(i, getQte(i+1)-commande1.get(i));
				}
			}
			else {
				// Si la somme des commandes 1 et 2 dépasse notre stock pour certains produits, on plafonne la 2 
				// qui en théorie commande plus. On ne vend alors rien au distibuteur fictif.
				if(commande1.get(i)+commande2.get(i)>getQte(i+1)) {
					commande2.set(i, getQte(i+1)-commande1.get(i));
					commande3.set(i, 0);
				}
				else {
					// On plafonne la commande3, le distributeur fictif n'étant là que pour compléter les commandes.
					if(commande1.get(i)+commande2.get(i)+commande3.get(i)>getQte(i+1)) {
						commande3.set(i, getQte(i+1)-commande1.get(i)-commande2.get(i));
					}
				}
			}
			}
		//On insère les livraisons aux deux distributeurs dans la liste Livraison
		Livraison.add(commande1);
		Livraison.add(commande2);
		Livraison.add(commande3);
		// On ajoute la vente à notre journal de ventes
		getVentes().ajouter("Livraison : " + Livraison);
		return Livraison;
	}

	public Journal getVentes() {
		return ventes;
	}
}
