package org.blog;

import java.util.Objects;

public class Address {

  private String street;
  private String streetNumber;
  private String city;
  private String country;
  private Long postalCode;

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Long getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(Long postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return Objects.equals(street, address.street) && Objects.equals(streetNumber, address.streetNumber) && Objects.equals(city, address.city) && Objects.equals(country, address.country) && Objects.equals(postalCode, address.postalCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, streetNumber, city, country, postalCode);
  }

  @Override
  public String toString() {
    return "Address{" +
      "street='" + street + '\'' +
      ", streetNumber='" + streetNumber + '\'' +
      ", city='" + city + '\'' +
      ", country='" + country + '\'' +
      ", postalCode=" + postalCode +
      '}';
  }
}
