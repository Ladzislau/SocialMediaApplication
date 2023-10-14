package by.ladz.gusakov.SocialMedialApp.util;

import by.ladz.gusakov.SocialMedialApp.model.Image;
import by.ladz.gusakov.SocialMedialApp.model.Publication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class ImageUtil {

    @Value("${image_upload_directory}")
    private String uploadDir;

    public List<Image> saveImages(List<MultipartFile> images, Publication publication) {
        if(images == null) return null;

        List<Image> imageList = new ArrayList<>();
        try {
            for (MultipartFile img : images) {
                String fileName = UUID.randomUUID() + "-" + img.getOriginalFilename();
                String imagePath = uploadDir + fileName;

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                Path file = Path.of(imagePath);
                img.transferTo(file);

                Image image = new Image(imagePath, publication);

                imageList.add(image);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка! Изображение не сохранено");
        }

        return imageList;
    }

    public void deleteImagesFromServer(List<Image> images){
        for (Image image :
                images) {
            Path path = Paths.get(image.getPathToImage());
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Файл не был удален с сервера!");
            }
        }
    }

    public List<String> loadImages(List<Image> imageList) throws IOException {
        if(!imageList.isEmpty()) {
            List<String> imagesToSend = new ArrayList<>();
            for (Image image : imageList) {
                Path path = Path.of(image.getPathToImage());
                byte[] bytes = Files.readAllBytes(path);
                String base64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                imagesToSend.add(base64);
            }
            return imagesToSend;
        }
        return null;
    }

}
