package dao;

import models.EndangeredAnimal;

import java.util.List;

public interface EndangeredAnimalDao {
    //LIST
    List<EndangeredAnimal> getAll();

    //CREATE
    void add(EndangeredAnimal endangeredAnimal);

    //READ
    EndangeredAnimal findById(int id);

    //UPDATE
    void update(int id, String animal_name, String animal_health, String animal_age, int sighting_id);

    //DELETE
    void deleteById(int id);
    void clearAllEndangeredAnimals();
}
