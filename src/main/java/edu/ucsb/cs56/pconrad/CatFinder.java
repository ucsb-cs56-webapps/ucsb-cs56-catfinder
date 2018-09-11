package edu.ucsb.cs56.pconrad;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.json.JSONArray;
import spark.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import org.json.JSONObject;
import static spark.Spark.get;
import static spark.Spark.port;


/**
 * Hello world!
 */

/*
    This WebApp uses Spark.Java framework and FreeMarker Template engine.
    The API is from RescueGroups.org.
    TODO: Tinker with amount of cats retrieved. Create a nav bar. Make a way for users to input zipcode.
    TODO: Remove absurdly big base64 string from home file
    TODO: Update ReadME for API instructions with Https URLs
 */

public class CatFinder {
    public static void main(String[] args) {

        // Get port from Heroku
        port(getHerokuAssignedPort());

        // This will store the cats retrieved from the API
        ArrayList<PetModel> cats = new ArrayList<>();

        // Set up Configuration for Freemark. This should only be done once.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(CatFinder.class, "/layouts/");
        cfg.setDefaultEncoding("UTF-8");

        // Home page
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

        // Page for list of cats
        get("/results", (req, res) -> {
            cats.clear();

            StringWriter writer = new StringWriter();
            try {
                //String key = System.getenv("GOOGLE_MAPS_API_KEY");
                Map<String, Object> attributes = new HashMap<>();
                //attributes.put("google_key", key);
                //System.out.println("gkey: "+key);
                //attributes.put("map_zipcode", 93117);
                buildCatsList(93117, cats);

                attributes.put("cats", cats);
                Template resultsTemplate = cfg.getTemplate("results.ftl");
                resultsTemplate.process(attributes, writer);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("results.ftl not found!");
                Spark.halt(500);
            }
            return writer;

        });

        // a sample to demo API
        ArrayList<PetModel> pets = new ArrayList<>();
        get("/sample", (req, res) -> {

            // Store your key in a .env file and be sure to ignore that from git.
            String key = System.getenv("PETFINDER_KEY");
            String urlString = "http://api.petfinder.com/pet.find?key=" + key + "&animal=cat&location=93117&format=json";

            System.out.println(urlString);

            // Create a URL with the url formed.
            URL url = new URL(urlString);
            // Open a connection.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // because we are retrieving...
            connection.setRequestMethod("GET");
            connection.connect();

            // There will be an issue with the response code is not 200..
            int responsecode = connection.getResponseCode();
            if (responsecode != 200) { System.out.println("oh no");}

            // Use a scanner to read from the API.
            Scanner sc = new Scanner(url.openStream());
            String inline = "";
            while (sc.hasNext()) {
                inline += sc.nextLine();
            }
            sc.close();

            // Using the string formed from the scanner, create a JSON object
            JSONObject json = new JSONObject(inline);

            // We want to store every pet into a PetModel. Note the layout of the JSON file retrieved from the API.
            json = (JSONObject) json.get("petfinder");
            json = (JSONObject) json.get("pets");

            // Now get the array of pets from the JSON. We can iterate through it to store them into the model.
            JSONArray json_pets = (JSONArray) json.get("pet");
            for (int i = 0; i < json_pets.length(); i++) {
                PetModel pet = new PetModel();
                JSONObject json_pet = (JSONObject) json_pets.get(i);
                JSONObject json_pet_name = (JSONObject) json_pet.get("name");
                JSONObject json_pet_gender = (JSONObject) json_pet.get("sex");
                JSONObject json_pet_description = (JSONObject) json_pet.get("description");
                JSONObject json_pet_img = (JSONObject) json_pet.get("media");

                // Watch out for situations where some fields may be empty. We can check the length of it before trying
                // to grab from it.
                if (json_pet_img.length() != 0) {
                    json_pet_img = (JSONObject) json_pet_img.get("photos");
                    JSONArray json_pet_img_arr = (JSONArray) json_pet_img.get("photo");

                    if (json_pet_img_arr.length() != 0) {
                        for (int k = 0; k < json_pet_img_arr.length(); k++) {
                            json_pet_img = (JSONObject) json_pet_img_arr.get(0);
                        }
                    }

                    pet.img = json_pet_img.get("$t").toString();
                }

                if (json_pet_name.length() != 0) {
                    pet.name = json_pet_name.get("$t").toString();
                }
                if (json_pet_description.length() != 0) {
                    pet.description = json_pet_description.get("$t").toString();
                }
                if (json_pet_gender.length() != 0) {
                    pet.gender = json_pet_gender.get("$t").toString();
                }

                pets.add(pet);
            }

            for ( PetModel cat:pets) {
                System.out.println("name: " + cat.name + " gender: " + cat.gender);
            }

            return "";
        });


        System.out.println("");
        System.out.println("(Don't worry about the warnings below about SLF4J... we'll deal with those later)");
        System.out.println("");
        System.out.println("In browser, visit: http://localhost:" + getHerokuAssignedPort() + "");
        System.out.println("");

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    // use PetFinder API to build 'cats' list. Hide your key! Try to do something if URL does not work.
    static void buildCatsList(int zipCode, ArrayList<PetModel> pets) {

        String zip = "" + zipCode;
        String key = System.getenv("PETFINDER_KEY");
        String urlString = "http://api.petfinder.com/pet.find?key=" + key + "&animal=cat&location=" + zip
                + "&format=json";

        System.out.println("URL to 'get' from: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            System.out.println("URL malformed");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("oh no");
        }
        // because we are retrieving...
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            System.out.println("cannot set request method");
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ahh");
        }
        try {
            int responseCode = connection.getResponseCode();
        } catch (IOException e) {
            System.out.println("response code failed");
        }


        Scanner sc = null;
        try {
            sc = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("crap");
        }
        String inline = "";
        while (sc.hasNext()) {
            inline += sc.nextLine();
        }
        sc.close();

        JSONObject json = new JSONObject(inline);
        json = (JSONObject) json.get("petfinder");
        json = (JSONObject) json.get("pets");

        JSONArray json_pets = (JSONArray) json.get("pet");
        for (int i = 0; i < json_pets.length(); i++) {
            PetModel pet = new PetModel();
            JSONObject json_pet = (JSONObject) json_pets.get(i);
            JSONObject json_pet_name = (JSONObject) json_pet.get("name");
            JSONObject json_pet_gender = (JSONObject) json_pet.get("sex");
            JSONObject json_pet_description = (JSONObject) json_pet.get("description");
            JSONObject json_pet_img = (JSONObject) json_pet.get("media");

            if (json_pet_img.length() != 0) {
                json_pet_img = (JSONObject) json_pet_img.get("photos");
                JSONArray json_pet_img_arr = (JSONArray) json_pet_img.get("photo");

                if (json_pet_img_arr.length() != 0) {
                    for (int k = 0; k < json_pet_img_arr.length(); k++) {
                        json_pet_img = (JSONObject) json_pet_img_arr.get(k);
                        //search for one with better quality...
                        //JSONObject json_img_size = (JSONObject) json_pet_img.get("@size");
                        if (Objects.equals(json_pet_img.get("@size").toString(), "x")) {
                            break;
                        }
                    }
                }

                pet.img = json_pet_img.get("$t").toString();
            }

            //System.out.println(json_pet_name.get("$t"));
            if (json_pet_name.length() != 0) {
                pet.name = json_pet_name.get("$t").toString();
            }
            if (json_pet_description.length() != 0) {
                pet.description = json_pet_description.get("$t").toString();
            }
            if (json_pet_gender.length() != 0) {
                pet.gender = json_pet_gender.get("$t").toString();
            }

            //System.out.println("name: " + pet.name);
            //System.out.println("gender: " + pet.gender);
            //System.out.println("description: " + pet.description);

            pets.add(pet);
        }
    }

}
