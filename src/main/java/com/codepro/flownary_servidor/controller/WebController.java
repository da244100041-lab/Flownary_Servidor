package com.codepro.flownary_servidor.controller;

import com.codepro.flownary_servidor.entity.Usuario;
import com.codepro.flownary_servidor.service.UsuarioWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web para manejar las vistas de la aplicación.
 * Proporciona los endpoints para servir las páginas HTML y procesar formularios.
 */
@Controller
public class WebController {

    @Autowired
    private UsuarioWebService usuarioWebService;

    /**
     * Sirve la página principal de la aplicación.
     * 
     * @param model Modelo para pasar datos a la vista
     * @return Nombre de la plantilla HTML
     */
    @GetMapping("/")
    public String index(Model model) {
        System.out.println("=== WebController GET /: Sirviendo página principal ===");
        // Agregar un objeto Usuario vacío para el formulario
        model.addAttribute("user", new Usuario());
        System.out.println("Usuario vacío agregado al modelo para GET /");
        return "index";
    }

    /**
     * Sirve la página principal para la ruta /index (GET).
     * Evita el error 405 Method Not Supported.
     * 
     * @param model Modelo para pasar datos a la vista
     * @return Nombre de la plantilla HTML
     */
    @GetMapping("/index")
    public String indexPage(Model model) {
        System.out.println("=== WebController GET /index: Sirviendo página principal ===");
        // Agregar un objeto Usuario vacío para el formulario
        model.addAttribute("user", new Usuario());
        System.out.println("Usuario vacío agregado al modelo para GET /index");
        return "index";
    }

    /**
     * Procesa el formulario de registro de usuarios.
     * 
     * @param usuario Datos del usuario enviados desde el formulario
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para redirección con mensajes flash
     * @return Nombre de la plantilla HTML con mensajes
     */
    @PostMapping("/index")
    public String registrarUsuarioWeb(@ModelAttribute Usuario usuario, 
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        System.out.println("=== WebController: Iniciando registro ===");
        System.out.println("Usuario recibido: " + usuario.getEmail());
        System.out.println("Teléfono recibido: " + usuario.getTelefono());
        
        try {
            // Delegar toda la lógica de negocio al servicio web
            System.out.println("Llamando a UsuarioWebService...");
            Usuario usuarioGuardado = usuarioWebService.registrarUsuarioWeb(usuario);
            System.out.println("Usuario guardado exitosamente: " + usuarioGuardado.getEmail());
            
            // Agregar mensaje de éxito directamente al modelo
            String successMessage = "¡Usuario registrado exitosamente! Bienvenido a Flownary.";
            model.addAttribute("message", successMessage);
            model.addAttribute("user", new Usuario()); // Nuevo usuario vacío para el formulario
            
            System.out.println("Mensaje de éxito agregado al modelo: " + successMessage);
            System.out.println("Retornando a index.html con mensaje de éxito");
            
            // Devolver la misma página con el mensaje de éxito
            return "index";
            
        } catch (IllegalArgumentException e) {
            // Manejar errores de validación del negocio
            System.out.println("Error de validación capturado: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", usuario); // Devolver el usuario con datos para corregir
            
            System.out.println("Mensaje de error agregado al modelo: " + e.getMessage());
            System.out.println("Retornando a index.html con mensaje de error");
            
            // Devolver la misma página con el error
            return "index";
            
        } catch (Exception e) {
            // Manejar otros errores
            System.out.println("Error general capturado: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            model.addAttribute("user", usuario); // Devolver el usuario con datos para corregir
            
            System.out.println("Mensaje de error general agregado al modelo: " + e.getMessage());
            System.out.println("Retornando a index.html con mensaje de error general");
            
            // Devolver la misma página con el error
            return "index";
        }
    }


}
