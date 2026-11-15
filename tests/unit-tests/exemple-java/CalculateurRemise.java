public class CalculateurRemise {

    public double calculerPrixFinal(double montantTotal, boolean estMembreVIP) {
        if (montantTotal < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        }

        double tauxRemise = 0.0;

        if (estMembreVIP) {
            tauxRemise = 0.20; // 20% de remise pour les VIP
        } else if (montantTotal > 100.0) {
            tauxRemise = 0.10; // 10% si supérieur à 100 €
        }

        return montantTotal * (1.0 - tauxRemise);
    }
}