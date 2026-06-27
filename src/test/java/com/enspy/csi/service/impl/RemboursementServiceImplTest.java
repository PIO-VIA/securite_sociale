package com.enspy.csi.service.impl;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.entity.Remboursement;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.PrescriptionRepository;
import com.enspy.csi.repository.RemboursementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemboursementServiceImplTest {

    @Mock
    private RemboursementRepository remboursementRepository;

    @Mock
    private FeuillemMaladieRepository feuillemMaladieRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private RemboursementServiceImpl remboursementService;

    private FeuillemMaladie f1;
    private FeuillemMaladie f2;

    @BeforeEach
    void setUp() {
        f1 = new FeuillemMaladie();
        f1.setId(1L);
        f1.setIdFeuille("FM-1");
        f1.setMontantSoin(100.0);
        f1.setEstRembourse(false);

        f2 = new FeuillemMaladie();
        f2.setId(2L);
        f2.setIdFeuille("FM-2");
        f2.setMontantSoin(200.0);
        f2.setEstRembourse(false);
    }

    @Test
    void initierRemboursementPourFeuilles_ShouldSucceed() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(feuillemMaladieRepository.findAllById(ids)).thenReturn(Arrays.asList(f1, f2));
        when(remboursementRepository.save(any(Remboursement.class))).thenAnswer(invocation -> {
            Remboursement r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        RemboursementResponseDTO response = remboursementService.initierRemboursementPourFeuilles(ids);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(300.0, response.getMontant()); // 100 + 200 (100% reimbursement by default)
        assertEquals(Remboursement.STATUT_EN_ATTENTE, response.getStatut());
        assertEquals(Arrays.asList(1L, 2L), response.getFeuilleMaladieIds());

        verify(remboursementRepository, times(1)).save(any(Remboursement.class));
        verify(feuillemMaladieRepository, times(1)).saveAll(anyList());
    }

    @Test
    void confirmerRemboursement_WithMultipleFeuilles_ShouldSucceed() {
        Remboursement r = new Remboursement();
        r.setId(10L);
        r.setStatut(Remboursement.STATUT_EN_ATTENTE);
        r.getFeuillesMaladie().add(f1);
        r.getFeuillesMaladie().add(f2);
        f1.setRemboursement(r);
        f2.setRemboursement(r);

        when(feuillemMaladieRepository.findById(1L)).thenReturn(Optional.of(f1));
        when(remboursementRepository.findByFeuilleMaladieId(1L)).thenReturn(Optional.of(r));
        when(remboursementRepository.save(any(Remboursement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RemboursementResponseDTO response = remboursementService.confirmerRemboursement(1L, "CASH");

        assertNotNull(response);
        assertEquals(Remboursement.STATUT_EFFECTUE, response.getStatut());
        assertEquals("CASH", response.getModePaiement());
        assertTrue(f1.getEstRembourse());
        assertTrue(f2.getEstRembourse());
        assertEquals(300.0, response.getMontant());

        verify(feuillemMaladieRepository, times(2)).save(any(FeuillemMaladie.class));
        verify(remboursementRepository, times(1)).save(r);
    }
}
