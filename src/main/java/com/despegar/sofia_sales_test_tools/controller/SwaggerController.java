package com.despegar.sofia_sales_test_tools.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * This controller redirects / to /swagger-ui/index.html#/ so http://localhost:9290 shows swagger
 * Also changes swagger-ui colors depending on environment
 */
@RestController
public class SwaggerController {

    private final String environmentName;

    public SwaggerController(@Value("${environment.name}") String environmentName) {
        this.environmentName = environmentName;
    }

    private record ColorConfig(String icon, String logo, String topbar, String background) {
    }

    private static final ColorConfig ORIGINAL = new ColorConfig("original", "#85ea2d", "#2a2e30", "#1c2022");
    private static final ColorConfig PROD = new ColorConfig("prod", "#ff0000", "#992022", "#882022");//rojos
    private static final ColorConfig SANDBOX = new ColorConfig("sandbox", "#ffff00", "#999922", "#888822");//amarillos
    private static final ColorConfig TEST = new ColorConfig("test", "#00ff00", "#2a9922", "#1c8822"); //verdes
    private static final Map<String, ColorConfig> COLOR_MAP = Map.of(
            "prod", PROD,
            "sandbox", SANDBOX,
            "test", TEST
    );

    @Operation(hidden = true)
    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html#/");
    }

    @Operation(hidden = true)
    @GetMapping(value = "swagger-ui/swagger-ui.css", produces = "text/css")
    public ResponseEntity<String> swaggerUiCss() {
        ColorConfig colorConfig = findColorConfig();
        String logoColor = colorConfig.logo();
        String topbarColor = colorConfig.topbar();
        String backgroundColor = colorConfig.background();
        // Importamos el css original de Swagger y aplicamos el override visual.
        String css = """
                @import url("/webjars/swagger-ui/swagger-ui.css");
                
                .logo_small_svg__cls-3 {
                  fill: {logoColor} !important;
                }
                
                html.dark-mode .swagger-ui .topbar {
                  background: {topbarColor} !important;
                }
                
                html.dark-mode .swagger-ui {
                  background: {backgroundColor} !important;
                }
                
                html.dark-mode .swagger-ui .scheme-container {
                  background: {backgroundColor} !important;
                }
                
                html.dark-mode {
                  background: {backgroundColor} !important;
                }
                """.replace("{logoColor}", logoColor)
                .replace("{topbarColor}", topbarColor)
                .replace("{backgroundColor}", backgroundColor);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/css"))
                .body(css);
    }

    private ColorConfig findColorConfig() {
        return COLOR_MAP.getOrDefault(environmentName, ORIGINAL);
    }

    @Operation(hidden = true)
    @GetMapping(value = "swagger-ui/favicon-16x16.png", produces = "image/png")
    public byte[] swaggerUiFavicon() throws IOException {
        ColorConfig colorConfig = findColorConfig();
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/swagger/favicon-16x16-" + colorConfig.icon() + ".png")) {
            return resourceAsStream.readAllBytes();
        }
    }
}
