import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculateurRemiseTest {

    private CalculateurRemise calculateur;

    @BeforeEach
    void setUp() {
        // Exécuté avant chaque test pour initialiser l'objet
        calculateur = new CalculateurRemise();
    }

    @Test
    void etantDonneMontantInferieurOuEgalA100EtNonVIP_quandCalculerPrix_alorsPasDeRemise() {
        // Arrange (Préparation)
        double montant = 50.0;
        boolean estVIP = false;

        // Act (Exécution)
        double resultat = calculateur.calculerPrixFinal(montant, estVIP);

        // Assert (Vérification)
        assertEquals(50.0, resultat, 0.001, "Le montant ne doit pas être modifié");
    }

    @Test
    void etantDonneMontantSuperieurA100EtNonVIP_quandCalculerPrix_alorsRemiseDeDixPourcent() {
        // Arrange
        double montant = 200.0;
        boolean estVIP = false;

        // Act
        double resultat = calculateur.calculerPrixFinal(montant, estVIP);

        // Assert
        assertEquals(180.0, resultat, 0.001, "Une remise de 10% doit être appliquée (200 - 20)");
    }

    @Test
    void etantDonneClientVIP_quandCalculerPrix_alorsRemiseDeVingtPourcent() {
        // Arrange
        double montant = 50.0;
        boolean estVIP = true;

        // Act
        double resultat = calculateur.calculerPrixFinal(montant, estVIP);

        // Assert
        assertEquals(40.0, resultat, 0.001, "Les VIP doivent avoir 20% peu importe le montant (50 - 10)");
    }

    @Test
    void etantDonneMontantNegatif_quandCalculerPrix_alorsLanceException() {
        // Arrange & Act & Assert combinés pour les exceptions
        assertThrows(IllegalArgumentException.class, () -> {
            calculateur.calculerPrixFinal(-10.0, false);
        }, "Un montant négatif doit lever une IllegalArgumentException");
    }
}