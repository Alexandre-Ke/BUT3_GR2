package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

@RunWith(MockitoJUnitRunner.class)
public class BanqueManagerMockitoTest {

    @Mock
    private DaoHibernate dao; // On mock la DAO

    @InjectMocks
    private BanqueManager bm; // On teste la logique de BanqueManager

    private Client dummyClient;

    @Before
    public void setUp() throws Exception {
        // Utilisation d'un userId conforme ("e.dupont123") et d'un numéro de client valide ("1234567890")
        dummyClient = new Client("Test", "Nom", "Adresse", true, "e.dupont123", "pwd", "1234567890");
    }

    @Test
    public void testSuppressionDunCompteAvecDecouvertSoldeZero() throws Exception {
        // GIVEN : Création d'un compte avec découvert avec solde = 0
        // Utilisation d'un numéro de compte conforme : 2 lettres majuscules suivies de 10 chiffres (exemple : "FR1000000001")
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR1000000001", 0, 100, dummyClient);
        when(dao.getAccountById("FR1000000001")).thenReturn(compte);

        // WHEN
        bm.deleteAccount(bm.getAccountById("FR1000000001"));

        // THEN : On vérifie que la DAO a bien supprimé le compte
        verify(dao).deleteAccount(compte);
    }

    @Test
    public void testSuppressionDunCompteAvecDecouvertSoldeDifferentDeZero() throws Exception {
        // GIVEN : Création d'un compte avec découvert avec solde != 0 (ici 100)
        // Utilisation d'un numéro de compte conforme (exemple : "FR1000000002")
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR1000000002", 100, 100, dummyClient);
        when(dao.getAccountById("FR1000000002")).thenReturn(compte);

        // WHEN + THEN : La suppression doit lever une IllegalOperationException
        try {
            bm.deleteAccount(bm.getAccountById("FR1000000002"));
            fail("Aurait dû lever IllegalOperationException");
        } catch (IllegalOperationException e) {
            // OK
        }
        verify(dao, never()).deleteAccount(any(Compte.class));
    }

    @Test
    public void testSuppressionDunCompteSansDecouvertSoldeZero() throws Exception {
        // GIVEN : Création d'un compte sans découvert avec solde = 0
        // Utilisation d'un numéro de compte conforme (exemple : "FR2000000001")
        CompteSansDecouvert compte = new CompteSansDecouvert("FR2000000001", 0, dummyClient);
        when(dao.getAccountById("FR2000000001")).thenReturn(compte);

        // WHEN
        bm.deleteAccount(bm.getAccountById("FR2000000001"));

        // THEN : Vérifier la suppression
        verify(dao).deleteAccount(compte);
    }

    @Test
    public void testSuppressionDunCompteSansDecouvertSoldeDifferentDeZero() throws Exception {
        // GIVEN : Création d'un compte sans découvert avec solde non nul (ici 50)
        // Utilisation d'un numéro de compte conforme (exemple : "FR2000000002")
        CompteSansDecouvert compte = new CompteSansDecouvert("FR2000000002", 50, dummyClient);
        when(dao.getAccountById("FR2000000002")).thenReturn(compte);

        // WHEN + THEN : La suppression doit lever une IllegalOperationException
        try {
            bm.deleteAccount(bm.getAccountById("FR2000000002"));
            fail("Aurait dû lever IllegalOperationException");
        } catch (IllegalOperationException e) {
            // OK
        }
        verify(dao, never()).deleteAccount(any(Compte.class));
    }
}
