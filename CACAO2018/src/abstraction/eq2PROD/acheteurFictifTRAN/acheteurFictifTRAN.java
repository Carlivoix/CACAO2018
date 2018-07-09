package abstraction.eq2PROD.acheteurFictifTRAN;

import abstraction.fourni.Acteur;
import abstraction.fourni.Monde;

import java.util.ArrayList;
import java.util.List;

import abstraction.eq3PROD.echangesProdTransfo.*;

public class acheteurFictifTRAN implements Acteur, IAcheteurFeveV4 {
/* VARIABLES D'INSTANCE */
	private List<ContratFeveV3> offreFinale;
	private List<ContratFeveV3> contratPrecedent;
	public final static double pond = 4.0;
	
	public acheteurFictifTRAN() {
		this.offreFinale = new ArrayList<>();
		this.contratPrecedent = new ArrayList<>();
	} 
	
	
/* GETTEURS */
	/* Romain Bernard */
	public String getNom() {
		return "acheteurFictifTRAN";
	}
	
	/* Guillaume Sallé */
	public List<IVendeurFeveV4> getVendeurs() {
		List<Acteur> acteurs = Monde.LE_MONDE.getActeurs();
		List<IVendeurFeveV4> vendeurs = new ArrayList<>();
		for (Acteur v : acteurs) {
			if (v instanceof IVendeurFeveV4) {
				vendeurs.add((IVendeurFeveV4)v);
			}
		}
		return vendeurs;
	}
	
	public List<ContratFeveV3> getContratPrecedent() {
		return this.contratPrecedent;
	}
	public void setContratPrecedent(List<ContratFeveV3> l) {
		this.contratPrecedent = new ArrayList<ContratFeveV3>();
		for (ContratFeveV3 c : l) {
			if ((c.getTransformateur() != this)&&(c.getReponse())) {
				this.contratPrecedent.add(c);
			}
		}
	}
	
	public List<ContratFeveV3> getOffreFinale() {
		return this.offreFinale;
	}
	public void setOffreFinale(List<ContratFeveV3> l) {
		this.offreFinale = new ArrayList<ContratFeveV3>();
		for (ContratFeveV3 c : l) {
			this.offreFinale.add(c);
		}
	}
	
/* NEXT DE L'ACTEUR FICTIF */	
	public void next() {
		/* L'acheteur fictif n'a pas de next() car il n'a pas de stock, ni de solde */
	}

// IMPLEMENTATION DE IACHETEURFEVEV4
	/* Agathe Chevalier */
	public void sendOffrePubliqueV3(List<ContratFeveV3> offrePublique) {
		/*L'acheteur fictif n'a pas besoin de recuperer les offres publiques des producteurs aux transformateurs
		 * car aucun ne lui adresse de demande:
		 * ses commandes sont directement calculés selon un pourcentage des demandes du moins precedent */
	}

	/* Agathe Chevalier, Guillaume Sallé */
	public List<ContratFeveV3> getDemandePriveeV3() {
		//this.contratPrecedent = new ArrayList<ContratFeveV3>();
		//this.contratPrecedent = ((MarcheFeve)(Monde.LE_MONDE.getActeur("Marche"))).getContratPrecedent();
		List<ContratFeveV3> c = new ArrayList<ContratFeveV3>();
		//* Pour l'acheteur fictif :		
		int tonnageQB = 0; double prixQB =0; // Prix à la tonne !
		int tonnageQM_1 = 0; double prixQM_1 = 0;
		int tonnageQM_2 = 0; double prixQM_2 = 0;
		int tonnageQH = 0; double prixQH = 0;
		int nbQB = 0 ; int nbQM_1 = 0 ; int nbQM_2 = 0; int nbQH = 0;
		
		for(ContratFeveV3 contrat : getContratPrecedent()) {
			if(contrat.getQualite()==0) {
				tonnageQB += (int)(contrat.getDemande_Quantite()*pond);
				prixQB += contrat.getDemande_Prix();
				nbQB += 1;
			}
			if(contrat.getQualite()==2) {
				tonnageQH += (int)(contrat.getDemande_Quantite()*pond);
				prixQH += contrat.getDemande_Prix();
				nbQH += 1;
			}
			if(contrat.getQualite()==1) {
				if(contrat.getProducteur()==getVendeurs().get(0)
						) {
					tonnageQM_1 += (int)(contrat.getDemande_Quantite()*pond);
					prixQM_1 += contrat.getDemande_Prix();
					nbQM_1 += 1;
				} else {
					tonnageQM_2 += (int)(contrat.getDemande_Quantite()*pond);
					prixQM_2 += contrat.getDemande_Prix();
					nbQM_2 += 1;
				}
			}

		}
		
		if (tonnageQB != 0) {
			c.add(new ContratFeveV3(this, getVendeurs().get(0)
					, 0, 0, tonnageQB, 0, 
					0, prixQB/nbQB, 0, false));
		}
		if (tonnageQH != 0) {
			c.add(new ContratFeveV3(this, getVendeurs().get(1)
					, 2, 0, tonnageQH, 0, 
					0, prixQH/nbQH, 0, false));
		}
		if (tonnageQM_1 != 0) {
			c.add(new ContratFeveV3(this, getVendeurs().get(0)
					, 1, 0, tonnageQM_1, 0, 
					0, prixQM_1/nbQM_1, 0, false));
		}
		if (tonnageQM_2 != 0) {
			c.add(new ContratFeveV3(this, getVendeurs().get(1)
					, 1, 0, tonnageQM_2, 0, 
					0, prixQM_2/nbQM_2, 0, false));
		}
		//*/
		
		/* -> //*    :   Test pour notre Eq2PROD
		c.add(new ContratFeveV3(this, getVendeurs().get(0), 0,
				0, 1, 0, 
				0, 70800000.0/0.65, 0, false));
		c.add(new ContratFeveV3(this, getVendeurs().get(1), 0,
				0, 1, 0, 
				0, 70800000.0/0.65, 0, false)); 
		// */
		return c;
	}

	/* Guillaume Sallé + Agathe Chevalier */
	public void sendContratFictifV3(List<ContratFeveV3> listContrats) {
		setContratPrecedent(listContrats);
	}

	/* Agathe Chevalier */
	public void sendOffreFinaleV3(List<ContratFeveV3> offreFinale) {
		setOffreFinale(offreFinale);
	}

	/* Agathe Chevalier */
	public List<ContratFeveV3> getResultVentesV3() {
		for(ContratFeveV3 c : getOffreFinale()) {
			c.setReponse(true); /* l'acheteur fictif signe tous les contrats */
		}
		return offreFinale;
	}
}
