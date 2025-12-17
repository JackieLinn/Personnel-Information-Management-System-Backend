package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import ynu.edu.pims.repository.RegistrationRepository;

@Service
public class RegistrationService {

    @Resource
    private RegistrationRepository registrationRepository;
}

