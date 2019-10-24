package models;

import java.time.LocalDateTime;

public class Sighting {
    private int id;
    private String animal_location;
    private String ranger_name;
    private LocalDateTime sighted_at;

    public Sighting(String animal_location, String ranger_name) {
        this.animal_location = animal_location;
        this.ranger_name = ranger_name;
        this.sighted_at = LocalDateTime.now();
    }
    public LocalDateTime getSightedAt() { return sighted_at; }

    public String getAnimal_location() { return animal_location; }

    public void setAnimal_location(String animal_location) {
        this.animal_location = animal_location;
    }

    public String getRanger_name() {
        return ranger_name;
    }

    public void setRanger_name(String ranger_name) {
        this.ranger_name = ranger_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
