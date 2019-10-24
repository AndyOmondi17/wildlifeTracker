package dao;
import models.Animal;
import models.EndangeredAnimal;
import models.Sighting;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oSightingDao implements SightingDao {
    private final Sql2o sql2o;

    public Sql2oSightingDao(Sql2o sql2o) { this.sql2o = sql2o; }

    @Override
    public List<Sighting> getAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM sightings")
                    .executeAndFetch(Sighting.class);
        }
    }

    @Override
    public void add(Sighting sighting) {
        String sql = "INSERT INTO sightings (animal_location, ranger_name, sighted_at) VALUES (:animal_location, :ranger_name, :sighted_at)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql, true)
                    .bind(sighting)
                    .executeUpdate()
                    .getKey();
            sighting.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public Sighting findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM sightings WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Sighting.class);
        }  }

    @Override
    public List<Animal> getAllAnimalsBySighting(int sighting_id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM animals WHERE sighting_id = :sighting_id")
                    .addParameter("sighting_id", sighting_id)
                    .executeAndFetch(Animal.class);
        }
    }

    @Override
    public List<EndangeredAnimal> getAllEndangeredAnimalsBySighting(int sighting_id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM endangeredAnimals WHERE sighting_id = :sighting_id")
                    .addParameter("sighting_id", sighting_id)
                    .executeAndFetch(EndangeredAnimal.class);
        }
    }

    @Override
    public void update(int id, String animal_location, String ranger_name) {
        String sql = "UPDATE sightings SET animal_location = :animal_location, ranger_name = :ranger_name WHERE id = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("animal_location", animal_location)
                    .addParameter("ranger_name", ranger_name)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from sightings WHERE id=:id"; //raw sql
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void clearAllSightings() {
        String sql = "DELETE from sightings"; //raw sql
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }

    }

}
