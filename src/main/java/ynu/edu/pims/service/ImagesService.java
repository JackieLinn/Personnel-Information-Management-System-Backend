package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import ynu.edu.pims.repository.ImagesRepository;

@Service
public class ImagesService {

    @Resource
    private ImagesRepository imagesRepository;
}
