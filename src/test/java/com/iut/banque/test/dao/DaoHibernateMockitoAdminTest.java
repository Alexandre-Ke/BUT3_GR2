package com.iut.banque.test.dao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.modele.Gestionnaire;

@RunWith(MockitoJUnitRunner.class) // Ancien runner
public class DaoHibernateMockitoAdminTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private DaoHibernate dao;

    @Before
    public void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    /**
     * Test montrant qu'on simule un utilisateur toujours connecté en mode admin.
     * Le userId "a.admin1" respecte la forme (1 lettre, 1 point, "admin", 1 chiffre).
     */
    @Test
    public void testUserAlwaysConnectedAsAdmin() throws IllegalFormatException {
        // GIVEN
        Gestionnaire adminUser = new Gestionnaire("Admin", "Admin", "Adr", true, "a.admin1", "adminPwd");
        when(session.get(eq(Utilisateur.class), anyString())).thenReturn(adminUser);

        // WHEN
        boolean allowed = dao.isUserAllowed("nimporte", "adminPwd");

        // THEN
        assertTrue("L'utilisateur doit être autorisé", allowed);
        verify(session).get(eq(Utilisateur.class), eq("nimporte"));
    }

    @Test
    public void testGetAccountByIdExist()
            throws IllegalFormatException, IllegalOperationException, TechnicalException {
        // userId = "a.exist1" : 1 lettre, 1 point, "exist", 1 chiffre
        CompteAvecDecouvert mockCompte = new CompteAvecDecouvert(
                "IO1010010001", 0, 100,
                new Client("Test", "Nom", "Adresse", true, "a.exist1", "pwd", "1234567890")
        );
        when(session.get(eq(Compte.class), eq("IO1010010001"))).thenReturn(mockCompte);

        Compte result = dao.getAccountById("IO1010010001");

        assertNotNull("Le compte ne doit pas être null", result);
        assertEquals("IO1010010001", result.getNumeroCompte());
        verify(session).get(eq(Compte.class), eq("IO1010010001"));
    }

    @Test
    public void testGetAccountByIdDoesntExist() {
        // Simuler qu'aucun compte n'est trouvé pour "IO1111111111"
        when(session.get(eq(Compte.class), eq("IO1111111111"))).thenReturn(null);

        Compte result = dao.getAccountById("IO1111111111");

        assertNull("Le compte aurait dû être null", result);
        verify(session).get(eq(Compte.class), eq("IO1111111111"));
    }

    @Test
    public void testCreateCompteAvecDecouvert()
            throws TechnicalException, IllegalFormatException, IllegalOperationException {
        // Simuler qu'aucun compte n'existe pour "NW1010010001"
        when(session.get(eq(CompteAvecDecouvert.class), eq("NW1010010001"))).thenReturn(null);

        // userId valide : "a.exist1"
        Client mockClient = new Client("Test", "Nom", "Adr", true, "a.exist1", "pwd", "1234567890");

        CompteAvecDecouvert compte = dao.createCompteAvecDecouvert(0, "NW1010010001", 100, mockClient);

        assertNotNull(compte);
        assertEquals("NW1010010001", compte.getNumeroCompte());
        assertEquals(mockClient, compte.getOwner());
        assertEquals(100, compte.getDecouvertAutorise(), 0.001);
        verify(session).save(compte);
    }

    @Test
    public void testCreateCompteSansDecouvert() throws TechnicalException, IllegalFormatException {
        when(session.get(eq(CompteSansDecouvert.class), eq("SA1011011011"))).thenReturn(null);

        Client mockClient = new Client("Test", "Nom", "Adr", true, "a.test1", "pwd", "1234567890");

        CompteSansDecouvert compte = dao.createCompteSansDecouvert(0, "SA1011011011", mockClient);

        assertNotNull(compte);
        assertEquals("SA1011011011", compte.getNumeroCompte());
        verify(session, times(2)).save(compte);
    }


    @Test
    public void testDeleteAccountExist() throws IllegalFormatException, TechnicalException {
        CompteSansDecouvert existing = new CompteSansDecouvert(
                "SA1011011011", 0,
                new Client("Test", "Nom", "Adresse", true, "a.exist1", "pwd", "1234567890")
        );
        when(session.get(eq(Compte.class), eq("SA1011011011"))).thenReturn(existing);

        dao.deleteAccount(existing);

        verify(session).delete(existing);
    }

    @Test
    public void testIsUserAllowed_Valid() {
        // Ici on mock directement l'Utilisateur car la DAO renvoie un userId = "a.exist1"
        Utilisateur mockUser = mock(Utilisateur.class);
        when(mockUser.getUserId()).thenReturn("a.exist1");
        when(mockUser.getUserPwd()).thenReturn("PASS");
        when(session.get(eq(Utilisateur.class), eq("a.exist1"))).thenReturn(mockUser);

        boolean allowed = dao.isUserAllowed("a.exist1", "PASS");

        assertTrue("L'utilisateur devrait être autorisé", allowed);
    }

    @Test
    public void testIsUserAllowed_WrongPassword() throws IllegalFormatException {
        // userId valide : "a.exist1"
        Client mockUser = new Client("Test", "Nom", "Adresse", true, "a.exist1", "TESTPASS", "1234567890");
        when(session.get(eq(Utilisateur.class), eq("a.exist1"))).thenReturn(mockUser);

        boolean allowed = dao.isUserAllowed("a.exist1", "WRONG PASS");

        assertFalse("Mauvais mot de passe => refusé", allowed);
    }

    @Test
    public void testIsUserAllowed_UserDoesntExist() {
        when(session.get(eq(Utilisateur.class), eq("a.nouser1"))).thenReturn(null);

        boolean allowed = dao.isUserAllowed("a.nouser1", "TEST PASS");

        assertFalse("User inexistant => refusé", allowed);
    }

    @Test
    public void testIsUserAllowed_NullPassword() {
        // WHEN
        boolean allowed = dao.isUserAllowed("a.exist1", null);

        // THEN
        assertFalse("Password null => refusé", allowed);
    }
}
