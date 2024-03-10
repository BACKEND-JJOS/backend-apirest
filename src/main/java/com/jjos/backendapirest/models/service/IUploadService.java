package com.jjos.backendapirest.models.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface IUploadService {
    Resource cargarFoto(String nombreFoto) throws MalformedURLException;

    String copiar(MultipartFile archivo) throws IOException;

    Path obtenerPath(String nombreFoto);
}
