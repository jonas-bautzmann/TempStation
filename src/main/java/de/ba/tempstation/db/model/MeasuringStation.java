package de.ba.tempstation.db.model;

import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;

@Entity
@Table(name = "measuring_station")
public class MeasuringStation extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measuring_station_id", nullable = false, unique = true)
    private int id;

    @Column(name = "hardware_id", nullable = false, unique = true)
    private String hardwareId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_id")
    private Settings settings;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    public MeasuringStation() {}

    public MeasuringStation(int id) {
        this.id = id;
    }

    //region Getter/Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonSetter("locationId")
    public void setLocationById(int locationId) {
        this.location = new Location(locationId);
    }

    @JsonSetter("settingsId")
    public void setSettingsById(int settingsId) {
        this.settings = new Settings(settingsId);
    }
    //endregion
}
