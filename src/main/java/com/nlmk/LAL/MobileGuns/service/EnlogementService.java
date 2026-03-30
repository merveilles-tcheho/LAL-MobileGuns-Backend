package com.nlmk.LAL.MobileGuns.service;

import com.nlmk.LAL.MobileGuns.dto.EnlogementRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.EnlogementResponseDTO;

public interface EnlogementService {

    EnlogementResponseDTO enloger(EnlogementRequestDTO request);
}