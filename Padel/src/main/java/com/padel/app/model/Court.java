package com.padel.app.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCourt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_owner", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String nameCourt;

    @Column(nullable = false, length = 255)
    private String direction;

    private Double lat;
    private Double lng;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Court() {
    }

    public Court(Long idCourt, User owner, String nameCourt, String direction, Double lat, Double lng, BigDecimal price, List<Booking> bookings) {
        this.idCourt = idCourt;
        this.owner = owner;
        this.nameCourt = nameCourt;
        this.direction = direction;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.bookings = bookings;
    }

    public Long getIdCourt() {
        return idCourt;
    }

    public void setIdCourt(Long idCourt) {
        this.idCourt = idCourt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getNameCourt() {
        return nameCourt;
    }

    public void setNameCourt(String nameCourt) {
        this.nameCourt = nameCourt;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
