package no.brreg.regnskap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SwaggerRedirectController {

    @RequestMapping(value="/regnskapsregisteret/regnskap/swagger-ui.html")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/regnskap-api-impl/getLog";
    }

}