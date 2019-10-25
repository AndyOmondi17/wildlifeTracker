import dao.Sql2oAnimalDao;
import dao.Sql2oEndangeredAnimalDao;
import dao.Sql2oSightingDao;
import models.Animal;
import models.EndangeredAnimal;
import models.Sighting;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;


public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");
        String connectionString = "jdbc:postgresql://localhost:5432/wildlife";
        Sql2o sql2o = new Sql2o(connectionString, "postgres", "myPassword");
        Sql2oEndangeredAnimalDao endangeredAnimalDao = new Sql2oEndangeredAnimalDao(sql2o);
        Sql2oAnimalDao animalDao = new Sql2oAnimalDao(sql2o);
        Sql2oSightingDao sightingDao = new Sql2oSightingDao(sql2o);
        Map<String,Object> model = new HashMap<>();

        //get: show all endangered animals, animals and sightings
        get("/",(request, response) ->{
            List<Sighting> allSightings = sightingDao.getAll();
            model.put("sightings",allSightings);
            List<Animal> animals = animalDao.getAll();
            model.put("animals",animals);
            List<EndangeredAnimal> allEndangeredAnimals = endangeredAnimalDao.getAll();
            model.put("endangeredAnimals",allEndangeredAnimals);
            return new ModelAndView(model, "index.hbs");
            }, new HandlebarsTemplateEngine());

        //get: delete an individual sighting
        get("/sightings/:sighting_id/delete", (req, res) -> {
            int idOfSightingToDelete = Integer.parseInt(req.params("sighting_id"));
            sightingDao.deleteById(idOfSightingToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //  get: add new sightings
        get("/sightings/new",(req,res)->{
            List<Sighting> sightings = sightingDao.getAll();
            model.put("sightings", sightings);
            return new ModelAndView ( model, "sighting-form.hbs");
        },new HandlebarsTemplateEngine());

        //    post: process a form to create a new sighting
        post("/sightings", (req, res) -> { //new
            String animal_location = req.queryParams("animal_location");
            String ranger_name = req.queryParams("ranger_name");
            Sighting newSighting = new Sighting(animal_location,ranger_name);
            sightingDao.add(newSighting);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all sightings and all animals and all endangered animals
        get("/sightings/delete", (req, res) -> {
            sightingDao.clearAllSightings();
            animalDao.clearAllAnimals();
            endangeredAnimalDao.clearAllEndangeredAnimals();
            res.redirect("/");
            return null;
        },new HandlebarsTemplateEngine());

        //get: delete all endangered animals
        get("/endangeredAnimals/delete", (req, res) -> {
            endangeredAnimalDao.clearAllEndangeredAnimals();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete all animals
        get("/animals/delete", (req, res) -> {
            animalDao.clearAllAnimals();
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get a specific sighting (and the animals and endangered animals it contains)
        get("/sightings/:id", (req, res) -> {
            int idOfSightingToFind = Integer.parseInt(req.params("id")); //new
            Sighting foundSighting = sightingDao.findById(idOfSightingToFind);
            model.put("sighting", foundSighting);
            List<Animal> allAnimalsBySighting = sightingDao.getAllAnimalsBySighting(idOfSightingToFind);
            model.put("animals", allAnimalsBySighting);
            model.put("sightings", sightingDao.getAll()); //refresh list of links for navbar
            List<EndangeredAnimal> allEndangeredAnimalsBySighting = sightingDao.getAllEndangeredAnimalsBySighting(idOfSightingToFind);
            model.put("endangeredAnimals", allEndangeredAnimalsBySighting);
            model.put("sightings", sightingDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "sighting-detail.hbs"); //new
        },new HandlebarsTemplateEngine());

        //get: show a form to update a sighting
        get("/sightings/:id/edit", (req, res) -> {
            model.put("editSighting", true);
            Sighting sighting = sightingDao.findById(Integer.parseInt(req.params("id")));
            model.put("sighting", sighting);
            model.put("sightings", sightingDao.getAll()); //refresh list of links for navbar
            return new ModelAndView(model, "sighting-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a sighting
        post("/sightings/:id", (req, res) -> {
            int idOfSightingToEdit = Integer.parseInt(req.params("id"));
            String animal_location = req.queryParams("animal_location");
            String ranger_name = req.queryParams("ranger_name");
            sightingDao.update(idOfSightingToEdit, animal_location, ranger_name);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete an individual animal
        get("/sightings/:sighting_id/animals/:animal_id/delete", (req, res) -> {
            int idOfAnimalToDelete = Integer.parseInt(req.params("animal_id"));
            animalDao.deleteById(idOfAnimalToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //    get: add new animals
        get("/animals/new",(req,res)->{
            List<Sighting> sightings = sightingDao.getAll();
            model.put("sightings", sightings);
            return new ModelAndView ( model, "animal-form.hbs");
        },new HandlebarsTemplateEngine());

        post("/animals", (req, res) -> { //new
            String animal_name = req.queryParams("animal_name");
            int sighting_id = Integer.parseInt(req.queryParams("sighting_id"));
            Animal newAnimal = new Animal(animal_name,sighting_id);
            animalDao.add(newAnimal);
            List<Sighting> allSightings = sightingDao.getAll();
            model.put("sightings", allSightings);
            res.redirect("/");
            return null;
        },new HandlebarsTemplateEngine());

        //get: show an individual animal that is nested in a sighting
        get("/sightings/:sighting_id/animals/:animal_id", (req, res) -> {
            int idOfAnimalToFind = Integer.parseInt(req.params("animal_id")); //pull id - must match route segment
            Animal foundAnimal = animalDao.findById(idOfAnimalToFind); //use it to find hero
            int idOfSightingToFind = Integer.parseInt(req.params("sighting_id"));
            Sighting foundSighting = sightingDao.findById(idOfSightingToFind);
            model.put("sighting", foundSighting);
            model.put("animal", foundAnimal);
            model.put("sightings", sightingDao.getAll());
            return new ModelAndView(model, "animal-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a animal
        get("/animals/:id/edit", (req, res) -> {
            List<Sighting> allSightings = sightingDao.getAll();
            model.put("sightings", allSightings);
            Animal animal = animalDao.findById(Integer.parseInt(req.params("id")));
            model.put("animal", animal);
            model.put("editAnimal", true);
            return new ModelAndView(model, "animal-form.hbs");
        }, new HandlebarsTemplateEngine());

        //hero: process a form to update a hero
        post("/animals/:id", (req, res) -> { //URL to update hero on POST route
            int animalToEditId = Integer.parseInt(req.params("id"));
            int newSighting_id = Integer.parseInt(req.queryParams("sighting_id"));
            String newAnimal_name = req.queryParams("animal_name");
            animalDao.update(animalToEditId, newSighting_id, newAnimal_name);  // remember the hardcoded sightingId we placed? See what we've done to/with it?
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: delete an individual endangeredAnimal
        get("/sightings/:sighting_id/endangeredAnimals/:endangeredAnimal_id/delete", (req, res) -> {
            int idOfEndangeredAnimalToDelete = Integer.parseInt(req.params("endangeredAnimal_id"));
            endangeredAnimalDao.deleteById(idOfEndangeredAnimalToDelete);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //    get: add new endangeredAnimals
        get("/endangeredAnimals/new",(req,res)->{
            List<Sighting> sightings = sightingDao.getAll();
            model.put("sightings", sightings);
            return new ModelAndView ( model, "endangeredAnimal-form.hbs");
        },new HandlebarsTemplateEngine());

        //    post: process new endangeredAnimal form
        post("/endangeredAnimals", (req, res) -> { //new
            String animal_name = req.queryParams("animal_name");
            String animal_health = req.queryParams("animal_health");
            String animal_age = req.queryParams("animal_age");
            EndangeredAnimal newEndangeredAnimal = new EndangeredAnimal(animal_name, animal_health,animal_age);
            endangeredAnimalDao.add(newEndangeredAnimal);
            List<Sighting> allSightings = sightingDao.getAll();
            model.put("sightings", allSightings);
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

        //get: show an individual endangeredAnimal that is nested in a sighting
        get("/sightings/:sighting_id/endangeredAnimals/:endangeredAnimal_id", (req, res) -> {
            int idOfEndangeredAnimalToFind = Integer.parseInt(req.params("endangeredAnimal_id")); //pull id - must match route segment
            EndangeredAnimal foundEndangeredAnimal = endangeredAnimalDao.findById(idOfEndangeredAnimalToFind); //use it to find individual endangered animal
            int idOfSightingToFind = Integer.parseInt(req.params("sighting_id"));
            Sighting foundSighting = sightingDao.findById(idOfSightingToFind);
            model.put("sighting", foundSighting);
            model.put("endangeredAnimal", foundEndangeredAnimal);
            model.put("sightings", sightingDao.getAll());
            return new ModelAndView(model, "endangeredAnimal-detail.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a endangeredAnimal
        get("/endangeredAnimals/:id/edit", (req, res) -> {
            List<Sighting> allSightings = sightingDao.getAll();
            model.put("sightings", allSightings);
            EndangeredAnimal endangeredAnimal = endangeredAnimalDao.findById(Integer.parseInt(req.params("id")));
            model.put("endangeredAnimal", endangeredAnimal);
            model.put("editEndangeredAnimal", true);
            return new ModelAndView(model, "endangeredAnimal-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a endangeredAnimal
        post("/endangeredAnimals/:id", (req, res) -> { //URL to update hero on POST route
            int endangeredAnimalToEditId = Integer.parseInt(req.params("id"));
            String animal_name = req.queryParams("animal_name");
            String animal_health = req.queryParams("animal_health");
            String animal_age = req.queryParams("animal_age");
            int sighting_id = Integer.parseInt(req.queryParams("sighting_id"));
            endangeredAnimalDao.update(endangeredAnimalToEditId, animal_name, animal_health, animal_age, sighting_id);  // remember the hardcoded sightingId we placed? See what we've done to/with it?
            res.redirect("/");
            return null;
        }, new HandlebarsTemplateEngine());

    }

}
