package edu.ucsb.cs56.pconrad;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import spark.*;
import spark.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.port;


/**
 * Hello world!
 *
 */

/*
    This WebApp uses Spark.Java framework and FreeMarker Template engine.
    The API is from RescueGroups.org.
 */

public class CatFinder {
    public static void main(String[] args) {

        port(getHerokuAssignedPort());

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(CatFinder.class, "layouts");
        cfg.setDefaultEncoding("UTF-8");


        get("/", (Request req, Response res) -> {
            StringWriter writer = new StringWriter();
            Map attributes = new HashMap();
            attributes.put("home_banner", "Lets find a cat.");
            try {
                Template homeTemplate = cfg.getTemplate("home.ftl");
                homeTemplate.process(attributes, writer);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("home.ftl not found!");
                Spark.halt(500);
            }
            return writer;
        });


		System.out.println("");
		System.out.println("(Don't worry about the warnings below about SLF4J... we'll deal with those later)");
		System.out.println("");						  
		System.out.println("In browser, visit: http://localhost:" + getHerokuAssignedPort() + "");
		System.out.println("");
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

	}
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

	
}
