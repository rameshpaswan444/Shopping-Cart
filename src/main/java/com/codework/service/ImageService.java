package com.codework.service;

import com.codework.dto.ImageDto;
import com.codework.model.Image;
import com.codework.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    Image getImageById(Long id);
    void  deleteImageById(Long id);
    List<ImageDto> saveImage(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long imageId);
}
