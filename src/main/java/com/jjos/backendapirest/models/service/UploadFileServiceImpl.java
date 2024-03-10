package com.jjos.backendapirest.models.service;

import com.jjos.backendapirest.controllers.ClienteController;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UploadFileServiceImpl  implements IUploadService{
    private  final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Override
    public Resource cargarFoto(String nombreFoto) throws MalformedURLException {

        return new UrlResource(obtenerPath(nombreFoto).toUri());
    }

    @Override
    public String copiar(MultipartFile archivo) throws IOException {
        String nombreArchivo =   UUID.randomUUID().toString()+'_'+archivo.getOriginalFilename().replace(" ", "");
        Files.copy(archivo.getInputStream(), obtenerPath(nombreArchivo));
        return nombreArchivo;
    }

    @Override
    public Path obtenerPath(String nombreFoto) {
        return Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
    }
}
