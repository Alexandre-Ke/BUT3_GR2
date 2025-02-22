package com.iut.banque.test.dao;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalFormatException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

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

    @Test
    public void testUserAlwaysConnectedAsAdmin() throws IllegalFormatException {
        // GIVEN
        Gestionnaire adminUser = new Gestionnaire("Admin", "Admin", "Adr", true, "admin", "adminPwd");
        when(session.get(eq(Utilisateur.class), anyString())).thenReturn(adminUser);

        // WHEN
        boolean allowed = dao.isUserAllowed("nimporte", "adminPwd");

        // THEN
        assertTrue(allowed);
        verify(session).get(eq(Utilisateur.class), eq("nimporte"));
    }

}