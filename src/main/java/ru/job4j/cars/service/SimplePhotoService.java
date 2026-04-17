package ru.job4j.cars.service;

import org.springframework.beans.factory.annotation.Value;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimplePhotoService implements PhotoService {

    private final PhotoRepository photoRepository;

    private final String storageDirectory;

    /* @Value("${file.directory}") String storageDirectory.
     Эта строка позволяет подставить на место storageDirectory
      значение из файла application.properties с ключом file.directory; */
    public SimplePhotoService(PhotoRepository hbnPhotoRepository,
                             @Value("${file.directory}") String storageDirectory) {
        this.photoRepository = hbnPhotoRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Photo save(PhotoDto photoDto) {
        var newFilePath = getNewFilePath(photoDto.getName());
        writeFileBytes(newFilePath, photoDto.getContent());
        return photoRepository.add(new Photo(photoDto.getName(), newFilePath));
    }

    private String getNewFilePath(String sourceName) {
        /* Так создается уникальный путь для ногового файла.
         UUID это просто рандомная строка определенного формата; */
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PhotoDto> findAllFiles() {
        List<PhotoDto> photoDtoList = new ArrayList<>();
        for (Photo onePhoto : photoRepository.findAll()) {
            var content = readFileAsBytes(onePhoto.getPath());
            photoDtoList.add(new PhotoDto(onePhoto.getName(), content));
        }
        return photoDtoList;
    }

    @Override
    public Optional<PhotoDto> getFileById(int id) {
        var fileOptional = photoRepository.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(fileOptional.get().getPath());
        return Optional.of(new PhotoDto(fileOptional.get().getName(), content));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        boolean result = false;
        var fileOptional = photoRepository.findById(id);
        if (fileOptional.isPresent()) {
            result = deleteFile(fileOptional.get().getPath()) && photoRepository.delete(id);
        }
        return result;
    }

    private boolean deleteFile(String path) {
        try {
            return Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

