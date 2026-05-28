package com.enspy.csi.service.impl;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.repository.RemboursementRepository;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.service.RemboursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemboursementServiceImpl implements RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final FeuillemMaladieRepository feuillemMaladieRepository;

    @Override
    public RemboursementResponseDTO effectuerRemboursement(Long feuilleMaladieId, String modePaiement) {
        throw new UnsupportedOperationException("TODO: implement effectuerRemboursement");
    }

    @Override
    public RemboursementResponseDTO getRemboursementById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getRemboursementById");
    }
}
