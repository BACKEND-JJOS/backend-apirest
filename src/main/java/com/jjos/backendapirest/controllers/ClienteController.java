package com.jjos.backendapirest.controllers;

import com.jjos.backendapirest.controllers.responses.ErrorResponse;
import com.jjos.backendapirest.models.entity.Cliente;
import com.jjos.backendapirest.models.entity.Region;
import com.jjos.backendapirest.models.service.IClienteService;
import com.jjos.backendapirest.models.service.IUploadService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cliente")
@AllArgsConstructor
public class ClienteController {

    private IClienteService iClienteService;

    private IUploadService iUploadService;

    private  final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    @GetMapping("/todos")
    public List<Cliente> obtenerTodosClientes(){
        return iClienteService.buscarTodos();
    }


    @GetMapping(value = "/todos/page")
    public Page<Cliente> obtenerTodosClientes(@RequestParam("numeroPagina") Integer numeroPagina, @RequestParam(value = "tamanioPagina", defaultValue = "10") Integer tamanioPagina){
        return iClienteService.buscarTodos(PageRequest.of(numeroPagina,tamanioPagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerClientePorId(@PathVariable  Long id){
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente cliente = iClienteService.buscarPorId(id);
            if (cliente ==null) {
                return new ResponseEntity<>(new ErrorResponse("El cliente con ID " + id + " no existe en la base de datos") , HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(cliente , HttpStatus.OK);
        }catch (DataAccessException e) {
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error en base de datos") , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarClientePorId(@PathVariable  Long id){
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente cliente = iClienteService.buscarPorId(id);
            if (cliente ==null) {
                return new ResponseEntity<>(new ErrorResponse("El cliente con ID " + id + " no existe en la base de datos" ),HttpStatus.NOT_FOUND);
            }
            iClienteService.eliminar(id);
            response.put("mensaje", "Cliente eliminado con exito");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (DataAccessException e){
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error en base de datos al eliminar" ),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping
    public ResponseEntity<?> guardarClienteNuevo(@RequestBody @Valid Cliente cliente, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                            .stream()
                            .map(err -> "Error  el campo '"+ err.getField() +"' " +err.getDefaultMessage())
                            .collect(Collectors.toList());
            response.put("errors" , errors);
            return new ResponseEntity<> (response, HttpStatus.BAD_REQUEST);
        }

        try {
            Cliente nuevoCliente = iClienteService.guardar(cliente);
            response.put("mensaje" , "Cliente creado con exito.");
            response.put("cliente" , nuevoCliente);
            return new ResponseEntity<> (response, HttpStatus.CREATED);
        }catch (DataAccessException e){
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error en base de datos" ),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{idClienteAModificar}")
    public  ResponseEntity<?> actualizarCliente(@RequestBody @Valid Cliente clienteModificado,BindingResult result , @PathVariable Long idClienteAModificar){
        Map<String, Object> response = new HashMap<>();
        try {
            Cliente clienteActual = iClienteService.buscarPorId(idClienteAModificar);
            if (clienteActual ==null) {
                return new ResponseEntity<>(new ErrorResponse(
                        "El cliente ID" .concat(clienteModificado.toString().concat(" no existe en la base de datos"))) ,
                        HttpStatus.NOT_FOUND);
            }
            Cliente clienteActualizado = null;

            clienteActual.setApellido(clienteModificado.getApellido());
            clienteActual.setNombre(clienteModificado.getNombre());
            clienteActual.setEmail(clienteModificado.getEmail());
            clienteActual.setCreateAt(clienteModificado.getCreateAt());
            clienteActual.setRegion(clienteModificado.getRegion());
            clienteActualizado = iClienteService.guardar(clienteActual);
            response.put("mensaje" , "Cliente actualizado con exito.");
            response.put("cliente" , clienteActualizado);
            return new ResponseEntity<> (response, HttpStatus.CREATED);

        }catch (DataAccessException e){
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error en base de datos") , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/upload")
    public ResponseEntity<?> cargarArchivoPorUsuario(@RequestParam("archivo")MultipartFile archivo, @RequestParam("idCliente") Long idCliente) {
        Map<String, Object> response = new HashMap<>();
        Cliente cliente = iClienteService.buscarPorId(idCliente);
        if (!archivo.isEmpty()) {
            String nombreArchivo = "";
            try {
                nombreArchivo = iUploadService.copiar(archivo);
            } catch (IOException e) {
                logger.info(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error al cargar la imagen") , HttpStatus.INTERNAL_SERVER_ERROR);
            }

            cliente.setFoto(nombreArchivo);
            iClienteService.guardar(cliente);
            response.put("mensaje" , "Foto agregada para el cliente.");
            response.put("cliente" , cliente);
        }
        return  new ResponseEntity<>( response, HttpStatus.CREATED);
    }

    @GetMapping("/foto/{idCliente}")
    public ResponseEntity<?> mostrarFotoCliente(@PathVariable("idCliente") Long idCliente) {
        Map<String, Object> response = new HashMap<>();

        Cliente cliente = iClienteService.buscarPorId(idCliente);
        if(cliente.getFoto() == null){

            logger.info("Usuario " + cliente.toString() + "no tiene imagnees cargadas");

            return new ResponseEntity<>(new ErrorResponse("No hay una imagen cargada para el usuario") , HttpStatus.NOT_FOUND);
        }

        Resource recurso = null;
        try {
            recurso = iUploadService.cargarFoto(cliente.getFoto());
            response.put("recurso", recurso);

        } catch (MalformedURLException e) {
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error al obtener  la imagen") , HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(!recurso.exists() && !recurso.isReadable()){
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error al leer  la imagen") , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename()+ "\"");
        return new ResponseEntity<>(recurso, cabecera, HttpStatus.OK);
    }

    @GetMapping("/regiones")
    public ResponseEntity<?> obtenerRegiones() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Region> regiones = iClienteService.buscarRegiones();
            if (regiones.isEmpty()) {
                return new ResponseEntity<>(new ErrorResponse("No hay regiones creadas") , HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(regiones , HttpStatus.OK);
        }catch (DataAccessException e) {
            return new ResponseEntity<>(new ErrorResponse("Ha ocurrido un error en base de datos") , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
